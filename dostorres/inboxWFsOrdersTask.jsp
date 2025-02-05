<!-- /**
 * Title: inboxWFsOrdersTask.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v1.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 13/07/2005 se coloco todos los checkbox habilitados para eliminar </li>
 </ul>
 */ -->
<%@ page language="java" %>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	response.sendRedirect("loadInboxWF.do");
    }

    String isNew = null;
    
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'\"");
    } else{
        response.sendRedirect(rb.getString("href.logout"));
    }
    ToolsHTML.clearSession(session,"application.inboxWFs");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

    function showWF(idWorkFlow,owner,isNew,rowUser,isFlexFlow) {
    	var forma = document.getElementById("selection");
    	forma.idWorkFlow.value = idWorkFlow;
        forma.owner.value = owner;
        forma.isNew.value = isNew;
        forma.rowUser.value = rowUser;
        forma.isFlexFlow.value = isFlexFlow;
        forma.submit();
    }
    
    function showWFPrincipal(idWorkFlow,row,owner,isFlexFlow,isPrintWF){
    	var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.isFlexFlow.value = isFlexFlow;
        forma.isPrintWF.value = (typeof isPrintWF == 'undefined'?"false":isPrintWF);
        forma.showStruct.value = 'false';
        forma.submit();
    }
    

    function validateCheck() {
    	var formaSelection = document.getElementById("selection");
    	for (var i=5; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked) {
                return true;
            }
        }
        return false;
    }

    function youAreSure() {
        if (!validateCheck()) {
            alert('<%=rb.getString("error.selectValue")%>');
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
            	var forma = document.getElementById("selection");
                forma.target="bandeja";
                forma.action = "deleteInboxWF.do";
                forma.submit();
            }
        }
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
    <form name="selection" id="selection" action="loadWF.do">
        <table align=center border=0 width="100%">
            <input type="hidden" name="ordersTask" value="true" />
            <input type="hidden" name="idWorkFlow" value="" />
            <input type="hidden" name="toShow" value="true" />
            <input type="hidden" name="owner" value="false" />
            <input type="hidden" name="isNew" value="false" />
            <input type="hidden" name="isFlexFlow" value="false" />
            <input type="hidden" name="rowUser" value="0" />
            <input type="hidden" name="origen" value="inboxWFs" />
	        <input type="hidden" name="row" value="" />
	        <input type="hidden" name="isPrintWF" value="false" />
	        <input type="hidden" name="showStruct" value="false"/>
        <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
            <tr>
                <td>
                    <logic:present name="taskRequest" scope="session">
                        <table width="100%" border="0">
                            <%
                                isNew = "false";
                            %>
                            <logic:iterate id="myWFs" name="taskRequest" indexId="ind" type="com.desige.webDocuments.workFlows.forms.BaseWorkFlow" scope="session" >
		                    <%
		                        int item = ind.intValue()+1;
		                        int num = item%2;
		                    %>
		                    <tr class='fondo_<%=num%>'>
                                    <td class="td_gris_l">
                                        <% if (myWFs.isNewWF()) { %>
                                              <!--  <input name="taskReq" type="checkbox" disabled value=""/> -->
                                                <!--SIMON 13 DE JULIO 2005 INICIO -->
                                                  <input name="taskReq" type="checkbox" value="<%= myWFs.getRow()%>,<%=myWFs.isFlexFlow()%>"/>
                                                <!--SIMON 13 DE JULIO 2005 FIN -->
                                        <%      isNew = "true";
                                            } else {
                                                isNew = "false";
                                        %>
                                                <input name="taskReq" type="checkbox" value="<%=myWFs.getRow()%>,<%=myWFs.isFlexFlow()%>"/>
                                        <% } %>
                                        <bean:write name="myWFs" property="nameOwner"/>
                                    </td>
                                    <%--ydavila Ticket 001-00-003023--%>
                                    <%--<td>--%>
                                    <td width="30%">
                                    	<a href="javascript:showWFPrincipal('<bean:write name="myWFs" property="numWF"/>','<%=myWFs.getRow()%>',<%=myWFs.isOwnerCurrent()%>,<bean:write name="myWFs" property="flexFlow"/>,'false')"  class="ahref_b">
                                            <%
                                                String pattern = rb.getString("wf.dateExpire") + myWFs.getDateExpireWF();
                                                if(myWFs.getDateExpireWF().contains("4005")){
                                                    //filtramos por año
                                                    out.println(myWFs.getTitleForMail().substring(0, myWFs.getTitleForMail().indexOf(pattern)));
                                                }else {
                                            %>
                                                    <bean:write name="myWFs" property="titleForMail"/>
                                            <%
                                                }
                                            %>
                                        </a>
                                    </td>
                                    <td class="td_gris_l">
                                        <p align="center">
                                            <bean:write name="myWFs" property="dateCreationWF"/>
                                        </p>
                                    </td>
                                    <td class="td_gris_l">
                                        <p align="center">
                                            <%
                                                if(myWFs.getDateExpireWF().contains("4005")){
                                                	//filtramos por año
                                            %>
                                                    N/A
                                            <%
                                                }else {
                                            %>
                                                	<bean:write name="myWFs" property="dateExpireWF"/>
                                            <%
                                                }
                                            %>
                                        </p>
                                    </td>
                                    <td class="td_gris_l">
                                        <p align="center">
                                            <bean:write name="myWFs" property="dateCompletion"/>
                                        </p>
                                    </td>
                                </tr>
                            </logic:iterate>
                        </table>
                    </logic:present>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>
<script language="javascript" event="onload" for="window">
	try{
		window.parent.document.getElementById("totalOrdersTask").innerHTML="(<%=Integer.parseInt(String.valueOf(request.getAttribute("taskRequestSize")))%>)";
		if(window.parent.document.getElementById("totalMyTask").innerHTML!="(0)"){
			window.parent.document.getElementById("totalMyTask").style.display="";
		} else {
			window.parent.document.getElementById("totalMyTask").style.display="none";
		}
	}catch(e){}
</script>