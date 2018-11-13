package deepdive.cnm.edu.nasaapod.controller;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deepdive.cnm.edu.nasaapod.ApodApplication;
import deepdive.cnm.edu.nasaapod.BuildConfig;
import deepdive.cnm.edu.nasaapod.R;
import deepdive.cnm.edu.nasaapod.controller.DateTimePickerFragment.Mode;
import deepdive.cnm.edu.nasaapod.model.Apod;
import deepdive.cnm.edu.nasaapod.service.ApodService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String CALENDAR_KEY = "calendar";
  private static final String APOD_KEY = "apod";

  private WebView webView;
  private String apiKey;
  private ProgressBar progressSpinner;
  private FloatingActionButton jumpDate;
  private Calendar calendar;
  private ApodService service;
  private Apod apod;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupWebView();
    setupService();
    setupUI();
    setupDefaults(savedInstanceState);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
   boolean handled = true;
    switch (item.getItemId()){
      case R.id.sign_out:
        signOut();
        break;
    default:
      handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(CALENDAR_KEY, calendar.getTimeInMillis());
    outState.putParcelable(APOD_KEY, apod);
  }

  private void setupWebView() {
    webView = findViewById(R.id.web_view);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
      }
      @Override
      public void onPageFinished(WebView view, String url) {
        progressSpinner.setVisibility(View.GONE);
        if (apod != null) {
          Toast.makeText(MainActivity.this, apod.getTitle(), Toast.LENGTH_LONG).show();
        }
      }
    });
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
  }

  private void setupUI() {
    progressSpinner = findViewById(R.id.progress_spinner);
    progressSpinner.setVisibility(View.GONE);
    jumpDate = findViewById(R.id.jump_date);
    jumpDate.setOnClickListener((v) -> pickDate());
  }

  private void setupService() {
    Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setDateFormat(DATE_FORMAT)
        .create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(getString(R.string.base_url))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    service = retrofit.create(ApodService.class);
    apiKey = BuildConfig.API_KEY;
  }

  private void setupDefaults(Bundle savedInstanceState) {
    calendar = Calendar.getInstance();
    if (savedInstanceState != null) {
      calendar.setTimeInMillis(savedInstanceState.getLong(CALENDAR_KEY, calendar.getTimeInMillis()));
      apod = savedInstanceState.getParcelable(APOD_KEY);
    }
    if (apod != null) {
      progressSpinner.setVisibility(View.VISIBLE);
      webView.loadUrl(apod.getUrl());
    } else {
      new ApodTask().execute();
    }
  }

  private void pickDate() {
    DateTimePickerFragment picker = new DateTimePickerFragment();
    picker.setMode(Mode.DATE);
    picker.setCalendar(calendar);
    picker.setListener((cal) -> new ApodTask().execute(cal.getTime()));
    picker.show(getSupportFragmentManager(), picker.getClass().getSimpleName());
  }

  private void signOut(){
    ApodApplication app = ApodApplication.getInstance();
    app.getClient().signOut().addOnCompleteListener(this, (task) -> {
      app.setAccount(null);
      Intent intent = new Intent(MainActivity.this, LoginActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    });

  }
  private class ApodTask extends AsyncTask<Date, Void, Apod> {

    private Date date;

    @Override
    protected void onPreExecute() {
      progressSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Apod apod) {
      MainActivity.this.apod = apod;
      // TODO Handle hdUrl.
      webView.loadUrl(apod.getUrl());
    }

    @Override
    protected void onCancelled(Apod apod) {
      progressSpinner.setVisibility(View.GONE);
      Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Apod doInBackground(Date... dates) {
      Apod apod = null;
      try {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        date = (dates.length == 0) ? calendar.getTime() : dates[0];
        Response<Apod> response = service.get(apiKey, format.format(date)).execute();
        if (response.isSuccessful()) {
          apod = response.body();
          calendar.setTime(date);
        }
      } catch (IOException e) {
        // Do nothing: apod is already null.
      } finally {
        if (apod == null) {
          cancel(true);
        }
      }
      return apod;
    }
  }

}








