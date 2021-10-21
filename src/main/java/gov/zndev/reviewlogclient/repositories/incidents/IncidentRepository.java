package gov.zndev.reviewlogclient.repositories.incidents;

import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.RequestResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

public class IncidentRepository {
    private Retrofit retrofit;
    private IncidentApi incidentApi;

    public IncidentRepository() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigProperties.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        incidentApi = retrofit.create(IncidentApi.class);
    }

    /*==================================================================================================================
                                                      POST REQUEST
    ==================================================================================================================*/

    public void createIncident(Incident incident, RepoInterface delegate) {
        new Thread(() -> {

            RequestBody reviewLogId = RequestBody.create("" + incident.getReviewLogId(), MediaType.parse("text/plain"));
            RequestBody day = RequestBody.create(incident.getDay(), MediaType.parse("text/plain"));
            RequestBody time = RequestBody.create(incident.getTime(), MediaType.parse("text/plain"));
            RequestBody description = RequestBody.create(incident.getDescription(), MediaType.parse("text/plain"));

            Call<RequestResponse> call = incidentApi.createIncident(reviewLogId, day, time, description);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {


                        delegate.activityDone(response.body().getSuccess(), response.body().getMessage(), Helper.CastToIncidentList(response.body().getList()));
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
        }).start();
    }

    /*==================================================================================================================
                                                       GET REQUEST
    ==================================================================================================================*/

    public void getIncidentByReviewLog(int review_id, RepoInterface delegate) {
        new Thread(() -> {
            Call<RequestResponse> call = incidentApi.getIncidentByReviewLog(review_id);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {
                        delegate.activityDone(response.body().getSuccess(), response.body().getMessage(), Helper.CastToIncidentList(response.body().getList()));
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
        }).start();
    }


    public void getIncidentCountByReviewId(int review_id, RepoInterface delegate) {
        new Thread(() -> {
            Call<Map<String, Long>> call = incidentApi.getIncidentCountByReviewLog(review_id);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {

                    if (response.code() == 200) {
                        Map<String, Long> responseBody = response.body();
                        int count = responseBody.get("count").intValue();
                        delegate.activityDone(true, "Review Logs Counted.", count);
                    } else {
                        delegate.activityDone(false, "Server Error.\nError Code: " + response.code(), null);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Long>> call, Throwable t) {
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

        }).start();

    }

    public void awaitGetIncidentCountByReviewId(int review_id, RepoInterface delegate) {

            Call<Map<String, Long>> call = incidentApi.getIncidentCountByReviewLog(review_id);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {

                    if (response.code() == 200) {
                        Map<String, Long> responseBody = response.body();
                        int count = responseBody.get("count").intValue();
                        delegate.activityDone(true, "Review Logs Counted.", count);
                    } else {
                        delegate.activityDone(false, "Server Error.\nError Code: " + response.code(), null);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Long>> call, Throwable t) {
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



    }

    /*==================================================================================================================
                                                      PUT REQUEST
    ==================================================================================================================*/

    public void updateIncident(int id, Incident details, RepoInterface delegate) {
        new Thread(() -> {
            try {

                RequestBody day = RequestBody.create(details.getDay(), MediaType.parse("text/plain"));
                RequestBody time = RequestBody.create(details.getTime(), MediaType.parse("text/plain"));
                RequestBody reviewLogId = RequestBody.create("" + details.getReviewLogId(), MediaType.parse("text/plain"));
                RequestBody description = RequestBody.create(details.getDescription(), MediaType.parse("text/plain"));


                Call<RequestResponse> call = incidentApi.updateIncident(id, day, time, reviewLogId, description);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {
                            delegate.activityDone(true, "Request Successful.", Helper.CastToIncidentList(response.body().getList()));
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

        }).start();
    }

    /*==================================================================================================================
                                                      PUT REQUEST
    ==================================================================================================================*/

    public void deleteIncident(int id, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = incidentApi.deleteIncident(id);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {
                            delegate.activityDone(true, "Request Successful.", Helper.CastToIncidentList(response.body().getList()));
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

        }).start();
    }

}
