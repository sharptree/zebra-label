package psdi.zebralabel.en;

import psdi.script.AutoUpgradeTemplate;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Creates the required automation scripts for the Zebra Label printing.
 *
 * @author Jason VenHuizen
 */

@SuppressWarnings("unused")
public class V1000_01 extends AutoUpgradeTemplate {

    /**
     * Creates a new instance of the V1000_01 class.
     *
     * @param con    the database connection.
     * @param params the parameter provided.
     * @param ps     the output stream for logging statements.
     * @throws Exception thrown if an error occurs creating the scripts.
     */
    public V1000_01(Connection con, HashMap params, PrintStream ps) throws Exception {
        super(con, params, ps);
    }

    /**
     * Sets the scriptFileName to `V1000_01`
     *
     * @throws Exception throw if an error occurs while initializing the upgrade template.
     */
    public void init() throws Exception {
        this.scriptFileName = "V1000_01";
        super.init();
    }

    /**
     * {@inerhitDoc}
     *
     * @see AutoUpgradeTemplate#process()
     */
    @Override
    protected void process() throws Exception {
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.LABELS", "psdi/zebralabel/en/resources/stautoscript.zebralabel.labels.js", "Barcode Label Definitions", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTERS", "psdi/zebralabel/en/resources/stautoscript.zebralabel.printers.js", "Barcode Printers", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTLABEL", "psdi/zebralabel/en/resources/stautoscript.zebralabel.printlabel.js", "Print a Barcode Label", "1.0.0", dbIn);

        AutoScriptUtil.createScriptIfNotExists(con, "STAUTOSCRIPT.ZEBRALABEL.LABELCFG", "psdi/zebralabel/en/resources/stautoscript.zebralabel.labelcfg.js", "Barcode Label Configurations", "1.0.0", dbIn);
        AutoScriptUtil.createScriptIfNotExists(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTERCFG", "psdi/zebralabel/en/resources/stautoscript.zebralabel.printercfg.js", "Barcode Printer Configurations", "1.0.0", dbIn);
        super.process();
    }
}
