/**************************************************************************
 * copyright file="ResolveNameSearchLocation.java" company="Microsoft"
 *     Copyright (c) Microsoft Corporation.  All rights reserved.
 * 
 * Defines the ResolveNameSearchLocation.java.
 **************************************************************************/
package microsoft.exchange.webservices.data;

/**
 * Defines the location where a ResolveName operation searches for contacts.
 */
public enum ResolveNameSearchLocation {

	// The name is resolved against the Global address List.
	/** The Directory only. */
	DirectoryOnly,

	// The name is resolved against the Global address List and then against the
	// contacts folder if no match was found.
	/** The Directory then contacts. */
	DirectoryThenContacts,

	// The name is resolved against the contacts folder.
	/** The contacts only. */
	ContactsOnly,

	// The name is resolved against the contacts folder and then against the
	// Global address List if no match was found.
	/** The contacts then directory. */
	ContactsThenDirectory
}
