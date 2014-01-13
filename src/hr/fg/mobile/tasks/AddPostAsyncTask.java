package hr.fg.mobile.tasks;

import hr.fg.mobile.PostsActivity;
import hr.fg.mobile.plugins.JsonRequestLoader;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * <h2>AddPostAsyncTask</h2> AsyncTask for adding a post to 'Buksa'
 * 
 * @author Fenster Gang
 * 
 */
public class AddPostAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * Used to define the pairs which are send with the request
	 */
	private List<NameValuePair> pairs;

	/**
	 * Used to define the request to the web server
	 */
	private JsonRequestLoader request;

	/**
	 * Used to define the context
	 */
	private Context context;

	/**
	 * Used to define the PostsActivity from which we use a method
	 */
	private PostsActivity activity;

	/**
	 * <h3>AddPostAsyncTask</h3> The constructor for the class AddPostAsyncTask
	 * 
	 * @param c
	 *            The Context to set
	 * @param a
	 *            The PostsActivity to set
	 */
	public AddPostAsyncTask(Context c, PostsActivity a) {
		this.context = c;
		this.activity = a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Boolean doInBackground(Object... params) {
		this.pairs = (List<NameValuePair>) params[0];
		this.request = (JsonRequestLoader) params[1];
		Boolean result = request.postRequestStatus(
				"http://fenstergang.com/posts/android_post_add", pairs);
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
			activity.buksa(null);
		} else {
			Toast.makeText(context, "Greška u spremanju posta",
					Toast.LENGTH_SHORT).show();
		}
	}

}
