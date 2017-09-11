package project.allstate.speakitvisualcommunication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

/**
 * Class that adds functionality to the create user page
 * Authored by Anthony McDonald and Gareth Moore
 */
public class CreateUser extends AppCompatActivity {

    /**
     * The EditName view where the username is enetered
     */
    EditText edtName;

    /**
     * The choose button and add button
     */
    Button btnChoose, btnAdd;

    /**
     * ImageView where the image of the user is shown
     */
    ImageView imageView;

    /**
     * User chosen
     */
    private String userChosen;


    /**
     * Request code gallery number
     */
    final int REQUEST_CODE_GALLERY = 1;

    /**
     * Request image capture number
     */
    final int REQUEST_IMAGE_CAPTURE = 0;

    /**
     * The database operations calss
     */
    private DatabaseOperations ops;


    /**
     * Spinner to indicate loading
     */
    private ProgressBar spinner;

    /**
     * login name
     */
    String login;


    /**
     * Authored by Anthony McDonald and Gareth Moore
     * Class used to create the main screen of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        //Set back button in the bar at the top of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        ops = new DatabaseOperations(getApplicationContext());
        ops.open();
        login = DataHolder.getInstance().getLogin();

        init();

        btnAdd.setEnabled(true);

        /**
         * This is a button that allows the user to select the image chooser.
         * Which will then call the intent required for that users selection
         * Authored by Anthony McDonald
         */
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }

        });

        /**
         * This is an image view n that allows the user to select the image chooser.
         * Which will then call the intent required for that users selection
         * Authored by Anthony McDonald
         */
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }

        });

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd.setEnabled(false);
                //final String username = ops.getUserName(edtName.getText().toString());

                if (edtName.getText().toString().equals("")) {
                    Toast.makeText(CreateUser.this, "Please enter a UserName", Toast.LENGTH_SHORT).show();
                    btnAdd.setEnabled(true);
                } else {
                    spinner.setVisibility(View.VISIBLE);

                    /** 
                     * Volley method used to check whether the username exists in the database already 
                     * Created by Gareth Moore 
                     */
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getUsername";
                    //String BASE_URL = "http://10.0.2.2:5000/project/getUsername";
                    final String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("username", edtName.getText().toString());

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(CreateUser.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);
                            String user = edtName.getText().toString();
                            /** 
                             * If the response from the first volley request is successful 
                             * If the response is null then the username is available, the second volley request is sent to add 
                             * that user profile to the database. The username, user image and account login name are added to the body of the request 
                             * Created by Gareth Moore 
                             */
                            if(!result.equals(user)) {
                                //String BASE_URL = "http://10.0.2.2:5000/project/insertUser";
                                String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/insertUser";

                                String url = BASE_URL;

                                HashMap<String, String> headers  = new HashMap<>();
                                HashMap<String, String> body  = new HashMap<>();

                                body.put("username", edtName.getText().toString());
                                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                body.put("image", BitMapToString(bitmap));
                                body.put("accountusername", login);

                                String contentType =  "application/json";
                                VolleyRequest request =   new VolleyRequest(CreateUser.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                                request.serviceJsonCall(new VolleyCallBack(){
                                    @Override
                                    public void onSuccess(String result){
                                        System.out.print("CALLBACK SUCCESS: " + result);
                                        Toast.makeText(CreateUser.this, "Success ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CreateUser.this, UserSelect.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("project.allstate.speakitvisualcommunication.Login", login);
                                        startActivity(intent);
                                    }
                                    @Override
                                    public void onError(ErrorResponse errorResponse){
                                        System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                                    }
                                });
                            } else {
                                Toast.makeText(CreateUser.this, "UserName "+result+ " already exists", Toast.LENGTH_SHORT).show();
                                btnAdd.setEnabled(true);
                                spinner.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onError(ErrorResponse errorResponse){
                            System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());

                        }
                    });
                }
            }
        });


    }

    /**
     * Method that was initially used for converting an imageview to byte data
     * @param image
     * @return
     */
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /**
     * Method used to check that permissions have been allowed by the User
     * This references the Utility classes permission method
     * Authored by Anthony McDonald
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Manages which type of request the user made
        switch (requestCode) {
            // Approves or denies the permissions upon the user's request
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permissions are accepted and take photo is selected, then camera intent will launch
                    if(userChosen.equals("Take Photo"))
                        cameraIntent();
                    else if(userChosen.equals("Choose from Library"))
                        // If permissions are accepted and gallery is selected, then gallery intent will launch
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     *
     * This is a dialogue pop-up component to allow the user to select various options
     * Authored by Anthony McDonald
     */
    private void selectImage() {
        // This will display and list three categories available for the user to select
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        // This displays the pop-up based on the items defined in the previous statement
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            /**
             * Once the dialogue options have been selected.
             * This code will produce different intent results based on that chosen
             * selection.
             * Authored by Anthony McDonald
             * @param dialog
             * @param item
             */
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(CreateUser.this);

                if (items[item].equals("Take Photo")) {
                    userChosen ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChosen ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * This is a method that is used to launch the gallery intent.
     * It will launche the intent once permissions have been allowed and
     * if a user has chosen the gallery option.
     * Authored by Anthony McDonald
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_CODE_GALLERY);
    }

    /**
     * This is a method that is used to launch the camera intent.
     * It will launche the intent once permissions have been allowed and
     * if a user has chosen the camera option
     * Authored by Anthony McDonald
     */
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * This is a method that uses prior proccess such as Camera and Gallery intent.
     * This intent data is then passed into this method to display the results
     * of the previous intent.
     * Authored by Anthony McDonald
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY)
                // If gallery was selected this code will display gallery data
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_IMAGE_CAPTURE)
                // If caputre was selected this code will display camera data
                onCaptureImageResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * This is a method that allows the saving of a photo file to the phone's
     * internal storage. This method is called when Camera intent has been utilised.
     * Once the photo is taken the photo file is saved to the phone and
     * displayed in the image view allocated for showing activity result intent data.
     * Authored by Anthony McDonald
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        // The below variables and functions are specifying to save the photo file
        // as a JPEG and to allocate this file to the phones storage section.
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Displaying the captured image result
        imageView.setImageBitmap(thumbnail);
    }

    /**
     * This is a method to access the phone's storage and allow for the selection
     * of a specific photo file. Once the selection has been made this photo
     * will be displayed in the image view for the activity result intent data.
     * Authored by Anthony McDonald
     * @param data
     */
    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if (data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // Displaying the selected image in the image view
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Initialising all of the resources used in this class
     */
    private void init(){
        edtName = (EditText) findViewById(R.id.username);
        btnChoose = (Button) findViewById(R.id.btnChooseImage);
        btnAdd = (Button) findViewById(R.id.btnAddImage);
        imageView = (ImageView) findViewById(R.id.userImageAdd);
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

    /**
     *
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

    /**
     * onResume method
     */
    public void onResume() {
        super.onResume();
        //Open database
        ops.open();

    }

    /**
     *onStop method closes the SQLIte database
     */
    @Override
    public void  onStop() {
        super.onStop();
        ops.close();
    }

    /**
     * When the activity is finished the method will close the SQLite database.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Calling the close method to close the database.
        ops.close();
    }

}



