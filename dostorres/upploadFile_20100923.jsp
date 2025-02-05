<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.to.DigitalTO,
				 com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm,
				 com.desige.webDocuments.utils.Constants,
				 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 java.util.Collection,
                 java.util.Iterator,
                 java.io.File,
                 com.desige.webDocuments.utils.beans.Search,
			     com.desige.webDocuments.utils.ToolsHTML" %>
<%@ page language="java" %>

<jsp:include page="richeditDocType.jsp" /> 
<!--/**
 * Title: upploadFile_20100923.jsp <br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)  
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..</li>
 *      <li> 08/05/2006 (SR) Se valido los botones para cuando se pulse aceptar, se invaliden en upploadFile.jsp</li>
 *      <li> 14/07/2006 (NC) Cambios para el correcto manejo de la Versión según el estatu del Documento </li>
 * <ul>
 */-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
				 com.desige.webDocuments.files.forms.DocumentForm,
                 java.util.Hashtable,
                 java.util.ArrayList,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.structured.forms.BaseStructForm"%>
<%@ page import="com.desige.webDocuments.document.forms.BaseDocumentForm"%>
<%@ page import="com.desige.webDocuments.utils.DesigeConf"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    String docPublic = "1";
%>
<logic:equal value="0" name="newDocument" property="docPublic">
    <%
        docPublic = "0";
    %>
</logic:equal>
<%

	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

    Users usuario = (Users)session.getAttribute("user");
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    
    boolean isSave=true;
    
    boolean isModuloDigital = session.getAttribute(Constants.MODULO_ACTIVO).equals(Constants.MODULO_DIGITALIZAR);
	String idDigital = session.getAttribute("idDigital")!=null?String.valueOf(session.getAttribute("idDigital")):"";
	DigitalTO digitalTO = (isModuloDigital?(DigitalTO)session.getAttribute("digitalTO"):null);
	
	ArrayList conf = (ArrayList)session.getAttribute("confDocument");
	DocumentForm obj;
	
	
    String nodeActive = (String)session.getAttribute("nodeActive");
    boolean swborradorCorrelativo=false;
    if (session.getAttribute("borradorCorrelativo")!=null){
           swborradorCorrelativo="1".equalsIgnoreCase((String)session.getAttribute("borradorCorrelativo"));
    }
    pageContext.setAttribute("cmd",cmd);
    Hashtable tree = (Hashtable)session.getAttribute("tree");
    BaseStructForm dataForm = (BaseStructForm)tree.get(nodeActive);
    String rout = dataForm.getRout();
    boolean validDatePublic = false;
    String dateSystem = ToolsHTML.date.format(new java.util.Date());       
    String dateExpiresMax = ToolsHTML.calculateDateExpires(dateSystem,docPublic,"0");
    //System.out.println("[upploadFile] dateExpiresMax = " + dateExpiresMax);
    boolean readOnly=false;

    Collection tipoOnLine = (Collection)session.getAttribute("typesDocumentsDoc");
    
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    
    
    // datos del scanner
    int modo = usuario.getModo();
    int lado = usuario.getLado();
    int ppp = usuario.getPpp();
    int panel = usuario.getPanel();
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css" />

<script language=javascript src="./estilo/popcalendar2.js"></script>
<script language=javascript src="./estilo/fechas.js"></script>
<script language="JavaScript" src="estilo/Calendario_2.js"></script>
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">

<jsp:include page="richeditHead.jsp" /> 

