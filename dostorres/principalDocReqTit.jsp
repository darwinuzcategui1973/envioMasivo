<!-- principalPenTit.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    
    int MAXIMO = 2;

    //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    //Fin 12/03/07

    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok);
        onLoad.append("'");

        //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
        if (ToolsHTML.checkValue(info)) {
            onLoad.append(";alert('").append(info).append("');");
        }
        //Fin 12/03/07
        
        onLoad.append("\"");
    } else{
        response.sendRedirect(rb.getString("href.logout"));
    }


    
    ToolsHTML.clearSession(session,"application.ppal");
    String tipoImpresion=HandlerDocuments.TypeDocumentsImpresion;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
/* Style the tab */
.tab {
    overflow: hidden;
    background-color: #f1f1f1;
}

/* Style the buttons inside the tab */
.tab button {
    background-color: inherit;
    float: left;
    border: none;
    outline: none;
    cursor: pointer;
    transition: 0.3s;
    font-size: 14px;
    width:50%;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
	font-size: 11px;
	font-weight:bold;
	color: #000000;
}

/* Change background color of buttons on hover */
.tab button:hover {
    background-color: #ddd;
}

/* Create an active/current tablink class */
.tab button.active {
    background-color: #ccc;
}
</style>
<script>
function openLink(evt,etiqueta) {
    var i, tablinks;
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    evt.currentTarget.className += " active";
	window.open("principal"+etiqueta+"Tit.jsp","bandejaTit");
	window.open("principal"+etiqueta+".jsp","bandeja");
}

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="0" cellPadding="0" align=center border=0 width="100%">
	<tr>
	<td width="100%" valign="top">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding="0" align=center border=0>
                        	<tr>
                        		<td>
									<div class="tab">
									  <button class="tablinks" onclick="openLink(event,'DocPen')"><%=rb.getString("doc.pending")%></button>
									  <button class="tablinks active" ><%=rb.getString("doc.pendingRequired")%></button>
									</div>
                        		</td>
                        	</tr>
                        </table>
                    	
                    	<!--
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding="1" align=center border=0>
                            <tbody>
                                <tr>
                                    <td width="50%" class="td_title_bc" height="15">
                                        <%=rb.getString("doc.pending")%>
                                    </td>
                                	<td width="50%" class="boton">
                                		<input type="button" class="boton" value="Documentos Requeridos" style="width:200px;" onClick="javascript:incluir(this.form);" />
                                	</td>
                                </tr>
                            </tbody>
                        </table>
                        -->
                        <!-- Flujos Pendientes -->
                        <table width="100%" border="0" cellspacing="0" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" width="7%"><%=rb.getString("doc.Ver")%></td>
                                <td class="td_titulo_C" width="68%"><%=rb.getString("doc.name")%></td>
                                <td class="td_titulo_C" width="25%"><%=rb.getString("doc.dateCheckOut")%></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
	</td>
  </tr>
 </table>
</body>
</html>
