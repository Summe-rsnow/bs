package com.sms.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Slf4j
public class JwtUtils {
    @Value("${sms.my-secret-key}")
    private static String SECRET_KEY;

    @Value("${sms.timeout}")
    // 超时时间
    private static long EXPIRATION_TIME;

    /**
     * 使用用户ID生成令牌
     *
     * @param id
     * @return
     */
    public static String generateToken(Long id) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从令牌中获取用户的ID
     *
     * @param token
     * @return
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    /**
     * 判断令牌的有效性
     *
     * @param token
     * @return
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("无效的JWT签名", ex);
        } catch (MalformedJwtException ex) {
            log.error("无效的JWT令牌", ex);
        } catch (ExpiredJwtException ex) {
            log.error("JWT令牌已过期", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("不支持的JWT令牌", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims字符串为空", ex);
        }
        return false;
    }
}
