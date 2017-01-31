/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.util;

import shaded.org.apache.http.client.utils.URIBuilder;

/**
 *
 * @author Sergio
 */
public class UriUtil {
    
    public static String uriTypeFile(String uuid,String rootPath, String fileName,String version){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "file");
        uri.addParameter("uuid", uuid);
        uri.addParameter("filename", fileName);
        uri.addParameter("rootpath", rootPath);
        uri.addParameter("version", version);
        String URI = uri.toString();
        return URI;
    }
    
    public static String uriTypeXMLFile(String uuid,String version){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "json");
        uri.addParameter("uuid", uuid);
        uri.addParameter("version", version);
        String URI = uri.toString();
        return URI;
    }
    
    public static String uriTypeFinalTransfer(String uuid){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "json");
        uri.addParameter("uuid", uuid);
        String URI = uri.toString();
        return URI;
    }
    
    public static String uriTypeProjectInfo(String uuid){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "json");
        uri.addParameter("uuid", uuid);
        String URI = uri.toString();
        return URI;
    }
    
    public static String uriTypeAreYouThere(){
        URIBuilder uri = new URIBuilder();
        uri.addParameter("type", "are you there");
        String URI = uri.toString();
        return URI;
    }
    
}
