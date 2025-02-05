<!--
 * Title: importar.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting <br/>
 * @version WebDocuments
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Search,
                 java.util.ArrayList,
                 sun.jdbc.rowset.CachedRowSet,
                 com.desige.webDocuments.files.forms.DocumentForm,
                 com.desige.webDocuments.utils.DesigeConf"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    Search bean = null;
    DocumentForm df = null;
    ArrayList listaTypeDoc = (ArrayList) request.getAttribute("listaTypeDoc");
    CachedRowSet listaFields = (CachedRowSet) request.getAttribute("listaFields");
%>
<html>
<head>
<title></title>
<style>
.botonSmall{
	cursor:pointer;
	border-left:1px solid #efefef;
	border-top:1px solid #efefef;
	border-right:1px solid #afafaf;
	border-bottom:1px solid #afafaf;
	margin:2px;
}
</style>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">

<script language="JavaScript">

    String.prototype.endsWith = function(str){return (this.match(str+"$")==str)}

    function validar(forma){
    	forma.nombreArchivo.value=forma.nombre.value;
        if (forma.nombreArchivo.value.length==0) {
            alert('<%=rb.getString("migr.msg1")%>');
            return false;
        } else if (!forma.nombreArchivo.value.toLowerCase().endsWith(".xls") && !forma.nombreArchivo.value.toLowerCase().endsWith(".txt") && !forma.nombreArchivo.value.toLowerCase().endsWith(".xml")) {
        	alert('<%=rb.getString("migr.tipFile")%>');
        	return false;
    	} else {
    		forma.btnAccept.disabled = true;
    		forma.submit();
    	}
    }
</script>

</head>

<body class="bodyInternas" style="margin:10;">
<fieldset style="padding:10;">
<legend style="font-size:13pt;font-family:Tahoma,Arial,Verdana;"><%=rb.getString("migr.data")%></legend>
<form method="POST"  enctype='multipart/form-data' action="importar.do?cmd=archivo">
        <table cellSpacing=0 cellPadding=0 align=center border=0 >
            <tr>
	            <td class="pagesTitle" align="center">
		        <table cellSpacing=3 cellPadding=5 align=center border="0" align="center">
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Archivo guía :
		               </td>
		               <td valign="middle" width="300" align="left">
		               		<input type="file" value="" maxlength="250" size="70" name="nombre">
		               		<input type="hidden" value="" maxlength="250" size="25" name="nombreArchivo">
		               </td>
		            </tr>
		            <tr>
		            	<td colspan="2" align="center">
			            	<br>
		            		<input type="button" value="<%=rb.getString("btn.ok")%>" name="btnAccept" onclick="javascript:validar(this.form);">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="<%=rb.getString("btn.back")%>" name="btnBack" onclick="javascript:document.location='administracionMain.do';">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="<%=rb.getString("migr.estr_folder")%>" name="btnRead" onclick="javascript:alert("<%=rb.getString("migr.enElab")%>);">
		            	</td>
		            </tr>
		            <tr>
		            	<td colspan="2">
		            		&nbsp;
		            	</td>
		            </tr>
		        </table>
                </td>
            </tr>
        </table>
<form>
</fieldset>
<fieldset style="padding:10;">
<legend style="font-size:13pt;font-family:Tahoma,Arial,Verdana;">Indices por Tipo de Documento</legend>
<table width="100%">
	<tr>
		<td colspan="4" class="td_title_bc">
			<p>
				<%=rb.getString("migr.msg1")%>				
				
				<%=rb.getString("migr.msg2")%>				
		</td>
	</tr>
	<tr>
		<td colspan="4" class="td_title_bc">
			<%=rb.getString("doc.typeLM")%><br>
			<select id="tipo" style="width:100%" onchange="cargar(this)">
				<option><%=rb.getString("migr.msg3")%></option>
				<%for(int i=0; listaTypeDoc!=null && i<listaTypeDoc.size(); i++){
					bean = (Search)listaTypeDoc.get(i);%>
					<option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
				<%}%>
			</select>
		</td>
	</tr>
	<tr>
		<td width="49%" class="td_title_bc">
			Campos Adicionales Definidos<br>
			<select id="campo" style="width:100%;" size="10">
			</select>
		</td>
		<td width="1%" class="td_title_bc">
			<img src="icons/arrow_right.png" class="botonSmall" onclick="derecha()" alt="<%=rb.getString("migr.selIndice")%>"><br>
			<img src="icons/arrow_left.png" class="botonSmall" onclick="izquierda()" alt="<%=rb.getString("migr.campIndice")%>"><br><br>
			<img src="icons/disk.png" class="botonSmall" onclick="almacenar()" alt="<%=rb.getString("btn.applyChanges")%>"><br>
		</td>
		<td width="50%" class="td_title_bc">
			<%=rb.getString("migr.campAdic")%><br>
			<select id="indice" style="width:100%" size="10">
			</select>
		</td>
	</tr>
