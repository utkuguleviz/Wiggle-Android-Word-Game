package utku.guleviz.anagram.androidproject;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.view.animation.AlphaAnimation;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import org.json.JSONException;
        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;


/**
 * Created by Aras on 19.1.2015.
 */
public class RegisterScreen extends Activity implements View.OnClickListener {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    private static final String LOGIN_URL = "http://wiggle.mksengun.com/register.php"; //TODO google id yi yaz.
    private EditText user, pass, repass, email;
    private Button registerButton;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    JSONObject json;
    List<NameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        user = (EditText) findViewById(R.id.usernameeditreg);
        pass = (EditText) findViewById(R.id.passeditreg);
        repass = (EditText) findViewById(R.id.reentereditreg);
        email = (EditText) findViewById(R.id.emaileditreg);
        registerButton = (Button) findViewById(R.id.btnRegisterreg);
        Button backToLogin = (Button) findViewById(R.id.btnBackreg);
        backToLogin.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.btnBackreg:
                Intent nextActivityIntent = new Intent(this, LoginScreen.class);
                startActivity(nextActivityIntent);
                break;
            case R.id.btnRegisterreg:
                new CreateUser().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();//bu olunca bi daha register ekraina gelmiyo. Buna gerek yok.
    }

    class CreateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;
        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterScreen.this); // context alıyor.
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            String repassword = repass.getText().toString();
            String em = email.getText().toString();

            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("reenter", repassword));
            params.add(new BasicNameValuePair("email", em));

            //Log.d("request!", "starting");

            //Posting user data to script
            json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);

            // full json response
            //Log.d("Login attempt", json.toString());

            // json success element


            return "";

        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;
            int user_id = 0;
            String furl = "";
            pDialog.dismiss();
            params.clear();
            try {
                success = json.getInt("success");
                if (success == 1) {
                    // Log.d("User Created!", json.toString());
                    //   finish();
                    user_id = json.getInt("userid");
                    furl = json.getString("message");
                    Toast.makeText(RegisterScreen.this, furl, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterScreen.this, multiRoomPanel.class);
                    i.putExtra("user_id", user_id);//extralara id yi koyduk room activity si icin
                    startActivity(i);
                } else {
                    // Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    furl = json.getString("message");
                    Toast.makeText(RegisterScreen.this, furl, Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();

            }
        }

    }


}


