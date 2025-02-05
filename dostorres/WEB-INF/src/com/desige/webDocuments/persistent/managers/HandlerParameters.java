package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.focus.util.ModuloBean;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerParameters.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>20/05/2004 (NC) Creation</li>
 *          <li>14/05/2005 (SR) Se modific� para que cargara y actualizara en el formulario la variable session</li>
 *          <li>14/05/2005 (NC) se actualiz� para manipular el campo msgWFExpires
 *          <li>22/05/2005 (NC) Cambios para manipular el campo typePrefix
 *          <li>22/05/2005 (SR) Se actualizaron y cargaron los parametros expireDrafts,monthsExpireDrafts,unitTimeExpireDrafts,msgWFBorrador
 *          <li>15/06/2005 (NC) Cambios para manipular el campo NumByLocation
 *          <li>22/07/2005 (SR) se actualizo el msgDocExpirado en parameters e cargo
 *          <li>27/07/2005 (SR) se actualizo el setTypeNumByLocation para ver si ya escojieron el numerocorrelativo
 *          <li>08/12/2005 (NC) Uso del Log</li>
 *          <li>15/06/2007 (YS) Se setea el atributo viewReaders en el metodo load() y se modifica en el metodo save()</li>
 *          <li>07/07/2007 (YS) Se setea el atributo reasigneUserFTP en el metodo load() y se modifica en el metodo save()</li>
 *          <li>20/07/2007 (YS) Se setea el atributo downloadLastVer en el metodo load() y se modifica en el metodo save()</li>
 *          <li>01/08/2007 (YS) Se setea el atributo impNameCharge en el metodo load() y se modifica en el metodo save()</li>
 *          <li>03/08/2007 (YS) Se setean atributos headImp1, headImp2, headImp3 en el metodo load() y se modifica en el metodo save()</li>
 *          <li>15/10/2007 (YS) Se setea el atributo mailAccount en el metodo load() y se modifica en el metodo save()</li>
 *          <li>23/11/2007 (YS) Se setea el atributo endSessionEdit en el metodo load() y se modifica en el metodo save()</li>
 *          <li>01/08/2007 (YS) Se setea el atributo viewCreator en el metodo load() y se modifica en el metodo save()</li>
 *          <li>27/01/2009 (JR) Se setea el atributo viewCollaborator en el metodo load() y se modifica en el metodo save()</li>
 *          <li>27/01/2009 (JR) Se setea el atributo viewpdftool en el metodo load() y se modifica en el metodo save()</li>
 *          <li>27/01/2009 (JR) Se setea el atributo oooExeFolder en el metodo load() y se modifica en el metodo save()</li>
 *          </ul>
 */
