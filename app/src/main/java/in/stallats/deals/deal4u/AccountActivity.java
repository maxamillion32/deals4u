package in.stallats.deals.deal4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        findViewById(R.id.edit_profile).setOnClickListener(this);
        findViewById(R.id.update_password).setOnClickListener(this);
        findViewById(R.id.account_about).setOnClickListener(this);
        findViewById(R.id.account_support).setOnClickListener(this);
        findViewById(R.id.account_termsofservice).setOnClickListener(this);
        findViewById(R.id.account_logout).setOnClickListener(this);

        session = new Session(this);
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        String mobile = user.get("mobile");

        TextView view_email = (TextView) findViewById(R.id.profile_email);
        view_email.setText(email);

        TextView view_name = (TextView) findViewById(R.id.profile_name);
        view_name.setText(name);

        TextView view_mobile = (TextView) findViewById(R.id.profile_mobile);
        view_mobile.setText(mobile);

    }

    @Override
    public void onClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (view.getId()){

            case R.id.edit_profile:
                startActivity(new Intent(this, ProfileUpdateActivity.class));
                break;

            case R.id.update_password:
                startActivity(new Intent(this, PasswordChangeActivity.class));
                break;

            case R.id.account_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.account_support:
                startActivity(new Intent(this, SupportActivity.class));
                break;
            case R.id.account_termsofservice:
                startActivity(new Intent(this, TermsActivity.class));
                break;
            case R.id.account_logout:
                logout();
                break;

            default:
        }
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void logout(){
        session.setLoggedIn(false);
        session.clearEditorData();
        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
        finish();
    }
}
