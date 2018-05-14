package com.zahariev.dimitar.loanchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.text.MessageFormat;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);
        if (Utils.googleAccount != null) {
            String personName = Utils.googleAccount.getDisplayName();
            TextView userGreetingsView = findViewById(R.id.user_greetings_view);
            userGreetingsView.setText(MessageFormat.format("{0}{1}", getString(R.string.greetings) + " ", personName));
        }

        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        Query allBanknotesOfAUserQuery = database.child("userBanknoteAmount").orderByChild("userId").equalTo(Utils.googleAccount.getId());
        ValueEventListener moneyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateMyMoney(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("CancelledMoneyListener", "moneyListener:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Could not load money data", Toast.LENGTH_LONG);
            }

        };
        allBanknotesOfAUserQuery.addValueEventListener(moneyListener);

    }

    private void updateMyMoney(DataSnapshot dataSnapshot) {
        HashMap<String, Integer> moneyHashMap = new HashMap<>();
        for (DataSnapshot banknoteAmountFromDb : dataSnapshot.getChildren()) {
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = banknoteAmountFromDb.getValue(UserBanknoteAmountBindModel.class);
            String banknoteCurrencyType = userBanknoteAmountBindModel.getBanknoteType().split("_")[1];
            int banknoteAmountType = Integer.parseInt(userBanknoteAmountBindModel.getBanknoteType().split("_")[0]);
            int banknoteAmount = userBanknoteAmountBindModel.getBanknoteAmount();
            if (moneyHashMap.containsKey(banknoteCurrencyType)) {
                // get it by key and update it's value
                moneyHashMap.put(banknoteCurrencyType, moneyHashMap.get(banknoteCurrencyType) + (banknoteAmountType * banknoteAmount));
            } else {
                //create the key and add the value
                moneyHashMap.put(banknoteCurrencyType, banknoteAmountType * banknoteAmount);
            }
        }

        TextView myMoneyTextView = findViewById(R.id.my_money_text_view);
        myMoneyTextView.setText(R.string.my_money);
        for (String moneyAmountKey :
                moneyHashMap.keySet()) {
            String myMoneyCurrentText = myMoneyTextView.getText().toString();
            myMoneyTextView.setText(MessageFormat.format(" {0} {1} {2}s", myMoneyCurrentText, moneyHashMap.get(moneyAmountKey), moneyAmountKey));
        }

    }

    public void addBanknotes(View view) {
        Intent addBanknotesIntent = new Intent(this, AddBanknotesActivity.class);
        startActivity(addBanknotesIntent);
    }

    public void addCurrency(View view) {
        Intent addPossibleCurrenciesIntent = new Intent(this, AddCurrencyActivity.class);
        startActivity(addPossibleCurrenciesIntent);
    }

    public void giveALoan(View view) {
        Intent giveALoanIntent = new Intent(this, GiveALoanActivity.class);
        startActivity(giveALoanIntent);
    }

    public void getAvailableLoans(View view) {
        Intent getAvailableLoansIntent = new Intent(this, GivenLoansDropdownActivity.class);
        startActivity(getAvailableLoansIntent);
    }
}
