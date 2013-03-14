package misc;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class QuestioneerParser {
 
    public void getAllUserNames(String fileName) {
        try {
        	int instructionValue = 0;
        	int happyValue = 0;
        	int computerValue = 0;
        	int fairnessValue = 0;
        	String type;
        	int longCounter = 0 , shortCounter = 0, compCounter = 0;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);
            if (file.exists()) {
                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
 
                // Print root element of the document
                System.out.println("Root element of the document: "+ docEle.getNodeName());
 
                NodeList studentList = docEle.getElementsByTagName("entry");
 
                // Print total student elements in document
                System.out.println("Total students: " + studentList.getLength());
 
                if (studentList != null && studentList.getLength() > 0) {
                    for (int i = 0; i < studentList.getLength(); i++) {
 
                        Node node = studentList.item(i);
 
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) node;
                            NodeList nodeList = e.getElementsByTagName("instructions");
                            instructionValue += Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            NodeList nodeList1 = e.getElementsByTagName("happy");
                            happyValue += Integer.parseInt(nodeList1.item(0).getChildNodes().item(0).getNodeValue());
                            NodeList nodeList2 = e.getElementsByTagName("computer_program");
                            computerValue += Integer.parseInt(nodeList2.item(0).getChildNodes().item(0).getNodeValue());
                            NodeList nodeList3 = e.getElementsByTagName("fairness");
                            fairnessValue += Integer.parseInt(nodeList3.item(0).getChildNodes().item(0).getNodeValue());
                            NodeList nodeList4 = e.getElementsByTagName("type");
                            //System.out.println(nodeList4.item(0).getChildNodes().item(0).getNodeValue());
                           type = nodeList4.item(0).getChildNodes().item(0).getNodeValue();
                            if (type.equalsIgnoreCase("comp"))
                            	compCounter++;
                            if (type.equalsIgnoreCase("short"))
                            	shortCounter++;
                            if (type.equalsIgnoreCase("long"))
                            	longCounter++;
                        }
                    }
                } else {
                    System.exit(1);
                }
                System.out.println("instructions avg value is:" + (instructionValue / (double) studentList.getLength() ));	
                System.out.println("happy avg value is:" + (happyValue / (double) studentList.getLength() ));	
                System.out.println("computer_program avg value is:" + (computerValue / (double) studentList.getLength() ));	
                System.out.println("fairness avg value is:" + (fairnessValue / (double) studentList.getLength() ));	
                System.out.println("Number of players who thought the opponent is COMPROMISE: " + compCounter);
                System.out.println("Number of players who thought the opponent is LONG TERM: " + longCounter);
                System.out.println("Number of players who thought the opponent is SHORT TERM: " + shortCounter);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void main(String[] args) {
 
    	QuestioneerParser parser = new QuestioneerParser();
        parser.getAllUserNames("/Users/inon/Desktop/NGO_FILES/PostQuestion.xml");
    }
}