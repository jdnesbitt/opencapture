/*
 * NewClass.java
 *
 * Created on May 30, 2006, 12:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.filterlogic.OpenCapture.crypto;

/**
 *
 * @author Darron Nesbitt
 */
public class Crypt 
{

    private javax.crypto.spec.SecretKeySpec keySpec;
    // Default key
    private byte[] key = {(byte)0x45,(byte)0xA3,(byte)0x45,(byte)0x19,(byte)0x59,(byte)0x87,(byte)0xD2,(byte)0xBF};
    
    private String algorithm;

    /** Creates a new instance of Crypt */
    public Crypt(String algorithm)
    {
        this.algorithm = algorithm;
        this.keySpec = new javax.crypto.spec.SecretKeySpec(this.key,this.algorithm);      
    }

    /** Creates a new instance of Crypt */
    public Crypt(byte[] key, String algorithm) 
    {
        this.key = key;
        this.algorithm = algorithm;
        this.keySpec = new javax.crypto.spec.SecretKeySpec(this.key,this.algorithm);
    }

        /** Encrypts the give String to an array of bytes */
        public byte[] encryptString(String text) 
        {
            try 
            {
                javax.crypto.Cipher cipher =
                javax.crypto.Cipher.getInstance(this.algorithm);
                cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, this.keySpec);
                return cipher.doFinal(text.getBytes());
            } catch (Exception e) 
            {
                return null;
            }
        }

        /** Decrypts the given array of bytes to a String */
        public String decryptString(byte[] b) 
        {
            try 
            {
                javax.crypto.Cipher cipher =
                javax.crypto.Cipher.getInstance(this.algorithm);
                cipher.init(javax.crypto.Cipher.DECRYPT_MODE, this.keySpec);
                return new String(cipher.doFinal(b));
            } catch (Exception e) 
            {
                return null;
            }
        }

        /** Encrypts the given String to a hex representation of the array
        of bytes */
        public String encryptHexString(String text) 
        {
            return toHex(encryptString(text));
        }

        /** Decrypts the given hex representation of the array of bytes to
        a String */
        public String decryptHexString(String text) 
        {
            return decryptString(toByteArray(text));
        }

        /** Converts the given array of bytes to a hex String */
        private String toHex(byte[] buf) 
        {
            String res = "";
            for (int i=0; i<buf.length; i++) 
            {
                int b = buf[i];
                if (b<0) 
                {
                    res = res.concat("-");
                    b = -b;
                }
                if (b<16) 
                {
                    res = res.concat("0");
                }

                res = res.concat(Integer.toHexString(b).toUpperCase());

            }
            return res;
        }

        /** Converts the given hex String to an array of bytes */
        private byte[] toByteArray(String hex) 
        {
            java.util.Vector res = new java.util.Vector();
            String part;
            int pos = 0;
            int len = 0;
            while (pos<hex.length()) 
            {
                len = ((hex.substring(pos,pos+1).equals("-")) ? 3 : 2);
                part = hex.substring(pos,pos+len);
                pos += len;
                int test = Integer.parseInt(part,16);
                res.add(new Byte((byte)test));
            }
            if (res.size()>0) 
            {
                byte[] b = new byte[res.size()];
                for (int i=0; i<res.size(); i++) 
                {
                    Byte a = (Byte)res.elementAt(i);
                    b[i] = a.byteValue();
                }
                return b;
            } else 
            {
                return null;
            }
        }
}
