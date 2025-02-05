package com.desige.webDocuments.persistent.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.List;

import ooo.connector.BootstrapSocketConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.SectionTable;
import org.apache.poi.hwpf.usermodel.SectionProperties;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.util.Archivo;
import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.uno.UnoRuntime;

public class PdfConvert {
	private static final Logger log = LoggerFactory.getLogger(PdfConvert.class);
	
	// Activa el servicio de openoffice
	//C:\Program Files\OpenOffice.org 2.3\program\soffice.exe -nologo -nodefault -norestore -nocrashreport -nolockcheck -accept=pipe,name=uno4595603765916300889;urp;
	//tomcat5 //TS//Tomcat5 --Classpath="%JAVA_HOME%\lib\tools.jar";"%CATALINA_HOME%\bin\bootstrap.jar";"%OOOLIBPATH%\classes\juh.jar";"%OOOLIBPATH%\classes\\unoil.jar" o "%OOOLIBPATH%\classes\\unoloader.jar"
	
	// en openoffice 3.0 la ruta de las librerias es C:\Archivos de programa\OpenOffice.org 3\URE\java

	/*
	 * 
	 * definir la variable classpath en windows separador ; y en linux separador :
	 *
	 *	root@ubuntu-sun:/opt/tomcat/bin# echo $CLASSPATH
	 *	/opt/openoffice.org2.3/program/classes/juh.jar:
	 *  /opt/openoffice.org2.3/program/classes/unoil.jar:
	 *  /opt/openoffice.org2.3/program/classes/ridl.jar:
	 *  /opt/openoffice.org2.3/program/classes/jurt.jar
	 *
	 */
	

	public static com.sun.star.frame.XComponentLoader xCompLoader = null; 

	// ------------------------ Constants
	private static final String DOCTYPE_EXT_OUT = "pdf";

	private static final int OO_WRITER = 0;
	private static final int OO_CALC = 1;
	private static final int OO_IMPRESS = 2;
	private static final int OO_DRAW = 3;
	private static final int OO_MATH = 4;
	private static final int OO_CHART = 5;

	private static final String OO_WRITER_EXT = "odt";
	private static final String OO_CALC_EXT = "ods";
	private static final String OO_IMPRESS_EXT = "odp";
	private static final String OO_DRAW_EXT = "odg";

	private static final String DOCTYPE_SCALC = "com.sun.star.sheet.SpreadsheetDocument";
	private static final String DOCTYPE_SWRITER = "com.sun.star.text.TextDocument";
	private static final String DOCTYPE_SDRAW = "com.sun.star.drawing.DrawingDocument";
	private static final String DOCTYPE_SMATH = "com.sun.star.formula.FormulaProperties";
	private static final String DOCTYPE_SIMPRESS = "com.sun.star.presentation.PresentationDocument";
	private static final String DOCTYPE_SCHART = "com.sun.star.chart.ChartDocument";

	private String nameFile = "";
	private String pathFile = "";

	public PdfConvert() {

	}

	public String toUrlFile(String file) {
		return "file:///".concat(file).replace('\\', '/');

	}

	public com.sun.star.beans.PropertyValue[] MakePropertyValue(String[] cName, Object[] uValue) {
		com.sun.star.beans.PropertyValue[] oPropertyValue = new com.sun.star.beans.PropertyValue[cName.length];
		// //System.out.println("...");
		for (int i = 0; i < cName.length; i++) {
			oPropertyValue[i] = new com.sun.star.beans.PropertyValue();
			if (cName[i] != null)
				// //System.out.println(cName[i]);
				oPropertyValue[i].Name = cName[i];
			if (uValue[i] != null)
				// //System.out.println(uValue[i]);
				oPropertyValue[i].Value = uValue[i];
		}

		return oPropertyValue;
	}

