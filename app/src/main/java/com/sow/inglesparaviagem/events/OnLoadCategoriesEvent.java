package com.sow.inglesparaviagem.events;

import com.sow.inglesparaviagem.classes.Category;

import java.util.ArrayList;

/**
 * Created by renato.rezende on 20/06/2017.
 */

public class OnLoadCategoriesEvent {
    private ArrayList<Category> categories;

    public OnLoadCategoriesEvent(ArrayList<Category> mCategories) {
        categories = mCategories;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
