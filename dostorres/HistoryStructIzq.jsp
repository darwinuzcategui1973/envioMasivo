<!--historystructIzq.jsp-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
var active=true;
function load(id){
	var formaSelection = document.getElementById("Selection");
    formaSelection.histSelect.value = id;
    //formaSelection.target="_parent";
    //formaSelection.submit();
    
    with(formaSelection){
    	window.open(action+"?histSelect="+histSelect.value+"&idNodeSelected="+idNodeSelected.value,"_parent");
    }
}
</script>
</head>

<body class="bodyInternas">
<logic:present name="historystruct" >
    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
            <logic:present name="loadDocs">
                <form name="Selection" id="Selection" action="<%=(session.getAttribute("f1")!=null?basePath+"loadHistoryFiles.do":basePath+"loadHistoryDocs.do")%>">
                	<input type="hidden" name="histSelect" value=""/>
                	<input type="hidden" name="idNodeSelected" value="<%=session.getAttribute("nodeActive")%>"/>
	            </form>
            </logic:present>
            <logic:notPresent name="loadDocs">
                <form name="Selection" id="Selection" action="<%=basePath%>loadHistoryStruct.do">
        	        <input type="hidden" name="histSelect" value=""/>
            	    <input type="hidden" name="idNodeSelected" value="<%=session.getAttribute("nodeActive")%>"/>
    	        </form>
            </logic:notPresent>
    <!-- END Form -->
    <table align=center border=0 width="100%">
        <tr>
            <td valign="top" class="td_center_blue">
                <logic:present name="nameStruct" scope="session">
                    <%=session.getAttribute("nameStruct")%>
                </logic:present>
            </td>
        </tr>
        <logic:iterate id="struct" name="historystruct" type="com.desige.webDocuments.structured.forms.DataHistoryStructForm" scope="session" >
            <tr>
                <td width="100%" class="td_gris_l">
                    <a class="ahref_b" href="javascript:load('<%=struct.getIdHistory()%>');">
                        <%=rb.getString("cbs.historyType"+struct.getTypeMovement()) + " " + struct.getDateChange()%>
                        <%
                            if (!ToolsHTML.isEmptyOrNull(struct.getMayorVer())||!ToolsHTML.isEmptyOrNull(struct.getMinorVer())) {
                                out.println(rb.getString("sch.version") + " " + struct.getMayorVer()+ "." + struct.getMinorVer());
                            }
                        %>
                    </a>
                </td>
            </tr>
        </logic:iterate>
       	<%if(session.getAttribute("f1")==null){%>
        <logic:present name="loadDocs">
            <tr>
                <td width="100%" class="td_gris_l">
                    <a class="ahref_b" href="javascript:load('-1');">
                        <%=rb.getString("imp.solic")%>
                    </a>
                </td>
            </tr>
        </logic:present>
        <%}%>
        <%if(ToolsHTML.showDistributionList()){%>
        <tr>
            <td width="100%" class="td_gris_l">
                <a class="ahref_b" href="javascript:load('-2');">
                    <%=rb.getString("imp.preview")%>
                </a>
            </td>
        </tr>
        <%}%>
    </table>
</logic:present>
<logic:notPresent name="historystruct">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
        <tr>
            <td class="td_orange_l_b">
                <%=rb.getString("wf.selecItem")%>
            </td>
        </tr>
    </table>
</logic:notPresent>
</body>
</html>
