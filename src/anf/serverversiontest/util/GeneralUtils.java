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


import anf.serverversiontest.controllers.ProjectsController;
import anf.serverversiontest.controllers.VertxController;
import io.vertx.core.file.FileSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import shaded.org.apache.commons.codec.binary.Base64InputStream;
import shaded.org.apache.commons.codec.binary.Base64OutputStream;

/**
 *
 * @author Jordy
 */
public class GeneralUtils {
    
    public static File getOrCreateTemporalRoot() {
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File root = new File(tmp, "autoupdate.clients.temp.root/");
        if (!root.exists()) {
            root.mkdir();
        }
        return root;
    }

    public static void clearTemporalRoot() {
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File root = new File(tmp, "autoupdate.clients.temp.root/");
        if (root.exists()) {
            GeneralUtils.delete(root);
        }
    }
    
    public static boolean hasChildNamed(File thisFile, String childName){
        for(File f: thisFile.listFiles()){
            if(f.getName().equals(childName))
                return true;
        }
        return false;
    }

    public static boolean delete(final File directory) {
        assert directory != null && directory.exists();
        if (!directory.isDirectory()) {
            return directory.delete();
        }
        for (final File f : directory.listFiles()) {
            delete(f);
        }
        return directory.delete();
    }

    public static boolean deleteChildsOfDir(final File directory) {
        assert directory != null && directory.exists();
        boolean res = false;
        for (final File f : directory.listFiles()) {
            res |= delete(f);
        }
        return res;
    }
    
//    public static String encodeToBase64FileName(String ppath) {
//        String bpath = Base64.getEncoder().encodeToString(ppath.getBytes());
//        bpath = bpath.replace('/', '_');
//        bpath = bpath.replace('+', '-');
//        return bpath;
//    }

    public static void encodeToBase64(File file, OutputStream base64OutputStream) {

        try {

            InputStream in = new FileInputStream(file);
            OutputStream out = new Base64OutputStream(base64OutputStream);
            IOUtils.copy(in, out);
            in.close();
            out.close();

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }

    public static void decodeFromBase64(InputStream base64InputStream, File toFile) {

        try {

            Base64InputStream in = new Base64InputStream(base64InputStream);
            OutputStream out = new FileOutputStream(toFile);
            IOUtils.copy(in, out);
            in.close();
            out.close();

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }
    
    /**
     *
     * @param f
     * @return
     */
    public static boolean isFileLocked(File f) {
        if (!f.exists()) {
            return false;
        }
        boolean isF_locked = true;
        File tf = null;
        try {
            tf = new File(f.getParentFile(), "_checkIsLocked_" + f.getName());
            Files.copy(f.toPath(), tf.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
//            TimeUnit.SECONDS.sleep(6);
            Files.copy(tf.toPath(), f.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            isF_locked = false;
        } catch (Throwable e) {
            isF_locked = true;
        } finally {
            if (tf != null && tf.exists()) {
                delete(tf);
            }
        }
        System.out.println("el archivo está bloqueado? " + isF_locked);
        return isF_locked;
    }
    
    /**
     * Devuelve la diferencia relativa entre un path y otro.
     *
     * @param pfromPath Directorio desde donde partirá la ruta relativa.
     * @param toPath Directorio al cual llegará la ruta relativa
     * @return La diferencia relativa, es decir, los pasos necesarios para
     * llegar desde fromPath hacia toPath.
     * @throws java.io.IOException
     */
    public static String getRelativeDifference(File pfromPath, File toPath) throws IOException {
        File fromPath = pfromPath;
        if (fromPath.isFile()) {
            fromPath = fromPath.getParentFile();
        }

        String canocicalFrom = fromPath.getCanonicalPath();
        String canocicalTo = toPath.getCanonicalPath();
      //  System.out.println("canocicalFrom: " + canocicalFrom);
       // System.out.println("canocicalTo: " + canocicalTo);
        String result = "";
        if (fromPath.getParentFile().getName().equals(toPath.getParentFile().getName())) {
            result = "./" + toPath.getName();
        } else if (canocicalTo.contains(canocicalFrom)) { //!> esto sucede si toPath está contenido dentro de fromPath
            File parent = toPath;
            Stack<String> st = new Stack<>();
            st.push(toPath.getName());
//            System.out.println("fromPath.getName() = " + fromPath.getName());
            while (parent.getName() != null && !parent.getName().equals(fromPath.getName())) {
                parent = parent.getParentFile();
                if (parent != null) {
                    String s = parent.getName();
                    st.push(s);
                } else {
                    break;
                }
            }
            st.pop();
            result += "./"; //+System.getProperty("file.separator");
            while (!st.isEmpty()) {
                result += st.pop();
                result += "/";
            }
            if (toPath.isFile()) {
                result = result.substring(0, result.length() - 1);
            }
        } else { //!> sucede si toPath no está dentro de fromPath

            File sToPath = toPath;
            if (sToPath.isFile()) {
                sToPath = toPath.getParentFile();
            }

            String lastParentName = "";
            int count = 0;

            Stack<String> st = new Stack<>();
            if (toPath.isFile()) {
                st.push(toPath.getName());
            }

            do {
               // System.out.println("sToPath.getName() = " + sToPath.getName());
                File parent = fromPath;
                count = 0;
                while (!parent.getName().equals(sToPath.getName())) {
                   // System.out.println("parent.getName() = " + parent.getName());
                    parent = parent.getParentFile();
                    if (parent != null) {
                        lastParentName = parent.getName();
                        //System.out.println("lastParentName = " + lastParentName);
                        count++;
                    } else {
                        break;
                    }
                }
                if (lastParentName.isEmpty()) {
                    st.add(sToPath.getName());
                    sToPath = sToPath.getParentFile();
                }

            } while (lastParentName.isEmpty());
            result += "./";
            while (count-- > 0) {
                result += "../";
            }
            while (!st.isEmpty()) {
                result += st.pop();
                result += "/";
            }
//            result += (toPath.getName());
            if (result.charAt(result.length() - 1) == '/') {
                result = result.substring(0, result.length() - 1);
            }

        }

        return result;
    }
    //@Jordy
     public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }
     //@Jordy
    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
    //@Jordy
    public static int compareVersions(String v1, String v2) {
        String cv1 = v1.startsWith("v") ? v1.substring(1) : v1;
        String cv2 = v2.startsWith("v") ? v2.substring(1) : v2;
        String s1 = normalisedVersion(cv1);
        String s2 = normalisedVersion(cv2);
//        System.out.println("s1: " + s1);
//        System.out.println("s2: " + s2);
        int cmp = s1.compareTo(s2);
        return cmp;
    }

    public static boolean isOnTransfer() {
        String rootPath = PropertiesUtil.getInstance().getRootDatas();
        String tempStatusPath = rootPath + File.separator + "tempStatus";
        FileSystem fs = VertxController.getInstance().getVertx().fileSystem();
        List<String> list = fs.readDirBlocking(tempStatusPath);
        //System.out.println("isOnTransfer: " + list.isEmpty());
        return !list.isEmpty();
        
    }
    
    public static boolean isOnTransferProject(String idProject) {
        String rootPath = PropertiesUtil.getInstance().getRootDatas();
        String tempStatusPath = rootPath + File.separator + "tempStatus";
        String projectInProgres = tempStatusPath + File.separator + idProject+".xml";
        File file = new File(projectInProgres);
        FileSystem fs = VertxController.getInstance().getVertx().fileSystem();
        
        return fs.existsBlocking(file.getPath());
        
    }

}
