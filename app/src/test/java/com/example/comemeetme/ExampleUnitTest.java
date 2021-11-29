package com.example.comemeetme;

import static org.junit.Assert.assertEquals;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.junit.Test;



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void map_isGenerated() {
        LatLng latLng = new LatLng(39.103119, -84.512016);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(10)                  // Sets the tilt of the camera to 30 degrees
                .build();
        assertEquals(cameraPosition.target, latLng);
    }

    @Test
    public void cameraPositionZoom_isCorrect() {
        LatLng latLng = new LatLng(39.103119, -84.512016);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(10)                  // Sets the tilt of the camera to 30 degrees
                .build();
        assertEquals(cameraPosition.zoom, 10, 0);
    }


}