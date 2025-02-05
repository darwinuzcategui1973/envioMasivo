--cambiar el nombre de este campo porque es keys es una palabra reserada
ALTER TABLE Documents CHANGE keys keysDoc VARCHAR(1000);
-- nota hay que cambiar el nombte a la tabla transaction por ser palabra reserveda sql server



CREATE DATABASE qwebdocuments;

-- otorgar todos los privilegios al usuario qwebdocuments en la bd qwebdocumentsR
grant all privileges on qwebdocuments.* to qwebdocuments@127.0.0.1 identified by 'qwebdocuments';  
grant all privileges on qwebdocuments.* to qwebdocuments@localhost identified by 'qwebdocuments';


USE qwebdocuments;

DROP TABLE IF EXISTS act_user;
DROP TABLE IF EXISTS actionrecommended;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS audit;
DROP TABLE IF EXISTS audpermstructuser;
DROP TABLE IF EXISTS backupfiles;
DROP TABLE IF EXISTS changeswf;
DROP TABLE IF EXISTS ciudad;
DROP TABLE IF EXISTS conf_documento;
DROP TABLE IF EXISTS conf_expediente;
DROP TABLE IF EXISTS confdocumento_typedocument;
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS digital;
DROP TABLE IF EXISTS doccheckout;
DROP TABLE IF EXISTS docofrejection;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS documentslinks;
DROP TABLE IF EXISTS dtproperties;
DROP TABLE IF EXISTS dual_seqgen;
DROP TABLE IF EXISTS enlaces;
DROP TABLE IF EXISTS equivalencias;
DROP TABLE IF EXISTS expediente;
DROP TABLE IF EXISTS expediente_detalle;
DROP TABLE IF EXISTS expediente_history;
DROP TABLE IF EXISTS expediente_version;
DROP TABLE IF EXISTS flexflowhistory;
DROP TABLE IF EXISTS flexworkflow;
DROP TABLE IF EXISTS groupusers;
DROP TABLE IF EXISTS historydocs;
DROP TABLE IF EXISTS historyfiles;
DROP TABLE IF EXISTS historystruct;
DROP TABLE IF EXISTS historywf;
DROP TABLE IF EXISTS idiomas;
DROP TABLE IF EXISTS indice;
DROP TABLE IF EXISTS listdist;
DROP TABLE IF EXISTS listdistdocument;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS messagexuser;
DROP TABLE IF EXISTS module;
DROP TABLE IF EXISTS norms;
DROP TABLE IF EXISTS pais;
DROP TABLE IF EXISTS parameters;
DROP TABLE IF EXISTS permisiondocgroup;
DROP TABLE IF EXISTS permisiondocuser;
DROP TABLE IF EXISTS permissionfilesgroup;
DROP TABLE IF EXISTS permissionfilesuser;
DROP TABLE IF EXISTS permissionrecordgroup;
DROP TABLE IF EXISTS permissionrecorduser;
DROP TABLE IF EXISTS permissionstructgroups;
DROP TABLE IF EXISTS permissionstructuser;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS planaudit;
DROP TABLE IF EXISTS possiblecause;
DROP TABLE IF EXISTS preview;
DROP TABLE IF EXISTS programaudit;
DROP TABLE IF EXISTS registerclass;
DROP TABLE IF EXISTS security;
DROP TABLE IF EXISTS seguridadgrupo;
DROP TABLE IF EXISTS seguridaduser;
DROP TABLE IF EXISTS state;
DROP TABLE IF EXISTS statusdigital;
DROP TABLE IF EXISTS statususermov;
DROP TABLE IF EXISTS struct;
DROP TABLE IF EXISTS subactivities;
DROP TABLE IF EXISTS tbl_activefactory;
DROP TABLE IF EXISTS tbl_area;
DROP TABLE IF EXISTS tbl_cargo;
DROP TABLE IF EXISTS tbl_clasificacionplanillassacop;
DROP TABLE IF EXISTS tbl_conftagname;
DROP TABLE IF EXISTS tbl_deletesacop;
DROP TABLE IF EXISTS tbl_planillasacop1;
DROP TABLE IF EXISTS tbl_planillasacop1esqueleto;
DROP TABLE IF EXISTS tbl_planillasacopaccion;
DROP TABLE IF EXISTS tbl_procesosacop;
DROP TABLE IF EXISTS tbl_sacop_intouch_hijo;
DROP TABLE IF EXISTS tbl_sacop_intouch_padre;
DROP TABLE IF EXISTS tbl_sacopaccionporpersona;
DROP TABLE IF EXISTS tbl_solicitudimpresion;
DROP TABLE IF EXISTS tbl_tagname;
DROP TABLE IF EXISTS tbl_titulosplanillassacop;
DROP TABLE IF EXISTS titulos;
DROP TABLE IF EXISTS transaction_qweb;
DROP TABLE IF EXISTS typedocuments;
DROP TABLE IF EXISTS urlsusers;
DROP TABLE IF EXISTS user_flexworkflows;
DROP TABLE IF EXISTS user_workflows;
DROP TABLE IF EXISTS usersystem;
DROP TABLE IF EXISTS versiondoc;
DROP TABLE IF EXISTS versiondocview;
DROP TABLE IF EXISTS wfhistory;
DROP TABLE IF EXISTS workflows;
DROP TABLE IF EXISTS workflowspendings;
DROP TABLE IF EXISTS tbl_tipo;

CREATE TABLE activity (
       Act_Number bigint
     , Act_Name varchar(100)
     , Act_Description varchar(100)
     , Act_Active int
     , Act_TypeDocument bigint
     , PRIMARY KEY (Act_Number)
);

CREATE TABLE address (
       idAddress int
     , nombre varchar(50)
     , apellido varchar(50)
     , email varchar(50)
     , active int default 1
     , PRIMARY KEY (idAddress)
);

