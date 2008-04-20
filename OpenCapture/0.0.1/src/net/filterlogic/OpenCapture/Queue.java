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

package net.filterlogic.OpenCapture;

/**
 *
 * @author dnesbitt
 */
public class Queue 
{
    private long ID = 0;
    private String QueueName = "";
    private String Description = "";
    private String CurrentQueue = "";
    private String PluginID = "";

    public long getID()
    {
        return ID;
    }

    protected void setID(long ID)
    {
        this.ID = ID;
    }

    public String getQueueName()
    {
        return QueueName;
    }

    protected void setQueueName(String QueueName)
    {
        this.QueueName = QueueName;
    }

    public String getDescription()
    {
        return Description;
    }

    protected void setDescription(String Description)
    {
        this.Description = Description;
    }

    public String getPluginID()
    {
        return PluginID;
    }

    protected void setPluginID(String PluginID)
    {
        this.PluginID = PluginID;
    }

    public String getCurrentQueue()
    {
        return CurrentQueue;
    }

    protected void setCurrentQueue(String CurrentQueue)
    {
        this.CurrentQueue = CurrentQueue;
    }
}
