package in.stallats.deals.deal4u;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    String refer_code;
    ShareDialog shareDialog;
    String text;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        session = new Session(this);
        HashMap<String, String> user = session.getUserDetails();
        refer_code = user.get("referal");;
        text = "Download Jenga Deals App. Enter this code '"+refer_code+"' while signup or click here to register";

        ImageView whatsapp = (ImageView) findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(this);

        ImageView facebook_share = (ImageView) findViewById(R.id.facebook_logo);
        facebook_share.setOnClickListener(this);

        ImageView email_share = (ImageView) findViewById(R.id.email_share);
        email_share.setOnClickListener(this);

        ImageView sms_share = (ImageView) findViewById(R.id.sms_share);
        sms_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.whatsapp:
                PackageManager pm = getPackageManager();
                try {
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    Intent whatsappIntent = new Intent();
                    whatsappIntent.setAction(Intent.ACTION_SEND);
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(whatsappIntent, "Share with"));
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.facebook_logo:
                String url = "www.appscharge.com";
                shareDialog = new ShareDialog(this);
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle("Jenga Deals")
                            .setContentUrl(Uri.parse(url))
                            .setQuote(text).build();
                    shareDialog.show(linkContent);
                }
                break;
            case R.id.email_share:
                Intent email_sharing = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
                email_sharing.putExtra(Intent.EXTRA_SUBJECT, "Jenga Deals Refer & Earn");
                email_sharing.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(email_sharing, "Send Email"));
                break;
            case R.id.sms_share:
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("sms_body", text);
                startActivity(Intent.createChooser(sendIntent, "Choose an App to Send SMS"));
        }
    }
}
