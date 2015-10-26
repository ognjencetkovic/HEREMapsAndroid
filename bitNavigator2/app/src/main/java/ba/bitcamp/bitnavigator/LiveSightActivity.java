package ba.bitcamp.bitnavigator;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class LiveSightActivity extends Activity {

    // map embedded in the composite fragment
    private Map map;

    // composite fragment embedded in this activity
    private CompositeFragment compositeFragment;

    // ARController is a facade for controlling LiveSight behavior
    private ARController arController;

    // buttons which will allow the user to start LiveSight and add objects
    private Button startButton;
    private Button stopButton;
    private Button toggleObjectButton;

    // the image we will display in LiveSight
    private Image image;

    // ARIconObject represents the image model which LiveSight accepts for display
    private ARIconObject arIconObject;
    private boolean objectAdded;

    // Application paused
    private boolean paused;

    // Define positioning listener
    private PositioningManager.OnPositionChangedListener positionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_sight);

        // Search for the composite fragment to finish setup by calling init().
        compositeFragment = (CompositeFragment) getFragmentManager().findFragmentById(R.id.compositefragment);
        compositeFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    Log.d("dibag", "if-------------");
                    // retrieve a reference of the map from the composite fragment
                    map = compositeFragment.getMap();
                    // Set the map center coordinate to current position (no animation)
                    getPosition();
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    // LiveSight setup should be done after fragment init is complete
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
        toggleObjectButton = (Button) findViewById(R.id.toggleObject);
    }

    private void setupLiveSight() {
        // ARController should not be used until fragment init has completed
        arController = compositeFragment.getARController();
        // tells LiveSight to display icons while viewing the map (pitch down)
        arController.setUseDownIconsOnMap(true);
        // tells LiveSight to use a static mock location instead of the devices GPS fix
        arController.setAlternativeCenter(new GeoCoordinate(49.279483, -123.116906, 0.0));
    }

    public void startLiveSight(View view) {
        if (arController != null) {
            // triggers the transition from Map mode to LiveSight mode
            Error error = arController.start();

            if (error == Error.NONE) {

                Log.d("dibag", "if-------------"+error);
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
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(getApplicationContext(), "Error stopping LiveSight: " + error.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    public void toggleObject(View view) {
        if (arController != null) {
            if (arController != null) {
                if (!objectAdded) {
                    if (arIconObject == null) {

                        image = new Image();
                        try {
                            image.setImageResource(R.drawable.icon);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // creates a new icon object which uses the same image in up and down views
                        arIconObject = new ARIconObject(new GeoCoordinate(49.276744, -123.112049, 2.0), (View) null, image);
                    }

                    // adds the icon object to LiveSight to be rendered
                    arController.addARObject(arIconObject);
                    objectAdded = true;
                    toggleObjectButton.setText("Remove Object");
                } else {

                    // removes the icon object from LiveSight, it will no longer be rendered
                    arController.removeARObject(arIconObject);
                    objectAdded = false;
                    toggleObjectButton.setText("Add Object");
                }
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
                    map.setCenter(position.getCoordinate(), Map.Animation.NONE);

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

}
