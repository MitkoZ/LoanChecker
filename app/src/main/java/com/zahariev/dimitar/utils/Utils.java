package com.zahariev.dimitar.utils;

import android.support.annotation.IntegerRes;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class Utils {
    public static GoogleSignInAccount googleAccount;
    public static final List<Integer> BANKNOTES = asList(5, 10, 20, 50, 100);
    public static HashMap<String, Integer> programmaticallyAssignedIds = new HashMap<>();

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

