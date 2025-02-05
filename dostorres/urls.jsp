<!-- urls.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    String info = (String)request.getAttribute("info");
    String back = (String)request.getAttribute("back");
    
	int lineas=Constants.LINEAS_POR_PAGINA;
    int pintada=0;
    
    String mostrar = ""; // para cuando este en modo de insertar
    
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");        
    } else{// El Usuario No se ha Logeado.... => Lo reboto...
        response.sendRedirect(rb.getString("href.logout"));
    }
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
        session.removeAttribute("info");
    }
    if (ToolsHTML.checkValue(back)) {
        onLoad.append(";window.parent.cancelarUrl(document.editarUrls,'loadUrls.do');");
        session.removeAttribute("back");
    }
    info = (String)session.getAttribute("error");
    if (ToolsHTML.checkValue(info)){
        onLoad.append(";alert('").append(info).append("')");
        session.removeAttribute("error");
    }
    if (onLoad.length()>0){
        onLoad.append("\"");
    }
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
<script language="JavaScript">
    function edit(num){
    	window.parent.agregarNuevoUrl();
        document.urls.cmd.value = "<%=SuperActionForm.cmdEdit%>";
        document.urls.id.value = num;
        document.urls.submit();
    }

    function cancelar(form,action){
    	if((typeof form)!='undefined'){
	        form.action = action;
	        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
	        form.submit();
        }
    }

    function incluir(form) {
        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
    }
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
    
    function salvar(form) {
        var reg = /^http[s]?:\/\/\www\.\w[\.\w]+$/i;
        //Validando el Nombre del LINK
        form.name.value = form.name.value.trim();
        form.url.value = form.url.value.trim();
        form.type.value = form.type.value.trim();
        if (form.name.value.length == 0) {
            alert('<%=rb.getString("url.badName")%>');
            return false;
        }
        if (form.url.value.length == 0) {
            alert('<%=rb.getString("url.badURL")%>');
            return false;
        }
        //Validando Formato de la Dirección WEB
//        if (!(reg.test(form.url.value))) {
            <%--alert('<%=rb.getString("url.badURL")%>');--%>
//            return false;
//        }
        form.submit();
    }
    function youAreSure(form) {
        if (confirm("<%=rb.getString("areYouSure")%>")) {
            form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
            form.submit();
        }
    }

    function editField(nodoIdent){
        var hWnd = null;
        hWnd = window.open("validity.jsp?id="+nodoIdent,"editvalidity","width=750,height=400,resizable=yes,scrollbars=yes");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing=0 cellPadding="0" align=center border="0" width="100%"  height="100%">
	<logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
		<% mostrar="none"; %>
	</logic:notEqual>
    <logic:present name="userURLs" scope="session">
        <%
            String from = request.getParameter("from");
            String size = (String)session.getAttribute("size");
            Users users = (Users)session.getAttribute("user");
            //PaginPage bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
            PaginPage bean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(lineas));
        %>
        <html:form action="/loadEdit.do">
            <html:hidden property="id" value=""/>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdLoad%>"/>
        </html:form>
        <tr class="<%=mostrar%>">
            <td valign="top">
                <table align=center border="0" width="100%" height="100%"  cellspacing="1" cellpadding="1"> 
                    <tr>
                        <td colspan="4" height="1%">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
                                <tbody>
                                	<tr>
                                		<td width="25%">
					                        <!-- Inicio de Paginación -->
                                            <table class="paginadorNuevo" align="left">
                                                <tr>
                                                    <td align="center">&nbsp;<img src="images/inicio2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                                       onclick="paging_OnClick(0)">&nbsp;
                                                    </td>
                                                    <td align="center"> <img src="images/left2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())-1)%>)">
                                                    </td>
                                                    <td align="center" width="120px">
                                                            <font size="1" color="#000000">
                                                                <%=rb.getString("pagin.title")+ " "%>
                                                                <%=Integer.parseInt(bean.getPages())+1%>
                                                                <%=rb.getString("pagin.of")%>
                                                                <%=bean.getNumPages()%>
                                                            </font>
                                                    </td>
                                                    <td align="center"> <img src="images/right2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                                       onclick="paging_OnClick(<%=(Integer.parseInt(bean.getPages())+1)%>)">&nbsp;
                                                    </td>
                                                    <td align="center"> <img src="images/fin2.gif"
                                                       class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                                       onclick="paging_OnClick(<%=bean.getNumPages()%>)">
                                                    </td>
                                                </tr>
                                            </table>
					                        <!-- final de Paginación -->
                                		</td>
                                        <td class="td_title_bc" width="50%">
                                            <%=rb.getString("urls.myLinks")%>
                                        </td>
                                        <td class="td_title_bc" width="25%">
                                            &nbsp;
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr >
                        <td width="5%" class="td_titulo_C barraBlue"  height="1%">
                            <img src="img/Edit.gif" alt="<%=rb.getString("btn.edit")%>" >
                        </td>

                        <td width="20%" class="td_titulo_C barraBlue">
                            <%=rb.getString("url.name")%>
                        </td>
                        <td width="*" class="td_titulo_C barraBlue">
                            <%=rb.getString("url.url")%>
                        </td>
                        <td width="20%" class="td_titulo_C barraBlue">
                            <%=rb.getString("url.type")%>
                        </td>
                    </tr>
                    
                    <logic:iterate id="link" name="userURLs" indexId="ind" offset="<%=bean.getDesde()%>" length="<%=bean.getCuantos()%>"
                        type="com.desige.webDocuments.enlaces.forms.UrlsActionForm" scope="session">
                        <%  int item = ind.intValue()+1;
                            pintada++;
                            int num = item%2;
                        %>
                        <tr>
                            <td class='td_<%=num%>'>
                                <a href="javascript:edit('<%=link.getId().trim()%>')" class="ahref_b">
                                    <p align="center">
                                        <%=item%>
                                    </p>
                                </a>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=link.getName()%>
                            </td>
                            <td class='td_<%=num%>'>
                                <%
                                    if (link.getUrl()!=null && link.getUrl().startsWith("http:")) {
                                %>
                                        <a href="<%=link.getUrl()%>" class="ahref_b"  target="_blank">
                                <%
                                    } else {
                                %>
                                    <a href="http://<%=link.getUrl()%>" class="ahref_b"  target="_blank">
                                <%
                                    }
                                    out.println(link.getUrl());
                                %>
                                </a>
                            </td>
                            <td class='td_<%=num%>'>
                                <%=link.getType()%>
                            </td>
                        </tr>
                    </logic:iterate>
                        <%for(pintada++;pintada<=lineas;pintada++){%>
                        	<tr>
                        		<td colspan="4">&nbsp;</td>
                        	</tr>
                        <%}%>
               </table>
            </td>
        </tr>
        <%
            if (size.compareTo("0") > 0){
        %>
                        <!-- Inicio de Paginación -->
                        <form name="formPagingPage" method="post" action="urls.jsp">
                          <input type="hidden" name="from"  value="">
                          <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                        </form>
                        <!-- Fin de Paginación -->
            <%
                }
            %>
      
    </logic:present>

   
    <logic:equal name="cmd" value="<%=SuperActionForm.cmdLoad%>">
            <tr class="none">
                <td>
                    <html:form action="/newUrlsUser.do">
                        <input type="hidden" name="backURL" value="true"/>
                        <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
                        <!--
                            <table width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                                <tr>
                                    <td align="center">
                                        <input type="button" class="boton" value="<%=rb.getString("btn.incluir")%>" onClick="javascript:incluir(this.form);"/>
                                        &nbsp;
                                        <input type="button" class="boton" value="<%=rb.getString("btn.back")%>" onClick="javascript:cancelar(this.form,'loadPerfil.do?backURL=true');"/>
                                    </td>
                                </tr>
                            </table>
                         -->
                    </html:form>
                </td>
            </tr>
        </logic:equal>

    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
    <%
        String forma = "/editUrls.do";
        //System.out.println("[urls.jsp] cmd = " + cmd);
        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = "/newUrlsUser.do";
        }
    %>
    <tr>
        <td valing="top">
            <html:form action="<%=forma%>">
                <html:hidden property="cmd"/>
                <html:hidden property="id"/>
                <table align=center border="0" width="100%" valing="top">
                    <tr>
                        <td colspan="2">
                            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                                <tbody>
                                    <tr>
                                        <td class="td_title_bc">
                                            <%=rb.getString("urls.title")%>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>

                    <tr>
<%--                        <td class="td_orange_l_b" width="15%">--%>
                        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="15%" valign="middle">
                            <%=rb.getString("url.name")%>:
                        </td>
                        <td width="*" class="td_gris_l">
                             <html:text property="name" size="30" styleClass="classText"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("url.url")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="url" size="30" styleClass="classText"/> <font size="-1" color="blue">(Ejemplo: www.focus.com.ve)</font>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <%=rb.getString("url.type")%>:
                        </td>
                        <td class="td_gris_l">
                            <html:text property="type" size="30" styleClass="classText"/>
                        </td>
                    </tr>
                    <tr  style="display:none">
                        <td colspan="2" align="center">
                            <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" onClick="javascript:salvar(this.form);"/>
                            <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" onClick="javascript:youAreSure(this.form);"/>
                            </logic:notEqual>
                                &nbsp;
                            	<input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form,'loadUrls.do');" />
                        </td>
                    </tr>
                </table>
                <table align=center border="0" width="100%" height="100%">
                	<tr>
                		<td>
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
