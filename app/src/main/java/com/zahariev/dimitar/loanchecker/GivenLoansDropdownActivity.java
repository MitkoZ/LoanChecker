package com.zahariev.dimitar.loanchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.util.Random;

import static com.zahariev.dimitar.utils.Utils.loansProgrammaticallyAssignedIds;

public class GivenLoansDropdownActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_loans);
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
        final ScrollView loansActivityScrollView = findViewById(R.id.loansActivityScrollView);
        updateUI(loansActivityScrollView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final ScrollView loansActivityScrollView = findViewById(R.id.loansActivityScrollView);
        loansActivityScrollView.removeAllViews();
        updateUI(loansActivityScrollView);
    }

    private void updateUI(final ScrollView scrollViewContainer) {
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference("userGivenLoans");
        Query givenLoansQuery = database.orderByChild("userId").equalTo(Utils.googleAccount.getId());
        givenLoansQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            LinearLayout allLoansLinearLayoutContainer = new LinearLayout(getApplicationContext());

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(GivenLoansDropdownActivity.this, "You haven't given any loans", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                for (final DataSnapshot givenLoan :
                        dataSnapshot.getChildren()) {
                    final LoanBindModel loanBindModel = givenLoan.getValue(LoanBindModel.class);
                    TextView loaneeTextView = new TextView(getApplicationContext());
                    loaneeTextView.setText(MessageFormat.format("Loanee: {0}", loanBindModel.getLoaneeName()));
                    loaneeTextView.setTextColor(Color.BLACK);
                    TextView returnDateTextView = new TextView(getApplicationContext());
                    returnDateTextView.setText(MessageFormat.format("Return date: {0}", loanBindModel.getReturnDate()));
                    returnDateTextView.setTextColor(Color.BLACK);
                    TextView moneyTextView = new TextView(getApplicationContext());
                    moneyTextView.setText(MessageFormat.format("Money: {0} {1}", loanBindModel.getAmount(), loanBindModel.getCurrency()));
                    moneyTextView.setTextColor(Color.BLACK);;
                    final LinearLayout loanLinearLayoutContainer = new LinearLayout(getApplicationContext());
                    final int id = View.generateViewId();
                    loanLinearLayoutContainer.setId(id);
                    loansProgrammaticallyAssignedIds.put(id, givenLoan.getKey());
                    loanLinearLayoutContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent checkLoanIntent = new Intent(getApplicationContext(), CheckALoanActivity.class);
                            checkLoanIntent.putExtra("viewId", loanLinearLayoutContainer.getId());
                            checkLoanIntent.putExtra("moneyAmount", loanBindModel.getAmount());
                            checkLoanIntent.putExtra("userGivenLoanId", givenLoan.getKey());
                            checkLoanIntent.putExtra("userId", loanBindModel.getUserId());
                            startActivity(checkLoanIntent);
                        }
                    });

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    loanLinearLayoutContainer.setLayoutParams(layoutParams);
                    loanLinearLayoutContainer.setOrientation(LinearLayout.VERTICAL);
                    Random random = new Random();
                    loanLinearLayoutContainer.setBackgroundColor(Color.argb(255, random.nextInt(100) + 100, random.nextInt(256), random.nextInt(50) + 200));
                    loanLinearLayoutContainer.addView(loaneeTextView);
                    loanLinearLayoutContainer.addView(returnDateTextView);
                    loanLinearLayoutContainer.addView(moneyTextView);
                    allLoansLinearLayoutContainer.addView(loanLinearLayoutContainer);
                }
                allLoansLinearLayoutContainer.setOrientation(LinearLayout.VERTICAL);
                scrollViewContainer.addView(allLoansLinearLayoutContainer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("onGivenLoansActivityQueryFailed", "The loading of the given loans query has failed");
                Toast.makeText(GivenLoansDropdownActivity.this, "", Toast.LENGTH_SHORT).show();
                finish();
            }

        });

    }


}
