package com.zahariev.dimitar.loanchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zahariev.dimitar.bindmodels.UserCurrencyBindModel;
import com.zahariev.dimitar.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class AddCurrencyActivity extends AppCompatActivity {
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
    }

    public void addCurrency(View view) {
        EditText editTextView = findViewById(R.id.addCurrencyEditText);
        String currency = editTextView.getText().toString();
        if (currency.isEmpty()) {
            Toast.makeText(this, "Currency cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        UserCurrencyBindModel userCurrencyBindModel = new UserCurrencyBindModel(Utils.googleAccount.getId(), currency);
        writeUserCurrency(userCurrencyBindModel);
        Toast.makeText(this, "Currency added successfully!", Toast.LENGTH_SHORT).show();
        editTextView.setText("");
    }

    private void writeUserCurrency(UserCurrencyBindModel userCurrencyBindModel) {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("/userCurrencies/");
        Map<String, Object> userCurrencyMap = new HashMap<>();
        String key = mDatabaseReference.push().getKey();
        userCurrencyMap.put(key, userCurrencyBindModel);
        mDatabaseReference.updateChildren(userCurrencyMap);
    }

    public void cancel(View view) {
        finish();
    }
}
