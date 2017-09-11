package project.allstate.speakitvisualcommunication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

/**
 * Created by Gareth Moore
 */
public class SecondScreen extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener, AdapterView.OnItemLongClickListener, View.OnClickListener, View.OnLongClickListener{

    //TTS object
    private TextToSpeech myTTS;

    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    /**
     * The list of image object
     */
    private List<PecsImages> imageWords;

    /**
     * The list of image objects in sentence builder
     */
    private List<PecsImages> sentenceWords;

    /**
     * The RecyclerView used to make the sentence builder
     */
    private RecyclerView recyclerView;

    /**
     * The adapter used to populate the RecyclerView
     */
    private SentenceBuilderAdapter mAdapter;

    /**
     * Instance of the DatabaseOperations class
     */
    private DatabaseOperations ops;

    /**
     * An ImageView
     */
    private ImageView pecsView;

    /**
     * The category
     */
    private String category;

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
     * The login name of the account
     */
    private String logName;

    /**
     * The user selected
     */
    String user = null;

    /**
     * The adapter class used to populate the grid view
     * @param savedInstanceState
     */
    private ImageAdapter imageAdapter;

    /**
     * onCreate method called when screen is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        //Set back button in the bar at the top of screen
        //Created by Gareth Moore
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(category);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        //Opens the SQLite database
        ops = new DatabaseOperations(getApplicationContext());
        ops.open();

        //Intent get the category and user
        //The login name is retrieved from the DataHolder class
        //Created by Gareth Moore
        Intent intent = getIntent();
        category = intent.getStringExtra("project.allstate.speakitvisualcommunication.Category");
        user = intent.getStringExtra("project.allstate.speakitvisualcommunication.username2");
        logName = DataHolder.getInstance().getLogin();

        imageWords = new ArrayList<>();
        imageWords.clear();

        //The correct pre-loaded image objects are added to the list used to populate
        //the grid view based on category
        //Created by Gareth Moore
        switch (category){
            case "Favourites":
                PecsImages image = new PecsImages(getString(R.string.Action_Words),R.drawable.red_action_word,1);
                imageWords.add(image);
                break;
            case "At Home":
                PecsImages[] atHome = {
                        new PecsImages("Action Words", R.drawable.yellow_action_word,1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.yellow_plus,1)
                };
                for (PecsImages s : atHome) {
                    imageWords.add(s);
                }
                break;
            case "About Me":
                PecsImages[] AboutMe = {
                        new PecsImages("Action Words", R.drawable.action_word_green,1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.add_word_green,1),
                        new PecsImages("Angry",R.drawable.angry,1),
                        new PecsImages("Anxious",R.drawable.anxious,1),
                        new PecsImages("Arm",R.drawable.arm,1),
                        new PecsImages("Back",R.drawable.back,1),
                        new PecsImages("Dont touch",R.drawable.donttouch,1),
                        new PecsImages("Ear",R.drawable.ear,1),
                        new PecsImages("Excited",R.drawable.excited,1),
                        new PecsImages("Eye",R.drawable.eye,1),
                        new PecsImages("Foot",R.drawable.foot,1),
                        new PecsImages("Go Away",R.drawable.goaway,1),
                        new PecsImages("Hair",R.drawable.hair,1),
                        new PecsImages("Hand",R.drawable.hand,1),
                        new PecsImages("Happy",R.drawable.happy,1),
                        new PecsImages("Head",R.drawable.head,1),
                        new PecsImages("Help",R.drawable.helping,1),
                        new PecsImages("Leg",R.drawable.leg,1),
                        new PecsImages("Love You",R.drawable.loveyou,1),
                        new PecsImages("Mouth",R.drawable.mouth,1),
                        new PecsImages("Nose",R.drawable.nose,1),
                        new PecsImages("Not Sore",R.drawable.notsore,1),
                        new PecsImages("Proud",R.drawable.proud,1),
                        new PecsImages("Sad",R.drawable.sad,1),
                        new PecsImages("Scared",R.drawable.scared,1),
                        new PecsImages("Sick",R.drawable.sick,1),
                        new PecsImages("Stomach",R.drawable.stomach,1),
                        new PecsImages("Sore",R.drawable.sore,1),
                };
                for (PecsImages s : AboutMe) {
                    imageWords.add(s);
                }
                break;
            case "Food And Drink":
                PecsImages[] FoodAndDrink = {
                        new PecsImages("Action Words", R.drawable.orange_action_word,1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.orange_plus,1),
                        new PecsImages("Cereal",R.drawable.cereal,1),
                        new PecsImages("Lunch",R.drawable.lunch,1),
                        new PecsImages("Dinner",R.drawable.dinner,1),
                        new PecsImages("Snacks",R.drawable.snacks,1),
                        new PecsImages("Drinks",R.drawable.drinks,1),
                        new PecsImages("Apple",R.drawable.apple,1),
                        new PecsImages("Banana",R.drawable.banana,1),
                        new PecsImages("Burger",R.drawable.burger,1),
                        new PecsImages("Cheese",R.drawable.cheese,1),
                        new PecsImages("Chicken",R.drawable.chicken,1),
                        new PecsImages("Chips",R.drawable.chips,1),
                        new PecsImages("Chocolate",R.drawable.chocolate,1),
                        new PecsImages("Crisps",R.drawable.crips,1),
                        new PecsImages("Hot Chocolate",R.drawable.hot_chocolate,1),
                        new PecsImages("Ice Cream",R.drawable.ice_cream,1),
                        new PecsImages("Milk",R.drawable.milk,1),
                        new PecsImages("Milkshake",R.drawable.milkshake,1),
                        new PecsImages("Orange Juice",R.drawable.orange_juice,1),
                        new PecsImages("Orange",R.drawable.orange,1),
                        new PecsImages("Popcorn",R.drawable.pop_corn,1),
                        new PecsImages("Salt and Pepper",R.drawable.salt_and_peper,1),
                        new PecsImages("Sandwich",R.drawable.sandwhich,1),
                        new PecsImages("Strawberry",R.drawable.strawberry,1),
                        new PecsImages("Tea",R.drawable.tea,1),
                        new PecsImages("Water",R.drawable.water,1)
                };
                for (PecsImages s : FoodAndDrink) {
                    imageWords.add(s);
                }
                break;
            case "Greetings":
                PecsImages[] Greetings = {
                        new PecsImages("Action Words", R.drawable.grey_action_word,1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.grey_plus,1),
                        new PecsImages("hello",R.drawable.hello,1),
                        new PecsImages("Good Morning",R.drawable.good_morning,1),
                        new PecsImages("Good Night",R.drawable.good_night,1),
                        new PecsImages("What's Your Name?",R.drawable.whats_your_name,1),
                        new PecsImages("See You Later",R.drawable.see_you_later,1),
                        new PecsImages("How Are You?",R.drawable.how_are_you,1),
                        new PecsImages("Would You Like To Join?",R.drawable.would_u_like_to_join,1),
                        new PecsImages("My Name Is",R.drawable.my_name_is,1),
                        new PecsImages("Nice To Meet You",R.drawable.nice_to_meet_you,1)
                };
                for (PecsImages s : Greetings) {
                    imageWords.add(s);
                }
                break;
            case "Leisure":
                PecsImages[] Leisure = {
                        new PecsImages("Action Words", R.drawable.pink_action_word,1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.pink_plus,1),
                        new PecsImages("Walk",R.drawable.walking,1),
                        new PecsImages("Basketball",R.drawable.basketball,1),
                        new PecsImages("Swing",R.drawable.swing,1),
                        new PecsImages("Playstation",R.drawable.playstation,1),
                        new PecsImages("TV",R.drawable.tv,1),
                        new PecsImages("Computer",R.drawable.compute,1),
                        new PecsImages("Football",R.drawable.football,1),
                        new PecsImages("Cinema",R.drawable.cinema,1),
                        new PecsImages("Cycling",R.drawable.cycling,1),
                        new PecsImages("Hula Hoop",R.drawable.hula_hoop,1),
                        new PecsImages("Skipping Rope",R.drawable.skipping_rope,1),
                        new PecsImages("Swimming",R.drawable.swimming,1),
                        new PecsImages("Tennis",R.drawable.tennis,1)
                };
                for (PecsImages s : Leisure) {
                    imageWords.add(s);
                }
                break;
            case "Animals":
                PecsImages[] animals = {
                        new PecsImages("Action Words", R.drawable.green_action_word, 1),
                        new PecsImages((getString(R.string.Add_Word)), R.drawable.green_plus,1),
                        new PecsImages("Bear", R.drawable.bear, 1),
                        new PecsImages("Cat", R.drawable.cat, 1),
                        new PecsImages("Chicken", R.drawable.chick, 1),
                        new PecsImages("Cow", R.drawable.cow, 1),
                        new PecsImages("Dog", R.drawable.dog, 1),
                        new PecsImages("Duck", R.drawable.duck, 1),
                        new PecsImages("Elephant", R.drawable.elephant, 1),
                        new PecsImages("Frog", R.drawable.frog, 1),
                        new PecsImages("Hippo", R.drawable.hippo, 1),
                        new PecsImages("Horse", R.drawable.horse, 1),
                        new PecsImages("Lion", R.drawable.lion, 1),
                        new PecsImages("Monkey", R.drawable.monkey, 1),
                        new PecsImages("Owl", R.drawable.owl, 1),
                        new PecsImages("Penguin", R.drawable.penguin, 1),
                        new PecsImages("Pig", R.drawable.pig, 1),
                        new PecsImages("Rabbit", R.drawable.rabbit, 1),
                        new PecsImages("Tiger", R.drawable.tiger, 1)
                };
                for (PecsImages s : animals) {
                    imageWords.add(s);
                }
                break;
            default:
                PecsImages action = new PecsImages("Action Words", R.drawable.action_word_green,1);
                imageWords.add(action);
                PecsImages add = new PecsImages((getString(R.string.Add_Word)), R.drawable.add_word_green,1);
                imageWords.add(add);
                break;
        }
        //The grid view is referenced and the list of image objects added using ImageAdapter
        //Created by Gareth Moore
        final GridView gridView = (GridView)findViewById(R.id.gridviewSecond);
        imageAdapter = new ImageAdapter(this, imageWords, category);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(this);

        //
        if (user != null) {
            /**
             * If the category is favourites a volley request getting the image objects from favourites is set up
             * If successful these image objects are added to the grid view
             * Created by Gareth Moore
             */
            if (category.equals("Favourites")) {
                //list = ops.getData(category, user);
                //String BASE_URL = "http://10.0.2.2:5000/project/getFavourite";
                String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getFavourite";
                String url = BASE_URL;

                HashMap<String, String> headers  = new HashMap<>();
                HashMap<String, String> body  = new HashMap<>();

                body.put("username", user);

                String contentType =  "application/json";
                VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

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
                                int id = jsonobject.getInt("id");
                                String username = jsonobject.getString("username");
                                byte [] images= Base64.decode(jsonobject.getString("image"),Base64.DEFAULT);
                                PecsImages pecsImages = new PecsImages(word, images, id, username);
                                imageWords.add(pecsImages);
                            }
                            imageAdapter.notifyDataSetChanged();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse){
                        System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                    }
                });
            } else {
                /**
                 * If it is any other catgegory then a volley request is set up to return image objects
                 * from the images table with a specific category and username
                 * Created by Gareth Moore
                 */
                //list = ops.getData(category, user);
                //String BASE_URL = "http://10.0.2.2:5000/project/return";
                String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/return";
                String url = BASE_URL;

                HashMap<String, String> headers  = new HashMap<>();
                HashMap<String, String> body  = new HashMap<>();

                body.put("category", category);
                body.put("username", user);

                String contentType =  "application/json";
                VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

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
                                imageWords.add(pecsImages);
                            }
                            imageAdapter.notifyDataSetChanged();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ErrorResponse errorResponse){
                        System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
                    }
                });
            }
        }

        gridView.setOnItemLongClickListener(this);

        //The recycler view is setup
        //Created by Gareth Moore
        sentenceWords = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        mAdapter = new SentenceBuilderAdapter(sentenceWords);
        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManage);
        recyclerView.setAdapter(mAdapter);

        //onClickListener setup with delete button. This allows for an image to be deleted from
        //the sentence builder.
        //Created by Gareth Moore
        ImageButton cancelButton = (ImageButton) findViewById(R.id.deleteB2);
        cancelButton.setOnClickListener(this);
        cancelButton.setOnLongClickListener(this);

        //check for TTS data
        //Created by Gareth Moore
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        //The sentence builder is populated with any image objects in sentence table
        //Created by Gareth Moore
        List<PecsImages> sentenceList = new ArrayList<>();
        sentenceList = ops.getSentenceData();
        sentenceWords.addAll(sentenceList);
        mAdapter.notifyDataSetChanged();

    }

    /**
     *Method adds functionality to the item clicks within the GridView.
     * @param parent
     * @param view
     * @param position
     * @param id
     * Created by Gareth Moore
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridviewSecond:
                PecsImages image = imageWords.get(position);
                speakWords(image.getWord());
                //if action words selected brings up action words pop up
                if (image.getWord().equals("Action Words")) {
                    Intent actionWords = new Intent(getApplicationContext(), ActionWords.class);
                    startActivity(actionWords);
                } else if (image.getWord().equals("Add Word") && user != null) {
                    //If add word is selected and user is not null takes user to uploader page
                    Intent upload = new Intent(getApplicationContext(), Uploader.class);
                    upload.putExtra("project.allstate.speakitvisualcommunication.User", user);
                    upload.putExtra("project.allstate.speakitvisualcommunication.page", category);
                    startActivity(upload);
                } else if(image.getWord().equals("Add Word") && user == null) {
                    //If add word is selected and user is null

                    if (logName == null) {
                        //If not logged takes user to login page
                        Toast.makeText(this, "Please Login",Toast.LENGTH_SHORT ).show();
                        Intent logIntent = new Intent(SecondScreen.this, LoginScreen.class);
                        startActivity(logIntent);
                    } else if (logName != null) {
                        //If logged in takes user to user select page
                        Toast.makeText(this, "Please select user",Toast.LENGTH_SHORT ).show();
                        Intent userIntent = new Intent(SecondScreen.this, UserSelect.class);
                        startActivity(userIntent);
                    }
                } else {
                    //if any other word is selected adds item to the sentence builder
                    if (image.getNumber() == 1) {
                        //if a drawable has to be converted
                        Drawable drawable = getResources().getDrawable(image.getImage());
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitMapData = stream.toByteArray();
                        image.setImages(bitMapData);
                    }
                    ops.insertSentenceData(image.getWord(),image.getImages());
                    List<PecsImages> list2 = ops.getSentenceData();
                    sentenceWords.clear();
                    mAdapter.notifyDataSetChanged();
                    sentenceWords.addAll(list2);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.recyclerView2:
                PecsImages sentenceImage = sentenceWords.get(position);
                speakWords(sentenceImage.getWord());

        }

    }

    /**
     * Adds onLongClick functionality
     * @param adapterView
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        boolean status;
        switch (adapterView.getId()) {
            case R.id.gridviewSecond:
                final PecsImages image = imageWords.get(position);
                if (image.getNumber() != 1 && !category.equals("Favourites")) {
                    CharSequence[] items = {"Update", "Delete", "Add to Favourites"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SecondScreen.this);

                    dialog.setTitle("Choose an action");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {

                        /**
                         * @param dialog
                         * @param item
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0) {
                                // show dialog update at here
                                showDialogUpdate(SecondScreen.this, image.getId());
                            } else if (item == 1) {
                                showDialogDelete(image.getId());
                            } else if (item == 2) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(image.getImages(), 0, image.getImages().length, options);
                                addToFavourites(bitmap, image.getWord());
                            }
                        }
                    });
                    dialog.show();
                    status = true;
                } else if (category.equals("Favourites") && !image.getWord().equals("Action Words")) {
                    CharSequence[] items = {"Delete"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SecondScreen.this);

                    dialog.setTitle("Choose an action");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {

                        /**
                         * @param dialog
                         * @param item
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0) {
                                showDialogDeleteFavourite(image.getId());
                            }
                        }
                    });
                    dialog.show();
                    status = true;
                } else if (image.getNumber() == 1 && !category.equals("Favourites") && !image.getWord().equals("Action Words") && !image.getWord().equals("Add Word") && logName != null && user != null) {
                    CharSequence[] items = {"Add to Favourites"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SecondScreen.this);

                    dialog.setTitle("Choose an action");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {

                        /**
                         * @param dialog
                         * @param item
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0) {
                                Drawable drawable = getResources().getDrawable(image.getImage());
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                addToFavourites(bitmap, image.getWord());
                            }
                        }
                    });
                    dialog.show();
                    status = true;
                } else {
                    status = false;
                }
                break;
            case R.id.recyclerView2:
                PecsImages removeImage = sentenceWords.get(position);
                sentenceWords.remove(removeImage);
                mAdapter.notifyDataSetChanged();
                status = true;
                break;
            default:
                status = false;
                break;
        }
        return status;
    }

    /**
     * Method called when delete button for the sentence builder is clicked
     * If the sentence builder contains items the item last in the list will be deleted
     * Created by Gareth Moore
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteB2:
                if (sentenceWords.size() > 0) {
                    ops.deleteSentenceData(sentenceWords.get(sentenceWords.size()-1).getId());
                    sentenceWords.remove(sentenceWords.size()-1);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    /**
     * If the delete button for sentence builder is long clicked method will be called
     * This will delete all items in the sentence builder.
     * Created by Gareth Moore
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.deleteB2:
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
     * Method used to speak
     * Created by Gareth Moore
     * @param speech
     */
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }


    /**
     * Method sets up the language for text to speech
     * Created by Gareth Moore
     * @param initStatus
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
     * Inflates the menu layout to create menu icon in toolbar
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
     * Method for the selection of the home button, user select item and play icon
     * within the toolbar
     * Created by Gareth Moore
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
            case R.id.account:
                if (logName == null) {
                    Intent loginIntent = new Intent(SecondScreen.this, LoginScreen.class);
                    startActivity(loginIntent);
                } else {
                    Intent intent = new Intent(SecondScreen.this, UserSelect.class);
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
        }
        return super.onOptionsItemSelected(item);
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
        VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

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
//                            Uploader.imageViewToByte(pecsView), category,
//                            id
//                    );
                    /**
                     * Volley request used to upadate the data within the database
                     * Created by Gareth Moore
                     */
                    final Integer userId = id;
                    //String BASE_URL = "http://10.0.2.2:5000/project/updateData";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/updateData";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", userId.toString());
                    body.put("word", edtName.getText().toString());
                    body.put("category", category);
                    Bitmap bitmap = ((BitmapDrawable)pecsView.getDrawable()).getBitmap();
                    body.put("images", BitMapToString(bitmap));

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.PUT, contentType, url, headers, body);

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
                            Iterator<PecsImages> iterator = imageWords.iterator();
                            while (iterator.hasNext()) {
                                if(iterator.next().getId() == id) {
                                    iterator.remove();
                                    imageAdapter.notifyDataSetChanged();
                                    //PecsImages item = ops.getItem(id);
                                    //String BASE_URL = "http://10.0.2.2:5000/project/getOne";
                                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getOne";
                                    String url = BASE_URL;

                                    HashMap<String, String> headers  = new HashMap<>();
                                    HashMap<String, String> body  = new HashMap<>();

                                    body.put("id", userId.toString());

                                    String contentType =  "application/json";
                                    VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

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
                                                imageWords.add(pecsImages);
                                                imageAdapter.notifyDataSetChanged();
                                                Toast.makeText(SecondScreen.this, "Update Success ", Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(SecondScreen.this);

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
                    //ops.deleteData(idPecs);
                    Integer deleteId = idPecs;
                    //String BASE_URL = "http://10.0.2.2:5000/project/deleteData";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteData";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", deleteId.toString());

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);
                            Toast.makeText(SecondScreen.this, "Success ", Toast.LENGTH_SHORT).show();
                            Iterator<PecsImages> iterator = imageWords.iterator();
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
     * /**
     * Method used to add the item to the favourites table. Voley request is used to add the information
     * If successful a message will be displayed telling the user the item was added successfully
     * Created by Gareth Moore
     * @param image
     *    - The image to be added
     * @param word
     *    - The word to be added
     */
    public void addToFavourites(Bitmap image, String word) {
        //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/addFavourite";
        //String BASE_URL = "http://10.0.2.2:5000/project/addFavourite";
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/addFavourite";
        String url = BASE_URL;

        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        body.put("id", null);
        body.put("word", word);
        body.put("username", user);
        body.put("image", BitMapToString(image));

        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);
                Toast.makeText(SecondScreen.this, "Added Successfully ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });
    }

    /**
     * Method used to delete an item if the category is favourites
     * Volley request is used to delete an item in the database with a particular id
     * If successful the item is then removed from the list used to populate the grid view
     * Created by Gareth Moore
     * @param idPecs
     *   - The id of the item
     */
    private void showDialogDeleteFavourite(final int idPecs) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(SecondScreen.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //ops.deleteData(idPecs);
                    Integer deleteId = idPecs;
                    //String BASE_URL = "http://10.0.2.2:5000/project/deleteFavourite";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteFavourite";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("id", deleteId.toString());

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(SecondScreen.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);
                            Toast.makeText(SecondScreen.this, "Success ", Toast.LENGTH_LONG).show();
                            Iterator<PecsImages> iterator = imageWords.iterator();
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
     * Select whether to take a photo using camera or from gallery
     */
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SecondScreen.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SecondScreen.this);

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
     * Checks the data is available for text to speech
     * Created by Gareth Moore
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
//            }
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
     * Convert bitmap to string
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
     * onResume method open SQLite database and updates the sentence builder
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
     *onStop method closes SQLite database
     * Created by Gareth Moore
     */
    @Override
    public void  onStop() {
        super.onStop();
        ops.close();
    }

    /**
     * When the activity is finished the method will close the  SQLite database.
     * Closes the text to speech preventing data leaks
     * Created by Gareth Moore
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Calling the close method to close the database.
        ops.close();
        if (myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }

    }

}
