package com.mgh.mccatcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ErrorActivity extends Activity {

    private TextView errorTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        errorTextView = findViewById(R.id.error_text_view);
        if(savedInstanceState != null){
            errorTextView.setText(savedInstanceState.getString("error"));
        }else{
            errorTextView.setText("null");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}