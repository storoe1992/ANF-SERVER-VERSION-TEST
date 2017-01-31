/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.verticlesworkers;

import anf.serverversiontest.controllers.ProjectsController;
import anf.serverversiontest.util.GeneralUtils;
import anf.serverversiontest.util.JsonMessage;
import anf.serverversiontest.util.PropertiesUtil;
import anf.serverversiontest.util.UpdateXMLUtil;
import anf.serverversiontest.util.UriUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.streams.Pump;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import shaded.org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Sergio
 */
public class ClientToExplotation extends AbstractVerticle{
    private boolean sending = false;
    private HttpClient client;
    private Iterator iter;
    private String uuid = null;
    private int totalSize;
    private int hasArrived;
    
      @Override
  public void start() throws Exception {
      consumerReleaseVersionToExplotation();
      MessageConsumer<JsonArray>consumer = vertx.eventBus().consumer("verticlesworkers.clienttoexplotation.json_array_files_to_transfer");
      consumer.handler(res->{
          String ip = PropertiesUtil.getInstance().getProperties().getProperty("client-ip-for-server-exploitation");
          int port = Integer.valueOf(PropertiesUtil.getInstance().getProperties().getProperty("client-port-for-server-exploitation"));
          //System.out.println("ClientToExplotation on: " + ip + " " + port);
          HttpClientOptions options= new HttpClientOptions().setDefaultHost(ip).setDefaultPort(port);
                 // .setSsl(true)
                 // .setKeyStoreOptions(new JksOptions().setPath("client.jks").setPassword("anfupdatereleaser")
                  
                 // );
          client = vertx.createHttpClient(options);
          
          JsonArray filesToTransfer = res.body();
          hasArrived = 0;
          totalSize=filesToTransfer.size();
          uuid = UUID.randomUUID().toString();
          filesToTransfer.add(JsonMessage.jsonFinalTransfer(uuid));
          iter = filesToTransfer.iterator();
         
            
          
         MessageConsumer <JsonObject> consumerloop = vertx.eventBus().consumer("this.start.send_file_loop");
         consumerloop.handler(result -> {
             
             if( result.body().getInteger("responseCode")==200 ){
                 
                if(iter.hasNext()){
                   
             JsonObject item = (JsonObject) iter.next();
             String type = item.getString("type");
             switch (type){
                 case "fileVersionInfo": sendFile(item);break;
                 case "configVersioXMLFile": sendXMLFileVersion(item);break;
                 case "finalTransfer" : sendFinalTransfer(item);break;
                 case "projectInfo" : sendProjectInfo(item);break;
                     default: System.out.println("verticleworkers: ClientToExplotation: start(): swithLoop: Action not recognised");break;
         }
            
              
         }else{
                     uuid = null;
                 }
                 
             } 
         
         });
         
         if(iter.hasNext()){ //init del bucle
          
             JsonObject item = (JsonObject) iter.next();
             String type = item.getString("type");
             switch (type){
                 case "fileVersionInfo": sendFile(item);break;
                 case "configVersioXMLFile": sendXMLFileVersion(item);break;
                 case "finalTransfer" : sendFinalTransfer(item);break;
                 default: System.out.println("verticleworkers: ClientToExplotation: start(): swithInicioTransfer");break;
         }
              
         }else{
             uuid = null;
         }
             
                 
          
          
          
          
          
      
      
      });
      
    
    


  }
  
  
  private void consumerReleaseVersionToExplotation(){
      MessageConsumer<JsonObject>consumer=vertx.eventBus().consumer("verticlesworkers.clienttoexplotation.release_version_project");
      consumer.handler(res->{
          
          JsonObject projectVersionToRelease = res.body();
          String version = projectVersionToRelease.getString("versionToRelease");
          String idProject = projectVersionToRelease.getString("projectId");
          String projectName = projectVersionToRelease.getString("projectName");
          //System.out.println("verticleworkers: ClientToExplotation: consumerReleaseVersionToExplotation: client: " + client);  
             HttpClient clientCheckServer = vertx.createHttpClient();
             String ip = PropertiesUtil.getInstance().getProperties().getProperty("client-ip-for-server-exploitation");
          int port = Integer.valueOf(PropertiesUtil.getInstance().getProperties().getProperty("client-port-for-server-exploitation"));
             HttpClientRequest request = clientCheckServer.request(HttpMethod.PUT, port,ip,UriUtil.uriTypeAreYouThere(),response->{
                int statusCode = response.statusCode();
                switch (statusCode){
                    case 200:  JsonArray files = ProjectsController.getInstance().pathFilesToTransfer(version,idProject);
                                    if(files.isEmpty()){
                                    res.reply(JsonMessage.jsonVersionProjectAlreadyReleased(projectName, version));
                  
                                    }else if(GeneralUtils.isOnTransfer()){
                                                //System.out.println("verticleworkers: ClientToExplotation: consumerReleaseVersionToExplotation: Entro al onTransfer");
                                                res.reply(JsonMessage.jsonIsOnTransfer());
                                                }
                                                    else{
                                                         //System.out.println("verticleworkers: ClientToExplotation: consumerReleaseVersionToExplotation: JsonArray files:" + files.encodePrettily());
                                                         vertx.eventBus().send("verticlesworkers.clienttoexplotation.json_array_files_to_transfer",files);
                                                            res.reply(JsonMessage.jsonInitTransfer(idProject));
                                                         }break;
                                                           }
            
             });
            request.exceptionHandler(ex->{res.reply(JsonMessage.jsonServerExploitationDown());});
            request.end();
           
             
          
          
      
      });
  }
  
