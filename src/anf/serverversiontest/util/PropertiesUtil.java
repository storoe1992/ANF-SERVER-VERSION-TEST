/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.util;

/**
 *
 * @author Sergio
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jordy
 */
public class PropertiesUtil {

    private final String propertiesName = "server.properties";
    private final String propertiesFilePath;
    private static PropertiesUtil instance = null;

    private PropertiesUtil() {
        propertiesFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + propertiesName;
    }

    /**
     * Devuelve el nombre de la aplicacion cuyo hash id se correponde con el
     * valor del hash
     *
     * @param hashAppID
     * @return
     * @throws Exception
     */
    public String checkAndGetAppNameByHash(String hashAppID) throws Exception {

//        hashAppID = CryptoUtil.generateOwnHashOfString(hashAppID); //!> ya estas vienen con el hash
//        hashDevID = CryptoUtil.generateOwnHashOfString(hashDevID);
        String rootData = PropertiesUtil.getInstance().getRootDatas();
        File xml = new File(rootData + System.getProperty("file.separator") + "projects.xml");
        Document dom = UpdateXMLUtil.loadXMLFromFile(xml);
        Element doc = dom.getDocumentElement();

        /**
         * Proyectos
         */
        NodeList proj = doc.getElementsByTagName("project");
        int i = 0;
        while (i < proj.getLength()) {
            Node node = proj.item(i++);

            if (node.hasAttributes()) {
                String appID = node.getAttributes().getNamedItem("app-id").getNodeValue();
                String hash = CryptoUtil.generateOwnHashOfString(appID);
                if (hash.equals(hashAppID)) {
                    String currentAppName = node.getAttributes().getNamedItem("app-name").getNodeValue();
                    return currentAppName;
                }
            }
        }

        return null;

    }

    public static PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

//    public static String getPrintableCurrentVersion() {
//        Properties conf = PropertiesUtil.getProperties();
//        return conf.getProperty("version");
//    }
    private static String rootDatas = null;

    public String getRootDatas() {
        if (rootDatas == null) {
            rootDatas = this.getProperties().getProperty("root-datas");
            if (!rootDatas.endsWith("/")) {
                rootDatas += "/";
            }
        }
        return rootDatas;
    }

    public File getPacketsFolder() {
        if (rootDatas == null) {
            rootDatas = this.getProperties().getProperty("root-datas");
            if (!rootDatas.endsWith("/")) {
                rootDatas += "/";
            }
        }
        File pData = new File(rootDatas, "packetFolder");
        if (!pData.exists()) {
            pData.mkdir();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                GeneralUtils.delete(pData);
            }
        }));

        return pData;
    }

    public synchronized Properties getProperties() {

        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream(propertiesFilePath);
            prop.load(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertiesUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertiesUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }

}

