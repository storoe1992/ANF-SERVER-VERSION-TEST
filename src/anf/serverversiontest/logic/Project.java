/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.serverversiontest.logic;

import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class Project {

        public String name;
        public String id;
        public LinkedList<Developer> developers = new LinkedList<>();
        private LinkedList <String> versions;
        public boolean mustReset = false;
        public boolean isNew;
        public boolean mustDelete = false;

        public Project() {
            isNew = true;
            versions = new LinkedList<>();
        }

        public void addDeveloper(Developer dev) {
            if (!developers.contains(dev)) {
                developers.add(dev);
            }
        }

        public void removeDeveloper(Developer dev) {
            if (developers.contains(dev)) {
                developers.remove(dev);
            }
        }

        public Developer findDeveloperByEmail(String email) {
            for (Developer d : developers) {
                if (d.email.equalsIgnoreCase(email)) {
                    return d;
                }
            }
            return null;
        }

        public Developer findDeveloperByName(String name) {
            for (Developer d : developers) {
                if (d.name.equalsIgnoreCase(name)) {
                    return d;
                }
            }
            return null;
        }

        public void reset() {
            mustReset = true;
        }

    public void setVersions(LinkedList<String> versions) {
        this.versions = versions;
    }

    public LinkedList<String> getVersions() {
        return versions;
    }
        
        
        

    }
