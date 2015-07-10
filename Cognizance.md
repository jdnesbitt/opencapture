# Introduction #

Cognizance generally the second module in the system.  It performs bar code separation meaning it creates documents based on a specific bar code value found.

# Details #

The Zone configuration controls which value to search for when performing separation.  The Zone named FORMID (Name attribute) is used to identify the location of the FORMID barcode.  The following Zone is an example of a FORMID zone:

```
<Zone Name="FORMID" Type="CODE39" X="393" Y="218" W="851" H="300" MinAccuracy="" FieldType="FormID"/>
```

The value that identifies the page as a separator page can be found in the Document tag.  The value of the FormID attribute of the Document tag is the value that's used to identify the document.  When the value of the barcode located at the FORMID zone matches the Document's FormID attribute's value, a new document is created and all pages from that point forward are added to the current document until the next document is found.  The following is an example of a Document tag:

```
<Document Name="Carrier Header" FormID="CARRIER" Number="" Indexed="False">
```

When separation is done correctly the Document tag in the Batch section of the batch xml file looks like the following:

```
<Document FormID="CARRIER" Name="Carrier Header" Number="1">
  <Pages>
    <Page Name="00000000.tif" PageNumber="1" Sequence="1"/>
  </Pages>
  <IndexFields>
    <IndexField Name="CUSTOMER_NAME" Stickey="Y" Type="S" Value="THE CUSTOMER NAME"/>
    <IndexField Name="CUSTOMER_ID" Stickey="Y" Type="N" Value="98765"/>
    <IndexField Name="MC_NUMBER" Stickey="Y" Type="N" Value="123456"/>
    <IndexField Name="DOC_TYPE" Stickey="N" Type="S" Value=""/>
  </IndexFields>
</Document>
```