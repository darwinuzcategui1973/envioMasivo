<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: titulosplanillassacop.jsp<br>
 * Copyright: (c) 2005 Desige Servicios de Computación<br/>
 *
  * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 06-12-2005 (SR)  Creaciòn
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
    //System.out.println("info = " + info);
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
    if (inputReturn==null){
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
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript" src="./estilo/popcalendar.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>

<script type="text/javascript">
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

   function validateRadio() {
	    var formaSelection = document.getElementById("selection");
        for (var i=1; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked){
                msg = formaSelection.elements[i].value;
                p = msg.indexOf(",");
                formaSelection.id.value = msg.substring(0,p);
                formaSelection.desc.value = msg.substring(p+1,msg.length);
                return true;
            }
        }
        return false;
    }
    function edit(num){
        var formEdit='<%=(String)session.getAttribute("formEdit")%>';
        forma = document.<%=(String)session.getAttribute("formEdit")%>;
        forma.numtitulosplanillas.value=num;
        forma.id.value = num;
        forma.cmd.value == '<%=SuperActionForm.cmdEdit%>';
        forma.submit();
    }
    function salvar(form){
        if (validar(form)){
           // alert(form.cmd.value);
            botones(form);
            form.submit();
        } else {
            alert("<%=rb.getString("err.notValueInField")%>");
        }
    }

	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function validar(forma){
	    forma.titulosplanillas.value = forma.titulosplanillas.value.trim();
        if (forma.titulosplanillas.value.length==0) {
            return false;
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
    	var formaSelection = document.getElementById("selection");
        if (!validateRadio()){
            alert('<%=rb.getString("error.selectValue")%>');
            return false;
        } else {
            setValue(formaSelection.id.value,formaSelection.desc.value);
        }
     }

     function paging_OnClick(pageFrom) {
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
    function youAreSure(form){
      if (confirm("<%=rb.getString("areYouSure")%>")) {
         form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
         //alert("form.action="+form.action);
         form.submit();
      }
    }


     function updateCheck(check,field) {
        if (check.checked){
            field.value = "7";
        } else{
            field.value = 0;
        }
    }

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
            <html:hidden property="numtitulosplanillas" value=""/>
            <html:hidden property="titulosplanillas" value=""/>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
            <% if (isInput) { %>
                <html:hidden property="input" value="<%=input%>"/>
                <html:hidden property="value" value="<%=value%>"/>
                <html:hidden property="nameForma" value="<%=nameForma%>"/>
            <% } %>
        </html:form>

        <form name="selection" id="selection" action="">
            <input type="hidden" name="id"/>
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
                                                <%=rb.getString("admin.scpplanillas")%>
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
                                            <input name="id" type="radio" value="<%=bean.getId()%>,<%=bean.getDescript()%>"/>
                                        <%}%>
                                        <a href="javascript:edit('<%=bean.getId()%>')" class="ahref_b">
                                           <%=ToolsHTML.cortarCadena(bean.getDescript()!=null?bean.getDescript():"")%>
                                        </a>
                                       <% if (!ToolsHTML.isEmptyOrNull(bean.getAditionalInfo())){%>
                                              <a href="javascript:showNorms('<%=bean.getId()%>')" class="ahref_b">
                                              &nbsp;&nbsp;&nbsp;<img src="img/pendings.gif" width="16" height="16">
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
                        <html:hidden property="numtitulosplanillas" value=""/>
                        <html:hidden property="titulosplanillas" value=""/>
                        <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);"/>
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="window.close();"/>
                 </html:form>
                </center>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <%
            if (size.compareTo("0") > 0){
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

                        <form name="formPagingPage" method="post" action="titulosplanillassacop.jsp">
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
                <td align="center">
                    <html:form action='<%=(String)session.getAttribute("newManager")%>'>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                         <html:hidden property="numtitulosplanillas" value=""/>
                         <html:hidden property="titulosplanillas" value=""/>
                        <% if (isInput) { %>
                            <html:hidden property="input" value="<%=input%>"/>
                            <html:hidden property="value" value="<%=value%>"/>
                            <html:hidden property="nameForma" value="<%=nameForma%>"/>
                        <%}%>
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
                <html:hidden property="cmd" value='<%=cmd%>'/>
                <html:hidden property="id"/>
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
                                            <%=rb.getString("admin.scpplanillas")%>
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
                             <html:text property="titulosplanillas" size="40"/>
                        </td>
                    </tr>
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
            </html:form>
        </td>
    </tr>
    </logic:notEqual>
</table>
</body>
</html>
<script language="javascript">
	function cerrar(){
		try{
			window.opener.clear();
		}catch(e){}
	}
	window.onunload=cerrar;
</script>