CREATE TABLE audpermstructuser (
       Aud_idStruct bigint
     , Aud_idPerson bigint
     , Aud_toView int
     , Aud_toRead int
     , Aud_toAddFolder int
     , Aud_toAddProcess int
     , Aud_toDelete int
     , Aud_toMove int
     , Aud_toEdit int
     , Aud_toAddDocument int
     , Aud_toAdmon int
     , Aud_toViewDocs int
     , Aud_toEditDocs int
     , Aud_toAdmonDocs int
     , Aud_toDelDocs int
     , Aud_toReview int
     , Aud_toApproved int
     , Aud_toMoveDocs int
     , Aud_toCheckOut int
     , Aud_toEditRegister int
     , Aud_toImpresion int
     , Aud_toCheckTodos int
     , Aud_toDoFlows int
     , Aud_docInline int
     , Aud_User varchar(20)
     , Aud_Fecha DATETIME default NOW()
     , Aud_Evento varchar(50)
);

CREATE TABLE audit (
	id_audit bigint(20) NOT NULL AUTO_INCREMENT,
	id_transaction INT NOT NULL,
	fecha DATETIME default NOW(),
	detalle VARCHAR(1000) DEFAULT '',
	id_usuario INT NOT NULL,
	valor_inicial  VARCHAR(1000) DEFAULT '',
	valor_final  VARCHAR(1000) DEFAULT '',
	terminal VARCHAR(30) DEFAULT '',
	PRIMARY KEY(id_audit)
);

CREATE TABLE changeswf (
       idMovement int
     , idWorkFlow int
     , typeMovement varchar(1)
     , dateMovement DATETIME default NOW()
     , PRIMARY KEY (idMovement)
);

CREATE TABLE ciudad (
       IdCiudad varchar(10)
     , Nombre varchar(50)
     , PRIMARY KEY (IdCiudad)
);

CREATE TABLE contacts (
       idAddress int
     , idUser varchar(50)
     , PRIMARY KEY (idAddress, idUser)
);

CREATE TABLE doccheckout (
       idCheckOut int
     , idDocument int
     , checkOutBy varchar(30)
     , dateCheckOut DATETIME default NOW()
     , active int default 0
     , PRIMARY KEY (idCheckOut)
);

CREATE TABLE documents (
       numGen int
     , number varchar(40)
     , nameDocument varchar(1000)
     , IdNode int
     , versionPublic bigint default 0
     , datePublic datetime
     , owner varchar(30)
     , type varchar(50)
     , prefix varchar(1000)
     , normISO varchar(50)
     , docProtected int
     , docPublic int
     , docOnline int
     , url varchar(1000)
     , keysDoc varchar(1000)
     , descript varchar(8000)
     , comments text
     , nameFile varchar(1000)
     , statu varchar(2)
     , statuAnt varchar(2)
     , contextType varchar(100)
     , dateCreation datetime default NOW()
     , active int default 1
     , lastOperation varchar(2) default 0
     , lastVersionApproved bigint default 0
     , dateDocVenc datetime
     , docRelations varchar(1000)
     , toForFiles int
     , dateDead datetime
     , loteDoc varchar(20)
     , idDocumentOrigen INT default 0
     , idVersionOrigen INT default 0
     , sacopOccurrenceDate DATETIME
     , idRegisterClass INT default 0
     , idProgramAudit INT default 0
     , idPlanAudit INT default 0
     , PRIMARY KEY (numGen)
);

CREATE TABLE documentslinks (
       numGen int
     , numGenLink int
     , numVer bigint
     , numVerLink bigint
     , PRIMARY KEY (numGen, numGenLink, numVer, numVerLink)
);

CREATE TABLE dual_seqgen (
       NAME varchar(50)
     , NEXT_SEQ bigint
     , LUTS bigint
     , TABLA varchar(50)
     , CAMPO varchar(50)
     , PRIMARY KEY (NAME)
);

CREATE TABLE enlaces (
       IdUrl varchar(5)
     , Name varchar(20)
     , Url varchar(200)
     , Type varchar(20)
     , PRIMARY KEY (IdUrl)
);

CREATE TABLE flexflowhistory (
       idHistory bigint
     , idWorkFlow bigint
     , nameUser varchar(50)
     , dateChange DATETIME default NOW()
     , type varchar(2)
);


CREATE TABLE flexworkflow (
       idWorkFlow bigint
     , DateCreation DATETIME default NOW()
     , owner varchar(30)
     , idDocument int
     , secuential int
     , conditional int
     , notified int
     , expire int
     , dateExpire datetime
     , statu varchar(2)
     , Act_Number bigint
     , type int
     , result varchar(2)
     , comments text
     , dateCompleted datetime
     , idVersion bigint
     , idLastVersion bigint
     , readingWF int
     , activeWF int
     , eliminar int
     , toImpresion int
     , orden int
     , copy int default 0
     , IDFlexFlow bigint
     , cambiar int default 1
);

CREATE TABLE groupusers (
       idGrupo int
     , nombreGrupo varchar(50)
     , descripcionGrupo varchar(256)
     , accountActive int default 1
     , PRIMARY KEY (idGrupo)
);

CREATE TABLE historydocs (
       idHistory int
     , idNode int
     , idNodeFatherAnt int default 0
     , idNodeFatherNew int default 0
     , nameUser varchar(50)
     , dateChange DATETIME default NOW()
     , type varchar(2)
     , comments text
     , MayorVer varchar(5)
     , MinorVer varchar(5)
     , PRIMARY KEY (idHistory)
);

CREATE TABLE historystruct (
       idHistory int
     , idNode int
     , idNodeFatherAnt int
     , idNodeFatherNew int
     , nameUser varchar(50)
     , dateChange DATETIME default NOW()
     , type varchar(1) default 1
     , comments varchar(8000)
     , PRIMARY KEY (idHistory)
);

