package openoli.cj.controllers.problems;

import openoli.cj.IController;
import openoli.cj.ResponseUtils;
import openoli.cj.SessionInfo;
import openoli.cj.SessionManager;
import openoli.cj.models.Account;
import openoli.cj.models.Problem;
import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ProblemListController extends HttpServlet implements IController {

    public void doGet(HttpServletRequest req, HttpServletResponse res) {

        SessionInfo sessionInfo = SessionManager.getSessionInfoById(req.getSession().getId());
        VelocityMaster vm = new VelocityMaster("views/website/problems.html", sessionInfo.getLangType());

        boolean isSignedIn = sessionInfo.getAccountId() > -1;
        vm.add("isSignedIn", isSignedIn);
        vm.add("langCode", sessionInfo.getLangType().toString());

        if (isSignedIn)
            vm.add("account", Account.getById(sessionInfo.getAccountId()));

        List<Problem> problems = Problem.list();
        vm.add("problems", problems);

        vm.add("url", req.getRequestURI());
        vm.add("p", req.getParameterMap());

        ResponseUtils.sendHTML(res, vm.render());
    }
}
