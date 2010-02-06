/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.admin.plugins;

/**
 *
 * @author D106931
 */
public interface IOCAdminPlugin
{
    public void OpenUI();
    public void CloseUI();
    public java.awt.Component getThis();
}
