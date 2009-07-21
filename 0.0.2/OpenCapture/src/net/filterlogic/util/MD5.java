/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author dnesbitt
 */
public class MD5 
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

        return new String(hex, "ASCII").toUpperCase();
    }

      public static String generateMD5(String clave) throws Exception
      {
          byte[] password = {00};
          try 
          {
              MessageDigest md5 = MessageDigest.getInstance("MD5");
              md5.update(clave.getBytes());
              password = md5.digest();

              return getHexString(password);
          } 
          catch (NoSuchAlgorithmException e) 
          {
            throw new Exception("Error generating MD5: " + e.toString());
          }
      }
}
