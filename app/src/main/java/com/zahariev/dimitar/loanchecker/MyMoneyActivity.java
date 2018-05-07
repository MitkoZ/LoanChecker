package com.zahariev.dimitar.loanchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zahariev.dimitar.utils.Utils;

import java.text.MessageFormat;

public class MyMoneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_money);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.googleAccount != null) {
            String personName = Utils.googleAccount.getDisplayName();
            TextView userGreetingsView = findViewById(R.id.user_greetings_view);
            userGreetingsView.setText(MessageFormat.format("{0}{1}", getString(R.string.greetings) + " ", personName));
        }

        //todo - add my money here
    }

    public void addBanknotes(View view) {
        Intent addBanknotesIntent = new Intent(this, AddBanknotesActivity.class);
        startActivity(addBanknotesIntent);
    }

    public void addCurrency(View view) {
        Intent addPossibleCurrenciesItent = new Intent(this, AddCurrencyActivity.class);
        startActivity(addPossibleCurrenciesItent);
    }

    public void giveALoan(View view) {
        Intent giveALoanIntent = new Intent(this, GiveALoanActivity.class);
        startActivity(giveALoanIntent);
    }
}
