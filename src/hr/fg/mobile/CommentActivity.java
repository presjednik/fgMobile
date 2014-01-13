package hr.fg.mobile;

import hr.fg.mobile.models.PostModel;
import hr.fg.mobile.plugins.ImageDownloader;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.AddCommentAsyncTask;
import hr.fg.mobile.tasks.CommentAsyncTask;
import hr.fg.mobile.tasks.DeletePostAsyncTask;
import hr.fg.mobile.tasks.EditPostAsyncTask;
import hr.fg.mobile.tasks.ImageUploadAsyncTask;
import hr.fg.mobile.tasks.VoteAsyncTask;
import hr.fg.mobile.tasks.VotersAsyncTask;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <h2>CommentActivity</h2> Activity for showing comment with all his replies
 * and votes
 * 
 * @author Fenster Gang
 * 
 */
public class CommentActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the form for writing a new comment
	 */
	public static LinearLayout form;

	/**
	 * Used to define the editText field for writing a new comment
	 */
	public static EditText commentText;

	/**
	 * Used to define the layout which holds the comments
	 */
	private LinearLayout l;

	/**
	 * Used to define the main ScrollView
	 */
	public static ScrollView sv;

	/**
	 * Used to define the post ID
	 */
	public static int postId;

	/**
	 * Used to define the name of the image (if there are images)
	 */
	public static String imageName;

	/**
	 * Used to define the preferences for the login
	 */
	private FgPrefs pref = new FgPrefs();

	/**
	 * Used to define the bundle which holds information about a comment that
	 * needs to be edited or deleted
	 */
	public Bundle bundle = new Bundle();

	/**
	 * Used to define the TextView for editing a post
	 */
	private TextView editContentText;

	/**
	 * Used to define the LinearLayout which needs to be deleted
	 */
	private LinearLayout deleteLayout;

	/**
	 * Used to define the path of the image
	 */
	private String picturePath = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		showComments();
	}

	/**
	 * <h3>showComments</h3> Gets comments from Web-service and display them
	 */
	private void showComments() {
		sv = new ScrollView(c);
		l = new LinearLayout(c);
		String postId = this.getIntent().getStringExtra("postId").toString();
		CommentActivity.postId = Integer.parseInt(postId);
		this.setContentView(R.layout.load_layout);

		final JsonRequestLoader request = new JsonRequestLoader();

		CommentAsyncTask commentTask = new CommentAsyncTask(this,
				new FgPrefs(), CommentActivity.this);

		commentTask.execute(new Object[] { request, l, sv, postId });

	}

	/**
	 * <h3>setLayout</h3> Adds the scrollView to a LinearLayout, and sets it as
	 * content view
	 * 
	 * @param scrollView
	 *            The scrollView to be added to LinearLayout
	 */
	public void setLayout(ScrollView scrollView) {

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.activity_comment_layout, null);

		ScrollView s = (ScrollView) v.findViewById(R.id.comments_scroll);
		form = (LinearLayout) v.findViewById(R.id.comment_add_form);

		commentText = (EditText) form.findViewById(R.id.newComment);

		s = scrollView;
		LinearLayout l = (LinearLayout) v.findViewById(R.id.l);

		final Button buttonNewComment = (Button) form
				.findViewById(R.id.btnNewComment);
		Button buttonCancelComment = (Button) form
				.findViewById(R.id.btnCancelComment);

		buttonNewComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String ctxt = commentText.getText().toString();
				if (!ctxt.equals("")) {
					List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
					pairs.add(new BasicNameValuePair("data[Post][parent_id]",
							String.valueOf(PostsActivity.postId)));
					pairs.add(new BasicNameValuePair("data[Post][user_id]",
							pref.getPrefs("userId", c)));
					pairs.add(new BasicNameValuePair("data[Post][sessionKey]",
							pref.getPrefs("sessionKey", c)));
					pairs.add(new BasicNameValuePair("data[Post][content]",
							ctxt));

					ProgressDialog d = ProgressDialog.show(c, getResources()
							.getString(R.string.dialog), getResources()
							.getString(R.string.save_comment), true, true);

					if (picturePath.equals("")) {
						commentAsyncTaskDo(new JsonRequestLoader(), pairs, d);
					} else {
						ImageUploadAsyncTask imageTask = new ImageUploadAsyncTask(
								c, CommentActivity.this);
						imageTask.execute(new Object[] { picturePath, pairs, 2,
								d });
					}

				} else
					Toast.makeText(c,
							getResources().getString(R.string.comment_no_text),
							Toast.LENGTH_SHORT).show();
			}

		});

		final Button imgPost = (Button) form
				.findViewById(R.id.btnCommentLoadImg);
		imgPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 1);
			}
		});
		final ImageView imgView = (ImageView) v
				.findViewById(R.id.newCommentImg);
		buttonCancelComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
				imgView.setVisibility(View.GONE);
				picturePath = "";
				commentText.setText("");
				imgPost.setEnabled(true);
				LayoutParams params = form.getLayoutParams();
				params.height = 0;
				form.setLayoutParams(params);
			}
		});

		l.addView(s);

		setContentView(l);
	}

	/**
	 * <h3>commentAsyncTask</h3> Sets the parameters for AddCommentAsyncTask and
	 * executes it
	 * 
	 * @param request
	 *            The JsonRequestLoader for the AddCommentAsyncTask
	 * @param pairs
	 *            The List<NameValuePair> for the AddCommentAsyncTask, variables
	 *            for the request
	 * @param d
	 *            The ProgressDialog for the AddCommentAsyncTask
	 */
	public void commentAsyncTaskDo(JsonRequestLoader request,
			List<NameValuePair> pairs, ProgressDialog d) {
		AddCommentAsyncTask addComment = new AddCommentAsyncTask(
				CommentActivity.this);
		addComment.execute(new Object[] { request, pairs, d });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
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

			Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 2; // for 1/2 the image to be loaded
			Bitmap thumb = Bitmap.createScaledBitmap(
					BitmapFactory.decodeFile(picturePath, opts), 96, 96, false);

			final ImageView imageView = (ImageView) findViewById(R.id.newCommentImg);
			imageView.setImageBitmap(thumb);
			imageView.setVisibility(View.VISIBLE);

			imageView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					imageView.setVisibility(View.GONE);
					picturePath = "";
				}
			});
		}
	}

	/**
	 * <h3>getPostView</h3> Returns layout for post or comment
	 * 
	 * @param singlePost
	 *            The post for which to set the layout
	 * @param postOrComment
	 *            Defines if it is a post or a comment
	 * @return layout The layout for post or comment
	 */
	public LinearLayout getPostView(PostModel singlePost, boolean postOrComment) {

		List<Integer> listVotes = new ArrayList<Integer>();
		JSONArray array = singlePost.getVotes();
		final PostModel m = singlePost;
		int id;

		String userVoted = new String();

		try {
			for (int j = 0; j < singlePost.getVotesNo(); j++) {
				JSONObject object = array.getJSONObject(j);
				id = object.getInt("user_id");
				if (j == 0) {
					userVoted = userVoted.concat(object.getJSONObject("User")
							.optString("username"));
				} else {
					userVoted = userVoted.concat(", ");
					userVoted = userVoted.concat(object.getJSONObject("User")
							.optString("username"));
				}
				listVotes.add(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.comment_layout, null);

		final LinearLayout post = (LinearLayout) v
				.findViewById(R.id.mainLayout);
		LinearLayout buttons = (LinearLayout) v.findViewById(R.id.buttonLayout);

		TextView commentid = (TextView) v.findViewById(R.id.postId);
		commentid.setText("#" + String.valueOf(singlePost.getPostId()));

		TextView user = (TextView) v.findViewById(R.id.txtUser);
		TextView time = (TextView) v.findViewById(R.id.txtTime);
		final TextView content = (TextView) v.findViewById(R.id.txtPost);

		if (!postOrComment) {
			post.setBackgroundColor(Color.parseColor("#eaeaea"));

		}

		final Button votes = (Button) v.findViewById(R.id.btnVotes);
		final Button vote = (Button) v.findViewById(R.id.btnVote);

		votes.setId(singlePost.getPostId());
		vote.setId(singlePost.getPostId());

		user.setText(singlePost.getUsername());
		time.setText(" @" + singlePost.getTime());
		content.setText(Html
				.fromHtml(singlePost
						.getContent()
						.replaceAll("@FG", "<b> FG - </b>")
						.replaceAll("@([a-zA-Z0-9ï¿½æžšï¿½ï¿½ÆŽï¿½ï¿½_]*)",
								"<b>$1</b>")
						.replaceAll("(>>[0-9]*)", "<b>$1</b>")));

		Integer imgCount = singlePost.getImgNo();
		if (imgCount > 0) {

			LinearLayout imgs = (LinearLayout) v.findViewById(R.id.imgLayout);

			for (int im = 0; im < singlePost.getImgNo(); im++) {
				final Integer imgNo = im;
				ImageDownloader image = new ImageDownloader(c, false);

				View view = null;
				ImageView img = null;
				switch (imgCount) {
				case 1:
					view = View.inflate(c, R.layout.one_img, null);
					img = (ImageView) view.findViewById(R.id.one_one);
					break;
				case 2:
					view = View.inflate(c, R.layout.two_img, null);
					if (im == 0) {
						img = (ImageView) view.findViewById(R.id.two_one);
					} else {
						img = (ImageView) view.findViewById(R.id.two_two);
					}
					break;
				case 3:
					view = View.inflate(c, R.layout.three_img, null);
					switch (im) {
					case 0:
						img = (ImageView) view.findViewById(R.id.three_one);
						break;
					case 1:
						img = (ImageView) view.findViewById(R.id.three_two);
						break;
					case 2:
						img = (ImageView) view.findViewById(R.id.three_three);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}

				try {
					imageName = singlePost.getImgs().getJSONObject(im)
							.optString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				img.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						try {
							imageName = m.getImgs().getJSONObject(imgNo)
									.optString("name");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent img = new Intent(c, ImageActivity.class);
						img.putExtra("posts", false);
						startActivity(img);

					}
				});
				imgs.addView(view);

				image.download("http://fenstergang.com/img/thumbs/150x150/"
						+ imageName, img);

			}
		}

		votes.setText(" | " + String.valueOf(singlePost.getVotesNo()));

		for (int i = 0; i < listVotes.size(); i++) {
			if (listVotes.get(i) == Integer
					.parseInt(pref.getPrefs("userId", c))) {
				vote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nospit,
						0, 0, 0);
				vote.setId(0);
				break;
			} else
				vote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spit,
						0, 0, 0);
			vote.setId(1);
		}
		if (singlePost.getVotesNo() == 0) {
			vote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.spit, 0, 0,
					0);
			vote.setId(1);
		}

		votes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				votes.setEnabled(false);
				JsonRequestLoader request = new JsonRequestLoader();
				Integer votersId = votes.getId();
				VotersAsyncTask votersTask = new VotersAsyncTask(c, pref);
				ProgressDialog dialog = new ProgressDialog(c);
				votersTask.execute(new Object[] { request, dialog, votes,
						votersId });

			}
		});

		final int postId = singlePost.getPostId();
		final String userId = pref.getPrefs("userId", c);
		final String sessionKey = pref.getPrefs("sessionKey", c);

		vote.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				vote.setEnabled(false);
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
				pairs.add(new BasicNameValuePair("data[Vote][post_id]", String
						.valueOf(postId)));
				pairs.add(new BasicNameValuePair("data[Vote][user_id]", userId));
				pairs.add(new BasicNameValuePair("data[Vote][sessionKey]",
						sessionKey));
				JsonRequestLoader request = new JsonRequestLoader();

				VoteAsyncTask voteTask = new VoteAsyncTask(c);
				voteTask.execute(new Object[] { request, pairs, vote.getId(),
						vote, votes });
			}

		});

		if (Integer.parseInt(pref.getPrefs("userId", c)) == (singlePost
				.getUserId()) && !postOrComment) {
			buttons.removeView(vote);

			content.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					registerForContextMenu(content);
					bundle.putString("postId", Integer.toString(m.getPostId()));
					bundle.putString("content", m.getContent());
					setEditContentText(content);
					setDeleteLayout(post);
					return false;
				}
			});
		}
		return post;
	}

	/**
	 * <h3>reset</h3> Resets the activity (it is called from
	 * AddCommentAsyncTask)
	 * 
	 * @param c
	 *            Context
	 */
	public void reset(CommentActivity c) {
		c.finish();
		Intent i = new Intent(c, CommentActivity.class);
		i.putExtra("postId", String.valueOf(postId));
		startActivity(i);
	}

	/**
	 * <h3>showImage</h3> Displays image in full screen
	 * 
	 * @param name
	 *            Name of the image to show
	 */
	public void showImage(String name) {
		Intent img = new Intent(c, ImageActivity.class);
		img.putExtra("name", name);
		startActivity(img);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_comment, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.comment:
			LayoutParams params = form.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			form.setLayoutParams(params);
			commentText.requestFocus();
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * <h3>getEditContentText</h3> Returns TextView for editing
	 * 
	 * @return The TextView for editing
	 */
	public TextView getEditContentText() {
		return editContentText;
	}

	/**
	 * <h3>setEditContentText</h3> Sets the editContenttext TextView
	 * 
	 * @param editContentText
	 *            The TextView which needs to be set as
	 *            <b>this</b>.editContentText
	 */
	public void setEditContentText(TextView editContentText) {
		this.editContentText = editContentText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Uredi/Izbriši");
		menu.add(0, v.getId(), 0, "Uredi");
		menu.add(0, v.getId(), 0, "Izbriši");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// if edit(Uredi) get the new parameters and set them or not (depends on
		// the user)
		if (item.getTitle() == "Uredi") {

			final Dialog editDialog = new Dialog(c);
			editDialog.setContentView(R.layout.edit_post);
			editDialog.setTitle("Uredi #" + bundle.getString("postId"));
			editDialog.setCancelable(true);

			final EditText editText = (EditText) editDialog
					.findViewById(R.id.editPost);

			TextView t2 = getEditContentText();
			bundle.remove("content");
			bundle.putString("content", t2.getText().toString());

			editText.setText(bundle.getString("content"));
			Button editPostYes = (Button) editDialog
					.findViewById(R.id.editButtonYes);
			Button editPostNo = (Button) editDialog
					.findViewById(R.id.editButtonNo);
			editDialog.show();

			final JsonRequestLoader request = new JsonRequestLoader();

			editPostNo.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editDialog.dismiss();
				}
			});

			final TextView t = getEditContentText();

			editPostYes.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
					pairs.add(new BasicNameValuePair("data[Post][post_id]",
							bundle.getString("postId")));
					pairs.add(new BasicNameValuePair("data[Post][user_id]",
							pref.getPrefs("userId", c)));
					pairs.add(new BasicNameValuePair("data[Post][sessionKey]",
							pref.getPrefs("sessionKey", c)));
					pairs.add(new BasicNameValuePair("data[Post][content]",
							editText.getText().toString()));
					Toast.makeText(c, "Rudarim..", Toast.LENGTH_SHORT).show();
					EditPostAsyncTask editAsyncTask = new EditPostAsyncTask(c,
							CommentActivity.this);
					editAsyncTask.execute(new Object[] { request, pairs, false,
							t, editDialog });
				}
			});

			// else if delete (Izbriši) delete the comment
		} else if (item.getTitle() == "Izbriši") {

			final Dialog deleteDialog = new Dialog(c);
			deleteDialog.setContentView(R.layout.delete_post);
			deleteDialog.setTitle("Si ziher?");
			deleteDialog.setCancelable(true);

			Button deletePostYes = (Button) deleteDialog
					.findViewById(R.id.deleteButtonYes);
			Button deletePostNo = (Button) deleteDialog
					.findViewById(R.id.deleteButtonNo);

			final JsonRequestLoader request = new JsonRequestLoader();

			deletePostNo.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					deleteDialog.dismiss();
				}
			});

			deletePostYes.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
					pairs.add(new BasicNameValuePair("data[Post][post_id]",
							bundle.getString("postId")));
					pairs.add(new BasicNameValuePair("data[Post][user_id]",
							pref.getPrefs("userId", c)));
					pairs.add(new BasicNameValuePair("data[Post][sessionKey]",
							pref.getPrefs("sessionKey", c)));
					Toast.makeText(c, "Rudarim..", Toast.LENGTH_SHORT).show();
					DeletePostAsyncTask deleteAsyncTask = new DeletePostAsyncTask(
							c);
					deleteAsyncTask.execute(new Object[] { request, pairs,
							getDeleteLayout(), deleteDialog });
				}
			});

			deleteDialog.show();

		} else {
			return false;
		}
		return true;
	}

	/**
	 * <h3>getDeleteLayout</h3> Returns layout which needs to be deleted
	 * 
	 * @return The LinearLayout which needs to be deleted
	 */
	public LinearLayout getDeleteLayout() {
		return deleteLayout;
	}

	/**
	 * <h3>setDeleteLayout</h3> Sets the deleteLayout as the layout which needs
	 * to be deleted
	 * 
	 * @param deleteLayout
	 *            The layout which needs to be set as <b>this</b>deleteLayout
	 */
	public void setDeleteLayout(LinearLayout deleteLayout) {
		this.deleteLayout = deleteLayout;
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
