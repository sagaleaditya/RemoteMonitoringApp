package com.project.vic.remotemonitoringsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button speedButton, distanceButton;

    LocationManager locationManager;

    float speed, distance;

    boolean speedSms, distanceSms;

    String mobile_number;

    String speedMessage, distanceMessage;

    int speedLimit, distanceLimit;

    String spd, dist;

    double house_latitude, house_longitude;


    private void update(){

        speedSms = true;

        distanceSms = true;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mobile_number = prefs.getString(getString(R.string.mobile_number_key),
                getString(R.string.mobile_default));

        String spdLimit = prefs.getString(getString(R.string.speed_limit_key),
                getString(R.string.speed_limit_default));

        String disLimit = prefs.getString(getString(R.string.distance_limit_key),
                getString(R.string.distance_limit_default));

        String lat = prefs.getString(getString(R.string.latitude_key),
                getString(R.string.latitude_default));

        String lng = prefs.getString(getString(R.string.longitude_key),
                getString(R.string.longitude_default));

        house_latitude = Double.parseDouble(lat);

        house_longitude = Double.parseDouble(lng);

        speedLimit = Integer.parseInt(spdLimit);

        distanceLimit = Integer.parseInt(disLimit);


        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedButton = (Button) findViewById(R.id.spd_button);
        distanceButton = (Button) findViewById(R.id.distance_button);

        speedMessage = "Speed Limit Exceeded ";

        distanceMessage = "Boundary Limit Crossed ";

        update();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        update();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null) {

            Location fixedLocation = new Location("");
            fixedLocation.setLatitude(house_latitude);
            fixedLocation.setLongitude(house_longitude);

            distance = (fixedLocation.distanceTo(location))/1000;

            speed = location.getSpeed() * 3.6f;

            if (speed > speedLimit && speedSms) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mobile_number, null, speedMessage + speedLimit + "km/h", null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                    speedSms = false;
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }

            if (distance > distanceLimit && distanceSms) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mobile_number, null, distanceMessage + distanceLimit + "km", null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                    distanceSms = false;
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }

            spd = String.format("%.2f", speed);
            dist = String.format("%.2f", distance);

            speedButton.setText("Speed- " + spd + "km/h");
            distanceButton.setText("Distance- " + dist + "km");

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_exit) {
            onDestroy();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
