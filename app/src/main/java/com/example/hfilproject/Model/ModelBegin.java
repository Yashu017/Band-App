package com.example.hfilproject.Model;

public class ModelBegin {

    private int ImgStart;
    private String Title,descr;

    public ModelBegin(int imgStart, String title, String descr) {
        ImgStart = imgStart;
        Title = title;
        this.descr = descr;
    }

    public int getImgStart() {
        return ImgStart;
    }

    public void setImgStart(int imgStart) {
        ImgStart = imgStart;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
