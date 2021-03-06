package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.Manifest;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopRepository;

/**
 * Created by Bhavik Patel on 19/08/2018.
 */

public class ShopProfileViewModel extends BaseViewModel {
    private final ShopRepository mRepository;

    private long mShopPk;
    private MediatorLiveData<Resource<RestaurantModel>> mShopData = new MediatorLiveData<>();

    public ShopProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopRepository.getInstance(application);
    }

    public LiveData<Resource<RestaurantModel>> getShopData() {
        return mShopData;
    }

    public void callShop(Context context) {
        Resource<RestaurantModel> shopResource = mShopData.getValue();
        if (shopResource == null || shopResource.data == null || shopResource.status != Resource.Status.SUCCESS)
            return;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;
        String phone = shopResource.data.getPhone();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    public void fetchShop(long shopPk) {
        mShopPk = shopPk;
        mShopData.addSource(mRepository.getShopModel(shopPk), mShopData::setValue);
    }

    public long getShopPk() {
        return mShopPk;
    }

    @Override
    public void updateResults() {
        fetchShop(mShopPk);
    }
}
