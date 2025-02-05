<!-- /**
 * Title: searchFiles.jsp<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.FilesForm,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 java.util.ArrayList,                 
                 java.util.Collection"%>
                 
                 
                 
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>



<%@ page language="java" %>
<%
	//paginacion ini
	/* estos import son necesarios para paginar
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
	*/
	Users usuarioActual = session.getAttribute("user")!=null?(Users)session.getAttribute("user"):null;
	PaginPage Pagingbean = null;
	int lineas=Constants.LINEAS_POR_PAGINA;
	//paginacion fin
	int nameWidth=60;
	int rootWidth=100;
	String ruta = "";
	
	
	ArrayList conf = (ArrayList)session.getAttribute("conf");
	FilesForm obj;

    //if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	//response.sendRedirect("searchFiles.do");
    //}

	Collection users = null;
	Collection norms = null;

    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getAlertInfo(request,rb);
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String)session.getAttribute("usuario");

   // ToolsHTML.clearSession(session,"application.search");
    String keys = ToolsHTML.getAttribute(request,"keys")!=null?(String)ToolsHTML.getAttribute(request,"keys"):"";

    String TypesStatus = (String)ToolsHTML.getAttribute(request,"TypesStatus");
    if (ToolsHTML.isEmptyOrNull(TypesStatus)) {
        TypesStatus = "";
    }
    String typeDocument = (String)ToolsHTML.getAttribute(request,"typeDocument");
    if (ToolsHTML.isEmptyOrNull(typeDocument)) {
        typeDocument = "";
    }
    String cargo = (String)ToolsHTML.getAttribute(request,"cargo");
    if (ToolsHTML.isEmptyOrNull(cargo)) {
        cargo = "";
    }
    String owner = (String)ToolsHTML.getAttribute(request,"owner");
    if (ToolsHTML.isEmptyOrNull(owner)) {
        owner = "";
    }
    String normISO = (String)ToolsHTML.getAttribute(request,"normISO");
    if (ToolsHTML.isEmptyOrNull(normISO)) {
        normISO = "";
    }
    String docPublic = (String)ToolsHTML.getAttribute(request,"public");
    if (ToolsHTML.isEmptyOrNull(docPublic)) {
        docPublic = "";
    }
    String ordenTipo=request.getParameter("ordenTipo")!=null?"checked":"";
    String ordenNombre=request.getParameter("ordenNombre")!=null?"checked":"";
    String ordenNumero=request.getParameter("ordenNumero")!=null?"checked":"";
    String ordenPropietario=request.getParameter("ordenPropietario")!=null?"checked":"";
    String ordenCreate=request.getParameter("ordenCreate")!=null?"checked":"";
    String ordenISO=request.getParameter("ordenISO")!=null?"checked":"";
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">

    function seguridad() {
        alert('<%=rb.getString("sch.notDocsView")%>');
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=450,resizable=yes,statubar=yes,scrollbars=yes,left=200,top=150");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showDocument(idDoc,idVersion) {
    	var formaSelection = document.getElementById("Selection");
        formaSelection.idDocument.value = idDoc;
        formaSelection.action = "showDataDocument.do";
        formaSelection.submit();
    }

    function checkField(field) {
        if (field){
            if (field.value.length > 0) {
                return true;
            }
        }
        return false;
    }

    function searchFiles() {
    	forma = document.search;
        forma.target = "info";
        forma.action = "searchFiles.do";
        forma.submit();
    }

    function showStatus(ind){
        if (ind==0) {
            self.status = '';
        }
    }

    function refresh(forma,expand) {
        forma.action = "searchFiles.jsp?expand=" + expand;
        forma.submit();
    }

    function ordenar(orderBy) {
        forma = document.search;
        forma.orderBy.value = orderBy;
        forma.action = "orderFiles.do";
        forma.pages.value = "searchDocs";
        forma.submit();
    }
    
    function reporteLista() {
        frm = document.search;
        //Tipo de Documento
        if (frm.nameTypeDoc && frm.typeDocument.options[frm.typeDocument.selectedIndex].value != "0") {
            frm.nameTypeDoc.value = frm.typeDocument.options[frm.typeDocument.selectedIndex].text;
        }
        //Propietario del Documento
        if (frm.nameOwner && frm.propietario.options[frm.propietario.selectedIndex].value != "0") {
            frm.nameOwner.value = frm.propietario.options[frm.propietario.selectedIndex].text;
        }
        frm.action = "CrearReporteFiles.do";
        frm.submit();
    }
    
	function ver(etiqueta,valor) {
		if(valor=="new"){
			crear();
		}
		if(valor=="mod"){
			crear();
		}
		limpiar();
		
		window.open(etiqueta,"bandeja");
	}

	function limpiar() {
		document.getElementById("titleLeft0").style.display="";
		document.getElementById("titleLeft1").style.display="none";
		document.getElementById("titleRight0").style.display="";
		document.getElementById("titleRight1").style.display="none";
	}
	
	function crear() {
		document.getElementById("screenMain").style.display="none";
		document.getElementById("frameWork").style.display="";
	}
	
	function permission(type,nodeType) {
		  crear();
		  limpiar();
		  var forma = document.getElementById("Selection");
	      forma.target="bandeja";
	      forma.action = "loadAllUserFiles.do";
	      
	      if (forma.nodeType) {
	          forma.nodeType.value = nodeType;
	      }
	      if (type==1) {
	          forma.action = "loadAllGroupsFiles.do";
	      }
	      forma.submit();
	}
	
	function main(){
		redimensionarIframe("0");
	}
    
