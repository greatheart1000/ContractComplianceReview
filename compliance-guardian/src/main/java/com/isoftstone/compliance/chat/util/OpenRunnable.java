package com.isoftstone.compliance.chat.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OpenRunnable {
	
    /**
     * 根据条件执行consumer
     * @param arg1
     * @param predicate
     * @param consumer
     */
    public static <T> void run(T arg1, Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate.test(arg1)) consumer.accept(arg1);
    }

}
