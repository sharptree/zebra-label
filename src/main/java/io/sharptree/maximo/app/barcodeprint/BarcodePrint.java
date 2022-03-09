package io.sharptree.maximo.app.barcodeprint;

import psdi.mbo.Mbo;
import psdi.mbo.MboConstants;
import psdi.mbo.MboSet;
import psdi.util.MXException;

import java.rmi.RemoteException;

/**
 * Mbo for managing the Barcode printing configurations.
 *
 * @author Jason VenHuizen
 */
public class BarcodePrint extends Mbo {
    /**
     * Creates a new instance of BarcodePrint.
     *
     * @param ms the owning MboSet.
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    public BarcodePrint(MboSet ms) throws RemoteException {
        super(ms);
    }
}
