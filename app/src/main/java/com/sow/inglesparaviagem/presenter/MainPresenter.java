package com.sow.inglesparaviagem.presenter;

import android.content.Context;

/**
 * Created by renato.rezende on 26/05/2017.
 */

public interface MainPresenter {

    void loadCategories(Context context);

    void toogleSearchViewState(int visibility);

    void filterPhrases(Context context, String query);
}
