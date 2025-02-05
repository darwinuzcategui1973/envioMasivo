package com.desige.webDocuments.util;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: lcisneros
 * Date: Mar 26, 2007
 * Time: 1:36:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Disco {

    public static void main(String[] args) {
        Properties p = System.getProperties();

       Enumeration e = p.elements();
           
       while (e.hasMoreElements()){
           String s = (String) e.nextElement();
           //System.out.println(s);
       } 
    }
}
