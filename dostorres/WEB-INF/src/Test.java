import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class Test {
	
	
	
	public static void main4(String[] args) {
		Date fecha = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		//System.out.println(sdf.format(fecha));

		char[] COMPANY = {42,70,48,99,117,115};

		String cad = "*F0cus";
		
		for(int i = 0; i < COMPANY.length; i++){
			//System.out.println((char)COMPANY[i]);
		}
			
		
	}

	public static String updateURLVerDocumento(String coment, HttpServletRequest request, String path) {
		StringBuffer url = new StringBuffer();
		String clave = "<otnemucoD reV>";
		StringBuffer r = new StringBuffer();
		String cad;
		try {
			if (request != null) {
				url.append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
			} else {
				url.append(path);
			}

			r = new StringBuffer(coment);
			r.reverse();
			cad = r.toString().substring(r.toString().indexOf(clave) + clave.length());
			cad = cad.substring(0, cad.indexOf("<"));

			r.setLength(0);
			r.append(cad);
			r = r.reverse();
			cad = r.toString();

			cad = cad.substring(cad.indexOf("http://") + 7);
			String[] palabras = cad.split("/");
			cad = palabras[0] + "/" + palabras[1];

			return coment.toString().replaceAll(cad, url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return coment;
	}

	public static void main3(String[] args) {
		StringBuffer coment = new StringBuffer();
		String path = "wks-ccs-41:8085/qwebdocafdadfxxxxxxx";

		coment.append("Tome en cuenta la fecha estipulada en&nbsp;este flujo&nbsp;para dar sus comentarios ");
		coment.append("con respecto al documento, para su aprobación. Comentarios: <P>prueba</P><br/>Nombre: ");
		coment.append("solicitud de materiales<br/>Versión: 0.1<br/>Código: 100-ME-2008-0009<br/>Requerida:  ");
		coment.append("Diaz Mireya<br/>Fecha de Creación: 07/03/2008 03:39:02 PM<br/> ");
		coment.append("<a href='http://qwebdocuments:8085/qwebdocuments/viewDocument.jsp?nameFile=4ReqMatyEqui2007.doc&idDocument=7376&idVersion=7542' target='_blank'>Ver Documento</a> ");

		//System.out.println(updateURLVerDocumento(coment.toString(), null, path));
	}

	public static void main2(String[] args) {
		// try {
		// String key="113-166-98-119-64-32-93-158";
		// //System.out.println(EncryptorMD5.getMD5(key+":FTP"));
		// //System.out.println(EncryptorMD5.getMD5(key+":SACOP"));
		// //System.out.println("1".compareToIgnoreCase("2"));
		// //System.out.println("2".compareToIgnoreCase("2"));
		// //System.out.println("3".compareToIgnoreCase("2"));

		// TreeMap sort = new TreeMap();
		// sort.put("2.1","Dos");
		// sort.put("3.1","Tres");
		// sort.put("1.1","Uno");
		// sort.put("4.1","Cuatro");
		// sort.put("3.0","Unouno");
		//			
		//
		// for ( Iterator it = sort.keySet().iterator(); it.hasNext();) {
		// //System.out.println(sort.get(it.next()));
		// }
		// } catch (NoSuchAlgorithmException e) {
		// e.printStackTrace();
		// }
		//System.out.println(System.getProperty("java.io.tmpdir"));
	}
	
	public static void main10(String[] args) {
		java.io.File file = new java.io.File("");
		file = new java.io.File(file.getAbsolutePath());
		java.io.File file2 = null;
		java.io.File[] lista = null;
		java.io.File[] lista2 = null;
		if(file.isDirectory()) {
			lista = file.listFiles();
			for(int i=0;i<lista.length;i++){
				//System.out.println(lista[i].getAbsolutePath());
				file2 = new java.io.File(lista[i].getAbsolutePath());
				if(file2.getName().endsWith("catalina.out")) {
					//System.out.println("------------------------------------");
				}
				if(file2.isDirectory()) {
					lista2 = file2.listFiles();
					for(int k=0;k<lista2.length;k++){
						//System.out.println(lista2[k].getAbsolutePath());
					}
				}
			}
		}
	}
	
	public static void main6(String[] args) {
		String name = "e:\\app\\Tomcat5\\conf\\web.xml";
		name="/usr/share/tomcat5/logs/catalina.out";
		java.io.File inputFile = new java.io.File(name.replace('/',File.separatorChar));
		// Creamos entradas y salidas por cónsola
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(inputFile);
			int c;
			while ((c = fis.read()) != -1) {
				System.out.print((char) c);
			}
			fis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main9(String[] args) {
		//File f = new File("//ubuntu-sun/opt/data_qweb/014/22416770b8de377a2209b3dfb232e7c2");
		//f = new File("//ubuntu-sun/opt/data_qweb/014/xxx3453254234vaefra345eertvtr");
		////System.out.println(isPDF(f.getAbsolutePath()));
		
		//String cad = "4.5.1";
		////System.out.println(cad.replaceAll("\\.", ""));
		
		//System.out.println(System.getProperty("user.dir"));
		String cad = "asdf&%afAD JHKH@#!+__)_dsaf, la casa";
		//System.out.println(cad.replaceAll("[^a-zA-Z]", ""));
		
	}
	
	public static boolean isPDF(String nameFile) {
		StringBuffer data = new StringBuffer();
		try {
			File inputFile = new File(nameFile.replace('/', File.separatorChar));
			FileInputStream fis = new FileInputStream(inputFile);
			int c;
			int cont = 0;
			while ((c = fis.read()) != -1) {
				data.append((char) c);
				if(cont++>2)
					break;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toString().toUpperCase().indexOf("%PDF")!=-1;
	}
	
	public static void mainxx(String[] args) {
		String n = "1.23456789E8";
		
		BigDecimal a = new BigDecimal(n);
		
		System.out.println(String.valueOf(a)); 
	}
	
	 public static long hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        long val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            long d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
	 
	 public static void main(String[] args) {
		String s = "21C1E221D123B51F"; //38919582538037680000
		
		//2026094390618575358
		                         //38919582538037678590
		//long x = Long.parseLong("38919582538037680000");
		//System.out.println(x);
		
		System.out.println(hex2decimal(s));
		System.out.println(Long.parseLong(s,16));
		
        BigInteger val = new BigInteger(s,16);
        System.out.print(val.toString(10));

		
	}
}
