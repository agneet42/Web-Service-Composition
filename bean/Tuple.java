/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

import java.util.ArrayList;

public class Tuple {
    private ArrayList<Integer> QoSParameters = new ArrayList<>();

    public ArrayList<Integer> getQoSParameters() {
        return QoSParameters;
    }

    public void setQoSParameters(ArrayList<Integer> QoSParameters) {
        this.QoSParameters = QoSParameters;
    }
    
    
}
