package id.slametriyadi.ojekkece.network;

import id.slametriyadi.ojekkece.model.ResponseCheckBooking;
import id.slametriyadi.ojekkece.model.ResponseDaftar;
import id.slametriyadi.ojekkece.model.ResponseInsertBooking;
import id.slametriyadi.ojekkece.model.ResponseMasuk;
import id.slametriyadi.ojekkece.model.WayPoints.ModelWayPoints;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {

    @FormUrlEncoded
    @POST("daftar")
    Call<ResponseDaftar> requestDaftar(
            @Field("nama") String nama,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("login")
    Call<ResponseMasuk> requestMasuk(
            @Field("f_device") String device,
            @Field("f_email") String email,
            @Field("f_password") String password
    );

    //    todo End point ke api google maps
    @GET("json")
    Call<ModelWayPoints> getRouteLocation(
            @Query("origin") String alamatasal,
            @Query("destination") String alamattujuan

    );

    @FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseInsertBooking> getInsertBooking(
            @Field("f_idUser") String iduser,
            @Field("f_latAwal") String latawal,
            @Field("f_lngAwal") String longawal,
            @Field("f_awal") String awal,
            @Field("f_latAkhir") String latakhir,
            @Field("f_lngAkhir") String longakhir,
            @Field("f_akhir") String akhir,
            @Field("f_catatan") String catatan,
            @Field("f_jarak") String jarak,
            @Field("f_token") String token,
            @Field("f_device") String device
    );

    @FormUrlEncoded
    @POST("checkBooking")
    Call<ResponseCheckBooking> checkBooking(
            @Field("idbooking") String idBooking
    );
}