	public XComponent openFile(String sourceFile) throws Exception {
		// get the remote office component context
		//System.out.println("Voy a conectarme a office 1...");

		String oooExeFolder="";
		com.sun.star.uno.XComponentContext xContext=null;

		//String oooExeFolder = "C:/Archivos de programa/OpenOffice.org 2.3/program/";
		//oooExeFolder=DesigeConf.getProperty("oooExeFolder");
		oooExeFolder = HandlerParameters.PARAMETROS.getOooExeFolder();
		//System.out.println("Ruta office ..."+oooExeFolder);
		
		if (oooExeFolder==null || oooExeFolder.trim().equals("")) {
			//System.out.println("No esta definidad la variable oooExeFolder en webdocuments.properties. Voy por el metodo viejo");
			//System.out.println("Se debe colocar la ruta del ejecutable de office.bin en el CLASSPATH en setclasspath.sh");
			// forma antigua (puede generar el error "no office executable found!" BootstrapException
			xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
		} else {
			//System.out.println("Voy por el metodo nuevo");
			if (!oooExeFolder.endsWith("/")) {
				oooExeFolder = oooExeFolder.concat("/");
			}
			// si se queda un rato en esta linea y no se mueve
			// luego da error com.sun.star.comp.helper.BootstrapException en esta linea
			// Recordar correrlo en el servidor para aceptar el acuerdo de licencia del openoffice
			xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
		}
		

		// com.sun.star.uno.XComponentContext xContext =
		// com.sun.star.comp.helper.Bootstrap.bootstrap();
		//System.out.println("Connected to a running office ...");

		// get the remote office service manager
		com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();
		Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

		xCompLoader = (com.sun.star.frame.XComponentLoader) UnoRuntime.queryInterface(com.sun.star.frame.XComponentLoader.class, oDesktop);

		//System.out.println(sourceFile);
		//System.out.println(toUrlFile(sourceFile));
		
		// Si esto genera un error del tipo "URL seems to be an unsupported one."
		// puede que el archivo este corrupto, baja el archivo y abrelo en su visor nativo.
		return xCompLoader.loadComponentFromURL(toUrlFile(sourceFile), "_blank", 0, MakePropertyValue(new String[] { "Hidden" }, new Object[] { new Boolean(true) }));
		
		//String path2 = com.sun.star.uri.ExternalUriReferenceTranslator.create(xContext).translateToInternal(sourceFile);
		////System.out.println(path2);
		//if (path2.length() == 0 && path.length() != 0) {
			//throw new RuntimeException();
	  	//}
		//return xCompLoader.loadComponentFromURL(sourceFile, "_blank", 0, MakePropertyValue(new String[] { "Hidden" }, new Object[] { new Boolean(true) }));
	}

