package psdi.barcodeprint.en;

import psdi.script.AutoUpgradeTemplate;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.HashMap;

@SuppressWarnings("unused")
public class V1000_01 extends AutoUpgradeTemplate {

    public V1000_01(Connection con, HashMap params, PrintStream ps) throws Exception {
        super(con, params, ps);
    }

    public void init() throws Exception {
        this.scriptFileName = "V1000_01";
        super.init();
    }

    @Override
    protected void process() throws Exception {
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.LABELCFG","psdi/barcodeprint/en/resources/stautoscript.zebralabel.labelcfg.js","Barcode Label Configurations", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.LABELS","psdi/barcodeprint/en/resources/stautoscript.zebralabel.labels.js","Barcode Label Definitions", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTERCFG","psdi/barcodeprint/en/resources/stautoscript.zebralabel.printercfg.js","Barcode Printer Configurations", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTERS","psdi/barcodeprint/en/resources/stautoscript.zebralabel.printers.js","Barcode Printers", "1.0.0", dbIn);
        AutoScriptUtil.createOrUpdateScript(con, "STAUTOSCRIPT.ZEBRALABEL.PRINTLABEL","psdi/barcodeprint/en/resources/stautoscript.zebralabel.printlabel.js","Print a Barcode Label", "1.0.0", dbIn);

        super.process();
    }
}
