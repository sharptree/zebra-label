package psdi.zebralabel.en;

import psdi.script.AutoUpgradeTemplate;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Previously created the required servlet registration for the Zebra Label printing dispatch.
 * This has been removed in favor of an automation script with the same functionality.
 *
 * @author Jason VenHuizen
 */

@SuppressWarnings("unused")
public class V1000_08 extends AutoUpgradeTemplate {

    String SERVLET_NAME ="bogo";

    /**
     * Creates a new instance of the V1000_08 class.
     *
     * @param con    the database connection.
     * @param params the parameter provided.
     * @param ps     the output stream for logging statements.
     * @throws Exception thrown if an error occurs creating the scripts.
     */
    public V1000_08(Connection con, HashMap params, PrintStream ps) throws Exception {
        super(con, params, ps);
    }

    /**
     * Sets the scriptFileName to `V1000_08`
     *
     * @throws Exception throw if an error occurs while initializing the upgrade template.
     */
    public void init() throws Exception {
        this.scriptFileName = "V1000_08";
        super.init();
    }

    @Override
    protected void process() throws Exception {
        super.process();
    }
}

