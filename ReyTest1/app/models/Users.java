package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by PB5N0034 on 8/17/2016.
 */
@Entity
public class Users extends Model {
    public String firstName;
    public String lastName;
    public String title;
    public String companyName;
}
