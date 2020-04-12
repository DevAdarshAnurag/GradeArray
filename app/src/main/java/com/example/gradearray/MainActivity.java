package com.example.gradearray;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 200;
    int[] grades = new int[10000];
    int count = 0;
    private Button readButton, generateButton;
    private TextView totalGrades, thirdHighest, lehmerText;
    private EditText fileEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize all the views below
        readButton = findViewById(R.id.bt_read);
        generateButton = findViewById(R.id.bt_generate);
        totalGrades = findViewById(R.id.tv_grdes_number);
        thirdHighest = findViewById(R.id.tv_third);
        lehmerText = findViewById(R.id.tv_lehmer);
        fileEditText = findViewById(R.id.et_filename);

        //attach click listener to read button
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromFile();
                Arrays.sort(grades, 0, count); //sort the grades
                String lehmerResult = lehmerMean();
                //update the text views below
                totalGrades.setText("Number of Grades: " + count);
                thirdHighest.setText("Third Highest Grade: " + grades[count - 3]);
                lehmerText.setText("Lehmer Mean: " + lehmerResult);
            }
        });

        //attach click listener to generate file button
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToFile(); //call writeToFile
            }
        });

    }

    void readFromFile() {
        /*read from asset file grade.txt below*/
        try {
            InputStream in = getApplicationContext().getAssets().open("grades.txt");
            Scanner sc = new Scanner(in);
            while (sc.hasNextInt()) {
                grades[count++] = sc.nextInt(); //read grades into int array
            }
        } catch (Exception e) {
            Log.e("Read Failure", e.getMessage());
        }
    }

    String lehmerMean() {
        /*this method calculates lehmer mean and returns it in proper format*/
        int i;
        double g4 = 0, g3 = 0, res;
        for (i = 0; i < grades.length; i++) {
            g4 = g4 + Math.pow(grades[i], 4);
            g3 = g3 + Math.pow(grades[i], 3);
        }
        res = g4 / g3;
        return String.format("%.4f", res);
    }

    private void writeToFile() {
        /*This method writes the sorted array to desired filename*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //if permission not granted no write will be performed
            Toast.makeText(this, "File Write Permission is not granted...", Toast.LENGTH_SHORT).show();
            //request userpermission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            return;
        }

        String dirPath = Environment.getExternalStorageDirectory() + "/GradeArray";
        File dirFile = new File(dirPath);
        if (!dirFile.exists())
            dirFile.mkdir(); //create directory

        String filename = fileEditText.getText().toString().trim();
        if (filename.equals(""))
            filename = "default"; //if edittext is empty default is the filename
        String path = Environment.getExternalStorageDirectory().toString() + "/GradeArray/" + filename + ".txt"; //create file path

        File file = new File(path);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            int i = 0;
            while (i < count) {
                out.write(grades[i++] + "\n"); //write grades to file
            }
            out.close();
            Toast.makeText(this, "Writing to " + path, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Write Failure", e.getMessage());
        }
    }

}
