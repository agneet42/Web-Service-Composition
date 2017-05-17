/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

import java.util.ArrayList;
import java.util.Random;


public class Service {
    private String name;
    private ArrayList<Instance> input = new ArrayList<>();
    private ArrayList<Instance> output = new ArrayList<>();
    private ArrayList<Concept> inputConcept = new ArrayList<>();
    private ArrayList<Concept> outputConcept = new ArrayList<>();
    private int responseTime;
    private int throughput;
    private int allResponseTime = 1000000;
    private int allThroughput = 0;
    private double reliability;
    private double availability;
    private double nResponseTime;
    private double nThroughput;
    private double nReliability;
    private double nAvailability;
    private boolean dummy;
    private double utility;
    private int count;

    public double getnReliability() {
        return nReliability;
    }

    public void setnReliability(double nReliability) {
        this.nReliability = nReliability;
    }

    public double getnAvailability() {
        return nAvailability;
    }

    public void setnAvailability(double nAvailability) {
        this.nAvailability = nAvailability;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public int getAllResponseTime() {
        return allResponseTime;
    }

    public void setAllResponseTime(int allResponseTime) {
        this.allResponseTime = allResponseTime;
    }

    public int getAllThroughput() {
        return allThroughput;
    }

    public void setAllThroughput(int allThroughput) {
        this.allThroughput = allThroughput;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }
    
    public double getnResponseTime() {
        return nResponseTime;
    }

    public void setnResponseTime(double nResponseTime) {
        this.nResponseTime = nResponseTime;
    }

    public double getnThroughput() {
        return nThroughput;
    }

    public void setnThroughput(double nThroughput) {
        this.nThroughput = nThroughput;
    }

    public ArrayList<Concept> getInputConcept() {
        return inputConcept;
    }

    public void setInputConcept(ArrayList<Concept> inputConcept) {
        this.inputConcept = inputConcept;
    }

    public ArrayList<Concept> getOutputConcept() {
        return outputConcept;
    }

    public void setOutputConcept(ArrayList<Concept> outputConcept) {
        this.outputConcept = outputConcept;
    }

    
    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
        if (dummy){
            this.responseTime = 0;
            this.throughput = 1000000;
            this.nResponseTime = 1;
            this.nThroughput = 1;
            this.reliability = 1;
            this.availability = 1;
            this.nAvailability = 1;
            this.nReliability = 1;
        }
    }   

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getThroughput() {
        return throughput;
    }

    public void setThroughput(int throughput) {
        this.throughput = throughput;
    }
    
        
    public Service(){
        Random r = new Random();
        this.responseTime = r.nextInt(1000);
        this.throughput = r.nextInt(10000);
        this.reliability = 0.99 + ((double)r.nextInt(100) / 10000.0);
        this.availability = 0.99 + ((double)r.nextInt(100) / 10000.0);
        this.dummy = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Instance> getInput() {
        return input;
    }

    public void setInput(ArrayList<Instance> input) {
        this.input = input;
    }

    public ArrayList<Instance> getOutput() {
        return output;
    }

    public void setOutput(ArrayList<Instance> output) {
        this.output = output;
    }
    
    
    
}
