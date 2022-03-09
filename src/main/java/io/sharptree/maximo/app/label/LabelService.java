package io.sharptree.maximo.app.label;

import psdi.server.AppService;
import psdi.server.MXServer;

import java.rmi.RemoteException;

/**
 * A service for the Barcode Print Service.
 *
 * @author Jason VenHuizen
 */
public class LabelService extends AppService {

    /**
     * Creates a new instance of the BarcodePrintService class.
     *
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    @SuppressWarnings("unused")
    public LabelService() throws RemoteException {
        super();
    }

    /**
     * Creates a new instance of the mxserver class.
     *
     * @param mxServer the owning Maximo application server.
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    @SuppressWarnings("unused")
    public LabelService(MXServer mxServer) throws RemoteException {
        super(mxServer);
    }
}