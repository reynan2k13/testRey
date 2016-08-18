package controllers;

import models.Users;
import org.nobel.highriseapi.HighriseClient;
import org.nobel.highriseapi.InvalidUserCredentialsException;
import org.nobel.highriseapi.entities.Person;
import org.nobel.highriseapi.entities.User;
import org.nobel.highriseapi.resources.PersonResource;
import org.nobel.highriseapi.resources.UserResource;
import play.mvc.Controller;

import java.util.List;

public class Application extends Controller {

    public static void index()  {
       //WS.HttpResponse res = WS.url("https://kinpoelectronicsinc.highrisehq.com/login").get();

        render();
    }

    public static void getConnection(String username, String password, String yourURL, Users contacts) throws InvalidUserCredentialsException {
        HighriseClient highriseClient = HighriseClient.create(yourURL);
        highriseClient.auth(username, password);

        UserResource userResource = highriseClient.getResource(UserResource.class);
        List<User> users = userResource.getAll();

        System.out.println(users);

        PersonResource contactList = highriseClient.getResource(PersonResource.class);
        List<Person> contact = contactList.getAll();

        System.out.println(contact);

        for(Person item: contact){
            contacts.firstname = item.getFirstName();
            contacts.lastName =item.getLastName();
            contacts.title =item.getTitle();
            contacts.companyName = item.getCompanyName();
            contacts.save();
        }

        render(users, contact);
    }

}