	public String quitarExtension(String name) {
		while (name != null && !name.trim().equals("") && !name.endsWith(".")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	public synchronized String saveToPdf(String sourceFile) throws Exception {
		return saveToPdf(sourceFile,0, false);
	}
	public synchronized String saveToPdf(String sourceFile, int idVersion, boolean isDobleVersion) throws Exception {
		return saveToPdf(null, sourceFile,idVersion, isDobleVersion);
	}
	
	private boolean isLandscape(String nombreArchivo) {
		// EL sistema de archivo de poi de donde se va a cargar el libro de excel
		POIFSFileSystem fs = null;
		HSSFWorkbook wb = null;
		try {
			fs = new POIFSFileSystem(new FileInputStream(nombreArchivo));

			// Cargo el libro de excel.
			wb = new HSSFWorkbook(fs);
	
			// Obtengo la Hoja de Excel.
			HSSFSheet hoja = wb.getSheetAt(0);
			
			return hoja.getPrintSetup().getLandscape(); // pregunta si es horizontal (landscape)
			
		} catch (Exception e) {
			//System.out.println("El archivo no existe");
			return false;
		} finally {
			fs = null;
			wb = null;
		}
	}
	
	public synchronized String saveToPdf(Connection con, String sourceFile, int idVersion, boolean isDobleVersion) throws Exception {
		
		String nameFile = null;
		XComponent doc = null;
		StringBuffer pageSize = new StringBuffer("0");
		String sourceFileExt = "";
		//String nameFileCache = ToolsHTML.getPathCache().concat("datos").concat(String.valueOf(idVersion)).concat(".dat");
		String nameFileCache = Archivo.getNameFileEncripted(con, "versiondocview", idVersion, null);
		
		sourceFileExt = sourceFile.substring(quitarExtension(sourceFile).length());
		nameFile = quitarExtension(sourceFile).concat(DOCTYPE_EXT_OUT); 
		setPathFile(nameFile);
		setNameFile(ToolsHTML.getNameFile(nameFile));
		String fileUrl = toUrlFile(nameFile);

		boolean viewpdftool = String.valueOf(HandlerParameters.PARAMETROS.getViewpdftool()).equals("1");


		File cache = new File(nameFileCache);
		
		boolean existeCache = cache.exists();
		
		// verificamos si hay que generar nuevamente el cache
		if("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getDisabledCache())) && !isDobleVersion) {
			cache.delete();
			existeCache = false;
		}
		
		// si el archivo es un pdf borramos su cache ya que el original es el visualidor
		if(sourceFile.toLowerCase().endsWith(".pdf") && existeCache && !isDobleVersion) {
			cache.delete();
			existeCache = false;
		}

		if(!existeCache) {
			return sourceFile;
		}
		
		// A partir de la version 4.5.2 se elimina la conversion de pdf en el servidor
		// ahora se hara local
/*		
		if(!sourceFile.toLowerCase().endsWith(".pdf") && (idVersion==0 || !existeCache)) {
			try {
	
				// XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, doc);
				// XFrame xFrame = xModel.getCurrentController().getFrame();
				// xFrame.activate();
	
				
				// si escogio docprint o el archivo es de officce 2007
				//if(!viewpdftool || sourceFile.toLowerCase().endsWith("x")) { // conversion con doc2pdf 
				if(viewpdftool==false) { // conversion con doc2pdf
					Runtime r = Runtime.getRuntime();
					Process p = null;
					
					boolean isLandscape = false; 
					// si es un archivo excel preguntamos la orientacion
					if(sourceFile!=null && sourceFile.toLowerCase().endsWith(".xls")) {
						isLandscape = isLandscape(sourceFile);
					}
					
					if(sourceFile!=null && sourceFile.toLowerCase().endsWith(".doc")) {
						// ahora buscaremos el tamaño del documento para
						// un archivo de word 2003
						
						File file = new File(sourceFile);

						POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
						HWPFDocument documento = new HWPFDocument(fs);

						SEPX section;
						
				        SectionTable sectab = documento.getSectionTable();
				        ArrayList sepx =  sectab.getSections();

				        try {
					        for(int x=0; x<sepx.size(); x++) {
					            section = (SEPX)sepx.get(x);
					            SectionProperties secprop = section.getSectionProperties();
					            double factor = 1440;
					            double width = secprop.getXaPage()/factor;
					            double height = secprop.getYaPage()/factor;
					            
					            width = Math.round(width*100.0);
					            height = Math.round(height*100.0);
	
					            pageSize.setLength(0);
					            pageSize.append(width/100.0);
					            pageSize.append("x");
					            pageSize.append(height/100.0);
					            pageSize.append("in");

					            isLandscape = (width>height);
					            System.out.println(pageSize);
					            break;
					        }
				        } catch(Exception e) {
				        	pageSize.setLength(0);
				        	pageSize.append("0");
				        	e.printStackTrace();
				        }
				        
					}
					
					
					StringBuffer cmd = new StringBuffer(ToolsHTML.getPathDocPrint()).append("docPrintQWeb.exe \"");
					cmd.append(sourceFile).append("\" \"").append(nameFile).append("\" ").append(isLandscape?"H":"V");
					cmd.append(" ").append(pageSize);
					
					log.info("Ejecutando comando de conversion " + cmd);
					//System.out.println(cmd);
					p = r.exec(cmd.toString());  // ejecutamos el comando
					
					// Se obtiene el stream de salida del programa
					InputStream is = p.getInputStream();
					// Se prepara un bufferedReader para poder leer la salida más comodamente.
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					// Se lee la primera linea
					String aux = br.readLine();
					// Mientras se haya leido alguna linea
					while (aux != null) {
						// Se escribe la linea en pantalla
						log.info("Result of docPrintQWeb.exe: " + aux);
						// y se lee la siguiente.
						aux = br.readLine();
					}
					//if (!System.getProperty("os.name").startsWith("Windows")) {
						//r.exec("rm -f ".concat(nameFile));  // borramos el archivo
					//}
				} else {
					doc = openFile(sourceFile); // openFile just loads source File as XComponent
					
					XStorable xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, doc);
					//System.out.println(fileUrl);
					PropertyValue[] propertyValue = MakePropertyValue(new String[] { "FilterName", "CompressionMode", "Overwrite" , "Hidden"}, new Object[] { detectFilterName(getDocumentType(sourceFileExt), DOCTYPE_EXT_OUT), "1", new Boolean(true), new Boolean(false) });
					//System.out.println(detectFilterName(getDocumentType(sourceFileExt), DOCTYPE_EXT_OUT));
					xStorable.storeToURL(fileUrl, propertyValue);
				}
	
			} catch (ErrorCodeIOException exc) {
				throw new ErrorCodeIOException("Si estas utilizando openoffice presenta problemas de conversion, se debe bajar el servicio manualmente");
			} catch (Exception exc) {
				exc.printStackTrace();
				throw exc;
			} finally {
				if (doc != null) {
					doc.dispose();
				}
			}
			
			
			log.info("Iniciando espera por creacion de archivo  '" + nameFile + "', con docPrint");
			Archivo.waitCreationFile(nameFile, 10);
			log.info("Finalizada espera por creacion de archivo  '" + nameFile + "', con docPrint");
			
			
			// copiamos el archivo pdf generado en el cache para que no se siga generando
			File temporal = new File(nameFile);
			if(temporal.exists()) {
				InputStream in = new FileInputStream(temporal);
				Archivo.writeDocumentToDisk("versiondocview", idVersion, in);
			}
			
			
		}
		
*/		
		
		
		// si el archivo existe en cache lo copiamos a la carpeta

		if(existeCache) {
			//Archivo.copiar(nameFileCache,nameFile);
			log.info("Este archivo se tomo de cache en la ruta, '"
					+ nameFileCache + "', omitiendo asi el uso de docPrint para la conversion");
			File temporal = new File(nameFile);
			InputStream in = Archivo.readDocumentFromDisk(con, "versiondocview", idVersion);
			Archivo.copyfile(in, temporal);
		}
		
		// antes de borrar el archivo crearemos una copia para no convertirlo nuevamente
		//if(idVersion>0 && !(new File(nameFileCache).exists())) {
		//	Archivo.copiar(nameFile,nameFileCache); 
		//}
		
		
		// borramos el archivo que acabamos de convertir a pdf, si el origen no es un pdf
		if(sourceFile!=null && !sourceFile.toLowerCase().endsWith(".pdf")) {
			File borrar = new File(sourceFile);
			if (borrar.exists()) { 
				if (Archivo.delete(borrar)) {
					//System.out.println("Borramos el archivo que convertimos a pdf ".concat(sourceFile));
				} else {
					//System.out.println("No se pudo borrar el archivo que convertimos a pdf ".concat(sourceFile));
				}
			}
		}

		return nameFile;
	}

	public String detectFilterName(int pintFromType, String pstrToExtension) {
		String detectFilterName = "writer8";

		if (pstrToExtension != null && pstrToExtension.length() > 0) {
			detectFilterName = getPDFFilter(pintFromType);
		}
		return detectFilterName;
	}

	public boolean equalsAnyOf(String[] pobjTokens, String pstrValue) {
		if (pstrValue != null && pstrValue.length() == 0)
			return false;
		for (int i = 0; i < pobjTokens.length; i++) {
			if (pstrValue.equals(pobjTokens[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param pstrExtention
	 *            File extension.
	 * @return Returns the documentType.
	 */
	public int getDocumentType(String pstrExtention) {
		int getDocumentType = OO_WRITER;

		if (equalsAnyOf("sxw,stw,sdw,odt,ott,oth,odm,doc".split(","), pstrExtention)) {
			getDocumentType = OO_WRITER;

		} else if (equalsAnyOf("sxc,stc,sdc,ods,ots,xls".split(","), pstrExtention)) {
			getDocumentType = OO_CALC;

		} else if (equalsAnyOf("sxi,sti,sdd,sdp,odp,otp,ppt".split(","), pstrExtention)) {
			getDocumentType = OO_IMPRESS;

		} else if (equalsAnyOf("odg".split(","), pstrExtention)) {
			getDocumentType = OO_DRAW;

		}

		return getDocumentType;
	}

	/**
	 * @param documentType
	 *            The openoffice doc type.
	 * @return Returns the correct pdf filter.
	 */
	public String getPDFFilter(int documentType) {
		String getPDFFilter = "writer_pdf_Export";

		switch (documentType) {
		case OO_CALC:
			getPDFFilter = "calc_pdf_Export";
			break;
		case OO_DRAW:
			getPDFFilter = "draw_pdf_Export";
			break;
		case OO_IMPRESS:
			getPDFFilter = "impress_pdf_Export";
			break;
		case OO_MATH:
			getPDFFilter = "math_pdf_Export";
			break;
		case OO_WRITER:
			getPDFFilter = "writer_pdf_Export";
			break;
		}

		return getPDFFilter;
	}



	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getPathFile() {
		return pathFile;
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}

	
	
	
	
	public synchronized String convert(String sourceFile, String destinyFile) throws Exception {
		XComponent doc = null;
		String sourceFileExt = "";
		sourceFileExt = sourceFile.substring(quitarExtension(sourceFile).length());
		StringBuffer pageSize = new StringBuffer("0");
		
		String fileUrl = toUrlFile(destinyFile);

		boolean viewpdftool = String.valueOf(HandlerParameters.PARAMETROS.getViewpdftool()).equals("1");

		try {
			if(viewpdftool==false) { // conversion con doc2pdf
				Runtime r = Runtime.getRuntime();
				Process p = null;
				
				boolean isLandscape = false; 
				// si es un archivo excel preguntamos la orientacion
				if(sourceFile!=null && sourceFile.toLowerCase().endsWith(".xls")) {
					isLandscape = isLandscape(sourceFile);
				}
				if(sourceFile!=null && sourceFile.toLowerCase().endsWith(".doc")) {
					// ahora buscaremos el tamaño del documento para
					// un archivo de word 2003
					
					File file = new File(sourceFile);

					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
					HWPFDocument documento = new HWPFDocument(fs);

					SEPX section;
					
			        SectionTable sectab = documento.getSectionTable();
			        List sepx =  sectab.getSections(); /* List por ArrayList para compatibilida de poi*/

			        try {
				        for(int x=0; x<sepx.size(); x++) {
				            section = (SEPX)sepx.get(x);
				            SectionProperties secprop = section.getSectionProperties();
				            double factor = 1440;
				            double width = secprop.getXaPage()/factor;
				            double height = secprop.getYaPage()/factor;
				            
				            width = Math.round(width*100.0);
				            height = Math.round(height*100.0);

				            pageSize.setLength(0);
				            pageSize.append(width/100.0);
				            pageSize.append("x");
				            pageSize.append(height/100.0);
				            pageSize.append("in");

				            System.out.println(pageSize);
				            break;
				        }
			        } catch(Exception e) {
			        	pageSize.setLength(0);
			        	pageSize.append("0");
			        	e.printStackTrace();
			        }
			        
				}
				
				/*
				StringBuffer cmd = new StringBuffer(ToolsHTML.getPathDocPrint()).append("docPrintQWeb.exe \"");
				cmd.append(sourceFile).append("\" \"").append(destinyFile).append("\" ").append(isLandscape?"H":"V");
				cmd.append(" ").append(pageSize);
				*/
				
				if(isLandscape) {
					String[] aux = pageSize.toString().split("x");
					pageSize.setLength(0);
					pageSize.append(aux[1].replaceAll("in","")).append("x").append(aux[0]).append("in");
				}
				
				// Se copia doc2pdf.exe a docPrintQWeb.exe y se hace la llamada pasandole la licencia
				// doc2pdf.exe" -* VL6IEN2GRW8EYN8P1D7F -f "215.9x279.4mm" -i D:test.doc -o D:out.pdf
				StringBuffer cmd = new StringBuffer(ToolsHTML.getPathDocPrint()).append("doc2pdf.exe -* VL6IEN2GRW8EYN8P1D7F -i \"");
				cmd.append(sourceFile).append("\" -o \"").append(nameFile).append("\" -f \"").append(pageSize).append("\"");
				
				
				System.out.println(cmd);
				p = r.exec(cmd.toString());  // ejecutamos el comando
				
				
				// Se obtiene el stream de salida del programa
				InputStream is = p.getInputStream();
				// Se prepara un bufferedReader para poder leer la salida más comodamente.
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				// Se lee la primera linea
				String aux = br.readLine();
				// Mientras se haya leido alguna linea
				while (aux != null) {
					// Se escribe la linea en pantalla
					//System.out.println(aux);
					// y se lee la siguiente.
					aux = br.readLine();
				}
				System.out.println("finalizado...");
			} else {
				doc = openFile(sourceFile); // openFile just loads source File as XComponent
				
				XStorable xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, doc);
				//System.out.println(fileUrl);
				PropertyValue[] propertyValue = MakePropertyValue(new String[] { "FilterName", "CompressionMode", "Overwrite" , "Hidden"}, new Object[] { detectFilterName(getDocumentType(sourceFileExt), DOCTYPE_EXT_OUT), "1", new Boolean(true), new Boolean(false) });
				//System.out.println(detectFilterName(getDocumentType(sourceFileExt), DOCTYPE_EXT_OUT));
				xStorable.storeToURL(fileUrl, propertyValue);
			}

		} catch (ErrorCodeIOException exc) {
			throw new ErrorCodeIOException("Si estas utilizando openoffice y presenta problemas de conversion, se debe bajar el servicio manualmente");
		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		} finally {
			if (doc != null) {
				doc.dispose();
			}
		}
			
		return nameFile;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//
		// set OOOLIBPATH=c:\programme\OpenOffice.org1.1.4\program\classes
		// set CLASSPATH=%OOOLIBPATH%\ unoil.jar
		// java -cp .;../lib/unoil.jar;../lib/ridl.jar;../lib/juh.jar;../lib/jurt.jar;"C:/Program
		// Files/OpenOffice.org 2.3/program/soffice.exe" PdfConvert
		PdfConvert c = new PdfConvert();
		//c.sxaveToPdf("C:/tempoffice/Pensum_Piloto.doc");
		// c.sxaveToPdf("C:/tempoffice/Formato de Hoja de Vida.xls");
		// c.sxaveToPdf("C:\\tempoffice\\mejoramiento_V4_qweb.doc");
		// c.sxaveToPdf("C:\\pdf\\inicio.html");

		System.exit(0);
	}

//	// metodo de prueba
//	private static void insertBasicFactories(XSet xset, XImplementationLoader ximplementationloader) throws Exception {
//		xset.insert(ximplementationloader.activate("com.sun.star.comp.loader.JavaLoader", null, null, null));
//		xset.insert(ximplementationloader.activate("com.sun.star.comp.urlresolver.UrlResolver", null, null, null));
//		xset.insert(ximplementationloader.activate("com.sun.star.comp.bridgefactory.BridgeFactory", null, null, null));
//		xset.insert(ximplementationloader.activate("com.sun.star.comp.connections.Connector", null, null, null));
//		xset.insert(ximplementationloader.activate("com.sun.star.comp.connections.Acceptor", null, null, null));
//	}
//
//	public static XComponentContext createInitialComponentContext(Hashtable hashtable) throws Exception {
//		XImplementationLoader ximplementationloader = (XImplementationLoader) UnoRuntime.queryInterface(com.sun.star.loader.XImplementationLoader.class, new JavaLoader());
//		XSingleComponentFactory xsinglecomponentfactory = (XSingleComponentFactory) UnoRuntime.queryInterface(com.sun.star.lang.XSingleComponentFactory.class, ximplementationloader.activate("com.sun.star.comp.servicemanager.ServiceManager", null, null, null));
//		XMultiComponentFactory xmulticomponentfactory = (XMultiComponentFactory) UnoRuntime.queryInterface(com.sun.star.lang.XMultiComponentFactory.class, xsinglecomponentfactory.createInstanceWithContext(null));
//		XInitialization xinitialization = (XInitialization) UnoRuntime.queryInterface(com.sun.star.lang.XInitialization.class, ximplementationloader);
//		Object aobj[] = { xmulticomponentfactory };
//		xinitialization.initialize(aobj);
//		if (hashtable == null)
//			hashtable = new Hashtable(1);
//		hashtable.put("/singletons/com.sun.star.lang.theServiceManager", new ComponentContextEntry(null, xmulticomponentfactory));
//		ComponentContext componentcontext = new ComponentContext(hashtable, null);
//		xinitialization = (XInitialization) UnoRuntime.queryInterface(com.sun.star.lang.XInitialization.class, xmulticomponentfactory);
//		aobj = (new Object[] { null, componentcontext });
//		xinitialization.initialize(aobj);
//		XSet xset = (XSet) UnoRuntime.queryInterface(com.sun.star.container.XSet.class, xmulticomponentfactory);
//		xset.insert(xsinglecomponentfactory);
//		insertBasicFactories(xset, ximplementationloader);
//		return componentcontext;
//	}
//
//	private static void pipe(InputStream inputstream, PrintStream printstream, String s) {
//		(new Thread(s) {
//
//			public void run() {
//				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
//				try {
//					do {
//						String s1 = bufferedreader.readLine();
//						if (s1 == null)
//							break;
//						//System.out.println(s1);
//					} while (true);
//				} catch (IOException ioexception) {
//					ioexception.printStackTrace(System.err);
//				}
//			}
//
//		}).start();
//	}
//
//	public static final XComponentContext bootstrap() throws BootstrapException {
//		XComponentContext xcomponentcontext = null;
//		try {
//			//System.out.println("1");
//			XComponentContext xcomponentcontext1 = createInitialComponentContext(null);
//			if (xcomponentcontext1 == null)
//				throw new BootstrapException("no local component context!");
//			String s = System.getProperty("os.name").startsWith("Windows") ? "soffice.exe" : "soffice";
//			File file = NativeLibraryLoader.getResource((com.sun.star.comp.helper.Bootstrap.class).getClassLoader(), s);
//			if (file == null)
//				throw new BootstrapException("no office executable found!");
//			//System.out.println("2");
//			String s1 = "uno" + Long.toString((new Random()).nextLong() & 0x7fffffffffffffffL);
//			String as[] = new String[7];
//			as[0] = file.getPath();
//			as[1] = "-nologo";
//			as[2] = "-nodefault";
//			as[3] = "-norestore";
//			as[4] = "-nocrashreport";
//			as[5] = "-nolockcheck";
//			as[6] = "-accept=pipe,name=" + s1 + ";urp;";
//			for (int i = 0; i < as.length; i++) {
//				//System.out.println(as[i]);
//			}
//			Process process = Runtime.getRuntime().exec(as);
//			pipe(process.getInputStream(), System.out, "CO> ");
//			pipe(process.getErrorStream(), System.err, "CE> ");
//			//System.out.println("3");
//			XMultiComponentFactory xmulticomponentfactory = xcomponentcontext1.getServiceManager();
//			if (xmulticomponentfactory == null)
//				throw new BootstrapException("no initial service manager!");
//			XUnoUrlResolver xunourlresolver = UnoUrlResolver.create(xcomponentcontext1);
//			String s2 = "uno:pipe,name=" + s1 + ";urp;StarOffice.ComponentContext";
//			int i = 0;
//			//System.out.println("4");
//			do
//				try {
//					//System.out.println("i=" + i + " - " + s2);
//					Object obj = xunourlresolver.resolve(s2);
//					//xcomponentcontext = OOConnect(s2).;
//					xcomponentcontext = (XComponentContext) UnoRuntime.queryInterface(com.sun.star.uno.XComponentContext.class, obj);
//
//					if (xcomponentcontext == null)
//						throw new BootstrapException("no component context!");
//					break;
//				} catch (NoConnectException noconnectexception) {
//					//System.out.println(noconnectexception.getMessage());
//					if (i == 600)
//						throw new BootstrapException(noconnectexception.toString());
//					Thread.currentThread();
//					Thread.sleep(500L);
//					i++;
//				}
//			while (true);
//			//System.out.println("5");
//		} catch (BootstrapException bootstrapexception) {
//			throw bootstrapexception;
//		} catch (RuntimeException runtimeexception) {
//			throw runtimeexception;
//		} catch (Exception exception) {
//			throw new BootstrapException(exception);
//		}
//		return xcomponentcontext;
//	}
//
//	// XMultiServiceFactory componentContext;
//	// try
//	// {
//	// numConnections++;
//	// componentContext = OOConnect();
//	// }
//	// catch (unoidl.com.sun.star.connection.NoConnectException e1)
//	// {
//	// if (numConnections > maxTries)
//	// throw new System.Exception("Could not connect to OpenOffice.org. Max tries reached.", e1);
//	//
//	// System.Threading.Thread.Sleep(250);
//	// componentContext = OOConnect();
//	// }
//	//
//	// ...
//
//	private static XMultiServiceFactory OOConnect(String conexString) throws Exception {
//
//		XComponentContext localContext = Bootstrap.defaultBootstrap_InitialComponentContext();
//
//		XMultiComponentFactory multiComponentFactory = (XMultiComponentFactory) localContext.getServiceManager();
//
//		XUnoUrlResolver resolver = (XUnoUrlResolver) multiComponentFactory.createInstanceWithContext("com.sun.star.bridge.UnoUrlResolver", localContext);
//
//		//conexString -> puede ser -> "uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager"
//		XMultiServiceFactory componentContext = (XMultiServiceFactory) resolver.resolve(conexString);
//
//		return componentContext;
//	}

}
