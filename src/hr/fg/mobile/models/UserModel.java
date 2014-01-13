package hr.fg.mobile.models;

/**
 * <h2>UserModel</h2> Defines which elements a user must have
 * 
 * @author Fenster Gang
 * 
 */
public class UserModel {

	/**
	 * Used to define the id of the user
	 */
	private int id;

	/**
	 * Used to define the username of the user
	 */
	private String username;

	/**
	 * Used to define the email of the user
	 */
	private String email;

	/**
	 * Used to define the time of the registration
	 */
	private String registration;

	/**
	 * Used to define the name of the profile picture
	 */
	private String profilePic;

	/**
	 * Used to define the number of post the user has posted
	 */
	private int postCount;

	/**
	 * Used to define the number of votes for the users posts
	 */
	private int votesCount;

	/**
	 * Used to define the number of calls
	 */
	private int mentCount;

	/**
	 * <h3>UserModel</h3> The constructor for the class UserModel
	 * 
	 * @param id
	 *            The id of the user
	 * @param username
	 *            The username of the user
	 * @param email
	 *            The email of the user
	 * @param registration
	 *            Registration time
	 * @param profilePic
	 *            Name of the profile picture
	 * @param postCount
	 *            Number of posts posted
	 * @param votesCount
	 *            Number of votes for the posts the user has posted
	 * @param mentCount
	 *            Number of met
	 */
	public UserModel(int id, String username, String email,
			String registration, String profilePic, int postCount,
			int votesCount, int mentCount) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.registration = registration;
		this.profilePic = profilePic;
		this.postCount = postCount;
		this.votesCount = votesCount;
		this.mentCount = mentCount;
	}

	/**
	 * <h2>getMentCount</h2> Gets the number of calls
	 * 
	 * @return The number of calls
	 */
	public int getMentCount() {
		return mentCount;
	}

	/**
	 * <h2>setMentCount</h2> Sets the number of calls
	 * 
	 * @param mentCount
	 *            Number of calls to set
	 */
	public void setMentCount(int mentCount) {
		this.mentCount = mentCount;
	}

	/**
	 * <h3>getId</h3> Gets the id of the user
	 * 
	 * @return The id
	 */
	public int getId() {
		return id;
	}

	/**
	 * <h3>setId</h3> Sets the <b>this</b>.id as id
	 * 
	 * @param id
	 *            The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * <h3>getUsername</h3> Gets the username of the user
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <h3>setUsername</h3> Sets the <b>this</b>.username as username
	 * 
	 * @param username
	 *            The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * <h3>getEmail</h3> Gets the email of the user
	 * 
	 * @return The email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <h3>setEmail</h3> Sets the <b>this</b>.email as email
	 * 
	 * @param email
	 *            The email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * <h3>getProfilePic</h3> Gets the profile picture of the user
	 * 
	 * @return The profile picture
	 */
	public String getProfilePic() {
		return profilePic;
	}

	/**
	 * <h3>setProfilePic</h3> Sets the <b>this</b>.profilePic as profilePic
	 * 
	 * @param profilePic
	 *            The name of the profile picture to set
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	/**
	 * <h3>getPostCount</h3> Gets the number of posts the user has posted
	 * 
	 * @return The number of posts
	 */
	public int getPostCount() {
		return postCount;
	}

	/**
	 * <h3>setPostCount</h3> Sets the <b>this</b>.postCount as postCount
	 * 
	 * @param postCount
	 *            The number of posts to set
	 */
	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	/**
	 * <h3>getVotesCount</h3> Gets the number of votes for the posts from the
	 * user
	 * 
	 * @return The number of votes
	 */
	public int getVotesCount() {
		return votesCount;
	}

	/**
	 * <h3>setVotesCount</h3> Sets the <b>this</b>.votesCount as votesCount
	 * 
	 * @param votesCount
	 *            The number of votes for the users posts to set
	 */
	public void setVotesCount(int votesCount) {
		this.votesCount = votesCount;
	}

	/**
	 * <h3>getRegistration</h3> Gets the registration time of the user
	 * 
	 * @return The registration time
	 */
	public String getRegistration() {
		return registration;
	}

	/**
	 * <h3>setRegistration</h3> Sets the <b>this</b>.registration as
	 * registration
	 * 
	 * @param registration
	 *            The registration time to set
	 */
	public void setRegistration(String registration) {
		this.registration = registration;
	}
}
