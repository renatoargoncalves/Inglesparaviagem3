package com.sow.inglesparaviagem.presenter;

import android.content.Context;

import com.sow.inglesparaviagem.model.CategoryModel;
import com.sow.inglesparaviagem.model.CategoryModelImpl;
import com.sow.inglesparaviagem.view.CategoryView;

/**
 * Created by renato.rezende on 26/05/2017.
 */

public class CategoryPresenterImpl implements CategoryPresenter {

    private CategoryView categoryView;
    private CategoryModel categoryModel;

    public CategoryPresenterImpl(CategoryView categoryView) {
        this.categoryView = categoryView;
        this.categoryModel = new CategoryModelImpl();
    }

    @Override
    public void loadCategories(Context context) {
        categoryView.showProgress(true);
        categoryModel.loadCategories(context);
    }
}