</script>
<!-- paginacion ini -->
<script language="javascript">
    function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        document.formPagingPage.submit();
    }
</script>
<!-- paginacion fin -->
<!--<script type="text/javascript" src="estilo/scroll.js"></script>-->
<script type="text/javascript">
	//registraScroll('flechaAbajo','flechaArriba','listaOpcion',8,-8);
</script>
</head>

<body class="bodyInternas">

<!-- Formulario para Obtener el nodo Seleccionado por el usuario -->
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2">
							<tr>
								<td>
									<a id="opt" href="#" class="menutip enlace_imagen">
									<img src="images/rightDark.gif" border="0" onClick="" onmouseover="document.getElementById('x').style.display='';">
								    <span id="x" onmouseover="limitar('opt')" class="nueva" style="display:none;position:absolute;left:11px;top:9px;">
								    	<iframe src="optionFiles.jsp" frameborder="0" style="border: 1px solid #0cf"></iframe>
								    </span>
								    </a>
								</td>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.files")%>
								</td>
								<td>
								    &nbsp;
								</td>
<!--
								<td>
								    <span ><img src="img/normas.gif" title="<%=rb.getString("sistema.configuration")%>" onClick="javascript:ver('filesNew.do','new');"></span>&nbsp;
								</td>
-->								
							    <logic:equal name="securityFiles" property="toFilesCreate"  value="1">
								<td>
								    <span ><img src="icons/page_white.png" title="<%=rb.getString("sistema.new")%>" onClick="javascript:ver('filesRegister.do','new');"></span>&nbsp;
								</td>
					            </logic:equal>
								<td>
								    <span ><img src="icons/find.png" title="<%=rb.getString("btn.search")%>" onClick="javascript:searchFiles();"></span>&nbsp;
								</td>
							    <logic:equal name="securityFiles" property="toFilesExport"  value="1">
								<td>
								    <span ><img src="icons/page_excel.png" title="<%=rb.getString("lst.reporte")%>" onClick="javascript:reporteLista();"></span>&nbsp;
								</td>
					            </logic:equal>
							</tr>
						</table>
					</td>
				</tr>
				<!-- menu 
		        <logic:equal value="true" parameter="expand">
		        </logic:equal>
		        -->
				<tr>
					<form name="search" method="post" action="searchFiles.do">
					    <input type="hidden" name="accion" value="1"/>
					    <input type="hidden" name="orderBy" value=""/>
					    <input type="hidden" name="pages" value=""/>
					    <input id="public" name="public"   type="hidden" value="2"/>
					<td height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png">
						<div id="listaOpcion"  style="height:100%" class="scrollEnabled" >
						<table class="ppalText" style="cursor:pointer;" cellspacing="0" border="0" valign="top">
							<logic:equal name="securityFiles" property="toFilesSecurity"  value="1">
								<tr onclick="permission(0,3);">
									<td style="padding:5px">
										<img src="icons/user_green.png"/>&nbsp;<%=rb.getString("btn.permission")%>
									</td>
								</tr>
								<tr onclick="permission(1,3);">
									<td style="padding:5px;border-bottom:1px solid gray">
										<img src="icons/group.png"/>&nbsp;<%=rb.getString("btn.permission1")%>
										
									</td>
								</tr>
							</logic:equal>								
                    		<% 
                    		for(int k=0;k<conf.size();k++){%>
							    <%obj=(FilesForm)conf.get(k);
								if(obj.getCriterio()==1) {%>
								<tr>
									<td>
									    <%=obj.getEtiqueta(usuario.getLanguage())%>
						                <br/>
						                <input type="text" name="<%=obj.getId()%>" style="width:180px;height: 19px" value="<%=request.getAttribute(obj.getId())!=null?request.getAttribute(obj.getId()):""%>" <%=(obj.getTipo()==FilesForm.TYPE_DATE?Constants.VALID_DATE_JAVASCRIPT:"")%> />
									</td>
								</tr>
                            <%	}
                            }%>
						</table>
						</div>
						</form>
						<form name="Selection" id="Selection" action="loadStructMain.do">
						    <input type="hidden" name="idDocument" value=""/>
						    <input type="hidden" name="idVersion" value=""/>
						    <input type="hidden" name="showStruct" value="true"/>
						    <input type="hidden" name="from" value="searchFiles.jsp?expand=false"/>
						</form>
					    <!-- paginacion ini -->
					    <form name="formPagingPage" method="post" action="searchFiles.jsp">
					      <input type="hidden" name="visible"  value="true">
					      <input type="hidden" name="from"  value="">
					      <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
					    </form>
					    <!-- paginacion fin -->
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center" valign="top">

		    <table align=center border="0" width="100%" cellspacing="0" cellpadding="0">
		        <tr>
		            <td colspan="4">
		                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
	                        <tbody>
	                            <tr>
	                                <td id="titleLeft0" class="td_title_bc" height="21" width="30%" style="display:none">
	                                	&nbsp;
	                                </td>
	                                <td id="titleLeft1" class="td_title_bc" height="21" width="30%">
	                                	<!-- paginador -->
					                    <logic:present name="searchDocs" scope="session">
					                        <!-- paginacion ini -->
					                        <%
					
					                            String from = request.getParameter("from")!=null?request.getParameter("from"):"";
					                            String size = session.getAttribute("size")!=null?(String)session.getAttribute("size").toString():"0";
					
					                            if (!ToolsHTML.isNumeric(size)) {
					                                size = "1";
					                            }
					                            if (!ToolsHTML.isNumeric(from)) {
					                                from = "0";
					                            }
					                            //PaginPage Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(usuarioActual.getNumRecord()));
					                            Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(lineas));
					                            String routImgs = "menu-images/";
					                        %>
					                        <!-- paginacion fin -->
					                        
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
										    <!-- paginacion fin -->
										    
					                    </logic:present>
	                                </td>
	                                <td class="td_title_bc" height="21"  width="40%">
		                                <%=rb.getString("enl.files")%>
	                                </td>
	                                <td id="titleRight0" class="td_title_bc" height="21"  width="30%" style="display:none">
	                                	&nbsp;
	                                </td>
	                                <td id="titleRight1" class="td_title_bc" height="21"  width="30%">
								        <logic:present name="size" scope="session">
			                            <span class="tituloSuave2">
			                                    (<%=rb.getString("sch.totalDoc") + " " + session.getAttribute("size") + " " + rb.getString("cbs.documents")%>)
			                            </span>
								        </logic:present>
								        &nbsp;
	                                </td>
	                            </tr>
	                        </tbody>
		                </table>
		            </td>
		        </tr>
		    </table>
		    <table align=center border="0" width="100%"  height="96%" cellspacing="0" cellpadding="0">
                <tr id="frameWork" style="display:none">
                    <td colspan="8"  valign="top" height="100%">
                        <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="19">
										<iframe name="bandeja" id="bandeja" width="100%" height="100%" border="0px" marginwidth="0px" marginheight="0px" frameborder="0"></iframe>
                                    </td>
                              </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
		        <tr id="screenMain" >
		            <td><!--titulos-->
		                <table align="center" border="0" cellspacing="1" cellpadding="1" width="100%" height="100%">
		                    <tr>
                        		<%for(int k=0;k<conf.size();k++){%>
		                        <td id="COL_F<%=(k+1)%>" class="td_orange_l_b barraBlue" style="height:14px;text-align:center" nowrap>
									<%obj=(FilesForm)conf.get(k);
									out.print(obj.getEtiqueta(usuario.getLanguage()));%>
                                </td>
                                <%}%>
		                    </tr>
							<logic:notPresent name="searchDocs" scope="session">
			                        <tr>
			                            <td colspan="<%=conf.size()%>"  valign="top" height="100%">
			                                <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border="0">
			                                    <tbody>
			                                        <tr>
			                                            <td class="td_title_bc" height="19" >
										                    <%=rb.getString("sch.notFound")%>
			                                            </td>
			                                      </tr>
			                                    </tbody>
			                                </table>
			                            </td>
			                        </tr>
							</logic:notPresent>
		                    <logic:present name="searchDocs" scope="session">
		                    
		                    	<% int pintada=0;
		                    	   int cont=0; 
		                    	   int indice=-1;%>
		                    	<!-- paginacion ini -->
		                        <logic:iterate id="bean" name="searchDocs" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
		                        	type="com.desige.webDocuments.files.forms.ExpedienteForm"  scope="session">
		                        	<%pintada++;%>
		                        <!-- paginacion fin -->
		                        	<tr class='fondo_<%=cont==0?cont++:cont--%> mano'  onclick="ver('filesView.do?f1=<%=bean.getF1()%>','mod');">
		                        		<%indice++;%>
		                        		<%for(int k=1;k<=conf.size();k++){
		                        		obj=(FilesForm)conf.get(k-1);%>
		                                <td id="LIN_F<%=(k)%>_<%=indice%>" class="td_gris_l" nowrap style="text-align:<%=(obj.getTipo()==2?"right":(obj.getTipo()==3?"center":""))%>;">
		                                	<%if(obj.getTipo()==FilesForm.TYPE_DATE){%>
												<%
												try{
													out.println(ToolsHTML.getSdfShowWithoutHour(ToolsHTML.date.parse(ToolsHTML.isEmptyOrNull(bean.get("F"+String.valueOf(k)),""))));
												} catch(Exception e){
													out.println(ToolsHTML.isEmptyOrNull(bean.get("F"+String.valueOf(k)),""));
												}
												%>
		                                	<%} else if(obj.getTipo()==FilesForm.TYPE_NUMERIC){%>
		                                		<%=ToolsHTML.formatNumberInteger(ToolsHTML.isEmptyOrNull(bean.get("F"+String.valueOf(k)),"0"))%>
		                                	<%} else {%>
												<%=ToolsHTML.isEmptyOrNull(bean.get("F"+String.valueOf(k)),"")%>
		                                	<%}%>
		                                </td>
		                                <%}%>
		                            </tr>
		                        </logic:iterate>
		                        <%for(pintada++;pintada<=lineas;pintada++){%>
		                        	<tr>
		                        		<td colspan="<%=conf.size()%>">&nbsp;</td>
		                        	</tr>
		                        <%}%>
		                    </logic:present>
		                </table>
		            </td>
		        </tr>
		    </table>

     </tr>
