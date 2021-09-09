package gov.zndev.reviewlogclient.repositories.incidents;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface IncidentApi {

    @POST("api/incident/add")
    @Multipart
    Call<RequestResponse> createIncident(
            @Part("reviewLogId") RequestBody reviewLogId,
            @Part("day") RequestBody day,
            @Part("time") RequestBody time,
            @Part("incidentDescription") RequestBody incidentDescription
    );


    @GET("api/incident/count")
    Call<Map<String, Long>> getIncidentCount();

    @GET("api/incident/count_by_review_log/{review_log_id}")
    Call<Map<String, Long>> getIncidentCountByReviewLog(@Path("review_log_id") int id);


    @GET("api/incident/view_by_review_log/{review_log_id}")
    Call<RequestResponse> getIncidentByReviewLog(
            @Path("review_log_id") int id
    );


    @PUT("api/incident/update/{incident_id}")
    @Multipart
    Call<RequestResponse> updateIncident(
            @Path("incident_id") int incidentId,
            @Part("day") RequestBody day,
            @Part("time") RequestBody time,
            @Part("reviewLogId") RequestBody reviewLogId,
            @Part("incidentDescription") RequestBody incidentDescription
    );


    @DELETE("api/incident/delete/{incident_id}")
    Call<RequestResponse> deleteIncident(
            @Path("incident_id") int incidentId
    );
}
