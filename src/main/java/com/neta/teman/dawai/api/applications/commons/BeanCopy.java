package com.neta.teman.dawai.api.applications.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SuppressWarnings({"unchecked"})
public class BeanCopy {

    public static <T> T copy(Object source, Class<T> tClass) {
        T o = BeanUtils.instantiateClass(tClass);
        BeanUtils.copyProperties(source, o);
        return o;
    }

    public static void copy(Object destination, Object... sources) {
        copy(destination, new String[]{}, sources);
    }

    public static void copy(Object destination, String[] exclude, Object... sources) {
        for (Object source : sources) {
            BeanUtils.copyProperties(source, destination, exclude);
        }
    }

    // collection
    public static <E, T> List<T> copyCollection(List<E> source, Class<T> tClass) {
        return copyCollection(source, new ArrayList<T>(), tClass);
    }

    // collection
    public static <E, T> List<T> copyCollection(List<E> sources, List<T> destination, Class<T> tClass) {
        for (Object source : sources) {
            T o = BeanUtils.instantiateClass(tClass);
            BeanUtils.copyProperties(source, o);
            destination.add(o);
        }
        return destination;
    }

}
