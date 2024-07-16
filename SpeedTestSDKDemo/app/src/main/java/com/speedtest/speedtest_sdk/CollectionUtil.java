package com.speedtest.speedtest_sdk;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectionUtil {
    public static boolean isEmpty(Map map) {
        return map == null || map.size() <= 0 || map.isEmpty();
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() <= 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length <= 0;
    }

    public static <T extends Number> double sumNum(Collection<T> list) {
        double sum = 0;
        for (T v : list) {
            sum += v.doubleValue();
        }
        return sum;
    }

    public static <T extends Number> float sumNumF(Collection<T> list) {
        float sum = 0;
        for (T v : list) {
            sum += v.doubleValue();
        }
        return sum;
    }

    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.get(obj) instanceof List) {
                map.put(field.getName(), new Gson().toJson(field.get(obj)));
            } else {
                map.put(field.getName(), String.valueOf(field.get(obj)));
            }
        }
        return map;
    }
}
