package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: NodesTree.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 26/10/2004 (RR) Creation </li>
 </ul>
 */

public class NodesTree implements Serializable {
    private String idNode;
    private String nameNode;
    private String nodeFather;
    private String toListDist;

    public NodesTree(){

    }

    public NodesTree(String idNode,String nameNode,String nodeFather, String toListDist){
        setIdNode(idNode);
        setNameNode(nameNode);
        setNodeFather(nodeFather);
        setToListDist(toListDist);
    }

    public NodesTree(String idNode,String nameNode,String nodeFather){
        setIdNode(idNode);
        setNameNode(nameNode);
        setNodeFather(nodeFather);
        setToListDist("0");
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public String getNameNode() {
        return nameNode;
    }

    public void setNameNode(String nameNode) {
        this.nameNode = nameNode;
    }

    public String getNodeFather() {
        return nodeFather;
    }

    public void setNodeFather(String nodeFather) {
        this.nodeFather = nodeFather;
    }

	public String getToListDist() {
		return toListDist;
	}

	public void setToListDist(String toListDist) {
		this.toListDist = toListDist;
	}
}
