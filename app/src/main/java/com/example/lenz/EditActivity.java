package com.example.lenz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    private EditText editText;
    private Button copyBtn, backBtn;
    private TextToSpeech textToSpeech;
    private ImageView speaker,share,save;
    Boolean speakerFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editText = findViewById(R.id.editText);
        copyBtn = findViewById(R.id.copyBtn);
        backBtn = findViewById(R.id.backBtn);
        speaker = findViewById(R.id.speaker);
        save = findViewById(R.id.saveToDevice);
        share = findViewById(R.id.share);


        String resultText = getIntent().getStringExtra("recognizedText");
        resultText=resultText+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        editText.setText(resultText);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedText = editText.getText().toString();
                copyToClipBoard(editedText);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        textToSpeech = new TextToSpeech(EditActivity.this, new TextToSpeech.OnInitListener() {
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
                String toSpeak = editText.getText().toString();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = editText.getText().toString().trim();
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

    private void copyToClipBoard(String editedText) {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied data",editedText);
        clipBoard.setPrimaryClip(clip);
        Toast.makeText(EditActivity.this,"Copied To ClipBoard",Toast.LENGTH_SHORT).show();
    }
}