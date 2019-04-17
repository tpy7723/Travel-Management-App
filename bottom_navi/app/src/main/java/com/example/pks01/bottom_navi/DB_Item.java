package com.example.pks01.bottom_navi;

import java.io.Serializable;

public class DB_Item implements Serializable {
    private Integer idStr=0;
    private String titleStr="";
    private String snippetStr="";
    private String ImageStr="";
    private String CategoryStr="";
    private double longitudeStr=0;
    private double latitudeStr=0;

    public void setid(Integer id) {
        idStr = id;
    }
    public void setTitle(String title) {
        titleStr = title;
    }
    public void setSnippet(String snippet) {
        snippetStr = snippet;
    }
    public void setLongitude(double longitude) {
        longitudeStr = longitude;
    }
    public void setLatitude(double latitude) { latitudeStr = latitude; }
    public void setImageStr(String image) { ImageStr = image; }
    public void setCategory(String category) { CategoryStr = category; }
    public String getTitle() {
        return this.titleStr;
    }
    public String getSnippet() {
        return this.snippetStr;
    }
    public double getLongitude() {
        return this.longitudeStr;
    }
    public double getLatitude() {
        return this.latitudeStr;
    }
    public String getImage() { return this.ImageStr;}
    public String getCategory() { return this.CategoryStr;}
    public Integer getID() { return this.idStr;}
}

