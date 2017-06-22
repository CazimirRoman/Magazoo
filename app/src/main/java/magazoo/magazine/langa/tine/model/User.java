package magazoo.magazine.langa.tine.model;

public class User {
    private String id;
    private int addQuota;

    public User() {
    }

    public User(String id, int addQuota) {
        this.id = id;

        this.addQuota = addQuota;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAddQuota() {
        return addQuota;
    }

    public void setAddQuota(int addQuota) {
        this.addQuota = addQuota;
    }
}
