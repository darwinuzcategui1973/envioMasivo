<jsp:include page="richeditDocType.jsp" /> 
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>

<!--/**
 * Title: editFieldAccept.jsp<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo   (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 22-09-2005 (SR)  se agrego el bean data para la session  typeOtroFormato</li>
 *      <li> 07-07-2005 (SR)  Se valido si existe una session descript para
                              MsgInEditor(document.editNorms.descript,document.editNorms.richedit);</li>
 *      <li> 18-08-2005 (SR)  se agrego doc.upFile  para la norma</li>
 *      <li> 20-04-2006 (SR)  Se modifico el border del link igual a cero, en caso de tener un documento enlazado.</li>
 *      <li> 24-04-2006 (SR)  Se coca un punto en esta direccion ./estilo/funciones.js</li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 *      <li> 26-06-2006 (SR)  Se coloca un espacio vacio</li>
 * <ul>
 */-->
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String)request.getAttribute("info");
    String loadForm = (String)session.getAttribute("loadManager");
    if (ToolsHTML.isEmptyOrNull(loadForm)) {
        loadForm = "";
    }
    if (!ToolsHTML.isEmptyOrNull(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }
    String err = (String)session.getAttribute("error");
    if (!ToolsHTML.isEmptyOrNull(err)) {
        onLoad.append(" onLoad=\"alert('").append(err).append("')\"");
        session.removeAttribute("error");
    }

    boolean isInput = true;
    String inputReturn = (String)session.getAttribute("input");
    if (inputReturn==null) {
        inputReturn = "";
        isInput = false;
    }
    String valueReturn = (String)session.getAttribute("value");
    if (valueReturn==null){
        valueReturn = "";
    }
    String input = (String)ToolsHTML.getAttribute(request,"input");
    String value = (String)ToolsHTML.getAttribute(request,"value");

    String nameForma = ToolsHTML.getAttribute(request,"nameForma")!=null?(String)ToolsHTML.getAttribute(request,"nameForma"):"";


    String cmd = (String)request.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<jsp:include page="richeditHead.jsp" /> 
<script language="JavaScript">
function showNorms(idNorms) {
    var idNorms = escape(idNorms);
    abrirVentana("viewDocumentNorms.jsp?idNorms=" + idNorms ,800,600);

}
  function botones(forma) {
        if (forma.btnDelete) {
            forma.btnDelete.disabled = true;
        }
        if (forma.btnIncluir) {
            forma.btnIncluir.disabled = true;
        }
        if (forma.btnSalvar) {
            forma.btnSalvar.disabled = true;
        }
        if (forma.btnCancelar) {
            forma.btnCancelar.disabled = true;
        }
    }
    function setValue(id,desc){
        <%=inputReturn%>
        <%=valueReturn%>
        window.close();
    }

    function cancelar(form){
        form.action = "<%=loadForm%>";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
    
    function habilitarCerrar() {
    	window.opener.habilitarBotones();
    	window.close();
    }
    function habilitarCerrarOnClose() {
    	window.opener.habilitarBotones();
    }
    function habilitarCerrarOnUnLoad() {
		window.onunload=habilitarCerrarOnClose;
    }

	setInterval("habilitarCerrarOnUnLoad()",1000);
	habilitarCerrarOnUnLoad();
	
   function validateRadio() {
   		var listaInput = document.getElementsByTagName("input");
        for (var i=1; i < listaInput.length;i++) {
            if (listaInput[i].type=='radio' && listaInput[i].checked){
                msg = listaInput[i].value;
                p = msg.indexOf(",");
                document.frmSelection.id.value = msg.substring(0,p);
                document.frmSelection.desc.value = msg.substring(p+1,msg.length);
                return true;
            }
        }
        return false;
    }
    function edit(num){
        forma = document.<%=(String)session.getAttribute("formEdit")%>;
        forma.id.value = num;
        forma.idDocument.value = num;
        forma.rejectDocument.value = num;
        forma.type.value = "0";
    }
    
    function salvar(form){
        if (validar(form)){
           //en tal caso de agregar una norma, este metodo de botonoes deshabilita las opciones de eliminar,agregar,cancelar
           botones(form);
           form.submit();
        } else {
            alert("<%=rb.getString("err.notValueInField")%>");
        }
    }

    function validar(forma){
        if (forma.name.value.length==0) {
            return false;
        }
        if (forma.descript) {
            forma.descript.value = forma.richedit.docXHtml;
            if (forma.descript.value==0) {
                return false;
            }
        }
        return true;
    }

    function incluir(form) {
        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
    }
    function Buscar(form){
        form.submit();
    }
    function check() {
        if (!validateRadio()) {
            alert('<%=rb.getString("error.selectValue")%>');
            return false;
        } else {
            forma = document.<%=(String)session.getAttribute("formEdit")%>;
	        forma.action = "newRegister.do?flujoParametrico=true";
	        window.onunload=null;
	        forma.submit();
        }
     }

     function paging_OnClick(pageFrom) {
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         form.submit();
      }
    }


//     function updateCheck(check,field) {
//        if (check.checked){
//            field.value = "7";
//        } else{
//            field.value = 0;
//        }
//    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<br/>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <logic:present name="dataTable" scope="session">
        <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("sizeParam");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>
        <html:form action='<%=(String)session.getAttribute("loadEditManager")%>'>
            <html:hidden property="id" value=""/>
            <html:hidden property="idDocument" value=""/>
            <html:hidden property="idNodeSelected" value=""/>
            <html:hidden property="rejectDocument" value=""/>
            <html:hidden property="type" value=""/>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <% } %>
        </html:form>

        <form name="frmSelection" action="">
            <input type="hidden" name="id"/>
            <html:hidden property="idDocument" value=""/>
            <html:hidden property="idNodeSelected" value='<%=String.valueOf(session.getAttribute("nodeActive"))%>'/>
            <html:hidden property="rejectDocument" value=""/>
            <html:hidden property="type" value=""/>
            <input type="hidden" name="desc"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <%}%>
            <tr>
                <td>
                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                        <tr>
                            <td>
                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc">
                                                <%=rb.getString("cat.title")%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td width="*" class="td_titulo_C">
                                <%=rb.getString("url.name")%>
                            </td>
                        </tr>
                        <logic:iterate id="bean" name="dataTable" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                        type="com.desige.webDocuments.utils.beans.Search" scope="session">
                            <%
                                int item = ind.intValue()+1;
                                int num = item%2;
                            %>
                            <tr>
                                <td class='td_<%=num%>'>
                                        <% if (isInput) { %>
                                            <input name="codigo" type="radio" value="<%=bean.getId()%>,<%=bean.getDescript()%>" onclick="edit('<%=bean.getId()%>')" />
                                        <%}%>
                                        <%--ToolsHTML.cortarCadena(bean.getDescript()!=null?bean.getDescript():"")--%>
                                        <%=(bean.getDescript()!=null?bean.getDescript():"")%>
                                       <% if (!ToolsHTML.isEmptyOrNull(bean.getAditionalInfo())){%>
                                              <a href="javascript:showNorms('<%=bean.getId()%>')" class="ahreMenu">
                                              &nbsp;&nbsp;&nbsp;<img src="img/pendings.gif" width="16" height="16" border=0>
                                              </a>
                                        <%}%>
                                </td>
                            </tr>
                        </logic:iterate>
                   </table>
                </td>
            </tr>
        </form>
        <tr>
            <td>
                <center>
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <html:hidden property="idDocument" value=""/>
			            <html:hidden property="rejectDocument" value=""/>
			            <html:hidden property="idNodeSelected" value=""/>
                        <html:hidden property="type" value=""/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <% } %>
                        <%--<html:hidden property="input" value="<%=input%>"/>--%>
                        <%--<html:hidden property="value" value="<%=value%>"/>--%>
                        <%--<html:hidden property="nameForma" value="<%=nameForma%>"/>--%>
	                   <input type="button" class="boton" value="<%=rb.getString("btn.create")%>" onClick="javascript:check();" name="btnOK" />
	                   &nbsp;
	                   <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:habilitarCerrar();" name="btnOK" />
                     </html:form>
                    &nbsp;
                </center>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <%
            if (size.compareTo("0") > 0) {
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
                                <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                <%=rb.getString("pagin.of")%>
                                <%=Pagingbean.getNumPages()%>
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
                                   onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                              </td>
                              <td align="center"> <img src="img/right.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                   onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                              </td>
                              <td align="center"> <img src="img/End.gif" width="24" height="24"
                                   class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                   onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                              </td>
                          </tr>
                        </table>

                        <form name="formPagingPage" method="post" action="editFieldAccept.jsp">
                            <input type="hidden" name="from"  value="">
                            <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                            <% if (isInput) { %>
                                <html:hidden property="input" value="<%=input%>"/>
                                <html:hidden property="value" value="<%=value%>"/>
                                <html:hidden property="nameForma" value="<%=nameForma%>"/>
                            <%}%>
                        </form>
                        <!-- Fin de Paginación -->
                    </td>
                </tr>
            <% } %>

        </logic:equal>
    </logic:present>
            <tr>
                <td>
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <% } %>
                        <logic:notPresent name="dataTable" scope="session">
                            <logic:notEqual value="<%=SuperActionForm.cmdInsert%>" name="cmd" >
                                <table width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                                    <tr>
                                        <td>
                                            <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);"/>
                                        </td>
                                    </tr>
                                </table>
                            </logic:notEqual>
                        </logic:notPresent>
                    </html:form>
                </td>
            </tr>
        </table>


        <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
    <%
        String forma = (String)session.getAttribute("editManager");
        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = (String)session.getAttribute("newManager");
        }
    %>

    <tr>
        <td>
            <html:form action="<%=forma%>" enctype='multipart/form-data' >
                <html:hidden property="cmd"/>
                <html:hidden property="id"/>
	            <html:hidden property="idDocument" value=""/>
	            <html:hidden property="rejectDocument" value=""/>
	            <html:hidden property="idNodeSelected" value=""/>
	            <html:hidden property="type" value=""/>
                <% if (isInput) { %>
                    <html:hidden property="input" value="<%=input%>"/>
                    <html:hidden property="value" value="<%=value%>"/>
                    <html:hidden property="nameForma" value="<%=nameForma%>"/>
                <%}%>
                <!--Se agrega una norma nueva -->
                <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="2">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc">
                                            <%=rb.getString("cat.update")%>
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
                        <td width="16%" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("url.name")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                             <html:text property="name" size="40" styleClass="classText"/>
                        </td>
                        <!-- SIMON 21 DE JUNIO 2005 IICIO -->
                        <logic:present name="typeOtroFormato" scope="session" >
                            <bean:define id="data" name="typeOtroFormato" type="com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm" scope="session" />
                            <logic:notPresent name="valor" scope="session" >
                             <logic:present name="formEdit" scope="session">
                              <logic:equal value="editTypeDoc" name="formEdit" scope="session">
                                     <tr>
                                         <td class="titleLeft" height="26" style="background: url(img/btn140x4.png); background-repeat: no-repeat" valign="middle">
                                            <%=rb.getString("tipoformato")%>
                                         </td>
                                         <td class="td_gris_l">
                                             <%
                                                 if (!ToolsHTML.isNumeric(data.getType())) {
                                                     data.setType("0");
                                                 }
                                             %>
                                             <%=ToolsHTML.getRadioButton(rb,"tDoc.typeDoc","type",Integer.parseInt(data.getType()),null)%>
                                         </td>
                                   </tr>
                            </logic:equal>
                           </logic:present>
                          </logic:notPresent>
                       </logic:present>
                            <!-- SIMON 21 DE JUNIO 2005 FIN -->
                    </tr>
                    <logic:present name="descript" scope="session" >
                      <logic:present name="formEdit" scope="session">
                       <logic:equal value="editNorms" name="formEdit" scope="session">-
                            <tr>
                               <!-- SIMON 18 AGOSTO 20056 INICIO -->
                               <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
                                   <%=rb.getString("doc.upFile")%>:
                               </td>
                               <td class="td_gris_l">
                                   <html:file property="nameFile" styleClass="classText"/>
                                </td>    
                               <!--SIMON 18 AGOSTO 2005 FIN -->
                            </tr>
                        </logic:equal>
                     </logic:present>
                       <tr>
                            <td colspan="2" class="titleLeft" height="22" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("url.description")%>
                            </td>
                        </tr>
                        <tr>
                            <td class="fondoEditor" colspan="2" width="*" class="td_gris_l" height="300">
                                 <html:textarea property="descript" cols="30" rows="5" style="display:none"/>
								  <jsp:include page="richedit.jsp">
									<jsp:param name="richedit" value="richedit"/>
								  </jsp:include>
                            </td>
                        </tr>
                    </logic:present>
                    <tr>
                        <td colspan="2" align="center">
                            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="btnSalvar"  onClick="javascript:salvar(this.form);"/>
                            <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>"  name="btnIncluir" onClick="javascript:incluir(this.form);"/>
                                &nbsp;
                                <logic:notPresent name="valor" scope="session" >
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btnDelete" onClick="javascript:youAreSure(this.form);"/>
                                </logic:notPresent>
                             </logic:notEqual>
                            &nbsp;
                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);"/>
                        </td>
                    </tr>
                </table>
                   <script language="JavaScript" event="onload" for="window">
                   	alert(1);
                   <logic:present name="descript" scope="session" >
                           <%if ("/editNorm.do".equalsIgnoreCase(forma)) {%>
                                MsgInEditor(document.editNorms.descript,document.editNorms.richedit);
                           <%}%>
                   </logic:present>
                    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
                        <logic:present name="info" scope="request" >
                            alert('<%=request.getAttribute("info")%>');
                            <%request.removeAttribute("info");%>
                        </logic:present>
                        <logic:present name="error" scope="session" >
                            alert('<%=session.getAttribute("error")%>');
                            <%session.removeAttribute("error");%>
                        </logic:present>
                    </logic:notEqual>
                  </script>
            </html:form>
        </td>
    </tr>
    </logic:notEqual>
</table>
</body>
</html>
