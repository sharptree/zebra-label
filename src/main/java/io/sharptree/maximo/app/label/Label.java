package io.sharptree.maximo.app.label;

import psdi.mbo.*;
import psdi.util.MXException;

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

    /**
     * {@inerhitDoc}
     *
     * @see Mbo#init()
     */
    @Override
    public void init() throws MXException {
        setFieldFlag(new String[]{"LABEL", "MEDIA", "ZPL", "USEWITH"}, MboConstants.REQUIRED, true);

        super.init();
    }

    /**
     * {@inerhitDoc}
     *
     * @see Mbo#duplicate()
     */
    @Override
    public MboRemote duplicate() throws MXException, RemoteException {
        MboRemote newLabel = this.copy();
        newLabel.setValueNull("LABEL");
        newLabel.setValue("DESCRIPTION", getString("DESCRIPTION"));
        newLabel.setValue("DEFAULT", false);
        newLabel.setValue("MEDIA", getString("MEDIA"));
        newLabel.setValue("USEWITH", getString("USEWITH"));
        newLabel.setValue("ZPL", getString("ZPL"));
        return newLabel;
    }
}
