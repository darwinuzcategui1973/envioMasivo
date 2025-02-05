package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class PersonTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String id; // CodPais
	private String name; // Nombre
	private String codigo; // Codigo
	
	private String idPerson;
	private String nameUser;
	private String clave;
	private String idGrupo;
	private String nombres;
	private String apellidos;
	private String cargo;
	private String email;
	private String direccion;
	private String ciudad;
	private String estado;
	private String codPais;
	private String codigoPostal;
	private String tlf;
	private String idLanguage;
	private String numRecordPages;
	private String accountActive;
	private String primeravez;
	private String idarea;
	private String dateLastPass;
	private String editDocumentCheckOut;
	private String dateLastPassEdit;
	private String edit;
	private String published;
	private String search;
	private String publishedOption;
	private String filesOption;
	private String searchOption;
	private String menuHeadOption;
	private String modo;
	private String lado;
	private String ppp;
	private String panel;
	private String pagina;
	private String separar;
	private String minimo;
	private String typeDocuments;
	private String ownerTypeDoc;
	private String idNodeDigital;
	private String typesetter;
	private String checker;
	private String lote;
	private String correlativo;
	private String javaWebStart;
	private String idNodeService;
	private String sacopOption;

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



	public String getCodigo() {
		return codigo;
	}



	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}



	public String getIdPerson() {
		return idPerson;
	}

	public int getIdPersonInt() {
		return ToolsHTML.parseInt(idPerson);
	}


	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}



	public String getNameUser() {
		return nameUser;
	}



	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}



	public String getClave() {
		return clave;
	}



	public void setClave(String clave) {
		this.clave = clave;
	}



	public String getIdGrupo() {
		return idGrupo;
	}

	public int getIdGrupoInt() {
		return ToolsHTML.parseInt(idGrupo);
	}


	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}



	public String getNombres() {
		return nombres;
	}



	public void setNombres(String nombres) {
		this.nombres = nombres;
	}



	public String getApellidos() {
		return apellidos;
	}



	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}



	public String getCargo() {
		return cargo;
	}



	public void setCargo(String cargo) {
		this.cargo = cargo;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getDireccion() {
		return direccion;
	}



	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}



	public String getCiudad() {
		return ciudad;
	}



	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}



	public String getEstado() {
		return estado;
	}



	public void setEstado(String estado) {
		this.estado = estado;
	}



	public String getCodPais() {
		return codPais;
	}



	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}



	public String getCodigoPostal() {
		return codigoPostal;
	}



	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}



	public String getTlf() {
		return tlf;
	}



	public void setTlf(String tlf) {
		this.tlf = tlf;
	}



	public String getIdLanguage() {
		return idLanguage;
	}



	public void setIdLanguage(String idLanguage) {
		this.idLanguage = idLanguage;
	}



	public String getNumRecordPages() {
		return numRecordPages;
	}

	public int getNumRecordPagesInt() {
		return ToolsHTML.parseInt(numRecordPages);
	}


	public void setNumRecordPages(String numRecordPages) {
		this.numRecordPages = numRecordPages;
	}



	public String getAccountActive() {
		return accountActive;
	}

	public byte getAccountActiveByte() {
		return ToolsHTML.parseByte(accountActive, (byte)0);
	}


	public void setAccountActive(String accountActive) {
		this.accountActive = accountActive;
	}



	public String getPrimeravez() {
		return primeravez;
	}

	public int getPrimeravezByte() {
		return ToolsHTML.parseByte(primeravez,(byte)0);
	}


	public void setPrimeravez(String primeravez) {
		this.primeravez = primeravez;
	}



	public String getIdarea() {
		return idarea;
	}



	public void setIdarea(String idarea) {
		this.idarea = idarea;
	}



	public String getDateLastPass() {
		return dateLastPass;
	}



	public void setDateLastPass(String dateLastPass) {
		this.dateLastPass = dateLastPass;
	}



	public String getEditDocumentCheckOut() {
		return editDocumentCheckOut;
	}

	public int getEditDocumentCheckOutInt() {
		return ToolsHTML.parseInt(editDocumentCheckOut);
	}


	public void setEditDocumentCheckOut(String editDocumentCheckOut) {
		this.editDocumentCheckOut = editDocumentCheckOut;
	}



	public String getDateLastPassEdit() {
		return dateLastPassEdit;
	}



	public void setDateLastPassEdit(String dateLastPassEdit) {
		this.dateLastPassEdit = dateLastPassEdit;
	}



	public String getEdit() {
		return edit;
	}

	public int getEditInt() {
		return ToolsHTML.parseInt(edit);
	}


	public void setEdit(String edit) {
		this.edit = edit;
	}



	public String getPublished() {
		return published;
	}



	public void setPublished(String published) {
		this.published = published;
	}



	public String getSearch() {
		return search;
	}



	public void setSearch(String search) {
		this.search = search;
	}



	public String getPublishedOption() {
		return publishedOption;
	}



	public void setPublishedOption(String publishedOption) {
		this.publishedOption = publishedOption;
	}



	public String getFilesOption() {
		return filesOption;
	}



	public void setFilesOption(String filesOption) {
		this.filesOption = filesOption;
	}



	public String getSearchOption() {
		return searchOption;
	}



	public void setSearchOption(String searchOption) {
		this.searchOption = searchOption;
	}



	public String getMenuHeadOption() {
		return menuHeadOption;
	}



	public void setMenuHeadOption(String menuHeadOption) {
		this.menuHeadOption = menuHeadOption;
	}



	public String getModo() {
		return modo;
	}

	public int getModoInt() {
		return ToolsHTML.parseInt(modo);
	}


	public void setModo(String modo) {
		this.modo = modo;
	}



	public String getLado() {
		return lado;
	}

	public int getLadoInt() {
		return ToolsHTML.parseInt(lado);
	}


	public void setLado(String lado) {
		this.lado = lado;
	}



	public String getPpp() {
		return ppp;
	}

	public int getPppInt() {
		return ToolsHTML.parseInt(ppp);
	}


	public void setPpp(String ppp) {
		this.ppp = ppp;
	}



	public String getPanel() {
		return panel;
	}

	public int getPanelInt() {
		return ToolsHTML.parseInt(panel);
	}


	public void setPanel(String panel) {
		this.panel = panel;
	}



	public String getPagina() {
		return pagina;
	}

	public int getPaginaInt() {
		return ToolsHTML.parseInt(pagina);
	}


	public void setPagina(String pagina) {
		this.pagina = pagina;
	}



	public String getSeparar() {
		return separar;
	}

	public int getSepararInt() {
		return ToolsHTML.parseInt(separar);
	}


	public void setSeparar(String separar) {
		this.separar = separar;
	}



	public String getMinimo() {
		return minimo;
	}


	public int getMinimoInt() {
		return ToolsHTML.parseInt(minimo);
	}

	public void setMinimo(String minimo) {
		this.minimo = minimo;
	}



	public String getTypeDocuments() {
		return typeDocuments;
	}


	public int getTypeDocumentsInt() {
		return ToolsHTML.parseInt(typeDocuments);
	}

	public void setTypeDocuments(String typeDocuments) {
		this.typeDocuments = typeDocuments;
	}



	public String getOwnerTypeDoc() {
		return ownerTypeDoc;
	}

	public int getOwnerTypeDocInt() {
		return ToolsHTML.parseInt(ownerTypeDoc);
	}


	public void setOwnerTypeDoc(String ownerTypeDoc) {
		this.ownerTypeDoc = ownerTypeDoc;
	}



	public String getIdNodeDigital() {
		return idNodeDigital;
	}

	public int getIdNodeDigitalInt() {
		return ToolsHTML.parseInt(idNodeDigital);
	}


	public void setIdNodeDigital(String idNodeDigital) {
		this.idNodeDigital = idNodeDigital;
	}



	public String getTypesetter() {
		return typesetter;
	}

	public int getTypesetterInt() {
		return ToolsHTML.parseInt(typesetter);
	}


	public void setTypesetter(String typesetter) {
		this.typesetter = typesetter;
	}



	public String getChecker() {
		return checker;
	}

	public int getCheckerInt() {
		return ToolsHTML.parseInt(checker);
	}


	public void setChecker(String checker) {
		this.checker = checker;
	}



	public String getLote() {
		return lote;
	}



	public void setLote(String lote) {
		this.lote = lote;
	}



	public String getCorrelativo() {
		return correlativo;
	}



	public void setCorrelativo(String correlativo) {
		this.correlativo = correlativo;
	}



	public String getJavaWebStart() {
		return javaWebStart;
	}



	public void setJavaWebStart(String javaWebStart) {
		this.javaWebStart = javaWebStart;
	}
	
	public String getIdNodeService() {
		return idNodeService;
	}

	public int getIdNodeServiceInt() {
		return ToolsHTML.parseInt(idNodeService);
	}


	public void setIdNodeService(String idNodeService) {
		this.idNodeService = idNodeService;
	}
	
	public String getSacopOption() {
		return sacopOption;
	}



	public void setSacopOption(String sacopOption) {
		this.sacopOption = sacopOption;
	}






	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PersonTO [");
		builder.append("  idPerson=").append(idPerson);
		builder.append(", nameUser=").append(nameUser);
		builder.append(", clave=").append(clave);
		builder.append(", idGrupo=").append(idGrupo);
		builder.append(", Nombres=").append(nombres);
		builder.append(", Apellidos=").append(apellidos);
		builder.append(", cargo=").append(cargo);
		builder.append(", email=").append(email);
		builder.append(", Direccion=").append(direccion);
		builder.append(", Ciudad=").append(ciudad);
		builder.append(", Estado=").append(estado);
		builder.append(", CodPais=").append(codPais);
		builder.append(", CodigoPostal=").append(codigoPostal);
		builder.append(", Tlf=").append(tlf);
		builder.append(", IdLanguage=").append(idLanguage);
		builder.append(", numRecordPages=").append(numRecordPages);
		builder.append(", accountActive=").append(accountActive);
		builder.append(", primeravez=").append(primeravez);
		builder.append(", idarea=").append(idarea);
		builder.append(", dateLastPass=").append(dateLastPass);
		builder.append(", EditDocumentCheckOut=").append(editDocumentCheckOut);
		builder.append(", dateLastPassEdit=").append(dateLastPassEdit);
		builder.append(", edit=").append(edit);
		builder.append(", published=").append(published);
		builder.append(", search=").append(search);
		builder.append(", publishedOption=").append(publishedOption);
		builder.append(", filesOption=").append(filesOption);
		builder.append(", searchOption=").append(searchOption);
		builder.append(", menuHeadOption=").append(menuHeadOption);
		builder.append(", modo=").append(modo);
		builder.append(", lado=").append(lado);
		builder.append(", ppp=").append(ppp);
		builder.append(", panel=").append(panel);
		builder.append(", pagina=").append(pagina);
		builder.append(", separar=").append(separar);
		builder.append(", minimo=").append(minimo);
		builder.append(", typeDocuments=").append(typeDocuments);
		builder.append(", ownerTypeDoc=").append(ownerTypeDoc);
		builder.append(", idNodeDigital=").append(idNodeDigital);
		builder.append(", typesetter=").append(typesetter);
		builder.append(", checker=").append(checker);
		builder.append(", lote=").append(lote);
		builder.append(", correlativo=").append(correlativo);
		builder.append(", javaWebStart=").append(javaWebStart);
		builder.append(", idNodeService=").append(idNodeService);
		builder.append(", sacopOption=").append(sacopOption);
		builder.append("]");

		return builder.toString();
	}

}
