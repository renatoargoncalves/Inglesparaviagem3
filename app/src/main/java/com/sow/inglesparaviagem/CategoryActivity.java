package com.sow.inglesparaviagem;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.sow.inglesparaviagem.classes.Phrase;
import com.sow.inglesparaviagem.events.OnLoadPhrasesEvent;
import com.sow.inglesparaviagem.events.OnStartSpeaking;
import com.sow.inglesparaviagem.events.OnStopSpeaking;
import com.sow.inglesparaviagem.fragments.AudioSettingsFragment;
import com.sow.inglesparaviagem.presenter.CategoryPresenterImpl;
import com.sow.inglesparaviagem.view.CategoryView;
import com.uxcam.UXCam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity implements CategoryView {

    @BindView(R.id.relativeLayout_category)    RelativeLayout relativeLayout_category;
    @BindView(R.id.textView_title) TextView textView_title;
    @BindView(R.id.imageView_category) ImageView imageView_category;
    @BindView(R.id.recyclerView_phrases) RecyclerView recyclerView_phrases;
    @BindView(R.id.toolbar_category) Toolbar toolbar;
    @BindView(R.id.adView) AdView adView;
    @BindView(R.id.llFragmentContainer) LinearLayout llFragmentContainer;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;


    private String TAG = "CategoryActivity";
    private int category_id;
    private ViewGroup.LayoutParams params;
    private MyApplication myApplication;
    private PhraseAdapter phraseAdapter;
    private SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
    private RecyclerView.LayoutManager layoutManager_phrases;
    private AdRequest adRequest;
    private CategoryPresenterImpl mCategoryPresenter;
    private ArrayList<Phrase> mPhrases;
    private Animation slide_down;
    private Animation slide_up;
    private RelativeLayout llSpeaker;
    private RelativeLayout llSpeakerCollapsed;
    private ImageView imageViewSpeaker;
    private AudioSettingsFragment audioSettingsFragment;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "CategoryActivity");
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        UXCam.startWithKey(getString(R.string.uxcamkey));

        myApplication = (MyApplication) getApplication();

        setupToolbar();

        setupAds();

        llSpeaker = (RelativeLayout) findViewById(R.id.llSpeaker);

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//        slide_down.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                imageView_settings.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                imageView_settings.setImageDrawable(getDrawable(R.drawable.ic_action_show));
//                imageView_settings.setVisibility(View.VISIBLE);
//                rlSettingsPanel.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//        slide_up.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                imageView_settings.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                imageView_settings.setImageDrawable(getDrawable(R.drawable.ic_action_arrow_hide));
//                imageView_settings.setVisibility(View.VISIBLE);
//                rlSettingsPanel.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        String title = getIntent().getStringExtra("title");
        int image = getIntent().getIntExtra("image", 0);
        String category = getIntent().getStringExtra("category");

        textView_title.setText(title);
        imageView_category.setImageDrawable(getDrawable(image));

        mCategoryPresenter = new CategoryPresenterImpl(this);
        mCategoryPresenter.loadPhrases(this, category);

        layoutManager_phrases = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_phrases.setLayoutManager(layoutManager_phrases);
        phraseAdapter = new PhraseAdapter(this, null, false);

        audioSettingsFragment = new AudioSettingsFragment();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("main");
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        mFragmentTransaction.add(R.id.llFragmentContainer, audioSettingsFragment, "main").commit();
        android.util.Log.i(TAG, "onCreate: "+ audioSettingsFragment.getTag());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("createFragment", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    PhraseAdapter.OnItemClickListener onItemClickListener = new PhraseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            float maxVolume = Float.valueOf(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            float currentVolume = Float.valueOf(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
            if(currentVolume <= maxVolume*0.05)
                Snackbar.make(coordinatorLayout, "Aumente o volume para ouvir a pronúncia!", 2500).show();

            audioSettingsFragment.getTextViewPhraseEng().setText(mPhrases.get(position).getEng());
            myApplication.getTts().speak(mPhrases.get(position).getEng(), TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
        }
    };

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adView.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(boolean showProgress) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    /**
     * @param onLoadPhrasesEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadPhrasesEvent(OnLoadPhrasesEvent onLoadPhrasesEvent) {
        Log.w(TAG, "CategoryActivity.onLoadPhrasesEvent()");
        mPhrases = onLoadPhrasesEvent.getPhrases();
        phraseAdapter = new PhraseAdapter(this, mPhrases, false);
        recyclerView_phrases.setAdapter(phraseAdapter);
        phraseAdapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * @param onStartSpeaking
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartSpeaking(OnStartSpeaking onStartSpeaking) {
        Log.w(TAG, "CategoryActivity.onStartSpeaking()");
        audioSettingsFragment.getImageView_icon_speaker().setImageDrawable(getDrawable(R.drawable.ic_action_stop_speaker));
    }

    /**
     * @param onStopSpeaking
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopSpeaking(OnStopSpeaking onStopSpeaking) {
        Log.w(TAG, "CategoryActivity.onStopSpeaking()");
        audioSettingsFragment.getImageView_icon_speaker().setImageDrawable(getDrawable(R.drawable.ic_action_speaker));
    }

    private void setupAds() {
        try {
            Log.i(TAG, "adView - preparing");

            adView.setVisibility(View.GONE);
            adRequest = new AdRequest.Builder()
                    .addTestDevice("E74C03E550CA044A0E2F5F27B86BAA1B")
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


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
