package utku.guleviz.anagram.androidproject;

import android.app.Activity;
import android.content.res.AssetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class XMLParser {

    static List<List<String>> threeToFour = new ArrayList<>(); // 0. index ana kelime, listenin geri kalan elemanları clues.
    static List<List<String>> fiveToSix = new ArrayList<>(); // 0. index ana kelime, listenin geri kalan elemanları clues.
    static List<List<String>> sevenToEight = new ArrayList<>(); // 0. index ana kelime, listenin geri kalan elemanları clues.
    Activity current;


    public XMLParser(Activity current)
    {
        this.current = current;
        try {
            xmlParse(threeToFour,"3_4harf.xml");
            xmlParse(fiveToSix,"5_6harf.xml");
            xmlParse(sevenToEight,"7_8harf.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void xmlParse(List<List<String>> genericWordList, String xmlFileName) throws Exception { // random kelime sectik ve kelime
        // degiskenine attik.

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        AssetManager assetManager = current.getAssets();
        InputStream tinstr = assetManager.open(xmlFileName);
        parser.setInput(new InputStreamReader(tinstr));

        int event = parser.getEventType();


        //kelime ve cluesi yarattığın 2d arraylistten random olarak çek.
        while (event != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:

                    if (name.equals("word")) {

                        String kelime = parser.getAttributeValue(null, "word");
                        event = parser.nextTag();
                        genericWordList.add(new ArrayList<String>()); // 0. index kelime gerisi clue şeklinde olacak.
                        genericWordList.get(genericWordList.size()-1).add(kelime);
                 //       System.out.println(wordsWithClues.get(wordsWithClues.size()-1).get(0));

                        while (event != XmlPullParser.END_DOCUMENT) { // meaning tagi icin

                            if (parser.getName().equals("meaning")) {

                                if (parser.next() == XmlPullParser.TEXT) {
                                    genericWordList.get(genericWordList.size()-1).add(parser.getText());
                                 //   System.out.println(parser.getText());
                                    parser.nextTag();
                                }

                            } else
                                break;

                            event = parser.nextTag();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next();
        }
    }
}