/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.util;

/**
 *
 * @author Sergio
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import anf.serverversiontest.controllers.VertxController;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Random;
import java.util.RandomAccess;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jordy
 */
public class UpdateXMLUtil {
 
    private final static Object lock = new Object();
    
    public static Document loadXMLFromFile(File xmlFile) throws Exception {
       
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }
    
    public static Document loadXMLFromFile(InputStream xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }
    
    public static void createVersionReleased(String projectName,String id,String version) {
       try{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("projects");
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
                String pathToFileXML = PropertiesUtil.getInstance().getRootDatas()+ File.separator+projectName+File.separator+"last_version_released.xml";
		StreamResult result = new StreamResult(new File(pathToFileXML));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
       }catch(Throwable ex){
           ex.printStackTrace();
       }
    }
    
    public static boolean updateVersionReleased(String projectName, String project_id, String version){
        boolean updateSuccefull = false;
        String pathToFileXML = PropertiesUtil.getInstance().getRootDatas()+ File.separator+projectName+File.separator+"last_version_released.xml";
        File file = new File(pathToFileXML);
        if(file.exists()){
            try{
            Document doc = loadXMLFromFile(file);
            int pos =existVersionReleased(doc, project_id);
            if(pos!=-1){
                doc
                        .getDocumentElement()
                        .getElementsByTagName("project")
                        .item(pos)
                        .getAttributes()
                        .getNamedItem("version_released")
                        .setNodeValue(version);
                saveProjectReleasedXML(doc, projectName);
                updateSuccefull=true;
            }
            
            
            
            
            }catch(Throwable ex){
                ex.printStackTrace();
            }
        }return updateSuccefull;
    }
    
    public static int existVersionReleased(Document doc,String id){
    
        int result = -1;
        String idProject="";
        Element element = doc.getDocumentElement();
        NodeList nodes = element.getElementsByTagName("project");
        int i = 0;
        boolean breaker = false;
        while(i<nodes.getLength()&& ! breaker){
            Node tempNode = nodes.item(i);
            if(tempNode.hasAttributes()){
                idProject = tempNode.getAttributes().getNamedItem("id").getNodeValue();
                if(idProject.equalsIgnoreCase(id)){
                    result = i;
                    breaker = true;
                }
            }i++;
        }
        
            return result;
    }
    
    public static void saveProjectReleasedXML(Document doc,String projectName){
        try{        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
                String pathToFileXML = PropertiesUtil.getInstance().getRootDatas()+ File.separator+projectName+File.separator+"last_version_released.xml";
		StreamResult result = new StreamResult(new File(pathToFileXML));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
        }catch(Throwable ex){
            ex.printStackTrace();
        }
    }
    
    public static String getVersionReleased(String projectName,String projectId){ // return empty String when :last_version_released.xml file not found
        String released_version="";                                                 // this mean that this project have no version released
        String pathToFileXML = PropertiesUtil.getInstance().getRootDatas()+ File.separator+projectName+File.separator+"last_version_released.xml";
        File file = new File(pathToFileXML);
        if(file.exists()){
            try{
            Document doc = loadXMLFromFile(file);
            int posVersion = existVersionReleased(doc,projectId);
            if(posVersion > -1){
               released_version = doc
                        .getDocumentElement()
                        .getElementsByTagName("project")
                        .item(posVersion)
                        .getAttributes()
                        .getNamedItem("version_released")
                        .getNodeValue();
            }
            
            }catch(Throwable ex){
                ex.printStackTrace();
            }
        }
        
        
        return released_version;
    }
    
