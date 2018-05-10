package com.zahariev.dimitar.utils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class Utils {
    public static GoogleSignInAccount googleAccount;
    public static final List<Integer> BANKNOTES = asList(5, 10, 20, 50, 100);
    public static HashMap<String, Integer> programmaticallyAssignedIds = new HashMap<>();

    public static int getElementByIndex(Ref<LinkedHashMap<Integer, Integer>> wrappedMap, int index) {
        return wrappedMap.get().get((wrappedMap.get().keySet().toArray())[index]);
    }

    public static Integer getKeyByIndex(Ref<LinkedHashMap<Integer, Integer>> wrappedMap, Integer index) {
        return (toIntArray(wrappedMap.get().keySet().toArray()))[index];
    }

    private static Integer[] toIntArray(Object[] objectArray) {
        Integer[] integerArray = Arrays.copyOfRange(objectArray, 0, objectArray.length, Integer[].class);
        return integerArray;
    }

}

