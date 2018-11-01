package deepdive.cnm.edu.nasaapod.controller;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deepdive.cnm.edu.nasaapod.BuildConfig;
import deepdive.cnm.edu.nasaapod.R;
import deepdive.cnm.edu.nasaapod.model.Apod;
import deepdive.cnm.edu.nasaapod.service.ApodService;
import java.util.Calendar;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {
private  static final String Date_Format = "yyyy-MM-dd";
private static final String CALENDER_KEY = "calender";
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
  }

  private void setupWebView(){
    webView = findViewById(R.id.web_view);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        progressSpinner.setVisibility(View.INVISIBLE);
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

  private void setupUI(){
    progressSpinner = findViewById(R.id.progress_spinner);
    progressSpinner.setVisibility(View.GONE);
    jumpDate = findViewById(R.id.jump_date);
    jumpDate.setOnClickListener(new OnClickListener() {
      //TODO Use lambda form
      @Override
      public void onClick(View v) {
        //TODO Dispay date picker
      }
    });
  }

  private void setupService(){
    Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setDateFormat(Date_Format)
        .create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(getString(R.string.base_url))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    service = retrofit.create(ApodService.class);
    apiKey = BuildConfig.API_KEY;
  }
}
