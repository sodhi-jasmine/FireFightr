package com.jasminesodhi.firefighterapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.UtilityFunctions;
import rx.schedulers.Schedulers;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    final String url = "http://twentyeight10.tech/clashhacks3/set/chotu";
    String message;

    LocationManager lm;
    Location location;

    double longitude, latitude;

    protected boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions()
    {
        String permissions[] = {"android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION","android.permission.INTERNET"};
        requestPermissions(permissions,200);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(shouldAskPermission())
        {
            askPermissions();
        }

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                url_sequence();
            }
        }, 0, 1000);

    }



    private String get_location() {

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.d("Latitude", Double.toString(latitude));
        Log.d("Longitude", Double.toString(longitude));

        message = "/" + latitude + "," + longitude;
        return message;
    }

    private String get_magnetic_field_values() {

        new ReactiveSensors(this).observeSensor(TYPE_MAGNETIC_FIELD)
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ReactiveSensorEvent>() {
                    @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                        SensorEvent event = reactiveSensorEvent.getSensorEvent();

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        message = "/" + x + "," + y + "," + z;

                        Log.d("Magnetic Field Readings", message);
                        //Log.d("URL", url);
                    }
                });
        return message;
    }

    private String get_accelerometer_values() {

        new ReactiveSensors(this).observeSensor(TYPE_ACCELEROMETER)
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ReactiveSensorEvent>() {
                    @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                        SensorEvent event = reactiveSensorEvent.getSensorEvent();

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        message = "/" + x + "," + y + "," + z;

                        Log.d("Accelerator Readings", message);
                        //Log.d("URL", url);
                    }
                });
        return message;
    }

    private String get_gyroscope_values() {

        new ReactiveSensors(this).observeSensor(TYPE_GYROSCOPE)
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ReactiveSensorEvent>() {
                    @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
                        SensorEvent event = reactiveSensorEvent.getSensorEvent();

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        message = "/" + x + "," + y + "," + z;

                        Log.d("Gyroscope Readings", message);
                        //Log.d("URL", url);
                    }
                });
        return message;
    }

    private void url_sequence(){
        String urlToBe = url+"/28.53612163,77.27057318";
//        urlToBe += get_location();
        urlToBe += get_accelerometer_values();
        urlToBe += get_gyroscope_values();
        urlToBe += get_magnetic_field_values();
        Log.d("URL_1",urlToBe);

        queue = Volley.newRequestQueue(this);
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlToBe, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("URL_2", url);
                Log.d("Response_1", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("URL_3", url);
                Log.e("Response_2", error.toString());
            }
        });
        queue.add(getRequest);
    }

}
