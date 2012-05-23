/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author dnesbitt
 */
public class StringUtils 
{
    static final byte[] HEX_CHAR_TABLE = {
    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
    };    

    public static String getHexString(byte[] raw) throws UnsupportedEncodingException 
    {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) 
        {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }

        return new String(hex, "ASCII");
    }

    /**
     * Returns StringBuffer as a character array.
     * @param sb StringBuffer
     * @return Character array from StringBuffer.
     * @throws Exception
     */
    public static char[] StringBuffer2CharArray(StringBuffer sb) throws Exception
    {
        char[] chars;

        if(sb == null || sb.length()<1)
            throw new Exception("StringBuffer must contain data!");

        chars = new char[sb.length()];

        for(int i=0;i<sb.length();i++)
        {
            chars[i] = sb.charAt(i);
        }

        return chars;
    }

    /**
     * Convert string to byte array
     * @param data
     * @return byte array
     */
    public static byte[] toBytes(String data)
    {
        byte[] result = data.getBytes();

        return result;
    }

    /**
     * Convert string to char array
     * @param data
     * @return char array
     */
    public static char[] toChars(String data)
    {
        char[] result = new char[data.length()];

        data.getChars(0, data.length(), result, 0);

        return result;
    }

    /***
     * Convert string to integer.
     *
     * @param data
     *
     * @return Returns -999 if exception occurs.
     */
    public static int toInt(String data)
    {
        int i = Integer.parseInt(data.trim());

        return i;
    }

    /**
     * Capitalize first letter in word.
     *
     * @param word
     * @return Capitalized word.
     */
    public static String firstCapital(String word)
    {
        String tmp;

        word = word.trim();

        if(word.length()>0)
        {
            tmp = word.substring(0, 1).toUpperCase() + word.substring(1);
        }
        else
            tmp = word;

        return tmp;
    }

}