CREATE TABLE historywf (
       idHistory int
     , idWorkFlow int
     , dateCreation DATETIME default NOW()
     , owner varchar(50)
     , idDocument int
     , secuential int
     , conditional int
     , notified int
     , expire int
     , dateExpire datetime
     , statu varchar(1)
     , type int
     , result varchar(1)
     , comments varchar(8000)
     , dateCompleted datetime
     , idVersion bigint
     , PRIMARY KEY (idHistory)
);

CREATE TABLE idiomas (
       id varchar(2)
     , description varchar(20)
     , codigo varchar(2)
     , country varchar(2)
     , PRIMARY KEY (id)
);

CREATE TABLE location (
       IdNode int
     , Number varchar(6)
     , MajorID SMALLINT
     , HistAprob varchar(2)
     , MinorID SMALLINT
     , HistRev varchar(2)
     , AllowUserWF int
     , Secuential int
     , Conditional int
     , AutomaticNotified int
     , Days int
     , NumberDays varchar(10)
     , showCharge int default 0
     , typePrefix int default 0
     , docInline int
     , timeDocVenc int
     , txttimeDocVenc varchar(100)
     , cantExpDoc varchar(5)
     , unitExpDoc varchar(1)
     , checkvijenToprint int
     , vijenToprint int
     , checkborradorCorrelativo int
     , copy int default 0
     , PRIMARY KEY (IdNode)
);

CREATE TABLE messagexuser (
       id varchar(10)
     , idMessage int
     , toMessage varchar(100)
     , statu int default 1
     , active int default 1
     , userName varchar(100)
     , PRIMARY KEY (id)
);

CREATE TABLE messages (
       idMessage int
     , userName varchar(20)
     , nameFrom varchar(100)
     , fromMessage varchar(100)
     , Subject varchar(250)
     , dateEmail DATETIME default NOW()
     , Message TEXT
     , toMessage varchar(8000)
     , copyMessage varchar(8000)
     , statuMail int default 1
);

CREATE TABLE module (
       IdNode int
     , Description varchar(8000)
     , Prefix varchar(1000)
     , parentPrefix varchar(1000)
     , PRIMARY KEY (IdNode)
);

CREATE TABLE norms (
       idNorm int
     , titleNorm varchar(100)
     , Description varchar(8000)
     , active int default 1
     , data blob
     , nameFile varchar(1000)
     , contextType varchar(100)
     , key_search varchar(255) not null default ''
     , indice varchar(255) not null default ''
     , sistema_gestion varchar(255) not null default ''
	 , documentRequired INT NOT NULL default 0
     , auditProcess INT NOT NULL default 0
     , PRIMARY KEY (idNorm)
);

CREATE TABLE pais (
       CodPais varchar(5)
     , Nombre varchar(50)
     , Codigo varchar(2)
     , PRIMARY KEY (CodPais)
);

CREATE TABLE permisiondocgroup (
       idDocument bigint
     , idGroup varchar(20)
     , toRead int default 0
     , toEdit int default 0
     , toDelete int default 0
     , toMove int default 0
     , toPrint int default 0
     , toReview int default 0
     , toAprove int default 0
     , toCheckOut int default 0
     , toAdmon int default 0
     , toCheckTodos int default 0
     , toImpresion int
     , toEditRegister int
     , toViewDocs int
     , toMoveDocs int
     , toFlexFlow int
     , toChangeUsr int
     , toCompleteFlow int
     , toPublicEraser int
     , toDownload int
     , PRIMARY KEY (idDocument, idGroup)
);

CREATE TABLE permisiondocuser (
       idDocument bigint
     , idUser varchar(50)
     , toRead int default 0
     , toEdit int default 0
     , toDelete int default 0
     , toMove int default 0
     , toPrint int default 0
     , toReview int default 0
     , toAprove int default 0
     , toCheckOut int default 0
     , toAdmon int default 0
     , toCheckTodos int default 0
     , toImpresion int
     , toEditRegister int
     , toViewDocs int
     , toMoveDocs int
     , toFlexFlow int
     , toChangeUsr int
     , toCompleteFlow int
     , toPublicEraser int
     , toDownload int
     , PRIMARY KEY (idDocument, idUser)
);

CREATE TABLE permissionstructgroups (
       idStruct bigint
     , idGroup bigint
     , toView int default 0
     , toRead int default 0
     , toAddFolder int default 0
     , toAddProcess int default 0
     , toDelete int default 0
     , toMove int default 0
     , toEdit int default 0
     , toAddDocument int default 0
     , toAdmon int default 0
     , active int default 1
     , toViewDocs int default 0
     , toEditDocs int default 0
     , toAdmonDocs int default 0
     , toDelDocs int default 0
     , toReview int default 0
     , toApproved int default 0
     , toMoveDocs int default 0
     , toCheckOut int default 0
     , toEditRegister int default 0
     , toImpresion int default 0
     , toCheckTodos int default 0
     , toDoFlows int
     , docInline int
     , toFlexFlow int
     , toChangeUsr int
     , toCompleteFlow int default 0
     , permisoModificado int
     , toPublicEraser int
     , toDownload int
     , PRIMARY KEY (idStruct, idGroup)
);

CREATE TABLE permissionstructuser (
       idStruct bigint
     , idPerson bigint
     , toView int default 0
     , toRead int default 0
     , toViewSub int default 0
     , toAddFolder int default 0
     , toAddProcess int default 0
     , toDelete int default 0
     , toMove int default 0
     , toEdit int default 0
     , toAddDocument int default 0
     , toAdmon int default 0
     , active int default 1
     , toViewDocs int default 0
     , toEditDocs int default 0
     , toAdmonDocs int default 0
     , toDelDocs int default 0
     , toReview int default 0
     , toApproved int default 0
     , toMoveDocs int default 0
     , toCheckOut int default 0
     , toEditRegister int default 0
     , toImpresion int default 0
     , toCheckTodos int default 0
     , toDoFlows int
     , docInline int
     , toFlexFlow int
     , toChangeUsr int
     , toCompleteFlow int
     , permisoModificado int
     , toPublicEraser int
     , toDownload int
     , PRIMARY KEY (idStruct, idPerson)
);


