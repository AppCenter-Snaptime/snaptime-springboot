package me.snaptime.component.redis.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.component.redis.RedisComponent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Transactional
public class RedisComponentImpl implements RedisComponent {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public String getValues(String key){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        if(valueOperations.get(key) == null) return "false";
        return (String) valueOperations.get(key);
    }

    @Override
    public void setValuesExpire(String key, String data, Duration duration){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data, duration);
    }

    @Override
    public boolean checkExistSValue(String value){
        return !value.equals("false");
    }
}
