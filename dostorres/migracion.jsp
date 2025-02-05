<!--
 * Title: migracion.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting <br/>
 * @version WebDocuments v4.8
 * <br/>
 *       Changes:<br/>
 *           
 * </ul>
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
    String tipo = (String)request.getAttribute("tipo");
    String formato = (String)request.getAttribute("formato");
    tipo = tipo==null?"0":tipo;
    formato = formato==null?"0":formato;
    
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<link href="estilo/tabs.css" rel="stylesheet" type="text/css">

<script language="JavaScript">
    function validar(forma){
    	forma.nombreArchivo.value=forma.nombre.value;
        if (forma.nombreArchivo.value.length==0) {
            alert('<%=rb.getString("migr.msg1")%>');
            return false;
        }/*else if(forma.nombreLogs.length==0){
            alert('<%=rb.getString("migr.msg2")%>');
            return false;
    	}*/else{
    		forma.btnAccept.disabled = true;
    		try{
	    	document.getElementById("resultado").style.display="none";
	    	}catch(e){}
    		forma.submit();
    	}
    }
    
    function abre(obj) {
    	var arc = '<%=String.valueOf(request.getAttribute("nameFileLog"))%>';
    	
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
<html:form action="/ejecutarMigracion.do">
        <table cellSpacing=0 cellPadding=0 align=center border=0 >
            <tr>
	            <td class="pagesTitle" align="center">
				<span class="ppalTextBoldBig">
				<logic:equal name="statusMigracion" scope="session" value="1">
				    <%
				      	if(tipo.equals("1")){
				      		out.println(rb.getString("migrValid.end"));
						}else{
					    	out.println(rb.getString("migr.end"));
					    }
					%>
			    </logic:equal>
				</span>
				<br><br>
		        <table cellSpacing=3 cellPadding=5 align=center border="0" align="center">
		            <tr>
		               <td valign="top" width="110" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Tipo de archivo :
		               </td>
		               <td valign="middle" align="left" class="ppalText" style="text-align:left;">
		               		<input type="radio" value="0" maxlength="250" size="25" name="formato" <%=formato.equals("0")?"checked":""%> >Hoja de calculo (xls)
		               		<input type="radio" value="1" maxlength="250" size="25" name="formato" <%=formato.equals("1")?"checked":""%> >Archivo plano con separadores (txt)
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		Proceso a ejecutar :
		               </td>
		               <td valign="middle" align="left" class="ppalText" style="text-align:left;">
		               		<input type="radio" value="0" maxlength="250" size="25" name="tipo" <%=tipo.equals("0")?"checked":""%> >Migración de Datos
		               		<input type="radio" value="1" maxlength="250" size="25" name="tipo" <%=tipo.equals("1")?"checked":""%> >Validación de Archivo
		               		<input type="radio" value="2" maxlength="250" size="25" name="tipo" <%=tipo.equals("2")?"checked":""%> >Configuración de Campos
		               </td>
		            </tr>
		            <tr>
		               <td valign="top" align="right" class="td_title_bc" style="text-align:left;" nowrap>
		               		<%=rb.getString("migr.file")%> :
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
		            	</td>
		            </tr>
		            <tr>
		            	<td colspan="2">
		            		&nbsp;
		            	</td>
		            </tr>
		            <%if(request.getAttribute("nameFileLog")!=null){%>
		            <tr id="resultado">
		            	<td colspan="2">
							<ul>
								<li><a id="log1" href="#" onclick="abre(this);" class="actual">Invalidas</a></li>
								<li><a id="log2" href="#" onclick="abre(this);">Validas</a></li>
								<li><a id="log3" href="#" onclick="abre(this);">Log</a></li>
							</ul>
							
							<div id="contenido" style="width:100%"> 
								<iframe name="log" id="log" width="100%" height="250" src="logs/<%=String.valueOf(request.getAttribute("nameFileLog"))%>_MAL.txt" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
							</div>		            	
		            	</td>
		            </tr>
		            <%}%>
		        </table>

                </td>
            </tr>
        </table>
        
        
  
    </html:form>
</fieldset>
</body>
</html>
