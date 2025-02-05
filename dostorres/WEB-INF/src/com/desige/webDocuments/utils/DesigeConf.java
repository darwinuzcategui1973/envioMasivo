package com.desige.webDocuments.utils;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Title: DesigeConf.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */
public class DesigeConf {
    private static final DesigeConf DESIGE_CONF = new DesigeConf();
    ResourceBundle rb = null;
    Properties prop = new Properties();

    private DesigeConf() {
        try {
            this.rb = ResourceBundle.getBundle("WebDocuments");
        } catch (MissingResourceException e) {
            System.err.println("(Exception) DesigeConf: " + e.getMessage());
            this.rb = null;
        }
   }
    public static String getProperty(String key) {
        String result = null;
        result = cascadeRetrieve(key);
        return result;
   }

    private static String cascadeRetrieve(String key) {
        String result = null;
        try {
            result = DESIGE_CONF.rb.getString(key);
        } catch (MissingResourceException e) {
            //System.out.println("Missing key in WebDocuments.properties: " + key);
            result = DESIGE_CONF.prop.getProperty(key);
        }catch (NullPointerException e) {
            if (key != null) {
                result = DESIGE_CONF.prop.getProperty(key);
            }
        }
        return result;
   }
}
