package com.example.lenz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private Button captureBtn,detectBtn;
    private Bitmap bitmap;
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        detectBtn = findViewById(R.id.captureBtn);
        captureBtn = findViewById(R.id.captureBtn);

//        detectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                detectText();
//            }
//        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    captureImage();
                }else{
                    requestPermission();
                }
            }
        });
    }

    private void captureImage(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this);
    }

    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_CODE);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean cameraPermissionGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if(cameraPermissionGranted){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                captureImage();
            }
            else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            /*
//            Bundle extras = data.getExtras();
//            bitmap = (Bitmap) extras.get("data");
//            captureTv.setImagebitmap(bitmap);
//            */

            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                        detectText(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//        }

    }
//
//    private void getTextFromImage(Bitmap bitmap){
//        TextRecognizer recognizer = new TextRecognizerOptions.Builder(this).build();
//    }

        private void detectText(Bitmap bitmap){
        InputImage image = InputImage.fromBitmap(this.bitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                String blockText = null;
                String tempBlockText = null;
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks()){
                    blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line: block.getLines()){
                        String lineText = line.getText();
                        Point[] linearCornerPoint = line.getCornerPoints();
                        Rect linRect = line.getBoundingBox();
                        for(Text.Element element: line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        result.append("\n");
//                        resultTv.setText(blockText);
                            tempBlockText = tempBlockText +blockText+"\n";
//                        Log.i("a",blockText);
                    }


                }
                Intent myIntent = new Intent(MainActivity.this, ResultActivity.class);
                myIntent.putExtra("recognizedText", tempBlockText);
                myIntent.putExtra("capturedImage",bitmap);
                startActivity(myIntent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to detect text from Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

}




