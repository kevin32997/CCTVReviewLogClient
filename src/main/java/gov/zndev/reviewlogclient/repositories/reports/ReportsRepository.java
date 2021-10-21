package gov.zndev.reviewlogclient.repositories.reports;

import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class ReportsRepository {

    private Retrofit retrofit;
    private ReportsApi reportsApi;

    public ReportsRepository() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigProperties.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reportsApi = retrofit.create(ReportsApi.class);
    }

    /*==================================================================================================================
                                                     GET REQUEST
    ==================================================================================================================*/

    public void getReviewsBetweenDates(String dateFrom,String dateUntil, RepoInterface delegate) {

            try {

                Call<RequestResponse> call = reportsApi.getReviewsBetweenDates(dateFrom,dateUntil);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {
                            delegate.activityDone(true, "Request Successful.", Helper.CastToReviewLogList(response.body().getList()));
                        } else {
                            delegate.activityDone(false, "Server not responding.\nError Code: " + response.code(), null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestResponse> call, Throwable t) {
                        if (t instanceof ConnectException) {
                            delegate.activityDone(false, "Cannot connect to server, Please check your connection.", null);
                        } else if (t instanceof SocketTimeoutException) {
                            delegate.activityDone(false, "Cannot connect to server. Please try again later. ", null);
                        } else {
                            delegate.activityDone(false, "An Error has occurred! \nError: " + t.getMessage(), null);
                            t.printStackTrace();
                        }
                    }

                });
            } catch (Exception ex) {
                delegate.activityDone(false, "An Error has occurred! \nError: " + ex.getMessage(), null);
            }
    }


    public void getReviewByMonthAndYear(int month,int year, RepoInterface delegate) {

        try {

            Call<RequestResponse> call = reportsApi.getReviewsByMonthAndYear(month,year);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {
                        delegate.activityDone(true, "Request Successful.", Helper.CastToReviewLogList(response.body().getList()));
                    } else {
                        delegate.activityDone(false, "Server not responding.\nError Code: " + response.code(), null);
                    }
                }

                @Override
                public void onFailure(Call<RequestResponse> call, Throwable t) {
                    if (t instanceof ConnectException) {
                        delegate.activityDone(false, "Cannot connect to server, Please check your connection.", null);
                    } else if (t instanceof SocketTimeoutException) {
                        delegate.activityDone(false, "Cannot connect to server. Please try again later. ", null);
                    } else {
                        delegate.activityDone(false, "An Error has occurred! \nError: " + t.getMessage(), null);
                        t.printStackTrace();
                    }
                }

            });
        } catch (Exception ex) {
            delegate.activityDone(false, "An Error has occurred! \nError: " + ex.getMessage(), null);
        }
    }



}
