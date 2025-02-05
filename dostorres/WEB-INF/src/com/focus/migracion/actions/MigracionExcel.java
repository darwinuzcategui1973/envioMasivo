package com.focus.migracion.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.actions.ShowDocument;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

public class MigracionExcel {

	public static final short COLUMNA_NUMERO = 0;

	public static final short COLUMNA_nombre = 1;
	public static final short COLUMNA_carpetaDestino = 2;
	public static final short COLUMNA_carpetaActual = 3;
	public static final short COLUMNA_publicado = 4;
	public static final short COLUMNA_propietario = 5;
	public static final short COLUMNA_tipoDocumento = 6;
	//public static final short COLUMNA_prefijo = 7;
	public static final short COLUMNA_norma = 7;
	public static final short COLUMNA_palabrasClaves = 8;
	public static final short COLUMNA_url = 9;
	public static final short COLUMNA_descripcion = 10;
	public static final short COLUMNA_comentarios = 11;
	public static final short COLUMNA_nombreFisico = 12;
	public static final short COLUMNA_status = 13;
	public static final short COLUMNA_creado = 14;
	public static final short COLUMNA_vencimiento = 15;
	public static final short COLUMNA_verMayor = 16;
	public static final short COLUMNA_verMenor = 17;
	public static final short COLUMNA_documentosRelacionados = 18;
	public static final short COLUMNA_CONTROL = 21;
	public static final short COLUMNA_CONTROL_REL = 22;

	private SimpleDateFormat dateFormat;
	private SimpleDateFormat format;

	private PrintWriter log;
	private PrintWriter ok;
	private PrintWriter mal;

	public static String errorProceso = "";
	
	private static String nameFileLog="";
	
	public static String getNameFileLog() {
		return nameFileLog;
	}

	public void setUp(String prefijo, String path) throws ClassNotFoundException, SQLException, IOException {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		format = new SimpleDateFormat("dd-MM-yyyy: hh:mm:ss");
		prefijo = "";
		String diahora = format.format(new Date());
		diahora = diahora.replaceAll(":", "_");
		diahora = diahora.replaceAll(" ", "");
		nameFileLog = "migracion_" + diahora;
        String nameFile = path.concat("logs").concat(File.separator).concat(nameFileLog);

		log = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_LOG.txt")));
		ok = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_OK.txt")));
		mal = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_MAL.txt")));
	}


	public static void inicio(String archivoLectura, String nombreLogs, String path) throws Exception {
		MigracionExcel me = new MigracionExcel();

		String nombreArchivo = archivoLectura;

		me.setUp(nombreLogs,path);

		//System.out.println("Procesando archivo = ".concat(nombreArchivo));
		me.migrar(nombreArchivo);
	}

