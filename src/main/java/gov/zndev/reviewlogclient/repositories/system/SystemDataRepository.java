package gov.zndev.reviewlogclient.repositories.system;

import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Streaming;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

public class SystemDataRepository {

    private Retrofit retrofit;
    private SystemDataApi systemDataApi;

    public SystemDataRepository() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigProperties.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        systemDataApi = retrofit.create(SystemDataApi.class);
    }

    /*==================================================================================================================
                                                      GET REQUEST
    ==================================================================================================================*/

    public void getConfig(String key, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<Map<String, String>> call = systemDataApi.getConfigByKey(key);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.code() == 200) {
                            Map<String, String> map = response.body();
                            delegate.activityDone(true, "Request Successful.", map);
                        } else {
                            delegate.activityDone(false, "Server not responding.\nError Code: " + response.code(), null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
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


    public void testConnection(String base_url, RepoInterface delegate) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(base_url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SystemDataApi systemDataApi = retrofit.create(SystemDataApi.class);

                Call<RequestResponse> call = systemDataApi.testConnection();
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {

                            delegate.activityDone(response.body().getSuccess(), response.body().getMessage(), null);
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
