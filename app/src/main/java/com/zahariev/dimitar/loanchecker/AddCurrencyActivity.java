package com.zahariev.dimitar.loanchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.bindmodels.UserCurrencyBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class AddCurrencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
    }

    public void addCurrency(View view) {
        final EditText editTextView = findViewById(R.id.addCurrencyEditText);
        final String currency = editTextView.getText().toString();
        if (currency.isEmpty()) {
            Toast.makeText(this, "Currency cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currency.contains("_")) {
            Toast.makeText(this, "Currency contains illegal symbols", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference("userCurrencies");
        Query myCurrenciesQuery = database.orderByChild("userId").equalTo(Utils.googleAccount.getId());
        myCurrenciesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserCurrencyBindModel> userCurrencyBindModelList = new ArrayList<>();
                for (DataSnapshot userCurrencyWithKey :
                        dataSnapshot.getChildren()) {
                    UserCurrencyBindModel userCurrencyBindModel = userCurrencyWithKey.getValue(UserCurrencyBindModel.class);
                    userCurrencyBindModelList.add(userCurrencyBindModel);
                }
                UserCurrencyBindModel userCurrencyBindModelDb = Utils.CollectionUtil.find(userCurrencyBindModelList, new Utils.Predicate<UserCurrencyBindModel>() {
                    @Override
                    public boolean contains(UserCurrencyBindModel userCurrencyBindModel) {
                        if (userCurrencyBindModel.getCurrency().equals(currency)) {
                            return true;
                        }
                        return false;
                    }
                });

                boolean isAlreadyContainsThisCurrency = false;
                if (userCurrencyBindModelDb != null) {
                    isAlreadyContainsThisCurrency = true;
                }

                if (isAlreadyContainsThisCurrency) {
                    Toast.makeText(getApplicationContext(), "You have already added this currency", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserCurrencyBindModel userCurrencyBindModel = new UserCurrencyBindModel(Utils.googleAccount.getId(), currency);
                writeUserCurrency(userCurrencyBindModel);
                Toast.makeText(getApplicationContext(), "Currency added successfully!", Toast.LENGTH_SHORT).show();
                editTextView.setText("");
                initializeUserBanknoteAmount(currency);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddCurrencyActivity.this, "Could not load current currencies", Toast.LENGTH_SHORT).show();
                finish();
            }

        });


    }

    private void writeUserCurrency(UserCurrencyBindModel userCurrencyBindModel) {
        DatabaseReference databaseReference;
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("/userCurrencies/");
        Map<String, Object> userCurrencyMap = new HashMap<>();
        String key = databaseReference.push().getKey();
        userCurrencyMap.put(key, userCurrencyBindModel);
        databaseReference.updateChildren(userCurrencyMap);
    }

    private void initializeUserBanknoteAmount(String currency) { //initialize each possible banknote with amount 0
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("userBanknoteAmount");
        Map<String, Object> userBanknoteAmountMap = new HashMap<>();
        for (int banknote : Utils.BANKNOTES) {
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = new UserBanknoteAmountBindModel();
            String key = database.push().getKey();
            userBanknoteAmountBindModel.setUserId(Utils.googleAccount.getId());
            userBanknoteAmountBindModel.setBanknoteAmount(0);
            userBanknoteAmountBindModel.setBanknoteType(banknote + "_" + currency);
            userBanknoteAmountMap.put(key, userBanknoteAmountBindModel);
        }
        database.updateChildren(userBanknoteAmountMap);
    }

    public void cancel(View view) {
        finish();
    }
}
