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

        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;


public class LoginScreen extends Activity implements View.OnClickListener {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    private static final String LOGIN_URL = "http://wiggle.mksengun.com/logincontrol.php"; //TODO google id yi yaz.
    private EditText user, pass;
    Button register, login;
    JSONParser jsonParser = new JSONParser();
    JSONObject json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(this);
        login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(this);

        user = (EditText) findViewById(R.id.usernameedit);
        pass = (EditText) findViewById(R.id.passedit);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.btnRegister:
                Intent registerIntent = new Intent(LoginScreen.this, RegisterScreen.class);
                startActivity(registerIntent);
                break;

            case R.id.btnLogin:
                new LoginUser().execute();
                break;
        }


    }

    class LoginUser extends AsyncTask<String, String, String> {

        boolean failure = false;
        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            String username = user.getText().toString();
            String password = pass.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            //Posting user data to script
            json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);
            params.clear();
            // full json response

            // json success element



            return null;

        }

        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;
            String furl = "";
            try {
                success = json.getInt("success");
                if (success == 1) {
                    // Log.d("User Created!", json.toString());
                    //   finish();
                    int user_id = json.getInt("userid");
                    furl = json.getString("message");
                    Toast.makeText(LoginScreen.this, furl, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginScreen.this, multiRoomPanel.class);
                    i.putExtra("user_id", user_id);//extralara id yi koyduk room activity si icin
                    startActivity(i);
                } else {
                    // Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    furl = json.getString("message");
                    Toast.makeText(LoginScreen.this, furl, Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);





        }

    }


}


