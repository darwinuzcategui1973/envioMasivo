package com.desige.webDocuments.document.officceDocuments;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.desige.webDocuments.document.servlet.SuperFrame;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class FrLogin extends SuperFrame{//extends JFrame {
    JTextField txtNameUser = new JTextField();
    JPasswordField txtPass = new JPasswordField();
    Locale defaultLocal;
    ResourceBundle rb;

    public FrLogin() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public FrLogin(Locale local) {
        try {
            this.defaultLocal = local;
            rb = ResourceBundle.getBundle("LoginBundle",local);
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Locale local = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
        FrLogin frLogin = new FrLogin(local);
    }

    private void showMessage(String mensaje,int typeMsg){
        JOptionPane.showMessageDialog(null, mensaje, "Error", typeMsg);//JOptionPane.ERROR_MESSAGE);
    }

//    private Object sendRequeriment(String dirServer,String service,Object data) throws Exception{
//        URL url = new URL(dirServer+service);
//        URLConnection uc = url.openConnection();
//        uc.setDoOutput(true);
//        uc.setDoInput(true);
//        uc.setUseCaches(false);
//        uc.setRequestProperty("Content-type","application/x-www-form-urlencoded");
//        ObjectOutputStream objOut = new ObjectOutputStream(uc.getOutputStream());
//        if (data!=null){
//            objOut.writeObject(data);
//        }
//        InputStream in = uc.getInputStream();
//        ObjectInputStream objStream;
//        objStream = new ObjectInputStream(uc.getInputStream());
//        Object dataRead = objStream.readObject();
//        objStream.close();
//        in.close();
//        return dataRead;
//    }

    private void jbInit() throws Exception {

        JLabel lblNameUser = new JLabel();
        lblNameUser.setText(rb.getString("login.user"));// "Usuario:");
        lblNameUser.setBounds(new Rectangle(12, 10, 56, 20));

        this.setLocale(java.util.Locale.getDefault());
        this.getContentPane().setLayout(null);


        txtNameUser.setToolTipText("");
        txtNameUser.setText("");
        txtNameUser.setBounds(new Rectangle(90, 14, 95, 18));

        JLabel lblPassword = new JLabel();
        lblPassword.setText(rb.getString("login.pass"));
        lblPassword.setBounds(new Rectangle(12, 37, 70, 15));

        txtPass.setText("");
        txtPass.setBounds(new Rectangle(90, 39, 95, 18));

        JButton btnOk = new JButton();
        btnOk.setBounds(new Rectangle(12, 65, 86, 18));
        btnOk.setText(rb.getString("btn.ok"));
        btnOk.addActionListener(new FrLogin_btnOk_actionAdapter(this));

        JButton btnCancel = new JButton();
        btnCancel.setBounds(new Rectangle(100, 65,86, 18));
        btnCancel.setText(rb.getString("btn.cancel"));
        btnCancel.addActionListener(new FrLogin_btnCancel_actionAdapter(this));
        this.getContentPane().add(lblNameUser, null);
        this.getContentPane().add(lblPassword, null);
        this.getContentPane().add(txtPass, null);
        this.getContentPane().add(txtNameUser, null);
        this.getContentPane().add(btnOk, null);
        this.getContentPane().add(btnCancel, null);

        this.setSize(new Dimension(200, 120));
        this.setLocation(200,100);
        this.show();
        txtNameUser.requestFocus();
    }

    void btnCancel_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    void btnOk_actionPerformed(ActionEvent e) {
        if ((ToolsHTML.checkValue(txtNameUser.getText()))&&(ToolsHTML.checkValue(txtPass.getPassword().toString()))){
            //System.out.println("txtPass.getPassword().toString() = " + txtPass.getText());// .getPassword().toString());
            Users user = new Users(txtNameUser.getText(),txtPass.getText(),null);
            try {
                Object data = sendRequeriment("application/x-www-form-urlencoded",
                                              "http://localhost:7001/WebDocuments/servletloaddocuments","?cmd=checkUser",user);
                if (data!=null){
                    if (data instanceof Boolean){
                        //System.out.println("data = " + data);
                    }
                    if (data instanceof Hashtable){
                        Hashtable datos = (Hashtable)data;
                        boolean isUserValid = ((Boolean) datos.get("userValid")).booleanValue();
                        if (!isUserValid){
                            showMessage(rb.getString((String)datos.get("error")),JOptionPane.ERROR_MESSAGE);
                        } else{
                            this.setVisible(false);
                            DataDocument dd;
                            if (datos.get("userLocale")!=null){
                                dd = new DataDocument((Locale)datos.get("userLocale"));
                            } else{
                                dd = new DataDocument(defaultLocal);
                            }
                            dd.show();
                            //System.out.println("Saliendo....");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class FrLogin_btnCancel_actionAdapter implements java.awt.event.ActionListener {
    FrLogin adaptee;

    FrLogin_btnCancel_actionAdapter(FrLogin adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCancel_actionPerformed(e);
    }
}

class FrLogin_btnOk_actionAdapter implements java.awt.event.ActionListener {
    FrLogin adaptee;

    FrLogin_btnOk_actionAdapter(FrLogin adaptee) {
        this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
        adaptee.btnOk_actionPerformed(e);
    }
}
