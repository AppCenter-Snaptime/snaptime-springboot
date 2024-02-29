package me.snaptime.common.exception.customs;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    // Social Exception
    SELF_FRIEND_REQ(HttpStatus.BAD_REQUEST, "자신에게 친구추가 요청을 보낼 수 없습니다."),
    WATING_FRIEND_REQ(HttpStatus.BAD_REQUEST,"이미 친구요청을 보냈습니다."),
    REJECT_FRIEND_REQ(HttpStatus.BAD_REQUEST,"팔로우요청이 거절되었습니다."),
    ALREADY_FOLLOW(HttpStatus.BAD_REQUEST,"이미 팔로우관계입니다."),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 친구입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    ACCESS_FAIL_FRIENDSHIP(HttpStatus.FORBIDDEN,"해당 친구에 대한 권한이 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재하지 않는 페이지입니다."),

    // ProfilePhotoException
    PROFILE_PHOTO_EXIST(HttpStatus.BAD_REQUEST,"이미 프로필 사진이 존재합니다."),
    PROFILE_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 프로필 사진입니다."),
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND,"해당하는 경로에 파일이 존재하지 않습니다.");


    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
