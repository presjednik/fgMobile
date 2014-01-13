package hr.fg.mobile;

import hr.fg.mobile.models.PostModel;
import hr.fg.mobile.plugins.ImageDownloader;
import hr.fg.mobile.plugins.JsonRequestLoader;
import hr.fg.mobile.prefs.FgPrefs;
import hr.fg.mobile.tasks.AddPostAsyncTask;
import hr.fg.mobile.tasks.DeletePostAsyncTask;
import hr.fg.mobile.tasks.EditPostAsyncTask;
import hr.fg.mobile.tasks.ImageUploadAsyncTask;
import hr.fg.mobile.tasks.PostsAsyncTask;
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
 * <h2>PostsActivity</h2> Activity for showing all the posts on 'Buksa'
 * 
 * @author Fenster Gang
 * 
 */
public class PostsActivity extends Activity {

	/**
	 * Used to define the context
	 */
	private Context c = this;

	/**
	 * Used to define the offset for getting the posts from server
	 */
	public static int offset = 0;

	/**
	 * Used to define length of the post
	 */
	public static int postLenght = 0;

	/**
	 * Used to define the layout which holds the posts
	 */
	public static LinearLayout l;

	/**
	 * Used to define the main ScrollView
	 */
	public static ScrollView sv;

	/**
	 * Used to define the button for loading more posts
	 */
	public static Button loadMore;

	/**
	 * Used to define main View for activity
	 */
	public static View postsLayout;

	/**
	 * Used to define the form for writing a new post
	 */
	public static LinearLayout form;

	/**
	 * Used to define the editText field for writing a new post
	 */
	public static EditText postTxt;

	/**
	 * Used to define the post id
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
	 * Used to define the bundle which holds information about a post that needs
	 * to be edited or deleted
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

	/**
	 * Used to define the Button for searching images on phone
	 */
	private Button imgPost;

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

		offset = 0;

