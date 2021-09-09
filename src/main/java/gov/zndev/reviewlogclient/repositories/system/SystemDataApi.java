package gov.zndev.reviewlogclient.repositories.system;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.Map;

public interface SystemDataApi {


    @GET("api/system_data/view/{key}")
    Call<Map<String,String>> getConfigByKey(@Path("key") String key);

    @GET("api/system_data/test_connection")
    Call<RequestResponse> testConnection();

}
