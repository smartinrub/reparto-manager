package com.sergio.martin.repartomanager;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sergio.martin.repartomanager.aux.HelperDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    final int MY_PERMISSIONS_REQUEST_MAPS = 1;

    private HelperDB db;
    int zoom = 13;
    GoogleMap mMap;
    List<LatLng> ptos = new ArrayList<>();
    private boolean error1 = false, error2 = false, error3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = new HelperDB(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "SIN permisos", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Geo geo = new Geo(getApplicationContext(), db.getCodigosPostales());
        geo.execute();
    }

    public class Geo extends AsyncTask<Void, Void, Void> {
        Context context;
        ArrayList<String> CPs;
        List<Address> addresses;
        Address address;
        ArrayList<Address> addressesLatLng;

        Geo(Context context, ArrayList<String> cps) {
            this.context = context;
            CPs = cps;
            addressesLatLng = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                for (String cp : CPs) {
                    addresses = geocoder.getFromLocationName(cp, 1);
                    address = addresses.get(0);
                    addressesLatLng.add(address);
                }
            } catch (NullPointerException e) {
                error1 = true;
                finish();
            } catch (IOException e) {
                error2 = true;
                finish();
            } catch (RuntimeException e) {
                error3 = true;
                finish();
            }

            return null;
        }

        protected void onPostExecute(Void avoid) {
            int i = 0;
            for (Address ad : addressesLatLng) {
                String cp = CPs.get(i);
                addMarker(ad.getLatitude(), ad.getLongitude(), cp);
                i++;
            }
        }
    }

    private void addMarker(double latitud, double longitud, String cp) {
        LatLng newDir = new LatLng(latitud, longitud);

        ptos.add(newDir);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newDir, zoom));
        mMap.addMarker(new MarkerOptions().position(newDir).snippet(cp).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).
                setTitle(db.cantPedidosPorCP(cp) + " orders");

        if (ptos.size() > 1) {
            PolylineOptions aa = new PolylineOptions().geodesic(true).color(Color.rgb(217, 133, 59));
            aa.addAll(ptos);
            mMap.addPolyline(aa);
        }
    }

    @Override
    public void finish() {
        Intent intentError = new Intent();
        if (error1) {
            intentError.putExtra("error", "No hay ningua localización");
            setResult(RESULT_OK, intentError);
        } else if (error2) {
            intentError.putExtra("error", "No hay conexión");
            setResult(RESULT_OK, intentError);
        } else if (error3) {
            intentError.putExtra("error", "El mapa no se puede mostrar");
            setResult(RESULT_OK, intentError);
        }
        super.finish();
    }
}