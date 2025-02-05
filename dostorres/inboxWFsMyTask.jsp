<!-- inboxWFsMyTask.jsp --><%@ page language="java" %>
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
        <input type="hidden" name="myTask" value="true" />
        <input type="hidden" name="idWorkFlow" value="" />
        <input type="hidden" name="toShow" value="true" />
        <input type="hidden" name="owner" value="false" />
        <input type="hidden" name="isNew" value="false" />
        <input type="hidden" name="isFlexFlow" value="false" />
        <input type="hidden" name="rowUser" value="0" />
        <input type="hidden" name="origen" value="inboxWFs" />
            
        <table align=center border=0 width="100%">
            <tr>
                <td>
                    <logic:present name="myTask" scope="session">
                        <table width="100%" border="0" >
                            <%
                                isNew = "false";
                            %>
                            <logic:iterate id="myWFs" name="myTask" indexId="ind" type="com.desige.webDocuments.workFlows.forms.BaseWorkFlow" scope="session" >
		                    <%
		                        int item = ind.intValue()+1;
		                        int num = item%2;
		                    %>
		                    <tr class='fondo_<%=num%>'>
                                    <td class="txt_b" width="18%">
                                        <% if (myWFs.isNewWF()) { %>
                                                <!--SIMON 13 DE JULIO 2005 INICIO -->
                                                <input name="myTask" type="checkbox" value="<%=myWFs.getNumWF()%>,<%=myWFs.isFlexFlow()%>"/>
                                                <!--SIMON 13 DE JULIO 2005 FIN -->
                                        <%      isNew = "true";
                                            } else {
                                                isNew = "false";
                                        %>
                                                <input name="myTask" type="checkbox" value="<%=myWFs.getNumWF()%>,<%=myWFs.isFlexFlow()%>"/>
                                        <% } %>
                                            <bean:write name="myWFs" property="nameOwner"/>
                                    </td>
                                    <td width="30%">
                                       <%--ydavila <a href="javascript:showWF(,false,<%=isNew%>,0,<bean:write name="myWFs" property="flexFlow"/>)" class="ahref_b">--%>
                                        <a href="javascript:showWF('<bean:write name="myWFs" property="numWF"/>',false,<%=isNew%>,0,<bean:write name="myWFs" property="flexFlow"/>)" class="ahref_b">
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
                                    <td class="txt_b" width="18%">
                                        <p align="center">
                                            <bean:write name="myWFs" property="dateCreationWF"/>
                                        </p>
                                    </td>
                                    <td class="txt_b" width="17%">
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
                                    <td class="txt_b" width="17%">
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
		window.parent.document.getElementById("totalMyTask").innerHTML="(<%=Integer.parseInt(String.valueOf(request.getAttribute("myTaskSize")))%>)";
		if(window.parent.document.getElementById("totalMyTask").innerHTML!="(0)"){
			window.parent.document.getElementById("totalMyTask").style.display="";
		} else {
			window.parent.document.getElementById("totalMyTask").style.display="none";
		}
	}catch(e){}
</script>