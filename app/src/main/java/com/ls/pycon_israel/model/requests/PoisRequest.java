package com.ls.pycon_israel.model.requests;

import com.ls.drupal.DrupalClient;
import com.ls.pycon_israel.model.data.POI;
import com.ls.http.base.BaseRequest;

import java.util.Map;

public class PoisRequest extends BaseSafeConsumeContainerRequest<POI.Holder> {

    public PoisRequest(DrupalClient client) {
        super(client, new POI.Holder());
    }

    @Override
    protected String getPath() {
        return "getPOI";
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
