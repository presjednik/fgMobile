package hr.fg.mobile;

import hr.fg.mobile.models.NotifyModel;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.DeleteNotifyAsyncTask;
import hr.fg.mobile.tasks.NotifyAsyncTask;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h2>NotificationActivity</h2> Activity for showing notifications and votes
 * 
 * @author Fenster Gang
 * 
 */
public class NotificationsActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/**
	 * Used to define the View which holds a single notification
	 */
	private View singleNotify;

	/**
	 * Used to define the Button to delete all notification
	 */
	private Button delAll;

	/**
	 * Used to define the number of notification
	 */
	public int notifyNum;

	/**
	 * <h3>getSingleNotify</h3> Returns the singleNotify
	 * 
	 * @return The singleNotify
	 */
	public View getSingleNotify() {
		return singleNotify;
	}

	/**
	 * <h3>setSingleNotify</h3> Sets the <b>this</b>.singleNotify to
	 * singleNotify
	 * 
	 * @param singleNotify
	 *            The singleNotify to be set
	 */
	public void setSingleNotify(View singleNotify) {
		this.singleNotify = singleNotify;
	}

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

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View notifyLayout = inflater.inflate(R.layout.notifications_layout,
				null);
		LinearLayout l = (LinearLayout) notifyLayout
				.findViewById(R.id.notifyLayout);
		delAll = (Button) notifyLayout.findViewById(R.id.notifyDelAll);

		delAll.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(c, "Brišem..", Toast.LENGTH_SHORT).show();
				DeleteNotifyAsyncTask delTask = new DeleteNotifyAsyncTask(c,
						NotificationsActivity.this, pref);
				delTask.execute(new Object[] { null, "0", "0", false, null,
						new JsonRequestLoader() });
			}
		});

		l.removeView(delAll);

		NotifyAsyncTask notifyTask = new NotifyAsyncTask(c, pref);
		notifyTask.execute(new JsonRequestLoader(), NotificationsActivity.this,
				l, delAll, notifyLayout);

	}

	/**
	 * <h3>createNotiyModel</h3> Creates a notification model for one
	 * notification
	 * 
	 * @param poruke
	 *            JSON array of the notifications
	 * @param i
	 *            Index of the single notification
	 * @return The NotifyModel of the single notification on the index <b>i</b>
	 * @throws JSONException
	 */
	public NotifyModel createNotifyModel(JSONArray poruke, int i)
			throws JSONException {
		JSONObject notifObject = poruke.getJSONObject(i).optJSONObject(
				"Notification");
		int id = notifObject.optInt("id");
		int senderId = notifObject.optInt("sender_id");
		int recipientId = notifObject.optInt("recipient_id");
		int postId = notifObject.optInt("post_id");
		String type = notifObject.optString("type");

		int parentId = poruke.getJSONObject(i).optJSONObject("Post")
				.optInt("parent_id");

		String senderName = poruke.getJSONObject(i).optJSONObject("Sender")
				.optString("username");

		NotifyModel notification = new NotifyModel(id, senderId, senderName,
				recipientId, postId, parentId, type);

		return notification;
	}

	/**
	 * <h3>getNotifyView</h3> Gets the notification view for a single
	 * notification
	 * 
	 * @param notification
	 *            Single notification for which to get the view
	 * @return Layout which holds the single notification
	 */
	public LinearLayout getNotifyView(final NotifyModel notification) {

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.notification, null);

		final LinearLayout single = (LinearLayout) v
				.findViewById(R.id.singleNotify);
		TextView message = (TextView) single.findViewById(R.id.notifyText);
		final Button del = (Button) single.findViewById(R.id.notifyDelete);

		final String type = notification.getType();
		String senderName = notification.getSenderName();
		String postId = String.valueOf(notification.getPostId());
		String parentId = String.valueOf(notification.getParentId());

		del.setTag(String.valueOf(notification.getId()));

		if (type.equals("fg")) {
			message.setText(senderName + " je objavio novi fg post");
			if (parentId.equals("0")) {
				message.setTag(postId);
			} else
				message.setTag(parentId);
		} else if (type.equals("reply")) {
			message.setText("Novi odgovor na #" + postId);
			message.setTag(parentId);
		} else if (type.equals("mention")) {
			message.setText(senderName + " te spomenuo u #" + postId);
			if (parentId.equals("0")) {
				message.setTag(postId);
			} else
				message.setTag(parentId);
		} else if (type.equals("vote")) {
			message.setText(senderName + " je hracnuo na #" + postId);
			if (parentId.equals("0")) {
				message.setTag(postId);
			} else
				message.setTag(parentId);
		}

		message.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String id = v.getTag().toString();
				setSingleNotify(single);
				Toast.makeText(c, "Žimtra..", Toast.LENGTH_SHORT).show();
				DeleteNotifyAsyncTask delTask = new DeleteNotifyAsyncTask(c,
						NotificationsActivity.this, pref);
				delTask.execute(new Object[] { getSingleNotify(),
						del.getTag().toString(), id, true, delAll,
						new JsonRequestLoader(), });
			}

		});

		del.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String id = v.getTag().toString();
				setSingleNotify(single);
				Toast.makeText(c, "Brišem..", Toast.LENGTH_SHORT).show();
				DeleteNotifyAsyncTask delTask = new DeleteNotifyAsyncTask(c,
						NotificationsActivity.this, pref);
				delTask.execute(new Object[] { getSingleNotify(), id, "0",
						false, delAll, new JsonRequestLoader() });
			}
		});
		return (LinearLayout) v;
	}

	/**
	 * <h3>intentComment</h3> Starts the CommentActivity
	 * 
	 * @param id
	 *            The postId on which to intent
	 */
	public void intentComment(String id) {
		Intent i = new Intent(c, CommentActivity.class);
		i.putExtra("postId", id);
		startActivity(i);
	}

	/**
	 * <h3>setLayout</h3> Sets the <b>view</b> as content view
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