<script language="JavaScript">

    var docLinks;
    var tipoDoc = new Array();
    var tipoDocActNumber = new Array();
    
    String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
    

    function validarFechaAprobacion(valor) {
        if (!validarFecha(valor)){
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        if (valor > '<%=dateSystem%>') {
            alert('<%=rb.getString("err.badDateEndWF")%>');
            return false;
        }
        return true;
    }

    function validarFechaExpiracion(valor) {
        if (!validarFecha(valor)) {
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }

        if (valor < '<%=dateSystem%>') {
            alert('<%=rb.getString("err.badDateExpiresDoc")%>');
            return false;
        }
        <%
            if (dateExpiresMax!=null) {
        %>
                if (valor > '<%=dateExpiresMax%>') {
                    alert('<%=rb.getString("err.badDateExpiresDocI")%>');
                }
        <%
            }
        %>
        return true;
    }
 //salva documentos que no estan en linea inicio
    function validar(forma,validate,typeMay,typeMin){
          //chequeamos si el borrador lleva correlativo
            <%if (swborradorCorrelativo){%>
                 //verificamos si el campo numero correlativo tiene numero porque se exige el numero
                 //si es borrador y en propiedades de localidad se pide colocar numero
                  if ( (typeof forma.number.value=='undefined') || forma.number.value.length<=0){
                      alert('<%=rb.getString("doc.numerborrador")%>');
                      forma.number.focus();
                      return false;
                  }
            <%}%>
        if (validate) {
            if (forma.approvedI) { //Primero Verifico que el Check Existe en el Formulario
                if (forma.approvedI.checked) { //Si esta Seleccionado
                    if (!validarFechaAprobacion(forma.dateApproved.value)){
                        return false;
                    }
                } else { //Si el CheckBox no esta Seleccionado
                    var datos = forma.dateApproved.value;
                    if (datos.length > 0){
                        alert('<%=rb.getString("err.mustSelect")%>');
                        return false;
                    }
                }
            }
        }
        if (forma.expiresI) { //Primero Verifico que el Check Existe en el Formulario
            //Si hay una fecha tipeada por el usuario
             //se Verifica si el Check está seleccionado
             if (forma.dateExpires.value.length > 0) {
                 forma.expiresI.checked = true;
                 forma.expires.value = "0";
             }
            if (forma.expiresI.checked) { //Si esta Seleccionado
                if (forma.approvedI.checked) {
                    if (!validarFechaExpiracion(forma.dateExpires.value)) {
                        return false;
                    }
                } else {
                    alert('<%=rb.getString("err.mustSelectExpire")%>');
                    return false;
                }
            }
        }
       
        if (forma.isFileScanned.value==false && forma.docOnlineI && forma.docOnlineI.checked==false && forma.nameFile.value.length == 0) {
            alert('<%=rb.getString("err.notFile")%>');
            return false;
        }
        
        
        if (document.newDocument.nameDocument && document.newDocument.nameDocument.value.trim()=="") {
            alert('<%=rb.getString("err.notNameFile")%>');
            return false;
        }
        
        if (forma.mayorVer) {
            if (!valVersion(forma.mayorVer.value,typeMay,'<%=rb.getString("E0114")%>')) {
                return false;
            }
        }
        
        if (forma.minorVer) {
            if (!valVersion(forma.minorVer.value,typeMin,'<%=rb.getString("E0115")%>',false)) {
                return false;
            }
        }
        
        return true;
    }

    function valVersion(valor,type,msg,isMayor) {
    	isMayor = (isMayor==undefined?true:isMayor);
        if (type == 0) {
            if (!(/^([0-9])*$/.test(valor))) {
                alert(msg);
                return false;
            }
        } else {
            if (!(/^([A-Z])*$/.test(valor))) {
                alert(msg);
                return false;
            }
            if (isMayor) {
	            if (!valor || valor=="" || valor.replace(/ /g,"").length==0) {
	                alert(msg);
	                return true;
	            }
            }
        }
        return true;
    }
    
    function isExtensionDoble(forma) {
    	if(forma.nameFile.value.length==0) {
    		return false;
    	}
    	cad = forma.nameFile.value;
    	if(isDoblePunto(cad)) {
    		alert("<%=rb.getString("err.isNotValidFileExt")%>");
    		return false;
    	}
    	if(!isValidName(cad)) {
    		alert("<%=rb.getString("err.isNotValidFileName")%>");
			return false;
    	}

    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;
    	
   		document.getElementById("paralelo").style.display="none";
    	if(extensionDouble.toLowerCase().indexOf(cad.toLowerCase())!=-1 && forma.nameFileParalelo.value.length==0 ) {
    		alert("<%=rb.getString("err.insertViewFile")%>");
    		document.getElementById("paralelo").style.display="";
    		return false;
    	}
		if(extensionDouble.indexOf(cad)!=-1) {
    		cad = forma.nameFileParalelo.value;
    		if(isDoblePunto(cad)) {
	    		alert("<%=rb.getString("err.isNotValidFileExt")%>");
	    		document.getElementById("paralelo").style.display="";
    			return false;
	    	}
	    	if(!isValidName(cad)) {
	    		alert("<%=rb.getString("err.isNotValidFileName")%>");
	    		document.getElementById("paralelo").style.display="";
    			return false;
	    	}
	    }
    	return true;
    }
    
    function isDoblePunto(name) {
    	name = name.replace(/\\/g,"/");
    	var arr = name.split("/");
    	name = arr[arr.length-1];
    	var nPunto = 0;
    	while(name.indexOf(".")!=-1 && nPunto<2) {
    		nPunto++;
    		name = name.substring(name.indexOf(".")+1);
    	}
		return (nPunto!=1);
    }
    
    function isValidName(name) {
    	name = name.replace(/\\/g,"/");
    	var arr = name.split("/");
    	name = arr[arr.length-1];
    	if(name!=name.replace(/[^a-z,^0-9 \.\-\_]/gi,"")) {
			return false;
		}
		return true;
    }
    
    
    function salvar(formulario,validate,typeMay,typeMin,checkerData) {
    
    	<%if(isModuloDigital){%>
    	
		formulario.isCheckerData.value=checkerData;
    	
    	if(!formulario.idRout.value || parseInt(formulario.idRout.value)<=0) {
    		alert("Seleccione la ubicacion del archivo dentro de la estructura.");
    		return;
    	}
    	formulario.idNodeSelected.value=formulario.idRout.value;
    	<%}%>
    
    	if(formulario.nameFile.value.length!=0) {
    		formulario.isFileScanned.value = false;
    	}
    	
    	if (formulario.docOnlineI) { 
	    	if (formulario.isFileScanned.value==false && formulario.docOnlineI.checked==false && !isExtensionDoble(formulario)) { 
    			return;
    		}
    	} else {
    		if (formulario.isFileScanned.value==false) {
		    	if (!isExtensionDoble(formulario)) { 
	    			return;
		    	}
	    	}
    	}



        formulario.action = "servletloaddocuments?cmd=upFile";
        
        updateRTEs();
        if (validar(formulario,validate,typeMay,typeMin)) {
        	if(formulario.richedit){
            	formulario.comments.value = formulario.richedit.value;
            }
            
	        //if (formulario.docOnlineI && formulario.docOnlineI.checked==true && isEmptyRicheditValue(formulario.richedit.value)) {
	        if (formulario.docOnlineI && formulario.docOnlineI.checked==true && formulario.comments.value=='') {
                alert('<%=rb.getString("err.invalidContentDoc")%>');
                return false;
            }
            
            formulario.btnsalvar.style.display="none";
            if(formulario.btnsalvar2){
            	formulario.btnsalvar2.style.display="none";
            }
            document.getElementById("btncancelar").style.display="none";

			if(tipoDoc[document.newDocument.typeDocument.value]==0) {
				formulario.isSendToFlexWF.value = true;
				formulario.actNumber.value = tipoDocActNumber[document.newDocument.typeDocument.value];
			} else {
				formulario.isSendToFlexWF.value = false;
			}

            formulario.submit();
        }
    }
//salva documentos que no estan en linea fin

//salva documentos que estan en linea inicio
 <!--simon inicio 13 de junio 2005 -->
    function validar1(forma,validate,typeMay,typeMin) {
      //chequeamos si el borrador lleva correlativo
            <%if (swborradorCorrelativo){%>
           //verificamos si el campo numero correlativo tiene numero porque se exige el numero
           //si es borrador y en propiedades de localidad se pide colocar numero
            if (forma.number.value.length<=0){
                alert('<%=rb.getString("doc.numerborrador")%>');
                forma.number.focus();
                return false;
            }
            <%}%>
         if (validate) {
             if (forma.approvedI) { //Primero Verifico que el Check Existe en el Formulario
                 if (forma.approvedI.checked) { //Si esta Seleccionado
                     if (!validarFechaAprobacion(forma.dateApproved.value)){
                         return false;
                     }
                 } else { //Si el CheckBox no esta Seleccionado
                     var datos = forma.dateApproved.value;
                     if (datos.length > 0){
                         alert('<%=rb.getString("err.mustSelect")%>');
                         return false;
                     }
                 }
             }
         }

         if (forma.expiresI) { //Primero Verifico que el Check Existe en el Formulario
             //Si hay una fecha tipeada por el usuario
             //se Verifica si el Check está seleccionado
             if (forma.dateExpires.value.length > 0) {
                 forma.expiresI.checked = true;
                 forma.expires.value = "0";
             }
             if (!forma.expiresI.checked) {
                 alert('<%=rb.getString("err.badDateExpiresDoc")%>');
                 return false;
             }
             if (forma.expiresI.checked) { //Si esta Seleccionado
                if (forma.approvedI.checked){
                     if (!validarFechaExpiracion(forma.dateExpires.value)){
                         return false;
                     }
               } else {
                    alert('<%=rb.getString("err.mustSelectExpire")%>');
                    return false;
                }
             }
         }
        if (forma.mayorVer) {
            if (!valVersion(forma.mayorVer.value,typeMay,'<%=rb.getString("E0114")%>')) {
                return false;
            }
        }

        if (forma.minorVer) {
            if (!valVersion(forma.minorVer.value,typeMin,'<%=rb.getString("E0115")%>')) {
                return false;
            }
        }
        
       //if (isEmptyRicheditValue(forma.richedit.value)) {
       if (forma.comments.value=='') {
          alert('<%=rb.getString("E0126")%>');
          return false;
       }
        
        return true;
     }

     function salvar1(formulario,validate,typeMay,typeMin){
        formulario.action = "servletloaddocuments?cmd=upFile";
        
        updateRTEs();
        if (validar1(formulario,validate,typeMay,typeMin)) {
        	if(formulario.richedit){
            	formulario.comments.value = formulario.richedit.value;
            }
          // alert("archivo en linea:"+formulario.action);
            formulario.btnsalvar.disabled=true;
            if(formulario.btnsalvar2){
	            formulario.btnsalvar2.disabled=true;
	        }
            document.getElementById("btncancelar").style.disabled=true;
            formulario.submit();
        }
    }

 <!--simon fin 13 de junio 2005 -->
//salva documentos que  estan en linea fin


    function getDateExpire(dateField,nameForm,dateValue,text){
    	if(document.all) {
        	window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=280,height=170,resizable=no,scrollbars=yes,left=300,top=250");
        } else {
        	window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=260,height=170,resizable=no,scrollbars=yes,left=300,top=250");
        }
    }

    function showWindow(pages,input,nameForm,value,width,height){
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&nameForma="+nameForm+"&value="+value,"","width="+width+",height="+height+",resizable=no,scrollbars=yes,statusbar=yes,left=250,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function updateCheck(check,field,texto) {
        if (check.checked) {
            field.value = "0";
            if(texto){
	            texto.readOnly = false;
    	        texto.focus();
    	    }
        } else{
            if(texto){
	            texto.readOnly = true;
	        }
            field.value = 1;
        }
        validarOnLine();
        
        document.newDocument.dateExpires.readOnly = true;
        if(document.newDocument.expiresI.checked==false){
	        document.newDocument.expiresI.check = false;
	        document.newDocument.dateExpires.value=""; 
	        document.newDocument.initialDay.value="";  
	        document.newDocument.initialMonth.value="";  
	        document.newDocument.initialYear.value="";
	        document.newDocument.expires.value="";
	        document.newDocument.expiresI.checked=false; 
	     }
    }
    
    function updateCheckPublic(check,field,onLine) {
    	form = document.newDocument;
    	valor = form.typeDocument.value;

    	// validamos el tipo de documento
    	if(tipoDoc[valor]==0) {
	   		document.getElementById('trPublicar').style.display='none';
    		check.checked = false;
	   	} else {
	   		document.getElementById('trPublicar').style.display='';
	   	}
   		
        if (check.checked) {
            field.value = "0";
            document.getElementById("filaFecha").style.display="";
            document.newDocument.approvedI.checked=true;
            if(document.newDocument.dateApproved.value==''){
            	document.newDocument.dateApproved.value='<%=ToolsHTML.getFechaAMD()%>';
            }
        } else{
            field.value = 1;
            document.getElementById("filaFecha").style.display="none";
            document.newDocument.approvedI.checked=false;
            
	        document.newDocument.dateApproved.value=""; 
	        document.newDocument.initialDay.value="";  
	        document.newDocument.initialMonth.value="";  
	        document.newDocument.initialYear.value="";
	        document.newDocument.approved.value="1";
        }
        if(onLine) {
	        validarOnLine();
	    }
    }
    
    
	var tiposDocument=new Array();
	<%Search s = null;
	//System.out.println(tipoOnLine);
    int i =0;
	if(tipoOnLine!=null){
	    Iterator ite = tipoOnLine.iterator();
	    //System.out.println("ite="+ite);
	    while(ite.hasNext()) {
	    	s = (Search)ite.next();	
	    	%>
			tiposDocument[<%=i++%>]=<%=s.getId()%>
		<%}
	}%>    
    
    
    function validarOnLine(formulario,idTypeDoc) {
    	form = document.newDocument;
    	valor = form.typeDocument.value;
    	
    	// validamos el tipo de documento
    	if(tipoDoc[valor]==0) {
			document.getElementById('trPublicar').style.display='none';
			updateCheckPublic(form.docPublicI,form.docPublic,false);
    	} else {
    		document.getElementById('trPublicar').style.display='';
    	}
    	
    	// buscamos el tipo de documento
    	<%if(isModuloDigital){%>
    	if(typeof(idTypeDoc)!='undefined'){
	    	buscarTipo(idTypeDoc);
	    }
    	<%}%>

		if(!form.docOnlineI) {
			return true;
		}
    	if(form.docOnlineI.checked==false && <%=!isModuloDigital%> ) {
    		document.getElementById("tab_0").style.display="";
    		document.getElementById("tab_1").style.display="none";
    		document.getElementById("tab_file").style.display="";
    		return true;
    	}
		document.getElementById("tab_0").style.display="none";
		document.getElementById("tab_1").style.display="";
		document.getElementById("tab_file").style.display="none";
    	var valido=false;
    	for(var i=0; i<<%=i%>;i++){
    		if(valor==tiposDocument[i]) {
    			valido=true;
    			break;
    		}
    	}
    	if(!valido) {
    		form.docOnlineI.checked=false;
	    	updateCheck(form.docOnlineI,form.docOnline);
	    	alert("<%=rb.getString("E0130")%>");
    	}
    	
		if(typeof(form.nameFile.value)!='undefined' && form.nameFile.value!="" ) {
			alert("El archivo en linea no debe tener un archivo");
    		form.docOnlineI.checked=false;
		}
    	
    	if(<%=isModuloDigital%>) {
	    		document.getElementById("tab_0").style.display="";
    			document.getElementById("tab_1").style.display="none";
				document.getElementById("tab_file").style.display="none";
    	}
    }
    
    function cancelar(){
    	var formaSelection = document.getElementById("Selection");
        formaSelection.submit();        
    }
    
    function searchDigital() {
    	window.open("searchDigital.do?accion=1","info");
    }
    
    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    
    
    window.onmove="alert(1)";
    
    function regresar(){
		location.href="newDocument.jsp?publicado=<%=docPublic%>";
    }
    
    
    function abre(obj) {
    	var arc = '<%=String.valueOf(request.getAttribute("nameFileLog"))%>';
    	
    	for(var i=1;i<=10;i++) {
    		try{
	    		document.getElementById("log"+i).className="";
	    		document.getElementById("tab"+i).style.display="none";
    		}catch(e){break;}
    	}
    	
    	obj.className="actual";

    	var cad="logs/";
    	if(obj.id=="log1") {
    		document.getElementById("tab1").style.display="";
    	} else if(obj.id=="log2") {
    		document.getElementById("tab2").style.display="";
    	} else if(obj.id=="log3") {
    		document.getElementById("tab3").style.display="";
    	} else if(obj.id=="log4") {
    		document.getElementById("tab4").style.display="";
    	} else if(obj.id=="log5") {
    		document.getElementById("tab5").style.display="";
    	} else if(obj.id=="log6") {
       		<%if(!isModuloDigital){%>
    		document.getElementById("tab6").style.display="";
    		<%}%>
    	}
   		//window.open(cad,'log');
    		
    }
    
	function removeChildrenFromNode(e) {
	    if(!e) {
	        return false;
	    }
	    if(typeof(e)=='string') {
	        e = xGetElementById(e);
	    }
	    while (e.hasChildNodes()) {
	        e.removeChild(e.firstChild);
	    }
	    return true;
	}   

	function nombreSugerido() {
		if(document.newDocument.nameFile && document.newDocument.nameFile.value!="" && document.newDocument.nameDocument.value=="") {
			var arr = document.newDocument.nameFile.value.replace(/\134/g,'/').split("/");
			var pos = arr[arr.length-1].indexOf(".");
			if(pos!=-1){
				document.newDocument.nameDocument.value=arr[arr.length-1].substring(0,pos);
			} else {
				document.newDocument.nameDocument.value=arr[arr.length-1];
			}
			try{
    		document.newDocument.docOnlineI.checked=false;
    		document.newDocument.docOnlineI.disabled;
    		} catch(e){}
		}
	} 

	function getCheckedRadioValue(obj) {
		for(var x=0; x<obj.length; x++) {
			if(obj[x].checked) {
				return obj[x].value;
			}
		}	
	}
	function reloadScanner() {
		var cad = "modo="+getCheckedRadioValue(document.newDocument.modo);
		cad += "&lado="+getCheckedRadioValue(document.newDocument.lado);
		cad += "&ppp="+document.newDocument.ppp.value;
		cad += "&panel="+document.newDocument.panel.value;
		cad += "&aplicar=true";
		window.open('<%=basePath%>digitalizarLoad.do?'+cad,'scanner');
	}
	function setDimensionScreen() {
	    if (screen.availWidth) {
	        winWidth = screen.availWidth;
	    }
	    if (screen.availHeight){
	        winHeight = screen.availHeight - 100;
	    }
	}
	
	function abrirVentanaFull(pagina,nameWin) {
	    var hWnd = null;
	    setDimensionScreen();
	    var left = 0;
	    var top = 0;
	    hWnd = window.open(pagina, nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+winWidth+",height="+winHeight+",left="+left+",top="+top);
	}

	function rechazar(){
		// el destino
		var destino = "0";
		if(!document.newDocument.destino[0].checked && !document.newDocument.destino[1].checked){
			alert("Elija un destinatario en el rechazo");
			return;
		} else if(document.newDocument.destino[0].checked) {
			destino=document.newDocument.destino[0].value;
		} else {
			destino=document.newDocument.destino[1].value;
		}
		
		// el comentario
		document.newDocument.comentario.value=document.newDocument.comentario.value.replace(/'/g,'');
		document.newDocument.comentario.value=document.newDocument.comentario.value.trim();
		if(document.newDocument.comentario.value==''){
			alert("Escriba un comentario que haga referencia al rechazo de este archivo");
			return;
		}
		verPDF(document.newDocument.idDigital.value,false,true,document.newDocument.comentario.value,destino);
	}

	function eliminar(){
		verPDF(document.newDocument.idDigital.value,true,false,"","");
	}
	
	function anterior(){
		verPDF(document.newDocument.anterior.value,false,false,"","");
	}
	function siguiente(){
		verPDF(document.newDocument.siguiente.value,false,false,"","");
	}
	
	window.http = null;
	function verPDF(idDigital,eliminar,rechazar,comentario,status) {
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http = new XMLHttpRequest();
		}
		window.http.open("POST", "upploadFileAjaxFrame.do?idDigital="+idDigital+"&eliminar="+eliminar+"&status="+status+"&rechazar="+rechazar+"&comentario="+comentario);
		window.http.onreadystatechange=function() {
		  try {
			  if(window.http.readyState == 4) {
			  	if(window.http.responseText=='null') {
			  		searchDigital();
			  		return;
			  	}
			  	var arr = eval(window.http.responseText);

			  	if(document.newDocument.idDigital.value==arr[0]){
			  		return;
			  	}
		  		document.newDocument.idDigital.value=arr.idDigital;
			  	document.newDocument.anterior.value=arr.anterior;
		  		document.newDocument.siguiente.value=arr.siguiente;
		  		document.getElementById("idDigitalName").innerHTML='( '+arr.idDigitalName+' )';
			  	
			  	if(document.getElementById('btnanterior')){
			  		document.getElementById('btnanterior').style.display=(isNaN(parseInt(document.newDocument.anterior.value))?"none":"");
			  	}
			  	if(document.getElementById('btnsiguiente')) {
			  		document.getElementById('btnsiguiente').style.display=(isNaN(parseInt(document.newDocument.siguiente.value))?"none":"");
			  	}
				window.open('<%=ToolsHTML.getBasePath(request)%>upploadFileFrame.do?idDigital='+document.newDocument.idDigital.value+'&pdf=true','digitalLeft');
				
				// desactivamos la redigitalizacion
				if(arr.idStatusDigital==<%=Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN%>){
					document.getElementById("redigitalizar").style.display="";
				} else {
					document.getElementById("redigitalizar").style.display="none";
				}
			  }
		  } catch(e){
		  }
		}
		window.http.send(null);
	}
	
	
	window.http2 = null;
	function buscarTipo(idTypeDoc) {
		if(navigator.appName == "Microsoft Internet Explorer") {
		  window.http2 = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  window.http2 = new XMLHttpRequest();
		}
		window.http2.open("POST", "findTypeDocumentAjax.do?idTypeDoc="+idTypeDoc);
		window.http2.onreadystatechange=function() {
		  //try {
			  if(window.http2.readyState == 4) {
			  	var tipo = eval(window.http2.responseText);
			  	
			  	if(tipo.idNode!='null'){
        			document.newDocument.idRout.value=tipo.idNode;
        		}
			  	if(tipo.idNodeName!='null'){
        			document.newDocument.nameRout.value=tipo.idNodeName;
        		}
			  	if(tipo.ownerTypeDocUserName!='null'){
        			document.newDocument.owner.value=tipo.ownerTypeDocUserName;
        		}
			  	
			  }
		  //} catch(e){
		  //}
		}
		window.http2.send(null);
	}
	
	
</script>

<script language="javascript">
	var extensionDouble="";
	<%
	try {
		// validar o no el navegador - para este caso ie7
		String valor = HandlerParameters.PARAMETROS.getFileDoubleVersion();
		out.println("extensionDouble=\"".concat(valor==null?"":valor.toLowerCase()).concat("\";"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	%>
</script>

</head>

<body class="bodyInternas" >

<html:form action="/upploadFile.do" method="post" enctype="multipart/form-data" >
    <input type="hidden" name="cmd" property="upFile"/>
    <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
    <input type="hidden" name="isFileScanned" value="<%=isModuloDigital%>"/>
    <input type="hidden" name="isCheckerData" value="false"/>
    <input type="hidden" name="idDigital" value="<%=idDigital%>"/>
    <input type="hidden" name="isSendToFlexWF" value="false"/>
    <input type="hidden" name="actNumber" value="0"/>
    <input type="hidden" name="anterior" value="<%=session.getAttribute("anterior")%>"/>
    <input type="hidden" name="siguiente" value="<%=session.getAttribute("siguiente")%>"/>
    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
        <html:hidden property="idNodeParent" value="<%=dataForm.getIdNodeParent()%>"/>
    </logic:notEqual>
    <table align="center" border="0" width="100%">
        <tr class="none">
            <td class="pagesTitle" colspan="2" >
                <%=rb.getString("btn.addDocument")%>
            </td>
        </tr>
        <tr>
            <td colspan="2" valign="top">
                <%
                    String style = "clsTableTitle";
                    String height = "21";
                    if (rout!=null&&rout.length() > 104) {
                        style = "clsTableTitle2";
                        height = "42";
                    }
                %>
                <table class="<%=style%>" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="<%=height%>">
                                <%=rb.getString("cbs.location") + ": "+rout%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr>
            <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;width:500" valign="middle">
                <%--rb.getString("doc.title")--%>
                <%=rb.getString("btn.addDocument")%>
                <span id="idDigitalName">
		        <%if(isModuloDigital){%>
					( <%DigitalTO dg = (DigitalTO)session.getAttribute("digitalTO");
					  if(dg.getNameFile()!=null && !dg.getNameFile().equals("")){
					  	out.println(dg.getNameFile());
					  } else {
					  	out.println(dg.getNameFileDisk());
					  }
					  %> )
				<%}%>
				</span>
            </td>
        </tr>

        <%if(isModuloDigital){%>
        <tr>
            <td class="titleLeft" height="25" width="200px" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.type")%>:
            </td>
            <td>
            	<table border="0">
            	<tr>
            	<td>
                <html:select property="typeDocument" styleClass="classText" style="width: 250px" onchange="validarOnLine(this.form,this.value)">
                    <logic:present name="typesDocuments" scope="session">
                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                            <html:option value="<%=bean.getId()%>">
                                <%=bean.getDescript()%>
                                <script>
                                	tipoDoc[<%=bean.getId()%>]=<%=bean.getSendToFlexWF()%>;
                                	tipoDocActNumber[<%=bean.getId()%>]=<%=bean.getActNumber()%>;
                                </script>
                            </html:option>
                        </logic:iterate>
                    </logic:present>
                    <logic:notPresent name="typesDocuments" scope="session">
                        <%=rb.getString("E0006")%>
                    </logic:notPresent>
                </html:select>
                </td>
        		<%if(isModuloDigital){%>
                <td id="redigitalizar" style="display:<%=digitalTO.getIdStatusDigital().equals(Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN)?"":"none"%>">
	            <iframe name="scanner" id="scanner" src="digitalizar.jsp" style="height:20px;width:300px;" FRAMEBORDER="0"></iframe>
	            </td>
	            <%}%>
	            </table>
            </td>
        </tr>
        <%}%>
		
		<tr style="display:<%=isModuloDigital?"":"none"%>">
            <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
	            <%=rb.getString("sch.arbol")%>
            </td>
            <td class="td_gris_l">
                 <input type="text" name="nameRout"  style="width:155px;height: 19px" value="<%=session.getAttribute("idNodeSelectedName")!=null?session.getAttribute("idNodeSelectedName"):""%>"/>

                &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;"/>
                <input type="hidden" name="idRout" value="<%=session.getAttribute("idNodeSelected")!=null?session.getAttribute("idNodeSelected"):""%>"/>
                
            </td>
		</tr>
        

        <tr id="tab_file" style="display:<%=isModuloDigital?"none":""%>">
            <!-- SIMOPN INICIO 13 DE JUNIO 2005 -->
            <logic:notEqual value="0" name="newDocument" property="docOnline">
                <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.upFile")%>:
                </td>
                <td class="td_gris_l" >
	            	<table cellspacing="0" cellpadding="0" border="0">
	            	<tr>
	            		<td>
		                    <html:file property="nameFile" styleClass="classText" value="c:/temp/tmp.pdf" style="width:660;" onchange="nombreSugerido()" onkeyup="nombreSugerido()"/>
                    	</td>
                    	<td>&nbsp;&nbsp;
                    	</td>
                    	<td>
                    		<%if(!isModuloDigital){%>
        		            <iframe name="scanner" id="scanner" src="digitalizar.jsp" style="height:22px;width:600px;" FRAMEBORDER="0"></iframe>
        		            <%}%>
        		        </td>
        		    </tr>
        		    </table>
                </td>
            </logic:notEqual>
            <!-- SIMON 13 DE JUNIO 2005 FIN -->
        </tr>
        
        <tr>
            <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("cbs.name")%>:
            </td>
            <td class="td_gris_l">
            	<html:text property="nameDocument" size="110" maxlength="1000" styleClass="classText" onclick="this.select();"/>
            </td>
        </tr>
        
        <tr>
            <td class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("cbs.owner")%>:
            </td>
            <td>
                <html:select property="owner" styleClass="classText" style="width: 250px">
                    <logic:present name="userSystem" scope="session" >
                        <logic:present name="showCharge" scope="session">
                            <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                <html:option value="<%=bean.getId()%>">
                                    <%=bean.getAditionalInfo()%> ( <%=bean.getDescript()%> )
                                </html:option>
                            </logic:iterate>
                        </logic:present>
                        <logic:notPresent name="showCharge" scope="session">
                            <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                <html:option value="<%=bean.getId()%>">
                                    <%=bean.getDescript()%> ( <%=bean.getAditionalInfo()%> )
                                </html:option>
                            </logic:iterate>
                        </logic:notPresent>
                    </logic:present>
                </html:select>
            </td>
        </tr>
        
        <tr>
            <td class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.prefix")%>:
            </td>
            <td>
                <html:text property="prefix" styleClass="classText" disabled="true" size="39"/>
            </td>
        </tr>

        <%
            byte typeMay = 0;
            byte typeMin = 0;
            BaseDocumentForm forma = (BaseDocumentForm)session.getAttribute("newDocument");
            request.setAttribute("doc",forma);
            forma.setApproved(forma.getApproved()!=null?forma.getApproved():"0");
            forma.setDocPublic(docPublic);
        %>
            <%
            	String a1 = forma.getMayorVer();
            	String a2 = forma.getMinorVer();
                validDatePublic = true;
                if (ToolsHTML.isNumeric(forma.getMayorVer())) {
                    try {
                        forma.setMayorVer(DesigeConf.getProperty("begin.VersionApproved"));
                    } catch (Exception ex) {
                        forma.setMayorVer("1");
                    }
                } else {
                    forma.setMayorVer("A");
                    typeMay = 1;
                }
                if (ToolsHTML.isNumeric(forma.getMinorVer())) {
                    forma.setMinorVer("0");
                } else {
                    forma.setMinorVer("");
                    typeMin = 1;
                }
                if(a1!=null && !a1.trim().equals("") && !a1.trim().equals("0")) {
                	forma.setMayorVer(a1);
                	forma.setMinorVer(a2);
                }
                
            %>
        <tr>
            <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.Ver")%>:
            </td>
            <td class="td_gris_l">
                <logic:equal value="" name="newDocument" property="minorVer">
                    <%--<html:text property="mayorVer" size="4" styleClass="classText" onchange="valVersion(this.value,<%=typeMay%>,'La versión mayor es inválida')"/>.<html:text property="minorVer" size="4" styleClass="classText" readonly="true" onchange="valVersion(this.value,'<%=typeMin%>','La versión menor es inválida')"/>--%>
                    <input type="text" name="mayorVer" size="4" value="<%=forma.getMayorVer()%>" onchange="valVersion(this.value,'<%=typeMay%>','<%=rb.getString("E0114")%>')" class="classText">.<input type="text" name="minorVer" readonly="true" size="4" value="<%=forma.getMinorVer()%>" onchange="valVersion(this.value,'<%=typeMin%>','<%=rb.getString("E0115")%>')" class="classText">
                </logic:equal>
                <logic:notEqual value="" name="newDocument" property="minorVer">
                    <%--<html:text property="mayorVer" size="4" styleClass="classText" onchange="valVersion(this.value,'<%=typeMay%>','La versión mayor es inválida')"/>.<html:text property="minorVer" size="4" styleClass="classText" onchange="valVersion(this.value,'<%=typeMin%>','La versión menor es inválida')"/>--%>
                    <input type="text" name="mayorVer" size="4" value="<%=forma.getMayorVer()%>" onchange="valVersion(this.value,'<%=typeMay%>','<%=rb.getString("E0114")%>')" class="classText">.<input type="text" name="minorVer" size="4" value="<%=forma.getMinorVer()%>" onchange="valVersion(this.value,'<%=typeMin%>','<%=rb.getString("E0115")%>')" class="classText">
                </logic:notEqual>

            </td>
        </tr>

        <logic:present scope="session" name="borradorCorrelativo">
            <logic:equal value="1" name="borradorCorrelativo" scope="session">
                <%
                    swborradorCorrelativo = true;
                %>
              <tr>
               <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                  <%=rb.getString("doc.number")%>:
               </td>
               <td>
                 <html:text property="number"  disabled="false" styleClass="classText"/>
               </td>
            </tr>
            </logic:equal>
        </logic:present>

             <!--Genera el link automatico para los documentos, el cero es un uno negado, logica inversa.-->
             <html:hidden property="checkactiveFactory" value="0"/>
            <%
                validDatePublic = true;
            %>
        <logic:notPresent scope="session" name="borradorCorrelativo">
        <tr>
           <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
              <%=rb.getString("doc.number")%>:
           </td>
           <td>
             <html:text property="number"  disabled="false" styleClass="classText"/>
           </td>
        </tr>
        </logic:notPresent>
        
        <%if(!isModuloDigital){%>
        <tr>
            <td class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.type")%>:
            </td>
            <td>
                <html:select property="typeDocument" styleClass="classText" style="width: 250px" onchange="validarOnLine(this.form,this.value)">
                    <logic:present name="typesDocuments" scope="session">
                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                            <html:option value="<%=bean.getId()%>">
                                <%=bean.getDescript()%>
                                <script>
                                	tipoDoc[<%=bean.getId()%>]=<%=bean.getSendToFlexWF()%>;
                                	tipoDocActNumber[<%=bean.getId()%>]=<%=bean.getActNumber()%>;
                                </script>
                            </html:option>
                        </logic:iterate>
                    </logic:present>
                    <logic:notPresent name="typesDocuments" scope="session">
                        <%=rb.getString("E0006")%>
                    </logic:notPresent>
                </html:select>
            </td>
        </tr>
        <%}%>

        <tr id="trPublicar" style="display:<%= session.getAttribute("publicEraser")!=null && session.getAttribute("publicEraser").equals("true")?"":"none"%>">
            <td  width="180px" class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.public")%>:
            </td>
            <td width="100px">
               <%=ToolsHTML.showCheckBox("docPublicI","updateCheckPublic(this.form.docPublicI,this.form.docPublic,true)",Integer.parseInt(forma.getDocPublic()),0,false)%>
               <html:hidden property="docPublic" value="<%=forma.getDocPublic()%>"/>
            </td>
        </tr>

        <tr id="filaFecha" style="display:none">
            <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.approved")%>:
            </td>
            <td class="td_gris_l">
                <table cellpadding="0" cellspacing="0" border="0">
			        <tr>
			            <td width="250px">
			                <%=ToolsHTML.showCheckBox("approvedI","updateCheck(this.form.approvedI,this.form.approved,this.form.dateApproved)",Integer.parseInt(forma.getApproved()),0,true)%>
			                <input type="hidden" name="approved" value="<%=forma.getApproved()%>"/><%=rb.getString("wf.dateDoc")%>:<html:text property="dateApproved" readonly="true" maxlength="10" size="12" />
			                <span id="calendario"></span>
			                <a href="#" onclick='showCalendar(document.newDocument.dateApproved,"calendario")' ><img src="images/calendario2.gif" border="1" ></a>
			                <%if (validDatePublic) {%>
				                <a href="#" onclick='document.newDocument.dateApproved.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.approved.value="1";' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
			                <%} else {%>
				                <a href="#" onclick='document.newDocument.dateApproved.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.approved.value="1";document.newDocument.approvedI.checked=false; ' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
			                <%}%>
			                <input type=hidden name="initialYear" value='<%= ((request.getParameter("initialYear") != null) ? request.getParameter("initialYear") : "")%>'>
			                <input type=hidden name="initialMonth" value='<%= ((request.getParameter("initialMonth") != null) ? request.getParameter("initialMonth") : "")%>'>
			                <input type=hidden name="initialDay" value='<%= ((request.getParameter("initialDay") != null) ? request.getParameter("initialDay") : "")%>' >
			            </td>
			            <td>&nbsp;&nbsp;
			            </td>
			            <td width="80px" height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
			                <%=rb.getString("doc.expires")%>:
			            </td>
			            <td class="td_gris_l">
			                <%=ToolsHTML.showCheckBox("expiresI","updateCheck(this.form.expiresI,this.form.expires,this.form.dateExpires)",1,0,false)%>
			               <html:hidden property="expires" value="1"/><%=rb.getString("wf.dateDoc")%>:<html:text property="dateExpires" readonly="true" maxlength="10" size="12"/>
			                <span id="calendario2"></span>
			                <a href="#" onclick='showCalendar(document.newDocument.dateExpires,"calendario2")' ><img src="images/calendario2.gif" border="1"  ></a>
			                <a href="#" onclick='document.newDocument.expiresI.check = false;document.newDocument.dateExpires.value=""; document.newDocument.initialDay.value="";  document.newDocument.initialMonth.value="";  document.newDocument.initialYear.value="";document.newDocument.expires.value="";document.newDocument.expiresI.checked=false; ' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
			            </td>
			        </tr>
			    </table>
            </td>
        </tr>

        <tr style="display:<%=!isModuloDigital?"":"none"%>">
            <td width="180px" class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.toForFiles")%>:
            </td>
            <td width="100px">
                <%=ToolsHTML.showCheckBox("toForFilesI","updateCheck(this.form.toForFilesI,this.form.toForFiles)",forma.getToForFiles(),"0")%>
                <html:hidden property="toForFiles"  value="<%=forma.getToForFiles()%>"/>
            </td>
        </tr>
        
       <logic:present name="docInline" scope="session">
        <tr style="display:<%=(!isModuloDigital?"":"none")%>">
            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.online")%>:
            </td>
            <td> 
                <%=ToolsHTML.showCheckBox("docOnlineI","updateCheck(this.form.docOnlineI,this.form.docOnline)",1,0,false)%>
               <html:hidden property="docOnline" value="1"/>
            </td>
        </tr>
       </logic:present>
        

	    <logic:equal value="true" name="showNorms" >
        <tr style="display:<%=!isModuloDigital?"":"none"%>">
            <td class="titleLeft" height="25" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.normISO")%>:
            </td>
            <td>
                <html:select property="normISO" styleClass="classText" style="width: 675px">
                    <logic:present name="norms" scope="session">
                            <html:option value="0">
                                --------------------------------------------------------------------------------------------------------------------------------------------------------
                            </html:option>
                        <logic:iterate id="bean" name="norms" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                            <html:option value="<%=bean.getId()%>">
                                <%=bean.getDescript()%>
                            </html:option>
                        </logic:iterate>
                    </logic:present>
                </html:select>
            </td>
        </tr>
	    </logic:equal>
	    <logic:notEqual value="true" name="showNorms" >
	        <html:hidden property="normISO" value="0"/>
	    </logic:notEqual>
    
        <tr id="paralelo" style="display:none">
			<!-- INI: Permite cargar el archivo alterno para ser visualizado -->
            <logic:notEqual value="0" name="newDocument" property="docOnline">
                <td height="25" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.upFile2")%>:
                </td>
                <td class="td_gris_l">
                    <html:file property="nameFileParalelo" styleClass="classText" style="width:300"/>
                </td>
            </logic:notEqual>
			<!-- FIN: Permite cargar el archivo alterno para ser visualizado -->
        </tr>
        
        <html:hidden property="docRelations"/>
        <tr style="display:<%=!isModuloDigital?"":"none"%>">
            <td height="25" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.links")%>:
             </td>
            <td>
                <a href="javascript:showWindow('loadAllDocuments.do','docRelations','newDocument',document.newDocument.docRelations.value,500,400);" class="ahref_b" target="_self">
                    <%=rb.getString("doc.links")%>
                </a>
            </td>
        </tr>

        <tr id="resultado">
        	<td colspan="2">
				<ul>
					<li><a id="log1" href="#" onclick="abre(this);"><span id="tab_0"><%=rb.getString("wf.comments")%></span><span id="tab_1" style="display:none"><%=rb.getString("wf.contenido")%></span></a></li>
					<li><a id="log2" href="#" onclick="abre(this);" class="actual"><%=rb.getString("doc.url")%></a></li>
					<li><a id="log3" href="#" onclick="abre(this);"><%=rb.getString("doc.keys")%></a></li>
					<li><a id="log4" href="#" onclick="abre(this);"><%=rb.getString("doc.descript")%></a></li>
					<li><a id="log5" href="#" onclick="abre(this);"><%=rb.getString("doc.other")%></a></li>
					<%if(!isModuloDigital){%>
					<li><a id="log6" href="#" onclick="abre(this);"><%=rb.getString("doc.escaner")%></a></li>
					<%}%>
				</ul>
				
				<div id="contenido" style="width:100%"> 
					<!--<iframe name="log" id="log" width="100%" height="250" src="logs/<%=String.valueOf(request.getAttribute("nameFileLog"))%>_MAL.txt" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>-->
		            <div id="tab1" class="fondoEditor" width="100%" >
		            	<%if(isModuloDigital){%>
   	                        <html:textarea property="comments" cols="100" rows="3" styleClass="classText" style="width:99%;height:145px;" />
		                <%} else {%>
   	                        <html:textarea property="comments" style="display:none"/>
		                <jsp:include page="richedit.jsp" > 
		                	<jsp:param name="richedit" value="richedit"/>
		                	<jsp:param name="height" value="100"/>
                       	  	<jsp:param name="defaultValue" value="<%=forma.getComments()%>" />
		                </jsp:include>
		                <%}%>
		                
		            </div>
		            <div id="tab2" class="fondoEditor" width="100%" colspan="2" style="display:none" >
		            	<html:textarea property="URL" cols="100" rows="3" styleClass="classText" style="width:99%;height:145px;" />
		            </div>
		            <div id="tab3" class="fondoEditor" width="100%" style="display:none">
		            	<html:textarea property="keys" cols="100" rows="3" styleClass="classText" style="width:99%;height:145px;" />
		            </div>
		            <div id="tab4" class="fondoEditor" width="100%" style="display:none" >
		            	<html:textarea property="descript" cols="100" rows="3" styleClass="classText" style="width:99%;height:145px;" />
		            </div>
		            <div id="tab5" class="fondoEditor" width="100%" style="display:none" >
		                <table cellpadding="0" cellspacing="2" border="0">
		<!-- INI CAMPOS ADICIONALES -->
		<!-- FIN CAMPOS ADICIONALES -->
						</table>
		            </div>
               		<%if(!isModuloDigital){%>
		            <div id="tab6" class="fondoEditor"  width="100%" style="display:none;" >
		            	<table border="0" width="100%" cellspacing="10">
		            		<tr>
		            			<td width="50%"> 
		            				Modo<br/>
		            				<input type="radio" name="modo" value="0" <%=(modo==0?"Checked":"")%>>Blanco y Negro
									&nbsp;&nbsp;&nbsp;
		            				<input type="radio" name="modo" value="1" <%=(modo==1?"Checked":"")%>>Escala de Grises
									&nbsp;&nbsp;&nbsp;
		            				<input type="radio" name="modo" value="2" <%=(modo==2?"Checked":"")%>>Color
		            			</td>
		            			<td>
		            				Puntos por Pulgada<br/>
		            				<select style="width:350px;" name="ppp"  value="<%=ppp%>">
		            					<option value="100" <%=ppp==100?"selected":""%>>100 ppp</option>
		            					<option value="150" <%=ppp==150?"selected":""%>>150 ppp</option>
		            					<option value="200" <%=ppp==200?"selected":""%>>200 ppp</option>
		            					<option value="300" <%=ppp==300?"selected":""%>>300 ppp</option>
		            					<option value="400" <%=ppp==400?"selected":""%>>400 ppp</option>
		            					<option value="600" <%=ppp==600?"selected":""%>>600 ppp</option>
		            				</select>
		            			</td>
		            			<td valign="middle">
		            				<input type="button" value="Aplicar" onclick="reloadScanner()"/>
		            			</td>
		            		</tr>
		            		<tr>
		            			<td width="50%">
		            				Lado de escaneado<br/>
		            				<input type="radio" name="lado" value="0" <%=(lado==0?"Checked":"")%>>Una cara
									&nbsp;&nbsp;&nbsp;
		            				<input type="radio" name="lado" value="1" <%=(lado==1?"Checked":"")%>>Doble cara
		            			</td>
		            			<td>
		            				Mostrar panel del escaner<br/>
		            				<select style="width:350px;" name="panel" value="<%=panel%>">
		            					<option value="0" <%=panel==0?"selected":""%>>No</option>
		            					<option value="1" <%=panel==1?"selected":""%>>Si</option>
		            				</select>
		            			</td>
		            			<td>
		            				&nbsp;
		            			</td>
		            		</tr>
		            	</table>
		            </div>
		            <%}%>
				</div>		            	
        	</td>
        </tr>
        
        <%--<%if (validDatePublic){%>--%>
        <!--<tr>-->
              <!--<td class="titleLeft" height="25" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">-->
                 <%--<%=rb.getString("wonderware.activefactory")%>--%>
              <!--</td>-->
              <!--<td class="td_gris_l">-->
                  <%--<%=ToolsHTML.showCheckBox("checkactiveFactoryI","updateCheck(this.form.checkactiveFactoryI,this.form.checkactiveFactory,this.form.checkactiveFactory)",!validDatePublic?0:1,0,readOnly)%>--%>
                  <%--<html:hidden property="checkactiveFactory"/>--%>
                 <%----%>
<%----%>
              <!--</td>-->
        <!--</tr>-->
        <%--<%}%>--%>


        <script language="JavaScript" event="onload" for="window">
        	if(document.newDocument.richedit){
        		document.newDocument.richedit.value = document.newDocument.comments.innerText;
        	}
        	abre(document.getElementById("log1"));
        	
        	<%if(isModuloDigital && digitalTO!=null){%>
				// cargamos los datos digitados

        		document.newDocument.typeDocument.value=<%=digitalTO.getType()%>;
        		document.newDocument.idRout.value=<%=digitalTO.getIdNode()%>;
        		document.newDocument.nameRout.value='<%=ToolsHTML.getNodeName(String.valueOf(digitalTO.getIdNode()))%>';
				document.newDocument.nameDocument.value='<%=digitalTO.getNameDocument()%>';
        		document.newDocument.owner.value='<%=ToolsHTML.getUserName(String.valueOf(digitalTO.getOwnerTypeDoc()))%>';
        		<%if(digitalTO.getVersionMayor()!=null && !digitalTO.getVersionMayor().trim().equals("")){%>
        			document.newDocument.mayorVer.value='<%=digitalTO.getVersionMayor()%>';
        		<%}%>
        		<%if(digitalTO.getVersionMenor()!=null && !digitalTO.getVersionMenor().trim().equals("")){%>
        		document.newDocument.minorVer.value='<%=digitalTO.getVersionMenor()%>';
        		<%}%>
				document.newDocument.number.value='<%=digitalTO.getCodigo()%>';
				document.newDocument.docPublic.value='<%=digitalTO.getPublicado()%>';
				document.newDocument.docPublicI.checked=(document.newDocument.docPublic.value=='0');
				document.newDocument.expires.value='<%=digitalTO.getExpira()%>';
				document.newDocument.expiresI.checked=(document.newDocument.expires.value=='0');
				document.newDocument.dateApproved.value='<%=digitalTO.getFechaPublicacion()!=null?digitalTO.getFechaPublicacionToShow():""%>';
				document.newDocument.dateExpires.value='<%=digitalTO.getFechaVencimiento()!=null?digitalTO.getFechaVencimientoToShow():""%>';
				document.newDocument.comments.value='<%=digitalTO.getComentarios()%>';
				document.newDocument.URL.value='<%=digitalTO.getUrl()%>';
				document.newDocument.keys.value='<%=digitalTO.getPalabrasClaves()%>';
				document.newDocument.descript.value='<%=digitalTO.getDescripcion()%>';
        		<%for(int k=0;k<conf.size();k++){
				    obj=(DocumentForm)conf.get(k);
					%>try{document.newDocument.<%=obj.getId()%>.value='<%=digitalTO.getOtrosDatos(obj.getId())%>';}catch(e){}
                      <%}
        	    }
	        	if(onLoad!=null) {
		        	onLoad=onLoad.replaceAll("onLoad=\"","");
		        	onLoad=onLoad.replaceAll("\"","");
	        		onLoad=String.valueOf(onLoad).concat(";");
	        		out.println(onLoad);
	        	}%>
        </script>
        <tr>
            <td colspan="2" align="center">
               <!-- SIMOPN INICIO 13 DE JUNIO 2005 -->
               <logic:notEqual name="newDocument" property="docOnline" value="0">
               		<%if(digitalTO!=null){
	               	   boolean isIguales = digitalTO.getChecker().equals(digitalTO.getTypesetter());
	               	   boolean isVerificar = String.valueOf(usuario.getIdPerson()).equals(digitalTO.getChecker()) && digitalTO.getIdStatusDigital().equals(Constants.STATUS_DIGITAL_POR_VERIFICAR);
	               	   isSave = (isIguales || isVerificar);
	               	   isSave=(!isModuloDigital?true:isSave);
	               	}%>
           			<span id='btnSave' style="display:<%=isSave?"":"none"%>">
              		<input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnsalvar" onClick="javascript:salvar(this.form,<%=validDatePublic%>,<%=typeMay%>,<%=typeMin%>,false);" />
              		</span>
           			<span id='btnSend' style="display:<%=isSave?"none":""%>">
              		<input type="button" class="boton" value="<%=rb.getString("btn.send")%>" name="btnsalvar2" onClick="javascript:salvar(this.form,<%=validDatePublic%>,<%=typeMay%>,<%=typeMin%>,true);" />
              		</span>
               </logic:notEqual>
               <logic:equal name="newDocument" property="docOnline" value="0">
                  <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnsalvar" onClick="javascript:salvar1(this.form,<%=validDatePublic%>,<%=typeMay%>,<%=typeMin%>);" />
               </logic:equal>
                <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                    <html:hidden property="idNodeParent" value="<%=dataForm.getIdNodeParent()%>"/>
                    &nbsp;
                </logic:notEqual>
                    &nbsp;
                  <input type="button" class="boton none" value="<%=rb.getString("btn.back")%>" name="btnregresar" onClick="javascript:regresar();" />
                    &nbsp;
                <%if(isModuloDigital){%>    
					<a id="btncancelar" class="boton botonLinkBack" onClick="eliminar()" > 
						<%=rb.getString("btn.delete")%>
					</a>
                    &nbsp;&nbsp;
					<a id="btncancelar" class="boton botonLinkBack" onClick="searchDigital()" > 
						<%=rb.getString("btn.cancel")%>
					</a>
                    &nbsp;&nbsp;
					<a id="btnanterior" class="boton botonLinkBack" onClick="anterior()" style="display:<%=(session.getAttribute("anterior")!=null && !session.getAttribute("anterior").equals("")?"":"none")%>">
						<%=rb.getString("btn.before")%>
					</a>
                    &nbsp;&nbsp;
					<a id="btnsiguiente" class="boton botonLinkBack" onClick="siguiente()" style="display:<%=(session.getAttribute("siguiente")!=null && !session.getAttribute("siguiente").equals("")?"":"none")%>" >
						<%=rb.getString("btn.after")%>
					</a>
				<%} else {%>
				<a id="btncancelar" class="boton botonLinkBack" onClick="cancelar()" >
					<%=rb.getString("btn.cancel")%>
				</a>
				<%}%>
            </td>
        </tr>
    	<%if(isModuloDigital){%>
        <tr style="display:<%=(digitalTO.getIdStatusDigital().equals(Constants.STATUS_DIGITAL_NUEVO)?"none":"")%>">
            <td colspan="2" align="left">
            	<fieldset>
            		<legend>Rechazar</legend>
            		<table border="0" width="100%">
            			<tr>
            				<td nowrap>
			            		<b>Destino :</b>
            				</td>
            				<td>
			            		<input type="radio" value="0" name="destino" checked> Digitalizador &nbsp;&nbsp;&nbsp;
			            		
			            		<span style="display:<%=!isSave?"none":""%>">
			            		<input type="radio" value="1" name="destino" > Transcriptor
			            		</span>
            				</td>
            				<td rowspan="2" align="center">
			              		<input type="button" class="boton" value="<%=rb.getString("btn.rejected")%>" name="btnsalvar3" onClick="javascript:rechazar();" />
            				</td>
            			</tr>
            			<tr>
            				<td nowrap>
			            		<b>Comentarios :</b>
            				</td>
            				<td>
			            		<input type="text" name="comentario" maxlength="200" size="70">
            				</td>
            			</tr>
            		</table>
            	</fieldset>
            </td>
        <%}%>
        </tr>
    </table>
</html:form>
<form name="Selection" id="Selection" action="loadStructMain.do">
    <input type="hidden" name="cmd" property="<%=SuperActionForm.cmdLoad%>"/>
    <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
</form>
</body>
</html>
<script type="text/javascript">
	updateCheckPublic(document.newDocument.docPublicI,document.newDocument.docPublic,true);
	<% String info2 = (String)session.getAttribute("info2");
	   if(info2!=null) {
	   		out.println("alert('"+info2+"');");	
	   }
	   session.removeAttribute("info2");
	%>
	document.newDocument.target='<%=isModuloDigital?"info":""%>';
	
</script>
