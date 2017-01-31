/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author Sergio
 */
public class JsonTest {
    
    public static String json(){
        JsonObject json = new JsonObject();
        json.put("fg", Boolean.TRUE);
        return json.encode();
    }
    
    public static void main(String [] args0){
        System.out.println(JsonTest.json()); 
    }
    
}
