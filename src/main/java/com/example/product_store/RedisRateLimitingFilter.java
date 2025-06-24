package com.example.product_store;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;

@Component
public class RedisRateLimitingFilter implements Filter{
    private final StringRedisTemplate redisTemplate;
    private static final int LIMIT = 40;
    private static final long WINDOW_DURATION = 60;

    public RedisRateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = httpRequest.getRemoteAddr();
        String key = "rate_limit:" + clientIp;
        Long requests = redisTemplate.opsForValue().increment(key, 1);

        if (requests == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_DURATION));
        }

        if (requests > LIMIT) {
            response.getWriter().write("Too Many Requests");
            response.getWriter().flush();
            return;
        }

        chain.doFilter(request, response);
    }
}
