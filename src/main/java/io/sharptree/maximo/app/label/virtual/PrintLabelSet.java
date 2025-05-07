package io.sharptree.maximo.app.label.virtual;

import com.ibm.tivoli.maximo.script.ScriptAction;
import io.sharptree.maximo.LabelLogger;
import psdi.common.context.UIContext;
import psdi.mbo.*;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.FixedLoggers;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

/**
 * Non-persistent MboSet for managing printing a label.
 *
 * @author Jason VenHuizen
 */
@SuppressWarnings("unused")
public class PrintLabelSet extends NonPersistentMboSet {

    /**
     * Creates a new instance of PrintLabelSet.
     *
     * @param ms the owning mbo server.
     * @throws RemoteException thrown if a network error occurs.
     */
    public PrintLabelSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {

        return new PrintLabel(mboSet);
    }

    @Override
    public void execute() throws MXException, RemoteException {
        MboRemote printLabel = getMbo(0);

        if (printLabel.getInt("COUNT") < 1) {
            throw new MXApplicationException("sharptree", "countLessThanOne");
        }

        int maxCount = 10;

        try {
            String max = MXServer.getMXServer().getProperty("sharptree.zebralabel.maxcount");
            if (max != null && !max.isEmpty()) {
                Integer.parseInt(MXServer.getMXServer().getProperty("sharptree.zebralabel.maxcount"));
            }
        } catch (Throwable t) {
            LabelLogger.LABEL_LOGGER.error("Error parsing property sharptree.zebralabel.maxcount: " + t.getMessage());
        }

        if (printLabel.getInt("COUNT") > maxCount) {
            throw new MXApplicationException("sharptree", "countGreaterThanMax", new String[]{printLabel.getString("COUNT"), String.valueOf(maxCount)});
        }

        for (int i = 0; i < printLabel.getInt("COUNT"); i++) {
            // invoking the script should only throw MXExceptions which can be handled by the application framework.
            try {
                (new ScriptAction()).applyCustomAction(printLabel, new String[]{"STAUTOSCRIPT.ZEBRALABEL.PRINTLABEL"});
            } catch (Throwable e) {
                if (e instanceof MXException) {
                    throw e;
                } else {
                    throw new MXApplicationException("sharptree", "unknownPrintError", new String[]{e.getMessage()});
                }
            }
        }

        UIContext uiContext = UIContext.getCurrentContext();
        if (uiContext != null) {

            String[] args ={"unknown"};

            if (getOwner().isBasedOn("ASSET")) {
                args = new String[]{"asset",getOwner().getString( "ASSETNUM")};
            } else if (getOwner().isBasedOn("INVBALANCES")|| getOwner().isBasedOn("INVENTORY")){
                args = new String[]{"item",getOwner().getString("ITEMNUM")};
            }else if (getOwner().isBasedOn("MATRECTRANS")){
                args = new String[]{"poline", getOwner().getString("POLINENUM")};
            }else if (getOwner().isBasedOn("LOCATIONS")){
                args = new String[]{"location", getOwner().getString("LOCATION")};
            } else if (getOwner().isBasedOn("WORKORDER")){
                args = new String[]{"workorder", getOwner().getString("WONUM")};
            }
            Object wcs = uiContext.getWebClientSession();
            try {
                Method m = wcs.getClass().getMethod("showMessageBox", String.class, String.class, Array.newInstance(String.class, 0).getClass());
                m.invoke(wcs, "sharptree", "labelPrinted", args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
//                throw new RuntimeException(e);
            }

        }


        // reset the MboSet so related sets such as the temporary domain used for the combo boxes isn't saved.
        reset();
    }
}
