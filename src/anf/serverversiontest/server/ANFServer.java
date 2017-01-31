/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.server;

import anf.serverversiontest.util.GeneralUtils;
import static anf.serverversiontest.util.GeneralUtils.delete;
import static anf.serverversiontest.util.GeneralUtils.deleteChildsOfDir;
import anf.serverversiontest.util.JsonMessage;
import anf.serverversiontest.util.PropertiesUtil;
import anf.serverversiontest.util.SSLManager;
import anf.serverversiontest.util.UpdateXMLUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.streams.StreamBase;
import io.vertx.core.streams.WriteStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class ANFServer extends AbstractVerticle{
        private EventBus bus;
    
    @Override
    public void start(){
        
        String ip = PropertiesUtil.getInstance().getProperties().getProperty("server-ip-for-server-test");
        int port = Integer.valueOf(PropertiesUtil.getInstance().getProperties().getProperty("server-port-for-server-test"));
        
        HttpServerOptions options = new HttpServerOptions().setSsl(true)
               // .setKeyStoreOptions(new JksOptions().setPath("server-keystore.jks").setPassword("wibble"));//.setClientAuth(ClientAuth.REQUIRED)
                .setPfxKeyCertOptions(new PfxOptions().setPath("updates.anf.es.pfx").setPassword("12341234"));
        HttpServer server = vertx.createHttpServer();
        bus=vertx.eventBus();
        
        server.requestHandler(req ->{
          
            req.bodyHandler(buffer ->{
                if(buffer.length()!=0){
                    JsonObject jsonMessage = new JsonObject();
                    jsonMessage.readFromBuffer(0, buffer);
                    //System.out.println("server: ANFServer: start: jsonMessage.getValue(type): "+jsonMessage.getValue("type"));
                    String type = jsonMessage.getString("type");
                    switch (type) {
                            case "get": functionSendProjectsInfo(req, jsonMessage);break;
                            case "send":functionGetSendProjectsInfo(req,jsonMessage) ;break;
                            case "status":functionStatusProjectTransfer(req,jsonMessage);break;    
                            default: req.response().end();
                    }
                    
                }else
                        req.response().end();
                    
                
            });
        });
        
        server.listen(port,ip, res -> {
        if (res.succeeded()) {
        System.out.println("Server online on: " + ip + " " + port);
        } else {
             System.out.println("Failed to bind!");
            }
            });
       }
    
    
    private void functionSendProjectsInfo(HttpServerRequest req,JsonObject jsonMessage){
                if(jsonMessage.getString("what").equalsIgnoreCase("projects")){
                    bus.send("verticlesworkers.verticleproject.get_projects_server", "Hola", reply->{
                if(reply.succeeded()){
                    JsonArray json = (JsonArray) reply.result().body();
                       HttpServerResponse response = req.response();
                       //response.setChunked(true);
                       Buffer bufferJsonProjects = Buffer.buffer();
                       json.writeToBuffer(bufferJsonProjects);
                       long size = bufferJsonProjects.length();
                       //System.out.println("server: ANFServer: functionSendProjectsInfo:");
                       //response.setChunked(true);
                       response.putHeader("content-length", String.valueOf(size));
                       response.write(bufferJsonProjects);
                       response.end();               
                }
            });
                }                 
    }
    
    private void functionGetSendProjectsInfo(HttpServerRequest req,JsonObject jsonMessage){
        //System.out.println("server: ANFServer: functionGetSendProjectsInfo: Entro a la funcion");
        if(jsonMessage.getString("information").equalsIgnoreCase("project")){
        bus.send("controllers.vertxcontroller.release_version_to_explotation", jsonMessage, replay->{
            if(replay.succeeded())
            functionActionRequest(req, (JsonObject) replay.result().body());
            
        });
       // System.out.println("server: ANFServer: functionGetSendProjectsInfo: jsonMessage: " + jsonMessage.encodePrettily());
         //req.response().end();
        }
    }
    
    private void functionActionRequest(HttpServerRequest req,JsonObject json){
        
        String type = json.getString("type");
       
        switch (type){
            case "versionProjectAlreadyReleased" : fuctionVersionProjectAlreadyReleased(req, json);break;
            case "initTransfer" :functionStatusProjectTransfer(req, json);break;
            case "onTransfer" : functionOnTransfer(req,json);break;
            case "serverExploitationDown": functionServerExploitationDown(req,json);break;
            //default: System.out.println("server: ANFServer: functionActionRequest: default"); req.response().end();break;
        }
    }
    
    private void fuctionVersionProjectAlreadyReleased(HttpServerRequest req,JsonObject json){
        Buffer buffer = Buffer.buffer();
        json.writeToBuffer(buffer);
        req.response().end(buffer);
    }

    private void functionStatusProjectTransfer(HttpServerRequest req, JsonObject jsonMessage) {
        Buffer buffer = Buffer.buffer();
        String projectId = jsonMessage.getString("projectId");
        JsonObject json = UpdateXMLUtil.getStatusTransfer(projectId);
        
       
        if(json!=null){
            
            String status = json.getString("status");
            json.writeToBuffer(buffer);
            req.response().end(buffer);
            if(status.equalsIgnoreCase("Transferencia satisfactoria")){
            String pathToFileTemp = PropertiesUtil.getInstance().getRootDatas() + "tempStatus" + File.separator ;
                
            File file = new File(pathToFileTemp);
            deleteChildsOfDir(file);
                    
       
      
            }
                
                    
        }else{
          
            JsonMessage.jsonInitTransfer(projectId).writeToBuffer(buffer);
            req.response().end(buffer);
        };
    }

    private void functionOnTransfer(HttpServerRequest req, JsonObject json) {
        Buffer buffer = Buffer.buffer();
        json.writeToBuffer(buffer);
        req.response().end(buffer);
    }

    private void functionServerExploitationDown(HttpServerRequest req, JsonObject json) {
        Buffer buffer = Buffer.buffer();
        json.writeToBuffer(buffer);
        req.response().end(buffer);
    }
        
    
    
}
