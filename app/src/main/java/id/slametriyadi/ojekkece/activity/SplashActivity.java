package id.slametriyadi.ojekkece.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import id.slametriyadi.ojekkece.MainActivity;
import id.slametriyadi.ojekkece.R;
import id.slametriyadi.ojekkece.helper.SessionManager;


public class SplashActivity extends AppCompatActivity {

    SessionManager sesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sesi = new SessionManager(SplashActivity.this);

        //sebelum pindah ke halaman ke login atau ke mainactivity kita delay

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sesi.isLogin()) {
                    //arahkan mainactivity
                    Intent i = new Intent(SplashActivity.this, MapsActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    //arahkan ke login class

                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            //waktu delaynya 4000 ms = 4 s
        }, 3000);


    }
}
