package com.zahariev.dimitar.utils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.zahariev.dimitar.bindmodels.BanknoteAmountAndBanknoteAmountTypeBindModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class Utils {
    public static GoogleSignInAccount googleAccount;
    public static final List<Integer> BANKNOTES = asList(5, 10, 20, 50, 100);
    public static HashMap<String, Integer> banknotesProgrammaticallyAssignedIds = new HashMap<>();
    public static HashMap<Integer, String> loansProgrammaticallyAssignedIds = new HashMap<>();

    public static int getElementByIndex(LinkedHashMap<Integer, Integer> map, int index) {
        return map.get((map.keySet().toArray())[index]);
    }

    public static BanknoteAmountAndBanknoteAmountTypeBindModel getBindModelElementByIndex(LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> map, int index) {
        return map.get((map.keySet().toArray())[index]);
    }

    public static Integer getKeyByIndex(LinkedHashMap<Integer, Integer> map, Integer index) {
        return (toIntArray(map.keySet().toArray()))[index];
    }

    public static String getStringKeyByIndex(LinkedHashMap<String, BanknoteAmountAndBanknoteAmountTypeBindModel> map, Integer index) {
        return (toStringArray(map.keySet().toArray()))[index];
    }


    private static Integer[] toIntArray(Object[] objectArray) {
        Integer[] integerArray = Arrays.copyOfRange(objectArray, 0, objectArray.length, Integer[].class);
        return integerArray;
    }

    private static String[] toStringArray(Object[] objectArray) {
        String[] integerArray = Arrays.copyOfRange(objectArray, 0, objectArray.length, String[].class);
        return integerArray;
    }

    public static int[] stringArrayToIntArray(String[] stringArray) {
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }

        return intArray;
    }


    public interface Predicate<T> {
        boolean contains(T item);
    }

    public static class CollectionUtil {

        public static <T> T find(final Collection<T> collection, final Predicate<T> predicate) {
            for (T item : collection) {
                if (predicate.contains(item)) {
                    return item;
                }
            }
            return null;
        }
    }

}

