package openoli.cj;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import openoli.cj.models.ELangType;

public class SessionManager {
    
    public static final Long GUEST = (long) -1;
    public static final ELangType DEF_LANG = ELangType.AZERBAIJANI;

    public static SessionInfo getSessionInfoById(String sessionId) {
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

        if (cache.contains(sessionId)) {
            return (SessionInfo) cache.get(sessionId);
        }
        else {
            SessionInfo sessionInfo = new SessionInfo(sessionId, GUEST, DEF_LANG);
            cache.put(sessionId, sessionInfo);

            return sessionInfo;
        }
    }

    public static void update(SessionInfo sessionInfo) {
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        String sessionId = sessionInfo.getSessionId();
        cache.put(sessionId, sessionInfo);
    }
}
