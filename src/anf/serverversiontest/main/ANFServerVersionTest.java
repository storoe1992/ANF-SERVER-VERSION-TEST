/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.main;


import anf.serverversiontest.info.Info;
import anf.serverversiontest.controllers.VertxController;

import anf.serverversiontest.logic.Project;
import anf.serverversiontest.server.ANFServer;
import static anf.serverversiontest.util.GeneralUtils.deleteChildsOfDir;
import anf.serverversiontest.util.PropertiesUtil;
import anf.serverversiontest.verticlesworkers.ClientToExplotation;
import anf.serverversiontest.verticlesworkers.VerticleProject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class ANFServerVersionTest {
   // private EventBus bus;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new FileOutputStream(new File("anfserverversiontest.out.log"))));
            System.setErr(new PrintStream(new FileOutputStream(new File("anfserverversiontest.err.log"))));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ANFServerVersionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       Vertx vertx = Vertx.vertx();
       VertxController controller = VertxController.getInstance();
       controller.setVertx(vertx);
       Verticle server = new ANFServer();      
       vertx.deployVerticle(server);
       Verticle verticleProject = new VerticleProject();
       vertx.deployVerticle(verticleProject);
       Verticle clientToExplotation = new ClientToExplotation();
       vertx.deployVerticle(clientToExplotation);
       clearTempStatus();
               
        
        
    }
    
    private static void clearTempStatus(){
        String pathToFileTemp = PropertiesUtil.getInstance().getRootDatas() + "tempStatus" ;
                
            File file = new File(pathToFileTemp);
            deleteChildsOfDir(file);
    } 
    
    
    
    
    
    
    
    
    
}
