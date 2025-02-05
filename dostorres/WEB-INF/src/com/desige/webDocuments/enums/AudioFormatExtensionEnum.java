package com.desige.webDocuments.enums;

public enum AudioFormatExtensionEnum {

	WAV("wav"),
	WAVE("wave"),
	AIFF("aiff"),
	AIF("aif"),
	AIFC("aifc"),
	AU("au"),
	CAF("caf"),
	PCM("pcm"),
	FLAC("flac"),
	ALAC("alac"),
	WMA("wma"),
	WMV("wmv"),
	MP3("mp3"),
	OGG("ogg"),
	OGA("oga"),
	MOGG("mogg"),
	AAC("acc"),
	M4A("m4a"),
	F3GP("3gp"),
	M4R("m4r");
	
	
	private String value;

	AudioFormatExtensionEnum(String value) {
		this.value=value;
	}
	
	public String getValue() {
		return value;
	}
}
