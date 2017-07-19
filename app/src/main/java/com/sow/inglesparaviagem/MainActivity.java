package com.sow.inglesparaviagem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sow.inglesparaviagem.adapters.CategoryAdapter;
import com.sow.inglesparaviagem.adapters.PhraseAdapter;
import com.sow.inglesparaviagem.application.MyApplication;
import com.sow.inglesparaviagem.classes.Category;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.classes.Phrase;
import com.sow.inglesparaviagem.classes.Utils;
import com.sow.inglesparaviagem.events.OnLoadCategoriesEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesFailEvent;
import com.sow.inglesparaviagem.events.OnLoadFilteredPhrasesEvent;
import com.sow.inglesparaviagem.events.OnStartSpeaking;
import com.sow.inglesparaviagem.events.OnStopSpeaking;
import com.sow.inglesparaviagem.events.OnToogleSearchViewToGone;
import com.sow.inglesparaviagem.events.OnToogleSearchViewToVisible;
import com.sow.inglesparaviagem.fragments.AudioSettingsFragment;
import com.sow.inglesparaviagem.presenter.MainPresenter;
import com.sow.inglesparaviagem.presenter.MainPresenterImpl;
import com.sow.inglesparaviagem.view.MainView;
import com.uxcam.UXCam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements MainView,
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.content_main) RelativeLayout content_main;
    @BindView(R.id.recyclerView_categories) RecyclerView recyclerView_categories;
    @BindView(R.id.linearLayout_main_adView) LinearLayout linearLayout_main_adView;
    @BindView(R.id.adView) AdView adView;
    @BindView(R.id.relativeLayout_search) RelativeLayout relativeLayout_search;
    @BindView(R.id.recyclerView_search) RecyclerView recyclerView_search;
//    @BindView(R.id.plus_one_button) PlusOneButton mPlusOneButton;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

    //    private static final int PLUS_ONE_REQUEST_CODE = 1515;
    private String TAG = "MainActivity";
    private RecyclerView.LayoutManager layoutManager_search;
    private PhraseAdapter phraseAdapter;
    private MyApplication myApplication;
    private SearchView searchView;
    private AdRequest adRequest;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManagerSearch;
    private AudioSettingsFragment audioSettingsFragment;
    private MainPresenter mMainPresenter;
    private ArrayList<Category> mCategories;
    private ArrayList<Phrase> mPhrases;
    private AudioManager audio;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "MainActivity.onCreate()");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        UXCam.startWithKey(getString(R.string.uxcamkey));

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        myApplication = (MyApplication) getApplication();

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        setupToolbar();

        content_main.setBackground(getResources().getDrawable(R.color.lightGray, null));

        setupNavigationView();

        setupAds();

        mMainPresenter = new MainPresenterImpl(this);
        mMainPresenter.loadCategories(this);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("main");
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        audioSettingsFragment = new AudioSettingsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.llFragmentContainer, audioSettingsFragment, "main").commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("createFragment", true);
        super.onSaveInstanceState(outState, outPersistentState);
    }
    private void setupRecyclerView(CategoryAdapter mAdapter) {
        Log.w(TAG, "MainActivity.setupRecyclerView()");
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_categories.setLayoutManager(layoutManager);
        recyclerView_categories.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);
    }


    private void setupNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

