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

package net.filterlogic.io;

/**
 *
 * @author dnesbitt
 */
public class Path 
{
    public static boolean ValidatePath(String path)
    {
        if(!new java.io.File(path).exists())
            return false;
        else
            return true;
    }
    
    /**
     * FixPath appends trailing slash to the path.
     * @param path Path to fix.
     * @return Path with trailing slash appended.
     */
    public static String FixPath(String path)
    {
        String sep = System.getProperty("file.separator");
        
        if(path.lastIndexOf(sep) != path.length())
            path += sep;
        
        return path;
    }
    
    public static boolean createPath(String path)
    {
        boolean ok = false;
        
        if(!ValidatePath(path))
            ok = (new java.io.File(path)).mkdir();
        
        return ok;
    }
}
