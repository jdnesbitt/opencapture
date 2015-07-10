# Introduction #

The OpenCapture XML configuration file is primarily used for plugin registration in the current version.  Any custom plugins should be registered in this file before they can be used in OC.  Following is a sample configuration file:

```
<?xml version="1.0" encoding="UTF-8"?>
 
 <!--
 Document : OpenCaptureConfig.xml
 Created on : June 8, 2008, 5:46 PM
 Author : Darron Nesbitt
 Description:
 This is the XML configuration file for OpenCapture.
 -->
 
<OpenCapture version="1.0">
   <ReaderPlugins>
      <Plugin Name="ZXing 3 of 9 Reader" PluginID="CODE39" Class="net.filterlogic.OpenCapture.readers.Code39Reader" Description="Reads 3 of 9 Barcodes." />
   </ReaderPlugins>
   <ConverterPlugins>
      <Plugin Name="PDF Converter" PluginID="PDF" Class="net.filterlogic.OpenCapture.converters.ToPDF" Description="Converts TIFFs to PDF format." />
   </ConverterPlugins>
   <DeliveryPlugins>
      <Plugin Name="Alfresco Delivery" PluginID="AlfrescoDelivery" Class="net.filterlogic.OpenCapture.alfresco.delivery" Description="Deliverys documents to Alfresco document repository." />
   </DeliveryPlugins>
</OpenCapture>
```

Plugins are loaded dynamically at runtime based on the configured plugin in the BatchClassDefinitionFile.

**ReaderPlugins** are used by Cognizance.  These plugins are designed to read various types of barcodes or perform OCR.

**ConverterPlugins** are used by the Converter module.  These plugins are designed to convert TIFFs to other file formats.

**DeliveryPlugins** are used by the Delivery module.  These plugins are designed to deliver documents to a downstream system or repository.