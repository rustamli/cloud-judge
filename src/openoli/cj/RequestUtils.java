package openoli.cj;

import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    private static Map<String, String> processParams(Map<String, String[]> parameterMap) {
        Map<String, String> result = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet())
            result.put(entry.getKey(), entry.getValue()[0]);

        return result;
    }

}
