package deepdive.cnm.edu.nasaapod;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
private  static final String Date_Format = "yyyy-MM-dd";
private static final String CALENDER_KEY = "calender";
private static final String APOD_KEY = "apod";

private WebView webView;
private String apiKey;
private ProgressBar progressSpinner;
private FloatingActionButton jumpDate;
private Calendar calendar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupWebView();
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
}
