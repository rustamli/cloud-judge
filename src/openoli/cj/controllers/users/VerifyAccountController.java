package openoli.cj.controllers.users;

import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class VerifyAccountController extends HttpServlet {
       
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Account account = Account.getById(id);

            if (!account.getVerified()) {
                account.setVerified(true);
                Long accountId = account.save();

                SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
                sessionInfo.setAccountId(accountId);
                SessionManager.update(sessionInfo);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        res.sendRedirect("/");
    }
}
