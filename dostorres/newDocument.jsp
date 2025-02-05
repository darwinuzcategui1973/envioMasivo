<!--**
 * Title: newDocument.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz. (SR)
 * @version WebDocuments v1.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 27-08-2004 (NC) Creation </li>
 *      <li> 11-10-2006 (SR) Se cambio el procedimiento de java script showMessage </li>

 </ul>

 */-->


<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 java.util.Hashtable,
                 java.util.Collection,
                 java.util.Iterator,
                 com.desige.webDocuments.utils.beans.Search,
                 com.desige.webDocuments.structured.forms.BaseStructForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    String errorCorrelativo = (String)ToolsHTML.getAttributeSession(session,"info2",true);
    StringBuffer onLoadCorrelativo= new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");

    if ((errorCorrelativo != null) && (errorCorrelativo.compareTo("") != 0)) {
        onLoadCorrelativo.append("  onLoad=\"showMessage('").append(errorCorrelativo).append("');\"");
        session.removeAttribute("info2");
    }
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb,"initPage();");
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null) {
        cmd = SuperActionForm.cmdLoad;
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    pageContext.setAttribute("cmd",cmd);
    Hashtable tree = (Hashtable)session.getAttribute("tree");
    
    Collection tipoOnLine = (Collection)session.getAttribute("typesDocumentsDoc");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">

    re = /^(file|http):\/\/\S+\.(com|net|org|info|biz|ws|us|tv|cc|ve)$/i

   function showMessage(mensaje) {
        alert(mensaje);
    }

  function validarURL(link) {
        if (re.test(link)) {
            return true;
        } else {
            return false;
        }
     }

    function initPage() {
        forma = document.forms[0];
        if ((forma)&&(forma.nameDocument)) {
            forma.nameDocument.focus();
        }
    }

    function cancelar(form) {
        form.action="loadStructMain.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }

    function incluir(form){
        if (form.URL.value.length > 0){
            validarURL(form.URL.value)
        }
        form.cmd.value = "<%=SuperActionForm.cmdNew%>";
        form.submit();
    }

    function salvar(form) {
        if (validar(form)) {
            form.target = "_self";
          //form.comments.value = form.richedit.docXHtml;
           form.submit();
        }
    }

    function updateCheck(check,field)  {
        if (check.checked) {
            field.value = "0";
        } else{
            field.value = 1;
        }
        validarOnLine();
    }

    function validar(form) {
        if (form.nameDocument.value.length==0) {
            alert('<%=rb.getString("err.notNameFile")%>');
            return false;
        }
//        if (form.URL.value.length > 0) {
//            if (!validarURL(form.URL.value)) {
                <%--alert('<%=rb.getString("err.badLink")%>');--%>
//                return false;
//            }
//        }
        return true;
    }
    
	var tiposDocument=new Array();
	<%Search s = null;
	//System.out.println(tipoOnLine);
    Iterator ite = tipoOnLine.iterator();
    //System.out.println("ite="+ite);
    int i =0;
    while(ite.hasNext()) {
    	s = (Search)ite.next();	
    	%>
		tiposDocument[<%=i++%>]=<%=s.getId()%>
	<%}%>    
    
    function validarOnLine() {
    	form = document.newDocument;
    	valor = form.typeDocument.value;
    	if(form.docOnlineI.checked==false) {
    		return true;
    	}
    	var valido=false;
    	for(var i=0; i<<%=i%>;i++){
    		if(valor==tiposDocument[i]) {
    			valido=true;
    			break;
    		}
    	}
    	if(!valido) {
    		form.docOnlineI.checked=false;
	    	updateCheck(form.docOnlineI,form.docOnline);
	    	alert("<%=rb.getString("E0130")%>");
    	}
    }

</script>
</head>
 <%if (!"".equalsIgnoreCase(onLoadCorrelativo.toString())) { %>
      <body class="bodyInternas" <%=onLoadCorrelativo.toString()%>>
  <% } else {%>
      <body class="bodyInternas" <%=onLoad%>>
 <%  } %>
<table align=center border=0 width="100%">
    <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdLoad%>">
        <%
            String forma = "/newDocument.do";
            String next = "upploadFile.jsp";
            if (SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
                forma = "/newDocument.do";
            }
        %>
        <tr>
            <td valign="top">
                <td class="pagesTitle">
                    <%=rb.getString("btn.addDocument")%>
                </td>
                <html:form action="<%=forma%>">
                    <bean:define id ="doc" name="newDocument" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>
                    <html:hidden property="cmd"/>
                    <input type="hidden" name="nexPage" value="<%=next%>"/>
                    <%
                        BaseStructForm dataForm = (BaseStructForm)tree.get(nodeActive);
                        String rout = dataForm.getRout();
                        String nodeSelected = nodeActive;
                        String nodeTypeNew = (String)request.getAttribute("nodeTypeNew");
                        if (!ToolsHTML.checkValue(nodeTypeNew)){
                            nodeTypeNew = "0";
                        }
                    %>
                      <input type="hidden" name="idNodeSelected" value="<%=nodeSelected%>"/>
                      <table width="100%" border="0">
                        <tr>
                            <td colspan="2">
                                <%
                                    String style = "clsTableTitle";
                                    String height = "21";
                                    if (rout!=null&&rout.length() > 104) {
                                        style = "clsTableTitle2";
                                        height = "42";
                                    }
                                %>
                                <table class="<%=style%>" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                                    <tbody>
                                        <tr>
                                            <td class="td_title_bc" height="<%=height%>">
                                                <%=rb.getString("cbs.location") + ": "+rout%>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="td_gris_l" colspan="2">
                                &nbsp;
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" heigth="26" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat;width=500" valign="middle">
                                <%=rb.getString("doc.title")%>
                            </td>
                        </tr>
                        <tr>
                          <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" width="18%" valign="middle">
                            <%=rb.getString("cbs.name")%>:
                          </td>
                          <td class="td_gris_l" width="*">
                            <html:text property="nameDocument" size="110" maxlength="1000"  styleClass="classText"/>
                          </td>
                        </tr>
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("cbs.owner")%>:
                            </td>
                            <td>
                                <html:select property="owner" styleClass="classText" style="width: 250px">
                                    <logic:present name="userSystem" scope="session" >
                                        <logic:present name="showCharge" scope="session">
                                            <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                <html:option value="<%=bean.getId()%>">
                                                    <%=bean.getAditionalInfo()%> ( <%=bean.getDescript()%> )
                                                </html:option>
                                            </logic:iterate>
                                        </logic:present>
                                        <logic:notPresent name="showCharge" scope="session">
                                            <logic:iterate id="bean" name="userSystem" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                <html:option value="<%=bean.getId()%>">
                                                    <%=bean.getDescript()%> ( <%=bean.getAditionalInfo()%> )
                                                </html:option>
                                            </logic:iterate>
                                        </logic:notPresent>
                                    </logic:present>
                                </html:select>
                            </td>
                        </tr>

                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.prefix")%>:
                            </td>
                            <td>
                                <html:text property="prefix" styleClass="classText" disabled="true" size="39"/>
                            </td>
                        </tr>
<%--
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.number")%>:
                            </td>
                            <td>
                                    <html:text property="number"  disabled="false" styleClass="classText"/>
                            </td>
                        </tr>
--%>
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.type")%>:
                            </td>
                            <td>
                                <html:select property="typeDocument" styleClass="classText" style="width: 250px" onchange="validarOnLine(this.form,this.value)">
                                    <logic:present name="typesDocuments" scope="session">
                                        <logic:iterate id="bean" name="typesDocuments" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                            <html:option value="<%=bean.getId()%>">
                                                <%=bean.getDescript()%>
                                            </html:option>
                                        </logic:iterate>
                                    </logic:present>
                                    <logic:notPresent name="typesDocuments" scope="session">
                                        <%=rb.getString("E0006")%>
                                    </logic:notPresent>
                                </html:select>
                            </td>
                        </tr>
                        <logic:equal value="true" name="showNorms" >
                            <tr>
                                <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                    <%=rb.getString("doc.normISO")%>:
                                </td>
                                <td>
                                    <html:select property="normISO" styleClass="classText" style="width: 675px">
                                        <logic:present name="norms" scope="session">
                                                <html:option value="0">
                                                    --------------------------------------------------------------------------------------------------------------------------------------------------------
                                                </html:option>
                                            <logic:iterate id="bean" name="norms" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                                <html:option value="<%=bean.getId()%>">
                                                    <%=bean.getDescript()%>
                                                </html:option>
                                            </logic:iterate>
                                        </logic:present>
                                    </html:select>
                                </td>
                            </tr>
                        </logic:equal>
                        <logic:notEqual value="true" name="showNorms" >
                            <html:hidden property="normISO" value="0"/>
                        </logic:notEqual>
            <tr style="display:<%=doc.getOwner().equals(usuario.getUser())?"":"none"%>">
                <td class="titleLeft" height="26" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.toForFiles")%>:
                </td>
                <td class="td_gris_l" style="display:<%=doc.getOwner().equals(usuario.getUser())?"":"none"%>" >
                    <%=ToolsHTML.showCheckBox("toForFilesI","updateCheck(this.form.toForFilesI,this.form.toForFiles)",doc.getToForFiles(),"0")%>
                    <html:hidden name="doc" property="toForFiles"/>
                </td>
            </tr>
                        
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.public")%>:
                            </td>
                            <td>
                               <%
                                    int valor = 1;
                                    if (!ToolsHTML.isEmptyOrNull(doc.getDocPublic())){
                                        valor = Integer.parseInt(doc.getDocPublic());
                                    }
                                    valor = 1;
                                %>
                                
								<%
								    String docPublic = "1";
								    docPublic = (request.getParameter("publicado") != null) ? request.getParameter("publicado") : "1";
								    if(!ToolsHTML.isEmptyOrNull(docPublic)){
								    	if ("1".equals(docPublic)){
								    		valor = 1;
								    	}
								    	if("0".equals(docPublic)){
								    		valor = 0;
								    	}
								    }
								%>
                                
                               <%=ToolsHTML.showCheckBox("docPublicI","updateCheck(this.form.docPublicI,this.form.docPublic)",valor,0,false)%>
                               <html:hidden property="docPublic" value="<%=String.valueOf(valor)%>"/>
                            </td>
                        </tr>
                       <logic:present name="docInline" scope="session">
                        <tr>
                            <td class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.online")%>:
                            </td>
                            <td> 
                                <%
                                    valor = 1;
                                    if (!ToolsHTML.isEmptyOrNull(doc.getDocOnline())){
                                        valor = Integer.parseInt(doc.getDocOnline());
                                    }
                                %>

								<%
									
								    String docOnline = "1";
								    docOnline = (request.getParameter("docOnline") != null) ? request.getParameter("docOnline") : "1";
								    if(!ToolsHTML.isEmptyOrNull(docOnline)){
								    	if ("1".equals(docOnline)){
								    		valor = 1;
								    	}
								    	if("0".equals(docOnline)){
								    		valor = 0;
								    	}
								    }
								%>

                                <%=ToolsHTML.showCheckBox("docOnlineI","updateCheck(this.form.docOnlineI,this.form.docOnline)",valor,0,false)%>
                               <html:hidden property="docOnline" value="<%=String.valueOf(valor)%>"/>
                            </td>
                        </tr>
                       </logic:present>
                        <tr>
                            <td class="titleLeft" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.url")%>:
                            </td>
                            <td>
                                <html:textarea property="URL" cols="100" rows="2" styleClass="classText" />
                            </td>
                        </tr>

                        <tr>
                            <td class="titleLeft" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.keys")%>:
                            </td>
                            <td>
                                <html:textarea property="keys" cols="100" rows="3" styleClass="classText" />
                            </td>
                        </tr>

                        <tr>
                            <td class="titleLeft" style="background: url(img/btn120x4.png); background-repeat: no-repeat" valign="middle">
                                <%=rb.getString("doc.descript")%>:
                            </td>
                            <td>
                                <html:textarea property="descript" cols="100" rows="3" styleClass="classText"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="center">
                                <input type="button" class="boton" value="<%=rb.getString("pagin.nextbtn")%>" onClick="javascript:salvar(this.form);" name="btSigu"/>
                                <logic:notEqual name="cmd" value="<%=SuperActionForm.cmdInsert%>">
                                    <html:hidden property="idNodeParent" value="<%=dataForm.getIdNodeParent()%>"/>
                                    &nbsp;
                                </logic:notEqual>
                                    &nbsp;
                                <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar(this.form);"/>
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
