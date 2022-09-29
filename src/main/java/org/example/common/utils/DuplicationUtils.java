package org.example.common.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class DuplicationUtils {

    public static <T> Predicate<T> duplication(Function<? super T, ?> key) {
        final Set<Object> set = ConcurrentHashMap.newKeySet();
        return predicated -> set.add(key.apply(predicated));
    }

    public static <T> Predicate<T> notDuplication(Function<? super T, ?> key) {
        final Set<Object> set = ConcurrentHashMap.newKeySet();
        return predicated -> !set.add(key.apply(predicated));
    }
}
