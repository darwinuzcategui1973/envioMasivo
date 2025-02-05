package com.desige.webDocuments.persistent.managers;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;




/**
 * Title: Prueba <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */
public class Prueba {
    private int edad;

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public static void mainViejo(String[] args) {
        String name = "5. Registrar valores prefijados p.usuario p.listas salida.doc";
        int pos = 0;
        while (name.indexOf(".",pos + 1)>0) {
            pos = name.indexOf(".",pos + 1);
        }
        //System.out.println("POS" + pos);
        //System.out.println("name = " + name.substring(pos));
		/*ArrayList uno = new ArrayList();
        ArrayList dos = new ArrayList();
        ArrayList tres = new ArrayList();
		uno.add(new Integer(1));
		uno.add(new Integer(2));
        uno.add(new Integer(3));
        dos.add(new Integer(1));
		dos.add(new Integer(3));
        dos.add(new Integer(4));
        dos.add(new Integer(5));
        tres.addAll(uno);
        tres.retainAll(dos);
        dos.removeAll(tres);
        uno.addAll(dos);      */
	}
    
    public static void main(String[] arg) throws IOException {
//    	String cad = "\n\r<br><br><br><br><br><br><br><br><br><br><br><br>  <p><font color=#000099><strong>Este es el mensaje configurado para Documentos ya Expirados.</strong></font></p><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>";
//    	cad=cleanStarString(cad);
//    	//System.out.println(cad);
//    	
//    	Process p = Runtime.getRuntime().exec("cls");
    	
    	TreeMap<Integer,String> map = new TreeMap<Integer,String>();
    	//HashMap map = new HashMap<String, String>();
    	String[] s = {"jairo","rivero","jairo","JAIRO","carmen","josefa","jairo","jose","admin","camen","admin"};

    	for (int i = 0; i < s.length; i++) {
        //for (int i = s.length-1; i >= 0; i--) {
    		if(!map.containsValue(s[i])) {
    			//System.out.println(s[i]);
    			map.put(new Integer(i), s[i]); 
    		}
		}

    	for(Iterator ite = map.entrySet().iterator();ite.hasNext();) {
    		Object obj = (Object)ite.next();
    		//System.out.println(obj);
    		
    	}
    	
    	//HashSet o = new HashSet<String>();
//    	TreeSet o = new TreeSet<String>();
//    	o.add("uno");
//    	o.add("baba");
//    	o.add("zapato");
//    	o.add("marco");
//    	o.add("arbol");
//    	
//    	for (Iterator i = o.iterator(); i.hasNext() ; ) {
//    		//System.out.println(i.next());
//    	}
//    	//System.out.println(" ---- ");
//   		//System.out.println(o.contains("marco"));
//   		//System.out.println(o.contains("marCo"));
    	
    	
    }
    
    public static String cleanStarString(String cad) {
    	cad=cad.replaceAll("^\n*","");
    	cad=cad.replaceAll("^\r*","");
    	cad=cad.replaceAll("^\n*","");
    	while(cad.startsWith("<br>")) {
        	cad=cad.replaceAll("^<br>*","");
    	}
    	return cad.trim();
    }
    
}
