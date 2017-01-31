/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author Jordy
 */
public class CryptoUtil {

    public static long getCRC32OfFile(File f) throws FileNotFoundException, IOException {
     
        InputStream fi = new BufferedInputStream(new FileInputStream(f));
        int gByte = 0;
        CRC32 gCRC = new CRC32();
        byte[] buf = new byte[1024 * 64];
        while ((gByte = fi.read(buf)) > 0) {
            gCRC.update(buf, 0, gByte);
        }
        fi.close();        
        
        return gCRC.getValue();
    }
    
    
    public static String generateOwnHashOfString(String entry) throws NoSuchAlgorithmException{
        int b0 =  9711;//(83*117);
        int b1 =  8424;//(72*117);
        int b2 =  7605;//(65*117);
        int b3 =  5265;//(45*117);
        int b4 =  5850;//(50*117);
        int b5 =  6201;//(53*117);
        int b6 =  6318;//(54*117);
        int[] big = {b0, b1, b2, b3, b4, b5, b6}; 
        byte[] bs = new byte[7];
        bs[5] = (byte) (big[5]/117);
        bs[3] = (byte) (big[3]/117);
        bs[6] = (byte) (big[6]/117);
        bs[2] = (byte) (big[2]/117);
        bs[1] = (byte) (big[1]/117);
        bs[4] = (byte) (big[4]/117);
        bs[0] = (byte) (big[0]/117);
        String alg = new String(bs); //!> aqui dice SHA-256        
//        System.out.println("alg: " + alg);        
        MessageDigest sha = MessageDigest.getInstance(alg);
        sha.update(entry.getBytes());
        return new String(Base64.getEncoder().encode((sha.digest())));        
    }
    
    
    
}

