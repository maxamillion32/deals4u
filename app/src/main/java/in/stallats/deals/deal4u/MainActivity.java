package in.stallats.deals.deal4u;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    LinearLayout apperal_menu, auto_mobile, bars_pub, bouquests, dineouts, entertainment, electronics, hotels, petrol_bunks, sallon_spa, super_markets, wellness;
    ViewPager viewPager;
    customSwipe customSwipe;
    Session session;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mmtoolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
        }

        session = new Session(this);
        if (!session.loggedin()) {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            logout();
        }

        HashMap<String, String> user = session.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        String mobile = user.get("mobile");
        String id = user.get("id");

        Future<JsonArray> getFav = Ion.with(this)
                .load("http://dealsapi.stallats.org/api/favour/" + id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray resultArray) {
                        if (e != null) {

                        } else {
                            for(int n=0; n<resultArray.size(); n++){
                                String x = resultArray.get(n).toString();
                                try {
                                    final JSONObject xxx = new JSONObject(x);
                                    session.set_favourites(xxx.getString("store_id"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        }
                    }
                });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter adapter = new customSwipe(viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(3);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(50);
        viewPager.setPadding(100, 0, 100, 0);
        viewPager.setOffscreenPageLimit(1);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        TextView nav_user = (TextView) hView.findViewById(R.id.logged_usermobile);
        nav_user.setText(mobile);

        TextView view_name = (TextView) hView.findViewById(R.id.logged_username);
        view_name.setText(name);

        TextView view_email = (TextView) hView.findViewById(R.id.logged_useremail);
        view_email.setText(email);

        apperal_menu = (LinearLayout) findViewById(R.id.apperals_menu);
        apperal_menu.setOnClickListener(this);

        auto_mobile = (LinearLayout) findViewById(R.id.auto_mobiles);
        auto_mobile.setOnClickListener(this);

        bars_pub = (LinearLayout) findViewById(R.id.bars_pubs);
        bars_pub.setOnClickListener(this);

        bouquests = (LinearLayout) findViewById(R.id.bouquests);
        bouquests.setOnClickListener(this);

        dineouts = (LinearLayout) findViewById(R.id.dineouts);
        dineouts.setOnClickListener(this);

        entertainment = (LinearLayout) findViewById(R.id.entertainment);
        entertainment.setOnClickListener(this);

        electronics = (LinearLayout) findViewById(R.id.electronics);
        electronics.setOnClickListener(this);

        hotels = (LinearLayout) findViewById(R.id.hotels);
        hotels.setOnClickListener(this);

        petrol_bunks = (LinearLayout) findViewById(R.id.petrol_bunks);
        petrol_bunks.setOnClickListener(this);

        sallon_spa = (LinearLayout) findViewById(R.id.sallon_spa);
        sallon_spa.setOnClickListener(this);

        super_markets = (LinearLayout) findViewById(R.id.super_markets);
        super_markets.setOnClickListener(this);

        wellness = (LinearLayout) findViewById(R.id.wellness);
        wellness.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void logout(){
        session.setLoggedIn(false);
        session.clearEditorData();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void oFfers(View view){
        Intent i = new Intent(getApplicationContext(), OffersActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(this, MapsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_apps_linked) {
            startActivity(new Intent(this, AppsLinkedActivity.class));
        }else if (id == R.id.nav_payhistory) {
            startActivity(new Intent(this, PaymentActivity.class));
        }else if (id == R.id.nav_favourites) {
            startActivity(new Intent(this, FavouritesActivity.class));
        }else if (id == R.id.nav_refer) {
            startActivity(new Intent(this, ReferActivity.class));
        }else if (id == R.id.nav_offers) {
            startActivity(new Intent(this, OffersActivity.class));
        }else if (id == R.id.nav_rating) {
            Toast.makeText(this, "RATE OUR APP", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.apperals_menu:
                Intent i = new Intent(getApplicationContext(), ShopListActivity.class);
                i.putExtra("parameter","Apperals");
                i.putExtra("cat_id","1");
                startActivity(i);
                break;
            case R.id.auto_mobiles:
                Intent i1 = new Intent(getApplicationContext(), ShopListActivity.class);
                i1.putExtra("parameter","AutoMobiles");
                i1.putExtra("cat_id","2");
                startActivity(i1);
                break;
            case R.id.bars_pubs:
                Intent i2 = new Intent(getApplicationContext(), ShopListActivity.class);
                i2.putExtra("parameter","Bars&Pubs");
                i2.putExtra("cat_id","3");
                startActivity(i2);
                break;
            case R.id.bouquests:
                Intent i3 = new Intent(getApplicationContext(), ShopListActivity.class);
                i3.putExtra("parameter","Bouquests");
                i3.putExtra("cat_id","4");
                startActivity(i3);
                break;
            case R.id.dineouts:
                Intent i4 = new Intent(getApplicationContext(), ShopListActivity.class);
                i4.putExtra("parameter","Dineouts");
                i4.putExtra("cat_id","5");
                startActivity(i4);
                break;
            case R.id.entertainment:
                Intent i5 = new Intent(getApplicationContext(), ShopListActivity.class);
                i5.putExtra("parameter","Entertainment");
                i5.putExtra("cat_id","6");
                startActivity(i5);
                break;
            case R.id.electronics:
                Intent i6 = new Intent(getApplicationContext(), ShopListActivity.class);
                i6.putExtra("parameter","Electronics");
                i6.putExtra("cat_id","7");
                startActivity(i6);
                break;
            case R.id.hotels:
                Intent i7 = new Intent(getApplicationContext(), ShopListActivity.class);
                i7.putExtra("parameter","Hotels");
                i7.putExtra("cat_id","8");
                startActivity(i7);
                break;
            case R.id.petrol_bunks:
                Intent i8 = new Intent(getApplicationContext(), ShopListActivity.class);
                i8.putExtra("parameter","PetrolBunks");
                i8.putExtra("cat_id","9");
                startActivity(i8);
                break;
            case R.id.sallon_spa:
                Intent i9 = new Intent(getApplicationContext(), ShopListActivity.class);
                i9.putExtra("parameter","SaloonSpa");
                i9.putExtra("cat_id","12");
                startActivity(i9);
                break;
            case R.id.super_markets:
                Intent i10 = new Intent(getApplicationContext(), ShopListActivity.class);
                i10.putExtra("parameter","SuperMarkets");
                i10.putExtra("cat_id","10");
                startActivity(i10);
                break;
            case R.id.wellness:
                Intent i11 = new Intent(getApplicationContext(), ShopListActivity.class);
                i11.putExtra("parameter","Wellness");
                i11.putExtra("cat_id","11");
                startActivity(i11);
                break;
            default:

        }
    }
}
