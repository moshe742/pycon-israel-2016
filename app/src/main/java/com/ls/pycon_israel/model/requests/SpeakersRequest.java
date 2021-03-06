package com.ls.pycon_israel.model.requests;

import com.ls.drupal.DrupalClient;
import com.ls.pycon_israel.model.data.Speaker;
import com.ls.http.base.BaseRequest;

import java.util.Map;

public class SpeakersRequest extends BaseSafeConsumeContainerRequest<Speaker.Holder> {

    public SpeakersRequest(DrupalClient client) {
        super(client, new Speaker.Holder());
    }

    @Override
    protected String getPath() {
        return "getSpeakers.js";
    }

    @Override
    protected Map<String, String> getItemRequestPostParameters() {
        return null;
    }

    @Override
    protected Map<String, Object> getItemRequestGetParameters(BaseRequest.RequestMethod method) {
        return null;
    }

}
