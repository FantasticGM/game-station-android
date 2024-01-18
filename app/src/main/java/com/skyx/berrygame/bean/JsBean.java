package com.skyx.berrygame.bean;

import java.io.Serializable;

public class JsBean {

    private String action;
    private String type;
    private String url;
    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

}
