package com.desige.webDocuments.parameters.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: BaseParametersForm.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez. (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 19/05/2004 (NC) Creation </li>
 *      <li> 14/05/2005 (SR) Se agreg� el campo endSession y los correspondientes m�todos get & set </li>
 *      <li> 14/05/2005 (NC) Se agreg� el campo msgWFExpires y los correspondientes m�todos get & set </li>
 *      <li> 22/05/2005 (NC) Se agreg� el campo typePrefix y los correspondientes m�todos get & set </li> 
 *      <li> 26/05/2005 (SR) Se agregaron los beans cancelados,aprobados,revision </li>
 *      <li> 29/05/2005 (NC) Se elimino el par�metro typePrefix </li>
 *      <li> 16/06/2005 (SR) Se creo el par�metro expireDrafts,unitTimeExpireDrafts,monthsExpireDrafts,msgWFBorrador</li>
 *      <li> 15/06/2005 (NC) Se agreg� el campo numDocLoc </li> 
 *      <li> 22/07/2005 (SR) Se agrego el bean msgDocExpirado. </li>
 *      <li> 27/07/2005 (SR) Se agrego el bean TypeNumByLocation. </li>
 *      <li> 15/06/2007 (YS) Se agrego el atributo viewReaders y los correspondientes m�todos get & set </li>
 *      <li> 07/07/2007 (YS) Se agrego el atributo reasigneUserFTP y los correspondientes m�todos get & set </li>
 *      <li> 20/07/2007 (YS) Se agrego el atributo downloadLastVer y los correspondientes m�todos get & set </li>
 *      <li> 01/08/2007 (YS) Se agrego el atributo impNameCharge y los correspondientes m�todos get & set </li>
 *      <li> 03/08/2007 (YS) Se agregaron atributos headImp1, headImp2, headImp3 y los correspondientes m�todos get & set </li>
 *      <li> 15/10/2007 (YS) Se agrego el atributo mailAccount y los correspondientes m�todos get & set </li>
 *      <li> 23/11/2007 (YS) Se agrego el atributo endSessionEdit y los correspondientes m�todos get & set </li>
 *      <li> 01/08/2008 (YS) Se agrego el atributo viewCreator y los correspondientes m�todos get & set </li>
 </ul>
 */
public class BaseParametersForm extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7410327464873265112L;
	
	private String lengthPass;
    private String allowDuplicatePass;
    private byte expirePass;

    private byte validateEmail; //Luis Cisneros 28/02/2007

    private String domainEmail; 
    private String portEmail;
    private String authProtocolEmail;
    private String passwordEmail;
    private String debugEmail;
    
    private byte printOwnerAdmin;  //Luis Cisneros 24/03/2007  Permite al administrador y al dueno imprimir a discresion.

    private byte completeFTP;  
    
    private String msgWFCompletados;

    private byte notifyEmail;
    private String smtpMail;
    private String postfixMail;

    private byte qualitySystem;
    private int docNumberGenerator;
    private String lengthDocNumber;
    private String resetDocNumber;
    private byte versionCom;
    private byte logDoc;
    private byte allowWF;
    private String daysEndPass;
    private byte expireDoc;
    private byte expireDrafts;
    private byte deadDoc;
    private String monthsExpireDrafts;
    private String unitTimeExpireDrafts;

    private String monthsExpireDocs;
    private String unitTimeExpire;
    private String monthsDeadDocs;
    private String unitTimeDead;
    private String msgWFExpires;
    private String msgWFCancelados;
    private String msgWFBorrador;
    private String msgWFAprobados;
    private String msgWFRevision;
    private String msgDocExpirado;
    private boolean typeNumByLocation;
    private int endSession;
    private byte numDocLoc;
    private byte cargosacop;
    private byte typeOrder;
    private byte changeProperties;
    private String idioma;
    
    private byte fileDeadFile;
    private String fileDeadTime;
    private String fileDeadUnit;
    private byte fileDeadVerbose;
    
    private byte viewReaders;  //YSA 15/06/2007 Permite ver o no los revisores de un documento aprobado (en caso que haya pasado por un flujo de revision)
    private byte reasigneUserFTP;  //YSA 07/07/2007 Permite reiniciar un FTP rechazado en una actividad o firmante particular
    private byte downloadLastVer; //YSA 20/07/2007 Permite descarga de ultima version de un documento
    private int impNameCharge; //YSA 01/08/2007 Permite seleccionar si se imprime nombre, cargo o ambos
    private String headImp1; //YSA 03/08/2007 Valor 1 del encabezado del formato de impresion
    private String headImp2; //YSA 03/08/2007 Valor 2 del encabezado del formato de impresion
    private String headImp3; //YSA 03/08/2007 Valor 3 del encabezado del formato de impresion
    private String mailAccount; //YSA 15/10/2007 Email del remitente, para notificaciones del sistema
    private byte viewCreator;  //YSA 01/08/2008 Permite ver o no el elaborador de un documento
    private byte viewCollaborator;  //Permite ver los editores/colaboradores de un documento    
    private byte viewpdftool;  // que herramienta se usara para convertir los archivos en pdf    
    private String oooExeFolder; // es la ruta de del ejecutable de openoffice office.exe o soffice
    
    private byte fileNotOpenSave;
    private String fileDoubleVersion;    
    private String fileUploadVersion;    
    private String fileNativeViewer;    
    private byte openoffice;
    private byte printVersion;
    private byte printNumber;
    private byte printApprovedDate;
    private String piePaginaWaterMark;
    private String folderCmp;
    private String attachedFolder0;
    private String attachedFolder1;
    private String attachedField;
    private byte editorOnDemand;
    private byte copyContents;
    private byte editOriginatorWF;
    private int endSessionEdit; //YSA 23/11/2007 Tiempo de expiracion para edicion
    private String domain;
    private String ldapUrl;
    private String repository;
    private String numberSacop;
    
    private byte documentReject;

    //ydavila Ticket 001-00-003023
    private byte autenticarflujo;
    private String respsolcambio;
    private String respsolelimin;
    private String respsolimpres;
    private String idUser;
    private String respsolsacop;
    
    private byte disabledCache;
    private byte publicEraser;
    private byte changeAditionalField;
    
    private int idNodeService;
    
    private int vigenciaFlujosCanceladosPrincipal;
    
    /**
     * Texto para los mensajes asociados al vencimiento de acciones de las SACOPS
     */
    private String msgSacopAccionesPorVencer;
    
    /**
     * Texto para los mensajes asociados al vencimiento de acciones de las SACOPS
     */
    private String msgSacopAccionesVencidas;
    
    /**
     * Rango en dias en los que seran enviadas alertas de acciones proximas a vencer en el modulo de SACOPS
     * 
     */
    private int diasAlertaAccionesSacopPorVencer;
    
    private int numberDaysInactivitySACOP;
    private int numberDaysWithoutActionSACOP;
    
    private String listUserAddressee;
    
    private int validitySacop;
    private int validitySacopType;
    private int validitySacopMonth;
    private int idNormAudit;
    
    
    private byte validNavigator;

    
    public byte getCargosacop() {
        return cargosacop;
    }

    public void setCargosacop(byte cargosacop) {
        this.cargosacop = cargosacop;
    }
    public boolean getTypeNumByLocation() {
            return typeNumByLocation;
    }

        public void setTypeNumByLocation(boolean typeNumByLocation) {
            this.typeNumByLocation = typeNumByLocation;
    }



    public int getEndSession() {
        return endSession;
    }

    public void setEndSession(int endSession) {
        this.endSession = endSession;
    }

    public String getLengthPass() {
        return lengthPass;
    }

    public void setLengthPass(String lengthPass) {
        this.lengthPass = lengthPass;
    }

    public String getAllowDuplicatePass() {
        return allowDuplicatePass;
    }

    public void setAllowDuplicatePass(String allowDuplicatePass) {
        this.allowDuplicatePass = allowDuplicatePass;
    }

    public byte getExpirePass() {
        return expirePass;
    }

    public void setExpirePass(byte expirePass) {
        this.expirePass = expirePass;
    }

    public byte getNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(byte notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public byte getValidateEmail() {
        return validateEmail;
    }

    public void setValidateEmail(byte validateEmail) {
        this.validateEmail = validateEmail;
    }
    
    public String getDomainEmail() {
		return domainEmail;
	}

	public void setDomainEmail(String domainEmail) {
		this.domainEmail = domainEmail;
	}
	
	public String getPortEmail() {
		return portEmail;
	}
	
	public void setPortEmail(String portEmail) {
		this.portEmail = portEmail;
	}
	
	public String getAuthProtocolEmail() {
		return authProtocolEmail;
	}
	
	public void setAuthProtocolEmail(String authProtocolEmail) {
		this.authProtocolEmail = authProtocolEmail;
	}
	
	public String getPasswordEmail() {
		return passwordEmail;
	}
	
	public void setPasswordEmail(String passwordEmail) {
		this.passwordEmail = passwordEmail;
	}
	
	public String getDebugEmail() {
		return debugEmail;
	}
	
	public void setDebugEmail(String debugEmail) {
		this.debugEmail = debugEmail;
	}
	
	public String getSmtpMail() {
        return smtpMail;
    }

    public void setSmtpMail(String smtpMail) {
        this.smtpMail = smtpMail;
    }

    public byte getQualitySystem() {
        return qualitySystem;
    }

    public void setQualitySystem(byte qualitySystem) {
        this.qualitySystem = qualitySystem;
    }

    public byte getVersionCom() {
        return versionCom;
    }

    public void setVersionCom(byte versionCom) {
        this.versionCom = versionCom;
    }

    public byte getLogDoc() {
        return logDoc;
    }

    public void setLogDoc(byte logDoc) {
        this.logDoc = logDoc;
    }

    public byte getAllowWF() {
        return allowWF;
    }

    public void setAllowWF(byte allowWF) {
        this.allowWF = allowWF;
    }

//    public byte getAutoDelWF() {
//        return autoDelWF;
//    }

//    public void setAutoDelWF(byte autoDelWF) {
//        this.autoDelWF = autoDelWF;
//    }

    public int getDocNumberGenerator() {
        return docNumberGenerator;
    }

    public void setDocNumberGenerator(int docNumberGenerator) {
        this.docNumberGenerator = docNumberGenerator;
    }

    public String getLengthDocNumber() {
        return lengthDocNumber;
    }

    public void setLengthDocNumber(String lengthDocNumber) {
        this.lengthDocNumber = lengthDocNumber;
    }

    public String getResetDocNumber() {
        return resetDocNumber;
    }

    public void setResetDocNumber(String resetDocNumber) {
        this.resetDocNumber = resetDocNumber;
    }

    public String getDaysEndPass() {
        return daysEndPass;
    }

    public void setDaysEndPass(String daysEndPass) {
        this.daysEndPass = daysEndPass;
    }

    public byte getExpireDoc() {
        return expireDoc;
    }

    public void setExpireDoc(byte expireDoc) {
        this.expireDoc = expireDoc;
    }

   //simon 16 de junio 2005 inicio
     public byte getExpireDrafts() {
        return expireDrafts;
    }

    public void setExpireDrafts(byte expireDrafts) {
        this.expireDrafts = expireDrafts;
    }


     public String getMonthsExpireDrafts() {
        return monthsExpireDrafts;
    }

    public void setMonthsExpireDrafts(String monthsExpireDrafts) {
        this.monthsExpireDrafts = monthsExpireDrafts;
    }


     public String getUnitTimeExpireDrafts() {
        return unitTimeExpireDrafts;
    }

    public void setUnitTimeExpireDrafts(String unitTimeExpireDrafts) {
        this.unitTimeExpireDrafts = unitTimeExpireDrafts;
    }

    //simon 16 de junio 2005 fin

    public String getMonthsExpireDocs() {
        return monthsExpireDocs;
    }

    public void setMonthsExpireDocs(String monthsExpireDocs) {
        this.monthsExpireDocs = monthsExpireDocs;
    }


    public String getUnitTimeExpire() {
        return unitTimeExpire;
    }

    public void setUnitTimeExpire(String unitTimeExpire) {
        this.unitTimeExpire = unitTimeExpire;
    }

    public String getMsgWFExpires() {
        return msgWFExpires;
    }

    public void setMsgWFExpires(String msgWFExpires) {
        this.msgWFExpires = msgWFExpires;
    }

      public String getMsgWFCancelados() {
         return msgWFCancelados;
     }
     public void setMsgWFCancelados(String msgWFCancelados) {
         this.msgWFCancelados = msgWFCancelados;
     }


     public String getMsgWFAprobados() {
         return msgWFAprobados;
     }
     public void setMsgWFAprobados(String msgWFAprobados) {
         this.msgWFAprobados = msgWFAprobados;
     }
      //SIMON INICIO 16 DE JUNIO
    public String getMsgWFBorrador() {
         return msgWFBorrador;
     }
     public void setMsgWFBorrador(String msgWFBorrador) {
         this.msgWFBorrador = msgWFBorrador;
     }
    //SIMON FIN 16 DE JUNIO
    public String getMsgDocExpirado() {
        return msgDocExpirado;
    }
    public void setMsgDocExpirado(String msgDocExpirado) {
        this.msgDocExpirado = msgDocExpirado;
    }



     public String getMsgWFRevision() {
         return msgWFRevision;
     }
     public void setMsgWFRevision(String msgWFRevision) {
         this.msgWFRevision = msgWFRevision;
     }
    public byte getNumDocLoc() {
        return numDocLoc;
    }

    public void setNumDocLoc(byte numDocLoc) {
        this.numDocLoc = numDocLoc;
    }

    public byte getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(byte typeOrder) {
        this.typeOrder = typeOrder;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public byte getPrintOwnerAdmin() {
        return printOwnerAdmin;
    }

    public void setPrintOwnerAdmin(byte printOwnerAdmin) {
        this.printOwnerAdmin = printOwnerAdmin;
    }

	public byte getCompleteFTP() {
		return completeFTP;
	}

	public void setCompleteFTP(byte completeFTP) {
		this.completeFTP = completeFTP;
	}

	public String getMsgWFCompletados() {
		return msgWFCompletados;
	}

	public void setMsgWFCompletados(String msgWFCompletados) {
		this.msgWFCompletados = msgWFCompletados;
	}

	public byte getViewReaders() {
		return viewReaders;
	}

	public void setViewReaders(byte viewReaders) {
		this.viewReaders = viewReaders;
	}

	public byte getViewCreator() {
		return viewCreator;
	}

	public void setViewCreator(byte viewCreator) {
		this.viewCreator = viewCreator;
	}
	public byte getReasigneUserFTP() {
		return reasigneUserFTP;
	}

	public void setReasigneUserFTP(byte reasigneUserFTP) {
		this.reasigneUserFTP = reasigneUserFTP;
	}

	public byte getDownloadLastVer() {
		return downloadLastVer;
	}

	public void setDownloadLastVer(byte downloadLastVer) {
		this.downloadLastVer = downloadLastVer;
	}

	public int getImpNameCharge() {
		return impNameCharge;
	}

	public void setImpNameCharge(int impNameCharge) {
		this.impNameCharge = impNameCharge;
	}

	public String getHeadImp1() {
		return headImp1;
	}

	public void setHeadImp1(String headImp1) {
		this.headImp1 = headImp1;
	}

	public String getHeadImp2() {
		return headImp2;
	}

	public void setHeadImp2(String headImp2) {
		this.headImp2 = headImp2;
	}

	public String getHeadImp3() {
		return headImp3;
	}

	public void setHeadImp3(String headImp3) {
		this.headImp3 = headImp3;
	}

	public byte getFileNotOpenSave() {
		return fileNotOpenSave;
	}

	public void setFileNotOpenSave(byte fileNotOpenSave) {
		this.fileNotOpenSave = fileNotOpenSave;
	}
	
	
	public byte getDocumentReject() {
		return documentReject;
	}
	
	//ydavila Ticket 001-00-003023 
	public byte getautenticarflujo() {
		return autenticarflujo;
	}
	
	public void setautenticarflujo(byte autenticarflujo) {
		this.autenticarflujo = autenticarflujo;
	}
	
	public String  getrespsolcambio() {
		return respsolcambio;
	}
	
	public void  setrespsolcambio(String  respsolcambio) {
		this. respsolcambio =  respsolcambio;
	}
	
	public String  getrespsolelimin() {
		return respsolelimin;
	}
	
	public void  setrespsolelimin(String  respsolelimin) {
		this. respsolelimin =  respsolelimin;
	}
	
	public String  getrespsolimpres() {
		return respsolimpres;
	}
	
	public void  setrespsolimpres(String  respsolimpres) {
		this. respsolimpres =  respsolimpres;
	}
	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	//ydavila Ticket 001-00-003023 
	
	public void setDocumentReject(byte documentReject) {
		this.documentReject = documentReject;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public String getFileDoubleVersion() {
		return fileDoubleVersion;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public void setFileDoubleVersion(String fileDoubleVersion) {
		this.fileDoubleVersion = fileDoubleVersion;
	}

	public String getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
	}

	public byte getOpenoffice() {
		return openoffice;
	}

	public void setOpenoffice(byte openoffice) {
		this.openoffice = openoffice;
	}

    public int getEndSessionEdit() {
        return endSessionEdit;
    }

    public void setEndSessionEdit(int endSessionEdit) {
        this.endSessionEdit = endSessionEdit;
    }

	public byte getChangeProperties() {
		return changeProperties;
	}

	public void setChangeProperties(byte changeProperties) {
		this.changeProperties = changeProperties;
	}

	public byte getFileDeadFile() {
		return fileDeadFile;
	}

	public void setFileDeadFile(byte fileDeadFile) {
		this.fileDeadFile = fileDeadFile;
	}

	public String getFileDeadTime() {
		return fileDeadTime;
	}

	public void setFileDeadTime(String fileDeadTime) {
		this.fileDeadTime = fileDeadTime;
	}

	public String getFileDeadUnit() {
		return fileDeadUnit;
	}

	public void setFileDeadUnit(String fileDeadUnit) {
		this.fileDeadUnit = fileDeadUnit;
	}

	public byte getFileDeadVerbose() {
		return fileDeadVerbose;
	}

	public void setFileDeadVerbose(byte fileDeadVerbose) {
		this.fileDeadVerbose = fileDeadVerbose;
	}

	public String getFileUploadVersion() {
		return fileUploadVersion;
	}

	public void setFileUploadVersion(String fileUploadVersion) {
		this.fileUploadVersion = fileUploadVersion;
	}

	public String getFileNativeViewer() {
		return fileNativeViewer;
	}

	public void setFileNativeViewer(String fileNativeViewer) {
		this.fileNativeViewer = fileNativeViewer;
	}

	public byte getViewCollaborator() {
		return viewCollaborator;
	}

	public void setViewCollaborator(byte viewCollaborator) {
		this.viewCollaborator = viewCollaborator;
	}

	public String getOooExeFolder() {
		return oooExeFolder;
	}

	public void setOooExeFolder(String oooExeFolder) {
		this.oooExeFolder = oooExeFolder;
	}

	public byte getViewpdftool() {
		return viewpdftool;
	}

	public void setViewpdftool(byte viewpdftool) {
		this.viewpdftool = viewpdftool;
	}

	public byte getPrintVersion() {
		return printVersion;
	}

	public void setPrintVersion(byte printVersion) {
		this.printVersion = printVersion;
	}

	public byte getPrintNumber() {
		return printNumber;
	}

	public void setPrintNumber(byte printNumber) {
		this.printNumber = printNumber;
	}
	
	public byte getPrintApprovedDate() {
		return printApprovedDate;
	}
	
	public void setPrintApprovedDate(byte printApprovedDate) {
		this.printApprovedDate = printApprovedDate;
	}
	
	public String getPiePaginaWaterMark(){
		return piePaginaWaterMark;
	}
	
	public void setPiePaginaWaterMark(String piePaginaWaterMark){
		this.piePaginaWaterMark = piePaginaWaterMark;
	}
	
	public String getFolderCmp() {
		return folderCmp;
	}

	public void setFolderCmp(String folderCmp) {
		this.folderCmp = folderCmp;
	}

	public byte getEditorOnDemand() {
		return editorOnDemand;
	}

	public void setEditorOnDemand(byte editorOnDemand) {
		this.editorOnDemand = editorOnDemand;
	}

	public byte getEditOriginatorWF() {
		return editOriginatorWF;
	}

	public void setEditOriginatorWF(byte editOriginatorWF) {
		this.editOriginatorWF = editOriginatorWF;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public byte getCopyContents() {
		return copyContents;
	}

	public void setCopyContents(byte copyContents) {
		this.copyContents = copyContents;
	}

	public String getNumberSacop() {
		return numberSacop;
	}

	public void setNumberSacop(String numberSacop) {
		this.numberSacop = numberSacop;
	}

	public String getMonthsDeadDocs() {
		return monthsDeadDocs;
	}

	public void setMonthsDeadDocs(String monthsDeadDocs) {
		this.monthsDeadDocs = monthsDeadDocs;
	}

	public String getUnitTimeDead() {
		return unitTimeDead;
	}

	public void setUnitTimeDead(String unitTimeDead) {
		this.unitTimeDead = unitTimeDead;
	}

	public byte getDeadDoc() {
		return deadDoc;
	}

	public void setDeadDoc(byte deadDoc) {
		this.deadDoc = deadDoc;
	}

	/**
	 * Se desabilita esta opcion por codigo, ya que eliminara los pdf subidos al sistema como cache
	 * @return
	 */
	public byte getDisabledCache() {
		return (byte)0; //disabledCache; 
	}

	public void setDisabledCache(byte disabledCache) {
		this.disabledCache = disabledCache;
	}

	public String getPostfixMail() {
		return postfixMail;
	}

	public void setPostfixMail(String postfixMail) {
		this.postfixMail = postfixMail;
	}

	public String getAttachedFolder0() {
		return attachedFolder0;
	}

	public void setAttachedFolder0(String attachedFolder0) {
		this.attachedFolder0 = attachedFolder0;
	}

	public String getAttachedFolder1() {
		return attachedFolder1;
	}

	public void setAttachedFolder1(String attachedFolder1) {
		this.attachedFolder1 = attachedFolder1;
	}

	public String getAttachedField() {
		return attachedField;
	}

	public void setAttachedField(String attachedField) {
		this.attachedField = attachedField;
	}

	public byte getPublicEraser() {
		return publicEraser;
	}

	public void setPublicEraser(byte publicEraser) {
		this.publicEraser = publicEraser;
	}

	public byte getChangeAditionalField() {
		return changeAditionalField;
	}

	public void setChangeAditionalField(byte changeAditionalField) {
		this.changeAditionalField = changeAditionalField;
	}

	public int getVigenciaFlujosCanceladosPrincipal() {
		return vigenciaFlujosCanceladosPrincipal;
	}
	
	public void setVigenciaFlujosCanceladosPrincipal(
			int vigenciaFlujosCanceladosPrincipal) {
		this.vigenciaFlujosCanceladosPrincipal = vigenciaFlujosCanceladosPrincipal;
	}
	
	public String getMsgSacopAccionesPorVencer() {
		return msgSacopAccionesPorVencer;
	}
	
	public void setMsgSacopAccionesPorVencer(String msgSacopAccionesPorVencer) {
		this.msgSacopAccionesPorVencer = msgSacopAccionesPorVencer;
	}
	
	public String getMsgSacopAccionesVencidas() {
		return msgSacopAccionesVencidas;
	}
	
	public void setMsgSacopAccionesVencidas(String msgSacopAccionesVencidas) {
		this.msgSacopAccionesVencidas = msgSacopAccionesVencidas;
	}
	
	public int getDiasAlertaAccionesSacopPorVencer() {
		return diasAlertaAccionesSacopPorVencer;
	}
	
	public void setDiasAlertaAccionesSacopPorVencer(
			int diasAlertaAccionesSacopPorVencer) {
		this.diasAlertaAccionesSacopPorVencer = diasAlertaAccionesSacopPorVencer;
	}

	public byte getAutenticarflujo() {
		return autenticarflujo;
	}

	public void setAutenticarflujo(byte autenticarflujo) {
		this.autenticarflujo = autenticarflujo;
	}

	public String getRespsolcambio() {
		return respsolcambio;
	}

	public void setRespsolcambio(String respsolcambio) {
		this.respsolcambio = respsolcambio;
	}

	public String getRespsolelimin() {
		return respsolelimin;
	}

	public void setRespsolelimin(String respsolelimin) {
		this.respsolelimin = respsolelimin;
	}

	public String getRespsolimpres() {
		return respsolimpres;
	}

	public void setRespsolimpres(String respsolimpres) {
		this.respsolimpres = respsolimpres;
	}

	public int getNumberDaysInactivitySACOP() {
		return numberDaysInactivitySACOP;
	}

	public void setNumberDaysInactivitySACOP(int numberDaysInactivitySACOP) {
		this.numberDaysInactivitySACOP = numberDaysInactivitySACOP;
	}

	public int getNumberDaysWithoutActionSACOP() {
		return numberDaysWithoutActionSACOP;
	}

	public void setNumberDaysWithoutActionSACOP(int numberDaysWithoutActionSACOP) {
		this.numberDaysWithoutActionSACOP = numberDaysWithoutActionSACOP;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRespsolsacop() {
		return respsolsacop;
	}

	public void setRespsolsacop(String respsolsacop) {
		this.respsolsacop = respsolsacop;
	}

	public String getListUserAddressee() {
		return listUserAddressee;
	}

	public void setListUserAddressee(String listUserAddressee) {
		this.listUserAddressee = listUserAddressee;
	}

	public int getValiditySacop() {
		return validitySacop;
	}

	public void setValiditySacop(int validitySacop) {
		this.validitySacop = validitySacop;
	}

	public int getValiditySacopType() {
		return validitySacopType;
	}

	public void setValiditySacopType(int validitySacopType) {
		this.validitySacopType = validitySacopType;
	}

	public int getValiditySacopMonth() {
		return validitySacopMonth;
	}

	public void setValiditySacopMonth(int validitySacopMonth) {
		this.validitySacopMonth = validitySacopMonth;
	}

	public int getIdNormAudit() {
		return idNormAudit;
	}

	public void setIdNormAudit(int idNormAudit) {
		this.idNormAudit = idNormAudit;
	}

	public byte getValidNavigator() {
		return validNavigator;
	}

	public void setValidNavigator(byte validNavigator) {
		this.validNavigator = validNavigator;
	}

	public int getIdNodeService() {
		return idNodeService;
	}
	
	public void setIdNodeService(int idNodeService) {
		this.idNodeService = idNodeService;
	}
	
	
}
