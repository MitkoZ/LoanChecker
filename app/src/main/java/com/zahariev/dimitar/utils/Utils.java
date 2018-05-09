package com.zahariev.dimitar.utils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class Utils {
    public static GoogleSignInAccount googleAccount;
    public static final List<Integer> BANKNOTES = asList(5, 10, 20, 50, 100);
    public static HashMap<String, Integer> programmaticallyAssignedIds = new HashMap<>();
}

