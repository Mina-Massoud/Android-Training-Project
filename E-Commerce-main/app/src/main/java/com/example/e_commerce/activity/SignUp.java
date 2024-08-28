package com.example.e_commerce.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    EditText username,email,password,calendarView;
    Button signup;
    MyDatabase obj;
    TextView haveaccount;

    int cday,cmonth,cyear;
    String birthdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign up");

        calendarView =(EditText) findViewById(R.id.Date);
        haveaccount =(TextView) findViewById(R.id.toLogin);
        signup=(Button)findViewById(R.id.signup);

        obj=MyDatabase.getInstance(this);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // myCalendar.add(Calendar.DATE, 0);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                calendarView.setText(sdf.format(myCalendar.getTime()));
            }


        };

        calendarView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar c = Calendar.getInstance();
                cyear = c.get(Calendar.YEAR);
                cmonth = c.get(Calendar.MONTH);
                cday = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(SignUp.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                birthdate=String.valueOf(dayOfMonth)+"-"+String.valueOf(monthOfYear)+"-"+String.valueOf(year);
                                calendarView.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, cyear, cmonth, cday);
                           dpd.show();

            }
        });


        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,Login.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent=new Intent();
            username=(EditText)findViewById(R.id.username);
            email=(EditText)findViewById(R.id.email);
            password=(EditText)findViewById(R.id.password);

            if(username.getText().toString().replace(" ", "").trim().equals("")||email.getText().toString().replace(" ", "").trim().equals("")||password.getText().toString().replace(" ", "").trim().equals("")||birthdate.equals("")){
                Toast.makeText(SignUp.this,"An empty value is not allowed",Toast.LENGTH_SHORT).show();
            }

            else{

                String str= obj.Insert_cust(username.getText().toString(),email.getText().toString(),password.getText().toString(),birthdate);
                Toast.makeText(SignUp.this,str,Toast.LENGTH_SHORT).show();
                intent.putExtra("username",username.getText().toString().replace(" ", "").trim());
                intent.putExtra("email",email.getText().toString().replace(" ", "").trim());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
            }
        });

    }
    public void onLoginClick(View view) {
//       startActivity(new Intent(this,Login.class));
//        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
