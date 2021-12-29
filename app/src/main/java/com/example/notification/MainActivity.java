package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.SearchManager;
import android.widget.EditText;

import android.widget.ImageView;
import android.util.Log;
import android.provider.MediaStore;
import android.os.Environment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RadioGroup;

import java.io.Console;
import java.io.File;



import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    Button b1;
    EditText editText;
    Button btnSearch;

    /******/
    File directory;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;

    final int REQUEST_CODE_PHOTO = 1;
    final int REQUEST_CODE_VIDEO = 2;

    final String TAG = "myLogs";

    ImageView ivPhoto;
    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=findViewById(R.id.button);

        editText = findViewById(R.id.editText);
        btnSearch = findViewById(R.id.btnSearch);

        /***/
        createDirectory();
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        /***/

        notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setAutoCancel(false)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("UTM")
                        .setContentText("Accesați site-ul universității.")
                        .setTimeoutAfter(10000)
                        .setPriority(PRIORITY_HIGH);

                createChannelIfNeeded(notificationManager);
                notificationManager.notify(NOTIFY_ID, notificationBuilder.build());


            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String term = editText.getText().toString();
                intent.putExtra(SearchManager.QUERY, term);
                startActivity(intent);
            }
        });

    }

    /*********/
    public void onClickPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    public void onClickVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_VIDEO));
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    public void onCameraTypeChanged(RadioGroup radioGroup, int id) {
        switch (id) {
            case 0:
                System.out.println("will capture visual content from rear camera");
                break;
            case 1:
                System.out.println("will capture content from front camera");
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            ivPhoto.setImageBitmap(bitmap);
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                break;
            case TYPE_VIDEO:
                file = new File(directory.getPath() + "/" + "video_"
                        + System.currentTimeMillis() + ".mp4");
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }
    /********/


    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}