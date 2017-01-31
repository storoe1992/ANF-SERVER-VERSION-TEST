/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.info;

import anf.serverversiontest.controllers.ProjectsController;
import anf.serverversiontest.logic.Project;
import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class Info {
    
    
    public static LinkedList <Project> projects() throws Exception{
        ProjectsController conf = ProjectsController.getInstance();
        LinkedList <Project> listProjects =  conf.getProjects();
        return listProjects;
    }
    
    public static void main(String[] args) throws Exception {
        String a = Info.projects().get(0).id;
        System.out.println(a);
    }
}
