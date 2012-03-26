package openoli.cj.models;

import com.google.appengine.api.datastore.Blob;
import openoli.cj.DAO;

import javax.persistence.Id;

public class ProblemFile implements IRecord {
    
    @Id
    private Long id;
    
    private Blob file;

    public ProblemFile(Blob file) {
        this.file = file;
    }

    public ProblemFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }
    
    public Long save() {
        DAO.getOfy().put(this);
        return this.id;
    }

    public static ProblemFile getById(Long id) {
        return DAO.getOfy().get(ProblemFile.class, id);
    }
}

