<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)ToolsHTML.getAttributeSession(session,"usuario",false);
    //System.out.println("perfil = " + ok);
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else {
        String link = rb.getString("href.logout");
        response.sendRedirect(link+"?target=parent");
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session,"application.actXUser");
    String styleTxt = "width: 300px; height: 19px";
    String numGo = request.getParameter("numGo");
    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }
    String idStruct = (String)ToolsHTML.getAttribute(request,"idStruct");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
  <tr>
    <td width="86%" valign="top">
		<form action="" method="post" name="associate">
            
        <table width="100%" border="0">
          <tr> 
            <td colspan="2" class="pagesTitle">
                <%=rb.getString("cbs.associate")%>
            </td>
          </tr>
          <tr> 
            <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="12%" height="26" valign="middle"> 
              <%=rb.getString("act.act")%>:
            </td>
            <td width="*" valign="middle" >
                <select name="activity" id="activity" style="width: 300px" onchange="javascript:alert(this.value)" >
                    <option value="0">______________________________________</option>
                    <logic:present name="activities" scope="session">
                        <logic:iterate id="act" name="activities" type="com.desige.webDocuments.activities.forms.Activities" scope="session">
                            <option value="<%=act.getNumber()%>"><%=act.getName()%></option>
                        </logic:iterate>
                    </logic:present>
                </select>
            </td>
          </tr>
		  <tr>
		  	<td colspan="2">
				<table width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
					<tr>
						<td colspan="3">
							<table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
								<tbody>
									<tr>
										<td class="td_title_bc" width="45%"align="center" height="21">
											<%=rb.getString("wf.users")%>
										</td>
										<td width="5%">&nbsp;
											
										</td>
										<td width="*" align="center">
											<%=rb.getString("wf.participation")%>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
					<tr> 
					    <td align="center">
						    <select name="select" size="5" style="width:210px;">
								<logic:present name="usuarios" scope="session">
                                     <logic:iterate id="bean" name="usuarios" scope="session" type="com.desige.webDocuments.utils.beans.Search">
<%--                                         <% if (!userActive.equalsIgnoreCase(bean.getId())) { %>--%>
                                            <logic:present name="showCharge" scope="session">
                                                <option value="<%=bean.getId()%>"><%=bean.getAditionalInfo()%></option>
                                            </logic:present>
                                            <logic:notPresent name="showCharge" scope="session">
                                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%> </option>
                                            </logic:notPresent>
<%--                                         <% } %>--%>
                                     </logic:iterate>
                                 </logic:present>
						    </select>
					    </td>
						<td width="10%">
							<input type="button" class="BUTTON" value=" > " style="width:28px;"  onClick="javascript:mover(this.form.groups,this.form.groupsSelected);" />
							<br/>
							<input type="button" class="BUTTON" value=" < " style="width:28px;" onClick="javascript:mover(this.form.groupsSelected,this.form.groups);" />
						</td>
					    <td align="center">
						    <select name="select" size="5" style="width:210px;">
						    </select>
					    </td>
                    </tr>
				</table>
			</td>
		  </tr>
          <tr> 
            <td colspan="2" align="center"> 
        	    <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar();"> 
    	        &nbsp; 
	            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:showHome();">
              </td>
          </tr>
          <tr> </tr>
        </table>
			</form>
        </td>
    </tr>
</table>
</body>
</html>
