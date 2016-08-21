package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * Created by PB5N0034 on 8/17/2016.
 */
@Entity
public class Tag extends Model {
    public String tagName;
    public String tagId;

    @ManyToMany
    public List<Contact> contacts;

    public static boolean checkTaginDB(String tagId) {
        if (Tag.find("tagId", tagId).first() != null) {
            return true;
        }
        return false;
    }
}
