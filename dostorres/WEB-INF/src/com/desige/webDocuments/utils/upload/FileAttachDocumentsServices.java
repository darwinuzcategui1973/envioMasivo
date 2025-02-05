/**
 * Title: FileAttachDocumentsServices.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International<br>
 * @author Consis International (CON)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *     <li> 2003-05-16 (CON) Applied Jalopy on this source
 * </ul>
 **/


// Copyright (c) 2000 Consis International
package com.desige.webDocuments.utils.upload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Properties;


/**
 * This class represents a utility class that use DefaultDBstore class
 * in benefit to any other class
 * @author  Consis International
 * @version 1.0
 * @since   JDK1.3
 */
public class FileAttachDocumentsServices implements Serializable {
   /**
    * Constructs a new Database Store
    */
   public FileAttachDocumentsServices() {
      dataBaseStoreImplementation = "com.consisint.acsele.workflow.upload.FileAttachDocumentsDefaultDBStore";
      fileAttachDocumentsDBStore = null;
   }

   /**
    * Return a  connection if DBStore interface is different to null
    * @return Connection as connection or null
    */
   public Connection getDatabasestore() {
      if (fileAttachDocumentsDBStore != null)
         return fileAttachDocumentsDBStore.getConnection();
      else
         return null;
   }

   /**
    * Set a Database store implementation class
    */
   public void setDatabasestoreimplementation(String impl) {
      dataBaseStoreImplementation = impl;
   }

   /**
    * this method load a Database store implementation class and
    * subsequently make a database connection
    * @param driver represent a String driver of propietary DBMS
    * @param URL represent a String URL of propietary DBMS
    * @param credentials  represent a Properties credentials
    */
   public void setDatabasestore(String driver, String URL,
      Properties credentials) throws Exception {
      LoadDBaseClassManager();
      fileAttachDocumentsDBStore.connect(driver, URL, credentials);
   }

   /**
    * Represent wrapper method to gain access to DBStoreCommand() .
    * @param fis  represent a FileInputStream
    * @param length represent size of file
    */
   public void store(FileInputStream fis,
      UploadedWorkflowFile uploadedWorkflowFile, long length)
      throws IOException {
      DBStoreCommand(fis, uploadedWorkflowFile, length);
   }

   /**
    * Represent wrapper method to gain access to store of generic interface.
    * DBStore.
    * @param fis  represent a FileInputStream
    * @param length represent size of file
    */
   private void DBStoreCommand(FileInputStream fis,
      UploadedWorkflowFile uploadedWorkflowFile, long length)
      throws IOException {
      try {
         fileAttachDocumentsDBStore.store(fis, uploadedWorkflowFile, length);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Update the operation Id  of a file attached
    * @param uploadedWorkflowId  the uploaded to update
    * @param operationId the operation to assing
    */
   public void updateOperId(String uploadedWorkflowId, String operationId) {
      DBUpdateCommand(uploadedWorkflowId, operationId);
   }

   /**
    * Update the operation Id  of a file attached
    * @param uploadedWorkflowId  the uploaded to update
    * @param operationId the operation to assing
    */
   private void DBUpdateCommand(String uploadedWorkflowId, String operationId) {
      try {
         fileAttachDocumentsDBStore.assingOperationId(uploadedWorkflowId,
            operationId);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Represent wrapper method to gain access to DBLoadCommand() .
    */
   public ResultSet load(UploadedWorkflowFile uploadedWorkflowFile)
      throws IOException, Exception {
      return DBLoadCommand(uploadedWorkflowFile);
   }

   /**
    * Represent wrapper method to gain access to load of generic interface.
    * DBStore.
    */
   private ResultSet DBLoadCommand(UploadedWorkflowFile uploadedWorkflowFile)
      throws Exception {
      return fileAttachDocumentsDBStore.load(uploadedWorkflowFile);
   }

   /**
    * Represent wrapper method to gain access to DBLoadCommand() .
    */
   public Hashtable loadFileList(String operId) throws IOException, Exception {
      return DBLoadFileListCommand(operId);
   }

   /**
    * Represent wrapper method to gain access to load of generic interface.
    * DBStore.
    */
   private Hashtable DBLoadFileListCommand(String operId)
      throws Exception {
      return fileAttachDocumentsDBStore.loadSqlFileList(operId);
   }

   /**
    * Load a dataBase store implementation class
    */
   private void LoadDBaseClassManager() {
      if (dataBaseStoreImplementation != null)
         try {
            Class aClass = Class.forName(dataBaseStoreImplementation);
            Class superClass = aClass.getSuperclass();

            if (superClass.getName().equals("com.consisint.acsele.workflow.upload.FileAttachDocumentsDBStore")) {
               Class[] argsClass = new Class[0];
               Constructor c = aClass.getConstructor(argsClass);
               FileAttachDocumentsDBStore genericDBStore = (FileAttachDocumentsDBStore) c.newInstance(null);
               fileAttachDocumentsDBStore = genericDBStore;
            } else {}
         } catch (Exception e) {}
   }

   /**
    * Close   connection if DBStore interface is different to null
    */
   public void closeDatabaseStoreDB() {
      if (fileAttachDocumentsDBStore != null)
         fileAttachDocumentsDBStore.closeDB();
   }

   public static final String EMPTYENTRY = "EmptyFile";
   public static String SQLUPLOADTABLE = "UPLOADS";
   public static String SQLUPLOADDOCUMENTID = "DOCUMENTID";
   public static String SQLUPLOADDOCUMENTFILENAME = "DOCUMENTFILENAME";
   public static String SQLUPLOADCONTENTTYPE = "CONTENTTYPE";
   public static String SQLUPLOADOPERATIONID = "OPERATIONID";
   public static String SQLUPLOADDOCUMENTDESCRIPTION = "DOCUMENTDESCRIPTION";
   public static String SQLUPLOADDOCUMENTDATE = "DOCUMENTDATE";
   public static String SQLUPLOADFILE = "BINARYFILE";
   private String dataBaseStoreImplementation;
   private FileAttachDocumentsDBStore fileAttachDocumentsDBStore;
}