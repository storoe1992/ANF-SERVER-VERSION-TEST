/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

/**
 *
 * @author Sergio
 */
public class serverHttps {
    
    public static void main (String [] args){
        Vertx vertx = Vertx.vertx();
        HttpServer server =
      vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
        new JksOptions().setPath("server-keystore.jks").setPassword("wibble")
      ));

    server.requestHandler(req -> {
      req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
    }).listen(4443);
    }
    
}
