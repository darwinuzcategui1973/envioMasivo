<!--**
 * Title: recordUsers.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting<br/>
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 19-12-2007 (YSA) Creation </li>
 </ul>

 */-->


<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.Collection,
                 com.desige.webDocuments.utils.beans.PaginPage"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb,"initPage();");
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
    function initPage() {
        forma = document.forms[0];
        if ((forma)&&(forma.nameDocument)) {
            forma.nameDocument.focus();
        }
    }
    
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

</script>
</head>
<body bgcolor="#efefef" <%=onLoad%>>
<form name="selection" id="selection" method="POST" action="">
<logic:present name="listUsersRecord" scope="session">
	<table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
        <tr>
            <td valign="top">
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td class="td_titulo_C" width="5%">&nbsp;</td>
                        <td class="td_titulo_C" width="95%">
                            <%=rb.getString("rcd.users")%>
                        </td>
                    </tr>

                    <%
                        String from = request.getParameter("from");
                        String size = String.valueOf(((Collection)session.getAttribute("listUsersRecord")).size());
                        Users users = (Users)session.getAttribute("user");
                        PaginPage bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                    %>

                    <logic:iterate id="user" name="listUsersRecord" type="com.desige.webDocuments.utils.beans.Users" scope="session"
                           indexId="ind">
                        <%
                            int item = ind.intValue()+1;
                            int num = item%2;
                        %>
                     <tr>
                           <td class='td_<%=num%>' align="center">
	                           <input name="listUsers" type="checkbox" value="<%=user.getIdPerson()%>">
                           </td>
                           <td class='td_<%=num%>' style="font-weight:normal;"><%=user.getNamePerson()%></td>
                     </tr>
                    </logic:iterate>
                </table>
            </td>
        </tr>
    </table>						
<div style="display:none"><input name="listUsers" type="checkbox" value="0"></div>
</logic:present>
</form>
<form name="formPagingPage" method="post" action="javascript:parent.verUsuarios(document.formPagingPage.from.value);">
 	<input type="hidden" name="from"  value="">
 	<input type="hidden" name="lista"  value="">
 	<input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
 	<input type="hidden" name="url"  value="usersRecord.do">
</form> 		
<script type="text/javascript" event="onload" for="window">
	function existeParent(valor){
		var existe = false;
		try{
			var listaParent = window.parent.filtro.document.recordForm.usersSelected.value;
			//alert(listaParent);
			if(listaParent!=""){
				var tokens = listaParent.tokenize(",", " ", true);
		
				for(var i=0; i<tokens.length; i++){
					if(valor==tokens[i]){
						existe = true;
					}
			  	}
		    }
	    }catch(e){
	    }
	  	return existe;
	}
	
	//Se revisa si estan en lista de usuarios seleccionados
	var formaSelection = document.getElementById("selection");
	var lista = formaSelection.listUsers;
	if (lista){
		for (row = 0; row < lista.length; row++){
			if (existeParent(lista[row].value)){
				lista[row].checked = true;
			}
		}
	}
	
	//alert("en iframe: " + formaSelection.listUsers.length);
</script>
</body>
</html>
