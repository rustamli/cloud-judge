package openoli.cj.controllers.general;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PagesController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) {

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        String template = getPageTemplateByUrl(req.getRequestURL().toString());

        VelocityMaster vm = new VelocityMaster(template, sessionInfo.getLangType());

        boolean isSignedIn = sessionInfo.getAccountId() > -1;
        vm.add("isSignedIn", isSignedIn);
        vm.add("langCode", sessionInfo.getLangType().toString());

        if (isSignedIn)
            vm.add("account", Account.getById(sessionInfo.getAccountId()));

        vm.add("url", req.getRequestURI());
        vm.add("p", req.getParameterMap());

        ResponseUtils.sendHTML(res, vm.render());
    }

    private String getPageTemplateByUrl(String url) {
        String template = "";
        String[] urlParts = url.split("/");

        if ((urlParts.length < 4) || urlParts[3].isEmpty()) {
            template = "views/website/home.html";
        }
        else if ((urlParts.length < 5) || urlParts[4].isEmpty()) {
            template = String.format("views/website/%s.html", urlParts[3]);
        }
        else {
            template = String.format("views/%s/%s.html", urlParts[3], urlParts[4]);
        }

        return template;
    }
}
