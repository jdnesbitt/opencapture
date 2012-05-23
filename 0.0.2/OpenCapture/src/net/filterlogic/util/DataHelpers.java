/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dnesbitt
 */
public class DataHelpers
{
    /**
     * This method converts a list of visible persistence objects into XML result.
     * {@code
     * <CLASS_NAME>
     *      <Row>
     *          <Data>...</Data>
     *          ................
     *      </Row>
     *      <ResultsReturned></ResultsReturned>
     *      <FirstResult></FirstResult>
     * </CLASS_NAME>
     * }
     * @param persistanceResult List of persistence class results.
     *
     * @return String containing XML.
     */
//    public static String toXml(List persistanceResult) throws Exception
//    {
//        Object pc = null;
//        StringBuffer sb = new StringBuffer();
//        String rootTag = "";
//
//        for(int i = 0;i<persistanceResult.size();i++)
//        {
//            pc = persistanceResult.get(i);
//
//            // if first pass, get class name and use as outter root tag
//            if(i<1)
//            {
//                // get class name
//                rootTag = pc.getClass().getSimpleName();
//
//                // remove non alpha numeric characters
//                rootTag = rootTag.replaceAll("[^A-Za-z0-9]", "");
//
//                sb.append("<" + rootTag + ">");
//                sb.append("<ResultsReturned>" + String.valueOf(persistanceResult.size()) + "</ResultsReturned>");
//            }
//
//            NamedValueList<String,String> nvl = getDataAsNVL(pc,"");
//
//            List list = nvl.getOrderedNameList();
//
//            if(list.size()>0)
//                sb.append("<Row>");
//
//            // loop through ordered list (order items where added)
//            for(int d=0;d<list.size();d++)
//            {
//                String tag = (String)list.get(d);
//                String value = (String)nvl.get(tag);
//                value = encodeXml(value);
//                String v = "<" + tag + ">" + value + "</" + tag + ">";
//
//                sb.append(v);
//            }
//
//            if(list.size()>0)
//                sb.append("</Row>");
//        }
//
//        // close root
//        sb.append("</" + rootTag + ">");
////System.out.println(sb.toString());
//        return sb.toString();
//    }

    public static String toXml(List persistanceResult, String tagNames) throws Exception
    {
        Object pc = null;
        StringBuffer sb = new StringBuffer();
        String rootTag = "";

        for(int i = 0;i<persistanceResult.size();i++)
        {
            pc = persistanceResult.get(i);

            // if first pass, get class name and use as outter root tag
            if(i<1)
            {
                // get class name
                rootTag = pc.getClass().getSimpleName();

                // remove non alpha numeric characters
                rootTag = rootTag.replaceAll("[^A-Za-z0-9]", "");

                sb.append("<" + rootTag + ">");
                sb.append("<ResultsReturned>" + String.valueOf(persistanceResult.size()) + "</ResultsReturned>");
            }

            NamedValueList<String,String> nvl = getDataAsNVL(pc, tagNames);

            List list = nvl.getOrderedNameList();

            if(list.size()>0)
                sb.append("<Row>");

            // loop through ordered list (order items where added)
            for(int d=0;d<list.size();d++)
            {
                String tag = (String)list.get(d);
                String value = (String)nvl.get(tag);
                value = encodeXml(value);
                String v = "<" + tag + ">" + value + "</" + tag + ">";

                sb.append(v);
            }

            if(list.size()>0)
                sb.append("</Row>");
        }

        // close root
        sb.append("</" + rootTag + ">");
//System.out.println(sb.toString());
        return sb.toString();
    }


