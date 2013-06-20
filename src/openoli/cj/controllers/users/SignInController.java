package openoli.cj.controllers.users;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.models.ELangType;
import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignInController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        renderSignInForm(req, res, new ArrayList<String>());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        List<String> validationResult = authenticate(email, password, req.getSession().getId());
        
        if (validationResult.isEmpty()) {
            res.sendRedirect(req.getParameter("from"));
        }
        else {
            renderSignInForm(req, res, validationResult);
        }
    }

    private List<String> authenticate(String email, String password, String sesionId) {

        List<String> result = new ArrayList<String>();
        Account account = Account.getByEmail(email);

        if (account == null) {
            result.add("$error:no-account-with-this-email");
            return result;
        }

        if (! account.getPassword().equals(password)) {
            result.add("$error:wrong-password");
            return result;
        }

        if (! account.getVerified()) {
            result.add("$error:not-verified-yet");
            return result;
        }

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(sesionId);
        sessionInfo.setAccountId(account.getId());
        SessionManager.update(sessionInfo);

        return result;
    }

    private void renderSignInForm(HttpServletRequest req, HttpServletResponse res, List<String> validationResult)
            throws IOException {

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        boolean isSignedIn = sessionInfo.getAccountId() > -1;

        if (isSignedIn) {
            res.sendRedirect("/");
            return;
        }

        ELangType langType = sessionInfo.getLangType();
        VelocityMaster vm = new VelocityMaster("views/website/users/sign-in.html", langType);
        vm.add("isSignedIn", false);
        vm.add("langCode", langType.toString());
        vm.add("url", req.getRequestURI());

        vm.add("from", req.getParameter("from"));

        if (!validationResult.isEmpty()) {
            vm.add("email", req.getParameter("email"));
            vm.add("validationResult", validationResult);
        }

        ResponseUtils.sendHTML(res, vm.render());
    }
}
