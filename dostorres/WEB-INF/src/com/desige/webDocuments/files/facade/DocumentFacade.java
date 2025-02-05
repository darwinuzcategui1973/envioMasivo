package com.desige.webDocuments.files.facade;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.EquivalenciasDAO;
import com.focus.qweb.to.EquivalenciasTO;
import com.focus.util.Archivo;

import sun.jdbc.rowset.CachedRowSet;

public class DocumentFacade {
	
	private ConfDocumentoDAO oConfDocumentoDAO = null;

	private HttpServletRequest request;
	private String eti1="<!-- INI CAMPOS ADICIONALES -->";
	private String eti2="<!-- FIN CAMPOS ADICIONALES -->";
	
	public DocumentFacade() {
	}

	public DocumentFacade(HttpServletRequest request) {
		this.request = request;
	}

	public void storeStructure(Servlet servlet, ActionForm form, SuperAction action, boolean isReader,
			boolean doQuerys) throws Exception {
		String fileNameOut = "editDataDocument.jsp";
		String fileNameOut2 = "upploadFileDocument.jsp";
		String fileNameOut2D = "upploadFileDigital.jsp";
		String fileNameOut3 = "searchDocument.jsp";
		String fileNameOut4 = "published.jsp";
		String fileNameOut5 = "searchDocumentUbicacion.jsp";
		
		String filePublic = "";
		if(isReader) {
			fileNameOut = "showDataDocument.jsp";
			fileNameOut2 = "";
			fileNameOut2D = "";
			fileNameOut3 = "";
			fileNameOut4 = "";
			fileNameOut5 = "";
			filePublic = "";
		}
		
		StringBuffer html = new StringBuffer();
		ArrayList lista;
		DocumentForm campo;
		boolean isDelete = false;
		DocumentForm document = (DocumentForm) form;
		
		// variables para el query
		StringBuffer query = new StringBuffer();
		StringBuffer query1 = new StringBuffer();
		StringBuffer query2 = new StringBuffer();
		StringBuffer query3 = new StringBuffer();
		StringBuffer query4 = new StringBuffer();

		EquivalenciasTO equivalenciasTO = new EquivalenciasTO();
		EquivalenciasDAO equivalenciasDAO = new EquivalenciasDAO();
		
		oConfDocumentoDAO = new ConfDocumentoDAO();
		if(document != null){
			if(request.getParameter("eliminar")!=null && String.valueOf(request.getParameter("eliminar")).equals("true")) {
				oConfDocumentoDAO.delete(document);
				// actualizamos las equivalencias 
				equivalenciasDAO.eliminarPorCampo(document.getId());
				isDelete=true;
			} else {
				oConfDocumentoDAO.save(document);
			}
		}


		// Html para las paginas
		
		// crear el html para la pagina de registro
		lista = (ArrayList) oConfDocumentoDAO.findAllByOrder();
		
		html = construirHTML(lista, isReader, false,"0");

		// leemos el archivo base y escribimos el archivo jsp final
		ServletContext servletContext = null;
		if(action != null){
			servletContext = action.getServlet().getServletContext();
		} else {
			servletContext = servlet.getServletConfig().getServletContext();
		}
		
		fileNameOut = ToolsHTML.getPath().concat(fileNameOut);
		if(!fileNameOut2.equals("")) {
			fileNameOut2 = ToolsHTML.getPath().concat(fileNameOut2);
			fileNameOut2D = ToolsHTML.getPath().concat(fileNameOut2D);
		}
		if(!fileNameOut3.equals("")) {
			fileNameOut3 = ToolsHTML.getPath().concat(fileNameOut3);
		}
		if(!fileNameOut4.equals("")) {
			fileNameOut4 = ToolsHTML.getPath().concat(fileNameOut4);
		}
		if(!fileNameOut5.equals("")) {
			fileNameOut5 = ToolsHTML.getPath().concat(fileNameOut5);
		}
		
		StringBuffer resultado = new StringBuffer();
		StringBuffer contenido = new StringBuffer();
		//contenido = contenido.replaceAll("<!--validacion-->", condicion.toString());

		// archivo donde se modifican las propiedades del documento
		resultado.setLength(0);
		contenido.setLength(0);
		Archivo archivo = new Archivo();
		contenido = archivo.leer(fileNameOut);
		resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(html).append(contenido.substring(contenido.indexOf(eti2)));
		archivo.escribir(fileNameOut, resultado.toString());
		
		if(!fileNameOut2.equals("")) {
			// archivo de ingreso de documentos manual
			resultado.setLength(0);
			contenido.setLength(0);
			// archivo de documento
			Archivo archivo2 = new Archivo();
			contenido = archivo2.leer(fileNameOut2);
			resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(html).append(contenido.substring(contenido.indexOf(eti2)));
			archivo2.escribir(fileNameOut2, resultado.toString());
			
			// archivo de ingreso de documentos digital
			resultado.setLength(0);
			contenido.setLength(0);
			// archivo digital
			archivo2 = new Archivo();
			contenido = archivo2.leer(fileNameOut2D);
			resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(html).append(contenido.substring(contenido.indexOf(eti2)));
			archivo2.escribir(fileNameOut2D, resultado.toString());
			
		}
		if(!fileNameOut3.equals("") || !fileNameOut4.equals("")) {
			StringBuffer htmlSearch = construirHTML(lista, isReader, true,"7");
			// archivo de busqueda de documentos
			if(!fileNameOut3.equals("")) {
				resultado.setLength(0);
				contenido.setLength(0);
				Archivo archivo3 = new Archivo();
				contenido = archivo3.leer(fileNameOut3);
				resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(htmlSearch).append(contenido.substring(contenido.indexOf(eti2)));
				
				archivo3.escribir(fileNameOut3, resultado.toString());
			} 
			if(!fileNameOut4.equals("")) {
				resultado.setLength(0);
				contenido.setLength(0);
				Archivo archivo4 = new Archivo();
				contenido = archivo4.leer(fileNameOut4);
				resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(htmlSearch).append(contenido.substring(contenido.indexOf(eti2)));
				archivo4.escribir(fileNameOut4, resultado.toString());
			}
		}
		if(!fileNameOut5.equals("")) {
			// crear el html para la pagina de registro
			//lista = (ArrayList) oConfDocumentoDAO.findAllByOrderUbicacion();
			lista = (ArrayList) oConfDocumentoDAO.findAllByOrder();

			StringBuffer htmlUbicacion = construirHTML(lista, isReader, false,"0");
			// archivo para el cambio de ubicacion en lote
			resultado.setLength(0);
			contenido.setLength(0);
			Archivo archivo5 = new Archivo();
			contenido = archivo.leer(fileNameOut5);
			resultado.append(contenido.substring(0,contenido.indexOf(eti1)+eti1.length())).append("\n").append(htmlUbicacion).append(contenido.substring(contenido.indexOf(eti2)));
			archivo.escribir(fileNameOut5, resultado.toString());
		}
		
		
		if(isReader && doQuerys) {
			// Modificamos la tabla documento
			boolean isColumnNew=true;
			if(true) {
				query.append(JDBCUtil.describeTable("documents"));
				
				CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName()); // obtenemos la estructura de la tabla
				while(crs.next()){
					//System.out.println(crs.getString("COLUMN_NAME"));
					if((crs.getString("COLUMN_NAME").equals(document.getId()))){
						isColumnNew=false;
						break;
					}
				}
				
				query.setLength(0);
				query1.setLength(0);
				query3.setLength(0);
				query4.setLength(0);
				if(!isDelete) {
					if(isColumnNew){
						query.append(JDBCUtil.alterTableAdd("documents",document.getId(),String.valueOf(document.getLongitud())));
						query4.append(JDBCUtil.alterTableAddUnique("documents",document.getId()));
					} else {
						query.append(JDBCUtil.alterTableModify("documents",document.getId(),String.valueOf(document.getLongitud())));
						query3.append(JDBCUtil.alterTableDropUnique("documents",document.getId()));
						query4.append(JDBCUtil.alterTableAddUnique("documents",document.getId()));
					}
					JDBCUtil.executeUpdate(query);
					try {
						if(query1.length()>0) {
							JDBCUtil.executeUpdate(query1);
						}
					}catch(SQLException e){
						//No especificamos el error;
					}
					try{
						if(query3.length()>0) {
							JDBCUtil.executeUpdate(query3);
						}
					}catch(Exception e){
						// antes de crearlo lo eliminamos si existe;
					}
					if(document.getAuditable()==1) {
						try{
							JDBCUtil.executeUpdate(query4);
						}catch(Exception e){
							document.setAuditable(0);
							oConfDocumentoDAO.updateAuditable(document);
							throw new ApplicationExceptionChecked("E0123");
						}
					}
				
					// actualizamos las equivalencias
					StringBuffer sql = new StringBuffer();
	
					equivalenciasTO.setCampo(document.getId().concat("-"));
					// consultamos para saber si el campo ya existe en las equivalencias
					equivalenciasDAO.cargarPorCampo(equivalenciasTO); 
					
					if(!equivalenciasTO.getIndice().equals("0")) {
						// si ya existe el campo solo actualizamos los datos
						equivalenciasTO.setCampo(document.getId().concat("-").concat(document.getEtiqueta02()));
						equivalenciasDAO.actualizarAdicionales(equivalenciasTO);
					} else {
						// si no existe insertamos las equivalencias por cada nombre
						int total = EquivalenciasTO.TOTAL_EQUIVALENCIAS_DEFINIDAS; // es el numero de equivalencias predefinidas
						ArrayList item = equivalenciasDAO.listarNombre();
						for(int i=0; i<item.size(); i++) {
							equivalenciasTO = (EquivalenciasTO)item.get(i);
							equivalenciasTO.setIndice(String.valueOf(Integer.parseInt(document.getId().replaceAll("d",""))+total));
							//equivalenciasTO.setNombre(); // ya lo tiene
							equivalenciasTO.setCampo(document.getId().concat("-").concat(document.getEtiqueta02()));
							equivalenciasTO.setPosicion("0");
							equivalenciasTO.setColumna("");
							equivalenciasTO.setValor("");
							equivalenciasTO.setIndexar("");
							//equivalenciasTO.setActivo("0"); // ya lo tiene
							equivalenciasDAO.insertar(equivalenciasTO);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param lista
	 * @param isReader
	 * @param isFiltro
	 * @param indice
	 * @return
	 * @throws Exception
	 */
	private StringBuffer construirHTML(ArrayList lista, boolean isReader, boolean isFiltro,String indice) throws Exception {
		StringBuffer html = new StringBuffer();
		StringBuffer cad = new StringBuffer();
		StringBuffer condicion = new StringBuffer();
		boolean isId = false;
		boolean isOwner = false;
		boolean isFecha = false;
		boolean isNumber = false;
		boolean isName = true;
		String idBefore = "";
		String nameBefore = "";
		
		DocumentForm campo;
		
		// creamos un arreglo ordenado
		TreeMap<String,String> mapa = new TreeMap<String,String>();
		for (int i = 0; i < lista.size(); i++) {
			campo = (DocumentForm) lista.get(i);
			mapa.put(campo.getId(), campo.getEtiqueta02()); 
		}

		//html.append("  <table cellspacing='2' cellpadding='").append(isReader?3:0).append("' border='0' class='texto' width='100%'>\n");
		for (int i = 0; i < lista.size(); i++) {
			cad.setLength(0);
			campo = (DocumentForm) lista.get(i);
			if(isFiltro && campo.getCriterio() == 0) {
				continue;
			}
			if (campo.getVisible() == 1) {
				isName = !campo.getEtiqueta02().equals(nameBefore);
				isId = campo.getId().equals("f1");
				isOwner = campo.getId().equals("f3");
				isFecha = campo.getTipo() == 3;
				isNumber = campo.getTipo()==2;
				String validDate = Constants.VALID_DATE_JAVASCRIPT;
				
				// condicion
				if(!campo.getCondicion().trim().equals("")) {
					String[] requeridos = campo.getCondicion().trim().split("-");
					condicion.append(StringUtil.replace("if(!isEmpty(document.forms[0].P1)) {\n",new String[]{campo.getId()}));
					for(int k=0;k<requeridos.length;k++){
						condicion.append(StringUtil.replace("				  if(!isValido(document.forms[0].P1,'P2')) return false;\n",new String[]{requeridos[k],mapa.get(requeridos[k])}));
					}
					condicion.append("				}\n");
				}
				
				if (isName) {
					html.append("    <tr ").append(isFiltro?"id=\"mas_<%=(contador++)+".concat(indice).concat("%>\" style=\"display:none\" "):"id='"+campo.getId()+"'");
					html.append(" class='");
					//if(!isFiltro) {
						html.append(",").append(oConfDocumentoDAO.typeDocumentsAssociateString(campo.getId())).append(",");
					//}
					html.append("' ");
					html.append(" ubicacion='").append(campo.getLocation()==1).append("' "); // marcamos los campos de ubicacion
					html.append(">\n");
					html.append("      <td ").append(isFiltro?"":"class='titleLeft' ").append("width='200' height='22' ").append(isFiltro?"":"style='background: url(img/btn160.gif);' ").append(" >\n");
					html.append("        ");
					html.append("<%=usuario.getLanguage().equals(\"es\")?\"").append(campo.getEtiqueta02()).append("\":\"").append(campo.getEtiqueta01()).append("\"%>");
					html.append(":\n");
					if(isFiltro) {
						html.append("      <br/>\n");
					} else {
						html.append("      </td>\n");
						html.append("      <td>\n");
					}
				}

				if (isOwner && !isReader) {
					HandlerDBUser.getAllUsers();
					String[] valores = campo.getValores().split(",");
					cad.append("        <html:select name='doc' property='A1' >\n".replaceAll("A1", campo.getId()));
					//cad.append("            <html:option value='0'>&nbsp;</html:option>");
					cad.append("            <logic:present name='usuarios'>");
					cad.append("                    <html:optionsCollection name='usuarios' value='id' label='descript' />");
					cad.append("            </logic:present>");
					cad.append("        \n</html:select>\n");
				} else if (campo.getEntrada().equals("text") || isReader) {
					if (isFecha && !isFiltro) {
						cad.append("        <table cellspacing='0' cellpadding='0' border='0'>\n");
						cad.append("          <tr><td>\n");
					}
					//cad.append(StringUtil.replace("        <input type='text' name='P1' maxlength='P2' size='P3' P4 P5 />\n",new String[]{campo.getId(),String.valueOf(campo.getLongitud()), String.valueOf(campo.getLongitud() < 70 ? campo.getLongitud() : 70), isFecha || isId ? "readonly='true'" : "", isNumber?"onkeyup='format(this)' onchange='format(this)'":""}));
					if(isReader) {
						cad.append(StringUtil.replace("        <span class='texto'><bean:write name='doc' property='P1' /></span>\n",new String[]{campo.getId().equals("f2")?"fecha":(campo.getId().equals("f3")?"nameUser":campo.getId())}));
					} else {
						cad.append(StringUtil.replace("        <html:text ".concat(isFiltro?"style='width:180px;height: 19px;' ":"").concat(" name='doc' property='P1' maxlength='P2' size='P3' P4 P5 P6 />\n"),new String[]{campo.getId().equals("f2")?"fecha":campo.getId(),String.valueOf(campo.getLongitud()), String.valueOf(campo.getLongitud() < 70 ? campo.getLongitud() : 70), isId || campo.getEditable()==0 ? "readonly='true'" : "", isNumber?"onkeyup='format(this)' onchange='format(this)'":"", isFecha?validDate:"" }));
					}
					if (isFecha && !isFiltro) {
						cad.append("          </td><td>\n");
					}
				} else if (campo.getEntrada().equals("radio") && !isFiltro) {
					String[] valores = campo.getValores().split(",");
					String valor;
					for (int x = 0; x < valores.length; x++) {
						valor = valores[x];
						if(valores[x].indexOf("(")!=-1){
							valor = valor.substring(0,valores[x].indexOf("("));
							String[] cond =  valores[x].substring(valores[x].indexOf("(")).replaceAll("[()]","").split("-");
							for(int k=0; k<cond.length; k++) {
								condicion.append(StringUtil.replace("				if(!isItemValid(document.forms[0].P1,'P2',document.forms[0].P3,'P4')) return false;\n",new String[]{campo.getId(),valor,cond[k],mapa.get(cond[k])}));
								//isItemValid(document.forms[0].f4,'Marca Comercial(f5)',document.forms[0].f5,'Propietario');
							}
						}
						cad.append(StringUtil.replace("        <html:radio name='doc' property='P1' value='P2' />P2&nbsp;&nbsp;\n",new String[]{campo.getId(),valor}));
					}
				} else if (campo.getEntrada().equals("checkbox") && !isFiltro) {
					cad.append(StringUtil.replace("        <html:checkbox name='doc' property='P1' value='P2' />P2&nbsp;&nbsp;\n<!--otroP1-->",new String[]{campo.getId(),campo.getValores()}));
				} else if (campo.getEntrada().equals("select") || (campo.getEntrada().equals("radio") && isFiltro) || (campo.getEntrada().equals("checkbox") && isFiltro) || (campo.getEntrada().equals("textarea") && isFiltro) ) {
					String[] valores = campo.getValores().split(",");
					cad.append("        <html:select ").append(isFiltro?"style='width:180px;height: 19px;' ":"").append("name='doc' property='A1' >\n".replaceAll("A1", campo.getId()));
					for (int x = 0; x < valores.length; x++) {
						if(x==0 && isFiltro && (valores[x]==null || valores[x]!=null && !valores[x].trim().equals(""))) {
							cad.append("          <html:option value=''></html:option>\n");
						}
						cad.append("          <html:option value='A1'>A1</html:option>\n".replaceAll("A1", valores[x]));
					}
					cad.append("        </html:select>\n");
				} else if (campo.getEntrada().equals("textarea") && !isFiltro) {
					cad.append(StringUtil.replace("        <html:textarea name='doc' property='P1' rows='5' cols='50'></html:textarea>\n",new String[]{campo.getId()}));
				}

				if (isFecha && !isFiltro) {
					if (!isReader && !campo.getId().equals("f2")) {
						cad.append(" <a href=\"javascript:show_calendar('forms[0].A1', 'forms[0].A1', 'forms[0].A1');\"".replaceAll("A1", campo.getId()));
						cad.append(" onmouseover=\"window.status='Seleccione la fecha';return true;\"");
						cad.append(" onmouseout=\"window.status='';return true;\">");
						cad.append(" <img src=\"images/calendario.gif\" border=\"0\" title=\"Seleccionar Fecha\"></a>");
					} else {
						//html.append("<html:hidden name='doc' property='A1' />".replaceAll("A1", campo.getId()));
						cad.append("&nbsp;");
					}
					cad.append("          </td></tr></table>\n");
				}

				if (isName) {
					html.append(cad);
					html.append("      </td>\n");
					html.append("    </tr>\n");
				} else {
					html.replace(0, html.length(), html.toString().replaceAll("<!--otro".concat(idBefore).concat("-->"), cad.toString()));
					// htmlCopy.setLength(0);
					// htmlCopy.append(html.toString());
					// html.setLength(0);
					// html.append(htmlCopy.toString());
					// html.append(htmlCopy.toString().replaceAll("<!--otro-->",
					// cad.toString()));
				}
				idBefore = campo.getId();
				nameBefore = campo.getEtiqueta02();
			} else {
				html.append("<html:hidden name='doc' property='A1' />".replaceAll("A1", campo.getId()));
			}
		}
		//html.append("  </table>\n");
		return html;
	}
	
	
	public static void main1(String[] args) {
		String cad="marca comercial f1 firmada(f1,f3)";
		//System.out.println(cad);
		//System.out.println(cad.substring(cad.indexOf("(")).replaceAll("[()]",""));
		cad=cad.substring(0,cad.indexOf("("));
		//System.out.println(cad);
		System.exit(0);
	}
	
	public static void main(String[] args) {
		String eti1="<ini>",eti2="<fin>";
		StringBuffer res = new StringBuffer();
		StringBuffer cad= new StringBuffer("marca comercial f1 firmada<ini> esste <asdfas> es un comentario x<fin> con toda la razon");
		//System.out.println(cad);
		
		res.append(cad.substring(0,cad.indexOf(eti1)+eti1.length())).append(cad.substring(cad.indexOf(eti2)));
		//res.append(cad.substring(0,cad.indexOf(eti1))).append(eti1).append(eti2).append(cad.substring(cad.indexOf(eti2)+eti2.length()));
		//System.out.println(res);
		//cad=cad.substring(0,cad.indexOf("<ini>"));
		////System.out.println(cad);
		System.exit(0);
	}

	public void saveTiposDeDocumentosAsociados(ActionForm form) throws Exception {
		DocumentForm document = (DocumentForm) form;
		
		String[] tiposAsociados = request.getParameterValues("tipos");
		
		ConfDocumentoDAO confDocumentoDAO = new ConfDocumentoDAO();
		confDocumentoDAO.mergeTypeDocuments(document.getId(), tiposAsociados);	
	}
}
