package io.sharptree.maximo.app.label;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldMedia extends MboValueAdapter {
    public FldMedia(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        getMboValue("DEFAULT").setValue(false);
        super.action();
    }
}
