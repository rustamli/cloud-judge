package openoli.cj.models;

import com.googlecode.objectify.Objectify;
import net.sf.json.JSONObject;
import openoli.cj.DAO;

import javax.persistence.Embedded;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Problem implements IRecord {

    @Id
    private Long id;
    private Long no;

    private Double timeLimit;
    private Double memoryLimit;
    private Double outputLimit;

    @Embedded
    private List<Test> tests;

    @Embedded
    private List<Translation> translations;

    private List<String> tags;

    private ECheckerType checkerType;
    
    private Float successPercentage;
    private Long submitsCount;

    private Long problemFileId;

    public Problem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public Double getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Double timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Double getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Double memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Double getOutputLimit() {
        return outputLimit;
    }

    public void setOutputLimit(Double outputLimit) {
        this.outputLimit = outputLimit;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ECheckerType getCheckerType() {
        return checkerType;
    }

    public void setCheckerType(ECheckerType checkerType) {
        this.checkerType = checkerType;
    }

    public Float getSuccessPercentage() {
        return successPercentage;
    }

    public void setSuccessPercentage(Float successPercentage) {
        this.successPercentage = successPercentage;
    }

    public Long getSubmitsCount() {
        return submitsCount;
    }

    public void setSubmitsCount(Long submitsCount) {
        this.submitsCount = submitsCount;
    }

    public Long getProblemFileId() {
        return problemFileId;
    }

    public void setProblemFileId(Long problemFileId) {
        this.problemFileId = problemFileId;
    }

    @Override
    public Long save() {
        // remove previous versions of the problem
        Objectify ofy = DAO.getOfy();
        List<Problem> oldVersions = ofy.query(Problem.class).filter("no", this.no).list();
        ofy.delete(oldVersions);

        ofy.put(this);
        return this.id;
    }

    public void setFieldsFromDescriptor(JSONObject json) {
        this.no = json.getLong("no");
        this.timeLimit = json.getDouble("time_limit");
        this.memoryLimit = json.getDouble("memory_limit");
        this.outputLimit = json.getDouble("output_limit");

        this.checkerType = ECheckerType.valueOf(json.getString("checker_type"));
    }

    public void setTestsFromMap(Map<Long, Test> tests) {
        this.tests = new ArrayList<Test>();
        int count = tests.size();
        long i = 1;

        while (i <= count) {
            this.tests.add(tests.get(i));
            i += 1;
        }
    }

    public static List<Problem> list() {
        return DAO.getOfy().query(Problem.class).order("no").list();
    }

    public Translation getTranslation(String langCode) {
        Translation requiredTranslation = null;
        Translation englishTranslation = null;
        if (translations != null)
            for (Translation translation : translations) {
                if (translation.getLang().toString().equals(langCode)) {
                    requiredTranslation = translation;
                    break;
                }
                else if (translation.getLang() == ELangType.ENGLISH) {
                    englishTranslation = translation;
                }
            }

        if ((requiredTranslation == null) && (englishTranslation != null))
            requiredTranslation = englishTranslation;

        return requiredTranslation;
    }
    
    public String getTitle(String langCode) {
        Translation translation = getTranslation(langCode);
        if (translation == null)
            return "{ invalid problem }";
        return translation.getTitle();
    }

    public static Problem getByNo(Long no) {
        return DAO.getOfy().query(Problem.class).filter("no", no).get();
    }

    public static Problem get(Long problemId) {
        return DAO.getOfy().get(Problem.class, problemId);
    }
}
