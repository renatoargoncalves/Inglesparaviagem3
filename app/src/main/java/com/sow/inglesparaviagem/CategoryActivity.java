package com.sow.inglesparaviagem;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sow.inglesparaviagem.adapters.PhraseAdapter;
import com.sow.inglesparaviagem.adapters.SimpleSectionedRecyclerViewAdapter;
import com.sow.inglesparaviagem.application.MyApplication;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.listeners.OnSpeechEventDetected;
import com.uxcam.UXCam;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.relativeLayout_category) RelativeLayout relativeLayout_category;
    @BindView(R.id.relativeLayout_speak) RelativeLayout relativeLayout_speak;
    @BindView(R.id.linearLayout_speak) LinearLayout linearLayout_speak;
    @BindView(R.id.textView_title) TextView textView_title;
    @BindView(R.id.textView_subtitle) TextView textView_subtitle;
    @BindView(R.id.button_speak) ImageButton button_speak;
    @BindView(R.id.button_stop) ImageButton button_stop;
    @BindView(R.id.textView_speak_port) TextView textView_port;
    @BindView(R.id.textView_speak_eng) TextView textView_eng;

    private CategoryProvider.Category category;
    private String TAG = "CategoryActivity";
    private int category_id;
    private ViewGroup.LayoutParams params;
    private MyApplication myApplication;
    private PhraseAdapter phraseAdapter;
    private SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
    private RecyclerView.LayoutManager layoutManager_phrases;
    private AdView adView;
    private AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        UXCam.startWithKey(getString(R.string.uxcamkey));

        myApplication = (MyApplication) getApplicationContext();

        category_id = getIntent().getIntExtra("category_id", 0);
        category = new CategoryProvider(this).getCategoryArrayList().get(category_id);

        params = relativeLayout_category.getLayoutParams();

        relativeLayout_speak.setVisibility(GONE);

        ImageView imageView_category = (ImageView) findViewById(R.id.imageView_category);
        imageView_category.setImageResource(category.image_id);

        textView_title = (TextView) findViewById(R.id.textView_title);
        textView_title.setText(category.getTitle());

        textView_subtitle.setText(category.getSubtitle());


        layoutManager_phrases = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView_phrases = (RecyclerView) findViewById(R.id.recyclerView_phrases);
        recyclerView_phrases.setLayoutManager(layoutManager_phrases);
        phraseAdapter = new PhraseAdapter(this, category.getIdentifier(), false);

        List<SimpleSectionedRecyclerViewAdapter.Section> sections = createSections(category.getIdentifier());

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(this, R.layout.section, R.id.section_text, phraseAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        recyclerView_phrases.setAdapter(mSectionedAdapter);
        phraseAdapter.setOnItemClickListener(new PhraseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showSpeakingLayout(position, null, null);
            }
        });

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


    }

    private void showSpeakingLayout(int position, String port, String eng) {

        if (position == -1) {
            textView_port.setText(port);
            textView_eng.setText(eng);
        } else {
            textView_port.setText("(" + phraseAdapter.getPhrasesArrayList().get(mSectionedAdapter.sectionedPositionToPosition(position)).getPort() + ")");
            textView_eng.setText(phraseAdapter.getPhrasesArrayList().get(mSectionedAdapter.sectionedPositionToPosition(position)).getEng());
        }
        textView_title.setText("Ouvir:");

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


        relativeLayout_speak.setVisibility(View.VISIBLE);
        speak(textView_eng.getText());

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

    private List<SimpleSectionedRecyclerViewAdapter.Section> createSections(String identifier) {

        ArrayList<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
        if (category.getIdentifier().equals("basics")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Comunicação básica:"));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(17, "Apresentações:"));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(23, "Ajuda:"));
        } else if (category.getIdentifier().equals("airport")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Locais:"));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14, "Embarque:"));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(42, "Alfândega:"));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(72, "Diversos:"));
        } else if (category.getIdentifier().equals("car")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Alugando um carro:"));
        } else if (category.getIdentifier().equals("directions")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Direções:"));
        } else if (category.getIdentifier().equals("hotel")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Recepção:"));
        } else if (category.getIdentifier().equals("shopping")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Compras:"));
        } else if (category.getIdentifier().equals("restaurant")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Restaurante:"));
        } else if (category.getIdentifier().equals("places")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Lugares:"));
        } else if (category.getIdentifier().equals("verbs")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Verbos:"));
        } else if (category.getIdentifier().equals("body")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Corpo:"));
        } else if (category.getIdentifier().equals("health")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Saúde:"));
        } else if (category.getIdentifier().equals("safety")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Segurança:"));
        } else if (category.getIdentifier().equals("kitchen")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Cozinha:"));
        } else if (category.getIdentifier().equals("bathroom")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Banheiro:"));
        } else if (category.getIdentifier().equals("food")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Comida:"));
        } else if (category.getIdentifier().equals("hobbies")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Hobbies:"));
        } else if (category.getIdentifier().equals("beach")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Praia:"));
        } else if (category.getIdentifier().equals("sports")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Esportes:"));
        } else if (category.getIdentifier().equals("music")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Música:"));
        } else if (category.getIdentifier().equals("animals")) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Animais:"));
        }

        return sections;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (relativeLayout_speak.getVisibility() == View.VISIBLE) {
            relativeLayout_speak.setVisibility(GONE);
            textView_title.setText(category.getTitle());
        } else {
            adView.setVisibility(View.GONE);
            super.onBackPressed();
        }
    }
}
