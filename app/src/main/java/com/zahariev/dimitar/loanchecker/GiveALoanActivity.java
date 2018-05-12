package com.zahariev.dimitar.loanchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.BanknoteAmountAndBanknoteAmountTypeBindModel;
import com.zahariev.dimitar.bindmodels.LoanBindModel;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, possibleCurrenciesDropdown);
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
        String loaneeName = loanerNameEditText.getText().toString();
        if (loaneeName.equals("")) {
            Toast.makeText(this, "Please enter a valid loanee name", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText moneyAmountToLoanEditText = findViewById(R.id.moneyAmountToLoan);
        String moneyAmountToLoanString = moneyAmountToLoanEditText.getText().toString();
        int moneyAmountToLoan = 0;
        try {
            moneyAmountToLoan = Integer.parseInt(moneyAmountToLoanString);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
            return;
        }
        if (moneyAmountToLoan <= 0) {
            Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
            return;
        }

        DatePicker returnDatePicker = findViewById(R.id.returnDateDatePicker);
        Calendar returnDate = new GregorianCalendar();
        returnDate.set(returnDatePicker.getYear(), returnDatePicker.getMonth(), returnDatePicker.getDayOfMonth());


        Spinner spinner = findViewById(R.id.currencySpinner);
        String chosenCurrency = spinner.getSelectedItem().toString();

        LoanBindModel loanBindModel = new LoanBindModel(loaneeName, moneyAmountToLoan, returnDate, Utils.googleAccount.getId(), chosenCurrency);

        processData(chosenCurrency, loanBindModel);

    }

    private void processData(final String chosenCurrency, final LoanBindModel loanBindModel) {
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        Query userBanknoteAmountQuery = database.child("userBanknoteAmount").orderByChild("userId").equalTo(Utils.googleAccount.getId());


        userBanknoteAmountQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList = getChosenCurrencyBanknotes(parseUserBanknoteAmount(dataSnapshot), chosenCurrency);
                LinkedHashMap<Integer, Integer> banknoteAmountTypeBanknoteAmountMap = getBankoteAmountAndBanknoteAmountType(userBanknoteAmountChosenCurrencyBindModelList);
                boolean isEnoughMoney = isEnoughMoney(banknoteAmountTypeBanknoteAmountMap, loanBindModel.getAmount());

                Log.wtf("enough money?", Boolean.toString(isEnoughMoney));
                if (!isEnoughMoney) {
                    Toast.makeText(getApplicationContext(), "Not enough money", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveLoanToDatabase(loanBindModel, chosenCurrency);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("onEnoughMoneyCancelled", "Is enough money query cancelled");
            }

        });
    }

    private boolean isEnoughMoney(LinkedHashMap<Integer, Integer> banknoteAmountTypeBanknoteAmountMap, Integer moneyAmountToLoan) {
        LinkedHashMap<Integer, Integer> changeNominalsMap = new LinkedHashMap<Integer, Integer>(banknoteAmountTypeBanknoteAmountMap);
        return isEnoughMoney(moneyAmountToLoan, changeNominalsMap, changeNominalsMap.size() - 1);
    }

    private static boolean isEnoughMoney(int amount, LinkedHashMap<Integer, Integer> changeNominalsMap, int j) { //key is banknoteAmountType and value is banknoteAmount
        if (j >= 0 && amount > 0) {
            int i = j;
            for (; i >= 0; i--) {
                if (Utils.getElementByIndex(changeNominalsMap, i) > 0 && //check if there is any amount of the current banknote
                        Utils.getKeyByIndex(changeNominalsMap, i) <= amount) { //check if the banknote amount type is <= amount
                    int key = Utils.getKeyByIndex(changeNominalsMap, i);
                    amount -= key;
                    changeNominalsMap.put(key, Utils.getElementByIndex(changeNominalsMap, i) - 1); //remove one banknote from the current banknotes
                    break;
                }

            }
            return isEnoughMoney(amount, changeNominalsMap, i);
        } else if (amount == 0) {
            return true;
        } else {
            return false;
        }
    }

    private LinkedHashMap<Integer, Integer> getBankoteAmountAndBanknoteAmountType(List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList) {
        LinkedHashMap<Integer, Integer> banknoteAmountTypeBanknoteAmountMap = new LinkedHashMap<>();
        for (UserBanknoteAmountBindModel userBanknoteAmountBindModel : userBanknoteAmountChosenCurrencyBindModelList) {
            int banknoteAmountType = Integer.parseInt(userBanknoteAmountBindModel.getBanknoteType().split("_")[0]);
            int banknoteAmount = userBanknoteAmountBindModel.getBanknoteAmount();
            banknoteAmountTypeBanknoteAmountMap.put(banknoteAmountType, banknoteAmount);
        }
        return banknoteAmountTypeBanknoteAmountMap;
    }

    private List<UserBanknoteAmountBindModel> getChosenCurrencyBanknotes(List<UserBanknoteAmountBindModel> userBanknoteAmountBindModelList, String chosenCurrency) {
        List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList = new ArrayList<>();
        for (UserBanknoteAmountBindModel userBanknoteAmountBindModel : userBanknoteAmountBindModelList) {
            String currency = userBanknoteAmountBindModel.getBanknoteType().split("_")[1];
            if (currency.equals(chosenCurrency)) {
                userBanknoteAmountChosenCurrencyBindModelList.add(userBanknoteAmountBindModel);
            }
        }

        return userBanknoteAmountChosenCurrencyBindModelList;
    }

    private List<UserBanknoteAmountBindModel> parseUserBanknoteAmount(DataSnapshot dataSnapshot) {
        List<UserBanknoteAmountBindModel> userBanknoteAmountBindModelList = new ArrayList<>();
        for (DataSnapshot userBanknoteAmount : dataSnapshot.getChildren()) {
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = userBanknoteAmount.getValue(UserBanknoteAmountBindModel.class);
            userBanknoteAmountBindModelList.add(userBanknoteAmountBindModel);
        }
        return userBanknoteAmountBindModelList;
    }

    private List<UserBanknoteAmountBindModel> parseUserBanknoteAmountWithKey(DataSnapshot dataSnapshot) {
        List<UserBanknoteAmountBindModel> userBanknoteAmountBindModelList = new ArrayList<>();
        for (DataSnapshot userBanknoteAmount : dataSnapshot.getChildren()) {
            UserBanknoteAmountBindModel userBanknoteAmountBindModel = userBanknoteAmount.getValue(UserBanknoteAmountBindModel.class);
            userBanknoteAmountBindModel.setId(userBanknoteAmount.getKey());
            userBanknoteAmountBindModelList.add(userBanknoteAmountBindModel);
        }
        return userBanknoteAmountBindModelList;
    }

    private void saveLoanToDatabase(final LoanBindModel loanBindModel, final String chosenCurrency) {
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        final Query userBanknoteAmountQuery = database.child("userBanknoteAmount").orderByChild("userId").equalTo(Utils.googleAccount.getId());

        userBanknoteAmountQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList = getChosenCurrencyBanknotes(parseUserBanknoteAmountWithKey(dataSnapshot), chosenCurrency);
                LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> banknoteAmountTypeBanknoteAmountMap = getBankoteAmountAndBanknoteAmountTypeWithKey(userBanknoteAmountChosenCurrencyBindModelList);
                LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> linkedHashMapBindModel = getNewBanknotesAmount(banknoteAmountTypeBanknoteAmountMap, loanBindModel.getAmount());
                for (String userBanknoteAmountKey : linkedHashMapBindModel.keySet()) {
                    database.child("userBanknoteAmount").child(userBanknoteAmountKey).child("banknoteAmount").setValue(linkedHashMapBindModel.get(userBanknoteAmountKey).getBanknoteAmount());
                }
                saveALoanObjectToDb(loanBindModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("onSaveLoanCancelled", "Save Loan to database cancelled");
            }

        });

    }

    private void saveALoanObjectToDb(LoanBindModel loanBindModel) {
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference("userGivenLoans/");
        String idDb = database.push().getKey();
        Map<String, Object> loanBindModelMap = new HashMap<>();
        loanBindModelMap.put(idDb, loanBindModel);
        database.updateChildren(loanBindModelMap);
    }


    private LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> getBankoteAmountAndBanknoteAmountTypeWithKey(List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList) {
        LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> banknoteAmountTypeBanknoteAmountMap = new LinkedHashMap<>();
        for (UserBanknoteAmountBindModel userBanknoteAmountBindModel : userBanknoteAmountChosenCurrencyBindModelList) {
            BanknoteAmountAndBanknoteAmountTypeBindModel banknoteAmountAndBanknoteAmountTypeBindModel = new BanknoteAmountAndBanknoteAmountTypeBindModel();
            banknoteAmountAndBanknoteAmountTypeBindModel.setBanknoteAmountType(Integer.parseInt(userBanknoteAmountBindModel.getBanknoteType().split("_")[0]));
            banknoteAmountAndBanknoteAmountTypeBindModel.setBanknoteAmount(userBanknoteAmountBindModel.getBanknoteAmount());
            banknoteAmountTypeBanknoteAmountMap.put(userBanknoteAmountBindModel.getId(), banknoteAmountAndBanknoteAmountTypeBindModel);
        }

        return banknoteAmountTypeBanknoteAmountMap;

    }

    private LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> getNewBanknotesAmount(LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> banknoteAmountTypeBanknoteAmountMap, Integer moneyAmountToLoan) {
        return getNewBanknotesAmount(moneyAmountToLoan, banknoteAmountTypeBanknoteAmountMap, banknoteAmountTypeBanknoteAmountMap.size() - 1);
    }

    private LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> getNewBanknotesAmount(int amount, LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> banknoteAmountTypeBanknoteAmountMap, int j) {
        if (j >= 0 && amount > 0) {
            int i = j;
            for (; i >= 0; i--) {
                if (Utils.getBindModelElementByIndex(banknoteAmountTypeBanknoteAmountMap, i).getBanknoteAmount() > 0 &&
                        Utils.getBindModelElementByIndex(banknoteAmountTypeBanknoteAmountMap, i).getBanknoteAmountType() <= amount) {
                    int banknoteAmountType = Utils.getBindModelElementByIndex(banknoteAmountTypeBanknoteAmountMap, i).getBanknoteAmountType();
                    amount -= banknoteAmountType;
                    Utils.getBindModelElementByIndex(banknoteAmountTypeBanknoteAmountMap, i).setBanknoteAmount(Utils.getBindModelElementByIndex(banknoteAmountTypeBanknoteAmountMap, i).getBanknoteAmount() - 1);
                    break;
                }

            }
            return getNewBanknotesAmount(amount, banknoteAmountTypeBanknoteAmountMap, i);
        } else { //amount == 0
            return banknoteAmountTypeBanknoteAmountMap;
        }
    }

}
