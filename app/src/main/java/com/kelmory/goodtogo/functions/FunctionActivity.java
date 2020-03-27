package com.kelmory.goodtogo.functions;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.kelmory.goodtogo.R;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class FunctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                this, getSupportFragmentManager());

        // Use view pager to present different segments;
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        // Use tab layout to show tab names
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
}