package id.slametriyadi.ojekkece.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.slametriyadi.ojekkece.R;
import id.slametriyadi.ojekkece.helper.MyContants;
import id.slametriyadi.ojekkece.model.ResponseCheckBooking;
import id.slametriyadi.ojekkece.network.InitRetrofit;
import id.slametriyadi.ojekkece.network.RestApi;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindDriverActivity extends AppCompatActivity {

    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.buttoncancel)
    Button buttoncancel;
    private int idBooking;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finddriver);
        ButterKnife.bind(this);
        Intent i = getIntent();

        idBooking = i.getIntExtra(MyContants.IDBOOKING, 0);
        checkBooking();
        timer = new Timer();
    }

    private void checkBooking() {
        RestApi api = InitRetrofit.getInstance();
        Call<ResponseCheckBooking> checkBookingCall = api.checkBooking(String.valueOf(idBooking));
        checkBookingCall.enqueue(new Callback<ResponseCheckBooking>() {
            @Override
            public void onResponse(Call<ResponseCheckBooking> call, Response<ResponseCheckBooking> response) {
                if (response.isSuccessful()) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        String idDriver = response.body().getDriver();
                        Log.d("IDDRIVER", response.body().getDriver());
                        Intent i = new Intent(FindDriverActivity.this, DetailPosisiDriverActivity.class);
                        i.putExtra(MyContants.IDDRIVER, idDriver);
                        Toast.makeText(FindDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(FindDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseCheckBooking> call, Throwable t) {
                Toast.makeText(FindDriverActivity.this, "Gagal Koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.buttoncancel)
    public void onViewClicked() {
        actionCancel();
    }

    private void actionCancel() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkBooking();
            }
        }, 0 , 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
