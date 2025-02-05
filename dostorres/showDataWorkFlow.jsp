<!--
 * Title: showDataWorkFlow.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguez.(SR)
  * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 14/05/2005 (NC) Se eliminaron líneas de código comentadas. </li>
 *      <li> 03/08/2005 (SR) Se muestra la version del documento. </li>
 *      <li> 29/06/2006 (SR) Se coloco una session devalidacion cuando sea visto
                             por historico de tal manera que no aparesca el boton regresar. </li>
 </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    pageContext.setAttribute("cmd",cmd);
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
  font-family: Geneva, Verdana, Arial, Helvetica;
  font-weight: normal;
  overflow: scroll;
}
</style>
<script type="text/javascript">
    function cancelar() {
    	var form = document.getElementById("Selection");
        <%if(session.getAttribute("idDocument")!=null) {%>
   	        form.target = "_parent";
        	form.idDocument.value = <%=session.getAttribute("idDocument")%>;
	        form.action="showDataDocument.do";
        <%} else {%>
	        form.action="loadStructMain.do";
	        if (window.parent.frames[0] && window.parent.frames[0].history && !window.parent[0].struct) {
    	        form.target = "_parent";
        	}
        <%}%>
        form.submit();
    }

       function cancelar2() {
    	   var form = document.getElementById("Selection");
        <%if(session.getAttribute("idDocument")!=null) {%>
	        form.target = "text";
    	   	form.idDocument.value = <%=session.getAttribute("idDocument")%>;
        	form.action="showDataDocument.do";
        <%} else {%>
	        form.target = "text";
	        form.action="loadStructMain.do";
        <%}%>
        form.submit();
    }

      function cancelar1(idDoc) {
    	  var form = document.getElementById("Selection");

        //opcional :)
       // if (!window.parent.frames['code']) {
            //          form.action = "loadAllStruct.do";
        form.idDocument.value = idDoc;
		form.action = "showDataDocument.do";
     //   }
        form.submit();
    }
    function edit(){
        document.showDocument.submit();
    }
    function updateCheck(check,field) {
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }

    function createWF(item) {
        document.newWF.typeWF.value = item;
        document.newWF.target = "_parent";
        document.newWF.submit();
    }

    function showDocument(idDocument,idVersion){
        var hWnd = null;
        hWnd = window.open("viewDocument.jsp?idDocument="+idDocument+"&idVersion="+idVersion,"","width=800,height=600,resizable=yes,scrollbars=yes,left=100,top=100");
        if ((document.window != null) && (!hWnd.opener)) {
            hWnd.opener = document.window;
        }
    }

    function showDataDocument(id) {
    	var formaSelection = document.getElementById("Selection");
        formaSelection.idDocument.value = id;
        formaSelection.submit();
    }

    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function toggleDiv(gn)  {
	var g = document.all(gn);

	if( g.style.display == "none")
		g.style.display = "";
	else
		g.style.display = "none";
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad%>>

    <!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
        <form name="Selection" id="Selection" action="showDataDocument.do">
            <input type="hidden" name="idDocument" value=""/>
            <input type="hidden" name="idNodeSelected" value="<%=nodeActive%>"/>
        </form>
    <!-- END Form -->
<logic:present name="showDataWF" scope="session">


    <bean:define id ="doc" name="showDataWF" type="com.desige.webDocuments.workFlows.forms.DataWorkFlowForm" scope="session"/>
    <table align=center border=0 width="100%">
        <tr>
            <td valign="top" colspan="4">
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tbody>
                        <tr>
                            <td class="td_title_bc" height="21">
                            <%--ydavila Ticket 001-00-003023--%>
                            <%--<%=rb.getString("wf.titleWF"+doc.getTypeWF())%> <%=doc.getEliminar()!=0?rb.getString("wf.delete"):""%>--%>
                                <%=rb.getString("wf.titleWF"+doc.getTypeWF())%> <%=doc.getEliminar()!=0?rb.getString("wf.titleWF4"):""%> <%=doc.getCambiar()!=0?rb.getString("wf.titleWF3"):""%>                               
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td width="20%" height="26" class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("doc.title")%>
            </td>
            <td class="td_gris_l" width="*" colspan="3">
                <bean:write name="showDataWF" property="nameDocument"/>
                <a href="javascript:showDataDocument('<%=doc.getNumDocument()%>')" class="ahref_b">
                    [<%=rb.getString("wf.details")%>]
                </a>
                <a href="javascript:showDocument(<%=doc.getNumDocument()%>,'<%=doc.getVersion()%>')" class="ahref_b">
                    [<%=rb.getString("wf.viewDocAnt")%>]
                </a>
            </td>
        </tr>
         <tr>
             <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" height="20 px" valign="middle">
                    <%=rb.getString("doc.Ver")%>
             </td>
             <td class="td_gris_l">
                       <bean:write name="showDataWF" property="idVersion"/>
             </td>
        </tr>
        <tr>
            <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.request")%>
            </td>
            <td width="30%" class="td_gris_l">
                <logic:present name="showCharge" scope="session">
                    <a href="javascript:showCharge('<bean:write name="showDataWF" property="request"/>','<bean:write name="showDataWF" property="charge"/>')" class="ahref_b">
                        <bean:write name="showDataWF" property="charge" />
                    </a>
                </logic:present>
                <logic:notPresent name="showCharge" scope="session">
                    <a href="javascript:showCharge('<bean:write name="showDataWF" property="request"/>','<bean:write name="showDataWF" property="charge"/>')" class="ahref_b">
                        <bean:write name="showDataWF" property="request"/>
                    </a>
                </logic:notPresent>
            </td>
            <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.dateExpire")%>
            </td>
            <td width="*" class="td_gris_l">
                <bean:write name="showDataWF" property="dateExpire"/>
            </td>
        </tr>

        <tr>
            <td class="td_orange_l_b" height="26" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.dateBegin")%>
            </td>
            <td class="td_gris_l">
                <bean:write name="showDataWF" property="dateBegin"/>
            </td>
            <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.dateEnd")%>
            </td>
            <td class="td_gris_l">
                <%=doc.getDateEnd()!=null?doc.getDateEnd():rb.getString("wf.inProgress")%>
            </td>
        </tr>
        <%=ToolsHTML.dibujarCheck("td_orange_l_b",rb.getString("cbs.sequential"),"td_gris_l",doc.getSequential(),0,null)%>
        <%=ToolsHTML.dibujarCheck("td_orange_l_b",rb.getString("wf.conditional"),"td_gris_l",doc.getConditional(),0,null)%>
        <tr>
            <td class="td_orange_l_b" height="26" showDataWorkFlow style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.statu")%>
            </td>
            <td class="td_gris_l">
                <%=rb.getString("wf.wfStatu"+doc.getStatu())%>
            </td>
            <td class="td_orange_l_b" style="background: url(img/btn160.png); background-repeat: no-repeat" valign="middle">
                <%=rb.getString("wf.result")%>
            </td>
            <td class="td_gris_l">
                <%=rb.getString("wf.result"+doc.getResult())%>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td colspan="4" align="left" class="txt_title">
                <span style="cursor: hand;" onclick='toggleDiv("comments"); if(comments.style.display == "none"){ commentsImg.src="menu-images/mas.gif";} else { commentsImg.src="menu-images/menos.gif";}'>
                    <img id='commentsImg' src='menu-images/mas.gif' />
                    <%=rb.getString("wf.requestDetail")%>
                </span>
            </td>
        </tr>
       <tr>
            <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                <div align="left" class="clsMessageView" id='comments' style="width: 100%; height: 160px">
                    <%=doc.getComments()%>
                </div>
                <script language="javascript">
                    <% if (doc.getComments()!=null){ %>
			            document.getElementById("comments").style.display = "none";
                    <%}%>
                </script>
            </td>
       </tr>
        <tr>
            <td colspan="4">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td valign="top" colspan="4" >
                <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                    <tr>
                        <td width="40%" class="td_title_bc">
                            <%=rb.getString("wf.participantion")%>
                        </td>
                        <td width="18%" class="td_title_bc">
                            <%=rb.getString("wf.statu")%>
                        </td>
                        <td class="td_title_bc" width="18%">
                            <%=rb.getString("wf.result")%>
                        </td>
                        <td class="td_title_bc" width="24%">
                            <%=rb.getString("wf.dateDoc")%>
                        </td>
                    </tr>
                </table>
                <logic:present name="usersWF" scope="session" >
                    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                        <logic:iterate id="participation" name="usersWF" type="com.desige.webDocuments.workFlows.forms.ParticipationForm" scope="session">
                            <tr>
                                <td class="td_gris_l" width="40%">
                                    <logic:present name="showCharge" scope="session">
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="charge" />
                                        </a>
                                    </logic:present>
                                    <logic:notPresent name="showCharge" scope="session">
                                        <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                            <bean:write name="participation" property="nameUser" />
                                        </a>
                                    </logic:notPresent>
                                </td>
                                <td class="td_gris_l" width="16%">
                                    <p align="center">
                                        <%=rb.getString("wf.statuUserWF"+participation.getStatu())%>
                                    </p>
                                </td>
                                <td class="td_gris_l" width="16%">
                                    <p align="center">
                                        <%=rb.getString("wf.resultUserWF"+participation.getResult())%>
                                    </p>
                                </td>
                                <td class="td_gris_l" width="*">
                                    <bean:write name="participation" property="dateReply" />
                                </td>
                            </tr>
                        </logic:iterate>
                    </table>
                </logic:present>
            </td>
        </tr>

        <logic:present name="comments" scope="session" >
            <logic:iterate id="replies" name="comments" type="com.desige.webDocuments.utils.beans.Search3" scope="session">
                <tr>
                    <td colspan="4">
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="left" class="txt_title">
                        <span style="cursor: hand;" onclick='toggleDiv("<%=replies.getId()%>"); if(<%=replies.getId()%>.style.display == "none"){ <%=replies.getId()%>Img.src="menu-images/mas.gif";} else { <%=replies.getId()%>Img.src="menu-images/menos.gif";}'>
                            <img id='<%=replies.getId()%>Img' src='menu-images/mas.gif' />
                            <%=rb.getString("wf.result")%>: <%=replies.getName() + ": " + replies.getAditionalInfo()%>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="left" style="padding: 0px" class="td_gris_l">
                        <div align="left" class="clsMessageView" id='<%=replies.getId()%>' style="width: 100%; height: 160px">
                            <%=replies.getDescript()%>
                        </div>
                        <script language="javascript">
                            <% if (replies.getDescript()!=null){ %>
                                <%=replies.getId()%>.style.display = "none";
                            <%}%>
                        </script>
                    </td>
               </tr>
           </logic:iterate>
        </logic:present>
        <tr>
            <td colspan="4">
                &nbsp;    
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <center>
                    <logic:present name="history" scope="session">
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar2();" />
                    </logic:present>
                    <logic:notPresent name="history" scope="session">
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar1('<%=doc.getNumDocument()%>');" />
                    </logic:notPresent>
                </center>
            </td>
        </tr>
    </table>
</logic:present>

<logic:notPresent name="showDataWF">
    <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
        <tr>
            <td class="td_orange_l_b" width="85%">
                <%=rb.getString("wf.selecItem")%>
            </td>
            <td width="*">
                <center>
                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();" />
                </center>
            </td>
        </tr>
    </table>
</logic:notPresent>
</body>
</html>
