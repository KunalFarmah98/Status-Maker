package com.apps.kunalfarmah;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mainLayout;
    private ImageView iv;
    private EditText et;
    private Button done;
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
        isPlain = true;
        ind = 0;

        plainBg = new ArrayList<>();
        imageBg = new ArrayList<>();

        plainBg.add(R.color.colorPrimary);
        plainBg.add(R.color.green);
        plainBg.add(R.color.yellow);
        plainBg.add(R.color.red);
        plainBg.add(R.color.hue);

        imageBg.add(R.drawable.bck1);
        imageBg.add(R.drawable.bck2);
        imageBg.add(R.drawable.bck3);
        imageBg.add(R.drawable.bck4);
        imageBg.add(R.drawable.bck5);

        OnSwipeTouchListener swipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                if (ind > 0) {
                    --ind;
                    if (isPlain) {
                        iv.setBackgroundColor(getResources().getColor(plainBg.get(ind)));
                        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(plainBg.get(ind)));
                    } else {
                        iv.setBackgroundResource(imageBg.get(ind));
                        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorPrimary));

                    }
                }
            }

            public void onSwipeLeft() {
                if (ind < 5) {
                    ++ind;
                    if (isPlain) {
                        iv.setBackgroundColor(getResources().getColor(plainBg.get(ind)));
                        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(plainBg.get(ind)));
                    } else {
                        iv.setBackgroundResource(imageBg.get(ind));
                        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorPrimary));
                    }
                }
            }
        };

        et.setTextColor(getResources().getColor(R.color.white));
        et.setHintTextColor(getResources().getColor(R.color.white));
        iv.setOnTouchListener(swipeTouchListener);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mainLayout.getDrawingCache());
                mainLayout.setDrawingCacheEnabled(false);

                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check Out My Staus");
                whatsappIntent.setDataAndType(getImageUri(MainActivity.this, bitmap), "*/*");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Whatsapp has not been installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ind = 0;
        switch (item.getItemId()) {
            case R.id.plain:
                isPlain = true;
                iv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(plainBg.get(ind)));
                return true;
            case R.id.image:
                isPlain = false;
                iv.setBackgroundResource(R.drawable.bck1);
                getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorPrimary));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}