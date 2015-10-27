package ba.bitcamp.bitNavigator.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ba.bitcamp.bitNavigator.bitnavigator.R;
import ba.bitcamp.bitNavigator.controllers.*;
import ba.bitcamp.bitNavigator.controllers.MapsActivity;
import ba.bitcamp.bitNavigator.models.User;
import ba.bitcamp.bitNavigator.service.ServiceRequest;


public class LoginActivity extends Activity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mLinkToRegister;

    public User user;

    public SharedPreferences sharedpreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("email")){
            Intent i = new Intent(getApplicationContext(),
                    ProfileActivity.class);
            startActivity(i);
            finish();
        }

        mEmailEditText = (EditText) findViewById(R.id.login_email);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login);
        mLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        mLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        Log.e("////////////////","id");



        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Loging in...");
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", email);
                    json.put("password", password);
                } catch (JSONException e) {
                    Log.e("++++++++++++++++++++++","id");
                    e.printStackTrace();
                }
                String url = getString(R.string.service_sign_in);
                ServiceRequest.post(url, json.toString(), loginVerification());
            }
        });


        Button mLoginButton = (Button) findViewById(R.id.btnProfile);
        mLoginButton.setOnClickListener(new View.OnClickListener(){
                                            public void onClick(View v) {
                                                Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.LoginActivity.class);
                                                startActivity(i);
                                            }
                                        }
        );

        Button mRegisterButton = (Button) findViewById(R.id.btnReservations);
        mRegisterButton.setOnClickListener(new View.OnClickListener(){
                                               public void onClick(View v) {
                                                   Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.LoginActivity.class);
                                                   startActivity(i);
                                               }
                                           }
        );

        Button mSearchButton = (Button) findViewById(R.id.btnSearch);
        mSearchButton.setOnClickListener(new View.OnClickListener(){
                                             public void onClick(View v) {
                                                 Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.LoginActivity.class);
                                                 startActivity(i);
                                             }
                                         }
        );

        Button mMapButton = (Button) findViewById(R.id.btnMap);
        mMapButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.MapsActivity.class);
                                              startActivity(i);
                                          }
                                      }
        );

    }


    private Callback loginVerification() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                makeToast("Email and/or password incorrect");
                Log.e("**************", "id");
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    String responseJSON= response.body().string();
                    JSONObject userObj = new JSONObject(responseJSON.toString());
                    Integer id = userObj.getInt("id");
                    String name = userObj.getString("firstName");
                    String surname = userObj.getString("lastName");
                    String email = userObj.getString("email");
                    String password = userObj.getString("password");
                    user = new User(id, name, surname, email, password);
                    makeToast("Successfull loged in " + user.getFirstName());
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("id", id);
                    editor.putString("name", name);
                    editor.putString("surname", surname);
                    editor.putString("email", email);
                    editor.commit();
                } catch (JSONException e) {
                    Log.e("**************", "id");
                    Log.e("Message = ",e.getMessage());
                    makeToast("Email and/or password incorrect");
                }
            }
        };
    }

    public void gotToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void makeToast(final String message){
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ba.bitcamp.bitNavigator.controllers.LoginActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
