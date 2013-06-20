package openoli.cj.models;

import com.google.appengine.api.datastore.Text;

public class Translation {

    private ELangType lang;
    private String title;
    private Text text;

    public Translation() {
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
        return text.getValue();
    }

    public void setText(String text) {
        this.text = new Text(text);
    }
}
