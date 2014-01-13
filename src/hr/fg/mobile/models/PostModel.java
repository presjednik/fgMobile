package hr.fg.mobile.models;

import org.json.JSONArray;

/**
 * <h2>PostModel</h2> Defines which elements a post must have
 * 
 * @author Fenster Gang
 * 
 */
public class PostModel {

	/**
	 * Used to define the post id
	 */
	private int postId;

	/**
	 * Used to define the user name of the user
	 */
	private String username;

	/**
	 * Used to define the time when the post was last modified
	 */
	private String time;

	/**
	 * Used to define the content of the post
	 */
	private String content;

	/**
	 * Used to define the number of replies for the post
	 */
	private int repliesNo;

	/**
	 * Used to define the number of votes for the post
	 */
	private int votesNo;

	/**
	 * Used to define the user id
	 */
	private int userId;

	/**
	 * Used to define the number of images for the post
	 */
	private int imgNo;

	/**
	 * Used to define the JSONArray which holds the votes of the post
	 */
	private JSONArray votes;

	/**
	 * Used to define the JSONArray which holds all the images for the post
	 */
	private JSONArray imgs;

	/**
	 * <h3>PostModel</h3> The constructor for the class PostModel
	 * 
	 * @param postId
	 *            The post id to set
	 * @param username
	 *            The user name to set
	 * @param time
	 *            The time to set
	 * @param content
	 *            The content of the post to set
	 * @param repliesNo
	 *            The number of replies to the post to set
	 * @param votes
	 *            Votes to the post to set
	 * @param userId
	 *            The user id to set
	 * @param imgs
	 *            Images for the post to set
	 */
	public PostModel(int postId, String username, String time, String content,
			int repliesNo, JSONArray votes, int userId, JSONArray imgs) {

		this.setPostId(postId);
		this.setUsername(username);
		this.setTime(time);
		this.setContent(content);
		this.setRepliesNo(repliesNo);
		this.setVotesNo(votes.length());
		this.setVotes(votes);
		this.setUserId(userId);
		this.setImgs(imgs);
		this.setImgNo(imgs.length());
	}

	/**
	 * <h3>getUserId</h3> Gets the id of the user
	 * 
	 * @return The id of the user
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * <h3>setUserId</h3> Sets the userId as the id of the user
	 * 
	 * @param userId
	 *            The userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * <h3>getVotes</h3> Gets the votes for the post
	 * 
	 * @return Votes for the post
	 */
	public JSONArray getVotes() {
		return votes;
	}

	/**
	 * <h3>setVotes</h3> Sets the votes of the post
	 * 
	 * @param votes
	 *            The votes to set
	 */
	public void setVotes(JSONArray votes) {
		this.votes = votes;
	}

	/**
	 * <h3>getPostId</h3> Gets the ID of the post
	 * 
	 * @return The ID of the post
	 */
	public int getPostId() {
		return postId;
	}

	/**
	 * <h3>setPostId</h3> Sets the ID of the post
	 * 
	 * @param postId
	 *            The ID of the post to set
	 */
	public void setPostId(int postId) {
		this.postId = postId;
	}

	/**
	 * <h3>getUsername</h3> Gets the name of the user who created the post
	 * 
	 * @return The name of the user
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <h3>setUsername</h3> Sets the name of the user who created the post
	 * 
	 * @param username
	 *            The name of the user to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * <h3>getTime</h3> Gets the time when the post was last modified
	 * 
	 * @return The time when the post was last modified
	 */
	public String getTime() {
		return time;
	}

	/**
	 * <h3>setTime</h3> Sets the time when the post was last modified
	 * 
	 * @param time
	 *            The time when the post was last modified to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * <h3>getContent</h3> Gets the content of the post
	 * 
	 * @return The content of the post
	 */
	public String getContent() {
		return content;
	}

	/**
	 * <h3>setContent</h3> Sets the content of the post
	 * 
	 * @param content
	 *            The content of the post to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * <h3>getRepliesNo</h3> Gets the number of replies for the post
	 * 
	 * @return The number of replies for the post
	 */
	public int getRepliesNo() {
		return repliesNo;
	}

	/**
	 * <h3>setRepliesNo</h3> Sets the number of replies for the post
	 * 
	 * @param repliesNo
	 *            The number of replies for the post to set
	 */
	public void setRepliesNo(int repliesNo) {
		this.repliesNo = repliesNo;
	}

	/**
	 * <h3>getVotesNo</h3> Gets the number of votes for the post
	 * 
	 * @return The number of votes for the post
	 */
	public int getVotesNo() {
		return votesNo;
	}

	/**
	 * <h3>setVotesNo</h3> Sets he number of votes for the post
	 * 
	 * @param votesNo
	 *            The number of votes for the post to set
	 */
	public void setVotesNo(int votesNo) {
		this.votesNo = votesNo;
	}

	/**
	 * <h3>getImgNo</h3> Gets the number of images for the post
	 * 
	 * @return The number of images for the post
	 */
	public int getImgNo() {
		return imgNo;
	}

	/**
	 * <h3>setImgNo</h3> Sets the number of images for the post
	 * 
	 * @param imgNo
	 *            The number of images for the post to set
	 */
	public void setImgNo(int imgNo) {
		this.imgNo = imgNo;
	}

	/**
	 * <h3>getImgs</h3> Gets the images for the post
	 * 
	 * @return Images for the post
	 */
	public JSONArray getImgs() {
		return imgs;
	}

	/**
	 * <h3>setImgs</h3> Sets the images for the post
	 * 
	 * @param imgs
	 *            Images for the post to set
	 */
	public void setImgs(JSONArray imgs) {
		this.imgs = imgs;
	}

}
