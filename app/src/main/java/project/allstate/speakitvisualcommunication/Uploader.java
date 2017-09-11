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
import android.view.Menu;
import android.view.MenuInflater;
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
 * Class involved in the upload of new Image objects
 * Authored by Anthony McDonald and Gareth Moore
 */
public class Uploader extends AppCompatActivity {

    /**
     * An EditText
     */
    EditText edtName;

    /**
     * Buttons
     */
    Button btnChoose, btnAdd;

    /**
     * ImageView
     */
    ImageView imageView;

    /**
     * The user chosen
     */
    private String userChosen;

    /**
     * The category selcted
     */
    private String categorySelected;

    /**
     *
     */
    final int REQUEST_CODE_GALLERY = 1;

    /**
     *
     */
    final int REQUEST_IMAGE_CAPTURE = 0;

    /**
     * Database operations class
     */
    private DatabaseOperations ops;

    /**
     * The user
     */
    private String user;

    /**
     * The login name of the account
     */
    private String logName;

    /**
     * A spinner
     */
    private ProgressBar spinner;


    //private static final int ACTION_TAKE_PHOTO_B = 1;

    //private String mCurrentPhotoPath;

    //private static final String JPEG_FILE_SUFFIX = ".jpg";

    //private static final String JPEG_FILE_PREFIX = "IMG_";

    //private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);

        //Set back button in the bar at the top of screen
        //Authored by Anthony McDonald and Gareth Moore
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        //Spinner used to show if page is loading
        //Created by Gareth Moore
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        Intent intent = getIntent();
        user = intent.getStringExtra("project.allstate.speakitvisualcommunication.User");
        categorySelected = intent.getStringExtra("project.allstate.speakitvisualcommunication.page");
        logName = intent.getStringExtra("project.allstate.speakitvisualcommunication.Login");
        if (user == null) {
            Toast.makeText(this, "Please select a User", Toast.LENGTH_SHORT).show();
            finish();
        }

        ops = new DatabaseOperations(getApplicationContext());
        ops.open();

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


        /**
         * Volley request is set up in the onClick method
         * This will insert the image object into the database
         * If successful user is taken either to the main screen or second screen depending on the category.
         * If unsuccessful user remains on the page and the update button is made availbale again.
         * Created by Gareth Moore
         */
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    btnAdd.setEnabled(false);
                    spinner.setVisibility(View.VISIBLE);

                    //String BASE_URL = "http://10.0.2.2:5000/project/insertImage";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/insertImage";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", null);
                    body.put("word", edtName.getText().toString());
                    body.put("category", categorySelected);
                    body.put("username", user);
                    body.put("number", "2");
                    body.put("images", BitMapToString(((BitmapDrawable)imageView.getDrawable()).getBitmap()));

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(Uploader.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);
                            Toast.makeText(Uploader.this, "Success ", Toast.LENGTH_SHORT).show();

