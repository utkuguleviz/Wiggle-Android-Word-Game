package utku.guleviz.anagram.androidproject;



        import java.io.File;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import twitter4j.StatusUpdate;
        import twitter4j.Twitter;
        import twitter4j.TwitterException;
        import twitter4j.TwitterFactory;
        import twitter4j.auth.AccessToken;
        import twitter4j.auth.RequestToken;
        import twitter4j.conf.Configuration;
        import twitter4j.conf.ConfigurationBuilder;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.SharedPreferences.Editor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;
        import android.content.Context;

public class TwitterActivity extends Activity {
    private String photoPath;
    private String message;
    private int preClass;
    // Constants
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     * */
    static String TWITTER_CONSUMER_KEY = "1ssq9ZZ8JhGMoSINUrAHGvGE0"; // place
    // your
    // cosumer
    // key
    // here
    static String TWITTER_CONSUMER_SECRET = "sdhFEvwm3gHBubFRFk7jf9xG8XSy1NKFVhIm1wwVlyH4uYA6M0"; // place
    // your
    // consumer
    // secret
    // here

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Progress dialog
    ProgressDialog pDialog;

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_layout);

        if (!isOnline(this)) {
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show();
        } else {
            photoPath = this.getIntent().getExtras().getString("URI");
            message = this.getIntent().getExtras().getString("TWITTERMESSAGE");
            preClass = getIntent().getExtras().getInt("PREVIOUSCLASS");
            if (photoPath != null && message != null) {
                TempStore.setPath(photoPath);
                TempStore.setMes(message);
                TempStore.setPreClass(preClass);
            }
            // Check if twitter keys are set
            if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
                // Internet Connection is not present
                alert.showAlertDialog(TwitterActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
                // stop executing code by return
                return;
            }

            // Shared Preferences
            mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
            logoutFromTwitter();
            /**
             * This if conditions is tested once is redirected from twitter
             * page. Parse the uri to get oAuth Verifier
             * */
            if (!isTwitterLoggedInAlready()) {
                Uri uri = getIntent().getData();
                if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                    // oAuth verifier
                    final String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                    try {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Get the access token
                                    TwitterActivity.this.accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                                    // Shared Preferences
                                    Editor e = mSharedPreferences.edit();
                                    // After getting access token, access token
                                    // secret
                                    // store them in application preferences
                                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                                    e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                                    // Store login status - true
                                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                                    e.commit(); // save changes
                                    if (TempStore.getPath() == null) {
                                        Log.e("ERKAN", "NULL");
                                    } else {
                                        uploadPic(new File(TempStore.getPath()), TempStore.getMes(), twitter);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        logoutFromTwitter();
                        e.printStackTrace();
                        Log.e("Twitter Login Error", "> " + e.getMessage());
                    }
                }else {
                    loginToTwitter();
                }
            } else {
                try {
                    uploadPic(new File(TempStore.getPath()), TempStore.getMes(), twitter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function to login twitter
     * */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        Intent recursive = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
                        TwitterActivity.this.startActivity(recursive);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // user already logged into twitter
            try {
                uploadPic(new File(TempStore.getPath()), TempStore.getMes(), twitter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To upload a picture with some piece of text.
     *
     *
     * @param file
     *            The file which we want to share with our tweet
     * @param message
     *            Message to display with picture
     * @param twitter
     *            Instance of authorized Twitter class
     * @throws Exception
     *             exception if any
     */

    public void uploadPic(File file, String message, Twitter twitter) throws Exception {
        try {
            StatusUpdate status = new StatusUpdate(message);
            status.setMedia(file);
            twitter.updateStatus(status);
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "We Shared Your Photo in Twitter", Toast.LENGTH_LONG).show();
                    if(TempStore.getPreClass() == 0){//singlePlayer
                        Intent i = new Intent(TwitterActivity.this, LevelSelect.class);
                        startActivity(i);
                        finish();
                    }
                    else if(TempStore.getPreClass() == 1){//multiPlayer
                        Intent i = new Intent(TwitterActivity.this, multiRoomPanel.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to logout from twitter It will just clear the application shared
     * preferences
     * */
    private void logoutFromTwitter() {
        // Clear the shared preferences
        Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static boolean isOnline(Activity act) {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}

