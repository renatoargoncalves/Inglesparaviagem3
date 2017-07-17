package com.sow.inglesparaviagem.model;

import android.content.Context;

/**
 * Created by renato.rezende on 26/05/2017.
 */

public interface MainModel {

    void loadCategories(Context context);

    void toogleSearchViewState(int visibility);

    void filterPhrases(Context context, String query);
}
