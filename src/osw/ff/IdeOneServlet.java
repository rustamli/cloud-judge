package osw.ff;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import openoli.cj.velocity.VelocityMaster;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class IdeOneServlet extends HttpServlet {

    private String code;
    private String input;

    private static final int WAIT_TIME_MS = 2000;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String fetchResult = tryIdeOneRequest();

        code = req.getParameter("code");
        input = req.getParameter("input");

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(fetchResult);
    }

    private String tryIdeOneRequest() {
        try {
            String submitLink = submitCodeRequest();
            Thread.sleep(WAIT_TIME_MS);
            return getSubmitDetailsRequest(submitLink);
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    private String getSubmitDetailsRequest(String submitLink) throws IOException {
        URL url = new URL(submitLink);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        Source source = new Source(reader);
        String output = getOutputFromSrc(source);
        String info = getInfoFromSrc(source);

        VelocityMaster vm = new VelocityMaster("output.html");
        vm.add("output", output);
        vm.add("info", info);

        return vm.render();
    }

    private String getInfoFromSrc(Source source) {

        Element infoContainerParent = source.getAllElementsByClass("li_inouterr").get(1);
        Element infoContainer = infoContainerParent.getAllElements(HTMLElementName.DIV).get(1);

        Element infoLine = infoContainer.getFirstElement();

        String time = "";
        String memory = "";

        Iterator<Segment> iter =  infoLine.getNodeIterator();
        while (iter.hasNext()) {
            Segment seg = iter.next();
            String segTxt = seg.toString();

            if (segTxt.contains("time: "))
                time = segTxt;
            else if (segTxt.contains("memory: "))
                memory = segTxt;
        }

        return String.format("%s %s", time, memory);
    }

    private String getOutputFromSrc(Source source) {
        List<Element> outDivs = source.getAllElementsByClass("out");
        Element outputContainer = outDivs.get(0).getAllElementsByClass("box").get(0);
        return outputContainer.getContent().toString();
    }


    private String submitCodeRequest() throws IOException {
        URL url = new URL("http://ideone.com/ideone/Index/submit");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        UrlParamsBuilder urlParamsBuilder = createRequestParams();
        OutputStreamWriter cnxOutWriter = new OutputStreamWriter(connection.getOutputStream());
        cnxOutWriter.write(urlParamsBuilder.toString());
        cnxOutWriter.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        return getLinkFromReader(reader);
    }

    private String getLinkFromReader(BufferedReader reader) {
        try {
            Source source = new Source(reader);
            Element linkInput = source.getElementById("link_presentation");

            return linkInput.getAttributeValue("value");
        } catch (IOException ex) {
            return "";
        }
    }

    private UrlParamsBuilder createRequestParams() {
        UrlParamsBuilder urlParamsBuilder = new UrlParamsBuilder();
        urlParamsBuilder.add("lang", "22");
        urlParamsBuilder.add("run", "1");
        urlParamsBuilder.add("private", "0");
        urlParamsBuilder.add("public", "1");
        urlParamsBuilder.add("Submit", "submit");
        urlParamsBuilder.add("file", code);
        urlParamsBuilder.add("syntax", "0");
        urlParamsBuilder.add("input", input);
        urlParamsBuilder.add("note", "");

        return urlParamsBuilder;
    }
}
