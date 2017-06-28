package com.sow.inglesparaviagem.classes;

/**
 * Created by renato.rezende on 26/06/2017.
 */

public class Category {

    String category;
    int image;
    String title;

    public Category() {
    }

    public Category(int image, String title) {
        this.image = image;
        this.title = title;
    }

    public Category(int image, String title, String category) {
        this.image = image;
        this.title = title;
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
