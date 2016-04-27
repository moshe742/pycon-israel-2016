package com.ls.pycon_israel.model.data;

import com.google.gson.annotations.SerializedName;

public class SettingsHolder {

    @SerializedName("settings")
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
