package openoli.cj.controllers.problems;

import com.google.appengine.repackaged.com.google.common.io.ByteStreams;
import net.sf.json.JSONObject;
import openoli.cj.models.*;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImportProblemTask extends HttpServlet {

    private static Logger log = Logger.getLogger("ImportProblemTask");
    private static final String PROBLEM_DESCRIPTOR_FILE_NAME = "problem.json";
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) {

        String strProblemFileId = req.getParameter("problemFileId");

        try {
            Long problemFileId = Long.parseLong(strProblemFileId);
            ProblemFile problemFile = ProblemFile.getById(problemFileId);
            importProblemFile(problemFile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.severe(
                    String.format("Problems while processing ProblemFile with id: %s. Error message: %s",
                        strProblemFileId, ex.getMessage())
            );
        }
    }

    private void importProblemFile(ProblemFile problemFile) throws IOException {
        ZipInputStream zipStream = getZipInputStreamFromProblemFile(problemFile);
        ZipEntry zipEntry = zipStream.getNextEntry();
        Problem problem = new Problem();
        Map<Long, Test> tests = new HashMap<Long, Test>();
        List<Translation> translations = new ArrayList<Translation>();

        while (zipEntry != null) {
            String name = zipEntry.getName();

            log.info(name);
            
            if (name.equals(PROBLEM_DESCRIPTOR_FILE_NAME)) {
                String descriptorStr = convertZipStreamToString(zipStream);
                JSONObject descriptorJson = JSONObject.fromObject(descriptorStr);
                problem.setFieldsFromDescriptor(descriptorJson);
            }
            
            else if (name.startsWith("tests") && name.endsWith(".txt")) {
                processTestEntry(zipStream, tests, name);
            }

            else if (name.startsWith("translations") && name.endsWith(".html")) {
                processTranslationEntry(zipStream, translations, name);
            }

            zipEntry = zipStream.getNextEntry();
        }

        zipStream.close();

        problem.setTestsFromMap(tests);
        problem.setTranslations(translations);
        problem.setProblemFileId(problemFile.getId());

        problem.save();
    }

    private void processTranslationEntry(ZipInputStream zipStream, List<Translation> translations, String name) throws IOException {
        String[] nameParts = name.split("/");
        Translation translation = new Translation();

        String langCode = nameParts[1].substring(0, 2);
        ELangType langType = ELangType.getByCode(langCode);
        translation.setLang(langType);

        String text = convertZipStreamToString(zipStream);

        String[] textParts = text.split("</title>");
        translation.setTitle(textParts[0].substring(7)) ;
        translation.setText(textParts[1]);

        translations.add(translation);
    }

    private void processTestEntry(ZipInputStream zipStream, Map<Long, Test> tests, String name) throws IOException {
        String[] nameParts = name.split("/");
        Test test = getTestByEntryNameParts(tests, nameParts);

        String fileName = nameParts[2];
        if (fileName.startsWith("in")) {
            String input = convertZipStreamToString(zipStream);
            test.setInput(input);
        }
        else if (fileName.startsWith("out")) {
            String output = convertZipStreamToString(zipStream);
            String strIndex = fileName.substring(4, fileName.length()-4);
            int index = Integer.parseInt(strIndex) - 1;
            test.pushOutput(index, output);
        }
        else if (fileName.startsWith("script")) {
            String script = convertZipStreamToString(zipStream);
            test.setScript(script);
        }
    }

    private Test getTestByEntryNameParts(Map<Long, Test> tests, String[] nameParts) {
        long noOfTest = Long.parseLong(nameParts[1]);

        Test test;
        if (! tests.containsKey(noOfTest)) {
            test = new Test();
            tests.put(noOfTest, test);
        }
        else {
            test = tests.get(noOfTest);
        }

        return test;
    }

    private ZipInputStream getZipInputStreamFromProblemFile(ProblemFile problemFile) throws IOException {
        byte[] problemFileBytes = problemFile.getFile().getBytes();
        ByteArrayInputStream stream = ByteStreams.newInputStreamSupplier(problemFileBytes).getInput();
        CheckedInputStream checksum = new CheckedInputStream(stream, new Adler32());
        return new ZipInputStream(new BufferedInputStream(checksum));
    }

    private String convertZipStreamToString(ZipInputStream zipStream) throws IOException {
        return IOUtils.toString(zipStream, "UTF-8");
    }
}
