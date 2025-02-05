

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>

<!--/**
 * Title: sacop_intouchMostrarLista.jsp<br>
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
<script language=javascript src="./estilo/funciones.js"></script>
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
       location.href = "administracionMain.do";
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
        forma.idactivefactorydocument.value=num;
        forma.id.value = num;
        forma.cmd.value = '<%=SuperActionForm.cmdEdit%>';
        forma.submit();
    }
    function salvar(form){
        if (validar(form)){
            botones(form);
            form.submit();
        } else {
            alert("<%=rb.getString("err.notValueInField")%>");
        }
    }

    function validar(forma){
        if (forma.descripcion.value.length==0) {
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

     function newsacop_intouch(nuevo) {

        document.newsacop_intouch1.action = "newsacop_intouch.do?nuevo="+nuevo;
        document.newsacop_intouch1.submit();

 }

function conftagname(forma) {
      //  forma.action="loadTagnameConf.do";


       forma.action="loadTagname.do";
        forma.submit();
}
 function showDocument(idDoc) {
	    var formaSelection = document.getElementById("Selection");
        formaSelection.idDocument.value = idDoc;
        formaSelection.action = "showDataDocument.do";
        formaSelection.submit();
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<br/>
<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
       <tr>
            <td colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                                <%=rb.getString("scpintouch.nombre")%>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
    <logic:present name="dataTable" scope="session">
        <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <!-- Paginación -->
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("sizeParam");
            Users users = (Users)session.getAttribute("user");
            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
        %>


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

                            <td width="25%" class="td_titulo_C">
                               <!--<div align="center">-->
                                <%=rb.getString("scpintouch.sacop")%>
                               <!--</div>-->
                            </td>
                              <td width="*" class="td_titulo_C">
                               <!--<div align="center">-->
                               <%=rb.getString("scpintouch.activo")%>

                               <!--</div>-->
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
                                  <div align="center">
                                       <a href="javascript:newsacop_intouch('<%=bean.getId()%>')" class="ahref_b">
                                        <%=ToolsHTML.cortarCadena(bean.getDescript()!=null?bean.getDescript():"")%>
                                       </a>
                                  </div>
                                </td>
                                 <td class='td_<%=num%>'>
                                  <div align="center">

                                         <logic:equal name="bean" property="contact" value="1">
                                              <%=rb.getString("scpintouch.habiltado")%>
                                         </logic:equal>
                                        <logic:notEqual name="bean" property="contact" value="1">
                                              <%=rb.getString("scpintouch.deshabiltado")%>
                                         </logic:notEqual>


                                  </div>
                                </td>
                            </tr>
                        </logic:iterate>
                   </table>
                </td>
            </tr>
        </form>

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

                        <form name="formPagingPage" method="post" action="sacop_intouchMostrarLista.jsp">
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
                               <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
                                    <tr>
                                        <td width="86%" valign="top" align="center">
                                            <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:newsacop_intouch('-1');" />
                                            &nbsp;
                                            <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                                            &nbsp;
                                            &nbsp;
                                            <input type="button" class="boton" value="<%=rb.getString("scpintouch.tagname")%>" onClick="javascript:conftagname(this.form);" />
                                        </td>
                                    </tr>
                                </table>


                    </html:form>
                </td>
            </tr>
        </table>



</table>
</body>
</html>
