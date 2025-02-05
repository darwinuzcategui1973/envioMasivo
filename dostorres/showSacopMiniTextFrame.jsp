<!-- showSacopMiniTextFrame -->
<jsp:include page="richeditDocType.jsp" /> 
<%@ page import="java.util.ResourceBundle,java.io.*,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String inputReturn = "";
    if (!ToolsHTML.isEmptyOrNull((String)session.getAttribute("input"))){
          inputReturn = session.getAttribute("input")!=null?(String)session.getAttribute("input"):"";
          inputReturn = inputReturn.replaceAll("opener.document","window.parent.document");
    }
     String valueReturn="";
    if (!ToolsHTML.isEmptyOrNull((String)session.getAttribute("value"))){
           valueReturn = session.getAttribute("value")!=null?(String)session.getAttribute("value"):"";
    }

    //String valueReturn = session.getAttribute("value")!=null?(String)session.getAttribute("value"):"";
    if (valueReturn==null){
        valueReturn = "";
    }
     if (inputReturn.contentEquals("<FONT ")){
         inputReturn="";
     }
     if (valueReturn.startsWith("<FONT ")){
         valueReturn="";
         inputReturn="";
     }

%>

<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<jsp:include page="richeditHead.jsp" /> 

<script language="JavaScript">
    function setValue(id){
        <%=inputReturn%>
        window.close();
    }


    function closeWin() {
//        alert('< %=request.getParameter("variable")%>');
		  updateRTEs();
          
          <%if(inputReturn.indexOf(".noconformidades.")!=-1){%>
          //	 if(isEmptyRicheditValue(document.getDate.richedit.value)) {
          //      alert('<%=rb.getString("E0129")%>');
          //      return false;
          //	 }
          <%}%>

          setValue(document.getDate.richedit.value);
//        top.window.close();
    }
   function cancelar() {
        top.window.close();
    }
</script>
</head>

<body class="bodyInternas" style="margin:0" >
<form name="getDate"  style="margin:0">
    <center>
        <table align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=request.getParameter("title")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
           <tr>
                <td colspan="2">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td class="fondoEditor" align="center" width="100%" colspan="2">
					  <jsp:include page="richedit.jsp">
						<jsp:param name="richedit" value="richedit"/>
						<jsp:param name="build" value="false" />
					  </jsp:include>
		              <script language="JavaScript">
			                  richedit.html = <%=inputReturn.substring(0,inputReturn.indexOf("=")) %>;
			                  richedit.build();
			          </script>
                </td>
            </tr>
            <tr style="display:none">
                <td align="center" >
                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:closeWin();"/>
                     &nbsp;
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" name="btnCancel"/>
                </td>
            </tr>
        </table>
    </center>
</form>
</body>
</html>

