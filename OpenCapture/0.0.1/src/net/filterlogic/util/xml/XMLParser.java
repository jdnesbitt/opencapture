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

/**
 *
 * @author Darron Nesbitt
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
     * Sets value of tag or attribute at xPath.
     * @param xPath
     * @param value
     * @throws java.lang.Exception
     */
    public void setValue(String xPath,String value) throws Exception
    {
        Element result = null;

        try
        {
            try
            {
                XPath xp = new DOMXPath(xPath);

                result = (Element)xp.selectSingleNode(this.doc);
            }
            catch(Exception xpe)
            {
                throw new Exception(xpe.toString());
            }
            
            if(result == null)
                throw new Exception("Unable to locate XPath[" + xPath + "]");

            result.setNodeValue(value);
        }
        catch(Exception e)
        {
            throw new Exception(e.toString());
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
            System.out.println(e);
            result = new ArrayList();
        }
        for(int i=0;i<result.size();i++)
        {
            Element elem = (Element)result.get(i);
            values.add(elem.getChildNodes().item(0).getNodeValue());
        }
        return values;
    }
    
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
     * getValue retrieves a single value located at the specified XPath.
     * @param xPath
     * @return String
     */
    public String getValue(String xPath)
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
            System.out.println(e);
            result = "";
        }
        return result;
    }
    
    public Element getNode(String xPath) throws Exception
    {
        Element elem;

        XPath xpath = new DOMXPath(xPath);
        elem = (Element)xpath.selectSingleNode(this.doc);

        return elem;
    }
    
    public static void main(String[] argv)
    {
        try
        {
            String filePath = "";
            String xPath = "";
            if(argv.length>1)
            {
                filePath = argv[0];
                xPath = argv[1];
            }
            else
            {
                System.out.println("\n\nInvalid parameters.  \n\nusage: java -cp FaxTrax.jar com.jpmchase.util.xml.XMLParser <xml file path> <xpath>\n");
                System.exit(0);
            }
            File XmlDocumentUrl= new File(filePath);
            XMLParser x = new XMLParser();
            String m = ReadWriteTextFile.getContents(XmlDocumentUrl);
            x.parseDocument(m);
            List list = x.getValues(xPath);
            for(int i=0;i<list.size();i++)
                System.out.println("Value " + i + " = " + list.get(i));
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
}