//    private void setupPlusOneButton() {
//        boolean liked = sharedPref.getBoolean("liked", false);
//        if(liked) {
//            mPlusOneButton.setVisibility(GONE);
//        } else {
//            mPlusOneButton.setVisibility(VISIBLE);
//        }
//    }

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

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    CategoryAdapter.OnItemClickListener onItemClickListener = new CategoryAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent transitionIntent = new Intent(MainActivity.this, CategoryActivity.class);

            transitionIntent.putExtra("category", mCategories.get(position).getCategory());

            RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout_category);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView_category);
            transitionIntent.putExtra("image", mCategories.get(position).getImage());
            TextView textViewTitle = (TextView) v.findViewById(R.id.textView_title);
            transitionIntent.putExtra("title", mCategories.get(position).getTitle());

            Pair<View, String> imagePair = Pair.create((View) imageView, "tImageView");
            Pair<View, String> textViewTitlePair = Pair.create((View) textViewTitle, "tTextView");
            Pair<View, String> layoutPair = Pair.create((View) relativeLayout, "tRelativeLayout");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imagePair, textViewTitlePair, layoutPair);
            ActivityCompat.startActivity(MainActivity.this, transitionIntent, options.toBundle());

            Bundle params = new Bundle();
            params.putString("category", mCategories.get(position).getCategory());
            mFirebaseAnalytics.logEvent("categoryEvent", params);
        }
    };

    @Override
    public void onBackPressed() {
        Log.w(TAG, "onBackPressed()");

        Log.i(TAG, "isDrawerOpen: " + drawer.isDrawerOpen(GravityCompat.START));
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.i(TAG, "isDrawerOpen");
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Log.i(TAG, "isIconified: " + searchView.isIconified());
        if (!searchView.isIconified()) {
            Log.i(TAG, "!isIconified");
            searchView.onActionViewCollapsed();
            relativeLayout_search.setVisibility(GONE);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.item_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPresenter.toogleSearchViewState(relativeLayout_search.getVisibility());
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mMainPresenter.toogleSearchViewState(relativeLayout_search.getVisibility());
                return false;
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1 || newText.length() == 0) {
                    mMainPresenter.filterPhrases(MainActivity.this, newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_pronunciation) {
            startActivity(new Intent(this, PronunciationActivity.class));
        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(this, RateThisAppActivity.class));
        } else if (id == R.id.menu_gpstracker) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.sow.gpstrackerpro")));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLUS_ONE_REQUEST_CODE) {
//            if (resultCode == -1) {
//                Toast.makeText(this, "Obrigado!", Toast.LENGTH_SHORT).show();
//                Handler h = new Handler();
//                h.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putBoolean("liked", true);
//                        editor.commit();
//                        mPlusOneButton.setVisibility(GONE);
//                    }
//                }, 3000);
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.sow.inglesparaviagem", PLUS_ONE_REQUEST_CODE);
    }

    @Override
    public void showProgress(boolean showProgress) {
        // TODO implement progress bar
    }

    /**
     * @param onLoadCategoriesEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadCategoriesEvent(OnLoadCategoriesEvent onLoadCategoriesEvent) {
        Log.w(TAG, "MainActivity.onLoadCategoriesEvent()");
        showProgress(false);
        mCategories = onLoadCategoriesEvent.getCategories();
        setupRecyclerView(new CategoryAdapter(this, mCategories));
    }

    /**
     * @param onLoadCategoriesFailEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadCategoriesFailEvent(OnLoadCategoriesFailEvent onLoadCategoriesFailEvent) {
        Log.w(TAG, "MainActivity.onLoadCategoriesFailEvent()");
        showProgress(false);
        Snackbar.make(coordinatorLayout, "Erro ao carregar categorias.", 2500).show();
    }

    /**
     * @param onToogleSearchViewToVisible
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToogleSearchViewToVisible(OnToogleSearchViewToVisible onToogleSearchViewToVisible) {
        Log.w(TAG, "MainActivity.onToogleSearchViewToVisible()");
        showProgress(false);
        relativeLayout_search.setVisibility(VISIBLE);
        adView.setEnabled(false);
        adView.setVisibility(GONE);
        mMainPresenter.filterPhrases(this, "");
    }

    /**
     * @param onToogleSearchViewToGone
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToogleSearchViewToGone(OnToogleSearchViewToGone onToogleSearchViewToGone) {
        Log.w(TAG, "MainActivity.onToogleSearchViewToGone()");
        showProgress(false);
        relativeLayout_search.setVisibility(GONE);
        adView.setEnabled(true);
        adView.setVisibility(VISIBLE);
    }

    /**
     * @param OnLoadFilteredPhrasesEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnLoadFilteredPhrasesEvent(OnLoadFilteredPhrasesEvent OnLoadFilteredPhrasesEvent) {
        Log.w(TAG, "MainActivity.OnLoadFilteredPhrasesEvent()");
        showProgress(false);
        mPhrases = OnLoadFilteredPhrasesEvent.getFilteredPhrases();
        PhraseAdapter filteredAdapter = new PhraseAdapter(this, mPhrases, true);
        layoutManagerSearch = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_search.setLayoutManager(layoutManagerSearch);
        recyclerView_search.setAdapter(filteredAdapter);
        filteredAdapter.setOnItemClickListener(onPhraseItemClickListener);
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

    PhraseAdapter.OnItemClickListener onPhraseItemClickListener = new PhraseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Utils.hideSoftKeyboard(MainActivity.this, v);
            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            float maxVolume = Float.valueOf(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            float currentVolume = Float.valueOf(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
            if(currentVolume <= maxVolume*0.05)
                Snackbar.make(coordinatorLayout, "Aumente o volume para ouvir a pronúncia!", 2500).show();


            audioSettingsFragment.getTextViewPhraseEng().setText(mPhrases.get(position).getEng());
            myApplication.getTts().speak(mPhrases.get(position).getEng(), TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            default:
                return false;
        }
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

}
