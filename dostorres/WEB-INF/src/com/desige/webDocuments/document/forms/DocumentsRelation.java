package com.desige.webDocuments.document.forms;

import java.io.Serializable;

/**
 * Title: DocumentsRelation.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 27-08-2004 (NC) Creation </li>
 *      <li> 30-06-2006 (NC) se agregó el uso del Log y cambios para mostrar los documentos vinculados.</li> 
 </ul>
 */
public class DocumentsRelation implements Serializable {
    private String id;
    private String number;
    private String nameDocument;
    private String numVer;
    private String nameFile;
    private boolean exitoBusqueda;
    private boolean selected;
    private byte toViewDocs;
    private byte toPrintDoc;
    private String ver;

    public DocumentsRelation(){

    }
    public boolean getExitoBusqueda() {
        return exitoBusqueda;
    }

    public void setExitoBusqueda(boolean exitoBusqueda) {
        this.exitoBusqueda = exitoBusqueda;
    }
    public DocumentsRelation(String id,String number,String nameDocument){
        setId(id);
        setNumber(number);
        setNameDocument(nameDocument);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNameDocument() {
        return nameDocument;
    }

    public void setNameDocument(String nameDocument) {
        this.nameDocument = nameDocument;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getNumVer() {
        return numVer;
    }

    public void setNumVer(String numVer) {
        this.numVer = numVer;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public byte getToViewDocs() {
        return toViewDocs;
    }

    public void setToViewDocs(byte toViewDocs) {
        this.toViewDocs = toViewDocs;
    }

    public byte getToPrintDoc() {
        return toPrintDoc;
    }

    public void setToPrintDoc(byte toPrintDoc) {
        this.toPrintDoc = toPrintDoc;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
