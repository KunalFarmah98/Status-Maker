package com.apps.kunalfarmah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity" ;
    private RelativeLayout mainLayout;
    private ImageView iv;
    private EditText et;
    private Button done;
    private LinearLayout backgroundLL;
    private ImageButton whatsApp;
    private TextView status;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch bckStyle;
    private boolean isPlain;
    public static int ind;
    private ArrayList<Integer> plainBg, imageBg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);
        et = findViewById(R.id.et_status);
        iv = findViewById(R.id.iv_background);
        done = findViewById(R.id.done);
        bckStyle = findViewById(R.id.bck_type);
        backgroundLL = findViewById(R.id.iv_background_style);
        whatsApp = findViewById(R.id.share);
        status = findViewById(R.id.tv_status);
        isPlain = true;
        ind = 0;

        // setting up backgrounds, can be extended further by adding more images, colors
        plainBg = new ArrayList<>();
        imageBg = new ArrayList<>();

        plainBg.add(R.color.colorPrimary);
        plainBg.add(R.color.green);
        plainBg.add(R.color.orange);
        plainBg.add(R.color.red);
        plainBg.add(R.color.hue);

        imageBg.add(R.drawable.bck1);
        imageBg.add(R.drawable.bck2);
        imageBg.add(R.drawable.bck3);
        imageBg.add(R.drawable.bck4);
        imageBg.add(R.drawable.bck5);

        et.setTextColor(getResources().getColor(R.color.white));
        et.setHintTextColor(getResources().getColor(R.color.white));

        Toast.makeText(MainActivity.this, R.string.swipe_option,Toast.LENGTH_SHORT).show();

        // implementing the gesture listener for the background
        OnSwipeTouchListener swipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                if (ind > 0) {
                    --ind;
                    if (isPlain) {
                        iv.setBackgroundColor(getResources().getColor(plainBg.get(ind)));
                    } else {
                        iv.setBackgroundResource(imageBg.get(ind));

                    }
                }
            }
            public void onSwipeLeft() {
                if (ind < 5) {
                    ++ind;
                    if (isPlain) {
                        iv.setBackgroundColor(getResources().getColor(plainBg.get(ind)));
                    } else {
                        iv.setBackgroundResource(imageBg.get(ind));
                    }
                }
            }
        };

        iv.setOnTouchListener(swipeTouchListener);

        // setting option for plain or image background
        bckStyle.setChecked(false);
            bckStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ind=0;
                if(!b){
                    isPlain = true;
                    iv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                else{
                    isPlain = false;
                    iv.setBackgroundResource(R.drawable.bck1);
                }
            }
        });

        done.setOnClickListener(this);
        whatsApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.share:
                // checking storage permissions
                if(!isStoragePermissionGranted())return;
                whatsApp.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, R.string.share_wa,Toast.LENGTH_SHORT).show();

                // converting relative layout to bitmap
                mainLayout.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mainLayout.getDrawingCache());
                mainLayout.setDrawingCacheEnabled(false);

                // creating whatsApp share intent
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setPackage(getString(R.string.package_wa));
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(bitmap));
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_status));
                whatsappIntent.setType("image/*");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, R.string.app_not_found, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.done:
                // status can't be empty
                if(et.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, R.string.error1,Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, R.string.ready_share,Toast.LENGTH_SHORT).show();
                // disabling gestures
                iv.setEnabled(false);

                et.setEnabled(false);
                et.clearComposingText();
                et.setVisibility(View.GONE);

                // replacing edittext with textview
                status.setText(et.getText());
                status.setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
                backgroundLL.setVisibility(View.GONE);
                whatsApp.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(done.getVisibility()==View.GONE){
            reset();
        }
        else{
            super.onBackPressed();
        }
    }

    void reset(){
        ind=0;
        iv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        iv.setEnabled(true);
        et.setText(null);
        bckStyle.setChecked(false);
        whatsApp.setVisibility(View.GONE);
        done.setVisibility(View.VISIBLE);
        status.setVisibility(View.GONE);
        et.setVisibility(View.VISIBLE);
        backgroundLL.setVisibility(View.VISIBLE);
        et.setEnabled(true);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    // continue work that had asked for permission
                    whatsApp.callOnClick();
                }else{
                    Toast.makeText(MainActivity.this,"Please Provide the Permission to Continue",Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                }else{
                    Toast.makeText(MainActivity.this,"Please Provide the Permission to Continue",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        // getting a safe path for file which will be overwritten on every share as it
        // is stored temporarily for sharing to make the app run in SDK 27+
        File file = getOutputMediaFile();
        if (file == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        Uri uri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", file);
        return uri;
    }

    private  File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File mediaFile;
        String mImageName="status.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}