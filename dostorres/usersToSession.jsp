<!--
 * Title: usersToSession.jsp
 * Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v4
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 20/07/2005 (NC) Creación </li>
 </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 java.util.Collection,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.seguridad.forms.SeguridadUserForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
%>
<html>
<head>
<title><%=rb.getString("seguridad.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

  function updateCheck(check,field){
        if (check.checked){
            field.value = 1;
        } else{
            field.value = 0;
        }
    }
      function youAreSure() {
    	var forma = document.getElementById("selection");
        if (!validateCheck()) {
            alert('<%=rb.getString("error.selectValue")%>');
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
                forma.action = "loadUsersToSession.do";
               forma.submit();
            }
        }
    }

  function validateCheck() {
	      var formaSelection = document.getElementById("selection");
          for (var i=0; i < formaSelection.length;i++) {
          if (formaSelection.elements[i].type == "checkbox"){
            if (formaSelection.elements[i].checked) {
                return true;
            }
          }
        }
        return false;
    }

    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
    function regresar(){
        //location.href = "administracionMain.do";
        //alert(document.location.href);
        alert(location.search);
       	//parent.frames["info"].history.back();
       	
    }
    function toSearch(value,forma){
        forma.toSearch.value = value;
        forma.action = "usersToSession.do?loadUser=true";
        forma.submit();
    }

    function backStruct(forma){
        forma.action = "administracionMain.do";
        forma.submit();
    }

    function checkKey(evt,value,forma){
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13){
            toSearch(value,forma);
        }
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
<!--loadWF.do -->
 <form name="selection" id="selection" method="POST" action="">
   <logic:notPresent name="usersToSession" scope="session">
       <input name="usersToSession" type="checkbox" value="0">
       <input name="usersToSession" type="checkbox" value="0">
   </logic:notPresent>
 <logic:present name="usersToSession" scope="session">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="80%">
        <tr>
            <td valign="top">
                <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                    <tr>
                        <td valign="top" colspan="3">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc">
                                            <%=rb.getString("session.title")%>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td class="td_orange_l_b" width="40%">
                            <%=rb.getString("session.usuario")%>
                        </td>
                        <td class="td_orange_l_b" width="30%">
                            <%=rb.getString("session.conexion")%>
                        </td>
                      <%--  <td class="td_orange_l_b" width="*">
                            <%=rb.getString("seguridad.grupo")%>
                        </td>--%>
                    </tr>

                    <%
                        String from = request.getParameter("from");
                        String size = String.valueOf(((Collection)session.getAttribute("usersToSession")).size());
                        Users users = (Users)session.getAttribute("user");
                        PaginPage bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                        //com.desige.webDocuments.utils.beans.Users
                    %>

                    <logic:iterate id="user" name="usersToSession" type="com.desige.webDocuments.utils.beans.Search" scope="session"
                           indexId="ind" offset="<%=bean.getDesde()%>" length="<%=bean.getCuantos()%>">

                        <%
                            int item = ind.intValue()+1;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>'>
                                 <a class="ahref_b" href="loadUsersToSession.do?nextPage=loadToEdit&cmd=<%=SuperActionForm.cmdLoad%>&user=<%=user.getId()%>">
                                    <%=user.getId()%> &nbsp;
                                 </a>
                            </td>

                           <td class='td_<%=num%>'>

                               <logic:equal name="user" value="<%=users.getUser()%>">
                                    <input name="usersToSession" type="checkbox" disabled value="<%=user.getId()%>">
                               </logic:equal>
                               <logic:notEqual name="user" value="<%=users.getUser()%>">
                                    <input name="usersToSession" type="checkbox" value="<%=user.getId()%>"> (<%=user.getAditionalInfo()%>)
                               </logic:notEqual>

                           </td>
                     </tr>
                    </logic:iterate>
                        <tr>
                            <td colspan="3">
                                <table width="100%"  cellspacing="0" cellpadding="0" bordercolor="#EEEEEE">
                                    <tr>
                                        <td align="center" >
                                            <input type="button" class="boton" value="<%=rb.getString("session.btn")%>" onClick="javascript:youAreSure();" />
                                             &nbsp;
											 <input type="button" class="boton" value="<%=rb.getString("btn.back")%>"  onClick="javascript:document.location='administracionMain.do'" />
                                        </td>
                                    </tr>
                                    <tr>
                                    </tr>
                                </table>
                            </td>
                      </tr>
                </table>
                
        <%
            if (size.compareTo("0") > 0){
        %>
                <tr>
                    <td align="center">
                        <!-- Inicio de Paginación -->
                        <table class="paginador">
                          <tr>
                            <td align="center" colspan="4">
                              <br>
                              <font size="1" color="#000000">
                                <%=rb.getString("pagin.title")+ " "%>
                                <%=Integer.parseInt(bean.getPages())+1%>
                                <%=rb.getString("pagin.of")%>
                                <%=bean.getNumPages()%>
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
                                   onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())-1)%>)">
                              </td>
                              <td align="center"> <img src="img/right.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                   onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())+1)%>)">
                              </td>
                              <td align="center"> <img src="img/End.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                   onclick="paging_OnClick(<%=bean.getNumPages()%>)">
                              </td>
                          </tr>
                        </table>
                        <!-- Fin de Paginación -->
                    </td>
                </tr>
            <%
                }
            %>
                            
            </td>
        </tr>
    </table>
  </logic:present>
 </form>
 
 <form name="formPagingPage" method="post" action="loadUsersToSession.do?loadUser=true">
 	<input type="hidden" name="from"  value="">
 	<input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
 </form>
</body>
</html>
