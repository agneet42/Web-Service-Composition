/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import wsc08composition.bean.Concept;
import wsc08composition.bean.EdgeVariable;
import wsc08composition.bean.Instance;
import wsc08composition.bean.Node;
import wsc08composition.bean.PNode;
import wsc08composition.bean.AbstractService;
import wsc08composition.bean.VertextVariable;


public class ProcessComposition {
    private final HashSet<Node> check = new HashSet<>();
    private ArrayList<AbstractService> services;
    private final ArrayList<Concept> concepts;
    private final HashMap<String, Instance> instanceMap;
    private final ArrayList<Instance> inputInstances;
    private final ArrayList<Instance> outputInstances;
    private ArrayList<ArrayList<Node>> labelNode = new ArrayList<>();
    private final Node startNode = new Node();
    private final Node endNode = new Node();
    private final HashMap<Node, VertextVariable> vertextVariableMap = new HashMap<>();
    private final ArrayList<EdgeVariable> edgeVariableList = new ArrayList<>();
    private boolean memoryIntencive = false;
    public static final int SIZE = 10;
    
    public ArrayList<ArrayList<Node>> getLabelNode() {
        return labelNode;
    }

    public void setLabelNode(ArrayList<ArrayList<Node>> labelNode) {
        this.labelNode = labelNode;
    }
    
    public ProcessComposition(String serviceFile, String taxonomyFile, String problemFile){
        ReadFile rf = new ReadFile();
        rf.readConceptFile(taxonomyFile);
        rf.readServiceFile(serviceFile);
        rf.readProblemFile(problemFile);
        //services = rf.getServices();
        concepts = rf.getConcepts();
        instanceMap = rf.getInstanceMap();
        inputInstances = rf.getInputInstances();
        outputInstances = rf.getOutputInstances();
    }
    
    public ProcessComposition(ArrayList<AbstractService> services, ArrayList<Concept> concepts, 
            HashMap<String, Instance> instanceMap, ArrayList<Instance> inputInstances, ArrayList<Instance> outputInstances){
        
        this.services = services;
        this.concepts = concepts;
        this.instanceMap = instanceMap;
        this.inputInstances = inputInstances;
        this.outputInstances = outputInstances;
    }
    //check whether ins belongs to the super set of inputs
    private boolean isIncluded(ArrayList<Instance> inputs, Instance ins){
        boolean isAdded = false;
        for (Instance i: inputs){
            if (i == ins)
                return true;
            Concept temp = i.getConcept();
            while (true){
                if (temp == null)
                    break;
                if (temp == ins.getConcept())
                    return true;
                temp = temp.getSuperConcept();                
            }
        }
        return isAdded;
    }
    
    private ArrayList<AbstractService> getServices(ArrayList<Instance> inputs, HashSet<AbstractService> serviceSet){
        //System.out.println(inputs.size());
        ArrayList<AbstractService> serviceList = new ArrayList<>();
        for (AbstractService s: services){
            if (serviceSet.contains(s))
                continue;
            boolean flag = true;
            for (Instance i: s.getInput()){
                if (!isIncluded(inputs, i)){
                    flag = false;
                    break;
                }                    
            }
            if (flag)
                serviceList.add(s);
        }
        return serviceList;
    }
    //check whether outputInstances is the subset of outputs
    private boolean isTerminated(ArrayList<Instance> outputs){
        for (Instance j : outputInstances){
            boolean flag = false;
            for (Instance i : outputs){
                flag = false;
                Concept temp = i.getConcept();
                while (true){                    
                    if (temp == null) break;
                    if (temp == j.getConcept()){
                        flag = true;
                        break;
                    }
                    temp = temp.getSuperConcept();
                }
                if (flag) break;
            }
            if (!flag) return false;
        }
        return true;
    }
    
    public void constructLabel(){
        HashSet<AbstractService> serviceSet = new HashSet<>();
        ArrayList<Instance> inputLabel = new ArrayList<>();
        ArrayList<Instance> outputLabel = new ArrayList<>();
        int labelCount = 0;
        startNode.setService(null);
        for (Instance i: inputInstances){
            startNode.getOutput().put(i, new ArrayList<>());
            inputLabel.add(i);
            outputLabel.add(i);
        }
        for (Instance i: outputInstances)
            endNode.getInput().put(i, new ArrayList<>());
        startNode.setDummy(true);
        endNode.setDummy(true);
        //Construct next label nodes
        labelNode.add(new ArrayList<>());
        //System.out.println("Testing" + labelCount);
        labelNode.get(labelCount).add(startNode);
        startNode.setLabel(labelCount);
        labelCount++;
        while (true){
            //if (isTerminated(outputLabel))System.out.println("Solution Found");
            outputLabel.clear();
            //if (labelCount == 11)break;
            ArrayList<AbstractService> serviceList = this.getServices(inputLabel, serviceSet);
            if (serviceList.isEmpty()){
                //if (isTerminated(inputLabel))System.out.println("Solution Found");
                break;
            }
            //System.out.println(serviceList.size() + " are added in label " + labelCount + " " + serviceSet.size());
            labelNode.add(new ArrayList<>());
            for (AbstractService s: serviceList){
                serviceSet.add(s);
                for (AbstractService s1: s.getSubServices())
                    serviceSet.add(s1);
                //System.out.println(labelCount + " " + s.getName());
                Node n = new Node();
                n.setService(s);
                for (Instance i: s.getInput())
                    n.getInput().put(i, new ArrayList<>());
                for (Instance i: s.getOutput())
                    n.getOutput().put(i, new ArrayList<>());
                n.setLabel(labelCount);
                //System.out.println(labelCount);
                labelNode.get(labelCount).add(n);
                for (Instance i: s.getOutput()){
                    outputLabel.add(i);
                    inputLabel.add(i);
                }
            }
            //System.out.println(inputLabel.size() + " " + outputLabel.size());
            labelCount++;
        }
        //System.out.println(labelCount);
        labelNode.add(new ArrayList<>());
        labelNode.get(labelCount).add(endNode);
        endNode.setLabel(labelCount);
        
    }
    
    private boolean isSuper(Instance ins1, Instance ins2){
        Concept temp = ins1.getConcept();
        while (true){
            if (temp == null)break;
            if (temp == ins2.getConcept())
                return true;
            temp = temp.getSuperConcept();
        }
        return false;
    }
    
    private void getRelevantNodes(Node n1, Instance ins, ArrayList<Node> nodes, int labeli, int labelj){
        for (Node n: nodes)
            for (Instance i: n.getOutput().keySet()){
                if (isSuper(i, ins)){
                    Node tempNode = n;
                    for (int temp = labelj + 1; temp < labeli; temp++){
                        Node newNode = new Node();
                        newNode.setService(new AbstractService());
                        newNode.setDummy(true);
                        newNode.setLabel(temp);
                        newNode.getInput().put(i, new ArrayList<>());
                        newNode.getOutput().put(i, new ArrayList<>());
                        newNode.getInput().get(i).add(tempNode);
                        tempNode.getOutput().get(i).add(newNode);
                        tempNode = newNode;
                        //System.out.println("dummy node is inserted...");
                    }
                    //if (labelj + 1 == labeli)
                    tempNode.getOutput().get(i).add(n1);
                    n1.getInput().get(ins).add(tempNode);
                    //tempNode.setLabel(labelj);
                }
            }
            /*if (n != startNode){
                //System.out.println(n.getService() + " " + (n == startNode));
                for (Instance i: n.getOutput().keySet())
                    if (isSuper(i, ins)){
                        Node tempNode = n;
                        //System.out.println(n.isDummy());
                        for (int temp = labelj + 1; temp < labeli; temp++){
                            Node newNode = new Node();
                            newNode.setService(new Service());
                            newNode.setDummy(true);
                            newNode.getInput().put(i, new ArrayList<Node>());
                            newNode.getOutput().put(i, new ArrayList<Node>());
                            newNode.getInput().get(i).add(tempNode);
                            tempNode.getOutput().get(i).add(newNode);
                            tempNode = newNode;
                            
                        }
                        tempNode.getOutput().get(i).add(n1);
                        n1.getInput().get(ins).add(tempNode);
                    }
            }
            else{*/
                 
            //}       
    }
    
