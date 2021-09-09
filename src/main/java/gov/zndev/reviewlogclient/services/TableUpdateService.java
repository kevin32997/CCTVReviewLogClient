package gov.zndev.reviewlogclient.services;


import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;

import gov.zndev.reviewlogclient.models.other.RequestResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class TableUpdateService {

    private Retrofit retrofit;
    private TableUpdateApi updateApi;

    public TableUpdateService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ResourceHelper.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        updateApi = retrofit.create(TableUpdateApi.class);
    }

    /*==================================================================================================================
                                                       GET REQUEST
    ==================================================================================================================*/
    public void getUpdates(UpdateServiceResponseInterface delegate) {
        new Thread(() -> {
            Call<RequestResponse> call = updateApi.getUpdates();
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {
                        delegate.updates(response.message(), true, Helper.CastToUpdatesList(response.body().getList()));
                    } else {
                        delegate.updates("Server not responding.\nError Code: " + response.code(), false, null);
                    }
                }
                @Override
                public void onFailure(Call<RequestResponse> call, Throwable t) {
                    if (t instanceof ConnectException) {
                        delegate.updates("Cannot connect to server, Please check your connection.", false, null);
                    } else if (t instanceof SocketTimeoutException) {
                        delegate.updates("Cannot connect to server. Please try again later. ", false, null);
                    } else {
                        delegate.updates("An Error has occurred! \nError: " + t.getMessage(), false, null);
                        t.printStackTrace();
                    }
                }
            });
        }).start();
    }

}
