<!--
 * Title: showDataStruct.jsp
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 01/06/2005 (NC) Cambios para mostrar los comentario </li>
 *      <li> 23/06/2005 (SR) Cambios para mostrar los comentario, se agrego (HandlerDocuments.docCheckIn) </li>
 *      <li> 07/06/2005 (NC) Cambios para mostrar los comentarios </li>   
 </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.to.PreviewTO,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	String version = "";
	String versionAnt = "";
	String color = "";
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    String nodeActive = (String)session.getAttribute("nodeActive");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageViewI
{
  background: white;
  color: black;
  border: 1px solid #afafaf;
  padding: 2;
  font-size: 12px;
  font-family: Geneva, Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: auto;
}
.tituloTable {
	font-family: Geneva, Verdana, Arial, Helvetica;
	font-size: 12px;
	font-weight:bold;
	color:#000;
	text-align:center;
}
.tituloCell {
	font-family: Geneva, Verdana, Arial, Helvetica;
	font-size: 12px;
	background-color:#01A9DB;
	color:#ffffff;
	text-align:center;
}
.detalleCell{
	font-family: Geneva, Verdana, Arial, Helvetica;
	font-size: 10px;
	background-color:#ffffff;
	color:#000000;
	text-align:center;
}

</style>
<script type="text/javascript">

    function cancelar() {
    	var form = document.getElementById("Selection");
        
        <%if(session.getAttribute("idDocument")!=null && !String.valueOf(session.getAttribute("idDocument")).equals("")) {%>
        	form.idDocument.value = <%=session.getAttribute("idDocument")%>;
	        form.action="showDataDocument.do";
        <%} else {%>
	        form.action="loadStructMain.do";
        <%}%>
        form.target = "_parent";
        form.submit();
    }
    
    function load(id) {
    	var formaSelection = document.getElementById("Selection");
        formaSelection.histSelect.value = id;

        with(formaSelection){
        	//alert(action+"?histSelect="+histSelect.value+"&idNodeSelected="+idNodeSelected.value);
	        window.open(action+"?histSelect="+histSelect.value+"&idNodeSelected="+idNodeSelected.value,"_parent");
        }
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
        <form name="Selection" id="Selection" action="loadHistoryDocs.do">
            <input type="hidden" name="idDocument" value=""/>
            <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
            <input type="hidden" name="histSelect" value=""/>
        </form>
<!-- END Form -->

<table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
    <tr>
        <td class="td_orange_l_b" width="75%">
            <%=rb.getString("history.title")%>
        </td>
        <td width="*">&nbsp;
            <input type="button" class="boton" value="Todos los Usuarios" onClick="javascript:load(-2);"/>&nbsp;
            <input type="button" class="boton" value="Lista de Divulgación" onClick="javascript:load(-3);"/>&nbsp;
        	<%if(session.getAttribute("f1")!=null){%>
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:window.parent.document.location='filesView.do?f1=<%=session.getAttribute("f1")%>';"/>
        	<%}else{%>
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();"/>
        	<%}%>
        </td>
    </tr>
    <tr>
    	<td colspan="2">
    		<table border="0" width="100%">
    				<logic:equal name="historyListDist" value="false">
		    			<caption class="tituloTable"><%=rb.getString("doc.all.users")%></caption>
	    			</logic:equal>
    				<logic:equal name="historyListDist" value="true">
		    			<caption class="tituloTable">Usuarios seg&uacute;n lista de divulgaci&oacute;n</caption>
	    			</logic:equal>
    			<tr>
    				<td class="tituloCell"><%=rb.getString("history.version")%></td>
    				<td class="tituloCell"><%=rb.getString("history.user")%></td>
    				<td class="tituloCell"><%=rb.getString("history.firstTime")%></td>
    				<td class="tituloCell"><%=rb.getString("history.lastTime")%></td>
    				<td class="tituloCell"><%=rb.getString("history.views")%></td>
    			</tr>
    			<logic:present name="dataHistoryViews">
    			<logic:iterate id="bean" name="dataHistoryViews">
    			<%
    				version = ((PreviewTO)bean).getMayorVer();
    				if(!version.equals(versionAnt)){
    					color=color.equals("#efefef")?"#d4d4d4":"#efefef";
    				}
    			%>
    			<tr>
    				<td class="detalleCell" style="background-color:<%=color%>;"><bean:write name="bean" property="mayorVer"/>.<bean:write name="bean" property="minorVer"/></td>
    				<td class="detalleCell" style="background-color:<%=color%>;"><bean:write name="bean" property="nameUser"/></td>
    				<td class="detalleCell" style="background-color:<%=color%>;"><bean:write name="bean" property="registraFormat" /></td>
    				<td class="detalleCell" style="background-color:<%=color%>;"><bean:write name="bean" property="actualizaFormat"/></td>
    				<td class="detalleCell" style="background-color:<%=color%>;"><bean:write name="bean" property="contador"/></td>
    			</tr>
    			<%
    				versionAnt = ((PreviewTO)bean).getMayorVer();
    			%>
    			</logic:iterate>
    			</logic:present>
    		</table>
    	</td>
    </tr>
</table>
</body>
</html>
