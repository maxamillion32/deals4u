package in.stallats.deals.deal4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login, register;
    private EditText etEmail, etPass;
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);
        login = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnReg);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

        if(session.loggedin()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnReg:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            default:

        }
    }

    public void login() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if (email.isEmpty()) {
            displayToast("Enter Email / Password");
        } else if (pass.isEmpty()) {
            displayToast("Enter Password");
        } else {
            logincheck();
        }

    }

    private void logincheck() {
        JsonObject json = new JsonObject();
        json.addProperty("user", etEmail.getText().toString());
        json.addProperty("pwd", etPass.getText().toString());
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
                                    Toast.makeText(LoginActivity.this, "Username / Password Wrong", Toast.LENGTH_SHORT).show();
                                    break;
                                case "2":
                                    Toast.makeText(LoginActivity.this, "Sorry, Your Account not Activated", Toast.LENGTH_SHORT).show();
                                    break;
                                case "3":
                                    Toast.makeText(LoginActivity.this, "Sorry, you're not authorized user", Toast.LENGTH_SHORT).show();
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

                                        Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                        session.setLoggedIn(true);

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } catch (JSONException e1) {
                                        Log.e(e1.getClass().getName(), e1.getMessage(), e1.getCause());
                                        //e1.printStackTrace();
                                        Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                                    }

                            }

                        }
                    }
                });
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
