/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;


public class AbstractService {
    private int level;
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<AbstractService> abstractServices = new ArrayList<>();
    private static Instrumentation instrumentation;
    private ArrayList<Instance> input = new ArrayList<>();
    private ArrayList<Instance> output = new ArrayList<>();
    private ArrayList<Concept> inputConcept = new ArrayList<>();
    private ArrayList<Concept> outputConcept = new ArrayList<>();    
    private ArrayList<AbstractService> subServices = new ArrayList<>();/*Only for service fusion based abstraction*/
    private double responseTime;
    private double throughput;
    private double reliability;
    private double availability;
    private double invocationCost;
    private double latency;
    private boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
    
    

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ArrayList<AbstractService> getAbstractServices() {
        return abstractServices;
    }

    public void setAbstractServices(ArrayList<AbstractService> abstractServices) {
        this.abstractServices = abstractServices;
    }
    
    public long getServiceSize(){
        return instrumentation.getObjectSize(this);
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void setInstrumentation(Instrumentation instrumentation) {
        AbstractService.instrumentation = instrumentation;
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

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public double getInvocationCost() {
        return invocationCost;
    }

    public void setInvocationCost(double invocationCost) {
        this.invocationCost = invocationCost;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public ArrayList<AbstractService> getSubServices() {
        return subServices;
    }

    public void setSubServices(ArrayList<AbstractService> subServices) {
        this.subServices = subServices;
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
    
}
