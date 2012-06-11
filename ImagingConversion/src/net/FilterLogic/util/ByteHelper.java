package net.FilterLogic.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class ByteHelper 
{
	  public static byte[] toBytes(Object obj) throws java.io.IOException
	  {
	      ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(obj);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	  }
}
