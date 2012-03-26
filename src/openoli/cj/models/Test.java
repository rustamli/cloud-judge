package openoli.cj.models;

import com.googlecode.objectify.Key;
import openoli.cj.DAO;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class Test implements IRecord {

    @Id
    private Long id;

    private String input;
    private List<String> outputs;
    private String script;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Test() {
        input = "";
        script = "";
        outputs = new ArrayList<String>();
    }

    @Override
    public Long save() {
        DAO.getOfy().put(this);
        return this.id;
    }

    public void pushOutput(int index, String output) {
        if (index >= outputs.size()) {
            outputs.add(index, output);
        }
        else {
            outputs.set(index, output);
        }
    }

    public Key<Test> getKey() {
        return new Key<Test>(Test.class, id);
    }
}
