package com.example.cheahhong.dbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProductActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_product);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_product;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.action_home;
    }
}
