package utku.guleviz.anagram.androidproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

public class roomAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Room> mPersonList;

    public roomAdapter(Activity activity, List<Room> persons) {
        //XML'i alıp View'a çevirecek inflater'ı örnekleyelim
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        //gösterilecek listeyi de alalım
        mPersonList = persons;
    }

    @Override
    public int getCount() {
        return mPersonList.size();
    }

    @Override
    public Room getItem(int position) {

        return mPersonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View satirView;

        satirView = mInflater.inflate(R.layout.listitemroom, null);
        TextView textView =
                (TextView) satirView.findViewById(R.id.roomName);
        textView.setTypeface(multiRoomPanel.typeFace);
        TextView textView_number =
                (TextView) satirView.findViewById(R.id.numberOfPerson);
        textView_number.setTypeface(multiRoomPanel.typeFace);
        ImageView imageView =
                (ImageView) satirView.findViewById(R.id.roomNumber);

         Room room = mPersonList.get(position);
        switch(room.getName()){

            case("ROOM-1"):
                imageView.setImageResource(R.drawable.room1);
                break;
            case("ROOM-2"):
                imageView.setImageResource(R.drawable.room2);
                break;
            case("ROOM-3"):
                imageView.setImageResource(R.drawable.room3);
                break;
            case("ROOM-4"):
                imageView.setImageResource(R.drawable.room4);
                break;
            case("ROOM-5"):
                imageView.setImageResource(R.drawable.room5);
                break;
            case("ROOM-6"):
                imageView.setImageResource(R.drawable.room6);
                break;
            case("ROOM-7"):
                imageView.setImageResource(R.drawable.room7);
                break;
            case("ROOM-8"):
                imageView.setImageResource(R.drawable.room8);
                break;
            case("ROOM-9"):
                imageView.setImageResource(R.drawable.room9);
                break;
            case("ROOM-10"):
                imageView.setImageResource(R.drawable.room10);
                break;
        }
        textView.setText(room.getName());
        textView_number.setText(room.getNumberoOfRoom()+"/2");
        return satirView;
    }
}