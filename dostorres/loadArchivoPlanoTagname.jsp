<%@ page import="java.util.Locale,
                 java.util.ResourceBundle,
                 java.text.SimpleDateFormat,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.sacop.actions.LoadSacop"%>
<%@ page import="java.util.Date"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String) ToolsHTML.getAttribute(request,"info",true);
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }

    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session,"application.sacop");
    String styleTxt = "width: 300px; height: 19px";

%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language=javascript src="./estilo/popcalendar.js"></script>
<script language=javascript src="./estilo/fechas.js"></script>
<script language="JavaScript">



function validar( ){
     if (Trim(forma.descripcion.value)==""){
         alert('<%=rb.getString("scp.vald4")%>');
         return false;
     }
     return true;
}








    function salvar(forma) {
        if (forma.nameFile.value==""){
            alert('<%=rb.getString("scpintouch.archplanocargar")%>');
            return false;
        }
        forma.submit();
    }


   function cancelar(forma) {
        forma.action="loadTagname.do";
        forma.submit();
    }


     function TagnameConf() {
        abrirVentana("loadTagnameConf.do",1000,400)
 }





</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
  <tr>
     <td width="86%" valign="top">
        <html:form action="/LoadArchivoPlanoTagnameII.do" enctype="multipart/form-data">
              <table align=center border=0 width="100%">
                 <tr>
                  <td colspan="4">
                     <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" width="*" height="18">
                                   <logic:present name="archivoprocesado" scope="request">
                                        <%=rb.getString("scpintouch.archplano")%>:<%=(String)request.getAttribute("archivoprocesado")%>
                                   </logic:present>
                                    <logic:notPresent name="archivoprocesado">
                                        <%=rb.getString("scpintouch.archplano")%>
                                   </logic:notPresent>

                                </td>
                            </tr>
                        </tbody>
                    </table>
                    </td>
                    <td></td>
                </tr>



               <tr>
                  <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                       <%=rb.getString("doc.upFile")%>:
                   </td>
                   <td class="td_gris_l">
                       <html:file property="nameFile"/>
                   </td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
               </tr>
               <tr>
                   <td></td>
                    <td colspan="6" align="left" style="padding: 0px" class="td_gris_l">
                           &nbsp;
                           <input type="button" class="boton" value="<%=rb.getString("scpintouch.archprocesar")%>" onClick="javascript:salvar(this.form);" name="btnOK" />
                           &nbsp;
                           <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" name="btnCancel" />
                           &nbsp;
                           &nbsp;
                           <input type="button" class="boton" value="<%=rb.getString("scpintouch.tipotagconf")%>" onClick="javascript:TagnameConf();" />
                   </td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
                   <td></td>
               </tr>
            </table>
           </html:form>
            <script language="JavaScript" event="onload" for="window">
            </script>
<%--
        </td>
    </tr>
</table>
--%>
</body>
</html>
