package com.example.hfilproject;

import android.os.Bundle;
import android.view.Menu;


import java.util.UUID;

public class ScanActivity extends BleProfileServiceReadyActivity<HTService.HTSBinder> {


    @Override
    protected void onCreateView(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);

    }

    @Override
    protected void setDefaultUI() {

    }

    @Override
    protected int getDefaultDeviceName() {
        return 0;
    }

    @Override
    protected int getAboutTextId() {
        return 0;
    }

    @Override
    protected UUID getFilterUUID() {
        return HTManager.HT_SERVICE_UUID;

    }

    @Override
    protected void onInitialize(final Bundle savedInstanceState) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onServiceBound(HTService.HTSBinder binder) {

    }

    @Override
    protected void onServiceUnbound() {

    }

    @Override
    protected Class<? extends BleProfileService> getServiceClass() {
        return HTService.class;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
}
