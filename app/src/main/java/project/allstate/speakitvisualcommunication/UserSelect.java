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
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import project.allstate.speakitvisualcommunication.volley.ErrorResponse;
import project.allstate.speakitvisualcommunication.volley.VolleyCallBack;
import project.allstate.speakitvisualcommunication.volley.VolleyHelp;
import project.allstate.speakitvisualcommunication.volley.VolleyRequest;

import static android.R.attr.id;

/**
 * Class involved in the functionality on the user select page
 * Created by Gareth Moore
 */
public class UserSelect extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    /**
     * Add button
     */
    private Button add;

    /**
     * Database operations class
     */
    private DatabaseOperations ops;

    /**
     *
     */
    final int REQUEST_CODE_GALLERY = 1;

    /**
     *
     */
    final int REQUEST_IMAGE_CAPTURE = 0;

    /**
     * GridView
     */
    private GridView gridView;

    /**
     * A list of user profiles
     */
    private List<User> userList = new ArrayList<>();

    /**
     * Adapter class used to populate the grid view
     */
    private UserAdapter userAdapter;

    /**
     * ImageView
     */
    private ImageView imageView;

    /**
     * The user chosen
     */
    private String userChosen;

    /**
     * The login name for the account
     */
    String logName = null;

    /**
     * onCreate method called when screen is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        //Set back button in the bar at the top of screen
        //Created by Gareth Moore
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        //Click event set up on the create user button
        //Created by Gareth Moore
        add = (Button) findViewById(R.id.buttonAddUser);
        add.setOnClickListener(this);

        ops = new DatabaseOperations(getApplicationContext());
        ops.open();

        //Gets the login name from the data holder
        //Created by Gareth Moore
        logName = DataHolder.getInstance().getLogin();

        //Sets up the grid view
        //Created by Gareth Moore
        List<User> list = new ArrayList<>();
        //list = ops.getUsers(logName);
        //userList.addAll(list);
        gridView = (GridView)findViewById(R.id.userlistView);
        userAdapter = new UserAdapter(this, userList);
        gridView.setAdapter(userAdapter);

        /**
         * Volley request returns all the user profiles under a particular account name
         * If successful adds these to the grid view using the adapter class
         * If the nuber of profile is 6 then the create user button is disabled
         * Created by Gareth Moore
         */
        //String BASE_URL = "http://10.0.2.2:5000/project/getUsers";
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getUsers";
        String url = BASE_URL;

        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        body.put("accountusername", logName);

        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);

                JSONArray jsonarray = null;
                try {
                    jsonarray = new JSONArray(result);

                    for (int loop = 0; loop < jsonarray.length(); loop++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(loop);
                        String username = jsonobject.getString("username");
                        byte [] image= Base64.decode(jsonobject.getString("image"),Base64.DEFAULT);
                        User users = new User(username, image);
                        userList.add(users);
                    }
                    userAdapter.notifyDataSetChanged();
                    if (jsonarray.length() >= 6) {
                        add.setEnabled(false);
                    } else {
                        add.setEnabled(true);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ErrorResponse errorResponse){
                System.out.print("CALLBACK ERROR: " + errorResponse.getMessage());
            }
        });

        //item click and long item click
        //Created by Gareth Moore
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

    }

    /**
     * Method takes user to the create user page when create user button is selected
     * Created by Gareth Moore
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(UserSelect.this, CreateUser.class);
        intent.putExtra("project.allstate.speakitvisualcommunication.login", logName);
        startActivity(intent);
    }

    /**
     * When a user profile is selected in grid view returns user to main screen passing that username
     * in an intent to the mains screen
     * Created by Gareth Moore
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        User user = userList.get(position);
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("project.allstate.speakitvisualcommunication.username", user.getUserName());
        intent.putExtra("project.allstate.speakitvisualcommunication.Login", logName);
        startActivity(intent);
    }

    /**
     * When a user profile is long clicked gives the option to update or delete
     * @param adapterView
     * @param view
     * @param position
     * @param l
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final User user = userList.get(position);
        CharSequence[] items = {"Update", "Delete"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserSelect.this);

        dialog.setTitle("Choose an action");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    //show dialog update at here
                    showDialogUpdate(UserSelect.this, user.getUserName());
                } else {
                    showDialogDelete(user.getUserName());
                }
            }
        });
        dialog.show();
        return true;
    }

    /**
     * Method called when user wants to update the user profile
     * Created by Gareth Moore
     * @param activity
     * @param userName
     */
    private void showDialogUpdate(Activity activity, final String userName) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_user);
        dialog.setTitle("Update");

        imageView = (ImageView) dialog.findViewById(R.id.userImage);
        final TextView edtName = (TextView) dialog.findViewById(R.id.userName);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate2);
        ImageButton back = (ImageButton)dialog.findViewById(R.id.dialogClose2);

        /**
         * Volley request get the details of the user selected from the database to display in the pop up
         * Created by Gareth Moore
         */
        //String BASE_URL = "http://10.0.2.2:5000/project/getOneUser";
        String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getOneUser";
        String url = BASE_URL;

        HashMap<String, String> headers  = new HashMap<>();
        HashMap<String, String> body  = new HashMap<>();

        body.put("username", userName);

        String contentType =  "application/json";
        VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

        request.serviceJsonCall(new VolleyCallBack(){
            @Override
            public void onSuccess(String result){
                System.out.print("CALLBACK SUCCESS: " + result);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String username = jsonObject.getString("username");
                    byte [] image= Base64.decode(jsonObject.getString("image"),Base64.DEFAULT);
                    User users = new User(username, image);
                    edtName.setText(users.getUserName());
                    edtName.setTextSize(22);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(users.getImage(), 0, users.getImage().length);
                    imageView.setImageBitmap(bitmap);
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
               selectImage();
            }
        });

        /**
         * When update button os clicked a volley request is used to update the data for that user profile
         * Created by Gareth Moore
         */
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    ops.updateUser(
//                            edtName.getText().toString().trim(),
//                            UserSelect.imageViewToByte(imageView), logName
//                    );
                    final Integer userId = id;
                    //String BASE_URL = "http://10.0.2.2:5000/project/updateUser";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/updateUser";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("username", edtName.getText().toString());
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    body.put("image", BitMapToString(bitmap));

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.PUT, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            System.out.print("CALLBACK SUCCESS: " + result);

                            /**
                             * If successful the old user profile is removed from the grid view.
                             * The new profile is then returned using a volley request and this is added to the grid view.
                             * Created by Gareth Moore
                             */
                            Iterator<User> iterator = userList.iterator();
                            while (iterator.hasNext()) {
                                if(iterator.next().getUserName() == userName) {
                                    iterator.remove();
                                    userAdapter.notifyDataSetChanged();
                                    //String BASE_URL = "http://10.0.2.2:5000/project/getOneUser";
                                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getOneUser";
                                    String url = BASE_URL;

                                    HashMap<String, String> headers  = new HashMap<>();
                                    HashMap<String, String> body  = new HashMap<>();

                                    body.put("username", userName);

                                    String contentType =  "application/json";
                                    VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                                    request.serviceJsonCall(new VolleyCallBack(){
                                        @Override
                                        public void onSuccess(String result){
                                            System.out.print("CALLBACK SUCCESS: " + result);

                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(result);
                                                String username = jsonObject.getString("username");
                                                byte [] image= Base64.decode(jsonObject.getString("image"),Base64.DEFAULT);
                                                User user = new User(username, image);
                                                userList.add(user);
                                                userAdapter.notifyDataSetChanged();
                                                Toast.makeText(UserSelect.this, "Update Success ", Toast.LENGTH_LONG).show();
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
     *
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UserSelect.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(UserSelect.this);

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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_IMAGE_CAPTURE)
                onCaptureImageResult(data);
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
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        imageView.setImageBitmap(thumbnail);
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Method deletes an image from the list used to populate the GridView
     * Created by Gareth Moore
     */
    private void showDialogDelete(final String userName) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(UserSelect.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * Volley request deleted user profile from the database
                 * Created by Gareth Moore
                 */
                try {
                    //String BASE_URL = "http://10.0.2.2:5000/project/deleteUser";
                    String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/deleteUser";
                    String url = BASE_URL;

                    HashMap<String, String> headers  = new HashMap<>();
                    HashMap<String, String> body  = new HashMap<>();

                    body.put("username", userName);

                    String contentType =  "application/json";
                    VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                    request.serviceJsonCall(new VolleyCallBack(){
                        @Override
                        public void onSuccess(String result){
                            /**
                             * When the user profile is successfully deleted the grid view is updated
                             * The number of users is then checked. If below five the create user button is enabled
                             * Created by Gareth Moore
                             */
                            System.out.print("CALLBACK SUCCESS: " + result);
                            Toast.makeText(UserSelect.this, "Success ", Toast.LENGTH_LONG).show();
                            Iterator<User> iterator = userList.iterator();
                            while (iterator.hasNext()) {
                                if(iterator.next().getUserName() == userName) {
                                    iterator.remove();
                                    userAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            //String BASE_URL = "http://awsandroid.eu-west-1.elasticbeanstalk.com/project/getUsers";
//                            String BASE_URL = "http://10.0.2.2:5000/project/getUsers";
                            String BASE_URL = "http://awsandroid-env.gxjm8mxvzx.eu-west-1.elasticbeanstalk.com/project/getUsers";
                            String url = BASE_URL;

                            HashMap<String, String> headers  = new HashMap<>();
                            HashMap<String, String> body  = new HashMap<>();

                            body.put("accountusername", logName);

                            String contentType =  "application/json";
                            VolleyRequest request =   new VolleyRequest(UserSelect.this, VolleyHelp.methodDescription.POST, contentType, url, headers, body);

                            request.serviceJsonCall(new VolleyCallBack(){
                                @Override
                                public void onSuccess(String result){
                                    System.out.print("CALLBACK SUCCESS: " + result);

                                    JSONArray jsonarray = null;
                                    try {
                                        jsonarray = new JSONArray(result);

                                        if (jsonarray.length() >= 6) {
                                            add.setEnabled(false);
                                        } else {
                                            add.setEnabled(true);
                                        }
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
     * Method for the selection of the home button or the logout button
     * If logout is selected the logName variable is set to null
     * Created by Gareth Moore
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(UserSelect.this, MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                DataHolder.getInstance().setLogin(null);
                Intent intent1 = new Intent(UserSelect.this, MainScreen.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method converts bitmap to string
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
     * Inflates the menu layout
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }



    /**
     * onResume method
     *
     */
    public void onResume() {
        super.onResume();
        //Open database

    }

    /**
     *onStop method closes the event listener
     */
    @Override
    public void  onStop() {
        super.onStop();
        ops.close();
    }

    /**
     * When the activity is finished the method will close the  SQLite database.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Calling the close method to close the database.
        ops.close();
    }


}
