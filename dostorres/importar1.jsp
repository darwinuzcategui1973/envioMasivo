<!--
 * Title: importar1.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting <br/>
 * @version WebDocuments
-->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
                 com.desige.webDocuments.utils.Constants,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.ToolsHTML,
                 java.util.ArrayList,
                 com.focus.qweb.bean.TextoReader,
                 com.focus.qweb.bean.Reader,
                 com.focus.qweb.to.EquivalenciasTO,
                 com.desige.webDocuments.utils.DesigeConf"%>


<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<%
    Users usuario = (Users)session.getAttribute("user");
    ResourceBundle rb = ToolsHTML.getBundle(request);
    ArrayList lista = (ArrayList)session.getAttribute("equivalencias");
    ArrayList listaNombre = (ArrayList)session.getAttribute("equivalenciasNombres");
    String equivalenciaActiva=(String)(request.getAttribute("equivalenciaActiva")!=null?request.getAttribute("equivalenciaActiva"):"");
    String info=(String)(request.getAttribute("info")!=null?request.getAttribute("info"):"");
    EquivalenciasTO equi = null;
    Reader datos = (Reader)session.getAttribute("datos");

	ArrayList conf = (ArrayList)session.getAttribute("confDocument");
	DocumentForm obj;
    
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>

<script language="JavaScript">
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
	
    function validar(forma){
    	if(forma.nombre.value.trim().length!=0){
			forma.btnAccept.disabled = true;
			forma.submit();
    	}
    }
    
    function eliminar(forma){
    	if(confirm("Desea eliminar esta configuración?")){
	    	forma.cmd.value="eliminarEquivalencias";
			forma.btnAccept.disabled = true;
			forma.submit();
		}
    }

    function marcar(obj,ind){
   		document.forma.indexar[ind].value=(obj.checked?"1":"0");
    }
    
    function abreIndice(forma) {
		abrirVentana("importarIndice.do?cmd=listar",800,500);
    }
    
    function verSelect(forma) {
    	if(document.forma.equivalencia.style.display!="") {
	    	document.forma.equivalencia.style.display="";
    	} else {
			document.forma.equivalencia.style.display="none";
    	}
    }
    function listar(forma){
		document.forma.equivalencia.style.display="none";
    	if(document.forma.equivalencia.value=="") {
	    	document.forma.nombre.readOnly=false;
    		with(document.forma){
				for(var x=0; x<posicion.length;x++) {
					posicion[x].value="";
					valor[x].value="";
					columna[x].value="";
					indexar[x].value="0";
					indexar1[x].checked=false;
				}
			}
	    	forma.nombre.value = forma.equivalencia.value; 
	    	document.forma.nombre.focus();
    	} else {
    		if(forma.equivalencia.value!=""){
	    		forma.nombre.value = forma.equivalencia.value; 
	    	}
    		forma.cmd.value="listarEquivalencias";
    		forma.submit();
    	}
    }
    
</script>

</head>

