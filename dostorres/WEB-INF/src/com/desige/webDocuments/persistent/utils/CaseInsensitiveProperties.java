package com.desige.webDocuments.persistent.utils;

import java.util.Properties;

/**
 * Title: CaseInsensitiveProperties.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */

public class CaseInsensitiveProperties extends Properties {
   private Properties prop;

   /**
    * Constructor
    */
   public CaseInsensitiveProperties() {
      super();
      this.prop = new Properties();
   }

   public Object setProperty(String key, String value) {
      return this.prop.setProperty(key.toUpperCase(), value);
   }

   public String getProperty(String key) {
      return this.prop.getProperty(key.toUpperCase());
   }
}