</table>
</body>
</html>
<script>
function visualizar(col){
	all="<%=Constants.COLUMN_FILES_ALL%>";
	var v = col.split(",");
	var permiso=false;
	for(var x=1; x<=<%=conf.size()%>; x++){
		permiso=false
		for(var k=0; k<v.length; k++){
			if(v[k]==x){
				permiso=true;
			}
		}
		document.getElementById("COL_F"+x).style.display=(permiso?"":"none");
		try{
			for(var n=0;n<<%=lineas%>;n++){
				document.getElementById("LIN_F"+x+"_"+n).style.display=(permiso?"":"none");
			}
		}catch(e){
		}
	}
}

visualizar("<%=HandlerBD.getOptionFiles((int)usuarioActual.getIdPerson())%>");

function redimensionarIframe(tipo) {
	// Guardamos el iframe que vamos a redimensionar
	var iFrame = window.document.getElementById('bandeja');

	//alert(document.body.scrollHeight);
	//alert(document.body.offsetHeight);
	//alert(document.body.clientHeight);
	
	var alturaIframe = document.body.scrollHeight-30; //-12
	// Redimensionamos la altura del iframe
	if(typeof tipo== "undefined" && !document.all){
		iFrame.style.height = "100%";
		setTimeout("redimensionarIframe('0')",500);
	} else {
		iFrame.style.height = eval("'"+alturaIframe+"px'");
	}
	
}// Fin de la función

//if(document.all) {
	window.onresize=redimensionarIframe;
//}
</script>

<script language="javascript" event="onload" for="window">
//if(document.all) {
	redimensionarIframe("0");
	<%=onLoad%>
//}

</script>
