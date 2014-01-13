package hr.fg.mobile.tasks;

import hr.fg.mobile.PostsActivity;
import hr.fg.mobile.models.PostModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <h2>PostsAsyncTask</h2> AsyncTask for displaying all posts on 'Buksa'
 * 
 * @author Fenster Gang
 * 
 */
public class PostsAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * Used to define the layout which holds the posts
	 */
	private LinearLayout l;

	/**
	 * Used to define the View which holds the posts
	 */
	private View postsLayout;

	/**
	 * Used to define the PostsActivty
	 */
	private PostsActivity main;

	/**
	 * Used to define the button for loading more posts
	 */
	private Button loadMore;

	/**
	 * Used to define if it is the first display of 'Buksa' or not
	 */
	private Boolean first;

	/**
	 * Used to define which posts to show
	 */
	private String pflag;

	/**
	 * Used to define the id of the user
	 */
	private String userp;

	/**
	 * <h3>PostsAsyncTask</h3> The constructor for the class PostsAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 * @param activity
	 *            The PostsActivity to set
	 */
	public PostsAsyncTask(Context c, FgPrefs pref, PostsActivity activity) {
		this.context = c;
		this.pref = pref;
		this.main = activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(Object... params) {
		request = (JsonRequestLoader) params[0];
		this.l = (LinearLayout) params[1];
		this.loadMore = (Button) params[2];
		this.first = (Boolean) params[3];
		this.postsLayout = (View) params[4];
		this.pflag = (String) params[5];
		this.userp = (String) params[6];

		Boolean result = request
				.getRequestStatus("http://fenstergang.com/posts/android_posts?key="
						+ pref.getPrefs("sessionKey", context)
						+ "&userId="
						+ pref.getPrefs("userId", context)
						+ "&offset="
						+ PostsActivity.offset
						+ "&pflag="
						+ pflag
						+ "&userp="
						+ userp);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(Params[])
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			l.removeView(loadMore);
			JSONArray posts = null;
			posts = request.getRequestData();
			PostsActivity.offset += 20;
			PostsActivity.postLenght = posts.length();

			for (int i = 0; i < posts.length(); i++) {

				try {

					PostModel postView = main.createPostModel(posts, i);

					LinearLayout text = main.getPostView(postView);
					l.addView(text);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			if (PostsActivity.postLenght == 20) {
				loadMore.setText("Daj još");
				loadMore.setEnabled(true);
				l.addView(loadMore);
			}
			if (!first) {
				Toast.makeText(
						context,
						"Uèitano još " + PostsActivity.postLenght + " postova.",
						Toast.LENGTH_SHORT).show();
			}
			main.setLayout(postsLayout);

		} else {
			Toast.makeText(
					context,
					"Greska kod logiranja. Logiranje na fensergang.com sa vise ureðaja!",
					Toast.LENGTH_LONG).show();
			main.postsError();
		}

	}

}
