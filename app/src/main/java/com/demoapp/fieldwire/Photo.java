package com.demoapp.fieldwire;

import com.demoapp.fieldwire.Model.Tag;
import java.util.List;

class Photo {
    String id;
    String title;
    String url;
    List<Tag> tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Tag> getTag() {
        return tag;
    }

    public void setTag( List<Tag> tag) {
        this.tag = tag;
    }
}