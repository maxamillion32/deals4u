package in.stallats.deals.deal4u;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class FavouritesActivity extends AppCompatActivity {
    Session session;
    String user_id;
    LinearLayout ll, l;
    Context mContext;
    GPSTracker gps;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        session = new Session(this);
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get("id");

        mContext = this;

        gps = new GPSTracker(mContext, this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        l = (LinearLayout) findViewById(R.id.parent_layout);

        Future<JsonArray> get = Ion.with(this)
                .load("http://dealsapi.stallats.org/api/favstores/" + user_id +"/"+ latitude +"/"+ longitude)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {

                        } else {
                            final String store_id[] = new String[result.size()];
                            final ToggleButton toggleButton[] = new ToggleButton[result.size()];
                            for (int i = 0; i < result.size(); i++) {
                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View format = inflater.inflate(R.layout.stores_list, null);

                                ImageView imageview = (ImageView)format.findViewById(R.id.imageView2);
                                ImageView logo = (ImageView)format.findViewById(R.id.imageView3);

                                ll = (LinearLayout) format.findViewById(R.id.banner);

                                TextView t1 = (TextView) format.findViewById(R.id.textView98);
                                TextView t2 = (TextView) format.findViewById(R.id.textView100);
                                TextView t3 = (TextView) format.findViewById(R.id.distancetext);

                                String x = result.get(i).toString();
                                try {
                                    final JSONObject xx = new JSONObject(x);

                                    toggleButton[i] = (ToggleButton) format.findViewById(R.id.myToggleButton);

                                    final int finalI = i;

                                    store_id[i] = String.valueOf(xx.getInt("store_id"));

                                    if(session.check_favourite(store_id[i])){
                                        toggleButton[i].setChecked(true);
                                        toggleButton[i].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_trash));
                                    }

                                    toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            JsonObject json = new JsonObject();

                                            if (isChecked){

                                            }
                                            else {
                                                json.addProperty("status", 1);
                                                session.del_favourites(store_id[finalI]);
                                                Ion.with(FavouritesActivity.this)
                                                        .load("POST", "http://dealsapi.stallats.org/api/favour/" + store_id[finalI] + "/" + user_id)
                                                        .setJsonObjectBody(json)
                                                        .asString()
                                                        .setCallback(new FutureCallback<String>() {
                                                            @Override
                                                            public void onCompleted(Exception e, String result) {
                                                                if (e != null) {

                                                                } else {
                                                                    switch (result) {
                                                                        case "1":
                                                                            break;
                                                                        case "2":
                                                                            Toast.makeText(FavouritesActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                }
                                                            }
                                                        });
                                                l.removeView(format);
                                                //toggleButton[finalI].setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.like));
                                                Toast.makeText(FavouritesActivity.this, "Favourite Removed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                    t1.setText(xx.getString("store_name").toUpperCase());
                                    t2.setText(xx.getString("area"));
                                    t3.setText("Distance: "+Math.round(xx.getDouble("distance"))+"Km"+", Rating: 4.2");
                                    new DownloadImageTask(imageview).execute("http://dealsapi.stallats.org/" + xx.getString("background_pic"));
                                    new DownloadImageTask(logo).execute("http://dealsapi.stallats.org/" + xx.getString("logo"));

                                    ll.setOnClickListener(new LinearLayout.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent i2 = new Intent(getApplicationContext(), ShopFullDeatilsActivity.class);
                                                i2.putExtra("id",xx.getString("store_id"));
                                                i2.putExtra("cat_id",xx.getString("category_id"));
                                                i2.putExtra("parameter", xx.getString("store_name"));
                                                startActivity(i2);
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                l.addView(format);
                            }

                        }
                    }
                });

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
}
