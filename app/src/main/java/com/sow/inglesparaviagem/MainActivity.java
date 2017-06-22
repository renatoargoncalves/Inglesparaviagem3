package com.sow.inglesparaviagem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.plus.PlusOneButton;
import com.sow.inglesparaviagem.adapters.CategoryAdapter;
import com.sow.inglesparaviagem.adapters.PhraseAdapter;
import com.sow.inglesparaviagem.application.MyApplication;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.listeners.OnSpeechEventDetected;
import com.sow.inglesparaviagem.presenter.MainPresenter;
import com.sow.inglesparaviagem.presenter.MainPresenterImpl;
import com.sow.inglesparaviagem.view.MainView;
import com.uxcam.UXCam;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.linearLayout_search) LinearLayout linearLayout_search;
    @BindView(R.id.linearLayout_main_adView) LinearLayout linearLayout_main_adView;
    @BindView(R.id.recyclerView_search) RecyclerView recyclerView_search;
    @BindView(R.id.relativeLayout_main_speak) RelativeLayout relativeLayout_main_speak;
    @BindView(R.id.textView_main_speak_port) TextView textView_main_speak_port;
    @BindView(R.id.textView_main_speak_eng) TextView textView_main_speak_eng;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.content_main) RelativeLayout content_main;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.recyclerView_categories) RecyclerView recyclerView_categories;
    @BindView(R.id.plus_one_button) PlusOneButton mPlusOneButton;
    @BindView(R.id.adView) AdView adView;

    private static final int PLUS_ONE_REQUEST_CODE = 1515;
    private String TAG = "MainActivity";
    private RecyclerView.LayoutManager layoutManager_search;
    private PhraseAdapter phraseAdapter;
    private MyApplication myApplication;
    private SearchView searchView;
    private AdRequest adRequest;
    private SharedPreferences sharedPref;
    private RecyclerView.LayoutManager layoutManager;

    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        UXCam.startWithKey(getString(R.string.uxcamkey));
        myApplication = (MyApplication) getApplicationContext();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        setupToolbar();

        content_main.setBackground(getResources().getDrawable(R.color.lightGray, null));

        setupNavigationView();

        setupRecyclerView();

        layoutManager_search = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_search.setLayoutManager(layoutManager_search);
        linearLayout_search.setVisibility(GONE);
        relativeLayout_main_speak.setVisibility(GONE);

        myApplication.getSpeechActivityDetected().setOnEventListener(new OnSpeechEventDetected() {
            @Override
            public void onEvent(String event) {
                if (event.equals("startSpeech")) {
//                    setupButtonsForSpeech();
                } else if (event.equals("stopSpeech")) {
                    setupButtonsForSilence();
                } else {

                }
            }
        });

        setupPlusOneButton();
        setupAds();

        mMainPresenter = new MainPresenterImpl(this);
        mMainPresenter.loadCategories(this);
    }

    private void setupRecyclerView() {
        layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView_categories.setLayoutManager(layoutManager);
        recyclerView_categories.setAdapter(null);
//        CategoryAdapter mAdapter = new CategoryAdapter(this);
//        recyclerView_categories.setAdapter(mAdapter);
//
//        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    private void setupNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupPlusOneButton() {
        boolean liked = sharedPref.getBoolean("liked", false);
        if(liked) {
            mPlusOneButton.setVisibility(GONE);
        } else {
            mPlusOneButton.setVisibility(VISIBLE);
        }
    }

    private void setupAds() {
        try {
            Log.i(TAG, "adView - preparing");

            adView.setVisibility(View.GONE);
            adRequest = new AdRequest.Builder()
                    .addTestDevice("C6E27E792E9C776653A67DDF90F3CB03")
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
            transitionIntent.putExtra("category_id", position);

            ImageView imageView = (ImageView) v.findViewById(R.id.imageView_category);
            LinearLayout linearLayout_title = (LinearLayout) v.findViewById(R.id.linearLayout_title);
            TextView textViewTitle = (TextView) v.findViewById(R.id.textView_title);

            Pair<View, String> imagePair = Pair.create((View) imageView, "tImageView");
            Pair<View, String> titlePair = Pair.create((View) linearLayout_title, "tLinearLayout_title");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imagePair, titlePair);
            ActivityCompat.startActivity(MainActivity.this, transitionIntent, options.toBundle());
        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.item_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    phraseAdapter = new PhraseAdapter(MainActivity.this, newText.toLowerCase(), true);
                    recyclerView_search.setAdapter(phraseAdapter);
                    phraseAdapter.setOnItemClickListener(new PhraseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, final int position) {
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
////                                    linearLayout_main_adView.setVisibility(GONE);
//                                    textView_main_speak_port.setText(phraseAdapter.getPhrasesArrayList().get(position).getPort());
//                                    textView_main_speak_eng.setText(phraseAdapter.getPhrasesArrayList().get(position).getEng());
//                                    relativeLayout_main_speak.setVisibility(VISIBLE);
//                                    speak(phraseAdapter.getPhrasesArrayList().get(position).getEng());
//                                }
//                            }, 200);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                            Intent intent = new Intent(MainActivity.this, SpeakActvity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("port", phraseAdapter.getPhrasesArrayList().get(position).getPort());
                            bundle.putString("eng", phraseAdapter.getPhrasesArrayList().get(position).getEng());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    linearLayout_search.setVisibility(VISIBLE);
                } else {
                    if (linearLayout_search.getVisibility() == VISIBLE)
                        linearLayout_search.setVisibility(GONE);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupButtonsForSilence() {
        Log.i(TAG, "setupButtonsForStop");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                        InputMethodManager.HIDE_IMPLICIT_ONLY);

                        relativeLayout_main_speak.setVisibility(View.GONE);
//                        linearLayout_main_adView.setVisibility(VISIBLE);
                    }
                }, 800);
            }
        });

    }

    private void speak(final CharSequence text) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                myApplication.getTts().speak(text, TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
            }
        }, 500);
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

//        if (id == R.id.nav_online_dictionary) {
//            startActivity(new Intent(this, DictionaryActivity.class));
//        } else

        if (id == R.id.nav_pronunciation) {
            startActivity(new Intent(this, PronunciationActivity.class));
        } else if (id == R.id.nav_rate) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.sow.inglesparaviagem")));
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
        if (requestCode == PLUS_ONE_REQUEST_CODE) {
            if (resultCode == -1) {
                Toast.makeText(this, "Obrigado!", Toast.LENGTH_SHORT).show();
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("liked", true);
                        editor.commit();
                        mPlusOneButton.setVisibility(GONE);
                    }
                }, 3000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.sow.inglesparaviagem", PLUS_ONE_REQUEST_CODE);
    }

    @Override
    public void showProgress(boolean showProgress) {
        // TODO implement progress bar
    }
}
