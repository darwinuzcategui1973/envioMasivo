<!--/**
 * Title: administracion.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelsón Crespo.(NC)
 * @author Ing. Simon Rodriguez.(SR)
 * @version WebDocuments v3.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *          <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
 */-->

<%@ page contentType="text/html; charset=iso-8859-1"
 language="java" import="java.util.ResourceBundle,
                         com.desige.webDocuments.utils.ToolsHTML,
                         com.desige.webDocuments.persistent.managers.HandlerGrupo,
                         com.desige.webDocuments.utils.beans.Users,
                         com.desige.webDocuments.utils.DesigeConf"%>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	response.sendRedirect("administracion.do");
    }

    Users usuario = (Users) session.getAttribute("user");
    if (usuario==null) {
        response.sendRedirect("errorSession.jsp");
    }
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    String mails = HandlerGrupo.getEmailGrupos(null);
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">

    setDimensionScreen();

    function abrirVent(pagina,width,height) {
            var hWnd = null;
            var left = getPosition(winWidth,width);
            var top = getPosition(winHeight,height);

            hWnd = window.open(pagina, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
    }

	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}
    function salvar(){
        document.perfil.submit();
    }

</script>
</head>

<body class="bodyInternas2" <%=onLoad%>>
    <logic:present name="user" scope="session">
<table width="100%" border="0">
    <tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("enl.Admin")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" width="*" height="18">
                            <%=rb.getString("admin.title")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td class="td_title_bc barraGris22"><%=rb.getString("admin.title1")%></td>
        <td class="td_title_bc barraGris22"><%=rb.getString("admin.title2")%></td>
    </tr>
    <tr>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>
            <img src="img/user33.jpg">
            <a href="newUserGrupo.do" class="ahreMenu">
                <%=rb.getString("admin.cuentas")%>
            </a>
        </td>
        <td>
            <img src="img/parameter33.jpg"> 
            <a href="loadParameters.do" class="ahreMenu">
                <%=rb.getString("admin.config")%>
            </a>

        </td>
    </tr>
    <tr>
        <td ><img src="img/addUser33.jpg">
            <a href="newUser.do" class="ahreMenu">
                <%=rb.getString("admin.usuario")%>
            </a>
        </td>
        <td >
            <img src="img/sendMail33.jpg">
                <a href="loadMailAll.do"<%=mails%> class="ahreMenu">
                    <%=rb.getString("admin.mensaje")%>
                </a>
        </td>
    </tr>
    <tr>
        <td >
            <img src="img/addGroup33.jpg">
            <a href="newGrupo.do" class="ahreMenu">
                <%=rb.getString("admin.grupo")%>
            </a>
        </td>
        <td>
            <img src="img/normas33.jpg">
            <a href="javascript:abrirVent('loadNorms.do',700,500)" class="ahreMenu">
              <%--<a href="loadNorms.do" class="ahreMenu">--%>
                <%=rb.getString("norms.update")%>  
            </a>
        </td>
    </tr>
    <tr>
        <td class="ahreMenu">
            <img src="img/candado33.jpg">
            <a href="segUserGrupo.do" class="ahreMenu">
                <%=rb.getString("admin.seguridad")%>
            </a>
        </td>
        <td>
            <img src="img/typeDoc33.jpg">
            <a href="javascript:abrirVentana('loadTypeDoc.do',700,500)" class="ahreMenu">
                <%=rb.getString("typeDoc.update")%>
            </a>
        </td>
    </tr>
    <tr>
        <td class="ahreMenu">
            <img src="img/passUser33.jpg">
                <a href="clave.do" class="ahreMenu"><%=rb.getString("admin.clave")%>
                </a>
        </td>
         <td>
            <img src="img/areas33.jpg">
            <a href="javascript:abrirVentana('loadArea.do',700,500)" class="ahreMenu">
               <!-- < %=rb.getString("admin.scpprocesos")%>-->
                <%=rb.getString("admin.areas")%>
            </a>
        </td>
    </tr>
        <tr>
            <td class="ahreMenu">
                <img src="img/editUser33.jpg">
                <a href="loadUsersToEdit.do?loadUser=true" class="ahreMenu">
                    <%=rb.getString("admin.changeUserGroup")%>
                </a>
            </td>
            <td>
                <img src="img/cargos33.jpg">
                <a href="javascript:abrirVentana('loadCargo.do',700,500)" class="ahreMenu">
                    <%=rb.getString("admin.cargos")%>
                </a>
            </td>
        </tr>        
        <tr>
            <td class="ahreMenu">
                <img src="img/editGroup33.jpg">
                <a href="loadUsersToEdit.do?goTo=loadGroups" class="ahreMenu">
                    <%=rb.getString("admin.changeGroup")%>
                </a>
            </td>
            <td>
               <%-- Luis Cisneros
                19/04/07
                Mostrar opciones de SCAOP --%>
               <%=ToolsHTML.menuSACOP(application, rb)%>   
<%----%>
                 <!--<img src="img/typeDocs.gif" width="18" height="18">-->
                <!--<a href="loadActiveFactory.do" class="ahreMenu">-->
                <%--<%=rb.getString("admin.scpplanillas")%>--%>
                 <!--</a>-->
            </td>
        </tr>
    <!--< /logic:equal>-->
    <!-- SIMON 15 DE JULIO 2005 INCIO-->
        <tr>
            <td class="ahreMenu">
                <img src="img/editSession33.jpg">
                <a href="loadUsersToSession.do?goTo=loadGroups" class="ahreMenu">
                   <%=rb.getString("session.admin")%>
                </a>
            </td>
            <td>
                <%-- Luis Cisneros
                    02/03/07
                    Mostrar opciones de FTP --%>
               <%=ToolsHTML.menuFTP(application, rb)%>
            </td>
        </tr>
    <tr>
        <td></td>
            <td>
               <%=ToolsHTML.menuInTouch(application, rb)%>            
            </td>
    </tr>
    
    <% 
    	String sMigracion = (String)DesigeConf.getProperty("migracionOn");
    	String sMigracionValidacion = (String)DesigeConf.getProperty("migracionValidacionOn");
      	if(sMigracionValidacion!=null && "1".equals(sMigracionValidacion)){
    %>         
    <tr>
        <td></td>
            <td>
               <img src="img/excel.jpg" width="17" height="17">
               <a class="ahreMenu" href="migracion.do" target='info'><%=rb.getString("migrValid.title")%></a>   
            </td>
    </tr>    
	<%	}else{
			if(sMigracion!=null && "1".equals(sMigracion)){
	%>
    <tr>
        <td></td>
            <td>
               <img src="img/excel.jpg" width="17" height="17">
               <a class="ahreMenu" href="migracion.do" target='info'><%=rb.getString("migr.title")%></a>   
            </td>
    </tr>    
	<%	    }
	    }
	%>
<!-- SIMON 15 DE JULIO 2005 FIN-->
</table>
    </logic:present>
<logic:notPresent name="user" scope="session">
    <script language="JavaScript">
        location.href = "inicio.do";
    </script>
</logic:notPresent>
</body>
</html>
