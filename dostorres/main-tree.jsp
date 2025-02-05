<!-- /**
 * Title: main-tree.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>21/04/2006 (NC) Change to show number document </li>
 </ul>
 */ -->
<%@ page language="java" %>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 java.util.Hashtable,
                 com.desige.webDocuments.structured.forms.BaseStructForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.seguridad.forms.PermissionUserForm"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    String nodeActive = session.getAttribute("nodeActive")!=null?(String)session.getAttribute("nodeActive"):"0";
    if (!ToolsHTML.checkValue(nodeActive)) {
        nodeActive = session.getAttribute("idNodeRoot")!=null?(String)session.getAttribute("idNodeRoot"):"0";
        session.setAttribute("nodeActive",nodeActive);
    }
    String cmd = session.getAttribute("cmd")!=null?(String)session.getAttribute("cmd"):null;
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    Hashtable tree = session.getAttribute("tree")!=null?(Hashtable)session.getAttribute("tree"):null;
    Hashtable security = session.getAttribute("security")!=null?(Hashtable)session.getAttribute("security"):null;
%>
<html:html>
    <head>
    <style>
div.transbox
  {
  width: 400px;
  height: 180px;
  margin: 30px 50px;
  background-color: #ffffff;
  border: 1px solid black;
  /* for IE */
  filter:alpha(opacity=60);
  /* CSS3 standard */
  opacity:0.6;
  }
div.transbox p
  {
  margin: 30px 40px;
  font-weight: bold;
  color: #000000;
  }
