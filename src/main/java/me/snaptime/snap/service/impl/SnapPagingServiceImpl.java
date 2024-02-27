package me.snaptime.snap.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.dto.res.FindSnapPagingResDto;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapPagingServiceImpl {

    private final UserRepository userRepository;
    private final SnapRepository snapRepository;

    // snap 페이징 조회
    public List<FindSnapPagingResDto> findSnapPaging(String loginId, Long pageNum){
        User reqUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        List<Tuple> result = snapRepository.findSnapPaging(loginId,pageNum,reqUser);

        return result.stream()
                .map(entity -> {
                    return FindSnapPagingResDto.toDto(entity);
                })
                .collect(Collectors.toList());
    }

}
