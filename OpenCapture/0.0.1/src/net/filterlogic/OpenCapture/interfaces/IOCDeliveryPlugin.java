/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.interfaces;

/**
 *
 * @author dnesbitt
 */
public interface IOCDeliveryPlugin 
{
    public void setBatch(net.filterlogic.OpenCapture.Batch batch);
    public boolean OpenRepository();
    public long DeliverDocument(net.filterlogic.OpenCapture.Document document);
    public long CloseRepository();
}
