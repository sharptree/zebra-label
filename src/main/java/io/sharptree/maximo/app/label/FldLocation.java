package io.sharptree.maximo.app.label;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.FixedLoggers;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldLocation extends MAXTableDomain {
    public FldLocation(MboValue mbv) {
        super(mbv);
        String thisAttr = this.getMboValue().getAttributeName();

        setRelationship("LOCATIONS","location=:location and siteid = :siteid and type = 'STOREROOM'");
        setListCriteria("type = 'STOREROOM' and siteid = :siteid and orgid = :orgid");
        this.setErrorMessage("sharptree", "invalidStoreroom");
    }

    @Override
    public void setValueFromLookup(MboRemote sourceMbo) throws MXException, RemoteException {
        if(sourceMbo!=null && sourceMbo.isBasedOn("LOCATIONS")){
            getMboValue("SITEID").setValue(sourceMbo.getString("SITEID"));
            getMboValue("ORGID").setValue(sourceMbo.getString("ORGID"), MboConstants.NOACCESSCHECK| MboConstants.NOVALIDATION);
            getMboValue("LOCATION").setValue(sourceMbo.getString("LOCATION"));
        }
                super.setValueFromLookup(sourceMbo);
    }
}
