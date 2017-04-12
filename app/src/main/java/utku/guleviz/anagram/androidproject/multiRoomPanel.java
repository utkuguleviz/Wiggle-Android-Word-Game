package utku.guleviz.anagram.androidproject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class multiRoomPanel extends Activity implements View.OnClickListener {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    List<NameValuePair> params;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    JSONObject json;

    private static final int ROOM_COUNT = 10;
    private int[] usercounts = new int[ROOM_COUNT];
    private int[] user1ids = new int[ROOM_COUNT];
    private int[] user2ids = new int[ROOM_COUNT];
    private static final String LOGIN_URL = "http://wiggle.mksengun.com/roominfo.php"; //roominfo almak icin
    private static final String LOGIN_URL2 = "http://wiggle.mksengun.com/accessroom.php"; //room'a giris icin
    private static final String LOGIN_URL3 = "http://wiggle.mksengun.com/message.php";
    private static final String LOGIN_URL4 = "http://wiggle.mksengun.com/userinfo.php";
    ArrayList<Room> rooms=new ArrayList<Room>();
    private boolean startControl = true;
    private boolean clickControl = false;
    private int clickPosition = 0;
    static Typeface typeFace;
    public String popupMessage = "";
    private int user_id = 0;
    private View popupLayout,popupScoreLayout;
    private PopupWindow popWindow,popupScoreWindow;
    private TextView userNameText,emailText,experianceText,levelText,roomName;
    private MultiPopupAdapter mAdapter;
    private String userName,email,experince,level;
    private int popupOpen = 0; // jsondan alınan success datasına eşit. eğer success 0 ise popup açılmamalı.
    Button logoutButton=null;
    Button infoButton=null;
    roomAdapter adaptorr;
    public boolean twitterClicked = false, faceClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        typeFace= Typeface.createFromAsset(getAssets(), "fonts/waltographUI.ttf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_room);
        logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);
        infoButton = (Button) findViewById(R.id.info);
        infoButton.setOnClickListener(this);
        roomName=(TextView)findViewById(R.id.roomName);


        informationPopup();
        init();
    }

    /*
    ADDED. window leak olmaması için
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(popWindow != null) {
            if (popWindow.isShowing() && !twitterClicked && !faceClicked) { // geri tuşuna basılması durumunda eğer açık popup varsa window leak olmasını engellemek için.
                popWindow.setFocusable(false);
                popWindow.dismiss();
            }
            if(twitterClicked)
                twitterClicked = false;
            if(faceClicked)
                faceClicked = false;
        }
        if(popupScoreWindow != null) {
            if (popupScoreWindow.isShowing()) {
                popupScoreWindow.setFocusable(false);
                popupScoreWindow.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //popWindow.showAtLocation();
        new PullRoomInfo().execute();


    }

    public void messagePopup()
    {
        //  if(popupMessage != "NULL" || popupMessage != "" || popupMessage.length()!=0 || popupOpen != 0) {
        if(popupMessage.length()!=0 && popupMessage.length() != 4 && popupOpen != 0) {

            LayoutInflater inflater = (LayoutInflater) multiRoomPanel.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popupLayout = inflater.inflate(R.layout.multi_messagepopup,
                    (ViewGroup) findViewById(R.id.multi_message));

            String [] values = popupMessage.split("_");


            ListView lv;
            MultiPopupAdapter  adapter = new MultiPopupAdapter(multiRoomPanel.this, // xmlParse içinde values arrayi doluyor.
                    values);
            lv = (ListView)popupLayout.findViewById(R.id.lvPopup);
            lv.setAdapter(adapter);

            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            // text = (TextView)popupLayout.findViewById(R.id.multi_message_text);
            //text.setText(popupMessage);


            popWindow = new PopupWindow(popupLayout, width / 2, height / 2, true);
            popWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
            popWindow.setFocusable(true);
            Button close = (Button)popupLayout.findViewById(R.id.multi_popup_button_dismiss);
            close.setOnClickListener(this);

        }

    }
    public void informationPopup()
    {
        LayoutInflater inflater = (LayoutInflater) multiRoomPanel.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupScoreLayout = inflater.inflate(R.layout.information,
                (ViewGroup) findViewById(R.id.information));

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        // text = (TextView)popupLayout.findViewById(R.id.multi_message_text);
        //text.setText(popupMessage);


        popupScoreWindow = new PopupWindow(popupScoreLayout, width, height, true);
        Button closeInfo = (Button)popupScoreLayout.findViewById(R.id.okButton);
        closeInfo.setOnClickListener(this);

        /*ADDED*/
        userNameText = (TextView)popupScoreLayout.findViewById(R.id.userNameText);
        userNameText.setTypeface(typeFace);
        emailText = (TextView)popupScoreLayout.findViewById(R.id.emailText);
        emailText.setTypeface(typeFace);
        experianceText = (TextView)popupScoreLayout.findViewById(R.id.experienceText);
        experianceText.setTypeface(typeFace);
        levelText = (TextView)popupScoreLayout.findViewById(R.id.levelText);
        levelText.setTypeface(typeFace);
        /*ADDED*/
    }



    public void init(){

        if(getIntent().getExtras() != null){
            user_id = getIntent().getExtras().getInt("user_id", 0);
        }

        new PullRoomInfo().execute();
        //   // bu asamadan sonra elimizde verilen userid ye gore cekilen mesajlar var

        final ListView ourList = (ListView) findViewById(R.id.gameroomlist);
        adaptorr=new roomAdapter(this, rooms);
        ourList.setAdapter(adaptorr);

        ourList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                clickControl = true;
                clickPosition = position;
                new PullRoomInfo().execute();
            }
        });
    }

    public void clickRoom(){

        if(usercounts[clickPosition] == 2){
            Toast.makeText(multiRoomPanel.this, "Room is full", Toast.LENGTH_SHORT).show();
        }
        else if(user1ids[clickPosition] == user_id || user2ids[clickPosition] == user_id){
            Toast.makeText(multiRoomPanel.this, "You can't play with yourself", Toast.LENGTH_SHORT).show();
        }
        else{
            new AccessRoom().execute();
        }
    }



    class AccessRoom extends AsyncTask<String, String, String>{

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... p) {

            // Building Parameters
            String word_index = "";
            if(usercounts[clickPosition] == 0){//daha once user girmediyse hic odanin random wordindex leri girilcek.
                for(int i = 0; i < 30; i++){
                    int index = (int) (Math.random() * XMLParser.fiveToSix.size());
                    word_index += index + "_";//wordindex icerigi: 30_1000_251_ tarzinda
                }
            }
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("click_position", clickPosition+""));
            params.add(new BasicNameValuePair("word_index", word_index));
            params.add(new BasicNameValuePair("user_id", user_id+""));
            params.add(new BasicNameValuePair("user_count", usercounts[clickPosition]+""));

            //Posting user data to script
            json = jsonParser.makeHttpRequest(
                    LOGIN_URL2, "POST", params);
            params.clear();

            return null;
        }
        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;
            String furl = "";
            try {
                success = json.getInt("success");
                if (success == 1) {

                    Toast.makeText(multiRoomPanel.this, "Let the game begin...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(multiRoomPanel.this, MultiMainActivity.class);
                    i.putExtra("user_id", user_id);
                    i.putExtra("click_position", clickPosition);
                    i.putExtra("user_count", usercounts[clickPosition]);
                    startActivity(i);

                } else {

                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);

        }
    }

    class FetchMessage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... args) {


            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", Integer.toString(user_id)));

            json = jsonParser.makeHttpRequest(
                    LOGIN_URL3, "POST", params);

            return "";

        }



        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;

            params.clear();
            try {
                success = json.getInt("success");
                popupOpen = success;
                if (success == 1) {
                    // Log.d("User Created!", json.toString());
                    //   finish();
                    popupMessage = json.getString("message");
                    //   Toast.makeText(getApplicationContext(),""+ popupMessage,
                    //         Toast.LENGTH_LONG).show();
                } else {
                    // Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    popupMessage = "Error while fetching DATA";
                    Toast.makeText(getApplicationContext(),""+ popupMessage,
                            Toast.LENGTH_LONG).show();

                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);
            messagePopup();
        }


    }
    class PullRoomInfo extends AsyncTask<String, String, String> {

        boolean failure = false;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(multiRoomPanel.this); // context alıyor.
            pDialog.setMessage("Pulling room info...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {

            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", "asd"));//oylesine yolladik ama silme kullanildi.

            json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);

            return "";
        }

        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;
            String furl = "";
            JSONArray json_usercount, json_user1id, json_user2id;
            pDialog.dismiss();
            params.clear();
            try {
                success = json.getInt("success");
                if (success == 1) {

                    json_usercount = json.getJSONArray("usercount");//usercount bilgilerini cektik
                    for (int i = 0; i < ROOM_COUNT; i++)
                        usercounts[i] = Integer.parseInt(json_usercount.get(i)+"");//usercount bilgilerini integer'a cevirdik

                    json_user1id = json.getJSONArray("user1id");//user1id bilgilerini cektik
                    for (int i = 0; i < ROOM_COUNT; i++)
                        user1ids[i] = Integer.parseInt(json_user1id.get(i)+"");//user1id bilgilerini integer'a cevirdik

                    json_user2id = json.getJSONArray("user2id");//user1id bilgilerini cektik
                    for (int i = 0; i < ROOM_COUNT; i++)
                        user2ids[i] = Integer.parseInt(json_user2id.get(i)+"");//user1id bilgilerini integer'a cevirdik

                    furl = json.getString("message");
                    //Toast.makeText(multiRoomPanel.this, furl, Toast.LENGTH_SHORT).show(); succesfully roompanel access
                    if(startControl) {//ilk basta odalari olusturmak icin yapildi.
                        for (int i = 0; i < ROOM_COUNT; i++)
                            rooms.add(new Room("ROOM-" + (i + 1), usercounts[i]));// ikinci parametre odada kaç kişinin olduğu, ilk basta 0 olarak atandi hepsine
                        startControl = false;
                    }
                    else {//daha sonra usercount changing icin
                        for (int i = 0; i < ROOM_COUNT; i++) {
                            rooms.get(i).setNumberOfRoom(usercounts[i]);
                            if(adaptorr != null)
                                adaptorr.notifyDataSetChanged();
                        }
                    }
                    if(clickControl){
                        //room a tiklama olayi ve kontroller olcak
                        clickRoom();
                        clickControl = false;
                    }
                } else {

                    furl = json.getString("message");
                    Toast.makeText(multiRoomPanel.this, furl, Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);
            new FetchMessage().execute();

        }
    }
    class infoMessage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... args) {


            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", Integer.toString(user_id)));

            json = jsonParser.makeHttpRequest(
                    LOGIN_URL4, "POST", params);

            return "";

        }

        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;

            params.clear();
            try {
                success = json.getInt("success");
                if (success == 1) {
                    // Log.d("User Created!", json.toString());
                    //   finish();
                    userName = json.getString("username");
                    email=json.getString("email");
                    experince=json.getString("exp");
                    level = json.getString("level");

                    userNameText.setText((userName));
                    emailText.setText(email);
                    experianceText.setText(experince);
                    levelText.setText(level);

                } else {
                    // Log.d("Login Failure!", json.getString(TAG_MESSAGE));

                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);
            popupScoreWindow.showAtLocation(popupScoreLayout, Gravity.CENTER, 0, 0);
            popupScoreWindow.setFocusable(true);
        }


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        v.startAnimation(buttonClick);
        switch (id)
        {
            case R.id.multi_popup_button_dismiss:
                popWindow.setFocusable(false);
                popWindow.update();
                popWindow.dismiss();
                break;

            case R.id.logout:
                Intent intent=new Intent(this,LoginScreen.class);
                finish();
                startActivity(intent);
                break;

            case R.id.info:
                new infoMessage().execute();//buraya bi bak

                break;
            case R.id.okButton:
                popupScoreWindow.setFocusable(false);
                popupScoreWindow.update();
                popupScoreWindow.dismiss();
                break;



        }


    }
}
