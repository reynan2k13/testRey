package controllers;

import models.Contact;
import models.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.libs.WS;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class Application extends WebService {

    public static void index() {
        render();
    }
    public static void home(String endpoint, String token, String password) {
        response.setCookie("endpoint", endpoint);
        response.setCookie("token", token);
        response.setCookie("password", password);
        render();
    }

    public static void getAllContact(String endpoint, String token, String password) throws XPathExpressionException {

        if(request.cookies.containsKey("endpoint")){
            if(request.cookies.get("endpoint").value.isEmpty()==false)
                endpoint = request.cookies.get("endpoint").value;
        }
        if(request.cookies.containsKey("token")){
            if(request.cookies.get("token").value.isEmpty()==false)
                token = request.cookies.get("token").value;
        }
        if(request.cookies.containsKey("password")){
            if(request.cookies.get("password").value.isEmpty()==false)
                password = request.cookies.get("password").value;
        }

        WS.HttpResponse res = WS.url( endpoint + "people.xml").authenticate(token, password, WS.Scheme.BASIC).get();
        Document doc  = res.getXml();
        String sss = res.getString();
        System.out.println(sss);
        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression = "/people/person";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        List<Contact> contacts = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Contact contact = new Contact();
                Element eElement = (Element) nNode;
                System.out.println(eElement.getElementsByTagName("id").item(0).getTextContent());
                System.out.println(eElement.getElementsByTagName("first-name").item(0).getTextContent());
                contact.userID = eElement.getElementsByTagName("id").item(0).getTextContent();
                contact.firstName =eElement.getElementsByTagName("first-name").item(0).getTextContent();
                contact.lastName = eElement.getElementsByTagName("last-name").item(0).getTextContent();
                contact.title = eElement.getElementsByTagName("title").item(0).getTextContent();
                contact.companyName = eElement.getElementsByTagName("company-name").item(0).getTextContent();
                contacts.add(contact);

            }
        }
        render(contacts);
    }

    public static void searchContact(String endpoint, String token, String password, String tagID, String tagname, String tagName) throws XPathExpressionException {
        if(request.cookies.containsKey("endpoint")){
            if(request.cookies.get("endpoint").value.isEmpty()==false)
                endpoint = request.cookies.get("endpoint").value;
        }
        if(request.cookies.containsKey("token")){
            if(request.cookies.get("token").value.isEmpty()==false)
                token = request.cookies.get("token").value;
        }
        if(request.cookies.containsKey("password")){
            if(request.cookies.get("password").value.isEmpty()==false)
                password = request.cookies.get("password").value;
        }
        String tag_id = null;
        WS.HttpResponse res = WS.url( endpoint + "people.xml").authenticate(token, password, WS.Scheme.BASIC).get();
        Document doc  = res.getXml();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/people/person/tags/tag";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(tagname)) {
                    tag_id = eElement.getElementsByTagName("id").item(0).getTextContent();
                    tagname = eElement.getElementsByTagName("name").item(0).getTextContent();
                    tagName = eElement.getElementsByTagName("name").item(0).getTextContent();
                }
            }

        }

        tagname = tag_id;
        WS.HttpResponse resTag = WS.url(endpoint + "people.xml?tag_id=" + tag_id + "").authenticate(token, password, WS.Scheme.BASIC).get();
        Document docs  = resTag.getXml();

        String expressionTag = "/people/person";
        NodeList nodeListTag = (NodeList) xPath.compile(expressionTag).evaluate(docs, XPathConstants.NODESET);
        List<Contact> contacts = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < nodeListTag.getLength(); i++) {
            Node nNode = nodeListTag.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Contact contact = new Contact();
                contact.userID = eElement.getElementsByTagName("id").item(0).getTextContent();
                contact.firstName =eElement.getElementsByTagName("first-name").item(0).getTextContent();
                contact.lastName = eElement.getElementsByTagName("last-name").item(0).getTextContent();
                contact.title = eElement.getElementsByTagName("title").item(0).getTextContent();
                contact.companyName = eElement.getElementsByTagName("company-name").item(0).getTextContent();
                contacts.add(contact);

                boolean checkContactinDB = Contact.checkContactinDB(contact.userID);
                if (checkContactinDB == false) {
                    contact.save();
                }

                Tag tag = new Tag();
                tag.tagId = tag_id;
                tag.tagName = tagName;
                tags.add(tag);

                boolean exists = Tag.checkTaginDB(tagname);
                if (exists == false ) {
                    tag.save();
                }

            }
        }

        render(contacts, tags);
    }

    public static void getContactDB() {
        List<Contact> contacts = Contact.findAll();
        render(contacts);
    }

    public static void filterContact(String filter) {
        List<Contact> contacts = Contact.find("title", filter).fetch();

        render(contacts);
    }



}