CREATE TABLE person (
       idPerson bigint
     , nameUser varchar(20)
     , clave varchar(50)
     , idGrupo int
     , Nombres varchar(40)
     , Apellidos varchar(40)
     , cargo varchar(100)
     , email varchar(100)
     , Direccion varchar(400)
     , Ciudad varchar(40)
     , Estado varchar(40)
     , CodPais varchar(4)
     , CodigoPostal varchar(10)
     , Tlf varchar(15)
     , IdLanguage varchar(3)
     , numRecordPages int
     , accountActive int default 1
     , primeravez int
     , idarea bigint
     , dateLastPass DATETIME default NOW()
     , EditDocumentCheckOut int
     , dateLastPassEdit datetime
     , edit int
     , published varchar(30)
     , search varchar(30)
     , publishedOption varchar(30)
     , filesOption varchar(1000)
     , searchOption varchar(30)
     , menuHeadOption varchar(1)
     , modo int
     , lado int
     , ppp int
     , panel int
     , pagina int
     , separar int
     , minimo int
     , typeDocuments int
     , ownerTypeDoc bigint default 0
     , idNodeDigital int default 0
     , typesetter bigint default 0
     , checker bigint default 0
     , lote varchar(20) default ''
     , correlativo varchar(20) default ''
     , javaWebStart varchar(250) default ''
     , idNodeService int default 0
     , PRIMARY KEY (idPerson)
);

CREATE TABLE state (
       Id varchar(10)
     , Nombre varchar(50)
     , PRIMARY KEY (Id)
);

CREATE TABLE statususermov (
       IdStatu int
     , IdMovement int
     , IdUser varchar(30)
     , Statu varchar(1)
     , dateReplied datetime
     , comments varchar(8000)
     , parent int
     , PRIMARY KEY (IdStatu)
);

CREATE TABLE struct (
       IdNode int
     , Name varchar(200)
     , IdNodeParent varchar(50)
     , NameIcon varchar(50)
     , NodeType varchar(1)
     , DateCreation DATETIME default NOW()
     , Owner varchar(30)
     , Description varchar(1000)
     , toImpresion int
     , heredarPrefijo int
     , toListDist int default 0
     , respsolcambio varchar(30)
     , respsolelimin varchar(30) 
     , respsolimpres varchar(30)
     , ownerResponsible varchar(30) NOT NULL DEFAULT '' 
     , PRIMARY KEY (IdNode)
);

CREATE TABLE subactivities (
       sAct_number bigint
     , Act_Number bigint
     , sAct_Name varchar(100)
     , sAct_Description varchar(100)
     , sAct_Active int
     , sAct_Order int
     , PRIMARY KEY (sAct_number)
);

CREATE TABLE typedocuments (
       idTypeDoc int
     , TypeDoc varchar(1000)
     , type_Formato int default 0
     , expireDoc int
     , monthExpireDoc varchar(2)
     , unitTimeExpire varchar(1)
     , expireDrafts int
     , monthsExpireDrafts varchar(2)
     , unitTimeExpireDrafts varchar(1)
     , firstPage int
     , sendToFlexWF int default 1
     , actNumber bigint default 0 
     , typesetter bigint default 0
     , ownerTypeDoc bigint default 0
     , idNodeTypeDoc int default 0
     , checker bigint default 0
     , publicDoc int default 1
     , deadDoc int
     , monthsDeadDocs varchar(2)
     , unitTimeDead varchar(1)
     , generateRequestSacop int not null default 0
     , PRIMARY KEY (idTypeDoc)
);


CREATE TABLE urlsusers (
       idUrl varchar(10)
     , idUserGroup varchar(20)
     , PRIMARY KEY (idUrl, idUserGroup)
);

CREATE TABLE usersystem (
       nameUser varchar(20)
     , clave varchar(50)
     , idPersona varchar(20)
     , idGrupo int default 0
     , PRIMARY KEY (nameUser)
);

CREATE TABLE user_flexworkflows (
       idFlexWF bigint
     , orden int
     , idUser varchar(30)
     , idWorkFlow int
     , type int
     , result varchar(2)
     , statu varchar(2)
     , dateReplied datetime
     , comments varchar(7000)
     , isOwner int
     , reading int
     , active int
     , pending int
     , wfActive int
     , IdFather bigint
     , uw_Circle int
     , dateExpireUser datetime
);

CREATE TABLE user_workflows (
       row int
     , orden int
     , idUser varchar(30)
     , idWorkFlow int
     , type int
     , result varchar(2)
     , statu varchar(2)
     , dateReplied datetime
     , comments varchar(7000)
     , isOwner int default 0
     , reading int default 1
     , active int default 1
     , pending int default 0
     , wfActive int default 1
     , IdFather bigint default 0
     , uw_Circle int default 0
     , editor int
     , PRIMARY KEY (row)
);

CREATE TABLE versiondoc (
       numVer int
     , numDoc int
     , MayorVer varchar(10)
     , MinorVer varchar(10)
     , approved int
     , dateCreated DATETIME default NOW()
     , dateApproved datetime
     , expires int
     , dateExpires datetime
     , comments TEXT
     , statu varchar(1)
     , active int default 1
     , dateExpiresDrafts datetime
     , statudraft int default 0
     , statuaprovado int default 0
     , ControldateExpires varchar(50)
     , isUpdate int
     , statuhist varchar(1)
     , typeWF int default 0
     , dayEmailNearExpireSend datetime
     , emailsExpires int
     , dayEmailExpireSend datetime
     , newRegisterToEdit int
     , createdBy varchar(30)
     , nameDocVersion varchar(1000)
     , ownerVersion varchar(30)
     , PRIMARY KEY (numVer)
);

