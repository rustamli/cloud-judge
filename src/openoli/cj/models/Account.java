
package openoli.cj.models;

import openoli.cj.DAO;

import javax.persistence.Id;

@SuppressWarnings("unused")
public class Account implements IRecord {

    @Id
    private Long id;

    private Long accessLevel;
    private String name;
    private String email;
    private String password;
    private String country;
    private String about;
    private Boolean verified;

    public Account(Long accessLevel, String name, String email, String password, String country,
                   String about, Boolean verified) {
        this.accessLevel = accessLevel;
        this.name = name;
        this.email = email;
        this.password = password;
        this.country = country;
        this.about = about;
        this.verified = verified;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Long accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public static Account getById(Long accountId) {
        return DAO.getOfy().get(Account.class, accountId);
    }

    public Long save() {
        DAO.getOfy().put(this);
        return this.getId();
    }

    public static Account getByEmail(String email) {
        return DAO.getOfy().query(Account.class).filter("email", email).get();
    }
}
