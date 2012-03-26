package openoli.cj.models;

import com.googlecode.objectify.Key;

import javax.persistence.Id;

public class Tag implements IRecord {

    @Id
    private Long id;

    private Key<Problem> problem;
    private String tagTitle;

    public Tag(String tagTitle, Key<Problem> problem) {
        this.tagTitle = tagTitle;
        this.problem = problem;
    }

    public Tag() {

    }

    @Override
    public Long save() {
        return id;
    }
}
