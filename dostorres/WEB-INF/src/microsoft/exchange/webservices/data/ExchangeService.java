/**************************************************************************
 * copyright file="ExchangeService.java" company="Microsoft"
 *     Copyright (c) Microsoft Corporation.  All rights reserved.
 * 
 * Defines the ExchangeService.java.
 **************************************************************************/
package microsoft.exchange.webservices.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.exceptions.AccountIsLockedException;
import microsoft.exchange.webservices.data.exceptions.ArgumentOutOfRangeException;
import microsoft.exchange.webservices.data.exceptions.AutodiscoverLocalException;
import microsoft.exchange.webservices.data.exceptions.ServiceLocalException;
import microsoft.exchange.webservices.data.exceptions.ServiceRemoteException;
import microsoft.exchange.webservices.data.exceptions.ServiceResponseException;
import microsoft.exchange.webservices.data.exceptions.ServiceValidationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents a binding to the Exchange Web Services.
 */
public final class ExchangeService extends ExchangeServiceBase implements
		IAutodiscoverRedirectionUrl {

	/** The url. */
	private URI url;

	/** The preferred culture. */
	private Locale preferredCulture;

	/** The DateTimePrecision */
	private DateTimePrecision dateTimePrecision = DateTimePrecision.Default;

	/** The impersonated user id. */
	private ImpersonatedUserId impersonatedUserId;
	// private Iterator<ItemId> Iterator;
	/** The file attachment content handler. */
	private IFileAttachmentContentHandler fileAttachmentContentHandler;

	/** The unified messaging. */
	private UnifiedMessaging unifiedMessaging;

	// private boolean exchange2007CompatibilityMode;
	private boolean enableScpLookup = true;

	private boolean exchange2007CompatibilityMode;

	/**
	 * Create response object.
	 * 
	 * @param responseObject
	 *            the response object
	 * @param parentFolderId
	 *            the parent folder id
	 * @param messageDisposition
	 *            the message disposition
	 * @return The list of items created or modified as a result of the
	 *         "creation" of the response object.
	 * @throws Exception
	 *             the exception
	 */
	protected List<Item> internalCreateResponseObject(
			ServiceObject responseObject, FolderId parentFolderId,
			MessageDisposition messageDisposition) throws Exception {
		CreateResponseObjectRequest request = new CreateResponseObjectRequest(
				this, ServiceErrorHandling.ThrowOnError);
		ArrayList<ServiceObject> serviceList = new ArrayList<ServiceObject>();
		serviceList.add(responseObject);
		request.setParentFolderId(parentFolderId);
		request.setItems(serviceList);
		request.setMessageDisposition(messageDisposition);

		ServiceResponseCollection<CreateResponseObjectResponse> responses = request
				.execute();

		return responses.getResponseAtIndex(0).getItems();
	}

	/**
	 * Creates a folder. Calling this method results in a call to EWS.
	 * 
	 * @param folder
	 *            The folder.
	 * @param parentFolderId
	 *            The parent folder Id
	 * @throws Exception
	 *             the exception
	 */
	protected void createFolder(Folder folder, FolderId parentFolderId)
			throws Exception {
		CreateFolderRequest request = new CreateFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);
		List<Folder> folArry = new ArrayList<Folder>();
		folArry.add(folder);
		request.setFolders(folArry);
		request.setParentFolderId(parentFolderId);

		request.execute();
	}

	/**
	 * Updates a folder.
	 * 
	 * @param folder
	 *            The folder.
	 * @throws Exception
	 *             the exception
	 */
	protected void updateFolder(Folder folder) throws Exception {
		UpdateFolderRequest request = new UpdateFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);

		request.getFolders().add(folder);

		request.execute();
	}

	/**
	 * Copies a folder. Calling this method results in a call to EWS.
	 * 
	 * @param folderId
	 *            The folderId.
	 * @param destinationFolderId
	 *            The destination folder id.
	 * @return the folder
	 * @throws Exception
	 *             the exception
	 */
	protected Folder copyFolder(FolderId folderId, FolderId destinationFolderId)
			throws Exception {
		CopyFolderRequest request = new CopyFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);

		request.setDestinationFolderId(destinationFolderId);
		request.getFolderIds().add(folderId);

		ServiceResponseCollection<MoveCopyFolderResponse> responses = request
				.execute();

		return responses.getResponseAtIndex(0).getFolder();
	}

	/**
	 * Move a folder.
	 * 
	 * @param folderId
	 *            The folderId.
	 * @param destinationFolderId
	 *            The destination folder id.
	 * @return the folder
	 * @throws Exception
	 *             the exception
	 */
	protected Folder moveFolder(FolderId folderId, FolderId destinationFolderId)
			throws Exception {
		MoveFolderRequest request = new MoveFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);

		request.setDestinationFolderId(destinationFolderId);
		request.getFolderIds().add(folderId);

		ServiceResponseCollection<MoveCopyFolderResponse> responses = request
				.execute();

		return responses.getResponseAtIndex(0).getFolder();
	}

	/**
	 * Finds folders.
	 * 
	 * @param parentFolderIds
	 *            The parent folder ids.
	 * @param searchFilter
	 *            The search filter. Available search filter classes include
	 *            SearchFilter.IsEqualTo, SearchFilter.ContainsSubstring and
	 *            SearchFilter.SearchFilterCollection
	 * @param view
	 *            The view controlling the number of folders returned.
	 * @param errorHandlingMode
	 *            Indicates the type of error handling should be done.
	 * @return Collection of service responses.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<FindFolderResponse> internalFindFolders(
			Iterable<FolderId> parentFolderIds, SearchFilter searchFilter,
			FolderView view, ServiceErrorHandling errorHandlingMode)
			throws Exception {
		FindFolderRequest request = new FindFolderRequest(this,
				errorHandlingMode);

		request.getParentFolderIds().addRangeFolderId(parentFolderIds);
		request.setSearchFilter(searchFilter);
		request.setView(view);

		return request.execute();

	}

	/**
	 * Obtains a list of folders by searching the sub-folders of the specified
	 * folder.
	 * 
	 * @param parentFolderId
	 *            The Id of the folder in which to search for folders.
	 * @param searchFilter
	 *            The search filter. Available search filter classes include
	 *            SearchFilter.IsEqualTo, SearchFilter.ContainsSubstring and
	 *            SearchFilter.SearchFilterCollection
	 * @param view
	 *            The view controlling the number of folders returned.
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindFoldersResults findFolders(FolderId parentFolderId,
			SearchFilter searchFilter, FolderView view) throws Exception {
		EwsUtilities.validateParam(parentFolderId, "parentFolderId");
		EwsUtilities.validateParam(view, "view");
		EwsUtilities.validateParamAllowNull(searchFilter, "searchFilter");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);
		ServiceResponseCollection<FindFolderResponse> responses = this
				.internalFindFolders(folderIdArray, searchFilter, view,
						ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getResults();
	}

	/**
	 * Obtains a list of folders by searching the sub-folders of the specified
	 * folder.
	 * 
	 * @param parentFolderId
	 *            The Id of the folder in which to search for folders.
	 * @param view
	 *            The view controlling the number of folders returned.
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindFoldersResults findFolders(FolderId parentFolderId,
			FolderView view) throws Exception {
		EwsUtilities.validateParam(parentFolderId, "parentFolderId");
		EwsUtilities.validateParam(view, "view");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindFolderResponse> responses = this
				.internalFindFolders(folderIdArray, null, /* searchFilter */
				view, ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getResults();
	}

	/**
	 * Obtains a list of folders by searching the sub-folders of the specified
	 * folder.
	 * 
	 * @param parentFolderName
	 *            The name of the folder in which to search for folders.
	 * @param searchFilter
	 *            The search filter. Available search filter classes include
	 *            SearchFilter.IsEqualTo, SearchFilter.ContainsSubstring and
	 *            SearchFilter.SearchFilterCollection
	 * @param view
	 *            The view controlling the number of folders returned.
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindFoldersResults findFolders(WellKnownFolderName parentFolderName,
			SearchFilter searchFilter, FolderView view) throws Exception {
		return this.findFolders(new FolderId(parentFolderName), searchFilter,
				view);
	}

	/**
	 * * Obtains a list of folders by searching the sub-folders of the specified
	 * folder.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindFoldersResults findFolders(WellKnownFolderName parentFolderName,
			FolderView view) throws Exception {
		return this.findFolders(new FolderId(parentFolderName), view);
	}

	/**
	 * Load specified properties for a folder.
	 * 
	 * @param folder
	 *            The folder
	 * @param propertySet
	 *            The property set
	 * @throws Exception
	 *             the exception
	 */
	protected void loadPropertiesForFolder(Folder folder,
			PropertySet propertySet) throws Exception {
		EwsUtilities.validateParam(folder, "folder");
		EwsUtilities.validateParam(propertySet, "propertySet");

		GetFolderRequestForLoad request = new GetFolderRequestForLoad(this,
				ServiceErrorHandling.ThrowOnError);

		request.getFolderIds().add(folder);
		request.setPropertySet(propertySet);

		request.execute();
	}

	/**
	 * Binds to a folder.
	 * 
	 * @param folderId
	 *            the folder id
	 * @param propertySet
	 *            the property set
	 * @return Folder
	 * @throws Exception
	 *             the exception
	 */
	protected Folder bindToFolder(FolderId folderId, PropertySet propertySet)
			throws Exception {
		EwsUtilities.validateParam(folderId, "folderId");
		EwsUtilities.validateParam(propertySet, "propertySet");

		GetFolderRequest request = new GetFolderRequest(this, ServiceErrorHandling.ThrowOnError);

		request.getFolderIds().add(folderId);
		request.setPropertySet(propertySet);

		ServiceResponseCollection<GetFolderResponse> responses = request.execute();

		return responses.getResponseAtIndex(0).getFolder();

	}

	/**
	 * Binds to folder.
	 * 
	 * @param <TFolder>
	 *            The type of the folder.
	 * @param cls
	 *            Folder class
	 * @param folderId
	 *            The folder id.
	 * @param propertySet
	 *            The property set.
	 * @return Folder
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	protected <TFolder extends Folder> TFolder bindToFolder(Class<TFolder> cls,
			FolderId folderId, PropertySet propertySet) throws Exception {

		Folder result = this.bindToFolder(folderId, propertySet);

		if (result instanceof Folder) {
			return (TFolder) result;
		} else {
			throw new ServiceLocalException(String.format("%s,%s,%s",
					Strings.FolderTypeNotCompatible, result.getClass().getName(), cls.getName()));
		}
	}

	/**
	 * Deletes a folder. Calling this method results in a call to EWS.
	 * 
	 * @param folderId
	 *            The folder id
	 * @param deleteMode
	 *            The delete mode
	 * @throws Exception
	 *             the exception
	 */
	protected void deleteFolder(FolderId folderId, DeleteMode deleteMode)
			throws Exception {
		EwsUtilities.validateParam(folderId, "folderId");

		DeleteFolderRequest request = new DeleteFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);

		request.getFolderIds().add(folderId);
		request.setDeleteMode(deleteMode);

		request.execute();
	}

	/**
	 * Empties a folder. Calling this method results in a call to EWS.
	 * 
	 * @param folderId
	 *            The folder id
	 * @param deleteMode
	 *            The delete mode
	 * @param deleteSubFolders
	 *            if set to <c>true</c> empty folder should also delete sub
	 *            folders.
	 * @throws Exception
	 *             the exception
	 */
	protected void emptyFolder(FolderId folderId, DeleteMode deleteMode,
			boolean deleteSubFolders) throws Exception {
		EwsUtilities.validateParam(folderId, "folderId");

		EmptyFolderRequest request = new EmptyFolderRequest(this,
				ServiceErrorHandling.ThrowOnError);

		request.getFolderIds().add(folderId);
		request.setDeleteMode(deleteMode);
		request.setDeleteSubFolders(deleteSubFolders);
		request.execute();
	}

	/**
	 * * Creates multiple items in a single EWS call. Supported item classes are
	 * EmailMessage, Appointment, Contact, PostItem, Task and Item. CreateItems
	 * does not support items that have unsaved attachments.
	 * 
	 * @param items
	 *            the items
	 * @param parentFolderId
	 *            the parent folder id
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsMode
	 *            the send invitations mode
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing creation results for each
	 *         of the specified items.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<ServiceResponse> internalCreateItems(
			Collection<Item> items, FolderId parentFolderId,
			MessageDisposition messageDisposition,
			SendInvitationsMode sendInvitationsMode,
			ServiceErrorHandling errorHandling) throws Exception {
		CreateItemRequest request = new CreateItemRequest(this, errorHandling);
		request.setParentFolderId(parentFolderId);
		request.setItems(items);
		request.setMessageDisposition(messageDisposition);
		request.setSendInvitationsMode(sendInvitationsMode);
		return request.execute();
	}

	/**
	 * * Creates multiple items in a single EWS call. Supported item classes are
	 * EmailMessage, Appointment, Contact, PostItem, Task and Item. CreateItems
	 * does not support items that have unsaved attachments.
	 * 
	 * @param items
	 *            the items
	 * @param parentFolderId
	 *            the parent folder id
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsMode
	 *            the send invitations mode
	 * @return A ServiceResponseCollection providing creation results for each
	 *         of the specified items.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<ServiceResponse> createItems(
			Collection<Item> items, FolderId parentFolderId,
			MessageDisposition messageDisposition,
			SendInvitationsMode sendInvitationsMode) throws Exception {
		// All items have to be new.
		if (!EwsUtilities.trueForAll(items, new IPredicate<Item>() {
			@Override
			public boolean predicate(Item obj) throws ServiceLocalException {
				return obj.isNew();
			}
		})) {
			throw new ServiceValidationException(
					Strings.CreateItemsDoesNotHandleExistingItems);
		}

		// E14:298274 Make sure that all items do *not* have unprocessed
		// attachments.
		if (!EwsUtilities.trueForAll(items, new IPredicate<Item>() {
			@Override
			public boolean predicate(Item obj) throws ServiceLocalException {
				return !obj.hasUnprocessedAttachmentChanges();
			}
		})) {
			throw new ServiceValidationException(
					Strings.CreateItemsDoesNotAllowAttachments);
		}
		return this.internalCreateItems(items, parentFolderId,
				messageDisposition, sendInvitationsMode,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * * Creates an item. Calling this method results in a call to EWS.
	 * 
	 * @param item
	 *            the item
	 * @param parentFolderId
	 *            the parent folder id
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsMode
	 *            the send invitations mode
	 * @throws Exception
	 *             the exception
	 */
	protected void createItem(Item item, FolderId parentFolderId,
			MessageDisposition messageDisposition,
			SendInvitationsMode sendInvitationsMode) throws Exception {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(item);
		internalCreateItems(items, parentFolderId, messageDisposition,
				sendInvitationsMode, ServiceErrorHandling.ThrowOnError);
	}

	/**
	 * * Updates multiple items in a single EWS call. UpdateItems does not
	 * support items that have unsaved attachments.
	 * 
	 * @param items
	 *            the items
	 * @param savedItemsDestinationFolderId
	 *            the saved items destination folder id
	 * @param conflictResolution
	 *            the conflict resolution
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsOrCancellationsMode
	 *            the send invitations or cancellations mode
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing update results for each of
	 *         the specified items.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<UpdateItemResponse> internalUpdateItems(
			Iterable<Item> items,
			FolderId savedItemsDestinationFolderId,
			ConflictResolutionMode conflictResolution,
			MessageDisposition messageDisposition,
			SendInvitationsOrCancellationsMode sendInvitationsOrCancellationsMode,
			ServiceErrorHandling errorHandling) throws Exception {
		UpdateItemRequest request = new UpdateItemRequest(this, errorHandling);

		request.getItems().addAll((Collection<? extends Item>) items);
		request.setSavedItemsDestinationFolder(savedItemsDestinationFolderId);
		request.setMessageDisposition(messageDisposition);
		request.setConflictResolutionMode(conflictResolution);
		request
				.setSendInvitationsOrCancellationsMode(sendInvitationsOrCancellationsMode);

		return request.execute();
	}

	/**
	 * * Updates multiple items in a single EWS call. UpdateItems does not
	 * support items that have unsaved attachments.
	 * 
	 * @param items
	 *            the items
	 * @param savedItemsDestinationFolderId
	 *            the saved items destination folder id
	 * @param conflictResolution
	 *            the conflict resolution
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsOrCancellationsMode
	 *            the send invitations or cancellations mode
	 * @return A ServiceResponseCollection providing update results for each of
	 *         the specified items.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<UpdateItemResponse> updateItems(
			Iterable<Item> items,
			FolderId savedItemsDestinationFolderId,
			ConflictResolutionMode conflictResolution,
			MessageDisposition messageDisposition,
			SendInvitationsOrCancellationsMode sendInvitationsOrCancellationsMode)
			throws Exception {

		// All items have to exist on the server (!new) and modified (dirty)
		if (!EwsUtilities.trueForAll(items, new IPredicate<Item>() {
			@Override
			public boolean predicate(Item obj) throws ServiceLocalException {
				return (!obj.isNew() && obj.isDirty());
			}
		})) {
			throw new ServiceValidationException(
					Strings.UpdateItemsDoesNotSupportNewOrUnchangedItems);
		}

		// E14:298274 Make sure that all items do *not* have unprocessed
		// attachments.
		if (!EwsUtilities.trueForAll(items, new IPredicate<Item>() {
			@Override
			public boolean predicate(Item obj) throws ServiceLocalException {
				return !obj.hasUnprocessedAttachmentChanges();
			}
		})) {
			throw new ServiceValidationException(
					Strings.UpdateItemsDoesNotAllowAttachments);
		}

		return this.internalUpdateItems(items, savedItemsDestinationFolderId,
				conflictResolution, messageDisposition,
				sendInvitationsOrCancellationsMode,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Updates an item.
	 * 
	 * @param item
	 *            the item
	 * @param savedItemsDestinationFolderId
	 *            the saved items destination folder id
	 * @param conflictResolution
	 *            the conflict resolution
	 * @param messageDisposition
	 *            the message disposition
	 * @param sendInvitationsOrCancellationsMode
	 *            the send invitations or cancellations mode
	 * @return A ServiceResponseCollection providing deletion results for each
	 *         of the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	protected Item updateItem(
			Item item,
			FolderId savedItemsDestinationFolderId,
			ConflictResolutionMode conflictResolution,
			MessageDisposition messageDisposition,
			SendInvitationsOrCancellationsMode sendInvitationsOrCancellationsMode)
			throws Exception {
		List<Item> itemIdArray = new ArrayList<Item>();
		itemIdArray.add(item);

		ServiceResponseCollection<UpdateItemResponse> responses = this
				.internalUpdateItems(itemIdArray,
						savedItemsDestinationFolderId, conflictResolution,
						messageDisposition, sendInvitationsOrCancellationsMode,
						ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getReturnedItem();
	}

	/**
	 * Send item.
	 * 
	 * @param item
	 *            the item
	 * @param savedCopyDestinationFolderId
	 *            the saved copy destination folder id
	 * @throws Exception
	 *             the exception
	 */
	protected void sendItem(Item item, FolderId savedCopyDestinationFolderId)
			throws Exception {
		SendItemRequest request = new SendItemRequest(this,
				ServiceErrorHandling.ThrowOnError);

		List<Item> itemIdArray = new ArrayList<Item>();
		itemIdArray.add(item);

		request.setItems(itemIdArray);
		request.setSavedCopyDestinationFolderId(savedCopyDestinationFolderId);

		request.execute();
	}

	/**
	 * Copies multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param destinationFolderId
	 *            the destination folder id
	 * @param returnNewItemIds
	 *            Flag indicating whether service should return new ItemIds or
	 *            not.
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<MoveCopyItemResponse> internalCopyItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId,
			Boolean returnNewItemIds, ServiceErrorHandling errorHandling)
			throws Exception {
		CopyItemRequest request = new CopyItemRequest(this, errorHandling);
		request.getItemIds().addRange(itemIds);
		request.setDestinationFolderId(destinationFolderId);
		request.setReturnNewItemIds(returnNewItemIds);
		return request.execute();

	}

	/**
	 * * Copies multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param destinationFolderId
	 *            the destination folder id
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<MoveCopyItemResponse> copyItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId)
			throws Exception {
		return this.internalCopyItems(itemIds, destinationFolderId, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Copies multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            The Ids of the items to copy.
	 * @param destinationFolderId
	 *            The Id of the folder to copy the items to.
	 * @param returnNewItemIds
	 *            Flag indicating whether service should return new ItemIds or
	 *            not.
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 */
	public ServiceResponseCollection<MoveCopyItemResponse> copyItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId,
			boolean returnNewItemIds) throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1, "CopyItems");

		return this.internalCopyItems(itemIds, destinationFolderId,
				returnNewItemIds, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Copies an item. Calling this method results in a call to EWS.
	 * 
	 * @param itemId
	 *            The Id of the item to copy.
	 * @param destinationFolderId
	 *            The folder in which to save sent messages, meeting invitations
	 *            or cancellations. If null, the message, meeting invitation or
	 *            cancellation is saved in the Sent Items folder
	 * @return The copy of the item.
	 * @throws Exception
	 *             the exception
	 */
	protected Item copyItem(ItemId itemId, FolderId destinationFolderId)
			throws Exception {
		List<ItemId> itemIdArray = new ArrayList<ItemId>();
		itemIdArray.add(itemId);

		return this.internalCopyItems(itemIdArray, destinationFolderId, null,
				ServiceErrorHandling.ThrowOnError).getResponseAtIndex(0)
				.getItem();
	}

	/**
	 * * Moves multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param destinationFolderId
	 *            the destination folder id
	 * @param returnNewItemIds
	 *            Flag indicating whether service should return new ItemIds or
	 *            not.
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<MoveCopyItemResponse> internalMoveItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId,
			Boolean returnNewItemIds, ServiceErrorHandling errorHandling)
			throws Exception {
		MoveItemRequest request = new MoveItemRequest(this, errorHandling);

		request.getItemIds().addRange(itemIds);
		request.setDestinationFolderId(destinationFolderId);
		request.setReturnNewItemIds(returnNewItemIds);
		return request.execute();
	}

	/**
	 * * Moves multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param destinationFolderId
	 *            the destination folder id
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<MoveCopyItemResponse> moveItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId)
			throws Exception {
		return this.internalMoveItems(itemIds, destinationFolderId, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Moves multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            The Ids of the items to move.
	 * @param destinationFolderId
	 *            The Id of the folder to move the items to.
	 * @param returnNewItemIds
	 *            Flag indicating whether service should return new ItemIds or
	 *            not.
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 */
	public ServiceResponseCollection<MoveCopyItemResponse> moveItems(
			Iterable<ItemId> itemIds, FolderId destinationFolderId,
			boolean returnNewItemIds) throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1, "MoveItems");

		return this.internalMoveItems(itemIds, destinationFolderId,
				returnNewItemIds, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Copies multiple items in a single call to EWS.
	 * 
	 * @param itemId
	 *            the item id
	 * @param destinationFolderId
	 *            the destination folder id
	 * @return A ServiceResponseCollection providing copy results for each of
	 *         the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	protected Item moveItem(ItemId itemId, FolderId destinationFolderId)
			throws Exception {
		List<ItemId> itemIdArray = new ArrayList<ItemId>();
		itemIdArray.add(itemId);

		return this.internalMoveItems(itemIdArray, destinationFolderId, null,
				ServiceErrorHandling.ThrowOnError).getResponseAtIndex(0)
				.getItem();
	}

	/**
	 * Finds items.
	 * 
	 * @param <TItem>
	 *            The type of item
	 * @param parentFolderIds
	 *            The parent folder ids.
	 * @param searchFilter
	 *            The search filter. Available search filter classes include
	 *            SearchFilter.IsEqualTo, SearchFilter.ContainsSubstring and
	 *            SearchFilter.SearchFilterCollection
	 * @param queryString
	 *            the query string
	 * @param view
	 *            The view controlling the number of folders returned.
	 * @param groupBy
	 *            The group by.
	 * @param errorHandlingMode
	 *            Indicates the type of error handling should be done.
	 * @return Service response collection.
	 * @throws Exception
	 *             the exception
	 */
	protected <TItem extends Item> ServiceResponseCollection<FindItemResponse<TItem>> findItems(
			Iterable<FolderId> parentFolderIds, SearchFilter searchFilter,
			String queryString, ViewBase view, Grouping groupBy,
			ServiceErrorHandling errorHandlingMode) throws Exception {
		EwsUtilities.validateParamCollection(parentFolderIds.iterator(),
				"parentFolderIds");
		EwsUtilities.validateParam(view, "view");
		EwsUtilities.validateParamAllowNull(groupBy, "groupBy");
		EwsUtilities.validateParamAllowNull(queryString, "queryString");
		EwsUtilities.validateParamAllowNull(searchFilter, "searchFilter");

		FindItemRequest<TItem> request = new FindItemRequest<TItem>(this,
				errorHandlingMode);

		request.getParentFolderIds().addRangeFolderId(parentFolderIds);
		request.setSearchFilter(searchFilter);
		request.setQueryString(queryString);
		request.setView(view);
		request.setGroupBy(groupBy);

		return request.execute();
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param queryString
	 *            the query string
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(FolderId parentFolderId,
			String queryString, ItemView view) throws Exception {
		EwsUtilities.validateParamAllowNull(queryString, "queryString");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, null, /* searchFilter */
				queryString, view, null, /* groupBy */
				ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getResults();
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param searchFilter
	 *            the search filter
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(FolderId parentFolderId,
			SearchFilter searchFilter, ItemView view) throws Exception {
		EwsUtilities.validateParamAllowNull(searchFilter, "searchFilter");
		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);
		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, searchFilter, null, /* queryString */
				view, null, /* groupBy */
				ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getResults();
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(FolderId parentFolderId,
			ItemView view) throws Exception {
		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);
		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, null, /* searchFilter */
				null, /* queryString */
				view, null, /* groupBy */
				ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getResults();
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param queryString
	 *            the query string
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(
			WellKnownFolderName parentFolderName, String queryString,
			ItemView view) throws Exception {
		return this
				.findItems(new FolderId(parentFolderName), queryString, view);
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param searchFilter
	 *            the search filter
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(
			WellKnownFolderName parentFolderName, SearchFilter searchFilter,
			ItemView view) throws Exception {
		return this.findItems(new FolderId(parentFolderName), searchFilter,
				view);
	}

	/**
	 * Obtains a list of items by searching the contents of a specific folder.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param view
	 *            the view
	 * @return An object representing the results of the search operation.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Item> findItems(
			WellKnownFolderName parentFolderName, ItemView view)
			throws Exception {
		return this.findItems(new FolderId(parentFolderName),
				(SearchFilter) null, view);
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param queryString
	 *            the query string
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A list of items containing the contents of the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public GroupedFindItemsResults<Item> findItems(FolderId parentFolderId,
			String queryString, ItemView view, Grouping groupBy)
			throws Exception {
		EwsUtilities.validateParam(groupBy, "groupBy");
		EwsUtilities.validateParamAllowNull(queryString, "queryString");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, null, /* searchFilter */
				queryString, view, groupBy, ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getGroupedFindResults();
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param searchFilter
	 *            the search filter
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A list of items containing the contents of the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public GroupedFindItemsResults<Item> findItems(FolderId parentFolderId,
			SearchFilter searchFilter, ItemView view, Grouping groupBy)
			throws Exception {
		EwsUtilities.validateParam(groupBy, "groupBy");
		EwsUtilities.validateParamAllowNull(searchFilter, "searchFilter");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, searchFilter, null, /* queryString */
				view, groupBy, ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getGroupedFindResults();
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A list of items containing the contents of the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public GroupedFindItemsResults<Item> findItems(FolderId parentFolderId,
			ItemView view, Grouping groupBy) throws Exception {
		EwsUtilities.validateParam(groupBy, "groupBy");

		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindItemResponse<Item>> responses = this
				.findItems(folderIdArray, null, /* searchFilter */
				null, /* queryString */
				view, groupBy, ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getGroupedFindResults();
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param <TItem>
	 *            the generic type
	 * @param cls
	 *            the cls
	 * @param parentFolderId
	 *            the parent folder id
	 * @param searchFilter
	 *            the search filter
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A list of items containing the contents of the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	protected <TItem extends Item> ServiceResponseCollection<FindItemResponse<TItem>> findItems(
			Class<TItem> cls, FolderId parentFolderId,
			SearchFilter searchFilter, ViewBase view, Grouping groupBy)
			throws Exception {
		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		return this.findItems(folderIdArray, searchFilter, null, /* queryString */
		view, groupBy, ServiceErrorHandling.ThrowOnError);
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param queryString
	 *            the query string
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A collection of grouped items containing the contents of the
	 *         specified.
	 * @throws Exception
	 *             the exception
	 */
	public GroupedFindItemsResults<Item> findItems(
			WellKnownFolderName parentFolderName, String queryString,
			ItemView view, Grouping groupBy) throws Exception {
		EwsUtilities.validateParam(groupBy, "groupBy");
		return this.findItems(new FolderId(parentFolderName), queryString,
				view, groupBy);
	}

	/**
	 * Obtains a grouped list of items by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param searchFilter
	 *            the search filter
	 * @param view
	 *            the view
	 * @param groupBy
	 *            the group by
	 * @return A collection of grouped items containing the contents of the
	 *         specified.
	 * @throws Exception
	 *             the exception
	 */
	public GroupedFindItemsResults<Item> findItems(
			WellKnownFolderName parentFolderName, SearchFilter searchFilter,
			ItemView view, Grouping groupBy) throws Exception {
		return this.findItems(new FolderId(parentFolderName), searchFilter,
				view, groupBy);
	}

	/**
	 * * Obtains a list of appointments by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderId
	 *            the parent folder id
	 * @param calendarView
	 *            the calendar view
	 * @return A collection of appointments representing the contents of the
	 *         specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Appointment> findAppointments(
			FolderId parentFolderId, CalendarView calendarView)
			throws Exception {
		List<FolderId> folderIdArray = new ArrayList<FolderId>();
		folderIdArray.add(parentFolderId);

		ServiceResponseCollection<FindItemResponse<Appointment>> response = this
				.findItems(folderIdArray, null, /* searchFilter */
				null /* queryString */, calendarView, null, /* groupBy */
				ServiceErrorHandling.ThrowOnError);

		return response.getResponseAtIndex(0).getResults();
	}

	/**
	 * * Obtains a list of appointments by searching the contents of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param parentFolderName
	 *            the parent folder name
	 * @param calendarView
	 *            the calendar view
	 * @return A collection of appointments representing the contents of the
	 *         specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public FindItemsResults<Appointment> findAppointments(
			WellKnownFolderName parentFolderName, CalendarView calendarView)
			throws Exception {
		return this.findAppointments(new FolderId(parentFolderName),
				calendarView);
	}

	/**
	 * * Loads the properties of multiple items in a single call to EWS.
	 * 
	 * @param items
	 *            the items
	 * @param propertySet
	 *            the property set
	 * @return A ServiceResponseCollection providing results for each of the
	 *         specified items.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<ServiceResponse> loadPropertiesForItems(
			Iterable<Item> items, PropertySet propertySet) throws Exception {
		EwsUtilities.validateParamCollection(items.iterator(), "items");
		EwsUtilities.validateParam(propertySet, "propertySet");

		return this.internalLoadPropertiesForItems(items, propertySet,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Loads the properties of multiple items in a single call to EWS.
	 * 
	 * @param items
	 *            the items
	 * @param propertySet
	 *            the property set
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing results for each of the
	 *         specified items.
	 * @throws Exception
	 *             the exception
	 */
	ServiceResponseCollection<ServiceResponse> internalLoadPropertiesForItems(
			Iterable<Item> items, PropertySet propertySet,
			ServiceErrorHandling errorHandling) throws Exception {
		GetItemRequestForLoad request = new GetItemRequestForLoad(this,
				errorHandling);
		// return null;

		request.getItemIds().addRangeItem(items);
		request.setPropertySet(propertySet);

		return request.execute();
	}

	/**
	 * * Binds to multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param propertySet
	 *            the property set
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing results for each of the
	 *         specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<GetItemResponse> internalBindToItems(
			Iterable<ItemId> itemIds, PropertySet propertySet,
			ServiceErrorHandling errorHandling) throws Exception {
		GetItemRequest request = new GetItemRequest(this, errorHandling);
		request.getItemIds().addRange(itemIds);
		request.setPropertySet(propertySet);
		return request.execute();
	}

	/**
	 * * Binds to multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param propertySet
	 *            the property set
	 * @return A ServiceResponseCollection providing results for each of the
	 *         specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<GetItemResponse> bindToItems(
			Iterable<ItemId> itemIds, PropertySet propertySet) throws Exception {
		EwsUtilities.validateParamCollection(itemIds.iterator(), "itemIds");
		EwsUtilities.validateParam(propertySet, "propertySet");

		return this.internalBindToItems(itemIds, propertySet,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * * Binds to multiple items in a single call to EWS.
	 * 
	 * @param itemId
	 *            the item id
	 * @param propertySet
	 *            the property set
	 * @return A ServiceResponseCollection providing results for each of the
	 *         specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	protected Item bindToItem(ItemId itemId, PropertySet propertySet)
			throws Exception {
		EwsUtilities.validateParam(itemId, "itemId");
		EwsUtilities.validateParam(propertySet, "propertySet");
		List<ItemId> itmLst = new ArrayList<ItemId>();
		itmLst.add(itemId);
		ServiceResponseCollection<GetItemResponse> responses = this
				.internalBindToItems(itmLst, propertySet,
						ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getItem();
	}

	/**
	 * Bind to item.
	 * 
	 * @param <TItem>
	 *            The type of the item.
	 * @param c
	 *            the c
	 * @param itemId
	 *            the item id
	 * @param propertySet
	 *            the property set
	 * @return the t item
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	protected <TItem extends Item> TItem bindToItem(Class<TItem> c,
			ItemId itemId, PropertySet propertySet) throws Exception {
		Item result = this.bindToItem(itemId, propertySet);
		if (result instanceof Item) {
			return (TItem) result;
		} else {
			throw new ServiceLocalException(String.format(
					Strings.ItemTypeNotCompatible, result.getClass().getName(), c.getName()));
		}
		// return (TItem)result;
	}

	/**
	 * Deletes multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param deleteMode
	 *            the delete mode
	 * @param sendCancellationsMode
	 *            the send cancellations mode
	 * @param affectedTaskOccurrences
	 *            the affected task occurrences
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing deletion results for each
	 *         of the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<ServiceResponse> internalDeleteItems(
			Iterable<ItemId> itemIds, DeleteMode deleteMode,
			SendCancellationsMode sendCancellationsMode,
			AffectedTaskOccurrence affectedTaskOccurrences,
			ServiceErrorHandling errorHandling) throws Exception {
		DeleteItemRequest request = new DeleteItemRequest(this, errorHandling);

		request.getItemIds().addRange(itemIds);
		request.setDeleteMode(deleteMode);
		request.setSendCancellationsMode(sendCancellationsMode);
		request.setAffectedTaskOccurrences(affectedTaskOccurrences);

		return request.execute();
	}

	/**
	 * Deletes multiple items in a single call to EWS.
	 * 
	 * @param itemIds
	 *            the item ids
	 * @param deleteMode
	 *            the delete mode
	 * @param sendCancellationsMode
	 *            the send cancellations mode
	 * @param affectedTaskOccurrences
	 *            the affected task occurrences
	 * @return A ServiceResponseCollection providing deletion results for each
	 *         of the specified item Ids.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<ServiceResponse> deleteItems(
			Iterable<ItemId> itemIds, DeleteMode deleteMode,
			SendCancellationsMode sendCancellationsMode,
			AffectedTaskOccurrence affectedTaskOccurrences) throws Exception {
		EwsUtilities.validateParamCollection(itemIds.iterator(), "itemIds");

		return this.internalDeleteItems(itemIds, deleteMode,
				sendCancellationsMode, affectedTaskOccurrences,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Deletes an item. Calling this method results in a call to EWS.
	 * 
	 * @param itemId
	 *            the item id
	 * @param deleteMode
	 *            the delete mode
	 * @param sendCancellationsMode
	 *            the send cancellations mode
	 * @param affectedTaskOccurrences
	 *            the affected task occurrences
	 * @throws Exception
	 *             the exception
	 */
	protected void deleteItem(ItemId itemId, DeleteMode deleteMode,
			SendCancellationsMode sendCancellationsMode,
			AffectedTaskOccurrence affectedTaskOccurrences) throws Exception {
		List<ItemId> itemIdArray = new ArrayList<ItemId>();
		itemIdArray.add(itemId);

		EwsUtilities.validateParam(itemId, "itemId");
		this.internalDeleteItems(itemIdArray, deleteMode,
				sendCancellationsMode, affectedTaskOccurrences,
				ServiceErrorHandling.ThrowOnError);
	}

	/**
	 * Gets an attachment.
	 * 
	 * @param attachments
	 *            the attachments
	 * @param bodyType
	 *            the body type
	 * @param additionalProperties
	 *            the additional properties
	 * @param errorHandling
	 *            the error handling
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<GetAttachmentResponse> internalGetAttachments(
			Iterable<Attachment> attachments, BodyType bodyType,
			Iterable<PropertyDefinitionBase> additionalProperties,
			ServiceErrorHandling errorHandling) throws Exception {
		
		GetAttachmentRequest request = new GetAttachmentRequest(this, errorHandling);

		Iterator<Attachment> it = attachments.iterator();
		while (it.hasNext()) {
			request.getAttachments().add(it.next());
		}
		request.setBodyType(bodyType);

		List propsArray = new ArrayList();
		propsArray.add(additionalProperties);

		if (additionalProperties != null) {
			request.getAdditionalProperties().addAll(propsArray);
		}

		return request.execute();
	}

	/**
	 * Gets attachments.
	 * 
	 * @param attachments
	 *            the attachments
	 * @param bodyType
	 *            the body type
	 * @param additionalProperties
	 *            the additional properties
	 * @return Service response collection.
	 * @throws Exception
	 */
	protected ServiceResponseCollection<GetAttachmentResponse> getAttachments(
			Attachment[] attachments, BodyType bodyType,
			Iterable<PropertyDefinitionBase> additionalProperties)
			throws Exception {
		List<Attachment> attList = new ArrayList<Attachment>();
		for (Attachment attachment : attachments) {
			attList.add(attachment);
		}
		return this.internalGetAttachments(attList, bodyType,
				additionalProperties, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Gets the attachment.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param bodyType
	 *            the body type
	 * @param additionalProperties
	 *            the additional properties
	 * @throws Exception
	 *             the exception
	 */
	protected void getAttachment(Attachment attachment, BodyType bodyType,
			Iterable<PropertyDefinitionBase> additionalProperties)
			throws Exception {

		List<Attachment> attachmentArray = new ArrayList<Attachment>();
		attachmentArray.add(attachment);

		this.internalGetAttachments(attachmentArray, bodyType,
				additionalProperties, ServiceErrorHandling.ThrowOnError);

	}

	/**
	 * * Creates attachments.
	 * 
	 * @param parentItemId
	 *            the parent item id
	 * @param attachments
	 *            the attachments
	 * @return Service response collection.
	 * @throws ServiceResponseException
	 *             the service response exception
	 * @throws Exception
	 *             the exception
	 */
	protected ServiceResponseCollection<CreateAttachmentResponse> createAttachments(
			String parentItemId, Iterable<Attachment> attachments)
			throws ServiceResponseException, Exception {
		CreateAttachmentRequest request = new CreateAttachmentRequest(this,
				ServiceErrorHandling.ReturnErrors);

		request.setParentItemId(parentItemId);
		/*
		 * if (null != attachments) { while (attachments.hasNext()) {
		 * request.getAttachments().add(attachments.next()); } }
		 */
		request.getAttachments().addAll(
				(Collection<? extends Attachment>) attachments);

		return request.execute();
	}

	/**
	 * * Deletes attachments.
	 * 
	 * @param attachments
	 *            the attachments
	 * @return the service response collection
	 * @throws ServiceResponseException
	 *             the service response exception
	 * @throws Exception
	 *             the exception
	 */
	protected ServiceResponseCollection<DeleteAttachmentResponse> deleteAttachments(
			Iterable<Attachment> attachments) throws ServiceResponseException,
			Exception {
		DeleteAttachmentRequest request = new DeleteAttachmentRequest(this,
				ServiceErrorHandling.ReturnErrors);

		request.getAttachments().addAll(
				(Collection<? extends Attachment>) attachments);

		return request.execute();
	}

	/**
	 * * Finds contacts in the user's contacts folder and the Global Address
	 * List (in that order) that have names that match the one passed as a
	 * parameter. Calling this method results in a call to EWS.
	 * 
	 * @param nameToResolve
	 *            the name to resolve
	 * @return A collection of name resolutions whose names match the one passed
	 *         as a parameter.
	 * @throws Exception
	 *             the exception
	 */
	public NameResolutionCollection resolveName(String nameToResolve)
			throws Exception {
		return this.resolveName(nameToResolve,
				ResolveNameSearchLocation.ContactsThenDirectory, false);
	}

	/**
	 * * Finds contacts in the user's contacts folder and the Global Address
	 * List (in that order) that have names that match the one passed as a
	 * parameter. Calling this method results in a call to EWS.
	 * 
	 * @param nameToResolve
	 *            the name to resolve
	 * @param parentFolderIds
	 *            the parent folder ids
	 * @param searchScope
	 *            the search scope
	 * @param returnContactDetails
	 *            the return contact details
	 * @return A collection of name resolutions whose names match the one passed
	 *         as a parameter.
	 * @throws Exception
	 *             the exception
	 */
	public NameResolutionCollection resolveName(String nameToResolve,
			Iterable<FolderId> parentFolderIds,
			ResolveNameSearchLocation searchScope, boolean returnContactDetails)
			throws Exception {
		return resolveName(nameToResolve, parentFolderIds, searchScope,
				returnContactDetails, null);

	}

	/**
	 * Finds contacts in the Global address List and/or in specific contact
	 * folders that have names that match the one passed as a parameter. Calling
	 * this method results in a call to EWS.
	 * 
	 * @param nameToResolve
	 *            The name to resolve.
	 *@param parentFolderIds
	 *            The Ids of the contact folders in which to look for matching
	 *            contacts.
	 *@param searchScope
	 *            The scope of the search.
	 *@param returnContactDetails
	 *            Indicates whether full contact information should be returned
	 *            for each of the found contacts.
	 *@param contactDataPropertySet
	 *            The property set for the contact details
	 * @throws Exception
	 * @returns A collection of name resolutions whose names match the one
	 *          passed as a parameter.
	 */
	public NameResolutionCollection resolveName(String nameToResolve,
			Iterable<FolderId> parentFolderIds,
			ResolveNameSearchLocation searchScope,
			boolean returnContactDetails, PropertySet contactDataPropertySet)
			throws Exception {
		if (contactDataPropertySet != null) {
			EwsUtilities.validateMethodVersion(this,
					ExchangeVersion.Exchange2010_SP1, "ResolveName");
		}

		EwsUtilities.validateParam(nameToResolve, "nameToResolve");

		if (parentFolderIds != null) {
			EwsUtilities.validateParamCollection(parentFolderIds.iterator(),
					"parentFolderIds");
		}
		ResolveNamesRequest request = new ResolveNamesRequest(this);

		request.setNameToResolve(nameToResolve);
		request.setReturnFullContactData(returnContactDetails);
		request.getParentFolderIds().addRangeFolderId(parentFolderIds);
		request.setSearchLocation(searchScope);
		request.setContactDataPropertySet(contactDataPropertySet);

		return request.execute().getResponseAtIndex(0).getResolutions();
	}

	/**
	 * Finds contacts in the Global address List that have names that match the
	 * one passed as a parameter. Calling this method results in a call to EWS.
	 * 
	 * @param nameToResolve
	 *            The name to resolve.
	 *@param searchScope
	 *            The scope of the search.
	 *@param returnContactDetails
	 *            Indicates whether full contact information should be returned
	 *            for each of the found contacts.
	 *@param contactDataPropertySet
	 *            The property set for the contact details
	 * @throws Exception
	 * @returns A collection of name resolutions whose names match the one
	 *          passed as a parameter.
	 */
	public NameResolutionCollection resolveName(String nameToResolve,
			ResolveNameSearchLocation searchScope,
			boolean returnContactDetails, PropertySet contactDataPropertySet)
			throws Exception {
		return this.resolveName(nameToResolve, null, searchScope,
				returnContactDetails, contactDataPropertySet);
	}

	/**
	 * * Finds contacts in the user's contacts folder and the Global Address
	 * List (in that order) that have names that match the one passed as a
	 * parameter. Calling this method results in a call to EWS.
	 * 
	 * @param nameToResolve
	 *            the name to resolve
	 * @param searchScope
	 *            the search scope
	 * @param returnContactDetails
	 *            the return contact details
	 * @return A collection of name resolutions whose names match the one passed
	 *         as a parameter.
	 * @throws Exception
	 *             the exception
	 */
	public NameResolutionCollection resolveName(String nameToResolve,
			ResolveNameSearchLocation searchScope, boolean returnContactDetails)
			throws Exception {
		return this.resolveName(nameToResolve, null, searchScope,
				returnContactDetails);
	}

	/**
	 * * Expands a group by retrieving a list of its members. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param emailAddress
	 *            the email address
	 * @return URL of the Exchange Web Services.
	 * @throws Exception
	 *             the exception
	 */
	public ExpandGroupResults expandGroup(EmailAddress emailAddress)
			throws Exception {
		EwsUtilities.validateParam(emailAddress, "emailAddress");
		ExpandGroupRequest request = new ExpandGroupRequest(this);
		request.setEmailAddress(emailAddress);
		return request.execute().getResponseAtIndex(0).getMembers();
	}

	/**
	 * * Expands a group by retrieving a list of its members. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param groupId
	 *            the group id
	 * @return An ExpandGroupResults containing the members of the group.
	 * @throws Exception
	 *             the exception
	 */
	public ExpandGroupResults expandGroup(ItemId groupId) throws Exception {
		EwsUtilities.validateParam(groupId, "groupId");
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setId(groupId);
		return this.expandGroup(emailAddress);
	}

	/**
	 * * Expands a group by retrieving a list of its members. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param smtpAddress
	 *            the smtp address
	 * @return An ExpandGroupResults containing the members of the group.
	 * @throws Exception
	 *             the exception
	 */
	public ExpandGroupResults expandGroup(String smtpAddress) throws Exception {
		EwsUtilities.validateParam(smtpAddress, "smtpAddress");
		return this.expandGroup(new EmailAddress(smtpAddress));
	}

	/**
	 * * Expands a group by retrieving a list of its members. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param address
	 *            the address
	 * @param routingType
	 *            the routing type
	 * @return An ExpandGroupResults containing the members of the group.
	 * @throws Exception
	 *             the exception
	 */
	public ExpandGroupResults expandGroup(String address, String routingType)
			throws Exception {
		EwsUtilities.validateParam(address, "address");
		EwsUtilities.validateParam(routingType, "routingType");

		EmailAddress emailAddress = new EmailAddress(address);
		emailAddress.setRoutingType(routingType);
		return this.expandGroup(emailAddress);
	}

	/**
	 * Get the password expiration date
	 * 
	 * @param mailboxSmtpAddress
	 *            The e-mail address of the user.
	 * @throws Exception
	 * @returns The password expiration date
	 */
	public Date getPasswordExpirationDate(String mailboxSmtpAddress)
			throws Exception {
		GetPasswordExpirationDateRequest request = new GetPasswordExpirationDateRequest(
				this);
		request.setMailboxSmtpAddress(mailboxSmtpAddress);

		return request.execute().getPasswordExpirationDate();
	}

	/**
	 * Subscribes to pull notifications. Calling this method results in a call
	 * to EWS.
	 * 
	 * @param folderIds
	 *            The Ids of the folder to subscribe to
	 * @param timeout
	 *            The timeout, in minutes, after which the subscription expires.
	 *            Timeout must be between 1 and 1440.
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription.
	 * @param EventType
	 *            The event types to subscribe to.
	 * @return A PullSubscription representing the new subscription.
	 * @throws Exception
	 */

	public PullSubscription subscribeToPullNotifications(
			Iterable<FolderId> folderIds, int timeout, String watermark,
			EventType... eventTypes) throws Exception {
		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToPullNotificationsRequest(folderIds,
				timeout, watermark, eventTypes).execute().getResponseAtIndex(0)
				.getSubscription();
	}

	/**
	 * Begins an asynchronous request to subscribes to pull notifications.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate.
	 * @param state
	 *            An object that contains state information for this request.
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param timeout
	 *            The timeout, in minutes, after which the subscription expires.
	 *            Timeout must be between 1 and 1440.
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription.
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @throws Exception 
	 * @returns An IAsyncResult that references the asynchronous request.
	 */
	public AsyncRequestResult beginSubscribeToPullNotifications(
			AsyncCallback callback, Object state, Iterable<FolderId> folderIds,
			int timeout, String watermark, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToPullNotificationsRequest(folderIds,
				timeout, watermark, eventTypes).beginExecute(callback, null);
	}

	/**
	 * * Subscribes to pull notifications on all folders in the authenticated
	 * user's mailbox. Calling this method results in a call to EWS.
	 * 
	 * @param timeout
	 *            the timeout
	 * @param watermark
	 *            the watermark
	 * @param eventTypes
	 *            the event types
	 * @return A PullSubscription representing the new subscription.
	 * @throws Exception
	 *             the exception
	 */
	public PullSubscription subscribeToPullNotificationsOnAllFolders(
			int timeout, String watermark, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateMethodVersion(this, ExchangeVersion.Exchange2010,
				"SubscribeToPullNotificationsOnAllFolders");

		return this.buildSubscribeToPullNotificationsRequest(null, timeout,
				watermark, eventTypes).execute().getResponseAtIndex(0)
				.getSubscription();
	}

	/**
	 * Begins an asynchronous request to subscribe to pull notifications on all
	 * folders in the authenticated user's mailbox. Calling this method results
	 * in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate.
	 * @param state
	 *            An object that contains state information for this request.
	 * @param timeout
	 *            The timeout, in minutes, after which the subscription expires.
	 *            Timeout must be between 1 and 1440.</param>
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription.
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @throws Exception 
	 * @returns An IAsyncResult that references the asynchronous request.
	 */
	public IAsyncResult beginSubscribeToPullNotificationsOnAllFolders(AsyncCallback callback,Object state,
			 int timeout,
			String watermark, EventType... eventTypes) throws Exception {
		EwsUtilities.validateMethodVersion(this, ExchangeVersion.Exchange2010,
				"BeginSubscribeToPullNotificationsOnAllFolders");

		return this.buildSubscribeToPullNotificationsRequest(null, timeout,
				watermark, eventTypes).beginExecute(null,null);
	}

	/**
	 * Ends an asynchronous request to subscribe to pull notifications in the
	 * authenticated user's mailbox.
	 * 
	 * @param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 *@returns A PullSubscription representing the new subscription.
	 */
	public PullSubscription endSubscribeToPullNotifications(
			IAsyncResult asyncResult) throws Exception {
		SubscribeToPullNotificationsRequest request = AsyncRequestResult
				.extractServiceRequest(this, asyncResult);

		return request.endExecute(asyncResult).getResponseAtIndex(0)
				.getSubscription();
	}

	/**
	 * * Builds a request to subscribe to pull notifications in the
	 * authenticated user's mailbox.
	 * 
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param timeout
	 *            The timeout, in minutes, after which the subscription expires.
	 *            Timeout must be between 1 and 1440
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription
	 * @param eventTypes
	 *            The event types to subscribe to
	 * @return A request to subscribe to pull notifications in the authenticated
	 *         user's mailbox
	 * @throws Exception
	 *             the exception
	 */
	private SubscribeToPullNotificationsRequest buildSubscribeToPullNotificationsRequest(
			Iterable<FolderId> folderIds, int timeout, String watermark,
			EventType... eventTypes) throws Exception {
		if (timeout < 1 || timeout > 1440) {
			throw new IllegalArgumentException("timeout", new Throwable(
					Strings.TimeoutMustBeBetween1And1440));
		}

		EwsUtilities.validateParamCollection(eventTypes, "eventTypes");

		SubscribeToPullNotificationsRequest request = new SubscribeToPullNotificationsRequest(
				this);

		if (folderIds != null) {
			request.getFolderIds().addRangeFolderId(folderIds);
		}

		request.setTimeOut(timeout);

		for (EventType event : eventTypes) {
			request.getEventTypes().add(event);
		}

		request.setWatermark(watermark);

		return request;
	}

	/**
	 * Unsubscribes from a pull subscription. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param subscriptionId
	 *            the subscription id
	 * @throws Exception
	 *             the exception
	 */
	protected void unsubscribe(String subscriptionId) throws Exception {

		this.buildUnsubscribeRequest(subscriptionId).execute();
	}

	/**
	 * Begins an asynchronous request to unsubscribe from a subscription.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate.
	 * @param state
	 *            An object that contains state information for this request.
	 * @param subscriptionId
	 *            The Id of the pull subscription to unsubscribe from.
	 * @throws Exception 
	 * @returns An IAsyncResult that references the asynchronous request.
	 **/
	protected IAsyncResult beginUnsubscribe(AsyncCallback callback,Object state,String subscriptionId) throws Exception {
		return this.buildUnsubscribeRequest(subscriptionId).beginExecute(
				callback,null);
	}

	/**
	 * Ends an asynchronous request to unsubscribe from a subscription.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 **/
	protected void endUnsubscribe(IAsyncResult asyncResult) throws Exception
    {
    	UnsubscribeRequest request = AsyncRequestResult.extractServiceRequest(this, asyncResult);

        request.endExecute(asyncResult);
    }

	/**
	 * Buids a request to unsubscribe from a subscription.
	 * 
	 * @param subscriptionId
	 *            The id of the subscription for which to get the events
	 * @return A request to unsubscripbe from a subscription
	 * @throws Exception
	 */
	private UnsubscribeRequest buildUnsubscribeRequest(String subscriptionId)
			throws Exception {
		EwsUtilities.validateParam(subscriptionId, "subscriptionId");

		UnsubscribeRequest request = new UnsubscribeRequest(this);

		request.setSubscriptionId(subscriptionId);

		return request;
	}

	/**
	 * * Retrieves the latests events associated with a pull subscription.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param subscriptionId
	 *            the subscription id
	 * @param waterMark
	 *            the water mark
	 * @return A GetEventsResults containing a list of events associated with
	 *         the subscription.
	 * @throws Exception
	 *             the exception
	 */
	protected GetEventsResults getEvents(String subscriptionId, String waterMark)
			throws Exception {

		return this.buildGetEventsRequest(subscriptionId, waterMark).execute()
				.getResponseAtIndex(0).getResults();
	}

	/**
	 * Begins an asynchronous request to retrieve the latest events associated
	 * with a pull subscription. Calling this method results in a call to EWS.
	 * 
	 *@param callback
	 *            The AsyncCallback delegate.
	 *@param state
	 *            An object that contains state information for this request.
	 * @param subscriptionId
	 *            The id of the pull subscription for which to get the events
	 * @param watermark
	 *            The watermark representing the point in time where to start
	 *            receiving events
	 * @return An IAsynResult that references the asynchronous request
	 * @throws Exception 
	 */
	protected IAsyncResult beginGetEvents(AsyncCallback callback, Object state,
			String subscriptionId, String watermark) throws Exception {
		return this.buildGetEventsRequest(subscriptionId, watermark)
				.beginExecute(callback, null);
	}

	/**
	 * Ends an asynchronous request to retrieve the latest events associated
	 * with a pull subscription.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 *@returns A GetEventsResults containing a list of events associated with
	 *          the subscription.
	 **/

	protected GetEventsResults endGetEvents(IAsyncResult asyncResult) throws Exception
    {
    	GetEventsRequest request = AsyncRequestResult.extractServiceRequest(this,asyncResult);

        return request.endExecute(asyncResult).getResponseAtIndex(0).getResults();
    }

	/**
	 * Builds a request to retrieve the letest events associated with a pull
	 * subscription
	 * 
	 * @param subscriptionId
	 *            The Id of the pull subscription for which to get the events
	 * @param watermark
	 *            The watermark representing the point in time where to start
	 *            receiving events
	 * @return An request to retrieve the latest events associated with a pull
	 *         subscription
	 * @throws Exception
	 */
	private GetEventsRequest buildGetEventsRequest(String subscriptionId,
			String watermark) throws Exception {
		EwsUtilities.validateParam(subscriptionId, "subscriptionId");
		EwsUtilities.validateParam(watermark, "watermark");

		GetEventsRequest request = new GetEventsRequest(this);

		request.setSubscriptionId(subscriptionId);
		request.setWatermark(watermark);

		return request;
	}

	/**
	 * * Subscribes to push notifications. Calling this method results in a call
	 * to EWS.
	 * 
	 * @param folderIds
	 *            the folder ids
	 * @param url
	 *            the url
	 * @param frequency
	 *            the frequency
	 * @param watermark
	 *            the watermark
	 * @param eventTypes
	 *            the event types
	 * @return A PushSubscription representing the new subscription.
	 * @throws Exception
	 *             the exception
	 */
	public PushSubscription subscribeToPushNotifications(
			Iterable<FolderId> folderIds, URI url, int frequency,
			String watermark, EventType... eventTypes) throws Exception {
		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToPushNotificationsRequest(folderIds, url,
				frequency, watermark, eventTypes).execute().getResponseAtIndex(
				0).getSubscription();
	}

	/**
	 * Begins an asynchronous request to subscribe to push notifications.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The asynccallback delegate
	 * @param state
	 *            An object that contains state information for this request
	 * @param folderIds
	 *            The ids of the folder to subscribe
	 * @param url
	 *            the url of web service endpoint the exchange server should
	 * @param frequency
	 *            the frequency,in minutes at which the exchange server should
	 *            contact the web Service endpoint. Frequency must be between 1
	 *            and 1440.
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @return An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 * @throws Exception
	 *             the exception
	 */
	public IAsyncResult beginSubscribeToPushNotifications(
			AsyncCallback callback, Object state, Iterable<FolderId> folderIds,
			URI url, int frequency, String watermark, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToPushNotificationsRequest(folderIds, url,
				frequency, watermark, eventTypes).beginExecute(callback, null);
	}

	/**
	 * * Subscribes to push notifications on all folders in the authenticated
	 * user's mailbox. Calling this method results in a call to EWS.
	 * 
	 * @param url
	 *            the url
	 * @param frequency
	 *            the frequency
	 * @param watermark
	 *            the watermark
	 * @param eventTypes
	 *            the event types
	 * @return A PushSubscription representing the new subscription.
	 * @throws Exception
	 *             the exception
	 */
	public PushSubscription subscribeToPushNotificationsOnAllFolders(URI url,
			int frequency, String watermark, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateMethodVersion(this, ExchangeVersion.Exchange2010,
				"SubscribeToPushNotificationsOnAllFolders");

		return this.buildSubscribeToPushNotificationsRequest(null, url,
				frequency, watermark, eventTypes).execute().getResponseAtIndex(
				0).getSubscription();
	}

	/**
	 * Begins an asynchronous request to subscribe to push notifications on all
	 * folders in the authenticated user's mailbox. Calling this method results
	 * in a call to EWS.
	 * 
	 * @param callback
	 *            The asynccallback delegate
	 * @param state
	 *            An object that contains state inforamtion for this request
	 * @param url
	 *            the url
	 * @param frequency
	 *            the frequency,in minutes at which the exchange server should
	 *            contact the web Service endpoint. Frequency must be between 1
	 *            and 1440.
	 * @param watermark
	 *            An optional watermark representing a previously opened
	 *            subscription
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @return An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 */
	public IAsyncResult beginSubscribeToPushNotificationsOnAllFolders(
			AsyncCallback callback, Object state, URI url, int frequency,
			String watermark, EventType... eventTypes) throws Exception {
		EwsUtilities.validateMethodVersion(this, ExchangeVersion.Exchange2010,
				"BeginSubscribeToPushNotificationsOnAllFolders");

		return this.buildSubscribeToPushNotificationsRequest(null, url,
				frequency, watermark, eventTypes).beginExecute(callback, null);
	}

	
	/**
	 * Ends an asynchronous request to subscribe to push notifications in the
	 * authenticated user's mailbox.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 *@return A PushSubscription representing the new subscription
	 * @throws Exception 
	 **/
	public PushSubscription endSubscribeToPushNotifications(
			IAsyncResult asyncResult) throws Exception {
		SubscribeToPushNotificationsRequest request = AsyncRequestResult
				.extractServiceRequest(this, asyncResult);

		return request.endExecute(asyncResult).getResponseAtIndex(0)
				.getSubscription();
	}

	/**
	 * Builds an request to request to subscribe to push notifications in the
	 * authenticated user's mailbox.
	 * 
	 * @param folderIds
	 *            the folder ids
	 * @param url
	 *            the url
	 * @param frequency
	 *            the frequency
	 * @param watermark
	 *            the watermark
	 * @param eventTypes
	 *            the event types
	 * @return A request to request to subscribe to push notifications in the
	 *         authenticated user's mailbox.
	 * @throws Exception
	 *             the exception
	 */
	private SubscribeToPushNotificationsRequest buildSubscribeToPushNotificationsRequest(
			Iterable<FolderId> folderIds, URI url, int frequency,
			String watermark, EventType[] eventTypes) throws Exception {
		EwsUtilities.validateParam(url, "url");
		if (frequency < 1 || frequency > 1440) {
			throw new ArgumentOutOfRangeException("frequency",
					Strings.FrequencyMustBeBetween1And1440);
		}

		EwsUtilities.validateParamCollection(eventTypes, "eventTypes");
		SubscribeToPushNotificationsRequest request = new SubscribeToPushNotificationsRequest(
				this);

		if (folderIds != null) {
			request.getFolderIds().addRangeFolderId(folderIds);
		}

		request.setUrl(url);
		request.setFrequency(frequency);

		for (EventType event : eventTypes) {
			request.getEventTypes().add(event);
		}

		request.setWatermark(watermark);

		return request;
	}

	/**
	 * Subscribes to streaming notifications. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param EventType
	 *            The event types to subscribe to.
	 * @return A StreamingSubscription representing the new subscription
	 * @throws Exception
	 */
	public StreamingSubscription subscribeToStreamingNotifications(
			Iterable<FolderId> folderIds, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"SubscribeToStreamingNotifications");

		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToStreamingNotificationsRequest(folderIds,
				eventTypes).execute().getResponseAtIndex(0).getSubscription();
	}

	/**
	 * Subscribes to streaming notifications on all folders in the authenticated
	 * user's mailbox. Calling this method results in a call to EWS.
	 * 
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @return A StreamingSubscription representing the new subscription.
	 * @throws Exception
	 */
	public StreamingSubscription subscribeToStreamingNotificationsOnAllFolders(
			EventType... eventTypes) throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"SubscribeToStreamingNotificationsOnAllFolders");

		return this.buildSubscribeToStreamingNotificationsRequest(null,
				eventTypes).execute().getResponseAtIndex(0).getSubscription();
	}

	/**
	 * Subscribes to streaming notifications. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @return A StreamingSubscription representing the new subscription.
	 * @throws Exception
	 */
/*	public StreamingSubscription subscribeToStreamingNotifications(
			Iterable<FolderId> folderIds, EventType... eventTypes)
			throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"SubscribeToStreamingNotifications");

		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToStreamingNotificationsRequest(folderIds,
				eventTypes).execute().getResponseAtIndex(0).getSubscription();
	}*/

	/**
	 * Begins an asynchronous request to subscribe to streaming notifications.
	 * Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate
	 * @param state
	 *            An object that contains state information for this request.
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param EventType
	 *            The event types to subscribe to.
	 * @return An IAsyncResult that references the asynchronous request
	 * @throws Exception 
	 */
	public IAsyncResult beginSubscribeToStreamingNotifications(AsyncCallback callback,Object state,
			 Iterable<FolderId> folderIds,
			EventType... eventTypes) throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"BeginSubscribeToStreamingNotifications");

		EwsUtilities.validateParamCollection(folderIds.iterator(), "folderIds");

		return this.buildSubscribeToStreamingNotificationsRequest(folderIds,
				eventTypes).beginExecute(callback,null);
	}

	/**
	 * Subscribes to streaming notifications on all folders in the authenticated
	 * user's mailbox. Calling this method results in a call to EWS.
	 * 
	 *@param eventTypes
	 *            The event types to subscribe to. @ returns A
	 *            StreamingSubscription representing the new subscription.
	 * @throws Exception 
	 * @throws Exception 
	 **/
	/*public StreamingSubscription SubscribeToStreamingNotificationsOnAllFolders(
			EventType... eventTypes) throws Exception, Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"SubscribeToStreamingNotificationsOnAllFolders");

		return this.BuildSubscribeToStreamingNotificationsRequest(null,
				eventTypes).execute().getResponseAtIndex(0).getSubscription();
	}*/

	/**
	 *Begins an asynchronous request to subscribe to streaming notifications on
	 * all folders in the authenticated user's mailbox. Calling this method
	 * results in a call to EWS.
	 * 
	 *@param callback
	 *            The AsyncCallback delegate
	 *@param state
	 *            An object that contains state information for this request.
	 *@param eventTypes
	 * @throws Exception 
	 *@returns An IAsyncResult that references the asynchronous request.
	 **/
	public IAsyncResult beginSubscribeToStreamingNotificationsOnAllFolders(AsyncCallback callback,Object state,
			 EventType... eventTypes) throws Exception {
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1,
				"BeginSubscribeToStreamingNotificationsOnAllFolders");

		return this.buildSubscribeToStreamingNotificationsRequest(null,
				eventTypes).beginExecute(callback,null);
	}

	/**
	 * Ends an asynchronous request to subscribe to push notifications in the
	 * authenticated user's mailbox.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 *@return A streamingSubscription representing the new subscription
	 * @throws Exception 
	 * @throws IndexOutOfBoundsException 
	 **/

	public StreamingSubscription endSubscribeToStreamingNotifications(IAsyncResult asyncResult) throws IndexOutOfBoundsException, Exception
        {
            EwsUtilities.validateMethodVersion(
                this,
                ExchangeVersion.Exchange2010_SP1,
                "EndSubscribeToStreamingNotifications");

            SubscribeToStreamingNotificationsRequest request = AsyncRequestResult.extractServiceRequest(this, asyncResult);
         //   SubscribeToStreamingNotificationsRequest request = AsyncRequestResult.extractServiceRequest<SubscribeToStreamingNotificationsRequest>(this, asyncResult);
            return request.endExecute(asyncResult).getResponseAtIndex(0).getSubscription();
        }

	/**
	 * Builds request to subscribe to streaming notifications in the
	 * authenticated user's mailbox.
	 * 
	 * @param folderIds
	 *            The Ids of the folder to subscribe to.
	 * @param eventTypes
	 *            The event types to subscribe to.
	 * @return A request to subscribe to streaming notifications in the
	 *         authenticated user's mailbox
	 * @throws Exception 
	 */
	private SubscribeToStreamingNotificationsRequest buildSubscribeToStreamingNotificationsRequest(
			Iterable<FolderId> folderIds, EventType[] eventTypes) throws Exception {
		EwsUtilities.validateParamCollection(eventTypes, "eventTypes");

		SubscribeToStreamingNotificationsRequest request = new SubscribeToStreamingNotificationsRequest(
				this);

		if (folderIds != null) {
			request.getFolderIds().addRangeFolderId(folderIds);
		}

		for (EventType event : eventTypes) {
			request.getEventTypes().add(event);
		}

		return request;
	}
	
	

	/**
	 * * Synchronizes the items of a specific folder. Calling this method
	 * results in a call to EWS.
	 * 
	 * @param syncFolderId
	 *            The Id of the folder containing the items to synchronize with.
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param ignoredItemIds
	 *            The optional list of item Ids that should be ignored.
	 * @param maxChangesReturned
	 *            The maximum number of changes that should be returned.
	 * @param syncScope
	 *            The sync scope identifying items to include in the
	 *            ChangeCollection.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return A ChangeCollection containing a list of changes that occurred in
	 *         the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public ChangeCollection<ItemChange> syncFolderItems(FolderId syncFolderId,
			PropertySet propertySet, Iterable<ItemId> ignoredItemIds,
			int maxChangesReturned, SyncFolderItemsScope syncScope,
			String syncState) throws Exception {
		return this.buildSyncFolderItemsRequest(syncFolderId, propertySet,
				ignoredItemIds, maxChangesReturned, syncScope, syncState)
				.execute().getResponseAtIndex(0).getChanges();
	}

	/**
	 * Begins an asynchronous request to synchronize the items of a specific
	 * folder. Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate
	 * @param state
	 *            An object that contains state information for this request
	 * @param syncFolderId
	 *            The Id of the folder containing the items to synchronize with
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param ignoredItemIds
	 *            The optional list of item Ids that should be ignored.
	 * @param maxChangesReturned
	 *            The maximum number of changes that should be returned.
	 * @param syncScope
	 *            The sync scope identifying items to include in the
	 *            ChangeCollection
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization
	 * @return An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 */
	public IAsyncResult beginSyncFolderItems( AsyncCallback callback,Object state,FolderId syncFolderId, PropertySet propertySet,
			Iterable<ItemId> ignoredItemIds, int maxChangesReturned,
			SyncFolderItemsScope syncScope, String syncState) throws Exception {
		return this.buildSyncFolderItemsRequest(syncFolderId, propertySet,
				ignoredItemIds, maxChangesReturned, syncScope, syncState)
				.beginExecute(callback,null);
	}

	/**
	 * Ends an asynchronous request to synchronize the items of a specific
	 * folder.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 *@returns A ChangeCollection containing a list of changes that occurred in
	 *          the specified folder.
	 **/
	public ChangeCollection<ItemChange> endSyncFolderItems(IAsyncResult asyncResult) throws Exception
        {
        	SyncFolderItemsRequest request = AsyncRequestResult.extractServiceRequest(this,asyncResult);

            return request.endExecute(asyncResult).getResponseAtIndex(0).getChanges();
        }

	/**
	 * Builds a request to synchronize the items of a specific folder.
	 * 
	 * @param syncFolderId
	 *            The Id of the folder containing the items to synchronize with
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param ignoredItemIds
	 *            The optional list of item Ids that should be ignored
	 * @param maxChangesReturned
	 *            The maximum number of changes that should be returned.
	 * @param syncScope
	 *            The sync scope identifying items to include in the
	 *            ChangeCollection.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return A request to synchronize the items of a specific folder.
	 * @throws Exception 
	 */
	private SyncFolderItemsRequest buildSyncFolderItemsRequest(
			FolderId syncFolderId, PropertySet propertySet,
			Iterable<ItemId> ignoredItemIds, int maxChangesReturned,
			SyncFolderItemsScope syncScope, String syncState) throws Exception {
		EwsUtilities.validateParam(syncFolderId, "syncFolderId");
		EwsUtilities.validateParam(propertySet, "propertySet");

		SyncFolderItemsRequest request = new SyncFolderItemsRequest(this);

		request.setSyncFolderId(syncFolderId);
		request.setPropertySet(propertySet);
		if (ignoredItemIds != null) {
			request.getIgnoredItemIds().addRange(ignoredItemIds);
		}
		request.setMaxChangesReturned(maxChangesReturned);
		request.setSyncScope(syncScope);
		request.setSyncState(syncState);

		return request;
	}

	/**
	 * * Synchronizes the sub-folders of a specific folder. Calling this method
	 * results in a call to EWS.
	 * 
	 * @param syncFolderId
	 *            the sync folder id
	 * @param propertySet
	 *            the property set
	 * @param syncState
	 *            the sync state
	 * @return A ChangeCollection containing a list of changes that occurred in
	 *         the specified folder.
	 * @throws Exception
	 *             the exception
	 */
	public ChangeCollection<FolderChange> syncFolderHierarchy(
			FolderId syncFolderId, PropertySet propertySet, String syncState)
			throws Exception {
		return this.buildSyncFolderHierarchyRequest(syncFolderId, propertySet,
				syncState).execute().getResponseAtIndex(0).getChanges();
	}

	/**
	 * Begins an asynchronous request to synchronize the sub-folders of a
	 * specific folder. Calling this method results in a call to EWS.
	 * 
	 * @param callback
	 *            The AsyncCallback delegate
	 * @param state
	 *            An object that contains state information for this request.
	 * @param syncFolderId
	 *            The Id of the folder containing the items to synchronize with.
	 *            A null value indicates the root folder of the mailbox.
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return An IAsyncResult that references the asynchronous request
	 * @throws Exception 
	 */
	public IAsyncResult beginSyncFolderHierarchy(AsyncCallback callback,Object state, FolderId syncFolderId, PropertySet propertySet,
			String syncState) throws Exception {
		return this.buildSyncFolderHierarchyRequest(syncFolderId, propertySet,
				syncState).beginExecute(callback,null);
	}

	/**
	 * Synchronizes the entire folder hierarchy of the mailbox this Service is
	 * connected to. Calling this method results in a call to EWS.
	 * 
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return A ChangeCollection containing a list of changes that occurred in
	 *         the specified folder.
	 * @throws Exception
	 * @throws 
	 */
	public ChangeCollection<FolderChange> syncFolderHierarchy(
			PropertySet propertySet, String syncState)
			throws Exception {
		return this.syncFolderHierarchy(null, propertySet, syncState);
	}

	/**
	 * Begins an asynchronous request to synchronize the entire folder hierarchy
	 * of the mailbox this Service is connected to. Calling this method results
	 * in a call to EWS
	 * 
	 * @param callback
	 *            The AsyncCallback delegate
	 * @param state
	 *            An object that contains state information for this request.
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return An IAsyncResult that references the asynchronous request
	 * @throws Exception 
	 */
	/*public IAsyncResult beginSyncFolderHierarchy(FolderId syncFolderId, PropertySet propertySet, String syncState) throws Exception {
		return this.beginSyncFolderHierarchy(null,null, null,
				propertySet, syncState);
	}*/

	/**
	 * Ends an asynchronous request to synchronize the specified folder
	 * hierarchy of the mailbox this Service is connected to.
	 * 
	 *@param asyncResult
	 *            An IAsyncResult that references the asynchronous request.
	 * @throws Exception 
	 *@returns A ChangeCollection containing a list of changes that occurred in
	 *          the specified folder.
	 **/
	public ChangeCollection<FolderChange> endSyncFolderHierarchy(IAsyncResult asyncResult) throws Exception
        {
        	SyncFolderHierarchyRequest request = AsyncRequestResult.extractServiceRequest(this, asyncResult);

            return request.endExecute(asyncResult).getResponseAtIndex(0).getChanges();
        }

	/**
	 * Builds a request to synchronize the specified folder hierarchy of the
	 * mailbox this Service is connected to.
	 * 
	 * @param syncFolderId
	 *            The Id of the folder containing the items to synchronize with.
	 *            A null value indicates the root folder of the mailbox.
	 * @param propertySet
	 *            The set of properties to retrieve for synchronized items.
	 * @param syncState
	 *            The optional sync state representing the point in time when to
	 *            start the synchronization.
	 * @return A request to synchronize the specified folder hierarchy of the
	 *         mailbox this Service is connected to
	 * @throws Exception
	 */
	private SyncFolderHierarchyRequest buildSyncFolderHierarchyRequest(
			FolderId syncFolderId, PropertySet propertySet, String syncState)
			throws Exception {
		EwsUtilities.validateParamAllowNull(syncFolderId, "syncFolderId"); // Null
																			// syncFolderId
																			// is
																			// allowed
		EwsUtilities.validateParam(propertySet, "propertySet");

		SyncFolderHierarchyRequest request = new SyncFolderHierarchyRequest(
				this);

		request.setPropertySet(propertySet);
		request.setSyncFolderId(syncFolderId);
		request.setSyncState(syncState);

		return request;
	}

	// Availability operations

	/**
	 * * Gets Out of Office (OOF) settings for a specific user. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param smtpAddress
	 *            the smtp address
	 * @return An OofSettings instance containing OOF information for the
	 *         specified user.
	 * @throws Exception
	 *             the exception
	 */
	public OofSettings getUserOofSettings(String smtpAddress) throws Exception {
		EwsUtilities.validateParam(smtpAddress, "smtpAddress");
		GetUserOofSettingsRequest request = new GetUserOofSettingsRequest(this);
		request.setSmtpAddress(smtpAddress);

		return request.execute().getOofSettings();
	}

	/**
	 * * Sets Out of Office (OOF) settings for a specific user. Calling this
	 * method results in a call to EWS.
	 * 
	 * @param smtpAddress
	 *            the smtp address
	 * @param oofSettings
	 *            the oof settings
	 * @throws Exception
	 *             the exception
	 */
	public void setUserOofSettings(String smtpAddress, OofSettings oofSettings)
			throws Exception {
		EwsUtilities.validateParam(smtpAddress, "smtpAddress");
		EwsUtilities.validateParam(oofSettings, "oofSettings");

		SetUserOofSettingsRequest request = new SetUserOofSettingsRequest(this);

		request.setSmtpAddress(smtpAddress);
		request.setOofSettings(oofSettings);

		request.execute();
	}

	/**
	 * * Gets detailed information about the availability of a set of users,
	 * rooms, and resources within a specified time window.
	 * 
	 * @param attendees
	 *            the attendees
	 * @param timeWindow
	 *            the time window
	 * @param requestedData
	 *            the requested data
	 * @param options
	 *            the options
	 * @return The availability information for each user appears in a unique
	 *         FreeBusyResponse object. The order of users in the request
	 *         determines the order of availability data for each user in the
	 *         response.
	 * @throws Exception
	 *             the exception
	 */
	public GetUserAvailabilityResults getUserAvailability(
			Iterable<AttendeeInfo> attendees, TimeWindow timeWindow,
			AvailabilityData requestedData, AvailabilityOptions options)
			throws Exception {
		EwsUtilities.validateParamCollection(attendees.iterator(), "attendees");
		EwsUtilities.validateParam(timeWindow, "timeWindow");
		EwsUtilities.validateParam(options, "options");

		GetUserAvailabilityRequest request = new GetUserAvailabilityRequest(
				this);

		request.setAttendees(attendees);
		request.setTimeWindow(timeWindow);
		request.setRequestedData(requestedData);
		request.setOptions(options);

		return request.execute();
	}

	/**
	 * * Gets detailed information about the availability of a set of users,
	 * rooms, and resources within a specified time window.
	 * 
	 * @param attendees
	 *            the attendees
	 * @param timeWindow
	 *            the time window
	 * @param requestedData
	 *            the requested data
	 * @return The availability information for each user appears in a unique
	 *         FreeBusyResponse object. The order of users in the request
	 *         determines the order of availability data for each user in the
	 *         response.
	 * @throws Exception
	 *             the exception
	 */
	public GetUserAvailabilityResults getUserAvailability(
			Iterable<AttendeeInfo> attendees, TimeWindow timeWindow,
			AvailabilityData requestedData) throws Exception {
		return this.getUserAvailability(attendees, timeWindow, requestedData,
				new AvailabilityOptions());
	}

	/**
	 * * Retrieves a collection of all room lists in the organization.
	 * 
	 * @return An EmailAddressCollection containing all the room lists in the
	 *         organization
	 * @throws Exception
	 *             the exception
	 */
	public EmailAddressCollection getRoomLists() throws Exception {
		GetRoomListsRequest request = new GetRoomListsRequest(this);
		return request.execute().getRoomLists();
	}

	/**
	 * * Retrieves a collection of all room lists in the specified room list in
	 * the organization.
	 * 
	 * @param emailAddress
	 *            the email address
	 * @return A collection of EmailAddress objects representing all the rooms
	 *         within the specifed room list.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<EmailAddress> getRooms(EmailAddress emailAddress)
			throws Exception {
		EwsUtilities.validateParam(emailAddress, "emailAddress");
		GetRoomsRequest request = new GetRoomsRequest(this);
		request.setRoomList(emailAddress);

		return request.execute().getRooms();
	}

	// region Conversation

	/**
	 * Retrieves a collection of all Conversations in the specified Folder.
	 * 
	 * @param view
	 *            The view controlling the number of conversations returned.
	 * @param filter
	 *            The search filter. Only search filter class supported
	 *            SearchFilter.IsEqualTo
	 * @param folderId
	 *            The Id of the folder in which to search for conversations.
	 * @throws Exception
	 */
	private Collection<Conversation> findConversation(
			ConversationIndexedItemView view, SearchFilter.IsEqualTo filter,
			FolderId folderId) throws Exception {
		EwsUtilities.validateParam(view, "view");
		EwsUtilities.validateParamAllowNull(filter, "filter");
		EwsUtilities.validateParam(folderId, "folderId");
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1, "FindConversation");

		FindConversationRequest request = new FindConversationRequest(this);
		request.setIndexedItemView(view);
		request.setConversationViewFilter(filter);
		request.setFolderId(new FolderIdWrapper(folderId));

		return request.execute().getConversations();

	}

	/**
	 * Retrieves a collection of all Conversations in the specified Folder.
	 * 
	 * @param view
	 *            The view controlling the number of conversations returned.
	 * @param folderId
	 *            The Id of the folder in which to search for conversations.
	 * @throws Exception
	 */
	public Collection<Conversation> findConversation(
			ConversationIndexedItemView view, FolderId folderId)
			throws Exception {
		return this.findConversation(view, null, folderId);
	}

	/**
	 * Applies ConversationAction on the specified conversation.
	 * 
	 * @param actionType
	 *            ConversationAction
	 * @param conversationIds
	 *            The conversation ids.
	 * @param processRightAway
	 *            True to process at once . This is blocking and false to let
	 *            the Assitant process it in the back ground
	 * @param categories
	 *            Catgories that need to be stamped can be null or empty
	 * @param enableAlwaysDelete
	 *            True moves every current and future messages in the
	 *            conversation to deleted items folder. False stops the alwasy
	 *            delete action. This is applicable only if the action is
	 *            AlwaysDelete
	 * @param destinationFolderId
	 *            Applicable if the action is AlwaysMove. This moves every
	 *            current message and future message in the conversation to the
	 *            specified folder. Can be null if tis is then it stops the
	 *            always move action
	 * @param errorHandlingMode
	 *            The error handling mode.
	 * @throws Exception
	 */
	private ServiceResponseCollection<ServiceResponse> applyConversationAction(
			ConversationActionType actionType,
			Iterable<ConversationId> conversationIds, boolean processRightAway,
			StringList categories, boolean enableAlwaysDelete,
			FolderId destinationFolderId, ServiceErrorHandling errorHandlingMode)
			throws Exception {
		EwsUtilities.EwsAssert(
				actionType == ConversationActionType.AlwaysCategorize
						|| actionType == ConversationActionType.AlwaysMove
						|| actionType == ConversationActionType.AlwaysDelete,
				"ApplyConversationAction", "Invalic actionType");

		EwsUtilities.validateParam(conversationIds, "conversationId");
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1, "ApplyConversationAction");

		ApplyConversationActionRequest request = new ApplyConversationActionRequest(
				this, errorHandlingMode);
		ConversationAction action = new ConversationAction();

		for (ConversationId conversationId : conversationIds) {
			action.setAction(actionType);
			action.setConversationId(conversationId);
			action.setProcessRightAway(processRightAway);
			action.setCategories(categories);
			action.setEnableAlwaysDelete(enableAlwaysDelete);
			action
					.setDestinationFolderId(destinationFolderId != null ? new FolderIdWrapper(
							destinationFolderId)
							: null);
			request.getConversationActions().add(action);
		}

		return request.execute();
	}

	/**
	 * Applies one time conversation action on items in specified folder inside
	 * the conversation.
	 * 
	 * @param actionType
	 *            The action
	 * @param idTimePairs
	 *            The id time pairs.
	 * @param contextFolderId
	 *            The context folder id.
	 * @param destinationFolderId
	 *            The destination folder id.
	 * @param deleteType
	 *            Type of the delete.
	 * @param isRead
	 *            The is read.
	 * @param errorHandlingMode
	 *            The error handling mode.
	 * @throws Exception
	 */
	private ServiceResponseCollection<ServiceResponse> applyConversationOneTimeAction(
			ConversationActionType actionType,
			Iterable<HashMap<ConversationId, Date>> idTimePairs,
			FolderId contextFolderId, FolderId destinationFolderId,
			DeleteMode deleteType, Boolean isRead,
			ServiceErrorHandling errorHandlingMode) throws Exception {
		EwsUtilities.EwsAssert(actionType == ConversationActionType.Move
				|| actionType == ConversationActionType.Delete
				|| actionType == ConversationActionType.SetReadState
				|| actionType == ConversationActionType.Copy,
				"ApplyConversationOneTimeAction", "Invalid actionType");

		EwsUtilities.validateParamCollection(idTimePairs.iterator(),
				"idTimePairs");
		EwsUtilities.validateMethodVersion(this,
				ExchangeVersion.Exchange2010_SP1, "ApplyConversationAction");

		ApplyConversationActionRequest request = new ApplyConversationActionRequest(
				this, errorHandlingMode);

		for (HashMap<ConversationId, Date> idTimePair : idTimePairs) {
			ConversationAction action = new ConversationAction();

			action.setAction(actionType);
			action.setConversationId(idTimePair.keySet().iterator().next());
			action
					.setContextFolderId(contextFolderId != null ? new FolderIdWrapper(
							contextFolderId)
							: null);
			action
					.setDestinationFolderId(destinationFolderId != null ? new FolderIdWrapper(
							destinationFolderId)
							: null);
			action.setConversationLastSyncTime(idTimePair.values().iterator()
					.next());
			action.setIsRead(isRead);
			action.setDeleteType(deleteType);

			request.getConversationActions().add(action);
		}

		return request.execute();
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is always categorized. Calling this method results in a call to EWS.
	 * 
	 * @param conversationId
	 *            The id of the conversation.
	 * @param categories
	 *            The categories that should be stamped on items in the
	 *            conversation.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once enabling
	 *            this rule and stamping existing items in the conversation is
	 *            completely done. If processSynchronously is false, the method
	 *            returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> enableAlwaysCategorizeItemsInConversations(
			Iterable<ConversationId> conversationId,
			Iterable<String> categories, boolean processSynchronously)
			throws Exception {
		EwsUtilities.validateParamCollection(categories.iterator(),
				"categories");
		return this.applyConversationAction(
				ConversationActionType.AlwaysCategorize, conversationId,
				processSynchronously, new StringList(categories), false, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is no longer categorized. Calling this method results in a call to EWS.
	 * 
	 * @param conversationId
	 *            The id of the conversation.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once enabling
	 *            this rule and stamping existing items in the conversation is
	 *            completely done. If processSynchronously is false, the method
	 *            returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> disableAlwaysCategorizeItemsInConversations(
			Iterable<ConversationId> conversationId,
			boolean processSynchronously) throws Exception {
		return this.applyConversationAction(
				ConversationActionType.AlwaysCategorize, conversationId,
				processSynchronously, null, false, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is always moved to Deleted Items folder. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param conversationId
	 *            The id of the conversation.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once enabling
	 *            this rule and stamping existing items in the conversation is
	 *            completely done. If processSynchronously is false, the method
	 *            returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> enableAlwaysDeleteItemsInConversations(
			Iterable<ConversationId> conversationId,
			boolean processSynchronously) throws Exception {
		return this.applyConversationAction(
				ConversationActionType.AlwaysDelete, conversationId,
				processSynchronously, null, true, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is no longer moved to Deleted Items folder. Calling this method results
	 * in a call to EWS.
	 * 
	 * @param conversationId
	 *            The id of the conversation.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once enabling
	 *            this rule and stamping existing items in the conversation is
	 *            completely done. If processSynchronously is false, the method
	 *            returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> disableAlwaysDeleteItemsInConversations(
			Iterable<ConversationId> conversationId,
			boolean processSynchronously) throws Exception {
		return this.applyConversationAction(
				ConversationActionType.AlwaysDelete, conversationId,
				processSynchronously, null, false, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is always moved to a specific folder. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param conversationId
	 *            The Id of the folder to which conversation items should be
	 *            moved.
	 * @param destinationFolderId
	 *            The Id of the destination folder.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once enabling
	 *            this rule and stamping existing items in the conversation is
	 *            completely done. If processSynchronously is false, the method
	 *            returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> enableAlwaysMoveItemsInConversations(
			Iterable<ConversationId> conversationId,
			FolderId destinationFolderId, boolean processSynchronously)
			throws Exception {
		EwsUtilities.validateParam(destinationFolderId, "destinationFolderId");
		return this.applyConversationAction(ConversationActionType.AlwaysMove,
				conversationId, processSynchronously, null, false,
				destinationFolderId, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets up a conversation so that any item received within that conversation
	 * is no longer moved to a specific folder. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param conversationIds
	 *            The conversation ids.
	 * @param processSynchronously
	 *            Indicates whether the method should return only once disabling
	 *            this rule is completely done. If processSynchronously is
	 *            false, the method returns immediately.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> disableAlwaysMoveItemsInConversations(
			Iterable<ConversationId> conversationIds,
			boolean processSynchronously) throws Exception {
		return this.applyConversationAction(ConversationActionType.AlwaysMove,
				conversationIds, processSynchronously, null, false, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Moves the items in the specified conversation to the specified
	 * destination folder. Calling this method results in a call to EWS.
	 * 
	 * @param idLastSyncTimePairs
	 *            The pairs of Id of conversation whose items should be moved
	 *            and the dateTime conversation was last synced (Items received
	 *            after that dateTime will not be moved).
	 * @param contextFolderId
	 *            The Id of the folder that contains the conversation.
	 * @param destinationFolderId
	 *            The Id of the destination folder.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> moveItemsInConversations(
			Iterable<HashMap<ConversationId, Date>> idLastSyncTimePairs,
			FolderId contextFolderId, FolderId destinationFolderId)
			throws Exception {
		EwsUtilities.validateParam(destinationFolderId, "destinationFolderId");
		return this.applyConversationOneTimeAction(ConversationActionType.Move,
				idLastSyncTimePairs, contextFolderId, destinationFolderId,
				null, null, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Copies the items in the specified conversation to the specified
	 * destination folder. Calling this method results in a call to EWS.
	 * 
	 * @param idLastSyncTimePairs
	 *            The pairs of Id of conversation whose items should be copied
	 *            and the dateTime conversation was last synced (Items received
	 *            after that dateTime will not be copied).
	 * @param contextFolderId
	 *            The context folder id.
	 * @param destinationFolderId
	 *            The destination folder id.
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> copyItemsInConversations(
			Iterable<HashMap<ConversationId, Date>> idLastSyncTimePairs,
			FolderId contextFolderId, FolderId destinationFolderId)
			throws Exception {
		EwsUtilities.validateParam(destinationFolderId, "destinationFolderId");
		return this.applyConversationOneTimeAction(ConversationActionType.Copy,
				idLastSyncTimePairs, contextFolderId, destinationFolderId,
				null, null, ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Deletes the items in the specified conversation. Calling this method
	 * results in a call to EWS.
	 * 
	 * @param idLastSyncTimePairs
	 *            The pairs of Id of conversation whose items should be deleted
	 *            and the date and time conversation was last synced (Items
	 *            received after that date will not be deleted). conversation
	 *            was last synced (Items received after that dateTime will not
	 *            be copied).
	 * @param contextFolderId
	 *            The Id of the folder that contains the conversation.
	 * @param deleteMode
	 *            The deletion mode
	 * @throws Exception
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> deleteItemsInConversations(
			Iterable<HashMap<ConversationId, Date>> idLastSyncTimePairs,
			FolderId contextFolderId, DeleteMode deleteMode) throws Exception {
		return this.applyConversationOneTimeAction(
				ConversationActionType.Delete, idLastSyncTimePairs,
				contextFolderId, null, deleteMode, null,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * Sets the read state for items in conversation. Calling this mehtod would
	 * result in call to EWS.
	 * 
	 * @param idLastSyncTimePairs
	 *            The pairs of Id of conversation whose items should read state
	 *            set and the date and time conversation was last synced (Items
	 *            received after that date will not have their read state set).
	 *            was last synced (Items received after that date will not be
	 *            deleted). conversation was last synced (Items received after
	 *            that dateTime will not be copied).
	 * @param contextFolderId
	 *            The Id of the folder that contains the conversation.
	 * @param isRead
	 *            if set to <c>true</c>, conversation items are marked as read;
	 *            otherwise they are marked as unread.
	 * @throws Exception
	 * @throws Exception
	 */
	public ServiceResponseCollection<ServiceResponse> setReadStateForItemsInConversations(
			Iterable<HashMap<ConversationId, Date>> idLastSyncTimePairs,
			FolderId contextFolderId, boolean isRead) throws Exception {
		return this.applyConversationOneTimeAction(
				ConversationActionType.SetReadState, idLastSyncTimePairs,
				contextFolderId, null, null, isRead,
				ServiceErrorHandling.ReturnErrors);
	}

	// Id conversion operations

	/**
	 * * Converts multiple Ids from one format to another in a single call to
	 * EWS.
	 * 
	 * @param ids
	 *            the ids
	 * @param destinationFormat
	 *            the destination format
	 * @param errorHandling
	 *            the error handling
	 * @return A ServiceResponseCollection providing conversion results for each
	 *         specified Ids.
	 * @throws Exception
	 *             the exception
	 */
	private ServiceResponseCollection<ConvertIdResponse> internalConvertIds(
			Iterable<AlternateIdBase> ids, IdFormat destinationFormat,
			ServiceErrorHandling errorHandling) throws Exception {
		EwsUtilities.validateParamCollection(ids.iterator(), "ids");

		ConvertIdRequest request = new ConvertIdRequest(this, errorHandling);

		request.getIds().addAll((Collection<? extends AlternateIdBase>) ids);
		request.setDestinationFormat(destinationFormat);

		return request.execute();
	}

	/**
	 * * Converts multiple Ids from one format to another in a single call to
	 * EWS.
	 * 
	 * @param ids
	 *            the ids
	 * @param destinationFormat
	 *            the destination format
	 * @return A ServiceResponseCollection providing conversion results for each
	 *         specified Ids.
	 * @throws Exception
	 *             the exception
	 */
	public ServiceResponseCollection<ConvertIdResponse> convertIds(
			Iterable<AlternateIdBase> ids, IdFormat destinationFormat)
			throws Exception {
		EwsUtilities.validateParamCollection(ids.iterator(), "ids");

		return this.internalConvertIds(ids, destinationFormat,
				ServiceErrorHandling.ReturnErrors);
	}

	/**
	 * * Converts Id from one format to another in a single call to EWS.
	 * 
	 * @param id
	 *            the id
	 * @param destinationFormat
	 *            the destination format
	 * @return The converted Id.
	 * @throws Exception
	 *             the exception
	 */
	public AlternateIdBase convertId(AlternateIdBase id,
			IdFormat destinationFormat) throws Exception {
		EwsUtilities.validateParam(id, "id");

		List<AlternateIdBase> alternateIdBaseArray = new ArrayList<AlternateIdBase>();
		alternateIdBaseArray.add(id);

		ServiceResponseCollection<ConvertIdResponse> responses = this
				.internalConvertIds(alternateIdBaseArray, destinationFormat, ServiceErrorHandling.ThrowOnError);

		return responses.getResponseAtIndex(0).getConvertedId();
	}

	/**
	 * * Adds delegates to a specific mailbox. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param meetingRequestsDeliveryScope
	 *            the meeting requests delivery scope
	 * @param delegateUsers
	 *            the delegate users
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> addDelegates(Mailbox mailbox,
			MeetingRequestsDeliveryScope meetingRequestsDeliveryScope,
			DelegateUser... delegateUsers) throws Exception {
		ArrayList<DelegateUser> delUser = new ArrayList<DelegateUser>();
		for (DelegateUser user : delegateUsers) {
			delUser.add(user);
		}

		return this
				.addDelegates(mailbox, meetingRequestsDeliveryScope, delUser);
	}

	/**
	 * * Adds delegates to a specific mailbox. Calling this method results in a
	 * call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param meetingRequestsDeliveryScope
	 *            the meeting requests delivery scope
	 * @param delegateUsers
	 *            the delegate users
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> addDelegates(Mailbox mailbox,
			MeetingRequestsDeliveryScope meetingRequestsDeliveryScope,
			Iterable<DelegateUser> delegateUsers) throws Exception {
		EwsUtilities.validateParam(mailbox, "mailbox");
		EwsUtilities.validateParamCollection(delegateUsers.iterator(),
				"delegateUsers");

		AddDelegateRequest request = new AddDelegateRequest(this);
		request.setMailbox(mailbox);

		for (DelegateUser user : delegateUsers) {
			request.getDelegateUsers().add(user);
		}

		request.setMeetingRequestsDeliveryScope(meetingRequestsDeliveryScope);

		DelegateManagementResponse response = request.execute();
		return response.getDelegateUserResponses();
	}

	/**
	 * * Updates delegates on a specific mailbox. Calling this method results in
	 * a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param meetingRequestsDeliveryScope
	 *            the meeting requests delivery scope
	 * @param delegateUsers
	 *            the delegate users
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> updateDelegates(Mailbox mailbox,
			MeetingRequestsDeliveryScope meetingRequestsDeliveryScope,
			DelegateUser... delegateUsers) throws Exception {

		ArrayList<DelegateUser> delUser = new ArrayList<DelegateUser>();
		for (DelegateUser user : delegateUsers) {
			delUser.add(user);
		}
		return this.updateDelegates(mailbox, meetingRequestsDeliveryScope,
				delUser);
	}

	/**
	 * * Updates delegates on a specific mailbox. Calling this method results in
	 * a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param meetingRequestsDeliveryScope
	 *            the meeting requests delivery scope
	 * @param delegateUsers
	 *            the delegate users
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> updateDelegates(Mailbox mailbox,
			MeetingRequestsDeliveryScope meetingRequestsDeliveryScope,
			Iterable<DelegateUser> delegateUsers) throws Exception {
		EwsUtilities.validateParam(mailbox, "mailbox");
		EwsUtilities.validateParamCollection(delegateUsers.iterator(),
				"delegateUsers");

		UpdateDelegateRequest request = new UpdateDelegateRequest(this);

		request.setMailbox(mailbox);

		ArrayList<DelegateUser> delUser = new ArrayList<DelegateUser>();
		for (DelegateUser user : delegateUsers) {
			delUser.add(user);
		}
		request.getDelegateUsers().addAll(delUser);
		request.setMeetingRequestsDeliveryScope(meetingRequestsDeliveryScope);

		DelegateManagementResponse response = request.execute();
		return response.getDelegateUserResponses();
	}

	/**
	 * * Removes delegates on a specific mailbox. Calling this method results in
	 * a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param userIds
	 *            the user ids
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> removeDelegates(Mailbox mailbox,
			UserId... userIds) throws Exception {
		ArrayList<UserId> delUser = new ArrayList<UserId>();
		for (UserId user : userIds) {
			delUser.add(user);
		}
		return this.removeDelegates(mailbox, delUser);
	}

	/**
	 * * Removes delegates on a specific mailbox. Calling this method results in
	 * a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param userIds
	 *            the user ids
	 * @return A collection of DelegateUserResponse objects providing the
	 *         results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public Collection<DelegateUserResponse> removeDelegates(Mailbox mailbox,
			Iterable<UserId> userIds) throws Exception {
		EwsUtilities.validateParam(mailbox, "mailbox");
		EwsUtilities.validateParamCollection(userIds.iterator(), "userIds");

		RemoveDelegateRequest request = new RemoveDelegateRequest(this);
		request.setMailbox(mailbox);

		ArrayList<UserId> delUser = new ArrayList<UserId>();
		for (UserId user : userIds) {
			delUser.add(user);
		}
		request.getUserIds().addAll(delUser);

		DelegateManagementResponse response = request.execute();
		return response.getDelegateUserResponses();
	}

	/**
	 * * Retrieves the delegates of a specific mailbox. Calling this method
	 * results in a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param includePermissions
	 *            the include permissions
	 * @param userIds
	 *            the user ids
	 * @return A GetDelegateResponse providing the results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public DelegateInformation getDelegates(Mailbox mailbox,
			boolean includePermissions, UserId... userIds) throws Exception {
		ArrayList<UserId> delUser = new ArrayList<UserId>();
		for (UserId user : userIds) {
			delUser.add(user);
		}
		return this.getDelegates(mailbox, includePermissions, delUser);
	}

	/**
	 * * Retrieves the delegates of a specific mailbox. Calling this method
	 * results in a call to EWS.
	 * 
	 * @param mailbox
	 *            the mailbox
	 * @param includePermissions
	 *            the include permissions
	 * @param userIds
	 *            the user ids
	 * @return A GetDelegateResponse providing the results of the operation.
	 * @throws Exception
	 *             the exception
	 */
	public DelegateInformation getDelegates(Mailbox mailbox,
			boolean includePermissions, Iterable<UserId> userIds)
			throws Exception {
		EwsUtilities.validateParam(mailbox, "mailbox");

		GetDelegateRequest request = new GetDelegateRequest(this);

		request.setMailbox(mailbox);

		ArrayList<UserId> delUser = new ArrayList<UserId>();
		for (UserId user : userIds) {
			delUser.add(user);
		}
		request.getUserIds().addAll(delUser);
		request.setIncludePermissions(includePermissions);

		GetDelegateResponse response = request.execute();
		DelegateInformation delegateInformation = new DelegateInformation(
				(List<DelegateUserResponse>) response
						.getDelegateUserResponses(), response
						.getMeetingRequestsDeliveryScope());

		return delegateInformation;
	}

	/**
	 * Creates the user configuration.
	 * 
	 * @param userConfiguration
	 *            the user configuration
	 * @throws Exception
	 *             the exception
	 */
	protected void createUserConfiguration(UserConfiguration userConfiguration)
			throws Exception {
		EwsUtilities.validateParam(userConfiguration, "userConfiguration");

		CreateUserConfigurationRequest request = new CreateUserConfigurationRequest(
				this);

		request.setUserConfiguration(userConfiguration);

		request.execute();
	}

	/**
	 * * Creates a UserConfiguration.
	 * 
	 * @param name
	 *            the name
	 * @param parentFolderId
	 *            the parent folder id
	 * @throws Exception
	 *             the exception
	 */
	protected void deleteUserConfiguration(String name, FolderId parentFolderId)
			throws Exception {
		EwsUtilities.validateParam(name, "name");
		EwsUtilities.validateParam(parentFolderId, "parentFolderId");

		DeleteUserConfigurationRequest request = new DeleteUserConfigurationRequest(
				this);

		request.setName(name);
		request.setParentFolderId(parentFolderId);
		request.execute();
	}

	/**
	 * * Creates a UserConfiguration.
	 * 
	 * @param name
	 *            the name
	 * @param parentFolderId
	 *            the parent folder id
	 * @param properties
	 *            the properties
	 * @return the user configuration
	 * @throws Exception
	 *             the exception
	 */
	protected UserConfiguration getUserConfiguration(String name,
			FolderId parentFolderId, UserConfigurationProperties properties)
			throws Exception {
		EwsUtilities.validateParam(name, "name");
		EwsUtilities.validateParam(parentFolderId, "parentFolderId");

		GetUserConfigurationRequest request = new GetUserConfigurationRequest(
				this);

		request.setName(name);
		request.setParentFolderId(parentFolderId);
		request.setProperties(EnumSet.of(properties));

		return request.execute().getResponseAtIndex(0).getUserConfiguration();
	}

	/**
	 * * Loads the properties of the specified userConfiguration.
	 * 
	 * @param userConfiguration
	 *            the user configuration
	 * @param properties
	 *            the properties
	 * @throws Exception
	 *             the exception
	 */
	protected void loadPropertiesForUserConfiguration(
			UserConfiguration userConfiguration,
			UserConfigurationProperties properties) throws Exception {
		EwsUtilities.EwsAssert(userConfiguration != null,
				"ExchangeService.LoadPropertiesForUserConfiguration",
				"userConfiguration is null");

		GetUserConfigurationRequest request = new GetUserConfigurationRequest(
				this);

		request.setUserConfiguration(userConfiguration);
		request.setProperties(EnumSet.of(properties));

		request.execute();
	}

	/**
	 * * Updates a UserConfiguration.
	 * 
	 * @param userConfiguration
	 *            the user configuration
	 * @throws Exception
	 *             the exception
	 */
	protected void updateUserConfiguration(UserConfiguration userConfiguration)
			throws Exception {
		EwsUtilities.validateParam(userConfiguration, "userConfiguration");
		UpdateUserConfigurationRequest request = new UpdateUserConfigurationRequest(
				this);

		request.setUserConfiguration(userConfiguration);

		request.execute();
	}

	// region InboxRule operations

	/**
	 * Retrieves inbox rules of the authenticated user.
	 * 
	 * @return A RuleCollection object containing the authenticated users inbox
	 *         rules.
	 * @throws Exception
	 */
	public RuleCollection getInboxRules() throws Exception {
		GetInboxRulesRequest request = new GetInboxRulesRequest(this);
		return request.execute().getRules();
	}

	/**
	 * Retrieves the inbox rules of the specified user.
	 * 
	 * @param mailboxSmtpAddress
	 *            The SMTP address of the user whose inbox rules should be
	 *            retrieved
	 * @return A RuleCollection object containing the inbox rules of the
	 *         specified user.
	 * @throws Exception
	 */
	public RuleCollection getInboxRules(String mailboxSmtpAddress)
			throws Exception {
		EwsUtilities.validateParam(mailboxSmtpAddress, "MailboxSmtpAddress");

		GetInboxRulesRequest request = new GetInboxRulesRequest(this);
		request.setmailboxSmtpAddress(mailboxSmtpAddress);
		return request.execute().getRules();
	}

	/**
	 * Updates the authenticated user's inbox rules by applying the specified
	 * operations.
	 * 
	 * @param operations
	 *            The operations that should be applied to the user's inbox
	 *            rules.
	 * @param removeOutlookRuleBlob
	 *            Indicate whether or not to remove Outlook Rule Blob.
	 * @throws Exception
	 */
	public void updateInboxRules(Iterable<RuleOperation> operations,
			boolean removeOutlookRuleBlob) throws Exception {
		UpdateInboxRulesRequest request = new UpdateInboxRulesRequest(this);
		request.setInboxRuleOperations(operations);
		request.setRemoveOutlookRuleBlob(removeOutlookRuleBlob);
		request.execute();
	}

	/**
	 * Updates the authenticated user's inbox rules by applying the specified
	 * operations.
	 * 
	 * @param operations
	 *            The operations that should be applied to the user's inbox
	 *            rules.
	 * @param removeOutlookRuleBlob
	 *            Indicate whether or not to remove Outlook Rule Blob.
	 * @param mailboxSmtpAddress
	 *            The SMTP address of the user whose inbox rules should be
	 *            retrieved
	 * @throws Exception
	 */
	public void updateInboxRules(Iterable<RuleOperation> operations,
			boolean removeOutlookRuleBlob, String mailboxSmtpAddress)
			throws Exception {
		UpdateInboxRulesRequest request = new UpdateInboxRulesRequest(this);
		request.setInboxRuleOperations(operations);
		request.setRemoveOutlookRuleBlob(removeOutlookRuleBlob);
		request.setMailboxSmtpAddress(mailboxSmtpAddress);
		request.execute();
	}

	/**
	 * Default implementation of AutodiscoverRedirectionUrlValidationCallback.
	 * Always returns true indicating that the URL can be used.
	 * 
	 * @param redirectionUrl
	 *            the redirection url
	 * @return Returns true.
	 * @throws AutodiscoverLocalException
	 *             the autodiscover local exception
	 */
	private boolean defaultAutodiscoverRedirectionUrlValidationCallback(
			String redirectionUrl) throws AutodiscoverLocalException {
		throw new AutodiscoverLocalException(String.format(
				Strings.AutodiscoverRedirectBlocked, redirectionUrl));
	}

	/**
	 * Initializes the Url property to the Exchange Web Services URL for the
	 * specified e-mail address by calling the Autodiscover service.
	 * 
	 * @param emailAddress
	 *            the email address
	 * @throws Exception
	 *             the exception
	 */
	public void autodiscoverUrl(String emailAddress) throws Exception {
		this.autodiscoverUrl(emailAddress, this);
	}

	/**
	 * Initializes the Url property to the Exchange Web Services URL for the
	 * specified e-mail address by calling the Autodiscover service.
	 * 
	 * @param emailAddress
	 *            the email address to use.
	 * @param validateRedirectionUrlCallback
	 *            The callback used to validate redirection URL
	 * @throws Exception
	 *             the exception
	 */
	public void autodiscoverUrl(String emailAddress,
			IAutodiscoverRedirectionUrl validateRedirectionUrlCallback)
			throws Exception {
		URI exchangeServiceUrl = null;

		if (this.getRequestedServerVersion().ordinal() > ExchangeVersion.Exchange2007_SP1
				.ordinal()) {
			try {
				exchangeServiceUrl = this.getAutodiscoverUrl(emailAddress, this
						.getRequestedServerVersion(),
						validateRedirectionUrlCallback);
				this.setUrl(this
						.adjustServiceUriFromCredentials(exchangeServiceUrl));
				return;
			} catch (AutodiscoverLocalException ex) {

				this.traceMessage(TraceFlags.AutodiscoverResponse, String
						.format("Autodiscover service call "
								+ "failed with error '%s'. "
								+ "Will try legacy service", ex.getMessage()));

			} catch (ServiceRemoteException ex) {
				// E14:321785 -- Special case: if
				// the caller's account is locked
				// we want to return this exception, not continue.
				if (ex instanceof AccountIsLockedException) {
					throw new AccountIsLockedException(ex.getMessage(),
							exchangeServiceUrl, ex);
				}

				this.traceMessage(TraceFlags.AutodiscoverResponse, String
						.format("Autodiscover service call "
								+ "failed with error '%s'. "
								+ "Will try legacy service", ex.getMessage()));
			}
		}

		// Try legacy Autodiscover provider

		exchangeServiceUrl = this.getAutodiscoverUrl(emailAddress,
				ExchangeVersion.Exchange2007_SP1,
				validateRedirectionUrlCallback);

		this.setUrl(this.adjustServiceUriFromCredentials(exchangeServiceUrl));
	}

	/**
	 * Autodiscover will always return the "plain" EWS endpoint URL but if the
	 * client is using WindowsLive credentials, ExchangeService needs to use the
	 * WS-Security endpoint.
	 * 
	 * @param uri
	 *            the uri
	 * @return Adjusted URL.
	 * @throws Exception 
	 */
	private URI adjustServiceUriFromCredentials(URI uri)
			throws Exception {
		return (this.getCredentials() != null) ? this.getCredentials().adjustUrl(uri) : uri;
	}

	/**
	 * Gets the autodiscover url.
	 * 
	 * @param emailAddress
	 *            the email address
	 * @param url
	 *            the url
	 * @param validateRedirectionUrlCallback
	 *            the validate redirection url callback
	 * @param redirectionEmailAddresses
	 *            the redirection email addresses
	 * @param currentHop
	 *            the current hop
	 * @return the autodiscover url
	 * @throws Exception
	 *             the exception
	 */
	private URI getAutodiscoverUrl(String emailAddress,
			ExchangeVersion requestedServerVersion,
			IAutodiscoverRedirectionUrl validateRedirectionUrlCallback)
			throws Exception {

		AutodiscoverService autodiscoverService = new AutodiscoverService(this,
				requestedServerVersion);
		autodiscoverService
				.setRedirectionUrlValidationCallback(validateRedirectionUrlCallback);
		autodiscoverService.setEnableScpLookup(this.getEnableScpLookup());

		GetUserSettingsResponse response = autodiscoverService.getUserSettings(
				emailAddress, UserSettingName.InternalEwsUrl,
				UserSettingName.ExternalEwsUrl);

		switch (response.getErrorCode()) {
		case NoError:
			return this.getEwsUrlFromResponse(response, Boolean.TRUE);

		case InvalidUser:
			throw new ServiceRemoteException(String.format(Strings.InvalidUser,
					emailAddress));

		case InvalidRequest:
			throw new ServiceRemoteException(String.format(
					Strings.InvalidAutodiscoverRequest, response
							.getErrorMessage()));

		default:
			this.traceMessage(TraceFlags.AutodiscoverConfiguration, String
					.format("No EWS Url returned for user %s, "
							+ "error code is %s", emailAddress, response
							.getErrorCode()));

			throw new ServiceRemoteException(response.getErrorMessage());
		}
	}

	private URI getEwsUrlFromResponse(GetUserSettingsResponse response,
			boolean isExternal) throws URISyntaxException,
			AutodiscoverLocalException {
		String uriString;

		// Bug E14:59063 -- Figure out which URL to use: Internal or External.
		// Bug E14:67646 -- AutoDiscover may not return an external protocol.
		// First try external, then internal.
		// Bug E14:82650 -- Either protocol
		// may be returned without a configured URL.
		OutParam<String> outParam = new OutParam<String>();
		if ((isExternal && response.tryGetSettingValue(String.class,
				UserSettingName.ExternalEwsUrl, outParam))) {
			uriString = outParam.getParam();
			if (!(uriString == null || uriString.isEmpty())) {
				return new URI(uriString);
			}
		}
		if ((response.tryGetSettingValue(String.class,
				UserSettingName.InternalEwsUrl, outParam) || response
				.tryGetSettingValue(String.class,
						UserSettingName.ExternalEwsUrl, outParam))) {
			uriString = outParam.getParam();
			if (!(uriString == null || uriString.isEmpty())) {
				return new URI(uriString);
			}
		}

		// If Autodiscover doesn't return an
		// internal or external EWS URL, throw an exception.
		throw new AutodiscoverLocalException(
				Strings.AutodiscoverDidNotReturnEwsUrl);
	}

	// region Diagnostic Method -- Only used by test

	/**
	 * Executes the diagnostic method.
	 * 
	 * @param verb
	 *            The verb.
	 * @param parameter
	 *            The parameter.
	 * @throws Exception
	 */
	protected Document executeDiagnosticMethod(String verb, Node parameter)
			throws Exception {
		ExecuteDiagnosticMethodRequest request = new ExecuteDiagnosticMethodRequest(
				this);
		request.setVerb(verb);
		request.setParameter(parameter);

		return request.execute().getResponseAtIndex(0).getReturnValue();

	}

	// endregion

	// region Validation
	/**
	 * * Validates this instance.
	 * 
	 * @throws ServiceLocalException
	 *             the service local exception
	 */
	@Override
	public void validate() throws ServiceLocalException {
		super.validate();
		if (this.getUrl() == null) {
			throw new ServiceLocalException(Strings.ServiceUrlMustBeSet);
		}
	}

	// region Constructors

	/**
	 * Initializes a new instance of the <see cref="ExchangeService"/> class,
	 * targeting the specified version of EWS and scoped to the to the system's
	 * current time zone.
	 */
	public ExchangeService() {
		super();
	}

	/**
	 * * Initializes a new instance of the <see cref="ExchangeService"/> class,
	 * targeting the specified version of EWS and scoped to the system's current
	 * time zone.
	 * 
	 * @param requestedServerVersion
	 *            the requested server version
	 */
	public ExchangeService(ExchangeVersion requestedServerVersion) {
		super(requestedServerVersion);
	}

	/**
	 * Initializes a new instance of the <see cref="ExchangeService"/> class,
	 * targeting the specified version of EWS and scoped to the to the specified
	 * time zone.
	 * 
	 * @param requestedServerVersion
	 *            The version of EWS that the service targets.
	 * @param timeZone
	 *            The time zone to which the service is scoped.
	 */
	public ExchangeService(ExchangeVersion requestedServerVersion,
			TimeZone timeZone) {
		super(requestedServerVersion, timeZone);
	}

	// Utilities

	/**
	 * Prepare http web request.
	 * 
	 * @return the http web request
	 * @throws ServiceLocalException
	 *             the service local exception
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	protected HttpWebRequest prepareHttpWebRequest()
			throws ServiceLocalException, URISyntaxException {
		try {
			this. url = this.adjustServiceUriFromCredentials(this.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.prepareHttpWebRequestForUrl(url, this.getAcceptGzipEncoding(), true);
	}

	/**
	 * Processes an HTTP error response.
	 * 
	 * @param httpWebResponse
	 *            The HTTP web response.
	 * @param webException
	 *            The web exception
	 * @throws Exception
	 */
	@Override
	protected void processHttpErrorResponse(HttpWebRequest httpWebResponse,
			Exception webException) throws Exception {
		this.internalProcessHttpErrorResponse(httpWebResponse, webException,
				TraceFlags.EwsResponseHttpHeaders, TraceFlags.EwsResponse);
	}

	// Properties

	/***
	 * Gets the URL of the Exchange Web Services.
	 * 
	 * @return URL of the Exchange Web Services.
	 */
	public URI getUrl() {
		return url;
	}

	/***
	 * Sets the URL of the Exchange Web Services.
	 * 
	 * @param url
	 *            URL of the Exchange Web Services.
	 */
	public void setUrl(URI url) {
		this.url = url;
	}

	/**
	 * Gets the impersonated user id.
	 * 
	 * @return the impersonated user id
	 */
	public ImpersonatedUserId getImpersonatedUserId() {
		return impersonatedUserId;
	}

	/**
	 * Sets the impersonated user id.
	 * 
	 * @param impersonatedUserId
	 *            the new impersonated user id
	 */
	public void setImpersonatedUserId(ImpersonatedUserId impersonatedUserId) {
		this.impersonatedUserId = impersonatedUserId;
	}

	/**
	 * Gets the preferred culture.
	 * 
	 * @return the preferred culture
	 */
	public Locale getPreferredCulture() {
		return preferredCulture;
	}

	/**
	 * Sets the preferred culture.
	 * 
	 * @param preferredCulture
	 *            the new preferred culture
	 */
	public void setPreferredCulture(Locale preferredCulture) {
		this.preferredCulture = preferredCulture;
	}

	/**
	 * Gets the DateTime precision for DateTime values returned from Exchange
	 * Web Services.
	 * 
	 * @return the DateTimePrecision
	 */
	public DateTimePrecision getDateTimePrecision() {
		return this.dateTimePrecision;
	}
	
	/**
	 * Sets the DateTime precision for DateTime values 
	 * Web Services.
	 * 
	 * @return the DateTimePrecision
	 */
	public void setDateTimePrecision(DateTimePrecision d) {
		 this.dateTimePrecision = d;
	}

	/**
	 * Sets the DateTime precision for DateTime values returned from Exchange
	 * Web Services.
	 * 
	 * @param DateTimePrecisione
	 *            the new DateTimePrecision
	 */
	public void setPreferredCulture(DateTimePrecision dateTimePrecision) {
		this.dateTimePrecision = dateTimePrecision;
	}

	/**
	 * Gets the file attachment content handler.
	 * 
	 * @return the file attachment content handler
	 */
	public IFileAttachmentContentHandler getFileAttachmentContentHandler() {
		return this.fileAttachmentContentHandler;
	}

	/**
	 * Sets the file attachment content handler.
	 * 
	 * @param fileAttachmentContentHandler
	 *            the new file attachment content handler
	 */
	public void setFileAttachmentContentHandler(
			IFileAttachmentContentHandler fileAttachmentContentHandler) {
		this.fileAttachmentContentHandler = fileAttachmentContentHandler;
	}

	/**
	 * Gets the time zone this service is scoped to.
	 * 
	 * @return the unified messaging
	 */
	// public TimeZone getTimeZone() { return super.getTimeZone(); }

	/**
	 * * Provides access to the Unified Messaging functionalities.
	 * 
	 * @return the unified messaging
	 */
	public UnifiedMessaging getUnifiedMessaging() {
		if (this.unifiedMessaging == null) {
			this.unifiedMessaging = new UnifiedMessaging(this);
		}

		return this.unifiedMessaging;
	}

	/**
	 * Gets or sets a value indicating whether the AutodiscoverUrl method should
	 * perform SCP (Service Connection Point) record lookup when determining the
	 * Autodiscover service URL.
	 */
	public boolean getEnableScpLookup() {
		return this.enableScpLookup;
	}

	public void setEnableScpLookup(boolean value) {
		this.enableScpLookup = value;
	}

	/***
	 * Gets or sets a value indicating whether Exchange2007 compatibility mode
	 * is enabled. <remarks> In order to support E12 servers, the
	 * Exchange2007CompatibilityMode property can be used to indicate that we
	 * should use "Exchange2007" as the server version String rather than
	 * Exchange2007_SP1. </remarks>
	 */
	protected boolean getExchange2007CompatibilityMode() {
		return this.exchange2007CompatibilityMode;
	}

	protected void setExchange2007CompatibilityMode(boolean value) {
		this.exchange2007CompatibilityMode = value;
	}

	/**
	 * * Retrieves the definitions of the specified server-side time zones.
	 * 
	 * @param timeZoneIds
	 *            the time zone ids
	 * @return A Collection containing the definitions of the specified time
	 *         zones.
	 */
	public Collection<TimeZoneDefinition> getServerTimeZones(
			Iterable<String> timeZoneIds) {
		Date today = new Date();
		Collection<TimeZoneDefinition> timeZoneList = new ArrayList<TimeZoneDefinition>();
		for (String timeZoneId : timeZoneIds) {
			TimeZoneDefinition timeZoneDefinition = new TimeZoneDefinition();
			timeZoneList.add(timeZoneDefinition);
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
			timeZoneDefinition.id = timeZone.getID();
			timeZoneDefinition.name = timeZone.getDisplayName(timeZone
					.inDaylightTime(today), TimeZone.LONG);
			/*
			 * String shortName =
			 * timeZone.getDisplayName(timeZone.inDaylightTime(today),
			 * TimeZone.SHORT); String longName =
			 * timeZone.getDisplayName(timeZone.inDaylightTime(today),
			 * TimeZone.LONG); int rawOffset = timeZone.getRawOffset(); int hour
			 * = rawOffset / (60*60*1000); int min = Math.abs(rawOffset /
			 * (60*1000)) % 60; boolean hasDST = timeZone.useDaylightTime();
			 * boolean inDST = timeZone.inDaylightTime(today);
			 */

		}
		return timeZoneList;
	}

	/***
	 * Retrieves the definitions of all server-side time zones.
	 * 
	 * @return A Collection containing the definitions of the specified time
	 *         zones.
	 */
	public Collection<TimeZoneDefinition> getServerTimeZones() {
		Date today = new Date();
		Collection<TimeZoneDefinition> timeZoneList = new ArrayList<TimeZoneDefinition>();
		for (String timeZoneId : TimeZone.getAvailableIDs()) {
			TimeZoneDefinition timeZoneDefinition = new TimeZoneDefinition();
			timeZoneList.add(timeZoneDefinition);
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
			timeZoneDefinition.id = timeZone.getID();
			timeZoneDefinition.name = timeZone.getDisplayName(timeZone
					.inDaylightTime(today), TimeZone.LONG);
		}

		return timeZoneList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemicrosoft.exchange.webservices.AutodiscoverRedirectionUrlInterface#
	 * autodiscoverRedirectionUrlValidationCallback(java.lang.String)
	 */
	public boolean autodiscoverRedirectionUrlValidationCallback(
			String redirectionUrl) throws AutodiscoverLocalException {
		return defaultAutodiscoverRedirectionUrlValidationCallback(redirectionUrl);

	}

}
