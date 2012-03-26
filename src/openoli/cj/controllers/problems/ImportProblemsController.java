package openoli.cj.controllers.problems;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import openoli.cj.IController;
import openoli.cj.models.ProblemFile;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.logging.Logger;

public class ImportProblemsController extends HttpServlet implements IController {
    
    public static final Logger log = Logger.getLogger("ImportProblemsController");
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(req);
                
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();
                if (!item.isFormField() && (stream != null)) {
                    byte[] bytes = IOUtils.toByteArray(stream);
                    Blob blob = new Blob(bytes);
                    ProblemFile problemFile = new ProblemFile(blob);
                    Long problemFileId = problemFile.save();

                    addImportProblemTaskToDefaultQueue(problemFileId);
                }
                
                if (stream != null)
                    stream.close();

                resp.sendRedirect("/admin/problems?done=true");
            }
            
        } catch (Exception ex) {
            log.severe("Problem while importing a problem: " + ex.getMessage());
        }
    }

    private void addImportProblemTaskToDefaultQueue(Long problemFileId) {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/import-problem-task")
                .param("problemFileId", problemFileId.toString()));
    }
}
