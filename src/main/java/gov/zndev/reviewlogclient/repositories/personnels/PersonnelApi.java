package gov.zndev.reviewlogclient.repositories.personnels;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface PersonnelApi {

    @POST("api/personnel/add")
    @Multipart
    Call<RequestResponse> createPersonnel(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("middleName") RequestBody middleName,
            @Part("office") RequestBody office,
            @Part("position") RequestBody position,
            @Part("addedBy") RequestBody addedBy
    );


    @GET("api/personnel/count")
    Call<Map<String, Long>> getPersonnelCount();


    @GET("api/personnel/page/{page}/{size}/sort/{sortType}/{sortBy}")
    Call<RequestResponse> getPersonnelByPageSorted(
            @Path("page") int page,
            @Path("size") int size,
            @Path("sortType") String sortType,
            @Path("sortBy") String sortBy
    );

    @GET("api/personnel/search/{search}/{size}")
    Call<RequestResponse> searchPersonnel(
            @Path("search") String search,
            @Path("size") int size
    );

    @PUT("api/personnel/update/{personnel_id}")
    @Multipart
    Call<RequestResponse> updatePersonnel(
            @Path("personnel_id") int personnel_id,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("middleName") RequestBody middleName,
            @Part("office") RequestBody office,
            @Part("position") RequestBody position
    );

    @GET("api/personnel/view/{personnel_id}")
    Call<RequestResponse> getPersonnelById(
            @Path("personnel_id") int personnelId
    );

}
