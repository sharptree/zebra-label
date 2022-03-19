package io.sharptree.maximo.app.label.virtual;

import psdi.mbo.*;
import psdi.util.MXException;
import psdi.util.logging.FixedLoggers;

import java.rmi.RemoteException;

public class FldLabel extends MboValueAdapter {

    public FldLabel(MboValue mbv) {
        super(mbv);
    }

    @Override
    public MboSetRemote getList() throws MXException, RemoteException {
        MboSetRemote domainSet = getMboValue().getMbo().getMboSet("$tmpmaxdomain", "MAXDOMAIN", "1=0");
        domainSet.reset();
        MboRemote domain = domainSet.add(NOACCESSCHECK);
        domain.setValue("DOMAINID", "STLABELTMP");
        domain.setValue("DOMAINTYPE", "ALN");
        domain.setValue("MAXTYPE", getMboValue().getMboValueInfo().getMaxType());
        domain.setValue("LENGTH", getMboValue().getMboValueInfo().getLength());

        MboSetRemote alnSet = domain.getMboSet("ALNDOMAINVALUE");

        MboSetRemote printerSet = getMboValue().getMbo().getMboSet("$stprinter", "STPRINTER", "printer = :printer");

        if (!printerSet.isEmpty()) {
            String media = printerSet.moveFirst().getString("MEDIA");

            SqlFormat sqlf = new SqlFormat("media = :1 and usewith = :2");
            sqlf.setObject(1, "STLABEL", "MEDIA", media);
            if (getMboValue().getMbo().getOwner() != null) {
                sqlf.setObject(2, "STLABEL", "USEWITH", getMboValue().getMbo().getOwner().getName());
            } else {
                FixedLoggers.APPLOGGER.error("The value adapter " + getClass().getName() + " on " + getMboValue().getMbo().getName() + " must have an owner to display a list of available labels.");
                sqlf.setObject(2, "STLABEL", "USEWITH", "DUMMY");
            }

            MboSetRemote labelSet = getMboValue().getMbo().getMboSet("$stlabel", "STLABEL", sqlf.format());
            labelSet.clear();
            labelSet.reset();
            MboRemote label = labelSet.moveFirst();
            while (label != null) {
                MboRemote aln = alnSet.add(NOACCESSCHECK | NOACTION | NOVALIDATION);
                aln.setValue("VALUE", label.getString("LABEL"));
                aln.setValue("DESCRIPTION", label.getString("DESCRIPTION"));
                label = labelSet.moveNext();
            }

            return alnSet;
        } else {
            return super.getList();
        }
    }

    @Override
    public boolean hasList() {
        return true;
    }
}
