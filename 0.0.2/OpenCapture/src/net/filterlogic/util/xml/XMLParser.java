/*
Copyright [2008] [Filter Logic]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package net.filterlogic.util.xml;

import com.sun.org.apache.xpath.internal.XPathAPI;
import net.filterlogic.io.ReadWriteTextFile;

import java.util.List;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import java.io.*;
import org.w3c.dom.*;
import org.jaxen.*;
import org.jaxen.dom.DOMXPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.filterlogic.util.NamedValueList;
//import org.eclipse.persistence.eis.EISException;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
public class XMLParser 
{
    // Step 1: create a DocumentBuilderFactory and setNamespaceAware
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private Document doc = null;
    /** Creates a new instance of XMLParser */
    public XMLParser() 
    {
    }
    /**
     * Getter for Document object.
     * @return org.w3c.dom.Document object.
     */
    public Document getXmlDocument() {
        return doc;
    }
    
    /**
     * Setter for Document object.
     * @param domDocument
     */
    public void setXmlDocument(Document domDocument)
    {
        this.doc = domDocument;
    }
    /**
     * Parses the xml passed into a org.w3c.dom.Document object.
     * @param xml
     * @throws java.lang.Exception
     */
    public void parseDocument(String xml) throws Exception
    {
        try
        {
            dbf.setNamespaceAware(true);
            // Step 2: create a DocumentBuilder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Step 3: parse the input file to get a Document object
            doc = db.parse(new InputSource(new StringReader(xml)));
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }
    
    /**
     * Load xml document from a file and parse to 
     * an org.w3c.dom.Document.
     * @param filename
     * @throws java.lang.Exception
     */
    public void loadDocument(String filename) throws Exception
    {
        try
        {
            //dbf.setNamespaceAware(true);
            // Step 2: create a DocumentBuilder
            //DocumentBuilder db = dbf.newDocumentBuilder();
            // Step 3: parse the input file to get a Document object
            //doc = db.parse(new File(filename));
            String xml = ReadWriteTextFile.getContents(new File(filename));
            parseDocument(xml);
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }

//    public void loadDocument(String filename) throws Exception
//    {
//        try
//        {
//            //dbf.setNamespaceAware(true);
//            // Step 2: create a DocumentBuilder
//            //DocumentBuilder db = dbf.newDocumentBuilder();
//            // Step 3: parse the input file to get a Document object
//            //doc = db.parse(new File(filename));
//            String xml = ReadWriteTextFile.getContents(new File(filename));
//            parseDocument(xml);
//        }
//        catch(Exception e)
//        {
//            throw new Exception(e);
//        }
//    }
    
    /**
     * Saves a org.w3c.dom.Document object to a file.
     * @param filename
     * @throws java.lang.Exception
     */
    public void saveDocument(String filename) throws Exception
    {
        try 
        {
            // Prepare the DOM document for writing
            Source source = new DOMSource(this.doc);
    
            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);
    
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        }
        catch (Exception e) 
        {
            throw new Exception(e);
        }
    }
    
    /**
     * Add xml node with or without attributes.
     * @param xPath XPath to where node should be added.
     * @param nodeName Name of new node.
     * @param attributes NamedNodeList of attributes.
     * @throws java.lang.Exception
     */
    public void addValue(String xPath, String nodeName, NamedValueList attributes) throws Exception
    {
        Element elem;
        
        if(nodeName.trim().length()<1 || xPath.trim().length()<1)
            throw new Exception("Either XPath, NodeName or both are empty!");
        
        List attList = null;
        
        Element newNode = this.doc.createElement(nodeName);
        
        // make sure there are values in attribute
        if(attributes.size()>0)
            attList = attributes.getOrderedNameList();
     
        if(attList != null)
        {
            // loop through attributes
            for(int i=0;i<attList.size();i++)
            {
                String attName = (String)attList.get(i);
                String attVal = (String)attributes.get(attName);
                
                // add attribute to element
                newNode.setAttribute(attName, attVal);
            }
        }
            
        elem = getNode(xPath);
        
        if(elem == null)
            throw new Exception("XPath[" + xPath + "] not found!");

        elem.appendChild(newNode);
    }

    /**
     * appendNode appends the specified node to the source document at XPath.
     * @param xPath where to append new node.
     * @param srcDocument document to append node to.
     * @param appendNode Node to append.
     * @param deep Set to true if import into source document should be deep import.
     * @throws Exception
     */
    public static void appendNode(String xPath, Document srcDocument,Node appendNode, boolean deep) throws Exception
    {
    	try
		{
            XPath xpath = new DOMXPath(xPath);
            Node result = (Node)xpath.selectSingleNode(srcDocument);

    		Node res = srcDocument.importNode(appendNode,deep);

    		result.appendChild(res);
		}
    	catch(Exception e)
		{
    		throw new Exception(e.toString());
		}
    }

    /**
     * Sets value of tag or attribute at xPath.
     * @param xPath
     * @param value
     * @throws java.lang.Exception
     */
    public void setValue(String xPath,String value) throws Exception
    {
        Element result = null;
        Object elem;
        
        try
        {
            try
            {
                XPath xpath = new DOMXPath(xPath);
                elem = xpath.selectSingleNode(this.doc);
                if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
                    ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).setNodeValue(value);
                else
                    ((Element)elem).getChildNodes().item(0).setNodeValue(value);

            }
            catch(Exception xpe)
            {
                throw new Exception(xpe.toString());
            }
        }
        catch(Exception e)
        {
            throw new Exception("Error setting value " + value + " @ " + xPath + "\n" + e.toString());
        }
    }

    /**
     * Set named attributes value at xpath.
     *
     * @param xPath
     * @param attributeName
     * @param value
     * @throws Exception
     */
    public void setValue(String xPath, String attributeName,String value) throws Exception
    {
        Element result = null;
        Object elem;

        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(this.doc);
            if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
                ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).getAttributes().getNamedItem(attributeName).setNodeValue(value);
            else
                if(elem.getClass().toString().contains("DeferredElementNSImpl"))
                    ((com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl)elem).setAttribute(attributeName, value);
                else
                    ((Element)elem).getChildNodes().item(0).getAttributes().getNamedItem(attributeName).setNodeValue(value);

        }
        catch(Exception e)
        {
            throw new Exception("Error setting value " + value + " @ " + xPath + ", attributeName= " + attributeName + "\n" + e.toString());
        }
    }

    /**
     * Delete node at XPath.
     *
     * @param xPath
     * @throws Exception
     */
    public void deleteNode(String xPath) throws Exception
    {
        Element result = null;
        Object elem;

        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(this.doc);
            
            if(elem != null)
                this.doc.removeChild((Node)elem);
            else
                throw new Exception("XPath[" + xPath + "] doesn't exist!");

//            if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
//                //((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem);
//                this.doc.removeChild((Node)elem);
//            else
//                ((Element)elem).getChildNodes().item(0).setNodeValue(value);

        }
        catch(Exception e)
        {
            throw new Exception("Error deleting node  @ " + xPath + "\n" + e.toString());
        }
    }
    
    /**
     * getValues retrieves the value(s) located at the specified XPath.
     * @param xPath
     * @return java.util.List 
     */
    public List getValues(String xPath)
    {
        List result;
        List <String>values = new ArrayList<String>();
        try
        {
            XPath xpath = new DOMXPath(xPath);
            result = (List)xpath.selectNodes(this.doc);
        }
        catch(Exception e)
        {
            System.out.println("Error retrieving values @ "+ xPath + "\n" + e.toString());
            result = new ArrayList();
        }
        for(int i=0;i<result.size();i++)
        {
            Element elem = (Element)result.get(i);
            values.add(elem.getChildNodes().item(0).getNodeValue());
        }
        return values;
    }

    /**
     * Get actual java.util.List containing Node objects
     * @param xPath
     * @param throwException
     * @return List of Node objects.
     * @throws Exception
     */
    public List getNodeList(String xPath, boolean throwException) throws Exception
    {
        NodeList result = null;
        List<Node> list = new ArrayList<Node>();
        try
        {
//            XPath xpath = new DOMXPath(xPath);
//            result = (List)xpath.selectNodes(this.doc);
            result = XPathAPI.selectNodeList((Node)this.doc, xPath);
        }
        catch(Exception e)
        {
            System.out.println(e);

            if(throwException)
                throw new Exception("Error retrieving node list: " + xPath + "\n" + e.toString());
            else
                list = new ArrayList<Node>();
        }

        // if null list returned, create empty array list
        if (result != null)
        {
            for (int i = 0; i < result.getLength(); i++)
            {
                list.add(result.item(i));
            }
        }

        return list;
    }

    /**
     * Returns a List of Maps containing attribute names/values.
     *
     * @param xPath
     * @return List of Maps
     */
    public List getNodeListOfStringArray(String xPath)
    {
        List result;
        List <String[]>values = new ArrayList<String[]>();

        try
        {
            XPath xpath = new DOMXPath(xPath);
            result = (List)xpath.selectNodes(this.doc);
        }
        catch(Exception e)
        {
            System.out.println(e);
            result = new ArrayList();
        }

        for(int i=0;i<result.size();i++)
        {
            Node elem = (Node)result.get(i);

            String[] map = new String[i];
            int nodeCount = elem.getChildNodes().getLength();

            for(int m=0;m<nodeCount;m++)
            {
                Node node = null;

                try
                {
                    node = elem.getChildNodes().item(m);
                }
                catch(Exception ne)
                {
                    node = null;
                    System.out.println("Error getting node:\n");
                    ne.printStackTrace();
                }

                if(node!=null)
                    map[m] = getValue(".", node, "");
                else
                    map[m] = "";

            }

            values.add(map);
        }

        return values;
    }


    /**
     * Returns a List of Maps containing attribute names/values.
     *
     * @param xPath
     * @return List of Maps
     */
    public List getNodeList(String xPath)
    {
        List result;
        List <Map>values = new ArrayList<Map>();
        
        try
        {
            XPath xpath = new DOMXPath(xPath);
            result = (List)xpath.selectNodes(this.doc);
        }
        catch(Exception e)
        {
            System.out.println(e);
            result = new ArrayList();
        }
        
        for(int i=0;i<result.size();i++)
        {
            Node elem = (Node)result.get(i);

            Map <String,String> map = new HashMap<String,String>();
            
            for(int m=0;m<elem.getAttributes().getLength();m++)
            {
                map.put(elem.getAttributes().item(m).getNodeName(), elem.getAttributes().item(m).getNodeValue());
            }

            values.add(map);
        }

        return values;
    }
    
    /**
     * Check if xPath exists.
     * 
     * @param xPath
     * @return True if exists, else false.
     */
    public boolean Exists(String xPath) throws Exception
    {
        boolean result=false;
        Object elem;
        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(this.doc);
//            if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
//                result = ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).getValue();
//            else
//                result = ((Element)elem).getChildNodes().item(0).getNodeValue();

            if(elem != null)
                result = true;
        }
        catch(Exception e)
        {
            throw new Exception("Error retrieving data @ " + xPath + "\n" + e.toString());
        }

        return result;
    }

    /**
     * getValue retrieves a single value located at the specified XPath.
     * @param xPath
     * @return String
     */
    public String getValue(String xPath) throws Exception
    {
        String result="";
        Object elem;
        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(this.doc);

            if(elem != null)
            {
                if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
                    result = ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).getValue();
                else
                    result = ((Element)elem).getChildNodes().item(0).getNodeValue();
            }
        }
        catch(Exception e)
        {
            throw new Exception("Error retrieving data @ " + xPath + "\n" + e.toString());
        }

        return result;
    }
    
    /**
     * getValue retrieves a single value located at the specified XPath.
     * @param xPath
     * @param defaultValue Value to return if xpath doesn't exists or error occurs.
     * @return String containing value at xpath.
     */
    public String getValue(String xPath,String defaultValue)
    {
        String result="";
        
        Object elem;
        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(this.doc);
            if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
                result = ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).getValue();
            else
                result = ((Element)elem).getChildNodes().item(0).getNodeValue();
        }
        catch(Exception e)
        {
            System.out.println("Error retrieving data @ " + xPath + "\n" + e.toString());
            result = defaultValue;
        }
        
        return result;
    }

    public String getValue(String xPath, Node node,String defaultValue)
    {
        String result="";

        Object elem;
        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = xpath.selectSingleNode(node);
            if(elem.getClass().toString().contains("DeferredAttrNSImpl"))
                result = ((com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl)elem).getValue();
            else
                result = ((Element)elem).getChildNodes().item(0).getNodeValue();
        }
        catch(Exception e)
        {
            System.out.println("Error retrieving data @ " + xPath + "\n" + e.toString());
            result = defaultValue;
        }

        return result;
    }
    
    /**
     * Get node at XPath.
     * @param xPath 
     * @return Return node.
     * @throws java.lang.Exception
     */
    public Element getNode(String xPath) throws Exception
    {
        Element elem = null;
        try
        {
            XPath xpath = new DOMXPath(xPath);
            elem = (Element)xpath.selectSingleNode(this.doc);
        }
        catch(Exception e)
        {
            System.out.println("Error retrieving node @ " + xPath + "\n" + e.toString());
        }
        
        return elem;
    }

    /**
     * Return attributes as string.
     * @param node Node that contains attributes to retrieve.
     * @return String containing attributes.
     */
    public String getXMLAttributeStructure(Node node)
    {
        String attributes = "";
        
        try
        {
            NamedNodeMap nnm = node.getAttributes();
            
            for(int i=0;i<nnm.getLength();i++)
            {
                Node attr = nnm.item(i);
                
                attributes += " ";

                attributes += attr.getNodeName() + "=\"\"";
            }
        }
        catch(Exception e)
        {
            
        }
        
        return attributes;
    }

    /**
     * Get node as XML string.
     * @param node Node to return as string.
     * @return String containing XML string.
     */
    public String getNodeAsXML(Node node)
    {
        String xml = "";

        try
        {
            NodeList nodes = node.getChildNodes();
            
            for(int i=0;i<nodes.getLength();i++)
            {
                Node theNode = nodes.item(i);
                
                if(!theNode.getNodeName().startsWith("#"))
                {
                    if(theNode.getNodeName().equals("Test"))
                        System.out.println("Test found!!");
                    
                    xml += "<" + theNode.getNodeName();

                    if(theNode.hasAttributes())
                        xml += getXMLAttributeStructure(theNode);

                    if(theNode.getChildNodes().getLength()>0)
                    {
                        xml += ">";

                        xml += getNodeAsXML(theNode);
                        
                        xml += "</" + theNode.getNodeName() + ">";
                    }
                    else
                    {
                        xml += "/>";
                    }
                }
            }
        }
        catch(Exception e)
        {
            
        }
        return xml;
    }
    
    /**
     * Get the current XML document structure.
     * @return String containing current XML document structure.
     */
    public String getXMLStructure()
    {
        Node node;
        StringBuffer sb = new StringBuffer();
        
        try
        {
            XPath xpath = new DOMXPath("/");

            node = (Node)xpath.selectSingleNode(this.doc);

            if(!node.getNodeName().startsWith("#"))
            {
                sb.append("<" + node.getNodeName());

                if(node.getChildNodes().getLength()<1)
                {
                    //sb.append(getNodeAsXML(node));

                    sb.append("/>");
                }
                else
                {
                    sb.append(">");

                    sb.append(getNodeAsXML(node));
                }
            }
            else
                sb.append(getNodeAsXML(node));
        }
        catch(Exception e)
        {
            
        }
        
        return sb.toString();
    }

    /**
     * Create xml document using an xml string.
     * @param strXML
     * @return Codument element
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static final Element createDocument(String strXML)
        throws ParserConfigurationException, SAXException, IOException
    {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource sourceXML = new InputSource(new StringReader(strXML));
        Document xmlDoc = db.parse(sourceXML);
        Element e = xmlDoc.getDocumentElement();
        e.normalize();
        return e;
    }

    /**
     * Pretty print XML.
     * @param xml XML string
     * @param out Output writer object.
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws javax.xml.transform.TransformerFactoryConfigurationError
     * @throws javax.xml.transform.TransformerException
     */
    public static final void prettyPrint(Node xml, Writer out)
        throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException
    {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.transform(new DOMSource(xml), new StreamResult(out));
    }

    
    public static void main(String[] argv)
    {
        try
        {
            String filePath = "/home/dnesbitt/NetBeansProjects/OCModules/config/carrierfiles.xml";
            //String filePath = "/home/dnesbitt/NetBeansProjects/OCModules/config/testXMLClassification.xml";
            
            String xPath = "";
//            if(argv.length>1)
//            {
//                filePath = argv[0];
//                xPath = argv[1];
//            }
//            else
//            {
//                System.out.println("\n\nInvalid parameters.  \n\nusage: java -cp FaxTrax.jar com.jpmchase.util.xml.XMLParser <xml file path> <xpath>\n");
//                System.exit(0);
//            }
            
            //File XmlDocumentUrl= new File(filePath);
            XMLParser x = new XMLParser();
            //String m = ReadWriteTextFile.getContents(XmlDocumentUrl);
            x.loadDocument(filePath);

            String xml = "";
            
            xml = x.getXMLStructure();

            System.out.println("MD5: " + net.filterlogic.util.MD5.generateMD5(xml));
            System.out.println(xml);
            
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Creates an empty DOM  Document object.
     * @return org.w3c.dom.Document object.
     * @throws java.lang.Exception
     */
    public static Document createDocument() throws Exception
    {
        Document xmlDoc;
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        xmlDoc = builder.newDocument();
        return xmlDoc;
    }

    /**
     * Get a list of nodes at XPath
     *
     * @param xPath XPath to search
     * @param node Node to search
     * @return Returns a list of Node object.
     * @throws java.lang.Exception
     */
    public List getNodes(String xPath, Object node) throws Exception {
        NodeList result;
        List<Node> list = new ArrayList<Node>();

        try {
            //XPath xpath = new DOMXPath(xPath);
            //result = (List)xpath.selectNodes(node);
            result = XPathAPI.selectNodeList((Node) node, xPath);
        } catch (Exception e) {
            throw new Exception("getNodes failed retrieving nodelist @ [" + xPath + "]." + e.toString());
        }

        // if null list returned, create empty array list
        if (result != null) {
            for (int i = 0; i < result.getLength(); i++) {
                list.add(result.item(i));
            }
        }


        return list;
    }

    /**
     * getValue retrieves a single value located at the specified XPath.
     *
     * @param xPath
     * @param node Node to retrieve value from.
     * @return String containing value at xpath.
     */
    public String getValue(String xPath, Object node) {
        String result = "";
        Node elem;
        try {
            elem = XPathAPI.selectSingleNode((Node) node, xPath);

            result = elem.getNodeValue();

        } catch (Exception e) {
            System.out.println(e);
            result = "";
        }

        return result;
    }


}
