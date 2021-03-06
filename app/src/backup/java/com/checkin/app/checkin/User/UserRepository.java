package com.checkin.app.checkin.User;

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
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.User.Friendship.FriendshipModel;
import com.checkin.app.checkin.User.NonPersonalProfile.ShopCustomerModel;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UserRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static UserRepository INSTANCE = null;

    private UserRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<FriendshipModel>>> getSelfFriends(){
        return new NetworkBoundResource<List<FriendshipModel>, List<FriendshipModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<FriendshipModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSelfFriends());
            }

            @Override
            protected void saveCallResult(List<FriendshipModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<FriendshipModel>>> getUserFriends(long userPk){
        return new NetworkBoundResource<List<FriendshipModel>, List<FriendshipModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<FriendshipModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUserFriends(userPk));
            }

            @Override
            protected void saveCallResult(List<FriendshipModel> data) {

            }
        }.getAsLiveData();
    }
    public LiveData<Resource<ObjectNode>> removeFriend(long userPk){
        return new NetworkBoundResource<ObjectNode, ObjectNode>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.removeFriend(userPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> addFriend(ObjectNode data){
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.addFriend(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> acceptFriendRequest(String requestPk){
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.acceptFriendRequest(requestPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> cancelFriendRequest(String requestPk){
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.cancelFriendRequest(requestPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> rejectFriendRequest(String requestPk){
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.rejectFriendRequest(requestPk));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<UserModel>>> getAllUsers() {
        return new NetworkBoundResource<List<UserModel>, List<UserModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<UserModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUsers());
            }

            @Override
            protected void saveCallResult(List<UserModel> data) {
            }
        }.getAsLiveData();
    }

    /**
     *
     * @param userPk - 0 for self, non-zero for others.
     * @return
     */
    public LiveData<Resource<UserModel>> getUser(long userPk) {
        return new NetworkBoundResource<UserModel, UserModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserModel>> createCall() {
                if (userPk == 0) {
                    return new RetrofitLiveData<>(mWebService.getPersonalUser());
                } else {
                    return new RetrofitLiveData<>(mWebService.getNonPersonalUser(userPk));
                }
            }

            @Override
            protected void saveCallResult(UserModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postUserData(ObjectNode objectNode) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postUserData(objectNode));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public Call<GenericDetailModel> postUserProfilePic(File pic, ProgressRequestBody.UploadCallbacks listener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), pic);
        ProgressRequestBody requestBody = new ProgressRequestBody(requestFile, listener);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("profile_pic", "profile.jpg", requestBody);
        return mWebService.postUserProfilePic(body);
    }

    public LiveData<Resource<List<ShopCustomerModel>>> getUserCheckinById(long userId) {
        return new NetworkBoundResource<List<ShopCustomerModel>, List<ShopCustomerModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ShopCustomerModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUserCheckins(userId));
            }

            @Override
            protected void saveCallResult(List<ShopCustomerModel> data) {

            }
        }.getAsLiveData();
    }

    public static UserRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
