package repick.repickserver.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component @RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void InsertKeyValueToRedisWithPattern(String pattern, String key, String value) {
        redisTemplate.opsForValue().set(pattern + "#" + key, value);
    }

    public Set<String> getKeys() {
        return redisTemplate.keys("*");

    }

    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern + "*");
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<Long> getMemberIdsFromKeys(Set<String> keys) {
        List<String> expiredKeys = filterKeysAfter24Hours(keys);

        return getMemberIdsFromKeys(expiredKeys);
    }

    private List<String> filterKeysAfter24Hours(Set<String> keys) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        LocalDateTime currentTimeMinus24Hours = LocalDateTime.now().minusDays(1);

        return keys.stream()
                .filter(key -> {
                    String dateTimePart = key.split("#")[1];
                    LocalDateTime keyTime = LocalDateTime.parse(dateTimePart, formatter);
                    return keyTime.isBefore(currentTimeMinus24Hours);
                })
                .collect(Collectors.toList());

    }

    private void removeKeys(List<String> keys) {
        redisTemplate.delete(keys);
    }

    private List<Long> getMemberIdsFromKeys(List<String> keys) {
        List<String> valueSet = keys.stream()
                .map(this::getValue)
                .collect(Collectors.toList());

        removeKeys(keys);

        return valueSet.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
