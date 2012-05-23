package net.filterlogic.util.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class JAXPValidator
{

  public void validateSchema(String SchemaUrl, String XmlDocumentUrl)   
  {    
     try{
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
          "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();     

        factory.setNamespaceAware(true); 
        factory.setValidating(true);     
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
        "http://www.w3.org/2001/XMLSchema" );
  
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",SchemaUrl);
  
        DocumentBuilder builder =factory.newDocumentBuilder();

        Validator handler=new Validator(); 

        builder.setErrorHandler(handler); 

        builder.parse(XmlDocumentUrl);       

        if(handler.validationError==true)     
        {
            String msg = "XML Document has Error: " + handler.saxParseException.getMessage() + "\n\n" + 
                    "File: " + handler.saxParseException.getSystemId() + "\n" + 
                    "Line: " + handler.saxParseException.getLineNumber() + "\n" +
                    "Column: " + handler.saxParseException.getColumnNumber() + "\n";            

            System.out.println(msg);
        }
        
        else  
            System.out.println("XML Document is valid");     
    } 
    catch(Exception ioe)    
    {        
        ioe.printStackTrace();
    }

}

private class Validator extends DefaultHandler	   
{	
    public boolean  validationError =false;	     
    public SAXParseException saxParseException=null;

    public void error(SAXParseException exception) throws SAXException	      
    {
        validationError = true;	  
        saxParseException=exception;     
    } 

    public void fatalError(SAXParseException exception) throws SAXException
    {
        validationError = true;
        saxParseException=exception;
    }
    
    public void warning(SAXParseException exception) throws SAXException
    {      }
}

    private static void usage()
    {
        String msg = "SchemaValidator - This utility validates an xml file based on an associated xsd.\nusage:\n" + 
                "\t\tjava -cp dist/Imagine_Solutions.jar;lib/xml-apis.jar;lib/xercesimpl.jar <\"path to xml file\"> <\"path to xsd file\">\nOR\n" +
                "\t\tschema-validator.cmd <\"path to xml file\"> <\"path to xsd file\">\n\n";
        
        System.out.println(msg);
    }
    
    public static void main(String[] argv)
    {
        String SchemaUrl="";    
        String XmlDocumentUrl=""; 

        try
        {
            if(argv.length<2 || argv.length>2)
            {
                usage();
                System.out.print("Too many or few arguments.\n");
            }
            else
            {
                XmlDocumentUrl = "file:///" + argv[0];
                SchemaUrl = "file:///" + argv[1];

                JAXPValidator validator=new JAXPValidator();
                validator.validateSchema(SchemaUrl, XmlDocumentUrl);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}