/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.util;

import anf.serverversiontest.controllers.VertxController;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pump;
import java.io.File;
import java.io.IOException;
import java.nio.file.OpenOption;

/**
 *
 * @author Sergio
 */
public class JsonMessage {
   static final private Vertx vertx = VertxController.getInstance().getVertx();
    
    public static JsonObject jsonInfoFile(String version, String path, String name,String rootPath,String projectId) {
        JsonObject infoFile = new JsonObject();
       /* */
       
        infoFile
                .put("type", "fileVersionInfo")
                .put("name",name)
                .put("pathToFile", path)
                .put("version", version)
                .put("projectId",projectId)
                .put("rootPath",rootPath);
               // .put("config.xml", bufferXML);
        //System.out.println("util: JsonObject: jsonInfoFile: rootPath: " + rootPath);
        return infoFile;
    }
    
    public  static JsonObject jsonInfoVersionFile(String pathConfigXMLVersion, String version,String projectName,String projectId){
        JsonObject jsonInfoVersionFile =new JsonObject();
        FileSystem fs = vertx.fileSystem();
        
        pathConfigXMLVersion = pathConfigXMLVersion + "config.xml";
        
       // System.out.println("util: JsonMessage: jsonInfoVersionFile: path: "+ pathConfigXMLVersion);
       // AsyncFile fileXML = fs.openBlocking(pathConfigXMLVersion ,new OpenOptions());
        Buffer bufferXML = fs.readFileBlocking(pathConfigXMLVersion);
       // System.out.println("util: JsonMessage: jsonInfoVersionFime: bufferXML.lenght(): " + bufferXML.length());
        
        jsonInfoVersionFile
                .put("type", "configVersioXMLFile")
                .put("path", pathConfigXMLVersion)
                .put("file", bufferXML.getBytes())
                .put("version",version)
                .put("projectName",projectName)
                .put("projectId", projectId);
        
        return jsonInfoVersionFile;
                
    }
    
    public static JsonObject jsonResponseStatus(int code){
            JsonObject json = new JsonObject();
            json
                    .put("type", "response")
                    .put("responseCode", code);
            return json;
    }
    
    public static JsonObject jsonFinalTransfer(String uuid){
        JsonObject json = new JsonObject();
        json.put("type", "finalTransfer")
                .put("uuid", uuid);
            return json;
    }
    
    public static JsonObject jsonProjectInfo(String projectName,String projectId,String version){
        JsonObject json = new JsonObject();
        json
                .put("type", "projectInfo")
                .put("projectName", projectName)
                .put("projectId", projectId)
                .put("version", version);
        return json;
    }
    
    public static JsonObject jsonInitTransfer(String projectId){
        JsonObject json = new JsonObject();
        json
                .put("type", "initTransfer")
                .put("projectId",projectId);
        return json;
    }
    
    public static JsonObject jsonVersionProjectAlreadyReleased(String projectName, String version){
        JsonObject json = new JsonObject();
        json
                .put("type", "versionProjectAlreadyReleased")
                .put("projectName", projectName)
                .put("version", version);
        return json;
    }
     public static JsonObject jsonStatusTransfer(int totalSize,int hasArrived,int code,String projectId,String status){
         JsonObject json = new JsonObject();
         json
                 .put("type", "statusTransfer")
                 .put("totalSize", totalSize)
                 .put("hasArrived", hasArrived)
                 .put("code",code)
                 .put("projectId", projectId)
                 //.put("projectName",projectName)
                 //.put("version",version)
                 .put("status",status);
                 return json;
     }
     
     public static JsonObject jsonIsOnTransferProject(String projectName,String projectId){
         JsonObject json = new JsonObject();
         json
                 .put("type", "onTransfer")
                 .put("projectName", projectName)
                 .put("projectId", projectId);
         return json;
     }
     public static JsonObject jsonIsOnTransfer(){
         JsonObject json = new JsonObject();
         json
                 .put("type", "onTransfer")
                /* .put("projectName", projectName)
                 .put("projectId", projectId)*/;
         return json;
     }
     
     public static JsonObject jsonServerExploitationDown(){
         JsonObject json = new JsonObject();
         json
                 .put("type","serverExploitationDown");
         return json;
                 
     }
    
}
