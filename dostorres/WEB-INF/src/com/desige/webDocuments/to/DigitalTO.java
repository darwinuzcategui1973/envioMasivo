package com.desige.webDocuments.to;

import java.io.Serializable;
import java.text.ParseException;
import java.util.TreeMap;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class DigitalTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4394928684455333384L;
	
	public static String SEPARADOR_CAMPO = "<--CAMPO-->";
	public static String SEPARADOR_VALOR = "<--VALOR-->";

	private String idDigital;
	private String nameFile;
	private String nameDocument;
	private String type;
	private String dateCreation;
	private String numberTest;
	private String idPerson;
    private String idPersonDelete;
    private String dateDelete;
	private String idStatusDigital;
	private String comentario;
	private String lote;
	private String ownerTypeDoc;
	private String idNode;
	private String typesetter;
	private String checker;
	private String visible;
	private String versionMayor;
	private String versionMenor;
	private String codigo;
	private String publicado;
	private String expira;
	private String fechaPublicacion;
	private String fechaVencimiento;
	private String comentarios;
	private String url;
	private String palabrasClaves;
	private String descripcion;
	private String otrosDatos;
	private String publicDoc;
	private String internalMetaData;

	private transient Users usuario;
	private transient TreeMap<String, String> otros = null;

	// attributos adicionales
	private StringBuffer nameFileDisk = new StringBuffer();

	public DigitalTO() {

	}

	// ini adicionales
	public String getNameFileDisk() {
		nameFileDisk.setLength(0);
		return nameFileDisk.append("dig_").append(ToolsHTML.zero(getIdDigital(), 15)).append(".pdf").toString();
	}

	public String getNameFileDiskSinExt() {
		nameFileDisk.setLength(0);
		return nameFileDisk.append("dig_").append(ToolsHTML.zero(getIdDigital(), 15)).toString();
	}

	public String getNameFileDiskFullPath() {
		nameFileDisk.setLength(0);
		nameFileDisk.append(ToolsHTML.getPathDigitalizados());
		return nameFileDisk.append("dig_").append(ToolsHTML.zero(getIdDigital(), 15)).append(".pdf").toString();
	}

	public String getNameFileDiskFullPath(String ext) {
		nameFileDisk.setLength(0);
		nameFileDisk.append(ToolsHTML.getPathDigitalizados());
		return nameFileDisk.append("dig_").append(ToolsHTML.zero(getIdDigital(), 15)).append(ext).toString();
	}

	public String getDateCreationToShow() {
		try {
			return ToolsHTML.sdfShow.format(ToolsHTML.sdfShowConvert1.parse(getDateCreation()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public String getOtrosDatos(String clave) {
		if (otros == null) {
			if (getOtrosDatos() != null && !getOtrosDatos().equals("")) {
				otros = new TreeMap<String, String>();
				String[] data = getOtrosDatos().split(DigitalTO.SEPARADOR_CAMPO);
				String[] valor;
				for (int i = 0; i < data.length; i++) {
					valor = data[i].split(DigitalTO.SEPARADOR_VALOR);
					if(valor.length==2) {
						otros.put(valor[0], ToolsHTML.isEmptyOrNull(valor[1],""));
					}
				}
			}
		}
		if (otros!=null && otros.containsKey(clave)) {
			return otros.get(clave);
		} else {
			return "";
		}
	}

	// fin adicionales

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getIdDigital() {
		return idDigital;
	}

	public void setIdDigital(String idDigital) {
		this.idDigital = idDigital;
	}

	public String getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}

	public String getIdStatusDigital() {
		return idStatusDigital;
	}

	public void setIdStatusDigital(String idStatusDigital) {
		this.idStatusDigital = idStatusDigital;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getNumberTest() {
		return numberTest;
	}

	public void setNumberTest(String numberTest) {
		this.numberTest = numberTest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Users getUsuario() {
		return usuario;
	}

	public void setUsuario(Users usuario) {
		this.usuario = usuario;
	}

	public void setNameFileDisk(StringBuffer nameFileDisk) {
		this.nameFileDisk = nameFileDisk;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getLote() {
		return lote == null ? "" : lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getIdNode() {
		return idNode;
	}

	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}

	public String getOwnerTypeDoc() {
		return ownerTypeDoc;
	}

	public void setOwnerTypeDoc(String ownerTypeDoc) {
		this.ownerTypeDoc = ownerTypeDoc;
	}

	public String getTypesetter() {
		return typesetter;
	}

	public void setTypesetter(String typesetter) {
		this.typesetter = typesetter;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFechaPublicacion() {
		return fechaPublicacion;
	}

	public String getFechaPublicacionToShow() {
		try {
			if (getFechaPublicacion() != null) {
				return ToolsHTML.date.format(ToolsHTML.sdfShowConvert1.parse(getFechaPublicacion()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setFechaPublicacion(String fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public String getFechaVencimiento() {
		return fechaVencimiento;
	}

	public String getFechaVencimientoToShow() {
		try {
			if (getFechaVencimiento() != null)
				return ToolsHTML.date.format(ToolsHTML.sdfShowConvert1.parse(getFechaVencimiento()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getOtrosDatos() {
		return otrosDatos;
	}

	public void setOtrosDatos(String otrosDatos) {
		this.otrosDatos = otrosDatos;
	}

	public String getPalabrasClaves() {
		return palabrasClaves;
	}

	public void setPalabrasClaves(String palabrasClaves) {
		this.palabrasClaves = palabrasClaves;
	}

	public String getPublicado() {
		return publicado;
	}

	public void setPublicado(String publicado) {
		this.publicado = publicado;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersionMayor() {
		return versionMayor;
	}

	public void setVersionMayor(String versionMayor) {
		this.versionMayor = versionMayor;
	}

	public String getVersionMenor() {
		return versionMenor;
	}

	public void setVersionMenor(String versionMenor) {
		this.versionMenor = versionMenor;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getExpira() {
		return expira;
	}

	public void setExpira(String expira) {
		this.expira = expira;
	}

	public String getDateDelete() {
		return dateDelete;
	}

	public String getDateDeleteToShow() {
		try {
			if (getDateDelete() != null) {
				return ToolsHTML.date.format(ToolsHTML.sdfShowConvert1.parse(getDateDelete()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setDateDelete(String dateDelete) {
		this.dateDelete = dateDelete;
	}

	public String getIdPersonDelete() {
		return idPersonDelete;
	}

	public void setIdPersonDelete(String idPersonDelete) {
		this.idPersonDelete = idPersonDelete;
	}

	public String getPublicDoc() {
		return publicDoc;
	}

	public void setPublicDoc(String publicDoc) {
		this.publicDoc = publicDoc;
	}

	public String getInternalMetaData() {
		return internalMetaData;
	}
	
	public void setInternalMetaData(String internalMetaData) {
		this.internalMetaData = internalMetaData;
	}
}
