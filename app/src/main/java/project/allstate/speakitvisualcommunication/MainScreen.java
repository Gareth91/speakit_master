package project.allstate.speakitvisualcommunication;

import android.app.Activity;
import android.app.Dialog;
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
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

import static project.allstate.speakitvisualcommunication.SQLiteHelper.category;

/**
 * Created by Gareth Moore
 * Class used to create the main screen of the app
 */
public class MainScreen extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener, View.OnClickListener, View.OnLongClickListener{

    /**
     * TTS object
     */
    private TextToSpeech myTTS;

    /**
     * status check code
     */
    private int MY_DATA_CHECK_CODE = 0;

    /**
     * A list containing PecsImages objects
     */
    private List<PecsImages> imageCategories;

    /**
     * A list containing PecsImages objects
     */
    private List<PecsImages> list;

    /**
     * ImageAdapter object which is used to create the grid view
     */
    private ImageAdapter imageAdapter;

    /**
     * GridView object
     */
    private GridView gridView;

    /**
     * The user chosen
     */
    private String userChosen;

    /**
     *
     */
    final int REQUEST_CODE_GALLERY = 1;

    /**
     *
     */
    final int REQUEST_IMAGE_CAPTURE = 0;

    /**
     * The login account name
     */
    private String logName = null;


    /**
     * Array containing PecsImages objects which take an image from
     * drawable resource
     */
    private PecsImages[] categories = {

            new PecsImages("Add Category", R.drawable.addcategory_white,1),
            new PecsImages("At Home", R.drawable.athome,1),
            new PecsImages("Favourites", R.drawable.favourites,1),
            new PecsImages("About Me", R.drawable.aboutme,1),
            new PecsImages("Greetings", R.drawable.greetings,1),
            new PecsImages("Food And Drink", R.drawable.foodanddrink,1),
            new PecsImages("Leisure", R.drawable.leisure,1),
            new PecsImages("Animals", R.drawable.animals,1)
    };

    /**
     * DatabaseOperations object
     */
    private DatabaseOperations ops;

    /**
     * ImageView object
     */
    private ImageView pecsView;

    /**
     * RecyclerView object used to create the sentence builder
     * at the top of the page
     */
    private RecyclerView recyclerView;

    /**
     * SentenceBuilderAdapter used to create the sentence builder at the top
     * of the page
     */
    private SentenceBuilderAdapter mAdapter;

    /**
     * A List containing PecsImages objects which will be placed in
     * the RecyclerView
     */
    private List<PecsImages> sentenceWords;

    /**
     * User
     */
    String user = null;
    String user2 = null;


