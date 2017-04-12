package utku.guleviz.anagram.androidproject;



        import android.app.Activity;
        import android.app.Dialog;
        import android.view.animation.Animation;
        import android.view.animation.Animation.AnimationListener;
        import android.view.animation.BounceInterpolator;
        import android.view.animation.TranslateAnimation;
        import android.widget.ImageView;

public class LoadAnimation {
    private static ImageView view;
    private static Dialog dialog;
    private static boolean stopAnimation;

    public static void runLoadAnimation(Activity activity) {
        stopAnimation = false;
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.loading);
        dialog.setTitle("Loading");
        view = (ImageView) dialog.findViewById(R.id.imageloading);
        int[] location = new int[2];
        dialog.show();
        // Animation part :)
        view.getLocationInWindow(location);
        view.clearAnimation();
        final TranslateAnimation an1 = new TranslateAnimation(0, 400, 0, 0);
        an1.setInterpolator(new BounceInterpolator());
        an1.setStartOffset(500);
        an1.setDuration(1000);
        an1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (stopAnimation) {
                    return;
                }
                an1.setStartOffset(500);
                an1.setDuration(1000);
                view.startAnimation(an1);
            }
        });
        view.startAnimation(an1);

    }

    public static void stopLoadAnimation() {
        stopAnimation = true;
        dialog.dismiss();
    }

}

