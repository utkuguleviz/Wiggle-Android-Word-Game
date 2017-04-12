package utku.guleviz.anagram.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final String[] values;

  public CustomArrayAdapter(Context context, String[] values) {
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
    
    View rowView = inflater.inflate(R.layout.listitem, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.tvClue);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.ivLamp);

    textView.setText(values[position]); // text ve imageviewları önce alıp sonra da istediğin şeye set ediyorsun.
    // change the icon for Windows and iPhone
    imageView.setImageResource(R.drawable.lamp);
    

    return rowView;
  }
} 
