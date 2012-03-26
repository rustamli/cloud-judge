package openoli.cj.controllers.users;

import openoli.cj.IController;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignOutController extends HttpServlet implements IController {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
           throws IOException {

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        sessionInfo.setAccountId((long) -1);
        SessionManager.update(sessionInfo);

        res.sendRedirect("/");
    }
}
