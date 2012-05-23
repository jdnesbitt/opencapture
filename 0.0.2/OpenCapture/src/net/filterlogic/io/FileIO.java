/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import net.filterlogic.util.DateUtil;

/**
 *
 * @author D106931
 */
public class FileIO
{
    /**
     * Read contents of file and return in byte array.
     *
     * @param fileName
     *
     * @return Byte array containing file data.
     */
    public static byte[] readFileToByteArray(String fileName) throws Exception
    {
        FileInputStream fis;

        try
        {
            fis = new FileInputStream(fileName);

            int fisSize = fis.available();

            byte[] in = new byte[fisSize];

            // read entire file
            fis.read(in, 0, fisSize);


            fis.close();

            return in;
        }
        catch(Exception fioe)
        {
            throw new Exception(fioe);
        }
    }

    /**
     * Write binary data to file.
     * 
     * @param fileName File path and name to write to.
     * @param data
     * @throws Exception
     */
    public static void writeByteArrayToFile(String fileName, byte[] data) throws Exception
    {
        File file;
        FileOutputStream fos;
        try
        {
            file = new File(fileName);
            fos = new FileOutputStream(file, false);

            fos.write(data);

            fos.close();
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }
    
    /**
     * Write binary data to file
     * 
     * @param file File object to write to.
     * @param data
     * @throws Exception
     */
    public static void writeByteArrayToFile(File file, byte[] data) throws Exception
    {
        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream(file, false);

            fos.write(data);

            fos.close();
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }

    /**
     * Read contents of file and return in byte array.
     *
     * FileInputStream is closed after file is read.
     * 
     * @param fis
     * 
     * @return Byte array containing file data.
     */
    public static byte[] readFileToByteArray(FileInputStream fis) throws Exception
    {
        //FileInputStream fis;

        try
        {
            //fis = new FileInputStream(fileName);

            int fisSize = fis.available();

            byte[] in = new byte[fisSize];

            // read entire file
            fis.read(in, 0, fisSize);


            fis.close();

            return in;
        }
        catch(Exception fioe)
        {
            throw new Exception(fioe);
        }
    }
    
    /**
     * Move src file to destination directory.
     * 
     * @param srcFile
     * @param destDir
     * @throws Exception
     */
    public static void moveFile(String srcFile, String destDir) throws Exception
    {
//    	 File (or directory) to be moved
    	File file = new File(srcFile);

    	if(!file.exists())
    		throw new Exception("Source file doesn't exist [" + srcFile + "]");

//    	 Destination directory
    	File dir = new File(destDir);

    	if(!dir.exists())
    		throw new Exception("Destination directory doesn't exist [" + destDir + "]");

//    	 Move file to new directory
    	boolean success = file.renameTo(new File(dir, file.getName()));

    	if (!success) 
    	{
    	    // File was not successfully moved
    		throw new Exception("Unable to move file[" + srcFile + "] to [" + destDir + "]");
    	}
    }
    
    /**
     * Generate temporary file.  File name based on prefix and suffix provided.
     * If directory exists and exception occurs, temp file with following name format will be created:
     * 
     * tmp-(D-Hms.S).fil
     * (D-Hms.S = date formatting
     * 
     * @param prefix At least 3 characters.
     * @param suffix
     * @param directory
     * @return File object to temp file.
     * @throws IOException
     */
    public static File generateTempFile(String prefix, String suffix, File directory) throws IOException
    {
    	
    	String fileName = "";

    	if(!directory.exists())
    		throw new IOException("Directory doesn't exist!");

    	try
    	{
    		return File.createTempFile(prefix, suffix,directory);
    	}
    	catch(Exception e)
    	{
    		fileName = "tmp-" + DateUtil.getDateTime("D-Hms.S") + ".fil";
    		
    		return new File(directory.getAbsoluteFile() + Path.pathSeparator + fileName);
    	}

    }

    /**
     * Retrieves file list.  Directories are not returned.
     * @param path Path to directory.
     * @return File array.
     */
    public static File[] getFileList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns files
        FileFilter dirFilter = new FileFilter()
        {
            public boolean accept(File file)
            {
                return !file.isDirectory();
            }
        };

        File[] files = new File(path).listFiles(dirFilter);

        return files;
    }

    /**
     *
     * @param path
     * @param ext
     * @return
     */
    public static File[] getFileList(String path,String ext)
    {
        if(!new File(path).exists())
            return null;

        File[] files = new File(path).listFiles(new FileNameFilter(ext));

        return files;
    }

    /**
     * Retrieve list of directories.
     * @param path Path to starting directory
     * @return File[] containing directories.  Returns null if no directories found.
     */
    public static File[] getDirList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns files
        FileFilter dirFilter = new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.isDirectory();
            }
        };

        File[] files = new File(path).listFiles(dirFilter);

        return files;
    }

}
