package osw.ff;

import java.net.URLEncoder;

public class UrlParamsBuilder {

    private StringBuilder paramsBuilder;

    public UrlParamsBuilder() {
        this.paramsBuilder = new StringBuilder();
    }

    public void add(String key, String value) {
        paramsBuilder.append(key).append("=").append(encode(value)).append("&");
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "ISO-8859-1");
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String toString() {

        String params = paramsBuilder.toString();
        if ((params != null) && params.endsWith("&"))
            params = params.substring(0, params.length() - 1);

        return params;
    }
}
