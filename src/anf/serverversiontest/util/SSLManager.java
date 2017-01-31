/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anf.serverversiontest.util;


import io.vertx.core.buffer.Buffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Jordy
 */
public class SSLManager {

//    private static void testJKS(String pass, KeyStore jksStore)
//            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, CertificateException, NoSuchProviderException, SignatureException {
//        if (jksStore != null) {
//            Enumeration<String> aliases = jksStore.aliases();
//            while (aliases.hasMoreElements()) {
//                String alias = (String) aliases.nextElement();
//                System.out.println("alias: " + alias);
//                Certificate[] cert = jksStore.getCertificateChain(alias);
//                X509Certificate[] chain = CERManager.toX509(cert);
////				CERManager.sortChain(cert);
////                PrivateKey key = (PrivateKey) jksStore.getKey(alias, pass.toCharArray());
//                X509Certificate sslCert = chain[CERManager.getFinalCert(chain)];
//                
//                sslCert.verify(chain[1].getPublicKey());
//                DatosPersonales d = new DatosPersonales(sslCert.getSubjectX500Principal());
//                System.out.println("cn: " + d.getCN());
////                System.out.println(sslCert);
////                System.out.println(key);
//            }
//        }
//    }
    public static String STORETYPE = "JKS";
    public static String KEYSTORE = "kstore.jks";
    public static String STOREPASSWORD = "12345678";
    public static String KEYPASSWORD = "1234";
    public static String KEYALIAS = "80510";

    public static Buffer getResourceAsBuffer(String pathName) {
        byte[] bytes = null;
        try {
            InputStream stream = SSLManager.class.getResourceAsStream(pathName);
            bytes = new byte[stream.available()];
            stream.read(bytes);
        } catch (IOException ex) {
            Logger.getLogger(SSLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Buffer serverKS = Buffer.buffer(bytes);
        return serverKS;
    }

    public static Buffer getANFRootCACertAsBuffer() {
        return getResourceAsBuffer("/anf/ANF_Global_Root_CA_SHA256.pem");
    }

    public static Buffer getANFIntermediateCACertAsBuffer() {
        return getResourceAsBuffer("/anf/ANF_High_Assurance_EV_CA1_SHA256.pem");
    }

    public static void exportPEMS() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(KEYSTORE), STOREPASSWORD.toCharArray());
        Certificate cert = keystore.getCertificate(KEYALIAS);

        FileWriter fw = new FileWriter(new File("server-cert.pem"));
        fw.write("-----BEGIN CERTIFICATE-----\n");
        fw.write(Base64.getEncoder().encodeToString(cert.getEncoded()));
        fw.write("\n");
        fw.write("-----END CERTIFICATE-----\n");
        fw.close();
        java.security.Key key = keystore.getKey(KEYALIAS, KEYPASSWORD.toCharArray());
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        fw = new FileWriter(new File("server-key.pem"));
        fw.write("---BEGIN PRIVATE KEY---\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("---END PRIVATE KEY---");
        fw.close();
    }

    public static void main(String[] args) {
//        try {
//            exportPEMS();
//        } catch (Exception ex) {
//            Logger.getLogger(SSLManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Buffer b = getServerKeyStoreAsBuffer();
//        System.out.println("size: " + b.length());

    }

//    public static void setForServer(WebSocketServer server) {
//        try {
//            // load up the key store
//            
////            String alias = "80510";
//
//            KeyStore ks = KeyStore.getInstance(STORETYPE);
//            File kf = new File(KEYSTORE);
//            ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());
//
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
//                    .getDefaultAlgorithm()); //"SunX509"
//            kmf.init(ks, KEYPASSWORD.toCharArray());
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
//                    .getDefaultAlgorithm()); //"SunX509"
//            tmf.init(ks);
//
//            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom()); //ULTIMO PARAM ERA NULL
////            testJKS(KEYPASSWORD, ks);
//            sslContext.getServerSocketFactory();
//            server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
//        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException | IOException | CertificateException ex) {
//            ex.printStackTrace();
//        }
//    }

    public static Buffer getServerPFX() {
        return getResourceAsBuffer("/anf/serverversiontest/pfx/wsi.pfx");
    }
}
