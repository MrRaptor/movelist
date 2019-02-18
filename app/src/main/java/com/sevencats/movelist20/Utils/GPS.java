package com.sevencats.movelist20.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.sevencats.movelist20.MainActivity;

import java.io.IOException;
import java.util.List;

public class GPS implements LocationListener {

    private Context context;

    public GPS(Context context) {
        this.context = context;
    }

    public Location getLocation() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(context,
                    " Немає прав  ", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider == null) {
            Toast.makeText(context, "Не вдалось отримати координати", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            locationManager.requestLocationUpdates(provider, 0, 0, this);
            return locationManager.getLastKnownLocation(provider);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

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

    public String getAddress (){

        Location l = getLocation();
        Geocoder geocoder = new Geocoder(context);

        if (l != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            List<Address> street = null;
            try {
                street = geocoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                Toast.makeText(context, "Проблема з кодером ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if (street != null) {
                String address = street.get(0).getAddressLine(0);
                String[] Street = address.trim().split(",");
                String[] miniStreet = Street[0].trim().split(" ");
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 1; i < miniStreet.length; i++) {
                    strBuilder.append(miniStreet[i] + " ");
                }
                return strBuilder.toString();
            } else
                Toast.makeText(context, "Проблема з отриманням адреса ", Toast.LENGTH_SHORT).show();
            return null;
        } else
            Toast.makeText(context, "Проблема з отриманням координат ", Toast.LENGTH_SHORT).show();
        return null;
    }
}
