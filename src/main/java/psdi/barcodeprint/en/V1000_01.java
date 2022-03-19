package psdi.barcodeprint.en;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import psdi.script.AutoUpgradeTemplate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.nio.file.FileVisitResult.CONTINUE;

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
