package com.sow.inglesparaviagem;

import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.sow.inglesparaviagem.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sow.inglesparaviagem.application.MyApplication;
import com.sow.inglesparaviagem.listeners.OnSpeechEventDetected;

import static com.sow.inglesparaviagem.R.id.adView;
import static com.sow.inglesparaviagem.R.id.button_speak;
import static com.sow.inglesparaviagem.R.id.button_stop;

public class SpeakActvity extends AppCompatActivity {

    private String TAG = "ActivitySpeak";
    private ImageButton button_speak;
    private ImageButton button_stop;
    private MyApplication myApplication;
    private TextView textView_port;
    private TextView textView_eng;
    private AdView adView;
    private AdRequest adRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);

        myApplication = (MyApplication) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_speak);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pesquisa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        textView_port = (TextView) findViewById(R.id.textView_speak_port);
        textView_eng = (TextView) findViewById(R.id.textView_speak_eng);
        button_speak = (ImageButton) findViewById(R.id.button_speak);
        button_stop = (ImageButton) findViewById(R.id.button_stop);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            textView_port.setText(bundle.getString("port"));
            textView_eng.setText(bundle.getString("eng"));
        }

        try {
            Log.i(TAG, "adView - preparing");

            adView = (AdView) findViewById(R.id.adView);

            adView.setVisibility(View.GONE);
            adRequest = new AdRequest.Builder()
//                    .addTestDevice("C6E27E792E9C776653A67DDF90F3CB03")
                    .build();

            adView.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    Log.i(TAG, "AdLoaded");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adView.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }

                public void onAdFailedToLoad(int errorcode) {
                    Log.i(TAG, "AdFailedToLoad: " + errorcode);
                    adView.setVisibility(View.GONE);
                }

                public void onAdOpened() {
                    Log.i(TAG, "AdOpened");
                }

                public void onAdClosed() {
                    Log.i(TAG, "AdClosed");
                    adView.setVisibility(View.GONE);
                }

                public void onAdLeftApplication() {
                    Log.i(TAG, "AdLeftApplication");
                    adView.setVisibility(View.GONE);
                }
            });
            adView.loadAd(adRequest);
        } catch (Exception e) {
            Log.e(TAG, "Exception - adView: " + e.getLocalizedMessage());
        } finally {
            adView.setVisibility(View.GONE);
            Log.i(TAG, "adView - finished");
        }

        myApplication.getSpeechActivityDetected().setOnEventListener(new OnSpeechEventDetected() {
            @Override
            public void onEvent(String event) {
                if (event.equals("startSpeech")) {
                    setupButtonsForSpeech();
                } else if (event.equals("stopSpeech")) {
                    setupButtonsForSilence();
                } else {

                }
            }
        });

        button_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(textView_eng.getText());
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.getTts().isSpeaking())
                    myApplication.getTts().stop();
            }
        });

        speak(textView_eng.getText().toString());
    }

    private void setupButtonsForSpeech() {
        Log.i(TAG, "setupButtonsForSpeech");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button_speak.setVisibility(View.GONE);
                button_stop.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupButtonsForSilence() {
        Log.i(TAG, "setupButtonsForStop");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button_speak.setVisibility(View.VISIBLE);
                button_stop.setVisibility(View.GONE);
            }
        });
    }

    private void speak(final CharSequence text) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    myApplication.getTts().speak(text, TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
                } else {
                    myApplication.getTts().speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }, 500);
    }

}



