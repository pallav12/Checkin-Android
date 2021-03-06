package com.checkin.app.checkin.session;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.SessionBriefModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class SessionRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static SessionRepository INSTANCE;

    private SessionRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<QRResultModel>> newCustomerSession(final ObjectNode data) {
        return new NetworkBoundResource<QRResultModel, QRResultModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<QRResultModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postNewCustomerSession(data));
            }

            @Override
            protected void saveCallResult(QRResultModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RecentCheckinModel>> getRecentCheckins(final long shopId) {
        return new NetworkBoundResource<RecentCheckinModel, RecentCheckinModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecentCheckinModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRecentCheckins(shopId));
            }

            @Override
            protected void saveCallResult(RecentCheckinModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<SessionBriefModel>> getSessionBriefDetail(final long sessionPk) {
        return new NetworkBoundResource<SessionBriefModel, SessionBriefModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<SessionBriefModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionBriefDetail(sessionPk));
            }

            @Override
            protected void saveCallResult(SessionBriefModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrders(long sessionId) {
        return new NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionOrderedItemModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionOrders(sessionId));
            }

            @Override
            protected void saveCallResult(List<SessionOrderedItemModel> data) {
            }
        }.getAsLiveData();
    }
    public LiveData<Resource<List<WaiterEventModel>>> getSessionEvents(long sessionId) {
        return new NetworkBoundResource<List<WaiterEventModel>, List<WaiterEventModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<WaiterEventModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getManagerSessionEvents(sessionId));
            }

            @Override
            protected void saveCallResult(List<WaiterEventModel> data) {
            }
        }.getAsLiveData();
    }

    public static SessionRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (SessionRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
