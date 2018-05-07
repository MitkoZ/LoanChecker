package com.zahariev.dimitar.loanchecker;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.bindmodels.UserCurrencyBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBanknotesActivity extends AppCompatActivity implements BanknotesFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banknotes);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("/userCurrencies/");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getApplicationContext(), "You don't have any added currencies", Toast.LENGTH_LONG).show();
                    return;
                }

                List<UserCurrencyBindModel> allUserCurrencyBindModelList = new ArrayList<>();
                HashMap<String, String> userCurrencyHashMap;
                for (DataSnapshot userCurrencySnapshot : dataSnapshot.getChildren()) {
                    userCurrencyHashMap = (HashMap<String, String>) userCurrencySnapshot.getValue();
                    allUserCurrencyBindModelList.add(new UserCurrencyBindModel(userCurrencyHashMap.get("userId"), userCurrencyHashMap.get("currency")));
                }

                List<UserCurrencyBindModel> currentUserCurrencyBindModelList = new ArrayList<>();

                for (int i = 0; i < allUserCurrencyBindModelList.size(); i++) {
                    UserCurrencyBindModel currentUserCurrencyBindModel = allUserCurrencyBindModelList.get(i);
                    if (currentUserCurrencyBindModel.userId.equals(Utils.googleAccount.getId())) {
                        currentUserCurrencyBindModelList.add(currentUserCurrencyBindModel);
                    }
                }


                if (currentUserCurrencyBindModelList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You don't have any added currencies", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                List<String> currencies = new ArrayList<>();
                for (UserCurrencyBindModel userCurrencyBindModel : currentUserCurrencyBindModelList) {
                    currencies.add(userCurrencyBindModel.currency);
                }

                ViewGroup banknotesFragment = findViewById(R.id.banknotes_fragment);
                for (String currency : currencies) {
                    for (int banknote : Utils.BANKNOTES) {
                        EditText amountEditText = new EditText(getApplicationContext());
                        amountEditText.setId(View.generateViewId());
                        amountEditText.setText("0");// default banknote amount is 0
                        Utils.programmaticallyAssignedIds.put(banknote + "_" + currency, amountEditText.getId());
                        banknotesFragment.addView(amountEditText);
                        TextView amountBanknotesTextView = new TextView(getApplicationContext());
                        amountBanknotesTextView.setText(MessageFormat.format("x {0} {1}", banknote, currency));
                        banknotesFragment.addView(amountBanknotesTextView);
                    }
                }
                Button submitButton = new Button(getApplicationContext());
                submitButton.setText("Submit");
                submitButton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        submitMyData(v);
                    }
                });
                Button cancelButton = new Button(getApplicationContext());
                cancelButton.setText("Cancel");
                cancelButton.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });
                banknotesFragment.addView(submitButton);
                banknotesFragment.addView(cancelButton);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("onCalledError", "loadPost:onCancelled", databaseError.toException());
            }

        };
        databaseReference.addValueEventListener(postListener);
    }

    private void submitMyData(View view) {
        for (Map.Entry<String, Integer> programmaticallyAssignedIdEntry : Utils.programmaticallyAssignedIds.entrySet()) {
            EditText amountEditText = findViewById(programmaticallyAssignedIdEntry.getValue());
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = new UserBanknoteAmountBindModel();
            userBanknoteAmountBindModel.userId = Utils.googleAccount.getId();
            userBanknoteAmountBindModel.banknoteType = programmaticallyAssignedIdEntry.getKey();
            userBanknoteAmountBindModel.banknoteAmount = amountEditText.getText().toString();
            saveDataToDatabase(userBanknoteAmountBindModel);
        }

    }

    private void saveDataToDatabase(UserBanknoteAmountBindModel userBanknoteAmountBindModel) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("userBanknoteAmount");
        String key = database.push().getKey();
        Map<String, Object> userBanknoteAmountMap = new HashMap<>();
        userBanknoteAmountMap.put(key, userBanknoteAmountBindModel);
        database.updateChildren(userBanknoteAmountMap);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
