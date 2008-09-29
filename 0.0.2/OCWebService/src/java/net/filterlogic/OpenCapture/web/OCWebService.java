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

package net.filterlogic.OpenCapture.web;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Darron Nesbitt
 */
@WebService()
public class OCWebService 
{

    /**
     * OpenBatch is used to open a batch using it's batchID. If
     * login successful a batch token will be returned in an xml 
     * message:
     * 
     * <OCResponce Token="token">
     *   <Method Name="OpenBatch" BatchID="1234" />
     * </OCResponse>
     */
    @WebMethod(operationName = "OpenBatch")
    public String OpenBatch(@WebParam(name = "batchID") long batchID)
    {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * GetBatchList is used to retrieve a list of batches in OpenCapture.
     */
    @WebMethod(operationName = "GetBatchList")
    public String GetBatchList(@WebParam(name = "moduleID")
    String moduleID)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "GetPage")
    public String GetPage(@WebParam(name = "batchID")
    long batchID, @WebParam(name = "pages")
    String pages)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "authenticate")
    public String authenticate(@WebParam(name = "moduleID")
    String moduleID, @WebParam(name = "userID")
    String userID, @WebParam(name = "password")
    String password)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "CloseBatch")
    public String CloseBatch(@WebParam(name = "batchID")
    long batchID)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "CreateBatch")
    public String CreateBatch(@WebParam(name = "batchClassName")
    String batchClassName, @WebParam(name = "batchName")
    String batchName)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "CreateBatchClass")
    public String CreateBatchClass(@WebParam(name = "batchClassName")
    String batchClassName)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "OpenBatchClass")
    public String OpenBatchClass(@WebParam(name = "batchClassName")
    String batchClassName)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "CloseBatchClass")
    public String CloseBatchClass(@WebParam(name = "batchClassName")
    String batchClassName)
    {
        //TODO write your implementation code here:
        return null;
    }

/**
     * Web service operation
     */
    @WebMethod(operationName = "GetSessions")
    public String GetSessions()
    {
        //TODO write your implementation code here:
        return null;
    }

}
