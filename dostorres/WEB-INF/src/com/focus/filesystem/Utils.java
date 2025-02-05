package com.focus.filesystem;

import java.io.*;

/**
 * Clase que incluye varios metodos hechos para practicar el manejo de las 
 * diferentes funciones de lectura de Java
 * 
 * @author Magus
 * @version 1.0
 */
public abstract class Utils {
    /**
     * Metodo que encripta el archivo pasado como parametro en in al archivo 
     * pasado como parametro en out utilizando el password pasado como 
     * parametro.
     *
     * @param in   El archivo que se va a encriptar
     * @param out  El archivo al que se va a encriptar
     * @param pass El password que vamos a utilizar para encriptar
     * @throws IOException            Si el archivo no se puede leer
     */
    public static void encrypt(File in, File out, int pass) throws IOException {
        
        DataInputStream  fileIn  = null;
        DataOutputStream fileOut = null;
        
        /**
         * Como voy a leer de un archivo binario archivo tengo que utilizar un 
         * try a pesar de que aviento IOException y FileNotFoundException
         */
        try {
            //Creo los objetos con los que voy a leer
            fileIn = new DataInputStream( new FileInputStream(in) );
            fileOut = new DataOutputStream( new FileOutputStream(out) );
            
            //Utilizo un loop infinito para leer todo el archivo
            while (true) {
                //escribo a fileOut
                fileOut.write(fileIn.readByte() ^ pass);
            }
        } catch (EOFException eof) {
            fileIn.close();
            fileOut.close();
        }
    }

    /**
     * Metodo que desencripta el archivo pasado como parametro en in al archivo 
     * pasado como parametro en out utilizando el password pasado como 
     * parametro y regresa un string con el contenido .
     *
     * @param in   El archivo que se va a desencriptar
     * @param pass El password que vamos a utilizar para desencriptar
     * @return  Un string con el archivo desencriptado
     * @throws IOException            Si el archivo no se puede leer
     */
    public static String readEncrypted(File in, int pass) throws IOException {

        String linea = "";
        String texto = "";
        BufferedReader fileIn = null;
        File temp = new File("temp");
        //File temp = new File("c:\\temp\\temp");
        //System.out.println(temp.getAbsolutePath());
        
        /** Mando a llamar el metodo para desencriptar */
        encrypt(in, temp, pass);
        
        /**
         * Abro el archivo desencriptado como un archivo de texto y voy 
         * concatenando las lineas, una vez que tengo todo el archivo lo cierro 
         * y devuelvo el texto.
         */
        fileIn = new BufferedReader(new FileReader(temp));
        
        while ((linea = fileIn.readLine()) != null)
            texto += linea + "\n";
        
        fileIn.close();
        /** Como solo era un archivo temporal lo borro */
        temp.delete();
        
        return texto;
    }
}
