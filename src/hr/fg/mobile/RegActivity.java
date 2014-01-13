package hr.fg.mobile;

import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.RegAsyncTask;

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
import android.widget.Toast;

/**
 * <h2>RegActivity</h2> Activity for registering a new user
 * 
 * @author Fenster Gang
 * 
 */
public class RegActivity extends Activity {

	/**
	 * Used to define the EditText for the username of the new user
	 */
	private EditText txtUserName;

	/**
	 * Used to define the EditText for the password of the new user
	 */
	private EditText password1;

	/**
	 * Used to define the EditText for the repeated password of the new user
	 */
	private EditText password2;

	/**
	 * Used to define the EditText for the user who recommended you
	 */
	private EditText userRecommender;

	/**
	 * Used to define the EditText for the email of the new user
	 */
	private EditText txtEmail;

	/**
	 * Used to define the Button for submitting the registration
	 */
	private Button btnReg;

	/**
	 * Used to define the Button to go back to the login
	 */
	private Button backLogin;

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the ProgressDialog which shows when submitting the
	 * registration
	 */
	private ProgressDialog dialog;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
	 * )
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_reg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);

		txtUserName = (EditText) this.findViewById(R.id.username);
		password1 = (EditText) this.findViewById(R.id.password1);
		password2 = (EditText) this.findViewById(R.id.password2);
		txtEmail = (EditText) this.findViewById(R.id.email);
		userRecommender = (EditText) this.findViewById(R.id.regCode);
		btnReg = (Button) this.findViewById(R.id.regMe);
		backLogin = (Button) this.findViewById(R.id.backLogin);

		btnReg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String pass1 = password1.getText().toString();
				String pass2 = password2.getText().toString();
				String email = txtEmail.getText().toString();

				if ((pass1.equals(pass2))) {

					if (!txtUserName.getText().equals("") && !email.equals("")
							&& !userRecommender.getText().equals("")
							&& !pass1.equals("") && !pass2.equals("")) {

						if (pass1.length() >= 8) {

							if (email.contains("@") & email.contains(".")) {

								List<NameValuePair> pairs = new ArrayList<NameValuePair>(
										4);
								pairs.add(new BasicNameValuePair(
										"data[User][username]", txtUserName
												.getText().toString()));
								pairs.add(new BasicNameValuePair(
										"data[User][password]", pass1));
								pairs.add(new BasicNameValuePair(
										"data[User][email]", email));
								pairs.add(new BasicNameValuePair(
										"data[User][preporuka]",
										userRecommender.getText().toString()));
								pairs.add(new BasicNameValuePair(
										"data[User][password_repeat]", pass2));
								JsonRequestLoader request = new JsonRequestLoader();

								RegAsyncTask task = new RegAsyncTask(c, pref);
								dialog = ProgressDialog.show(c, getResources()
										.getString(R.string.dialog),
										"�aljem podatke za registraciju", true,
										true);
								task.execute(new Object[] { request, pairs,
										RegActivity.this, dialog });

							} else
								Toast.makeText(
										c,
										"Email mora biti u obliku npr. @domena.com",
										Toast.LENGTH_LONG).show();

						} else
							Toast.makeText(
									c,
									"Password mora sadrzavati najmanje 8 znakova",
									Toast.LENGTH_LONG).show();
					}

					else
						Toast.makeText(c, "Sva polja moraju biti ispunjena",
								Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(c, "Ponovi upis passworda",
							Toast.LENGTH_SHORT).show();

				}

			}
		});

		backLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(c, LoginActivity.class);
				startActivity(i);
			}
		});

	}

	/**
	 * <h3>redirectPost</h3> Starts the PostsActivity for showing all posts
	 */
	public void redirectPost() {
		Intent i = new Intent(c, PostsActivity.class);
		i.putExtra("pflag", "all");
		i.putExtra("userp", "0");
		startActivity(i);
	}

	/**
	 * <h3>redirectReg</h3> Starts the RegActivity
	 */
	public void redirectReg() {
		this.finish();
		Intent i = new Intent(c, RegActivity.class);
		startActivity(i);

	}
}
