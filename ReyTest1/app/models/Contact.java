package models;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * Created by PB5N0034 on 8/17/2016.
 */
@Entity
public class Contact extends Model {
    public String userID;
    public String firstName;
    public String lastName;
    public String title;
    public String companyName;

    @ManyToMany
    public List<Tag> tags;

    public static boolean checkContactinDB(String userID) {
        if (Contact.find("userID", userID).first() != null) {
            return true;
        }
        return false;
    }

}
