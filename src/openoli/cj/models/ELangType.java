package openoli.cj.models;

public enum ELangType {
    AZERBAIJANI("az"),
    ENGLISH("en"),
    RUSSIAN("ru");

    private String langCode;

    ELangType(String langCode) {
        this.langCode = langCode;
    }

    @Override
    public String toString() {
        return langCode;
    }

    public static ELangType getByCode(String langCode) {
        ELangType result = AZERBAIJANI;
        for (ELangType langType : values()) {
            if (langType.langCode.equals(langCode)) {
                result = langType;
                break;
            }
        }
        return result;
    }
}
