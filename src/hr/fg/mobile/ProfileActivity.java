package hr.fg.mobile;

import hr.fg.mobile.models.UserModel;
import hr.fg.mobile.plugins.ImageDownloader;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.EditProfileFieldAsyncTask;
import hr.fg.mobile.tasks.ImageUploadAsyncTask;
import hr.fg.mobile.tasks.ProfileAsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h2>ProfileActivity</h2> Activity for showing the profile of a user
 * 
 * @author Fenster Gang
 * 
 */
public class ProfileActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the main View for the activity
	 */
	private View v;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/**
	 * Used to define the user id
	 */
	private int userId;

	/**
	 * Used to define the path of the image
	 */
	private String picturePath;

	/**
	 * Used to define the dialog which shows when uploading an image
	 */
	private ProgressDialog dialog;

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
		v = inflater.inflate(R.layout.profile_layout, null);

		int user = this.getIntent().getExtras().getInt("user");

		ProfileAsyncTask profileTask = new ProfileAsyncTask(c,
				ProfileActivity.this, pref);
		profileTask.execute(new Object[] { request, v, user });

	}

	/**
	 * <h3>createUserModel</h3> Creates a user model for the user
	 * 
	 * @param users
	 *            JSON array from which we get the JSONObject user
	 * @return UserModel of the user
	 * @throws JSONException
	 */
	public UserModel createUserModel(JSONArray users) throws JSONException {
		JSONObject user = users.getJSONObject(0).optJSONObject("User");
		int posts = users.getJSONObject(1).optInt("posts");
		int karma = users.getJSONObject(2).optInt("votes");
		int mentions = users.getJSONObject(3).optInt("mentions");

		UserModel userView = new UserModel(user.optInt("id"),
				user.optString("username"), user.optString("email"),
				user.optString("created"), user.optString("profile_picture"),
				posts, karma, mentions);

		return userView;
	}

	/**
	 * <h3>getProfileView</h3> Gets the profile view for the user
	 * 
	 * @param user
	 *            The user for which the profile view is shown
	 * @return The View for the user
	 */
	public View getProfileView(final UserModel user) {

		userId = user.getId();

		ImageView profilka = (ImageView) v.findViewById(R.id.profilePicture);

		if (!user.getProfilePic().equals("")) {

			ImageDownloader image = new ImageDownloader(c, false);
			image.download(
					"http://fenstergang.com/img/thumbs/250x250/"
							+ user.getProfilePic(), profilka);

		}

		profilka.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, 1);

				return false;
			}
		});

		TextView username = (TextView) v.findViewById(R.id.profileUsername);
		username.setText(user.getUsername());

		TextView email = (TextView) v.findViewById(R.id.profileEmail);
		email.setText(user.getEmail());

		if (String.valueOf(user.getId()).equals(pref.getPrefs("userId", c))) {
			username.setTag("username");
			username.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					editProfileField(v);
					return false;
				}

			});
			email.setTag("email");
			email.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					editProfileField(v);
					return false;
				}
			});
		}

		TextView reg = (TextView) v.findViewById(R.id.profileReg);
		reg.setText(user.getRegistration());

		TextView karma = (TextView) v.findViewById(R.id.profileKarma);
		karma.setText(String.valueOf(user.getVotesCount()));

		if (user.getPostCount() > 0) {
			Button posts = (Button) v.findViewById(R.id.profilePosts);
			posts.setText("Postovi: " + user.getPostCount());
			posts.setVisibility(View.VISIBLE);
			posts.setTag("users");
		}

		if (user.getMentCount() > 0) {
			Button mentions = (Button) v.findViewById(R.id.profileMentions);
			mentions.setText("Prozivi: " + user.getMentCount());
			mentions.setVisibility(View.VISIBLE);
			mentions.setTag("mentions");
		}

		if (String.valueOf(user.getId()).equals(pref.getPrefs("userId", c))) {
			Button fgposts = (Button) v.findViewById(R.id.profileFg);
			fgposts.setText("FG postovi");
			fgposts.setVisibility(View.VISIBLE);
			fgposts.setTag("fg");
		}

		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
			pairs.add(new BasicNameValuePair("data[User][user_id]", pref
					.getPrefs("userId", c)));
			pairs.add(new BasicNameValuePair("data[User][sessionKey]", pref
					.getPrefs("sessionKey", c)));
			pairs.add(new BasicNameValuePair("data[User][field]",
					"profile_picture"));

			dialog = ProgressDialog.show(c,
					getResources().getString(R.string.dialog),
					"Slika se šalje..");
			ImageUploadAsyncTask imgTask = new ImageUploadAsyncTask(c,
					ProfileActivity.this);
			imgTask.execute(new Object[] { picturePath, pairs, 3, null });

		}
	}

	/**
	 * <h3>intentPosts</h3> Starts the PostsActivity with some extra information
	 * sent with the intent
	 * 
	 * @param v
	 *            The View from which we get information to set pflag as extra
	 *            information
	 */
	public void intentPosts(View v) {
		Intent p = new Intent(c, PostsActivity.class);
		p.putExtra("pflag", v.getTag().toString());
		p.putExtra("userp", String.valueOf(userId));
		startActivity(p);
	}

	/**
	 * <h3>editProfileField</h3> Edits the profile fields and calls the
	 * editField method
	 * 
	 * @param v
	 *            The View from which the profile is edited
	 */
	public void editProfileField(View v) {
		final TextView text = (TextView) v;
		final Dialog editDialog = new Dialog(c);

		editDialog.setContentView(R.layout.edit_profile_field);
		editDialog.setTitle("Uredi " + v.getTag());
		editDialog.show();
		final EditText textBox = (EditText) editDialog
				.findViewById(R.id.editField);
		textBox.setText(text.getText().toString());
		Button yes = (Button) editDialog.findViewById(R.id.editFieldButtonYes);
		Button no = (Button) editDialog.findViewById(R.id.editFieldButtonNo);

		no.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				editDialog.dismiss();
			}
		});

		yes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String field = text.getTag().toString();
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
				pairs.add(new BasicNameValuePair("data[User][user_id]", pref
						.getPrefs("userId", c)));
				pairs.add(new BasicNameValuePair("data[User][sessionKey]", pref
						.getPrefs("sessionKey", c)));
				pairs.add(new BasicNameValuePair("data[User]["
						+ text.getTag().toString() + "]", textBox.getText()
						.toString()));
				pairs.add(new BasicNameValuePair("data[User][field]", field));

				Toast.makeText(c, "Rudarim..", Toast.LENGTH_SHORT).show();

				editField(text, editDialog, field, pairs, false);

			}

		});
	}

	/**
	 * <h3>editField</h3> Calls the EditProfileFieldAsyncTask to execute the
	 * editing
	 * 
	 * @param text
	 *            The TextView with the new text to be set
	 * @param editDialog
	 *            The Dialog which shows when executing
	 *            EditProfileFieldAsyncTask
	 * @param field
	 *            Tells us if we edited the username or profile picture
	 * @param pairs
	 *            The List<NameValuePair> for the EditProfileFieldAsyncTask,
	 *            variables for the request
	 * @param img
	 *            It tells whether we sent an image or not
	 */
	public void editField(final TextView text, final Dialog editDialog,
			String field, List<NameValuePair> pairs, boolean img) {
		if (dialog != null)
			dialog.cancel();
		EditProfileFieldAsyncTask fieldTask = new EditProfileFieldAsyncTask(c,
				ProfileActivity.this);
		fieldTask.execute(new Object[] { new JsonRequestLoader(), pairs, text,
				editDialog, field, img, pref});
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
