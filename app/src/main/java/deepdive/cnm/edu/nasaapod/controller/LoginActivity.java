package deepdive.cnm.edu.nasaapod.controller;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import deepdive.cnm.edu.nasaapod.ApodApplication;
import deepdive.cnm.edu.nasaapod.R;
public class LoginActivity extends AppCompatActivity {
  private static final int REQUEST_CODE = 1010;
SignInButton signIn;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    signIn = findViewById(R.id.sign_in);
    signIn.setOnClickListener((view) -> signin());

  }

  @Override
  protected void onStart() {
    super.onStart();
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    if(account != null){
      ApodApplication.getInstance().setAccount(account);
      switchToMain();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(requestCode== REQUEST_CODE){
      try {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        GoogleSignInAccount account = task.getResult(ApiException.class);
        ApodApplication.getInstance().setAccount(account);
      } catch (ApiException e) {
        //e.printStackTrace();
        Toast.makeText(this, R.string.sign_in_error, Toast.LENGTH_LONG).show();
      }
      switchToMain();
    }
  }


  private void signin(){
    Intent intent = ApodApplication.getInstance().getClient().getSignInIntent();
    startActivityForResult(intent, REQUEST_CODE);
  }

  private void switchToMain(){
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
}
