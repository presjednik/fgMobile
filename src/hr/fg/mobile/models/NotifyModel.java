package hr.fg.mobile.models;

/**
 * <h2>NotifyModel</h2> Defines which elements a notification must have
 * 
 * @author Fenster Gang
 * 
 */
public class NotifyModel {

	/**
	 * Used to define the id of the notification
	 */
	private int id;

	/**
	 * Used to define the id of the user who sent the notification
	 */
	private int senderId;

	/**
	 * Used to define the username of the sender who sent the notification
	 */
	private String senderName;

	/**
	 * Used to define the id of the user who receives the notification
	 */
	private int recipientId;

	/**
	 * Used to define the id of the post or comment for which the notification
	 * is sent
	 */
	private int postId;

	/**
	 * Used to define the id of the parent post
	 */
	private int parentId;

	/**
	 * Used to define the type of the post
	 */
	private String type;

	/**
	 * <h3>NotifyModel</h3> The constructor for the class NotifyModel
	 * 
	 * @param id
	 *            The id to set
	 * @param senderId
	 *            The sender id to set
	 * @param senderName
	 *            The username of the sender to set
	 * @param recipientId
	 *            The recipient id to set
	 * @param postId
	 *            The post id to set
	 * @param parentId
	 *            The id of the parent post to set
	 * @param type
	 *            The type of the post to set
	 */
	public NotifyModel(int id, int senderId, String senderName,
			int recipientId, int postId, int parentId, String type) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
		this.recipientId = recipientId;
		this.postId = postId;
		this.parentId = parentId;
		this.type = type;
	}

	/**
	 * <h3>getId</h3> Gets the id of the notification
	 * 
	 * @return The id of the notification
	 */
	public int getId() {
		return id;
	}

	/**
	 * <h3>setId</h3> Sets the id as the id of the notification
	 * 
	 * @param id
	 *            The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * <h3>getSenderId</h3> Gets the sender id
	 * 
	 * @return The senderId
	 */
	public int getSenderId() {
		return senderId;
	}

	/**
	 * <h3>setSenderId</h3> Sets the senderId as <b>this</b>.senderId
	 * 
	 * @param senderId
	 *            The senderId to set
	 */
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	/**
	 * <h3>getSenderName</h3> Gets the username of the sender
	 * 
	 * @return The senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * <h3>setSenderName</h3> Sets the senderName as the username of the sender
	 * 
	 * @param senderName
	 *            The senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * <h3>getRecipientId</h3> Gets the id of the recipient
	 * 
	 * @return The recipientId
	 */
	public int getRecipientId() {
		return recipientId;
	}

	/**
	 * <h3>setRecipientID</h3> Sets the id of the recipient
	 * 
	 * @param recipientId
	 *            The recipientId to set
	 */
	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
	}

	/**
	 * <h3>getPostId</h3> Gets the id of the post
	 * 
	 * @return The postId
	 */
	public int getPostId() {
		return postId;
	}

	/**
	 * <h3>setPostId</h3> Sets the id of the post as postId
	 * 
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId(int postId) {
		this.postId = postId;
	}

	/**
	 * <h3>getParentId</h3> Gets the id of the parent post
	 * 
	 * @return The parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * <h3>setParentId</h3> Sets the id of the parent post as parentId
	 * 
	 * @param parentId
	 *            The parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * <h3>getType</h3> Gets type of the post
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * <h3>setType</h3> Sets the type of the post as type
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
