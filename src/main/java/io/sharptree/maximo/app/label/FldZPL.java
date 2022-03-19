package io.sharptree.maximo.app.label;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldZPL extends MboValueAdapter {
    public FldZPL(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if (!getMboValue().isNull()) {
            String value = getMboValue().getString();

            if(!value.trim().startsWith("^XA") && !value.trim().endsWith("^XZ")){
                throw new MXApplicationException("sharptree", "invalidZPL");
            }
        }
        super.validate();
    }
}
