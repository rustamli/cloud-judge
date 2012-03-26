package openoli.cj.velocity;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import net.sf.json.JSONObject;
import openoli.cj.SessionManager;
import openoli.cj.models.ELangType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VelocityMaster {

    public Map<String, Object> data = null;
    private String templateName;
    private String langCode;

    private final static String LANG_FILE_PATH_FORMAT = "commons/langs/%s.json";
    public final static String TEMPLATE_NOT_FOUND = "[template-not-found]";

    public VelocityMaster(String templateName) {
        this.templateName = templateName;
        this.langCode = SessionManager.DEF_LANG.toString();
    }

    public VelocityMaster(String templateName, ELangType langType) {
        this.templateName = templateName;
        this.langCode = langType.toString();
    }

    public void init() {
        data = new HashMap<String, Object>();
    }

    public void add(String key, Object value) {
        if (data == null)
            init();

        data.put(key, value);
    }

    public void remove(String key) {
        if (data != null)
            data.remove(key);
    }


    public String render() {

        if (data == null)
            init();

        VelocityEngineManager.init();

        VelocityContext context = new VelocityContext();
        for (Map.Entry<String, Object> entry : data.entrySet())
            context.put(entry.getKey(), entry.getValue());

        JSONObject langData = getLangData();
        context.put("lang", langData);

        Template template = null;

        try {
            template = VelocityEngineManager.getTemplate(templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringWriter writer = new StringWriter();
        if (template != null) {
            template.merge(context, writer);
        } else {
            writer.write(TEMPLATE_NOT_FOUND);
        }

        return writer.toString();
    }

    public JSONObject getLangData() {

        JSONObject langData;

        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        String cachedLangKey = "lang:" + langCode;

        if (cache.contains(cachedLangKey)) {
            langData = (JSONObject) cache.get(cachedLangKey);
        }
        else {
            String langDataStr = tryReadLangFile();
            langData = JSONObject.fromObject(langDataStr);
            cache.put(cachedLangKey, langData);
        }

        return langData;
    }

    private String tryReadLangFile() {
        try {
            return readLangFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "{}";
    }

    private String readLangFile() throws IOException {
        FileInputStream file = new FileInputStream(String.format(LANG_FILE_PATH_FORMAT, langCode));
        InputStreamReader inStream = new InputStreamReader(file, "UTF-8");
        BufferedReader reader = new BufferedReader(inStream);

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
            sb.append(line);

        inStream.close();

        return sb.toString();
    }
}