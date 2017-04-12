package utku.guleviz.anagram.androidproject;


public class TempStore {
    private static String path1;
    private static String msj1;
    private static int preClass;
    public static int getPreClass(){return preClass;}
    public static String getPath() {
        return path1;
    }
    public static String getMes() {
        return msj1;
    }

    public static void setPath(String path) {
        path1 = path;
    }
    public static void setMes(String msj) {
        msj1 = msj;
    }
    public static void setPreClass(int m) { preClass = m; }
}