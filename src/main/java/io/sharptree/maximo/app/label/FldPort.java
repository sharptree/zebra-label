package io.sharptree.maximo.app.label;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldPort extends MboValueAdapter {
    public FldPort(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if (!getMboValue().isNull()) {
            int port = getMboValue().getInt();

            if(port <0 || port > 65535){
                throw new MXApplicationException("sharptree","invalidPort",new String[]{String.valueOf(port)});
            }
        }
        super.validate();
    }
}
