package openoli.cj.controllers.users;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.models.ELangType;
import openoli.cj.velocity.VelocityMaster;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        renderEditProfileFormPage(req, res, new ArrayList<String>());
    }
    
    private void renderEditProfileFormPage(HttpServletRequest req, HttpServletResponse res, List<String> validationResult)
        throws IOException {
        
        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        boolean isSignedIn = sessionInfo.getAccountId() > -1;

        if (!isSignedIn ) {
            res.sendRedirect("/");
            return;
        }

        ELangType langType = sessionInfo.getLangType();
        VelocityMaster vm = new VelocityMaster("views/website/users/edit-profile.html", langType);
        vm.add("isSignedIn", true);
        vm.add("langCode", langType.toString());
        vm.add("url", req.getRequestURI());

        Account account = Account.getById(sessionInfo.getAccountId());
        vm.add("account", account);

        if (!validationResult.isEmpty()) {
            vm.add("name", req.getParameter("name"));
            vm.add("email", req.getParameter("email"));
            vm.add("about", req.getParameter("about"));
            vm.add("country", req.getParameter("country"));
            vm.add("validationResult", validationResult);
        }
        else {
            vm.add("name", account.getName());
            vm.add("email", account.getEmail());
            vm.add("about", account.getAbout());
            vm.add("password", account.getPassword());
            vm.add("country", account.getCountry());
        }
        ResponseUtils.sendHTML(res, vm.render());
    }


    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        List<String> validationResult = validate(req);
        
        if (validationResult.isEmpty()) {

            SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
            boolean isSignedIn = sessionInfo.getAccountId() > -1;

            if (!isSignedIn) {
                res.sendRedirect("/");
                return;
            }


            Account account = Account.getById(sessionInfo.getAccountId());

            account.setName(req.getParameter("name"));
            account.setEmail(req.getParameter("email"));
            account.setPassword(req.getParameter("password"));
            account.setCountry(req.getParameter("country"));
            account.setAbout(req.getParameter("about"));

            Long id = account.save();
            res.sendRedirect("/profile?id=" + id);
        }
        else {
            renderEditProfileFormPage(req, res, validationResult);
        }
        
    }

    private List<String> validate(HttpServletRequest req) {
        List<String> result = new ArrayList<String>();

        String name = req.getParameter("name");
        if ((name == null) || (name.length() < 3) || (name.length() > 18)) {
            result.add("$error:invalid-name");
        }

        String email = req.getParameter("email");
        if (!EmailValidator.getInstance().isValid(email)) {
            result.add("$error:invalid-email");
        }

        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");

        if ((password == null) || (password2 == null) || (password.length() < 6) || !password.equals(password2))
            result.add("$error:invalid-password");

        return result;
    }
}
