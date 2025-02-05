<!--**
 * Title: grupo.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz. (SR)
 * @version WebDocuments v1.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 27-08-2004 (NC) Creation </li>
 *      <li> 17-04-2006 (SR)btn eliminar</li>

 </ul>

 */-->
<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*,
                                                                             java.util.ResourceBundle,
                                                                             com.desige.webDocuments.utils.ToolsHTML,
                                                                             com.desige.webDocuments.utils.beans.SuperActionForm,
                                                                             java.util.Hashtable,
                                                                             com.desige.webDocuments.utils.DesigeConf" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
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
    String desc = request.getAttribute("goBack")!=null?(String)request.getAttribute("goBack"):"";
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
	
    function validar(forma) {
    	forma.nombreGrupo.value = forma.nombreGrupo.value.trim();
    	forma.descripcionGrupo.value = forma.descripcionGrupo.value.trim();
    	
        if (forma.nombreGrupo.value.length==0 || forma.descripcionGrupo.value.length==0){
            return false;
        } else {
		    return true;
        }
	}
    
    function salvar(forma) {
    	//si no es insert coloco edit, sino, dejo insert
    	if(! forma.cmd.value == "<%=SuperActionForm.cmdInsert%>"){
    		forma.cmd.value = "<%=SuperActionForm.cmdEdit%>";
    	}
    	
        if (validar(forma)){
           forma.submit();
        } else {
            alert("<%=rb.getString("user.mensaje")%>");
        }
    }
    
    function cancelar(form) {
		try{
			if(window.opener){
				window.close();
			}
		}catch(e){
		}
        <logic:present name="goBack" scope="request">
            location.href = "loadUsersToEdit.do?goTo=loadGroups";
        </logic:present>
        <logic:notPresent name="goBack" scope="request">
          location.href = "administracionMain.do";
          //history.back();
        </logic:notPresent>
    }

<%--    function editField(pages,input,value,forma){--%>
<%--        var hWnd = null;--%>
<%--        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");--%>
<%--        if ((document.window != null) && (!hWnd.opener)){--%>
<%--            hWnd.opener = document.window;--%>
<%--        }--%>
<%--    }--%>
    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         form.submit();
      }
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<html:form action="/newGrupo.do">
<html:hidden property="cmd" />
<input type="hidden" name="goBack" value="<%=desc%>"/>
<table width="100%" border="0">
    <!--<tr>
        <td colspan="2" class="pagesTitle">
            <%=rb.getString("grupo.title")%>
        </td>
    </tr>-->
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc">
                            <%=rb.getString("grupo.title")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td width="25%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="16%" valign="middle">
            <%=rb.getString("grupo.nombre")%>
        </td>
        <td width="*" align="left">
            <html:text property="nombreGrupo" size="30"/>
        </td>
    </tr>
    <tr>
        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
            <%=rb.getString("grupo.descripcion")%>
        </td>
        <td align="left">
            <html:text property="descripcionGrupo" size="80" maxlength="250"/>
        </td>
    </tr>
    <tr>
        <td colspan="3" align="center">
            <%
                String disableBtns = "";
                String idAdmon = DesigeConf.getProperty("application.admon");
            %>
            <logic:notEqual value='<%=idAdmon.trim()%>' name='user' property='idGroup'>
                <%
                    disableBtns = "disabled";
                %>
            </logic:notEqual>

            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);" <%=disableBtns%> />
            <logic:equal value="<%=SuperActionForm.cmdEdit%>" name="cmd" >
                &nbsp;
               <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" onclick="javascript:youAreSure(this.form)" <%=disableBtns%> />
            </logic:equal>
            &nbsp;
            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" />
        </td>
    </tr>
</table>
</html:form>
</body>
</html>
<script language="javascript">
	function cerrar(){
		try{
			window.opener.getDatos();
		}catch(e){}
	}
	window.onunload=cerrar;
</script>
