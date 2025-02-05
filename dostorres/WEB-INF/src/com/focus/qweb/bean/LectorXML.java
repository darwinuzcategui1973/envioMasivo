package com.focus.qweb.bean;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class LectorXML extends DefaultHandler {

	private final XMLReader xr;
	private String valor;
	private String actual;
	private String anterior;
	private ArrayList data;
	private ArrayList dataHeader;
	private StringBuffer linea;
	private String registro = "";
	private final String vacio = "";
	private final String coma = ";";
	private String sep = "";

	public LectorXML() throws SAXException {
		xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
	}

	public void leer(final String archivoXML) throws FileNotFoundException, IOException, SAXException {
		FileReader fr = new FileReader(archivoXML);
		data = new ArrayList();
		dataHeader = new ArrayList();
		linea = new StringBuffer();
		xr.parse(new InputSource(fr));
	}

	@Override
	public void startDocument() {
		// //System.out.println("Comienzo del Documento XML");
	}

	@Override
	public void endDocument() {
		// //System.out.println("Final del Documento XML");
	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		// //System.out.println("tElemento: " + name);
		anterior = actual;
		actual = name;

		String label = "";
		String value = "";
		for (int i = 0; i < atts.getLength(); i++) {
			// //System.out.println("ttAtributo: " + atts.getLocalName(i) + " = "
			// + atts.getValue(i));
			if (atts.getLocalName(i).equals("name")) {
				label = atts.getValue(i);
			} else if (atts.getLocalName(i).equals("value")) {
				value = atts.getValue(i);
			}
		}
		if (!label.trim().equals("") && !value.trim().equals("")) {
			value = value.replaceAll("\n", "").replaceAll("\r", "").replace(coma, ",");
			value = value.trim();
			characters(label, value);
		}

	}

	@Override
	public void endElement(String uri, String name, String qName) {
		if (name.equals(registro)) {
			//System.out.println(linea.toString());
			data.add(linea.toString());
			linea.setLength(0);
			sep = vacio;
		}

		// //System.out.println(valor);
		// //System.out.println("tFin Elemento: " + name);
	}

	@Override
	public void characters(char[] ch, int start, int end) {
		valor = new String(ch, start, end);
		valor = valor.replaceAll("\n", "").replaceAll("\r", "").replace(coma, ",");
		valor = valor.trim();
		if (valor.length() > 0) {
			if (registro.length() == 0) {
				registro = anterior;
			}
			linea.append(sep).append(valor);
			sep = coma;
			if (data.size() == 0) {
				dataHeader.add(actual);
			}
		}

	}

	public void characters(String label, String valor) {
		if (valor.length() > 0) {
			if (registro.length() == 0) {
				registro = anterior;
			}
			linea.append(sep).append(valor);
			sep = coma;
			if (data.size() == 0) {
				dataHeader.add(label);
			}
		}

	}

	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
		LectorXML lector = new LectorXML();
		lector.leer("C:/migracion/prueba.xml");
	}

	public ArrayList getData() {
		return data;
	}

	public ArrayList getDataHeader() {
		return dataHeader;
	}
}
