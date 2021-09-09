package gov.zndev.reviewlogclient.services;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.http.GET;



public interface TableUpdateApi {

    @GET("api/table_updates/view")
    Call<RequestResponse> getUpdates();
}
