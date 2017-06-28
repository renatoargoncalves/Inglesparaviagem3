package com.sow.inglesparaviagem;

import android.content.Context;

import com.sow.inglesparaviagem.classes.Category;

import java.util.ArrayList;

public class CategoryProvider {

    private Context mContext;
    private ArrayList<Category> mCategories = new ArrayList<>();

    public CategoryProvider(Context mContext, ArrayList<Category> categories) {
        mContext = mContext;
        mCategories = categories;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<Category> getmCategories() {
        return mCategories;
    }

    public void setmCategories(ArrayList<Category> mCategories) {
        this.mCategories = mCategories;
    }

}
