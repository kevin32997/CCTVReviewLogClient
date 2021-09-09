package gov.zndev.reviewlogclient.repositories.users;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface UsersApi {

    @POST("api/users/add")
    @Multipart
    Call<RequestResponse> createUser(
            @Part("fullname") RequestBody fullname,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("userRole") RequestBody userRole
    );

    @GET("api/users/count")
    Call<Map<String,Long>> getUsersCount();


    @GET("api/users/view/{id}")
    Call<RequestResponse> getUserById(
            @Path("id") int id
    );

    @GET("api/users/page/{page}/{size}/sort/{sortType}/{sortBy}")
    Call<RequestResponse> getUserByPageSorted(
            @Path("page") int page,
            @Path("size") int size,
            @Path("sortType") String sortType,
            @Path("sortBy") String sortBy
    );

    @GET("api/users/check/{user_name}/{password}")
    Call<RequestResponse> checkUser(
            @Path("user_name") String username,
            @Path("password") String password
    );


    @PUT("api/user/update/{user_id}")
    @Multipart
    Call<RequestResponse> updateUser(
            @Path("user_id") int userId,
            @Part("fullname") RequestBody fullname,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("userRole") RequestBody userRole
    );
}
