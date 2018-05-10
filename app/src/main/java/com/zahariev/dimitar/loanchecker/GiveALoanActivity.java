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
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.bindmodels.UserCurrencyBindModel;
import com.zahariev.dimitar.utils.Ref;
import com.zahariev.dimitar.utils.Utils;

import java.io.Console;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        String loanerName = loanerNameEditText.getText().toString();
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
        GregorianCalendar returnDate = new GregorianCalendar();
        returnDate.set(returnDatePicker.getYear() + 1900, returnDatePicker.getMonth(), returnDatePicker.getDayOfMonth());
        LoanBindModel loanBindModel = new LoanBindModel(loanerName, moneyAmountToLoan, returnDate, Utils.googleAccount.getId());


        Spinner spinner = findViewById(R.id.currencySpinner);
        String chosenCurrency = spinner.getSelectedItem().toString();

        if (!checkIfThereIsEnoughMoney(chosenCurrency, moneyAmountToLoan)) {
            Toast.makeText(this, "Not enough money", Toast.LENGTH_SHORT).show();
            return;
        }
//
//        saveLoanToDatabase(loanBindModel);

    }

    //
    private boolean checkIfThereIsEnoughMoney(final String chosenCurrency, final Integer moneyAmountToLoan) {//todo refactor
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        Query userBanknoteAmountQuery = database.child("userBanknoteAmount").orderByChild("userId").equalTo(Utils.googleAccount.getId());


        userBanknoteAmountQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserBanknoteAmountBindModel> userBanknoteAmountChosenCurrencyBindModelList = getChosenCurrencyBanknotes(parseUserBanknoteAmount(dataSnapshot), chosenCurrency);
                getBankoteAmountAndBanknoteAmountType(userBanknoteAmountChosenCurrencyBindModelList);
                LinkedHashMap<Integer, Integer> banknoteAmountTypeBanknoteAmountMap = getBankoteAmountAndBanknoteAmountType(userBanknoteAmountChosenCurrencyBindModelList);
                boolean isEnoughMoney = isEnoughMoney(banknoteAmountTypeBanknoteAmountMap, moneyAmountToLoan);
                Log.wtf("enough money?", Boolean.toString(isEnoughMoney));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return false;

    }

    private boolean isEnoughMoney(LinkedHashMap<Integer, Integer> banknoteAmountTypeBanknoteAmountMap, Integer moneyAmountToLoan) {
        Ref<LinkedHashMap<Integer, Integer>> changeNominalsMap = new Ref<LinkedHashMap<Integer, Integer>>(banknoteAmountTypeBanknoteAmountMap);
        return isEnoughMoney(moneyAmountToLoan, moneyAmountToLoan, changeNominalsMap, changeNominalsMap.get().size() - 1);

    }

    private static boolean isEnoughMoney(int amountOriginal, int amount, Ref<LinkedHashMap<Integer, Integer>> changeNominalsMap, int j) {
        if (j >= 0 && (Utils.getElementByIndex(changeNominalsMap, j) <= amount) && amount > 0) {
            int i = j;
            for (; i >= 0; i--) {
                if (Utils.getElementByIndex(changeNominalsMap, i) > 0 &&
                        Utils.getKeyByIndex(changeNominalsMap, i) <= amount) {
//                    int element = Utils.getElementByIndex(changeNominalsMap, i) + 1;
//                    changeNominalsMap.get().put(Utils.getKeyByIndex(changeNominalsMap, i), element);
                    int key = Utils.getKeyByIndex(changeNominalsMap, i);
                    amount -= key;
                    changeNominalsMap.get().put(key, Utils.getElementByIndex(changeNominalsMap, i) - 1);
                    break;
                }

            }
            return isEnoughMoney(amountOriginal, amount, changeNominalsMap, i);
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


    private void saveLoanToDatabase(LoanBindModel loanBindModel) {
        //todo

    }

}
