package com.desige.webDocuments.utils;

import java.io.Serializable;
import java.util.HashMap;

import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;

/**
 * Title: Constants.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @author Ing: Simon Rodriguez (SR)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 28/12/2004 (NC) Creation </li>
 *          <li> 19/05/2005 agregar constante ToEditRegister(SR)
 *          </ul>
 */

public class Constants implements Serializable {

	public static final String COMA = ",";
	public static final String PUNTO_COMA = ";";
	
	public static final int FILE_ERROR_NOT_GENERATE_PDF = 8920;

	public static final int LINEAS_POR_PAGINA = 25;

	public static final Integer FALSE = new Integer(0);
	public static final Integer TRUE = new Integer(1);
	
	public static String notifyEmail = null; 
	
	public static final int MAXIMO_USUARIOS_VERSION_LITE = 4; // si se cambia este valor, cambiar loginbundl... E0135 

	public static final String ID_USER_TEMPORAL = "temporal";
	public static final String TIPO_DOC = "FP";
	
	public static final String ID_GROUP_ADMIN = "2";
	public static final String ID_GROUP_VIEWER = "1";
	public static final String ID_USER_VIEWER = "1";

	// si se modificar este numero esta en 
	// cliente on demand ListenerAplicarCambios
	public static final int PASS_ENCRYTED = 45;	

	public static final String imgToProcess = "menu_new_root.gif";
	public static final String numToProcess = "2";
	public static final String cmdToStruct = "toStruct";
	public static final String cmdToDocument = "toDocument";
	public static final String cmdToRecord = "toRecord";
	public static final byte permission = 1;
	public static final byte notPermission = 0;
	public static final byte notChange = -1;
	public static final String permissionSt = "1";
	public static final String notPermissionSt = "0";
	public static final String typeNumByLocationVacio = "0";
	public static final String typeNumByLocationLLeno = "1";

	public static final String minutes = "N";
	public static final String hour = "H";
	public static final String days = "D";
	public static final String months = "M";
	public static final String years = "A";

	public static final String historyRevised = "8";
	public static final String historyApproved = "9";
	public static final String historyExpired = "10";
	public static final String historyCompleted = "30"; 
	public static byte toImpresion = 1;
	public static byte ToEditRegister = 1;
	public static int maxCaracteresEnCadena = 1024;		// 50

	// Tipos de Documentos Especiales
	public static String tipoPlantilla = "1";
	public static String tipoFormato = "2";
	public static String tipoRegistro = "1002";
	public static int subtipoRegistro = 1;

	public static boolean ACTIVE_FACTORY = false;;
	// activar impresion en pdf
	public static boolean JDBC_CONNECTION = false; //indica si hubo conexion con base de datos

	public static boolean PRINTER_PDF = false; //"1".equals(String.valueOf(HandlerParameters.PARAMETROS.getOpenoffice()));
	public static String PATH_REPOSITORY = null;
	
	public static int MANEJADOR_ACTUAL = 0;
	public static final int MANEJADOR_MSSQL = 1;
	public static final int MANEJADOR_POSTGRES = 2;
	public static final int MANEJADOR_MYSQL = 3;
	
	public static final String FOLDER_JNLP = "editorPorInternet";
	public static final String FILE_JNLP = "qwds4";
	public static final String FILE_JNLP_VISOR = "qpdf4";
	
	public static final String[] FILE_EXT_LOAD = {"pdf","tiff"};
	
	public static final String COLUMN_FILES_ALL=",1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,";	

	
	public static final String COLUMN_SEARCH_DEFAULT="ACDGH";
	public static final String COLUMN_PUBLISHED_DEFAULT="ABDEFGHI";
	public static final String COLUMN_SACOP_DEFAULT="ABCFIJK";
	public static final String COLUMN_FILES_DEFAULT=",1,2,";
	public static final String COLUMN_MENUHEAD_DEFAULT="S";

