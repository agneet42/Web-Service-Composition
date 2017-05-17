/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

import java.util.ArrayList;

public class Concept {
    private String name;
    private Concept superConcept;
    private ArrayList<Concept> subSet = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

    public Concept getSuperConcept() {
        return superConcept;
    }

    public void setSuperConcept(Concept superConcept) {
        this.superConcept = superConcept;
    }

    public ArrayList<Concept> getSubSet() {
        return subSet;
    }

    public void setSubSet(ArrayList<Concept> subSet) {
        this.subSet = subSet;
    }
    
    
    
}
