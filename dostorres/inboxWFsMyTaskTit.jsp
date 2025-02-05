<!-- loadWFsMyTaskTitle.jsp -->
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
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="0" cellPadding="0" align=center border=0 width="100%">
	<tr>
	<td width="100%" valign="top">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
                <tr>
                    <td>
	                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
	                        <tbody>
	                            <tr>
	                                <td class="td_title_bc" height="21">
	                                    <%=rb.getString("inWF.myTask")%>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
                        <!-- Flujos Pendientes -->
                        <table width="100%" border="0" cellspacing="0" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("inWF.of")%>
                                </td>
                                <td class="td_titulo_C" width="30%">
                                    <%=rb.getString("inWF.suject")%>
                                </td>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("inWF.dateInit")%>
                                </td>
                                <td class="td_titulo_C" width="17%">
                                    <%=rb.getString("inWF.dateExpires")%>
                                </td>
                                <td class="td_titulo_C" width="17%">
                                    <%=rb.getString("inWF.dateEnd")%>
                                </td>
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
