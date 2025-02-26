/**************************************************************************
 * copyright file="WSSecurityBasedCredentials.java" company="Microsoft"
 *     Copyright (c) Microsoft Corporation.  All rights reserved.
 * 
 * Defines the WSSecurityBasedCredentials.java.
 **************************************************************************/
package microsoft.exchange.webservices.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


//import sun.util.calendar.CalendarDate;

/**
 * WSSecurityBasedCredentials is the base class for all credential classes using
 * WS-Security.
 */
public abstract class WSSecurityBasedCredentials extends ExchangeCredentials {

	/** The security token. */
	private String securityToken;

	/** The ews url. */
	private URI ewsUrl;
	
	protected static final String wsuTimeStampFormat =
        "<wsu:Timestamp>" +
        "<wsu:Created>{0:yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'}</wsu:Created>" +
        "<wsu:Expires>{1:yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'}</wsu:Expires>" +
        "</wsu:Timestamp>";
//kavi-start
	// WS-Security SecExt 1.0 Namespace (and the namespace prefix we will use
	// for it).
	/** The Constant WSSecuritySecExt10NamespacePrefix. */
	//protected static final String WSSecuritySecExt10NamespacePrefix = "wsse";

	/** The Constant WSSecuritySecExt10Namespace. */
	//protected static final String WSSecuritySecExt10Namespace = 
	//	"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	// WS-Addressing 1.0 Namespace (and the namespace prefix we will use for
	// it).
	
	
	/** The Constant WSAddressing10NamespacePrefix. */
	//protected static final String WSAddressing10NamespacePrefix = "wsa";

	/** The Constant WSAddressing10Namespace. */
	//protected static final String WSAddressing10Namespace =
	//	"http://www.w3.org/2005/08/addressing";
	
	//kavi end
	
	// The WS-Addressing headers format string to use for adding the
	// WS-Addressing headers.
	// Fill-Ins: %s = Web method name; %s = EWS URL
	/** The Constant WsAddressingHeadersFormat. */
	protected static final String wsAddressingHeadersFormat =
		"<wsa:Action soap:mustUnderstand='1'>http://schemas.microsoft.com/exchange/services/2006/messages/%s</wsa:Action>" +
		"<wsa:ReplyTo><wsa:Address>http://www.w3.org/2005/08/addressing/anonymous</wsa:Address>" +
		"</wsa:ReplyTo>" +
		"<wsa:To soap:mustUnderstand='1'>%s</wsa:To>";

	// The WS-Security header format string to use for adding the WS-Security
	// header.
	// Fill-Ins:
	// %s = EncryptedData block (the token)
	/** The Constant WsSecurityHeaderFormat. */
	protected static final String wsSecurityHeaderFormat = 
		"<wsse:Security soap:mustUnderstand='1'>" +
		"  %s" + // EncryptedData (token)
		"</wsse:Security>";

	private boolean addTimestamp;
	
	// / Path suffix for WS-Security endpoint.
	/** The Constant WsSecurityPathSuffix. */
	protected static final String wsSecurityPathSuffix = "/wssecurity";

	/**
	 * Initializes a new instance of the WSSecurityBasedCredentials class.	 
	 */
	protected WSSecurityBasedCredentials() {
	}

	/**
	 * Initializes a new instance of the WSSecurityBasedCredentials class.	
	 * @param securityToken
	 * 				The security token. 
	 */
	protected WSSecurityBasedCredentials(String securityToken) {
		this.securityToken = securityToken;
	}

	/**
	 * Initializes a new instance of the WSSecurityBasedCredentials class.	
	 * @param securityToken
	 * 				The security token. 
	* @param addTimestamp
	 * 				Timestamp should be added.
	 */
    protected WSSecurityBasedCredentials(String securityToken, boolean addTimestamp)
    {
        this.securityToken = securityToken;
        this.addTimestamp = addTimestamp;
    }

	/**
	 * This method is called to pre-authenticate credentials before a service
	 * request is made.
	 */
	@Override
	protected void preAuthenticate() {
		// Nothing special to do here.
	}