    /**
     * onCreate method called when screen is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Open a connection with the SQLite database using
        // instance of the DatabaseOperations class
        ops = new DatabaseOperations(getApplicationContext());
        ops.open();

        //Gets the username that has been selected on other screens
        //Also gets the login nae from the DataHolder class
        //Created by Gareth Moore
        Intent intent = getIntent();
        user = intent.getStringExtra("project.allstate.speakitvisualcommunication.username");
        user2 = intent.getStringExtra("project.allstate.speakitvisualcommunication.username2");
        logName = DataHolder.getInstance().getLogin();

        /**
         * Add the array containing PecsImages objects to the
         * imageCategories list which is then added to the GridView
         * using the ImageAdapter class
         * Created by Gareth Moore
         */
        imageCategories = new ArrayList<>(Arrays.asList(categories));
        gridView = (GridView)findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this, imageCategories);
        gridView.setAdapter(imageAdapter);

        /**
         * Volley request is used to add any uploaded image objects to the grid view
         * for the specific user that is selected.
         * The intent passing the user can arrive here from two different screens which is why there is an if statement
         * to determine which user variable to use.
         * Created by Gareth Moore
         */
        if (user != null && user2 == null) {
            //List<PecsImages> list2 = ops.getData("Home Page", user);
            //String BASE_URL = "http://10.0.2.2:5000/project/return";
            String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/return";
            String url = BASE_URL;

            HashMap<String, String> headers  = new HashMap<>();
            HashMap<String, String> body  = new HashMap<>();

            body.put("category", "Home Page");
            body.put("username", user);

            String contentType =  "application/json";
            VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

            request.serviceJsonCall(new VolleyCallBack(){
                @Override
                public void onSuccess(String result){
                    System.out.print("CALLBACK SUCCESS: " + result);

                    JSONArray jsonarray = null;
                    try {
                        jsonarray = new JSONArray(result);

                        for (int loop = 0; loop < jsonarray.length(); loop++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(loop);
                            String word = jsonobject.getString("word");
                            String category = jsonobject.getString("category");
                            int id = jsonobject.getInt("id");
                            String username = jsonobject.getString("username");
                            int number = jsonobject.getInt("number");
                            byte [] images= Base64.decode(jsonobject.getString("images"),Base64.DEFAULT);
                            PecsImages pecsImages = new PecsImages(word, images, id, category, username, number);
                            imageCategories.add(pecsImages);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(ErrorResponse errorResponse){
                    System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                    Toast.makeText(MainScreen.this, "Unsuccessful ", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (user == null && user2 != null) {
            //String BASE_URL = "http://10.0.2.2:5000/project/return";
            String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/return";
            String url = BASE_URL;

            HashMap<String, String> headers  = new HashMap<>();
            HashMap<String, String> body  = new HashMap<>();

            body.put("category", "Home Page");
            body.put("username", user2);

            String contentType =  "application/json";
            VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

            request.serviceJsonCall(new VolleyCallBack(){
                @Override
                public void onSuccess(String result){
                    System.out.print("CALLBACK SUCCESS: " + result);

                    JSONArray jsonarray = null;
                    try {
                        jsonarray = new JSONArray(result);

                        for (int loop = 0; loop < jsonarray.length(); loop++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(loop);
                            String word = jsonobject.getString("word");
                            String category = jsonobject.getString("category");
                            int id = jsonobject.getInt("id");
                            String username = jsonobject.getString("username");
                            int number = jsonobject.getInt("number");
                            byte [] images= Base64.decode(jsonobject.getString("images"),Base64.DEFAULT);
                            PecsImages pecsImages = new PecsImages(word, images, id, category, username, number);
                            imageCategories.add(pecsImages);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(ErrorResponse errorResponse){
                    System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                    Toast.makeText(MainScreen.this, "Unsuccessful ", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (user == null) {
            user = user2;
        }

        //Clears any images in the sentence builder
        //Created by Gareth Moore
        List<PecsImages> list = new ArrayList<>();
        list = ops.getSentenceData();
        for (PecsImages image: list) {
            ops.deleteSentenceData(image.getId());
        }

        // The SentenceBuilderAdapter sets up the RecyclerView
        //Created by Gareth Moore
        sentenceWords = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new SentenceBuilderAdapter(sentenceWords);
        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManage);
        recyclerView.setAdapter(mAdapter);


        // onItemClickListener setup on GridView to allow images to be selected
        //Created by Gareth Moore
        gridView.setOnItemClickListener(this);


        // onItemLongClickListener setup on Gridview. This long click allows images within the GridView
        // to be deleted or updated.
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final PecsImages image = imageCategories.get(position);
                if (image.getNumber() != 1) {
                    CharSequence[] items = {"Update", "Delete"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainScreen.this);

                    dialog.setTitle("Choose an action");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0) {
                                 //show dialog update at here
                                showDialogUpdate(MainScreen.this, image.getId());
                            } else {
                                showDialogDelete(image.getId());
                            }
                        }
                    });
                    dialog.show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        //check for TTS data
        //Created by Gareth Moore
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        //onClickListener setup with delete button. This allows for an image to be deleted from
        //the sentence builder.
        //Created by Gareth Moore
        ImageButton cancelButton = (ImageButton) findViewById(R.id.deleteB);
        cancelButton.setOnClickListener(this);
        cancelButton.setOnLongClickListener(this);

    }


    /**
     * Method adds functionality to the item clicks within the GridView.
     * @param parent
     * @param view
     * @param position
     * @param id
     * Created by Gareth Moore
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //PecsImages object selected
        PecsImages image = imageCategories.get(position);
        //The word in the object is spoken.
        speakWords(image.getWord());

        //if the item clicked has the word Add Category it takes you to the upload page
        if (image.getWord().equals("Add Category") && user != null) {
            Intent intent2  = new Intent(getApplicationContext(), Uploader.class);
            intent2.putExtra("project.allstate.speakitvisualcommunication.User", user);
            intent2.putExtra("project.allstate.speakitvisualcommunication.page", "Home Page");
            intent2.putExtra("project.allstate.speakitvisualcommunication.Login",logName);
            startActivity(intent2);
        } else if(image.getWord().equals("Add Category") && user == null) {
            if (logName == null) {
                Intent logIntent = new Intent(MainScreen.this, LoginScreen.class);
                Toast.makeText(this, "Please Login",Toast.LENGTH_LONG ).show();
                startActivity(logIntent);

            } else if (logName != null) {
                Intent userIntent = new Intent(MainScreen.this, UserSelect.class);
                Toast.makeText(this, "Please select user",Toast.LENGTH_LONG ).show();
                startActivity(userIntent);

            }
        } else {
            //if any other item is selected it takes you to the second screen.
            Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
            intent.putExtra("project.allstate.speakitvisualcommunication.Category", image.getWord());
            intent.putExtra("project.allstate.speakitvisualcommunication.username2", user);
            intent.putExtra("project.allstate.speakitvisualcommunication.Login2",logName);
            startActivity(intent);
        }

    }

    /**
     *Method adds functionality to click events on the delete button.
     * @param view
     * Created by Gareth Moore
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteB:
                //if the list sentenceWords is greater than zero the item at the end of the list
                //will be deleted
                if (sentenceWords.size() > 0) {
                    ops.deleteSentenceData(sentenceWords.get(sentenceWords.size()-1).getId());
                    sentenceWords.remove(sentenceWords.size()-1);
                    mAdapter.notifyDataSetChanged();

                }
                break;
            //case R.id.speakB:
                //The words from the different items in the view are added together and then spoken aloud
                //StringBuilder finalStringb =new StringBuilder();
                //for (PecsImages item : sentenceWords) {
                    //finalStringb.append(item.getWord()).append(" ");
                //}
                //speakWords(finalStringb.toString());
                //break;
            default:
                break;
        }
    }

    /**
     * Adds long Click functionality to the sentence builder delete button
     * @param v
     * @return
     * Created by Gareth Moore
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.deleteB:
                List<PecsImages> list = new ArrayList<>();
                list = ops.getSentenceData();
                for (PecsImages image: list) {
                    ops.deleteSentenceData(image.getId());
                }
                sentenceWords.clear();
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }

    }

    /**
     * Method called to speak words aloud
     * @param speech
     * Created by Gareth Moore
     */
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**

    /**
     *Method sets up the text to speech language
     * @param initStatus
     * Created by Gareth Moore
     */
    @Override
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);

        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method called when update is selected when user has clicked and held on an item within GridView.
     * This method will update a PecsImages object from the list. It will also then update
     * the database.
     * Created by Gareth Moore
     * @param activity
     * @param id
     */
    private void showDialogUpdate(Activity activity, final int id) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_pecs_images);
        dialog.setTitle("Update");

        pecsView = (ImageView) dialog.findViewById(R.id.pecsImage);
        final EditText edtName = (EditText) dialog.findViewById(R.id.pecsName);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);
        ImageButton back = (ImageButton)dialog.findViewById(R.id.dialogClose);

        /**
         * Volley request set up to get the information on the object that has been selected to update.
         * Created by Gareth Moore
         */
        //String BASE_URL = "http://10.0.2.2:5000/project/getOne";
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getOne";
        String url = BASE_URL;
        Integer imageId = id;

        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        body.put("id", imageId.toString());

        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String word = jsonObject.getString("word");
                    byte [] image= Base64.decode(jsonObject.getString("images"),Base64.DEFAULT);
                    int id = jsonObject.getInt("id");
                    int number = jsonObject.getInt("number");
                    PecsImages pecsImages = new PecsImages(word, image, id, number);
                    edtName.setText(pecsImages.getWord());
                    edtName.setTextSize(22);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(pecsImages.getImages(), 0, pecsImages.getImages().length);
                    pecsView.setImageBitmap(bitmap);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });


        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        pecsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        /**
         * When update button is clicked a volley request will be sent to update the information in the database
         * Created by Gareth Moore
         */
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Original method set up to mimic what would happen when image is brought in from database
//                    ops.updateData(
//                            edtName.getText().toString().trim(),
//                            Uploader.imageViewToByte(pecsView), "Home Page",
//                            id
//                    );
                    /**
                     * Volley request used to upadate the data within the database
                     * Created by Gareth Moore
                     */
                    final Integer userId = id;
                    //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/updateData";
                    //String BASE_URL = "http://10.0.2.2:5000/project/updateData";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/updateData";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", userId.toString());
                    body.put("word", edtName.getText().toString());
                    body.put("category", "Home Page");
                    Bitmap bitmap = ((BitmapDrawable)pecsView.getDrawable()).getBitmap();
                    body.put("images", BitMapToString(bitmap));

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.PUT, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);

                            /**
                             * As information has been successfully updated the original image object
                             * is removed from the list then a volley request returns the updated image
                             * and this is added to the list and the image adapter is notified
                             * Created by Gareth Moore
                             */
                            Iterator<PecsImages> iterator = imageCategories.iterator();
                            while (iterator.hasNext()) {
                                if(iterator.next().getId() == id) {
                                    iterator.remove();
                                    imageAdapter.notifyDataSetChanged();
                                    //PecsImages item = ops.getItem(id);
                                    //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/getOne";
                                    //String BASE_URL = "http://10.0.2.2:5000/project/getOne";
                                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getOne";
                                    String url = BASE_URL;

                                    HashMap<String, String> headers  = new HashMap<>();
                                    HashMap<String, String> body  = new HashMap<>();

                                    body.put("id", userId.toString());

                                    String contentType =  "application/json";
                                    VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                                    request.serviceJsonCall(new VolleyCallBack(){
                                        @Override
                                        public void onSuccess(String result){
                                            System.out.print("CALLBACK SUCCESS: " + result);

                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(result);
                                                String word = jsonObject.getString("word");
                                                byte [] images = Base64.decode(jsonObject.getString("images"),Base64.DEFAULT);
                                                int id = jsonObject.getInt("id");
                                                int number = jsonObject.getInt("number");
                                                String username = jsonObject.getString("username");
                                                PecsImages pecsImages = new PecsImages(word, images, id, category, username, number);
                                                imageCategories.add(pecsImages);
                                                imageAdapter.notifyDataSetChanged();
                                                Toast.makeText(MainScreen.this, "Update Success ", Toast.LENGTH_LONG).show();
                                            }catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onError(ErrorResponse errorResponse){
                                            System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onError(ErrorResponse errorResponse){
                            System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                        }
                    });
                    dialog.dismiss();
                } catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }



    /**
     * Method deletes an image from the list used to populate the GridView
     * Created by Gareth Moore
     * @param idPecs
     */
    private void showDialogDelete(final int idPecs) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainScreen.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    /**
                     * Volley request deletes the image object from the database. If successful
                     * the image object is removed from the list used to populate grid view
                     * and the image adapter is notified
                     * Created by Gareth Moore
                     */
                    Integer deleteId = idPecs;
                    //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/deleteData";
                    //String BASE_URL = "http://10.0.2.2:5000/project/deleteData";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteData";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", deleteId.toString());

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(MainScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);
                            Toast.makeText(MainScreen.this, "Success ", Toast.LENGTH_LONG).show();
                            Iterator<PecsImages> iterator = imageCategories.iterator();
                            while (iterator.hasNext()) {
                                if(iterator.next().getId() == idPecs) {
                                    iterator.remove();
                                    imageAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onError(ErrorResponse errorResponse){
                            System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }



    /**
     *Permission to use image
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosen.equals("Take Photo"))
                        cameraIntent();
                    else if(userChosen.equals("Choose from Library"))
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
     */
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainScreen.this);

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
     *
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_CODE_GALLERY);
    }

    /**
     *
     */
    private void cameraIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    /**
     * Method checks the data is available to use text to speech
     * Created by Gareth Moore
     * Also checks the data for the camera and gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
//            else {
//                //no data - install it now
//                Intent installTTSIntent = new Intent();
//                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                startActivity(installTTSIntent);
           // }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                onCaptureImageResult(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

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

        pecsView.setImageBitmap(thumbnail);
    }

    /**
     *
     * @param data
     */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if (data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                pecsView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * onResume method
     * Opens the SQLite database and updates the list used to populate the sentence builder
     * Created by Gareth Moore
     */
    public void onResume() {
        super.onResume();
        //Open database
        ops.open();
        //
        sentenceWords.clear();
        List<PecsImages> list = new ArrayList<>();
        list = ops.getSentenceData();
        sentenceWords.addAll(list);
        mAdapter.notifyDataSetChanged();


    }

    /**
     * Creates a menu icon
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * If menu item is clicked method is called.
     * If it is the user select item and the login name is null takes user to login page
     * If it is the user select item and the login name is not null takes user to user select page
     * If it is play icon builds a string from the image object words in the sentence builder and then call speakWords
     * to speak the sentence.
     * Created by Gareth Moore
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.account:
                if (logName == null) {
                    Intent loginIntent = new Intent(MainScreen.this, LoginScreen.class);
                    startActivity(loginIntent);
                } else {
                    Intent intent = new Intent(MainScreen.this, UserSelect.class);
                    startActivity(intent);
                }

                return true;
            case R.id.action_play:
                //The words from the different items in the view are added together and then spoken aloud
                StringBuilder finalStringb =new StringBuilder();
                for (PecsImages image : sentenceWords) {
                    finalStringb.append(image.getWord()).append(" ");
                }
                speakWords(finalStringb.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Converts the bitmap to a string
     * Created by Gareth Moore
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
     *onStop method
     * Created by Gareth Moore
     */
    @Override
    public void  onStop() {
        super.onStop();

    }


    /**
     * When the activity is finished the method will close the  SQLite database.
     * Also closes the text to speech to prevent any data leaks
     * Created by Gareth Moore
     */
    @Override
    public void onDestroy() {
        super.onDestroy();


        //Calling the close method to close the database.
        ops.close();

        if(myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }
    }
}

