<!-- /**
 * Title: editSecurityUsers.java <br/>
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
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function sendData(forma) {
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
        forma.action = "loadAllUser.do";
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

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<html:form action="/updateSecurityUser.do">
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

        <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command" >
            <%
                txt = rb.getString("security.toStruct"+sec.getNodeType());
            %>
        </logic:equal>
        <logic:notEqual value="<%=Constants.cmdToStruct%>" name="securityUser" property="command" >
            <%
                txt = rb.getString("security.toDocument");
                txtExplorer = "seguridad.toReadDoc";
            %>
        </logic:notEqual>

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
                                    <%=rb.getString("seguridad.title") + " " + txt%>
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
                <tr>
                    <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" height="26" valign="middle">
                        <%=rb.getString("seguridad.idUser")%>:
                    </td>
                    <td colspan="3" class="td_gris_l">
                        <bean:write name="securityUser" property="idUser"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                        <%=rb.getString("seguridad.nombre")%>:
                    </td>
                    <td class="td_gris_l" colspan="3">
                        <bean:write name="securityUser" property="nameUser"/>
                    </td>
                </tr>
                <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command">
                 <!-- SIMON 9 DE JUNI 2005 INICIO-->
                     <tr>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                           <%=rb.getString("seguridad.toCheckTodos")%>:
                        </td>
                         <td class="td_gris_l">
                              <logic:equal value="1" name="securityUser" property="toCheckTodos" >
                                     <%=ToolsHTML.showCheckBoxTodos("toCheckTodosI","ChequearTodos(this)","1","1")%>
                              </logic:equal>
                              <logic:notEqual value="1" name="securityUser" property="toCheckTodos" >
                                      <%=ToolsHTML.showCheckBoxTodos("toCheckTodosI","ChequearTodos(this)","0","1")%>
                              </logic:notEqual>
                              <html:hidden property="toCheckTodos" />
                          </td>

                          <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                              <%=rb.getString("seguridad.toDoFlows")%>:
                          </td>
                          <td class="td_gris_l">
                             <logic:equal value="1" name="securityUser" property="toDoFlows" >
                                <%=ToolsHTML.showCheckBoxTodos("toDoFlowsI","updateCheck(this.form.toDoFlowsI,this.form.toDoFlows)","1","1")%>
                              </logic:equal>
                              <logic:notEqual value="1" name="securityUser" property="toDoFlows" >
                                <%=ToolsHTML.showCheckBoxTodos("toDoFlowsI","updateCheck(this.form.toDoFlowsI,this.form.toDoFlows)","0","1")%>
                               </logic:notEqual>
                               <html:hidden property="toDoFlows" />
                           </td>
                      </tr>
                      <!-- SIMON 9 DE JUNI 2005 FIN-->
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toView") + " " + rb.getString("security.toStructLabel" + sec.getNodeType())%>
                        </td>
                        <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toView" >
                                <%=ToolsHTML.showCheckBox("toViewI","updateCheck(this.form.toViewI,this.form.toView)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toView" >
                                <%=ToolsHTML.showCheckBox("toViewI","updateCheck(this.form.toViewI,this.form.toView)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toView" />
                        </td>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toEdit")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toEdit" >
                                <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toEdit" >
                                <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toEdit" />
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.addFolder")%>:
                        </td>
                        <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toAddFolder" >
                                <%=ToolsHTML.showCheckBox("toAddFolderI","updateCheck(this.form.toAddFolderI,this.form.toAddFolder)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toAddFolder" >
                                <%=ToolsHTML.showCheckBox("toAddFolderI","updateCheck(this.form.toAddFolderI,this.form.toAddFolder)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toAddFolder" />
                        </td>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.addProcess")%>:
                        </td>
                        <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toAddProcess" >
                                <%=ToolsHTML.showCheckBox("toAddProcessI","updateCheck(this.form.toAddProcessI,this.form.toAddProcess)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toAddProcess" >
                                <%=ToolsHTML.showCheckBox("toAddProcessI","updateCheck(this.form.toAddProcessI,this.form.toAddProcess)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toAddProcess" />
                        </td>
                    </tr>
                    <tr>
                     <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.inline")%>
                     </td>
                     <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toDocinLine" >
                               <%=ToolsHTML.showCheckBox("toDocinLineI","updateCheck(this.form.toDocinLineI,this.form.toDocinLine)","1","1")%>
                             </logic:equal>
                             <logic:notEqual value="1" name="securityUser" property="toDocinLine" >
                                <%=ToolsHTML.showCheckBox("toDocinLineI","updateCheck(this.form.toDocinLineI,this.form.toDocinLine)","0","1")%>
                              </logic:notEqual>
                                 <html:hidden property="toDocinLine" />
                      </td>
                    </tr>
                </logic:equal>
                <html:hidden property="toRead" value="0"/>
                <logic:notEqual value="<%=Constants.cmdToStruct%>" name="securityUser" property="command">
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toEdit")%>:
                        </td>
                        <td colspan="3" class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toEdit" >
                                <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toEdit" >
                                <%=ToolsHTML.showCheckBox("toEditI","updateCheck(this.form.toEditI,this.form.toEdit)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toEdit" />
                        </td>
                    </tr>
                </logic:notEqual>
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toAdmon")%>: 
                        </td>
                        <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toAdmon" >
                                <%=ToolsHTML.showCheckBox("toAdmonI","updateCheck(this.form.toAdmonI,this.form.toAdmon)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toAdmon" >
                                <%=ToolsHTML.showCheckBox("toAdmonI","updateCheck(this.form.toAdmonI,this.form.toAdmon)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toAdmon" />
                        </td>
                        <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toDelete")%>:
                        </td>
                        <td class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toDelete" >
                                <%=ToolsHTML.showCheckBox("toDeleteI","updateCheck(this.form.toDeleteI,this.form.toDelete)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toDelete" >
                                <%=ToolsHTML.showCheckBox("toDeleteI","updateCheck(this.form.toDeleteI,this.form.toDelete)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toDelete" />
                        </td>
                    </tr>
                   <tr>
                         <td width="22%" class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                            <%=rb.getString("seguridad.toReadDoc")%>:
                        </td>
                        <td width="22%" class="td_gris_l">
                            <logic:equal value="1" name="securityUser" property="toViewDocs" >
                                <%=ToolsHTML.showCheckBox("toViewDocsI","updateCheck(this.form.toViewDocsI,this.form.toViewDocs)","1","1")%>
                            </logic:equal>
                            <logic:notEqual value="1" name="securityUser" property="toViewDocs" >
                                <%=ToolsHTML.showCheckBox("toViewDocsI","updateCheck(this.form.toViewDocsI,this.form.toViewDocs)","0","1")%>
                            </logic:notEqual>
                            <html:hidden property="toViewDocs" />
                        </td>
                    </tr>
                    <logic:notEqual value="<%=Constants.cmdToStruct%>" name="securityUser" property="command">
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("security.checkOut")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="checkOut" >
                                    <%=ToolsHTML.showCheckBox("checkOutI","updateCheck(this.form.checkOutI,this.form.checkOut)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="checkOut" >
                                    <%=ToolsHTML.showCheckBox("checkOutI","updateCheck(this.form.checkOutI,this.form.checkOut)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="checkOut" />
                            </td>
                             <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("wf.impresion")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toImpresion" >
                                    <%=ToolsHTML.showCheckBox("toImpresionI","updateCheck(this.form.toImpresionI,this.form.toImpresion)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toImpresion" >
                                    <%=ToolsHTML.showCheckBox("toImpresionI","updateCheck(this.form.toImpresionI,this.form.toImpresion)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toImpresion" />
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("wf.editRegister")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toEditRegister" >
                                    <%=ToolsHTML.showCheckBox("toEditRegisterI","updateCheck(this.form.toEditRegisterI,this.form.toEditRegister)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toEditRegister" >
                                    <%=ToolsHTML.showCheckBox("toEditRegisterI","updateCheck(this.form.toEditRegisterI,this.form.toEditRegister)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toEditRegister" />
                            </td>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toAprove")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toAprove" >
                                    <%=ToolsHTML.showCheckBox("toAproveI","updateCheck(this.form.toAproveI,this.form.toAprove)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toAprove" >
                                    <%=ToolsHTML.showCheckBox("toAproveI","updateCheck(this.form.toAproveI,this.form.toAprove)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toAprove" />
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toMoveDocs")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toMoveDocs" >
                                    <%=ToolsHTML.showCheckBox("toMoveDocsI","updateCheck(this.form.toMoveDocsI,this.form.toMoveDocs)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toMoveDocs" >
                                    <%=ToolsHTML.showCheckBox("toMoveDocsI","updateCheck(this.form.toMoveDocsI,this.form.toMoveDocs)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toMoveDocs" />
                            </td>
                         </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toReview")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toReview" >
                                    <%=ToolsHTML.showCheckBox("toReviewI","updateCheck(this.form.toReviewI,this.form.toReview)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toReview" >
                                    <%=ToolsHTML.showCheckBox("toReviewI","updateCheck(this.form.toReviewI,this.form.toReview)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toReview" />
                            </td>
                        </tr>
                </logic:notEqual>
                <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command" >
                    <!--SIMON 30 DE JUNIO 2005 INICIO -->
                    <logic:notEqual value="1" name="securityUser" property="nodeType" >
                    <!--SIMON 30 DE JUNIO 2005 FIN -->
                        <tr>
                            <td colspan="4" class="td_gris_l">
                                &nbsp;
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="21">
                                                <%=rb.getString("security.docStruct")%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toMove")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toMove" >
                                    <%=ToolsHTML.showCheckBox("toMoveI","updateCheck(this.form.toMoveI,this.form.toMove)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toMove" >
                                    <%=ToolsHTML.showCheckBox("toMoveI","updateCheck(this.form.toMoveI,this.form.toMove)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toMove" />
                            </td>
                            <logic:equal value="<%=Constants.cmdToStruct%>" name="securityUser" property="command" >
                                <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                    <%=rb.getString("seguridad.toAddDocument")%>:
                                </td>
                                <td class="td_gris_l">
                                    <logic:equal value="1" name="securityUser" property="toAddDocument" >
                                        <%=ToolsHTML.showCheckBox("toAddDocumentI","updateCheck(this.form.toAddDocumentI,this.form.toAddDocument)","1","1")%>
                                    </logic:equal>
                                    <logic:notEqual value="1" name="securityUser" property="toAddDocument" >
                                        <%=ToolsHTML.showCheckBox("toAddDocumentI","updateCheck(this.form.toAddDocumentI,this.form.toAddDocument)","0","1")%>
                                    </logic:notEqual>
                                    <html:hidden property="toAddDocument" />
                                </td>
                            </logic:equal>
                        </tr>
                        <tr>
                            <td width="22%" class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toEdit")%>:
                            </td>
                            <td width="*" class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toEditDocs" >
                                    <%=ToolsHTML.showCheckBox("toEditDocsI","updateCheck(this.form.toEditDocsI,this.form.toEditDocs)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toEditDocs" >
                                    <%=ToolsHTML.showCheckBox("toEditDocsI","updateCheck(this.form.toEditDocsI,this.form.toEditDocs)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toEditDocs" />
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toAdmon")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toAdmonDocs" >
                                    <%=ToolsHTML.showCheckBox("toAdmonDocsI","updateCheck(this.form.toAdmonDocsI,this.form.toAdmonDocs)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toAdmonDocs" >
                                    <%=ToolsHTML.showCheckBox("toAdmonDocsI","updateCheck(this.form.toAdmonDocsI,this.form.toAdmonDocs)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toAdmonDocs" />
                            </td>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toDelete")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toDelDocs" >
                                    <%=ToolsHTML.showCheckBox("toDelDocsI","updateCheck(this.form.toDelDocsI,this.form.toDelDocs)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toDelDocs" >
                                    <%=ToolsHTML.showCheckBox("toDelDocsI","updateCheck(this.form.toDelDocsI,this.form.toDelDocs)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toDelDocs" />
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toReview")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toReview" >
                                    <%=ToolsHTML.showCheckBox("toReviewI","updateCheck(this.form.toReviewI,this.form.toReview)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toReview" >
                                    <%=ToolsHTML.showCheckBox("toReviewI","updateCheck(this.form.toReviewI,this.form.toReview)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toReview" />
                            </td>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toAprove")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toAprove" >
                                    <%=ToolsHTML.showCheckBox("toAproveI","updateCheck(this.form.toAproveI,this.form.toAprove)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toAprove" >
                                    <%=ToolsHTML.showCheckBox("toAproveI","updateCheck(this.form.toAproveI,this.form.toAprove)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toAprove" />
                            </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("seguridad.toMoveDocs")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toMoveDocs" >
                                    <%=ToolsHTML.showCheckBox("toMoveDocsI","updateCheck(this.form.toMoveDocsI,this.form.toMoveDocs)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toMoveDocs" >
                                    <%=ToolsHTML.showCheckBox("toMoveDocsI","updateCheck(this.form.toMoveDocsI,this.form.toMoveDocs)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toMoveDocs" />
                            </td>
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("security.checkOut")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="checkOut" >
                                    <%=ToolsHTML.showCheckBox("checkOutI","updateCheck(this.form.checkOutI,this.form.checkOut)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="checkOut" >
                                    <%=ToolsHTML.showCheckBox("checkOutI","updateCheck(this.form.checkOutI,this.form.checkOut)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="checkOut" />
                            </td>
                       </tr>
                       <tr>
                           <!-- //SIMON 18 MAYO 2005 -->
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("wf.editRegister")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toEditRegister" >
                                    <%=ToolsHTML.showCheckBox("toEditRegisterI","updateCheck(this.form.toEditRegisterI,this.form.toEditRegister)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toEditRegister" >
                                    <%=ToolsHTML.showCheckBox("toEditRegisterI","updateCheck(this.form.toEditRegisterI,this.form.toEditRegister)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toEditRegister" />
                            </td>
                            <!-- //SIMON 18 MAYO 2005 -->
                            <!-- SIMON 2 DE JUNI 2005 INICIO-->
                            <td class="titleLeft" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle" height="26">
                                <%=rb.getString("wf.impresion")%>:
                            </td>
                            <td class="td_gris_l">
                                <logic:equal value="1" name="securityUser" property="toImpresion" >
                                    <%=ToolsHTML.showCheckBox("toImpresionI","updateCheck(this.form.toImpresionI,this.form.toImpresion)","1","1")%>
                                </logic:equal>
                                <logic:notEqual value="1" name="securityUser" property="toImpresion" >
                                    <%=ToolsHTML.showCheckBox("toImpresionI","updateCheck(this.form.toImpresionI,this.form.toImpresion)","0","1")%>
                                </logic:notEqual>
                                <html:hidden property="toImpresion" />
                            </td>
                           <!-- SIMON 2 DE JUNI 2005 IMPRESION-->
                        </tr>
                    <!-- SIMON INICIO 30 DE JUNIO 2005--->
                    </logic:notEqual>
                    <!-- SIMON FIN 30 DE JUNIO 2005--->
                </logic:equal>
                <tr>
                    <td colspan="4" class="td_gris_l">
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="center">
                        <center>
                            <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:sendData(this.form);" />
                            &nbsp;
                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);" />
                        </center>
                    </td>
                </tr>
        </table>
    </logic:present>
</html:form>

</body>
</html>
