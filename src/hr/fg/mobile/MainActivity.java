package hr.fg.mobile;

import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.LoginAsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

/**
 * <h2>MainActivity</h2> The main activity for the application
 * 
 * @author Fenster Gang
 * 
 */
public class MainActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the dialog which shows when logging into the application
	 */
	private ProgressDialog dialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (!isOnline()) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog
					.setTitle(getResources().getString(R.string.no_internet));
			alertDialog.setMessage(getResources().getString(
					R.string.conect_internet));
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							android.os.Process.killProcess(android.os.Process
									.myPid());
						}
					});
			alertDialog.show();
		} else {

			FgPrefs pref = new FgPrefs();
			String username = pref.getPrefs("username", c);
			String password = pref.getPrefs("password", c);
			String sessionKey = pref.getPrefs("sessionKey", c);

			if ((!username.equals("") && !password.equals("")) || !sessionKey.equals("")) {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
				pairs.add(new BasicNameValuePair("data[User][username]",
						username));
				pairs.add(new BasicNameValuePair("data[User][password]",
						password));
				JsonRequestLoader request = new JsonRequestLoader();

				LoginAsyncTask task = new LoginAsyncTask(c, pref, false);
				dialog = ProgressDialog.show(c,
						getResources().getString(R.string.dialog),
						getString(R.string.login_atempt), true, true);
				task.execute(new Object[] { request, pairs, MainActivity.this });

			} else {
				wrongLogin(username, password);
			}
		}
	}

	/**
	 * <h3>wrongLogin</h3> Returns back to login
	 */
	public void wrongLogin(String username, String password) {
		this.finish();
		Intent i = new Intent(c, LoginActivity.class);
		i.putExtra("username", username);
		i.putExtra("password", password);
		startActivity(i);
	}

	/**
	 * <h3>intentPosts</h3> Starts the PostsActivity
	 */
	public void intentPosts() {
		dialog.cancel();
		this.finish();
		Intent i = new Intent(c, PostsActivity.class);
		i.putExtra("pflag", "all");
		i.putExtra("userp", "0");
		startActivity(i);
	}

	/**
	 * <h3>loginRedirect</h3> Returns back to login
	 */
	public void loginRedirect(String username, String password) {
		dialog.cancel();
		this.finish();
		Intent i = new Intent(c, LoginActivity.class);
		i.putExtra("username", username);
		i.putExtra("password", password);
		startActivity(i);
	}

	/**
	 * <h3>isOnline</h3> Gets the user connection status (online or off line)
	 * 
	 * @return Is the user connected to the Internet or not
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
