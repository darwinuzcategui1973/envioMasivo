package com.desige.webDocuments.utils;

import javax.xml.bind.DatatypeConverter;

import com.desige.webDocuments.util.Encryptor;

/**
 * Title: StringUtil.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 01/01/2005 (NC) Creation </li>
 * </ul>
 */
public class StringUtil {
    public static synchronized String encrypter(String source) {
        if (source!=null) {
            int len = source.length();
            StringBuffer salida = new StringBuffer(50);
            for (int index = 0; index < len ; index++) {
                int car = source.charAt(index);
                //System.out.println("car = " + car);
                int result = ((car * 4) / 126) + 159;
                salida.append((char)result);
            }
            return salida.toString();
        }
        return "";
    }

    /**
     * 
     * @param cad
     * @return
     */
    public static String cleanStarString(String cad) {
    	if (cad==null) {
    		return null;
    	}
    	cad=cad.replaceAll("^\n*","");
    	cad=cad.replaceAll("^\r*","");
    	cad=cad.replaceAll("^\n*","");
    	while(cad.startsWith("<br>")) {
        	cad=cad.replaceAll("^<br>*","");
    	}
    	cad=cad.replaceAll("^\n*","");
    	cad=cad.replaceAll("^\r*","");
    	cad=cad.replaceAll("^\n*","");
    	return cad.trim();
    }
    
    /**
     * 
     * @param nameFile
     * @return
     */
	public static String getOnlyNameFile(String nameFile) {
		if (nameFile==null || nameFile.trim().equals(""))
			return nameFile;
		
		char back = (char)92;
		char slash = '/';
		
		StringBuffer name = new StringBuffer(nameFile);
		nameFile = name.reverse().toString();
		
		if(nameFile.indexOf(String.valueOf(back))!=-1) {
			nameFile = nameFile.substring(0,nameFile.indexOf(String.valueOf(back)));
		} 
		if(nameFile.indexOf(String.valueOf(slash))!=-1) {
			nameFile = nameFile.substring(0,nameFile.indexOf(String.valueOf(slash)));
		}
			
		name.setLength(0);
		name.append(nameFile);
		return name.reverse().toString();
		
	}

    /**
     * 
     * @param nameFile
     * @return
     */
	public static String getOnlyNameFileWithoutExt(String nameFile) {
		if (nameFile==null || nameFile.trim().equals(""))
			return nameFile;
		
		char back = (char)92;
		char slash = '/';
		
		StringBuffer name = new StringBuffer(nameFile);
		nameFile = name.reverse().toString();
		
		if(nameFile.indexOf(String.valueOf(back))!=-1) {
			nameFile = nameFile.substring(0,nameFile.indexOf(String.valueOf(back)));
		} 
		if(nameFile.indexOf(String.valueOf(slash))!=-1) {
			nameFile = nameFile.substring(0,nameFile.indexOf(String.valueOf(slash)));
		}
			
		name.setLength(0);
		name.append(nameFile);
		String newName = name.substring(name.indexOf(".")+1);
		name.setLength(0);
		name.append(newName);
		return name.reverse().toString();
		
	}

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	public static String zero(String cad,int largo) {
		if(largo>60){
			return cad;
		}
		cad = "000000000000000000000000000000000000000000000000000000000000".concat(cad);
		return cad.substring(cad.length()-largo);
	}
	

	/**
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        Encryptor enc = new Encryptor();
        String result =  ToolsHTML.encripterPass("Desige S.C.");

    }
    
    /**
     * 
     * @param cadena
     * @param palabras
     * @return una nueva cadena con las ocurrencias remplazadas, las cadenas a reemplazar
     * comienzan con la letra P mayuscula (Parametro)
     */
	public static String replace(String cadena, String[] palabras) {
		for(int x=0; x<palabras.length;x++){
			cadena = cadena.replaceAll("P".concat(String.valueOf(x+1)), palabras[x]);
		}
		return cadena;
	}

	public static String cleanTextContent(String text) 
	{
	  text = text.replaceAll("[^!-~¡-þ ]", "");
	 
	  text = text.replaceAll("'", "");

	  return text.trim();
	}
	
	public static String serialize(String cadena) {
		if(cadena!=null) {
			return DatatypeConverter.printBase64Binary(cadena.getBytes());
		} else {
			return "";
		}
	}
	
	public static String deserialize(String cadena) {
		if(cadena!=null) {
			return new String(DatatypeConverter.parseBase64Binary(cadena));
		} else {
			return "";
		}
	}
	
	
}
