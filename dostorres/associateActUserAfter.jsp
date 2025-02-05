<!--/**
 * Title: associateActUserAfter.jsp <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 2005-11-10 (NC) Bug in check method Fixed </li>
 * <ul>
 */-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else {
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null){
        inputReturn = "";
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    String userAssociate = request.getParameter("userAssociate");
    if (ToolsHTML.isEmptyOrNull(userAssociate)) {
        userAssociate = "";
    }
    String type = (String)ToolsHTML.getAttribute(request,"type");
    if (ToolsHTML.isEmptyOrNull(type)) {
        type = "0";
    }
%>
<html>
<head>
<title><%=rb.getString("principal.title")%>  asociado.................</title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    var selecteds = new Array();
    <%  if (ToolsHTML.isEmptyOrNull(userAssociate)) {
            userAssociate = "";
    %>
            selecteds = new Array();
    <%
        } else {
    %>
            selecteds = new Array(<%=userAssociate%>);
    <%
        }
    %>

    <logic:present name="closeWindow" scope="request">
        <%=valueReturn%>
        cancelar();
    </logic:present>

    function cancelar(){
        window.close();
    }

    function check() {
    	var formaSelection = document.getElementById("selection");
        if (validateCheck()) {
            formaSelection.submit();
            <%--window.close();--%>
        } else {
        	alert("Debe marcar al menos un participante");
        }
     }

     function validateCheck() {
    	var formaSelection = document.getElementById("selection");
        var set = false;
        var coma = ",";
        var comilla = "'";
        for (var i=0; i < formaSelection.length;i++) {
        	if ( formaSelection.elements[i].type=="checkbox") {
				msg = formaSelection.elements[i].value;        	
				
	            if (formaSelection.elements[i].checked){
	            	if ( (formaSelection.userAssociate.value).indexOf(comilla+msg+comilla)==-1 ) {
	            		if (formaSelection.userAssociate.value=="") {
		            		formaSelection.userAssociate.value += comilla+msg+comilla;
		            	} else {
		            		formaSelection.userAssociate.value += coma+comilla+msg+comilla;
		            	}
	            	}
	            } else {
	            	if ( (formaSelection.userAssociate.value).indexOf(coma+comilla+msg+comilla)!=-1 ) {
	            		formaSelection.userAssociate.value = formaSelection.userAssociate.value.replace(coma+comilla+msg+comilla,"");
	            	} else if ( (formaSelection.userAssociate.value).indexOf(comilla+msg+comilla+coma)!=-1 ) {
	            		formaSelection.userAssociate.value = formaSelection.userAssociate.value.replace(comilla+msg+comilla+coma,"");
	            	} else if ( (formaSelection.userAssociate.value).indexOf(comilla+msg+comilla)!=-1 ) {
	            		formaSelection.userAssociate.value = formaSelection.userAssociate.value.replace(comilla+msg+comilla,"");
	            	}
	            }
            }
        }
        //alert(formaSelection.userAssociate.value);
        return (formaSelection.userAssociate.value!="");
    }

    function paging_OnClick(pageFrom) {
    	var formaSelection = document.getElementById("selection");
        document.formPagingPage.from.value = pageFrom;
        validateCheck();
        document.formPagingPage.userAssociate.value = formaSelection.userAssociate.value;
        document.formPagingPage.submit();
    }
    
    function main() {
    	<%=request.getAttribute("main")!=null?request.getAttribute("main"):""%>
    }

</script>
</head>
<body class="bodyInternas" <%=onLoad%>>
    <logic:present name="usuarios" scope="session">
        <%
            String from = (request.getParameter("from")==null?"0":request.getParameter("from"));
            String size = (String)session.getAttribute("size");
            Users users = (Users)session.getAttribute("user");
            int record = (users==null?10:users.getNumRecord());
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(record));
        %>
        <form name="associate" method="post" action="saveActUsers.do">
            <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
            <input type="hidden" name="type" value="<%=type%>"/>
        </form>

         <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("cbs.associate")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td cass="td_gris_l" colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td class="td_orange_l_b" width="30%">
                    <%=rb.getString("user.ape")%>
                </td>
                <td class="td_orange_l_b" width="*">
                    <%=rb.getString("user.name")%>
                </td>
            </tr>
            <form name="selection" id="selection" action="saveNewUsersSubActivies.do" method="POST">
                <input type="hidden" name="primeraVez" value="true"/>
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>"/>
	            <input type="hidden" name="idWorkFlow" value="<%= session.getAttribute("idWorkFlow") %>"/>
	            <input type="hidden" name="type" value="<%= request.getAttribute("type") %>"/>
                <logic:iterate id="associate" name="usuarios" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                             type="com.desige.webDocuments.perfil.forms.PerfilActionForm" scope="session">
                    <tr>
                        <td class="td_gris_l">
                            <logic:notPresent name="onlyRead" scope="session">
                                <input name="docLinks" type="checkbox" value="<%=associate.getId()%>" onclick="validateCheck()" <%= false && associate.isSelected()?"checked":( userAssociate.indexOf("'"+associate.getId()+"'")!=-1 ? "checked" : "" )%> ">
                            </logic:notPresent>
                            <%=associate.getApellidos()%>
                        </td>
                        <td class="td_gris_l">
                            <%=associate.getNombres()%>
                        </td>
                    </tr>
                </logic:iterate>

                <%
                    if (size.compareTo("0") > 0) {
                %>
                        <tr>
                            <td align="center" colspan="2">
                                <!-- Inicio de Paginación -->
                                <table class="paginador">
                                  <tr>
                                    <td align="center" colspan="4">
                                      <br>
                                      <font size="1" color="#000000">
                                        <%=rb.getString("pagin.title")+ " "%>
                                        <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                        <%=rb.getString("pagin.of")%>
                                        <%=Pagingbean.getNumPages()%>
                                      </font>
                                    </td>
                                  </tr>
                                  <tr>
                                      <td align="center"> <img src="img/First.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                           onclick="paging_OnClick(0)">
                                      </td>
                                      <td align="center"> <img src="img/left.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                           onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                                      </td>
                                      <td align="center"> <img src="img/right.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                           onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                                      </td>
                                      <td align="center"> <img src="img/End.gif" width="24" height="24"
                                           class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                           onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                                      </td>
                                  </tr>
                                </table>                                
                                <!-- Fin de Paginación -->
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="td_gris_l">
                                &nbsp;
                            </td>
                        </tr>
                    <%
                        }
                    %>
            </form>
            <form name="formPagingPage" method="post" action="associateActUserAfter.jsp">
                <input type="hidden" name="from"  value="">
                <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                <input type="hidden" name="userAssociate" value="<%=userAssociate%>">
                <input type="hidden" name="type" value="<%=type%>"/>
            <tr>
<%--                <td colspan="3" class="td_orange_l_b">--%>
                <td colspan="3">
                    <center>
                        <logic:notPresent name="onlyRead" scope="session">
                            <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:check();" />
                            &nbsp;
                        </logic:notPresent>
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                    </center>
                </td>
            </tr>
        </table>
    </logic:present>
    <logic:notPresent name="usuarios" scope="session">
        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc">
                                    <%=rb.getString("E0116")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <p align="center"><br><br>
                        <input type="button" class="boton" value="<%=rb.getString("btn.close")%>" onclick="javascript:cancelar()" />
                    </p>
                </td>
            </tr>            
        </table>
    </logic:notPresent>
</body>
</html>
<%if (request.getAttribute("main")!=null) {%>
<script language="javascript">
	main();
</script>
<%}%>