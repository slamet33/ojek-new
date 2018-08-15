package id.slametriyadi.ojekkece.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.slametriyadi.ojekkece.R;
import id.slametriyadi.ojekkece.helper.DirectionMapsV2;
import id.slametriyadi.ojekkece.helper.GPSTracker;
import id.slametriyadi.ojekkece.helper.HeroHelper;
import id.slametriyadi.ojekkece.helper.MyContants;
import id.slametriyadi.ojekkece.helper.SessionManager;
import id.slametriyadi.ojekkece.model.ResponseInsertBooking;
import id.slametriyadi.ojekkece.model.WayPoints.Distance;
import id.slametriyadi.ojekkece.model.WayPoints.Duration;
import id.slametriyadi.ojekkece.model.WayPoints.Leg;
import id.slametriyadi.ojekkece.model.WayPoints.ModelWayPoints;
import id.slametriyadi.ojekkece.model.WayPoints.Polyline;
import id.slametriyadi.ojekkece.model.WayPoints.Route;
import id.slametriyadi.ojekkece.network.InitRetrofit;
import id.slametriyadi.ojekkece.network.RestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.slametriyadi.ojekkece.helper.MyContants.LOKASIAWAL;
import static id.slametriyadi.ojekkece.helper.MyContants.LOKASITUJUAN;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.imgpick)
    ImageView imgpick;
    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.txtharga)
    TextView txtharga;
    @BindView(R.id.txtjarak)
    TextView txtjarak;
    @BindView(R.id.txtdurasi)
    TextView txtdurasi;
    @BindView(R.id.requestorder)
    Button requestorder;
    @BindView(R.id.edtcatatan)
    EditText edtcatatan;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private double latawal;
    private double lonawal;
    private String namaLokasiAwal;
    private double latakhir;
    private double lonakhir;
    private String namaLokasiAkhir;
    private GPSTracker gpsTracker;
    private String name_location;
    private String points;
    private List<Polyline> polylines;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        manager = new SessionManager(MapsActivity.this);

        // Todo Cek GPS Aktif atau tidak
        cekGPS();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkPermission();
    }

    private void cekGPS() {
        // cek sttus gps aktif atau tidak
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        // Todo Location Already on  ... end
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        }
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, MyContants.REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gpsTracker = new GPSTracker(MapsActivity.this);
//        TODO CEK Permission > Marhmellow
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        110);
            }
            return;
        }
        if (gpsTracker.canGetLocation()) {
            //getkoordinat jika gps aktif
            latawal = gpsTracker.getLatitude();
            lonawal = gpsTracker.getLongitude();
            //ubah koordinat jadi nama lokasi
            name_location = posisiku(latawal, lonawal);
            lokasiawal.setText(name_location);
        }
        LatLng lokasiku = new LatLng(latawal, lonawal);
        mMap.addMarker(new MarkerOptions().position(lokasiku).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 14));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setPadding(30, 80, 30, 80);
    }

    private String posisiku(double latawal, double lonawal) {
        name_location = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latawal, lonawal, 1);
            if (list != null && list.size() > 0) {
                // Get DATA From Google Maps
                name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name_location;
    }

    @OnClick({R.id.imgpick, R.id.lokasiawal, R.id.lokasitujuan, R.id.requestorder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgpick:
                break;
            case R.id.lokasiawal:
                setLokasi(LOKASIAWAL);
                break;
            case R.id.lokasitujuan:
                setLokasi(LOKASITUJUAN);
                break;
            case R.id.requestorder:
                requestorderan();
                break;
        }
    }

    private void requestorderan() {

        int idUser = Integer.valueOf(manager.getIdUser());
        String awal = lokasiawal.getText().toString();
        String akhir = lokasitujuan.getText().toString();
        String ltawal = String.valueOf(latawal);
        String ltakhir = String.valueOf(latakhir);
        String lgawal = String.valueOf(lonawal);
        String lgakhir = String.valueOf(lonakhir);
        String catatan = edtcatatan.getText().toString();
        String token = manager.getToken().toString();
//        float jarak = Float.valueOf(txtjarak.getText().toString());
        float jarak1 = Float.valueOf(txtjarak.getText().toString().substring(0, txtjarak.getText().toString().indexOf("km")));

        String device = HeroHelper.getDeviceUUID(this).toString();

        final ProgressDialog alert = ProgressDialog.show(MapsActivity.this, "Proses request order", "loading");

        RestApi api = InitRetrofit.getInstance();
        Call<ResponseInsertBooking> insertBookingCall = api.getInsertBooking(
                idUser,
                ltawal,
                lgawal,
                ltakhir,
                lgakhir,
                akhir,
                catatan,
                jarak1,
                token,
                device,
                awal
                );

        insertBookingCall.enqueue(new Callback<ResponseInsertBooking>() {
            @Override
            public void onResponse(Call<ResponseInsertBooking> call, Response<ResponseInsertBooking> response) {
                if (response.isSuccessful()) {
                    alert.dismiss();
                    String status = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (status.equals("true")) {
                        int idbooking = response.body().getIdBooking();
                        Snackbar.make(rootlayout, msg, Snackbar.LENGTH_SHORT).show();
                        Intent i = new Intent(MapsActivity.this, FindDriverActivity.class);
                        i.putExtra(MyContants.IDBOOKING, idbooking);
                        startActivity(i);
                    } else {
                        Toast.makeText(MapsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(MapsActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertBooking> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage() + "AlA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLokasi(int lokasi) {
        // Filter Lokasi cari
        AutocompleteFilter filternegara = new AutocompleteFilter.Builder().setCountry("ID").build();

        Intent i = null;
        try {
            i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(filternegara).build(MapsActivity.this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

        startActivityForResult(i, lokasi);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOKASIAWAL) {
            if (resultCode == RESULT_OK && data != null) {
                Place p = PlaceAutocomplete.getPlace(this, data);
                latawal = p.getLatLng().latitude;
                lonawal = p.getLatLng().longitude;

                LatLng lokasi = new LatLng(latawal, lonawal);
                LatLng lokasiAkhir = new LatLng(latakhir, lonakhir);

                namaLokasiAwal = p.getAddress().toString();

                if (lokasiawal.getText().length() != 0) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(lokasi).title(namaLokasiAwal));
                    mMap.addMarker(new MarkerOptions().position(lokasiAkhir).title(namaLokasiAkhir));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 21));
//                    Data nama lokasi diset ke texview
                    lokasiawal.setText(namaLokasiAwal);
                    aksesroute();
                }
            }
        } else if (requestCode == LOKASITUJUAN) {
            if (resultCode == RESULT_OK && data != null) {
                Place p = PlaceAutocomplete.getPlace(this, data);
                latakhir = p.getLatLng().latitude;
                lonakhir = p.getLatLng().longitude;

                LatLng lokasi = new LatLng(latakhir, lonakhir);
                LatLng lokasiAwal = new LatLng(latawal, lonawal);
                namaLokasiAkhir = p.getAddress().toString();

                if (lokasiawal.getText().length() != 0) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(lokasi).title(namaLokasiAkhir));
                    mMap.addMarker(new MarkerOptions().position(lokasiAwal).title(namaLokasiAwal));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 21));
//                    Data nama lokasi diset ke texview
                lokasitujuan.setText(namaLokasiAkhir);
                aksesroute();
            }
        }
    }

    private void aksesroute() {
        String origin = String.valueOf(latawal) + "," + String.valueOf(lonawal);
        String destination = String.valueOf(latakhir) + "," + String.valueOf(lonakhir);

        LatLngBounds.Builder bound = LatLngBounds.builder();
        bound.include(new LatLng(latawal, lonawal));
        bound.include(new LatLng(latakhir, lonakhir));

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 16));

        RestApi api = InitRetrofit.getInstanceGoogle();
        Call<ModelWayPoints> modelWayPointsCall = api.getRouteLocation(origin, destination);
        modelWayPointsCall.enqueue(new Callback<ModelWayPoints>() {
            @Override
            public void onResponse(Call<ModelWayPoints> call, Response<ModelWayPoints> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("OK")) {
                        List<Route> routes = response.body().getRoutes();
                        List<Leg> legs = routes.get(0).getLegs();
                        Distance distance = legs.get(0).getDistance();
                        Duration duration = legs.get(0).getDuration();

                        txtdurasi.setText(duration.getText().toString());
                        txtjarak.setText(distance.getText().toString());

                        //Hitung Harga
                        double nilaiJarak = Double.valueOf(distance.getValue());
                        double harga = Math.ceil(nilaiJarak / 1000);
                        double total = harga * 1000;
                        txtharga.setText("Rp." + HeroHelper.toRupiahFormat2(String.valueOf(total)));

                        points = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        DirectionMapsV2 mapsV2 = new DirectionMapsV2(MapsActivity.this);
                        mapsV2.gambarRoute(mMap, points);
                    } else {
                        Toast.makeText(gpsTracker, "Invalid API" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(gpsTracker, "Response Gagal" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelWayPoints> call, Throwable t) {
                Toast.makeText(gpsTracker, "ONFAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history) {

        } else if (id == R.id.logout) {
            manager.logout();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
