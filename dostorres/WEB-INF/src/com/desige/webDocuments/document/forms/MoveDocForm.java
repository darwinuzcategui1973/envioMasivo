package com.desige.webDocuments.document.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: MoveDocForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 *  <br/>
 *          Changes:<br/>
 *  <ul>
 *          <li> 04/07/2005 (NC) Creation </li>
 *  </ul>
 */
public class MoveDocForm extends SuperActionForm implements Serializable {
    private String idDocument;
    private String from;
    private String fromID;
    private String to;
    private String toID;
    private String comments;
    private String numberDocAct;
    private String numberDocNew;
    private int typeNumber;

    public MoveDocForm() {

    }

    public MoveDocForm(String idDocument,String fromID,String toID) {
        setIdDocument(idDocument);
        setFromID(fromID);
        setToID(toID);
        setComments("");
    }

    public String getFrom() {
    	return from;
    }
    
    public String getFromNoHref() {
    	//debo picar solo los nombres
    	String r = "";
    	String[] ss = from.split("<a");
    	for (int i = 0; i < ss.length; i++) {
    		try {
    			//System.out.println(ss[i]);
    			if(ss[i] != null && ss[i].contains(">")){
    				if(!"".equals(ss[i].substring(ss[i].indexOf(">")+1, ss[i].indexOf("</a>")))){
    					r += ss[i].substring(ss[i].indexOf(">")+1, ss[i].indexOf("</a>"));
    					r += " \\ ";
    				}
    			}
    		} catch (Exception e) {
    			// TODO: handle exception
    			//e.printStackTrace();
    		}
    	}
    		
    	return r;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getTo() {
        return to;
    }

    public String getToNoHref() {
    	//debo picar solo los nombres
    	String r = "";
    	String[] ss = to.split("<a");
    	for (int i = 0; i < ss.length; i++) {
    		try {
    			//System.out.println(ss[i]);
    			if(ss[i] != null && ss[i].contains(">")){
    				if(!"".equals(ss[i].substring(ss[i].indexOf(">")+1, ss[i].indexOf("</a>")))){
    					r += ss[i].substring(ss[i].indexOf(">")+1, ss[i].indexOf("</a>"));
    					r += " \\ ";
    				}
    			}
    		} catch (Exception e) {
    			// TODO: handle exception
    			//e.printStackTrace();
    		}
    	}
    		
    	return r;
    }
    
    public void setTo(String to) {
        this.to = to;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    public String getNumberDocAct() {
        return numberDocAct;
    }

    public void setNumberDocAct(String numberDocAct) {
        this.numberDocAct = numberDocAct;
    }

    public String getNumberDocNew() {
        return numberDocNew;
    }

    public void setNumberDocNew(String numberDocNew) {
        this.numberDocNew = numberDocNew;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }
}
