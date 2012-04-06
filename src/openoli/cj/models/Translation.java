package openoli.cj.models;

import com.googlecode.objectify.Key;
import openoli.cj.DAO;

import javax.persistence.Id;

public class Translation implements IRecord {

    @Id
    private Long id;

    private ELangType lang;
    private String title;
    private String text;

    public Translation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ELangType getLang() {
        return lang;
    }

    public void setLang(ELangType lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Long save() {
        DAO.getOfy().put(this);
        return this.id;
    }

    public Key<Translation> getKey() {
        return new Key<Translation>(Translation.class, id);
    }

    public static Translation getByKey(Key<Translation> translationKey) {
        return DAO.getOfy().get(translationKey);
    }
}
