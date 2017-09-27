package com.example.cheahhong.dbest;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String inquiryID,inquiryName,lastMsgID;
    int unRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(getIntent().getStringExtra("inquiry_Name"));
        }

        inquiryID = getIntent().getStringExtra("inquiry_Ref");
        inquiryName = getIntent().getStringExtra("inquiry_Name");
        lastMsgID = getIntent().getStringExtra("last_MessageID");
        unRead = getIntent().getIntExtra("unread_Count",0);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ChatFragment.newInstance(inquiryID,inquiryName,lastMsgID,unRead),"Chat");
        adapter.addFragment(InquiryFragment.newInstance(inquiryID),"Inquiry");
        adapter.addFragment(QuotationFragment.newInstance(inquiryID,inquiryName),"Quotation");
        viewPager.setAdapter(adapter);
        Log.d("amount of fragment", Integer.toString(adapter.getCount()));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}