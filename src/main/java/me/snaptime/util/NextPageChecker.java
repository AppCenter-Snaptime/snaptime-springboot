package me.snaptime.util;

import com.querydsl.core.Tuple;

import java.util.List;

public class NextPageChecker {

    public static boolean hasNextPage(List<Tuple> resultList, Long pageSize){

        boolean hasNextPage = resultList.size() <= pageSize ? false : true;
        return hasNextPage;
    }
}
