package com.example.e_commerce.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.R;

public class ReportGenerated extends AppCompatActivity {
    ListView reportList;
    ArrayAdapter<String> reportAdapter;
    MyDatabase report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generated);
        getSupportActionBar().setTitle("Report Generated");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportList = findViewById(R.id.report);
        reportAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        reportList.setAdapter(reportAdapter);
        report = MyDatabase.getInstance(getApplicationContext());

        Cursor cursor = report.getTransactions();

        while (cursor.moveToNext()) {  // Ensure the cursor moves to the next item before accessing data
            String customerId = cursor.getString(cursor.getColumnIndex("id"));  // Assuming 'id' is the customer ID
            String customerName = cursor.getString(cursor.getColumnIndex("customername"));
            String productName = cursor.getString(cursor.getColumnIndex("productname"));
            String category = cursor.getString(cursor.getColumnIndex("catgoryname"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            float price = cursor.getFloat(cursor.getColumnIndex("price"));

            reportAdapter.add(
                    "User ID: " + customerId + "\n" +
                            "Customer: " + customerName + "\n" +
                            "Product: " + productName + "\n" +
                            "Category: " + category + "\n" +
                            "Date: " + date + "\n" +
                            "Quantity: " + quantity + "\n" +
                            "Price: " + price + "\n"
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adminmenu3, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadproduct11:
                startActivity(new Intent(ReportGenerated.this, UploadProduct.class));
                return true;
            case R.id.chart:
                startActivity(new Intent(ReportGenerated.this, ChartGenerated.class));
                return true;
            case R.id.deleteuser:
                startActivity(new Intent(ReportGenerated.this, deleteUser.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ReportGenerated.this, UploadProduct.class);
        startActivity(intent);
        return super.onSupportNavigateUp();
    }
}
