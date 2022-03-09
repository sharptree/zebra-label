SqlFormat = Java.type("psdi.mbo.SqlFormat");
MXServer = Java.type("psdi.server.MXServer");
MXException = Java.type("psdi.util.MXException");

DataOutputStream = Java.type("java.io.DataOutputStream");
IOException = Java.type("java.io.IOException");
SocketTimeoutException = Java.type("java.net.SocketTimeoutException");
BindException = Java.type("java.net.BindException");
Socket = Java.type("java.net.Socket");
InetSocketAddress = Java.type("java.net.InetSocketAddress");
RuntimeException = Java.type("java.lang.RuntimeException");

System = Java.type("java.lang.System");

var timeout = 5000;

main();

function main() {
    if (typeof request !== 'undefined' && request) {
        var response = {};
        try {
            checkPermissions("SHARPTREE_UTILS", "PRINTLABEL");
            var labelName = request.getQueryParam("label");
            var printerName = request.getQueryParam("printer");
            var invbalancesid = request.getQueryParam("invbalancesid");

            if ((!labelName || !printerName || !invbalancesid) && requestBody) {
                var params = JSON.parse(requestBody);
                labelName = params.label;
                printerName = params.printer;
                invbalancesid = params.invbalancesid;
            }

            if (!labelName) {
                throw new PrintError("missing_label", "A label parameter must be provided either as a query parameter or as part of the request body.");
            }
            if (!printerName) {
                throw new PrintError("missing_printer", "A printer parameter must be provided either as a query parameter or as part of the request body.");
            }
            if (!invbalancesid) {
                throw new PrintError("missing_invbalancesid", "A invbalancesid parameter must be provided either as a query parameter or as part of the request body.");
            }

            var printers = service.invokeScript("STAUTOSCRIPT.PRINTERS");
            var labels = service.invokeScript("STAUTOSCRIPT.LABELS");
            var printer;
            printers.printers.forEach(function (p) {
                if (p.printer === printerName) {
                    printer = p;
                    return;
                }
            });

            if (!printer) {
                throw new PrintError("printer_not_configured", "Printer " + printerName + " not configured.");
            }

            var label;
            labels.labels.forEach(function (l) {
                if (l.name == labelName) {
                    label = l;
                    return;
                }
            })

            if (!label) {
                throw new PrintError("label_not_configured", "Label " + labelName + " not configured.");
            }

            var invbalancesSet;
            var clientSocket;

            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(printer.address, printer.port), printer.timeout ? printer.timeout : timeout);

                invbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", userInfo);
                invbalances = invbalancesSet.getMboForUniqueId(invbalancesid);

                if (!invbalances) {
                    throw new PrintError("invbalances_not_found", "Inventory Balance record for id " + invbalancesid + " was not found.");
                }

                var sqlf = new SqlFormat(invbalances, label.template);
                sqlf.setIgnoreUnresolved(true);
                var zpl = sqlf.resolveContent();

                var outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.writeBytes(zpl);
                clientSocket.close();

            } finally {
                if (clientSocket) {
                    clientSocket.close();
                }
                close(invbalancesSet);
            }

            response.status = "success";
            responseBody = JSON.stringify(response);

        } catch (error) {
            response.status = "error";

            if (error instanceof PrintError) {
                response.message = error.message;
                response.reason = error.reason;
            } else if (error instanceof Error) {
                response.message = error.message;
            } else if (error instanceof MXException) {
                response.reason = error.getErrorGroup() + "_" + error.getErrorKey();
                response.message = error.getMessage();
            } else if (error instanceof SocketTimeoutException || error instanceof BindException) {
                response.message = error.getMessage();
                response.reason = "printer_connect_timeout";
            } else if (error instanceof IOException) {
                response.message = error.getMessage() + error.getClass().getName();
            } else if (error instanceof RuntimeException) {
                if (error.getCause() instanceof MXException) {
                    response.reason = error.getCause().getErrorGroup() + "_" + error.getCause().getErrorKey();
                    response.message = error.getCause().getMessage();
                } else {
                    response.reason = "runtime_exception";
                    response.message = error.getMessage();
                }
            } else {
                response.cause = error;
            }

            responseBody = JSON.stringify(response);
            service.log_error(error);

            return;
        }
    }
}

function checkPermissions(app, optionName) {
    if (!userInfo) {
        throw new PrintError("no_user_info", "The userInfo global variable has not been set, therefore the user permissions cannot be verified.");
    }

    if (!MXServer.getMXServer().lookup("SECURITY").getProfile(userInfo).hasAppOption(app, optionName) && !isInAdminGroup()) {
        throw new PrintError("no_permission", "The user " + userInfo.getUserName() + " does not have access to the " + optionName + " option in the " + app + " application.");
    }
}

// Determines if the current user is in the administrator group, returns true if the user is, false otherwise.
function isInAdminGroup() {
    var user = userInfo.getUserName();
    service.log_info("Determining if the user " + user + " is in the administrator group.");
    var groupUserSet;

    try {
        groupUserSet = MXServer.getMXServer().getMboSet("GROUPUSER", MXServer.getMXServer().getSystemUserInfo());

        // Get the ADMINGROUP MAXVAR value.
        var adminGroup = MXServer.getMXServer().lookup("MAXVARS").getString("ADMINGROUP", null);

        // Query for the current user and the found admin group.  
        // The current user is determined by the implicity `user` variable.
        sqlFormat = new SqlFormat("userid = :1 and groupname = :2");
        sqlFormat.setObject(1, "GROUPUSER", "USERID", user);
        sqlFormat.setObject(2, "GROUPUSER", "GROUPNAME", adminGroup);
        groupUserSet.setWhere(sqlFormat.format());

        if (!groupUserSet.isEmpty()) {
            service.log_info("The user " + user + " is in the administrator group " + adminGroup + ".");
            return true;
        } else {
            service.log_info("The user " + user + " is not in the administrator group " + adminGroup + ".");
            return false;
        }

    } finally {
        close(groupUserSet);
    }
}

// Cleans up the MboSet connections and closes the set.
function close(set) {
    if (set) {
        set.cleanup();
        set.close();
    }
}

function PrintError(reason, message) {
    Error.call(this, message);
    this.reason = reason;
    this.message = message;
}

// PrintError derives from Error
PrintError.prototype = Object.create(Error.prototype);
PrintError.prototype.constructor = PrintError;


var scriptConfig={
    "autoscript": "STAUTOSCRIPT.PRINTLABEL",
    "description": "Print a Barcode Label",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR"
};