package io.sharptree.maximo.app.label;

import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldDefaultPrinter extends MboValueAdapter {
    public FldDefaultPrinter(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void validate() throws MXException, RemoteException {

        MboSetRemote printerSet = getMboValue().getMbo().getMboSet("$printercheck", "STPRINTER", "location = :location and siteid = :siteid  and default = :yes");
        if (!printerSet.isEmpty()) {
            if (getMboValue().getMbo().getUserInfo().isInteractive()) {
                int userInput = MXApplicationYesNoCancelException.getUserInput("changedefaultprinter", MXServer.getMXServer(), getMboValue().getMbo().getUserInfo());
                switch (userInput) {
                    case MXApplicationYesNoCancelException.YES:
                        printerSet.getMbo(0).setValue("DEFAULT", false);
                        break;
                    case MXApplicationYesNoCancelException.NULL:
                        throw new MXApplicationYesNoCancelException("changedefaultprinter", "sharptree", "changedefault");
                    default:
                        getMboValue().setValue(false);
                }
            } else {
                printerSet.getMbo(0).setValue("DEFAULT", false);
            }
        }
        super.validate();
    }
}
