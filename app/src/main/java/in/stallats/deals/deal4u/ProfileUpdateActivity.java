package in.stallats.deals.deal4u;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.HashMap;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_name, edit_mobile, edit_email;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    String gender, name, mobile, email;
    Button edit_update, edit_cancel;
    Session session;
    String id, referal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        session = new Session(this);

        HashMap<String, String> user = session.getUserDetails();
        String ses_name = user.get("name");
        String ses_email = user.get("email");
        String ses_mobile = user.get("mobile");
        String ses_gender = user.get("gender");
        id = user.get("id");
        referal = user.get("referal");

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(ses_name, TextView.BufferType.EDITABLE);

        edit_mobile = (EditText) findViewById(R.id.edit_mobile);
        edit_mobile.setText(ses_mobile, TextView.BufferType.EDITABLE);
        edit_mobile.setKeyListener(null);
        edit_mobile.setEnabled(false);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_email.setText(ses_email, TextView.BufferType.EDITABLE);
        edit_email.setKeyListener(null);
        edit_email.setEnabled(false);

        radioSexGroup = (RadioGroup) findViewById(R.id.radioGrp);

        if(ses_gender.equals("Male")){
            radioSexGroup.check(R.id.radioM);
        }else{
            radioSexGroup.check(R.id.radioF);
        }

        edit_update = (Button) findViewById(R.id.edit_update);
        edit_update.setOnClickListener(this);

        edit_cancel = (Button) findViewById(R.id.edit_cancel);
        edit_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_update:

                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);

                name = edit_name.getText().toString();
                mobile = edit_mobile.getText().toString();
                email = edit_email.getText().toString();
                gender = radioSexButton.getText().toString();
                upDate();
                break;
            case R.id.edit_cancel:
                startActivity(new Intent(ProfileUpdateActivity.this, AccountActivity.class));
                break;
        }
    }

    private void upDate() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("gender", gender);
        Ion.with(this)
                .load("POST", "http://dealsapi.stallats.org/api/updateuser/"+id)
                .setJsonObjectBody(json)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        switch (result){
                            case "1": session.set_sessionData(id, name, email, mobile, gender, referal);
                                Toast.makeText(ProfileUpdateActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileUpdateActivity.this, AccountActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                Toast.makeText(ProfileUpdateActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
