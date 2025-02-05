package com.desige.webDocuments.document.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 13/10/2005
 * Time: 12:00:02 PM
 * To change this template use File | Settings | File Templates.
 */




/**
 * Title: BaseDocumentForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>30/06/2004 (NC) Creation </li>
 *      <li>06/06/2005 (SR) Se crea el bean impresion </li>
 *      <li>17/06/2005 (SR) Se crea el bean dateExpiresDrafts,dateApprovedDrafts; </li>
 *      <li>26/07/2005 (SR) Se crea el bean idLocation,nodeActive </li>
 *      <li>16/08/2005 (SR) Se crea el bean numCorrelativoExiste </li>
 *
 </ul>
 */
public class frmsolicitudImpresion extends SuperActionForm implements Serializable {
    private int numberGen;
    private int numVer;
    private int numsolicitud;
    private int solicitante;
    private String destinatarios;
    private String codigo;
    private String nameDocument;
    private String gerenciaSolicitante;
    private String path;
    private int autorizante;
    private int statusautorizante;
    private int statusimpresion;
    private String datesolicitud;
    private String copias;
    private String acumCopias;
    private String comments;
    private boolean nuevaSolicitud;
    private String nameSolicitante;

    public String getComments() {
           return comments;
       }

       public void setComments(String comments) {
           this.comments = comments;
       }


    public String getAcumCopias() {
           return acumCopias;
       }

       public void setAcumCopias(String acumCopias) {
           this.acumCopias = acumCopias;
       }

    public boolean getNuevaSolicitud() {
               return nuevaSolicitud;
           }

           public void setNuevaSolicitud(boolean nuevaSolicitud) {
               this.nuevaSolicitud = nuevaSolicitud;
           }

    public String getPath() {
               return path;
           }

    public void setPath(String path) {
               this.path = path;
           }


    public String getGerenciaSolicitante() {
           return gerenciaSolicitante;
       }

       public void setGerenciaSolicitante(String gerenciaSolicitante) {
           this.gerenciaSolicitante = gerenciaSolicitante;
       }

    public String getNameDocument() {
          return nameDocument;
      }

      public void setNameDocument(String nameDocument) {
          this.nameDocument = nameDocument;
      }


     public String getCodigo() {
           return codigo;
       }

       public void setCodigo(String codigo) {
           this.codigo = codigo;
       }


    public String getCopias() {
           return copias;
       }

       public void setCopias(String copias) {
           this.copias = copias;
       }

    public String getDestinatarios() {
           return destinatarios;
       }

       public void setDestinatarios(String destinatarios) {
           this.destinatarios = destinatarios;
       }


    public String getDatesolicitud() {
        return datesolicitud;
    }

    public void setDatesolicitud(String datesolicitud) {
        this.datesolicitud = datesolicitud;
    }

     public int getStatusimpresion() {
            return statusimpresion;
        }

        public void setStatusimpresion(int statusimpresion) {
            this.statusimpresion = statusimpresion;
        }


     public int getStatusautorizante() {
            return statusautorizante;
        }

        public void setStatusautorizante(int statusautorizante) {
            this.statusautorizante = statusautorizante;
        }

    public int getAutorizante() {
            return autorizante;
        }

        public void setAutorizante(int autorizante) {
            this.autorizante = autorizante;
        }

    public int getSolicitante() {
            return solicitante;
        }

        public void setSolicitante(int solicitante) {
            this.solicitante = solicitante;
        }

    public int getNumsolicitud() {
            return numsolicitud;
        }

        public void setNumsolicitud(int numsolicitud) {
            this.numsolicitud = numsolicitud;
        }



    public int getNumberGen() {
        return numberGen;
    }

    public void setNumberGen(int numberGen) {
        this.numberGen = numberGen;
    }

    public int getNumVer() {
        return numVer;
    }

    public void setNumVer(int numVer) {
        this.numVer = numVer;
    }

    public String getNameSolicitante() {
        return nameSolicitante;
    }

    public void setNameSolicitante(String nameSolicitante) {
        this.nameSolicitante = nameSolicitante;
    }
}
