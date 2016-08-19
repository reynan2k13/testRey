package controllers;

import models.Contact;
import org.nobel.highriseapi.InvalidUserCredentialsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.libs.WS;
import play.mvc.Controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Application extends Controller {


    public static void index() {
        render();
    }

    public static void getConnection(String token, String password, String yourURL, String tagName) throws InvalidUserCredentialsException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        WS.HttpResponse res = WS.url(yourURL + "people.xml").authenticate(token, password, WS.Scheme.BASIC).get();
        String contentAll = res.getString();

        FileWriter fileWritter = new FileWriter("myinput.txt");
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(contentAll);
        bufferWritter.close();

        String contentAllTags = null;

        File inputFile = new File("myinput.txt");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression = "/people/person/tags/tag";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                System.out.println("id : " + eElement.getElementsByTagName("id").item(0).getTextContent());
                System.out.println("tagsname : " + eElement.getElementsByTagName("name").item(0).getTextContent());


                if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(tagName)) {
                    String tag_id = eElement.getElementsByTagName("id").item(0).getTextContent();
                    String tag_name = eElement.getElementsByTagName("name").item(0).getTextContent();

                    WS.HttpResponse resTags = WS.url(yourURL + "people.xml?tag_id=" + tag_id + "").authenticate(token, password, WS.Scheme.BASIC).get();
                    contentAllTags = resTags.getString();

                    FileWriter fileWrittertag = new FileWriter("myinputTag.txt");
                    BufferedWriter bufferWritterTags = new BufferedWriter(fileWrittertag);
                    bufferWritterTags.write(contentAllTags);
                    bufferWritterTags.close();

                    File inputFileTag = new File("myinputTag.txt");
                    DocumentBuilderFactory dbFactoryTag = DocumentBuilderFactory.newInstance();
                    dBuilder = dbFactoryTag.newDocumentBuilder();
                    Document docTag = dBuilder.parse(inputFileTag);
                    doc.getDocumentElement().normalize();

                    if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(tagName)) {
                        String tagsExpression = "/people/person";
                        NodeList nodeTags = (NodeList) xPath.compile(tagsExpression).evaluate(docTag, XPathConstants.NODESET);
                        for (int b = 0; b < nodeTags.getLength(); b++) {
                            Node nTags = nodeTags.item(b);
                            System.out.println("\nCurrent Element :" + nTags.getNodeName());
                            if (nTags.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementTags = (Element) nTags;
                                System.out.println("First Name : " + eElementTags.getElementsByTagName("first-name").item(0).getTextContent());
                                System.out.println("Last Name : " + eElementTags.getElementsByTagName("last-name").item(0).getTextContent());
                                System.out.println("Title : " + eElementTags.getElementsByTagName("title").item(0).getTextContent());
                                System.out.println("Company Name : " + eElementTags.getElementsByTagName("company-name").item(0).getTextContent());
                                System.out.println("tagsname : " + eElement.getElementsByTagName("name").item(0).getTextContent());


                                Contact contact = new Contact();
                                contact.userId = Long.parseLong(eElement.getElementsByTagName("id").item(0).getTextContent());
                                contact.firstName =eElementTags.getElementsByTagName("first-name").item(0).getTextContent();
                                contact.lastName = eElementTags.getElementsByTagName("last-name").item(0).getTextContent();
                                contact.title = eElementTags.getElementsByTagName("title").item(0).getTextContent();
                                contact.companyName = eElementTags.getElementsByTagName("company-name").item(0).getTextContent();
                                contact.tags = eElement.getElementsByTagName("name").item(0).getTextContent();
                                contacts.add(contact);
                                contact.save();

                            }
                        }
                        render(contacts, password, token, yourURL);
                    }
                }
            }
        }
    }

    public static void getData(String yourURL, String token, String password){
        List<Contact> getAll = Contact.findAll();

        render(getAll ,password, token, yourURL);
    }

    public static void addFilter(String yourURL, String token, String password, String tagName) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        WS.HttpResponse res = WS.url(yourURL + "people.xml").authenticate(token, password, WS.Scheme.BASIC).get();
        String contentAll = res.getString();

        FileWriter fileWritter = new FileWriter("allTag.txt");
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(contentAll);
        bufferWritter.close();

        String contentAllTags = null;

        File inputFile = new File("allTag.txt");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();

        String expression = "/people/person/tags/tag";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                System.out.println("id : " + eElement.getElementsByTagName("id").item(0).getTextContent());
                System.out.println("tagsname : " + eElement.getElementsByTagName("name").item(0).getTextContent());


                if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(tagName)) {
                    String tag_id = eElement.getElementsByTagName("id").item(0).getTextContent();
                    String tag_name = eElement.getElementsByTagName("name").item(0).getTextContent();

                    WS.HttpResponse resTags = WS.url(yourURL + "people.xml?tag_id=" + tag_id + "").authenticate(token, password, WS.Scheme.BASIC).get();
                    contentAllTags = resTags.getString();

                    FileWriter fileWrittertag = new FileWriter("allTagInput.txt");
                    BufferedWriter bufferWritterTags = new BufferedWriter(fileWrittertag);
                    bufferWritterTags.write(contentAllTags);
                    bufferWritterTags.close();

                    File inputFileTag = new File("allTagInput.txt");
                    DocumentBuilderFactory dbFactoryTag = DocumentBuilderFactory.newInstance();
                    dBuilder = dbFactoryTag.newDocumentBuilder();
                    Document docTag = dBuilder.parse(inputFileTag);
                    doc.getDocumentElement().normalize();

                    if (eElement.getElementsByTagName("name").item(0).getTextContent().equals(tagName)) {
                        String tagsExpression = "/people/person";
                        NodeList nodeTags = (NodeList) xPath.compile(tagsExpression).evaluate(docTag, XPathConstants.NODESET);
                        for (int b = 0; b < nodeTags.getLength(); b++) {
                            Node nTags = nodeTags.item(b);
                            System.out.println("\nCurrent Element :" + nTags.getNodeName());
                            if (nTags.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementTags = (Element) nTags;
                                System.out.println("First Name : " + eElementTags.getElementsByTagName("first-name").item(0).getTextContent());
                                System.out.println("Last Name : " + eElementTags.getElementsByTagName("last-name").item(0).getTextContent());
                                System.out.println("Title : " + eElementTags.getElementsByTagName("title").item(0).getTextContent());
                                System.out.println("Company Name : " + eElementTags.getElementsByTagName("company-name").item(0).getTextContent());
                                System.out.println("tagsname : " + eElement.getElementsByTagName("name").item(0).getTextContent());


                                Contact contact = new Contact();
                                contact.userId = Long.parseLong(eElement.getElementsByTagName("id").item(0).getTextContent());
                                contact.firstName =eElementTags.getElementsByTagName("first-name").item(0).getTextContent();
                                contact.lastName = eElementTags.getElementsByTagName("last-name").item(0).getTextContent();
                                contact.title = eElementTags.getElementsByTagName("title").item(0).getTextContent();
                                contact.companyName = eElementTags.getElementsByTagName("company-name").item(0).getTextContent();
                                contact.tags = eElement.getElementsByTagName("name").item(0).getTextContent();
                                contacts.add(contact);
//                              contact.save();

                            }
                        }
                        render(contacts, password, token, yourURL);
                    }
                }
            }
        }



    }
}