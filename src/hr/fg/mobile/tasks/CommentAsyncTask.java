package hr.fg.mobile.tasks;

import hr.fg.mobile.CommentActivity;
import hr.fg.mobile.models.PostModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * <h2>CommentAsyncTask</h2> AsyncTask for displaying a comment and all of his
 * votes and comments
 * 
 * @author Fenster Gang
 * 
 */
public class CommentAsyncTask extends AsyncTask<Object, Void, Boolean> {

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
	 * Used to define the layout which holds the comments
	 */
	private LinearLayout l;

	/**
	 * Used to define the CommentActivity from which we use a method
	 */
	private CommentActivity main;

	/**
	 * Used to define the main ScrollView
	 */
	private ScrollView sv;

	/**
	 * Used to define the id of the post
	 */
	private String postId;

	/**
	 * <h3>CommentAsyncTask</h3> The constructor for the class CommentAsyncTask
	 * 
	 * @param c
	 *            The context to set
	 * @param pref
	 *            Preference to set
	 * @param activity
	 *            The CommentActivity to set
	 */
	public CommentAsyncTask(Context c, FgPrefs pref, CommentActivity activity) {
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
		this.sv = (ScrollView) params[2];
		this.postId = (String) params[3];

		Boolean result = request
				.getRequestStatus("http://fenstergang.com/posts/android_post_view?key="
						+ pref.getPrefs("sessionKey", context)
						+ "&userId="
						+ pref.getPrefs("userId", context)
						+ "&postId="
						+ postId);

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

			try {

				JSONArray array = request.getRequestData();
				JSONObject o = array.getJSONObject(0).optJSONObject("Post");
				JSONObject u = array.getJSONObject(0).optJSONObject("User");
				JSONArray childPost = array.getJSONObject(0).getJSONArray(
						"ChildPost");
				JSONArray votes = array.getJSONObject(0).getJSONArray("Vote");
				JSONArray imgs = array.getJSONObject(0).getJSONArray("Image");
				PostModel model = new PostModel(o.optInt("id"),
						u.optString("username"), o.optString("created"),
						o.optString("content"), childPost.length(), votes,
						o.optInt("user_id"), imgs);

				LinearLayout proba = new LinearLayout(context);

				l.setOrientation(LinearLayout.VERTICAL);

				proba = main.getPostView(model, true);
				l.addView(proba);

				for (int i = 0; i < childPost.length(); i++) {
					proba = main
							.getPostView(
									new PostModel(childPost.getJSONObject(i)
											.optInt("id"), childPost
											.getJSONObject(i)
											.optJSONObject("User")
											.optString("username"), childPost
											.getJSONObject(i).optString(
													"created"), childPost
											.getJSONObject(i).optString(
													"content"), 0, childPost
											.getJSONObject(i).getJSONArray(
													"Vote"),
											childPost.getJSONObject(i)
													.optJSONObject("User")
													.optInt("id"), childPost
													.getJSONObject(i)
													.getJSONArray("Image")),
									false);
					l.addView(proba);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			sv.addView(l);
			main.setLayout(sv);
		} else {
			main.buksa(new View(context));
		}

	}

}