  private void sendFile(JsonObject item){
   sending = true;   //filename = "C:\\Users\\Sergio\\Desktop\\Fichero_prueba\\Java 8 Lambdas.pdf";
   String pathToFile = item.getString("pathToFile");
   String rootPath = item.getString("rootPath");
   String name = item.getString("name");
   String version = item.getString("version");
   String projectId = item.getString("projectId");
     // System.out.println("verticleworkers: ClientToExplotation: sendFile: projectId: " + projectId);
    HttpClientRequest req = client.request(HttpMethod.PUT, UriUtil.uriTypeFile(uuid,rootPath,name,version), res->{
        int statusCode = res.statusCode();
        switch(statusCode){
            case 200: hasArrived++; 
                      JsonObject json = JsonMessage.jsonStatusTransfer(totalSize, hasArrived, statusCode, projectId, "Transfiriendo ficheros"); //rootPath equal to projectId
                      vertx.executeBlocking(future->{UpdateXMLUtil.statusTransferProject(json);}, null);
                      vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(statusCode));
                      break;
        }
        
        //System.out.println("vercleworkers: ClientToExplotation: sendfILE: requestCode from server: " + res.statusMessage() + " statusCode: " + res.statusCode() );
        //return;
    });
    
    req.exceptionHandler(ex->{System.err.println("verticleworkers: ClientToExploitation: sendFile: req: exception: " +ex.getLocalizedMessage());});
  
    
    FileSystem fs = vertx.fileSystem();
    //System.out.println("verticlesworkers: ClientToExplotation: sendFile: pathToFile: " + pathToFile);
    fs.props(pathToFile, ares -> {
      FileProps props = ares.result();
      //System.out.println("props is " + props);
      long size = props.size();
      req.headers().set("content-length", String.valueOf(size));
      //req.headers().set("fileName",);
      fs.open(pathToFile, new OpenOptions(), ares2 -> {
        AsyncFile file = ares2.result();
        Pump pump = Pump.pump(file, req);
        file.endHandler(v -> {
         req.end();
        });
        pump.start();
      });
    });
  }
  
  private void sendXMLFileVersion(JsonObject item){
          String projectId = item.getString("projectId");
          String projectName = item.getString("projectName");
          String version = item.getString("version");
      HttpClientRequest request = client.request(HttpMethod.PUT, UriUtil.uriTypeXMLFile(uuid,version),response->{
          //System.out.println("vercleworkers: ClientToExplotation: sendXMLFileVersion: requestCode from server: " + response.statusMessage() + " statusCode: " + response.statusCode() );
          
          int statusCode = response.statusCode();
          switch(statusCode){
            case 200: hasArrived++; 
                      JsonObject json = JsonMessage.jsonStatusTransfer(totalSize, hasArrived, statusCode, projectId, "Transfiriendo ficheros"); //rootPath equal to projectId
                      vertx.executeBlocking(future->{UpdateXMLUtil.statusTransferProject(json);}, null);
                      vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(statusCode));
                      break;
        }
          
          //vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(response.statusCode()));
          //return;
      });
      
       request.exceptionHandler(ex->{
           System.err.println("verticleworkers: ClientToExploitation: sendXMLFileVersion: request: exception: " +ex.getLocalizedMessage());
           
          });
     
      Buffer buffer = Buffer.buffer();
      item.writeToBuffer(buffer);
      request.headers().set("content-length", String.valueOf(buffer.length()));
      request.write(buffer);
      request.end();
  }
  
  private void sendFinalTransfer(JsonObject item){
      HttpClientRequest request = client.request(HttpMethod.PUT, UriUtil.uriTypeFinalTransfer(uuid),response->{
          //System.out.println("vercleworkers: ClientToExplotation: sendXMLFileVersion: requestCode from server: " + response.statusMessage() + " statusCode: " + response.statusCode() );
          int statusCode = response.statusCode();
          response.bodyHandler(res->{
              JsonObject json = new JsonObject();
              json.readFromBuffer(0, res);
              FileSystem fs = vertx.fileSystem();
              String projectName = json.getString("projectName");
              String projectId = json.getString("projectId");
              String projectVersion = json.getString("version");
              String pathToFile = PropertiesUtil.getInstance().getRootDatas()+File.separator+projectName+File.separator+"last_version_released.xml";
              fs.exists(pathToFile, result->{
                  if(result.result()){
                      //System.out.println("verticleworkers: ClientToExplitation: sendFinalTransfer: projectName:" + projectName);
                      UpdateXMLUtil.updateVersionReleased(projectName, projectId, projectVersion);
                     
                  } else{      //System.out.println("verticleworkers: ClientToExplitation: sendFinalTransfer: projectName:" + projectName);
                              UpdateXMLUtil.createVersionReleased(projectName, projectId, projectVersion);
                              }
              
              });
              switch(statusCode){
            case 200: hasArrived++; 
                      JsonObject jsonStatusTransfer = JsonMessage.jsonStatusTransfer(totalSize, hasArrived, statusCode, projectId, "Transferencia satisfactoria"); //rootPath equal to projectId
                      vertx.executeBlocking(future->{UpdateXMLUtil.statusTransferProject(jsonStatusTransfer);}, null);
                      vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(statusCode));
                      client.close();System.out.println("Connection closed!!");
                      break;
        }
              
          });
          
          // vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(response.statusCode()));
          //return;
      });
      Buffer buffer = Buffer.buffer();
      item.writeToBuffer(buffer);
      request.headers().set("content-length", String.valueOf(buffer.length()));
      request.write(buffer);
      request.end();
  }
  
  private void sendProjectInfo(JsonObject item){
      
      HttpClientRequest request = client.request(HttpMethod.PUT, UriUtil.uriTypeProjectInfo(uuid),response->{
        String projectId = item.getString("projectId");
       // String version = item.getString("version");
       // String projectName = item.getString("projectName");
          int statusCode = response.statusCode();
          switch(statusCode){
            case 200: //hasArrived++; 
                      JsonObject jsonStatusTransfer = JsonMessage.jsonStatusTransfer(totalSize, hasArrived, statusCode, projectId, "Transfiriendo ficheros"); //rootPath equal to projectId
                      vertx.executeBlocking(future->{UpdateXMLUtil.statusTransferProject(jsonStatusTransfer);}, null);
                      vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(statusCode));
                      break;
        }
          //vertx.eventBus().send("this.start.send_file_loop", JsonMessage.jsonResponseStatus(response.statusCode()));
          
      });
      Buffer buffer = Buffer.buffer();
      item.writeToBuffer(buffer);
      request.headers().set("content-length", String.valueOf(buffer.length()));
      request.write(buffer);
      request.end();
  }
}
