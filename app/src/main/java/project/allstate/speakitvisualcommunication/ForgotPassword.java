package project.allstate.speakitvisualcommunication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

/**
 * Authored by Ashley Elliott and Anthony McDonald
 */
public class ForgotPassword extends AppCompatActivity {

    //Instantiate objects
    EditText userEmail, userName;
    TextView information;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Set back button in the bar at the top of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setTitle("Forgot Password");

        userEmail = (EditText) findViewById(R.id.editText8);
        userName = (EditText) findViewById(R.id.editText9);
        information = (TextView) findViewById(R.id.textView);
        submit = (Button) findViewById(R.id.button2);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AWS link for this method
                // String BASE_URL = "http://10.0.2.2:5000/project/getPassword";
                String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getPassword";
                String url = BASE_URL;

                //Creates a new Hashmap of String key value pairs
                HashMap<String, String> headers  = new HashMap<>();
                HashMap<String, String> body  = new HashMap<>();

                //Adds Key Value pair to the JSON body
                body.put("email", userEmail.getText().toString());
                body.put("username", userName.getText().toString());


                //Defines that the body type should be JSON
                String contentType =  "application/json";
                VolleyRequest request =   new VolleyRequest(ForgotPassword.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                //Starts a new JSON request call
                request.serviceJsonCall(new VolleyCallBack(){
                    @Override
                    //on Success method if JSON request is successful
                    public void onSuccess(String result){
                        System.out.print("CALLBACK SUCCESS: " + result);

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String dbPassword = jsonObject.getString("password");
                            String dbUsername = jsonObject.getString("username");

                            //Getting content for email
                            String email = userEmail.getText().toString().trim();
                            String subject = "Password Recovery";
                            String message = "Hi " + dbUsername + " !" + "\n\n\n" +
                                    "Your Password is: "+dbPassword+" ";

                            //Creating SendMail object
                            SendMail sm = new SendMail(ForgotPassword.this, email, subject, message);

                            //Executing sendmail to send email
                            sm.execute();


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    @Override
                    //on Error method if JSON request does not succeed
                    public void onError(ErrorResponse errorResponse){
                        System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                        Toast.makeText(ForgotPassword.this, "Invalid email or username", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

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

