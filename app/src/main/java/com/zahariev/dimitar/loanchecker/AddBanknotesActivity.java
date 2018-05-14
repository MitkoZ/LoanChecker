package com.zahariev.dimitar.loanchecker;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.bindmodels.UserCurrencyBindModel;
import com.zahariev.dimitar.utils.ISaveBanknotesToDatabaseCallback;
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseUserCurrenciesReference = database.getReference("/userCurrencies/");
        Query currencyQuery = databaseUserCurrenciesReference.orderByChild("userId").equalTo(Utils.googleAccount.getId());
        ValueEventListener currencyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<UserCurrencyBindModel> currentUserCurrencyBindModelList = new ArrayList<>();

                for (DataSnapshot userCurrencyWithKey :
                        dataSnapshot.getChildren()) {
                    UserCurrencyBindModel userCurrencyBindModel = userCurrencyWithKey.getValue(UserCurrencyBindModel.class);
                    currentUserCurrencyBindModelList.add(userCurrencyBindModel);
                }

                if (currentUserCurrencyBindModelList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You don't have any added currencies", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                DatabaseReference databaseReference = database.getReference("userBanknoteAmount");
                Query banknotesAmountQuery = databaseReference.orderByChild("userId").equalTo(Utils.googleAccount.getId());
                ValueEventListener banknotesListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ViewGroup banknotesFragmentGridLayout = findViewById(R.id.banknotes_fragment_grid_layout);
                        for (DataSnapshot banknoteAmount : dataSnapshot.getChildren()) {

                            UserBanknoteAmountBindModel userBanknoteAmountBindModel = banknoteAmount.getValue(UserBanknoteAmountBindModel.class);
                            EditText amountEditText = new EditText(getApplicationContext());
                            amountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            amountEditText.setId(View.generateViewId());
                            amountEditText.setText(Integer.toString(userBanknoteAmountBindModel.getBanknoteAmount()));
                            Utils.banknotesProgrammaticallyAssignedIds.put(userBanknoteAmountBindModel.getBanknoteType(), amountEditText.getId());
                            banknotesFragmentGridLayout.addView(amountEditText);
                            TextView amountBanknotesTextView = new TextView(getApplicationContext());
                            String[] banknoteType = userBanknoteAmountBindModel.getBanknoteType().split("_");
                            amountBanknotesTextView.setText(MessageFormat.format("x {0} {1}", banknoteType[0], banknoteType[1]));
                            banknotesFragmentGridLayout.addView(amountBanknotesTextView);
                        }

                        Button submitButton = new Button(getApplicationContext());
                        submitButton.setText(R.string.submit);
                        submitButton.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                submitMyData(v);
                            }
                        });
                        Button cancelButton = new Button(getApplicationContext());
                        cancelButton.setText(R.string.cancel);
                        cancelButton.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        banknotesFragmentGridLayout.addView(submitButton);
                        banknotesFragmentGridLayout.addView(cancelButton);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.wtf("", "Could not load userBanknoteAmount data");
                        Toast.makeText(AddBanknotesActivity.this, "Banknotes data could not be loaded", Toast.LENGTH_LONG).show();
                        finish();
                    }

                };

                banknotesAmountQuery.addListenerForSingleValueEvent(banknotesListener);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("onCalledError", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(AddBanknotesActivity.this, "Loading currencies failed", Toast.LENGTH_SHORT).show();
            }
        };

        currencyQuery.addValueEventListener(currencyListener);
    }

    private void submitMyData(View view) {
        final List<UserBanknoteAmountBindModel> userBanknoteAmountBindModelList = new ArrayList<>();
        for (Map.Entry<String, Integer> programmaticallyAssignedIdEntry : Utils.banknotesProgrammaticallyAssignedIds.entrySet()) {
            EditText amountEditText = findViewById(programmaticallyAssignedIdEntry.getValue());
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = new UserBanknoteAmountBindModel();
            userBanknoteAmountBindModel.setUserId(Utils.googleAccount.getId());
            userBanknoteAmountBindModel.setBanknoteType(programmaticallyAssignedIdEntry.getKey());
            int moneyAmount;
            try {
                moneyAmount = Integer.parseInt(amountEditText.getText().toString());
            } catch (NumberFormatException numberFormatException) {
                Toast.makeText(getApplicationContext(), "Please enter a valid money amount", Toast.LENGTH_LONG).show();
                return;
            }

            if (moneyAmount < 0) {
                Toast.makeText(getApplicationContext(), "Please enter a valid money amount", Toast.LENGTH_LONG).show();
                return;
            }
            userBanknoteAmountBindModel.setBanknoteAmount(moneyAmount);
            userBanknoteAmountBindModelList.add(userBanknoteAmountBindModel);
        }

        ISaveBanknotesToDatabaseCallback saveBanknotesToDatabaseCallback = new ISaveBanknotesToDatabaseCallback() {
            @Override
            public void onCallback(HashMap<String, UserBanknoteAmountBindModel> myBanknotesFromDb) {
                saveDataToDatabase(userBanknoteAmountBindModelList, myBanknotesFromDb);
            }
        };
        getUserBanknotesFromDatabase(Utils.googleAccount.getId(), saveBanknotesToDatabaseCallback);
    }

    private void saveDataToDatabase(List<UserBanknoteAmountBindModel> userBanknoteAmountBindModelList, HashMap<String, UserBanknoteAmountBindModel> userBanknotesFromDatabase) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("userBanknoteAmount");
        for (final UserBanknoteAmountBindModel userBanknoteAmountBindModel :
                userBanknoteAmountBindModelList) {
            //update the record in the db
            UserBanknoteAmountBindModel currentUserBanknoteFromDatabaseBindModel = userBanknotesFromDatabase.get((userBanknoteAmountBindModel.getBanknoteType()));
            currentUserBanknoteFromDatabaseBindModel.setBanknoteAmount(userBanknoteAmountBindModel.getBanknoteAmount());
            UserBanknoteAmountBindModel userBanknoteAmountBindModelForDb = new UserBanknoteAmountBindModel();
            userBanknoteAmountBindModelForDb.setBanknoteType(currentUserBanknoteFromDatabaseBindModel.getBanknoteType());
            userBanknoteAmountBindModelForDb.setUserId(currentUserBanknoteFromDatabaseBindModel.getUserId());
            userBanknoteAmountBindModelForDb.setBanknoteAmount(currentUserBanknoteFromDatabaseBindModel.getBanknoteAmount());
            database.child(currentUserBanknoteFromDatabaseBindModel.getId()).setValue(userBanknoteAmountBindModelForDb);

        }
        Toast.makeText(getApplicationContext(), "Banknotes saved successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void getUserBanknotesFromDatabase(final String userId, final ISaveBanknotesToDatabaseCallback myCallback) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userBanknoteAmountRef = rootRef.child("userBanknoteAmount");
        Query userBanknoteAmountQuery = userBanknoteAmountRef.orderByChild("userId").equalTo(userId);
        final HashMap<String, UserBanknoteAmountBindModel> myBanknotesDb = new HashMap<>();
        userBanknoteAmountQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userBanknoteAmountSnapshot : dataSnapshot.getChildren()) {
                    String banknoteTypeDb = userBanknoteAmountSnapshot.child("banknoteType").getValue().toString();
                    int banknoteAmountDb = Integer.parseInt(userBanknoteAmountSnapshot.child("banknoteAmount").getValue().toString());
                    UserBanknoteAmountBindModel userBanknoteAmountBindModel = new UserBanknoteAmountBindModel();
                    userBanknoteAmountBindModel.setBanknoteType(banknoteTypeDb);
                    userBanknoteAmountBindModel.setUserId(userId);
                    userBanknoteAmountBindModel.setBanknoteAmount(banknoteAmountDb);
                    userBanknoteAmountBindModel.setId(userBanknoteAmountSnapshot.getKey());
                    myBanknotesDb.put(banknoteTypeDb, userBanknoteAmountBindModel);
                }
                myCallback.onCallback(myBanknotesDb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("onCancelledError", "userBanknoteAmountQuery:onCancelled", databaseError.toException());
                Toast.makeText(AddBanknotesActivity.this, "Could not load the banknotes data", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
