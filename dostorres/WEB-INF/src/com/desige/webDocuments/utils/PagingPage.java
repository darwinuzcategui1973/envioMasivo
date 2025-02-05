package com.desige.webDocuments.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Title: PagingPage.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>08/04/2004 (NC) Creation </li>
 </ul>
 */
public class PagingPage {
    // manejo de l�neas
    private int displayRows = Integer.parseInt(DesigeConf.getProperty("recordDisplay"));
    private int startRow = 0;
    private int totalRows = 0;
    //
    // manejo de p�ginas
    private int numPages = 1;
    private Integer currentPage = new Integer(1);

    /**
     * Devuelve la cantidad de filas que se pueden mostrar en un reporte dado
     * @return La cantidad de filas que se pueden mostrar en un reporte dado
     */
    public int getDisplayRows() {
        return this.displayRows;
    }

    /**
     * Coloca el valor de filas que se pueden mostrar en nu reporte dado
     * @param displayRows
     */
    public void setDisplayRows(int displayRows) {
        this.displayRows = displayRows;
    }

    public void setDisplayRows(Object displayRows) {
        setDisplayRows(ToolsHTML.getNumber(displayRows));
    }

    /**
     * Devuelve el numero de la fila desde la cual se empezar� a mostrar un listado dado
     * @return El numero de la fila desde la cual se empezar� a mostrar un listado dado
     */
    public int getStartRow() {
        return this.startRow;
    }

    /**
     * Devuelve el n�mero total de filas existentes
     * @return El n�mero total de filas existentes
     */
    public int getTotalRows() {
        return this.totalRows;
    }

    /**
     * Inicializa el n�mero total de filas de un listado cualquiera
     * @param totalRows
     */
    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
        this.numPages = this.totalRows / this.displayRows;
        //
        if ((this.totalRows % this.displayRows) != 0) {
            this.numPages++;
        }
        if (this.numPages < 1) {
            this.numPages = 1;
        }
    }

    public void setTotalRows(Object totalRows) {
        setTotalRows(ToolsHTML.getNumber(totalRows));
    }

    /**
     * Devuelve el n�mero total de p�ginas que pueden haber en un listado cualquiera. Este se calcula autom�ticamente
     * Dependiendo del valor del n�mero total de filas y la cantidad de filas que se pueden mostrar en un momento dado
     * @return
     */
    public int getNumPages() {
        return this.numPages;
    }

    /**
     * Devuelve el n�mero de la p�gina actual que se estar�a mostrando
     * @return
     */
    public Integer getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Inicializa el valor del n�mero de la p�gina que se est� mostrando. Autom�ticamente se estar�a calculando el valor
     * de inicio del listado a presentarse "startRow"
     * @param currentPage
     */
    public void setCurrentPage(Integer currentPage) {
        int page = ToolsHTML.getNumber(currentPage);
        //
        if (page < 1) {
            page = 1;
        }
        if (page > this.numPages) {
            page = this.numPages;
        }
        this.currentPage = new Integer(page);
        this.startRow = (page - 1) * this.displayRows;
    }

    /**
     * M�todo que es invocado para inicializar los valores de las propiedades, y el cual es llamado sobre m�s que nada
     * a nivel interno de servlets
     * @param mapping
     * @param request
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.startRow = 0;
        this.totalRows = 0;
        this.numPages = 1;
        this.currentPage = new Integer(1);
    }
}
