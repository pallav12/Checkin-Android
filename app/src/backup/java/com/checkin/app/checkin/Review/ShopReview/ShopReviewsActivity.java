package com.checkin.app.checkin.Review.ShopReview;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;

/**
 * Created by ACER on 12/27/2018.
 */

public class ShopReviewsActivity extends BaseActivity {
    public static final String KEY_SHOP_PK = "shop.reviews";

    private ShopReviewViewModel mViewModel;
    private ShopReviewFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
            actionBar.setElevation(10);
        }

        mViewModel = ViewModelProviders.of(this).get(ShopReviewViewModel.class);
        setupReviews();
    }

    private void setupReviews() {
        mFragment = ShopReviewFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_shop_review, mFragment)
                .commit();

        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopReviews(shopPk);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
