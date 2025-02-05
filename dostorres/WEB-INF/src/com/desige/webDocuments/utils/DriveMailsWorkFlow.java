package com.desige.webDocuments.utils;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.utils.mail.SendMail;


/**
 * Created by IntelliJ IDEA.
 * User: lcisneros
 * Date: Nov 17, 2006
 * Time: 11:15:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class DriveMailsWorkFlow implements Runnable {

    private ResourceBundle rb;
    private MailForm forma;
    static  Logger log = LoggerFactory.getLogger("[V3.0] " + DriveMailsWorkFlow.class.getName());
    private SendMail sendMail;
    private String smtp;
    public DriveMailsWorkFlow() {


    }

    public void run() {
        //System.out.println("Iniciando envio de Mail");
        try {
            Thread.sleep(10000);
             log.info("Termino del Dormir sleep................ ");
//             sendMail.sendMailInst(getSmtp(),forma);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("FIN del envio de Mail");
    }


    public ResourceBundle getRb() {
        return rb;
    }

    public void setRb(ResourceBundle rb) {
        this.rb = rb;
    }


    public MailForm getForma() {
        return forma;
    }

    public void setForma(MailForm forma) {
        this.forma = forma;
    }

    public SendMail getSendMail() {
        return sendMail;
    }

    public void setSendMail(SendMail sendMail) {
        this.sendMail = sendMail;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }
}
