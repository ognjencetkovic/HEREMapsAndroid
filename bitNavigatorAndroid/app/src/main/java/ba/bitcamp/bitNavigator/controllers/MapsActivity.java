package ba.bitcamp.bitNavigator.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.mapping.MapMarker;
import java.io.IOException;

import ba.bitcamp.bitNavigator.bitnavigator.R;
import ba.bitcamp.bitNavigator.lists.PlaceList;
import ba.bitcamp.bitNavigator.models.Place;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.ar.ARController;
import com.here.android.mpa.ar.ARController.Error;
import com.here.android.mpa.ar.ARIconObject;
import com.here.android.mpa.ar.CompositeFragment;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;

public class MapsActivity extends Activity {

    // map embedded in the composite fragment
    private Map map;

    // composite fragment embedded in this activity
    private CompositeFragment compositeFragment;

    // ARController is a facade for controlling LiveSight behavior
    private ARController arController;

    // buttons which will allow the user to start LiveSight and add objects
    private Button startButton;
    private Button stopButton;
    private ImageView hereButton;

    // the image we will display in LiveSight
    private Image image;

    // ARIconObject represents the image model which LiveSight accepts for display
    private ARIconObject arIconObject;
    private boolean objectAdded;

    // Application paused
    private boolean paused;

    // Define positioning listener
    private PositioningManager.OnPositionChangedListener positionListener;

    //
    private ClusterLayer mClusterLayer;

    private GeoCoordinate mGPSPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Search for the composite fragment to finish setup by calling init().
        compositeFragment = (CompositeFragment) getFragmentManager().findFragmentById(R.id.compositefragment);
        compositeFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == Error.NONE) {
                    Log.d("dibag", "if-------------");
                    // retrieve a reference of the map from the composite fragment
                    map = compositeFragment.getMap();
                    // Set the map center coordinate to current position (no animation)
                    map.setCenter(new GeoCoordinate(43.850, 18.390, 0.0), Map.Animation.NONE);
                    getPosition();
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    // LiveSight setup should be done after fragment init is complete
                    addMarkers();
                    setupLiveSight();
                } else {
                    Log.d("dibag", "else-------------"+error);
                    System.out.println("ERROR: Cannot initialize Composite Fragment");
                }
            }
        });

        // hold references to the buttons for future use
        startButton = (Button) findViewById(R.id.startLiveSight);
        stopButton = (Button) findViewById(R.id.stopLiveSight);
        hereButton = (ImageView) findViewById(R.id.here_button);

        Button mLoginButton = (Button) findViewById(R.id.btnProfile);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(i);
                                            }
                                        }
        );

        Button mRegisterButton = (Button) findViewById(R.id.btnReservations);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
                                               public void onClick(View v) {
                                                   Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                   startActivity(i);
                                               }
                                           }
        );

        Button mSearchButton = (Button) findViewById(R.id.btnSearch);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View v) {
                                                 Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
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

    private void addMarkers() {
        mClusterLayer = new ClusterLayer();
        for (Place place : PlaceList.getInstance().getPlaceList()) {
            MapMarker marker = new MapMarker();
            marker.setCoordinate(new GeoCoordinate(place.getLatitude(), place.getLongitude()));
            marker.setTitle("title");
            marker.setDescription("description");
            marker.showInfoBubble();
            mClusterLayer.addMarker(marker);
        }
        map.addClusterLayer(mClusterLayer);

    }

    private void setupLiveSight() {
        // ARController should not be used until fragment init has completed
        arController = compositeFragment.getARController();
        // tells LiveSight to display icons while viewing the map (pitch down)
        arController.setUseDownIconsOnMap(true);
        for (Place place : PlaceList.getInstance().getPlaceList()) {
            TextView label = new TextView(this);
            label.setText(place.getTitle());
            ARIconObject arIconObject = new ARIconObject(new GeoCoordinate(place.getLatitude(), place.getLongitude()), label, R.drawable.icon);
            arIconObject.setFrontIconSizeScale(3.3f);
            arController.addARObject(arIconObject);
        }
        // tells LiveSight to use a static mock location instead of the devices GPS fix
        //arController.setAlternativeCenter(new GeoCoordinate(49.279483, -123.116906, 0.0));
    }

    public void startLiveSight(View view) {
        if (arController != null) {
            // triggers the transition from Map mode to LiveSight mode
            Error error = arController.start();

            if (error == Error.NONE) {

                map.removeClusterLayer(mClusterLayer);

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "Error starting LiveSight: " + error.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    public void stopLiveSight(View view) {
        if (arController != null) {
            // exits LiveSight mode and returns to Map mode with exit animation
            Error error = arController.stop(true);

            if (error == Error.NONE) {

                map.addClusterLayer(mClusterLayer);

                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(getApplicationContext(), "Error stopping LiveSight: " + error.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    // Resume positioning listener on wake up
    @Override
    public void onResume() {
        super.onResume();
        paused = false;
    }

    public void getPosition() {
        // Register positioning listener
        positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
                // set the center only when the app is in the foreground
                // to reduce CPU consumption
                if (!paused) {

                    mGPSPosition = position.getCoordinate();
                    // Display position indicator
                    map.getPositionIndicator().setVisible(true);
                }
            }
            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {

            }
        };

        PositioningManager.getInstance().addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));

        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
    }

    public void setCenter(View view) {
        if (mGPSPosition != null) {
            map.setCenter(mGPSPosition, Map.Animation.BOW);
        } else {
            Toast.makeText(this, R.string.error_gps, Toast.LENGTH_SHORT).show();
        }
    }
}
