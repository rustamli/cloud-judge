package openoli.cj.controllers.general;

import openoli.cj.IController;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.ELangType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SetLangController extends HttpServlet implements IController {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());

        String lang = req.getParameter("lang");
        String fromUrl = req.getParameter("from");

        sessionInfo.setLangType(ELangType.getByCode(lang));
        SessionManager.update(sessionInfo);

        res.sendRedirect(fromUrl);
    }
}
