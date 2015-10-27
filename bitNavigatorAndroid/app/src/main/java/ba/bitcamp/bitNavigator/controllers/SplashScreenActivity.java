package ba.bitcamp.bitNavigator.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ba.bitcamp.bitNavigator.bitnavigator.R;
import ba.bitcamp.bitNavigator.controllers.*;
import ba.bitcamp.bitNavigator.controllers.MapsActivity;
import ba.bitcamp.bitNavigator.lists.PlaceList;
import ba.bitcamp.bitNavigator.models.Place;
import ba.bitcamp.bitNavigator.service.ServiceRequest;

/**
 * Created by Sehic on 26.10.2015.
 */
public class SplashScreenActivity extends Activity{

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String url = getString(R.string.service_all_places);
        ServiceRequest.get(url, getPlaces());

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, MapsActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private Callback getPlaces() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //makeToast(R.string.toast_try_again);
                Log.d("dibag", "hdashgdkjsa87998987");
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    String responseJSON= response.body().string();
                    JSONArray array = new JSONArray(responseJSON);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject postObj = array.getJSONObject(i);
                        Integer id = postObj.getInt("id");
                        String name = postObj.getString("title");
                        String address = postObj.getString("address");
                        Double longitude = postObj.getDouble("longitude");
                        Double latitude = postObj.getDouble("latitude");

                        Place place = new Place(id, name, address, longitude, latitude);
                        if (!PlaceList.getInstance().getPlaceList().contains(place)) {
                            PlaceList.getInstance().add(place);
                        }
                        Log.d("dibag", address);

                    }
                } catch (JSONException e) {
                    //makeToast(R.string.toast_try_again);
                    e.printStackTrace();
                }
            }
        };
    }

}
