/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.packages;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 *
 * @author Sergio
 */
public class Certificates {
    
    public static void main(String [] args){
        KeyStore st;
        try {
            st = KeyStore.getInstance("JKS");
            st.load(new FileInputStream(new File("ServerTrustedCerts.jks")), "anfupdatereleaser".toCharArray());
            Enumeration<String> aliases = st.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                final Certificate[] certificateChain = st.getCertificateChain(alias);
                final Key key = st.getKey(alias, "anfupdatereleaser".toCharArray());
                PrivateKey pk=(PrivateKey) key;
                System.out.println(pk);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
