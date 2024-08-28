package com.example.e_commerce.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.R;
import com.example.e_commerce.fragment.Cart;

public class payment extends AppCompatActivity {
    EditText CardNumber, ExpireDateMonth, ExpireDateYear;
    Button addCreditCard;
    String userId;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        MyDatabase obj = MyDatabase.getInstance(this);
        Intent i = getIntent();
        userId = i.getStringExtra("id");
        CardNumber = findViewById(R.id.CardNumber);
        ExpireDateMonth = findViewById(R.id.ExpireDateMonth);
        ExpireDateYear = findViewById(R.id.ExpireDateYear);
        addCreditCard = findViewById(R.id.AddPayment);

        addCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get credit card information from the input fields
                String cardNumber = CardNumber.getText().toString();
                String expireDateMonth = ExpireDateMonth.getText().toString();
                String expireDateYear = ExpireDateYear.getText().toString();

                if(cardNumber.isEmpty() || expireDateMonth.isEmpty() || expireDateYear.isEmpty()){
                    Toast.makeText(payment.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    if(isLuhnValid(cardNumber)){
                        Intent paymentIntent = new Intent(payment.this, Cart.class);
                        // Pass any necessary data using Intent extras
                        flag = 1;
                        paymentIntent.putExtra("paymentFlag", flag);
                        // Start the OrderActivity
                        startActivity(paymentIntent);

                        // Finish the current activity
                        finish();
                    }
                    else {
                        Toast.makeText(payment.this, "Number Not Valid", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
    // Luhn algorithm validation method
    private boolean isLuhnValid(String number) {
        // Remove any whitespace or hyphens from the number
        number = number.replaceAll("\\s+", "").replaceAll("-", "");

        // Check if the number is composed of digits only
        if (!number.matches("\\d+")) {
            return false;
        }

        // Reverse the number and convert it to an array of integers
        int[] digits = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            digits[i] = Character.getNumericValue(number.charAt(number.length() - 1 - i));
        }

        // Apply the Luhn algorithm
        int total = 0;
        for (int i = 0; i < digits.length; i++) {
            if (i % 2 == 0) {
                total += digits[i];
            } else {
                int doubledDigit = digits[i] * 2;
                total += doubledDigit > 9 ? doubledDigit - 9 : doubledDigit;
            }
        }

        // If the total is divisible by 10, the card number is valid
        return total % 10 == 0;
    }
}