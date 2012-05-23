/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util.xml;

import com.sun.org.apache.xpath.internal.XPathAPI;
import java.net.*;
import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

//import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;


/**
 *
 * @author dnesbitt
 */
public class XSLTransform
{

    public static Document transform(Document source, String xsltPath) throws FileNotFoundException,TransformerException
    {
        Document resultDoc = null;
        DOMResult resultDom = new DOMResult();

        // Use the static TransformerFactory.newInstance() method to instantiate
        // a TransformerFactory. The javax.xml.transform.TransformerFactory
        // system property setting determines the actual class to instantiate --
        // org.apache.xalan.transformer.TransformerImpl.
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Use the TransformerFactory to instantiate a Transformer that will work with
        // the stylesheet you specify. This method call also processes the stylesheet
        // into a compiled Templates object.
        // You will need to replace "amazon_search.xsl" with your xsl style sheet.
        // Be sure to read the section in the project description on handling namespaces.
        Transformer transformer = tFactory.newTransformer(new StreamSource(new FileInputStream(xsltPath)));

        // Use the Transformer to apply the associated Templates object (your xsl style sheet)
        // to the returned XML document.
        // The result is put in the file "as.html" in the same directory.
        transformer.transform(new DOMSource(source), resultDom);

        resultDoc = (Document)resultDom.getNode();

        return resultDoc;
    }

    public static void transform(Document source, String xsltPath, String outputPath) throws FileNotFoundException,TransformerException
    {
        DOMResult resultDom = new DOMResult();

        // Use the static TransformerFactory.newInstance() method to instantiate
        // a TransformerFactory. The javax.xml.transform.TransformerFactory
        // system property setting determines the actual class to instantiate --
        // org.apache.xalan.transformer.TransformerImpl.
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Use the TransformerFactory to instantiate a Transformer that will work with
        // the stylesheet you specify. This method call also processes the stylesheet
        // into a compiled Templates object.
        // You will need to replace "amazon_search.xsl" with your xsl style sheet.
        // Be sure to read the section in the project description on handling namespaces.
        Transformer transformer = tFactory.newTransformer(new StreamSource(new FileInputStream(xsltPath)));

        // Use the Transformer to apply the associated Templates object (your xsl style sheet)
        // to the returned XML document.
        // The result is put in the file "as.html" in the same directory.
        transformer.transform(new DOMSource(source),
                new StreamResult(new FileOutputStream(outputPath)));

    }

// public static void main(String[] args)  {
//
//      // enter your password using the PasswordField utility class, to mask it.  See above for a link to the source.
//      String password = "";
//      //System.out.println("The password entered is: "+password);
//      // replace 'your_sub_id'with your own subscription id, and replace the test keywords with the
//      //actual keywords entered from the command line.
//      String request = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&SubscriptionId=your_sub_id&Operation=ItemSearch&Keywords=cloud%20atlas&SearchIndex=Books&ResponseGroup=Request,Small&Version=2004-11-10";
//
//    try {
//        //URL url = new URL(request);
//        // note: you will need to authenticate with proxy server before initiating the http request
//        // do not make more than one request to amazon per second!
//        //System.out.println("starting sleep...");
//        //Thread.sleep(1000);
//        //System.out.println("finishing sleep...");
//
//        //URLConnection conn = url.openConnection();
//        // parse the response stream, which will be XML
//        FileInputStream is = new FileInputStream("");
//        InputSource in = new InputSource(is);
//        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
//        dfactory.setNamespaceAware(true);
//        Document doc = dfactory.newDocumentBuilder().parse(in);
//
//        // Use the static TransformerFactory.newInstance() method to instantiate
//        // a TransformerFactory. The javax.xml.transform.TransformerFactory
//        // system property setting determines the actual class to instantiate --
//        // org.apache.xalan.transformer.TransformerImpl.
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//
//        // Use the TransformerFactory to instantiate a Transformer that will work with
//        // the stylesheet you specify. This method call also processes the stylesheet
//        // into a compiled Templates object.
//        // You will need to replace "amazon_search.xsl" with your xsl style sheet.
//        // Be sure to read the section in the project description on handling namespaces.
//        Transformer transformer = tFactory.newTransformer(new StreamSource("amazon_search.xsl"));
//
//        // Use the Transformer to apply the associated Templates object (your xsl style sheet)
//        // to the returned XML document.
//        // The result is put in the file "as.html" in the same directory.
//        transformer.transform(new DOMSource(doc),
//                    new StreamResult(new FileOutputStream("as.html")));
//        //System.out.println("************* The result is in as.html *************");
//
//        //Now do an xpath selection on the same document object.
//        // Be sure to read the section in the project description on handling namespaces.
//        String xpath = "<your xpath string here>";
//        // Use the simple XPath API to select a nodeIterator.
//        System.out.println("Querying DOM using "+xpath);
//        NodeIterator nl = XPathAPI.selectNodeIterator(doc, xpath);
//        Node n;
//        while ((n = nl.nextNode())!= null)  { // for each node found...
//            // In this example, we print out the value of the node's first child.
//            // The way that you want to access the found node(s) will depend upon your
//            // XPath query and the document.  For example, if your selected node is an element with
//            // only text content, the first child of this selected node would be the text node.
//            // You can reference the Node API documentation for more information.
//            System.out.println("node's first child is: " + n.getFirstChild().getNodeValue());
//        }
//    }
//    catch (Exception e) {
//        e.printStackTrace();
//    }
//  }

}
