package controllers;

import models.Users;
import org.nobel.highriseapi.InvalidUserCredentialsException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.libs.WS;
import play.mvc.Controller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



public class Application extends Controller {

    public static void index()  {

        render();
    }

    public static void getConnection(String token, String password, String yourURL, Users contacts, String tagName) throws InvalidUserCredentialsException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

//
//        for(Person item: contact){
//            contacts.firstname = item.getFirstName();
//            contacts.lastName =item.getLastName();
//            contacts.title =item.getTitle();
//            contacts.companyName = item.getCompanyName();
//            contacts.save();
//        }
        WS.HttpResponse res = WS.url(yourURL+"people.xml?tag_name="+tagName+"").authenticate(token, password, WS.Scheme.BASIC).get();
        int status = res.getStatus();
        String type = res.getContentType();
        String content = res.getString();

        File inputFile = new File("tmp/input.text");
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        XPath xPath =  XPathFactory.newInstance().newXPath();

        String expression = "/people/person";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                System.out.println("First Name : "+ eElement.getElementsByTagName("first-name").item(0).getTextContent());
                contacts.firstName = eElement.getElementsByTagName("first-name").item(0).getTextContent();

                System.out.println("Last Name : "+ eElement.getElementsByTagName("last-name").item(0).getTextContent());
                contacts.lastName = eElement.getElementsByTagName("last-name").item(0).getTextContent();

                System.out.println("title : "+ eElement.getElementsByTagName("title").item(0).getTextContent());
                contacts.title = eElement.getElementsByTagName("title").item(0).getTextContent();

                System.out.println("Company : "+ eElement.getElementsByTagName("company-name").item(0).getTextContent());
                contacts.companyName = eElement.getElementsByTagName("company-name").item(0).getTextContent();
                contacts.save();
            }
        }
        render(content, contacts);
    }

}