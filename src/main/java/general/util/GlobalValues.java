package general.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalValues {

    public static String jwtTokenCookieName;

    public static String jwtUsernameCookieName;

    public static String jwtLangCookieName;

    public static String jwtDomainName;

    public static Long jwtTimeout;

    public static String jwtLastQueryTime;

    public static String redisDomainName;

    public static String redisSubjectsName;

    public static String matnrImagePath;

    @Value("${matnr_image_path}")
    public void setMatnrImagePath(String matnrImagePath) {
        GlobalValues.matnrImagePath = matnrImagePath;
    }

    @Value("${cookie-params.jwt-token-name}")
    public void setJwtTokenCookieName(String jwtTokenCookieName) {
        GlobalValues.jwtTokenCookieName = jwtTokenCookieName;
    }

    @Value("${cookie-params.username-token-name}")
    public void setJwtUsernameCookieName(String jwtUsernameCookieName) {
        GlobalValues.jwtUsernameCookieName = jwtUsernameCookieName;
    }

    @Value("${cookie-params.lang-token-name}")
    public void setJwtLangCookieName(String jwtLangCookieName) {
        GlobalValues.jwtLangCookieName = jwtLangCookieName;
    }

    @Value("${cookie-params.domain}")
    public void setJwtDomainName(String jwtDomainName) {
        GlobalValues.jwtDomainName = jwtDomainName;
    }

    @Value("${cookie-params.last-query-time}")
    public void setJwtLastQueryTime(String jwtLastQueryTime) {
        GlobalValues.jwtLastQueryTime = jwtLastQueryTime;
    }

    @Value("${session-params.jwt-timeout}")
    public void setJwtTimeout(Long jwtTimeout) {
        GlobalValues.jwtTimeout = jwtTimeout;
    }

    @Value("${redis-params.domain}")
    public void setRedisDomainName(String redisDomainName) {
        GlobalValues.redisDomainName = redisDomainName;
    }

    @Value("${redis-params.subjects-name}")
    public void setRedisSubjectsName(String redisSubjectsName) {
        GlobalValues.redisSubjectsName = redisSubjectsName;
    }
}
