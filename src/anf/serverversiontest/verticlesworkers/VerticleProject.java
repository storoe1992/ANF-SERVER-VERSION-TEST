/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.verticlesworkers;

import anf.serverversiontest.info.Info;
import anf.serverversiontest.logic.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class VerticleProject extends AbstractVerticle{
    private EventBus bus;
    
    public void start(){
        bus = vertx.eventBus();
        consumerGetProjectsServer();
    }
    
    public void consumerGetProjectsServer(){
        MessageConsumer <JsonArray> consumer = bus.consumer("verticlesworkers.verticleproject.get_projects_server");
                     consumer.handler(message -> {
                        try{
                         JsonArray jsonListProject = new JsonArray();
                         LinkedList<Project>listProjects = Info.projects();
                         
                         for (Project project : listProjects) {
                             ObjectMapper projectMapper = new ObjectMapper();
                             jsonListProject.add(projectMapper.writeValueAsString(project));
                         }
                         
                        
                         //LinkedList <Project> listProject = Info.projects();
                         message.reply(jsonListProject);
                         //bus.send("getProject", mess);
                         }catch(Exception e){
                             e.printStackTrace();
                         }
                     });
                    
                     
                    
    }
    
}