    public void connectGraph(){
        //System.out.println(labelNode.size());
        for (int i = 1; i < labelNode.size(); i++){
            for (int j = 0; j < i; j++){
                for (Node n: labelNode.get(i)){
                    if (n == endNode)
                        for (Instance ins: n.getInput().keySet()){
                            getRelevantNodes(n, ins, labelNode.get(j), i, j);
                        }
                    else
                        for (Instance ins: n.getService().getInput()){
                            getRelevantNodes(n, ins, labelNode.get(j), i, j);
                        }
                }
            }
        }
        
    }
    
    public void generateDependencyGraph(){
        this.constructLabel();
        this.connectGraph();
    }
    
    public void findOptimalResponseTime(){
        long sTime = System.nanoTime();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(startNode);
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            for (Node n: prevNodes){
                for (Instance i: n.getOutput().keySet()){
                    for (Node n1: n.getOutput().get(i))
                        newNodes.add(n1);
                }
            }
           if (newNodes.isEmpty())break;
           for (Node n: newNodes){
               int maxCR = 0;
               for (Instance i: n.getInput().keySet()){
                   int minCR = Integer.MAX_VALUE;
                   Node temp = null;
                   for (Node n1: n.getInput().get(i)){
                       if (n1.getcRT() < minCR){
                           temp = n1;
                           minCR = n1.getcRT();
                       }
                       //minCR = (n1.getcRT() < minCR) ? n1.getcRT() : minCR;
                   }
                   maxCR = (minCR > maxCR) ? minCR : maxCR;
                   n.getTraceRT().add(temp);
               }
               if (n != endNode)
                   n.setcRT((int)n.getService().getResponseTime() + maxCR);
               else
                   n.setcRT(maxCR);
           }
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        System.out.println("Response Time (Optimal): " + endNode.getcRT() + " " + (eTime - sTime));
    }
          
    public void findOptimalThroughput(){
        long sTime = System.nanoTime();
        startNode.setcTR(Integer.MAX_VALUE);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(startNode);
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            for (Node n: prevNodes){
                for (Instance i: n.getOutput().keySet()){
                    for (Node n1: n.getOutput().get(i))
                        newNodes.add(n1);
                }
            }
           if (newNodes.isEmpty())break;
           for (Node n: newNodes){
               int minCRNextLavel = Integer.MAX_VALUE;
               Node temp = null;
               for (Instance i: n.getInput().keySet()){
                   int maxCR = 0;
                   for (Node n1: n.getInput().get(i)){
                       if (n1.getcTR() > maxCR){
                           temp = n1;
                           maxCR = n1.getcTR();
                       }
                       //maxCR = (n1.getcTR() > maxCR) ? n1.getcTR() : maxCR;
                   }
                   minCRNextLavel = (maxCR < minCRNextLavel) ? maxCR : minCRNextLavel;
                   n.getTraceTR().add(temp);
               }
               if (n != endNode){
                   if (!n.isDummy())
                        minCRNextLavel = (n.getService().getThroughput() < minCRNextLavel) ? 
                                (int)n.getService().getThroughput() : minCRNextLavel;
                   n.setcTR(minCRNextLavel);
                   //System.out.println(n.getService().getThroughput() + " " + n.isDummy());
               }
               else
                   n.setcTR(minCRNextLavel);
           }
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        System.out.println("Throughput (Optimal): " + endNode.getcTR() + " " + (eTime - sTime));
    }
    /*Using utility function*/
    
    private void modifyParam(double w1, double w2, double w3, double w4){
        long sTime = System.nanoTime();
        int maxR = 0;
        int minR = 100000;
        int maxT = 0;
        int minT = 100000;
        double minRE = 1;
        double maxRE = 0;
        double maxAV = 0;
        double minAV = 1;
        
        for (ArrayList<Node> nodes: labelNode){
            for (Node n: nodes){
                if (n == startNode || n == endNode)continue;
                int r = (int)n.getService().getResponseTime();
                maxR = (maxR < r) ? r : maxR;
                minR = (minR > r) ? r : minR;
                int t = (int)n.getService().getThroughput();
                maxT = (maxT < t) ? t : maxT;
                minT = (minT > t) ? t : minT;
                double re = n.getService().getReliability();
                maxRE = (maxRE < re) ? re : maxRE;
                minRE = (minRE > re) ? re : minRE;
                double av = n.getService().getAvailability();
                maxAV = (maxAV < av) ? av : maxAV;
                minAV = (minAV > av) ? av : minAV;
            }
        }        
        for (ArrayList<Node> nodes: labelNode){
            for (Node n: nodes){
                if (n == startNode || n == endNode)continue;
              /*  n.getService().setnResponseTime(((double)(maxR - n.getService().getResponseTime())) / (maxR - minR));
                n.getService().setnThroughput(((double)(n.getService().getThroughput() - minT)) / (maxT - minT));
                n.getService().setnReliability(((double)(n.getService().getReliability()- minRE)) / (maxRE - minRE));
                n.getService().setnAvailability(((double)(n.getService().getAvailability()- minAV)) / (maxAV - minAV));
                n.getService().setUtility((w1 * n.getService().getnResponseTime()) + (w2 * n.getService().getnThroughput())
                         + (w3 * n.getService().getnReliability())  + (w4 * n.getService().getnAvailability()));*/
            }
        }
        this.findOptimalMultipleQoS();
        long eTime = System.nanoTime();
        System.out.println("Time (Optimal): " + (eTime - sTime));
    }
    
    private void calculateReAv(HashSet<Node> traces, Node current){
        if (current.getTrace().isEmpty())return;
        for (Node n: current.getTrace()){
            traces.add(n);
            calculateReAv(traces, n);
        }
            
    }
    
    private void calculateReAv(){
        double re = 1;
        double av = 1;
        HashSet<Node> traces = new HashSet<>();
        this.calculateReAv(traces, endNode);
        for (Node n: traces){
            re *= n.getService().getReliability();
            av *= n.getService().getAvailability();
        }
        endNode.setcRE(re);
        endNode.setcAV(av);
    }
    
    public void findOptimalMultipleQoS(){
        startNode.setcTR(100000);
        startNode.setcRT(0);
        startNode.setcRE(1);
        startNode.setcAV(1);
        startNode.setcUtility(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(startNode);
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            for (Node n: prevNodes){
                for (Instance i: n.getOutput().keySet()){
                    for (Node n1: n.getOutput().get(i))
                        newNodes.add(n1);
                }
            }
           if (newNodes.isEmpty())break;
           for (Node n: newNodes){
               int minCTNextLavel = 100000;
               int maxCRNextLavel = 0;
               double cRE = 1, cAV = 1;
               double maxUtility = 0;
               for (Instance i: n.getInput().keySet()){
                   double maxU = 0;
                   Node temp = n.getInput().get(i).get(0);
                   for (Node n1: n.getInput().get(i)){
                       //System.out.println(n1.getcUtility() + " testing " + maxU);
                       if (n1.getcUtility() >= maxU){
                           maxU = n1.getcUtility();
                           temp = n1;
                       }                       
                   }
                   if (temp != endNode && temp != startNode){
                        maxUtility += maxU;
                        //System.out.println(temp.getService() + " testing " + maxU);
                        maxCRNextLavel = (maxCRNextLavel < temp.getcRT()) ? 
                                temp.getcRT() : maxCRNextLavel;
                        //System.out.println(maxCRNextLavel + " testing " + maxU);
                        minCTNextLavel = (minCTNextLavel > temp.getcTR()) ?
                                temp.getcTR() : minCTNextLavel;
                        //cRE *= temp.getcRE();
                        //cAV *= temp.getcAV();
                       // System.out.println("****" + cAV);
                        n.getTrace().add(temp);
                   }
               }
               if (n != endNode){
                   //n.setcUtility(maxUtility + n.getService().getUtility());
                   minCTNextLavel = (n.getService().getThroughput() < minCTNextLavel) ? 
                           (int)n.getService().getThroughput() : minCTNextLavel;
                   n.setcTR(minCTNextLavel);
                   n.setcRT(maxCRNextLavel + (int)n.getService().getResponseTime());
                   //n.setcRE(cRE * n.getService().getReliability());
                   //n.setcAV(cAV * n.getService().getAvailability());
                   //System.out.println("****" + cAV + " " + n.getService().getAvailability());
               }
               else{
                   n.setcTR(minCTNextLavel);
                   n.setcRT(maxCRNextLavel);
                   n.setcUtility(maxUtility);
                   //n.setcRE(cRE);n.setcAV(cAV);
               }
           }
           prevNodes = newNodes;
        }
        this.calculateReAv();
        System.out.println("Multi QoS using Selection (Response Time, Throughput, Reliability, Availability) (Utility): (" 
                + endNode.getcRT() + ", " + endNode.getcTR() + ", " + endNode.getcRE() + ", " + endNode.getcAV() + ") ");        
    }
    
