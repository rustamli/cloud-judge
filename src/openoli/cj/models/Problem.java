package openoli.cj.models;

import com.googlecode.objectify.Key;
import net.sf.json.JSONObject;
import openoli.cj.DAO;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class Problem implements IRecord {

    @Id
    private Long id;
    private Long no;

    private Double timeLimit;
    private Double memoryLimit;
    private Double outputLimit;

    private List<Key<Test>> tests;
    private List<Key<Translation>> translations;
    private List<Key<Tag>> tags;

    private ECheckerType checkerType;
    
    private Float successPercentage;
    private Long submintsCount;

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

    public List<Key<Test>> getTests() {
        return tests;
    }

    public void setTests(List<Key<Test>> tests) {
        this.tests = tests;
    }

    public List<Key<Translation>> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Key<Translation>> translations) {
        this.translations = translations;
    }

    public List<Key<Tag>> getTags() {
        return tags;
    }

    public void setTags(List<Key<Tag>> tags) {
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

    public Long getSubmintsCount() {
        return submintsCount;
    }

    public void setSubmintsCount(Long submintsCount) {
        this.submintsCount = submintsCount;
    }

    public Long getProblemFileId() {
        return problemFileId;
    }

    public void setProblemFileId(Long problemFileId) {
        this.problemFileId = problemFileId;
    }

    @Override
    public Long save() {
        DAO.getOfy().put(this);
        return this.id;
    }

    public void setFieldsFromDescriptor(JSONObject json) {
        this.no = json.getLong("no");
        this.timeLimit = json.getDouble("time_limit");
        this.memoryLimit = json.getDouble("memory_limit");
        this.outputLimit = json.getDouble("output_limit");

        this.checkerType = ECheckerType.valueOf(json.getString("checker_type"));
    }

    public void setTestsFromList(List<Test> tests) {
        this.tests = new ArrayList<Key<Test>>();
        for (Test test : tests) {
            this.tests.add(test.getKey());
        }
    }

    public void setTranslationsFromList(List<Translation> translations) {
        this.translations = new ArrayList<Key<Translation>>();
        for (Translation translation : translations) {
            this.translations.add(translation.getKey());
        }
    }

    public static List<Problem> list() {
        return DAO.getOfy().query(Problem.class).list();
    }
    
    public String getTitle(String langCode) {
        Translation requiredTranslation = null;
        Translation englishTranslation = null;
        for (Key<Translation> translationKey : translations) {
            Translation translation = Translation.getByKey(translationKey);
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
        else {
            requiredTranslation = new Translation();
            requiredTranslation.setTitle(String.format("#%d", id));
        }

        return requiredTranslation.getTitle();
    }
}
