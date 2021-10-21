package gov.zndev.reviewlogclient.repositories.reviewlogs;

import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.ReviewLog;
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

public class ReviewLogsRepository {
    private Retrofit retrofit;
    private ReviewLogsApi reviewLogsApi;

    public ReviewLogsRepository() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigProperties.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reviewLogsApi = retrofit.create(ReviewLogsApi.class);
    }

    /*==================================================================================================================
                                                      POST REQUEST
    ==================================================================================================================*/

    public void createReviewLog(ReviewLog review, RepoInterface delegate) {
        new Thread(() -> {
            RequestBody personnelId = RequestBody.create("" + review.getPersonnelId(), MediaType.parse("text/plain"));
            RequestBody inclDates = RequestBody.create(review.getInclusiveDates(), MediaType.parse("text/plain"));
            RequestBody reviewerId = RequestBody.create("" + review.getReviewerId(), MediaType.parse("text/plain"));
            RequestBody reviewerName = RequestBody.create(review.getReviewerName(), MediaType.parse("text/plain"));

            Call<RequestResponse> call = reviewLogsApi.createReviewLog(personnelId, inclDates, reviewerId, reviewerName);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                    if (response.code() == 200) {


                        delegate.activityDone(response.body().getSuccess(), response.body().getMessage(), Helper.CastToReviewLogList(response.body().getList()));
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

    // Get Single


    // By Page ////////////////////////////////////

    public void getReviewLogById(int id, RepoInterface delegate) {
        new Thread(() -> {
            try {


                Call<RequestResponse> call = reviewLogsApi.getReviewById(id);
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }

    // Counts //////////////////////////

    public void getReviewLogCount(RepoInterface delegate) {
        new Thread(() -> {
            Call<Map<String, Long>> call = reviewLogsApi.getReviewLogCount();
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

    // Count By UserId//////////////////////////

    public void getReviewLogCountByUserId(int user_id,RepoInterface delegate) {
        new Thread(() -> {
            Call<Map<String, Long>> call = reviewLogsApi.getReviewLogCount(user_id);
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

    public void getReviewLogCountByPersonnelId(int per_id,RepoInterface delegate) {
        new Thread(() -> {
            Call<Map<String, Long>> call = reviewLogsApi.getReviewLogCountByPersonnelId(per_id);
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


    // By Page ////////////////////////////////////

    public void getReviewsByPageSorted(int page, int size, Sort sort, RepoInterface delegate) {
        new Thread(() -> {
            try {


                Call<RequestResponse> call = reviewLogsApi.getReviewByPageSorted(page, size, sort.getSortType(), sort.getSortBy());
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }

    // By Page and User ////////////////////////////////////

    public void getReviewsByPageAndUserSorted(int user_id, int page, int size, Sort sort, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = reviewLogsApi.getReviewByPageAndUserSorted(user_id, page, size, sort.getSortType(), sort.getSortBy());
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }


    // By Page and User ////////////////////////////////////

    public void getReviewsByPageAndPersonnelSorted(int per_id, int page, int size, Sort sort, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = reviewLogsApi.getReviewByPageAndPersonnelSorted(per_id, page, size, sort.getSortType(), sort.getSortBy());
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }

    // Search  ///////////////////////////////////

    public void searchReviewLogByPersonnel(String search, int size, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = reviewLogsApi.searchReviewLogByPersonnel(search, size);
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }

    public void searchReviewLogById(int search, int size, RepoInterface delegate) {
        new Thread(() -> {
            try {
                Call<RequestResponse> call = reviewLogsApi.searchReviewLogById(search, size);
                call.enqueue(new Callback<RequestResponse>() {
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
        }).start();
    }

    /*==================================================================================================================
                                                      PUT REQUEST
    ==================================================================================================================*/

    public void updateReviewLogInclusiveDates(int id, ReviewLog details, RepoInterface delegate) {
        new Thread(() -> {
            try {

                RequestBody inclusiveDates = RequestBody.create(details.getInclusiveDates(), MediaType.parse("text/plain"));

                Call<RequestResponse> call = reviewLogsApi.updateReviewInclusiveDates(id, inclusiveDates);
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

        }).start();
    }


}
