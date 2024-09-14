package me.snaptime.component.redis;

import java.time.Duration;

public interface RedisComponent {

    String getValues(String key);
    void setValuesExpire(String key, String data, Duration duration);
    boolean checkExistSValue(String value);
}
