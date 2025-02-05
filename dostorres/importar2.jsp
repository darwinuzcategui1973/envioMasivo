<!--
 * Title: importar2.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting <br/>
 * @version WebDocuments
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">

<script language="JavaScript">
	var http = null;
	var http2 = null;
    var actual=0;
    var fileLog="";

	function hacer(forma,etiqueta) {
		http = null;
		document.getElementById("resultado").style.display="none";
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http = new XMLHttpRequest();
		}
		http.open("POST", "importar.do?cmd="+etiqueta);
		http.onreadystatechange=function() {
		  try {
			  if(http.readyState == 4) {
				actual = "100";
				aumentar();
				fileLog = http.responseText;
				window.open("logs/"+fileLog+"_MAL.txt","log");
				document.getElementById("resultado").style.display="";
				document.forma.btnValid.disabled = false;
				document.forma.btnAccept.disabled = false;
				document.forma.btnBack.disabled = false;
				//alert(Etiqueta+" finalizada.");
			  }
		  } catch(e){
		  }
		}
		http.send(null);
		dibuja(forma,etiqueta);
	}
	
	function barra(forma,etiqueta) {
		//alert("barra");
		http2 = null;
		if(navigator.appName == "Microsoft Internet Explorer") {
		  http2 = new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  http2 = new XMLHttpRequest();
		}
		http2.open("POST", "importar.do?cmd=porcentaje");
		http2.onreadystatechange=function() {
		  try {
			  if(http2.readyState == 4) {
				actual = http2.responseText;
				aumentar();
			  }
		  } catch(e){
		  }
		}
		http2.send(null);
	}
	

    function validar(forma,etiqueta){
    	forma.cmd.value=etiqueta;
		forma.btnValid.disabled = true;
		forma.btnAccept.disabled = true;
		forma.btnBack.disabled = true;
		hacer(forma,etiqueta);
		//forma.submit();
    }
    
    function dibuja(forma,etiqueta) {
	//alert("dibuja");
    	actual=0
    	consulta(forma,etiqueta);
    }
    
    function consulta(forma,etiqueta) {
	//alert("consulta");
		barra(forma,etiqueta);
    }
    
    function aumentar() {
    	var obj = document.getElementById("barra");
    	var por = document.getElementById("porcentaje");
    	//actual=actual+1;
    	obj.style.width=actual+"%";
    	por.innerHTML=actual+"%";
    	if(actual<100){
	    	setTimeout("consulta()",1000);	
    	}
    }

    function abre(obj) {
    	var arc = fileLog;
    	
    	document.getElementById("log1").className="";
    	document.getElementById("log2").className="";
    	document.getElementById("log3").className="";
    	
    	obj.className="actual";
    	
    	var cad="logs/";
    	if(obj.id=="log1") {
    		cad=cad+arc+"_MAL.txt";
    	} else if(obj.id=="log2") {
    		cad=cad+arc+"_OK.txt";
    	} else if(obj.id=="log3") {
    		cad=cad+arc+"_LOG.txt";
    	}
   		window.open(cad,'log');
    		
    }
</script>

</head>

<body class="bodyInternas" style="margin:10;">
<fieldset style="padding:10;">
<legend style="font-size:13pt;font-family:Tahoma,Arial,Verdana;"><%=rb.getString("migr.data")%></legend>
<form method="POST" action="importar.do" name="forma">
<input type="hidden" name="cmd" value=""/>
        <table cellSpacing=0 cellPadding=0 align=center border=0  width="80%">
            <tr>
	            <td class="pagesTitle" align="center">
		        <table cellSpacing=3 cellPadding=5 align="center" border="0" width="100%">
		            <tr>
		               <td colspan="2" align="center" class="td_title_bc" nowrap>
		               		Equivalencias almacenadas
		               </td>
		            </tr>
		            <tr>
		            	<td colspan="2" align="center">
			            	<br>
		            		<input type="button" value="Validar datos" name="btnValid" onclick="javascript:validar(this.form,'validacion');">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="Importar documentos" name="btnAccept" onclick="javascript:validar(this.form,'migracion');">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="<%=rb.getString("btn.back")%>" name="btnBack" onclick="javascript:document.location='importar.do?cmd=retornarEquivalencias';">
		            	</td>
		            </tr>
		            <tr>
		            	<td colspan="2">
		            		<div style="width:100%;height:30;border:0px solid #efefef;padding:5px;">
			            		<div style="font-weight:bold;padding:3;text-align:center;border:1px solid #afafaf;">
				            		<div id="porcentaje" style="position:absolute;padding:4;z-index:1000;">0%</div>
				            		<div style="width:100%;height:30;border:1px solid #afafaf;text-align:left;z-index:10;">
					            		<div id="barra" style="position:absolute;width:0%;height:28;background-color:#c2d2e0;align:left;">
					            		<div>
				            		<div>
			            		</div>
		            		<div>
		            	</td>
		            </tr>
		            <tr id="resultado" style="display:none">
		            	<td colspan="2">
							<ul>
								<li><a id="log1" href="#" onclick="abre(this);" class="actual">Invalidas</a></li>
								<li><a id="log2" href="#" onclick="abre(this);">Validas</a></li>
								<li><a id="log3" href="#" onclick="abre(this);">Log</a></li>
							</ul>
							
							<div id="contenido" style="width:100%"> 
								<iframe name="log" id="log" width="100%" height="250" src="" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
							</div>		            	
		            	</td>
		            </tr>
		        </table>
                </td>
            </tr>
        </table>
<form>
</fieldset>
</body>
</html>
