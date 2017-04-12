package utku.guleviz.anagram.androidproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import android.graphics.Canvas;
import android.graphics.Bitmap;


import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;





public class CounterClass extends CountDownTimer implements View.OnClickListener{
    Activity t = null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    TextView textViewTime;
    static long millis = 0;
    private View scoreLayout;
    private PopupWindow scoreWindow;
    private TextView scoreText;
    Button facebookPopup,twitterPopup,exitPopup;
    String photoPath =  Environment.getExternalStorageDirectory().toString() + "/ScoreScreen.jpg";

    public void scorePopup() {


        //popupButton.setOnClickListener(this);
        //insidePopupButton.setOnClickListener(this);
        LayoutInflater inflater = (LayoutInflater)t.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scoreLayout= inflater.inflate(R.layout.score,
                (ViewGroup)t.findViewById(R.id.scorePopup));

        Display display = t.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();


        scoreWindow = new PopupWindow(scoreLayout,width,height,true);
        facebookPopup = (Button)scoreLayout.findViewById(R.id.score_face);
        facebookPopup.setOnClickListener(this);
        twitterPopup = (Button)scoreLayout.findViewById(R.id.score_twit);
        twitterPopup.setOnClickListener(this);
        exitPopup = (Button)scoreLayout.findViewById(R.id.score_exit);
        exitPopup.setOnClickListener(this);
        scoreText=(TextView)scoreLayout.findViewById(R.id.score_point);
        scoreText.setTypeface(MainActivity.typeFace);


    }
    public CounterClass(long millisInFuture, long countDownInterval, Activity t, TextView textViewTime) {
        super(millisInFuture, countDownInterval);
        this.t = t;
        this.textViewTime = textViewTime;
    }


    @Override
    public void onFinish() {
        scorePopup();
        if(t instanceof MainActivity) {
            scoreText.setText(""+MainActivity.CURRENT_SCORE);
        }
        else{
            scoreText.setText(""+MultiMainActivity.CURRENT_SCORE);
        }
        scoreWindow.showAtLocation(scoreLayout, Gravity.CENTER,0,0);
        scoreWindow.setFocusable(true);

    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onTick(long millisUntilFinished) {
        millis = millisUntilFinished;
        String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);
        textViewTime.setText(hms);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.score_exit:
                if(t instanceof MainActivity) {
                    MainActivity.CURRENT_SCORE = 0;
                    t.finish();
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();
                }
                else if(t instanceof MultiMainActivity)
                {
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();
                    MultiMainActivity current;
                    current = (MultiMainActivity)t;
                    current.new FinishGame().execute();

                }

                break;
            case R.id.score_face: // face e yonlendir
                if(t instanceof MainActivity) {
                    popUpFoto(scoreWindow.getContentView());
                    FaceTwitterPaylas.openFacebookShare(t, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ");
                    MainActivity.CURRENT_SCORE = 0;
                    t.finish();
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();

                }
                else if (t instanceof MultiMainActivity)
                {
                    popUpFoto(scoreWindow.getContentView());
                    FaceTwitterPaylas.openFacebookShare(t, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ");
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();
                    MultiMainActivity current;
                    current = (MultiMainActivity)t;
                    current.new FinishGame().execute();

                }


                break;
            case R.id.score_twit:
                if(t instanceof MainActivity) {

                    popUpFoto(scoreWindow.getContentView());
                    FaceTwitterPaylas.openTwitterShare(t, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ", 0);
                    MainActivity.CURRENT_SCORE = 0;
                    t.finish();
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();
                }
                else if(t instanceof MultiMainActivity)
                {

                    popUpFoto(scoreWindow.getContentView());
                    FaceTwitterPaylas.openTwitterShare(t, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ", 1);
                    scoreWindow.setFocusable(false);
                    scoreWindow.update();
                    scoreWindow.dismiss();
                    MultiMainActivity current;
                    current = (MultiMainActivity)t;
                    current.new FinishGame().execute();

                }

                /*twit e yönlendir*/
                break;

        }

    }

    public void popUpFoto(View v){
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        File screen = new File(Environment.getExternalStorageDirectory()
                .toString(), "ScoreScreen.jpg");
        if (screen.exists())
            screen.delete();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(screen);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.close();
            }
        } catch (Exception e) {

        }

        //return bitmap;

    }
}