package com.sow.inglesparaviagem.events;

import com.sow.inglesparaviagem.classes.Phrase;

import java.util.ArrayList;

/**
 * Created by renato.rezende on 27/06/2017.
 */

public class OnLoadPhrasesEvent {

    private ArrayList<Phrase> phrases;

    public OnLoadPhrasesEvent(ArrayList<Phrase> mPhrases) {
        phrases = mPhrases;
    }


    public ArrayList<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(ArrayList<Phrase> phrases) {
        this.phrases = phrases;
    }
}
