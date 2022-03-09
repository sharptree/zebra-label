System = Java.type("java.lang.System");

SqlFormat = Java.type("psdi.mbo.SqlFormat");
MXException = Java.type("psdi.util.MXException");

DataOutputStream = Java.type("java.io.DataOutputStream");
IOException = Java.type("java.io.IOException");
SocketTimeoutException = Java.type("java.net.SocketTimeoutException");
BindException = Java.type("java.net.BindException");
Socket = Java.type("java.net.Socket");
InetSocketAddress = Java.type("java.net.InetSocketAddress");
RuntimeException = Java.type("java.lang.RuntimeException");

// This is the name of the label to print.
var labelName = "receipt";
var timeout = 5000;

main();

function main() {

    if (!mbo || !interactive) {
        // if this is not being invoked as an action, just return.
        return;
    }

    if (!mbo.isBasedOn("INVBALANCES")) {
        // if this is being invoked where the Mbo is not INVBALANCES skip.
        errorgroup = "sharptree";
        errorkey = "notInvBalances";
        return;
    }

    // find the label 
    var label = findLabel();

    var siteId = mbo.getString("SITEID");
    var storeroom = mbo.getString("LOCATION");

    printer = findPrinterForLabel(label, siteId, storeroom);

    printLabel(label, printer, mbo);
}

function findPrinterForLabel(label, siteId, storeroom) {
    var printers = service.invokeScript("STAUTOSCRIPT.PRINTERS");


    if (!printers || !printers.printers) {
        service.error("sharptree", "noprinters");

    }

    var printerFound = false;

    var printPrinter;

    printers.printers.forEach(function (printer) {
        if (printer.siteId = siteId && printer.storeroom == storeroom) {
            printerFound = true;
            if (printer.media == label.media) {
                printPrinter = printer;
                return;
            }
        }
    })

    if (!printPrinter) {
        if (printerFound) {
            serivce.error("sharptree", "noPrinterMediaFound", [storeroom, siteId, label.media])
        } else {
            serivce.error("sharptree", "noPrinterFound", [storeroom, siteId])
        }
    }

    return printPrinter;

}

function findLabel() {
    var labels = service.invokeScript("STAUTOSCRIPT.LABELS");
    if (!labels || !labels.labels) {
        service.error("sharptree", "nolabels");
    }

    var printLabel;

    labels.labels.forEach(function (label) {
        if (label.name == labelName) {
            printLabel = label;
            return;
        }
    })


    if (!printLabel) {
        service.error("sharptree", "noLabelFound", [labelName]);
    }

    return printLabel;
}

function printLabel(label, printer, invbalances) {

    var clientSocket;

    try {

        clientSocket = new Socket();

        clientSocket.connect(new InetSocketAddress(printer.address, printer.port), printer.timeout ? printer.timeout : timeout);

        var sqlf = new SqlFormat(invbalances, label.template);
        sqlf.setIgnoreUnresolved(true);

        var zpl = sqlf.resolveContent();

        var outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(zpl);
        clientSocket.close();
    } catch (error) {
        service.log_error(error);

        if (error instanceof MXException) {
            service.error(error.getErrorGroup(), error.getErrorKey(), error.getParameters());
        } else if (error instanceof SocketTimeoutException || error instanceof BindException) {
            service.error("sharptree", "printerTimeout", [error.getMessage()]);
        } else if (error instanceof IOException) {
            service.error("sharptree", "ioError", [error.getMessage()]);
        } else {
            service.error("sharptree", "genericError", [error.getMessage()]);
        }
    } finally {
        if (clientSocket) {
            clientSocket.close();
        }
    }
}

var scriptConfig = {
    "autoscript": "STAUTOSCRIPT.PRINTINVLABEL",
    "description": "Print Inventory Label",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR",
    "scriptLaunchPoints": [
        {
            "launchPointName": "STAUTOSCRIPT.PRINTINVLABEL",
            "launchPointType": "ACTION",
            "active": true,
            "description": "Print Inventory Label",
            "actionName": "STAUTOSCRIPT.PRINTINVLABEL"
        }
    ]
};