public class HandlerParameters extends HandlerBD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -824888208623223869L;
	private final static Logger log = LoggerFactory.getLogger(HandlerParameters.class.getName());

	public static final int validitySacopType0 = 0; // validacion por cierre de ejercicio
	public static final int validitySacopType1 = 1; // validacion por meses
	
	public static final BaseParametersForm PARAMETROS = new BaseParametersForm();

	public static final ArrayList<Integer> DOCUMENTOS_POR_VENCER = new ArrayList<Integer>();

	public static void loadParametros() {
		//System.out.println("Load parametros en memoria *****************");
		try {
			load(PARAMETROS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load(BaseParametersForm forma) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT * FROM parameters");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {

			forma.setLengthPass(prop.getProperty("lengthPass"));

			forma.setAllowDuplicatePass(prop.getProperty("allowDuplicatePass"));

			if (ToolsHTML.isNumeric(prop.getProperty("expirePass"))) {
				forma.setExpirePass(Byte.parseByte(prop.getProperty("expirePass")));
			} else {
				forma.setExpirePass((byte) 1);
			}

			if (ToolsHTML.isEmptyOrNull(prop.getProperty("endsession"))) {
				forma.setEndSession(Integer.parseInt("10"));
			} else {
				forma.setEndSession(Integer.parseInt(prop.getProperty("endsession")));
			}

			// YSA 23/11/2007
			if (ToolsHTML.isEmptyOrNull(prop.getProperty("endSessionEdit"))) {
				forma.setEndSessionEdit(Integer.parseInt("10"));
			} else {
				forma.setEndSessionEdit(Integer.parseInt(prop.getProperty("endSessionEdit")));
			}

			forma.setNotifyEmail(Byte.parseByte(prop.getProperty("notifyEmail")));

			// Luis Cisneros 28/02/07
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("validateEmail"))) {
				forma.setValidateEmail(Byte.parseByte(prop.getProperty("validateEmail")));
			}

			// JRivero
			// System.out.println(prop.getProperty("domainEmail"));
			// System.out.println(prop.getProperty("domainemail"));
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("domainEmail"))) {
				forma.setDomainEmail(prop.getProperty("domainEmail"));
			}
			forma.setDebugEmail(prop.getProperty("mail_enable_debug"));
			forma.setPasswordEmail(prop.getProperty("mail_auth_password"));
			forma.setPortEmail(prop.getProperty("mail_smtp_port"));
			forma.setAuthProtocolEmail(prop.getProperty("mail_auth_protocol"));

			// Luis Cisneros 24/03-07
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("printOwnerAdmin"))) {
				forma.setPrintOwnerAdmin(Byte.parseByte(prop.getProperty("printOwnerAdmin")));
			}

			// JRivero
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("completeFTP"))) {
				forma.setCompleteFTP(Byte.parseByte(prop.getProperty("completeFTP")));
			}

			// JRivero
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFCompletados"))) {
				forma.setMsgWFCompletados(prop.getProperty("msgWFCompletados"));
			}

			// YSA 15/06/2007
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("viewReaders"))) {
				forma.setViewReaders(Byte.parseByte(prop.getProperty("viewReaders")));
			}

			// YSA 01/08/2008
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("viewCreator"))) {
				forma.setViewCreator(Byte.parseByte(prop.getProperty("viewCreator")));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("viewCollaborator"))) {
				forma.setViewCollaborator(Byte.parseByte(prop.getProperty("viewCollaborator")));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("viewpdftool"))) {
				forma.setViewpdftool(Byte.parseByte(prop.getProperty("viewpdftool")));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("oooExeFolder"))) {
				forma.setOooExeFolder(prop.getProperty("oooExeFolder"));
			}

			// YSA 07/07/2007
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("reasigneUserFTP"))) {
				forma.setReasigneUserFTP(Byte.parseByte(prop.getProperty("reasigneUserFTP")));
			}

			// YSA 20/07/2007
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("downloadLastVer"))) {
				forma.setDownloadLastVer(Byte.parseByte(prop.getProperty("downloadLastVer")));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("validNavigator"))) {
				forma.setValidNavigator(Byte.parseByte(prop.getProperty("validNavigator")));
			} else {
				forma.setValidNavigator(Byte.parseByte("1"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("fileNotOpenSave"))) {
				forma.setFileNotOpenSave(Byte.parseByte(prop.getProperty("fileNotOpenSave")));
			} else {
				forma.setFileNotOpenSave(Byte.parseByte("1"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("documentReject"))) {
				forma.setDocumentReject(Byte.parseByte(prop.getProperty("documentReject")));
			} else {
				forma.setDocumentReject(Byte.parseByte("0"));
			}

			// ydavila Ticket 001-00-003023
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("autenticarflujo"))) {
				forma.setautenticarflujo(Byte.parseByte(prop.getProperty("autenticarflujo")));
			}
			forma.setrespsolcambio("");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("respsolcambio"))) {
				forma.setrespsolcambio(String.format(prop.getProperty("respsolcambio")));
			}
			forma.setrespsolelimin("");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("respsolelimin"))) {
				forma.setrespsolelimin(String.format(prop.getProperty("respsolelimin")));
			}
			forma.setRespsolsacop("");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("respsolsacop"))) {
				forma.setRespsolsacop(String.format(prop.getProperty("respsolsacop")));
			}
			forma.setrespsolimpres("");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("respsolimpres"))) {
				forma.setrespsolimpres(String.format(prop.getProperty("respsolimpres")));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("fileDoubleVersion"))) {
				forma.setFileDoubleVersion(String.valueOf(prop.getProperty("fileDoubleVersion")));
			} else {
				forma.setFileDoubleVersion("");
			}

			forma.setFileUploadVersion(ToolsHTML.isEmptyOrNull(prop.getProperty("fileUploadVersion"), ""));
			forma.setFileNativeViewer(ToolsHTML.isEmptyOrNull(prop.getProperty("fileNativeViewer"), ""));

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("openoffice"))) {
				forma.setOpenoffice(Byte.parseByte(prop.getProperty("openoffice")));
			} else {
				forma.setOpenoffice(Byte.parseByte("1"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("printVersion"))) {
				forma.setPrintVersion(Byte.parseByte(prop.getProperty("printVersion")));
			} else {
				forma.setPrintVersion(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("disabledCache"))) {
				forma.setDisabledCache(Byte.parseByte(prop.getProperty("disabledCache")));
			} else {
				forma.setDisabledCache(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("folderCmp"))) {
				forma.setFolderCmp(prop.getProperty("folderCmp"));
			} else {
				forma.setFolderCmp("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("attachedFolder0"))) {
				forma.setAttachedFolder0(prop.getProperty("attachedFolder0"));
			} else {
				forma.setAttachedFolder0("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("attachedFolder1"))) {
				forma.setAttachedFolder1(prop.getProperty("attachedFolder1"));
			} else {
				forma.setAttachedFolder1("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("attachedField"))) {
				forma.setAttachedField(prop.getProperty("attachedField"));
			} else {
				forma.setAttachedField("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("domain"))) {
				forma.setDomain(prop.getProperty("domain"));
			} else {
				forma.setDomain("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("ldapUrl"))) {
				forma.setLdapUrl(prop.getProperty("ldapUrl"));
			} else {
				forma.setLdapUrl("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("repository"))) {
				forma.setRepository(prop.getProperty("repository"));
			} else {
				forma.setRepository("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("editorOnDemand"))) {
				forma.setEditorOnDemand(Byte.parseByte(prop.getProperty("editorOnDemand")));
			} else {
				forma.setEditorOnDemand(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("copyContents"))) {
				forma.setCopyContents(Byte.parseByte(prop.getProperty("copyContents")));
			} else {
				forma.setCopyContents(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("editOriginatorWF"))) {
				forma.setEditOriginatorWF(Byte.parseByte(prop.getProperty("editOriginatorWF")));
			} else {
				forma.setEditOriginatorWF(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("printNumber"))) {
				forma.setPrintNumber(Byte.parseByte(prop.getProperty("printNumber")));
			} else {
				forma.setPrintNumber(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("printApprovedDate"))) {
				forma.setPrintApprovedDate(Byte.parseByte(prop.getProperty("printApprovedDate")));
			} else {
				forma.setPrintApprovedDate(Byte.parseByte("0"));
			}

			// YSA 01/08/2007
			if (ToolsHTML.isNumeric(prop.getProperty("impNameCharge"))) {
				forma.setImpNameCharge(Integer.parseInt(prop.getProperty("impNameCharge")));
			} else {
				forma.setImpNameCharge(0);
			}

			// YSA 03/08/2007
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("headImp1"))) {
				forma.setHeadImp1(prop.getProperty("headImp1"));
			} else {
				forma.setHeadImp1("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("headImp2"))) {
				forma.setHeadImp2(prop.getProperty("headImp2"));
			} else {
				forma.setHeadImp2("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("headImp3"))) {
				forma.setHeadImp3(prop.getProperty("headImp3"));
			} else {
				forma.setHeadImp3("");
			}

			// YSA 15/10/2007
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("mailAccount"))) {
				forma.setMailAccount(prop.getProperty("mailAccount"));
			} else {
				forma.setMailAccount("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("numberSacop"))) {
				forma.setNumberSacop(prop.getProperty("numberSacop"));
			} else {
				forma.setNumberSacop("1000010");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("publicEraser"))) {
				forma.setPublicEraser(Byte.parseByte(prop.getProperty("publicEraser")));
			} else {
				forma.setPublicEraser(Byte.parseByte("0"));
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("changeAditionalField"))) {
				forma.setChangeAditionalField(Byte.parseByte(prop.getProperty("changeAditionalField")));
			} else {
				forma.setChangeAditionalField(Byte.parseByte("0"));
			}
			
			forma.setSmtpMail(prop.getProperty("smtpMail"));
			forma.setPostfixMail(prop.getProperty("postfixMail"));
			forma.setQualitySystem(Byte.parseByte(prop.getProperty("qualitySystem")));
			forma.setVersionCom(Byte.parseByte(prop.getProperty("versionCom")));
			forma.setLogDoc(Byte.parseByte(prop.getProperty("logDoc")));
			forma.setAllowWF(Byte.parseByte(prop.getProperty("allowWF")));
			forma.setDocNumberGenerator(Integer.parseInt(prop.getProperty("docNumber")));
			forma.setLengthDocNumber(prop.getProperty("lengthDocNumber"));
			forma.setResetDocNumber(prop.getProperty("resetDocNumber"));
			if (ToolsHTML.isEmptyOrNull(prop.getProperty("daysEndPass").trim())) {
				forma.setDaysEndPass(DesigeConf.getProperty("param.daysEndPass0"));
			} else {
				forma.setDaysEndPass(prop.getProperty("daysEndPass").trim());
			}
			if (ToolsHTML.isNumeric(prop.getProperty("expireDoc"))) {
				forma.setExpireDoc(Byte.parseByte(prop.getProperty("expireDoc")));
			} else {
				forma.setExpireDoc((byte) 1);
			}
			if (ToolsHTML.isEmptyOrNull(prop.getProperty("monthExpireDoc"))) {
				forma.setMonthsExpireDocs(DesigeConf.getProperty("param.monthExpireDoc0"));
				forma.setUnitTimeExpire(DesigeConf.getProperty("param.unit0"));
			} else {
				forma.setMonthsExpireDocs(prop.getProperty("monthExpireDoc").trim());
				forma.setUnitTimeExpire(prop.getProperty("unitTimeExpire").trim());
			}

			if (ToolsHTML.isEmptyOrNull(prop.getProperty("monthsDeadDoc"))) {
				forma.setMonthsDeadDocs(DesigeConf.getProperty("param.monthDeadDoc0"));
				forma.setUnitTimeDead(DesigeConf.getProperty("param.unitDead0"));
			} else {
				forma.setMonthsDeadDocs(prop.getProperty("monthsDeadDocs").trim());
				forma.setUnitTimeDead(prop.getProperty("unitTimeDead").trim());
			}

			// SIMON 16 DE JUNIO 2005 INICIO
			if (ToolsHTML.isNumeric(prop.getProperty("expireDrafts"))) {
				forma.setExpireDrafts(Byte.parseByte(prop.getProperty("expireDrafts")));
			} else {
				forma.setExpireDrafts((byte) 1);
			}

			if (ToolsHTML.isNumeric(prop.getProperty("deadDoc"))) {
				forma.setDeadDoc(Byte.parseByte(prop.getProperty("deadDoc")));
			} else {
				forma.setDeadDoc((byte) 1);
			}

			// SIMON 16 DE JUNIO 2005 INICIO
			if (ToolsHTML.isNumeric(prop.getProperty("expireDrafts"))) {
				forma.setExpireDrafts(Byte.parseByte(prop.getProperty("expireDrafts")));
			} else {
				forma.setExpireDrafts((byte) 1);
			}
			// ESTOS PARAMETROS DE DOCS SE CUMPLEN PARA BORARDORES,POR ESO SE UTILIZAR�N.
			if (ToolsHTML.isEmptyOrNull(prop.getProperty("monthsExpireDrafts"))) {
				forma.setMonthsExpireDrafts(DesigeConf.getProperty("param.monthExpireDoc0"));
				forma.setUnitTimeExpireDrafts(DesigeConf.getProperty("param.unit0"));
			} else {
				forma.setMonthsExpireDrafts(prop.getProperty("monthsExpireDrafts").trim());
				forma.setUnitTimeExpireDrafts(prop.getProperty("unitTimeExpireDrafts").trim());
			}
			// SIMON 16 DE JUNIO 2005 FIN

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFExpires"))) {
				forma.setMsgWFExpires(prop.getProperty("msgWFExpires"));
			} else {
				forma.setMsgWFExpires("");
			}
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFRevision"))) {
				forma.setMsgWFRevision(prop.getProperty("msgWFRevision"));
			} else {
				forma.setMsgWFRevision("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFAprobados"))) {
				forma.setMsgWFAprobados(prop.getProperty("msgWFAprobados"));
			} else {
				forma.setMsgWFAprobados("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFCancelados"))) {
				forma.setMsgWFCancelados(prop.getProperty("msgWFCancelados"));
			} else {
				forma.setMsgWFCancelados("");
			}
			// 16 DE JUNIO 2005 INICIO
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgWFBorrador"))) {
				forma.setMsgWFBorrador(prop.getProperty("msgWFBorrador"));
			} else {
				forma.setMsgWFBorrador("");
			}
			// 16 DE JUNIO 2005 FIN

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgDocExpirado"))) {
				forma.setMsgDocExpirado(prop.getProperty("msgDocExpirado"));
			} else {
				forma.setMsgDocExpirado("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgSacopAccionesPorVencer"))) {
				forma.setMsgSacopAccionesPorVencer(prop.getProperty("msgSacopAccionesPorVencer"));
			} else {
				forma.setMsgSacopAccionesPorVencer("");
			}

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("msgSacopAccionesVencidas"))) {
				forma.setMsgSacopAccionesVencidas(prop.getProperty("msgSacopAccionesVencidas"));
			} else {
				forma.setMsgSacopAccionesVencidas("");
			}

			forma.setDiasAlertaAccionesSacopPorVencer(ToolsHTML.parseInt(prop.getProperty("diasAlertaAccionesSacopPorVencer"), 5));
			forma.setNumberDaysInactivitySACOP(ToolsHTML.parseInt(prop.getProperty("numberDaysInactivitySACOP"), 5));
			forma.setNumberDaysWithoutActionSACOP(ToolsHTML.parseInt(prop.getProperty("numberDaysWithoutActionSACOP"), 5));
			forma.setListUserAddressee(ToolsHTML.isEmptyOrNull(String.valueOf(prop.getProperty("listUserAddressee")), ""));
			forma.setValiditySacop(ToolsHTML.parseInt(prop.getProperty("validitySacop"), 0));
			forma.setValiditySacopType(ToolsHTML.parseInt(prop.getProperty("validitySacopType"), 0));
			forma.setValiditySacopMonth(ToolsHTML.parseInt(prop.getProperty("validitySacopMonth"), 0));
			forma.setIdNormAudit(ToolsHTML.parseInt(prop.getProperty("idNormAudit"), 0));

			String numByLocation = prop.getProperty("NumByLocation");
			log.debug("numByLocation = " + numByLocation);
			if (ToolsHTML.isNumeric(numByLocation)) {
				forma.setNumDocLoc(Byte.parseByte(numByLocation));
			} else {
				forma.setNumDocLoc((byte) 0);
			}
			// aqui se ve si ya escojieron el tipo de nuero correlativo
			String typeNumByLocation = prop.getProperty("typeNumByLocation");
			log.debug("typeNumByLocation = " + typeNumByLocation);
			if ((ToolsHTML.isEmptyOrNull(typeNumByLocation)) || (typeNumByLocation.equalsIgnoreCase(Constants.typeNumByLocationVacio))) {
				forma.setTypeNumByLocation(false);
			} else {
				forma.setTypeNumByLocation(true);
			}
			String cargosacop = prop.getProperty("cargosacop");
			if (ToolsHTML.isNumeric(cargosacop)) {
				forma.setCargosacop(Byte.parseByte(cargosacop));
			} else {
				forma.setCargosacop((byte) 0);
			}
			// Tipo de Ordenamiento de la Estructura
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("typeOrden"))) {
				forma.setTypeOrder(Byte.parseByte(prop.getProperty("typeOrden")));
			} else {
				forma.setTypeOrder(Constants.notPermission);
			}
			// Permisologia para el cambios de nombre y propietario de un doc.
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("changeProperties"))) {
				forma.setChangeProperties(Byte.parseByte(prop.getProperty("changeProperties")));
			} else {
				forma.setChangeProperties(Constants.notPermission);
			}

			// Idioma Predeterminado de la Aplicaci�n para la creaci�n de Usuarios en el Sistema
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("idioma"))) {
				forma.setIdioma(prop.getProperty("idioma").trim());
			} else {
				forma.setIdioma(DesigeConf.getProperty("language.Default"));
			}

			// Permisologia para la desincorporacion de documentos eliminados
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("fileDeadFile"))) {
				forma.setFileDeadFile(Byte.parseByte(prop.getProperty("fileDeadFile")));
			} else {
				forma.setFileDeadFile(Constants.notPermission);
			}

			if (ToolsHTML.isEmptyOrNull(prop.getProperty("fileDeadTime"))) {
				forma.setFileDeadTime(DesigeConf.getProperty("param.monthExpireDoc0"));
				forma.setFileDeadUnit(DesigeConf.getProperty("param.unit0"));
			} else {
				forma.setFileDeadTime(prop.getProperty("fileDeadTime").trim());
				forma.setFileDeadUnit(prop.getProperty("fileDeadUnit").trim());
			}

			// Permisologia para la desincorporacion de versiones de documentos
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("fileDeadVerbose"))) {
				forma.setFileDeadVerbose(Byte.parseByte(prop.getProperty("fileDeadVerbose")));
			} else {
				forma.setFileDeadVerbose(Constants.notPermission);
			}

			forma.setVigenciaFlujosCanceladosPrincipal(Integer.parseInt(prop.getProperty("vigencia_flujos_cancelados")));
			forma.setPiePaginaWaterMark(prop.getProperty("piePaginaWaterMark"));
			//Collection users = HandlerDBUser.getAllUsers(); // NO TIENE SENTIDO ESTA CONSULTA, NO SE UTILIZA
		}
	}

	public static boolean IsNotNulltypeNumByLocation() {
		StringBuffer edit = new StringBuffer("select typeNumByLocation  from  parameters  ");
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		// System.out.println("[actualizaParametro]" + edit.toString());
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			ResultSet rs = st.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					String typeNumByLocation = rs.getString("typeNumByLocation");
					if ((ToolsHTML.isEmptyOrNull(typeNumByLocation)) || (typeNumByLocation.equalsIgnoreCase(Constants.typeNumByLocationVacio))) {
						resp = false;
					} else {
						resp = true;
					}
				}
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}

		} catch (Exception e) {
			setMensaje(e.getMessage());
			e.printStackTrace();
			resp = false;
		}
		return resp;
	}

	// SIMON 25 DE JULIO 2005 INICIO
	// actualizamos el tipo de numcorrelativo par la base de datos
	// de esta manera el administrador ya define el correlativo y queda predefinido para el resto de vida del software
	public synchronized static boolean actualizaParametro(String TypeNumByLocation) {
		return actualizaParametro(null, TypeNumByLocation);
	}

	public synchronized static boolean actualizaParametro(Connection con, String TypeNumByLocation) {
		StringBuffer edit = new StringBuffer("UPDATE parameters SET ");
		edit.append("typeNumByLocation = CAST(").append(TypeNumByLocation).append(" as bit) ");
		PreparedStatement st = null;
		boolean resp = false;
		boolean cerrar = false;
		log.debug("[actualizaParametro]" + edit.toString());
		try {
			if (con == null || con.isClosed()) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				cerrar = true;
			}
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.executeUpdate();
			resp = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
			resp = false;
		} finally {
			if (cerrar) {
				JDBCUtil.closeConnection(con, "Error en actualizaParametro()");
			}
		}
		return resp;
	}

	// SIMON 25 DE JULIO 2005 FIN
	// YSA 25 DE ABRIL 2008 INICIO
	// Se incrementa en 1 la longitud para correlativos
	public synchronized static boolean updateLengthDocNumber() {
		return updateLengthDocNumber(null);
	}

	public synchronized static boolean updateLengthDocNumber(Connection con) {
		StringBuffer edit = new StringBuffer("UPDATE parameters SET ");
		edit.append(" lengthDocNumber = lengthDocNumber + 1");
		PreparedStatement st = null;
		boolean resp = false;
		log.debug("[updateLengthDocNumber]" + edit.toString());
		boolean cerrar = false;
		try {
			if (con == null || con.isClosed()) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				cerrar = true;
			}
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.executeUpdate();
			resp = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
			resp = false;
		} finally {
			if (cerrar) {
				JDBCUtil.closeConnection(con, "updateLengthDocNumber()");
			}
		}
		return resp;
	}
	// YSA 25 DE ABRIL 2008 FIN

	public synchronized static boolean save(BaseParametersForm forma) {
		StringBuffer edit = new StringBuffer("UPDATE parameters SET lengthPass = '").append(forma.getLengthPass());
		edit.append("', allowDuplicatePass='").append(forma.getAllowDuplicatePass()).append("'");
		edit.append(", expirePass='").append(forma.getExpirePass()).append("'");
		edit.append(",notifyEmail='").append(forma.getNotifyEmail()).append("'");
		edit.append(",smtpMail='").append(forma.getSmtpMail()).append("',qualitySystem='").append(forma.getQualitySystem()).append("'");

		// Luis Cisneros 27/02/07
		edit.append(",validateEmail='").append(forma.getValidateEmail()).append("'");

		// JRivero
		edit.append(",domainEmail='").append(forma.getDomainEmail()).append("'");
		edit.append(",postfixMail='").append(forma.getPostfixMail()).append("'");
		edit.append(",mail_auth_protocol='").append(forma.getAuthProtocolEmail()).append("'");
		edit.append(",mail_smtp_port='").append(forma.getPortEmail()).append("'");
		edit.append(",mail_auth_password='").append(forma.getPasswordEmail()).append("'");
		edit.append(",mail_enable_debug='").append(ToolsHTML.isEmptyOrNull(forma.getDebugEmail(), "0")).append("'");

		// Luis Cisneros 24/03-07
		edit.append(",printOwnerAdmin='").append(forma.getPrintOwnerAdmin()).append("'");

		// JRivero
		edit.append(",completeFTP='").append(forma.getCompleteFTP()).append("'");

		// JRivero
		edit.append(",msgWFCompletados='").append(forma.getMsgWFCompletados()).append("'");

		// YSA
		edit.append(",viewReaders='").append(forma.getViewReaders()).append("'");

		// YSA
		edit.append(",viewCreator='").append(forma.getViewCreator()).append("'");

		edit.append(",viewCollaborator='").append(forma.getViewCollaborator()).append("'");

		edit.append(",viewpdftool='").append(forma.getViewpdftool()).append("'");

		edit.append(",oooExeFolder='").append(forma.getOooExeFolder()).append("'");

		// YSA
		edit.append(",reasigneUserFTP='").append(forma.getReasigneUserFTP()).append("'");

		// YSA
		edit.append(",downloadLastVer='").append(forma.getDownloadLastVer()).append("'");

		edit.append(",validNavigator='").append(forma.getValidNavigator()).append("'");

		edit.append(",fileNotOpenSave='").append(forma.getFileNotOpenSave()).append("'");

		edit.append(",documentReject='").append(forma.getDocumentReject()).append("'");

		// ydavila Ticket 001-00-003023
		edit.append(",autenticarflujo='").append(forma.getautenticarflujo()).append("'");
		edit.append(",respsolcambio='").append(forma.getrespsolcambio()).append("'");
		edit.append(",respsolelimin='").append(forma.getrespsolelimin()).append("'");
		edit.append(",respsolimpres='").append(forma.getrespsolimpres()).append("'");
		edit.append(",respsolsacop='").append(forma.getRespsolsacop()).append("'");

		// TODO: CAMBIO PARA DOBLE VERSION
		edit.append(",fileDoubleVersion='").append(forma.getFileDoubleVersion()).append("'");

		// JR --- extension no editables
		edit.append(",fileUploadVersion='").append(forma.getFileUploadVersion()).append("'");

		// JR --- extension visor nativo
		edit.append(",fileNativeViewer='").append(forma.getFileNativeViewer()).append("'");

		// JR
		edit.append(",openoffice='").append(forma.getOpenoffice()).append("'");

		// YSA
		edit.append(",impNameCharge=").append(forma.getImpNameCharge());

		// YSA
		edit.append(",headImp1='").append(forma.getHeadImp1()).append("'");
		edit.append(",headImp2='").append(forma.getHeadImp2()).append("'");
		edit.append(",headImp3='").append(forma.getHeadImp3()).append("'");

		// YSA
		edit.append(",mailAccount='").append(forma.getMailAccount()).append("'");

		edit.append(",versionCom='").append(forma.getVersionCom()).append("'");
		edit.append(",logDoc='").append(forma.getLogDoc()).append("'");
		edit.append(",allowWF='").append(forma.getAllowWF()).append("'");
		edit.append(",docNumber='").append(forma.getDocNumberGenerator()).append("',");
		edit.append("lengthDocNumber='").append(forma.getDocNumberGenerator() == 1 ? forma.getLengthDocNumber().trim() : "");
		edit.append("',resetDocNumber='").append(forma.getDocNumberGenerator() == 1 ? forma.getResetDocNumber().trim() : "").append("'");
		edit.append(",expireDoc = '").append(forma.getExpireDoc()).append("'");
		edit.append(",monthExpireDoc='").append(forma.getExpireDoc() == 0 ? forma.getMonthsExpireDocs().trim() : "").append("'");
		edit.append(",unitTimeExpire='").append(forma.getUnitTimeExpire()).append("'");

		edit.append(",deadDoc = '").append(forma.getDeadDoc()).append("'");
		edit.append(",monthsDeadDocs='").append(forma.getDeadDoc() == 0 ? forma.getMonthsDeadDocs().trim() : "").append("'");
		edit.append(",unitTimeDead='").append(forma.getUnitTimeDead()).append("'");
		// SIMON JUNIO 16 DEL 2005 INICIO
		edit.append(",expireDrafts = '").append(forma.getExpireDrafts()).append("'");
		edit.append(",monthsExpireDrafts='").append(forma.getExpireDrafts() == 0 ? forma.getMonthsExpireDrafts().trim() : "").append("'");
		edit.append(",unitTimeExpireDrafts='").append(forma.getUnitTimeExpireDrafts()).append("'");
		// SIMON 16 DEL 2005 FIN

		edit.append(",daysEndPass='").append(forma.getExpirePass() == 0 ? forma.getDaysEndPass().trim() : "").append("'");
		edit.append(",endsession=").append(forma.getEndSession());
		edit.append(",endSessionEdit=").append(forma.getEndSessionEdit());
		edit.append(",msgWFExpires='").append(forma.getMsgWFExpires() != null ? StringUtil.cleanStarString(forma.getMsgWFExpires()) : "").append("'");
		edit.append(",msgWFCancelados='").append(forma.getMsgWFCancelados() != null ? StringUtil.cleanStarString(forma.getMsgWFCancelados()) : "").append("'");
		edit.append(",msgWFAprobados='").append(forma.getMsgWFAprobados() != null ? StringUtil.cleanStarString(forma.getMsgWFAprobados()) : "").append("'");
		edit.append(",msgWFRevision='").append(forma.getMsgWFRevision() != null ? StringUtil.cleanStarString(forma.getMsgWFRevision()) : "").append("'");
		edit.append(",msgWFBorrador='").append(forma.getMsgWFBorrador() != null ? StringUtil.cleanStarString(forma.getMsgWFBorrador()) : "").append("'");
		edit.append(",msgDocExpirado='").append(forma.getMsgDocExpirado() != null ? StringUtil.cleanStarString(forma.getMsgDocExpirado()) : "").append("'");
		edit.append(",msgSacopAccionesVencidas='")
				.append(forma.getMsgSacopAccionesVencidas() != null ? StringUtil.cleanStarString(forma.getMsgSacopAccionesVencidas()) : "").append("'");
		edit.append(",msgSacopAccionesPorVencer='")
				.append(forma.getMsgSacopAccionesPorVencer() != null ? StringUtil.cleanStarString(forma.getMsgSacopAccionesPorVencer()) : "").append("'");
		edit.append(",NumByLocation = ").append(forma.getNumDocLoc());
		edit.append(",cargosacop = '").append(forma.getCargosacop()).append("'");
		// Tipo de Ordenamiento de la Estructura
		edit.append(",typeOrden = '").append(forma.getTypeOrder()).append("'");
		// Permisologia para el cambios de nombre y propietario de un doc.
		edit.append(",changeProperties = '").append(forma.getChangeProperties()).append("'");
		// Idioma Predeterminado de la Aplicaci�n
		edit.append(",idioma = '").append(forma.getIdioma().trim()).append("'");
		edit.append(",printVersion = '").append(forma.getPrintVersion()).append("'");
		edit.append(",printApprovedDate = '").append(forma.getPrintApprovedDate()).append("'");
		edit.append(",disabledCache = '").append(forma.getDisabledCache()).append("'");
		edit.append(",printNumber = '").append(forma.getPrintNumber()).append("'");
		edit.append(",folderCmp = '").append(forma.getFolderCmp()).append("'");
		edit.append(",attachedFolder0 = '").append(forma.getAttachedFolder0()).append("'");
		edit.append(",attachedFolder1 = '").append(forma.getAttachedFolder1()).append("'");
		edit.append(",attachedField = '").append(forma.getAttachedField()).append("'");
		edit.append(",editorOnDemand = '").append(forma.getEditorOnDemand()).append("'");
		edit.append(",copyContents = '").append(forma.getCopyContents()).append("'");
		edit.append(",editOriginatorWF = '").append(forma.getEditOriginatorWF()).append("'");
		edit.append(",domain = '").append(forma.getDomain()).append("'");
		edit.append(",ldapurl = '").append(forma.getLdapUrl()).append("'");
		edit.append(",repository = '").append(forma.getRepository()).append("'");
		edit.append(",numberSacop = '").append(forma.getNumberSacop()).append("'");
		edit.append(",publicEraser = '").append(forma.getPublicEraser()).append("'");
		edit.append(",changeAditionalField = '").append(forma.getChangeAditionalField()).append("'");
		edit.append(",vigencia_flujos_cancelados=").append(forma.getVigenciaFlujosCanceladosPrincipal());
		edit.append(",piePaginaWaterMark='").append(forma.getPiePaginaWaterMark()).append("'");
		edit.append(",diasAlertaAccionesSacopPorVencer=").append(forma.getDiasAlertaAccionesSacopPorVencer()).append(" ");
		edit.append(",numberDaysInactivitySACOP=").append(forma.getNumberDaysInactivitySACOP()).append(" ");
		edit.append(",numberDaysWithoutActionSACOP=").append(forma.getNumberDaysWithoutActionSACOP()).append(" ");
		edit.append(",listUserAddressee='").append(forma.getListUserAddressee()).append("' ");
		edit.append(",validitySacop=").append(forma.getValiditySacop()).append(" ");
		edit.append(",validitySacopType=").append(forma.getValiditySacopType()).append(" ");
		edit.append(",validitySacopMonth=").append(forma.getValiditySacopMonth()).append(" ");
		edit.append(",idNormAudit=").append(forma.getIdNormAudit()).append(" ");
	
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.executeUpdate();
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			if (forma.getDocNumberGenerator() == 1) {
				IDDBFactorySql.updateValue("numDocs", forma.getResetDocNumber().trim(), con, st);
			}
			if (con != null) {
				con.close();
			}
			resp = true;

			Constants.PRINTER_PDF = "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getOpenoffice()));

			// colocamos el valor a notifyEmail
			Constants.notifyEmail = null;
			ToolsHTML.isNotifyEmail();
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
			resp = false;
		}
		
		
		// cargamos la variable estatica
		try {
			loadParametros();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}

	public static int getTypeNumberParam() throws Exception {
		try {
			return Integer.parseInt(PARAMETROS.getLengthDocNumber());
		} catch(Exception e) {
			return 0;
		}
	}

	public static String getLengthNumberDocuments() throws Exception {
		return getLengthNumberDocuments(null);
	}

	public static String getLengthNumberDocuments(Connection con) throws Exception {
		try {
			if(PARAMETROS.getDocNumberGenerator()==1) {
				return PARAMETROS.getLengthDocNumber();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "4";
	}

	/*
	 * Busca el vencimiento segun el tipo de documento o en configuracion general
	 */

	public static String[] getFieldsExpired(String fields[], String typeDoc) throws Exception {
		return getFieldsExpired(null, fields, typeDoc);
	}

	public static String[] getFieldsExpired(Connection con, String fields[], String typeDoc) throws Exception {
		if (fields != null && fields.length >= 0) {
			String[] result = new String[fields.length];

			ArrayList<Object> parametros = new ArrayList<Object>();
			StringBuffer query = new StringBuffer();
			String fieldDrafs = "monthsExpireDrafts, unitTimeExpireDrafts, expireDrafts,";
			String fieldDoc = "monthExpireDoc, unitTimeExpire, expireDoc,";
			String fieldDead = "monthsDeadDocs, unitTimeDead, deadDoc,";

			parametros.add(new Integer(typeDoc));
			query.append("SELECT 1 As orden, monthsExpireDrafts, unitTimeExpireDrafts, expireDrafts,monthExpireDoc, unitTimeExpire, expireDoc, ");
			query.append("monthsDeadDocs, unitTimeDead, deadDoc ");
			query.append("FROM typedocuments ");
			query.append("WHERE (expireDrafts IS NOT NULL AND expireDrafts=CAST(0 AS BIT) OR expireDoc IS NOT NULL AND expireDoc=CAST(0 AS BIT) OR deadDoc IS NOT NULL AND deadDoc=CAST(0 AS BIT)) ");
			query.append("AND idTypeDoc=? ");
			query.append(" ");
			query.append("UNION ");
			query.append("SELECT 0 As orden, monthsExpireDrafts, unitTimeExpireDrafts, expireDrafts,monthExpireDoc, unitTimeExpire, expireDoc, ");
			query.append("monthsDeadDocs, unitTimeDead, deadDoc ");
			query.append("FROM parameters ");
			query.append("ORDER BY orden ");

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, con, Thread.currentThread().getStackTrace()[1].getMethodName());

			/* vamos a colocar los valores */
			if (crs.size() != 0) {
				for (int i = 0; crs.next(); i++) {
					for (int row = 0; row < fields.length; row++) {
						if (i == 0) {
							result[row] = crs.getString(fields[row]);
						} else {
							if (fieldDrafs.indexOf(fields[row].concat(",")) != -1) {
								if (crs.getString("expireDrafts") != null
										&& (crs.getString("expireDrafts").equals("0") || crs.getString("expireDrafts").equals("false"))) {
									result[row] = crs.getString(fields[row]);
								}
							} else if (fieldDoc.indexOf(fields[row].concat(",")) != -1) {
								if (crs.getString("expireDoc") != null
										&& (crs.getString("expireDoc").equals("0") || crs.getString("expireDoc").equals("false"))) {
									result[row] = crs.getString(fields[row]);
								}
							} else if (fieldDead.indexOf(fields[row].concat(",")) != -1) {
								if (crs.getString("deadDoc") != null && (crs.getString("deadDoc").equals("0") || crs.getString("deadDoc").equals("false"))) {
									result[row] = crs.getString(fields[row]);
								}
							}
						}
					}
				}
			} else {
				return null;
			}

			return result;
		}
		return null;
	}

	// ydavila Ticket 001-00-003023 - tabla PARAMETERS - RESPONSABLE SOLICITUDES DE CAMBIO
	static String getUpdateidrespsolcambioUser_Admin(String oldOwner, String newrespsolcambio) {
		StringBuffer sql = new StringBuffer("UPDATE parameters set respsolcambio = '");
		sql.append(newrespsolcambio).append("' WHERE respsolcambio='").append(oldOwner).append("' ");
		log.debug("[getUpdateidrespsolcambio_Admin]" + sql.toString());
		return sql.toString();
	}

	// ydavila Ticket 001-00-003023 - tabla PARAMETERS - RESPONSABLE ELIMINACI�N
	static String getUpdateidrespsoleliminUser_Admin(String oldOwner, String newrespsolelimin) {
		StringBuffer sql = new StringBuffer("UPDATE parameters set respsolelimin = '");
		sql.append(newrespsolelimin).append("' WHERE respsolelimin='").append(oldOwner).append("' ");
		log.debug("[getUpdateidrespsolelimin_Admin]" + sql.toString());
		return sql.toString();
	}

	// ydavila Ticket 001-00-003023 - tabla PARAMETERS - RESPONSABLE IMPRESI�N
	static String getUpdateidrespsolimpresUser_Admin(String oldOwner, String newrespsolimpres) {
		StringBuffer sql = new StringBuffer("UPDATE parameters set respsolimpres = '");
		sql.append(newrespsolimpres).append("' WHERE respsolimpres='").append(oldOwner).append("' ");
		log.debug("[getUpdateidrespsolimpres_Admin]" + sql.toString());
		return sql.toString();
	}

	// public static synchronized String g

	// ydavila Ticket 001-00-003023
	public static Collection getRespCambElimDocAdm(String subtypeWF) throws Exception {
		String query = null;
		StringBuilder sql = new StringBuilder(100);
		String resultstr = null;
		Vector result = new Vector();
		Search bean = null;
		int subtypeWFn = Integer.parseInt(subtypeWF);
		Properties prop = null;
		Properties prop1 = null;

		if (subtypeWFn == 3) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT top 1 idPerson,nameUser, c.cargo, (apellidos + ' ' +nombres) AS nombre");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolcambio()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' ");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT idPerson,nameUser,c.cargo, (apellidos || ' ' || nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolcambio()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("SELECT idPerson,nameUser,c.cargo, CONCAT(apellidos , ' ' , nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolcambio()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			}
		} else if (subtypeWFn == 4) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT top 1 idPerson,nameUser, c.cargo, (apellidos + ' ' +nombres) AS nombre");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolelimin()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1'");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT idPerson,nameUser,c.cargo, (apellidos || ' ' || nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolelimin()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("SELECT idPerson,nameUser,c.cargo, CONCAT(apellidos , ' ' , nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolelimin()).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			}
		}
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				prop = (Properties) datos.elementAt(row);
				switch (subtypeWFn) {
				case 3:
					//bean = new Search(prop.getProperty("respsolcambio"), prop.getProperty("nameUser"));
					bean = new Search(prop.getProperty("nombre"), resultstr = prop.getProperty("nombre"));
					break;
				case 4:
					//bean = new Search(prop.getProperty("respsolelimin"), prop.getProperty("nameUser"));
					bean = new Search(prop.getProperty("nombre"), resultstr = prop.getProperty("nombre"));
					break;
				}
				bean.setId(prop.getProperty("nameUser"));
				bean.setAditionalInfo(prop.getProperty("cargo"));
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	// ydavila Ticket 001-00-003023
	public static Collection getRespCambElimDocAdm1(String subtypeWF) throws Exception {
		String query = null;
		StringBuilder sql = new StringBuilder();
		String resultstr = null;
		Vector result = new Vector();
		Search bean = null;
		int subtypeWFn = Integer.parseInt(subtypeWF);
		Properties prop = null;
		Properties prop1 = null;
		if (subtypeWFn == 3) {
			// ydavila mostrar nombre completo en vez de user
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT idPerson,nameUser,cargo, (apellidos + ' ' +nombres) AS nombre  FROM person ");
				sql.append("WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolcambio()).append("' ");
				sql.append("AND accountactive = '1'");
				log.debug("sql HandleParameters.getRespCambElimDocAdm1 = " + sql);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT idPerson,nameUser, (apellidos || ' ' || nombres) AS nombre  FROM person ");
				sql.append("WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolcambio()).append("' ");
				sql.append("AND accountactive = '1'");
				log.debug("sql HandleParameters.getRespCambElimDocAdm1 = " + sql);
				break;
			}
		} else if (subtypeWFn == 4) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT idPerson,nameUser,cargo, (apellidos + ' ' +nombres) AS nombre  FROM person ");
				sql.append("WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolelimin()).append("' ");
				sql.append("AND accountactive = '1'");
				log.debug("sql HandleParameters.getRespCambElimDocAdm1 = " + sql);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT idPerson,nameUser, (apellidos || ' ' || nombres) AS nombre  FROM person ");
				sql.append("WHERE nameuser='").append(HandlerParameters.PARAMETROS.getRespsolelimin()).append("' ");
				sql.append("AND accountactive = '1'");
				log.debug("sql HandleParameters.getRespCambElimDocAdm1 = " + sql);
				break;
			}
		}
		try {
			Vector datos1 = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int rowx = 0; rowx < datos1.size(); rowx++) {
				prop1 = (Properties) datos1.elementAt(rowx);
				switch (subtypeWFn) {
				case 3:
					bean = new Search(prop1.getProperty("nombre"), resultstr = prop1.getProperty("nombre"));
					break;
				case 4:
					bean = new Search(prop1.getProperty("nombre"), resultstr = prop1.getProperty("nombre"));
					break;
				}
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// public static synchronized String g

	// ydavila Ticket 001-00-003366
	public static String getRespImpresDocAdm() throws Exception {
		return HandlerParameters.PARAMETROS.getRespsolimpres();
	}

	public static String getRespImpresDocAdm1() throws ApplicationExceptionChecked, Exception {
		return PARAMETROS.getRespsolimpres()==null || PARAMETROS.getRespsolimpres().trim().equals("")?null:PARAMETROS.getRespsolimpres();
	}

	private static Properties JDBCUtil(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getRespImpresDocAdm2() throws Exception {
		String respsolimpres = null;
		StringBuffer sql = new StringBuffer("select idperson from person where nameuser='").append(PARAMETROS.getRespsolimpres()).append("'");
		log.debug("sql getRespImpresDocAdm = " + sql);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("idperson"))) {
			respsolimpres = prop.getProperty("idperson");
		}
		return respsolimpres;
	}

	// ydavila Ticket 001-00-003366
	public static String getAutenticarFlujo() throws Exception {
		return String.valueOf(PARAMETROS.getAutenticarflujo());
	}

	// ydavila Ticket 001-00-003366
	public static String getLDAPurl() throws Exception {
		return PARAMETROS.getLdapUrl();
	}

	// ydavila Ticket 001-00-003366
	public static String getRespSolCambioAdm() throws Exception {
		String respsolcambio = null;
		log.debug("sql getrespsolcambio = ");
		if (!ToolsHTML.isEmptyOrNull(PARAMETROS.getRespsolcambio())) {
			respsolcambio = HandlerDBUser.getCargoArea(PARAMETROS.getRespsolcambio());
		}
		return respsolcambio;
	}

	// ydavila Ticket 001-00-003366
	public static String getRespSolCambioAdm1() throws Exception {
		return PARAMETROS.getRespsolcambio();
	}

	// ydavila Ticket 001-00-003366
	public static String getRespSolEliminAdm() throws Exception {
		String respsolelimin = null;
		if (!ToolsHTML.isEmptyOrNull(PARAMETROS.getRespsolelimin())) {
			respsolelimin = (HandlerDBUser.getCargoArea(PARAMETROS.getRespsolelimin()));
		}
		return respsolelimin;
	}

	// ydavila Ticket 001-00-003366
	public static String getRespSolEliminAdm1() throws Exception {
		return PARAMETROS.getRespsolelimin();
	}

	public static boolean isExtensionNativeViewer(Connection con, String nameFile) {
		Connection conLocal = null;
		String extensiones;
		String fileDoubleVersion;
		try {
			/*
			if(ModuloBean.IS_EDICION_LITE) {
				return true;
			}
			*/
			conLocal = (con == null) ? JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()) : con;

			nameFile = nameFile.toLowerCase();
			
			extensiones = PARAMETROS.getFileNativeViewer().toLowerCase();
			fileDoubleVersion = PARAMETROS.getFileDoubleVersion().toLowerCase();
			
			if (extensiones == null) {
				extensiones = ".jpg,.png,.jpeg,.bmp,.gif";
			} else {
				extensiones = extensiones.concat(extensiones.length()>0?",":"").concat(".jpg,.png,.jpeg,.bmp,.gif");
			}

			if (extensiones != null) {
				String[] ext = extensiones.split(",");
				String[] dbl = fileDoubleVersion.split(",");
				
				if (nameFile != null) {
					// si la extension esta en doble version y pdf esta en visor nativo
					// sera candidato automatico para visor nativo
					for (int i = 0; i < dbl.length; i++) {
						if (nameFile.toLowerCase().endsWith(dbl[i])) {
							if(extensiones.indexOf("pdf")!=-1) {
								return true; 
							}
						}
					}

					for (int i = 0; i < ext.length; i++) {
						if (nameFile.toLowerCase().endsWith(ext[i])) {
							return true; // si es extension a ser vista en el navegador
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con == null) {
				setFinally(conLocal);
			}
		}
		// si no se encuentra devolvemos falso
		return false;
	}
	
	public static CachedRowSet getParameters() throws Exception {
		ArrayList<Object> parametros = new ArrayList<Object>();
		StringBuffer query = new StringBuffer("SELECT * FROM parameters");
		
		return JDBCUtil.executeQuery(query, parametros, null, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

}