		showPosts();
	}

	/**
	 * <h3>showPosts</h3> Gets posts from Web-service and display them
	 */
	private void showPosts() {

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		postsLayout = inflater.inflate(R.layout.posts_layout, null);

		sv = (ScrollView) postsLayout.findViewById(R.id.postsScroll);
		l = (LinearLayout) postsLayout.findViewById(R.id.postsList);

		form = (LinearLayout) postsLayout.findViewById(R.id.post_add_form);
		postTxt = (EditText) form.findViewById(R.id.newPost);
		final Button newPost = (Button) form.findViewById(R.id.btnNewPost);
		Button cnlPost = (Button) form.findViewById(R.id.btnCancelPost);

		imgPost = (Button) form.findViewById(R.id.btnLoadImg);
		imgPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, 1);

			}
		});

		newPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				postTxt.clearFocus();
				String txt = postTxt.getText().toString();
				if (!txt.equals("")) {
					newPost.setEnabled(false);
					List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
					pairs.add(new BasicNameValuePair("data[Post][user_id]",
							pref.getPrefs("userId", c)));
					pairs.add(new BasicNameValuePair("data[Post][sessionKey]",
							pref.getPrefs("sessionKey", c)));
					pairs.add(new BasicNameValuePair("data[Post][content]", txt));

					Toast.makeText(c, getString(R.string.send_post),
							Toast.LENGTH_SHORT).show();
					if (picturePath.equals("")) {
						postAsyncTaskDo(pairs);
					} else {
						ImageUploadAsyncTask imageTask = new ImageUploadAsyncTask(
								c, PostsActivity.this);
						imageTask.execute(new Object[] { picturePath, pairs, 1,
								null });
					}
				} else
					Toast.makeText(c, getString(R.string.comment_no_text),
							Toast.LENGTH_SHORT).show();
			}
		});

		final ImageView imgView = (ImageView) postsLayout
				.findViewById(R.id.newImg);
		cnlPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(postTxt.getWindowToken(), 0);
				imgView.setVisibility(View.GONE);
				picturePath = "";
				imgPost.setEnabled(true);
				postTxt.setText("");
				LayoutParams params = form.getLayoutParams();
				params.height = 0;
				form.setLayoutParams(params);
			}
		});

		View b = inflater.inflate(R.layout.load_more, null);
		loadMore = (Button) b;
		loadMore.setText(getString(R.string.load_more));

		final JsonRequestLoader request = new JsonRequestLoader();

		final String pflag = this.getIntent().getStringExtra("pflag")
				.toString();
		final String userp = this.getIntent().getStringExtra("userp")
				.toString();

		loadMore.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				loadMore.setEnabled(false);
				loadMore.setText("�imtra..");
				PostsAsyncTask postsTask = new PostsAsyncTask(c, new FgPrefs(),
						PostsActivity.this);
				postsTask.execute(new Object[] { request, l, loadMore, false,
						postsLayout, pflag, userp });
			}
		});

		PostsAsyncTask postsTask = new PostsAsyncTask(this, new FgPrefs(),
				PostsActivity.this);

		postsTask.execute(new Object[] { request, l, loadMore, true,
				postsLayout, pflag, userp });
	}

	/**
	 * <h3>postAsyncTask</h3> Sets the parameters for AddPostAsyncTask and
	 * executes it
	 * 
	 * @param pairs
	 *            The List<NameValuePair> for the AddPostAsyncTask, variables
	 *            for the request
	 */
	public void postAsyncTaskDo(List<NameValuePair> pairs) {
		AddPostAsyncTask task = new AddPostAsyncTask(c, PostsActivity.this);

		task.execute(new Object[] { pairs, new JsonRequestLoader() });
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

			final ImageView imageView = (ImageView) findViewById(R.id.newImg);
			imageView.setImageBitmap(thumb);
			imageView.setVisibility(View.VISIBLE);
			imgPost.setEnabled(false);

			imageView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					picturePath = "";
					imageView.setVisibility(View.GONE);
					imgPost.setEnabled(true);
				}
			});
		}
	}

	/**
	 * <h3>createPostModel</h3> Creates a post model for one single post
	 * 
	 * @param posts
	 *            JSON array from which we get the JSONOBjects we need
	 * @param i
	 *            Index of the single post
	 * @return Single post
	 * @throws JSONException
	 */
	public PostModel createPostModel(JSONArray posts, int i)
			throws JSONException {
		JSONObject post = posts.getJSONObject(i).optJSONObject("Post");
		JSONObject user = posts.getJSONObject(i).optJSONObject("User");
		JSONArray childPost = posts.getJSONObject(i).getJSONArray("ChildPost");
		JSONArray votes = posts.getJSONObject(i).getJSONArray("Vote");
		JSONArray imgs = posts.getJSONObject(i).getJSONArray("Image");

		PostModel postView = new PostModel(post.optInt("id"),
				user.optString("username"), post.optString("created"),
				post.optString("content"), childPost.length(), votes,
				post.optInt("user_id"), imgs);
		return postView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_posts, menu);
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
		case R.id.logout:
			FgPrefs pref = new FgPrefs();
			//pref.removePrefs("username", c);
			//pref.removePrefs("password", c);
			pref.removePrefs("sessionKey", c);
			pref.removePrefs("userId", c);
			this.finish();
			Intent i = new Intent(c, LoginActivity.class);
			Toast.makeText(c, getString(R.string.logout_succeeded),
					Toast.LENGTH_SHORT).show();
			i.putExtra("username", pref.getPrefs("username", c));
			i.putExtra("password", pref.getPrefs("password", c));
			startActivity(i);
			break;
		case R.id.post:
			LayoutParams params = form.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			form.setLayoutParams(params);
			postTxt.requestFocus();
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * <h3>getPostView</h3> Gets the post view for a single post
	 * 
	 * @param singlePost
	 *            Single post for which to get the view
	 * @return Layout which holds the single post
	 */
	public LinearLayout getPostView(PostModel singlePost) {

		final PostModel m = singlePost;

		List<Integer> listVotes = new ArrayList<Integer>();
		JSONArray array = singlePost.getVotes();

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
		View v = inflater.inflate(R.layout.post_layout, null);

		final LinearLayout layout = (LinearLayout) v
				.findViewById(R.id.mainLayout);
		LinearLayout buttons = (LinearLayout) v.findViewById(R.id.buttonLayout);

		TextView postid = (TextView) v.findViewById(R.id.postId);
		postid.setText("#" + String.valueOf(singlePost.getPostId()));

		TextView user = (TextView) v.findViewById(R.id.txtUser);

		TextView time = (TextView) v.findViewById(R.id.txtTime);
		final TextView content = (TextView) v.findViewById(R.id.txtPost);

		Button comments = (Button) v.findViewById(R.id.btnComments);
		final Button votes = (Button) v.findViewById(R.id.btnVotes);
		final Button vote = (Button) v.findViewById(R.id.btnVote);

		comments.setId(R.string.comments);
		votes.setId(R.string.votes);
		vote.setId(singlePost.getPostId());

		user.setText(singlePost.getUsername());
		final int userProfile = singlePost.getUserId();
		user.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(c, ProfileActivity.class);
				i.putExtra("user", userProfile);
				startActivity(i);
			}
		});

		time.setText(" @" + singlePost.getTime());
		content.setText(Html
				.fromHtml(singlePost
						.getContent()
						.replaceAll("@FG", "<b> FG - </b>")
						.replaceAll("@([a-zA-Z0-9�枚��Ǝ��_]*)",
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

					e.printStackTrace();
				}

				img.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						try {
							imageName = m.getImgs().getJSONObject(imgNo)
									.optString("name");
						} catch (JSONException e) {

							e.printStackTrace();
						}
						Intent img = new Intent(c, ImageActivity.class);
						img.putExtra("posts", true);
						startActivity(img);

					}
				});
				imgs.addView(view);

				image.download("http://fenstergang.com/img/thumbs/150x150/"
						+ imageName, img);

			}
		}
		comments.setText(" | " + String.valueOf(singlePost.getRepliesNo()));
		votes.setText(" | " + String.valueOf(singlePost.getVotesNo()));

		comments.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				postId = m.getPostId();
				Intent i = new Intent(c, CommentActivity.class);
				i.putExtra("postId", String.valueOf(postId));
				startActivity(i);

			}
		});

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
				postId = m.getPostId();
				JsonRequestLoader request = new JsonRequestLoader();

				VotersAsyncTask votersTask = new VotersAsyncTask(c, pref);
				ProgressDialog dialog = new ProgressDialog(c);
				votersTask.execute(new Object[] { request, dialog, votes,
						postId });

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
				.getUserId())) {
			buttons.removeView(vote);

			content.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					registerForContextMenu(content);
					bundle.putString("postId", Integer.toString(m.getPostId()));
					bundle.putString("content", m.getContent());
					setEditContentText(content);
					setDeleteLayout(layout);
					return false;
				}
			});
		}

		return (LinearLayout) v;
	}

	public TextView getEditContentText() {
		return editContentText;
	}

	public void setEditContentText(TextView editContentText) {
		this.editContentText = editContentText;
	}

	/**
	 * <h3>setLayout</h3> Sets the view as content view
	 * 
	 * @param view
	 *            View to be set as layout
	 */
	public void setLayout(View view) {
		setContentView(view);
	}

	/**
	 * <h3>postsError</h3> Returns back to login
	 */
	public void postsError() {
		Intent i = new Intent(c, LoginActivity.class);
		this.finish();
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
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Uredi/Izbri�i");
		menu.add(0, v.getId(), 0, "Uredi");
		menu.add(0, v.getId(), 0, "Izbri�i");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
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
							PostsActivity.this);
					editAsyncTask.execute(new Object[] { request, pairs, true,
							t, editDialog });
				}
			});

		} else if (item.getTitle() == "Izbri�i") {

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
