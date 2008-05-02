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

package net.filterlogic.OpenCapture.module;

import net.filterlogic.OpenCapture.*;
import net.filterlogic.io.Path;
import net.filterlogic.util.imaging.*;

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileFilter;



class FileNameFilter implements FilenameFilter 
{
    String ext;
    
    public FileNameFilter(String ext) 
    {
        this.ext = ext;
    }
    
    public boolean accept(File dir, String name) 
    {
        return (name.endsWith(this.ext));
    }
}
/**
 *
 * @author Darron Nesbitt
 */
public class OCImport 
{
    static Logger myLogger = Logger.getLogger(OCImport.class.getName( ));

    Appender myAppender;
    SimpleLayout myLayout;
    
    private String configFile = "";
    private String log4j = "";
    private Properties prop = new Properties();
    private String OC_HOME = "";

    public OCImport(String configFile) throws Exception
    {
        this.configFile = configFile;
        
        // load property config file.
        FileInputStream fis = new FileInputStream(new File(configFile));
        prop.load(fis);
        fis.close();

        OC_HOME = prop.getProperty("oc_home");
        log4j = prop.getProperty("log4j");

        PropertyConfigurator.configure(log4j);

        /* Instantiate a layout and an appender, assign layout to
           appender programmatically */
        myLayout = new SimpleLayout();
        myAppender = new ConsoleAppender(myLayout);    // Appender is
                                                       // Interface
        /* Assign appender to the logger programmatically */
        myLogger.addAppender(myAppender);
    }

    /**
     * Retrieves file list.  Directories are not returned.
     * @param path Path to directory.
     * @return File array.
     */
    private File[] getFileList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns directories
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
    
    private File[] getFileList(String path,String ext)
    {
        if(!new File(path).exists())
            return null;

        File[] files = new File(path).listFiles(new FileNameFilter(ext));

        return files;
    }
    
    public void importBatches()
    {
        Batch batch;

        int pollDirs = Integer.parseInt(prop.getProperty("polldircount"));
        
        for(int i=1;i<=pollDirs;i++)
        {
            String path = prop.getProperty("polldir." + String.valueOf(i));
            String batchclass = prop.getProperty("batchclass." + String.valueOf(i));
            boolean filesonly = ((String)prop.get("importfilesonly." + String.valueOf(i))).toLowerCase().equals("yes") ? true : false;
            String ext = prop.getProperty("importtrigger." + String.valueOf(i));
            String batchname = "";
            String imagePath = "";

            File[] files;
            
            if(filesonly)
                 files = getFileList(path);
            else
                files =  getFileList(path,ext);

            for(int b=0;b<files.length;b++)
            {
                batch = new Batch();

                batchname = Path.getFileNameWithoutExtension(files[b].getName().toUpperCase());
                
                try
                {
                    try
                    {
                        batch.CreateBatch(batchclass, batchname);
                        long batchID = Long.parseLong(batch.getID());
                        String[] tiffs = new String[1];

                        tiffs[0] = files[i].getAbsolutePath();

                        List list = ToTIFF.toTIFF(tiffs, OpenCaptureCommon.getBatchFilePath(batchID), "00000000", false, 200);

                        batch.OpenBatch(batchID);

                        for(int p=0;p<list.size();p++)
                        {
                            batch.addLoosePage(new Page((String)list.get(p)));
                        }

                        batch.CloseBatch();
                    }
                    catch(OpenCaptureException oe)
                    {
                         myLogger.error("Error creating batch!\n" + oe.toString());
                    }
                }
                catch(Exception e)
                {
                    myLogger.error("Error creating batch!\n" + e.toString());
                }
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            if(args.length<1)
            {
                System.out.println("Must pass path to configuration file.");
                System.exit(0);
            }

            OCImport ocImport = new OCImport(args[0]);

            ocImport.importBatches();
            
        }
        catch(Exception e)
        {
            myLogger.fatal(e.toString());
            System.out.println(e.toString());
        }
    }

}