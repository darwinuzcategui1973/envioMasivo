<%@ page import="com.desige.webDocuments.utils.DesigeConf"%>
<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<SCRIPT LANGUAGE=JavaScript>
   <!--

   // -->
</SCRIPT>

<!--
 * Title: openRegister.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 14/05/2005 (NC) se modificó para optimizar el manejo de registros </li>
 *      <li> 08/06/2005 (SR) se cambia la dirección del registro por ("file:///url fisica del archivo.")</li>
 *      <li> 08/06/2005 (SR) se agrego la funcion java script extNoseEdita</li>
 *      <li> 08/06/2005 (SR) la funcion startWord, se tuvo que comentar la parte que no funcionaba. </li>
 *      <li> 08/06/2005 (SR) se trae el nombre del servidor a travez del archivo webdocuments.properties. </li>
 *      <li> 08/06/2005 (SR) se agrego el form showDocument para que no diera error en el script. </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..



 </ul>
-->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<html>
<head>


<%
    String onLoad = (String)session.getAttribute("onLoad");
    if (ToolsHTML.isEmptyOrNull(onLoad)) {
        onLoad = "openDocument()";
    }
%>
<title></title>
<jsp:include page="meta.jsp" /> 
<script language=javascript src="./estilo/funciones.js"></script>

<script>
    var nameFile = "<%=request.getAttribute("nameDoc")%>";
    var fichero = '<%=ToolsHTML.getServerName(request)%>';
    fichero = '\\\\' + fichero + '\\<%=com.desige.webDocuments.utils.ToolsHTML.getFolderCmp()%>\\' + nameFile;
    
    
	function setCookie(nombre,valor,tiempo){
		document.cookie=nombre+"="+valor+"; expires="+tiempo;
	}
	function getCookie(nombre) {
		var a;
		if(document.cookie.indexOf(nombre)!=-1){
			a = document.cookie.substring(document.cookie.indexOf( nombre + '=') + nombre.length + 1,document.cookie.length);
			if(a.indexOf(';') != -1)a = a.substring(0,a.indexOf(';'))
		}
		return a;
	} 
	
	function setIllustrator(){
			var c = getCookie("Illustrator");
			var ruta = prompt("Por favor, indique la ruta del ejecutable de Adobe Illustrator",typeof (c)!='undefined'?c:"Illustrator.exe");
			if(!ruta){
				window.close();
			}
			hacerCookie(ruta);
	}	
	
	function hacerCookie(ruta) {
		var d = new Date();
		d.setYear(d.getYear()+5);
		setCookie("Illustrator",ruta,d.toGMTString());
	}
	

    function extNoseEdita() {
        alert("Este archivo '" + nameFile + "' no es un documento editable");
    }

    function startWord() {
   		var myApp = new ActiveXObject("Word.Application");
       	if (myApp != null) {
           	var appWord = new ActiveXObject("Word.Application");
           	appWord.Documents.Open(fichero,null,false,null);
           	appWord.Visible = true;
        } else {
            alert("No se pudo cargar la aplicación");
        }
    }

    function startExcel() {
        var myApp = new ActiveXObject("Excel.Application");
        if (myApp != null) {
            myApp.Visible = false;
            parent.doc = myApp.Workbooks.Open(fichero);
            myApp.UserControl = true;
            myApp.Visible = true;
        } else {
            alert("No se pudo cargar la aplicación");
        }
//        sendForm();
    }

    function sendForm() {
        document.showDocument.submit();
    }

    function startPowerPoint(strFile) {
        var myApp = new ActiveXObject("PowerPoint.Application");
        if (myApp != null) {
            myApp.Visible = true;
            myApp.Presentations.Open(fichero);
        } else {
            alert("No se puede cargar la aplicacion");
        }
     }

    function startVisio(strFile) {
    // Alternatively, you can test for the Visio OCX drawing control
    // This isn't recommended, since it takes a while to
    // load the OCX, and the page becomes slow to load
    // var VisioOCX = new ActiveXObject('VisOCX.DrawingControl')

        //var myApp  = new ActiveXObject('VisioViewer.Viewer');
        var myApp  = new ActiveXObject('Visio.Application');
        if (myApp != null) {
            myApp.Visible = true;
            myApp.Documents.Open(fichero);
        } else {
            alert("No se puede cargar la aplicacion");
        }
     }


     function startcorel(strFile) {
        //var myApp = new ActiveXObject("CorelDRAW.Application");
         var myApp = new ActiveXObject("CorelDRAW.Application.12");

        //"Word.Application" Opens MS Word

        if (myApp != null) {
           
            myApp.Visible = true;
            myApp.XXX.Open(fichero);
            // myApp.Documents.Open(strFile) opens a .doc file, what is the MS Project equivalent of Documents.open to open a .mpp file
        }
    }
    function startProj(strFile) {
        var myApp = new ActiveXObject("MSProject.Application");
        //"Word.Application" Opens MS Word
        if (myApp != null) {
            myApp.Visible = true;
            myApp.XXX.Open(fichero);
            // myApp.Documents.Open(strFile) opens a .doc file, what is the MS Project equivalent of Documents.open to open a .mpp file
        }
    }
    
    function startIllustrator(strFile) {
        ruta = getCookie("Illustrator");
		if(!ruta) {
	    	setIllustrator();
	        ruta = getCookie("Illustrator");
	    }
        ruta = ruta.replace(/\134/g,'/');
        var Consola = new ActiveXObject("WScript.Shell");
        //alert('"C:\\Archivos de programa\\Adobe\\Illustrator 10 Tryout\\Support Files\\Contents\\Windows\\Illustrator.exe" "');
        //alert(ruta+" "+fichero+'"');
   	    Consola.run('"'+ruta+'" "'+fichero+'"');
    }
    
    

    function openDocument() {
    	var ext3 = fichero.substring(fichero.length-3);
    	var ext4 = fichero.substring(fichero.length-4);
    	if(ext3=="htm" || ext4=="html"  ) {
    		document.location.target="_blank";
    		document.location.href="enlinea.do?fichero="+fichero;
        } else {
            var Consola = new ActiveXObject("WScript.Shell");
    	    Consola.run(fichero);
        }
    }


</script>




</head>
<body onLoad="<%=onLoad%>">
    <form name="dataDocument" action="">
        <input type="hidden" name="nameDoc" value="<%=request.getAttribute("nameDoc")%>"/>
    </form>
    <form name="showDocument" action="">
    </form>
</body>
</html>
