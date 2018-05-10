package com.zahariev.dimitar.loanchecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.LoanBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        Query userPossibleCurrenciesQuery = database.child("userCurrencies").orderByChild("userId").equalTo(Utils.googleAccount.getId());
        userPossibleCurrenciesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> possibleCurrenciesDropdown = parseCurrencyData(dataSnapshot);
                SpinnerAdapter spinnerAdapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, possibleCurrenciesDropdown);
                Spinner dropdown = findViewById(R.id.currencySpinner);
                dropdown.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("onCurrenciesCancelled", "loadPossibleCurrencies:onCancelled", databaseError.toException());
            }
        });

    }


    private List<String> parseCurrencyData(DataSnapshot dataSnapshot) {
        List<String> possibleCurrenciesDropdown = new ArrayList<>();
        for (DataSnapshot currencyObjectFromDb : dataSnapshot.getChildren()) {
            for (DataSnapshot currencyObjectChildren : currencyObjectFromDb.getChildren()) {
                if (currencyObjectChildren.getKey().equals("currency")) {
                    possibleCurrenciesDropdown.add(currencyObjectChildren.getValue().toString());
                }
            }
        }
        return possibleCurrenciesDropdown;
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
