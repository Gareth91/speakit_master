package project.allstate.speakitvisualcommunication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;


/**
 * Created by Connaire Reid on 05/08/2017.
 * Modified by Gareth, Anthony and Ashley on 22/08/2017
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    /**
     * Declaration of items within the activity
     */
    EditText Username, Password;
    Button Login;
    Button Forgot;
    Button Register;

    /**
     * onCreate method called when screen is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Set back button in the bar at the top of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setTitle("Login");


        Login = (Button) findViewById(R.id.button3);
        Username = (EditText) findViewById(R.id.editText6);
        Password = (EditText) findViewById(R.id.editText7);
        Forgot = (Button) findViewById(R.id.forgotbutton);
        Register = (Button) findViewById(R.id.button4);

        Login.setOnClickListener(this);
        Register.setOnClickListener(this);
        Forgot.setOnClickListener(this);

    }


    /**
     * Method to set the onclick for each button within the activity
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button3:
                //Validation that a username is entered
                if (Username.getText().toString().equals("")) {
                    Username.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    // Validation that a password is entered
                } else if (Password.getText().toString().equals("")) {
                    Password.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                } else {
                    Username.getBackground().clearColorFilter();
                    Password.getBackground().clearColorFilter();
                }
                /**
                 * Volley request to get the account details
                 * Created by Gareth Moore
                 */
                //String BASE_URL = "http://10.0.2.2:5000/project/getAccountDetails";
                String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getAccountDetails";
                String url = BASE_URL;

                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> body = new HashMap<>();

                //username and password added to the body
                body.put("username", Username.getText().toString());
                body.put("password", Password.getText().toString());

                //VolleyRequest to determine if the username and password exist within the database
                String contentType = "application/json";
                VolleyRequest request = new VolleyRequest(LoginScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                request.serviceJsonCall(new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.print("CALLBACK SUCCESS: " + result);
                        if (result.length() > 0) {
                            JSONObject jsonObject = null;
                            try {
                                // Comparison of Object returned from database to the username and password entered
                                // to assert if the user exists or not
                                jsonObject = new JSONObject(result);
                                String dbUsername = jsonObject.getString("username");
                                String dbPassword = jsonObject.getString("password");

                                if (Username.getText().toString().equals(dbUsername) && Password.getText().toString().equals(dbPassword)) {

                                    Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    DataHolder.getInstance().setLogin(dbUsername);
                                    Intent intent = new Intent(LoginScreen.this, UserSelect.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginScreen.this, "Username or Password Incorrect", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(LoginScreen.this, "Username or Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());

                    }
                });
                break;
            case R.id.button4:
                Intent registerIntent = new Intent(LoginScreen.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.forgotbutton:
                Intent forgotIntent = new Intent(LoginScreen.this, ForgotPassword.class);
                startActivity(forgotIntent);
                break;
            default:
                break;
        }
    }


    /**
     * Method for the selection of the home button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


