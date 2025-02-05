package com.focus.custom.delsur.dto;

public class CustomNodeDTO {
	private int idNode;
	private int idNodeParent;
	private String nodeName;
	private int nodeType;
	
	public CustomNodeDTO() {
		// TODO Auto-generated constructor stub
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	public int getIdNodeParent() {
		return idNodeParent;
	}

	public void setIdNodeParent(int idNodeParent) {
		this.idNodeParent = idNodeParent;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public String toString() {
		return "CustomNodeDTO [idNode=" + idNode + ", idNodeParent="
				+ idNodeParent + ", nodeName=" + nodeName + ", nodeType="
				+ nodeType + "]";
	}
}
