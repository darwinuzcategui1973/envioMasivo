<!-- /**
 * Title: editSecurityUsersIRecord.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *
 * </ul>
 * </ul>
 * @author Ing. Simon Rodriguez (SR)
 * Changes:<br/>
 * <ul>
 *      <li>18/05/2005 (SR)se agrego el showCheckBox toEditRegisterI </li>
 *      <li>02/06/2005 (SR)se agrego el showCheckBox toImpresionI </li>
 *      <li>10/06/2005 (SR)se agrego la funcion para seleccionar todos los checkbox</li>
 *      <li>30/06/2005 Se agrego logic:notEqual nodeType para validar que la localidad no viera Seguridad Documentos </li>
 *      <li>19/07/2005 Se movio seguridad.toMove,seguridad.toAddDocument dentro de logic:notEqual nodeType para validar que la localidad no viera Seguridad Documentos </li>
 *      <li>27/04/2006 (NC) Cambios en Condiciones para mostrar Opciones de Seguridad del Documento
 *      <li>27/04/2006      Se agregó cambos de Seguridad para ver Documentos </li>
 *      <li>30/06/2006 (NC) Cambios Menores en errores </li>
  </ul>
 */ -->
<%@ page import="java.util.ResourceBundle,com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    String cmd = (String)request.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String txt = "";
    String txtExplorer = "seguridad.toRead";
    //System.out.println("[editSecurityUsersI]");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageView
{
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Geneva, Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script language="JavaScript">
    function sendData(forma) {
        document.getElementById("btnOK").disabled=true;
        document.getElementById("btnCancel").disabled=true;
        forma.submit();
    }
    function updateCheck(check,field){
        if (check.checked){
            field.value = 1;
        } else{
            field.value = 0;
        }
    }

    function cancelar(forma) {
        forma.action = "loadAllUserRecord.do";
        forma.submit();
    }
      function ChequearTodos(chkbox) {
               palabras = new Array(document.forms[0].elements.length);
               var j=-1;
               for (var i=0;i < (document.forms[0].elements.length);i++) {
               var nombre=document.forms[0].elements[i];
                   if  (nombre.name.charAt(nombre.name.lastIndexOf('I'))=='I'){
                        palabras[++j]=nombre.name.substring(0,nombre.name.length-1);
                   }
               }
                arreglo= new Array(j);
                for (var k=0;k<=j;k++){
                     arreglo[k]=palabras[k];
               }
             for (var l=0;l<=j;l++){
               var nom0=arreglo[l];
               for (var i=0;i < (document.forms[0].elements.length);i++) {
                    var nom1=document.forms[0].elements[i];
                    if (nom0==nom1.name){
                       if ((nom1.type=="hidden")){
                           if (chkbox.checked==false){
                                nom1.value=0;
                             }else{
                                nom1.value=1;
                            }
                        }
                    }
               }
               for (var i=0;i < (document.forms[0].elements.length);i++) {
                 var  elemento=document.forms[0].elements[i];
                 if (elemento.type == "checkbox"){
                   elemento.checked = chkbox.checked
                 }
               }
           }
        }
    function toggleDiv(gn)  {
        var g = document.all(gn);
        if( g.style.display == "none") {
            g.style.display = "";
        } else {
            g.style.display = "none";
        }
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<html:form action="/updateSecurityUserRecord.do">
    <logic:present name="securityUser" scope="session">
        <bean:define id="sec" name="securityUser" scope="session" type="com.desige.webDocuments.seguridad.forms.PermissionUserForm" />
        <html:hidden property="command"/>
        <html:hidden property="idPerson"/>
        <html:hidden property="nodeType"/>
        <html:hidden property="rout"/>

        <input type="hidden" name="idStruct" value='<%=session.getAttribute("idStruct")%>'/>
        <input type="hidden" name="idDocument" value='<%=session.getAttribute("idDocument")%>'/>
        <input type="hidden" name="idNodeSelected" value='<%=session.getAttribute("idStruct")%>'/>
        <input type="hidden" name="nameDocument" value='<%=request.getAttribute("nameDocument")%>'/>
        <% txt=rb.getString("rcd.security.toUser"); %>
        <table border="0" width="100%">
            <tr>
                <td class="pagesTitle">
                    <%=rb.getString("seguridad.title") + " " + txt%>
                </td>
            </tr>
            <tr>
                <td>
                    <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                    <%=rb.getString("seguridad.title") + " " + txt%>:&nbsp;
                                    <bean:write name="securityUser" property="rout"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
        <table align=center border=0 width="100%">
            <tr>
                <td>
                    <table align=center border=0 width="100%">
                        <tr>
                            <td width="24%">
                            </td>
                            <td width="26%">
                            </td>
                            <td width="24%">
                            </td>
                            <td width="26%">
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" height="26" valign="middle">
                                <%=rb.getString("seguridad.idUser")%>:
                            </td>
                            <td colspan="3" class="td_gris_l">
                                <bean:write name="securityUser" property="idUser"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.nombre")%>:
                            </td>
                            <td class="td_gris_l" colspan="3">
                                <bean:write name="securityUser" property="nameUser"/>
                            </td>
                        </tr>
                        <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command">
                             <tr>
                                <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                   <%=rb.getString("seguridad.toCheckTodos")%>:
                                </td>
                                 <td class="td_gris_l">
                                      <%=ToolsHTML.showCheckBoxTodos("toCheckTodosI","ChequearTodos(this)",String.valueOf(sec.getToCheckTodos()).trim(),"1")%>
                                      <html:hidden property="toCheckTodos" />
                                  </td>

                                  <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                      <%=rb.getString("rcd.security.modifySecurity")%>:
                                  </td>
                                  <td class="td_gris_l">
                                      <%=ToolsHTML.showCheckBox("toUpdateI","updateCheck(this.form.toUpdateI,this.form.toUpdate)",String.valueOf(sec.getToUpdate()).trim(),"1")%>
                                       <html:hidden property="toUpdate" />
                                   </td>
                              </tr>

                             <tr>
                                <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                   <%=rb.getString("rcd.security.generateReport")%>:
                                </td>
                                 <td class="td_gris_l">
                                      <%=ToolsHTML.showCheckBox("toGenerateI","updateCheck(this.form.toGenerateI,this.form.toGenerate)",String.valueOf(sec.getToGenerate()).trim(),"1")%>
                                      <html:hidden property="toGenerate" />
                                  </td>

                                  <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                      <%=rb.getString("rcd.security.sendMessage")%>:
                                  </td>
                                  <td class="td_gris_l">
                                      <%=ToolsHTML.showCheckBox("toSendI","updateCheck(this.form.toSendI,this.form.toSend)",String.valueOf(sec.getToSend()).trim(),"1")%>
                                       <html:hidden property="toSend" />
                                   </td>
                              </tr>

                             <tr>
                                <td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                   <%=rb.getString("rcd.security.exportReport")%>:
                                </td>
                                 <td class="td_gris_l">
                                      <%=ToolsHTML.showCheckBox("toExportI","updateCheck(this.form.toExportI,this.form.toExport)",String.valueOf(sec.getToExport()).trim(),"1")%>
                                      <html:hidden property="toExport" />
                                  </td>

                                  <!--<td class="titleLeft" style="background: url(img/btn250.gif); background-repeat: no-repeat" valign="middle" height="26">
                                      <%=rb.getString("rcd.security.print")%>: -->
                                  <td>
                                  </td>
                                  <td class="td_gris_l">
                                      <!-- <%=ToolsHTML.showCheckBox("toPrintI","updateCheck(this.form.toPrintI,this.form.toPrint)",String.valueOf(sec.getToPrint()).trim(),"1")%> -->
                                       <html:hidden property="toPrint" />
                                   </td>
                              </tr>
                        </logic:equal>
                    </table>
                </td>
            </tr>
            <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command" >
                <tr>
                    <td>
                        <table align=center border=0 width="100%">
                            <tr>
                                <td width="24%">
                                </td>
                                <td width="26%">
                                </td>
                                <td width="24%">
                                </td>
                                <td width="26%">
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </logic:equal>
        </table>
        <table align=center border=0 width="100%">
             <tr>
                <td colspan="4" class="td_gris_l">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <center>
						<a id="btnOK" class="boton botonLinkBack" id="lnkEditar" name="lnkEditar"  onClick="javascript:sendData(document.securityUser)" >
                            <%=rb.getString("btn.ok")%>
						</a>
                        &nbsp;
						<a id="btnCancel" class="boton botonLinkBack" id="lnkEditar" name="lnkEditar" onClick="javascript:cancelar(document.securityUser)" >
                            <%=rb.getString("btn.cancel")%>
						</a>
                    </center>
                </td>
            </tr>
        </table>
    </logic:present>
</html:form>

</body>
</html>
