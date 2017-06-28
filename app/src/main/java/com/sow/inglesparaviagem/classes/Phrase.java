package com.sow.inglesparaviagem.classes;

/**
 * Created by renato.rezende on 26/06/2017.
 */

public class Phrase {

    private String category;
    private String port;
    private String eng;

    public Phrase() {
    }

    public Phrase(String port, String eng) {
        this.port = port;
        this.eng = eng;
    }

    public Phrase(String port, String eng, String category) {
        this.port = port;
        this.eng = eng;
        this.category = category;
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
