package gov.zndev.reviewlogclient.repositories.reviewlogs;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ReviewLogsApi {

    @POST("api/review_log/add")
    @Multipart
    Call<RequestResponse> createReviewLog(
            @Part("personnelId") RequestBody personnelId,
            @Part("inclusiveDates") RequestBody inclDates,
            @Part("reviewerId") RequestBody reviewerId,
            @Part("reviewerName") RequestBody reviewer
    );


    @GET("api/review_log/count")
    Call<Map<String, Long>> getReviewLogCount();


    @GET("api/review_log/count_user/{user_id}")
    Call<Map<String, Long>> getReviewLogCount(
            @Path("user_id") int user_id
    );


    @GET("api/review_log/count_personnel/{per_id}")
    Call<Map<String, Long>> getReviewLogCountByPersonnelId(
            @Path("per_id") int per_id
    );


    @GET("api/review_log/page/{page}/{size}/sort/{sortType}/{sortBy}")
    Call<RequestResponse> getReviewByPageSorted(
            @Path("page") int page,
            @Path("size") int size,
            @Path("sortType") String sortype,
            @Path("sortBy") String sortBy
    );

    @GET("api/review_log/page_user/{user_id}/{page}/{size}/sort/{sortType}/{sortBy}")
    Call<RequestResponse> getReviewByPageAndUserSorted(
            @Path("user_id") int user_id,
            @Path("page") int page,
            @Path("size") int size,
            @Path("sortType") String sortype,
            @Path("sortBy") String sortBy
    );

    @GET("api/review_log/page_personnel/{per_id}/{page}/{size}/sort/{sortType}/{sortBy}")
    Call<RequestResponse> getReviewByPageAndPersonnelSorted(
            @Path("per_id") int per_id,
            @Path("page") int page,
            @Path("size") int size,
            @Path("sortType") String sortype,
            @Path("sortBy") String sortBy
    );

    @GET("api/review_log/search/{search}/{size}")
    Call<RequestResponse> searchReviewLog(
            @Path("search") String search,
            @Path("size") int size
    );

    @GET("api/review_log/view/{review_id}")
    Call<RequestResponse> getReviewById(
            @Path("review_id") int reviewId
    );

    @PUT("api/review_log/update/{review_id}")
    @Multipart
    Call<RequestResponse> updateReviewInclusiveDates(
            @Path("review_id") int incidentId,
            @Part("inclusiveDates") RequestBody inclusiveDates
    );
}
