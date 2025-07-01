package com.atipls.chat.model;

import com.atipls.chat.State;

public interface HasIcon {
    public Long getIconID();

    public String getIconHash();

    public String getIconType();

    public void iconLoaded(State s);

    public void largeIconLoaded(State s);
}