</style>
        <title>
        </title>
            <meta http-equiv=Content-Type content="text/html; charset=windows-1252">
            <meta content="MSHTML 6.00.2800.1106" name=generator>
            <meta http-equiv=Cache-Control content="no-cache, must-revalidate">
            <meta http-equiv=Pragma content=no-cache>
            <script type="text/javascript">
                if (parent.frames['code']) {
                    if((navigator.appName == "Netscape" || navigator.appName == "Microsoft Internet Explorer") && parseInt(navigator.appVersion) >= 4) {
                        if(parent.frames['code'].MTMDisplayMenu != null) {
                            parent.frames['code'].MTMTrack = true;
//                            setTimeout("parent.frames['code'].MTMDisplayMenu()", 250);
                        }
                    }
                }
            </script>
            <script type="text/javascript">
               var active = true;
               function MTMakeSubmenu(menu) {
                    this.submenu = menu;
                }

                <%=ToolsHTML.getAttributeSession(session,"updateNode", false)%>
                <%=ToolsHTML.getAttributeSession(session,"updateNodeII",true)%>
                <%=ToolsHTML.getAttributeSession(session,"dataNode",true)%>

                function setValueSelected(value,expandir) {
                	var formaSelection = document.getElementById("Selection");
                    if (formaSelection.expandir) {
                        formaSelection.idNodeSelected.value = value;
                        formaSelection.expandir.value = expandir;
                        formaSelection.submit();
                    }
                }

                function isCopy(){
                	var formaSelection = document.getElementById("Selection");
                	alert("<%=rb.getString("cbs.nodeEnd")%>");
                    formaSelection.isCopy.value = 1;
                    formaSelection.idNodeChange.value = formaSelection.idNodeSelected.value;
                }

                function pasteNode() {
                	var formaSelection = document.getElementById("Selection");
                    formaSelection.action = "editStruct.do";
                    formaSelection.cmd.value = "<%=SuperActionForm.cmdPaste%>";
                    formaSelection.target = "_self";
<%--                    formaSelection.target = "_parent";--%>
                    formaSelection.submit();
                }

                function cancelarNodee(){
                	var formaSelection = document.getElementById("Selection");
                    formaSelection.action = "editStruct.do";
                    formaSelection.cmd.value = "";
                    formaSelection.target = "_self";
                    formaSelection.submit();
                }
                
                function showSelect(value){
                	var trContent = document.getElementById("subNodesTable");
                	var trContentWhenLoad = document.getElementById("subNodesTableWhenLoad");
                	if(trContent != null){
                		trContent.style.display = "none";
                		
                		if(trContentWhenLoad != null){
                			trContentWhenLoad.style.display = "";
                		}
                	}
                	var formaSelection = document.getElementById("Selection");
                	formaSelection.idNodeSelected.value = value;
                    formaSelection.expandir.value = true;
                    formaSelection.submit();
                }

                function edit() {
                    document.loadStruct.submit();
                }

                function validar() {
                    if (document.login.docSearch.value.length>0){
                        alert('<%=rb.getString("err.notDocumentSearch")%>');
                    }else{
                        document.login.submit();
                    }
                }

                function salvar(form) {
                    var type;
                    form.target = "_parent";
                    if (form.cmd.value == '<%=SuperActionForm.cmdEdit%>'){
                        type = 0;
                    } else {
                        type = 1;
                    }
                    if (validar(form,type)) {
                        form.submit();
                    }
                }

                function incluir(nodeType) {
                    form = document.insertStruct;
                    form.cmd.value = "<%=SuperActionForm.cmdNew%>";
                    form.nodeType.value = nodeType;
                    form.submit();
                }

                function addDocument() {
                	if('<%=ToolsHTML.isRepositoryDefine(request)%>'=='false') {
                		alert("<%=rb.getString("err.invalidRepository")%>");
                		return;
                	}
                    forma = document.newDocument;
                    forma.target = "_self";
                    forma.submit();
                }

               function noaddDocument() {
                    forma = document.newDocument;
                    forma.target = "_self";
                    alert('<%=rb.getString("cbs.nocrearDocumento")%>');
                }

                function validar(form,type) {
                    if (type==0){
                        if (form.idNode.value==form.idNodeParent.value){
                            alert("<%=rb.getString("err.invalidNodeParent")%>");
                            return false;
                        }
                        if (form.name.value.length == 0){
                            alert("<%=rb.getString("err.invalidName")%>");
                            return false;
                        }
                    }
                    return true;
                }
                function youAreSure() {
                    form = document.editarStruct;
                    if (confirm("<%=rb.getString("areYouSure")%>")) {
                        form.target = "_self";
                        form.submit();
                    }
                }

                function paging_OnClick(pageFrom){
                    document.formPagingPage.from.value = pageFrom;
                    document.formPagingPage.submit();
                }
                function showDocument(idDoc){
                	var formaSelection = document.getElementById("Selection");
                	formaSelection.idDocument.value = idDoc;
                    formaSelection.action = "showDataDocument.do";
                    formaSelection.submit();
                }

                function showStatus(ind) {
                    if (ind==0){
                        self.status = '';
                    }
                }

                function loadHistory() {
                	var forma = document.getElementById("Selection");
                    forma.action = "loadHistoryStruct.do";
                    if ((parent.frames['text'])||(parent.frames['opciones'])){
                        forma.target = "_self";
                    }
                    forma.submit();
                }

                function permission(type,nodeType) {
                	var forma = document.getElementById("Selection");
                    forma.action = "loadAllUser.do?idDocument=null";
                    if (forma.nodeType) {
                        forma.nodeType.value = nodeType;
                    }
                    if (type==1) {
                        forma.action = "loadAllGroups.do?idDocument=null";
                    }
                    if ((parent.frames['text'])||(parent.frames['opciones'])) {
                        forma.target = "_self";
                    }
                    forma.submit();
                }

                function public() {
                	var forma = document.getElementById("Selection");
                    forma.action = "publicDocs.do";
                    forma.submit();
                }

                function showCharge(userName,charge) {
            	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
                    window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
                }
            </script>
        <meta content="MSHTML 6.00.2800.1106" name=generator>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
    <body class="bodyInternas" <%=onLoad%>>
