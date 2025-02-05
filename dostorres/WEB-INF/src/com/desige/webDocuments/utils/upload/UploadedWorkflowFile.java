/**
 * Title: UploadedWorkflowFile.java<br>
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

import java.io.Serializable;
import java.sql.Date;


// A class to hold information about an uploaded file.
//
public class UploadedWorkflowFile extends UploadedFile implements Serializable {
   private String operationID;

   public UploadedWorkflowFile(UploadedFile uploadedFile, String operationID) {
      super(uploadedFile.getDir(), uploadedFile.getDocumentID(),
         uploadedFile.getDocumentFileName(), uploadedFile.getContentType(),
         uploadedFile.getDocumentDescription(), uploadedFile.getDocumentDate());
      this.operationID = operationID;
   }

   public UploadedWorkflowFile(String dir, int documentID,
      String documentFileName, String contentType, String operationID,
      String documentDescription, Date documentDate) {
      super(dir, documentID, documentFileName, contentType,
         documentDescription, documentDate);
      this.operationID = operationID;
   }

   public String getOperationID() {
      return operationID;
   }

   public void setOperationID(String operationID) {
      this.operationID = operationID;
   }
}