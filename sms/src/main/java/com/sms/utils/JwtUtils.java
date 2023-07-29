package com.sms.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    /**
     * 使用用户ID生成令牌
     *
     * @param id
     * @return
     */
    public static String generateToken(Long id, long timeout, String secretKey) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + timeout);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 从令牌中获取用户的ID
     *
     * @param token
     * @return
     */
    public static Long getUserIdFromToken(String token, String secretKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
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
    public static boolean validateToken(String token, String secretKey) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("无效的JWT签名", e);
            throw e;
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims字符串为空", e);
            throw e;
        }
    }
}
