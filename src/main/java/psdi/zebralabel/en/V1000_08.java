package psdi.zebralabel.en;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Creates the required servlet registration for the Zebra Label printing dispatch.
 *
 * @author Jason VenHuizen
 */

@SuppressWarnings("unused")
public class V1000_08 extends AutoUpgradeTemplate {

    private static final String SERVLET_NAME = "ZebraLabelDispatch";

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

        installServlets(new File(getPropDir() + File.separator + ".." + File.separator + "maximouiweb" + File.separator + "webmodule" + File.separator + "WEB-INF" + File.separator + "web.xml"));

        super.process();
    }

    private void installServlets(File webXML) throws Exception {

        boolean zebraLabelDispatchServletInstalled = zebraLabelDispatchServletInstalled(webXML);
        boolean zebraLabelDispatchMappingInstalled = zebraLabelDispatchServletMappingInstalled(webXML);

        if (zebraLabelDispatchServletInstalled && zebraLabelDispatchMappingInstalled) {
            // if all the servlets are already installed then return.
            return;
        }

        // backup the web.xml before performing actions
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File backup = new File(webXML.getParentFile().getPath() + File.separator + "web_" + timestamp + ".xml");
        Path source = Paths.get(webXML.toURI());
        Path dest = Paths.get(backup.toURI());
        Files.copy(source, dest);

        installZebraLabelDispatchServlet(webXML);

    }

    /**
     * Create the zebra label dispatch servlet and mapping entries.
     *
     * @param webXML the web.xml file.
     * @throws Exception thrown if an error occurs while adding the servlet.
     */
    private void installZebraLabelDispatchServlet(File webXML) throws Exception {
        if (!zebraLabelDispatchServletInstalled(webXML)) {
            Document document = loadDocument(webXML);
            insertServlet(SERVLET_NAME, "io.sharptree.maximo.webclient.servlet.ZebraLabelPrintDispatchServlet", "Zebra Label Printing Dispatch Servlet", true, document);
            writeDocument(document, Files.newOutputStream(webXML.toPath()));
        }
        if (!zebraLabelDispatchServletMappingInstalled(webXML)) {
            Document document = loadDocument(webXML);
            insertServletMapping(SERVLET_NAME, "/labeldispatch/*", "Zebra Label Printing Dispatch Servlet Mapping", document);
            writeDocument(document, Files.newOutputStream(webXML.toPath()));
        }
    }

    /**
     * Creates the zebra label dispatch servlet entry
     *
     * @param name          the name of the servlet to add.
     * @param className     the classname for the servlet to add.
     * @param comment       a comment to add to the entry.
     * @param loadOnStartup flag indicating that the servlet should load on startup.
     * @param document      the web.xml as an XML document object.
     * @throws Exception thrown if an error occurs while adding the servlet.
     */
    @SuppressWarnings("SameParameterValue")
    private void insertServlet(String name, String className, String comment, @SuppressWarnings("SameParameterValue") boolean loadOnStartup, Document document) throws Exception {

        if (name == null || name.isEmpty()) {
            throw new Exception("A servlet name is required.");
        }
        if (className == null || className.isEmpty()) {
            throw new Exception("A servlet class name is required.");
        }

        Element servlet = document.createElement("servlet");
        Element servletName = document.createElement("servlet-name");
        Element servletClass = document.createElement("servlet-class");
        servletName.setTextContent(name);
        servletClass.setTextContent(className);

        Comment servletComment = null;

        if (comment != null && !comment.isEmpty()) {
            servletComment = document.createComment(comment);
        }

        servlet.appendChild(servletName);
        servlet.appendChild(servletClass);

        if (loadOnStartup) {
            Element startup = document.createElement("load-on-startup");
            startup.setTextContent("1");
            servlet.appendChild(startup);
        }

        Node firstServlet = document.getElementsByTagName("servlet").item(0);
        document.setStrictErrorChecking(false);

        firstServlet.insertBefore(servlet, firstServlet);
        if (servletComment != null) {
            //TODO this ends up putting the comments above both the servlet entries.  Review to properly add comments.
            servlet.insertBefore(servletComment, servlet);
        }

    }

    /**
     * Creates the zebra label dispatch servlet mapping entry
     *
     * @param name     the name of the servlet to add.
     * @param pattern  the matching mapping pattern
     * @param comment  a comment to add to the entry.
     * @param document the web.xml as an XML document object.
     * @throws Exception thrown if an error occurs while adding the servle
     */
    @SuppressWarnings("SameParameterValue")
    private void insertServletMapping(String name, String pattern, String comment, Document document) throws Exception {

        if (name == null || name.isEmpty()) {
            throw new Exception("A servlet name is required.");
        }
        if (pattern == null || pattern.isEmpty()) {
            throw new Exception("A servlet mapping pattern is required.");
        }

        Element servletMapping = document.createElement("servlet-mapping");
        Element servletName = document.createElement("servlet-name");
        Element urlPattern = document.createElement("url-pattern");

        servletName.setTextContent(name);
        urlPattern.setTextContent(pattern);

        Comment servletComment = null;

        if (comment != null && !comment.isEmpty()) {
            servletComment = document.createComment(comment);
        }

        servletMapping.appendChild(servletName);
        servletMapping.appendChild(urlPattern);

        Node firstServlet = document.getElementsByTagName("servlet-mapping").item(0);
        document.setStrictErrorChecking(false);

        firstServlet.insertBefore(servletMapping, firstServlet);
        if (servletComment != null) {
            firstServlet.insertBefore(servletComment, servletMapping);
        }
    }

    /**
     * Writes the XML document formatted with proper indentations.
     *
     * @param document the XML document to write.
     * @param output   the outpstream to write the document to.
     * @throws TransformerException thrown if an error occurs formatting the XML document.
     */
    private void writeDocument(Document document, OutputStream output) throws TransformerException {
        @SuppressWarnings("StringBufferReplaceableByString")
        String xslt = new StringBuilder("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">")
                .append("\n    <xsl:output indent=\"yes\" cdata-section-elements=\"cdata-other-elements\"/>")
                .append("\n    <xsl:strip-space elements=\"*\"/>")
                .append("\n    <xsl:template match=\"@*|node()\">")
                .append("\n        <xsl:copy>")
                .append("\n            <xsl:apply-templates select=\"@*|node()\"/>")
                .append("\n        </xsl:copy>")
                .append("\n    </xsl:template>")
                .append("\n    <xsl:template match=\"*/text()[not(normalize-space())]\" />")
                .append("</xsl:stylesheet>").toString();

        InputStream xsltStream = new ByteArrayInputStream(xslt.getBytes());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltStream));

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        transformer.transform(new DOMSource(document), new StreamResult(output));

    }

    /**
     * Loads an XML document from a file.
     *
     * @param path the file to load the XML document from.
     * @return the XML contents as an XML document object.
     * @throws ParserConfigurationException thrown if an error occurs parsing the document.
     * @throws IOException                  thrown if an error occurs reading the XML file.
     * @throws SAXException                 thrown if an error occurs during the SAX processing.
     */
    private Document loadDocument(File path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(path);
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Is the Zebra label printing dispatch servlet entry present in the web.xml.
     *
     * @param file the web.xml file.
     * @return true if the  Zebra label printing dispatch servlet has been registered, false otherwise.
     * @throws Exception thrown if an error occurs checking for the servlet.
     */
    private boolean zebraLabelDispatchServletInstalled(File file) throws Exception {
        return nodeInstalled("/web-app/servlet[servlet-name=\"" + SERVLET_NAME + "\"]", file);
    }

    /**
     * Is the Zebra label printing dispatch servlet mapping entry present in the web.xml.
     *
     * @param file the web.xml file.
     * @return true if the  Zebra label printing dispatch servlet mapping has been registered, false otherwise.
     * @throws Exception thrown if an error occurs checking for the servlet.
     */
    private boolean zebraLabelDispatchServletMappingInstalled(File file) throws Exception {
        return nodeInstalled("/web-app/servlet-mapping[servlet-name=\"" + SERVLET_NAME + "\"]", file);
    }

    /**
     * Checks for the provided Xpath.
     *
     * @param path the Xpath to check for.
     * @param file the file from which to check for the Xpath in.
     * @return true if the path is found, false otherwise.
     * @throws Exception thrown if an error occurs verifying the path.
     */
    private boolean nodeInstalled(String path, File file) throws Exception {
        return ((NodeList) XPathFactory.newInstance()
                .newXPath()
                .compile(path)
                .evaluate(
                        DocumentBuilderFactory
                                .newInstance()
                                .newDocumentBuilder()
                                .parse(file), XPathConstants.NODESET)
        ).getLength() == 1;
    }
}

