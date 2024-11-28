package me.snaptime.user.service.impl;


import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindByNameResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.dto.res.UserPagingResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserService;
import me.snaptime.util.NextPageChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.user.domain.QUser.user;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UrlComponent urlComponent;

    @Transactional(readOnly = true)
    public UserFindResDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        return UserFindResDto.toDto(user);
    }

    @Override
    public UserPagingResDto findUserPageByName(String searchKeyword, Long pageNum){
        List<Tuple> tuples = userRepository.findUserPageByName(searchKeyword,pageNum);
        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);
        List<UserFindByNameResDto> userFindByNameResDtos = tuples.stream().map(tuple ->
        {
            String profilePhotoURL = urlComponent.makeProfileURL(tuple.get(user.profilePhoto.profilePhotoId));
            return UserFindByNameResDto.toDto(tuple,profilePhotoURL);
        }).collect(Collectors.toList());

        return UserPagingResDto.toDto(userFindByNameResDtos, hasNextPage);
    }

    public UserFindResDto updateUser(String email, UserUpdateReqDto userUpdateReqDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        if (userUpdateReqDto.name() != null && !userUpdateReqDto.name().isEmpty()) {
            user.updateUserName(userUpdateReqDto.name());
        }
        if(userUpdateReqDto.nickName() != null && !userUpdateReqDto.nickName().isEmpty()){
            user.updateNickName(userUpdateReqDto.nickName());
        }
        return UserFindResDto.toDto(user);
    }

    public void deleteUser(String email, String password) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        userRepository.deleteById(user.getUserId());
    }

    public void updatePassword(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.updateUserPassword(passwordEncoder.encode(password));
        }
        else{
            throw new CustomException(ExceptionCode.PASSWORD_DUPLICATE);
        }
    }

    @Override
    public void updateAuth(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));
        user.updateAuth();
    }


}