CREATE TABLE versiondocview (
       numVer int
     , nameFile varchar(1000)
     , PRIMARY KEY (numVer)
);

CREATE TABLE wfhistory (
       idHistory bigint
     , idWorkFlow bigint
     , nameUser varchar(50)
     , dateChange DATETIME default NOW()
     , type varchar(2)
     , PRIMARY KEY (idHistory)
);

CREATE TABLE workflows (
       idWorkFlow int
     , DateCreation DATETIME default NOW()
     , owner varchar(30)
     , idDocument int
     , secuential int
     , conditional int
     , notified int
     , expire int
     , dateExpire datetime
     , statu varchar(2)
     , type int
     , result varchar(2)
     , comments TEXT
     , dateCompleted datetime
     , idVersion bigint
     , idLastVersion bigint
     , readingWF int default 1
     , activeWF int default 1
     , eliminar int default 0
     , toImpresion int
     , viewInCanceled int default 1
     , copy int default 0
     , subtype varchar(1)
     , cambiar int default 1
     , PRIMARY KEY (idWorkFlow)
);

CREATE TABLE workflowspendings (
       idWorkFlow bigint
     , idRechazo bigint
     , idPending bigint
     , statu varchar(1)
);

CREATE TABLE dtproperties (
       id integer
     , objectid int
     , property varchar(64)
     , value varchar(255)
     , uvalue varchar(255)
     , lvalue blob
     , version int default 0
     , PRIMARY KEY (id, property)
);

CREATE TABLE parameters (
       lengthPass varchar(5) default 0
     , allowDuplicatePass int
     , expirePass int
     , notifyEmail int
     , smtpMail varchar(30)
     , qualitySystem int
     , versionCom int
     , logDoc int
     , allowWF int
     , docNumber int
     , lengthDocNumber varchar(1)
     , resetDocNumber varchar(6)
     , daysEndPass varchar(3)
     , expireDoc int
     , monthExpireDoc varchar(2)
     , unitTimeExpire varchar(1)
     , msgWFExpires varchar(3000)
     , endsession int default 10
     , typePrefix int default 0
     , msgDocExpirado varchar(8000)
     , msgWFCancelados varchar(8000)
     , msgWFAprobados varchar(8000)
     , msgWFRevision varchar(8000)
     , msgWFBorrador varchar(8000)
     , expireDrafts int
     , monthsExpireDrafts varchar(2)
     , unitTimeExpireDrafts varchar(1)
     , numByLocation SMALLINT
     , typeNumByLocation int
     , cargosacop int 
     , typeOrden int
     , idioma varchar(2)
     , validateEmail int
     , printOwnerAdmin int default 0
     , completeFTP int
     , msgWFCompletados varchar(8000)
     , viewReaders int default 0
     , reasigneUserFTP int
     , downloadLastVer int
     , impNameCharge SMALLINT
     , headImp1 varchar(250)
     , headImp2 varchar(100)
     , headImp3 varchar(100)
     , validNavigator int
     , fileNotOpenSave int
     , fileDoubleVersion varchar(250)
     , mailAccount varchar(250)
     , openoffice int
     , endSessionEdit SMALLINT
     , domainEmail varchar(250)
     , documentReject int
     , viewCreator int
     , viewCollaborator int
     , viewpdftool int
     , oooExeFolder varchar(300)
     , changeProperties int default 0
     , fileDeadFile int default 0
     , fileDeadTime int
     , fileDeadUnit varchar(1)
     , fileDeadVerbose int
     , fileUploadVersion varchar(250)
     , printVersion int
     , printNumber int
     , folderCmp varchar(100)
     , editorOnDemand int
     , editOriginatorWF int
     , domain varchar(100)
     , ldapurl varchar(100)
     , repository varchar(200)
     , copyContents int default 0
     , numberSacop varchar(10) default '1000010'
     , postfixMail varchar(50)
     , deadDoc int
     , monthsDeadDocs varchar(2)
     , unitTimeDead varchar(1)
     , disabledCache int default 0
     , attachedFolder0 varchar(500)
     , attachedFolder1 varchar(500)
     , attachedField varchar(1) 
     , publicEraser int default 0
     , changeAditionalField int default 0     
     , printApprovedDate int(1)     
     , mail_auth_protocol varchar(10)
     , mail_smtp_port varchar(10)
     , mail_auth_password varchar(100)
     , mail_enable_debug int
     , vigencia_flujos_cancelados int default 15
     , piePaginaWaterMark char(1) default 1
     , msgSacopAccionesVencidas varchar(4000)
     , msgSacopAccionesPorVencer varchar(4000)
     , diasAlertaAccionesSacopPorVencer int default 5 
	 , autenticarflujo int default 0
	 , respsolcambio varchar(20)
	 , respsolelimin varchar(20)
	 , respsolimpres varchar(20)
	 , respsolsacop varchar(30) not null default ''
     , numberDaysInactivitySACOP int default 5
     , numberDaysWithoutActionSACOP int default 5
     , listUserAddressee VARCHAR(500) default ''
     , validitySacop INT default 0
     , validitySacopType INT default 0
     , validitySacopMonth INT default 0
     , idNormAudit INT default 0
     , fileNativeViewer varchar(250)
);

CREATE TABLE security (
       keyGen varchar(50)
     , PRIMARY KEY (keyGen)
);

CREATE TABLE seguridadgrupo (
       idGrupo int
     , estructura int
     , flujos int
     , mensajes int
     , administracion int
     , perfil int
     , active int default 1
     , toImpresion int
     , search int
     , sacop int
     , record int     
     , files int
     , digital int
     , PRIMARY KEY (idGrupo)
);

CREATE TABLE seguridaduser (
       idPerson bigint
     , nameUser varchar(50)
     , estructura int
     , flujos int
     , administracion int
     , perfil int
     , mensajes int
     , active int default 1
     , toImpresion int
     , search int
     , sacop int
     , record int     
     , files int
     , digital int
     , PRIMARY KEY (idPerson)
);

