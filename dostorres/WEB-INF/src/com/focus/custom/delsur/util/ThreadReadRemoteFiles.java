package com.focus.custom.delsur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.custom.delsur.dto.CreditoDTO;
import com.focus.custom.delsur.dto.RecaudoCreditoDTO;

/**
 * 
 * @author felipe.rojas
 *
 */
class ThreadReadRemoteFiles implements Runnable{
	private static final Logger log = LoggerFactory.getLogger(ThreadReadRemoteFiles.class);
	
	private boolean processEnd;
	private long maxWaitToRead;
	private long lastOperationMark;
	private String prefixPath;
	private String destinationPath;
	private String remotePathSeparator;
	private String palabrasClaves;
	private Users usuario;
	private CreditoDTO creditoDTO;
	
	/**
	 * 
	 * @param maxWaitToRead
	 * @param prefixPath
	 * @param destinationPath
	 * @param remotePathSeparator
	 * @param palabrasClaves
	 * @param usuario
	 * @param creditoDTO
	 */
	public ThreadReadRemoteFiles(long maxWaitToRead, String prefixPath, 
			String destinationPath, String remotePathSeparator,
			String palabrasClaves, Users usuario, CreditoDTO creditoDTO) {
		// TODO Auto-generated constructor stub
		this.lastOperationMark = System.currentTimeMillis();
		this.processEnd = false;
		this.maxWaitToRead = maxWaitToRead;
		this.prefixPath = prefixPath;
		this.destinationPath = destinationPath;
		this.remotePathSeparator = remotePathSeparator;
		this.palabrasClaves = palabrasClaves;
		this.usuario = usuario;
		this.creditoDTO = creditoDTO;
	}
	
	public boolean isProcessEnd() {
		return processEnd;
	}
	