    public static String toJson(List persistanceResult, String tagNames) throws Exception
    {
        Object pc = null;
        StringBuffer sb = new StringBuffer();
        String rootTag = "";

        for(int i = 0;i<persistanceResult.size();i++)
        {
            pc = persistanceResult.get(i);

            // if first pass, get class name and use as outter root tag
            if(i<1)
            {
                // get class name
                rootTag = pc.getClass().getSimpleName();

                // remove non alpha numeric characters
                rootTag = rootTag.replaceAll("[^A-Za-z0-9]", "");

                sb.append("{\"" + rootTag + "\":{");
                sb.append("\"ResultsReturned\":" + String.valueOf(persistanceResult.size()) + ",");
            }
            
            NamedValueList<String,String> nvl = getDataAsNVL(pc, tagNames);

            List list = nvl.getOrderedNameList();

            if(list.size()>0)
                sb.append("\"Row\":[{");

            // loop through ordered list (order items where added)
            for(int d=0;d<list.size();d++)
            {
                if (d > 0 || i>0)
                {
                    sb.append(",");
                }
                
                String tag = (String)list.get(d);
                String value = (String)nvl.get(tag);
                //value = encodeXml(value);
                String v = "\"" + tag + "\":\"" + value + "\"";

                sb.append(v);
            }

            if(list.size()>0)
                sb.append("}]");
        }

        // close root
        sb.append("}}");

        return sb.toString();
    }

