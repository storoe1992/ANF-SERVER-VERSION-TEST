/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.controllers;

import anf.serverversiontest.logic.Developer;
import anf.serverversiontest.logic.Project;
import anf.serverversiontest.util.GeneralUtils;
import anf.serverversiontest.util.JsonMessage;
import anf.serverversiontest.util.PropertiesUtil;
import anf.serverversiontest.util.UpdateXMLUtil;
import static io.netty.handler.ssl.OpenSsl.version;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeError.printStackTrace;
import static jdk.nashorn.internal.runtime.Version.version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Sergio
 */
 public class ProjectsController {

        private LinkedList<Project> projects = new LinkedList<>();
        private VertxController controller;
        private Vertx vertx;
        private static ProjectsController projectController;

        /**
         * Desarrolladores en general.
         */
        public LinkedList<Developer> developers = new LinkedList<>();

        private ProjectsController() {
            
                controller = VertxController.getInstance();
                vertx=controller.getVertx();
                developers = loadDevelopers();
                projects = loadProjects();
            
        } 
        
        public static ProjectsController getInstance(){
            if(projectController==null)
                projectController = new ProjectsController();
            return projectController;
        }
        
      

        private LinkedList<Project> loadProjects(){

            LinkedList<Project> theprojects = new LinkedList<>();
            try{
            String rootData = PropertiesUtil.getInstance().getRootDatas();
            File xml = new File(rootData + "projects.xml");
            Document dom = UpdateXMLUtil.loadXMLFromFile(xml);
            Element doc = dom.getDocumentElement();
            
            

            /**
             * Proyectos
             */
            NodeList proj = doc.getElementsByTagName("project");
            int i = 0;
            while (i < proj.getLength()) {
                Node node = proj.item(i++);

                if (node.hasAttributes()) {
                    Project pr = new Project();
                    pr.name = node.getAttributes().getNamedItem("app-name").getNodeValue();
                    pr.setVersions(getProjectsVersion(pr.name));
                    pr.id = node.getAttributes().getNamedItem("app-id").getNodeValue();
                    pr.developers = loadDevelopersInProject(pr.name);
                    pr.isNew = false;
                    theprojects.add(pr);
                }
            }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return theprojects;
        }

        private LinkedList<Developer> loadDevelopersInProject(String projectName) throws Exception {

            LinkedList<Developer> developersIn = new LinkedList<>();

            String rootData = PropertiesUtil.getInstance().getRootDatas();
            String pathApp = rootData + System.getProperty("file.separator") + projectName;
            File xml = new File(pathApp + System.getProperty("file.separator") + "developers.xml");
            Document dom = UpdateXMLUtil.loadXMLFromFile(xml);
            Element doc = dom.getDocumentElement();

            /**
             * Desarrolladores
             */
            NodeList devs = doc.getElementsByTagName("d");
            int i = 0;
            boolean foundDeveloper = false;
            while (i < devs.getLength() && !foundDeveloper) {
                Node node = devs.item(i++);

                if (node.hasAttributes()) {
                    String id = node.getAttributes().getNamedItem("developer-id").getNodeValue();
                    Developer dev = findDeveloperByID(id);
                    developersIn.add(dev);
                }

            }
            return developersIn;

        }

        private LinkedList<Developer> loadDevelopers() {

            LinkedList<Developer> developersIn = new LinkedList<>();
            try{
            String rootData = PropertiesUtil.getInstance().getRootDatas();
            File xml = new File(rootData + "developers.xml");
            Document dom = UpdateXMLUtil.loadXMLFromFile(xml);
            Element doc = dom.getDocumentElement();

            /**
             * Desarrolladores
             */
            NodeList devs = doc.getElementsByTagName("d");
            int i = 0;
            boolean foundDeveloper = false;
            while (i < devs.getLength() && !foundDeveloper) {
                Node node = devs.item(i++);

                if (node.hasAttributes()) {
                    Developer dev = new Developer();
                    dev.id = node.getAttributes().getNamedItem("developer-id").getNodeValue();
                    dev.email = node.getAttributes().getNamedItem("developer-email").getNodeValue();
                    dev.name = node.getAttributes().getNamedItem("developer-name").getNodeValue();
                    developersIn.add(dev);
                }

            }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return developersIn;

        }

        /**
         * Crea un proyecto dado el nombre y devuelve el id generado para este.
         *
         * @param projectName
         * @return el id del nuevo proyecto.
         */
        public String createProject(String projectName) {
            Project p = new Project();
            p.id = UUID.randomUUID().toString();
            p.name = projectName;
            this.projects.add(p);
            return p.id;
        }

        /**
         * Determina si existe o no el proyecto
         *
         * @param projectName
         * @return verdadero o falso
         */
        public boolean existProject(String projectName) {
            for (Project p : this.projects) {
                if (p.name.equalsIgnoreCase(projectName)) {
                    return true;
                }
            }
            return false;
        }

        public Project findProjectByName(String projectName) {
            for (Project p : this.projects) {
                if (p.name.equalsIgnoreCase(projectName)) {
                    return p;
                }
            }
            return null;
        }

     

        public Developer findDeveloperByID(String id) {
            for (Developer p : this.developers) {
                if (p.id.equalsIgnoreCase(id)) {
                    return p;
                }
            }
            return null;
        }

        public Developer findDeveloperByName(String name) {
            for (Developer p : this.developers) {
                if (p.name.equalsIgnoreCase(name)) {
                    return p;
                }
            }
            return null;
        }

       

        

       

    

       
        
        

    public LinkedList<Project> getProjects() {
        return projects;
    }
    
    private  LinkedList<String> getProjectsVersion(String project) {
        LinkedList<String> projectsVersion = new LinkedList<>();
        //final Semaphore semaphore = new Semaphore(1);
        //semaphore.acquire();
        //Vertx vertx = Vertx.vertx();
        
        FileSystem fs = vertx.fileSystem();
        
        String urlVersions = PropertiesUtil.getInstance().getRootDatas()+ File.separator + project + File.separator + "versions";
        /*fs.readDir(urlVersions, res ->{
            if(res.succeeded()){
                System.out.println(res.result());
                res.result().stream().map((dir) -> {
                    int lastPos = dir.lastIndexOf(File.separator);
                    String version = dir.substring(lastPos + 1,dir.length());
                    return version;
                }).map((version) -> {
                    projectsVersion.add(version);
                    return version;
                }).forEach((_item) -> {
                    System.out.println("ProjectController: item:" +_item+" projectVersion.size(): "+projectsVersion.size());
                });
                //semaphore.release();
            }//else semaphore.release();
        
        });
        System.out.println("Size de la lista antes del return "+ projectsVersion.size());*/
        List<String>temp = fs.readDirBlocking(urlVersions);
        for(String dir : temp){
            int lastPos = dir.lastIndexOf(File.separator);
            String version = dir.substring(lastPos + 1,dir.length());
            projectsVersion.add(version);
        }
       // System.out.println("controllers: ProjectsController: getProjectsVersions: " + projectsVersion);
       return projectsVersion;
    }
    
    public String getProjectNameById(String id_project){
        String rootData = PropertiesUtil.getInstance().getRootDatas();
        File xml = new File(rootData + "projects.xml");
        String name = "";
        String id = "";
       try{
        Document dom = UpdateXMLUtil.loadXMLFromFile(xml);
        Element doc = dom.getDocumentElement();
        
        
        NodeList proj = doc.getElementsByTagName("project");
            int i = 0;
           
            boolean break_loop = false;
            while (i < proj.getLength() && !break_loop) {
                Node node = proj.item(i++);

                if (node.hasAttributes()) {
                    id = node.getAttributes().getNamedItem("app-id").getNodeValue();
                    if(id.equalsIgnoreCase(id_project)){
                    name = node.getAttributes().getNamedItem("app-name").getNodeValue();
                    break_loop = true;
                    }
                }
            }
       }catch(Exception ex){
           ex.printStackTrace();
       }
            
            return name;
    }
    
    public  LinkedList<String> getVersionsFromTo(String project_name,String v1 , String v2){
        boolean haveVersionReleased = false;
        if(!v1.isEmpty()){
            haveVersionReleased = true;
        }else v1="v0.0";
        LinkedList<String>versions=new LinkedList<>();
        LinkedList<String>currents_versions = new LinkedList<>();
        currents_versions.addAll(getProjectsVersion(project_name));
        ListIterator<String>iter = currents_versions.listIterator();
        while (iter.hasNext()) {
            String tempVersion = iter.next();
            int compareV1 = GeneralUtils.compareVersions(tempVersion, v1);
            int compareV2 = GeneralUtils.compareVersions(tempVersion, v2);
            if(!haveVersionReleased){
            if(compareV1 >= 0  && compareV2 <= 0)
             versions.add(tempVersion);
            }else{
                if(compareV1>0 && compareV2<=0)
                    versions.add(tempVersion);
            }
            }
            
               // System.out.println("controller: ProjectsController: getVersionFromTo: versions: " + versions);
                return versions;
                
        }
    
    public JsonArray pathFilesToTransfer(String version,String projectId){
        String projectName = getProjectNameById(projectId);
        String versionReleased = UpdateXMLUtil.getVersionReleased(projectName, projectId);
        LinkedList<String>versions=getVersionsFromTo(projectName,versionReleased,version); // at the moment always by from 0.0 from 
        JsonArray result= pathFilesToTransfer(versions, projectName,projectId);
        if(!result.isEmpty()){
            result.add(JsonMessage.jsonProjectInfo(projectName,projectId, version));
        }
        return result;
    }
    
    private JsonArray pathFilesToTransfer(LinkedList<String>versions,String projectName,String projectId){
        JsonArray pathFiles = new JsonArray();
        
        Iterator iter;
        for(String version : versions){
            JsonArray pathFilesByVersion = pathFilesByVersion(version, projectName,projectId);
            //System.out.println("controller: ProjectsController: pathFlesToTransfer: versions.size(): " + versions.size());
            iter = pathFilesByVersion.iterator();
            while(iter.hasNext()){
            pathFiles.add(iter.next());
            }
        }
        
        return pathFiles;
    }
    
    public JsonArray pathFilesByVersion(String version, String projectName,String projectId){
        JsonArray pathFiles = new JsonArray();
        String pathPorjectVersion = "datas"+ File.separator +projectName +File.separator + "versions" +File.separator + version + File.separator;
        
        Document dom;
            try {
                File xml = new File(pathPorjectVersion + "config.xml");
                dom = UpdateXMLUtil.loadXMLFromFile(xml);
                Element doc = dom.getDocumentElement();
                NodeList replace = doc.getElementsByTagName("replace");
                NodeList add = doc.getElementsByTagName("add");
                addElemtsByTag(add, pathFiles,projectName,version,projectId);
                addElemtsByTag(replace, pathFiles,projectName,version,projectId);
                pathFiles.add(JsonMessage.jsonInfoVersionFile(pathPorjectVersion,version,projectName,projectId));                
                //System.out.println("controllers: ProjectControllers: pathFilesByVersio: pathFiles.lengtht: " + pathFiles.size());
                //System.out.println("controllers: ProjectControllers: pathFilesByVersio: add.lengtht: " + add.getLength());
                //System.out.println("controllers: ProjectControllers: pathFilesByVersio: replace.lengtht: " + replace.getLength());
                //System.out.println("controllers: ProjectControllers: pathFilesByVersio: add" + add.item(0).getChildNodes().getLength());
            } catch (Exception ex) {
                Logger.getLogger(ProjectsController.class.getName()).log(Level.SEVERE, null, ex);
                //System.out.println("controllers: ProjectsControllers: pathFilesByVersion: log: config.xml from version & projectName not Found");
            }
            
        return pathFiles;
    }
    
    public  void addElemtsByTag(NodeList list,JsonArray jsonArray,String projectName,String version,String projectId){
        int i = 0;
            String path ="";
            String name ="";
            String productVersion = "";
            NodeList childNodes = list.item(0).getChildNodes();
            while (i < childNodes.getLength()) {
                Node node = childNodes.item(i++);
                //System.out.println(node);
                if (node.hasAttributes()) {
                    String tempPath = node.getAttributes().getNamedItem("path").getNodeValue();
                    String pathToSource = PropertiesUtil.getInstance().getRootDatas()+projectName+File.separator+"versions"+File.separator+version+File.separator;
                    path =pathToSource+"files"+ File.separator + tempPath;
                    path = path.replace("./", "").replace("/", File.separator);
                    
                    name = node.getAttributes().getNamedItem("name").getNodeValue();
                    productVersion = node.getAttributes().getNamedItem("product-version").getNodeValue();
                    String rootPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
                    JsonObject fileInfo = JsonMessage.jsonInfoFile("v"+productVersion,path,name,rootPath,projectId);
                    jsonArray.add(fileInfo);
                }
            }
    }
        
        
    

   /* public static void main(String[] arg) throws Exception{
        String project_name = getProjectById("fe514d93-bde2-4539-a231-4ea416de4a3d");
        System.out.println(project_name);
       // LinkedList<String>a=getVersionsFromTo(project_name, "v0.0", "v0.1");
       // System.out.println("Esta es la lista" + a);
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();
        String urlVersions = PropertiesUtil.getInstance().getRootDatas()+ File.separator + "autoupdate" + File.separator + "versions";
        List<String> list = fs.readDirBlocking(urlVersions);
        System.out.println(list);
 }*/
            
    
    
        
        public static void main(String [] args){
           // pathFilesByVersion("v3.4.1.45", "ARManager");
        }
        

    }
