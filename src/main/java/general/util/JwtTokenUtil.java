package general.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static general.util.GlobalValues.redisSubjectsName;

public class JwtTokenUtil {

    private static final long serialVersionUID = -3301605591108950415L;

    private String secret = "secret";

    private Long expiration = 1320L; // in minute (multiple by 60*1000)
    
    public Date getExpirationDateFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        return claims.getExpiration();
    }

    public Long getUserId(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object userId = claims.get("userId");
        return userId != null ? Long.parseLong(String.valueOf(userId)) : null;
    }

    public String getLanguage(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object language = claims.get("language");
        return language != null ? String.valueOf(language) : null;
    }


    private Claims getAllClaimsFromToken(String token) {
    	String SECRET = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username, long userId, String language) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", "Admin");
        claims.put("language", language);
        return doGenerateToken(claims, username);
    }
    
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        String SECRET = Base64.getEncoder().encodeToString(secret.getBytes());

        System.out.println("doGenerateToken " + createdDate);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                //.setAudience(audience)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        RedisUtil.INSTANCE.sadd(redisSubjectsName, token);

        return token;
    }
    
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }
    
    public String refreshToken(String token, String language) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        String SECRET = Base64.getEncoder().encodeToString(secret.getBytes());

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        claims.put("language", language);

        String newToken =  Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        //RedisUtil.INSTANCE.srem(REDIS_SET_ACTIVE_SUBJECTS, token);
        RedisUtil.INSTANCE.sadd(redisSubjectsName, newToken);

        return newToken;
    }
    
    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 60000); // default 30 minute
    }	
	
	private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }	
   
}


