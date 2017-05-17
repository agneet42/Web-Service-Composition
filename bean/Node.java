/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


public class Node {
    private AbstractService service;
    private HashMap<Instance, ArrayList<Node>> input = new HashMap<>();
    private HashMap<Instance, ArrayList<Node>> output = new HashMap<>();
    private ArrayList<Node> traceRT = new ArrayList<>();
    private ArrayList<Node> traceTR = new ArrayList<>();
    private ArrayList<Node> trace = new ArrayList<>();
    private boolean dummy = false;
    private boolean mark = false;
    private boolean markedVariable = false;
    private int cRT = 0;
    private int cTR = Integer.MAX_VALUE;
    private double cRE = 1;
    private double cAV = 1;
    private double cUtility = 0;
    private int label = 0;
    private int muRT = 0;
    private int sigmaRT = 0;
    private double muTR = 0;
    private double sigmaTR = 0;
    private double reliabilty = 0;
    private double availability = 0;
    private int invocationCost = 0;

    public Node() {
        Random r = new Random();
        this.muRT = r.nextInt(500);
        this.sigmaRT = r.nextInt(100);
        this.availability = 0.99 + (double)((double)r.nextInt(10) / 1000);
        this.reliabilty = 0.99 + (double)((double)r.nextInt(10) / 1000);
        this.invocationCost = 1;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
        if (dummy){
            if (this.service != null)
                this.service.setDummy(dummy);
            this.invocationCost = 0;
            this.reliabilty = 1;
            this.availability = 1;
            this.muRT = 0;
            this.sigmaRT = 0;
            
        }
    }

    public ArrayList<Node> getTraceRT() {
        return traceRT;
    }

    public void setTraceRT(ArrayList<Node> traceRT) {
        this.traceRT = traceRT;
    }

    public ArrayList<Node> getTraceTR() {
        return traceTR;
    }

    public void setTraceTR(ArrayList<Node> traceTR) {
        this.traceTR = traceTR;
    }

    public ArrayList<Node> getTrace() {
        return trace;
    }

    public void setTrace(ArrayList<Node> trace) {
        this.trace = trace;
    }
    

    public double getcRE() {
        return cRE;
    }

    public void setcRE(double cRE) {
        this.cRE = cRE;
    }

    public double getcAV() {
        return cAV;
    }

    public void setcAV(double cAV) {
        this.cAV = cAV;
    }
    
    public int getInvocationCost() {
        return invocationCost;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public void setInvocationCost(int invocationCost) {
        this.invocationCost = invocationCost;
    }

    public boolean isMarkedVariable() {
        return markedVariable;
    }

    public void setMarkedVariable(boolean markedVariable) {
        this.markedVariable = markedVariable;
    }

    public int getMuRT() {
        return muRT;
    }

    public void setMuRT(int muRT) {
        this.muRT = muRT;
    }

    public int getSigmaRT() {
        return sigmaRT;
    }

    public void setSigmaRT(int sigmaRT) {
        this.sigmaRT = sigmaRT;
    }

    public double getMuTR() {
        return muTR;
    }

    public void setMuTR(double muTR) {
        this.muTR = muTR;
    }

    public double getSigmaTR() {
        return sigmaTR;
    }

    public void setSigmaTR(double sigmaTR) {
        this.sigmaTR = sigmaTR;
    }

    public double getReliabilty() {
        return reliabilty;
    }

    public void setReliabilty(double reliabilty) {
        this.reliabilty = reliabilty;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }
        
    public double getcUtility() {
        return cUtility;
    }

    public void setcUtility(double cUtility) {
        this.cUtility = cUtility;
    }
    
    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public int getcRT() {
        return cRT;
    }

    public void setcRT(int cRT) {
        this.cRT = cRT;
    }

    public int getcTR() {
        return cTR;
    }

    public void setcTR(int cTR) {
        this.cTR = cTR;
    }

    public boolean isDummy() {
        return dummy;
    }
  
       
    public AbstractService getService() {
        return service;
    }

    public void setService(AbstractService service) {
        this.service = service;
    }

    public HashMap<Instance, ArrayList<Node>> getInput() {
        return input;
    }

    public void setInput(HashMap<Instance, ArrayList<Node>> input) {
        this.input = input;
    }

    public HashMap<Instance, ArrayList<Node>> getOutput() {
        return output;
    }

    public void setOutput(HashMap<Instance, ArrayList<Node>> output) {
        this.output = output;
    }
    
    
    
}
