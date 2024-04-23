package com.bus_tours_ex.apps.bustours.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bus_tours_ex.apps.bustours.R;
import com.google.android.material.tabs.TabLayout;

public class AuthActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.loginSignUpViewPager);

        loadViewPager();

    }

    private void loadViewPager(){

        tabLayout.addTab(tabLayout.newTab().setText("Log in"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        SliderAdapter myAdapter = new SliderAdapter(getSupportFragmentManager(), getLifecycle());
        myAdapter.addFragment(new LoginFragment());
        myAdapter.addFragment(new SignupFragment());
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(myAdapter);
    }

}