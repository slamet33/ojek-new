package id.slametriyadi.ojekkece.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.slametriyadi.ojekkece.R;
import id.slametriyadi.ojekkece.helper.MyContants;
import id.slametriyadi.ojekkece.helper.SessionManager;
import id.slametriyadi.ojekkece.model.DataDetailDriver;
import id.slametriyadi.ojekkece.model.ResponseDetailDriver;
import id.slametriyadi.ojekkece.network.InitRetrofit;
import id.slametriyadi.ojekkece.network.RestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPosisiDriverActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.lokasiawal)
    TextView lokasiawal;
    @BindView(R.id.lokasitujuan)
    TextView lokasitujuan;
    @BindView(R.id.txtnamadriver)
    TextView txtnamadriver;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.txthpdriver)
    TextView txthpdriver;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    private GoogleMap mMap;
    private String iddriver;
    private LatLng posisidriver;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_posisi_driver);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        iddriver = getIntent().getStringExtra(MyContants.IDDRIVER);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getDetailDriver(mMap);
        manager = new SessionManager(this);
    }

    private void getDetailDriver(final GoogleMap mMap) {
        final ProgressDialog dialog = ProgressDialog.show(this, "get data driver", "loading...");
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseDetailDriver> call = api.cekDetailDriver(iddriver);
        call.enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    List<DataDetailDriver> detailDrivers = response.body().getData();
                    txtnamadriver.setText(detailDrivers.get(0).getUserNama());
                    txthpdriver.setText(detailDrivers.get(0).getUserHp());

                    Double lat = Double.parseDouble(detailDrivers.get(0).getTrackingLat());
                    Double lon = Double.parseDouble(detailDrivers.get(0).getTrackingLng());
                    posisidriver = new LatLng(lat, lon);

                    mMap.addMarker(new MarkerOptions().position(posisidriver).title("Your Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_driver)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(posisidriver));
                    // menampilkan control zoom in zoom out
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    // menampilkan compas
                    mMap.getUiSettings().setCompassEnabled(true);
                    // mengatur jenis peta
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    //padding maps
                    mMap.setPadding(40, 150, 50, 120);

                    //auto zoom
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisidriver, 16));

                    //button current location
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Toast.makeText(DetailPosisiDriverActivity.this, "Apaan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {
                Toast.makeText(DetailPosisiDriverActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.txthpdriver)
    public void onViewClicked() {
        Intent telfon = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txthpdriver.getText().toString()));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(telfon);
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
