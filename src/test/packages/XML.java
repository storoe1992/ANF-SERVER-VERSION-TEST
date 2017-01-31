/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import anf.serverversiontest.util.PropertiesUtil;
import anf.serverversiontest.util.UpdateXMLUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 *
 * @author Sergio
 */
public class XML {
    
    public static void writeVersionReleased(String projectName,String id,String version) {
       try{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("projects");
                doc.createDocumentFragment();
		doc.appendChild(rootElement);
                
                Element project = doc.createElement("project");
		rootElement.appendChild(project);
                
                Attr name = doc.createAttribute("name");
                name.setValue(projectName);
                Attr id_project = doc.createAttribute("id");
                id_project.setValue(id);
                Attr version_released = doc.createAttribute("version_released");
                version_released.setValue(version);
                
                project.setAttributeNode(name);
                project.setAttributeNode(id_project);
                project.setAttributeNode(version_released);
       
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\Users\\Sergio\\Documents\\1.Documentos\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
       }catch(Throwable ex){
           ex.printStackTrace();
       }
    }
    
    public static void main(String [] args) throws ParserConfigurationException, TransformerException{
       try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("company");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("Staff");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr attr = doc.createAttribute("id");
		attr.setValue("1");
		staff.setAttributeNode(attr);

		// shorten way
		// staff.setAttribute("id", "1");

		// firstname elements
		Element firstname = doc.createElement("firstname");
		firstname.appendChild(doc.createTextNode("yong"));
		staff.appendChild(firstname);

		// lastname elements
		Element lastname = doc.createElement("lastname");
		lastname.appendChild(doc.createTextNode("mook kim"));
		staff.appendChild(lastname);

		// nickname elements
		Element nickname = doc.createElement("nickname");
		nickname.appendChild(doc.createTextNode("mkyong"));
		staff.appendChild(nickname);

		// salary elements
		Element salary = doc.createElement("salary");
		salary.appendChild(doc.createTextNode("100000"));
		staff.appendChild(salary);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\Users\\Sergio\\Documents\\1.Documentos\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);

		System.out.println("File saved!");

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
    }
    
}
