package com.focus.migracion.actions;

import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.actions.FileUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.bean.ExcelReader;
import com.focus.qweb.bean.ExcelReaderComplete;
import com.focus.qweb.facade.MigracionFacade;

public class ExcelAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(ExcelAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			// sube los archivos seleccionados
			FileUtil fu = new FileUtil();
			Properties campos = fu.procesaFicheros(request);
			
			ArrayList<String> lista = new ArrayList<String>();
			if(campos.get("nombre0").toString().length()>0) {
				lista.add(campos.get("nombre0").toString());
			}
			if(campos.get("nombre1").toString().length()>0) {
				lista.add(campos.get("nombre1").toString());
			}
			if(campos.get("nombre2").toString().length()>0) {
				lista.add(campos.get("nombre2").toString());
			}
			if(campos.get("nombre3").toString().length()>0) {
				lista.add(campos.get("nombre3").toString());
			}
			if(campos.get("nombre4").toString().length()>0) {
				lista.add(campos.get("nombre4").toString());
			}

			ExcelReaderComplete datos;
			StringBuffer name = new StringBuffer();
			String[][] data = new String[lista.size()][11];
			for (int i = 0; i < lista.size(); i++) {
				System.out.println(lista.get(i));

				name.setLength(0);
				name.append(ToolsHTML.getPathTmp()).append(lista.get(i));
        		datos = new ExcelReaderComplete();
        		datos.load(name.toString());
        		
        		data[i][0] = 
        		
        		data[i][0] =datos.getValue(1, 17); // codigo
        		data[i][1] =datos.getValue(1, 20); // fecha
        		data[i][2] =datos.getValue(13, 5); // nombre
        		data[i][3] =datos.getValue(15, 5); // apellido
        		data[i][4] =datos.getValue(19, 5); // cedula
        		data[i][5] =datos.getValue(21, 5); // fecha
        		
        		data[i][6] =datos.getValue(108, 2); // total preguntas
        		data[i][7] =datos.getValue(109, 6); // puntaje
        		data[i][8] =datos.getValue(109, 7); // resultado
        		data[i][9] =datos.getValue( 99, 8); // buenas
        		data[i][10] =datos.getValue( 99, 9); // malas
			}

			request.setAttribute("data", data);
			return goSucces();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}
}
