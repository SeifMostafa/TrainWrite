package com.example.maged.testgv;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maged.testgv.model.Direction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Direction> mUserGuidedVectors;
    public static ArrayList<Point> mTouchedPoints;

    private final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    Stack<String> chars;
    Stack<String> charPositions;
    String current;
    TextView wordview;
    private String SF = "/SeShatTrainWrite/";
    private int count = 0;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private String firstTimekey = "1st";
    private String charIndexkey = "indexKey";
    private String SFKey = "SFKey";
    private String appendKey = "appendKey";
    private boolean append = false;

    private void writeStackTofile(ArrayList<Direction> result_words, String filepath) {
        try {
            FileWriter writer = new FileWriter(filepath, append);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write('I' + "\n");
            for (Direction s : result_words) {
                bufferedWriter.write(s.toString().charAt(0) + "\n");
                // Log.i("Result_words: ", " s.toString().charAt(0) " + s.toString().charAt(0));
            }
            bufferedWriter.write('E' + "\n");
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stack<String> readfileintoStack(String filepath) {
        Stack<String> words = new Stack<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(filepath)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                words.push(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivity", "readfileintoStack: e" + e.toString());
                }
            }
        }
        return words;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = this.getSharedPreferences("Config", Context.MODE_PRIVATE);
        editor = preferences.edit();

        checkPermission_AndroidVersion();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void checkPermission_AndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // write your logic code if permission already granted
            startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {

                    boolean write_storagePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean read_storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (write_storagePermission && read_storagePermission) {
                        startApp();
                    } else {
                        // Log.i("onRequestPermResult",    write_storagePermission + "," + read_storagePermission);
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to be able to work",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                                                        Manifest.permission.RECORD_AUDIO},
                                                PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void startApp() {
        if (Boolean.valueOf(preferences.getString(firstTimekey, "true"))) {
            SF = Environment.getExternalStorageDirectory() + SF;

            saveOnSharedPref(firstTimekey, String.valueOf(false));
            saveOnSharedPref(charIndexkey, String.valueOf(0));
            saveOnSharedPref(SFKey, SF);
            saveOnSharedPref(appendKey, String.valueOf(append));
        } else {
            SF = preferences.getString(SFKey, SF);
            count = Integer.parseInt(preferences.getString(charIndexkey, "0"));
            append = Boolean.parseBoolean(preferences.getString(appendKey, "false"));
        }
     //   Log.i("MainActivity","startApp: append: "+append);
        try {
            chars = readfileintoStack("chars");
            // Log.i("MainActivity","startApp: chars.sz" +chars.size());
        } catch (Exception e) {
            Log.e("MainActivity", "startApp:: e: " + e.getMessage());
        }
        wordview = (WordView) findViewById(R.id.textView_maintext);
        charPositions = configChar(chars.get(count));
        current = charPositions.pop();
        wordview.setText(current);
    }

    public void reset(View view) {
        ((WordView) wordview).reset();
        Toast.makeText(this, "Reset successfully ", Toast.LENGTH_LONG).show();
    }

    public void done(View view) {
        writeStackTofile(mUserGuidedVectors, SF + current);

        if (count + 1 == chars.size()) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("New version appending")
                    .setMessage("Thanks! You finished all chars, goto internal storage and copy files from SeShatTrainWrite." +
                            "Choose (ok) to append new version")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            append = true;
                            count=0;
                            saveOnSharedPref(appendKey,String.valueOf(append));
                            saveOnSharedPref(charIndexkey,String.valueOf(count));
                            startApp();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();

            if (charPositions.size() == 0) {
                charPositions = configChar(chars.get(++count));
            }
            current = charPositions.pop();
            wordview.setText(current);
            ((WordView) wordview).reset();
            saveOnSharedPref(charIndexkey, "" + count);
        }
    }


    private void saveOnSharedPref(String key, String value) {
        editor.putString(key, value).apply();
        editor.commit();
    }

    private Stack<String> configChar(String s) {
        // Log.i("MainActivity","configChar: "+s);
        Stack<String> result = new Stack<>();
        char charConnector = 'ـ';
        ArrayList<String> differentchars = new ArrayList<>();
        String[] charactersWithoutEndConnector = {"لأ", "لآ", "لا", "لإ", "لأ", "أ", "إ", "د", "ذ", "ر", "ز", "و", "ؤ", "ا", "آ",
                "ة", "ﻵ", "ﻷ", "ئ","ى"};
        differentchars.addAll(Arrays.asList(charactersWithoutEndConnector));
        // Log.i("MainActivity","configChar: differentchars.sz "+differentchars.size());
        if (s.equals("ء")) {
            result.push(s);
            return result;
        }
        result.push(s);
        result.push(charConnector + s);
        if (!differentchars.contains(s)) {
            result.push(s + charConnector);
            result.push(charConnector + s + charConnector);
        }
        return result;
    }
}
