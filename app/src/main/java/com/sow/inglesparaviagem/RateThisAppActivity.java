package com.sow.inglesparaviagem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.plus.PlusOneButton;
import com.sow.inglesparaviagem.classes.Log;
import com.uxcam.UXCam;

public class RateThisAppActivity extends AppCompatActivity {

    private static final String TAG = "RateThisAppActivity";
    private Toolbar toolbar;
    private PlusOneButton mPlusOneButton;
    private static final int PLUS_ONE_REQUEST_CODE = 1515;
    private LinearLayout btnLoginToLike, linearLayout_google_play;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_app);
        UXCam.startWithKey(getString(R.string.uxcamkey));

        toolbar = (Toolbar) findViewById(R.id.toolbar_rate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.rate_this_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        linearLayout_google_play = (LinearLayout) findViewById(R.id.linearLayout_google_play);
        linearLayout_google_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String APP_MARKET_URL = "market://details?id=com.sow.inglesparaviagem";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_MARKET_URL));
                startActivity(intent);
                Toast.makeText(RateThisAppActivity.this, getString(R.string.please_rate_5_stars), Toast.LENGTH_SHORT).show();
            }
        });


        mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);
        initInstances();
        initCallbackManager();
        refreshButtonsState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
        mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.sow.inglesparaviagem", PLUS_ONE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        try {
            if (requestCode == PLUS_ONE_REQUEST_CODE) {
                if (resultCode == -1) {
                    Log.i(TAG, "User gave +1 on Google!");
                }
            }
//            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: " + e.getMessage());
        }
    }

    private void initInstances() {
//        btnLoginToLike = (LinearLayout) findViewById(R.id.btnLoginToLike);
//        likeView = (LikeView) findViewById(R.id.likeView);
//        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
//        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
//        likeView.setObjectIdAndType(
//                "https://www.facebook.com/locatorfamilyandfriends",
//                LikeView.ObjectType.PAGE);
//
//        btnLoginToLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginManager.getInstance().logInWithReadPermissions(RateThisAppActivity.this, Arrays.asList("public_profile"));
//            }
//        });
    }

    private void initCallbackManager() {
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                refreshButtonsState();
//                Log.i(TAG, "onSuccess()");
//            }
//
//            @Override
//            public void onCancel() {
//                Log.i(TAG, "onCancel()");
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                Log.i(TAG, "onError()");
//            }
//        });
    }

    private void refreshButtonsState() {
//        if (!isLoggedIn()) {
//            btnLoginToLike.setVisibility(View.VISIBLE);
//            likeView.setVisibility(View.GONE);
//        } else {
//            btnLoginToLike.setVisibility(View.GONE);
//            likeView.setVisibility(View.VISIBLE);
//        }
    }

//    public boolean isLoggedIn() {
//        return AccessToken.getCurrentAccessToken() != null;
//    }

}
