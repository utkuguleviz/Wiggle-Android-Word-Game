package utku.guleviz.anagram.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;


public class singleMultiPlayer extends Activity implements View.OnClickListener {
    Button singleButton=null,multiButton=null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_multi);
        singleButton = (Button)findViewById(R.id.single_button);
        multiButton = (Button)findViewById(R.id.multi_button);
        new XMLParser(singleMultiPlayer.this); // xml parçalama işlemi
        singleButton.setOnClickListener(this);
        multiButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(buttonClick);
        switch (id) {
            case R.id.single_button:
                Intent gameIntent = new Intent(this, LevelSelect.class);
                startActivity(gameIntent);
                break;

            case R.id.multi_button:
                gameIntent = new Intent(this, LoginScreen.class);
                startActivity(gameIntent);
                break;


        }
    }
}
