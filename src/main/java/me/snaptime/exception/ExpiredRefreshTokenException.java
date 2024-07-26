package me.snaptime.exception;

public class ExpiredRefreshTokenException extends RuntimeException{
    public ExpiredRefreshTokenException(String m){
        super(m);
    }
}