    public void findOurCaseResponseTime(){
        long sTime = System.nanoTime();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(endNode);
        int RT = 0;
        int l = 0;
        while (true){
            l++;
            HashSet<Node> newNodes = new HashSet<>();
            int maxRT = 0;
            for (Node n: prevNodes){
                for (Instance i: n.getInput().keySet()){
                    //System.out.println("Testing");
                    Node temp = null;
                    int minRT = 100000;
                    for (Node n1: n.getInput().get(i)){
                        //System.out.println(n1.getService().getResponseTime() + " " + minRT);
                        if (n1 != startNode && n1.getService().getResponseTime() < minRT){
                            minRT = (int)n1.getService().getResponseTime();
                            temp = n1;
                        }
                        else if (n1 == startNode){
                            minRT = 0;
                            temp = n1;
                        }
                    }
                    maxRT = (minRT > maxRT) ? minRT : maxRT;
                    newNodes.add(temp);
                }               
            }
            RT += maxRT;
           if (newNodes.isEmpty())break;           
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        System.out.println("Response Time (OurCase): " + RT + " " + (eTime - sTime));
    }
    
    public void findOurCaseReliability(){
        long sTime = System.nanoTime();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(endNode);
        double Re = 1;
        int l = 0;
        while (true){
            //l++;
            HashSet<Node> newNodes = new HashSet<>();
            double cRe = 1;
            for (Node n: prevNodes){
                HashSet<Instance> instanceSet = new HashSet<>();
                for (Instance i: n.getInput().keySet()){
                    if (instanceSet.contains(i))continue;
                    //System.out.println("Testing");
                    Node temp = null;
                    double maxRe = 0;
                    for (Node n1: n.getInput().get(i)){
                       // System.out.println(n1.getService().getReliability()+ " " + maxRe);
                        if (n1 != startNode && n1.getService().getReliability()> maxRe){
                            maxRe = n1.getService().getReliability();
                            temp = n1;
                        }
                        else if (n1 == startNode){
                            maxRe = 1;
                            temp = n1;
                        }
                    }
                    cRe *= maxRe;
                    newNodes.add(temp); 
                    if (!temp.isDummy())l++;
                   for (Instance j: n.getInput().keySet())
                        if (n.getInput().get(j).contains(temp))
                            instanceSet.add(j);
                }  
                
                        
            }
           Re *= cRe;
           if (newNodes.isEmpty())break;           
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        System.out.println("Reliability (OurCase): " + Re + " " + (eTime - sTime) + " " + l);
    }
    
    public void findOurCaseThroughput(){
     long sTime = System.nanoTime();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(endNode);
        int TR = 100000;
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            for (Node n: prevNodes){
                int minTR1 = 100000;
                for (Instance i: n.getInput().keySet()){
                    Node temp = null;
                    int maxRT = 0;
                    for (Node n1: n.getInput().get(i)){
                        //if (n1 != startNode)
                        //System.out.println(n1.getService().getThroughput());
                        if (n1 != startNode && n1.getService().getThroughput()> maxRT){
                            maxRT = (int)n1.getService().getThroughput();
                            temp = n1;
                        }
                        else if (n1 == startNode){
                            maxRT = 100000;
                            temp = n1;
                        }
                    }
                    //if (temp != startNode)
                    //System.out.println(temp.getService().getThroughput() + " got selected");
                    minTR1 = (maxRT < minTR1) ? maxRT : minTR1;
                    newNodes.add(temp);
                }
                TR = (TR > minTR1) ? minTR1 : TR;
            }
           if (newNodes.isEmpty())break;           
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        System.out.println("Throughput (OurCase): " + TR + " " + (eTime - sTime));
    }
        
    private Node getBestNodeUsingScore(ArrayList<Node> nodes){
        Node bestNode = null;
        TreeMap<Integer, ArrayList<Node>> nodesSortedByRT = new TreeMap<>();
        TreeMap<Integer, ArrayList<Node>> nodesSortedByTR = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Double, ArrayList<Node>> nodesSortedByRE = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Double, ArrayList<Node>> nodesSortedByAV = new TreeMap<>(Collections.reverseOrder());
        
        HashMap<Node, Integer> rankSortedByRT = new HashMap<>();
        HashMap<Node, Integer> rankSortedByTR = new HashMap<>();
        HashMap<Node, Integer> rankSortedByRE = new HashMap<>();
        HashMap<Node, Integer> rankSortedByAV = new HashMap<>();
        for (Node n: nodes){
            if (n == startNode || n == endNode){
                bestNode = n;
                break;
            }
            if (nodesSortedByRT.get((int)n.getService().getResponseTime()) == null)
                nodesSortedByRT.put((int)n.getService().getResponseTime(), new ArrayList<>());
            nodesSortedByRT.get((int)n.getService().getResponseTime()).add(n);
            if (nodesSortedByTR.get((int)n.getService().getThroughput()) == null)
                nodesSortedByTR.put((int)n.getService().getThroughput(), new ArrayList<>());
            nodesSortedByTR.get((int)n.getService().getThroughput()).add(n);
            if (nodesSortedByRE.get(n.getService().getReliability()) == null)
                nodesSortedByRE.put(n.getService().getReliability(), new ArrayList<>());
            nodesSortedByRE.get(n.getService().getReliability()).add(n);
            if (nodesSortedByAV.get(n.getService().getAvailability()) == null)
                nodesSortedByAV.put(n.getService().getAvailability(), new ArrayList<>());
            nodesSortedByAV.get(n.getService().getAvailability()).add(n);
        }
        int rank = 0;
        
        for (Integer i: nodesSortedByRT.keySet()){
            for (Node n: nodesSortedByRT.get(i)){
                rankSortedByRT.put(n, rank);
            }
            rank++;
        }
        int lastRTRank = rank;
        rank = 0;
        for (Integer i: nodesSortedByTR.keySet()){
            for (Node n: nodesSortedByTR.get(i)){
                rankSortedByTR.put(n, rank);
            }
            rank++;
        }
        int lastTRank = rank;
        
        rank = 0;
        for (Double i: nodesSortedByRE.keySet()){
            for (Node n: nodesSortedByRE.get(i)){
                rankSortedByRE.put(n, rank);
            }
            rank++;
        }
        int lastRERank = rank;
        
        rank = 0;
        for (Double i: nodesSortedByAV.keySet()){
            for (Node n: nodesSortedByAV.get(i)){
                rankSortedByAV.put(n, rank);
            }
            rank++;
        }
        int lastAVRank = rank;
        
        int maxScore = 0;
        for (Node n: rankSortedByRT.keySet()){
            int score = (lastRTRank - rankSortedByRT.get(n)) + (lastTRank - rankSortedByTR.get(n)) + 
                    (lastRERank - rankSortedByRE.get(n)) + (lastAVRank - rankSortedByAV.get(n));
            if (maxScore < score){
                maxScore = score;
                bestNode = n;
            }
        }
        
        return bestNode;
    }
    
    public void findOurCaseMultipleQoSUsingScore(){
        long sTime = System.nanoTime();
        HashSet<Node> trace = new HashSet<>();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(endNode);
        int RT = 0;
        int TR = 1000000;
        double RE = 1;
        double AV = 1;
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            int maxRT = 0;
            int minT = 1000000;
            double minRE = 1;
            double minAV = 1;
            for (Node n: prevNodes){
                
                for (Instance i: n.getInput().keySet()){                    
                    Node temp = this.getBestNodeUsingScore(n.getInput().get(i));
                    if (temp != startNode)
                        maxRT = (temp.getService().getResponseTime() > maxRT) ? (int)temp.getService().getResponseTime() : maxRT;
                    else 
                        maxRT = 0;
                    if (temp != startNode)
                        minT = (temp.getService().getThroughput() < minT) ? (int)temp.getService().getThroughput(): minT;
                    else
                        minT = 1000000;
                   /* if (temp != startNode)
                        minRE *= temp.getService().getReliability();
                   
                    if (temp != startNode)
                        minAV *= temp.getService().getAvailability();*/
                    trace.add(temp);
                    
                    newNodes.add(temp);
                }
            }
            RT += maxRT;
            TR = (minT < TR) ? minT : TR;
            //RE *= minRE;
            //AV *= minAV;
           if (newNodes.isEmpty())break;           
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        double re = 1, av = 1;
        for (Node n: trace){
            if (n.isDummy())continue;
            re *= n.getService().getReliability();
            av *= n.getService().getAvailability();
        }
        System.out.println("Multi QoS Using Score (Response Time, Throughput, Reliability, Availability) (OurCase): (" + RT + ", " + TR + ", " + re + ", " + av + ") " + (eTime - sTime));        
    }
    
    private Node getBestNodeUsingSelection(ArrayList<Node> nodes){
        Node bestNode = nodes.get(0);
        double minRT = 10000000, meanRT = 0;
        double maxT = 0, meanT = 0;
        double maxRE = 0, meanRE = 0;
        double maxAV = 0, meanAV = 0;
        //System.out.println("testing");
        if (nodes.size() == 1)
            return nodes.get(0);
        for (Node n: nodes){
            if (n == startNode)break;
            int rt = (int)n.getService().getResponseTime();
            minRT = (minRT > rt)? rt : minRT;
            meanRT += rt;
            
            int t = (int)n.getService().getThroughput();
            maxT = (maxT < t)? t : maxT;
            meanT += t;
            
            double re = n.getService().getReliability();
            maxRE = (maxRE < re) ? re: maxRE;
            meanRE += re;
            
            double av = n.getService().getAvailability();
            maxAV = (maxAV < av) ? av: maxAV;
            meanAV += av;
        }
        meanRT /= nodes.size(); meanT /= nodes.size(); meanRE /= nodes.size(); meanAV /= nodes.size();
        
        double maxValue = -100000;
        for (Node n: nodes){
            if (n == startNode){
                bestNode = n;
                break;
            }
            //System.out.println(meanRT + " " + minRT + " " + maxT + " " + meanT);
            double rt = ((double)(meanRT - n.getService().getResponseTime())) / (meanRT - minRT);
            double t = ((double)(maxT - n.getService().getThroughput())) / (maxT - meanT);
            double re = (maxRE - n.getService().getReliability()) / (maxRE - meanRE);
            double av = (maxAV - n.getService().getAvailability()) / (maxAV - meanAV);
            
            double comp = ((rt < t) && (rt < re) && (rt < av)) ? rt : ((t < re) && (t < av)) ? t : (re < av)? re : av;
            //System.out.println(comp + " " + maxValue + " " + nodes.size());
            if (comp > maxValue){
                //System.out.println(n.getService().getName());
                maxValue = comp;
                bestNode = n;
            }
        }
        //System.out.println(nodes.size() + " " + bestNode.getService().getResponseTime() + " " + bestNode.getService().getThroughput());
        return bestNode;
     }
    
    public void findOurCaseMultipleQoSUsingSelection(){
        long sTime = System.nanoTime();
        HashSet<Node> trace = new HashSet<>();
        startNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(endNode);
        int RT = 0;
        int TR = 1000000;
        double RE = 1, AV = 1;
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            int maxRT = 0;
            int minT = 1000000;
            double minRE = 1, minAV = 1;
            for (Node n: prevNodes){                
                for (Instance i: n.getInput().keySet()){
                    //System.out.println("Testing");
                    Node temp = this.getBestNodeUsingSelection(n.getInput().get(i));
                    if (temp != startNode){
                        //System.out.println(temp + " ");
                        maxRT = (temp.getService().getResponseTime() > maxRT) ? (int)temp.getService().getResponseTime() : maxRT;
                    }
                    else 
                        maxRT = 0;
                    if (temp != startNode)
                        minT = (temp.getService().getThroughput() < minT) ? (int)temp.getService().getThroughput(): minT;
                    else
                        minT = 1000000;
                    /*if (temp != startNode)
                        minRE *= temp.getService().getReliability();
                    if (temp != startNode)
                        minAV *= temp.getService().getAvailability();*/
                    trace.add(temp);
                    newNodes.add(temp);
                }
                
            }
            RT += maxRT;
            TR = (minT < TR) ? minT : TR;
            //RE *= minRE;
            //AV *= minAV;
           if (newNodes.isEmpty())break;           
           prevNodes = newNodes;
        }
        long eTime = System.nanoTime();
        double re = 1, av = 1;
        for (Node n: trace){
            if (n.isDummy())continue;
            re *= n.getService().getReliability();
            av *= n.getService().getAvailability();
        }
        System.out.println("Multi QoS using Selection** (Response Time, Throughput, Reliability, Availability) (OurCase): (" + RT + ", " + TR + ", " + re + ", " + av + ") " + (eTime - sTime));        
    }   
    
    public int findAnyTimeOptimalResponseTime(){
        startNode.setcRT(0);
        endNode.setcRT(0);
        HashSet<Node> prevNodes = new HashSet<>();
        prevNodes.add(startNode);
        while (true){
            HashSet<Node> newNodes = new HashSet<>();
            for (Node n: prevNodes){                
                for (Instance i: n.getOutput().keySet()){
                    for (Node n1: n.getOutput().get(i)){
                        if (n1.isMark())
                            newNodes.add(n1);
                    }
                }
            }
           if (newNodes.isEmpty())break;
           for (Node n: newNodes){
               int maxCR = 0;
               for (Instance i: n.getInput().keySet()){
                   int minCR = 100000;
                   for (Node n1: n.getInput().get(i)){                       
                       if (!n1.isMark()){ 
                           //System.out.println("unexplored");
                           continue;
                       }
                       minCR = (n1.getcRT() < minCR) ? n1.getcRT() : minCR;
                   }
                   maxCR = (minCR > maxCR) ? minCR : maxCR;
               }
               if (n != endNode){
                   n.setcRT((int)n.getService().getResponseTime() + maxCR);
               }
               else{
                   n.setcRT(maxCR);
               }
           }
           prevNodes = newNodes;
        }
        return endNode.getcRT();
    }
    
    public void recursiveClean(Node n, HashSet<Node> h, int l){
       // System.out.println(n + " " + h.contains(n) + " " + l);
        if (n == null || h.contains(n))return;
        n.getTrace().clear();
        h.add(n);
        n.setMark(false);
        n.setcRT(0);
        n.setcTR(100000);
        n.setcRE(1);
        n.setcAV(1);
        for (Instance i: n.getOutput().keySet()){
            for (Node n1: n.getOutput().get(i))
                recursiveClean(n1, h, l + 1);
            //System.out.println("Done " + n.getOutput().keySet().size());
        }
    }
    
    public void clean(){
        recursiveClean(startNode, new HashSet<Node>(), 0);
    }
    
    private int vCount = 0;
    private int eCount = 0;
    private void generateVariables(Node prevNode){
        if (prevNode.isMarkedVariable())
            return;        
        prevNode.setMarkedVariable(true);
        VertextVariable v = new VertextVariable(prevNode, vCount);
        vertextVariableMap.put(prevNode, v);
        vCount++;
        
        for (Instance i: prevNode.getInput().keySet())
            for (Node n: prevNode.getInput().get(i)){
                EdgeVariable e = new EdgeVariable(n, prevNode, i, eCount);
                edgeVariableList.add(e);                
                eCount++;
                generateVariables(n);
            }
    }
    
    private void generateObjective(){
        ArrayList<Integer> constraints = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints.add(0);
        for (Node n: vertextVariableMap.keySet())
            constraints.set(vertextVariableMap.get(n).getCount(), n.getInvocationCost());
        String str = "";
        for (Integer i: constraints)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
    }
    
    public void formulateILP(boolean isUnknown){
        this.generateVariables(endNode);
        //System.out.println(vertextVariableMap.size() + edgeVariableList.size());
        this.generateObjective();
        ArrayList<Integer> constraints = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints.add(0);
        //First constraint Ns = 1
        constraints.set(vertextVariableMap.get(startNode).getCount(), 1);
        String str = "";
        for (Integer i: constraints)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        constraints.clear();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints.add(0);
        //Second constraint Ne = 1
        constraints.set(vertextVariableMap.get(endNode).getCount(), 1);
        str = "";
        for (Integer i: constraints)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        //reliability time constraints
        ArrayList<Double> constraints1 = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints1.add(0.0);
        for (Node n: vertextVariableMap.keySet())
            constraints1.set(vertextVariableMap.get(n).getCount(), Math.log((n.getReliabilty())));
        
        str = "";
        for (Double i: constraints1)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        //************************************************************************************************************************
        //mu response time constraints
        constraints = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints.add(0);
        for (Node n: vertextVariableMap.keySet())
            constraints.set(vertextVariableMap.get(n).getCount(), n.getMuRT());
        constraints.set(vertextVariableMap.size() + edgeVariableList.size(), -1);
        str = "";
        for (Integer i: constraints)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        //variance response time constraints
        constraints = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints.add(0);
        for (Node n: vertextVariableMap.keySet())
            constraints.set(vertextVariableMap.get(n).getCount(), n.getSigmaRT());
        constraints.set(vertextVariableMap.size() + edgeVariableList.size() + 1, -1);
        str = "";
        for (Integer i: constraints)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        //approximation constraints
        constraints1 = new ArrayList<>();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints1.add(0.0);
        constraints1.set(vertextVariableMap.size() + edgeVariableList.size() + 2, 1.0);
        constraints1.set(vertextVariableMap.size() + edgeVariableList.size() + 1, -0.005);
        str = "";
        for (Double i: constraints1)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        //************************************************************************************************************************
        
        //availability time constraints
        constraints1.clear();
        for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
            constraints1.add(0.0);
        for (Node n: vertextVariableMap.keySet())
            constraints1.set(vertextVariableMap.get(n).getCount(), Math.log((n.getAvailability())));
        
        str = "";
        for (Double i: constraints1)
            str += i + ",";
        System.out.println(str.substring(0, str.length() - 1));
        
        //If each node gets selected then its outputs should be available
        for (EdgeVariable e: edgeVariableList){
            constraints.clear();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            constraints.set(vertextVariableMap.size() + e.getCount(), 1);
            constraints.set(vertextVariableMap.get(e.getInputNode()).getCount(), -1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            System.out.println(str.substring(0, str.length() - 1));
        }
            
        //A node can be selected if all its inputs are available
        for (Node n: vertextVariableMap.keySet()){
            if (n == startNode)continue;
            
            for (Instance in: n.getInput().keySet()){
                constraints.clear();
                for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                    constraints.add(0);
                for (EdgeVariable e: edgeVariableList){
                    if (e.getOutputNode() == n && e.getOutputInstance() == in){
                        constraints.set(vertextVariableMap.size() + e.getCount(), -1);
                    }
                } 
                constraints.set(vertextVariableMap.get(n).getCount(), 1);
                str = "";
                for (Integer i: constraints)
                    str += i + ",";
                System.out.println(str.substring(0, str.length() - 1));
            }            
        }
        
        
        
        //for (EdgeVariable e: edgeVariableList)
            //System.out.println(e.getCount());
        //System.out.println(vertextVariableMap.size() + " " + edgeVariableList.size());
        //System.out.println("x" + vertextVariableMap.get(startNode).getCount() + " = 1");
        //System.out.println("x" + vertextVariableMap.get(endNode).getCount() + " = 1");
        
    }
         
    private ArrayList<HashSet<Node>> getDummyCartesianProduct(ArrayList<HashSet<Node>> inputNodes, 
            int index, ArrayList<HashSet<Node>> product){
        
        HashSet<Node> nodes = inputNodes.get(index);
        ArrayList<HashSet<Node>> newProduct = new ArrayList<>();
        for (HashSet<Node> h: product){
            boolean exit = false;
            for (Node i: h){
                if (nodes.contains(i)){
                    exit = true;
                    newProduct.add(h);
                    break;
                }
            }
            if (exit)continue;
            for (Node node: nodes){
                boolean flag = true;
                for (int j = 0; j < index; j++){
                    //System.out.println(node + " *****" );
                    if (!inputNodes.get(j).contains(node)){
                        flag = false;
                        break;
                    }
                }
                if (flag)continue;
                HashSet<Node> h1 = new HashSet<>();
                for (Node k: h)
                    h1.add(k);
                h1.add(node);
                newProduct.add(h1);
                if (this.memoryIntencive && product.size() > SIZE)break;
            }
        }
        if (product.isEmpty()){
            for (Node i: nodes){
                HashSet<Node> h = new HashSet<>();
                h.add(i);
                newProduct.add(h);
            }
        }
        return newProduct;
    }
                
    public ArrayList<HashSet<Node>> getDummyCartesianProduct(ArrayList<ArrayList<Node>> inputNodes){
        ArrayList<HashSet<Node>> product = new ArrayList<>();
        //this.modifyList(inputNodes);
        /*int size = 1;
        for (ArrayList<Node> nodes: inputNodes)
            size *= nodes.size();
        System.out.println(size + " " + inputNodes.size());*/
        ArrayList<HashSet<Node>> inputSet = new ArrayList<>();
        for (ArrayList<Node> nodes: inputNodes){
            HashSet<Node> h = new HashSet<>();
            for (Node node: nodes)
                h.add(node);
            inputSet.add(h);
        }
        
        for (int i = 0; i < inputNodes.size(); i++)
            product = getDummyCartesianProduct(inputSet, i, product);
        //System.out.println(product.size());
        return product;
    }
    
    private static final int LENGTH = 3;
    private void modifyList(ArrayList<ArrayList<Node>> inputNodes){
        int s = 1;
        for (ArrayList<Node> list : inputNodes){
            s *= list.size();
            //System.out.println(list.size());
        }
        if (s <= 1000)return;
        TreeMap<Integer, ArrayList<ArrayList<Node>>> sizeMap = new TreeMap<>();
        for (ArrayList<Node> nodes: inputNodes){
            int length = 0;
            for (Node n: nodes){
                length += 1;
            }
            if (sizeMap.get(length) == null)
                sizeMap.put(length, new ArrayList<ArrayList<Node>>());
            sizeMap.get(length).add(nodes);
        }
        for (Integer size: sizeMap.keySet()){
            if (size < LENGTH)continue;
            HashSet<Node> h = new HashSet<>();
            for (ArrayList<Node> nodes: sizeMap.get(size)){
                for (Node n: nodes)
                    if (n.getInvocationCost() == 0)h.add(n);
                if (h.size() > LENGTH){
                    int count = 0;
                    for (int i = nodes.size() - 1; i >= 0; i--)
                        if (!h.contains(nodes.get(i)))nodes.remove(i);
                        else{
                            if (count == LENGTH){
                                nodes.remove(i);
                            }
                            else
                                count++;
                        }
                }
                else{
                    int count = 0;
                    for (int i = nodes.size() - 1; i >= 0; i--){
                        if (nodes.get(i).getInvocationCost() == 0)
                            continue;
                        //count++;
                        if (count + h.size() == LENGTH)
                            nodes.remove(i);
                        else count++;
                    }
                }
            }
        }
        
    }
    
    public ArrayList<HashSet<Node>> getCartesianProduct(ArrayList<ArrayList<Node>> inputNodes){
        ArrayList<HashSet<Node>> product = new ArrayList<>();
        int length = 0;
        int size = 1;
        for (ArrayList<Node> list : inputNodes){
            size *= list.size();
            //System.out.println(list.size());
        }
        //System.out.println(inputNodes.size() + " Constructed : " + size);
        //size = size < 1000 ? size : 1000;
        for (int i = 0; i < size; i++)
            product.add(new HashSet<Node>());
        for (int j = 0; j < inputNodes.size(); j++){
            if (j == 0) length = 1;
            else if (j == 1) length = inputNodes.get(j - 1).size();
            else length *= inputNodes.get(j - 1).size();
            int count = 0;
            for (int i = 0; i < size; i += length){
                for (int k = 0; k < length; k++)
                    product.get(i).add(inputNodes.get(j).get(count % inputNodes.get(j).size()));
                count++;
            }
        }
        
        return product;
    }
    
    private PNode getClosed(HashMap<String, ArrayList<PNode>> closedNodes, HashSet<Node> newNodes){
        String str = "";
        for (Node n: newNodes){
            str = n.getLabel() + "";
            break;
        }        
        if (closedNodes.get(str) == null)return null;        
        for (PNode pn: closedNodes.get(str)){
            int count = 0;
            Boolean flag = true;
            for (Node n: pn.getNodes()){
                if (!newNodes.contains(n)){
                    flag = false;
                    break;
                }         
                else
                    count++;
            }
            if (flag || count == newNodes.size())return pn;
        }
        return null;
    }
     
    public void subOptimalAlgo(){
        PriorityQueue<PNode> pQ = new PriorityQueue<>(10, new Comparator<PNode>(){
            @Override
            public int compare(PNode p1, PNode p2) {
            return (p1.getCumulativeCost()== p2.getCumulativeCost()) ? 0 : 
                    (p1.getCumulativeCost() > p2.getCumulativeCost() ? 1 : -1);
        }
        });   
        HashMap<String, ArrayList<PNode>> closedNodes = new HashMap<>();
        PNode p = new PNode();
        p.setCumulativeCost(endNode.getLabel());
        //System.out.println(endNode.getLabel());
        p.getNodes().add(endNode);
        p.setLabel(endNode.getLabel());
        pQ.add(p);
        String str = endNode.getLabel() + "" + p.getNodes().size();
        closedNodes.put(str, new ArrayList<PNode>());
        closedNodes.get(str).add(p);
       // boolean flag = false;
        while (!pQ.isEmpty()){
            PNode newPNode = pQ.poll(); 
            if (newPNode.getNodes().size() == 1 && newPNode.getNodes().get(0) == startNode){
                   // double tempR = newPNode.getCumulativeMuRT() + PHI_INVERSE_EIGHTY_PERCENT * Math.pow(newPNode.getCumulativeSigmaRT(), 0.5);
                    //System.out.println(newPNode.getTrueCost() + " " + newPNode.getCumulativeAvailability()
                      //      + " " + newPNode.getCumulativeReliability() + " " + tempR);
                    //flag = true;
                    break;
            } 
            //System.out.println(newPNode + " " + newPNode.getLabel());
            ArrayList<ArrayList<Node>> inputNodes = new ArrayList<>();
            
            HashMap<HashSet<Node>, ArrayList<Node>> temp = new HashMap<>();
            HashSet<Instance> instanceSet = new HashSet<>();
            for (Node n: newPNode.getNodes()){
                for (Instance i: n.getInput().keySet()){
                    if (instanceSet.contains(i))continue;
                    instanceSet.add(i);//inputNodes.add(n.getInput().get(i));
                    /*Put the nodes in a hashmap*/
                    HashSet<Node> h2 = new HashSet<>();
                    for (Node n1: n.getInput().get(i))
                        h2.add(n1);                
                    
                    boolean exist = true;
                    for (HashSet<Node> h1: temp.keySet()){
                        if (h1.size() >= h2.size()){                                                    
                            for (Node n1: h2){
                                if (!h1.contains(n1)){
                                    exist = false;
                                    break;
                                }
                            }
                            if (exist){
                                inputNodes.remove(temp.get(h1));
                                inputNodes.add(n.getInput().get(i));
                                break;
                            }
                        }
                        else{                         
                            for (Node n1: h1){
                                if (!h2.contains(n1)){
                                    exist = false;
                                    break;
                                }
                            }
                            if  (exist){
                                break;
                            }
                        }
                        //System.out.println(exist);
                    }
                    //System.out.println(exist);
                    if (temp.isEmpty() || !exist){
                        temp.put(h2, n.getInput().get(i));
                        inputNodes.add(n.getInput().get(i));
                    }
                }
            }
            //ArrayList<HashSet<Node>> outputNode = this.getCartesianProduct(inputNodes);            
            ArrayList<HashSet<Node>> outputNode = this.getDummyCartesianProduct(inputNodes);
            for (HashSet<Node> h: outputNode){
                PNode p1 = this.getClosed(closedNodes, h);
                boolean isNew = false;
                //System.out.println(p1);
                if (p1 == null){
                    p1 = new PNode();
                    for (Node n: h){
                        p1.getNodes().add(n);
                        p1.setInvocationCost(p1.getInvocationCost() + n.getInvocationCost());
                        p1.setAvailability(p1.getAvailability() * n.getAvailability());
                        p1.setReliability(p1.getReliability()* n.getReliabilty());
                        p1.setMuRT(p1.getMuRT() + n.getMuRT());
                        p1.setSigmaRT(p1.getSigmaRT() + n.getSigmaRT());//variance calculation
                        p1.setLabel(n.getLabel()); 
                        isNew = true;
                        //System.out.println(n.getLabel() + "++++ " + n.isDummy() + " " + pQ.size() + " ");
                    }
                                       
                }
                double tempRe = newPNode.getCumulativeReliability()* p1.getReliability();
                double tempA = newPNode.getCumulativeAvailability() * p1.getAvailability();
                double tempR = 0;
                //if (tempRe >= alphaReliability && tempA >= alphaAvailability && tempR <= alphaResponseTime){
                                           
                    if (p1.getCumulativeCost() > (newPNode.getCumulativeCost() + p1.getInvocationCost()) ){
                        p1.setCumulativeCost(newPNode.getCumulativeCost() + p1.getInvocationCost() + p1.getLabel());
                        p1.setTrueCost(newPNode.getTrueCost() + p1.getInvocationCost());
                        p1.setCumulativeAvailability(tempA);
                        p1.setCumulativeReliability(tempRe);
                        p1.setCumulativeMuRT(newPNode.getCumulativeMuRT() + p1.getMuRT());
                        p1.setCumulativeSigmaRT(newPNode.getCumulativeSigmaRT() + p1.getSigmaRT());
                        if (isNew)
                            pQ.add(p1); 

                    }    
                //}
                                
                str = p1.getLabel() + "";
                if (closedNodes.get(str) == null)
                    closedNodes.put(str, new ArrayList<>());
                closedNodes.get(str).add(p1);
                
            }
            //if (flag)break;
            
        }
                
    }
    
    private int countNodesInDependencyGraph(HashSet<Node> nodes, Node n, int count){
        for (Instance i: n.getOutput().keySet())
            for (Node n1: n.getOutput().get(i))
                if (!nodes.contains(n1)){
                    nodes.add(n1);
                    count++;
                    count = countNodesInDependencyGraph(nodes, n1, count);
                }
        return count;
    }
    
    public void countNodesInDependencyGraph(){
        HashSet<Node> nodes = new HashSet<>();
        int count = 1;
        nodes.add(startNode);
        System.out.println(countNodesInDependencyGraph(nodes, startNode, count));
    }
    
    public void localOptimalAlgo(){
        ArrayList<PNode> pQ = new ArrayList<>();
        HashMap<String, ArrayList<PNode>> closedNodes = new HashMap<>();
        PNode p = new PNode();
        p.setCumulativeCost(0);        
        p.getNodes().add(endNode);
        p.setLabel(endNode.getLabel());
        pQ.add(p);
        
        while (!pQ.isEmpty()){
            boolean isComplete = false;
            PNode newPNode = pQ.remove(0); 
            if (newPNode.getNodes().size() == 1 && newPNode.getNodes().get(0) == startNode){
                    //double tempR = newPNode.getCumulativeMuRT() + 
                      //      PHI_INVERSE_EIGHTY_PERCENT * Math.pow(newPNode.getCumulativeSigmaRT(), 0.5);
                    //System.out.println(newPNode.getTrueCost() + " " + newPNode.getCumulativeAvailability()
                      //      + " " + newPNode.getCumulativeReliability() + " " + tempR);
                    break;
            } 
            ArrayList<ArrayList<Node>> inputNodes = new ArrayList<>();
            HashMap<HashSet<Node>, ArrayList<Node>> temp = new HashMap<>();
            HashSet<Instance> instanceSet = new HashSet<>();
            for (Node n: newPNode.getNodes()){
                for (Instance i: n.getInput().keySet()){
                    if (instanceSet.contains(i))continue;
                    instanceSet.add(i);
                    /*Put the nodes in a hashmap*/
                    HashSet<Node> h2 = new HashSet<>();
                    for (Node n1: n.getInput().get(i))
                        h2.add(n1);                
                    boolean exist = true;
                    for (HashSet<Node> h1: temp.keySet()){
                        if (h1.size() >= h2.size()){                                                    
                            for (Node n1: h2){
                                if (!h1.contains(n1)){
                                    exist = false;
                                    break;
                                }
                            }
                            if (exist){
                                inputNodes.remove(temp.get(h1));
                                inputNodes.add(n.getInput().get(i));
                                break;
                            }
                        }
                        else{                         
                            for (Node n1: h1){
                                if (!h2.contains(n1)){
                                    exist = false;
                                    break;
                                }
                            }
                            if  (exist){
                                break;
                            }
                        }
                    }
                    if (temp.isEmpty() || !exist){
                        temp.put(h2, n.getInput().get(i));
                        inputNodes.add(n.getInput().get(i));
                    }
                }
            }           
            ArrayList<HashSet<Node>> outputNode = this.getDummyCartesianProduct(inputNodes);
            TreeMap<Integer, ArrayList<HashSet<Node>>> sizeMap = new TreeMap<>();            
            for (HashSet<Node> h : outputNode){
                int length = 0;
                for (Node n: h)
                    length += n.getInvocationCost();
                if (sizeMap.get(length) == null)
                    sizeMap.put(length, new ArrayList<HashSet<Node>>());
                sizeMap.get(length).add(h);
            }
            isComplete = false;
            PNode pn = null;
            boolean flag1 = true;
            for (Integer i: sizeMap.keySet()){
                for (HashSet<Node> h: sizeMap.get(i)){
                    PNode p1 = new PNode();
                    for (Node n: h){
                        p1.getNodes().add(n);
                        p1.setInvocationCost(p1.getInvocationCost() + n.getInvocationCost());
                        p1.setAvailability(p1.getAvailability() * n.getAvailability());
                        p1.setReliability(p1.getReliability()* n.getReliabilty());
                        p1.setMuRT(p1.getMuRT() + n.getMuRT());
                        p1.setSigmaRT(p1.getSigmaRT() + n.getSigmaRT());//variance calculation
                        p1.setLabel(n.getLabel());                         
                    }
                    if (flag1){
                        pn = p1;
                        flag1 = false;
                    }
                    /*
                    double tempRe = newPNode.getCumulativeReliability()* p1.getReliability();
                    double tempA = newPNode.getCumulativeAvailability() * p1.getAvailability();
                    double tempR = (newPNode.getCumulativeMuRT() + p1.getMuRT()) + 
                            PHI_INVERSE_EIGHTY_PERCENT * Math.pow((newPNode.getCumulativeSigmaRT() + p1.getSigmaRT()), 0.5);
                    */
                    //if (tempRe >= alphaReliability && tempA >= alphaAvailability && tempR <= alphaResponseTime){
                    
                    double tempR = p1.getMuRT() + Math.pow(p1.getSigmaRT(), 0.5);
                    /*if (p1.getReliability() >= (Math.pow(alphaReliability, (double)(1.0 / ((double)endNode.getLabel())))) 
                            && p1.getAvailability() >= (Math.pow(alphaAvailability, (double)(1.0 / ((double)endNode.getLabel())))) 
                            && tempR <= (alphaResponseTime / endNode.getLabel())){*/
                        p1.setCumulativeCost(newPNode.getCumulativeCost() + p1.getInvocationCost() + p1.getLabel());
                        p1.setTrueCost(newPNode.getTrueCost() + p1.getInvocationCost());
                        p1.setCumulativeAvailability(newPNode.getCumulativeAvailability() * p1.getAvailability());
                        p1.setCumulativeReliability(newPNode.getCumulativeReliability()* p1.getReliability());
                        p1.setCumulativeMuRT(newPNode.getCumulativeMuRT() + p1.getMuRT());
                        p1.setCumulativeSigmaRT(newPNode.getCumulativeSigmaRT() + p1.getSigmaRT());
                        pQ.add(p1);
                        isComplete = true;
                        break;
                 //   }                    
                }
                if (isComplete)break;
            } 
            if (!isComplete){
                pn.setCumulativeCost(newPNode.getCumulativeCost() + pn.getInvocationCost() + pn.getLabel());
                pn.setTrueCost(newPNode.getTrueCost() + pn.getInvocationCost());
                pn.setCumulativeAvailability(newPNode.getCumulativeAvailability() * pn.getAvailability());
                pn.setCumulativeReliability(newPNode.getCumulativeReliability()* pn.getReliability());
                pn.setCumulativeMuRT(newPNode.getCumulativeMuRT() + pn.getMuRT());
                pn.setCumulativeSigmaRT(newPNode.getCumulativeSigmaRT() + pn.getSigmaRT());
                pQ.add(pn);
            }
        }                
    }
        
    public void formulateILPInFile(String file, boolean isUnknown){
        try {
            PrintWriter writer = null;
            writer = new PrintWriter(file, "UTF-8");
            this.generateVariables(endNode);
            //System.out.println(vertextVariableMap.size() + edgeVariableList.size());
            ArrayList<Integer> constraints = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            for (Node n: vertextVariableMap.keySet())
                constraints.set(vertextVariableMap.get(n).getCount(), n.getInvocationCost());
            String str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));          
            
            constraints = new ArrayList<>();
            
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            //First constraint Ns = 1
            constraints.set(vertextVariableMap.get(startNode).getCount(), 1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            constraints.clear();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            //Second constraint Ne = 1
            constraints.set(vertextVariableMap.get(endNode).getCount(), 1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //reliability time constraints
            ArrayList<Double> constraints1 = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints1.add(0.0);
            for (Node n: vertextVariableMap.keySet())
                constraints1.set(vertextVariableMap.get(n).getCount(), Math.log((n.getReliabilty())));
            
            str = "";
            for (Double i: constraints1)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //************************************************************************************************************************
            //mu response time constraints
            constraints = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            for (Node n: vertextVariableMap.keySet())
                constraints.set(vertextVariableMap.get(n).getCount(), n.getMuRT());
            constraints.set(vertextVariableMap.size() + edgeVariableList.size(), -1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //variance response time constraints
            constraints = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints.add(0);
            for (Node n: vertextVariableMap.keySet())
                constraints.set(vertextVariableMap.get(n).getCount(), n.getSigmaRT());
            constraints.set(vertextVariableMap.size() + edgeVariableList.size() + 1, -1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //approximation constraints
            constraints1 = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints1.add(0.0);
            constraints1.set(vertextVariableMap.size() + edgeVariableList.size() + 2, 1.0);
            constraints1.set(vertextVariableMap.size() + edgeVariableList.size() + 1, -0.005);
            str = "";
            for (Double i: constraints1)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            //************************************************************************************************************************
            
            //availability time constraints
            constraints1.clear();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                constraints1.add(0.0);
            for (Node n: vertextVariableMap.keySet())
                constraints1.set(vertextVariableMap.get(n).getCount(), Math.log((n.getAvailability())));
            
            str = "";
            for (Double i: constraints1)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //If each node gets selected then its outputs should be available
            for (EdgeVariable e: edgeVariableList){
                constraints.clear();
                for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                    constraints.add(0);
                constraints.set(vertextVariableMap.size() + e.getCount(), 1);
                constraints.set(vertextVariableMap.get(e.getInputNode()).getCount(), -1);
                str = "";
                for (Integer i: constraints)
                    str += i + ",";
                writer.println(str.substring(0, str.length() - 1));
            }            
                
            //A node can be selected if all its inputs are available
            for (Node n: vertextVariableMap.keySet()){
                if (n == startNode)continue;
                
                for (Instance in: n.getInput().keySet()){
                    constraints.clear();
                    for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size() + 3); i++)
                        constraints.add(0);
                    for (EdgeVariable e: edgeVariableList){
                        if (e.getOutputNode() == n && e.getOutputInstance() == in){
                            constraints.set(vertextVariableMap.size() + e.getCount(), -1);
                        }
                    } 
                    constraints.set(vertextVariableMap.get(n).getCount(), 1);
                    str = "";
                    for (Integer i: constraints)
                        str += i + ",";
                    writer.println(str.substring(0, str.length() - 1));
                }            
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ProcessComposition.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void refineRT(){
        ArrayList<Node> prevLevelNodes = new ArrayList<>();
        ArrayList<Node> currentLevelNodes;
        System.out.println(services.size());
        int count = 0;
        services.clear();
        prevLevelNodes.add(endNode);
        while (true){
            currentLevelNodes = new ArrayList<>();
            for (Node n: prevLevelNodes){
                if (!n.isDummy()){
                    count++;
                    //System.out.println(n.getService().getSubServices().size());
                    for (AbstractService as: n.getService().getSubServices()){
                        //System.out.println("Size: " + as.getAbstractServices().get(0).getSubServices().size());
                        services.add(as.getAbstractServices().get(0));
                    }
                }
                for (Node n1: n.getTraceRT()){
                    currentLevelNodes.add(n1);
                }
            }
            if (currentLevelNodes.isEmpty())break;
            prevLevelNodes = currentLevelNodes;
        }
        System.out.println(services.size() + " " + count);
        this.generateDependencyGraph();
        this.findOptimalResponseTime();
    }
    
    public void refineTR(){
        //ArrayList<AbstractService> allServices = new ArrayList<>();
        ArrayList<Node> prevLevelNodes = new ArrayList<>();
        ArrayList<Node> currentLevelNodes;
        services.clear();
        prevLevelNodes.add(endNode);
        while (true){
            currentLevelNodes = new ArrayList<>();
            for (Node n: prevLevelNodes){
                //System.out.println(n.isDummy());
                if (!n.isDummy()){
                    //System.out.println(n.getService().getSubServices().size());
                    for (AbstractService as: n.getService().getSubServices())
                        services.add(as.getAbstractServices().get(0));
                }
                for (Node n1: n.getTraceTR()){
                    currentLevelNodes.add(n1);
                }
            }
            if (currentLevelNodes.isEmpty())break;
            prevLevelNodes = currentLevelNodes;
        }
        this.generateDependencyGraph();
        //this.findOptimalResponseTime();
        this.findOptimalThroughput();
    }
    
    public void process(){
        this.memoryIntencive = false;
        long s = System.currentTimeMillis();
        //this.connectGraph();
        //this.findOptimalThroughput();
        this.generateDependencyGraph();
        long e = System.currentTimeMillis();
        System.out.println("Time required to construct dependency graph: " + (e - s));
        this.countNodesInDependencyGraph();
        this.subOptimalAlgo();
        //this.findOurCaseResponseTime();
        //this.findOurCaseReliability();
        //this.countNodesInDependencyGraph();
        //this.WriteLPFile(file, isUnknown);
        /*
        System.out.println("Sub-optimal Solution: ");
        s = System.currentTimeMillis();
        this.subOptimalAlgo();
        e = System.currentTimeMillis();
        System.out.println("Time required: " + (e - s));
        
        System.out.println("Local Solution: ");
        s = System.currentTimeMillis();
        this.localOptimalAlgo();
        e = System.currentTimeMillis();
        System.out.println("Time required: " + (e - s));
        this.formulateILPInFile(file, isUnknown);*/
        
        //this.findOptimalResponseTime();
        /*this.findOurCaseResponseTime();
        this.findOurCaseProbabilisticResponseTime();
        
        this.clean();
        applyAnyTimeAlgoResponseTime(1);
        this.clean();
        applyAnyTimeAlgoResponseTime(2);
        this.clean();
        applyAnyTimeAlgoResponseTime(3);
        this.clean();
        applyAnyTimeAlgoResponseTime(4);
        this.clean();
        applyAnyTimeAlgoResponseTime(5);
        this.clean();
        */
        this.findOptimalThroughput();
        //this.refinement();
        /*this.findOurCaseThroughput();
        this.findOurCaseProbabilisticThroughput();
        
        this.clean();
        applyAnyTimeAlgoThroughput(1);
        this.clean();
        applyAnyTimeAlgoThroughput(2);
        this.clean();
        applyAnyTimeAlgoThroughput(3);
        this.clean();
        applyAnyTimeAlgoThroughput(4);
        this.clean();
        applyAnyTimeAlgoThroughput(5);
        this.clean();
        applyAnyTimeAlgoThroughput(6);
        this.clean();
        applyAnyTimeAlgoThroughput(7);
        this.clean();
        *//*
        this.findOurCaseMultipleQoSUsingScore();
        this.clean();
        this.findOurCaseMultipleQoSUsingSelection();
        this.clean();
        modifyParam(0.25, 0.25, 0.25, 0.25);
        this.clean();
        this.findOptimalResponseTime();
        this.findOptimalThroughput();
        Random r = new Random();*/
      /*  for (int i = 0; i < 10; i++){            
            double d1 = r.nextDouble();
            double d2 = r.nextDouble();
            double d3 = r.nextDouble();
            double d4 = r.nextDouble();
            double d = d1 + d2 + d3 + d4;
            modifyParam(d1/d, d2/d, d3/d, d4/d);
        }*/
            
    }
    
}


 