	public static final String COLUMN_SEARCH_NAME="searchOption";
	public static final String COLUMN_PUBLISHED_NAME="publishedOption";
	public static final String COLUMN_FILES_NAME="filesOption";
	public static final String COLUMN_MENUHEAD_NAME="menuHeadOption";
	public static final String COLUMN_DIGITAL_NAME="digitalOption";
	public static final String COLUMN_SACOP_NAME="sacopOption";
	public static final String COLUMN_TIPO_SACOP_NAME="tipo";
	public static final String COLUMN_PRUEBA="NOMBRE";

	public static final String VALID_DATE_JAVASCRIPT = "onblur=\"DateFormat(this,this.value,event,true,'3')\"  onkeyup=\"DateFormat(this,this.value,event,false,'3')\" onfocus=\"javascript:vDateType='3'\"";
	
	public static final String FECHA_EXPIRACION_GENERICA="4005-11-09 08:36:47";
	
	
	// status de documentos digitales
	public static final String STATUS_DIGITAL_ELIMINADO = "0";
	public static final String STATUS_DIGITAL_NUEVO = "1";
	public static final String STATUS_DIGITAL_RECHAZADO_TO_SCAN = "2";
	public static final String STATUS_DIGITAL_RECHAZADO_TO_TYPESETTER = "3";
	public static final String STATUS_DIGITAL_POR_VERIFICAR = "4";
	public static final String STATUS_DIGITAL_PROCESADO = "5";
	public static final String STATUS_DIGITAL_PROCESADO_VERIFICADO = "6";
	
	public static final String[] STATUS_DIGITAL = new String[]{"Eliminado","Nuevo","Rechazado/Escanear","Rechazado/Digitar","Verificar","Procesado","Proc./Verif."};
	
	public static final String MODULO_ACTIVO = "moduloActivo";
	
	public static final int MODULO_PRINCIPAL = 0;
	public static final int MODULO_AVISO_PENDIENTE = 1;
	public static final int MODULO_AVISO = 2;
	
//	public static final int MODULO_EXPEDIENTE = 3;
//	public static final int MODULO_LISTA_MAESTRA = 4;
//	public static final int MODULO_SACOP = 5;
//	public static final int MODULO_BUSCAR = 6;
	public static final int MODULO_MENSAJES = 3;
//	public static final int MODULO_ESTADISTICAS = 8;
//	public static final int MODULO_DIGITALIZAR = 9;
	public static final int MODULO_ADMINISTRACION = 4;
	public static final int MODULO_PERFIL = 5;
	public static final int MODULO_ADMINISTRACION_TECNICA = 6;
	public static final int MODULO_INTRODUCCION = 7;
//	public static final int MODULO_FICHA_MAESTRA = 14;
//	public static final int MODULO_ESTRUCTURA = 15;
//  public static final int MODULO_FLUJO = 16;
		
	
	public static HashMap<String, Usuario> TOKEN_USUARIOS_ONDEMAND = new HashMap<String, Usuario>();

	public static HashMap<Integer, String> registerclassTable = null;

	public static String[] ACTION_TYPE = new String[]{"SAC","SAP","SCR","GOM","GDR","GOA","GDQ","GRC"};
	

	/**
	 * Constante para permitir el registro de sesion iniciada
	 * cuando se esta intentando ingresar al sistema con un action
	 * distinto al de login, y debemos dirigir al usuario a la pagina inicial
	 * para que introduzca su clave y password, para que luego de dicho acto de logueo
	 * el usuario sea redirigido al action inicialmente solicitado.
	 */
	public static final String PARAMETER_ALLOW_SESSION_CONNECTION = "&allowSessionConnection=" + TRUE;
	
	/**
	 * Esta constante nos permite definir la apertura de la peticion inicial en una nueva ventana o no.
	 * Esto se debe a que muchas veces, luego de vernos obligados a hacer el login, queremos cargar de igual manera
	 * el panel principal del aplicativo y en otra ventana, el request original que nos obligo a loguearnos
	 * por no tener una sesion activa.
	 */
	public static final String OPEN_URL_PETITION_IN_A_NEW_WINDOW = "&openURLInNewWindow=" + TRUE;
	

}
