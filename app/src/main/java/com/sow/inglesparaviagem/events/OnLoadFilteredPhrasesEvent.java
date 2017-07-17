package com.sow.inglesparaviagem.events;

import com.sow.inglesparaviagem.classes.Phrase;

import java.util.ArrayList;

/**
 * Created by renato.rezende on 15/07/2017.
 */

public class OnLoadFilteredPhrasesEvent {
    ArrayList<Phrase> filteredPhrases = new ArrayList<>();

    public OnLoadFilteredPhrasesEvent(ArrayList<Phrase> mFilteredPhrases) {
        filteredPhrases = mFilteredPhrases;
    }

    public ArrayList<Phrase> getFilteredPhrases() {
        return filteredPhrases;
    }

    public void setFilteredPhrases(ArrayList<Phrase> filteredPhrases) {
        this.filteredPhrases = filteredPhrases;
    }
}
