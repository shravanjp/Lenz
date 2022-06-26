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
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
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
        captureBtn = findViewById(R.id.captureBtn);
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

    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_CODE);
    }

    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
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

//        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Toast.makeText(MainActivity.this,"Camera",Toast.LENGTH_SHORT).show();
//            Log.i("CAMERA","Camera is working");
//            Bundle extras = data.getExtras();
//            bitmap = (Bitmap) extras.get("data");
//            detectText(bitmap);
//        }


              if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                try {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        detectText(bitmap);
                    }
                }catch (IOException e) {
                        Toast.makeText(MainActivity.this,"Some unknown Error",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
            }
            else{
                Log.i("resultCode",String.valueOf(resultCode));
            }
    }
//
//    private void getTextFromImage(Bitmap bitmap){
//        TextRecognizer recognizer = new TextRecognizerOptions.Builder(this).build();
//    }

        private void detectText(Bitmap bitmap){
        Log.i("1","Inside detectText");
        InputImage image = InputImage.fromBitmap(this.bitmap,0);
            Log.i("2","After Input image");
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                String blockText = "";
                String tempBlockText = "";
                StringBuilder result = new StringBuilder();
                try {
                    for(Text.TextBlock block: text.getTextBlocks()){
                        blockText = block.getText();
                        Point[] blockCornerPoint = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();

                        Log.i("mid0","checking");
                        for(Text.Line line: block.getLines()){
                            Log.i("mid1","checking");

                            String lineText = line.getText();
                            Point[] linearCornerPoint = line.getCornerPoints();
                            Rect linRect = line.getBoundingBox();
                            for(Text.Element element: line.getElements()){
                                Log.i("mid2","checking");

                                String elementText = element.getText();
                                result.append(elementText);
                            }
                            result.append("\n");
                        }
                        tempBlockText = tempBlockText +blockText+"\n";
                        result.append("\n");
                    }
                    Intent myIntent = new Intent(MainActivity.this, ResultActivity.class);
                    Log.i("IText","Before adding Textblock");
                    myIntent.putExtra("recognizedText", tempBlockText);
                    Log.i("IBitmap","Before adding Bitmap");

                    ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, _bs);
                    myIntent.putExtra("byteArray", _bs.toByteArray());
//                    startActivity(myIntent);

//                    myIntent.putExtra("capturedImage",bitmap);
                    Log.i("IResult","After  adding Bitmap before start");
                    startActivity(myIntent);
                    Log.i("IResult1","After Start");
                }catch (Exception e){
                    Log.i("exp",e.toString());
                    Toast.makeText(MainActivity.this,"Failed to detect text from Image",Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to detect text from Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

}