	/**
	 * Emit the extra namespace aliases used for WS-Security and WS-Addressing.
	 * 
	 * @param writer
	 *            The writer.
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	@Override
	protected void emitExtraSoapHeaderNamespaceAliases(XMLStreamWriter writer)
	throws XMLStreamException {
		writer.writeAttribute(
                "xmlns",
                EwsUtilities.WSSecuritySecExtNamespacePrefix,
                null,
                EwsUtilities.WSSecuritySecExtNamespace);
            writer.writeAttribute(
                "xmlns",
                EwsUtilities.WSAddressingNamespacePrefix,
                null,
                EwsUtilities.WSAddressingNamespace);
	}

	/**
	 * Serialize the WS-Security and WS-Addressing SOAP headers.
	 * 
	 * @param writer
	 *            The writer.
	 * @param webMethodName
	 *            The Web method being called.
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	@Override
	protected void serializeExtraSoapHeaders(XMLStreamWriter writer,
			String webMethodName) throws XMLStreamException {
		this.serializeWSAddressingHeaders(writer, webMethodName);
		this.serializeWSSecurityHeaders(writer);
	}

	/**
	 * Creates the WS-Addressing headers necessary to send with an outgoing
	 * request.
	 * 
	 * @param xmlWriter
	 *            The XML writer to serialize the headers to.
	 * @param webMethodName
	 *            The Web method being called.
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	private void serializeWSAddressingHeaders(XMLStreamWriter xmlWriter,
			String webMethodName) throws XMLStreamException {
		EwsUtilities.EwsAssert(webMethodName != null,
				"WSSecurityBasedCredentials.SerializeWSAddressingHeaders",
		"Web method name cannot be null!");

		EwsUtilities.EwsAssert(this.ewsUrl != null,
				"WSSecurityBasedCredentials.SerializeWSAddressingHeaders",
		"EWS Url cannot be null!");

		// Format the WS-Addressing headers.
		String wsAddressingHeaders = String.format(
				WSSecurityBasedCredentials.wsAddressingHeadersFormat,
				webMethodName, this.ewsUrl);

		// And write them out...
		xmlWriter.writeCharacters(wsAddressingHeaders);
	}

	/**
	 * Creates the WS-Security header necessary to send with an outgoing
	 * request.
	 * 
	 * @param xmlWriter
	 *            The XML writer to serialize the headers to.
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	@Override
	public void serializeWSSecurityHeaders(XMLStreamWriter xmlWriter) throws XMLStreamException {
		EwsUtilities.EwsAssert(this.securityToken != null,
				"WSSecurityBasedCredentials.SerializeWSSecurityHeaders",
		"security token cannot be null!");
		
		// <wsu:Timestamp wsu:Id="_timestamp">
        //   <wsu:Created>2007-09-20T01:13:10.468Z</wsu:Created>
        //   <wsu:Expires>2007-09-20T01:18:10.468Z</wsu:Expires>
        // </wsu:Timestamp>
        //
        String timestamp = null;
        if (this.addTimestamp) {
          	Calendar utcNow=Calendar.getInstance();
        	utcNow.add(Calendar.MINUTE, 5);
        	timestamp=String.format(WSSecurityBasedCredentials.wsuTimeStampFormat,utcNow,utcNow);
        }

		// Format the WS-Security header based on all the information we have.
		String wsSecurityHeader = String.format(
				WSSecurityBasedCredentials.wsSecurityHeaderFormat,
				 timestamp + this.securityToken);

		// And write the header out...
		xmlWriter.writeCharacters(wsSecurityHeader);
	}

	/**
	 * Adjusts the URL based on the credentials.
	 * 
	 * @param url
	 *            The URL.
	 * @return Adjust URL.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Override
	protected URI adjustUrl(URI url) throws URISyntaxException {
		return new URI(getUriWithoutWSSecurity(url) + WSSecurityBasedCredentials.wsSecurityPathSuffix);
	}

	/**
	 * Gets the security token.	
	 */
	protected String getSecurityToken() {
		return this.securityToken;
	}
	
	/**
	 * Sets the security token.	
	 */
	protected void setSecurityToken(String value) {
		securityToken = value;
	}
	
	/**
	 * Gets the EWS URL.
	 */
	protected URI getEwsUrl() {
		return this.ewsUrl;
	}
	
	/**
	 * Sets the EWS URL.
	 */
	protected void setEwsUrl(URI value) {
		ewsUrl = value;
	}
}
