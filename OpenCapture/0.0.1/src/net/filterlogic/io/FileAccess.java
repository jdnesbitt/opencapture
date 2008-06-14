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
public class FileAccess 
{

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
}