CREATE TABLE tbl_activefactory (
       idactivefactorydocument bigint
     , descripcion varchar(8000)
     , url varchar(8000)
     , active int
     , numgen varchar(50)
     , PRIMARY KEY (idactivefactorydocument)
);

CREATE TABLE tbl_area (
       idarea bigint
     , area varchar(1000)
     , activea int
     , prefijo varchar(5)
     , PRIMARY KEY (idarea)
);

CREATE TABLE tbl_tipo (
       idtipo bigint
     , tipo varchar(1000)
     , activea int
     , prefijo varchar(5)
     , PRIMARY KEY (idtipo)
);

CREATE TABLE tbl_cargo (
       idcargo bigint
     , cargo varchar(1000)
     , idarea bigint
     , activec int
     , PRIMARY KEY (idcargo)
);



CREATE TABLE tbl_conftagname (
       idtagname bigint
     , tipotag varchar(8000)
     , tipoalarma varchar(8000)
     , valor varchar(8000)
     , active int
     , PRIMARY KEY (idtagname)
);

CREATE TABLE tbl_deletesacop (
       id bigint
     , idplanillasacop1 bigint
     , idperson varchar(1000)
     , active int
     , PRIMARY KEY (id)
);

CREATE TABLE tbl_planillasacop1 (
       idplanillasacop1 bigint
     , sacopnum varchar(1000)
     , emisor bigint
     , usernotificado bigint
     , respblearea bigint
     , estado int
     , origensacop int
     , fechaemision datetime NOT NULL
     , requisitosaplicable varchar(1000)
     , procesosafectados varchar(1000)
     , solicitudinforma varchar(1000)
     , descripcion varchar(4000)
     , causasnoconformidad varchar(4000)
     , accionesrecomendadas varchar(4000)
     , correcpreven varchar(1)
     , rechazoapruebo varchar(1)
     , noaceptada varchar(1000)
     , accionobservacion varchar(4000)
     , fechaestimada varchar(20)
     , fechareal varchar(20)
     , accionesEstablecidas varchar(1)
     , accionesEstablecidastxt varchar(4000)
     , eliminarcausaraiz varchar(1)
     , eliminarcausaraiztxt varchar(4000)
     , nameFile varchar(1000)
     , contentType varchar(1000)
     , activecomntresponsablecerrar varchar(200)
     , active int
     , fechaculminar varchar(1000)
     , comntresponsablecerrar TEXT
     , sacop_relacionadas varchar(8000)
     , noconformidadesref varchar(8000)
     , noconformidades varchar(8000)
     , idplanillasacop1esqueleto bigint
     , clasificacion int4
     , fechaWhenDiscovered datetime NULL
     , archivo_tecnica varchar(250) NULL
     , fecha_verificacion datetime null
     , fecha_cierre datetime null
     , usuarioSacops1 INT
     , fechaSacops1 datetime
     , idDocumentRelated INT default 0
     , actionType VARCHAR(3) DEFAULT ''
     , idDocumentAssociate INT default 0
     , numVerDocumentAssociate INT default 0
     , nameDocumentAssociate VARCHAR(200)
     , requireTracking INT default 0
     , requireTrackingDate datetime
     , trackingSacop INT NOT NULL default 0
     , numberTrackingSacop VARCHAR(100) NOT NULL DEFAULT ''
     , idRegisterGenerated INT NOT NULL default 0
     , descripcionAccionPrincipal VARCHAR(4000) NOT NULL DEFAULT ''
     , PRIMARY KEY (idplanillasacop1)
);

CREATE TABLE tbl_planillasacop1esqueleto (
       idplanillasacop1 bigint
     , sacopnum varchar(1000)
     , emisor bigint
     , usernotificado bigint
     , respblearea bigint
     , estado int
     , origensacop int
     , fechaemision DATETIME default NOW()
     , requisitosaplicable varchar(1000)
     , procesosafectados varchar(1000)
     , solicitudinforma varchar(1000)
     , descripcion varchar(1000)
     , causasnoconformidad varchar(8000) 
     , accionesrecomendadas varchar(8000)
     , correcpreven varchar(1)
     , rechazoapruebo varchar(1)
     , noaceptada varchar(1000)
     , accionobservacion varchar(8000)
     , fechaestimada varchar(20)
     , fechareal varchar(20)
     , accionesEstablecidas varchar(1)
     , accionesEstablecidastxt varchar(8000)
     , eliminarcausaraiz varchar(1)
     , eliminarcausaraiztxt varchar(8000)
     , nameFile varchar(1000)
     , contentType varchar(1000)
     , activecomntresponsablecerrar varchar(200)
     , active int
     , fechaculminar varchar(1000)
     , comntresponsablecerrar TEXT
     , sacop_relacionadas varchar(8000)
     , numgen_version varchar(8000)
     , noconformidades varchar(8000)
     , noconformidadesref varchar(8000)
     , estadoEsqueletoConfiguradoSacop int
     , usuarioSacops1 INT
     , fechaSacops1 datetime
     , idDocumentRelated INT default 0
     , actionType VARCHAR(3) DEFAULT ''
     , idDocumentAssociate INT default 0
     , numVerDocumentAssociate INT default 0
     , nameDocumentAssociate VARCHAR(200)
     , requireTracking INT default 0
     , requireTrackingDate datetime
     , trackingSacop INT NOT NULL default 0
     , numberTrackingSacop VARCHAR(100) NOT NULL DEFAULT ''
     , idRegisterGenerated INT NOT NULL default 0
     , descripcionAccionPrincipal VARCHAR(4000) NOT NULL DEFAULT ''
     , PRIMARY KEY (idplanillasacop1)
);

CREATE TABLE tbl_planillasacopaccion (
       idplanillasacopaccion bigint
     , idplanillasacop1 bigint
     , accion varchar(1000)
     , fecha varchar(20)
     , responsables varchar(1000)
     , active int
     , PRIMARY KEY (idplanillasacopaccion)
);

