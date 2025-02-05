<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,com.desige.webDocuments.util.Encryptor"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    //verificamos si ya esta en un atributo el id del documento que es grabada en activefactory
    //en tal caso que no sea asi, la sacamos del request a travez de  ToolsHTML.parseParametersWonderWare y en el action
    // ActiveFactory.java inicializamo el atributo documentoActiveFactory 
    String documentoActiveFactory=request.getAttribute("documentoActiveFactory")!=null?(String)request.getAttribute("documentoActiveFactory"):"";
    if (ToolsHTML.isEmptyOrNull(documentoActiveFactory)){
       documentoActiveFactory = ToolsHTML.parseParametersWonderWare(request);
    }
    StringBuffer onLoad = new StringBuffer("");
    String error = (String)ToolsHTML.getAttributeSession(session,"error",true);




    if ((error != null) && (error.compareTo("") != 0)) {
        onLoad.append("  onLoad=\"showMessage('").append(error).append("');linkActive();\"");
        session.removeAttribute("error");
    } else {
        onLoad.append("onLoad=\"linkActive();\"");
    }
    String usuarioActFactory=request.getAttribute("usuarioActFactory")!=null?(String)request.getAttribute("usuarioActFactory"):"";
    String passwordActFactory=request.getAttribute("passwordActFactory")!=null?(String)request.getAttribute("passwordActFactory"):"";

    if (!"".equalsIgnoreCase(usuarioActFactory)){
        onLoad = new StringBuffer("");
        onLoad.append("onLoad=\"javascript:sendForm();\"");
    }
%>
<html:html xhtml="true" locale="true">
<head>
<title><%=rb.getString("login.title")%></title>
<jsp:include page="meta.jsp" /> 
<script language="JavaScript">
    function Aceptar(forma) {
        if (forma.btnOk) {
            forma.btnOk.disabled = true;
        }
      //  alert(forma.action);
        forma.submit();
	}

    function showMessage(mensaje,op){
        alert(mensaje);
    }
    function checkKey(evt,forma) {
        var charCode = (evt.which) ? ect.which : event.keyCode;
        if (charCode == 13) {
            Aceptar(forma);
        }
    }
    function linkActive() {
        document.login.user.focus();
    }
    function sendForm() {
      //  alert(document.login.action);
        document.login.submit();
    }
</script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInic" <%=onLoad.toString()%>>

<div class="txtInput">
    <html:form action="/inicioActiveFactory.do">
        <input type="hidden" name="url" value="<%=session.getAttribute("url")%>"/>
        <input type="hidden" name="documentoActiveFactory" value="<%=documentoActiveFactory%>"/>
        <input type=hidden name="usuarioActFactory" value="<%=usuarioActFactory%>">
        <input type=hidden name="passwordActFactory" value="<%=passwordActFactory%>">
        <table width="100%" align=center border=0>
            <tr>
                <td align="left" class="txt">
                    &nbsp;
                    <%=rb.getString("login.user")%>
                </td>
            </tr>
            <tr>
                <td align="center">
                    <html:text property="user" style="width: 160px" styleClass="classText" onkeypress="javascript:checkKey(event,this.form);"/>
                </td>
            </tr>
            <tr>
                <td align="left" class="txt">
                    &nbsp;
                    <%=rb.getString("login.pass")%>
                </td>
            </tr>
            <tr>
                <td align="center">
                    <html:password property="clave" style="width: 160px" styleClass="classText" onkeypress="javascript:checkKey(event,this.form);" />
                </td>
            </tr>
            <tr>
                <td align="center" valign="middle" >
                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:Aceptar(document.getElementById('login'));" name="btnOk" id="btnOk"/>
                </td>
            </tr>
        </table>
    </html:form>
</div>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>

<table width="50%" border="0" align="right">

  <tr>
    <td width="30%"  style="width: 160px" styleClass="classText"><%=rb.getString("sch.version")%></td>
    <td width="100%" class="txt">&nbsp;4.0</td>
  </tr>
  <tr>
    <td width="30%"  style="width: 160px" styleClass="classText"><%=rb.getString("lic.propietario0")%></td>
    <td width="100%" class="txt">&nbsp;<%=com.desige.webDocuments.utils.ToolsHTML.getLicenceOwner()%></td>
  </tr>
  <tr>
    <td  style="width: 160px" styleClass="classText"><%=rb.getString("lic.licencia0")%></td>
    <td class="txt">&nbsp;<%=Encryptor.usuarios%>&nbsp;&nbsp;<%=rb.getString("lic.licencia1")%></td>
  </tr>
  <tr>
    <td  style="width: 160px" styleClass="classText"><%=rb.getString("lic.tipo0")%></td>
    <td class="txt">&nbsp;<%=rb.getString("lic.tipo1")%></td>
  </tr>
  <tr>
    <td style="width: 160px" styleClass="classText"></td>
    <%--20060818--%>
    <td class="txt"><%=rb.getString("lic.caduca")%><%=rb.getString("lic.caduca1")%></td>
  </tr>

</table>

</body>
</html:html>
