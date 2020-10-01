package com.unclepaul.uicacheprototype.utils;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Utils {

    private Utils(){}

    public static <T> Stream<T> pickRandom(List<T> list, int n) {


        Random random = new Random();
        return IntStream
                .generate(() -> random.nextInt(list.size()))
                .distinct()
                .limit(n)
                .mapToObj(list::get);
    }

}

