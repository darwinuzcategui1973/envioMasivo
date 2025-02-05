package com.desige.webDocuments.enlaces.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: UrlsActionForm.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>08/04/2004 (NC) Creation </li>
 </ul>
 */
public class UrlsActionForm extends SuperActionForm implements Serializable {
    private String id;
    private String name;
    private String url;
    private String type;
    private String user;

    public UrlsActionForm(){
        cleanForm();
    }
    public UrlsActionForm(String id,String name,String url,String type){
        setId(id);
        setName(name);
        setUrl(url);
        setType(type);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getCmd() {
//        return cmd;
//    }
//
//    public void setCmd(String cmd) {
//        this.cmd = cmd;
//    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public void cleanForm(){
        setId("");
        setName("");
        setUrl("");
        setType("");
    }
}