<body class="bodyInternas" style="margin:10;">
<fieldset style="padding:10;">
<legend style="font-size:13pt;font-family:Tahoma,Arial,Verdana;"><%=rb.getString("migr.data")%></legend>
<form name="forma" method="POST" action="importar.do">
<input type="hidden" name="cmd" value="equivalencias" />
        <table cellSpacing=0 cellPadding=0 align=center border=0  width="100%">
            <tr>
	            <td class="pagesTitle" align="center">
		        <table cellSpacing=0 cellPadding=0 align=center border="0" align="center"  width="100%">
		        	<tr>
		        		<td width="100%">
		               		<fieldset>
			               			<legend>Equivalencia :</legend>
						        <table id="tablaNombre" cellSpacing=0 cellPadding=0 align="center" border="0" align="center" width="100%">
						        	<tr>
						        		<td width="99%">
						               		<input type="text" name="nombre" style="width:100%;" readonly>
						               	</td>
						               	<td valign="middle">
							               	<img src="images/combodown.gif" border="0" style="margin-top:2pt;" onclick="verSelect()">
						               	</td>
						             </tr>
			               		</table>
			               		<select name="equivalencia" size="10" style="position:absolute;width:100%;display:none;" onchange="listar(this.form)" >
			               			<%if(listaNombre!=null){
						        	  for(int i=0;i<listaNombre.size();i++){
						        		equi = (EquivalenciasTO)listaNombre.get(i);%>
			               			    <option value="<%=equi.getNombre()%>" <%=(equi.getActivo().equals("1")?"selected":"")%>><%=equi.getNombre()%></option>
			               			<%}
			               			}%>
			               			<option value="">Nueva equivalencia . . . </option>
			               		</select>
		               		</fieldset>
		               		<br/>
		        		</td>
		        	</tr>
		            <tr>
		               <td width="100%">
					        <table cellSpacing="0" cellPadding="1" align=center border="0" align="center" class="td_gris_l" style="border:1px solid #c2d2e0;text-align:center;" width="100%">
					            <tr  bgcolor="#c2d2e0">
					               <td width="5%" >
									  <b>Nro.</b>
					               </td>
					               <td width="29%">
									  <b>Campo</b>
					               </td>
					               <td width="30%">
									  <b>&nbsp;Columnas</b>
					               </td>
					               <td width="29%">
									  <b>Valor predeterminado</b>
					               </td>
					               <td width="5%">
									  <b>Indexar</b>
					               </td>
					               <td width="2%">
					               </td>
					            </tr>
		               		</table>
		               		<div style="height:400;overflow:auto;border:1px solid #c2d2e0;vertical-align:top">
					        <table cellSpacing="0" cellPadding="0" align=center border="0" align="center" class="td_gris_l" style="border:1px solid #c2d2e0"  width="100%">
					        	<%for(int i=0;i<lista.size();i++){
					        		equi = (EquivalenciasTO)lista.get(i);%>
						            <tr>
						               <td width="5%">
										  <input type="text" name="indice" value="<%=equi.getIndice()%>" size="3" readOnly="true" style="border:0px;background: transparent;text-align:center;"/>	
						               </td>
						               <td width="30%">
										  <input type="text" name="campo" value="<%=equi.getCampo()%>" size="40" readOnly="true" style="border:0px;background: transparent;"/>	
						               </td>
						               <td width="30%">
										  <select name="posicion" value="<%=equi.getPosicion()%>" style="width:100%;">
										  	<option value="">Seleccione...</option>
								        	<%for(int k=1;k<=datos.getColumnsCount();k++){%>
											  	<option value="<%=k%>" <%=equi.getPosicion().equals(String.valueOf(k))?"selected":""%> ><%=datos.getHeaders()[k-1]%></option>
								        	<%}%>
										  	<option value="1000" <%=equi.getPosicion().equals("1000")?"selected":""%>>Todas</option>
										  </select>
										  <input type="hidden" name="columna" value="" />	
						               </td>
						               <td width="30%">
										  <input type="text" name="valor" value="<%=equi.getValor()%>" style="width:100%;"/>	
						               </td>
						               <td width="5%">
										  <input type="checkbox" name="indexar1" value="<%=equi.getIndexar()%>" <%=equi.getIndexar().equals("1")?"checked":""%> onclick="marcar(this,<%=i%>)"/>	
										  <input type="hidden" name="indexar" value="<%=equi.getIndexar()%>" />	
						               </td>
						            </tr>
					            <%}%>
		               		</table>
		               		</div>
		               </td>
		            </tr>
		            <tr>
		            	<td align="center">
			            	<br>
		            		<input type="button" value="<%=rb.getString("btn.ok")%>" name="btnAccept" onclick="javascript:validar(this.form);">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="<%=rb.getString("btn.delete")%>" name="btnAccept" onclick="javascript:eliminar(this.form);">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="Indice de Equivalencias" name="btnIndice" onclick="javascript:abreIndice(this.form);">
		            		&nbsp;&nbsp;&nbsp;
		            		<input type="button" value="<%=rb.getString("btn.back")%>" name="btnBack" onclick="javascript:document.location='importar.do';">
		            	</td>
		            </tr>
		            <tr>
		            	<td >
		            		&nbsp;
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
<script language="javascript">
   	<%if(!equivalenciaActiva.equals("")){%>
  	document.forma.equivalencia.value="<%=equivalenciaActiva%>";
   	<%}%>
   	document.forma.nombre.value=document.forma.equivalencia.value;
   	<%if(!info.equals("")){%>
  		setTimeout('alert("<%=info%>")',0);
   	<%}%>
   	
</script>