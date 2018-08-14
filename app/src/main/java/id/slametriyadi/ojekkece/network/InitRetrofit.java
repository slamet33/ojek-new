package id.slametriyadi.ojekkece.network;

import id.slametriyadi.ojekkece.helper.MyContants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitRetrofit {
    //inizialisasi sebuah library untuk accesss data dari server
    //library yang digunakan adalah retrofit dari square

    public static Retrofit setInit() {

        return new Retrofit.Builder().baseUrl(MyContants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
    public static RestApi getInstance(){

        return setInit().create(RestApi.class);


    }

    public static Retrofit setInitGoogle() {

        return new Retrofit.Builder().baseUrl(MyContants.BASE_MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static RestApi getInstanceGoogle() {

        return setInitGoogle().create(RestApi.class);


    }
}
