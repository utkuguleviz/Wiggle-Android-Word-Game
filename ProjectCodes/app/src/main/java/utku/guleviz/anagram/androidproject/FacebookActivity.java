package utku.guleviz.anagram.androidproject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;


public class FacebookActivity extends Activity {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    private Button buttonLoginLogout;
    private Button buttonSend;
    private final Session.StatusCallback statusCallback = new SessionStatusCallback();

    private String photoPath;

    private String message;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "user_photos");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_layout);
        buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);
        buttonSend = (Button) findViewById(R.id.buttonSendImage);
        buttonSend.setVisibility(View.GONE);
        photoPath = this.getIntent().getExtras().getString("URI");
        message = this.getIntent().getExtras().getString("FACEBOOKMESSAGE");
        if (photoPath != null && message != null) {
            TempStore.setPath(photoPath);
            TempStore.setMes(message);
        }
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            buttonSend.setVisibility(View.VISIBLE);
            buttonSend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    sendPhotoWithFacebook();
                }

            });
            buttonLoginLogout.setText("Logout");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickLogout();
                }
            });
        } else {
            buttonLoginLogout.setText("Login");
            onClickLogin();
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    private void sendPhotoWithFacebook() {
        publishStory();
    }

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }

    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
            Bitmap img = BitmapFactory.decodeFile(TempStore.getPath());
            if (img != null) {

                Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), img, uploadPhotoRequestCallback);
                Bundle parameters = request.getParameters();
                parameters.putString("message", TempStore.getMes());
                // add more params here
                request.setParameters(parameters);
                request.executeAsync();
                LoadAnimation.runLoadAnimation(this);
            }
        }
    }

    private String idUploadResponse;
    Request.Callback uploadPhotoRequestCallback = new Request.Callback() {

        @Override
        public void onCompleted(Response response) {
            if (response.getError() != null) {
                // post error
            } else {
                idUploadResponse = (String) response.getGraphObject().getProperty("id");
                if (idUploadResponse != null) {
                    //"https://www.facebook.com/photo.php?fbid=" + idUploadResponse;
                    LoadAnimation.stopLoadAnimation();
                    finish();
                } else {
                    // error
                }

            }
        }
    };

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
}