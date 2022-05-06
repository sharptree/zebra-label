package io.sharptree.maximo.webclient.servlet;

import io.sharptree.maximo.LabelLogger;
import io.sharptree.maximo.app.label.LabelPrintEvent;
import psdi.server.MXServer;
import psdi.server.event.EventListener;
import psdi.server.event.EventMessage;
import psdi.util.MXException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Servlet that provides support for Server Side Event (SSE) long polling for Zebra label printing events.
 *
 * @author Jason VenHuizen
 */
public class ZebraLabelPrintDispatchServlet extends HttpServlet implements EventListener {
    // the event registration Id for the Maximo zebra printing events.
    private int eventRegistrationId = -1;
    // The event topic to listen for.
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TOPIC = "sharptree.printlabel";
    // Boolean value to provide a way to shut down the SSE loop.
    private boolean shutdown = false;
    private final List<LabelPrintDispatch> queues = new ArrayList<>();
    private final Object lock = new Object();

    @Override
    public void init() throws ServletException {
        try {
            eventRegistrationId = MXServer.getEventTopicTree().register(TOPIC, this);
        } catch (MXException e) {
            //there is nothing to do so just log the error message
            LabelLogger.LABEL_LOGGER.error("Error initializing " + this.getClass().getName() + ", print events will not be handled.", e);
        }

        super.init();
    }

    @Override
    public void destroy() {
        if (eventRegistrationId >= 0) {
            try {
                MXServer.getEventTopicTree().unregister(TOPIC, eventRegistrationId);
            } catch (MXException e) {
                //there is nothing to do so just log the error message
                LabelLogger.LABEL_LOGGER.error("Error destroying " + this.getClass().getName() + ".", e);
            }
        }
        shutdown = true;
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequestRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequestRequest(req, resp);
    }

    private void handleRequestRequest(@SuppressWarnings("unused") HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (eventRegistrationId < 0) {
            throw new ServletException("The print servlet was not initialized and is not handling print requests.");
        }

        response.setContentType("text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        ServletOutputStream out = response.getOutputStream();

        long id = System.currentTimeMillis();

        SynchronousQueue<LabelPrintEvent> queue = new SynchronousQueue<>();
        LabelPrintDispatch dispatch = new LabelPrintDispatch(queue, out);
        synchronized (lock) {
            queues.add(dispatch);
        }

        while (!shutdown) {
            try {
                // the queue.take() method blocks until something is added to the queue.
                LabelPrintEvent event = queue.poll(60, TimeUnit.SECONDS);
                if (event != null) {

                    if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                        LabelLogger.LABEL_LOGGER.debug("Sending event action to the registered print agent: " + event);
                    }

                    out.println("event: print");
                    out.println("id: " + id++);
                    // the \n\n on the end signifies that the server is done sending event data.
                    out.println("data:{\"printer\":\"" + event.getPrinter() + "\",\"port\":" + event.getPort() + ",\"label\":\"" + event.getLabel() + "\"}\n\n");
                    out.flush();
                } else {
                    // return every 60 seconds so WebSphere doesn't think the thread is hung.  The client will just retry the call.
                    if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                        LabelLogger.LABEL_LOGGER.debug("Queue polling has timed out, returning and allowing the client print agent to reregister.  Removing dispatch from queues.");
                    }
                    synchronized (lock) {
                        queues.remove(dispatch);
                    }
                    break;
                }

            } catch (InterruptedException e) {
                LabelLogger.LABEL_LOGGER.error("An error occurred taking the print event from the queue.", e);
            }
        }
    }

    @Override
    public boolean eventValidate(EventMessage eventMessage) {
        return true;
    }

    @Override
    public void preSaveEventAction(EventMessage eventMessage) {
        // intentionally do nothing
    }


    @Override
    public void eventAction(EventMessage eventMessage) {
        Object obj = eventMessage.getEventObject();

        if (obj instanceof LabelPrintEvent) {
            synchronized (lock) {

                LabelPrintEvent event = (LabelPrintEvent) obj;
                List<LabelPrintDispatch> toRemove = new ArrayList<>();
                queues.forEach(it -> {
                    try {
                        if (it.getOutpuStream().isReady()) {
                            if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                                LabelLogger.LABEL_LOGGER.debug("Posting event to registered queue: " + event);
                            }
                            it.getQueue().offer(event);
                        } else {
                            if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                                LabelLogger.LABEL_LOGGER.debug("Encountered queue with an output stream that is not ready, marking it for removal.");
                            }
                            toRemove.add(it);
                        }
                    } catch (Throwable error) {
                        // catch any errors trying to check if the output writer is still available.
                        // in testing, because WebSphere has a thread pool and doesn't actively check connections,
                        // these don't get removed until they time out
                        if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                            LabelLogger.LABEL_LOGGER.debug("Encountered queue with a null output stream, marking it for removal.");
                        }
                        toRemove.add(it);
                    }
                });

                if (!toRemove.isEmpty()) {
                    if (LabelLogger.LABEL_LOGGER.isDebugEnabled()) {
                        LabelLogger.LABEL_LOGGER.debug("Removing print queues that no longer have a valid output stream, indicating a closed connection, from the registered queues.");
                    }
                    toRemove.forEach(queues::remove);
                }

            }
        }
    }

    @Override
    public void postCommitEventAction(EventMessage eventMessage) {
        // intentionally do nothing
    }

    /**
     * Class to hold the label print dispatch queue.
     *
     * @author Jason VenHuizen
     */
    private static class LabelPrintDispatch {
        private final SynchronousQueue<LabelPrintEvent> queue;
        private final ServletOutputStream outputStream;

        /**
         * Creates a new LabelPrintDispatch Object.
         *
         * @param queue  the queue to notify when a label print event occurs.
         * @param outpuStream a reference to the output stream, used to check if the client connection is still valid.
         */
        public LabelPrintDispatch(SynchronousQueue<LabelPrintEvent> queue, ServletOutputStream outpuStream) {
            this.queue = queue;
            this.outputStream = outpuStream;
        }

        /**
         * Get the print queue.
         * @return the print queue.
         */
        public SynchronousQueue<LabelPrintEvent> getQueue() {
            return queue;
        }

        /**
         * Get the output stream.
         * @return the output stream.
         */
        public ServletOutputStream getOutpuStream() {
            return outputStream;
        }
    }
}


