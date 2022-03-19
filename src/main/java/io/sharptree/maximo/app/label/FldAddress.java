package io.sharptree.maximo.app.label;

import com.google.common.net.InternetDomainName;
import com.google.common.net.InetAddresses;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldAddress extends MboValueAdapter {
    public FldAddress(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void validate() throws MXException, RemoteException {
        if (!getMboValue().isNull()) {
            //noinspection UnstableApiUsage
            if (!InternetDomainName.isValid(getMboValue().getString()) && !InetAddresses.isInetAddress(getMboValue().getString())) {
                throw new MXApplicationException("sharptree", "invalidHost", new String[]{getMboValue().getString()});
            }
        }
        super.validate();
    }
}
