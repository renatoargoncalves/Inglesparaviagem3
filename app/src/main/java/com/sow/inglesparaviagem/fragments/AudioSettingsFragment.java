package com.sow.inglesparaviagem.fragments;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.application.MyApplication;
import com.sow.inglesparaviagem.classes.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioSettingsFragment extends Fragment {

    @BindView(R.id.imageView_settings) ImageView imageView_settings;
    @BindView(R.id.rlPhrase) RelativeLayout rlPhrase;
    @BindView(R.id.rlSettingsPanel) RelativeLayout rlSettingsPanel;
    @BindView(R.id.textViewPhraseEng) TextView textViewPhraseEng;
    @BindView(R.id.imageView_icon_speaker) ImageView imageView_icon_speaker;
    @BindView(R.id.seekBarSpeechSpeed) SeekBar seekBarSpeechSpeed;

    private static final String TAG = "AudioSettingsFragment";
    private MyApplication myApplication;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audio_settings_view, container, false);
        ButterKnife.bind(this, rootView);
        myApplication = (MyApplication) getActivity().getApplication();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(myApplication);

        return rootView;
    }


    @OnClick(R.id.rlPhrase)
    public void rlPhraseClicked() {
        try {
            myApplication.getTts().speak(textViewPhraseEng.getText(), TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
            Bundle params = new Bundle();
            params.putString("phrase", textViewPhraseEng.getText().toString());
            mFirebaseAnalytics.logEvent("playPhraseEvent", params);

        } catch (Exception e) {
            Log.e(TAG, "AudioSettingsFragment.rlPhraseClicked(): " + e.getMessage());
        }
    }

    @OnClick(R.id.imageView_icon_speaker)
     public void speakLastPhrase() {
        if(myApplication.getTts().isSpeaking()) {
            myApplication.getTts().stop();
            imageView_icon_speaker.setImageDrawable(getActivity().getDrawable(R.drawable.ic_action_speaker));
        }
        else
            myApplication.getTts().speak(textViewPhraseEng.getText(), TextToSpeech.QUEUE_FLUSH, null, "inglesparaviagem");
    }

    @OnClick(R.id.imageView_settings)
    public void animateLayoutSettings() {
        Bundle params = new Bundle();
        params.putString("settings", "settings");
        mFirebaseAnalytics.logEvent("settingsEvent", params);

        if(rlSettingsPanel.getVisibility() == View.VISIBLE) {
            rlSettingsPanel.setVisibility(View.GONE);
            imageView_settings.setImageDrawable(getActivity().getDrawable(R.drawable.ic_action_show));
        } else {
            rlSettingsPanel.setVisibility(View.VISIBLE);
            imageView_settings.setImageDrawable(getActivity().getDrawable(R.drawable.ic_action_arrow_hide));

            seekBarSpeechSpeed.setProgress((int) (myApplication.getSharedPreferences().getFloat("speechRate", 1)*10) );
            seekBarSpeechSpeed.setMax(20);
            seekBarSpeechSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    myApplication.getTts().stop();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    myApplication.getTts().setSpeechRate(Float.valueOf(seekBar.getProgress())/10);
                    myApplication.getSharedPreferences().edit().putFloat("speechRate", Float.valueOf(seekBar.getProgress())/10).apply();
                    Log.i(TAG, "speechRate: " + myApplication.getSharedPreferences().getFloat("speechRate", 1));
                }
            });


        }

    }



    public TextView getTextViewPhraseEng() {
        return textViewPhraseEng;
    }

    public void setTextViewPhraseEng(TextView textViewPhraseEng) {
        this.textViewPhraseEng = textViewPhraseEng;
    }

    public ImageView getImageView_settings() {
        return imageView_settings;
    }

    public void setImageView_settings(ImageView imageView_settings) {
        this.imageView_settings = imageView_settings;
    }

    public ImageView getImageView_icon_speaker() {
        return imageView_icon_speaker;
    }

    public void setImageView_icon_speaker(ImageView imageView_icon_speaker) {
        this.imageView_icon_speaker = imageView_icon_speaker;
    }

    public SeekBar getSeekBarSpeechSpeed() {
        return seekBarSpeechSpeed;
    }

    public void setSeekBarSpeechSpeed(SeekBar seekBarSpeechSpeed) {
        this.seekBarSpeechSpeed = seekBarSpeechSpeed;
    }

}