    public static String toCsv(List persistanceResult,boolean createHeaderRow) throws Exception
    {
        Object pc = null;
        StringBuffer sb = new StringBuffer();
        String fieldNames = "";
        String tmpValue = "";

        for(int i = 0;i<persistanceResult.size();i++)
        {
            pc = persistanceResult.get(i);

            NamedValueList<String,String> nvl = getDataAsNVL(pc,"");

            List list = nvl.getOrderedNameList();

            // loop through ordered list (order items where added)
            for(int d=0;d<list.size();d++)
            {

                String tag = (String)list.get(d);
                String value = (String)nvl.get(tag);

                // make sure value doesn't contain a comma.  if it does, put quotes around value
                if(value.indexOf(",")>=0)
                    value = "\"" + value + "\"";

                // first pass, collect field names
                if(i<1)
                {
                    if(fieldNames.length()>0)
                        fieldNames += ",";

                    fieldNames += tag;

                    if(tmpValue.length()>0)
                        tmpValue += ",";

                    tmpValue += value;
                }
                else
                {
                    // if not first value on row, add comma
                    if(d>0)
                        sb.append(",");

                    sb.append(value);
                }
            }

            // first row of data, append field names and first row of data to string builder
            if(i<1)
            {
                // if create header row is true, append names
                if(createHeaderRow)
                {
                    sb.append(fieldNames);
                    sb.append("\n");
                }

                sb.append(tmpValue);
                sb.append("\n");

                tmpValue = null;
            }
            else
                sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Create a named value list of persistence object.
     * @param persistenceObjs Persistence object.
     * @param tagNames Name of tags to use when building list.  If not set, Field# is used.
     * @return NamedValueList
     * @throws Exception
     */
    private static NamedValueList<String,String> getDataAsNVL(Object persistenceObjs, String tagNames) throws Exception
    {
        Class oClass = persistenceObjs.getClass();
        String className = oClass.getName();
        //Field[] fields = oClass.getDeclaredFields();
        Method[] methods = oClass.getMethods();
        String objectName = persistenceObjs.getClass().getSimpleName();

        NamedValueList<String,String> nvl = new NamedValueList<String, String>();

        String[] tags = null;

        // trim spaces
        tagNames = tagNames.trim();

        // if tag names has value split into array
        if(tagNames.length()>0)
        {
            tags = tagNames.split(",");
        }

        // create new instance of object
        //Object o = oClass.newInstance();
//System.out.println("SimpleName: " + persistenceObjs.getClass().getSimpleName());

        // if not object array
        if(!persistenceObjs.getClass().getSimpleName().toUpperCase().contains("OBJECT[]"))
        {
            for(int i=0;i<methods.length;i++)
            {
                // get field from fields array
                //Field field = fields[i];
                Method method = methods[i];

                //field.setAccessible(true);

                // get field name
                //String fieldName = field.getName();
                String methodName = method.getName();

                if(methodName.startsWith("get"))
                {
                    String property = methodName.substring(3).equals("") ? methodName : methodName.substring(3);
                    // prepend objectName
                    property = objectName + "." + property;

                    String sValue = "";

                    // get field value
                    //Object value = field.get(o) != null ? field.get(o) : "";
                    Object value = "";

                    try
                    {
                        value = method.invoke(persistenceObjs);
                    }
                    catch(Exception o)
                    {
                        value = "";
                    }

                    if(value == null || value.equals(null))
                        sValue = "";
                    else
                        sValue = String.valueOf(value);

                    char[] c = sValue.toCharArray();
                    int g=-1;
                    if(c.length>0)
                    {
                        g = (int)c[0];

                    if(g<32 || g>126)
                        sValue = "";
                    }
    //System.out.println(methodName + ": " + property + " = " + sValue);
                    // add to nvl
                    //nvl.put(fieldName, value.toString());
                    nvl.put(property, sValue);
                }
            }
        }
        else
        {
            // object array found
            Object[] po = (Object[])persistenceObjs;

            for(int i=0;i<po.length;i++)
            {

                String property = "Field"+i;
                String sValue = "";

                // check if tagsNames passed and use if so
                if(tagNames.length()>0)
                {
                    if(tags.length>i)
                        property = tags[i];
                }
                // get field value
                //Object value = field.get(o) != null ? field.get(o) : "";
                Object value = "";

                try
                {
                    value = po[i];
                }
                catch(Exception o)
                {
                    value = "";
                }
//
                // if is simple java type, get the value
                if(isSimpleJavaType(value.getClass().getSimpleName()))
                {
                    try
                    {
                        if(value == null)
                            sValue = "";
                        else
                            sValue = String.valueOf(value);
                    }
                    catch(Exception nve)
                    {
                        sValue = "";
                    }


                    char[] c = sValue.toCharArray();
                    int g=-1;
                    if(c.length>0)
                    {
                        g = (int)c[0];

                        if(g<32 || g>126)
                            sValue = "";
                    }

                    // add property and value to name value list
                    nvl.put(property, sValue);
                }
                else
                {
                    // recurse into the object
                    NamedValueList<String,String> nvl2 = getDataAsNVL(value, tagNames);
                    // add new nvl2 to current nvl
                    nvl.putAll(nvl2);
                }

            }
        }

        return nvl;
    }

    public static String encodeXml(String xml)
    {
        String newXml = xml.replaceAll("&", "&amp;");
        newXml = newXml.replaceAll("<", "&lt;");
        newXml = newXml.replaceAll(">", "&gt;");
        newXml = newXml.replaceAll("\"", "&quot;");
        newXml = newXml.replaceAll("'", "&apos;");

        return newXml;
    }

    /**
     * Check if specified object simple name is one of the java simple types.
     *
     * Simple java types are:<br/>
     *<br/>
     * byte
     * short
     * int
     * long
     * float
     * double
     * char
     * String
     * boolean
     *
     * @param objectSimpleName
     * @return True if a simple type found, else false.
     */
    private static boolean isSimpleJavaType(String objectSimpleName)
    {
        String simpleTypes = ",byte,short,int,long,float,double,char,String,boolean,";

        boolean result = false;

        // if object name has a value, check it
        if(objectSimpleName != null && objectSimpleName.length()>0)
            result = simpleTypes.contains("," + objectSimpleName + ",");

        return result;
    }

    /**
     * Convert string to long
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static long toLong(String value)
    {
        long result = 0;

        try
        {
            result = Long.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }

    /**
     * Convert string to int
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static int toInt(String value)
    {
        int result = 0;

        try
        {
            result = Integer.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }

    /**
     * Convert string to byte
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static byte toByte(String value)
    {
        byte result = 0;

        try
        {
            result = Byte.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }

    /**
     * Convert string to short
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static short toShort(String value)
    {
        short result = 0;

        try
        {
            result = Short.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }

    /**
     * Convert string to double
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static double toDouble(String value)
    {
        double result = 0;

        try
        {
            result = Double.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }

    /**
     * Convert string to float
     * @param value
     * @return Value as expected type or -1 for error.
     */
    public static float toFloat(String value)
    {
        float result = 0;

        try
        {
            result = Float.valueOf(value);
        }
        catch(Exception e)
        {
            result = -1;
        }

        return result;
    }


    public static void main(String[] args)
    {
        Object dh = new NamedValueList();
        List l = new ArrayList();
        l.add(dh);

        try
        {
            String xml = DataHelpers.toJson(l,"");

            System.out.println(xml);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }


}
