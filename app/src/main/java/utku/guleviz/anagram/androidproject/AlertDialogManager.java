package utku.guleviz.anagram.androidproject;



        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.DialogInterface.OnClickListener;

public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     *
     * @param context
     *            - application context
     * @param title
     *            - alert dialog title
     * @param message
     *            - alert message
     * @param status
     *            - success/failure (used to set icon) - pass null if you don't
     *            want icon
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);


        // Showing Alert Message
        alertDialog.show();
    }
}
