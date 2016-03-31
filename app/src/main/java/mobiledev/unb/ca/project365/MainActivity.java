package mobiledev.unb.ca.project365;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private Button mTodaysPhotoBtn;
    private Button mViewCalendarBtn;
    private String mCurrentPhotoPath;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG ="Debug MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

       
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        mTodaysPhotoBtn = (Button) findViewById(R.id.btnTodaysPhoto);
        mTodaysPhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mViewCalendarBtn = (Button) findViewById(R.id.btnViewCalendar);
        mViewCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewCalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                // create a temporary image file that will be saved into a permanent location in the SaveNewPhotoActivity class
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                Log.i(TAG, "Photo file was not created. Error: "+ex.getMessage());
            }

            // Continue only if the File was successfully created

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createTempImageFile() throws IOException {
        // Create a unique image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String tempPhotoFileName = "PROJECT365_temp_" + timeStamp + "_";
        File tempStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create a new file

        File tempPhoto = File.createTempFile(tempPhotoFileName, ".jpg", tempStorageDir);
        mCurrentPhotoPath = tempPhoto.getAbsolutePath();
        Log.i(TAG, "The photo was saved in the following location: " + mCurrentPhotoPath);
        return tempPhoto;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i(TAG, "Switching to new activity");

            Intent intent = new Intent(MainActivity.this, SaveNewPhotoActivity.class);

            // Send the path of the new temporary photo to the SaveNewPhoto activity

            intent.putExtra(Photo.PHOTO_PATH,mCurrentPhotoPath);
            startActivity(intent);
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
