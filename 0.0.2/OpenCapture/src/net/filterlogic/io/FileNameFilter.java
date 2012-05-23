/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author dnesbitt
 */
class FileNameFilter implements FilenameFilter
{
    String ext;

    public FileNameFilter(String ext)
    {
        this.ext = ext.toUpperCase();
    }

    public boolean accept(File dir, String name)
    {
        return (name.toUpperCase().endsWith(this.ext));
    }
}

