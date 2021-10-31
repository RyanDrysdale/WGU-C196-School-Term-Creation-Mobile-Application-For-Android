package com.example.c196ryandrysdale;

import androidx.appcompat.app.AppCompatActivity;




import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.c196ryandrysdale.UI.Term.term_list;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToTermList(View view) {
        Intent intent = new Intent(MainActivity.this, term_list.class);
        startActivity(intent);
    }
}