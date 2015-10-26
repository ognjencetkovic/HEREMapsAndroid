package ba.bitcamp.bitnavigator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    private Button btnLoc;

    private boolean paused;

    // Define positioning listener
    PositioningManager.OnPositionChangedListener positionListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapfragment);

        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    Log.d("dibag", "if------------");
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Gets users position via GPS and centers the map to that position
                    getPosition();
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                } else {
                    Log.d("dibag", "else------------" + error);
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

        btnLoc = (Button) findViewById(R.id.btn_loc);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosition();
            }
        });


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
/*
    // To pause positioning listener
    @Override
    public void onPause() {
        PositioningManager.getInstance().stop();
        super.onPause();
        paused = true;
    }

    // To remove the positioning listener
    @Override
    public void onDestroy() {
        // Cleanup
        PositioningManager.getInstance().removeListener(
                positionListener);
        map = null;
        super.onDestroy();
    }
*/
}
