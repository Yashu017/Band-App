package com.example.hfilproject;

public class BandDetail {
    String description;
    private int imageResource;

    BandDetail(String description,int imageResource){
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
