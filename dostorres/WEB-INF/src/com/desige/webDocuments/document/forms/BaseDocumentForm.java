package com.desige.webDocuments.document.forms;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: BaseDocumentForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC) * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>30/06/2004 (NC) Creation</li>
 *          <li>06/06/2005 (SR) Se crea el bean impresion</li>
 *          <li>17/06/2005 (SR) Se crea el bean
 *          dateExpiresDrafts,dateApprovedDrafts;</li>
 *          <li>26/07/2005 (SR) Se crea el bean idLocation,nodeActive</li>
 *          <li>16/08/2005 (SR) Se crea el bean numCorrelativoExiste</li>
 *          <li>21/02/2006 (SR) Se crea el bean statuhist</li>
 *          <li>03/08/2006 (NC) Add new field "typeFormat"</li>
 *          </ul>
 */
public class BaseDocumentForm extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3846248759442774077L;

	public static final int statuDraft = 1;
	public static final int statuInReview = 2;
	public static final int statuReview = 3;
	public static final int statuInApproved = 4;
	public static final int statuApproved = 5;
	public static final int statudocObs = 6;
	public static final int docRejected = 7;

	private boolean printing = false;
	private boolean copyContents = false;

	private String numberGen;
	public String idDocument; 
	private String nameDocument;
	private String nameDocumentOld;
	private String owner;
	//ydavila Ticket 001-00-003023
	private String respsolcambio;
	private String respsolelimin;
	private String respsolimpres;
	
	private String ownerOld;
	private String idperson;
	private String prefix;
	private String number;
	private String typeDocument;
	private String descriptTypeDoc;
	private String normISO;
	private String normISODescriptId;
	private String docProtected;
	private String docPublic;
	private String docOnline;
	private String URL;
	private String keys;
	private String descript;
	private String nodeType;
	private String nameFile;
	// TODO: CAMBIO PARA DOBLE VERSION
	private String nameFileParalelo;
	private String mayorVer;
	private String minorVer;
	private String approved;
	private String expires;
	private String comments;
	private String idNode;
	private String dateCreation;
	private String normISODescript;
	private String dateApproved;
	private String dateExpires;
	private String dateApprovedDrafts;
	private String dateExpiresDrafts;
	private String datePublic;
	private int numVer;
	private int numVerFiles;
	private int idFiles;
	private boolean expediente = false;
	private String nameOwner;
	private String nameOwnerOld;
	private String razonDeCambio;
	private String razonDeCambioName;
	private String razonDeCambioOwner;
	private String razonDeCambioFiles;
	private int statu;
	private String contextType;
	// TODO: CAMBIO PARA DOBLE VERSION
	private String contextTypeParalelo;
	private String docRelations;
	private int checkOut;
	private boolean check;
	private String statuDoc;
	private String lastOperation;
	private boolean loadDoc;
	private String charge;
	private String imprimir;
	private String idLocation;
	private String nodeActive;
	private String numCorrelativoExiste;
	private String queryReporte;
	//ydavila Ticket 001-00-003023
	//private String lastVersionApproved;
	public String lastVersionApproved;
	private String rout;
	private String typeFormat;
	private boolean controlada;
	//ydavila Ticket 001-00-003023
	private boolean paracambiar;
	private boolean paraeliminar;
	
	
	private String dateDead;
	private String loteDoc;
	
	private String idDocumentOrigen;
	private String idVersionOrigen;

	private int rejectDocument;

	private int numberCopies;

	private String toForFiles;

	private byte toRead;
	private byte toDelete;
	private byte toEdit;
	private byte toMove;
	private byte toAdmon;
	private byte toViewDocs;
	private byte toReview;
	private byte toApproved;
	private byte toMoveDocs;
	private byte toCheckOut;
	private byte toEditRegister;
	private byte toImpresion;
	private byte toCheckTodos;
	private byte toDownload;
	private Object[] usersSelected;
	private String isflowusuario;
	private String isflowcargo;
	private String isNotflowsecuencial;
	private byte typeWF;
	private boolean selected;
	// es la descripcion de activeFactory de wonderware
	// se usa en editDataDocument.jsp
	private String descripcionActiveFactory;
	private String checkactiveFactory;

	// este es el archivo con la informacion de la impresion
	private String fileInicio = null;
	private String folderOut = null;

	private int idUser = 0;
	
	private int idRegisterClass = 0;
	private String descRegisterClass;
	
	private int idProgramAudit = 0;
	private int idPlanAudit = 0;
	private String descProgramAudit = "";
	private String descPlanAudit = "";
	
	//ydavila Ticket 001-00-003265
	private String comment;

	// variables para campos adicionales
	private String d1, d2, d3, d4, d5, d6, d7, d8, d9, d10;
	private String d11, d12, d13, d14, d15, d16, d17, d18, d19, d20;
	private String d21, d22, d23, d24, d25, d26, d27, d28, d29, d30;
	private String d31, d32, d33, d34, d35, d36, d37, d38, d39, d40;
	private String d41, d42, d43, d44, d45, d46, d47, d48, d49, d50;
	private String d51, d52, d53, d54, d55, d56, d57, d58, d59, d60;
	private String d61, d62, d63, d64, d65, d66, d67, d68, d69, d70;
	private String d71, d72, d73, d74, d75, d76, d77, d78, d79, d80;
	private String d81, d82, d83, d84, d85, d86, d87, d88, d89, d90;
	private String d91, d92, d93, d94, d95, d96, d97, d98, d99, d100;

	private ArrayList listaCampos;

	public BaseDocumentForm() {
		reset();
	}

	public void reset() {
		setD1("");
		setD2("");
		setD3("");
		setD4("");
		setD5("");
		setD6("");
		setD7("");
		setD8("");
		setD9("");
		setD10("");
		setD11("");
		setD12("");
		setD13("");
		setD14("");
		setD15("");
		setD16("");
		setD17("");
		setD18("");
		setD19("");
		setD20("");
		setD21("");
		setD22("");
		setD23("");
		setD24("");
		setD25("");
		setD26("");
		setD27("");
		setD28("");
		setD29("");
		setD30("");
		setD31("");
		setD32("");
		setD33("");
		setD34("");
		setD35("");
		setD36("");
		setD37("");
		setD38("");
		setD39("");
		setD40("");
		setD41("");
		setD42("");
		setD43("");
		setD44("");
		setD45("");
		setD46("");
		setD47("");
		setD48("");
		setD49("");
		setD50("");
		setD51("");
		setD52("");
		setD53("");
		setD54("");
		setD55("");
		setD56("");
		setD57("");
		setD58("");
		setD59("");
		setD60("");
		setD61("");
		setD62("");
		setD63("");
		setD64("");
		setD65("");
		setD66("");
		setD67("");
		setD68("");
		setD69("");
		setD70("");
		setD71("");
		setD72("");
		setD73("");
		setD74("");
		setD75("");
		setD76("");
		setD77("");
		setD78("");
		setD79("");
		setD80("");
		setD81("");
		setD82("");
		setD83("");
		setD84("");
		setD85("");
		setD86("");
		setD87("");
		setD88("");
		setD89("");
		setD90("");
		setD91("");
		setD92("");
		setD93("");
		setD94("");
		setD95("");
		setD96("");
		setD97("");
		setD98("");
		setD99("");
		setD100("");
	}

	// ini reflexion
	public Object get(String name) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return this.getClass()
				.getDeclaredMethod("get".concat(name), new Class[] {})
				.invoke(this, new Object[] {});
	}

	public Object set(String name, String valor)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return this
				.getClass()
				.getDeclaredMethod("set".concat(name),
						new Class[] { String.class })
				.invoke(this, new Object[] { valor });
	}

	public Object set(String name, int valor) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return this
				.getClass()
				.getDeclaredMethod("set".concat(name),
						new Class[] { Integer.class })
				.invoke(this, new Object[] { valor });
	}

	public Object set(String name, Date valor) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return this
				.getClass()
				.getDeclaredMethod("set".concat(name),
						new Class[] { Date.class })
				.invoke(this, new Object[] { valor });
	}

	// fin reflexion

	public String getNotflowsecuencial() {
		return isNotflowsecuencial;
	}

	public void setNotflowsecuencial(String notflowsecuencial) {
		isNotflowsecuencial = notflowsecuencial;
	}

	public void setIsflowusuario(String isflowusuario) {
		this.isflowusuario = isflowusuario;
	}

	public String getIsflowusuario() {
		return isflowusuario;
	}

	public void setIsflowcargo(String isflowcargo) {
		this.isflowcargo = isflowcargo;
	}

	public String getIsflowcargo() {
		return isflowcargo;
	}

	public void setIsNotflowsecuencial(String isNotflowsecuencial) {
		this.isNotflowsecuencial = isNotflowsecuencial;
	}

	public String getIsNotflowsecuencial() {
		return isNotflowsecuencial;
	}

	public String getQueryReporte() {
		return queryReporte;
	}

	public void setQueryReporte(String queryReporte) {
		this.queryReporte = queryReporte;
	}

	public Object[] getUsersSelected() {
		return usersSelected;
	}

	public void setUsersSelected(Object[] usersSelected) {
		this.usersSelected = usersSelected;
	}

	public byte getToCheckTodos() {
		return toCheckTodos;
	}

	public void setToCheckTodos(byte toCheckTodos) {
		this.toCheckTodos = toCheckTodos;
	}

	public byte getToImpresion() {
		return toImpresion;
	}

	public void setToImpresion(byte toImpresion) {
		this.toImpresion = toImpresion;
	}

	public byte getToEditRegister() {
		return toEditRegister;
	}

	public void setToEditRegister(byte toEditRegister) {
		this.toEditRegister = toEditRegister;
	}

	public byte getToCheckOut() {
		return toCheckOut;
	}

	public void setToCheckOut(byte toCheckOut) {
		this.toCheckOut = toCheckOut;
	}

	public byte getToMoveDocs() {
		return toMoveDocs;
	}

	public void setToMoveDocs(byte toMoveDocs) {
		this.toMoveDocs = toMoveDocs;
	}

	public byte getToApproved() {
		return toApproved;
	}

	public void setToApproved(byte toApproved) {
		this.toApproved = toApproved;
	}

	public byte getToReview() {
		return toReview;
	}

	public void setToReview(byte toReview) {
		this.toReview = toReview;
	}

	public byte getToViewDocs() {
		return toViewDocs;
	}

	public void setToViewDocs(byte toViewDocs) {
		this.toViewDocs = toViewDocs;
	}

	public byte getToAdmon() {
		return toAdmon;
	}

	public void setToAdmon(byte toAdmon) {
		this.toAdmon = toAdmon;
	}

	public byte getToMove() {
		return toMove;
	}

	public void setToMove(byte toMove) {
		this.toMove = toMove;
	}

	public byte getToEdit() {
		return toEdit;
	}

	public void setToEdit(byte toEdit) {
		this.toEdit = toEdit;
	}

	public byte getToDelete() {
		return toDelete;
	}

	public void setToDelete(byte toDelete) {
		this.toDelete = toDelete;
	}

	public byte getToRead() {
		return toRead;
	}

	public void setToRead(byte toRead) {
		this.toRead = toRead;
	}

	public String getNumCorrelativoExiste() {
		return numCorrelativoExiste;
	}

	public void setNumCorrelativoExiste(String numCorrelativoExiste) {
		this.numCorrelativoExiste = numCorrelativoExiste;
	}

	public String getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(String idLocation) {
		this.idLocation = idLocation;
	}

	public String getNodeActive() {
		return nodeActive;
	}

	public void setNodeActive(String nodeActive) {
		this.nodeActive = nodeActive;
	}

	public String getImprimir() {
		return imprimir;
	}

	public void setImprimir(String imprimir) {
		this.imprimir = imprimir;
	}

	public String getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	public String getNormISO() {
		return normISO;
	}

	public void setNormISO(String normISO) {
		this.normISO = normISO;
	}

	//ydavila Ticket 001-00-003265
	public String getcomment() {
		return comment;
	}

	public void setcomment(String comment) {
		this.comment = comment;
	}
	
	public String getHistoryComment() {
		return comment;
	}

	//public void setHistoryComment(String comment) {
	//	this.comment = comment;
	//}
	
	public String getIdperson() {
		return idperson;
	}

	public void setIdperson(String idperson) {
		this.idperson = idperson;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	//ydavila Ticket 001-00-003023
	public String getrespsolcambio() {
		return respsolcambio;
	}

	public void setrespsolcambio(String respsolcambio) {
		this.respsolcambio = respsolcambio;
	}
	
	public String getrespsolelimin() {
		return respsolelimin;
	}

	public void setrespsolelimin(String respsolelimin) {
		this.respsolelimin = respsolelimin;
	}
	
	public String getrespsolimpres() {
		return respsolimpres;
	}

	public void setrespsolimpres(String respsolimpres) {
		this.respsolimpres = respsolimpres;
	}
	//ydavila Ticket 001-00-003023
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getDocProtected() {
		return docProtected;
	}

	public void setDocProtected(String docProtected) {
		this.docProtected = docProtected;
	}

	public String getDocPublic() {
		return docPublic;
	}

	public void setDocPublic(String docPublic) {
		this.docPublic = docPublic;
	}

	public String getDocOnline() {
		return docOnline;
	}

	public void setDocOnline(String docOnline) {
		this.docOnline = docOnline;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public String getNameFileParalelo() {
		return nameFileParalelo;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public void setNameFileParalelo(String nameFileParalelo) {
		this.nameFileParalelo = nameFileParalelo;
	}

	public String getMayorVer() {
		return mayorVer;
	}

	public void setMayorVer(String mayorVer) {
		this.mayorVer = mayorVer;
	}

	public String getMinorVer() {
		return minorVer;
	}

	public void setMinorVer(String minorVer) {
		this.minorVer = minorVer;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getNumberGen() {
		return numberGen;
	}

	public void setNumberGen(String numberGen) {
		this.numberGen = numberGen;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getIdNode() {
		return idNode;
	}

	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getNormISODescriptId() {
		return normISODescriptId;
	}

	public void setNormISODescriptId(String normISODescriptId) {
		this.normISODescriptId = normISODescriptId;
	}

	public String getNormISODescript() {
		return normISODescript;
	}

	public void setNormISODescript(String normISODescript) {
		this.normISODescript = normISODescript;
	}

	public String getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(String dateApproved) {
		this.dateApproved = dateApproved;
	}

	public String getDateExpires() {
		return dateExpires;
	}

	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}

	// SIMON 17 DE JUNIO 2005 INICIO
	public String getDateApprovedDrafts() {
		return dateApprovedDrafts;
	}

	public void setDateApprovedDrafts(String dateApprovedDrafts) {
		this.dateApprovedDrafts = dateApprovedDrafts;
	}

	public String getDateExpiresDrafts() {
		return dateExpiresDrafts;
	}

	public void setDateExpiresDrafts(String dateExpiresDrafts) {
		this.dateExpiresDrafts = dateExpiresDrafts;
	}

	// SIMON 17 DE JUNIO 2005 FIN
	public int getNumVer() {
		return numVer;
	}

	public void setNumVer(int numVer) {
		this.numVer = numVer;
	}

	public String getDescriptTypeDoc() {
		return descriptTypeDoc;
	}

	public void setDescriptTypeDoc(String descriptTypeDoc) {
		this.descriptTypeDoc = descriptTypeDoc;
	}

	public String getNameOwner() {
		return nameOwner;
	}

	public void setNameOwner(String nameOwner) {
		this.nameOwner = nameOwner;
	}

	public int getStatu() {
		return statu;
	}

	public void setStatu(int statu) {
		this.statu = statu;
	}

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		/*
		 * Cambios de tipo para documentos de office 2007 ya que los reconoce
		 * como application/x-zip-compressed
		 */

		if (!ToolsHTML.isEmptyOrNull(this.getNameFile())
				&& !ToolsHTML.isEmptyOrNull(this.getNameDocument())) {
			if (!ToolsHTML.getMimeType(this.getNameFile().toLowerCase())
					.equals("")) {
				contextType = ToolsHTML.getMimeType(this.getNameFile()
						.toLowerCase());
			}
		}
		this.contextType = contextType;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public String getContextTypeParalelo() {
		return contextTypeParalelo;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public void setContextTypeParalelo(String contextTypeParalelo) {
		this.contextTypeParalelo = contextTypeParalelo;
	}

	public String getDocRelations() {
		return docRelations;
	}

	public void setDocRelations(String docRelations) {
		this.docRelations = docRelations;
	}

	public String getDatePublic() {
		return datePublic;
	}

	public void setDatePublic(String datePublic) {
		this.datePublic = datePublic;
	}

	public int getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(int checkOut) {
		this.checkOut = checkOut;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean getCheck() {
		return this.check;
	}

	public String getStatuDoc() {
		return statuDoc;
	}

	public void setStatuDoc(String statuDoc) {
		this.statuDoc = statuDoc;
	}

	public String getLastOperation() {
		return lastOperation;
	}

	public void setLastOperation(String lastOperation) {
		this.lastOperation = lastOperation;
	}

	public boolean isLoadDoc() {
		return loadDoc;
	}

	public void setLoadDoc(boolean loadDoc) {
		this.loadDoc = loadDoc;
	}

	public boolean getLoadDoc() {
		return this.loadDoc;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getLastVersionApproved() {
		return lastVersionApproved;
	}

	public void setLastVersionApproved(String lastVersionApproved) {
		this.lastVersionApproved = lastVersionApproved;
	}

	public String getRout(boolean asHref) {
		if (asHref) {
			return rout;
		} else {
			// debo picar solo los nombres
			String r = "";
			String[] ss = rout.split("<a");
			for (int i = 0; i < ss.length; i++) {
				try {
					// System.out.println(ss[i]);
					if (ss[i] != null && ss[i].contains(">")) {
						if (!"".equals(ss[i].substring(ss[i].indexOf(">") + 1,
								ss[i].indexOf("</a>")))) {
							r += ss[i].substring(ss[i].indexOf(">") + 1,
									ss[i].indexOf("</a>"));
							r += "&nbsp;\\&nbsp;";
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					// e.printStackTrace();
				}
			}

			// colocamos el nombre al final si originalmente estaba
			if (rout.contains(nameDocument)) {
				if (!r.endsWith(nameDocument)) {
					r += nameDocument;
				}
			}

			return r;
		}
	}

	public void setRout(String rout) {
		this.rout = rout;
	}

	public String getTypeFormat() {
		return typeFormat;
	}

	public void setTypeFormat(String typeFormat) {
		this.typeFormat = typeFormat;
	}

	public byte getTypeWF() {
		return typeWF;
	}

	public void setTypeWF(byte typeWF) {
		this.typeWF = typeWF;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getCheckactiveFactory() {
		return checkactiveFactory;
	}

	public void setCheckactiveFactory(String checkactiveFactory) {
		this.checkactiveFactory = checkactiveFactory;
	}

	public String getDescripcionActiveFactory() {
		return descripcionActiveFactory;
	}

	public void setDescripcionActiveFactory(String descripcionActiveFactory) {
		this.descripcionActiveFactory = descripcionActiveFactory;
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}

	public int getRejectDocument() {
		return rejectDocument;
	}

	public void setRejectDocument(int rejectDocument) {
		this.rejectDocument = rejectDocument;
	}

	public String getRazonDeCambio() {
		return razonDeCambio;
	}

	public void setRazonDeCambio(String razonDeCambio) {
		this.razonDeCambio = razonDeCambio;
	}

	public String getRazonDeCambioName() {
		return razonDeCambioName;
	}

	public void setRazonDeCambioName(String razonDeCambioName) {
		this.razonDeCambioName = razonDeCambioName;
	}

	public String getRazonDeCambioOwner() {
		return razonDeCambioOwner;
	}

	public void setRazonDeCambioOwner(String razonDeCambioOwner) {
		this.razonDeCambioOwner = razonDeCambioOwner;
	}

	public String getNameDocumentOld() {
		return nameDocumentOld;
	}

	public void setNameDocumentOld(String nameDocumentOld) {
		this.nameDocumentOld = nameDocumentOld;
	}

	public String getOwnerOld() {
		return ownerOld;
	}

	public void setOwnerOld(String ownerOld) {
		this.ownerOld = ownerOld;
	}

	public String getNameOwnerOld() {
		return nameOwnerOld;
	}

	public void setNameOwnerOld(String nameOwnerOld) {
		this.nameOwnerOld = nameOwnerOld;
	}

	public String getFileInicio() {
		return fileInicio;
	}

	public void setFileInicio(String fileInicio) {
		this.fileInicio = fileInicio;
	}

	public String getFolderOut() {
		return folderOut;
	}

	public void setFolderOut(String folderOut) {
		this.folderOut = folderOut;
	}

	public String getToForFiles() {
		if (toForFiles == null || toForFiles.equals("")
				|| toForFiles.equals("true") || toForFiles.equals("1")) {
			return "1";
		} else {
			return "0";
		}
	}

	public void setToForFiles(String toForFiles) {
		this.toForFiles = toForFiles;
	}

	public String getRazonDeCambioFiles() {
		return razonDeCambioFiles;
	}

	public void setRazonDeCambioFiles(String razonDeCambioFiles) {
		this.razonDeCambioFiles = razonDeCambioFiles;
	}

	public int getNumVerFiles() {
		return numVerFiles;
	}

	public void setNumVerFiles(int numVerFiles) {
		this.numVerFiles = numVerFiles;
	}

	public boolean isExpediente() {
		return expediente;
	}

	public void setExpediente(boolean expediente) {
		this.expediente = expediente;
	}

	public boolean isControlada() {
		return controlada;
	}

	public void setControlada(boolean controlada) {
		this.controlada = controlada;
	}

	//ydavila Ticket 001-00-003023
	public boolean getparaCambiar() {
		return paracambiar;
	}
	public void setParaCambiar(boolean paracambiar) {
		this.paracambiar = paracambiar;
	}
	public boolean getparaEliminar() {
		return paraeliminar;
	}
	public void setParaEliminar(boolean paraeliminar) {
		this.paraeliminar = paraeliminar;
	}
	
	
	public int getNumberCopies() {
		return numberCopies;
	}
	

	public void setNumberCopies(int numberCopies) {
		this.numberCopies = numberCopies;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdFiles() {
		return idFiles;
	}

	public void setIdFiles(int idFiles) {
		this.idFiles = idFiles;
	}

	public boolean isCopyContents() {
		return copyContents;
	}

	public void setCopyContents(boolean copyContents) {
		this.copyContents = copyContents;
	}

	public String getD1() {
		return d1;
	}

	public void setD1(String d1) {
		this.d1 = d1;
	}

	public String getD10() {
		return d10;
	}

	public void setD10(String d10) {
		this.d10 = d10;
	}

	public String getD100() {
		return d100;
	}

	public void setD100(String d100) {
		this.d100 = d100;
	}

	public String getD11() {
		return d11;
	}

	public void setD11(String d11) {
		this.d11 = d11;
	}

	public String getD12() {
		return d12;
	}

	public void setD12(String d12) {
		this.d12 = d12;
	}

	public String getD13() {
		return d13;
	}

	public void setD13(String d13) {
		this.d13 = d13;
	}

	public String getD14() {
		return d14;
	}

	public void setD14(String d14) {
		this.d14 = d14;
	}

	public String getD15() {
		return d15;
	}

	public void setD15(String d15) {
		this.d15 = d15;
	}

	public String getD16() {
		return d16;
	}

	public void setD16(String d16) {
		this.d16 = d16;
	}

	public String getD17() {
		return d17;
	}

	public void setD17(String d17) {
		this.d17 = d17;
	}

	public String getD18() {
		return d18;
	}

	public void setD18(String d18) {
		this.d18 = d18;
	}

	public String getD19() {
		return d19;
	}

	public void setD19(String d19) {
		this.d19 = d19;
	}

	public String getD2() {
		return d2;
	}

	public void setD2(String d2) {
		this.d2 = d2;
	}

	public String getD20() {
		return d20;
	}

	public void setD20(String d20) {
		this.d20 = d20;
	}

	public String getD21() {
		return d21;
	}

	public void setD21(String d21) {
		this.d21 = d21;
	}

	public String getD22() {
		return d22;
	}

	public void setD22(String d22) {
		this.d22 = d22;
	}

	public String getD23() {
		return d23;
	}

	public void setD23(String d23) {
		this.d23 = d23;
	}

	public String getD24() {
		return d24;
	}

	public void setD24(String d24) {
		this.d24 = d24;
	}

	public String getD25() {
		return d25;
	}

	public void setD25(String d25) {
		this.d25 = d25;
	}

	public String getD26() {
		return d26;
	}

	public void setD26(String d26) {
		this.d26 = d26;
	}

	public String getD27() {
		return d27;
	}

	public void setD27(String d27) {
		this.d27 = d27;
	}

	public String getD28() {
		return d28;
	}

	public void setD28(String d28) {
		this.d28 = d28;
	}

	public String getD29() {
		return d29;
	}

	public void setD29(String d29) {
		this.d29 = d29;
	}

	public String getD3() {
		return d3;
	}

	public void setD3(String d3) {
		this.d3 = d3;
	}

	public String getD30() {
		return d30;
	}

	public void setD30(String d30) {
		this.d30 = d30;
	}

	public String getD31() {
		return d31;
	}

	public void setD31(String d31) {
		this.d31 = d31;
	}

	public String getD32() {
		return d32;
	}

	public void setD32(String d32) {
		this.d32 = d32;
	}

	public String getD33() {
		return d33;
	}

	public void setD33(String d33) {
		this.d33 = d33;
	}

	public String getD34() {
		return d34;
	}

	public void setD34(String d34) {
		this.d34 = d34;
	}

	public String getD35() {
		return d35;
	}

	public void setD35(String d35) {
		this.d35 = d35;
	}

	public String getD36() {
		return d36;
	}

	public void setD36(String d36) {
		this.d36 = d36;
	}

	public String getD37() {
		return d37;
	}

	public void setD37(String d37) {
		this.d37 = d37;
	}

	public String getD38() {
		return d38;
	}

	public void setD38(String d38) {
		this.d38 = d38;
	}

	public String getD39() {
		return d39;
	}

	public void setD39(String d39) {
		this.d39 = d39;
	}

	public String getD4() {
		return d4;
	}

	public void setD4(String d4) {
		this.d4 = d4;
	}

	public String getD40() {
		return d40;
	}

	public void setD40(String d40) {
		this.d40 = d40;
	}

	public String getD41() {
		return d41;
	}

	public void setD41(String d41) {
		this.d41 = d41;
	}

	public String getD42() {
		return d42;
	}

	public void setD42(String d42) {
		this.d42 = d42;
	}

	public String getD43() {
		return d43;
	}

	public void setD43(String d43) {
		this.d43 = d43;
	}

	public String getD44() {
		return d44;
	}

	public void setD44(String d44) {
		this.d44 = d44;
	}

	public String getD45() {
		return d45;
	}

	public void setD45(String d45) {
		this.d45 = d45;
	}

	public String getD46() {
		return d46;
	}

	public void setD46(String d46) {
		this.d46 = d46;
	}

	public String getD47() {
		return d47;
	}

	public void setD47(String d47) {
		this.d47 = d47;
	}

	public String getD48() {
		return d48;
	}

	public void setD48(String d48) {
		this.d48 = d48;
	}

	public String getD49() {
		return d49;
	}

	public void setD49(String d49) {
		this.d49 = d49;
	}

	public String getD5() {
		return d5;
	}

	public void setD5(String d5) {
		this.d5 = d5;
	}

	public String getD50() {
		return d50;
	}

	public void setD50(String d50) {
		this.d50 = d50;
	}

	public String getD51() {
		return d51;
	}

	public void setD51(String d51) {
		this.d51 = d51;
	}

	public String getD52() {
		return d52;
	}

	public void setD52(String d52) {
		this.d52 = d52;
	}

	public String getD53() {
		return d53;
	}

	public void setD53(String d53) {
		this.d53 = d53;
	}

	public String getD54() {
		return d54;
	}

	public void setD54(String d54) {
		this.d54 = d54;
	}

	public String getD55() {
		return d55;
	}

	public void setD55(String d55) {
		this.d55 = d55;
	}

	public String getD56() {
		return d56;
	}

	public void setD56(String d56) {
		this.d56 = d56;
	}

	public String getD57() {
		return d57;
	}

	public void setD57(String d57) {
		this.d57 = d57;
	}

	public String getD58() {
		return d58;
	}

	public void setD58(String d58) {
		this.d58 = d58;
	}

	public String getD59() {
		return d59;
	}

	public void setD59(String d59) {
		this.d59 = d59;
	}

	public String getD6() {
		return d6;
	}

	public void setD6(String d6) {
		this.d6 = d6;
	}

	public String getD60() {
		return d60;
	}

	public void setD60(String d60) {
		this.d60 = d60;
	}

	public String getD61() {
		return d61;
	}

	public void setD61(String d61) {
		this.d61 = d61;
	}

	public String getD62() {
		return d62;
	}

	public void setD62(String d62) {
		this.d62 = d62;
	}

	public String getD63() {
		return d63;
	}

	public void setD63(String d63) {
		this.d63 = d63;
	}

	public String getD64() {
		return d64;
	}

	public void setD64(String d64) {
		this.d64 = d64;
	}

	public String getD65() {
		return d65;
	}

	public void setD65(String d65) {
		this.d65 = d65;
	}

	public String getD66() {
		return d66;
	}

	public void setD66(String d66) {
		this.d66 = d66;
	}

	public String getD67() {
		return d67;
	}

	public void setD67(String d67) {
		this.d67 = d67;
	}

	public String getD68() {
		return d68;
	}

	public void setD68(String d68) {
		this.d68 = d68;
	}

	public String getD69() {
		return d69;
	}

	public void setD69(String d69) {
		this.d69 = d69;
	}

	public String getD7() {
		return d7;
	}

	public void setD7(String d7) {
		this.d7 = d7;
	}

	public String getD70() {
		return d70;
	}

	public void setD70(String d70) {
		this.d70 = d70;
	}

	public String getD71() {
		return d71;
	}

	public void setD71(String d71) {
		this.d71 = d71;
	}

	public String getD72() {
		return d72;
	}

	public void setD72(String d72) {
		this.d72 = d72;
	}

	public String getD73() {
		return d73;
	}

	public void setD73(String d73) {
		this.d73 = d73;
	}

	public String getD74() {
		return d74;
	}

	public void setD74(String d74) {
		this.d74 = d74;
	}

	public String getD75() {
		return d75;
	}

	public void setD75(String d75) {
		this.d75 = d75;
	}

	public String getD76() {
		return d76;
	}

	public void setD76(String d76) {
		this.d76 = d76;
	}

	public String getD77() {
		return d77;
	}

	public void setD77(String d77) {
		this.d77 = d77;
	}

	public String getD78() {
		return d78;
	}

	public void setD78(String d78) {
		this.d78 = d78;
	}

	public String getD79() {
		return d79;
	}

	public void setD79(String d79) {
		this.d79 = d79;
	}

	public String getD8() {
		return d8;
	}

	public void setD8(String d8) {
		this.d8 = d8;
	}

	public String getD80() {
		return d80;
	}

	public void setD80(String d80) {
		this.d80 = d80;
	}

	public String getD81() {
		return d81;
	}

	public void setD81(String d81) {
		this.d81 = d81;
	}

	public String getD82() {
		return d82;
	}

	public void setD82(String d82) {
		this.d82 = d82;
	}

	public String getD83() {
		return d83;
	}

	public void setD83(String d83) {
		this.d83 = d83;
	}

	public String getD84() {
		return d84;
	}

	public void setD84(String d84) {
		this.d84 = d84;
	}

	public String getD85() {
		return d85;
	}

	public void setD85(String d85) {
		this.d85 = d85;
	}

	public String getD86() {
		return d86;
	}

	public void setD86(String d86) {
		this.d86 = d86;
	}

	public String getD87() {
		return d87;
	}

	public void setD87(String d87) {
		this.d87 = d87;
	}

	public String getD88() {
		return d88;
	}

	public void setD88(String d88) {
		this.d88 = d88;
	}

	public String getD89() {
		return d89;
	}

	public void setD89(String d89) {
		this.d89 = d89;
	}

	public String getD9() {
		return d9;
	}

	public void setD9(String d9) {
		this.d9 = d9;
	}

	public String getD90() {
		return d90;
	}

	public void setD90(String d90) {
		this.d90 = d90;
	}

	public String getD91() {
		return d91;
	}

	public void setD91(String d91) {
		this.d91 = d91;
	}

	public String getD92() {
		return d92;
	}

	public void setD92(String d92) {
		this.d92 = d92;
	}

	public String getD93() {
		return d93;
	}

	public void setD93(String d93) {
		this.d93 = d93;
	}

	public String getD94() {
		return d94;
	}

	public void setD94(String d94) {
		this.d94 = d94;
	}

	public String getD95() {
		return d95;
	}

	public void setD95(String d95) {
		this.d95 = d95;
	}

	public String getD96() {
		return d96;
	}

	public void setD96(String d96) {
		this.d96 = d96;
	}

	public String getD97() {
		return d97;
	}

	public void setD97(String d97) {
		this.d97 = d97;
	}

	public String getD98() {
		return d98;
	}

	public void setD98(String d98) {
		this.d98 = d98;
	}

	public String getD99() {
		return d99;
	}

	public void setD99(String d99) {
		this.d99 = d99;
	}

	public ArrayList getListaCampos() {
		return listaCampos;
	}

	public void setListaCampos(ArrayList listaCampos) {
		this.listaCampos = listaCampos;
	}

	public byte getToDownload() {
		return toDownload;
	}

	public void setToDownload(byte toDownload) {
		this.toDownload = toDownload;
	}

	public String getDateDead() {
		return dateDead;
	}

	public void setDateDead(String dateDead) {
		this.dateDead = dateDead;
	}

	public String getLoteDoc() {
		return loteDoc;
	}

	public void setLoteDoc(String loteDoc) {
		this.loteDoc = loteDoc;
	}
	
	public String getIdDocumentOrigen() {
		return idDocumentOrigen;
	}

	public void setIdDocumentOrigen(String idDocumentOrigen) {
		this.idDocumentOrigen = idDocumentOrigen;
	}

	public String getIdVersionOrigen() {
		return idVersionOrigen;
	}

	public void setIdVersionOrigen(String idVersionOrigen) {
		this.idVersionOrigen = idVersionOrigen;
	}

	public int getIdRegisterClass() {
		return idRegisterClass;
	}

	public void setIdRegisterClass(int idRegisterClass) {
		this.idRegisterClass = idRegisterClass;
	}

	public String getDescRegisterClass() {
		return descRegisterClass;
	}

	public void setDescRegisterClass(String descRegisterClass) {
		this.descRegisterClass = descRegisterClass;
	}

	public int getIdProgramAudit() {
		return idProgramAudit;
	}

	public void setIdProgramAudit(int idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public int getIdPlanAudit() {
		return idPlanAudit;
	}

	public void setIdPlanAudit(int idPlanAudit) {
		this.idPlanAudit = idPlanAudit;
	}
	
	public String getDescProgramAudit() {
		return descProgramAudit;
	}

	public void setDescProgramAudit(String descProgramAudit) {
		this.descProgramAudit = descProgramAudit;
	}

	public String getDescPlanAudit() {
		return descPlanAudit;
	}

	public void setDescPlanAudit(String descPlanAudit) {
		this.descPlanAudit = descPlanAudit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseDocumentForm [printing=").append(printing)
				.append(", copyContents=").append(copyContents)
				.append(", numberGen=").append(numberGen)
				.append(", idDocument=").append(idDocument)
				.append(", nameDocument=").append(nameDocument)
				.append(", nameDocumentOld=").append(nameDocumentOld)
				.append(", owner=").append(owner)
				//ydavila Ticket 001-00-003023
				.append(", respsolcambio=").append(respsolcambio)
				.append(", respsolelimin=").append(respsolelimin)
				.append(", respsolimpres=").append(respsolimpres)
				
				.append(", ownerOld=").append(", ownerOld=")
				.append(ownerOld).append(", idperson=").append(idperson)
				.append(", prefix=").append(prefix).append(", number=")
				.append(number).append(", typeDocument=").append(typeDocument)
				.append(", descriptTypeDoc=").append(descriptTypeDoc)
				//ydavila Ticket 001-00-003265
				.append(", comment=").append(comment)
				.append(", normISO=").append(normISO)
				.append(", normISODescriptId=").append(normISODescriptId)
				.append(", docProtected=").append(docProtected)
				.append(", docPublic=").append(docPublic)
				.append(", docOnline=").append(docOnline).append(", URL=")
				.append(URL).append(", keys=").append(keys)
				.append(", descript=").append(descript).append(", nodeType=")
				.append(nodeType).append(", nameFile=").append(nameFile)
				.append(", nameFileParalelo=").append(nameFileParalelo)
				.append(", mayorVer=").append(mayorVer).append(", minorVer=")
				.append(minorVer).append(", approved=").append(approved)
				.append(", expires=").append(expires).append(", comments=")
				.append(comments).append(", idNode=").append(idNode)
				.append(", dateCreation=").append(dateCreation)
				.append(", normISODescript=").append(normISODescript)
				.append(", dateApproved=").append(dateApproved)
				.append(", dateExpires=").append(dateExpires)
				.append(", dateApprovedDrafts=").append(dateApprovedDrafts)
				.append(", dateExpiresDrafts=").append(dateExpiresDrafts)
				.append(", datePublic=").append(datePublic).append(", numVer=")
				.append(numVer).append(", numVerFiles=").append(numVerFiles)
				.append(", idFiles=").append(idFiles).append(", expediente=")
				.append(expediente).append(", nameOwner=").append(nameOwner)
				.append(", nameOwnerOld=").append(nameOwnerOld)
				.append(", razonDeCambio=").append(razonDeCambio)
				.append(", razonDeCambioName=").append(razonDeCambioName)
				.append(", razonDeCambioOwner=").append(razonDeCambioOwner)
				.append(", razonDeCambioFiles=").append(razonDeCambioFiles)
				.append(", statu=").append(statu).append(", contextType=")
				.append(contextType).append(", contextTypeParalelo=")
				.append(contextTypeParalelo).append(", docRelations=")
				.append(docRelations).append(", checkOut=").append(checkOut)
				.append(", check=").append(check).append(", statuDoc=")
				.append(statuDoc).append(", lastOperation=")
				.append(lastOperation).append(", loadDoc=").append(loadDoc)
				.append(", charge=").append(charge).append(", imprimir=")
				.append(imprimir).append(", idLocation=").append(idLocation)
				.append(", nodeActive=").append(nodeActive)
				.append(", numCorrelativoExiste=").append(numCorrelativoExiste)
				.append(", queryReporte=").append(queryReporte)
				.append(", lastVersionApproved=").append(lastVersionApproved)
				.append(", rout=").append(rout).append(", typeFormat=")
				.append(typeFormat).append(", controlada=").append(controlada)
				//ydavila Ticket 001-00-003023
				.append(typeFormat).append(", paracambiar=").append(paracambiar)
				.append(typeFormat).append(", paraeliminar=").append(paraeliminar)
				
				.append(", dateDead=").append(dateDead).append(", loteDoc=")
				.append(loteDoc).append(", rejectDocument=")
				.append(rejectDocument).append(", numberCopies=")
				.append(numberCopies).append(", toForFiles=")
				.append(toForFiles).append(", toRead=").append(toRead)
				.append(", toDelete=").append(toDelete).append(", toEdit=")
				.append(toEdit).append(", toMove=").append(toMove)
				.append(", toAdmon=").append(toAdmon).append(", toViewDocs=")
				.append(toViewDocs).append(", toReview=").append(toReview)
				.append(", toApproved=").append(toApproved)
				.append(", toMoveDocs=").append(toMoveDocs)
				.append(", toCheckOut=").append(toCheckOut)
				.append(", toEditRegister=").append(toEditRegister)
				.append(", toImpresion=").append(toImpresion)
				.append(", toCheckTodos=").append(toCheckTodos)
				.append(", toDownload=").append(toDownload)
				.append(", usersSelected=")
				.append(Arrays.toString(usersSelected))
				.append(", isflowusuario=").append(isflowusuario)
				.append(", isflowcargo=").append(isflowcargo)
				.append(", isNotflowsecuencial=").append(isNotflowsecuencial)
				.append(", typeWF=").append(typeWF).append(", selected=")
				.append(selected).append(", descripcionActiveFactory=")
				.append(descripcionActiveFactory)
				.append(", checkactiveFactory=").append(checkactiveFactory)
				.append(", fileInicio=").append(fileInicio)
				.append(", folderOut=").append(folderOut).append(", idUser=")
				.append(idUser).append(", d1=").append(d1).append(", d2=")
				.append(d2).append(", d3=").append(d3).append(", d4=")
				.append(d4).append(", d5=").append(d5).append(", d6=")
				.append(d6).append(", d7=").append(d7).append(", d8=")
				.append(d8).append(", d9=").append(d9).append(", d10=")
				.append(d10).append(", d11=").append(d11).append(", d12=")
				.append(d12).append(", d13=").append(d13).append(", d14=")
				.append(d14).append(", d15=").append(d15).append(", d16=")
				.append(d16).append(", d17=").append(d17).append(", d18=")
				.append(d18).append(", d19=").append(d19).append(", d20=")
				.append(d20).append(", d21=").append(d21).append(", d22=")
				.append(d22).append(", d23=").append(d23).append(", d24=")
				.append(d24).append(", d25=").append(d25).append(", d26=")
				.append(d26).append(", d27=").append(d27).append(", d28=")
				.append(d28).append(", d29=").append(d29).append(", d30=")
				.append(d30).append(", d31=").append(d31).append(", d32=")
				.append(d32).append(", d33=").append(d33).append(", d34=")
				.append(d34).append(", d35=").append(d35).append(", d36=")
				.append(d36).append(", d37=").append(d37).append(", d38=")
				.append(d38).append(", d39=").append(d39).append(", d40=")
				.append(d40).append(", d41=").append(d41).append(", d42=")
				.append(d42).append(", d43=").append(d43).append(", d44=")
				.append(d44).append(", d45=").append(d45).append(", d46=")
				.append(d46).append(", d47=").append(d47).append(", d48=")
				.append(d48).append(", d49=").append(d49).append(", d50=")
				.append(d50).append(", d51=").append(d51).append(", d52=")
				.append(d52).append(", d53=").append(d53).append(", d54=")
				.append(d54).append(", d55=").append(d55).append(", d56=")
				.append(d56).append(", d57=").append(d57).append(", d58=")
				.append(d58).append(", d59=").append(d59).append(", d60=")
				.append(d60).append(", d61=").append(d61).append(", d62=")
				.append(d62).append(", d63=").append(d63).append(", d64=")
				.append(d64).append(", d65=").append(d65).append(", d66=")
				.append(d66).append(", d67=").append(d67).append(", d68=")
				.append(d68).append(", d69=").append(d69).append(", d70=")
				.append(d70).append(", d71=").append(d71).append(", d72=")
				.append(d72).append(", d73=").append(d73).append(", d74=")
				.append(d74).append(", d75=").append(d75).append(", d76=")
				.append(d76).append(", d77=").append(d77).append(", d78=")
				.append(d78).append(", d79=").append(d79).append(", d80=")
				.append(d80).append(", d81=").append(d81).append(", d82=")
				.append(d82).append(", d83=").append(d83).append(", d84=")
				.append(d84).append(", d85=").append(d85).append(", d86=")
				.append(d86).append(", d87=").append(d87).append(", d88=")
				.append(d88).append(", d89=").append(d89).append(", d90=")
				.append(d90).append(", d91=").append(d91).append(", d92=")
				.append(d92).append(", d93=").append(d93).append(", d94=")
				.append(d94).append(", d95=").append(d95).append(", d96=")
				.append(d96).append(", d97=").append(d97).append(", d98=")
				.append(d98).append(", d99=").append(d99).append(", d100=")
				.append(d100).append(", listaCampos=").append(listaCampos)
				.append("]");
		return builder.toString();
	}

}
