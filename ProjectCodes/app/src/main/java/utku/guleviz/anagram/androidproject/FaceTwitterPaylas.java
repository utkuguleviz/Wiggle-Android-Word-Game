package utku.guleviz.anagram.androidproject;


import android.content.Intent;
import android.app.Activity;

public class FaceTwitterPaylas {

    //previousClass 0 ise single, 1 ise multi
    public static void openTwitterShare(Activity page, String uri, String twitterMessage, int previousClass) {//burayi accaz

        Intent twitterIntent = new Intent(page, TwitterActivity.class);
        twitterIntent.putExtra("URI", uri);
        twitterIntent.putExtra("TWITTERMESSAGE", twitterMessage);
        twitterIntent.putExtra("PREVIOUSCLASS", previousClass);
        page.startActivityForResult(twitterIntent, 4);//4 twitterPref
    }

    public static void openFacebookShare(Activity editPage, String photoPath, String facebookMesaage) {
        Intent faceIntent = new Intent(editPage, FacebookActivity.class);
        faceIntent.putExtra("URI", photoPath);
        faceIntent.putExtra("FACEBOOKMESSAGE", facebookMesaage);
        editPage.startActivityForResult(faceIntent, 4);//4 twitterPref
    }

}
