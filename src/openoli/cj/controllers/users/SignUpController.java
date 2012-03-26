package openoli.cj.controllers.users;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.models.ELangType;
import openoli.cj.velocity.VelocityMaster;
import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SignUpController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        renderSignUpFormPage(req, res, new ArrayList<String>());
    }
    
    private void renderSignUpFormPage(HttpServletRequest req, HttpServletResponse res, List<String> validationResult)
        throws IOException {
        
        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        boolean isSignedIn = sessionInfo.getAccountId() > -1;

        if (isSignedIn) {
            res.sendRedirect("/");
            return;
        }

        ELangType langType = sessionInfo.getLangType();
        VelocityMaster vm = new VelocityMaster("views/website/users/sign-up.html", langType);
        vm.add("isSignedIn", false);
        vm.add("langCode", langType.toString());
        vm.add("url", req.getRequestURI());
        if (!validationResult.isEmpty()) {
            vm.add("name", req.getParameter("name"));
            vm.add("email", req.getParameter("email"));
            vm.add("about", req.getParameter("about"));
            vm.add("country", req.getParameter("country"));
            vm.add("validationResult", validationResult);
        }
        ResponseUtils.sendHTML(res, vm.render());
    }

    private void renderConfirmAccount(HttpServletRequest req, HttpServletResponse res) {
        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());

        ELangType langType = sessionInfo.getLangType();
        VelocityMaster vm = new VelocityMaster("views/website/users/sign-up.html", langType);
        vm.add("isSignedIn", false);
        vm.add("langCode", langType.toString());
        vm.add("url", req.getRequestURI());
        vm.add("showConfirmMsg", true);

        ResponseUtils.sendHTML(res, vm.render());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        List<String> validationResult = validate(req);
        
        if (validationResult.isEmpty()) {
            
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            
            Account account = new Account(
                    (long) 1,
                    name,
                    email,
                    req.getParameter("password"),
                    req.getParameter("country"),
                    req.getParameter("about"),
                    false
            );

            Long id = account.save();
            sendConfirmationEmail(id, name, email);

            renderConfirmAccount(req, res);
        }
        else {
            renderSignUpFormPage(req, res, validationResult);
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

        //TODO check for an account with same email

        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");

        if ((password == null) || (password2 == null) || (password.length() < 6) || !password.equals(password2))
            result.add("$error:invalid-password");

        return result;
    }


    private Boolean sendConfirmationEmail(Long id, String receiverName, String receiverMail) {

        Boolean invitationSent = false;

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        VelocityMaster vm = new VelocityMaster("views/emails/confirm.html");
        vm.add("name", receiverName);
        vm.add("id", id);

        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mc2run@gmail.com", "Cloud Judge"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverMail, receiverName));
            msg.setSubject("Confirm your registration");
            msg.setText(vm.render());
            Transport.send(msg);
            invitationSent = true;

        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        } catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
        }
        return invitationSent;
    }
    
}
