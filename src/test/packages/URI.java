/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import shaded.org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Sergio
 */
public class URI {
    
    public static void main(String [] args){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "file");
        String URI = uri.toString();
        System.out.println(URI);
    }
    
}
