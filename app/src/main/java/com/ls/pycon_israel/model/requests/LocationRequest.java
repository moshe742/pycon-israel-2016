package com.ls.pycon_israel.model.requests;

import com.ls.drupal.DrupalClient;
import com.ls.pycon_israel.model.data.Location;
import com.ls.http.base.BaseRequest;

import java.util.Map;

public class LocationRequest extends BaseSafeConsumeContainerRequest<Location.Holder> {

    public LocationRequest(DrupalClient client) {
        super(client, new Location.Holder());
    }

    @Override
    protected String getPath() {
        return "getLocations.js";
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
