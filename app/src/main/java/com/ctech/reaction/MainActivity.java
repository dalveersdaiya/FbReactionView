package com.ctech.reaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctech.reaction.util.ElasticAction;
import com.ctech.reaction.widget.ReactionView;
import com.facebook.keyframes.KeyframesDrawable;
import com.facebook.keyframes.KeyframesDrawableBuilder;
import com.facebook.keyframes.deserializers.KFImageDeserializer;
import com.facebook.keyframes.model.KFImage;
import com.fujiyuu75.sequent.Sequent;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    LinearLayout btReaction;
    ReactionView rvl;
    ImageView ivSelectedEmoji;
    TextView tvSelected;
    Vibrator vibrator;
    KeyframesDrawable kfDrawable;

    RelativeLayout relativeParent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();
        applyClickListeners();
        setSequentAnimation(relativeParent);
    }

    public void findViewByIds() {
        btReaction = (LinearLayout) findViewById(R.id.btReaction);
        rvl = (ReactionView) findViewById(R.id.reaction);
        ivSelectedEmoji = (ImageView) findViewById(R.id.ivSelectedEmoji);
        tvSelected = (TextView) findViewById(R.id.tvSelected);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        relativeParent = (RelativeLayout) findViewById(R.id.root);
    }

    public void applyClickListeners() {
        btReaction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btReaction:
//              Set elasticity on Click for the selected View
                setElasticity(btReaction, 0.30f, 400, false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Show Reactions followed by a Delay of the above period
                        showReactions();
                        btReaction.setVisibility(View.GONE);
                    }
                }, 400);
                break;
            default:
                break;
        }
    }

    public void setSequentAnimation(ViewGroup viewGroup) {
        Sequent.origin(viewGroup).start();
    }

    public void showReactions() {
        rvl.init();
        rvl.setVisibility(View.VISIBLE);
        rvl.setOnSelectNotification(new OnSelectNotification() {
            @Override
            public void onSelect(int pos) {
                showSelectedEmoji(pos);
            }

            @Override
            public void onDeselect(int pos) {
            }
        });

    }

    public void showSelectedEmoji(int pos) {
        ivSelectedEmoji.setVisibility(View.VISIBLE);
        KFImage kfImage = null;
        String asset = "";
        String selectedText = "";
        switch (pos) {
            case 0:
                asset = "Like.json";
                selectedText = "You liked this.";
                break;
            case 1:
                asset = "Love.json";
                selectedText = "You loved this.";
                break;
            case 2:
                asset = "Haha.json";
                selectedText = "You found it hilarious.";
                break;
            case 3:
                asset = "Wow.json";
                selectedText = "You are amazed.";
                break;
            case 4:
                asset = "Sorry.json";
                selectedText = "You felt sorry.";
                break;
            case 5:
                asset = "Anger.json";
                selectedText = "You got angry.";
                break;

            default:
                selectedText = "Select an Emotion.";
                break;
        }

        InputStream stream = null;
        try {
            stream = getResources().getAssets().open(asset);
            kfImage = KFImageDeserializer.deserialize(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        kfDrawable = new KeyframesDrawableBuilder().withImage(kfImage).build();
        ivSelectedEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ivSelectedEmoji.setImageDrawable(kfDrawable);
        ivSelectedEmoji.setImageAlpha(0);
        kfDrawable.startAnimation();

        tvSelected.setText(selectedText);
//        TODO : To play animation once only : Just un comment the code below :
//        kfDrawable.playOnce();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ivSelectedEmoji.setVisibility(View.GONE);
//            }
//        }, kfDrawable.getAnimationDuration());
    }

    public void setElasticity(ViewGroup view, float scaleXY, int duration, boolean wannaVibrate) {
        ElasticAction.doAction(view, duration, scaleXY, scaleXY);
        if (wannaVibrate) {
            vibrator.vibrate(50);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        kfDrawable.stopAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        kfDrawable.stopAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (kfDrawable != null) {
            kfDrawable.startAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivSelectedEmoji.setVisibility(View.GONE);
                }
            }, kfDrawable.getAnimationDuration());
        }
    }
}
