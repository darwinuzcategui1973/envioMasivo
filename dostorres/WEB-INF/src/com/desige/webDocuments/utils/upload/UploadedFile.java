/**
 * Title: UploadedFile.java<br>
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

package com.desige.webDocuments.utils.upload;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;


// A class to hold information about an uploaded file.
//
public class UploadedFile implements Serializable {
   private String dir;
   private int documentID;
   private String documentFileName;
   private String contentType;
   private String documentDescription;
   private Date documentDate;

   public UploadedFile(String dir, int documentID, String documentFileName,
      String contentType, String documentDescription, Date documentDate) {
      this.dir = dir;
      this.documentID = documentID;
      this.documentFileName = documentFileName;
      this.contentType = contentType;
      this.documentDescription = documentDescription;
      this.documentDate = documentDate;
   }

   public String getDir() {
      return dir;
   }

   public int getDocumentID() {
      return documentID;
   }

   public String getDocumentFileName() {
      return documentFileName;
   }

   public String getContentType() {
      return contentType;
   }

   public String getDocumentDescription() {
      return documentDescription;
   }

   public void setDocumentDescription(String documentDescription) {
      this.documentDescription = documentDescription;
   }

   public Date getDocumentDate() {
      return documentDate;
   }

   public void setDocumentDate(Date documentDate) {
      this.documentDate = documentDate;
   }

   public File getFile() {
      if (dir == null || documentFileName == null) {
         return null;
      } else {
         return new File(dir + File.separator + documentFileName);
      }
   }
}