package io.sharptree.maximo.app.label;

import psdi.mbo.Mbo;
import psdi.mbo.MboSet;

import java.rmi.RemoteException;

/**
 * Mbo for managing the Barcode printing configurations.
 *
 * @author Jason VenHuizen
 */
public class Label extends Mbo {
    /**
     * Creates a new instance of BarcodePrint.
     *
     * @param ms the owning MboSet.
     * @throws RemoteException thrown if a remote networking error occurs.
     */
    public Label(MboSet ms) throws RemoteException {
        super(ms);
    }
}
