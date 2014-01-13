package hr.fg.mobile.tasks;

import hr.fg.mobile.ProfileActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h2>EditProfileFieldAsyncTask</h2> AsyncTask for editing profile of the user
 * 
 * @author Fenster Gang
 * 
 */
public class EditProfileFieldAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the TextView with the new text
	 */
	private TextView field;

	/**
	 * Used to define the Dialog which shows when editing the profile
	 */
	private Dialog d;

	/**
	 * Used to define the context
	 */
	private Context c;

	/**
	 * Used to define if we edited the username or profile picture
	 */
	private String f;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define if we sent a image or not
	 */
	private boolean img;

	/**
	 * Used to define the ProfileActivity from which we call a method
	 */
	private ProfileActivity main;

	/**
	 * <h2>EditProfileFieldAsyncTask</h2> The constructor for the class
	 * EditProfileFieldAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param main
	 *            The ProfileActivity to set
	 */
	public EditProfileFieldAsyncTask(Context c, ProfileActivity main) {
		this.c = c;
		this.main = main;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Boolean doInBackground(Object... params) {

		request = (JsonRequestLoader) params[0];
		pairs = (List<NameValuePair>) params[1];
		field = (TextView) params[2];
		d = (Dialog) params[3];
		f = (String) params[4];
		img = (Boolean) params[5];
		pref = (FgPrefs) params[6];

		Boolean result = request.postRequestStatus(
				"http://fenstergang.com/users/android_field_edit", pairs);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (result) {
			JSONObject data = request.postRequestData();
			String val = data.optString("edit").toString();
			if (!img) {
				field.setText(val);
				d.dismiss();
			}
			if (f.equals("username")) {
				pref.setPref("username", val, c);
			} else if (f.equals("profile_picture")) {
				pref.setPref("p_profile", val, c);
				System.out.println(val);
				main.profil(new View(c));
			}

		} else {
			Toast.makeText(c, "Nevalidan unos", Toast.LENGTH_SHORT).show();
		}
	}

}
