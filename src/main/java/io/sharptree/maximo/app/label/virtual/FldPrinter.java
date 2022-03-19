package io.sharptree.maximo.app.label.virtual;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldPrinter extends MboValueAdapter {
    public FldPrinter(MboValue mbv) {
        super(mbv);
    }

    @Override
    public MboSetRemote getList() throws MXException, RemoteException {

        MboSetRemote domainSet = getMboValue().getMbo().getMboSet("$tmpmaxdomain", "MAXDOMAIN", "1=0");

        domainSet.reset();

        MboRemote domain = domainSet.add(NOACCESSCHECK);
        domain.setValue("DOMAINID", "STPRINTERTMP");
        domain.setValue("DOMAINTYPE", "ALN");
        domain.setValue("MAXTYPE", getMboValue().getMboValueInfo().getMaxType());
        domain.setValue("LENGTH", getMboValue().getMboValueInfo().getLength());

        MboSetRemote alnSet = domain.getMboSet("ALNDOMAINVALUE");

        MboSetRemote printerSet;
        printerSet = getMboValue().getMbo().getMboSet("$stprinter", "STPRINTER", "location = :location and siteid = :siteid");

        printerSet.clear();
        printerSet.reset();

        MboRemote printer = printerSet.moveFirst();
        while (printer != null) {
            MboRemote aln = alnSet.add(NOACCESSCHECK | NOACTION | NOVALIDATION);
            aln.setValue("VALUE", printer.getString("PRINTER"));
            aln.setValue("DESCRIPTION", printer.getString("DESCRIPTION"));
            printer = printerSet.moveNext();
        }

        return alnSet;
    }

    @Override
    public boolean hasList() {
        return true;
    }
}
