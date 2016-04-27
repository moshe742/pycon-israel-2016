package com.ls.ui.activity;

import com.google.android.gms.analytics.GoogleAnalytics;

import com.ls.pycon_israel.R;
import com.ls.pycon_israel.app.App;
import com.ls.pycon_israel.model.Model;
import com.ls.pycon_israel.model.UpdatesManager;
import com.ls.pycon_israel.model.dao.EventDao;
import com.ls.pycon_israel.model.data.Level;
import com.ls.pycon_israel.model.data.Speaker;
import com.ls.pycon_israel.model.data.SpeakerDetailsEvent;
import com.ls.pycon_israel.model.managers.SpeakerManager;
import com.ls.ui.view.CircleImageView;
import com.ls.ui.view.NotifyingScrollView;
import com.ls.utils.AnalyticsManager;
import com.ls.utils.DateUtils;
import com.ls.utils.WebviewUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SpeakerDetailsActivity extends StackKeeperActivity implements View.OnClickListener {

    public static final String EXTRA_SPEAKER = "EXTRA_SPEAKER";
    public static final String EXTRA_SPEAKER_ID = "EXTRA_SPEAKER_ID";

    private static final String TWITTER_URL = "https://twitter.com/";
    private static final String TWITTER_APP_URL = "twitter://user?screen_name=";

    private SpeakerManager mSpeakerManager;
    private long mSpeakerId = -1;
    private String mSpeakerName;
    private Speaker mSpeaker;

    private View mViewToolbar;
    private TextView mTitle;
    private NotifyingScrollView mScrollView;

    private boolean mIsDataLoaded;
    private boolean mIsWebLoaded;

    private UpdatesManager.DataUpdatedListener updateListener = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<Integer> requestIds) {
            loadSpeakerFromDb();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_speaker_details);

        Model.instance().getUpdatesManager().registerUpdateListener(updateListener);
        mSpeakerManager = Model.instance().getSpeakerManager();

        initData();
        initToolbar();
        initView();
        loadSpeakerFromDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Model.instance().getUpdatesManager().unregisterUpdateListener(updateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        mSpeaker = getIntent().getParcelableExtra(EXTRA_SPEAKER);
        mSpeakerId = getIntent().getLongExtra(EXTRA_SPEAKER_ID, -1);
        if (mSpeaker != null) {
            Log.d("initData", mSpeaker.getFirstName());
            mSpeakerName = String.format("%s %s", mSpeaker.getFirstName(), mSpeaker.getLastName());
        }
        AnalyticsManager.sendEvent(this, R.string.speaker_category, R.string.action_open, mSpeakerId);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mViewToolbar = findViewById(R.id.viewToolbar);
        mViewToolbar.setAlpha(0);

        mTitle = (TextView) findViewById(R.id.toolbarTitle);
        mTitle.setText(mSpeakerName);
        mTitle.setAlpha(0);
    }

    private void initView() {
        mScrollView = (NotifyingScrollView) findViewById(R.id.scrollView);
        mScrollView.setOnScrollChangedListener(onScrollChangedListener);
    }

    private void loadSpeakerFromDb() {
        if (mSpeakerId == -1) return;

        new AsyncTask<Void, Void, Speaker>() {
            @Override
            protected Speaker doInBackground(Void... params) {
                return mSpeakerManager.getSpeakerById(mSpeakerId);
            }

            @Override
            protected void onPostExecute(Speaker speaker) {
                if (speaker != null) {
                    fillView(speaker);
                }
            }
        }.execute();
    }

    private void fillView(Speaker speaker) {
        mSpeaker = speaker;
        fillSpeakerInfo();
        fillSpeakerDescription();
        fillSocialNetworks();
        loadSpeakerEvents(mSpeaker);
    }

    private void fillSpeakerInfo() {
        CircleImageView imgPhoto = (CircleImageView) findViewById(R.id.imgPhoto);
        String imageUrl = mSpeaker.getAvatarImageUrl();
        imgPhoto.setImageWithURL(imageUrl);

        String speakerName = TextUtils.isEmpty(mSpeaker.getFirstName()) ? "" : mSpeaker.getFirstName() + " ";
        speakerName += TextUtils.isEmpty(mSpeaker.getLastName()) ? "" : mSpeaker.getLastName();
        ((TextView) findViewById(R.id.txtSpeakerName)).setText(speakerName);

        if (TextUtils.isEmpty(mSpeaker.getJobTitle()) && TextUtils.isEmpty(mSpeaker.getOrganization())) {
            findViewById(R.id.txtSpeakerPosition).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txtSpeakerPosition).setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(mSpeaker.getJobTitle())) {
            ((TextView) findViewById(R.id.txtSpeakerPosition)).setText(mSpeaker.getJobTitle());
        }

        if (!TextUtils.isEmpty(mSpeaker.getOrganization())) {
            TextView jobTxt = (TextView) findViewById(R.id.txtSpeakerPosition);
            String text = jobTxt.getText().toString() + " at " + mSpeaker.getOrganization();
            jobTxt.setText(text);
        }
    }

    private void fillSpeakerDescription() {
        WebView webView = (WebView) findViewById(R.id.webView);
        if (!TextUtils.isEmpty(mSpeaker.getCharact())) {

            String html = WebviewUtils.getHtml(this, mSpeaker.getCharact());
            webView.setVisibility(View.VISIBLE);

            webView.setHorizontalScrollBarEnabled(false);
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    mIsWebLoaded = true;
                    completeLoading();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    WebviewUtils.openUrl(SpeakerDetailsActivity.this, url);
                    return true;
                }
            });

        } else {
            mIsWebLoaded = true;
            completeLoading();
        }
    }

    private void fillSocialNetworks() {
        if (TextUtils.isEmpty(mSpeaker.getTwitterName())) {
            findViewById(R.id.holderBtnTwitter).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btnTwitter).setOnClickListener(this);
        }

        if (TextUtils.isEmpty(mSpeaker.getWebSite())) {
            findViewById(R.id.holderBtnWebsite).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btnWebsite).setOnClickListener(this);
        }
    }

    private void loadSpeakerEvents(final Speaker speaker) {
        new AsyncTask<Void, Void, List<SpeakerDetailsEvent>>() {
            @Override
            protected List<SpeakerDetailsEvent> doInBackground(Void... params) {
                EventDao eventDao = new EventDao(App.getContext());
                return eventDao.getEventsBySpeakerId(speaker.getId());
            }

            @Override
            protected void onPostExecute(List<SpeakerDetailsEvent> events) {
                mIsDataLoaded = true;
                addSpeakerEvents(events);
                completeLoading();
            }
        }.execute();
    }

    private void addSpeakerEvents(List<SpeakerDetailsEvent> events) {
        LinearLayout layoutEvents = (LinearLayout) findViewById(R.id.layoutEvents);
        LayoutInflater inflater = LayoutInflater.from(SpeakerDetailsActivity.this);
        layoutEvents.removeAllViews();

        for (SpeakerDetailsEvent event : events) {
            View eventView = inflater.inflate(R.layout.item_speakers_event, null);
            fillEventView(event, eventView);
            layoutEvents.addView(eventView);
        }
    }

    private void fillEventView(final SpeakerDetailsEvent event, View eventView) {
        ((TextView) eventView.findViewById(R.id.txtArticleName)).setText(event.getEventName());
        TextView txtTrack = (TextView) eventView.findViewById(R.id.txtTrack);
        if (!TextUtils.isEmpty(event.getTrackName())) {
            txtTrack.setText(event.getTrackName());
            txtTrack.setVisibility(View.VISIBLE);
        }

        String weekDay = DateUtils.getInstance().getWeekDay(event.getFrom());
        String fromTime = DateUtils.getInstance().getTime(this, event.getFrom());
        String toTime = DateUtils.getInstance().getTime(this, event.getTo());

        TextView txtWhere = (TextView) eventView.findViewById(R.id.txtWhere);
        String date = String.format("%s, %s - %s", weekDay, fromTime, toTime);
        txtWhere.setText(date);
        if (!event.getPlace().equals("")) {
            txtWhere.append(String.format(" in %s", event.getPlace()));
        }

        initEventExpLevel(eventView, event);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.startThisActivity(SpeakerDetailsActivity.this, event.getEventId(), event.getFrom());
            }
        });
    }

    private void initEventExpLevel(View eventView, SpeakerDetailsEvent event) {
        TextView txtExpLevel = (TextView) eventView.findViewById(R.id.txtExpLevel);
        ImageView imgExpLevel = (ImageView) eventView.findViewById(R.id.imgExpLevel);

        String expLevel = event.getLevelName();
        if (!TextUtils.isEmpty(expLevel)) {

            String expText = String.format("%s %s", getResources().getString(R.string.experience_level), expLevel);
            txtExpLevel.setText(expText);
            txtExpLevel.setVisibility(View.VISIBLE);

            int expIcon = Level.getIcon(expLevel);
            if (expIcon != 0) {
                imgExpLevel.setImageResource(expIcon);
                imgExpLevel.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTwitter:
                try {
                    String url = TWITTER_APP_URL + mSpeaker.getTwitterName();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    String url = TWITTER_URL + mSpeaker.getTwitterName();
                    openBrowser(url);
                }
                break;
            case R.id.btnWebsite:
                String url = mSpeaker.getWebSite();
                openBrowser(url);
                break;
        }
    }

    private NotifyingScrollView.OnScrollChangedListener onScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {

        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            int headerHeight = findViewById(R.id.imgHeader).getHeight();
            float headerRatio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;

            fadeActionBar(headerRatio);
        }

        private void fadeActionBar(float headerRatio) {
            mTitle.setAlpha(headerRatio);
            mViewToolbar.setAlpha(headerRatio);
        }
    };

    private void openBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_apps_can_perform_this_action), Toast.LENGTH_SHORT).show();
        }
    }

    private void completeLoading() {
        if (mIsDataLoaded && mIsWebLoaded) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }
}
