package com.lilioss.lifecycle.persistent;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PersistentActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_persistent);

    Button buttonFGS = findViewById(R.id.fgs);
    buttonFGS.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }
}