<%--        <!-- Formulario para la Inclusion de Documentos -->--%>
        <html:form action="/newDocument.do" style="margin:0px;">
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdNew%>"/>
            <html:hidden property="nodeType" value="4"/>
            <!--<input type="hidden" name="nexPage" value="newDocument.jsp"/>-->
            <input type="hidden" name="nexPage" value="upploadFile.jsp"/>
        </html:form>
        <!-- Formulario para la Inclusion de nuevas Estructuras -->
        <html:form action="/newStructLoad.do" style="margin:0px;">
            <input type="hidden" name="cmd" value='<%=SuperActionForm.cmdNew%>'/>
            <input type="hidden" name="nodeType" value=""/>
        </html:form>
        <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
        <form name="Selection" id="Selection" action="loadStructMain.do" method="post" style="margin:0px;">
            <input type="hidden" name="idNode" value='<%=nodeActive!=null?nodeActive:"0"%>'/>
            <input type="hidden" name="idNodeSelected" value='<%=nodeActive!=null?nodeActive:"0"%>'/>
            <input type="hidden" name="idNodeChange" value='<%=request.getAttribute("idNodeChange")!=null?request.getAttribute("idNodeChange"):""%>'/>
            <input type="hidden" name="isCopy" value='<%=request.getAttribute("isCopy")!=null?request.getAttribute("isCopy"):""%>'/>
            <input type="hidden" name="movDocument" value='<%=request.getAttribute("movDocument")!=null?request.getAttribute("movDocument"):"0"%>'/>
            <input type="hidden" name="cmd"/>
            <input type="hidden" name="nexPage" value="loadStructMain.do"/>
            <input type="hidden" name="idDocument" value='<%=request.getAttribute("idDocument")!=null?request.getAttribute("idDocument"):""%>'/>
            <input type="hidden" name="idVersion" value=""/>
            <input type="hidden" name="toStruct" value="0"/>
            <input type="hidden" name="expandir" value=""/>
            <input type="hidden" name="nodeType" value=""/>
            <input type="hidden" name="securityPorStructura" value="true"/>
        </form>
        <!-- Formulario para la carga de un nodo en especifico -->
        <html:form action="/loadStructEdit.do" style="margin:0px;">
            <input type="hidden" name="idNode" value="<%=nodeActive!=null?nodeActive:"0"%>">
<%--            <html:hidden property="idNode" value="<%=nodeActive!=null?nodeActive:"0"%>"/>--%>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdEdit%>"/>
        </html:form>
        <html:form action="/editStruct.do" style="margin:0px;">
