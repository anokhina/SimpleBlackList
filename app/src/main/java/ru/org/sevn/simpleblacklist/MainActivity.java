/*
 * Copyright 2016 Veronica Anokhina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.org.sevn.simpleblacklist;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import ru.org.sevn.simpleblacklist.fragment.IncomingCallFragment;
import ru.org.sevn.simpleblacklist.fragment.OutgoingCallFragment;
import ru.org.sevn.simpleblacklist.fragment.PhoneInfoFragment;
import ru.org.sevn.simpleblacklist.fragment.SmsFragment;
import ru.org.sevn.simpleblacklist.fragment.SmsOutFragment;
import ru.org.sevn.simpleblacklist.fragment.Titled;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public Activity getContext() {
        return this;
    }

    private AppHelper appHelper;
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;

    public AppHelper getAppHelper() {
        return appHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appHelper = new AppHelper(getContext());
        appHelper.reinit();
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setCurrentItem(mAppSectionsPagerAdapter.getDefaultItemIndex());

        initActionBar();
        startSmsService();
        //createCallReceiver();
    }
    private void startSmsService() {
        getContext().startService(new Intent(getContext(), SmsService.class));
    }
    private void initActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) return;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.CYAN));
        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    CallReceiver callReceiver = null;

    private void createCallReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        callReceiver = new CallReceiver();
        registerReceiver(callReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aExportDb:
                appHelper.exportDB(new ViewActivityWrapper(this));
                return true;
            case R.id.aImportDb:
                break;
            case R.id.aPhoneInfo:
                appHelper.phoneInfo(new ViewActivityWrapper(this));
                break;
            case R.id.aReInit:
                appHelper.reinitialize();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mmenu, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        private final IncomingCallFragment incomingCallFragment = new IncomingCallFragment();
        private final OutgoingCallFragment outgoingCallFragment = new OutgoingCallFragment();
        private final SmsFragment smsFragment = new SmsFragment();
        private final SmsOutFragment smsOutFragment = new SmsOutFragment();
        private final PhoneInfoFragment phoneInfoFragment = new PhoneInfoFragment();

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getDefaultItemIndex() {
            return 1;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return phoneInfoFragment;
                case 1:
                    return incomingCallFragment;
                case 2:
                    return smsFragment;
                case 3:
                    return outgoingCallFragment;
                case 4:
                    return smsOutFragment;
                default:
                    return phoneInfoFragment;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((Titled)getItem(position)).getTitle();
        }
    }
}
