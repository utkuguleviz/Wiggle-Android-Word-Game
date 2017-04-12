package utku.guleviz.anagram.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LevelSelect extends Activity implements View.OnClickListener {

    Button easyButton = null, mediumButton = null, hardButton = null, insaneButton = null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "utku.guleviz.anagram.androidhw_3_4", PackageManager.GET_SIGNATURES); //Your            package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select);
        easyButton = (Button)findViewById(R.id.easy_button);
        mediumButton = (Button)findViewById(R.id.medium_button);
        hardButton = (Button)findViewById(R.id.hard_button);
        insaneButton = (Button)findViewById(R.id.insane_button);

        //showHashKey(getApplicationContext()); KEYHASH PROBLEMI CIKARSA BURAYI AC BAK. :)

        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);
        insaneButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(buttonClick);
        switch (id){
            case R.id.easy_button:
                MainActivity.BUTTON_CHOICE = 0; // easy zorluk
                Intent gameIntent = new Intent(this, MainActivity.class);
                startActivity(gameIntent);
                break;

            case R.id.medium_button:
                MainActivity.BUTTON_CHOICE = 1; // medium zorluk
                gameIntent = new Intent(this, MainActivity.class);
                startActivity(gameIntent);
                break;

            case R.id.hard_button:
                MainActivity.BUTTON_CHOICE = 2; // hard zorluk
                gameIntent = new Intent(this, MainActivity.class);
                startActivity(gameIntent);
                break;
            case R.id.insane_button:
                MainActivity.BUTTON_CHOICE = 3; // insane
                gameIntent = new Intent(this, MainActivity.class);
                startActivity(gameIntent);
                break;
        }


    }
}
