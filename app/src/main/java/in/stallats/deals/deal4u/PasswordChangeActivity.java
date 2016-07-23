package in.stallats.deals.deal4u;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PasswordChangeActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText change_old_password, change_new_password, change_renew_password;
    private Button change_submit, change_cancel;
    private String oldp, newp, renewp;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        session = new Session(this);

        change_old_password = (EditText) findViewById(R.id.change_old_password);
        change_new_password = (EditText) findViewById(R.id.change_new_password);
        change_renew_password = (EditText) findViewById(R.id.change_renew_password);


        change_submit = (Button) findViewById(R.id.change_submit);
        change_submit.setOnClickListener(this);
        change_cancel = (Button) findViewById(R.id.change_cancel);
        change_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_submit:
                oldp = change_old_password.getText().toString();
                newp = change_new_password.getText().toString();
                renewp = change_renew_password.getText().toString();

                if (oldp.isEmpty()) {
                    displayToast("Enter Current Password");
                } else if (newp.isEmpty()) {
                    displayToast("Enter New Password");
                } else if (renewp.isEmpty()) {
                    displayToast("Re Enter New Password");
                } else if (!newp.equals(renewp)) {
                    displayToast("Your New Passwords are not Matching");
                } else {
                    changePassword();
                }

                break;
            case R.id.change_cancel:
                startActivity(new Intent(this, AccountActivity.class));
                break;
            default:

        }
    }

    private void changePassword() {

        HashMap<String, String> user = session.getUserDetails();
        String id = user.get("id");

        JsonObject json = new JsonObject();
        json.addProperty("old_pwd", oldp );
        json.addProperty("new_pwd", newp);
        json.addProperty("user_type", 2);

        Ion.with(this)
                .load("POST", "http://dealsapi.stallats.org/api/password/"+id)
                .setJsonObjectBody(json)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {

                        } else {
                            switch (result){
                                case "1":
                                    Toast.makeText(PasswordChangeActivity.this, "Username Wrong", Toast.LENGTH_SHORT).show();
                                    break;
                                case "2":
                                    Toast.makeText(PasswordChangeActivity.this, "Sorry, Your Account not Activated", Toast.LENGTH_SHORT).show();
                                    break;
                                case "3":
                                    Toast.makeText(PasswordChangeActivity.this, "Sorry, your current password wrong", Toast.LENGTH_SHORT).show();
                                    break;
                                case "4":
                                    Toast.makeText(PasswordChangeActivity.this, "Congrats, Your Password Successfully changed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PasswordChangeActivity.this, AccountActivity.class));
                                    break;
                                default:
                                    Toast.makeText(PasswordChangeActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
