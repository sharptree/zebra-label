package io.sharptree.maximo.app.label.virtual;

import com.ibm.tivoli.maximo.script.ScriptAction;
import psdi.mbo.*;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.FixedLoggers;

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

    /**
     * {@inerhitDoc}
     *
     * @see MboSet#getMboInstance(MboSet)
     */
    @Override
    protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {
        return new PrintLabel(mboSet);
    }

    /**
     * {@inerhitDoc}
     *
     * @see NonPersistentMboSet#execute()
     */
    public void execute() throws MXException, RemoteException {
        MboRemote printLabel = getMbo(0);

        if (printLabel.getInt("COUNT") < 1) {
            throw new MXApplicationException("sharptree", "countLessThanOne");
        }

        int maxCount = 10;

        try {
            String max = MXServer.getMXServer().getProperty("sharptree.zebralabel.maxcount");
            if(max!=null && !max.isEmpty()){
                Integer.parseInt(MXServer.getMXServer().getProperty("sharptree.zebralabel.maxcount"));
            }
        } catch (Throwable t) {
            FixedLoggers.APPLOGGER.error("Error parsing property sharptree.zebralabel.maxcount: " + t.getMessage());
        }

        if (printLabel.getInt("COUNT") > maxCount) {
            throw new MXApplicationException("sharptree", "countGreaterThanMax", new String[]{printLabel.getString("COUNT"), String.valueOf(maxCount)});
        }

        for (int i = 0; i < printLabel.getInt("COUNT"); i++) {
            // invoking the script should only throw MXExceptions which can be handled by the application framework.
            try {
                (new ScriptAction()).applyCustomAction(printLabel, new String[]{"STAUTOSCRIPT.ZEBRALABEL.PRINTLABEL"});
            }catch(Throwable e){
                e.printStackTrace();
            }
        }

        // reset the MboSet so related sets such as the temporary domain used for the combo boxes isn't saved.
        reset();
    }
}
