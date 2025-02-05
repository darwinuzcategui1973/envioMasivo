package com.desige.webDocuments.enums;

public enum VideoFormatExtensionEnum {

	MP4("mp4"), 
	AVI("avi"), 
	MKV("mkv"), 
	FLV("flv"), 
	MOV("mov"), 
	WMV("wmv"), 
	DIVX("divx"), 
	H264("h.264"), 
	XVID("xvid"), 
	RM("rm");
	
	private String value;

	VideoFormatExtensionEnum(String value) {
		this.value=value;
	}
	
	public String getValue() {
		return value;
	}
}
