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
    FRIENDSHIP_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 친구입니다."),
    ACCESS_FAIL_FRIENDSHIP(HttpStatus.FORBIDDEN,"해당 친구에 대한 권한이 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재하지 않는 페이지입니다."),
    FRIENDSHIP_REQ_NOT_FOUND(HttpStatus.BAD_REQUEST, "수락대기중인 팔로우요청이 없습니다."),
    SNAPTAG_NOT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않는 태그유저입니다."),

    // Reply Exception
    REPLY_NOT_FOUND(HttpStatus.BAD_REQUEST,"존재하지 않는 댓글입니다."),
    ACCESS_FAIL_REPLY(HttpStatus.FORBIDDEN,"댓글에 대한 권한이 없습니다."),

    // SignIn Exception
    PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST,"잘못된 비밀번호를 입력하셨습니다."),

    // ProfilePhotoException
    PROFILE_PHOTO_EXIST(HttpStatus.BAD_REQUEST,"이미 프로필 사진이 존재합니다."),
    PROFILE_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 프로필 사진입니다."),
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND,"해당하는 경로에 파일이 존재하지 않습니다."),

    // Encryption Exception
    ENCRYPTION_NOT_EXIST(HttpStatus.BAD_REQUEST, "암호키를 찾을 수 없습니다."),
    ENCRYPTION_ERROR(HttpStatus.BAD_REQUEST, "암호화키를 생성하던 도중 문제가 발생했습니다. 관리자에게 문의해주세요"),

    // User Exception
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),
    PASSWORD_DUPLICATE(HttpStatus.BAD_REQUEST,"같은 비밀번호로 수정 할 수 없습니다."),
    LOGIN_ID_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 loginId 입니다."),
    SAME_LOGIN_ID(HttpStatus.BAD_REQUEST, "이전과 동일한 loginId 입니다."),

    // Snap Exception
    SNAP_NOT_EXIST(HttpStatus.BAD_REQUEST, "스냅이 존재하지 않습니다."),
    SNAP_USER_IS_NOT_THE_SAME(HttpStatus.BAD_REQUEST, "스냅을 저장한 유저와 스냅을 요청한 유저가 일치하지 않습니다."),

    // Photo Exception
    PHOTO_NOT_EXIST(HttpStatus.BAD_REQUEST, "사진을 찾을 수 없습니다."),

    // File Exception
    FILE_WRITE_ERROR(HttpStatus.BAD_REQUEST, "파일을 쓰던 중 문제가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "파일을 시스템에서 삭제하던 중 문제가 발생했습니다."),
    FILE_READ_ERROR(HttpStatus.BAD_REQUEST, "파일을 시스템에서 읽어오던 중 문제가 발생했습니다."),

    // Change Snap Visibility
    CHANGE_SNAP_VISIBILITY_ERROR(HttpStatus.BAD_REQUEST, "이미 설정되어 있습니다"),

    // Album Exception
    ALBUM_NOT_EXIST(HttpStatus.BAD_REQUEST, "앨범이 존재하지 않습니다."),
    ALBUM_ID_IS_NOT_GIVEN(HttpStatus.BAD_REQUEST, "앨범 id가 주어지지 않았습니다."),
    ALBUM_USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "앨범을 만든 사용자와 일치하지 않습니다."),

    // Jwt Exception
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,  "토큰이 만료되었습니다."),
    TOKEN_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없습니다"),
    TOKEN_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "인증되지 않은 토큰입니다."),
    TOKEN_INVALID_FORMAT(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "토큰이 비었거나 null입니다");


    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
