/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;

/**
 *
 * @author Sergio
 */
public class clientHttps {
   

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
      Vertx vertx = Vertx.vertx();
      vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).getNow(4443, "localhost", "/", resp -> {
      System.out.println("Got response " + resp.statusCode());
      resp.bodyHandler(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));
    });
    
  }

 
    
}
