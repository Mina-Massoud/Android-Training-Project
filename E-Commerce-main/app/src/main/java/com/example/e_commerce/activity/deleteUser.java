package com.example.e_commerce.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.R;

public class deleteUser extends AppCompatActivity {
    MyDatabase db;
    EditText userId;
    Button delete;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        userId = findViewById(R.id.idfordel);
        delete = findViewById(R.id.deleteUser);

        // Initialize the context and database instance
        context = this;
        db = MyDatabase.getInstance(context);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userId.getText().toString().trim();

                if (id.isEmpty()) {
                    Toast.makeText(deleteUser.this, "Enter ID to delete", Toast.LENGTH_SHORT).show();
                } else {
                    boolean exists = db.checkuserexist(id);
                    if (exists) {
                        String result = db.daletecust(id);
                        Toast.makeText(deleteUser.this, result, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(deleteUser.this, "ID does not exist in the database", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adminmenu5, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadproduct11:
                startActivity(new Intent(deleteUser.this, UploadProduct.class));
                return true;
            case R.id.report:
                startActivity(new Intent(deleteUser.this, ReportGenerated.class));
                return true;
            case R.id.chart:
                startActivity(new Intent(deleteUser.this, ChartGenerated.class));
                return true;
            case R.id.feedback:
                startActivity(new Intent(deleteUser.this, ShowRating.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
