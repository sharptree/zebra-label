var labels = service.invokeScript("STAUTOSCRIPT.ZEBRALABEL.LABELCFG").labels;

SqlFormat = Java.type("psdi.mbo.SqlFormat");
MXServer = Java.type("psdi.server.MXServer");
MXLoggerFactory = Java.type("psdi.util.logging.MXLoggerFactory");

var logger = MXLoggerFactory.getLogger("maximo.script." + service.scriptName);

main();

function main() {

    if (logger.isDebugEnabled()) {
        logger.debug("Getting the Zebra Label labels");
    }

    _mergeLabels();

    labels = _validateAndRemoveInvalidLabels(labels);


    if (typeof request !== 'undefined' && request) {
        responseBody = JSON.stringify(labels);
    }

    if (logger.isDebugEnabled()) {
        logger.debug("Successfully retrieved the Zebra Label labels");
    }

}

function _mergeLabels() {
    if (MXServer.getMXServer().getMaximoDD().getMboSetInfo("STLABEL")) {
        if (logger.isDebugEnabled()) {
            logger.debug("The Zebra Label Maximo application has been installed, merging Maximo configuration with values from the script.");
        }

        var labelSet;

        try {
            labelSet = MXServer.getMXServer().getMboSet("STLABEL", MXServer.getMXServer().getSystemUserInfo());
            if (!labelSet.isEmpty()) {
                var label = labelSet.moveFirst();

                while (label) {
                    var labelConfig = _convertMboToObject(label)

                    if (logger.isDebugEnabled()) {
                        logger.debug("Got label configuration from Maximo:\n" + JSON.stringify(labelConfig, null, 4));
                    }

                    labels.push(labelConfig);
                    label = labelSet.moveNext();
                }
            }
        } finally {
            close(labelSet);
        }

    } else {
        if (logger.isDebugEnabled()) {
            logger.debug("The Zebra Label Maximo application has not been installed, only returning values from the script.");
        }
    }
}

function _convertMboToObject(mbo) {
    var label = {};

    label.label = mbo.getString("LABEL");
    label.description = mbo.getString("DESCRIPTION");
    label.media = mbo.getString("MEDIA");
    label.usewith = mbo.getString("USEWITH");
    label.zpl = mbo.getString("ZPL");
    label.default = mbo.getBoolean("DEFAULT");

    return label;
}

function _validateAndRemoveInvalidLabels(labels) {
    var tmpLabels = [];
    labels.forEach(function (label) {
        if (_validateLabelAttributes(label) && _validateMedia(label) && _validateUseWith(label)) {
            label.label = label.label.toUpperCase();
            label.usewith = label.usewith.toUpperCase();
            tmpLabels.push(label);
        } else {
            logger.error("The following label configuration is missing either a name, media, usewith or zpl attribute and will not be returned. \n\n" + JSON.stringify(label, null, 4) + "\n");
        }
    });

    return tmpLabels;
}

function _validateLabelAttributes(label) {
    return label.label && label.media && label.zpl && label.usewith;
}

function _validateUseWith(label) {
    var maxObjectSet;

    try {
        var sqlf = new SqlFormat("objectname = :1");
        sqlf.setObject(1, "MAXOBJECT", "OBJECTNAME", label.usewith);

        maxObjectSet = MXServer.getMXServer().getMboSet("MAXOBJECT", MXServer.getMXServer().getSystemUserInfo());
        maxObjectSet.setWhere(sqlf.format());

        var result = !maxObjectSet.isEmpty();
        if (!result) {
            logger.error("The label usewith attribute " + label.usewith + " is not a valid Maximo object. The complete invalid label is:\n\n" + JSON.stringify(label, null, 4) + "\n");
        }

        return result;

    } finally {
        close(maxObjectSet);
    }
}

function _validateMedia(label) {
    if (label.media) {
        var mboSetInfo = MXServer.getMXServer().getMaximoDD().getMboSetInfo("STLABEL");
        if (mboSetInfo) {
            var domainId = mboSetInfo.getMboValueInfo("MEDIA").getDomainId();
            var sqlf = new SqlFormat("domainid = :1 and value = :2");
            sqlf.setObject(1, "ALNDOMAIN", 'DOMAINID', domainId);
            sqlf.setObject(2, "ALNDOMAIN", 'VALUE', label.media);

            var alnDomainSet;

            try {
                alnDomainSet = MXServer.getMXServer().getMboSet("ALNDOMAIN", MXServer.getMXServer().getSystemUserInfo());
                alnDomainSet.setWhere(sqlf.format());
                return !alnDomainSet.isEmpty();
            } finally {
                close(alnDomainSet);
            }

        } else {
            return true;
        }
    }
    return false;
}

function close(set) {
    if (set) {
        set.cleanup();
        set.close();
    }
}

var scriptConfig = {
    "autoscript": "STAUTOSCRIPT.ZEBRALABEL.LABELS",
    "description": "Barcode Label Definitions",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR"
};