	public void migrar(String nombreArchivo) {

		log.println(format.format(new Date()) + " iniciando la migracion");

		// EL sistema de archivo de poi de donde se va a cargar el libro de excel
		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(nombreArchivo));
		} catch (FileNotFoundException e) {
			//System.out.println("El archivo no existe");
			return;
		} catch (IOException e) {
			//System.out.println("error procesando el archivo");
			e.printStackTrace();
			return;
		}

		// Cargo el libro de excel.
		HSSFWorkbook wb;
		try {
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			//System.out.println("Error cargando el archivo EXCEL");
			e.printStackTrace();
			return;
		}

		log.println(format.format(new Date()) + "fue abierto el libro de excel");

		// Obtengo la Hoja de Excel.
		HSSFSheet hoja = wb.getSheetAt(0);
		
		//hoja.getPrintSetup().getLandscape() // pregunta si es horizontal (landscape)

		// Declaro las variables a usar.
		HSSFRow filaActual = null;

		int numeroFilaActual = 0; // Contador de la fila actual
		boolean eof = false; // Es fin del archivo

		String archivo;

		String numero = null;
		String nombre;
		String carpetaDestino;
		String carpetaActual;
		String publicado;
		String propietario;
		String tipoDocumento;
		String esPublico = "1";  //Logica inversa como la tabla

		String norma; // TODO Aplicar normas...
		String palabrasClaves;
		String url;
		String descripcion;
		String comentarios;
		String nombreFisico;
		String status = null;
		String creado;
		String vencimiento;
		String verMayor = null;
		String verMenor = null;
		String documentosRelacionados = "";

		HSSFCell celdaControl = null;

		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

		principal: while (!eof) {

			log.println("--------------------------------------------------------------");
			//System.out.println(++numeroFilaActual);
			log.println(format.format(new Date()) + "Carga de la fila N: " + numeroFilaActual);

			filaActual = hoja.getRow(numeroFilaActual);

			if (filaActual == null) {
				ok.println(format.format(new Date()) + "|FIN DEL PROCESO");
				mal.println(format.format(new Date()) + "|FIN DEL PROCESO");
				log.println(format.format(new Date()) + "|FIN DEL PROCESO");

				log.flush();
				ok.flush();
				mal.flush();

				break principal;
			}
			celdaControl = filaActual.getCell(COLUMNA_CONTROL);
			if (celdaControl == null)
				celdaControl = filaActual.createCell(COLUMNA_CONTROL);
			celdaControl.setCellType(HSSFCell.CELL_TYPE_STRING);
			// Columna de control


			try {
				// Carga de la informacion de la fila de Excel.

				try {
					numero = filaActual.getCell(COLUMNA_NUMERO).getStringCellValue();
				} catch (java.lang.NumberFormatException e) {
					numero = Integer.toString((int) filaActual.getCell(COLUMNA_NUMERO).getNumericCellValue());

				} catch (Exception e) {
					numero = null;
				}

				/*if (numero == null || numero.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|El numero esta vacio");
					mal.flush();
					continue principal;
				}*/

				log.println(format.format(new Date()) + "numero: " + numero);

				try {
					nombre = filaActual.getCell(COLUMNA_nombre).getStringCellValue();
				} catch (Exception e) {
					nombre = null;
				}

				if (nombre == null || nombre.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|El nombre esta vacio");
					mal.flush();
					continue principal;
				}

				log.println(format.format(new Date()) + "Nombre: " + nombre);

				try {
					carpetaDestino = filaActual.getCell(COLUMNA_carpetaDestino).getStringCellValue();
				} catch (Exception e) {
					carpetaDestino = null;
				}

				if (carpetaDestino == null || carpetaDestino.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|La carpeta destino es inv�lida");
					mal.flush();
					continue principal;
				}

				log.println(format.format(new Date()) + "carpetaDestino =: " + carpetaDestino);

				carpetaActual = filaActual.getCell(COLUMNA_carpetaActual).getStringCellValue();
				log.println(format.format(new Date()) + "carpetaActual =: " + carpetaActual);

				try {
					Date pub = filaActual.getCell(COLUMNA_publicado).getDateCellValue();
					publicado = dateFormat.format(pub);
				} catch (java.lang.NumberFormatException e) {
					publicado = filaActual.getCell(COLUMNA_publicado).getStringCellValue();
					int index = publicado.indexOf("/");
					if(index>0){
						if(index==2 && publicado.length()==10){
							String anio = publicado.substring(6,10);
							String mes = publicado.substring(3,5);
							String dia = publicado.substring(0,2);
							publicado = anio + "-" + mes + "-" + dia;
						}
					}
				} catch (Exception e) {
					publicado = null;
				}

				if (publicado== null || publicado.length()<10 || publicado.equals("1899-12-31") || publicado.equals("1900-01-01")) {
					publicado = null;
				}else{
					esPublico = "0";
				}

				/*if (publicado == null || publicado.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|La fecha de publicacion esta vacia");
					mal.flush();

					sel.selectFrame("relative=up");
					sel.selectFrame("relative=up");
					continue principal;
				}*/

				log.println(format.format(new Date()) + "publicado =: " + publicado);

				propietario = filaActual.getCell(COLUMNA_propietario).getStringCellValue();
				log.println(format.format(new Date()) + "propietario =: " + propietario);

				try {
					tipoDocumento = filaActual.getCell(COLUMNA_tipoDocumento).getStringCellValue().trim();
					log.println(format.format(new Date()) + "tipoDocumento =: " + tipoDocumento);
				} catch (Exception e) {
					tipoDocumento = null;
				}

				if (tipoDocumento == null || tipoDocumento.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|El tipoDocumento esta vacio");
					mal.flush();
					continue principal;
				}

				// Norma...
				try {
					norma = filaActual.getCell(COLUMNA_norma).getStringCellValue();
					if (norma == null || norma.equals("0"))
						norma = "";
				} catch (Exception e) {
					norma = "";
				}

				log.println(format.format(new Date()) + "norma =: " + norma);

				// Palabra Clave
				try {
					palabrasClaves = filaActual.getCell(COLUMNA_palabrasClaves).getStringCellValue();
					if (palabrasClaves.equals("0"))
						palabrasClaves = "";
				} catch (Exception e) {
					// Palabras claves esta vacio
					palabrasClaves = "";
				}

				log.println(format.format(new Date()) + "palabrasClaves =: " + palabrasClaves);

				url = "";
				try {
					url = filaActual.getCell(COLUMNA_url).getStringCellValue();
					if (url.equals("0"))
						url = "";
				} catch (Exception e) {
					// El URL esta vacio
					url = "";
				}

				log.println(format.format(new Date()) + "url =: " + url);

				try {
					descripcion = filaActual.getCell(COLUMNA_descripcion).getStringCellValue();
					if (descripcion.equals("0"))
						descripcion = "";
				} catch (Exception e) {
					// La Descripcion esta vacia
					descripcion = "";
				}
				log.println(format.format(new Date()) + "descripcion =: " + descripcion);

				try {
					comentarios = filaActual.getCell(COLUMNA_comentarios).getStringCellValue();
					if (comentarios.equals("0") || ToolsHTML.isEmptyOrNull(comentarios))
						comentarios = "";
				} catch (Exception e) {
					// La Descripcion esta vacia
					comentarios = "";
				}
				
				String sComment = (String)DesigeConf.getProperty("migracionComment");
		      	if(sComment!=null && "1".equals(sComment)){				
					if(!ToolsHTML.isEmptyOrNull(comentarios)){
						comentarios += "<br>";
					}
					comentarios += rb.getString("migr.comment");
		      	}
				
				log.println(format.format(new Date()) + "comentarios =: " + comentarios);

				try {
					nombreFisico = filaActual.getCell(COLUMNA_nombreFisico).getStringCellValue();
					log.println(format.format(new Date()) + "nombreFisico =: " + nombreFisico);
				} catch (Exception e) {
					nombreFisico = null;
				}

				if (nombreFisico == null || nombreFisico.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|El nombreFisico esta vacio");
					mal.flush();
					continue principal;
				}

				try {
					status = filaActual.getCell(COLUMNA_status).getStringCellValue();
				} catch (java.lang.NumberFormatException e) {

					status = Integer.toString((int) filaActual.getCell(COLUMNA_status).getNumericCellValue());
				}

				log.println(format.format(new Date()) + "status =: " + status);

				if (status.trim().length() == 0) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|El Status esta vacio");
					mal.flush();
					continue principal;
				}

				try {
					Date cre = filaActual.getCell(COLUMNA_creado).getDateCellValue();
					creado = dateFormat.format(cre);
				} catch (java.lang.NumberFormatException e) {
					creado = filaActual.getCell(COLUMNA_creado).getStringCellValue();
					int index = creado.indexOf("/");
					if(index>0){
						if(index==2 && creado.length()==10){
							String anio = creado.substring(6,10);
							String mes = creado.substring(3,5);
							String dia = creado.substring(0,2);
							creado = anio + "-" + mes + "-" + dia;
						}
					}
				} catch (Exception e) {
					creado = null;
				}

				if (creado.length() < 10 || creado.equals("1899-12-31") || creado.equals("1900-01-01")) {
					creado = null;
				}
				if (creado == null || creado.trim().length() == 0) {
					creado = this.dateFormat.format(new Date());
				}

				log.println(format.format(new Date()) + "creado =: " + creado);

				try {

					Date ven = filaActual.getCell(COLUMNA_vencimiento).getDateCellValue();
					vencimiento = dateFormat.format(ven);
				} catch (java.lang.NumberFormatException e) {
					// La Descripcion esta vacia
					vencimiento = filaActual.getCell(COLUMNA_vencimiento).getStringCellValue();
					int index = vencimiento.indexOf("/");
					if(index>0){
						if(index==2 && vencimiento.length()==10){
							String anio = vencimiento.substring(6,10);
							String mes = vencimiento.substring(3,5);
							String dia = vencimiento.substring(0,2);
							vencimiento = anio + "-" + mes + "-" + dia;
						}
					}
				} catch (Exception e) {
					vencimiento = null;
				}

				if (vencimiento!=null && (vencimiento.length() < 10 || vencimiento.equals("1899-12-31") || vencimiento.equals("1900-01-01"))) {
					vencimiento = null;
				}

				log.println(format.format(new Date()) + "vencimiento =: " + vencimiento);

				try {
					verMayor = null;
					if (filaActual.getCell(COLUMNA_verMayor)!=null) {
						verMayor = filaActual.getCell(COLUMNA_verMayor).getStringCellValue();
					}
				} catch (java.lang.NumberFormatException e) {
					try {
						verMayor = Integer.toString((int)filaActual.getCell(COLUMNA_verMayor).getNumericCellValue());
					} catch (java.lang.NumberFormatException e2) {
						//System.out.println("COLUMNA_verMayor ="+COLUMNA_verMayor);
						//System.out.println("Valor ="+filaActual.getCell(COLUMNA_verMayor).getStringCellValue().trim());
						verMayor = null;
						e2.printStackTrace();
					}

				}

				if (verMayor == null || verMayor.trim().equals("")) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|La versi�n mayor no es v�lida");
					mal.flush();
					continue principal;
				}

				log.println(format.format(new Date()) + "verMayor =: " + verMayor);

				try {
					verMenor = "";
					if (filaActual.getCell(COLUMNA_verMenor)!=null) {
						verMenor = filaActual.getCell(COLUMNA_verMenor).getStringCellValue();
					}
				} catch (java.lang.NumberFormatException e) {
					try {
						verMenor = Integer.toString((int)filaActual.getCell(COLUMNA_verMenor).getNumericCellValue());
					} catch (Exception e2) {
						verMenor = null;
						e2.printStackTrace();
					}
				} catch (Exception e) {
					verMenor = null;
					e.printStackTrace();
				}

				if (verMenor == null || verMenor.trim().equals("")) {
					celdaControl.setCellValue("NO");
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "|La versi�n menor no es v�lida");
					mal.flush();
					continue principal;
				}

				log.println(format.format(new Date()) + "verMenor =: " + verMenor);

				try {
					documentosRelacionados = filaActual.getCell(COLUMNA_documentosRelacionados).getStringCellValue();
				} catch (Exception e) {
					documentosRelacionados = "";
				}
				log.println(format.format(new Date()) + "documentosRelacionados =: " + documentosRelacionados);
				StringTokenizer items = new StringTokenizer(documentosRelacionados, ";");
				String prefijoNumero = "";
				StringBuffer docsRelacionados = new StringBuffer();
				while (items.hasMoreTokens()) {
					prefijoNumero = items.nextToken();
					String idDoc = buscarNumGenRelacionado(prefijoNumero);
					if(!ToolsHTML.isEmptyOrNull(idDoc) && ToolsHTML.isNumeric(idDoc)){
						String idVer = HandlerDocuments.getMayorVersion(idDoc);
						if(!ToolsHTML.isEmptyOrNull(idVer) && ToolsHTML.isNumeric(idVer))
							docsRelacionados.append(idDoc).append("_").append(idVer).append(",");
					}
				}

				// Ya tengo toda la informacion ahora me toca desmenusar lo que
				// haga falta...

				// El 0 representa el nodo ppal, el [1] la localidad y luego
				// carpetas y procesos...
				String[] estructura = carpetaDestino.split("\\\\");

				//archivo = carpetaActual + File.separator + nombreFisico;
				archivo = carpetaActual;

				// Carpetas y procesos
				int idNodo = 0;
				int idNodoPadre = 0;
				for (int struc = 0; struc < estructura.length; struc++) {
					log.println(format.format(new Date()) + "Cargando: " + estructura[struc]);
					idNodoPadre = idNodo;
					idNodo = buscarIdNode(idNodoPadre,estructura[struc]);
				}

				log.println(format.format(new Date()) + "Ir a Agregar documento ");

				// Tipo de Documento
				tipoDocumento = obtenerIdTipoDocumento(tipoDocumento);
				//
				// celdaControl.setCellValue("NO");
				// mal.println(format.format(new Date()) + "|" +
				// numeroFilaActual + "|" + numero + "|El tipo de documento no
				// es v�lido" );
				// mal.flush();
				//
				// sel.selectFrame("relative=up");
				// sel.selectFrame("relative=up");
				// }

				// Norma ISO, buscar por ID de la Base de datos
				// TODO Verificar esto....
				if(!ToolsHTML.isEmptyOrNull(norma))
					norma = obtenerIdNormaIso(norma);


				/******* VALIDACIONES  *************/
				BaseDocumentForm forma = new BaseDocumentForm();
				String path = archivo;

				String dateSystem = ToolsHTML.date.format(new java.util.Date());

				forma.setIdNode(idNodo+"");

				//Obligatorio Nombre del documento, carpeta destino, carpeta actual, propietario, tipo de documento, nombre fisico, status,
				if(ToolsHTML.isEmptyOrNull(nombre)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Nombre del documento est� vac�o o no es v�lido");
					mal.flush();
					continue principal;
				}
				if(ToolsHTML.isEmptyOrNull(carpetaDestino)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Carpeta Destino est� vac�a o no es v�lida");
					mal.flush();
					continue principal;
				}
				if(ToolsHTML.isEmptyOrNull(carpetaActual)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Carpeta Actual est� vac�a o no es v�lida");
					mal.flush();
					continue principal;
				}
				if(ToolsHTML.isEmptyOrNull(propietario)){
					//ERROR
					//mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Propietario est� vac�o o no es v�lido");
					//mal.flush();
					//continue principal;
					propietario = "admin";
				}
				if(ToolsHTML.isEmptyOrNull(tipoDocumento)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Tipo de Documento est� vac�o o no es v�lido");
					mal.flush();
					continue principal;
				}
				if(ToolsHTML.isEmptyOrNull(nombreFisico)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Nombre F�sico est� vac�o o no es v�lido");
					mal.flush();
					continue principal;
				}
				if(ToolsHTML.isEmptyOrNull(status)){
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Status est� vac�o o no es v�lido");
					mal.flush();
					continue principal;
				}

				//Si no existe fecha de creacion se coloca fecha actual
				if(ToolsHTML.isEmptyOrNull(creado)){
					creado = dateSystem;
				}

				//Numero obligatorio para aprobados
				if ((numero == null || numero.trim().length() == 0) && (status!=null && "A".equals(status))) {
					//ERROR
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) +  "| El n�mero est� vac�o");
					mal.flush();
					continue principal;
				}


				//Si esta publicado:
				//  fecha de aprobacion obligatoria
				//	fecha de aprobacion mayor a fecha de creacion
				//	fecha de aprobacion menor a fecha de vencimiento
				//  fecha de vencimiento mayor a la fecha actual
				if(status!=null && status.equals("A")){
					if(ToolsHTML.isEmptyOrNull(publicado) || esPublico==null && !"0".equals(esPublico)){
						//ERROR
						mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Fecha de aprobaci�n requerida si est� en status Aprobado");
						mal.flush();
						continue principal;
					}
				}

				if(esPublico!=null && "0".equals(esPublico) && status!=null && status.equals("A")){
				    //Si tiene fecha de aprobacion se valida que sea mayor a fecha de creacion
					if(!ToolsHTML.isEmptyOrNull(publicado) && !ToolsHTML.isEmptyOrNull(creado) &&
							ToolsHTML.compareDates(ToolsHTML.date.parse(publicado), ToolsHTML.date.parse(creado))<=0){
						//ERROR
						mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Fecha de aprobaci�n inv�lida, no puede ser menor o igual a la fecha de creacion");
						mal.flush();
						continue principal;
					}
				    //Si tiene fecha de vencimento se valida que sea mayor a fecha actual y de aprobacion.
					if(!ToolsHTML.isEmptyOrNull(publicado) && !ToolsHTML.isEmptyOrNull(vencimiento) &&
							ToolsHTML.compareDates(ToolsHTML.date.parse(publicado), ToolsHTML.date.parse(vencimiento))>=0){
						//ERROR
						mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Fecha de aprobaci�n inv�lida, no puede ser mayor o igual a la fecha de vencimiento");
						mal.flush();
						continue principal;
					}
					if(!ToolsHTML.isEmptyOrNull(vencimiento) && !ToolsHTML.isEmptyOrNull(dateSystem) &&
							ToolsHTML.compareDates(ToolsHTML.date.parse(vencimiento), ToolsHTML.date.parse(dateSystem))<=0){
						//ERROR
						mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "| Fecha de vencimiento inv�lida, no puede ser menor o igual a la fecha actual");
						mal.flush();
						continue principal;
					}
				}

				//SI SE MANEJA FECHA DE EXPIRACION DE APROBADOS
				//Si no tiene fecha de vencimiento de aprobacion se calcula
				//SI SE MANEJA FECHA DE EXPIRACION DE BORRADORES
			    //Se calcula fecha de expiracion de borrador


				// CARGA DE ESTRUCTURA
				// Para llenar el tree, colocamos el usuario como administrador,el grupo como administrador, la seguridad nula para que carque toda la estructura
				Hashtable subNodos = new Hashtable();
				Hashtable tree = HandlerStruct.loadAllNodes(null,DesigeConf.getProperty("application.admon"),DesigeConf.getProperty("application.admon"), subNodos, null);
				String idNodeDocument = idNodo+"";
				String idLocation = HandlerStruct.getIdLocationToNode(tree, idNodeDocument);
				// BaseStructForm location = HandlerStruct.loadStruct(idLocation);
				//BaseStructForm location = (BaseStructForm) tree.get(idLocation);

				// Fin de la Carga de la Estructura

                BaseStructForm carpeta = (BaseStructForm)tree.get(idNodeDocument);
                BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);

                HandlerStruct handlerStruct = new HandlerStruct();
                byte typePrefix = 0;
                if (idLocation!=null) {
                    if (localidad!=null) {
                    	if (localidad.getCheckborradorCorrelativo() == Constants.permission){
                    		//Se valida si la localidad requiere que los borradors tengan correlativo
                    		if(status!=null && status.equals("B") && (numero == null || numero.trim().length() == 0)){
                    			//ERROR
            					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) +  "| El n�mero est� vac�o. Es Borrador pero la Localidad puede requerir correlativo");
            					mal.flush();
            					continue principal;
                    		}
                    	}

                        typePrefix = localidad.getTypePrefix();

        				//Si no existe version mayor se coloca cero o A dependiendo del tipo
                        if(ToolsHTML.isEmptyOrNull(verMayor)){
                        	forma.setMayorVer(ToolsHTML.getInitValue(localidad.getMajorId(),false));
                        }

        				//Si no existe version menor se coloca cero
                        if(ToolsHTML.isEmptyOrNull(verMenor)){
	                        if (!ToolsHTML.isEmptyOrNull(localidad.getMinorKeep())) {
	                            if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
	                                int numHist = Integer.parseInt(localidad.getMinorKeep().trim());
	                                if (numHist > 0) {
	                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
	                                } else {
	                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
	                                }
	                            }
	                        } else {
	                            forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
	                        }
                        }
                    }
                }

				//Calculo de prefijo
                if (carpeta!=null) {
                    if (carpeta.getHeredarPrefijo() == Constants.permission) {
                        String prefix = handlerStruct.getParentPrefixinst(tree,idNodeDocument,0==typePrefix);
                        if (0==typePrefix) {
                            forma.setPrefix(carpeta.getPrefix()+prefix);
                        } else {
                            forma.setPrefix(prefix+carpeta.getPrefix());
                        }
                    } else {
                        forma.setPrefix(carpeta.getPrefix());
                    }
                }

                forma.setIdLocation(idLocation);
                forma.setNodeActive(idNodeDocument);


			    //Se calcula correlativo
			    //Correlativo obligatorio segun propiedad de la localidad
                String borradorCorrelativo="0";
                if(localidad!=null){
                	borradorCorrelativo = String.valueOf(localidad.getCheckborradorCorrelativo());
                }

			    if (ToolsHTML.isEmptyOrNull(borradorCorrelativo) && ("1".equals(borradorCorrelativo))){
			    	if(ToolsHTML.isEmptyOrNull(numero)){
						//ERROR
					}else{
		                if (!ToolsHTML.isEmptyOrNull(forma.getDocPublic())) {
		                }
					}

			    }else{
			    	if(ToolsHTML.isEmptyOrNull(numero)){
						//Se calcula correlativo
					}
			    }

                String numCorrelativo = null;
                String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
                if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
                    numByLocation = numByLocation.trim();
                }
                //busca para ver si se repite este numero creado por el usuario, si se repite te manda un null en respuesta.
                if (!ToolsHTML.isEmptyOrNull(forma.getNumber())) {
                     //numCorrelativo = EditDocumentAction.chkNumCorrelativo(forma.getIdNode(),forma.getNumber(),numByLocation,tree,forma);
                     numCorrelativo = EditDocumentAction.chkNumCorrelativo(forma.getIdNode(),forma.getNumber(),numByLocation,tree);
                    //esta repetido el numero si es nullo numCorrelativo
                    if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
                        //El N�mero debe ser �nico para todo el Sistema
                        if ("0".equalsIgnoreCase(numByLocation)) {
                             forma.setNumCorrelativoExiste(forma.getNumber());
                        } else  if (("1".equalsIgnoreCase(numByLocation)) ||(("2".equalsIgnoreCase(numByLocation)))) {
                             forma.setNumCorrelativoExiste(forma.getPrefix()+forma.getNumber());
                        }
                     }
                }


			    /******* FIN VALIDACIONES *************/

				/***** SE INSERTA EL DOCUMENTO INVOCANDO EL MISMO METODO DEL MANEJADOR ****/

				//Valores necesitados en HandlerStruct.upDocument()
                if(publicado==null)
                	forma.setDateApproved(null);
                else
                	forma.setDateApproved(publicado);

                forma.setOwner(propietario);
				forma.setComments(comentarios);
				forma.setMayorVer(verMayor);
				forma.setMinorVer(verMenor);
				forma.setNameFile(nombreFisico);
				forma.setDateCreation(creado);

				//En el metodo updateDocument() se calcula NumberGen y	NumVer

				//Valores necesitados en HandlerStruct.getPreparedStatementDocumentInsert()
				if(status!=null && status.equals("B"))
					forma.setDocPublic("1");
				else
					forma.setDocPublic("0");
				//forma.setPrefix("");  		//Ya se coloc� el valor
				forma.setNormISO(norma);
				//forma.setDocProtected("");
				//forma.setDocOnline("");
				forma.setURL(url);
				forma.setKeys(palabrasClaves);
				forma.setDescript(descripcion);
				forma.setNameDocument(nombre);
				forma.setNumber(numero);
				forma.setTypeDocument(tipoDocumento);
				if(status!=null && status.equals("A")){
					forma.setDatePublic(dateSystem);
				}
				//forma.setIdNode(""); 			//Ya se coloc� el valor
				if(status!=null && status.equals("A"))
					forma.setApproved("1");
				else
					forma.setApproved(null);
				//Tambien se necesita comments, owner, nameFile pero ya fueron seteados


				//Valores necesitados en HandlerStruct.getPreparementStatementVersionInsert()
				if(esPublico.equals("0") && status!=null && status.equals("A"))
					forma.setDateExpires(vencimiento);

				forma.setDateExpiresDrafts(null);

				if(vencimiento!=null && status!=null && status.equals("A"))
					forma.setExpires("0");
				//Tambien se necesita dateApproved, mayorVer, minorVer pero ya fueron seteados


				//Valores necesitado en EditDocumentAction.actualizarActiveFactory()
				//Inactivo activeFcatory
				if(status!=null && status.equals("A"))
					forma.setCheckactiveFactory("0");
                //forma.setDescripcionActiveFactory("");
				//forma.setIdDocument("");
				//Tambien se necesita descript, nameDocument, prefix, number,nameFile pero ya fueron seteados

				if(docsRelacionados!=null)
					forma.setDocRelations(docsRelacionados.toString());

				//HandlerStruct.upDocument(forma, path, request, false);
				try{
					procesaFicheros(forma, path);
				}catch(Exception e){
					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) +  "| Error al tratar de subir documento: " + e);
					mal.flush();
				}

				/****** FIN *******/

				celdaControl.setCellValue("OK");
				ok.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero);
				log.println(format.format(new Date()) + "Resultado: OK");

				log.flush();
				ok.flush();
				mal.flush();
			} catch (Exception errGen) {
				mal.println(format.format(new Date()) + "|" + (numeroFilaActual + 1) + "|" + numero + "| Error no controlado" + errGen.getMessage());
				mal.flush();
				errGen.printStackTrace();
			}
		}

		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(nombreArchivo + ".resultado");
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String buscarNumGenRelacionado (String prefijoNumero) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer();

		switch(Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("select numGen from documents where isnull(prefix,'')+CAST(isnull(number,'') as varchar) = '");
			sql.append(prefijoNumero).append("' ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("select numGen from documents where (SELECT CASE WHEN prefix is NULL THEN '' ELSE prefix END)||(SELECT CASE WHEN number is NULL THEN '' ELSE number END) = '");
			sql.append(prefijoNumero).append("' ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("select numGen from documents where IF(prefix is NULL,'',prefix) || IF(number is NULL,'',number) = '");
			sql.append(prefijoNumero).append("' ");
			break;
		}
		String numDoc = "";
		try{
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				numDoc = rs.getString("numGen");
			}
		}catch(Exception e){
			//System.out.println("Error en MigracionExcel.buscarNumGenRelacionado: " + e);
			return numDoc;
		} finally {
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return numDoc;
	}

	private String obtenerIdTipoDocumento(String tipo) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer("SELECT idTypeDoc FROM typedocuments WHERE TypeDoc ='");
		sql.append(tipo).append("'");
		String tipoDoc = "";
		try{
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				tipoDoc = rs.getString("idTypeDoc");
			}
		}catch(Exception e){
			//System.out.println("Error en MigracionExcel.obtenerIdTipoDocumento: " + e);
			return tipoDoc;
		} finally {
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return tipoDoc;
	}

	// TODO Verificar funcionamiento de Nomas ISO
	private String obtenerIdNormaIso(String normaIso) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer("SELECT idNorm FROM norms WHERE (titleNorm LIKE ");
		sql.append(" '").append(normaIso).append("' ) OR (Description LIKE ");
		sql.append(" '").append(normaIso).append("') " );
		String idNorm = "";
		try{
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				idNorm = rs.getInt("idNorm")+"";
			}
		}catch(Exception e){
			//System.out.println("Error en MigracionExcel.obtenerIdNormaIso: " + e);
			return idNorm;
		} finally {
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return idNorm;
	}

	private int buscarIdNode(int idNodoPadre, String nombre) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer("SELECT IdNode FROM struct WHERE (Name = ");
		sql.append(" '").append(nombre).append("' ) AND (IdNodeParent = ");
		sql.append(" '").append(idNodoPadre).append("') " );
		int idNodo = 0;
		try{
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				idNodo =Integer.parseInt(rs.getString("IdNode").trim());
			}
		}catch(Exception e){
			//System.out.println("Error en MigracionExcel.buscarIdNode: " + e);
			return idNodo;
		} finally {
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return idNodo;
	}

	public synchronized boolean procesaFicheros(BaseDocumentForm forma, String path) {
		boolean resp = false;
		try {
			if (forma != null) {
				// CARGA DE ESTRUCTURA
				// Para llenar el tree, colocamos el usuario como administrador,el grupo como administrador, la seguridad nula para que carque toda la estructura
				Hashtable subNodos = new Hashtable();
				Hashtable tree = HandlerStruct.loadAllNodes(null,DesigeConf.getProperty("application.admon"),DesigeConf.getProperty("application.admon"), subNodos, null);
				//String idNodeDocument = forma.getIdNode();
				//String idLocation = HandlerStruct.getIdLocationToNode(tree, idNodeDocument);
				// BaseStructForm location = HandlerStruct.loadStruct(idLocation);
				//BaseStructForm location = (BaseStructForm) tree.get(idLocation);
				// Fin de la Carga de la Estructura

				String numCorrelativo = null;
				// en caso que sea publico, validamos el num correlativo
				// ----o------------//
				String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc()); 
				
				// if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
				// numByLocation = numByLocation.trim();
				// }
				// num correlativo existe es una variable que trae el ;refijo,
				// luego mmas adelante se le agrega el numero
				String iqualPrefijo = forma.getNumCorrelativoExiste();
				// o en caso que sea borrador y las propiedades de la localidad se
				// le exige colocar numero a un borrador, este entonces lo trae
				// en la variable forma.getNumber()
				if ((!ToolsHTML.isEmptyOrNull(forma.getNumber())) || (forma.getDocPublic().equalsIgnoreCase("0"))) {
					forma.setNumCorrelativoExiste(null);
					if (!ToolsHTML.isEmptyOrNull(forma.getNumber())) {
						// obtenemos el prefijo
						String prefix = forma.getPrefix();
						// busca para ver si se repite este numero creado por el usuario, si se repite te manda un null en respuesta.
						// numCorrelativo = EditDocumentAction.numCorrelativo(forma.getIdNode(),forma.getIdLocation(),forma.getNumber(),forma);
						numCorrelativo = EditDocumentAction.chkNumCorrelativo(forma.getIdNode(), forma.getNumber(), numByLocation, tree);
						if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
							// el numero introducido por el usuario se repite y
							// se le manda un mensaje que este num ya esta en
							// uso
							// El N�mero debe ser �nico para todo el Sistema
							if ("0".equalsIgnoreCase(numByLocation)) {
								forma.setNumCorrelativoExiste(forma.getNumber());
							} else {
								forma.setNumCorrelativoExiste(prefix + forma.getNumber());
							}
						}
					}
				}
				// Ingresamos el TypeNumByLocation, de tal manera que en
				// administracion ya quede predefinido el tipo de
				// numcorrelativo y este inhabiliatdo
				// aqui se ve si ya escojieron el tipo de numero correlativo
				
				if (Constants.typeNumByLocationVacio.compareTo(String.valueOf(HandlerParameters.PARAMETROS.getTypeNumByLocation())) == 0
						|| (ToolsHTML.isEmptyOrNull(numByLocation))) {
					HandlerParameters.actualizaParametro(Constants.typeNumByLocationLLeno);
				}
				// pasa si el usuario coloco un numero que no este repetido o no
				// haya colocado un numero correlativo
				boolean swmismoprefijo = false;
				if (!ToolsHTML.isEmptyOrNull(forma.getNumCorrelativoExiste())) {
					swmismoprefijo = forma.getNumCorrelativoExiste().equalsIgnoreCase(iqualPrefijo);
				}

				if ((swmismoprefijo) || ToolsHTML.isEmptyOrNull(forma.getNumCorrelativoExiste())) {
					// Se consigue el num correlativo en caso que el usuario no
					// haya colocado un numero correlativo
					// y en caso que sea publico
					if ((ToolsHTML.isEmptyOrNull(numCorrelativo)) && (forma.getDocPublic().equalsIgnoreCase("0"))) {
						numCorrelativo = EditDocumentAction.numCorrelativo(forma.getIdNode(), forma.getIdLocation(), numByLocation, HandlerParameters.PARAMETROS.getResetDocNumber(), tree);
					}
					if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
						forma.setNumber("");
					} else {
						forma.setNumber(numCorrelativo);
					}

					File file = new File(path + File.separator + forma.getNameFile());

					MimetypesFileTypeMap mime1 = new MimetypesFileTypeMap();
					//FileDataSource fileData = new FileDataSource(file);

					String extensionDoc = ShowDocument.getExtensionFile(forma.getNameFile());
					if(extensionDoc!=null){
						extensionDoc=extensionDoc.toUpperCase();
						if(extensionDoc.equals(".DOC") || extensionDoc.equals(".DOCX")){
							forma.setContextType("application/msword");
						}else if(extensionDoc.equals(".XLS") || extensionDoc.equals("XLT") || extensionDoc.equals("XLSX")){
							forma.setContextType("application/msexcel");
						}else if(extensionDoc.equals(".PPT") || extensionDoc.equals(".PPTX") || extensionDoc.equals(".PPSX")){
							forma.setContextType("application/vnd.ms-powerpoint");
						}else if(extensionDoc.equals(".VSD")){
							forma.setContextType("application/vnd.ms-visio.viewer");
						}else if(extensionDoc.equals(".PDF")){
							forma.setContextType("application/pdf");
						}else if(extensionDoc.equals(".RTF")){
							forma.setContextType("text/rtf");
						}else if(extensionDoc.equals(".CDR")){
							forma.setContextType("application/coreldraw");
						}else{
							forma.setContextType(mime1.getContentType(file));
							//forma.setContextType(fileData.getContentType());
						}
					}else{
						forma.setContextType(mime1.getContentType(file));
					}
					////System.out.println("///////// extensionDoc: " + extensionDoc);
					////System.out.println("///////// forma.getContextType(): " +  forma.getContextType());
					try{
						resp = HandlerStruct.upDocument(forma, path, null, true, forma.getDateCreation(), false, "admin");
						//Si no dio error y el documento es publico se modifica fecha de publicacion
						//Se obtiene ultimo documento activo que se haya ingresado
						//Si el nombre coincide, se modifica fecha de publicacion
						if(forma!=null && forma.getDatePublic()!=null){
							BaseDocumentForm lastDocument = getLastDocument();
							if(lastDocument!=null && lastDocument.getNameDocument()!=null && lastDocument.getNameDocument().equals(forma.getNameDocument())
									&& forma.getNameFile().equals(lastDocument.getNameFile())){
								updateDatePublic(lastDocument.getNumberGen(), forma.getDatePublic());
							}
						}
						
					}catch(Exception e){
						//System.out.println("--- error al tratar de subir el archivo = " + e.getMessage());
					}

				} else {
					errorProceso = "N�mero correlativo existe";
					resp = false;
				}
			}
		} catch (Exception e) {
			//System.out.println("e.getMessage() = " + e.getMessage());
			e.printStackTrace();
			errorProceso = e.getMessage();
			resp = false;
		}
		return resp;
	}
	
	public static synchronized BaseDocumentForm getLastDocument() throws Exception {
		StringBuffer sql = new StringBuffer("SELECT * from documents where active = '1' and numGen = (select max(numGen) from documents) ");
		BaseDocumentForm forma = new BaseDocumentForm();
		try{
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				forma.setNumberGen(prop.getProperty("numGen"));
				forma.setNameDocument(prop.getProperty("nameDocument"));
				forma.setNameFile(prop.getProperty("nameFile"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return forma;
	}	

	public static void updateDatePublic(String numGen, String datePublic) throws Exception {
		try{
			if(ToolsHTML.isNumeric(numGen) && !ToolsHTML.isEmptyOrNull(datePublic) && datePublic.length()>9 ){
				PreparedStatement st = null;
				Connection con = null;
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				StringBuffer sql = new StringBuffer("");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("Update documents set datePublic=CONVERT(datetime,'").append(datePublic).append("',120) where numGen = ").append(numGen);
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("Update documents set datePublic=CAST('").append(datePublic).append("' AS timestamp) where numGen = ").append(numGen);
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("Update documents set datePublic=TIMESTAMP('").append(datePublic).append("') where numGen = ").append(numGen);
					break;
				}
				////System.out.println("update : " + sql.toString());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				st.executeUpdate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}	

}