</table>
</legend>
</body>
</html>
<script>
function derecha() {
	var campo = document.getElementById("campo");
	if(campo.selectedIndex>=0) {
		var indice = document.getElementById("indice");
		addOption(indice,campo.options[campo.selectedIndex].text,campo.value);
		campo.remove(campo.selectedIndex);
	} else {
		alert("<%=rb.getString("migr.msg4")%>");
	}
}
function izquierda() {
	var indice = document.getElementById("indice");
	if(indice.selectedIndex>=0) {
		var campo = document.getElementById("campo");
		addOption(campo,indice.options[indice.selectedIndex].text,indice.value);
		indice.remove(indice.selectedIndex);
	} else {
		alert("<%=rb.getString("migr.msg5")%>");
	}
}


function Campo(id,descri,tipo) {
	this.id=id;
	this.descri=descri;
	this.tipo=tipo;
}

var campos = new Array();
<%if(listaFields!=null){
  for(int i=0; listaFields.next(); i++){%>
	campos[<%=i%>]=new Campo('<%=listaFields.getString("id")%>','<%=listaFields.getString("etiqueta01")%>-(<%=listaFields.getString("id")%>)',<%=listaFields.getString("idTypeDoc")%>);
<%}
}%>

function almacenar(){
	var selectTipo = document.getElementById('tipo');
	var selectIndice = document.getElementById('indice');
	var cad = "";
	var sep = "";
	for(var i=0;i<selectIndice.options.length;i++){
		cad += sep + selectIndice.options[i].value;
		sep = "-";
	}
	ajax = ajaxobj();
	ajax.open("POST", "fillFieldSelectAjax.do?save=true&idTypeDoc="+selectTipo.value+"&ids="+cad, true);
	ajax.onreadystatechange=function() {
		if (ajax.readyState == 4) {
			alert(ajax.responseText);
		}
	}
	ajax.send(null);    		
}

function removeAllOptions(selectbox){
	for(var i=selectbox.options.length-1;i>=0;i--){
		selectbox.remove(i);
	}
}
function addOption(selectbox,text,value ){
	var optn = document.createElement("OPTION");
	optn.text = text;
	optn.value = value;
	selectbox.options.add(optn);
}
function removeOption(selectBox,id) {
	for(var i=selectBox.options.length-1;i>=0;i--){
		if(selectBox.options[i].value==id) {
			selectBox.remove(i);
			break;
		}
	}
}
function cargar(selectTipo) {
	var selectCampo = document.getElementById('campo');
	var indice = document.getElementById("indice");

	removeAllOptions(selectCampo);
	removeAllOptions(indice);
	for(var i=0; i<campos.length; i++) {
		if(selectTipo.value==campos[i].tipo) {
			addOption(selectCampo,campos[i].descri,campos[i].id);
		}
	}
	llenarLista(indice,selectTipo.value);
}

function ajaxobj() {
	try {
		_ajaxobj = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			_ajaxobj = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (E) {
			_ajaxobj = false;
		}
	}
	if (!_ajaxobj && typeof XMLHttpRequest!='undefined') {
		_ajaxobj = new XMLHttpRequest();
	}
	return _ajaxobj;
}    

function llenarLista(selectBox,idTypeDoc) {
	if(true) {
		ajax = ajaxobj();
		ajax.open("POST", "fillFieldSelectAjax.do?idTypeDoc="+idTypeDoc, true);
		ajax.onreadystatechange=function() {
			if (ajax.readyState == 4) {
				//alert(ajax.responseText);
				var arr = eval(ajax.responseText);

				for(var x=0;x<arr.length;x++) {
					addOption(selectBox,arr[x].etiqueta,arr[x].id);
					// eliminamos los campos indice de la lista de definidos
					removeOption(document.getElementById('campo'),arr[x].id);
			    }
			}
		}
		ajax.send(null);    		
	}
}


</script>