CREATE TABLE tbl_procesosacop (
       numproceso bigint
     , proceso varchar(1000)
     , active int
     , PRIMARY KEY (numproceso)
);

CREATE TABLE tbl_sacop_intouch_hijo (
       idtagname bigint
     , tagname varchar(8000)
     , disparadasacop int
     , active int
     , idplanillasacop1 bigint
     , PRIMARY KEY (idtagname)
);

CREATE TABLE tbl_sacop_intouch_padre (
       idsacopintouchpadre bigint
     , idplanillasacop1 bigint
     , active int
     , enable int
     , PRIMARY KEY (idsacopintouchpadre)
);

CREATE TABLE tbl_sacopaccionporpersona (
       idplanillasacopaccion bigint
     , idperson bigint
     , comentario varchar(8000)
     , fecha varchar(20)
     , firmo int
     , active int
     , evidencia varchar(250) NULL
     , PRIMARY KEY (idplanillasacopaccion,idperson,active)
);

CREATE TABLE tbl_solicitudimpresion (
       numsolicitud bigint
     , datesolicitud DATETIME default NOW()
     , solicitante bigint
     , autorizante bigint
     , statusautorizante int
     , statusimpresion int
     , numgen bigint
     , numVer bigint
     , copias varchar(1000)
     , destinatarios varchar(1000)
     , comments varchar(1000)
     , active int
     , PRIMARY KEY (numsolicitud)
);

CREATE TABLE tbl_tagname (
       idtagname2 bigint
     , tagname varchar(8000)
     , active int
     , idtipo varchar(8000)
     , PRIMARY KEY (idtagname2)
);

CREATE TABLE tbl_titulosplanillassacop (
       numtitulosplanillas bigint
     , titulosplanillas varchar(1000)
     , active int
     , PRIMARY KEY (numtitulosplanillas)
);

CREATE TABLE act_user (
       Act_Number bigint
     , idPerson bigint
);

create table docofrejection (
	dateCreate DATETIME default NOW(),
	idFlexFlow int not null,
	idUser int not null,
	numgen int not null,
	documentReject int not null,
	primary key (dateCreate)
);

CREATE TABLE permissionrecorduser (
     idUser VARCHAR(20)
     , toGenerate INT default 0
     , toUpdate INT default 0
     , toSend INT default 0
     , toExport INT default 0
     , toPrint INT default 0
     , PRIMARY KEY (idUser)
);

CREATE TABLE permissionrecordgroup (
     idGroup VARCHAR(20)
     , toGenerate INT default 0
     , toUpdate INT default 0
     , toSend INT default 0
     , toExport INT default 0
     , toPrint INT default 0
     , PRIMARY KEY (idGroup)
);

create table expediente (
    f1 bigint,
    f2 DATETIME default NOW(),
    f3 varchar(20),
    primary key(f1)
);

CREATE TABLE conf_expediente (
       id varchar(3)
     , etiqueta01 varchar(100)
     , etiqueta02 varchar(100)
     , tipo int
     , longitud int
     , entrada varchar(8)
     , valores varchar(4000)
     , condicion varchar(500)
     , editable int
     , visible int
     , criterio int
     , auditable int
     , orden int
     , imprimir int
     , PRIMARY KEY(id)
);

create table expediente_history (
    f1 bigint,
    filesVersion int,
    f2 DATETIME default NOW(),
    f3 varchar(20),
    primary key(f1,filesVersion)
);

create table expediente_detalle (
    f1 bigint,
    numgen int,
    orden int,
    primary key(f1,numgen)
);

create table expediente_version (
    f1 bigint,
    numgen int,
    orden int,
    numver int,
    filesVersion int,
    nameUser varchar(20),
    datePrint DATETIME default NOW(),
    ownerFiles varchar(20),
    primary key(f1,numgen,filesVersion)
);

CREATE TABLE historyfiles (
       idHistory int
     , idNode int
     , nameUser varchar(50)
     , dateChange DATETIME default NOW()
     , type varchar(2)
     , comments varchar(8000)
     , MayorVer varchar(5)
     , MinorVer varchar(5)
     , PRIMARY KEY (idHistory)
);

CREATE TABLE permissionfilesuser (
    idUser VARCHAR(20)
    , toFilesSecurity INT default 0
    , toFilesCreate INT default 0
    , toFilesExport INT default 0
    , toFilesEdit INT default 0
    , toFilesDelete INT default 0
    , toFilesRelated INT default 0
    , toFilesVersion INT default 0
    , toFilesView INT default 0
    , toFilesPrint INT default 0
    , toFilesHistory INT default 0
    , toFilesDownload INT default 0
    , toFilesSave INT default 0
    , PRIMARY KEY (idUser)
);

CREATE TABLE permissionfilesgroup (
    idGroup VARCHAR(20)
    , toFilesSecurity INT default 0
    , toFilesCreate INT default 0
    , toFilesExport INT default 0
    , toFilesEdit INT default 0
    , toFilesDelete INT default 0
    , toFilesRelated INT default 0
    , toFilesVersion INT default 0
    , toFilesView INT default 0
    , toFilesPrint INT default 0
    , toFilesHistory INT default 0
    , toFilesDownload INT default 0
    , toFilesSave INT default 0
    , PRIMARY KEY (idGroup)
);

CREATE TABLE equivalencias (
    indice int not null,
    nombre varchar(50) not null,
    campo varchar(50) not null,
    posicion int not null,
    columna varchar(50) not null,
    valor varchar(500),
    indexar int not null,
    activo int not null,
    primary key (indice,nombre)
);

CREATE TABLE titulos (
    posicion int not null,
    titulo varchar(50) not null,
    primary key (posicion)
);

CREATE TABLE indice (
    clave varchar(100) not null,
    valor varchar(500) not null,
    indice int not null,
    primary key (clave,indice)
);

