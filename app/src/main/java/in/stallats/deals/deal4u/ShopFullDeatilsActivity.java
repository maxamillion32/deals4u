package in.stallats.deals.deal4u;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ShopFullDeatilsActivity extends AppCompatActivity {
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 10;
    Double stringLatitude;
    Double stringLongitude;
    Context mContext;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_full_deatils);

        mContext = this;

        Bundle extras = getIntent().getExtras();
        String value = extras.getString("parameter");
        String store_id = extras.getString("id");



        getSupportActionBar().setTitle(value);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Future<JsonArray> get = Ion.with(this)
                .load("http://dealsapi.stallats.org/api/store/" + store_id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {

                        } else {
                            String x = result.get(0).toString();
                            final JSONObject xx;

                            ImageView imageview = (ImageView) findViewById(R.id.single_store_banner);
                            TextView t1 = (TextView) findViewById(R.id.single_store_location);

                            try {
                                xx = new JSONObject(x);
                                //t1.setText(xx.getString("area")+", "+xx.getString("city"));


                                String lat_lng = xx.getString("lat_lng");
                                ArrayList lat_lng_array = new ArrayList(Arrays.asList(lat_lng.split(",")));

                                Double lat = Double.parseDouble((String) lat_lng_array.get(0));
                                Double lng = Double.parseDouble((String) lat_lng_array.get(1));

                                gps = new GPSTracker(mContext, ShopFullDeatilsActivity.this);

                                if (gps.canGetLocation()) {

                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    String s = latitude+", "+longitude+","+lat+","+lng;
                                    Double distance = distance(latitude, longitude, lat, lng, "K");
                                    t1.setText(String.valueOf(distance));

                                } else {
                                    gps.showSettingsAlert();
                                }

                                new DownloadImageTask(imageview).execute("http://dealsapi.stallats.org/" + xx.getString("background_pic"));

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (Math.round(dist));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
