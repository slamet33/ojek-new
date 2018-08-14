package id.slametriyadi.ojekkece;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.slametriyadi.ojekkece.activity.MapsActivity;
import id.slametriyadi.ojekkece.helper.HeroHelper;
import id.slametriyadi.ojekkece.helper.SessionManager;
import id.slametriyadi.ojekkece.model.Data;
import id.slametriyadi.ojekkece.model.ResponseDaftar;
import id.slametriyadi.ojekkece.model.ResponseMasuk;
import id.slametriyadi.ojekkece.network.InitRetrofit;
import id.slametriyadi.ojekkece.network.RestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        manager = new SessionManager(MainActivity.this);
    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                login();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    //TODO Proses Register
    private void register() {
        final AlertDialog.Builder dialogregis = new AlertDialog.Builder(this);
        dialogregis.setTitle(R.string.Register);
        dialogregis.setMessage(R.string.requireEmailPassword);

        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilRegister = inflater.inflate(R.layout.layout_register, null);
        final EditText edtEmail = tampilRegister.findViewById(R.id.edtEmail);
        final EditText edtPassword = tampilRegister.findViewById(R.id.edtPassword);
        final EditText edtName = tampilRegister.findViewById(R.id.edtName);
        final EditText edtPhone = tampilRegister.findViewById(R.id.edtPhone);
        dialogregis.setView(tampilRegister);
        dialogregis.setPositiveButton(R.string.Register, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //check validasi
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requireemial, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootlayout, R.string.minimumpassord, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtName.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirename, Snackbar.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirephone, Snackbar.LENGTH_SHORT).show();

                } else {
//                    Todo Memasukkan data
                    RestApi api = InitRetrofit.getInstance();
                    Call<ResponseDaftar> daftarCall = api.requestDaftar(
                            edtName.getText().toString(),
                            edtPassword.getText().toString(),
                            edtPhone.getText().toString(),
                            edtEmail.getText().toString()
                    );
                    daftarCall.enqueue(new Callback<ResponseDaftar>() {
                        @Override
                        public void onResponse(Call<ResponseDaftar> call, Response<ResponseDaftar> response) {
                            if (response.isSuccessful()) {
                                String result = response.body().getResult();
                                String msg = response.body().getMsg();
                                if (result.equals("true")) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseDaftar> call, Throwable t) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Check your connection! "+ t.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        dialogregis.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogregis.show();
    }

    //TODO Proses Login
    private void login() {
        AlertDialog.Builder dialoglogin = new AlertDialog.Builder(this);
        dialoglogin.setTitle("Login");
        dialoglogin.setMessage("Please Use Email and Password to Login");

        LayoutInflater inflater = LayoutInflater.from(this);
        View tampilLogin = inflater.inflate(R.layout.layout_login, null);
        final EditText edtEmail = tampilLogin.findViewById(R.id.edtEmail);
        final EditText edtPassword = tampilLogin.findViewById(R.id.edtPassword);
        dialoglogin.setView(tampilLogin);
        dialoglogin.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requireemial, Snackbar.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootlayout, R.string.requirepassword, Snackbar.LENGTH_SHORT).show();
                } else if (edtPassword.length() < 6){
                    Snackbar.make(rootlayout, R.string.minimumpassord, Snackbar.LENGTH_SHORT).show();
                } else{
                    RestApi api = InitRetrofit.getInstance();
                    String device = HeroHelper.getDeviceUUID(MainActivity.this);
                    Call<ResponseMasuk> masukCall = api.requestMasuk(
                            device,
                            edtEmail.getText().toString(),
                            edtPassword.getText().toString()
                    );
                    masukCall.enqueue(new Callback<ResponseMasuk>() {
                        @Override
                        public void onResponse(Call<ResponseMasuk> call, Response<ResponseMasuk> response) {
                            if (response.isSuccessful()){
                                String result = response.body().getResult();
                                String msg = response.body().getMsg();

                                if (result.equals("true")){
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    Data d = response.body().getData();
                                    manager.setIduser(d.getIdUser());
                                    manager.setEmail(d.getUserEmail());
                                    manager.setPhone(d.getUserHp());
                                    String token = response.body().getToken();
                                    manager.createLoginSession(token);
//                                    dialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseMasuk> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this,"Check Your Connection!" , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        dialoglogin.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialoglogin.show();
    }
}
