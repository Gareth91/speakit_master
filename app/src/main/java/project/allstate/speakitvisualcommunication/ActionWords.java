package project.allstate.speakitvisualcommunication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gareth Moore
 */
public class ActionWords extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {

    //TTS object
    private TextToSpeech myTTS;

    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    /**
     * List of image items
     */
    private List<PecsImages> imageWords;

    /**
     * Instance of DatabaseOperations class
     */
    private DatabaseOperations ops;


    /**
     * Image adapter used to populate grid view
     */
    private ImageAdapter imageAdapter;

    /**
     * onCreate method called when screen is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_words);

        ops = new DatabaseOperations(getApplicationContext());
        ops.open();

        //Set back button in the bar at the top of screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        /**
         * Makes the activity appear as a pop up over the previous activity
         * Created by Gareth Moore
         */
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        int width = display.widthPixels;
        int height = display.heightPixels;
        getWindow().setLayout((int)(width*0.9), (int)(height*0.9));

        imageWords = new ArrayList<>();
        imageWords.clear();

        /**
         * Array containing PecsImages objects which take an image from
         * drawable resource
         *
         * Content created by Ashley 07/08/17
         */
        PecsImages[] actionWords = {

                new PecsImages("A", R.drawable.a, 1),
                new PecsImages("Am", R.drawable.am, 1),
                new PecsImages("Can", R.drawable.can, 1),
                new PecsImages("Do", R.drawable.do_word, 1),
                new PecsImages("Eat", R.drawable.eat, 1),
                new PecsImages("Get", R.drawable.get, 1),
                new PecsImages("Give", R.drawable.give, 1),
                new PecsImages("Go", R.drawable.go, 1),
                new PecsImages("Gone", R.drawable.gone, 1),
                new PecsImages("Have", R.drawable.have, 1),
                new PecsImages("He", R.drawable.he, 1),
                new PecsImages("Help", R.drawable.help, 1),
                new PecsImages("I", R.drawable.i, 1),
                new PecsImages("Is", R.drawable.is, 1),
                new PecsImages("It", R.drawable.it, 1),
                new PecsImages("Know", R.drawable.know, 1),
                new PecsImages("Like", R.drawable.like, 1),
                new PecsImages("Make", R.drawable.make, 1),
                new PecsImages("My", R.drawable.my, 1),
                new PecsImages("Need", R.drawable.need, 1),
                new PecsImages("No", R.drawable.no, 1),
                new PecsImages("Play", R.drawable.play, 1),
                new PecsImages("Put", R.drawable.put, 1),
                new PecsImages("Say", R.drawable.say, 1),
                new PecsImages("See", R.drawable.see, 1),
                new PecsImages("She", R.drawable.she, 1),
                new PecsImages("Sorry", R.drawable.sorry, 1),
                new PecsImages("Stop", R.drawable.stop, 1),
                new PecsImages("The", R.drawable.the, 1),
                new PecsImages("They", R.drawable.they, 1),
                new PecsImages("Think", R.drawable.think, 1),
                new PecsImages("Want", R.drawable.want, 1),
                new PecsImages("We", R.drawable.we, 1),
                new PecsImages("Will", R.drawable.will, 1),
                new PecsImages("Yes", R.drawable.yes, 1),
                new PecsImages("You", R.drawable.you, 1)

        };

        for (PecsImages s : actionWords) {
            imageWords.add(s);

        }

        //Grid view is referenced and items added using image adapter
        //Created by Gareth Moore
        final GridView gridView = (GridView)findViewById(R.id.gridviewThird);
        imageAdapter = new ImageAdapter(this, imageWords, "Action Words");
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(this);

        //check for TTS data
        //Created by Gareth Moore
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    /**
     * Method for the selection of the home button
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up item clicks on items within the grid view
     * Created by Gareth Moore
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        PecsImages image = imageWords.get(position);
        speakWords(image.getWord());
        //
        if (image.getNumber() == 1) {
            //If a drawable has to be converted
            Drawable drawable = getResources().getDrawable(image.getImage());
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitMapData = stream.toByteArray();
            image.setImages(bitMapData);
        }
        //Adds the item to the sentence table
        ops.insertSentenceData(image.getWord(),image.getImages());
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
     * Act on result of TTS data check
     * Created by Gareth Moore
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    /**
     * Sets up the text to speech language
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
     * onResume method
     * Opens SQlite database
     * Created by Gareth Moore
     */
    public void onResume() {
        super.onResume();
        //Open database
        ops.open();

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
     * When the activity is finished the method will close the  SQLite database and
     * close the text to speech
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
