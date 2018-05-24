package com.zahariev.dimitar.loanchecker;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.BanknoteAmountAndBanknoteAmountTypeBindModel;
import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.text.MessageFormat;
import java.util.HashMap;

import static com.zahariev.dimitar.utils.Utils.banknotesProgrammaticallyAssignedIds;

public class RepayBanknotesActivity extends AppCompatActivity implements BanknotesFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repay_banknotes);
        GridLayout gridLayout = findViewById(R.id.banknotes_fragment_grid_layout);
        final String currency = (String) getIntent().getExtras().get("givenCurrency");
        final Integer moneyAmount = Integer.parseInt(getIntent().getExtras().get("moneyAmount").toString());
        final String userGivenLoanId = getIntent().getExtras().get("userGivenLoanId").toString();
        for (Integer possibleBanknote : Utils.BANKNOTES) {
            EditText banknoteAmountEditText = new EditText(getApplicationContext());
            banknoteAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            banknoteAmountEditText.setText("0");
            banknoteAmountEditText.setTextColor(Color.BLACK);
            int id = View.generateViewId();
            banknotesProgrammaticallyAssignedIds.put(MessageFormat.format("{0}_{1}", possibleBanknote, currency), id);
            banknoteAmountEditText.setId(id);
            TextView currencyTextView = new TextView(getApplicationContext());
            currencyTextView.setText(MessageFormat.format("{0} {1}s", possibleBanknote, currency));
            currencyTextView.setTextColor(Color.BLACK);
            gridLayout.addView(banknoteAmountEditText);
            gridLayout.addView(currencyTextView);
        }
        Button repayButton = new Button(getApplicationContext());
        repayButton.setText(R.string.repay);
        repayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repayLoan(moneyAmount, userGivenLoanId, currency);
            }

        });
        Button cancelButton = new Button(getApplicationContext());
        cancelButton.setText(R.string.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridLayout.addView(repayButton);
        gridLayout.addView(cancelButton);
    }

    private void repayLoan(Integer loanMoney, String userGivenLoanId, String currency) {
        HashMap<Integer, Integer> moneyAmountMap = new HashMap<>();
        for (String banknoteProgrammaticalyAssignedIdKey : banknotesProgrammaticallyAssignedIds.keySet()) {
            if (banknoteProgrammaticalyAssignedIdKey.split("_")[1].equals(currency)) {
                EditText moneyAmountEditText = findViewById(banknotesProgrammaticallyAssignedIds.get(banknoteProgrammaticalyAssignedIdKey));
                Integer money;
                try {
                    money = Integer.parseInt(moneyAmountEditText.getText().toString());
                } catch (NumberFormatException numberFormatException) {
                    Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (money < 0) {
                    Toast.makeText(this, "Please enter a valid amount of money", Toast.LENGTH_SHORT).show();
                    return;
                }
                moneyAmountMap.put(Integer.parseInt(banknoteProgrammaticalyAssignedIdKey.split("_")[0]), money);
            }
        }
        int totalInputMoney = getTotalMoneyFromBanknotes(moneyAmountMap);
        if (totalInputMoney == loanMoney) {
            removeLoan(userGivenLoanId);
            updateBanknotes(moneyAmountMap, getIntent().getExtras().get("userId").toString(), currency);
            Toast.makeText(this, "Loan repaid successfully!", Toast.LENGTH_SHORT).show();
            Intent finishCheckALoanActivityIntent = new Intent("finish_activity");
            sendBroadcast(finishCheckALoanActivityIntent);
            finish();
        } else if (totalInputMoney < loanMoney) {
            Toast.makeText(this, "Not enough money", Toast.LENGTH_SHORT).show();
        } else if (totalInputMoney > loanMoney) {
            Toast.makeText(this, "You are giving more than enough money", Toast.LENGTH_LONG).show();
        }
    }


    private void updateBanknotes(final HashMap<Integer, Integer> moneyAmountMap, String userId, final String currency) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("userBanknoteAmount");
        Query userBanknotesQuery = database.orderByChild("userId").equalTo(userId); //all userBanknoteAmount from the database associated with this user
        userBanknotesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userBanknoteAmountWithKey :
                        (dataSnapshot.getChildren())) {
                    UserBanknoteAmountBindModel userBanknoteAmountBindModelFromDb = userBanknoteAmountWithKey.getValue(UserBanknoteAmountBindModel.class);
                    if (userBanknoteAmountBindModelFromDb.getBanknoteType().split("_")[1].equals(currency)) {
                        BanknoteAmountAndBanknoteAmountTypeBindModel banknoteAmountAndBanknoteAmountTypeBindModelFromDb = new BanknoteAmountAndBanknoteAmountTypeBindModel();
                        banknoteAmountAndBanknoteAmountTypeBindModelFromDb.setBanknoteAmount(userBanknoteAmountBindModelFromDb.getBanknoteAmount());
                        banknoteAmountAndBanknoteAmountTypeBindModelFromDb.setBanknoteAmountType(Integer.parseInt(userBanknoteAmountBindModelFromDb.getBanknoteType().split("_")[0]));
                        int loanMoneyAmount = moneyAmountMap.get(banknoteAmountAndBanknoteAmountTypeBindModelFromDb.getBanknoteAmountType());
                        int currentDbMoneyAmount = banknoteAmountAndBanknoteAmountTypeBindModelFromDb.getBanknoteAmount();
                        database.child(userBanknoteAmountWithKey.getKey()).child("banknoteAmount").setValue(loanMoneyAmount + currentDbMoneyAmount);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RepayBanknotesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    private void removeLoan(String userGivenLoanId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("userGivenLoans");
        database.child(userGivenLoanId).removeValue();
    }

    private int getTotalMoneyFromBanknotes(HashMap<Integer, Integer> moneyAmountMap) {
        int totalMoney = 0;
        for (Integer banknoteType : moneyAmountMap.keySet()) {
            totalMoney = totalMoney + (banknoteType * moneyAmountMap.get(banknoteType));
        }

        return totalMoney;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
