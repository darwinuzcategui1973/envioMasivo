
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.enlaces.forms.UrlsActionForm"%>
<%@ page import="java.util.Calendar"%>

<!--/**
 * Title: sacop_intouch_primera.jsp<br>
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
    Calendar dateobj = Calendar.getInstance();
    int dia=dateobj.get(Calendar.DAY_OF_MONTH);
    int mes=dateobj.get(Calendar.MONTH);
    int anio=dateobj.get(Calendar.YEAR);

    //se usan stas variables para tener la fecha estimada como variables globales en sta forma
    int diast=dateobj.get(Calendar.DAY_OF_MONTH);
    int messt=dateobj.get(Calendar.MONTH);
    int aniost=dateobj.get(Calendar.YEAR);

    boolean SwMuestraFormGnrl=false;
    String fechaEstimada=request.getAttribute("fechaEstimada")!=null?(String)request.getAttribute("fechaEstimada"):"";
    String comentario=request.getAttribute("comentario")!=null?(String)request.getAttribute("comentario"):"";
     if (!ToolsHTML.isEmptyOrNull((String)request.getAttribute("fechaEstimada"))){
            aniost =Integer.parseInt(fechaEstimada!=null?fechaEstimada.substring(0,4):String.valueOf(aniost));
        if (fechaEstimada!=null){
            messt= Integer.parseInt(fechaEstimada.substring(5,7))-1;
        }
            diast= Integer.parseInt(fechaEstimada!=null?fechaEstimada.substring(8,10):String.valueOf(diast));
     }
    String checked=request.getAttribute("estado")!=null?(String)request.getAttribute("estado"):"";
     String checkedBorrador="";
    String checkedEmitido="";
    if ("1".equalsIgnoreCase(checked)){
        checkedEmitido="checked";
    }else{
        //checkedEmitido="";
        checkedBorrador="checked";
    }
      checked=request.getAttribute("correcpreven")!=null?(String)request.getAttribute("correcpreven"):"";
     String checkedcorrectiva="";
     String correcpreventiva="";
    if ("0".equalsIgnoreCase(checked)){
        checkedcorrectiva="checked";
    }else{
        correcpreventiva="checked";
    }

    String origenSacop=request.getAttribute("origenSacop")!=null?(String)request.getAttribute("origenSacop"):"";

%>
<html>

<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">
//seleccionar de lista
function contSelectInList(lista) {
        var items = lista.length;
        if (items==0) {
            return 0;
        }
        var row = 0;
        for (row = 0; row < items ; row++) {
            var valor = lista[row];
            valor.selected = true;
        }
        return items;
    }
 function contarItemsSelecteds(lista){
           if (lista){
               var items = lista.length;
               var valor;
               var cont = 0;
               var row = 0;
               for (row = 0; row < items; row++){
                   var valor = lista[row];
                   if (valor.selected){
                       cont++;
                   }
               }
               return cont;
           }
           return 0;
       }
 function createElement(value,texto){
		var newItem = document.createElement("OPTION");
		newItem.value = value;
		newItem.text = texto;
		return newItem;
	}
    function removerDeLista(lista,items){
		if (lista){
            var row = 0;
			for (row = items - 1; row >= 0; row--){
				var valor = lista[row];
				if (valor.selected){
					lista.remove(row);
				}
			}
		}
	}
function mover(listDer,listIzq){
		var items = listDer.length;
		var selecteds = contarItemsSelecteds(listDer);
		if (selecteds > 0){
			var	newItem;
			for (row = 0; row < items; row++){
				var valor = listDer[row];
				if (valor.selected) {
                    newItem = createElement(listDer[row].value,listDer[row].text);
					listIzq.add(newItem);
				}
			}
			removerDeLista(listDer,items);
		}
	}

//end seleccionar de lista
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
        location.href = "loadsacop_intouch.do";
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
        forma = eval(document.<%=(String)session.getAttribute("formEdit")%>);
        forma.cmd.value = '<%=SuperActionForm.cmdEdit%>';
        forma.idplanillasacopaccion.value = num;
        forma.id.value = num;
        forma.submit();

    }

    function SaveGeneral(form){
             //alert(form.action);
           form.submit();
    }
    function salvar(form){

        if (validar(form)){
           
           form.submit();
        }
    }

   function salvar_userponecomentario(form,firma){
       //alert(form.action+"=editaccionesporpersona.do");
       //alert(form.action);
        form.action="editAccionesporpersona.do?firma="+firma
        form.btnSalvarf.disabled =true;
        form.btnSalvars.disabled =true;
        form.submit();
    }
    function validar(forma){

        if ((contSelectInList(<%out.print("forma.sacop_intouch");%>)==0)) {
             alert('<%=rb.getString("scp.faltatagname")%>');
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
 function vertexto(normsiso){
		var items = normsiso.length;
        for (row = 0; row < items; row++){
                   var valor = normsiso[row];
                   if (valor.selected){
                       alert(normsiso[row].text);
                       row +=items+1;
                   }
        }
}
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
  <tr>
    <td>
       <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
            <tbody>
                  <tr>
                     <td class="td_title_bc">
                        <%=rb.getString("scpintouch.nombre")%>
                     </td>
                  </tr>
             </tbody>
        </table>
     </td>
   </tr>
  </table>
    <%

        String forma = (String)session.getAttribute("editManager");
        //System.out.println("cmd = " + cmd);

        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(cmd)){
            forma = (String)session.getAttribute("newManager");
        }
    %>

    <tr>
        <td>
            <html:form action="<%=forma%>" enctype='multipart/form-data' >
                <html:hidden property="cmd" value='<%=cmd%>'/>
                <input type='hidden' name=goTo value=planillauno>


                <input type="hidden" name=idplanillasacop value=<%=(String)request.getAttribute("idplanillasacop")%>>

                <!--Se agrega una norma nueva -->
                 <table align=center border=0 width="100%">
                    <tr>
                        <td colspan="4">

                <table align=center border=0 width="100%">
                     <tr>
                            <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                             <%=rb.getString("scp.sacop")%>
                            </td>
                            <td colspan="2">
                                <%out.print(" <input type=\"radio\"  onClick='' "+correcpreventiva+ "  name=correcpreven value='1'>");%> <%=rb.getString("scp.ap")%>&nbsp;
                                <%out.print(" <input type=\"radio\"  onClick='' "+checkedcorrectiva+ "  name=correcpreven value='0'>");%> <%=rb.getString("scp.ac")%>&nbsp;

                            </td>
                     </tr>
                     <tr>
                            <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                             <%=rb.getString("scp.edo")%>
                            </td>
                            <td colspan="2">
                                <%out.print(" <input type=\"radio\"  onClick='' "+checkedBorrador+ "  name=edodelsacop value='0'>");%> <%=rb.getString("scp.borrador")%>&nbsp;
                                <%out.print(" <input type=\"radio\"  onClick='' "+checkedEmitido+ "  name=edodelsacop value='1'>");%> <%=rb.getString("scp.emitido")%>&nbsp;



                            </td>
                     </tr>
                     <tr>
                        <td class="titleLeft" width="26%" height="26" style="background: url(img/btn260.gif); background-repeat: no-repeat" valign="middle">
                               <%=rb.getString("admin.scpplanillas")%>:&nbsp;
                        </td>
                        <td width="24%">
                               <%String selected="";%>
                               <select name='origensacop'>
                                    <logic:present name="titulosplanillassacop" scope="request" >
                                        <logic:iterate id="bean" name="titulosplanillassacop" type="com.desige.webDocuments.utils.beans.Search" scope="request" >
                                            <%if (origenSacop.equals(String.valueOf(bean.getId()!=null?bean.getId():"0"))){
                                                  selected="selected";
                                            }else{
                                                  selected="";
                                            }%>
                                            <option value="<%=bean.getId()%>" <%=selected%>><%=bean.getDescript()%> </option>
                                        </logic:iterate>
                                    </logic:present>
                                </select>
                        </td>
                    </tr>
                    <tr>
                         <td  class="titleLeft" height="60" style="background: url(img/btn260x100.gif); background-repeat: no-repeat" width="26%" valign="middle">
                            <%=rb.getString("scpintouch.tagname")%>
                        </td>
                        <td>
                         <select name="intouch" size="8" multiple style="width:210px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.intouch);">
                            <logic:present name="intouch" scope="request">
                                <logic:iterate id="bean" name="intouch" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                </logic:iterate>
                            </logic:present>
                        </select>
                        </td>
                          <td width="10%">
                         <input type="button" value=" > " style="width:28px;" onClick="javascript:mover(this.form.intouch,this.form.sacop_intouch);" class="BUTTON" />
                            <br/>
                         <input type="button" value=" < " style="width:28px;" onClick="javascript:mover(this.form.sacop_intouch,this.form.intouch);" class="BUTTON" />
                       </td>
                        <select name="aux" style="display:none" multiple>
                        </select>
                        <select name="auxSelec" style="display:none" multiple>
                        </select>
                        <td>
                        <select name="sacop_intouch" size="8" multiple style="width:210px;" styleClass="classText" onDblClick="javascript:vertexto(this.form.sacop_intouch);">
                           <logic:present name="sacop_intouch" scope="request">
                                <logic:iterate id="bean" name="sacop_intouch" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
                                </logic:iterate>
                            </logic:present>
                        </select>
                        </td>
                    </tr>

                    <tr>
                        <td>

                        </td>
                        <td>

                        </td>
                    </tr>

                             <tr><td colspan=2><br/><br/></td></tr>
                             <tr><td colspan=2><br/><br/></td></tr>
                             <tr>
                               <td align="center" colspan="8">
                                <input type="button" class="boton" value="<%=rb.getString("scp.siguiente")%>" name="btnSalvar"  onClick="javascript:salvar(this.form);" />
                                    &nbsp;
                                    <logic:notPresent name="editar" scope="request" >
                                    &nbsp;
                                    <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btnDelete" onClick="javascript:youAreSure(this.form);" />
                                    </logic:notPresent>
                                    &nbsp;
                                    <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" name="btnCancelar" onClick="javascript:cancelar(this.form);" />
                               </td>
                             </tr>


                </table>
            </html:form>
        </td>
    </tr>

</table>
</body>
</html>
