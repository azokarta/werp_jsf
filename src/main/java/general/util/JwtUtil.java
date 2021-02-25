package general.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static general.util.GlobalValues.jwtTokenCookieName;
import static general.util.GlobalValues.redisSubjectsName;

public class JwtUtil {

    public static String generateToken(String signingKey, String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, signingKey);

        return builder.compact();
    }

    public static String getSubject(String token, String signingKey){
        if(token == null) return null;
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
    }

    public static String getSubject(HttpServletRequest httpServletRequest, String jwtTokenCookieName, String signingKey){
        String token = CookieUtil.getValue(httpServletRequest, jwtTokenCookieName);
        if(token == null) return null;
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
    }

    public static void invalidateRelatedTokens(HttpServletRequest httpServletRequest) {
        RedisUtil.INSTANCE.srem(redisSubjectsName, CookieUtil.getValue(httpServletRequest, jwtTokenCookieName));
    }
}
