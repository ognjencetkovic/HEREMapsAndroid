package ba.bitcamp.bitNavigator.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ba.bitcamp.bitNavigator.bitnavigator.R;
import ba.bitcamp.bitNavigator.controllers.*;
import ba.bitcamp.bitNavigator.controllers.LoginActivity;

/**
 * Created by hajrudin.sehic on 27/10/15.
 */
public class ProfileActivity extends Activity{

    private ImageView mImage;
    private TextView mName;
    private TextView mEmail;
    private Button mLogout;
    private LinearLayout llProfileLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_user_profile);

        mImage = (ImageView) findViewById(R.id.imgProfilePic);
        mName = (TextView) findViewById(R.id.txtName);
        mEmail = (TextView) findViewById(R.id.txtEmail);
        mLogout = (Button) findViewById(R.id.btn_sign_out);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        final SharedPreferences preferences = getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String name = preferences.getString("name","") + " ";
        name += preferences.getString("surname", "");
        String email = preferences.getString("email","");
        mName.setText(name);
        mEmail.setText(email);
        mLogout.setText("Logout");
        mLogout.setVisibility(View.VISIBLE);
        mLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });



    }

}
