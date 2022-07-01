package com.example.lenz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    private EditText editText;
    private Button copyBtn, updateBtn, deleteBtn;
    private TextToSpeech textToSpeech;
    private ImageView speaker,share,save,history,clipboard;
    private DBHandler dbHandler;
    Boolean speakerFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editText = findViewById(R.id.editText);
        copyBtn = findViewById(R.id.copyBtn);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        clipboard = findViewById(R.id.clipboard);
        speaker = findViewById(R.id.speaker);
        save = findViewById(R.id.saveToDevice);
        history = findViewById(R.id.history);
        share = findViewById(R.id.share);
        dbHandler = new DBHandler(EditActivity.this);


        String resultText = getIntent().getStringExtra("recognizedText");
        String upd = getIntent().getStringExtra("upd");
        String content = editText.getText().toString();

        if(upd.equals("1")){
            updateBtn.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            copyBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
            clipboard.setVisibility(View.VISIBLE);

            String title = getIntent().getStringExtra("title");
            String time = getIntent().getStringExtra("time");

            clipboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contentY = editText.getText().toString();
                    contentY.trim();
                    copyToClipBoard(contentY);
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    updateDatabase(title,content,time);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRecord(time);
                }
            });
        }
        else{
            updateBtn.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            copyBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.GONE);
            clipboard.setVisibility(View.GONE);
        }
        resultText=resultText+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        editText.setText(resultText);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedText = editText.getText().toString();
                copyToClipBoard(editedText);
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
                String subject = "Message";
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                startActivity(Intent.createChooser(shareIntent,"Sharing Options"));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveToDeviceDialog();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(EditActivity.this,SavedDetailsActivity.class);
                startActivity(historyIntent);
            }
        });

    }

    private void deleteRecord(String time) {
            if(dbHandler.deleteScannedText(time)){
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDatabase(String title, String content, String time) {
        content = editText.getText().toString();
        content.trim();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String newRecordTime = dtf.format(now);

        if(dbHandler.updateScannedText(title,content,time,newRecordTime)){
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private void showSaveToDeviceDialog() {
        final Dialog dialog = new Dialog(EditActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        final EditText titleText = dialog.findViewById(R.id.title);
        Button submitButton = dialog.findViewById(R.id.submitBtn);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String titleOfScannedText = titleText.getText().toString();
                if(!titleOfScannedText.isEmpty()) {
                    insertToDatabase(titleOfScannedText);
                    dialog.dismiss();
                }else{
                    titleText.setError("Please Enter the Title");
                }

            }
        });

        dialog.show();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void insertToDatabase(String titleOfScannedText) {
        String recordTitle = titleOfScannedText;
        String recordContent = editText.getText().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String recordTime = dtf.format(now);


        if (recordContent.isEmpty()) {
            Toast.makeText(EditActivity.this, "Contains no text to store", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHandler.addNewScannedText(recordTitle,recordContent,recordTime);
        Toast.makeText(EditActivity.this,"Scanned Text is successfully stored",Toast.LENGTH_SHORT).show();

        editText.setText("");
        final Intent intent = new Intent(EditActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
//            textToSpeech.shutdown();
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