
package openoli.cj;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;
import openoli.cj.models.*;

public class DAO extends DAOBase {

    private static DAO dao = null;

    static {
        ObjectifyService.register(Account.class);
        ObjectifyService.register(ProblemFile.class);
        ObjectifyService.register(Problem.class);
        ObjectifyService.register(Test.class);
        ObjectifyService.register(Translation.class);
        ObjectifyService.register(Tag.class);
    }


    public static Objectify getOfy() {
        if (dao == null)
            dao = new DAO();

        return dao.ofy();
    }
}

