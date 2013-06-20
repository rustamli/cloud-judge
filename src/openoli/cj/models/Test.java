package openoli.cj.models;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Serialized;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private Text input;

    @Serialized
    private List<Text> outputs;

    private Text script;

    public String getInput() {
        return input.getValue();
    }

    public void setInput(String input) {
        this.input = new Text(input);
    }

    public List<String> getOutputs() {
        List<String> strList = new ArrayList<String>();
        for (Text output : outputs) {
            strList.add(output.getValue());
        }
        return strList;
    }

    public void setOutputs(List<String> outputs) {
        List<Text> result = new ArrayList<Text>();
        for (String output : outputs) {
            result.add(new Text(output));
        }

        this.outputs = result;
    }

    public String getScript() {
        return script.getValue();
    }

    public void setScript(String script) {
        this.script = new Text(script);
    }

    public Test() {
        input = new Text("");
        script = new Text("");
        outputs = new ArrayList<Text>();
    }

    public void pushOutput(int index, String output) {
        Text outTxt = new Text(output);
        if (index >= outputs.size()) {
            outputs.add(index, outTxt);
        }
        else {
            outputs.set(index, outTxt);
        }
    }

}
