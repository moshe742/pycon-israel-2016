package com.ls.pycon_israel.model.requests;

import com.ls.drupal.DrupalClient;
import com.ls.pycon_israel.model.data.Event;
import com.ls.http.base.BaseRequest;

import java.util.Map;

public class SocialRequest extends BaseSafeConsumeContainerRequest<Event.Holder> {

    public SocialRequest(DrupalClient client) {
        super(client, new Event.Holder());
    }

    @Override
    protected String getPath() {
        return "getSocialEvents.js";
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
