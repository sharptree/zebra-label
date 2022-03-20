package io.sharptree.maximo.webclient.beans.zebralabel;

import io.sharptree.maximo.app.label.virtual.PrintLabelSet;
import psdi.mbo.Mbo;
import psdi.util.MXException;
import psdi.webclient.controls.Dialog;
import psdi.webclient.system.beans.DataBean;

import java.rmi.RemoteException;

/**
 * Bean that handles only displaying the print label dialog if more than one printer / label combination is available.
 *
 * @author Jason VenHuizen
 */
@SuppressWarnings("unused")
public class PrintLabelBean extends DataBean {
    @Override
    protected void initialize() throws MXException, RemoteException {
        super.initialize();

        if (!getMboSet().isEmpty()) {
            Mbo printLabel = (Mbo) getMboSet().getMbo(0);

            if (printLabel.getOwner() != null) {
                if(printLabel.getOwner().getName().equals("MATRECTRANS") && printLabel.getOwner().isNull("TOSTORELOC")){
                    clientSession.showMessageBox("sharptree","noToStoreLoc", null);
                    ((Dialog) clientSession.findDialog("zebralabelprint")).dialogcancel();
                }
                if (printLabel.getMboValue("PRINTER").getList().count() == 1 && printLabel.getMboValue("LABEL").getList().count() == 1) {
                    ((Dialog) clientSession.findDialog("zebralabelprint")).dialogcancel();

                    printLabel.setValue("PRINTER" ,printLabel.getMboValue("PRINTER").getList().getMbo(0).getString("VALUE"));
                    printLabel.setValue("LABEL" ,printLabel.getMboValue("LABEL").getList().getMbo(0).getString("VALUE"));

                    if (getMboSet().getName().equals("STPRINTLABEL")) {
                        String[] args = new String[]{getMboSet().getMbo().getOwner().getString("ITEMNUM")};
                        ((PrintLabelSet) getMboSet()).execute();
                        clientSession.showMessageBox("sharptree", "labelPrinted", args);
                    }
                }
            }
        }
    }

}

