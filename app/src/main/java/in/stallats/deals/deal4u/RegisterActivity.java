package in.stallats.deals.deal4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button reg;
    private TextView tvLogin, regusertype;
    private EditText etName, etEmail, etMobile, etPass;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private String usertype = "2", gender;
    String name, email, mobile, pass;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg = (Button)findViewById(R.id.btnReg);
        tvLogin = (TextView)findViewById(R.id.tvLogin);
        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etMobile = (EditText)findViewById(R.id.etMobile);
        etPass = (EditText)findViewById(R.id.etPass);
        regusertype = (TextView) findViewById(R.id.usertype);

        session = new Session(this);

        radioSexGroup = (RadioGroup) findViewById(R.id.radioGrp);
        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);
        gender = radioSexButton.getText().toString();

        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnReg:
                register();
                break;
            case R.id.tvLogin:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
            default:

        }
    }

    public void register(){
        email = etEmail.getText().toString();
        name = etName.getText().toString();
        mobile = etMobile.getText().toString();
        pass = etPass.getText().toString();

        if(name.isEmpty()){
            displayToast("Enter Name");
        }else if(email.isEmpty()){
            displayToast("Enter Email");
        }else if(!email.matches(emailPattern)){
            displayToast("Enter Valid Email");
        }else if(mobile.isEmpty()){
            displayToast("Enter Mobile Number");
        }else if (mobile.length() < 10 || mobile.length() > 10) {
            displayToast("Enter Valid Mobile Number");
        }else if(pass.isEmpty()){
            displayToast("Enter Password");
        }else{
            signUP();
        }
    }

    private void signUP() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("mobile", mobile);
        json.addProperty("email", email);
        json.addProperty("pword", pass);
        json.addProperty("gender", gender);
        json.addProperty("user_type", usertype);
        json.addProperty("ip", ip);
        Ion.with(RegisterActivity.this)
                .load("POST", "http://dealsapi.stallats.org/api/newuser")
                .setJsonObjectBody(json)
                .as(new TypeToken<Integer>() {
                })
                .setCallback(new FutureCallback<Integer>() {
                    @Override
                    public void onCompleted(Exception e, Integer result) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            switch (result) {
                                case 1:
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    logincheck();
                                    break;
                                case 2:
                                    Toast.makeText(RegisterActivity.this, "This Mobile is already registered", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(RegisterActivity.this, "This Email is already registered", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(RegisterActivity.this, "Registration failed.\n Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void logincheck() {
        JsonObject json = new JsonObject();
        json.addProperty("user", mobile);
        json.addProperty("pwd", pass);
        json.addProperty("user_type", 2);
        Ion.with(this)
                .load("POST", "http://dealsapi.stallats.org/api/login")
                .setJsonObjectBody(json)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {

                        } else {
                            switch (result){
                                case "1":
                                    Toast.makeText(RegisterActivity.this, "Username / Password Wrong", Toast.LENGTH_SHORT).show();
                                    break;
                                case "2":
                                    Toast.makeText(RegisterActivity.this, "Sorry, Your Account not Activated", Toast.LENGTH_SHORT).show();
                                    break;
                                case "3":
                                    Toast.makeText(RegisterActivity.this, "Sorry, you're not authorized user", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);

                                        String id = (String) jsonObject.get("id");
                                        String name = (String) jsonObject.get("name");
                                        String email = (String) jsonObject.get("email");
                                        String mobile = (String) jsonObject.get("mobile");
                                        String gender = (String) jsonObject.get("gender");
                                        String referal = (String) jsonObject.get("referal_code");
                                        session.set_sessionData(id, name, email, mobile, gender, referal);

                                        Toast.makeText(RegisterActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                        session.setLoggedIn(true);
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                        Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                                    }

                            }
                        }
                    }
                });
    }

    @SuppressLint("ShowToast")
    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
