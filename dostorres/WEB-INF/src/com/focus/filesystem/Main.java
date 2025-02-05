package com.focus.filesystem;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.io.*;

/**
 * Main.java
 * Clase de practica para el manejo de archivos binarios en Java. Esta clase 
 * simula el comportamiento de un editor de texto sencillo similar a notepad, 
 * nos permite realizar operaciones basicas de edicion y nos permite guardar y 
 * leer desde un archivo de texto.
 * 
 * @author Magus
 * @version 1.0
 */
public class Main extends JFrame implements ActionListener, ClipboardOwner{
    
    /**
     * Aqui declaramos todas las variables de la interfaz gráfica así como las 
     * variables que vamos a utilizar en todo el proyecto
     */
    
    /** 
     * Este bloque es el bloque con el que declaramos los menus, se necesita un 
     * JMenuItem por cada opción que queramos agregar al menu y se necesita un 
     * JMenu por cada menu que queramos. Tambien necesitamos un JMenuBar que es 
     * el contenedor de todos los controles
     */
    private JMenuBar menuBar;
    private JMenu mnuArchivo;
        private JMenuItem mnuItemNuevo;
        private JMenuItem mnuItemAbrir;
        private JMenuItem mnuItemGuardar;
        private JMenuItem mnuItemGuardarComo;
    private JMenu mnuEdicion;
        private JMenuItem mnuItemCopiar;
        private JMenuItem mnuItemCortar;
        private JMenuItem mnuItemPegar;
    private JMenu mnuOperaciones;
        private JMenuItem mnuItemContarLetra;
        private JMenuItem mnuItemEncriptar;
        private JMenuItem mnuItemDesencriptar;
   
    /** 
     * Es el JTextArea que vamos a utilizar para desplegar el archivo o para 
     * editar el archivo al ejecutarlo
     */
    private JTextArea txtTexto;
    
    /** 
     * Es un objeto de tipo File donde vamos a guardar el archivo que está 
     * abierto en este momento
     */
    private File openFile;
    
    /**
     * Metodo que crea la interfaz del programa, la interfaz contiene un menu
     * y un TextArea que sirve para escribir los datos.
     */
    public Main() {
        
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        menuBar = new JMenuBar();
        
        /** 
         * Utilizamos el constructor de la clase JMenu para crear un nuevo menu 
         * llamado Archivo
         */
        mnuArchivo = new JMenu("Archivo"); 
        /**
         * setMnemonic es una funcion que nos permite elegir la letra de acceso 
         * rapido para hacer el programa mas sencillo de utilizar
         */
        mnuArchivo.setMnemonic(KeyEvent.VK_A);
        menuBar.add(mnuArchivo);        
        
        mnuItemNuevo = new JMenuItem("Nuevo");
        /** 
         * setAccelerator es un metodo que nos permite presionar una combinacion 
         * de teclas para acceder rapidamente a un comando del menu. Recibe un 
         * objeto de tipo KeyStroke que podemos obtener mediante el metodo 
         * KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.MASK)
         * Donde:
         *   X    es una letra cualquiera, por ejemplo VK_A es la combinacion 
         *        que utilizamos para que el acceso directo utilice la tecla 'A'
         * 
         *   MASK es la tecla extra que queremos que se presione, las teclas 
         *        validas son CTRL_MASK, ALT_MASK, SHIFT_MASK, META_MASK (para 
         *        Mac) y 0 (cuando no se presiona ninguna tecla). Esta ultima es 
         *        muy poco recomendable.
         */
        mnuItemNuevo.setAccelerator( 
                KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        mnuItemNuevo.setMnemonic(KeyEvent.VK_N);
        mnuItemNuevo.addActionListener(this);
        mnuArchivo.add(mnuItemNuevo);
        
        mnuItemAbrir = new JMenuItem("Abrir...");
        mnuItemAbrir.setAccelerator( 
                KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK) );
        mnuItemAbrir.setMnemonic(KeyEvent.VK_A);
        mnuItemAbrir.addActionListener(this);
        mnuArchivo.add(mnuItemAbrir);
        
        mnuItemGuardar = new JMenuItem("Guardar...");
        mnuItemGuardar.setAccelerator( 
                KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK) );
        mnuItemGuardar.setMnemonic(KeyEvent.VK_G);
        mnuItemGuardar.addActionListener(this);
        mnuArchivo.add(mnuItemGuardar);
        
