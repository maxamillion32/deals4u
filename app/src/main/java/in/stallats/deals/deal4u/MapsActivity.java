package in.stallats.deals.deal4u;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "jj";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker customMarker;
    private MapFragment mp;
    private FragmentManager fm;
    private SupportMapFragment sf;
    Context mContext;
    GPSTracker gps;
    double latitude, longitude;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.category0) {
            setUpMap(0);
            return true;
        }
        if (id == R.id.category1) {
            setUpMap(1);
            return true;
        }
        if (id == R.id.category2) {
            setUpMap(2);
            return true;
        }
        if (id == R.id.category3) {
            setUpMap(3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fm = getSupportFragmentManager();
        sf = (SupportMapFragment) fm.findFragmentById(R.id.map);
        sf.getMapAsync(this);

        //setUpMapIfNeeded();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sf.getMapAsync(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * <p/>
     * <p/>
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap != null) {
            setUpMap(0);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(final int catg) {
        mMap.clear();
        try {

            Future<JsonArray> get = Ion.with(this)
                    .load("http://dealsapi.stallats.org/api/allstores")
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            if (e != null) {

                            } else {
                                for (int i = 0; i < result.size(); i++) {
                                    String x = result.get(i).toString();
                                    try {
                                        final JSONObject xx = new JSONObject(x);
                                        if (catg != 0) {
                                            if (catg == xx.getInt("category_id")) {
                                                setMarker(xx.getDouble("lat"), xx.getDouble("lng"), xx.getString("store_id"), xx.getString("logo"));
                                            }
                                        } else {
                                            setMarker(xx.getDouble("lat"), xx.getDouble("lng"), xx.getString("store_id"), xx.getString("logo"));
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap(0);
    }

    public void setMarker(final double lat, final double lng, final String title, final String logo) {
        //Toast.makeText(this, logo, Toast.LENGTH_SHORT).show();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View marker = inflater.inflate(R.layout.custom_marker, null);
        final ImageView numTxt = (ImageView) marker.findViewById(R.id.num_txt);
        //numTxt.setImageDrawable(getResources().getDrawable(R.drawable.ic_ratings));
        //new DownloadImageTask(numTxt).execute("http://dealsapi.stallats.org/" + logo);

        Picasso.with(this).load("http://dealsapi.stallats.org/" + logo).into(numTxt);

        mContext = this;

        gps = new GPSTracker(mContext, MapsActivity.this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        customMarker = mMap.addMarker(new MarkerOptions()
                .title(title)
                .position(new LatLng(lat, lng))
                .snippet(title)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Future<JsonArray> get = Ion.with(MapsActivity.this)
                        .load("http://dealsapi.stallats.org/api/store/" + marker.getTitle())
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                                         @Override
                                         public void onCompleted(Exception e, JsonArray result) {
                                             if (e != null) {

                                             } else {
                                                 String x = result.get(0).toString();
                                                 final JSONObject xx;
                                                 try {
                                                     xx = new JSONObject(x);
                                                     final Dialog dialog = new Dialog(MapsActivity.this, R.style.ImagePopUpStyle);
                                                     final LinearLayout imagePopUp = (LinearLayout) LayoutInflater.from(MapsActivity.this).inflate(R.layout.pop_up, null);
                                                     dialog.setContentView(imagePopUp);
                                                     dialog.setCanceledOnTouchOutside(true);
                                                     Display display = getWindowManager().getDefaultDisplay();
                                                     int width = display.getWidth();
                                                     int height = display.getHeight();

                                                     ImageView icon_close = (ImageView) dialog.findViewById(R.id.icon_close);
                                                     ImageView banner = (ImageView) dialog.findViewById(R.id.single_store_banner_popup);

                                                     new DownloadImageTask(banner).execute("http://dealsapi.stallats.org/" + xx.getString("background_pic"));

                                                     TextView store_title = (TextView) dialog.findViewById(R.id.store_title);
                                                     store_title.setText(xx.getString("store_name"));

                                                     icon_close.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             dialog.dismiss();
                                                         }
                                                     });

                                                     WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                     lp.copyFrom(dialog.getWindow().getAttributes());
                                                     lp.width = width * 90 / 100;
//                lp.height = height * 70 / 100;
                                                     dialog.getWindow().setAttributes(lp);
                                                     dialog.show();

                                                 } catch (JSONException e1) {
                                                     e1.printStackTrace();
                                                 }
                                             }
                                         }
                                     });

                return true;
            }
        });
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://in.stallats.deals.deal4u/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://in.stallats.deals.deal4u/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
                InputStream in = new URL(urldisplay).openStream();
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

}
