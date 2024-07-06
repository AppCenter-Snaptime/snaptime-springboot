package me.snaptime.common.component.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NextPageChecker {

    public boolean hasNextPage(List<Tuple> resultList, Long pageSize){

        boolean hasNextPage = resultList.size() <= pageSize ? false : true;
        return hasNextPage;
    }
}
