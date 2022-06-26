package com.example.lenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private Button copyBtn,editBtn;
    private TextToSpeech textToSpeech;
    private ImageView speaker,share,save;

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

        speaker = findViewById(R.id.speaker);
        save = findViewById(R.id.saveToDevice);
        share = findViewById(R.id.share);

        String value = getIntent().getStringExtra("recognizedText");
        textView.setText(value);

        if(getIntent().hasExtra("byteArray")) {
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            imageView.setImageBitmap(_bitmap);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scannedText = textView.getText().toString();
                Intent resultIntent = new Intent(ResultActivity.this, EditActivity.class);
                resultIntent.putExtra("recognizedText", scannedText);
                startActivity(resultIntent);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scannedText = textView.getText().toString();
                copyToClipBoard(scannedText);
            }
        });

        textToSpeech = new TextToSpeech(ResultActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = textView.getText().toString();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = textView.getText().toString().trim();
                String subject = "Message from JP";
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                startActivity(Intent.createChooser(shareIntent,"Sharing Options"));
            }
        });

    }

    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    private void copyToClipBoard(String text){
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied data",text);
        clipBoard.setPrimaryClip(clip);
        Toast.makeText(ResultActivity.this,"Copied To ClipBoard",Toast.LENGTH_SHORT).show();
    }
}