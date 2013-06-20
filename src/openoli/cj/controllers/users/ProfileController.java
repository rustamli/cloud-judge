package openoli.cj.controllers.users;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.models.Submit;
import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProfileController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        VelocityMaster vm = new VelocityMaster("views/website/users/profile.html", sessionInfo.getLangType());

        String idStr = req.getParameter("id");
        if (idStr == null) {
            res.sendRedirect("/");
            return;
        }

        Long id = Long.parseLong(idStr);

        Account profileAccount = Account.getById(id);
        vm.add("profileAccount", profileAccount);

        List<Submit> latestSubmits = Submit.listByAccountId(id, 25);
        vm.add("latestSubmits", latestSubmits);

        List<Submit> solvedProblems = Submit.filterSolvedByAccountId(id);
        vm.add("solvedProblems", solvedProblems);

        boolean isSignedIn = sessionInfo.getAccountId() > -1;
        vm.add("isSignedIn", isSignedIn);
        vm.add("langCode", sessionInfo.getLangType().toString());

        if (isSignedIn) {
            vm.add("account", Account.getById(sessionInfo.getAccountId()));
        }

        vm.add("url", req.getRequestURI());

        ResponseUtils.sendHTML(res, vm.render());
    }

}
