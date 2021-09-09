package gov.zndev.reviewlogclient.repositories.personnels;

import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.models.other.RequestResponse;
import gov.zndev.reviewlogclient.models.other.Sort;
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

public class PersonnelRepo {

    private Retrofit retrofit;
    private PersonnelApi personnelApi;

    public PersonnelRepo() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ResourceHelper.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        personnelApi = retrofit.create(PersonnelApi.class);
    }

    /*==================================================================================================================
                                                      POST REQUEST
    ==================================================================================================================*/

    public void createdPersonnel(Personnel personnel, RepoInterface delegate) {
        new Thread(() -> {


            RequestBody firstName = RequestBody.create(personnel.getFirstName(), MediaType.parse("text/plain"));
            RequestBody lastName = RequestBody.create(personnel.getLastName(), MediaType.parse("text/plain"));
            RequestBody middleName = RequestBody.create(personnel.getMiddleName(), MediaType.parse("text/plain"));
            RequestBody office = RequestBody.create(personnel.getOffice(), MediaType.parse("text/plain"));
            RequestBody position = RequestBody.create(personnel.getPosition(), MediaType.parse("text/plain"));
            RequestBody addedBy = RequestBody.create("" + personnel.getAddedBy(), MediaType.parse("text/plain"));
            Call<RequestResponse> call = personnelApi.createPersonnel(firstName, lastName, middleName, office, position, addedBy);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {


                        delegate.activityDone(response.body().getSuccess(), response.body().getMessage(), Helper.CastToPersonnelList(response.body().getList()));
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

    // View By id Single ////////////////////////////////////

    public void getPersonnelById(int id, RepoInterface delegate) {
        new Thread(() -> {
            try {


                Call<RequestResponse> call = personnelApi.getPersonnelById(id);
                call.enqueue(new Callback<RequestResponse>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {
                            delegate.activityDone(true, "Request Successful.", Helper.CastToPersonnelList(response.body().getList()));
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

    // Counts //////////////////////////

    public void getPersonnelCount(RepoInterface delegate) {
        new Thread(() -> {
            Call<Map<String, Long>> call = personnelApi.getPersonnelCount();
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                    if (response.code() == 200) {
                        Map<String, Long> responseBody = response.body();
                        int count = responseBody.get("count").intValue();
                        delegate.activityDone(true, "Personnel Counted.", count);
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

    // By Page ////////////////////////////////////

    public void getPersonnelByPageSorted(int page, int size, Sort sort, RepoInterface delegate) {
        new Thread(() -> {
            try {


                Call<RequestResponse> call = personnelApi.getPersonnelByPageSorted(page, size, sort.getSortType(), sort.getSortBy());
                call.enqueue(new Callback<RequestResponse>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {
                            delegate.activityDone(true, "Request Successful.", Helper.CastToPersonnelList(response.body().getList()));
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


    // Search  ///////////////////////////////////

    public void searchPersonnel(String search, int size, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = personnelApi.searchPersonnel(search, size);
                call.enqueue(new Callback<RequestResponse>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {

                            delegate.activityDone(true, "Request Successful.", Helper.CastToPersonnelList(response.body().getList()));
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

    public void updatePersonnel(int id, Personnel details, RepoInterface delegate) {
        new Thread(() -> {
            try {
                RequestBody firstName = RequestBody.create(details.getFirstName(), MediaType.parse("text/plain"));
                RequestBody lastName = RequestBody.create(details.getLastName(), MediaType.parse("text/plain"));
                RequestBody middleName = RequestBody.create(details.getMiddleName(), MediaType.parse("text/plain"));
                RequestBody office = RequestBody.create(details.getOffice(), MediaType.parse("text/plain"));
                RequestBody position = RequestBody.create(details.getPosition(), MediaType.parse("text/plain"));
                Call<RequestResponse> call = personnelApi.updatePersonnel(id, firstName, lastName, middleName, office, position);
                call.enqueue(new Callback<RequestResponse>() {
                    @Override
                    public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                        if (response.code() == 200) {

                            delegate.activityDone(true, "Request Successful.", Helper.CastToPersonnelList(response.body().getList()));
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