<%--            <html:hidden property="idNode" value="<%=nodeActive!=null?nodeActive:"0"%>"/>--%>
<%--            <html:hidden property="idNodeParent" value="<%=nodeActive!=null?nodeActive:"0"%>"/>--%>
            <input type="hidden" name="idNode" value="<%=nodeActive!=null?nodeActive:"0"%>">
            <input type="hidden" name="idNodeParent" value="<%=nodeActive!=null?nodeActive:"0"%>">
            <input type="hidden" name="idNodeSelected" value="<%=nodeActive!=null?nodeActive:"0"%>"/>
            <input type="hidden" name="nexPage" value="loadStructMain.do"/>
            <input type="hidden" name="idNodeChange" value="<%=request.getAttribute("idNodeChange")!=null?request.getAttribute("idNodeChange"):nodeActive!=null?nodeActive:"0"%>"/>
            <html:hidden property="cmd" value="<%=SuperActionForm.cmdDelete%>"/>
        </html:form>
        <%
            String title = "";                  
            Users users = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
            if (nodeActive.length() > 0 && tree!=null  ) {
                BaseStructForm forma = tree.get(nodeActive)!=null?(BaseStructForm)tree.get(nodeActive!=null?nodeActive:"0"):null;
                byte value = 0;
                if (forma!=null && forma.getNodeType()!=null && !forma.getNodeType().trim().equals("")) {
                    PermissionUserForm permission = null;
                    if (security!=null) {
                        permission = security.get(nodeActive)!=null?(PermissionUserForm)security.get(nodeActive!=null?nodeActive:"0"):null;
                    }
                    value = (byte)(Integer.parseInt(forma.getNodeType()) + 1);
                    title = rb.getString("cbs.typeNode"+value);
                    boolean isNodeParent = false;
                    boolean isCopy = false;
                    String attIsCopy = (String)request.getAttribute("isCopy");
                    if (!ToolsHTML.isEmptyOrNull(attIsCopy)) {
                        if (((String)request.getAttribute("isCopy")).equalsIgnoreCase("1")) {
                            isCopy = true;
                        }
                    } else if (request.getAttribute("movDocument")!=null) {
                        if (((String)request.getAttribute("movDocument")).equalsIgnoreCase("1")) {
                            isCopy = true;
                        }
                    }
        %>
                    <!-- BEGIN Menu Option Node -->
                        <logic:present name="nodes" scope="session">
                            <%
                                isNodeParent = true;
                            %>
                        </logic:present>
                    <!-- END Menu Option Node -->
                    <table align=center border="0" width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="100%" valign="top">
                                <%
                                    int mails = ((Integer)session.getAttribute("emails")).intValue();
                                    int wfs = ((Integer)session.getAttribute("wfs")).intValue();
                                    String showOptionPublic = (String)request.getAttribute("showOptionPublic");
                                %>

                                <%=ToolsHTML.getOptionStructsII(rb,forma.getNodeType(),isNodeParent,isCopy,
                                                              users.getIdGroup(),mails,wfs,permission,showOptionPublic,forma)%>
                            </td>
                        </tr>
                        <!-- <tr>
                            <td>
                                &nbsp;
                            </td>
                        </tr> -->
                    </table>
                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%" >
                        <tr>
                            <td width="100%" valign="top" >
                                <%
                                    String style = "clsTableTitle";
                                    String height = "21";
                                    if (forma!=null&&forma.getRout().length() > 104) {
                                        style = "clsTableTitle2";
                                        height = "42";
                                    }
                                %>
                                <table class="<%=style%>" width="100%" cellSpacing=0 cellPadding=2 align=center border=0 style="display:none">
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="<%=height%>" >
                                                <%=rb.getString("cbs.location")%>: <%=forma.getRout()%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <table width="100%" cellSpacing="1" cellPadding="1" align="center" border="0" style="border-bottom:1px solid #afafaf;">
                                    <tr style="display:">
                                    	<td style="width:75px;">
                                            <span class="mailTextColor">
	                                            <%=rb.getString("cbs.location")%>:&nbsp;
                                            </span>
                                    	</td>
                                    	<td style="">
                                            <span class="td_gris_l" >
	                                            <%=forma.getRout()%>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td >
                                            <span class="mailTextColor">
                                            	<%=rb.getString("cbs.owner")%>:&nbsp;
                                            </span>
                                    	</td>
                                    	<td>
                                            <span class="td_gris_l">
	                                            <logic:present name="showCharge" scope="session">
	                                                <a href="javascript:showCharge('<%=forma.getOwner()%>','<%=forma.getChargeOwner()%>')" class="ahref_b">
	                                                    <%=forma.getChargeOwner()%></a>
	                                            </logic:present>
	                                            <logic:notPresent name="showCharge" scope="session">
	                                                <a href="javascript:showCharge('<%=forma.getOwner()%>','<%=forma.getChargeOwner()%>')" class="ahref_b">
	                                                    <%=forma.getOwner()%></a>
	                                            </logic:notPresent>
                                            </span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                            <span class="mailTextColor">
	                                            <%=rb.getString("cbs.dateCreation")%>:&nbsp;
                                            </span>
                                            <span class="td_gris_l">
	                                            <%=forma.getDateCreation()%>
	                                        </span>
	                                    </td>
                                    </tr>
                                    <tr>
                                    	<td>
                                            <span class="mailTextColor" >
	                                            <%=rb.getString("cbs.description")%>:&nbsp;
                                            </span>
                                    	</td>
                                    	<td>
                                            <span class="td_gris_l">
	                                            <%=forma.getDescription()%>
	                                        </span>
	                                    </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>

            <%--        <%}%>--%>
                    <logic:equal name="emptyNodes" value="F">
                        <!-- Paginación -->
                        <%

                            String from = request.getParameter("from")!=null?request.getParameter("from"):"";
                            String size = session.getAttribute("size")!=null?(String)session.getAttribute("size").toString():"0";

                            if (!ToolsHTML.isNumeric(size)) {
                                size = "1";
                            }
                            if (!ToolsHTML.isNumeric(from)) {
                                from = "0";
                            }
                            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                            String routImgs = "menu-images/";
                        %>
                        
                        <logic:present name="nodes" scope="session">
                            <tr id="subNodesTableWhenLoad" style="display: none;">
                                <td>
                                    <div class="innerLoad">
                                        Cargando la informaci&oacute;n.<br />
                                        Espere por favor.<br />
                                        <img src="images/loading.gif"/>
                                    </div>
                                </td>
                            </tr>
                            <tr id="subNodesTable">
                                <td>
                                    <!-- BEGIN All SubNodes for Node Selected -->
                                    <table class="clsTableTitle" width="110" cellSpacing=0 cellPadding=0 align=center border=0>
                                        <tbody>
                                            <tr>
                                                <td class="td_title_bc">
                                                    <%=title%>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <table cellSpacing=1 cellPadding=0 align=center border="0" width="100%">
                                        <tr>
                                            <td width="10%" class="td_titulo_C">
                                                <%=rb.getString("cbs.name")%>
                                            </td>
                                            <td width="10%" class="td_titulo_C">
                                                <%=rb.getString("cbs.owner")%>
                                            </td>
                                        </tr>

                                        <logic:iterate id="bean" name="nodes" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                        type="com.desige.webDocuments.structured.forms.BaseStructForm" scope="session">
                                            <%
                                                int item = ind.intValue()+1;
                                                int num = item%2;
                                            %>
                                            <tr>
                                                <td class='td_<%=num%>'>
                                                    <img src="<%=routImgs+bean.getNameIcon()%>" border="0" width="18" height="18">
                                                    <a href="javascript:showSelect('<%=bean.getIdNode()%>')" class="ahref_b"
                                                        onmouseover="javascript:showStatus(0);return true;" onmouseout="showStatus(1);return true;">
                                                        <%=bean.getName()%>
                                                    </a>
                                                </td>
                                                <td class='td_<%=num%>'>
                                                	<div style="width:100%;text-align:center">
<%--                                                    <%=bean.getOwner()%>--%>
                                                    <logic:present name="showCharge" scope="session">
                                                        <a href="javascript:showCharge('<%=bean.getOwner()%>','<%=bean.getChargeOwner()%>')" class="ahref_b">
                                                            <%=bean.getChargeOwner()%>
                                                        </a>
                                                    </logic:present>
                                                    <logic:notPresent name="showCharge" scope="session">
                                                        <a href="javascript:showCharge('<%=bean.getOwner()%>','<%=bean.getChargeOwner()%>')" class="ahref_b">
                                                            <%=bean.getOwner()%>
                                                        </a>
                                                    </logic:notPresent>
                                                    </div>
                                                </td>
                                            </tr>
                                        </logic:iterate>
                                   </table>
                                   <!-- END All SubNodes for Node Selected -->
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
                                                        <br/>
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

                                            <form name="formPagingPage" method="post" action="main-tree.jsp">
                                              <input type="hidden" name="from"  value="">
                                              <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                            </form>
                                            <!-- Fin de Paginación -->
                                        </td>
                                    </tr>
                                <%
                                    }
                                %>
                            </logic:present>

                            <!-- BEGIN for List Documents -->
                            <input type="hidden" name="from" value="main-tree.jsp"/>
                            <logic:present name="documents" scope="session">
                                <tr>
                                    <td>
                                        <!-- BEGIN All SubNodes for Node Selected -->
                                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=0 align=center border=0>
                                            <tbody>
                                                <tr>
                                                    <td class="td_title_bc" >
                                                        <%=rb.getString("cbs.documents")%>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                        <table cellSpacing=1 cellPadding=0 align=center border="0" width="100%" >
                                            <tr>
                                                <td width="35%" class="td_titulo_C">
                                                    <p><%=rb.getString("cbs.name")%></p>
                                                    
                                                </td>
                                                <td  class="td_titulo_C" nowrap>
                                                    <%=rb.getString("doc.number")%>
                                                </td>

                                                <%--<td width="40%" class="td_titulo_C">--%>
                                                <td  class="td_titulo_C">
                                                    <%=rb.getString("cbs.owner")%>
                                                </td>
                                                <td  class="td_titulo_C">
                                                    <%=rb.getString("cbs.statu")%>
                                                </td>
                                            </tr>
                                            <%
                                                String img = routImgs + DesigeConf.getProperty("imgDocument");
                                                String image = "";
                                            %>
                                            <logic:iterate id="doc" name="documents" indexId="ind" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session">
                                                <%
                                                    int item = ind.intValue()+1;
                                                    int num = item%2;
                                                    String nameClass = "td_"+num;
                                                    
                                                    String nameFile = doc.getNameFile().toLowerCase();
                                                    image = img;
                                                    if(nameFile.endsWith("doc") || nameFile.endsWith("docx")) {
                                                    	image = routImgs + "page_doc.png";
                                                    } else if(nameFile.endsWith("dot") || nameFile.endsWith("dotx")) {
                                                        image = routImgs + "page_word_template.png";
                                                    } else if(nameFile.endsWith("odt")) {
                                                    	image = routImgs + "page_odt.png";
                                                    } else if(nameFile.endsWith("ppt") || nameFile.endsWith("pptx")) {
                                                    	image = routImgs + "page_ppt.png";
                                                    } else if(nameFile.endsWith("odp")) {
                                                    	image = routImgs + "page_odp.png";
                                                    } else if(nameFile.endsWith("gif") || nameFile.endsWith("jpg") || nameFile.endsWith("jpeg") || nameFile.endsWith("png")) {
                                                    	image = routImgs + "page_img.png";
                                                    } else if(nameFile.endsWith("xls") || nameFile.endsWith("xlsx") || nameFile.endsWith("xlsm")) {
                                                    	image = routImgs + "page_xls.png";
                                                    } else if(nameFile.endsWith("ods")) {
                                                    	image = routImgs + "page_ods.png";
                                                    } else if(nameFile.endsWith("html") || nameFile.endsWith("html")) {
                                                    	image = routImgs + "page_html.png";
                                                    } else if(nameFile.endsWith("pdf") || nameFile.endsWith(".ai")) {
                                                    	image = routImgs + "page_pdf.png";
                                                    } else if(nameFile.endsWith("txt")) {
                                                    	image = routImgs + "page_txt.png";
                                                    }
                                                %>
                                                <tr>
                                                    <td class='td_<%=num%>'>
                                                        <img src="<%=image%>" border="0" > <!--width="18" height="18"-->
                                                           <a href="javascript:showDocument('<%=doc.getNumberGen()%>')" class="ahref_b"
                                                                    onmouseover="javascript:showStatus(0);return true;" onmouseout="showStatus(1);return true;"
                                                                     >
                                                                    <%=doc.getNameDocument()%>
                                                            </a>
                                                    </td>

                                                    <td class='td_<%=num%>'  nowrap>
                                                            <div style="width:100%;text-align:center"><%=doc.getPrefix()%> <%=doc.getNumber()%>&nbsp;</div>
                                                            
                                                    </td>

                                                    <td class='<%=nameClass%>' align="center">
	                                                    <div style="width:100%;text-align:center">
                                                        <logic:present name="showCharge" scope="session">
                                                            <a href="javascript:showCharge('<%=doc.getOwner()%>','<%=doc.getCharge()%>')" class="ahref_b"
                                                                onmouseover="javascript:showStatus(0);return true;" onmouseout="showStatus(1);return true;">
                                                                <span class="td_grisl">
                                                                <%=doc.getCharge()%>
                                                                </span>
                                                            </a>
                                                        </logic:present>
                                                        <logic:notPresent name="showCharge" scope="session">
                                                            <a href="javascript:showCharge('<%=doc.getOwner()%>','<%=doc.getCharge()%>')" class="ahref_b"
                                                                onmouseover="javascript:showStatus(0);return true;" onmouseout="showStatus(1);return true;">
                                                                <span class="td_grisl">
                                                                <%=doc.getOwner()%>
                                                                </span>
                                                            </a>
                                                        </logic:notPresent>
                                                        
                                                        </div>
                                                    </td>
                                                   <td class='<%=nameClass%>'>
														<div class="td_grisl" style="width:100%;text-align:center">
                                                        <%=doc.getStatuDoc()%>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </logic:iterate>
                                       </table>
                                   <!-- END All SubNodes for Node Selected -->
                                </td>
                            </tr>
                        </logic:present>
                            <!-- END For List Documents-->
                </logic:equal>
             </table>
 <% } else { %>
        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
            <tbody>
                <tr>
                    <td class="td_title_bc" height="21 px" width="100%">
                        <%=rb.getString("cbs.selectNode")%>
                    </td>
                </tr>
            </tbody>
        </table>
 <%     }
    }
 %>
 </body>
</html:html>
<script>
//alert(document.location);
</script>