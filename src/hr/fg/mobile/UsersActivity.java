package hr.fg.mobile;

import hr.fg.mobile.models.UserModel;
import hr.fg.mobile.plugins.ImageDownloader;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.UsersAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <h2>UsersActivity</h2> Activity for showing all users
 * 
 * @author Fenster Gang
 * 
 */
public class UsersActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.load_layout);

		JsonRequestLoader request = new JsonRequestLoader();

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.users_layout, null);
		LinearLayout l = (LinearLayout) v.findViewById(R.id.usersList);

		UsersAsyncTask usersTask = new UsersAsyncTask(c, new FgPrefs(),
				UsersActivity.this);
		usersTask.execute(new Object[] { request, l, v });

	}

	/**
	 * <h3>createUserModel</h3> Creates a user model for the user
	 * 
	 * @param users
	 *            JSON array from which we get the JSONObject user
	 * @param i
	 *            Index of the user
	 * @return UserModel for the user
	 * @throws JSONException
	 */
	public UserModel createUserModel(JSONArray users, int i)
			throws JSONException {
		JSONObject user = users.getJSONArray(0).getJSONObject(i)
				.optJSONObject("User");
		JSONArray posts = users.getJSONArray(0).getJSONObject(i)
				.optJSONArray("Post");
		int karma = users.getJSONObject(1).optInt(user.optString("id"));

		UserModel userView = new UserModel(user.optInt("id"),
				user.optString("username"), user.optString("email"),
				user.optString("created"), user.optString("profile_picture"),
				posts.length(), karma, 0);

		return userView;
	}

	/**
	 * <h3>getUserView</h3> Gets the user view for the single user
	 * 
	 * @param user
	 *            The user for which the user view is set
	 * @return The LinearLayout with the user
	 */
	public LinearLayout getUserView(final UserModel user) {

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.user_layout, null);

		v.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent i = new Intent(c, ProfileActivity.class);
				i.putExtra("user", user.getId());
				startActivity(i);
			}
		});

		if (!user.getProfilePic().equals("")) {
			ImageView profilka = (ImageView) v.findViewById(R.id.profilePic);
			ImageDownloader image = new ImageDownloader(c, false);
			image.download(
					"http://fenstergang.com/img/thumbs/50x50/"
							+ user.getProfilePic(), profilka);
		}

		TextView username = (TextView) v.findViewById(R.id.usersUsername);
		username.setText(user.getUsername());

		TextView posts = (TextView) v.findViewById(R.id.usersPosts);
		posts.setText(String.valueOf(user.getPostCount()) + " postova");

		TextView karma = (TextView) v.findViewById(R.id.usersKarma);
		karma.setText(String.valueOf(user.getVotesCount()) + " bodova karme");

		return (LinearLayout) v;
	}

	/**
	 * <h3>setLayout</h3> Sets the view as content view
	 * 
	 * @param view
	 *            The View to be set as content view
	 */
	public void setLayout(View view) {
		setContentView(view);
	}

	// HEADER METODE

	/**
	 * <h3>buksa</h3> Intents the activity PostsActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void buksa(View v) {
		Intent p = new Intent(c, PostsActivity.class);
		p.putExtra("pflag", "all");
		p.putExtra("userp", "0");
		startActivity(p);
	}

	/**
	 * <h3>banda</h3> Intents the activity UsersActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void banda(View v) {
		Intent p = new Intent(c, UsersActivity.class);
		startActivity(p);
	}

	/**
	 * <h3>profil</h3> Intents the activity ProfileActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void profil(View v) {
		Intent p = new Intent(c, ProfileActivity.class);
		p.putExtra("user", Integer.parseInt(pref.getPrefs("userId", c)));
		startActivity(p);
	}

	/**
	 * <h3>notify</h3> Intents the activity NotificationActivity
	 * 
	 * @param v
	 *            The View for the onClickListener
	 */
	public void notify(View v) {
		Intent p = new Intent(c, NotificationsActivity.class);
		startActivity(p);
	}

}