CREATE TABLE conf_documento (
       id varchar(3)
     , etiqueta01 varchar(100)
     , etiqueta02 varchar(100)
     , tipo int
     , longitud int
     , entrada varchar(8)
     , valores varchar(4000)
     , condicion varchar(500)
     , editable int
     , visible int
     , criterio int
     , auditable int
     , orden int
     , imprimir int
     , location int
     , PRIMARY KEY(id)
);

create table statusdigital (
	idStatusDigital int not null
	, descStatusDigital varchar(30)
	, PRIMARY KEY(idStatusDigital)
);

create table digital (
    idDigital bigint not null
    , nameFile varchar(1000) not null default ''
    , nameDocument varchar(1000)
    , type int
    , dateCreation DATETIME default NOW()
    , numberTest int
    , idPerson bigint
    , idPersonDelete bigint
    , dateDelete datetime
    , idStatusDigital int
    , comentario varchar(1000)
    , lote varchar(20)
    , ownerTypeDoc bigint default 0
    , idNode int default 0
    , typesetter bigint default 0
    , checker bigint default 0
    , visible int default 1
    , versionMayor varchar(10) not null default ''
    , versionMenor varchar(10) not null default ''
    , codigo varchar(40) not null default ''
    , publicado int not null default 0
    , expira int not null default 0
    , fechaPublicacion datetime
    , fechaVencimiento datetime
    , comentarios varchar(1000) not null default ''
    , url varchar(1000) not null default ''
    , palabrasClaves varchar(1000) not null default ''
    , descripcion varchar(1000) not null default ''
    , otrosDatos text
    , internalmetadata varchar(250)
    , PRIMARY KEY(idDigital)
);

create table confdocumento_typedocument (
       id varchar(3) not null
       , idTypeDoc int not null
       , clave int not null default 0
       , primary key (id,idTypeDoc)
);

create table preview (
	idDocument int not null,
	idVersion int not null,
	idUsuario int not null,
	contador int not null,
	registra datetime not null,
	actualiza datetime not null,
    primary key(idDocument,idVersion,idUsuario)
);

CREATE TABLE listdist (
    idNode int not null,
    nameUser varchar(20) not null,
    primary key(idNode,nameUser)
);

CREATE TABLE listdistdocument (
    idDocument int not null,
    idVersion int not null,
	idUsuario int not null,
    primary key(idDocument,idVersion,idUsuario)
);

CREATE TABLE tbl_clasificacionplanillassacop(
  id int8 NOT NULL,
  descripcion varchar(1000) NOT NULL,
  active int4 NOT NULL,
  CONSTRAINT tbl_clasificacionplanillassacop_pkey PRIMARY KEY (id)
);

CREATE TABLE backupfiles (
       IDBackup bigint
     , IdDocument bigint
     , MayorVer varchar(10)
     , MinorVer varchar(10)
     , dateCreated datetime
);

create table registerclass (
	idRegisterClass int not null
	, descRegisterClass varchar(50)
	, PRIMARY KEY(idRegisterClass)
);

create table possiblecause (
	idPossibleCause int,
	descPossibleCause varchar(50),
	PRIMARY KEY (idPossibleCause)
);

create table actionrecommended (
	idActionRecommended int,
	descActionRecommended varchar(60),
	idRegisterClass int ,
	PRIMARY KEY(idActionRecommended)
);

create table programaudit (
	idProgramAudit INT NOT NULL,
	nameProgram VARCHAR(100) NOT NULL,
	idNorm INT NOT NULL,
	idPersonResponsible INT NOT NULL,
	dateFrom DATE NOT NULL,
	dateUntil DATE NOT NULL,
	status VARCHAR(1) NOT NULL,
	idNormCheck VARCHAR(200) NULL DEFAULT '',
	dateCreated DATE NOT NULL,
	idPersonCreator INT NOT NULL,
	PRIMARY KEY(idProgramAudit)
);

create table planaudit (
	idPlanAudit INT NOT NULL,
	namePlan VARCHAR(100) NOT NULL,
	idProgramAudit INT NOT NULL,
	idPersonPlan INT NOT NULL,
	typeAudit INT NOT NULL,
	dateFromPlan DATE NOT NULL,
	dateUntilPlan DATE NOT NULL,
	statusPlan VARCHAR(1) NOT NULL,
	idNorm INT NOT NULL,
	idNormPlanCheck VARCHAR(4000) NULL DEFAULT '',
	dateCreatedPlan DATE NOT NULL,
	idPersonCreatorPlan INT NOT NULL,
	PRIMARY KEY(idPlanAudit)
);

CREATE TABLE transaction_qweb (
  id_transaction INT NOT NULL AUTO_INCREMENT,
  tra_descrip VARCHAR(500) NOT NULL,
  tra_gravity INT default 0,
  tra_save INT default 0,
  tra_notify INT default 0,
  tra_text_history VARCHAR(500) DEFAULT '',
  tra_text_notify VARCHAR(500) DEFAULT '',
  tra_performer VARCHAR(500) DEFAULT '',
  tra_case_exception VARCHAR(500) DEFAULT '',
  tra_email VARCHAR(100) DEFAULT '',
  tra_jsp VARCHAR(50) DEFAULT '',
  tra_location VARCHAR(500) DEFAULT '',
  tra_class VARCHAR(100) DEFAULT '',
  tra_class_location VARCHAR(500) DEFAULT '',
  tra_activate INT default 1,
  tra_level INT default 1,
  PRIMARY KEY(id_transaction)
);

CREATE INDEX versiondoc_numdoc ON versiondoc(numdoc);
CREATE INDEX userflexworkflow_idflow ON user_flexworkflows(idWorkFlow);
CREATE INDEX userworkflow_idflow ON user_workflows(idWorkFlow);
CREATE INDEX ix_idnode_parent ON struct(idnodeparent);