        mnuItemGuardarComo = new JMenuItem("Guardar Como...");
        mnuItemGuardarComo.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0) );
        mnuItemGuardarComo.setMnemonic(KeyEvent.VK_C);
        mnuItemGuardarComo.addActionListener(this);
        mnuArchivo.add(mnuItemGuardarComo);
        
        mnuEdicion = new JMenu("Edicion");
        mnuEdicion.setMnemonic(KeyEvent.VK_E);
        menuBar.add(mnuEdicion);
        
        mnuItemCopiar = new JMenuItem("Copiar");
        mnuItemCopiar.setAccelerator( 
                KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK) );
        mnuItemCopiar.setMnemonic(KeyEvent.VK_C);
        mnuItemCopiar.addActionListener(this);
        mnuEdicion.add(mnuItemCopiar);
        
        mnuItemCortar = new JMenuItem("Cortar");
        mnuItemCortar.setAccelerator( 
                KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK) );
        mnuItemCortar.setMnemonic(KeyEvent.VK_T);
        mnuItemCortar.addActionListener(this);
        mnuEdicion.add(mnuItemCortar);
        
        mnuItemPegar = new JMenuItem("Pegar");
        mnuItemPegar.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK) );
        mnuItemPegar.setMnemonic(KeyEvent.VK_P);
        mnuItemPegar.addActionListener(this);
        mnuEdicion.add(mnuItemPegar);        
        
        mnuOperaciones = new JMenu("Operaciones");
        mnuEdicion.setMnemonic(KeyEvent.VK_O);
        menuBar.add(mnuOperaciones);
        
        mnuItemContarLetra = new JMenuItem("Contar letra...");
        mnuItemContarLetra.setMnemonic(KeyEvent.VK_C);
        mnuItemContarLetra.addActionListener(this);
        mnuOperaciones.add(mnuItemContarLetra);
        
        mnuItemEncriptar = new JMenuItem("Encriptar...");
        mnuItemEncriptar.setMnemonic(KeyEvent.VK_E);
        mnuItemEncriptar.addActionListener(this);
        mnuOperaciones.add(mnuItemEncriptar);
        
        mnuItemDesencriptar = new JMenuItem("Desencriptar...");
        mnuItemDesencriptar.setMnemonic(KeyEvent.VK_D);
        mnuItemDesencriptar.addActionListener(this);
        mnuOperaciones.add(mnuItemDesencriptar);
        
        this.setJMenuBar(menuBar);
        
        txtTexto = new JTextArea();
        /**
         * Las siguientes dos lineas nos permiten que el JTextArea se salte 
         * automaticamente de renglon cuando una palabra llega al borde
         */
        txtTexto.setLineWrap(true);
        txtTexto.setWrapStyleWord(true);
        /** Este constructor crea una barra de scroll para la caja de texto */
        JScrollPane scrollPane = new JScrollPane(txtTexto);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        setSize(500,500);
        setTitle("Editor de Texto");
        setVisible(true);
    }
    
    /**
     * Crea una nueva instancia del programa.
     *
     * @param args argumentos de la lina de comandos
     */
    public static void main(String[] args) {
        Main app = new Main();
    }
    
    /**
     * Metodo que copia datos al portapapeles.
     *
     * @param  str    El String que queremos copiar al portapapeles
     */
    private void setClipboard(String str) {
        /** 
         * Esta linea nos permite obtener el portapapeles predeterminado del 
         * sistema, es decir, nos permite ver que esta copiado en memoria.
         */
        Clipboard clip = getToolkit().getSystemClipboard();
        /**
         * En esta linea estamos creando un objeto de tipo StringSelection que 
         * es necesario para mandarlos datos al portapapeles
         */
        StringSelection fieldContent = new StringSelection (str);
        /**
         * Con el metodo setContents del objeto Clipboard podemos poner un dato 
         * en el portapapeles. El primer parametro que recibe es un objeto de 
         * tipo Transferable (que es el fieldContent que creamos) y el segundo 
         * es el dueño del portapapeles que a menos que la aplicacion utilice 
         * el portapapeles de alguna manera especial siempre va a ser la palabra 
         * clave this.
         */
        clip.setContents(fieldContent, this);
    }
    
    /**
     * Metodo que pega los datos al portapapeles. Utiliza un objeto Transferable
     * para poder leer desde el portapapeles y en caso de que el contenido de este
     * sea texto entonces regresa el texto que leyo.
     *
     * @return Regresa el texto que lee del portapapeles en caso de que pueda leer,
     *         nulo en caso contrario.
     */
    private String getClipboard() {
        
        /**
         * Esta linea es necesaria para poder leer del portapapeles, con ella 
         * creamos un objeto de tipo Transferable que nos permite obtener los 
         * datos guardados en memoria.
         */
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        
        /** Es necesario usar un try porque puede mandar un error en el que el 
         * dato guardado en el portapapeles no es un tipo soportado, por ejemplo 
         * podria guardar un archivo y en el programa solo podemos pegar texto.
         */
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String)t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e) {
        } catch (IOException e) {
        }
        return null;
    }
    
    /**
     * Metodo que nos permite abrir un archivo de texto y leerlo. El metodo nos
     * regresa el texto que leyo del archivo
     *
     * @param  file   El archivo a leer.
     * @return El texto que leyo del archivo.
     */
    String abrir(File file) {
        String linea = "";
        String texto = "";
        BufferedReader fileIn = null;
        try {
            fileIn = new BufferedReader(new FileReader(file));
            while ((linea = fileIn.readLine()) != null) {
                texto += linea + "\n";
            }
        } catch (IOException ioe) {
            String err = "Error De Lectura\n" + 
                         "Posiblemente el disco esta lleno o protegido contra lectura";
            /**
             * JOptionPane es un control que me permite mostrar una dialogo con 
             * informacion. El metodo que usamos a continuacion se llama 
             * showMessageDialog y nos muestra una ventana con un mensaje 
             * informativo. Puede recibir varios parametros aunque solamente los 
             * primeros dos son indispensables. El primero es el componente 
             * padre, para efectos de este proyecto siempre va a ser null, el 
             * segudno es el mensaje a desplegar, como podemos ver abajo puede 
             * ser una varaible o podemos poner el mensaje entre comillas, el 
             * tercer parametro es el titulo que queremos que aparezca en la 
             * ventana y el cuarto parametro es el tiop de mensaje que queremos 
             * que sea, los tipos de mensaje valido son: JOptionPane.ERROR_MESSAGE, 
             * JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE y 
             * JOptionPane.WARNING_MESSAGE;
             */
            JOptionPane.showMessageDialog(null, err, "Error De Lectura", JOptionPane.ERROR_MESSAGE);
            
            return("");
        } finally {
            try {
                if (fileIn != null) {
                    fileIn.close();
                }
            } catch (IOException ioe) {
            }
        }

        return texto;
    }
    
    /**
     * Metodo que nos permite guardar a un archivo de texto.
     *
     * @param  file    El archivo a escribir.
     * @param  texto   Texto a guardar en el archivo.
     */
    void guardar(File file, String texto) {
        
        try {
            PrintWriter fileOut = new PrintWriter(new FileWriter(file));
            fileOut.print(texto);
            fileOut.flush();
            fileOut.close();
        } catch (IOException ioe) {
            String err = "Error De Escritura\n" + 
                         "Posiblemente el disco esta lleno o protegido contra escritura";
            JOptionPane.showMessageDialog(null, err, "Error De Escritura", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metodo que maneja eventos. El programa responde a los eventos del menu, 
     * en caso de que se presione el boton nuevo despliega un aviso de que se perderan
     * los datos y prosigue a crear un arhcivo nuevo; en caso de que se elija 
     * la opcion abrir se abrira un dialogo para seleccionar el archivo a abrir 
     * y despues manda llamar el metodo para abrir; en caso de que la seleccion 
     * sea guardar y ya haya un archivo abierto, lo guarda, si no hay archivo 
     * abierto despliega un dialogo Guardar Como y guarda el archivo; si se elige
     * la opcion guardar como se abre el dialogo guardar como y se guarda el archivo.
     * Los metodos de edicion funcionan guardando y leyendo del portapapeles.
     *
     * @param e     Es el objeto que representa el evento
     */
    public void actionPerformed(ActionEvent e) {
        
        /**
         * Si el usuario hace click en "Nuevo" le pregunto si está seguro que 
         * quiere borrar el archivo. Si contesta que si entonces borro lo que 
         * estaba en la caja de texto.
         */
        if (e.getSource() == mnuItemNuevo) {
            int op;
            String msg = "Estas seguro de que quieres hacer un archivo " +
                         "nuevo (perderas todos los cambios)";
            
            op = JOptionPane.showConfirmDialog(
                    null, msg, "Nuevo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (op == JOptionPane.OK_OPTION) {
                txtTexto.setText("");
                openFile = null;
            }
            
        /**
         * Si el usuario hace click en "Abrir" le muestro una ventana para 
         * elegir el archivo a elegir y mando a llamar el metodo abrir que me 
         * devuelve un string y ese string lo pongo como el texto en la caja de 
         * texto.
         */
        } else if (e.getSource() == mnuItemAbrir) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ret = fc.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                txtTexto.setText(abrir(openFile));
            }
            
        /**
         * Si el usuario hace click en "Guardar" y no hay archivo abierto le 
         * muestro una ventana para elegir done quiere guardar el archivo y 
         * mando a llamar el metodo guardar que se encarga de escribir el texto 
         * a un archivo, si ya habia un archivo abierto nada mas mando llamar el 
         * metodo guardar con el archivo abierto.
         */
        } else if (e.getSource() == mnuItemGuardar) {
            if (openFile == null) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int ret = fc.showSaveDialog(this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    openFile = fc.getSelectedFile();
                }
            }
            guardar(openFile, txtTexto.getText());
        
        /**
         * Si el usuario hace click en "Guardar Como" le muestro una ventana 
         * para elegir done quiere guardar el archivo y mando a llamar el 
         * metodo guardar que se encarga de escribir el texto a un archivo, si 
         * ya habia un archivo abierto nada mas mando llamar el metodo guardar 
         * con el archivo abierto.
         */
        } else if (e.getSource() == mnuItemGuardarComo) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ret = fc.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                openFile  = fc.getSelectedFile();
                guardar(openFile, txtTexto.getText());
            }
        
        /**
         * Si el usuario hace click en "Copiar" copio el texto que este 
         * seleccionado al portapapeles mediante la funcion setClipboard
         */
        } else if (e.getSource() == mnuItemCopiar) {
            /**
             * setClipboard es una funcion que nos sirve para guardar cualquier 
             * tipo de dato en el portapeles, dependiendo de como la 
             * implementemos. 
             */
            setClipboard(txtTexto.getSelectedText());
        
        /**
         * Si el usuario hace click en "Cortar" copio el texto elegido al 
         * portapapeles y despues borro el texto elegido.
         */
        } else if (e.getSource() == mnuItemCortar) {
            setClipboard(txtTexto.getSelectedText()); 
            txtTexto.replaceSelection("");
        
        /**
         * Si el usuario hace click en "Pegar" pongo el texto que esta en el 
         * portapapeles en vez del texto que esta seleccionado
         */
        } else if (e.getSource() == mnuItemPegar) {
            txtTexto.replaceSelection(getClipboard());
            
        /**
         * Si el usuario hace click en "Contar Letra" le muestro un dialogo en 
         * el que puede elegir una letra y despues muestro otro dialogo con el 
         * numero de repeticiones de esa letra que se encuentran en el archivo 
         * abierto.
         */
        } else if (e.getSource() == mnuItemContarLetra) {
            try {
                if (openFile != null) {
                    /**
                     * showInputDialog es otro de los metodos de JOptionPane, 
                     * este nos permite mostrar un dialgo en el que pedimos al 
                     * usuario nos de cierta informacion, el metodo regresa un 
                     * valor de tipo String que podemos convertir en cualquier 
                     * otro tipo de variable.
                     */
                    char c = JOptionPane.showInputDialog(null, "Que letra quieres buscar?").charAt(0);
                    if (c != '\0')
                        JOptionPane.showMessageDialog(null, cuentaLetra(c, openFile));
                }
            } catch (IOException ioe) {
                String err = "Error De Lectura\n" + 
                             "Probablemente el disco esta lleno o protegido contra escritura";
                JOptionPane.showMessageDialog(null, err, "Error De Escritura", JOptionPane.ERROR_MESSAGE);
            }
        
        /**
         * Si el usuario hace click en "Encriptar" el programa muestra un 
         * dialogo de guardar archivo y pregunta al usuario por un password. El 
         * programa guarda el archivo abierto en la direccion seleccionada 
         * utilizando el password para encriptar.
         */
        } else if (e.getSource() == mnuItemEncriptar) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            int ret = fc.showSaveDialog(this);
            int pass = Integer.parseInt( 
                    JOptionPane.showInputDialog(null, "Pon un password para encriptar") );
            
            if (ret == JFileChooser.APPROVE_OPTION) {
                try { 
                    Utils.encrypt(openFile, fc.getSelectedFile(), pass);
                } catch (IOException ioe) {
                    String err = "Error De Lectura\n" + 
                                 "Probablemente el disco esta lleno o protegido contra escritura";
                    JOptionPane.showMessageDialog(null, err, "Error De Escritura", JOptionPane.ERROR_MESSAGE);
                }
            }
        /**
         * Si el usuario hace click en "Desencriptar" el programa muestra un 
         * dialogo de abrir archivo y despues pide un password al usuario. El 
         * programa abre el archivo especificado utilizando el password del 
         * usuario.
         */
        } else if (e.getSource() == mnuItemDesencriptar) {
            int pass = Integer.parseInt( 
                    JOptionPane.showInputDialog(null, "Pon un password para desencriptar") );
            
            try {
                txtTexto.setText(Utils.readEncrypted(openFile, pass));
            } catch (IOException ioe) {
                String err = "Error De Lectura\n" + 
                             "Probablemente el disco esta lleno o protegido contra escritura";
                JOptionPane.showMessageDialog(null, err, "Error De Escritura", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
    }

    /**
     * Metodo que devuelve el numero de repeticiones de la letra especificada 
     * en el archvio especificado y lo devuelve como un valor numerico. El 
     * metodo no distingue entre mayusculas y minusculas.
     *
     * @param letra  La letra de la que se buscan las repeticiones dentro del 
     *               archivo
     * @param file   El archivo en el que se va a buscar
     * @return El numero de veces que aparece la letra especificada en el archivo 
     * @throws IOException            Si el archivo no se puede leer
     */
    public static int cuentaLetra(char letra, File file) throws IOException {
        
        int suma = 0;
        /**
         * Convierto el caracter en mayusculas para que la comparacion no 
         * distinga entre mayusculas y minusculas.
         */
        letra = Character.toUpperCase(letra);        
        
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        int i = 0;
        while (i != -1) {
            /** 
             * El metodo lee el siguiente byte y lo convierte a un caracter.
             */
            i = fileIn.read();
            char c = (char) i;
            if (Character.toUpperCase(c) == letra) {
                suma++;
            }            
        }
        
        return suma;
    }
    
    public void lostOwnership(Clipboard c, Transferable t) {}
}
