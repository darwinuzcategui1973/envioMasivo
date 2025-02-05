package com.desige.webDocuments.utils;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Title: ApplicationExceptionChecked.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br>
 *     Changes:<br>
 * <ul>
 * 		<li> 12/09/2004 (NC) Creation </li>
 * <ul>
 */
public class ApplicationExceptionChecked extends Exception {
    private String keyError;
    private String additionalInfo;

	public ApplicationExceptionChecked(String mensaje){
        super(mensaje);
        setKeyError(mensaje);
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

    public String getKeyError() {
        return keyError;
    }

    public void setKeyError(String keyError) {
        this.keyError = keyError;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
