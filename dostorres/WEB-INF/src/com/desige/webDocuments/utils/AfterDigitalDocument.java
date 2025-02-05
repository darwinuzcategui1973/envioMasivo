package com.desige.webDocuments.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.desige.webDocuments.to.DigitalTO;

public abstract class AfterDigitalDocument {
	private static Map<String, AfterDigitalDocument> implementaciones = new HashMap<String, AfterDigitalDocument>();
	
	/**
	 * 
	 * @param instance
	 */
	public static void registrarImplementacion(AfterDigitalDocument instance){
		if(! implementaciones.containsKey(instance.getClass().getName())){
			implementaciones.put(instance.getClass().getName(),
					(AfterDigitalDocument) instance);
		}
	}
	
	/**
	 * 
	 * @param digitalTO
	 */
	public static void aprobarDigitalizar(DigitalTO digitalTO){
		for (Iterator<AfterDigitalDocument> impl = implementaciones.values().iterator(); impl.hasNext();) {
			AfterDigitalDocument obj = (AfterDigitalDocument) impl.next();
			obj.executeProcessAfterApproved(digitalTO);
		}
	}
	
	/**
	 * 
	 * @param digitalTO
	 */
	public abstract void executeProcessAfterApproved(DigitalTO digitalTO);
	
	/**
	 * 
	 * @param digitalTO
	 */
	public static void rechazarDigitalizar(DigitalTO digitalTO){
		for (Iterator<AfterDigitalDocument> impl = implementaciones.values().iterator(); impl.hasNext();) {
			AfterDigitalDocument obj = (AfterDigitalDocument) impl.next();
			obj.executeProcessAfterReject(digitalTO);
		}
	}
	
	/**
	 * 
	 * @param digitalTO
	 */
	public abstract void executeProcessAfterReject(DigitalTO digitalTO);
}
