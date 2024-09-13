package me.snaptime.util;

import com.querydsl.core.Tuple;
import me.snaptime.reply.domain.ChildReply;

import java.util.List;

public class NextPageChecker {

    public static boolean hasNextPage(List<Tuple> resultList, Long pageSize){

        boolean hasNextPage = resultList.size() <= pageSize ? false : true;
        if(hasNextPage)
            resultList.remove(pageSize);

        return hasNextPage;
    }

    public static boolean hasNextPageByChildReplies(List<ChildReply> resultList, Long pageSize){
        boolean hasNextPage = resultList.size() <= pageSize ? false : true;
        if(hasNextPage)
            resultList.remove(pageSize);

        return hasNextPage;
    }
}