                            if (!categorySelected.equals("Home Page")) {
                                Intent intent = new Intent(Uploader.this, SecondScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("project.allstate.speakitvisualcommunication.username2", user);
                                intent.putExtra("project.allstate.speakitvisualcommunication.Category", categorySelected);
                                intent.putExtra("project.allstate.speakitvisualcommunication.Login",logName);
                                spinner.setVisibility(View.GONE);
                                startActivity(intent);
                            } else if (categorySelected.equals("Home Page")){
                                Intent intent = new Intent(Uploader.this, MainScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("project.allstate.speakitvisualcommunication.username2", user);
                                intent.putExtra("project.allstate.speakitvisualcommunication.Category", categorySelected);
                                intent.putExtra("project.allstate.speakitvisualcommunication.Login",logName);
                                spinner.setVisibility(View.GONE);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onError(ErrorResponse errorResponse){
                            System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                            btnAdd.setEnabled(true);
                            spinner.setVisibility(View.GONE);
                            Toast.makeText(Uploader.this, "Opps, word is too long", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

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
     * This is a dialogue pop-up component to allow the user to select various options
     * Authored by Anthony McDonald
     */
    private void selectImage() {
        // This will display and list three categories available for the user to select
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        // This displays the pop-up based on the items defined in the previous statement
        AlertDialog.Builder builder = new AlertDialog.Builder(Uploader.this);
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
                boolean result=Utility.checkPermission(Uploader.this);

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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                // If gallery was selected this code will display gallery data
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // If caputre was selected this code will display camera data
                onCaptureImageResult(data);

            }
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
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        // The below variables and functions are specifying to save the photo file
        // as a JPEG and to allocate this file to the phones storage section.
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

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
        edtName = (EditText) findViewById(R.id.edtName);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        imageView = (ImageView) findViewById(R.id.imageViewAdd);
    }

    /**
     * Creating options menu and inflating it with menu
     * layout file
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_second, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Method for the selection of the home button or the menu icon
     * Created by Gareth Moore
     * @param item
     *        - the menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.account2:
                Intent intent = new Intent(Uploader.this, UserSelect.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Method converts the bitmap image to a string
     * Created by Gareth Moore
     * @param bitmap
     *      - The bitmap image
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
     * Below is an alternative method for the camera and gallery functionality which has been commented out.
     * Created by Gareth Moore
     */
    //private void dispatchTakePictureIntent(int actionCode) {

    //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    //switch(actionCode) {
    //case ACTION_TAKE_PHOTO_B:
    //File f = null;

    //try {
    // f = setUpPhotoFile();
    //mCurrentPhotoPath = f.getAbsolutePath();
    // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
    //} catch (IOException e) {
    //  e.printStackTrace();
    // f = null;
    // mCurrentPhotoPath = null;
    //}
    //break;

    //default:
    //break;
    // }

    //startActivityForResult(takePictureIntent, actionCode);
    //}

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // switch (requestCode) {
    //  case ACTION_TAKE_PHOTO_B:
    //   if (resultCode == RESULT_OK) {
    // handleBigCameraPhoto();
    //}
    //break;
    // } // switch
    //}

    //private void handleBigCameraPhoto() {

    //if (mCurrentPhotoPath != null) {
    //  setPic();
    // galleryAddPic();
    // mCurrentPhotoPath = null;
    // }
    //}


    //private File setUpPhotoFile() throws IOException {

    //File f = createImageFile();
    //mCurrentPhotoPath = f.getAbsolutePath();

    //return f;
    //}

    //private File createImageFile() throws IOException {
    // Create an image file name
    // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    // String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
    // File albumF = getAlbumDir();
    // File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    // return imageF;
    //}

    //private File getAlbumDir() {
    //File storageDir = null;

    //if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

    //storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("Album");

    //if (storageDir != null) {
    //if (! storageDir.mkdirs()) {
    //if (! storageDir.exists()){
    //Log.d("CameraSample", "failed to create directory");
    // return null;
    // }
    //}
    // }

    // } else {
    //  Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
    // }

    //return storageDir;
    // }

    // private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
    //int targetW = imageView.getWidth();
    //int targetH = imageView.getHeight();

		/* Get the size of the image */
    // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    //  bmOptions.inJustDecodeBounds = true;
    // BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    // int photoW = bmOptions.outWidth;
    // int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
    // int scaleFactor = 1;
    //if ((targetW > 0) || (targetH > 0)) {
    //     scaleFactor = Math.min(photoW/targetW, photoH/targetH);
    // }

		/* Set bitmap options to scale the image decode target */
    // bmOptions.inJustDecodeBounds = false;
    //bmOptions.inSampleSize = scaleFactor;
    // bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
    //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
    //imageView.setImageBitmap(bitmap);
    //imageView.setVisibility(View.VISIBLE);

    //}

    //private void galleryAddPic() {
    //Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
    // File f = new File(mCurrentPhotoPath);
    // Uri contentUri = Uri.fromFile(f);
    // mediaScanIntent.setData(contentUri);
    // this.sendBroadcast(mediaScanIntent);
    //}



    /**
     * onResume method
     * Created by Gareth Moore
     */
    public void onResume() {
        super.onResume();
        //Open database
        ops.open();

    }

    /**
     *onStop method closes the SQLIte database
     * Created by Gareth Moore
     */
    @Override
    public void  onStop() {
        super.onStop();
        ops.close();
    }

    /**
     * When the activity is finished the method will close the SQLite database.
     * Created by Gareth Moore
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Calling the close method to close the database.
        ops.close();
    }

}
