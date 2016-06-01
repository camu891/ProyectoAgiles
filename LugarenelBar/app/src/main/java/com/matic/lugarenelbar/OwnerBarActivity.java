package com.matic.lugarenelbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class OwnerBarActivity extends AppCompatActivity {

    private Button btnNvoBar;
    private Button btnAdmBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnNvoBar= (Button) findViewById(R.id.btn_nvoBar);
        btnNvoBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(OwnerBarActivity.this,NuevoBarActivity.class);
                startActivity(act);

            }
        });


        btnAdmBar= (Button) findViewById(R.id.btn_admBar);
        btnAdmBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent act=new Intent(OwnerBarActivity.this,AdmBarActivity.class);
                startActivity(act);

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
