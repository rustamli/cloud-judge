package openoli.cj;

import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtils {

    public static void sendHTML(HttpServletResponse res, String html) {

        try {
            if (html.equals(VelocityMaster.TEMPLATE_NOT_FOUND))
                res.sendRedirect("/");
            else {
                res.setContentType("text/html; charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().print(html);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
