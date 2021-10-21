package gov.zndev.reviewlogclient.repositories.reports;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReportsApi {

    @GET("api/report/review/date_range/{date_from}/{date_until}")
    Call<RequestResponse> getReviewsBetweenDates(
            @Path("date_from") String dateFrom,
            @Path("date_until") String dateUntil
    );


    @GET("api/report/review/month_year/{month}/{year}")
    Call<RequestResponse> getReviewsByMonthAndYear(
            @Path("month") int month,
            @Path("year") int year
    );

}
