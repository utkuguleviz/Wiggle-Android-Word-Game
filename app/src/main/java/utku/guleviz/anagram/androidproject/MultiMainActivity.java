package utku.guleviz.anagram.androidproject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MultiMainActivity extends Activity implements OnClickListener { //kodun sağ üst köşesindeki solve butonu için kullanılmış. Daha doğrusu kodda ActionBar kullanabilmek için.
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    static int BUTTON_CHOICE = 1; // zorluk seviyesi olarak ne seçtiği. 0 easy, 1 medium, 2 hard. BURADA OTOMATIK MEDIUM YAPTIK
    static int pop_on, pop_off, congrats, swosh;
    static SoundPool soundPool = null;
    int solveCounter = 5;




    PopupWindow popupMessage;
    Button popupButton, insidePopupButton;
    View layouttt;

    CustomArrayAdapter adapter;
    static Typeface typeFace=null;
    boolean micPressed = false;
    ArrayList<ImageView> ivList, ivSecim = new ArrayList<ImageView>();
    int kacSecildi = 0, harfSirasi = 0;
    ListView lvClues;
    String kelime = "", shuffledKelime = "";
    ArrayList<String> clues;
    String[] values;
    InputStream is;
    Random randomGenerator = new Random();
    Drawable letter;
    RelativeLayout RLWord, secondary;
    ImageView iv;
    TextView textViewTime = null, textViewScore = null,textViewHighScore=null;
    CounterClass timer = null;
    Button startStop = null, microphoneButton = null,pauseMenuStart = null, pauseMenuExit = null;
    static int CURRENT_SCORE = 0;
    protected static final int RESULT_SPEECH = 1;

    //php variables
    int[] randomWordIndex = new int[30];
    int kelimeIndex = 0;//birer birer artarak randomWordIndexten kelime ceker
    int user_id = 0, user_count = 0, click_position = 0;//intent ten cekilcekler
    List<NameValuePair> params;
    JSONParser jsonParser = new JSONParser();
    JSONObject json;
    private static final String LOGIN_URL = "http://wiggle.mksengun.com/finishgame.php";
    private static final String LOGIN_URL2 = "http://wiggle.mksengun.com/startgame.php";



    public void popupInit() {


        //popupButton.setOnClickListener(this);
        //insidePopupButton.setOnClickListener(this);
        LayoutInflater inflater = (LayoutInflater) MultiMainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layouttt = inflater.inflate(R.layout.pause_menu,
                (ViewGroup) findViewById(R.id.popup_element));

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        popupMessage = new PopupWindow(layouttt,width,height,true);
        pauseMenuStart = (Button)layouttt.findViewById(R.id.pause_menu_start);
        pauseMenuStart.setOnClickListener(this);
        pauseMenuExit = (Button)layouttt.findViewById(R.id.pause_menu_exit);
        pauseMenuExit.setOnClickListener(this);


    }

    public void getIntentVariables(){
        if(getIntent().getExtras() != null){
            user_id = getIntent().getExtras().getInt("user_id", 0);//kullanicinin id si
            click_position = getIntent().getExtras().getInt("click_position", 0);//tiklanan room position i. 0dan baslar
            user_count = getIntent().getExtras().getInt("user_count", 0);//tiklanan odadaki user sayisi.Bu user dahil degilken
            //Toast.makeText(MultiMainActivity.this, user_id + " " + click_position +" "+ user_count, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeFace= Typeface.createFromAsset(getAssets(), "fonts/waltographUI.ttf");
        setContentView(R.layout.multi_main_activity);
        getIntentVariables();
        textViewTime = (TextView) findViewById(R.id.counter_textviewM);
        textViewScore = (TextView) findViewById(R.id.score_textviewM);
        textViewHighScore = (TextView) findViewById(R.id.highscore_textviewM);
        textViewScore.setTypeface(typeFace);
        textViewScore.setTypeface(typeFace);
        textViewHighScore.setTypeface(typeFace);
        textViewScore.setTextColor(Color.WHITE);
        int high=loadPrefs();
        textViewScore.setText(""+CURRENT_SCORE);
        textViewHighScore.setText(""+high);
        microphoneButton = (Button) findViewById(R.id.microphone_buttonM);
        microphoneButton.setOnClickListener(this);
        startStop = (Button)findViewById(R.id.start_stop_buttonM);
        startStop.setOnClickListener(this);
        textViewTime.setText("03.00");
        textViewTime.setTextColor(Color.WHITE);
        timer = new CounterClass(200000, 1000, this, textViewTime); // milisaniye.
        timer.start();
        //View v = findViewById((R.id.rlWord));
        //v.setKeepScreenOn(true);

        lvClues = (ListView) findViewById(R.id.lvCluesM);
        RLWord = (RelativeLayout) findViewById(R.id.rlWordM);
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        pop_on = soundPool.load(this, R.raw.pop_on, 1);
        pop_off = soundPool.load(this, R.raw.pop_off, 1);
        congrats = soundPool.load(this, R.raw.congrats, 1);
        swosh = soundPool.load(this, R.raw.swosh, 1);
        new StartGame().execute();//INIT BURANIN ICINDE wordIndex cekebilmek icin
        popupInit();

    }

    //kelime sec karistir vs.
    public void init() {

        BUTTON_CHOICE = 1;//for medium
        ivSecim = new ArrayList<ImageView>();
        kacSecildi = 0;
        secondary = new RelativeLayout(this);//

        RelativeLayout.LayoutParams lpSecondary = new RelativeLayout.LayoutParams( // seconder layoutu set ediyorsun
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpSecondary.addRule(RelativeLayout.CENTER_IN_PARENT);
        RLWord.addView(secondary, lpSecondary);//secondari set et, view olarak RLWORD'e ekle. activity_maindeki relativeLayout içine yeni bir relative layout ekledin.

        try {
            xmlParse(); // kelimeyi set eder.
            shuffleKelime();
            for (int i = 0; i < shuffledKelime.length(); i++) {
                setLetter(shuffledKelime.charAt(i));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        adapter = new CustomArrayAdapter(MultiMainActivity.this, // xmlParse içinde values arrayi doluyor.
                values);
        lvClues.setAdapter(adapter);



    }

    //kelime karistir
    public void shuffleKelime() {
        List<String> letters = Arrays.asList(kelime.split(""));
        shuffledKelime = "";
        while (true) {
            Collections.shuffle(letters); // letters isimli listteki her elementi random karıştırır.
            shuffledKelime = "";
            for (String letter : letters) {
                shuffledKelime += letter;
            }
            if (!kelime.equals(shuffledKelime))
                break;
        }

    }

    //image view a image atadigimiz yer.
    public void setLetter(char c) throws Exception { //otn

        alphabet(c);// harfin imageini okuma islemi.
        ivList.add(new ImageView(this));
        ivList.get(harfSirasi).setImageDrawable(letter); // letter da bir resim var suan
        ivList.get(harfSirasi).setAlpha((float) 0.5);

        RelativeLayout.LayoutParams lpWord = new RelativeLayout.LayoutParams(
                75, 75);
        lpWord.setMargins(harfSirasi * 80, 0, 0, 0); // her bir letter diğerinden 80 uzaklıkta.

        secondary.addView(ivList.get(harfSirasi), lpWord); // add a child view(burada image) with special layout parameters

        ivList.get(harfSirasi).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                float alpha = v.getAlpha();

                if (alpha == 0.5 && kacSecildi < 2) {
                    soundPool.play(pop_on, 1, 1, 1, 0, 1.7f);
                }

                if (alpha == 1) {
                    soundPool.play(pop_off, 1, 1, 1, 0, 1.7f);
                    v.setAlpha((float) 0.5);
                    ivSecim.remove(0);
                    kacSecildi = 0;
                } else if (kacSecildi < 2) {
                    v.setAlpha((float) 1);
                    ImageView ivA = (ImageView) v;
                    ivSecim.add(ivA);
                    kacSecildi++;

                    if (kacSecildi == 2) {
                        change();
                    }

                }
            }
        });
        harfSirasi++;

    }

    //yer degistirme animasyonu
    public void change() {//

        final ImageView iv1 = ivSecim.get(0);
        final ImageView iv2 = ivSecim.get(1);// sectigimiz

        int marg1 = ivList.indexOf(iv1) * 80;
        int marg2 = ivList.indexOf(iv2) * 80;

        final int index1 = ivList.indexOf(ivSecim.get(0));
        final int index2 = ivList.indexOf(ivSecim.get(1));

        final RelativeLayout.LayoutParams param1 = (LayoutParams) iv1
                .getLayoutParams();
        final RelativeLayout.LayoutParams param2 = (LayoutParams) iv2
                .getLayoutParams();

        final TranslateAnimation aIV1 = new TranslateAnimation(0,
                marg2 - marg1, 0, 0);// burayi ayarla
        final TranslateAnimation aIV2 = new TranslateAnimation(0,
                marg1 - marg2, 0, 0);

        if (marg1 > marg2) {
            aIV1.setInterpolator(new DecelerateInterpolator());
            aIV2.setInterpolator(new AccelerateInterpolator());
        } else if (marg1 < marg2) {
            aIV1.setInterpolator(new AccelerateInterpolator());
            aIV2.setInterpolator(new DecelerateInterpolator());
        }

        aIV1.setDuration(1000);
        aIV2.setDuration(1000);

        aIV1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                disableEnable(0);
                soundPool.play(swosh, 1, 1, 1, 0, 1.7f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub


                iv1.setLayoutParams(param2);
                iv2.setLayoutParams(param1);


                iv1.setAlpha((float) 0.5);
                iv2.setAlpha((float) 0.5);


                StringBuilder tmp = new StringBuilder(shuffledKelime);// harf
                // change
                char c1 = shuffledKelime.charAt(index1);
                char c2 = shuffledKelime.charAt(index2);
                tmp.setCharAt(index1, c2);
                tmp.setCharAt(index2, c1);
                shuffledKelime = tmp.toString();
                disableEnable(1);
                kacSecildi = 0;
                finishControl();


            }
        });

        iv1.startAnimation(aIV1);
        iv2.startAnimation(aIV2);

        ivList.set(index2, ivSecim.get(0));
        ivList.set(index1, ivSecim.get(1));

        ivSecim = new ArrayList<ImageView>();


    }

    public boolean finishControl(){
        if (shuffledKelime.equals(kelime)) {

            soundPool.play(congrats, 1, 1, 1, 0, 1.7f);
            Toast toast = Toast.makeText(getApplicationContext(), // tebrikler
                    // mesaji
                    "Congrats!", Toast.LENGTH_SHORT);
            toast.setGravity(
                    Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    int highScore=loadPrefs();
                    for (int i = 0; i < ivList.size(); i++) {
                        ivList.get(i).setVisibility(View.GONE);
                        ;
                    }
                    if(BUTTON_CHOICE == 0) {
                        CURRENT_SCORE += 20;
                        if(highScore<CURRENT_SCORE){
                            savePrefs("HIGH_SCORE",CURRENT_SCORE);
                            highScore=loadPrefs();
                        }
                        textViewScore.setText(""+CURRENT_SCORE);
                        textViewHighScore.setText(""+highScore);
                    }

                    else  if(BUTTON_CHOICE == 1) {
                        CURRENT_SCORE += 40;
                        if(highScore<CURRENT_SCORE){
                            savePrefs("HIGH_SCORE",CURRENT_SCORE);
                            highScore=loadPrefs();
                        }
                        textViewScore.setText(""+CURRENT_SCORE);
                        textViewHighScore.setText(""+highScore);
                    }

                    else  if(BUTTON_CHOICE == 2) {
                        CURRENT_SCORE += 60;
                        if(highScore<CURRENT_SCORE){
                            savePrefs("HIGH_SCORE",CURRENT_SCORE);
                            highScore=loadPrefs();
                        }
                        textViewScore.setText(""+CURRENT_SCORE);
                        textViewHighScore.setText(""+highScore);
                    }

                    init(); // degistirmen gereken kısım. kelime her doğru bilindiğinde tekrar tekrar çağrılıyor.
                }
            }, 2000);
            kacSecildi = 2;
            return true;
        }
        return false;
    }

    //solve buttonu inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // solve butonu kodda menu olarak eklenmiş. yani genel activity_main layoutu içinde değil.

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //assets den harf image okuma
    public void alphabet(char harf) throws Exception {
        AssetManager assetManager = getAssets();

        if (harf == 'a') {
            is = assetManager.open("alphabet/Letter-A-icon.png");
            letter = Drawable.createFromStream(is, null); // letter'a bir resim atadın. // harfleri assetmanagera koydugu icin AssetManager kullanılmıs.
        } else if (harf == 'b') {
            is = assetManager.open("alphabet/Letter-B-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'c') {
            is = assetManager.open("alphabet/Letter-C-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'd') {
            is = assetManager.open("alphabet/Letter-D-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'e') {
            is = assetManager.open("alphabet/Letter-E-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'f') {
            is = assetManager.open("alphabet/Letter-F-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'g') {
            is = assetManager.open("alphabet/Letter-G-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'h') {
            is = assetManager.open("alphabet/Letter-H-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'i') {
            is = assetManager.open("alphabet/Letter-I-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'j') {
            is = assetManager.open("alphabet/Letter-J-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'k') {
            is = assetManager.open("alphabet/Letter-K-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'l') {
            is = assetManager.open("alphabet/Letter-L-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'm') {
            is = assetManager.open("alphabet/Letter-M-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'n') {
            is = assetManager.open("alphabet/Letter-N-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'o') {
            is = assetManager.open("alphabet/Letter-O-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'p') {
            is = assetManager.open("alphabet/Letter-P-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'q') {
            is = assetManager.open("alphabet/Letter-Q-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'r') {
            is = assetManager.open("alphabet/Letter-R-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 's') {
            is = assetManager.open("alphabet/Letter-S-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 't') {
            is = assetManager.open("alphabet/Letter-T-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'u') {
            is = assetManager.open("alphabet/Letter-U-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'v') {
            is = assetManager.open("alphabet/Letter-V-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'w') {
            is = assetManager.open("alphabet/Letter-W-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'x') {
            is = assetManager.open("alphabet/Letter-X-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'y') {
            is = assetManager.open("alphabet/Letter-Y-icon.png");
            letter = Drawable.createFromStream(is, null);
        } else if (harf == 'z') {
            is = assetManager.open("alphabet/Letter-Z-icon.png");
            letter = Drawable.createFromStream(is, null);
        }

    }

    //xml parse edip kelime ve meaning ayarlama
    public void xmlParse() throws Exception { // random kelime sectik ve kelime
        ivList = new ArrayList<ImageView>();
        harfSirasi = 0; // kelime yenilendiginden siralamayi kelime

        if (BUTTON_CHOICE == 0) {  // EASY ZORLUK

            int randWord = randomWordIndex[kelimeIndex++];
            kelime = XMLParser.threeToFour.get(randWord).get(0); // 0. index kelime gerisi clue lar.
            kelime = kelime.toLowerCase();
            values = new String[XMLParser.threeToFour.get(randWord).size() - 1];// array listten
            for (int i = 1; i <= values.length; i++)  // 0. index kelime ismi, geri kalanlar kelimeye ait ipuçları olduğu için 1 den başlıyor.
                values[i - 1] = XMLParser.threeToFour.get(randWord).get(i); // CustomArrayAdaptor string arrayi alıyor bu nedenle stringe attın.
        } else if (BUTTON_CHOICE == 1) { // MEDIUM ZORLUK
           // Toast.makeText(MultiMainActivity.this, ""+randomWordIndex[kelimeIndex], Toast.LENGTH_SHORT).show();
            int randWord = randomWordIndex[kelimeIndex++];
            kelime = XMLParser.fiveToSix.get(randWord).get(0); // 0. index kelime gerisi clue lar.
            kelime = kelime.toLowerCase();
            values = new String[XMLParser.fiveToSix.get(randWord).size() - 1];// array listten
            for (int i = 1; i <= values.length; i++)  // 0. index kelime ismi, geri kalanlar kelimeye ait ipuçları olduğu için 1 den başlıyor.
                values[i - 1] = XMLParser.fiveToSix.get(randWord).get(i); // CustomArrayAdaptor string arrayi alıyor bu nedenle stringe attın.
        } else { // BUTTON CHOICE == 2 HARD ZORLUK

            int randWord = randomWordIndex[kelimeIndex++];
            kelime = XMLParser.sevenToEight.get(randWord).get(0); // 0. index kelime gerisi clue lar.
            kelime = kelime.toLowerCase();
            values = new String[XMLParser.sevenToEight.get(randWord).size() - 1];// array listten
            for (int i = 1; i <= values.length; i++)  // 0. index kelime ismi, geri kalanlar kelimeye ait ipuçları olduğu için 1 den başlıyor.
                values[i - 1] = XMLParser.sevenToEight.get(randWord).get(i); // CustomArrayAdaptor string arrayi alıyor bu nedenle stringe attın.
        }
    }

    //solve butonuna basinca nolcak?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_solve:
                if(solveCounter > 0) {
                    solveCounter--;
                    item.setTitle("SOLVE(" + solveCounter +")");

                    CURRENT_SCORE -= 10;//solvea basinca puan kaybi
                    textViewScore.setText(""+CURRENT_SCORE);

                    for (int i = 0; i < ivList.size(); i++) {
                        try {
                            alphabet(kelime.charAt(i));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        ivList.get(i).setImageDrawable(letter);
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 0; i < ivList.size(); i++) {
                                ivList.get(i).setVisibility(View.GONE);
                                ;
                            }
                            init();
                        }
                    }, 1000);

                    kacSecildi = 2;

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!micPressed && timer != null) {
            timer.cancel();
            timer = null;
        }
        micPressed = false;;
        //   new FinishGame().execute();
        //finish();
    }

    @Override
    public void onBackPressed() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        new FinishGame().execute();

    }

    private void updateView(int count, int size,boolean isVisible){

        for(int i = 0; i<count; i++){
            View v = lvClues.getChildAt(i);
            if(v!=null) {
                TextView someText = (TextView) v.findViewById(R.id.tvClue);
                someText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            }
        }

        if(isVisible) {
            for (int i = 0; i < ivList.size();i++)
                ivList.get(i).setVisibility(View.VISIBLE);
        }
        else
            for (int i = 0; i < ivList.size();i++)
                ivList.get(i).setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {

        int buttonId = v.getId();
        // TextView clues = (TextView)findViewById(R.id.tvClue);
        v.startAnimation(buttonClick);
        int listSize;
        switch (buttonId)
        {
            case R.id.start_stop_buttonM:
                //startStop.setBackgroundResource(R.drawable.play);
                timer.cancel();
                //   timer = null;
                Button b = (Button)findViewById(R.id.start_stop_buttonM);
                listSize = adapter.getCount();
                updateView(listSize,1,false);
                popupMessage.showAtLocation(layouttt, Gravity.CENTER, 0, 0);
                popupMessage.setFocusable(true);
                b.setFocusable(true);

                break;


            case R.id.pause_menu_start: // popup icindeki start
                listSize = adapter.getCount();
                popupMessage.setFocusable(false);
                popupMessage.update();
                timer = new CounterClass(CounterClass.millis, 1000, MultiMainActivity.this, textViewTime);
                timer.start();
                popupMessage.dismiss();
                updateView(listSize,10,true);
                break;

            case R.id.pause_menu_exit:
                timer.cancel();
                timer = null;
                listSize = adapter.getCount();
                updateView(listSize,10,true);
                popupMessage.setFocusable(false);
                popupMessage.update();
                popupMessage.dismiss();
                new FinishGame().execute();
                break;

            case R.id.microphone_buttonM:
                micPressed = true;
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Ops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }

                break;


        }

    }
    public void disableEnable(int disEn){
        if(disEn == 0){
            for(int i = 0; i < ivList.size(); i++)
                ivList.get(i).setEnabled(false);
        }else{
            for(int i = 0; i < ivList.size(); i++)
                ivList.get(i).setEnabled(true);
        }
    }
    private void savePrefs(String key,int value){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=sp.edit();
        edit.putInt(key,value);
        edit.commit();
    }
    private int loadPrefs(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        int highScore=sp.getInt("HIGH_SCORE",0);
        return highScore;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String shuffledTmp = shuffledKelime;
                    shuffledKelime = text.get(0);//shuffled kelime de bu var.

                    if( kelime.equals(shuffledKelime) ){

                        soundPool.play(congrats, 1, 1, 1, 0, 1.7f);
                        Toast toast = Toast.makeText(getApplicationContext(), // tebrikler
                                // mesaji
                                "Congrats!", Toast.LENGTH_SHORT);
                        toast.setGravity(
                                Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        for (int i = 0; i < ivList.size(); i++) {
                            try {
                                alphabet(kelime.charAt(i));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            ivList.get(i).setImageDrawable(letter);
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                for (int i = 0; i < ivList.size(); i++) {
                                    ivList.get(i).setVisibility(View.GONE);
                                    ;
                                }
                                init();
                            }
                        }, 1000);

                        kacSecildi = 2;
                    }
                    else {
                        shuffledKelime = shuffledTmp;
                        Toast t = Toast.makeText(getApplicationContext(),
                                "Ops! " + text.get(0)+" is wrong :(",
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                    //txtText.setText(text.get(0));
                }
                break;
            }

        }
    }

    class FinishGame extends AsyncTask<String, String, String>{//oyun bittiginde cagircagimiz task

        boolean failure = false;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MultiMainActivity.this); // context alıyor.
            pDialog.setMessage("The game is finishing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... p) {
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("score", CURRENT_SCORE+""));//skor guncellencek simdilik 100 !!!!!!!
            params.add(new BasicNameValuePair("click_position", click_position+""));
            params.add(new BasicNameValuePair("user_id", user_id+""));
            params.add(new BasicNameValuePair("user_count", user_count+""));

            json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);

            return "";
        }

        protected void onPostExecute(String f) {


            this.cancel(true);
            pDialog.dismiss();
            CURRENT_SCORE = 0;
            finish();

        }
    }

    class StartGame extends AsyncTask<String, String, String>{//oyun baslarken kelime indexlerini cekmek icin kullanilcak

        @Override
        protected String doInBackground(String... p) {
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("click_position", click_position+""));
            json = jsonParser.makeHttpRequest(
                    LOGIN_URL2, "POST", params);
            return null;
        }
        protected void onPostExecute(String f) {
            // dismiss the dialog once product deleted
            int success = 0;
            String furl = "";
            try {
                success = json.getInt("success");
                if (success == 1) {
                    String wordIndex = json.getString("WordIndex");
                    int indexBas = 0, indexSon = 0, diziIndex = 0;

                    for(int i = 0; wordIndex.length() > i; i++){
                        if(wordIndex.charAt(i) == '_') {
                            indexSon = i;
                            randomWordIndex[diziIndex] = Integer.parseInt(wordIndex.substring(indexBas,indexSon));
                            diziIndex++;
                            indexBas = i + 1;//alt tireden sonraki indexe gecer.
                        }
                    }

                    init();//BASLANGIC BURADA
                } else {
                    // Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    furl = json.getString("message");
                   // Toast.makeText(MultiMainActivity.this, furl, Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();

            }
            this.cancel(true);





        }
    }


}
