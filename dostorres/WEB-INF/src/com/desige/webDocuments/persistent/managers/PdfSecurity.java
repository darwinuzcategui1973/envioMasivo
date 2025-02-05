package com.desige.webDocuments.persistent.managers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.util.Archivo;
import com.focus.util.Wait;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class PdfSecurity {

	public static final String BG_PREVIEW = "preview.gif";
	public static final String BG_ERASER = "borrador.gif";
	public static final String BG_NO_CONTROLADA = "nocontrolada.gif";

	private final static int INPUT_FILE = 0;
	private final static int OUTPUT_FILE = 1;
	private final static int USER_PASSWORD = 2;
	private final static int OWNER_PASSWORD = 3;
	private final static int PERMISSIONS = 4;
	private final static int STRENGTH = 5;
	private final static int MOREINFO = 6;
	private final static int permit[] = { PdfWriter.AllowPrinting, PdfWriter.AllowModifyContents, PdfWriter.AllowCopy, PdfWriter.AllowModifyAnnotations, PdfWriter.AllowFillIn, PdfWriter.AllowScreenReaders, PdfWriter.AllowAssembly, PdfWriter.AllowDegradedPrinting };

	private final char[] OWNER_PW = { 'q', 'w', 'e', 'b', '0', '1', '2', '3', '4', '5' };
	private String nameFilePdf = null;
	private boolean Printing = false;
	private boolean DegradedPrinting = false;
	private boolean ModifyContents = false;
	private boolean Assembly = false;
	private boolean CopyContents = false;
	private boolean ScreenReaders = false;
	private boolean ModifyAnnotations = false;
	private boolean FillIn = false;
	private boolean AllFeatures = false;
	
	private boolean controlada = false;

	private ArrayList<String> fileBefore = new ArrayList<String>();
	private ArrayList<String> fileAfter = new ArrayList<String>();

	private String background = null;
	
	private int idUser = 0;

	public PdfSecurity() {
		this.nameFilePdf = "";
		this.setBackground("");
	}

	public PdfSecurity(String nameFilePdf) {
		this.nameFilePdf = nameFilePdf;
	}

	public String quitarExtension(String name) {
		while (name != null && !name.trim().equals("") && !name.endsWith(".")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	public void applySecurity() {
		applySecurity(null,false,0);
	}

	public void applySecurity(BaseDocumentForm forma,boolean isExpediente,int idExpediente) {
		if(true) {
			return ;
		}
		try {
			boolean correcto = false;

			String ruta = nameFilePdf;
			String rutaTemp = nameFilePdf.concat("_");

			File fichero = new File(ruta);
			File ficheroTemp = null;

			if (fichero.exists()) {

				// ejecutamos la rutina que da seguridad al pdf
				StringBuffer sb = new StringBuffer();
				StringBuffer allow = new StringBuffer();

				// String pdf = quitarExtension(ruta).concat(".pdf");

				// si hay que sumar pdfs lo hacemos primero
				ArrayList<String> lista = new ArrayList<String>();
				lista.addAll(getFileBefore());
				lista.add(ruta);
				concat(lista, rutaTemp);

				ficheroTemp = new File(rutaTemp);

				//System.out.println("Fue creado el archivo = " + rutaTemp + " : Cierto =" + ficheroTemp.exists());

				fichero = new File(ruta);
				if (Archivo.delete(fichero) || !fichero.exists()) {
					//System.out.println("Borramos el original");
				} else {
					//System.out.println("No se pudo borrar el archivo original");
				}

				// borramos los archivos que concatenamos si los hay
				try {
					if (getFileBefore().size() > 0) {
						File borrar;
						for (int i = 0; i < getFileBefore().size(); i++) {
							borrar = new File(String.valueOf(getFileBefore().get(i)));
							if (borrar.exists()) {
								Archivo.delete(borrar);
							}
						}
					}
					if (getFileAfter().size() > 0) {
						File borrar;
						for (int i = 0; i < getFileAfter().size(); i++) {
							borrar = new File(String.valueOf(getFileAfter().get(i)));
							if (borrar.exists()) {
								Archivo.delete(borrar);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				//System.out.println("ficheroTemp =" + String.valueOf(ficheroTemp.exists()) + " " + rutaTemp);
				//System.out.println("fichero =" + String.valueOf(fichero.exists()));
				if (PdfSecurity.renameTo(ficheroTemp, fichero)) {
					//System.out.println("Renombramos el temporal para original");
				} else {
					//System.out.println("No se puede renombrar el temporal para original");
				}

				// aplicamos la seguridad al documento
				/*
				 * if (background != null && !background.trim().equals("")) {
				 * File fondo = new File(getBackground()); if (fondo.exists()) {
				 * sb.append("background \"");
				 * sb.append(getBackground().replace('/', File.separatorChar));
				 * sb.append("\""); } }
				 */

				// aqui otorgamos los permisos
				allow.setLength(0);
				allow.append("0");  //allow.append(isPrinting() ? "1" : "0"); // los documentos no se podran imprimir por esta via
				allow.append(isModifyContents() ? "1" : "0");
				allow.append(isCopyContents() ? "1" : "0");
				allow.append(isModifyAnnotations() ? "1" : "0");
				allow.append(isFillIn() ? "1" : "0");
				allow.append(isScreenReaders() ? "1" : "0");
				allow.append(isAssembly() ? "1" : "0");
				//allow.append(isDegradedPrinting() ? "1" : "0");
				allow.append("0");
				if (isAllFeatures()) {
					allow.setLength(0);
					allow.append("11111111");
				}

				if(forma!=null && forma.getIdDocument()!=null && forma.getNumVer()>0 && isPrinting()) {
					// crearmos una copia imprimible del documento
					String namePrint = "";
					if(isExpediente) {
						namePrint = ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(getIdUser())).concat("_").concat(String.valueOf(idExpediente)).concat("_").concat(String.valueOf(forma.getNumVerFiles())).concat(".pdf");
					} else {
						namePrint = ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(getIdUser())).concat("_").concat(forma.getIdDocument()).concat("_").concat(String.valueOf(forma.getNumVer())).concat(".pdf");
					}
					File f = new File(namePrint);
					
					if(f.exists()) f.delete();
					
					if(!isControlada()) {
						setBackgroundImage(ruta, PdfSecurity.BG_NO_CONTROLADA, namePrint );
					} else {
						PdfSecurity.copiar(fichero,f);
					}
				}

				if(getBackground()!=null && !getBackground().trim().equals("")) {
					setBackgroundImage(ruta, getBackground() );
				}
				
				//jairo
				encriptar(ruta, rutaTemp, "", OWNER_PW.toString(), allow.toString(), "128", null);

				if (Archivo.delete(fichero)) { // borramos el archivo.
					//System.out.println("El borrado ha sido correcto");
				} else {
					//System.out.println("El borrado no se ha podido realizar");
				}

				// renombramos el archivo.
				// fichero = new File(ruta);
				ficheroTemp = new File(rutaTemp);

				//System.out.println("ficheroTemp =" + String.valueOf(ficheroTemp.exists()));
				//System.out.println("fichero =" + String.valueOf(fichero.exists()));
				if (PdfSecurity.renameTo(ficheroTemp, fichero)) {
					//System.out.println("El renombrado ha sido correcto");
				} else {
					//System.out.println("El renombrado no se ha podido realizar ficheroTemp=" + ficheroTemp.exists() + " - " + fichero.exists());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void drawImage(String image, String texto) {
		int width = 400;
		int height = 20;
		BufferedImage imagen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics g = imagen.getGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		g.setFont(new Font("Sans",Font.BOLD,12));
		
		g.drawString(texto,25, 10);  // 5 14
		g.dispose();		
		
		try {
			FileOutputStream sos;
			sos = new FileOutputStream(new File(image));
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(sos);
			encoder.encode(imagen);
			
			sos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// PdfSecurity pdf = new PdfSecurity();
		// pdf.setNameFilePdf("c:/pdf/chao.pdf");
		// pdf.setBackground("C:/tempoffice/marca.pdf");
		// pdf.setNameFilePdf("/home/ubuntu/appdownload/pdf/chao.pdf");
		// pdf.setBackground("/home/ubuntu/appdownload/pdf/marca.pdf");
		// pdf.setPrinting(true);
		// pdf.applySecurity();

		String pdfIn = "c:/temp/entrada.pdf";
		String pdfOut = "c:/temp/holaDestino.pdf";
		String img = "numero.jpg";
		String ruta = "c:/temp/";

		PdfSecurity.drawImage(ruta.concat(img), "PRO1-NUM-0017");
		setBackgroundImage(pdfIn, img,0,20,ruta,null);
		//System.out.println("fin programa");
		System.exit(0);
	}

	// renombrar un fichero
	public static boolean renameTo(File source, File destination) {
		if (!destination.exists()) {
			// intentamos con renameTo
			System.gc();
			boolean result = source.renameTo(destination);
			if (!result) {
				// intentamos copiar
				result = true;
				result &= copiar(source, destination);
				result &= source.delete();
			}
			return (result);
		} else {
			// Si el fichero destination existe, cancelamos...
			return (false);
		}
	}

	/**
	 * copia el fichero source en el fichero destination devuelve true si
	 * funciona correctamente
	 */
	public static boolean copiar(File source, File destination) {
		boolean resultado = false;
		// declaración del flujo
		java.io.FileInputStream sourceFile = null;
		java.io.FileOutputStream destinationFile = null;
		try {
			// creamos fichero
			destination.createNewFile();
			// abrimos flujo
			sourceFile = new java.io.FileInputStream(source);
			destinationFile = new java.io.FileOutputStream(destination);
			// lectura por segmentos de 0.5Mb
			byte buffer[] = new byte[512 * 1024];
			int nbLectura;
			while ((nbLectura = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLectura);
			}
			// copia correcta
			resultado = true;
		} catch (java.io.FileNotFoundException f) {
		} catch (java.io.IOException e) {
		} finally {
			// pase lo que pase, cerramos flujo
			try {
				sourceFile.close();
			} catch (Exception e) {
			}
			try {
				destinationFile.close();
			} catch (Exception e) {
			}
		}
		return (resultado);
	}

	public void concat(ArrayList filesInput, String output) {
		try {
			if (filesInput.size() < 1 || output == null) {
				throw new Exception("PdfSecurity: No hay archivos para concatenar");
			}
			if (filesInput.size() == 1 || output == null) {
				File source = new File((String)filesInput.get(0));
				File destination = new File(output);
				Archivo.renameTo(source, destination);
				return;
			}
			String[] files = new String[filesInput.size() + 1];
			for (int i = 0; i < filesInput.size(); i++) {
				files[i] = (String) filesInput.get(i);
			}
			files[files.length - 1] = output;

			int pageOffset = 0;
			ArrayList master = new ArrayList();
			int f = 0;
			String outFile = files[files.length - 1];
			Document document = null;
			PdfCopy writer = null;
			while (f < files.length - 1) {
				// we create a reader for a certain document
				Archivo.waitCreationFile(files[f], 20);
				PdfReader reader = null;
				for (int xx = 0; xx < 10; xx++) {
					try {
						reader = new PdfReader(files[f]);
						break;
					} catch (IOException ex) {
						ex.printStackTrace();
						if ((xx + 1) == 10)
							throw ex;
						//System.out.println("Intentamos nuevamente " + xx);
						Wait.oneSec();
					}
				}
				reader.consolidateNamedDestinations();
				// we retrieve the total number of pages
				int n = reader.getNumberOfPages();
				List bookmarks = SimpleBookmark.getBookmark(reader);
				if (bookmarks != null) {
					if (pageOffset != 0)
						SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
					master.addAll(bookmarks);
				}
				pageOffset += n;
				// //System.out.println("There are " + n + " pages in " +
				// files[f]);

				if (f == 0) {
					// step 1: creation of a document-object
					document = new Document(reader.getPageSizeWithRotation(1));
					// step 2: we create a writer that listens to the document
					writer = new PdfCopy(document, new FileOutputStream(outFile));
					// step 3: we open the document
					document.open();
				}
				// step 4: we add content
				PdfImportedPage page;
				for (int i = 0; i < n;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
					// //System.out.println("Processed page " + i);
				}
				f++;
			}
			if (master.size() > 0)
				writer.setOutlines(master);
			// step 5: we close the document
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void setBackgroundId(String nameFilePDF, String idDocument, String numero, String version) {
		// creamos la imagen con el codigo del documento
		StringBuffer cadena = new StringBuffer();
		
		String img = idDocument.concat(".jpg");
		
		if(numero!=null && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintNumber()))) {
			cadena.append("Cod.:").append(numero);
		}
		if(version!=null && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintVersion()))) {
			cadena.append(cadena.length()>0?" / ":"");
			cadena.append("Ver.:").append(version);
		}
		
		try {
			BaseDocumentForm forma = new BaseDocumentForm();
			forma.setIdDocument(idDocument);
	        forma.setNumberGen(idDocument);
			HandlerStruct.loadDocument(forma,true,false,null, null);
			
			if("0".equals(forma.getApproved()) && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintApprovedDate()))){
				ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
						new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default")));
				
				cadena.append(cadena.length()>0?" / ":"");
				cadena.append(rb.getString("showDoc.approvedAbv")).append(".:").append(forma.getDateApproved().substring(0, 
						forma.getDateApproved().indexOf(" ")));
				
				forma = null;
				rb = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		PdfSecurity.drawImage(ToolsHTML.getPathTmp().concat(img), cadena.toString());
		PdfSecurity.setBackgroundImage(nameFilePDF,img,0,7,ToolsHTML.getPathTmp(),null);
		File g = new File(ToolsHTML.getPathTmp().concat(img));
		g.delete();
		
	}

	public static void setBackgroundImage(String pdfIn, String fondo, String fileOut) {
		//setBackgroundImage(pdfIn, fondo, 100, 250, null, fileOut);
		setBackgroundImage(pdfIn, fondo, 0, 0, null, fileOut);
	} 

	public static void setBackgroundImage(String pdfIn, String fondo) {
		//setBackgroundImage(pdfIn, fondo, 100, 250, null, null);
		setBackgroundImage(pdfIn, fondo, 0, 0, null, null);
	}

	public static void setBackgroundImage(String pdfIn, String fondo, int x, int y, String rutaImg, String fileOut) {

		String pdfOut = null;
		if(fileOut==null) {
			if(pdfIn.endsWith(".pdf")) {
				pdfOut = pdfIn.replaceAll(".pdf","_.pdf");
			} else {
				pdfOut = pdfIn.replaceAll(".PDF","_.PDF");
			}
		} else {
			pdfOut = fileOut;
		}
		if(rutaImg==null) {
			fondo = ToolsHTML.getPathImg().concat(fondo);
		} else {
			fondo = rutaImg.concat(fondo);
		}
		
		Document document = new Document();

		PdfWriter writer = null;
		try {
			PdfReader pdfReader = null;
			try {
				pdfReader = new PdfReader(pdfIn);
			} catch(java.io.IOException ex) {
				File source = new File(ToolsHTML.getPathAdvertenciaPdf());
				File destination = new File(pdfIn);
				if(destination.exists()) destination.delete();
				Archivo.copiar(source, destination);
				pdfReader = new PdfReader(pdfIn);
			}
			int n = pdfReader.getNumberOfPages();
			//System.out.println("Pag:: " + n);
			// creae an outputstream
			FileOutputStream streamOut = new FileOutputStream(pdfOut);
			// give it to the pdfstamper
			PdfStamper pdfStamper = new PdfStamper(pdfReader, streamOut);
			// contentbyte is a static object, no constructor
			PdfContentByte under = new PdfContentByte(writer);
			// now create an instance with this file (report_watermark.jpg)
			Image img = Image.getInstance(fondo);
			// //System.out.println("Here 1 ::" + img);
			img.setAbsolutePosition(x,y); //100,250 por defecto //350
			// //System.out.println("Here 2");
			for (int i = 1; i <= n; i++) {
				//under = pdfStamper.getUnderContent(i);
				under = pdfStamper.getOverContent(i);
				under.addImage(img);
				////System.out.println("Colocando fondo de pagina " + i);
			}
			pdfStamper.close();

			File ori = new File(pdfIn);
			if(fileOut==null) {
				if(Archivo.delete(ori)) {
					//System.out.println("Borramos el original");
				} else {
					//System.out.println("Error: No Borramos el original");
				}
				/*
				if (ori.delete()) {
					//System.out.println("Borramos el original");
				} else {
					for(int i=0; i<10 && ori.exists(); i++) {
						Thread.currentThread().sleep(1000);
						//System.out.println("Intentando borrar el archivo "+i);

						BufferedReader reader = new BufferedReader(new FileReader(ori)); // java.io.File file
						reader.close();
						System.gc();
						if(ori.delete()) {
							//System.out.println("Archivo borrado");
						}
					}
					if (ori.exists()) {
						//System.out.println("No se pudo borrar el original");
					}
				}
				*/

				File des = new File(pdfOut);
				renameTo(des, ori);
			}

			//System.out.println("fin");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param fileInput
	 *            Archivo a encriptar
	 * @param fileOut
	 *            Archivo encriptado (el que se va a generar)
	 * @param passUser
	 *            clave del usuario
	 * @param passOwner
	 *            clave para el propietario
	 * @param permission
	 *            permisos formato "00000000" segun el siguiente orden
	 *            AllowPrinting 1 si 0 no AllowModifyContents 1 si 0 no
	 *            AllowCopy 1 si 0 no AllowModifyAnnotations 1 si 0 no
	 *            AllowFillIn (128 bit only) 1 si 0 no AllowScreenReaders (128
	 *            bit only) 1 si 0 no AllowAssembly (128 bit only) 1 si 0 no
	 *            AllowDegradedPrinting (128 bit only) 1 si 0 no
	 * 
	 * @param crifado
	 *            128 o 40 bits
	 * @throws Exception
	 */
	public void encriptar(String fileInput, String fileOut, String passUser, String passOwner, String permission, String cifrado, String more) throws Exception {
		// "c:/temp/flujo.pdf c:/temp/flujoprotegido2.pdf jairo jairo 00000000
		// 128".split(" ");
		// more debe contener [nombre valor] [nombre valor] [nombre valor]
		int permissions = 0;
		String p = permission;
		for (int k = 0; k < p.length(); ++k) {
			permissions |= (p.charAt(k) == '0' ? 0 : permit[k]);
		}
		//System.out.println("Reading " + fileInput);
		Archivo.waitCreationFile(fileInput, 10);
		PdfReader reader = new PdfReader(fileInput);
		//System.out.println("Writing " + fileOut);
		HashMap moreInfo = new HashMap();
		if (more != null) {
			String[] args = more.split(" ");
			for (int k = 0; k < args.length - 1; k += 2)
				moreInfo.put(args[k], args[k + 1]);
		}
		PdfEncryptor.encrypt(reader, new FileOutputStream(fileOut), passUser.getBytes(), passOwner.getBytes(), permissions, cifrado.equals("128"), moreInfo);
		//System.out.println("Done.");
	}

	// getters and setters

	public boolean isAllFeatures() {
		return AllFeatures;
	}

	public void setAllFeatures(boolean allFeatures) {
		AllFeatures = allFeatures;
	}

	public boolean isAssembly() {
		return Assembly;
	}

	public void setAssembly(boolean assembly) {
		Assembly = assembly;
	}

	public boolean isCopyContents() {
		return CopyContents;
	}

	public void setCopyContents(boolean copyContents) {
		CopyContents = copyContents;
	}

	public boolean isDegradedPrinting() {
		return DegradedPrinting;
	}

	public void setDegradedPrinting(boolean degradedPrinting) {
		DegradedPrinting = degradedPrinting;
	}

	public boolean isFillIn() {
		return FillIn;
	}

	public void setFillIn(boolean fillIn) {
		FillIn = fillIn;
	}

	public boolean isModifyAnnotations() {
		return ModifyAnnotations;
	}

	public void setModifyAnnotations(boolean modifyAnnotations) {
		ModifyAnnotations = modifyAnnotations;
	}

	public boolean isModifyContents() {
		return ModifyContents;
	}

	public void setModifyContents(boolean modifyContents) {
		ModifyContents = modifyContents;
	}

	public boolean isPrinting() {
		return Printing;
	}

	public void setPrinting(boolean printing) {
		Printing = printing;
	}

	public boolean isScreenReaders() {
		return ScreenReaders;
	}

	public void setScreenReaders(boolean screenReaders) {
		ScreenReaders = screenReaders;
	}

	public String getNameFilePdf() {
		return nameFilePdf;
	}

	public void setNameFilePdf(String nameFilePdf) {
		this.nameFilePdf = nameFilePdf;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public ArrayList getFileAfter() {
		return fileAfter;
	}

	public void setFileAfter(ArrayList fileAfter) {
		this.fileAfter = fileAfter;
	}

	public ArrayList getFileBefore() {
		return fileBefore;
	}

	public void setFileBefore(ArrayList fileBefore) {
		this.fileBefore = fileBefore;
	}

	public void addFileBefore(String nameFile) {
		fileBefore.add(nameFile);
	}

	public void addFileAfter(String nameFile) {
		fileAfter.add(nameFile);
	}

	public boolean isControlada() {
		return controlada;
	}

	public void setControlada(boolean controlada) {
		this.controlada = controlada;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	
}
