package io.sharptree.maximo.app.barcodeprint;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;

import java.rmi.RemoteException;

/**
 * A service for the Barcode Print Service.
 *
 * @author Jason VenHuizen
 */
public class BarcodePrintService extends AppService {

    /**
     * Creates a new instance of the BarcodePrintService class.
     *
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    @SuppressWarnings("unused")
    public BarcodePrintService() throws RemoteException {
        super();
    }

    /**
     * Creates a new instance of the mxserver class.
     *
     * @param mxServer the owning Maximo application server.
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    @SuppressWarnings("unused")
    public BarcodePrintService(MXServer mxServer) throws RemoteException {
        super(mxServer);
    }
}