package ba.bitcamp.bitNavigator.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ba.bitcamp.bitNavigator.bitnavigator.R;
import ba.bitcamp.bitNavigator.controllers.*;
import ba.bitcamp.bitNavigator.controllers.LoginActivity;
import ba.bitcamp.bitNavigator.controllers.MapsActivity;
import ba.bitcamp.bitNavigator.service.ServiceRequest;


/**
 * Created by Semir on 17.10.2015.
 */

public class RegisterActivity extends Activity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button mLinkToLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);

        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        mLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);


        mLinkToLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String emailString = email.getText().toString();
                if (!isValidEmail(emailString)) {
                    email.setError("Invalid Email");
                    return;
                }

                String pass = password.getText().toString();
                if (!isValidPassword(pass)) {
                    password.setError("Invalid Password");
                    return;
                }

                final String fName = firstName.getText().toString();
                if(!isValidName(fName)){
                    firstName.setError("First name can only contain letters");
                    return;
                }

                final String lName = lastName.getText().toString();
                if(!isValidName(lName)){
                    lastName.setError("Last name can only contain letters");
                    return;
                }

                if(!(password.getText().toString().equals(confirmPassword.getText().toString()))){
                    confirmPassword.setError("Password does not match");
                    return;
                }
                Log.d("dibag", emailString + " ---  " + pass);
                JSONObject json = new JSONObject();
                try {
                    json.put("firstName", fName);
                    json.put("lastName", lName);
                    json.put("email", emailString);
                    json.put("password", pass);
                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("dibag", emailString + "++++++++++" + pass);
                }

                String url = getString(R.string.service_sign_up);

                ServiceRequest.post(url, json.toString(), registerUser());


            }
        });


        Button mLoginButton = (Button) findViewById(R.id.btnProfile);
        mLoginButton.setOnClickListener(new OnClickListener(){
                                            public void onClick(View v) {
                                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(i);
                                            }
                                        }
        );

        Button mRegisterButton = (Button) findViewById(R.id.btnReservations);
        mRegisterButton.setOnClickListener(new OnClickListener(){
                                               public void onClick(View v) {
                                                   Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.RegisterActivity.class);
                                                   startActivity(i);
                                               }
                                           }
        );

        Button mSearchButton = (Button) findViewById(R.id.btnSearch);
        mSearchButton.setOnClickListener(new OnClickListener(){
                                             public void onClick(View v) {
                                                 Intent i = new Intent(getApplicationContext(), ba.bitcamp.bitNavigator.controllers.RegisterActivity.class);
                                                 startActivity(i);
                                             }
                                         }
        );

        Button mMapButton = (Button) findViewById(R.id.btnMap);
        mMapButton.setOnClickListener(new OnClickListener() {
                                          public void onClick(View v) {
                                              Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                              startActivity(i);
                                          }
                                      }
        );
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidName(String name){
        if(name.equals(""))
            return true;
        String NAME_PATTERN = "[a-zA-Z]+";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 7) {
            return true;
        }
        return false;
    }

    private Callback registerUser() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                makeToast("Wrong input.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                    makeToast("Registered! Please log in.");
                    goToLogin();
            }
        };
    }

    public void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void makeToast(final String message){
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ba.bitcamp.bitNavigator.controllers.RegisterActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}