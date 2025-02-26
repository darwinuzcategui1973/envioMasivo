/**************************************************************************
 * copyright file="MessageDisposition.java" company="Microsoft"
 *     Copyright (c) Microsoft Corporation.  All rights reserved.
 * 
 * Defines the MessageDisposition.java.
 **************************************************************************/
package microsoft.exchange.webservices.data;

/**
 * Defines how messages are disposed of in CreateItem and UpdateItem operations.
 */
public enum MessageDisposition {
	/*
	 * messages are saved but not sent.
	 */
	/** The Save only. */
	SaveOnly,
	/*
	 * messages are sent and a copy is saved.
	 */
	/** The Send and save copy. */
	SendAndSaveCopy,
	/*
	 * messages are sent but no copy is saved.
	 */
	/** The Send only. */
	SendOnly
}
