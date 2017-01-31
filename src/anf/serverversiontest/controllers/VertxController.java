/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.controllers;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author Sergio
 */
public class VertxController {
    
    private Vertx vertx;
    private EventBus bus;
    private static VertxController controller;
    
    private VertxController(){}
    
    public static VertxController getInstance(){
        if(controller==null){
            controller = new VertxController();
        }
        return controller;
    }
    
    public Vertx getVertx(){
        return vertx;
    }
    
    public void setVertx(Vertx vertx){
        this.vertx=vertx;
        bus = vertx.eventBus();
        initConsumers();
    }
    
    private void initConsumers(){
        consumerReleaseVersionToExplotation();
    }
    
    private void consumerReleaseVersionToExplotation(){
        MessageConsumer<JsonObject>consumer=bus.consumer("controllers.vertxcontroller.release_version_to_explotation");
        consumer.handler(res->{
            bus.send("verticlesworkers.clienttoexplotation.release_version_project", res.body(),replay->{
                res.reply(replay.result().body());
            });
            //System.out.println("controllers: VertxController: consumerReleaseVersionToExplotation: res.body():" + res.body().encodePrettily());
        });
        
        
    }
    
   
}
