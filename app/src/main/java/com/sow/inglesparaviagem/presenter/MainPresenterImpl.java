package com.sow.inglesparaviagem.presenter;

import android.content.Context;

import com.sow.inglesparaviagem.model.MainModel;
import com.sow.inglesparaviagem.model.MainModelImpl;
import com.sow.inglesparaviagem.view.MainView;

/**
 * Created by renato.rezende on 26/05/2017.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;
    private MainModel mainModel;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        this.mainModel = new MainModelImpl();
    }

    @Override
    public void loadCategories(Context context) {
        mainView.showProgress(true);
        mainModel.loadCategories(context);
    }

    @Override
    public void toogleSearchViewState(int visibility) {
        mainView.showProgress(true);
        mainModel.toogleSearchViewState(visibility);
    }

    @Override
    public void filterPhrases(Context context, String query) {
        mainView.showProgress(true);
        mainModel.filterPhrases(context, query);
    }
}
