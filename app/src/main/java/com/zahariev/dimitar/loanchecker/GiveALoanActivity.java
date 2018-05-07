package com.zahariev.dimitar.loanchecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.zahariev.dimitar.bindmodels.LoanBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class GiveALoanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_aloan);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatePicker returnDateDatePicker = findViewById(R.id.returnDateDatePicker);
        long currentTime = new Date().getTime();
        returnDateDatePicker.setMinDate(currentTime);

        Spinner dropdown = findViewById(R.id.currencySpinner);
        String[] items = new String[]{"1", "2", "3"};//todo get the actual data from the database
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    public void submitLoan(View view) {
        EditText loanerNameEditText = findViewById(R.id.loanerName);
        String loanerName = loanerNameEditText.getText().toString();
        EditText moneyAmountToLoanEditText = findViewById(R.id.moneyAmountToLoan);
        String moneyAmountToLoanString = moneyAmountToLoanEditText.getText().toString();
        int moneyAmountToLoan = 0;
        try {
            moneyAmountToLoan = Integer.parseInt(moneyAmountToLoanString);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
        }
        if (moneyAmountToLoan < 0) {
            Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
        }

        DatePicker returnDatePicker = findViewById(R.id.returnDateDatePicker);
        GregorianCalendar returnDate = new GregorianCalendar();
        returnDate.set(returnDatePicker.getYear() + 1900, returnDatePicker.getMonth(), returnDatePicker.getDayOfMonth());
        LoanBindModel loanBindModel = new LoanBindModel(loanerName, moneyAmountToLoan, returnDate, Utils.googleAccount.getId());

//        if (!checkIfThereIsEnoughMoney()) {
//            Toast.makeText(this, "Not enough money", Toast.LENGTH_SHORT).show();
//        }
//
//        saveLoanToDatabase(loanBindModel);

    }
//
//    private boolean checkIfThereIsEnoughMoney() {
//        //todo
//    }

    private void saveLoanToDatabase(LoanBindModel loanBindModel) {
        //todo

    }

}
