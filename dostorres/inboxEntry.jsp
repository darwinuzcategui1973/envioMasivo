<!-- inboxEntry.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>
<%@ page import="com.desige.webDocuments.utils.beans.PaginPage"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("loadInbox.do");
	}

	ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }          
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    
    String parametro1=null;
    String parametro2=null;
    
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.clsMessageView
{
  background: white;
  color: black;
  border: 1px solid #FFFFFF;
  padding: 2;
  font-size: 12px;
  font-family: Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script type="text/javascript">
	var rec=new Image();
	rec.src="img/mailRevicedSmall.gif";

    function cancelar() {
    	var form = document.getElementById("Selection");
        form.action="loadStructMain.do";
        form.cmd.value="<%=SuperActionForm.cmdLoad%>";
        form.submit();
    }
   function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }
    
	function abrirCentrado(url) {
		var nWidth=screen.availWidth-(100);
		var nHeight=screen.availHeight-(150);
		fullscreen = window.open(url, "fullscreen", 'top=50,left=50,width='+(nWidth)+',height ='+(nHeight)+',fullscreen=no,toolbar=0 ,location=0,directories=0,status=0,menubar=0,resizable=1,scrolling=1,scrollbars=1');
	}
    
    function showDetailsMail(idMessage,idMessageUser,cuadro){
    	var formaSelection = document.getElementById("Selection");
    	try{
   			document.getElementById("nuevo"+idMessage).src=rec.src;
   		}catch(e){}
   		if(typeof cuadro=='undefined'){
        	formaSelection.target="detalle";
        } else {
        	with(formaSelection){
        		abrirCentrado(action+"?idMessage="+idMessage.value+"&idMessageUser="+idMessageUser.value);
        	}
        }
        formaSelection.idMessage.value = idMessage;
        formaSelection.idMessageUser.value = idMessageUser;
        formaSelection.submit();
    }

    function validateCheck() {
    	var formaSelection = document.getElementById("Selection");
    	for (var i=1; i < formaSelection.length;i++) {
            if (formaSelection.elements[i].checked) {
                return true;
            }
        }
        return false;
    }

    function youAreSure() {
        if (!validateCheck()) {
            alert('<%=rb.getString("error.selectValue")%>');
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
            	var forma = document.getElementById("Selection");
                try{
                    window.parent.frames['titulo'].formulario.all.checked=false;
                } catch(e){
                }
                if (forma) {
					forma.target="lista";
                    forma.action="deleteMail.do";
                    forma.submit();
                }
            }
        }
    }

    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
<logic:present name="inbox" scope="session">
    <table align=center width="100%" cellspacing="0" cellpadding="0" border="0">
        <tr class="none">
            <td class="pagesTitle">
                   <table cellspacing="0" cellpadding="0" border="0">
                   	<tr></td>
	                    <%=ToolsHTML.printMenuInnerToolBar(ToolsHTML.getMenuMailsII(rb),"",true,false, rb)%>
                   	</td></tr>
                   </table>
            </td>
        </tr>
        <form name="Selection" id="Selection" action="showDetailsMail.do">
            <input type="hidden" name="idMessage" value=""/>
            <input type="hidden" name="idMessageUser" value=""/>
            <tr>
                <td width="100%" valign="top">
                    <table align=center border=0 width="100%" cellSpacing="0" cellPadding="0">
                        <!-- Paginación -->
                        <%
                            String from = request.getParameter("from");
                            String size = (String)session.getAttribute("size");
                            if (!ToolsHTML.isNumeric(size)) {
                                size = "1";
                            }
                            if (!ToolsHTML.isNumeric(from)) {
                                from = "0";
                            }
                            PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),"30");
                        %>
                        <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
                        <logic:iterate id="mail" name="inbox" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                         type="com.desige.webDocuments.mail.forms.MailForm" scope="session">
		                    <%
		                        int item = ind.intValue()+1;
		                        int num = item%2;
		                    %>
		                    <tr class='fondo_<%=num%>'>
                                <td class="td_gris_l" valign="top"  width="7%">
                                    <center>
    <%--                                    <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">--%>
                                        <% if (mail.isNew()) { %>
                                           <%-- <input name="mail" type="checkbox" disabled value="">--%>
                                            <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">
                                            <img id="nuevo<%=mail.getIdMessage()%>" src="img/newMailSmall.gif" border="0">
                                        <% } else { %>
                                            <input name="mail" type="checkbox" value="<%=mail.getIdMessage()%>,<%=mail.getIdMessageUser()%>">
                                            <img id="recibido" src="img/mailRevicedSmall.gif" border="0">
                                        <% } %>
                                    </center>
                                </td>
                                <td class="td_gris_l"  width="28%">
                                    <%=mail.getNameFrom()%>
                                </td>
                                <td class="td_gris_l" width="51%">
                                	<%if(parametro1==null && parametro2==null){
                                		parametro1=mail.getIdMessage();
                                		parametro2=mail.getIdMessageUser();
                                	}%>
                                    <a class="ahref_b" href="javascript:showDetailsMail('<%=mail.getIdMessage()%>','<%=mail.getIdMessageUser()%>');"  ondblclick="javascript:showDetailsMail('<%=mail.getIdMessage()%>','<%=mail.getIdMessageUser()%>','_blank');">
                                        <%=mail.getSubject()%>
                                    </a>
                                </td>
                                <td class="td_gris_l" width="14%">
                                    <%=mail.getDateMail()%>
                                </td>
                            </tr>
                        </logic:iterate>
                    </form>
                    <%
                        if (size.compareTo("0") > 0) {
                    %>
                            <tr>
                                <td id="paginador" align="center" colspan="4" class="none">
                                    <!-- paginacion ini -->
                                    <table class="paginadorNuevo" align="left">
                                        <tr>
                                            <td align="center">&nbsp;<img src="images/inicio2.gif"
                                               class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                               onclick="paging_OnClick(0)">&nbsp;
                                            </td>
                                            <td align="center"> <img src="images/left2.gif"
                                               class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                               onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                                            </td>
                                            <td align="center" width="120px">
                                                    <font size="1" color="#000000">
                                                        <%=rb.getString("pagin.title")+ " "%>
                                                        <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                                        <%=rb.getString("pagin.of")%>
                                                        <%=Pagingbean.getNumPages()%>
                                                    </font>
                                            </td>
                                            <td align="center"> <img src="images/right2.gif"
                                               class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                               onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">&nbsp;
                                            </td>
                                            <td align="center"> <img src="images/fin2.gif"
                                               class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                               onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                                            </td>
                                        </tr>
                                    </table>
                                            
                                    <form name="formPagingPage" method="post" action="inboxEntry.jsp">
                                      <input type="hidden" name="from"  value="">
                                      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                    </form>
								    <!-- paginacion fin -->
                                </td>
                            </tr>
                        <%
                            }
                        %>
                </table>
            </td>
        </tr>
    </table>
</logic:present>
</body>
</html>
<script language="javascript">
function pag(){
	try{
		titulo=window.parent.titulo;
		titulo.document.getElementById("selector").innerHTML=document.getElementById("paginador").innerHTML;
	}catch(e){
		setTimeout("pag()",500);	
	}
}
</script>
<script language="javascript" event="onload" for="window">
	try{
		window.parent.parent.parent.frames['superior'].document.getElementById("mailwithoutreader").innerHTML="0";
	}catch(e){}

   	<%if(parametro1!=null && parametro2!=null){%>
		showDetailsMail('<%=parametro1%>','<%=parametro2%>');
	<%}%>
	pag();
</script>