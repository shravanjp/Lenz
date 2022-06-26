package com.example.lenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    Button copyBtn,editBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.textview);
        imageView = findViewById(R.id.imageview);
        copyBtn = findViewById(R.id.copyBtn);
        editBtn = findViewById(R.id.editBtn);
        String value = getIntent().getStringExtra("recognizedText");
        textView.setText(value);

        if(getIntent().hasExtra("byteArray")) {
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            imageView.setImageBitmap(_bitmap);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scannedText = textView.getText().toString();
                copyToClipBoard(scannedText);
            }
        });

    }

    private void copyToClipBoard(String text){
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied data",text);
        clipBoard.setPrimaryClip(clip);
        Toast.makeText(ResultActivity.this,"Copied To ClipBoard",Toast.LENGTH_SHORT).show();
    }
}