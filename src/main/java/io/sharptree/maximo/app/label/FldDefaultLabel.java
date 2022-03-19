package io.sharptree.maximo.app.label;

import psdi.mbo.MboConstants;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldDefaultLabel extends MboValueAdapter {
    public FldDefaultLabel(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if(getMboValue().getBoolean()) {
            MboSetRemote LabelSet = getMboValue().getMbo().getMboSet("$labelcheck", "STLABEL", "media = :media and default = :yes and label!=:label");
            if (!LabelSet.isEmpty()) {
                if (getMboValue().getMbo().getUserInfo().isInteractive()) {
                    int userInput = MXApplicationYesNoCancelException.getUserInput("changedefaultlabel", MXServer.getMXServer(), getMboValue().getMbo().getUserInfo());
                    switch (userInput) {
                        case MXApplicationYesNoCancelException.YES:
                            LabelSet.getMbo(0).setValue("DEFAULT", false, MboConstants.NOVALIDATION);
                            break;
                        case MXApplicationYesNoCancelException.NULL:
                            throw new MXApplicationYesNoCancelException("changedefaultlabel", "sharptree", "changeDefaultLabel", new String[]{getMboValue("MEDIA").getString()});
                        default:
                            getMboValue().setValue(false);
                    }
                } else {
                    LabelSet.getMbo(0).setValue("DEFAULT", false);
                }
            }
        }
        super.validate();
    }
}
