package com.rcoem.project.skymeet;

/**
 * Created by Akshat Shukla on 12-02-2017.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private View navHeader;

    FloatingActionButton fabplus;

    FirebaseAuth mAuth;
    private DatabaseReference mCurrentUser;
    private DatabaseReference mDatabaseDepartment;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button add;

    DrawerLayout di;
    long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabplus = (FloatingActionButton)findViewById(R.id.main_fab);

        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        di = (DrawerLayout) findViewById(R.id.drawer_layout_account);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_account);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                // Handle navigation view item clicks here.

                if (id == R.id.nav_home) {
                    //goto main Notice Feed
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                } else if (id == R.id.nav_profile) {
                    //goto Profile Setup activity
                    //startActivity(new Intent(getApplicationContext(),EditViewProfile.class));


                } else if (id == R.id.nav_meetings) {
                    //goto Block User Panel activity
                    //startActivity(new Intent(getApplicationContext(),BlockUserPlanel.class));

                } else if (id == R.id.nav_logout) {
                    //Log out from account
                    mAuth.signOut();
                    Snackbar snackbar = Snackbar
                            .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                } else if (id == R.id.nav_about_us) {
                    //startActivity(new Intent(getApplicationContext(),ActivitySendPushNotification.class));
                    startActivity(new Intent(getApplicationContext(),AboutUs.class));

                } DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_account);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        loadNavHeader();


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListFragment(), "List View");
        adapter.addFragment(new EditProfileFragment(), "Profile View");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    //Method to load current user details in the Navigation Bar header

    private void loadNavHeader() {
        // name, website
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtName.setText(dataSnapshot.child("name").getValue().toString().trim());

                Glide.with(getApplicationContext()).load(dataSnapshot.child("images").getValue().toString())
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });

        //Set the current user email-id
        txtWebsite.setText(mAuth.getCurrentUser().getEmail());

        //Loading header background image
        Glide.with(this).load(R.drawable.navmenuheaderbg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);


        //COde to show dot next to a particular notifications label
        //navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    //Navigation Bar

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //goto main Notice Feed
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

        } else if (id == R.id.nav_profile) {
            //goto Profile Setup activity
            //startActivity(new Intent(getApplicationContext(),EditViewProfile.class));


        } else if (id == R.id.nav_meetings) {
            //goto Block User Panel activity
            //startActivity(new Intent(getApplicationContext(),BlockUserPlanel.class));

        } else if (id == R.id.nav_logout) {
            //Log out from account
            mAuth.signOut();
            Snackbar snackbar = Snackbar
                    .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
            snackbar.show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        } else if (id == R.id.nav_about_us) {
            //startActivity(new Intent(getApplicationContext(),ActivitySendPushNotification.class));
            //startActivity(new Intent(getApplicationContext(),AboutUs.class));

        } DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_account);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_account);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (back_pressed + 3000 > System.currentTimeMillis()){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                back_pressed = System.currentTimeMillis();

                Snackbar snackbar = Snackbar
                        .make(di, R.string.backpress, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        }
    }
}

