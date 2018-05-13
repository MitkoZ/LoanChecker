package com.zahariev.dimitar.loanchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahariev.dimitar.bindmodels.LoanBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.text.MessageFormat;

public class CheckALoanActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_activity")) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_aloan);
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
        int viewId = (int) getIntent().getExtras().get("viewId");
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference("userGivenLoans");
        String userGivenLoansId = Utils.loansProgrammaticallyAssignedIds.get(viewId);
        Query selectedUserGivenLoanQuery = database.orderByKey().equalTo(userGivenLoansId);
        selectedUserGivenLoanQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LoanBindModel loanBindModel = (dataSnapshot.getChildren()).iterator().next().getValue(LoanBindModel.class);
                LinearLayout checkALoanLinearLayout = findViewById(R.id.checkALoanLinearLayout);
                TextView loaneeTextView = new TextView(getApplicationContext());
                loaneeTextView.setText("Loanee name: " + loanBindModel.getLoaneeName());
                TextView amountTextView = new TextView(getApplicationContext());
                amountTextView.setText(MessageFormat.format("Amount: {0} {1}", loanBindModel.getAmount(), loanBindModel.getCurrency()));
                DatePicker returnDateDatePicker = new DatePicker(getApplicationContext());
                int[] returnDateIntArray = Utils.stringArrayToIntArray(loanBindModel.getReturnDate().split("/"));
                returnDateDatePicker.updateDate(returnDateIntArray[2], returnDateIntArray[1] - 1, returnDateIntArray[0]);
                Button repayButton = new Button(getApplicationContext());
                repayButton.setText("Repay");
                repayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent repayBanknotesIntent = new Intent(getApplicationContext(), RepayBanknotesActivity.class);
                        repayBanknotesIntent.putExtra("givenCurrency", loanBindModel.getCurrency());
                        repayBanknotesIntent.putExtra("moneyAmount", loanBindModel.getAmount());
                        repayBanknotesIntent.putExtra("userGivenLoanId", getIntent().getExtras().get("userGivenLoanId").toString());
                        repayBanknotesIntent.putExtra("userId", loanBindModel.getUserId());
                        startActivity(repayBanknotesIntent);
                    }
                });
                checkALoanLinearLayout.setOrientation(LinearLayout.VERTICAL);
                checkALoanLinearLayout.addView(loaneeTextView);
                checkALoanLinearLayout.addView(amountTextView);
                checkALoanLinearLayout.addView(returnDateDatePicker);
                checkALoanLinearLayout.addView(repayButton);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CheckALoanActivity.this, "Could not load data.", Toast.LENGTH_SHORT).show();
                Log.wtf("onLoadUserGivenLoanFailed", "Could not load the data associated with userGivenLoan");
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
