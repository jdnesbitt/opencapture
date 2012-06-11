/*
Copyright 2008 Filter Logic

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

package net.FilterLogic.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author dnesbitt
 */
public class FileAccess 
{
	private static final String lockExtension = ".lck";

	/**
	 * Create a lock file for the specified file.
	 * @param fileName Name of file to create a lock file.
	 * @throws Exception
	 */
	public static Boolean LockFile(String fileName)
	{
		FileOutputStream file = null;

		try
		{
			if(!(new File(fileName + lockExtension)).exists())
			{
				file = new FileOutputStream(fileName + lockExtension);
				file.close();
			}
			else
				throw new Exception("Lock file already exists [" + fileName + lockExtension + "]");
		}
		catch(Exception e)
		{
			//throw new Exception("Unable to create lock file [" + fileName + lockExtension + "]: " + e.toString());
			return Boolean.FALSE;
		}
		finally
		{
			file = null;
		}

		return Boolean.TRUE;
	}

	/**
	 * Remove lock file for specified file.
	 * @param fileName Name of file to remove lock file.
	 * @throws Exception
	 */
	public static Boolean UnLockFile(String fileName)
	{
		File file = null;

		try
		{
			file = new File(fileName + lockExtension);

			if(file.exists())
			{
				file.delete();
			}
			else
				throw new Exception("Lock file doesn't exist [" + fileName + lockExtension + "]");
		}
		catch(Exception e)
		{
			//throw new Exception("Unable to remove lock file [" + fileName + lockExtension + "]: " + e.toString());
			return Boolean.FALSE;
		}
		finally
		{
			file = null;
		}

		return Boolean.TRUE;
	}

	/**
	 * Rename a file.
	 * @param fileName File to rename
	 * @param newFileName New file name.
	 * @return True if rename is successful, else false.
	 */
	public static Boolean RenameFile(String fileName, String newFileName)
	{
		File file = new File(fileName);
		File file2 = new File(newFileName);

		boolean result = false;

		if(file.exists())
		{
			result = file.renameTo(file2);
		}

		return result != true ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * Check if file exists.
	 * @param fileName Name of file
	 * @return Return true if exists, else false.
	 */
	public static java.lang.Boolean Exists(String fileName)
	{
		boolean res = new File(fileName).exists();
		return res != true ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * Check if file lock exists.
	 * @param fileName Name of file
	 * @return Return true if locked, else false.
	 */
	public static Boolean isLocked(String fileName)
	{
		return Exists(fileName + lockExtension);
	}

//	/**
//	 * Get list of files in specified folder as XML.
//	 * @param folder Path to directory.
//	 * @param filter File filter.
//	 * @return String containing XML file list.
//	 */
//	public static String getFileListAsXML(String folder, String filter)
//	{
//		    DirFilterWatcher dfw = new DirFilterWatcher(filter);
//		    File[] filesArray = new File(folder).listFiles(dfw);
//		    String xml = "<Files>";
//
//		    long currentTS = new Date().getTime();
//
//		    // transfer to the hashmap be used a reference and keep the
//		    // lastModfied value
//		    for(int i = 0; i < filesArray.length; i++)
//		    {
//		       long fileTS = filesArray[i].lastModified();
//		       long difference = (currentTS - fileTS)/60;
//
//		       xml += "<File Name=\"" + filesArray[i].getAbsolutePath() + "\" LastModified=\"" + String.valueOf(difference) + "\" />";
//		    }
//
//		    xml += "</Files>";
//
//		    return xml;
//	}

	/**
	 * Get contents of text file.
	 * @param fileName Name of file
	 * @return String containins contents of file.
	 * @throws MbException
	 */
	public static String getTextFileContents(String fileName)
	{
		File aFile;
		   //...checks on aFile are elided
	    StringBuffer contents = new StringBuffer();
	    //declared here only to make visible to finally clause
	    BufferedReader input = null;

	    try
		{
	    	aFile = new File(fileName);

		  //use buffering, reading one line at a time
		  //FileReader always assumes default encoding is OK!
		  input = new BufferedReader( new FileReader(aFile) );
		  String line = null; //not declared within while loop
		  /*
		  * readLine is a bit quirky :
		  * it returns the content of a line MINUS the newline.
		  * it returns null only for the END of the stream.
		  * it returns an empty String if two newlines appear in a row.
		  */
		  while (( line = input.readLine()) != null)
		  {
		    contents.append(line);
		    contents.append(System.getProperty("line.separator"));
		  }
	    }
	    catch (FileNotFoundException ex)
		{
//			throw new MbUserException(
//					"com.jpmc.io.FileAccess",
//					"evaluate",
//					"BIPv600",
//					"3002",
//					"getTextFileContents :" + ex.toString(),
//					new String[]{StringHelper.getStackTrace(ex)} );
	    }
	    catch (IOException ex)
		{
//			throw new MbUserException(
//					"com.jpmc.io.FileAccess",
//					"evaluate",
//					"BIPv600",
//					"3002",
//					"getTextFileContents :" + ex.toString(),
//					new String[]{StringHelper.getStackTrace(ex)} );
	    }
	    finally
		{
	      try
		  {
	        if (input!= null)
	        {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex)
		  {
//			throw new MbUserException(
//					"com.jpmc.io.FileAccess",
//					"evaluate",
//					"BIPv600",
//					"3002",
//					"getTextFileContents :" + ex.toString(),
//					new String[]{StringHelper.getStackTrace(ex)} );
	      }
	    }
	    return contents.toString();
	}

    /**
     * Move the srcFile to the destination folder.
     * @param srcFile FileAccess to move.
     * @param destFolder Path to folder where source file will be moved.
     * @return True if successfully moved, else false is returned.
     * @note If false is returned, check if source and destination exist.
     */
    public static boolean Move(String srcFile, String destFolder)
    {
        // FileAccess (or directory) to be moved
        java.io.File file = new java.io.File(srcFile);

        if(!file.exists())
            return false;
        // Destination directory
        java.io.File dir = new java.io.File(destFolder);

        if(!dir.exists())
            return false;

        // Move file to new directory
        boolean success = file.renameTo(new java.io.File(dir, file.getName()));

        return success;
    }

    /**
     * Return size of file.
     * @param fileName Path and name of file.
     * @return
     */
    public static long getFileSize(String fileName)
    {
    	long len= -1;
    	
        File file = new File(fileName);

        if(file.exists())
        	len = file.length();
        
        return len;
    }
}
