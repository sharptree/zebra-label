package psdi.barcodeprint.en;

import psdi.configure.UpgConstants;
import psdi.configure.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Utility class that adds or updates an automation script to Maximo from the UpdateDB / DBC process.
 */
public abstract class AutoScriptUtil {

    @SuppressWarnings("unused")
    public static void createOrUpdateObjectLaunchPoint(Connection connection, String scriptName, String launchPointName, String description, String objectName, int objectEvent, int dbIn) throws Exception {
        if (connection == null) {
            throw new Exception("The connection parameter is required and cannot be null");
        }
        if (scriptName == null || scriptName.trim().isEmpty()) {
            throw new Exception("The scriptName parameter is required and cannot be null or blank.");
        }
        if (launchPointName == null || launchPointName.trim().isEmpty()) {
            throw new Exception("The launchPointName parameter is required and cannot be null or blank.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new Exception("The scriptName parameter is required and cannot be null or blank.");
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new Exception("The objectName parameter is required and cannot be null or blank.");
        }

        if (dbIn != UpgConstants.ORACLE && dbIn != UpgConstants.SQLSERVER && dbIn != UpgConstants.DB2) {
            throw new Exception("The dbIn parameter must be 1 for Oracle, 2 for SQLServer, or 3 for DB2");
        }

        if (objectEvent <= 0) {
            throw new Exception("The objectEvent parameter must be greater than 0");
        }

        Util util = new Util(connection, null, null, true);
        PreparedStatement launchPointCheck = connection.prepareStatement("select 1 from scriptlaunchpoint where autoscript = ? and launchpointname = ?");
        PreparedStatement autoScriptInsert;
        if (dbIn == UpgConstants.SQLSERVER) {
            autoScriptInsert = connection.prepareStatement("insert into scriptlaunchpoint(" +
                    "scriptlaunchpointid, " +
                    "launchpointname, " +
                    "autoscript, " +
                    "description, " +
                    "launchpointtype, " +
                    "objectname, " +
                    "objectevent, " +
                    "active) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            autoScriptInsert = connection.prepareStatement("insert into scriptlaunchpoint(" +
                    "scriptlaunchpointid, " +
                    "launchpointname, " +
                    "autoscript, " +
                    "description, " +
                    "launchpointtype, " +
                    "objectname, " +
                    "objectevent, " +
                    "active, " +
                    "rowstamp) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }

        ResultSet resultSet = null;
        try {

            launchPointCheck.setString(1, scriptName);
            launchPointCheck.setString(2, launchPointName);
            resultSet = launchPointCheck.executeQuery();
            if (!resultSet.next()) {
                autoScriptInsert.setLong(1, getNextId(connection, "SCRIPTLAUNCHPOINTSEQ", dbIn, util));
                autoScriptInsert.setString(2, launchPointName);
                autoScriptInsert.setString(3, scriptName);
                autoScriptInsert.setString(4, description);
                autoScriptInsert.setString(5, "OBJECT");
                autoScriptInsert.setString(6, objectName.toUpperCase());
                autoScriptInsert.setInt(7, objectEvent);
                autoScriptInsert.setString(8, util.getYes());

                if (dbIn != UpgConstants.SQLSERVER) {
                    autoScriptInsert.setInt(9, 1);
                }

                autoScriptInsert.execute();
            }

        } finally {
            close(resultSet);
            close(launchPointCheck);
        }
    }

    public static void createOrUpdateScript(Connection connection, String scriptName, String scriptPath, String description, String version, int dbIn) throws Exception {

        if (connection == null) {
            throw new Exception("The connection parameter is required and cannot be null");
        }
        if (scriptName == null || scriptName.trim().isEmpty()) {
            throw new Exception("The scriptName parameter is required and cannot be null or blank.");
        }
        if (scriptPath == null || scriptPath.trim().isEmpty()) {
            throw new Exception("The scriptPath parameter is required and cannot be null or blank.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new Exception("The description parameter is required and cannot be null or blank.");
        }
        if (version == null || version.trim().isEmpty()) {
            throw new Exception("The version parameter is required and cannot be null or blank.");
        }

        if (dbIn != UpgConstants.ORACLE && dbIn != UpgConstants.SQLSERVER && dbIn != UpgConstants.DB2) {
            throw new Exception("The dbIn parameter must be 1 for Oracle, 2 for SQLServer, or 3 for DB2");
        }

        PreparedStatement autoScriptCheck = connection.prepareStatement("select 1 from autoscript where autoscript = ?");
        PreparedStatement autoScriptUpdate;

        autoScriptUpdate = connection.prepareStatement("update autoscript set source = ?, version = ?, description = ? where autoscript = ?");

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(AutoScriptUtil.class.getClassLoader().getResourceAsStream(scriptPath))));
        StringBuilder source = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            source.append(line).append("\n");
        }

        ResultSet resultSet = null;
        try {
            autoScriptCheck.setString(1, scriptName);
            resultSet = autoScriptCheck.executeQuery();
            if (resultSet.next()) {
                autoScriptUpdate.setString(1, source.toString());
                autoScriptUpdate.setString(2, version);
                autoScriptUpdate.setString(3, description);
                autoScriptUpdate.setString(4, scriptName);
                autoScriptUpdate.execute();
            } else {
                // insert the script.
                addScript(connection, scriptName, description, version, source.toString(), dbIn);
            }

        } finally {
            close(autoScriptCheck);
            close(resultSet);
            close(reader);
            close(autoScriptUpdate);
        }
    }

    private static void addScript(Connection connection, String scriptName, String description, String version, String source, int dbIn) throws Exception {
        Util util = new Util(connection, null, null, true);

        PreparedStatement autoScriptInsert;
        if (dbIn == UpgConstants.SQLSERVER) {
            autoScriptInsert = connection.prepareStatement("insert into autoscript(" +
                    "autoscript, " +
                    "status, " +
                    "description, " +
                    "source, " +
                    "createddate, " +
                    "statusdate, " +
                    "changedate, " +
                    "owner, " +
                    "createdby, " +
                    "changeby, " +
                    "hasld, " +
                    "langcode, " +
                    "scriptlanguage, " +
                    "userdefined, " +
                    "loglevel, " +
                    "interface, " +
                    "active, " +
                    "version, " +
                    "autoscriptid)" +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            autoScriptInsert = connection.prepareStatement("insert into autoscript(" +
                    "autoscript, " +
                    "status, " +
                    "description, " +
                    "source, " +
                    "createddate, " +
                    "statusdate, " +
                    "changedate, " +
                    "owner, " +
                    "createdby, " +
                    "changeby, " +
                    "hasld, " +
                    "langcode, " +
                    "scriptlanguage, " +
                    "userdefined, " +
                    "loglevel, " +
                    "interface, " +
                    "active, " +
                    "version, " +
                    "autoscriptid, " +
                    "rowstamp)" +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }

        try {
            autoScriptInsert.setString(1, scriptName);
            autoScriptInsert.setString(2, "Active");
            autoScriptInsert.setString(3, description);
            autoScriptInsert.setString(4, source);
            autoScriptInsert.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            autoScriptInsert.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            autoScriptInsert.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            autoScriptInsert.setString(8, "MAXADMIN");
            autoScriptInsert.setString(9, "MAXADMIN");
            autoScriptInsert.setString(10, "MAXADMIN");
            autoScriptInsert.setString(11, util.getNo());
            autoScriptInsert.setString(12, "EN");
            autoScriptInsert.setString(13, getVersion() > 7 ? "nashorn" : "javascript");
            autoScriptInsert.setString(14, util.getYes());
            autoScriptInsert.setString(15, "ERROR");
            autoScriptInsert.setString(16, util.getNo());
            autoScriptInsert.setString(17, util.getYes());
            autoScriptInsert.setString(18, version);
            autoScriptInsert.setLong(19, getNextId(connection, "AUTOSCRIPTSEQ", dbIn, util));

            if (dbIn != UpgConstants.SQLSERVER) {
                autoScriptInsert.setInt(20, 1);
            }

            autoScriptInsert.execute();

        } finally {
            close(autoScriptInsert);
        }
    }

    private static long getNextId(Connection connection, String sequenceName, int dbIn, Util util) throws Exception {
        if (dbIn == UpgConstants.SQLSERVER) {
            return Long.parseLong(util.getNextSequenceValueForSqlServer(sequenceName));
        } else {
            PreparedStatement nextIdStatement = null;
            ResultSet resultSet = null;
            try {
                nextIdStatement = connection.prepareStatement("select " + sequenceName + ".nextval from dummy_table");
                resultSet = nextIdStatement.executeQuery();
                resultSet.next();
                return resultSet.getLong(1);
            } finally {
                close(nextIdStatement);
                close(resultSet);
            }
        }
    }

    private static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // do nothing as there is nothing to do.
            }
        }
    }

    private static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // do nothing as there is nothing to do.
            }
        }
    }

    private static void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // do nothing as there is nothing to do.
            }
        }
    }

    private static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }
}

