package com.desige.webDocuments.document.officceDocuments;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.desige.webDocuments.document.servlet.SuperFrame;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DataDocument extends SuperFrame implements ActionListener {//extends JFrame implements ActionListener {
    JTabbedPane Contenedor = new JTabbedPane();
    JButton btnCancel = new JButton();
    JButton btnOk = new JButton();
    private String country;
    private String languaje;
    public static String action_Cancel = "Cancelar";
    JPanel dataBasic = new JPanel();
    JPanel dataComplement = new JPanel();
    private Locale local;
    private ResourceBundle rb;
    JFileChooser FileSelect = new JFileChooser();
    JButton btnSelect = new JButton();

    public DataDocument() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DataDocument(Locale local) {
        try {
            if (local==null){
                this.local = new Locale("en","US");
            } else {
                this.local = local;
            }
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void setStatuObject(JComponent obj,boolean statu){
        obj.setEnabled(statu);
    }

    public static void main(String[] args) {
        Locale local = new Locale("en","US");
        DataDocument dataDocument = new DataDocument(local);
    }

    private void createDataBasic(){
        dataBasic.setLayout(null);
        JLabel lblNumber = new JLabel();
        lblNumber.setToolTipText("");
        lblNumber.setText(rb.getString("app.lblNumber"));
        lblNumber.setBounds(new Rectangle(15, 17, 49, 14));

        JTextField txtNumber = new JTextField();
        txtNumber.setBounds(new Rectangle(106, 17, 90, 18));
        setStatuObject(txtNumber,false);

        JLabel lblName = new JLabel();
        lblName.setText(rb.getString("app.lblName"));
        lblName.setBounds(new Rectangle(15, 44, 62, 13));

        JTextField txtName = new JTextField();
        txtName.setBounds(new Rectangle(106, 41, 332, 18));

        JLabel lblKeys = new JLabel();
        lblKeys.setText(rb.getString("app.lblName"));
        lblKeys.setBounds(new Rectangle(15, 71, 62, 13));

        dataBasic.add(txtNumber, null);
        dataBasic.add(txtName, null);
        dataBasic.add(lblName, null);
        dataBasic.add(lblNumber, null);
        dataBasic.add(lblKeys, null);

    }

    void btnSelect_actionPerformed(ActionEvent e) {
        FileFilterClass ff = new FileFilterClass();
        ff.addExtension("doc");
        ff.addExtension("xls");
        ff.addExtension("ppt");
        ff.addExtension("mdb");
        ff.setDescription("Documentos");
        FileSelect.setFileFilter(ff);
        int returnVal = FileSelect.showDialog(new JFileChooser(),"Subir Archivo");
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            //System.out.println("PATH: "+FileSelect.getSelectedFile().getPath());
            //System.out.println("You chose to open this file: " + FileSelect.getSelectedFile().getName());
            try {
                File file = new File(FileSelect.getSelectedFile().getPath());
                //System.out.println("file.toURL() = " + file.toURL());
//                file.toURL();
                Object data = sendFile("http://localhost:7001/WebDocuments/servletloaddocuments","?cmd=upFile",FileSelect.getSelectedFile().getPath());
                              //sendRequeriment("multipart/form-data",
                              //                "http://localhost:7001/WebDocuments/servletloaddocuments","?cmd=upFile&namefile="+FileSelect.getSelectedFile().getPath()
                              //                ,file.toURL());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
         }
   }

    private void createFileLoad(){
        FileSelect.setBounds(new Rectangle(44, 50, 33, 24));
        btnSelect.setBounds(new Rectangle(24, 17, 81, 23));
        btnSelect.setToolTipText("");
        btnSelect.setActionCommand("btnSelect");
        btnSelect.setText("Abrir");
        btnSelect.addActionListener(new DataDocument_btnSelect_actionAdapter(this));
        dataComplement.add(btnSelect, null);
        dataComplement.add(FileSelect, null);
    }

    private void jbInit() throws Exception {
        rb = ResourceBundle.getBundle("LoginBundle",this.local);
        this.getContentPane().setLayout(null);
        btnCancel.setText(rb.getString("btn.cancel"));
        btnCancel.addActionListener(this);
        btnCancel.setBounds(new Rectangle(255, 347, 85, 20));
        btnCancel.setActionCommand(DataDocument.action_Cancel);
        setStatuObject(btnCancel,true);

        btnOk.setBounds(new Rectangle(160, 347, 85, 20));
        btnOk.setText(rb.getString("btn.ok"));
        setStatuObject(btnOk,false);
//        this.addWindowListener(new DataDocument_this_windowAdapter(this));
        Contenedor.setBounds(new Rectangle(11, 9, 468, 317));
        dataComplement.setLayout(null);

        createDataBasic();
        createFileLoad();
        this.getContentPane().add(Contenedor, null);
        Contenedor.add(dataBasic,  "Datos Básicos");
        Contenedor.add(dataComplement,  "Datos Complementarios");

        this.getContentPane().add(btnCancel, null);
        this.getContentPane().add(btnOk, null);
        this.setSize(new Dimension(500, 400));
        this.setLocation(200,100);
        this.setTitle(rb.getString("app.loadTitle"));
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String evento = e.getActionCommand().trim();
        //System.out.println("Llego "+evento);
        if (evento.equalsIgnoreCase(action_Cancel)){
            closeWindow();
        }
    }

    void this_windowClosed(WindowEvent e) {
        //System.out.println("Fuente: "+e.getSource());
        //System.out.println("Cerrando Ventana");
        System.exit(0);
    }

    void closeWindow(){
        //System.out.println("Cerrando Ventana");
        System.exit(0);
    }
}

class DataDocument_btnSelect_actionAdapter implements java.awt.event.ActionListener {
    DataDocument adaptee;

    DataDocument_btnSelect_actionAdapter(DataDocument adaptee) {
        this.adaptee = adaptee;
    }
    public void windowClosed(WindowEvent e) {
        adaptee.this_windowClosed(e);
    }
    public void actionPerformed(ActionEvent e) {
        adaptee.btnSelect_actionPerformed(e);
    }
}