	/**
	 * Se verifica si se alcanzo el timeout para la lectura remota
	 * 
	 * @return
	 */
	public boolean isTimeOutReached(){
		if(System.currentTimeMillis() - lastOperationMark > maxWaitToRead){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		lastOperationMark = System.currentTimeMillis();
		final String basePath = prefixPath + "Agencia" 
				+ creditoDTO.getCodigoAgencia();
		
		List<RecaudoCreditoDTO> recaudos = creditoDTO.getListadoRecaudos();
		
		//debo ubicar la solicitud dentro del nodo base de la agencia asi como en sus subcarpetas
		boolean wasFound = false;
		File baseDir = new File(basePath);
		
		if(! baseDir.exists()){
			log.info("No existe el nodo base de la agencia '"
					+ basePath + "' esto posibiblemente sea debido a un mapeo faltante");
		} else {
			log.info(baseDir.getAbsolutePath() 
					+ " existe, se procede a apuntar a su contenido");
			
			File[] nodosAgencia = baseDir.listFiles();
			List<File> subDirectoriosAgencia = new LinkedList<File>();
			subDirectoriosAgencia.add(baseDir);
			
			lastOperationMark = System.currentTimeMillis();
			log.info("Agregado el directorio base a la lista de lectura");
			
			if(nodosAgencia != null){
				List<File> nodosInternos = Arrays.asList(nodosAgencia);
				if(nodosInternos != null){
					subDirectoriosAgencia.addAll(nodosInternos);
				}
			} else {
				log.info("La carpeta " + basePath + " no existe aun, posible falta de mapeo");
			}
				
			log.info("Intentando leer los documentos desde la carpeta base de la agencia "
					+ basePath);
			
			for (File subDirectorio : subDirectoriosAgencia) {
				wasFound = false;
				
				log.info("Por revisar " + subDirectorio.getAbsolutePath()
						+ ", es directorio: " + subDirectorio.isDirectory());
				
				if(subDirectorio.isDirectory()){
					lastOperationMark = System.currentTimeMillis();
					File solicitudDir = new File(subDirectorio.getAbsolutePath()
							+ remotePathSeparator + creditoDTO.getNumeroSolicitud());
					
					if(solicitudDir.exists()){
						log.info("Procesando la solicitud " + creditoDTO.getNumeroSolicitud()
								+ " desde la carpeta " + solicitudDir.getAbsolutePath());
						
						File[] remoteFiles = solicitudDir.listFiles();
						if(remoteFiles != null){
							wasFound = true;
							log.info("Existen " + remoteFiles.length + " documentos en "
									+ solicitudDir.getAbsolutePath());
							
							//comparamos los recaudos en base de datos contra los recaudos fisicos
							if(remoteFiles.length < recaudos.size()){
								log.error("La base de datos dice que tenemos "
										+ recaudos.size() + " recaudos, pero en el directorio "
										+ "solo hay " + remoteFiles.length 
										+ " notificamos al ejecutivo y gerente");
								DelSurStructMailUtil.sendLessFileThanRecords(creditoDTO.getCodigoUsuarioAbanks(),
										creditoDTO.getCodigoGerenteAbanks(),
										Integer.toString(creditoDTO.getNumeroSolicitud()),
										solicitudDir.getAbsolutePath(),
										remoteFiles.length,
										recaudos.size());
							}
							
							RecaudoCreditoDTO recaudoDTO = null;
							
							for (int i = 0; i < remoteFiles.length; i++) {
								FileInputStream fis = null;
								FileOutputStream fos = null;
								
								try {
									recaudoDTO = recaudos.get(i);
								} catch (Exception e) {
									// TODO: handle exception
								}
								
								if (recaudoDTO != null) {
									try {
										String fileDest = destinationPath
												+ File.separator + creditoDTO.getNumeroSolicitud()
												+ "_" + creditoDTO.getNombreAgencia()
												//+ "_" + recaudoDTO.getNombreDocumento()
												//+ "_" + remoteFiles[i].getName();
												+ "_" + creditoDTO.getNombreProducto();
										
										int docTypeId = DelSurTypeDocUtil.getTypeDocId(
												recaudoDTO.getNombreDocumento(),
												Integer.toString(recaudoDTO.getCodigoDocumento()));
										String internalMetaData = recaudoDTO.getCodigoDocumento() + "|"
												+ creditoDTO.getNumeroSolicitud();
										boolean mustReadFile = true;
										
										DigitalDAO digitalDAO = new DigitalDAO();
										DigitalTO digitalTO = new DigitalTO();
										digitalTO.setNameFile(StringUtil.getOnlyNameFile(fileDest));
										digitalTO.setInternalMetaData(internalMetaData);
										digitalTO = digitalDAO.findByNameFileAndInternalMetaData(digitalTO);
										
										//verificamos si existia el registro o no
										if(digitalTO.getIdDigital() != null){
											//el registro existia, vemos si en un status que amerite
											//leer el archivo nuevamente
											if(Constants.STATUS_DIGITAL_NUEVO.equals(digitalTO.getIdStatusDigital())
													|| Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN.equals(digitalTO.getIdStatusDigital())
													|| Constants.STATUS_DIGITAL_RECHAZADO_TO_TYPESETTER.equals(digitalTO.getIdStatusDigital())){
												//el documento ya existia pero tiene un status que amerita traer el binario de nuevo
											} else {
												//el documento existe, pero su estatus implica una actualizacion del mismo
												mustReadFile = false;
											}
										} else {
											//el registro no existia
											//debe ser creado
											digitalTO.setIdDigital(String.valueOf(digitalDAO.nextId()));
											digitalTO.setNameFile(StringUtil.getOnlyNameFile(fileDest));
											digitalTO.setNameDocument(StringUtil.getOnlyNameFile(fileDest));
											digitalTO.setNumberTest("1");
											digitalTO.setIdPerson(String.valueOf(usuario.getIdPerson()));
											digitalTO.setIdStatusDigital(Constants.STATUS_DIGITAL_NUEVO);
											digitalTO.setOwnerTypeDoc(String.valueOf(usuario.getIdPerson()));
											digitalTO.setIdNode(recaudoDTO.getIdNode());
											digitalTO.setTypesetter(String.valueOf(usuario.getIdPerson()));
											digitalTO.setChecker(String.valueOf(usuario.getIdPerson()));
											//digitalTO.setLote(Integer.toString(recaudoDTO.getCodigoDocumento()));
											digitalTO.setType(Integer.toString(docTypeId));
											digitalTO.setCodigo("");
											digitalTO.setVisible("1");
											digitalTO.setPublicado("0");
											digitalTO.setPublicDoc("0");
											digitalTO.setFechaPublicacion(ToolsHTML.date.format(Calendar.getInstance().getTime()));
											digitalTO.setPalabrasClaves(palabrasClaves);
											digitalTO.setInternalMetaData(internalMetaData);
										}
										
										if(mustReadFile){
											//colocamos el nombre que usa el visor de digitalizados
											lastOperationMark = System.currentTimeMillis();
											fileDest = destinationPath
													+ File.separator + digitalTO.getNameFileDisk();
											
											fis = new FileInputStream(remoteFiles[i]);
											fos = new FileOutputStream(fileDest);
											
											byte[] buf = new byte[4096];
											
											while (true) {
												int read = fis.read(buf);
												if (read == -1) {
													break;
												} else if (read > 0) {
													fos.write(buf, 0, read);
												}
											}
											
											//se copio el archivo, procedemos a crear el registro del modulo digital
											digitalDAO.save(digitalTO);
											
											log.info("Creado Registro del modulo de digitalizacion para el documento "
													+ fileDest );
											//actualizamos el estatus a recibido
											DelSurRemoteFileUtil.updateStatusRecaudo(creditoDTO.getNumeroSolicitud(),
													recaudoDTO.getCodigoDocumento(), 
													StatusRecaudos.RECIBIDO);
											lastOperationMark = System.currentTimeMillis();
										}
									} catch (Throwable e) {
										// TODO Auto-generated catch block
										log.error("Error de lectura/escritura con archivo "
												+ remoteFiles[i].getAbsolutePath(), e);
									} finally {
										try {
											fos.close();
										} catch (Exception e2) {
											// TODO: handle exception
										}
										
										try {
											fis.close();
										} catch (Exception e2) {
											// TODO: handle exception
										}
									}
								}
							}
						}
						
						break;
					}
				}
			}
		}
		
		if(! wasFound){
			log.error("No fue encontrada la solicitud "
					+ creditoDTO.getNumeroSolicitud()
					+ " dentro del directorio base " + basePath);
			
			actualizarRecaudosComoNoLeidos(creditoDTO.getNumeroSolicitud(),
					recaudos,
					creditoDTO.getCodigoUsuarioAbanks(),
					creditoDTO.getCodigoGerenteAbanks(),
					basePath);
		}
		
		processEnd = true;
	}
	
	/**
	 * Por algun motivo no se pudo leer el directorio remoto
	 * Debemos indicar esto por correo y tambien 
	 * actualizar los insumos en la base de datos.
	 * 
	 * @param nroSolicitud
	 * @param recaudos
	 * @param ejecutivoAbanks 
	 * @param gerenteAbanks 
	 * @param codigoSolicitud 
	 * @param ruta 
	 */
	private static void actualizarRecaudosComoNoLeidos(int nroSolicitud,
			List<RecaudoCreditoDTO> recaudos, String ejecutivoAbanks,
			String gerenteAbanks, String ruta) {
		// TODO Auto-generated method stub
		//enviamos el correo que corresponda
		DelSurStructMailUtil.sendNotReadedDocumentsStatus(ejecutivoAbanks,
				gerenteAbanks, 
				Integer.toString(nroSolicitud), 
				ruta);
		
		for (RecaudoCreditoDTO recaudoCreditoDTO : recaudos) {
			DelSurRemoteFileUtil.updateStatusRecaudo(nroSolicitud, 
					recaudoCreditoDTO.getCodigoDocumento(), 
					StatusRecaudos.NO_LEIDO);
		}
	}
}
