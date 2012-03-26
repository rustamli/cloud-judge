package openoli.cj;

import openoli.cj.models.ELangType;

import java.io.Serializable;

public class SessionInfo implements Serializable {
    private String sessionId;
    private Long accountId;
    private ELangType langType;

    public SessionInfo(String sessionId, Long accountId, ELangType langType) {
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.langType = langType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public ELangType getLangType() {
        return langType;
    }

    public void setLangType(ELangType langType) {
        this.langType = langType;
    }
}
