package edu.wccnet.mycameraapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG=CameraActivity.class.getCanonicalName();

    private final int REQUEST_IMAGE_CAPTURE=1;

    private ImageView imageViewThumbnail;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageViewThumbnail=findViewById(R.id.imageViewThumbnail);
    }

    public void takePhoto( View clickedView ) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Note the checks in here -- one :: we are calling on resolveActivity, this ensures
        // that if there is more than one activity only the first is chosen.  This prevents
        // our user interface from having additional dialogs displayed between our application
        // and the use of the camera

        // Also note the check for null.  We are also checking to make sure we get some Activity
        // back!  Checking to make sure devices have a particular capability is always a
        // good choice.  The alternative is an application crash

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if( extras != null && extras.get("data") != null ) {
                Toast.makeText(this, "We have thumbnail, displaying", Toast.LENGTH_SHORT).show();

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageViewThumbnail.setImageBitmap(imageBitmap);
            } else {
                Toast.makeText(this, "There was no thumbnail.. displaying file", Toast.LENGTH_SHORT).show();

                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.wccnet.mycameraapplication",
                        new File( mCurrentPhotoPath));
                imageViewThumbnail.setImageURI(photoURI);
            }
        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.wccnet.mycameraapplication",
                        photoFile);

                // Take the photo -- This is what is telling the camera intent to save to a file
                // Try removing this

                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Creates a "File" so that we have a place to save the image.  Full sizes images are too big
     * to be passed around, so we need to do it this way
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

}
