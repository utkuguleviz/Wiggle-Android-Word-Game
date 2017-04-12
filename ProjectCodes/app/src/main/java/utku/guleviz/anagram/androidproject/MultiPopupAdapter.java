package utku.guleviz.anagram.androidproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class MultiPopupAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    String photoPath =  Environment.getExternalStorageDirectory().toString() + "/ScoreScreen.jpg";

    public MultiPopupAdapter(Context context, String[] values) {
        super(context, R.layout.listitem, values); // listItem, listviewdaki her bir itemın layoutun yazıldığı xml dosyası. values, itemların içeriğinin ne olduğu. ListView'ın genel layoutu ile(ekranın neresinde olacağı gibi) her bir itemının layoutunu -->
        this.context = context;//ayrı ayarlıyorsun.
        this.values = values;
    }

    /*yeni bir list view özelligi göstercek
     *
     * getView metodu, values arrayindeki her pozisyon için çağrılır. Yani bu oyun özelinde, ilgili kelime için 5 clue varsa ve value arrayi 5 elemanlıysa, bu her poziyon için çağrılacaktır.
     *
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { // listItemı dinamik olarak değiştirebilmek için implement ettin. her kelimeyle beraber layout değiştiği için ayrı bir class tanımlayıp bunu implement ettin.
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.listitemmultipopup, parent, false);
        ImageView winloss = (ImageView) rowView.findViewById(R.id.multimessage_winloss_image);
        TextView message = (TextView) rowView.findViewById(R.id.multimessage_score_point);

        message.setText(values[position]); // text ve imageviewları önce alıp sonra da istediğin şeye set ediyorsun.
        // change the icon for Windows and iPhone
        if(values[position].contains("kaybettiniz"))
            winloss.setImageResource(R.drawable.looser);

        else if(values[position].contains("berabere")){
            winloss.setImageResource(R.drawable.beraberlik);
        }
        else
            winloss.setImageResource(R.drawable.awards);


        //  Button exit = (Button)rowView.findViewById(R.id.multimessage_score_exit);
        Button face = (Button)rowView.findViewById(R.id.multimessage_score_face);
        Button twit = (Button)rowView.findViewById(R.id.multimessage_score_twit);


        face.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                multiRoomPanel current = (multiRoomPanel)context;
                current.faceClicked = true;
                popUpFoto(rowView);
                FaceTwitterPaylas.openFacebookShare((android.app.Activity) context, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ");//Face
            }
        });
        twit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                multiRoomPanel current = (multiRoomPanel)context;
                current.twitterClicked = true;
                popUpFoto(rowView);
                FaceTwitterPaylas.openTwitterShare((android.app.Activity) context, photoPath, "Wiggle gercekten inanilmaz bir oyun!! ", 1);//1 multi icin
            }
        });

        return rowView;
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
