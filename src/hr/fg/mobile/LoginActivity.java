package hr.fg.mobile;

import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.LoginAsyncTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * <h2>LoginActivity</h2> Activity to login into the application
 * 
 * @author Fenster Gang
 * 
 */
public class LoginActivity extends Activity {

	/**
	 * Used to define the username input
	 */
	private EditText txtUserName;

	/**
	 * Used to define the password input
	 */
	private EditText txtPassword;

	/**
	 * Used to define the button for login
	 */
	private Button btnLogin;

	/**
	 * Used to define the button for registration
	 */
	private Button btnReg;

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
	 * @see
	 * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
	 * )
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		String username = this.getIntent().getStringExtra("username")
				.toString();

		String password = this.getIntent().getStringExtra("password")
				.toString();

		txtUserName = (EditText) this.findViewById(R.id.username);
		txtUserName.setText(username);
		txtPassword = (EditText) this.findViewById(R.id.password);
		txtPassword.setText(password);
		btnLogin = (Button) this.findViewById(R.id.login);
		btnReg = (Button) this.findViewById(R.id.reg);
		final FgPrefs pref = new FgPrefs();
		btnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
				pairs.add(new BasicNameValuePair("data[User][username]",
						txtUserName.getText().toString()));
				pairs.add(new BasicNameValuePair("data[User][password]",
						txtPassword.getText().toString()));
				JsonRequestLoader request = new JsonRequestLoader();

				LoginAsyncTask task = new LoginAsyncTask(c, pref, true);
				dialog = ProgressDialog.show(c,
						getResources().getString(R.string.dialog),
						getResources().getString(R.string.login_atempt), true,
						true);
				task.execute(new Object[] { request, pairs, LoginActivity.this });
			}
		});

		btnReg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(c, RegActivity.class);
				startActivity(i);
			}
		});
	}

	/**
	 * <h3>intentPosts</h3> Starts the posts activity
	 */
	public void intentPosts() {
		dialog.cancel();
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
}