    public static void statusTransferProject(JsonObject json){
        synchronized(lock){
        String projectId = json.getString("projectId");
        //String version = json.getString("version");
        String root = PropertiesUtil.getInstance().getRootDatas();
        String tempStatusTransfer = root + File.separator + "tempStatus";
        String fileName = projectId+".xml";
        String pathToFile = tempStatusTransfer + File.separator +fileName;
        //Vertx vertx = Vertx.vertx();
        FileSystem fs = /*vertx.fileSystem();*/VertxController.getInstance().getVertx().fileSystem();
        if(!fs.existsBlocking(tempStatusTransfer)){
        fs.mkdirsBlocking(tempStatusTransfer);
        }
        File file = new File(pathToFile);
        if(!file.exists())
            createStatusTransferProject(json,pathToFile);
        else
            updateStatusTransferProject(json,pathToFile);
        
        }
        
        
        
    }
    
    private static void createStatusTransferProject(JsonObject json, String pathToFile){
        try{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        Document doc = docBuilder.newDocument();
        Element generalRoot = doc.createElement("StatusTransfer");
        doc.appendChild(generalRoot);
        
        Element rootElement = doc.createElement("Status");
        generalRoot.appendChild(rootElement);
        
       
        
        rootElement.setAttribute("totalSize", String.valueOf(json.getInteger("totalSize")));
        rootElement.setAttribute("hasArrived", String.valueOf(json.getInteger("hasArrived")));
        rootElement.setAttribute("code", String.valueOf(json.getInteger("code")));
        rootElement.setAttribute("status",json.getString("status"));
        
        
        
       TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc); 
		StreamResult result = new StreamResult(new File(pathToFile));
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(source, result);
        
        
        
        }catch(Throwable ex){
            ex.printStackTrace();
        }
        
                
    }

    private static void updateStatusTransferProject(JsonObject json,String pathToFile) {
        File file = new File(pathToFile);
        if(file.exists()){
            try{
            Document doc = loadXMLFromFile(file);
            Node node =    doc
                        .getDocumentElement()
                        .getElementsByTagName("Status")
                        .item(0);
            NamedNodeMap attributes = node.getAttributes();
                    attributes
                            .getNamedItem("hasArrived")
                            .setNodeValue(String.valueOf(json.getInteger("hasArrived")));
                    attributes
                            .getNamedItem("status")
                            .setNodeValue(json.getString("status"));
                    attributes
                            .getNamedItem("code")
                            .setNodeValue(String.valueOf(json.getInteger("code")));
                
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc); 
		StreamResult result = new StreamResult(new File(pathToFile));
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(source, result); 
               
            
    }catch(Throwable ex){
        ex.printStackTrace();
    }
        }
    }
    
    
    
    public static JsonObject getStatusTransfer(String projectId){
        
        synchronized(lock){
        JsonObject json = null;
        String status = "";
        int hasArrived =-1;
        int code=-1;
        int totalSize = -1;
        String root = PropertiesUtil.getInstance().getRootDatas();
        String rootTemp = root+ File.separator +"tempStatus";
        String pathToFile = rootTemp + File.separator + projectId + ".xml";
        File file = new File(pathToFile);
       
        if(file.exists()){
            try{
                 
                
                 Document doc = loadXMLFromFile(file);
                 Node node =    doc
                        .getDocumentElement()
                        .getElementsByTagName("Status")
                        .item(0);
            NamedNodeMap attributes = node.getAttributes();
                status = attributes
                        .getNamedItem("status")
                        .getNodeValue();
                hasArrived = Integer.valueOf(attributes
                        .getNamedItem("hasArrived")
                        .getNodeValue());
                code =    Integer.valueOf(attributes
                            .getNamedItem("code")
                            .getNodeValue());
                totalSize= Integer.valueOf(attributes
                            .getNamedItem("totalSize")
                            .getNodeValue());
                
                
               
                    
        json = JsonMessage.jsonStatusTransfer(totalSize,hasArrived,code,projectId,status);
        
                }catch(Throwable ex){
                    ex.printStackTrace();
                }
                }
        return json;
        }
    }
       
    public static void main (String [] args){
        
        JsonObject json = getStatusTransfer("f083f37c-5a9a-46fb-980f-22abd16446ba");
        System.out.println(json.encodePrettily());
        
    }
    
}

