package project.allstate.speakitvisualcommunication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

/**
 * Authored by Ashley Elliott and Gareth Moore
 */
public class ServerMain {

    List<PecsImages> list = new ArrayList<>();

    /**
     *Method to add new image using Volley
     * @param context
     */
    public void addImageWord(final Context context, String word, String category, String username, String number, Bitmap bitmapImage) {

//        String BASE_URL = "http://10.0.2.2:5000/project/insertImage";
        //AWS link for this method
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/insertImage";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pairs to the JSON body
        body.put("id", null);
        body.put("word", word);
        body.put("category", category);
        body.put("username", username);
        body.put("number", number);
        body.put("images", BitMapToString(bitmapImage));

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_SHORT).show();
            }
            //on Error method if JSON request does not succeed
            @Override
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }


    /**
     *
     * @param context
     */
    public void updateImageWord(final Context context, String id, String word, String category, Bitmap bitmapImage) {

        //AWS link for this method
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/updateData";
        //String BASE_URL = "http://localhost:5000/project/updateData";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pairs to the JSON body
        body.put("id", id);
        body.put("word", word);
        body.put("category", "At Home");//category);
        body.put("image", BitMapToString(bitmapImage));

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }

    /**
     *
     * @param context
     */
    public void deleteImageWord(final Context context, String id) {

        //AWS link for this method
        String BASE_URL = "awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteData";
        //String BASE_URL = "http://localhost:5000/project/deleteData";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pair to the JSON body
        body.put("id", id);

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }

    /**
     *
     * @param context
     */
    public void addUser(final Context context, String username, String accountUsername, Bitmap bitmapImage) {

        //AWS link for this method
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/insertUser";
        //String BASE_URL = "http://localhost:5000/project/insertUser";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pair to the JSON body
        body.put("username", "ashley");//username);
        body.put("image", BitMapToString(bitmapImage));
        body.put("accountusername", accountUsername);

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }


    /**
     *
     * @param context
     */
    public void updateUser(final Context context, String username, Bitmap bitmapImage) {

        //AWS link for this method
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/updateUser";
        //String BASE_URL = "http://localhost:5000/project/updateUser";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pair to the JSON body
        body.put("username", "ashley");//username);
        body.put("image", BitMapToString(bitmapImage));

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }

    /**
     *
     * @param context
     */
    public void deleteUser(final Context context, String username) {

        //AWS link for this method
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteUser";
        //String BASE_URL = "http://localhost:5000/project/deleteUser";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pair to the JSON body
        body.put("username", username);

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }




    /**
     *
     * @param context
     */
    public void ImageWord(final Context context) {

        //AWS link for this method
        //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/insertImage";
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/return";
        String url = BASE_URL;

        //Creates a new Hashmap of String key value pairs
        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        //Adds Key Value pair to the JSON body
        body.put("category", "HELP");
        body.put("username", "Ashley");

        //Defines that the body type should be JSON
        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(context, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        //Starts a new JSON request call
        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            //on Success method if JSON request is successful
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(context, "Success ", Toast.LENGTH_LONG).show();

                JSONArray jsonarray = null;
                DatabaseOperations ops = new DatabaseOperations(context);
                ops.open();
                try {
                    jsonarray = new JSONArray(result);
                    //for loop that parses returned array
                    for (int loop = 0; loop < jsonarray.length(); loop++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(loop);
                        String word = jsonobject.getString("word");
                        String category = jsonobject.getString("category");
                        int id = jsonobject.getInt("id");
                        String username = jsonobject.getString("username");
                        int number = jsonobject.getInt("number");
                        byte[] images = jsonobject.getString("images").getBytes();
                        PecsImages pecsImages = new PecsImages(word, images, id, category, username, number);
                        list.add(pecsImages);
                        System.out.println(list.get(0).getWord()+" "+list.get(0).getCategory()+" "+list.get(0).getId()+" "+list.get(0).getNumber()+" "+list.get(0).getUserName()+" "+list.get(0).getImages());


                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
                ops.close();
            }

            @Override
            //on Error method if JSON request does not succeed
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });

    }



    /**
     *Method to convert Bitmap image to string value
     * @param bitmap
     * @return
     */
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}
