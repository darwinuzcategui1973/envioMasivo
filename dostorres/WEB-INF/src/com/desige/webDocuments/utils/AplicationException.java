package com.desige.webDocuments.utils;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Title: AplicationException.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */

public class AplicationException extends java.lang.RuntimeException {

    public AplicationException(String mensaje){
        super(mensaje);
    }
    /**
     *
     * @param writer
     */
    public void printStackTrace(PrintWriter writer) {
        writer.println(this.getMessage());
        writer.println("Nested exception is: " + super.getMessage());
    }

    /**
     *
     * @param stream
     */
    public void printStackTrace(PrintStream stream) {
        this.printStackTrace(new PrintWriter(stream));
    }

    /**
     *
     */
    public void printStackTrace() {
        super.printStackTrace();
        System.err.println("Nested exception is: " + super.getMessage());
    }
}
