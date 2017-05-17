/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import wsc08composition.bean.AbstractService;
import wsc08composition.bean.Concept;
import wsc08composition.bean.Instance;
import wsc08composition.bean.Node;
import wsc08composition.bean.Service;


public class Abstraction {
    private final HashSet<Node> check = new HashSet<>();
    private final ArrayList<Service> services;
    private final ArrayList<Concept> concepts;
    private final HashMap<String, Instance> instanceMap;
    private final ArrayList<Instance> inputInstances;
    private final ArrayList<Instance> outputInstances;
    private final ArrayList<AbstractService> l0AbstractService = new ArrayList<>();
    private final ArrayList<AbstractService> l1AbstractService = new ArrayList<>();
    private final ArrayList<AbstractService> l2AbstractService = new ArrayList<>();
    private final ArrayList<AbstractService> l3AbstractService = new ArrayList<>();
    private final ArrayList<AbstractService> l5AbstractService = new ArrayList<>();
        
    public Abstraction(String serviceFile, String taxonomyFile, String problemFile){
        ReadFile rf = new ReadFile();
        rf.readConceptFile(taxonomyFile);
        rf.readServiceFile(serviceFile);
        rf.readProblemFile(problemFile);
        services = rf.getServices();
        concepts = rf.getConcepts();
        instanceMap = rf.getInstanceMap();
        inputInstances = rf.getInputInstances();
        outputInstances = rf.getOutputInstances();
    }
    
    public Abstraction(String serviceFile, String taxonomyFile, String problemFile, String slaFile){
        ReadFile rf = new ReadFile();
        rf.readConceptFile(taxonomyFile);
        rf.readServiceFile(serviceFile);
        rf.readProblemFile(problemFile);
        rf.readSLAParamFile(slaFile);
        services = rf.getServices();
        concepts = rf.getConcepts();
        instanceMap = rf.getInstanceMap();
        inputInstances = rf.getInputInstances();
        outputInstances = rf.getOutputInstances();
    }
    
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
    
    private ArrayList<AbstractService> getServices(ArrayList<Instance> inputs, ArrayList<AbstractService> serviceSet, 
            HashMap<AbstractService, AbstractService> map4to5){
        ArrayList<AbstractService> serviceList = new ArrayList<>();
        for (AbstractService s: l3AbstractService){
            if (serviceSet.contains(map4to5.get(s)))
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
    
    private void generateLevel0AbstractServices(){
        for (Service s: services){
            AbstractService as = new AbstractService();
            as.getServices().add(s);
            as.setInput(s.getInput());
            as.setOutput(s.getOutput());
            as.setInputConcept(s.getInputConcept());
            as.setOutputConcept(s.getOutputConcept());
            as.setResponseTime(s.getResponseTime());
            as.setReliability(s.getReliability());
            as.setAvailability(s.getAvailability());
            as.setThroughput(s.getThroughput());
            as.setLevel(0);
            l0AbstractService.add(as);
        }
    }
    
    private boolean checkEquivalence(AbstractService service){
        boolean flag = false;
        for (AbstractService as: l1AbstractService){
            flag = true;
            if (as.getInputConcept().size() != service.getInputConcept().size() || 
                    as.getOutputConcept().size() != service.getOutputConcept().size()){
                flag = false;continue;
            }
            for (Concept in: service.getInputConcept()){
                if (!as.getInputConcept().contains(in)){
                    flag = false;break;
                }
            }
            if (!flag)continue;
            for (Concept out: service.getOutputConcept()){
                if (!as.getOutputConcept().contains(out)){
                    flag = false;break;
                }
            }
            if (flag){
                as.getAbstractServices().add(service);
                break;
            }
        }
        return flag;
    }
    
    private static double getMax(ArrayList<Double> list){
        double max = 0;
        for (Double d: list)
            max = max < d ? d : max;
        return max;
    }
    
    private static double getMin(ArrayList<Double> list){
        double min = list.get(0);
        for (Double d: list)
            min = min > d ? d : min;
        return min;
    }
    
    private void assignQoS(AbstractService as){
        if (as.getAbstractServices().size() == 1){
            as.setResponseTime(as.getAbstractServices().get(0).getResponseTime());
            as.setThroughput(as.getAbstractServices().get(0).getThroughput());
            as.setReliability(as.getAbstractServices().get(0).getReliability());
            as.setAvailability(as.getAbstractServices().get(0).getAvailability());
            as.setLatency(as.getAbstractServices().get(0).getLatency());
            as.setInvocationCost(as.getAbstractServices().get(0).getInvocationCost());
            return;
        }
        ArrayList<Double> rts = new ArrayList<>();
        ArrayList<Double> trs = new ArrayList<>();
        ArrayList<Double> res = new ArrayList<>();
        ArrayList<Double> avs = new ArrayList<>();
        ArrayList<Double> lts = new ArrayList<>();
        ArrayList<Double> ics = new ArrayList<>();
        //System.out.println(as.getAbstractServices().size());
        for (AbstractService a: as.getAbstractServices()){
            rts.add(a.getResponseTime());
            trs.add(a.getThroughput());
            res.add(a.getReliability());
            avs.add(a.getAvailability());
            lts.add(a.getLatency());
            ics.add(a.getInvocationCost());
        }
        double maxRT = getMax(rts); double minRT = getMin(rts);
        double maxTR = getMax(trs); double minTR = getMin(trs);
        double maxRE = getMax(res); double minRE = getMin(res);
        double maxAV = getMax(avs); double minAV = getMin(avs);
        double maxLT = getMax(lts); double minLT = getMin(lts);
        double maxIC = getMax(ics); double minIC = getMin(ics);
        double minOfMaxDeviation = 1;
        AbstractService tempAS = as.getAbstractServices().get(0);
        for (AbstractService a: as.getAbstractServices()){
            double nRT = (maxRT - a.getResponseTime()) / (maxRT - minRT);
            double dRT = 1 - nRT;
            double nTR = (a.getThroughput() - minTR) / (maxTR - minTR);
            double dTR = 1 - nTR;
            double nRE = (a.getReliability()- minRE) / (maxRE - minRE);
            double dRE = 1 - nRE;
            double nAV = (a.getAvailability()- minAV) / (maxAV - minAV);
            double dAV = 1 - nAV;
            double nLT = (maxLT - a.getLatency()) / (maxLT - minLT);
            double dLT = 1 - nLT;
            double nIC = (maxIC - a.getInvocationCost()) / (maxIC - minIC);
            double dIC = 1 - nIC;
            double maxN = ((dRT >= dTR) && (dRT >= dRE) && (dRT >= dAV) && (dRT >= dLT) && (dRT >= dIC)) ? dRT :
                    (((dTR >= dRE) && (dTR >= dAV) && (dTR >= dLT) && (dTR >= dIC)) ? dTR :
                    (((dRE >= dAV) && (dRE >= dLT) && (dRE >= dIC)) ? dRE :
                    (((dAV >= dLT) && (dAV >= dIC)) ? dAV :
                    (((dLT >= dIC)) ? dLT : dIC))));
            if (minOfMaxDeviation > maxN){
                minOfMaxDeviation = maxN;
                tempAS = a;
            }
        }
        as.setResponseTime(tempAS.getResponseTime());
        as.setThroughput(tempAS.getThroughput());
        as.setReliability(tempAS.getReliability());
        as.setAvailability(tempAS.getAvailability());
        as.setLatency(tempAS.getLatency());
        as.setInvocationCost(tempAS.getInvocationCost());
        
    }
    
    private void generateLevel1AbstractServices(){
        for (AbstractService as: l0AbstractService){
            if (this.checkEquivalence(as))
                continue;
            AbstractService newAS = new AbstractService();
            newAS.getAbstractServices().add(as);
            newAS.setInput(as.getInput());
            newAS.setOutput(as.getOutput());
            newAS.setInputConcept(as.getInputConcept());
            newAS.setOutputConcept(as.getOutputConcept());
            newAS.setLevel(1);
            l1AbstractService.add(newAS);
        }
        for (AbstractService as: l1AbstractService)
            this.assignQoS(as);
    }
    
    private void checkCorrectness1(){
        int count = 0;
        for (AbstractService as: l1AbstractService)
            count += as.getAbstractServices().size();
        System.out.println(count);
    }
    
    /*******************************************************************************************/
    /*Abstraction in 2nd level*/
    
    private void setDominantRelation(AbstractService service, HashMap<AbstractService, HashSet<AbstractService>> dominance){
        HashSet<AbstractService> dominatedServices = new HashSet<>();
        boolean flag = false, utilize = false;
        for (AbstractService s: dominance.keySet()){
            flag = true;
            if (s.getInputConcept().size() >= service.getInputConcept().size() && 
                    s.getOutputConcept().size() <= service.getOutputConcept().size()){
                /*check: service dominates s*/
                for (Concept in: service.getInputConcept()){
                    if (!s.getInputConcept().contains(in)){
                        flag = false; break;
                    }
                }
                for (Concept out: s.getOutputConcept()){
                    if (!service.getOutputConcept().contains(out)){
                        flag = false; break;
                    }
                }
                if (flag){
                    dominatedServices.add(s);
                    utilize = true;
                }
            }
            else if (s.getInputConcept().size() <= service.getInputConcept().size() && 
                    s.getOutputConcept().size() >= service.getOutputConcept().size()){
                for (Concept in: s.getInputConcept()){
                    if (!service.getInputConcept().contains(in)){
                        flag = false; break;
                    }
                }
                for (Concept out: service.getOutputConcept()){
                    if (!s.getOutputConcept().contains(out)){
                        flag = false; break;
                    }
                }
                if (flag){
                    if (!dominance.get(s).contains(service))
                        dominance.get(s).add(service);utilize = true; break;
                }
            }
        }
        if (!dominatedServices.isEmpty()){
            HashSet<AbstractService> temp = new HashSet<>();
            for (AbstractService s: dominatedServices){
                for (AbstractService s1: dominance.get(s)){
                    if (!temp.contains(s1))
                        temp.add(s1);
                }
                //temp.add(s);
                dominance.remove(s);
            }
            if (!temp.contains(service))
                temp.add(service);
            dominance.put(service, temp);
        }
        else if (!utilize){
            HashSet<AbstractService> temp = new HashSet<>();
            temp.add(service);
            dominance.put(service, temp);
        }
    }
    
    private void generateLevel2AbstractServices(){
        HashMap<AbstractService, HashSet<AbstractService>> dominance = new HashMap<>();
        for (AbstractService as: l1AbstractService){
            this.setDominantRelation(as, dominance);
        }
        for (AbstractService s: dominance.keySet()){
            AbstractService newS = new AbstractService();
            for (AbstractService s1: dominance.get(s))
                newS.getAbstractServices().add(s1);
            //newS.getAbstractServices().add(s);
            newS.setInput(s.getInput());
            newS.setOutput(s.getOutput());
            newS.setInputConcept(s.getInputConcept());
            newS.setOutputConcept(s.getOutputConcept());
            newS.setResponseTime(s.getResponseTime());
            newS.setReliability(s.getReliability());
            newS.setAvailability(s.getAvailability());
            newS.setThroughput(s.getThroughput());
            newS.setLatency(s.getLatency());
            newS.setInvocationCost(s.getInvocationCost());
            newS.setLevel(2);
            l2AbstractService.add(newS);            
        }
    }
    
    private void checkCorrectness2(){
        int count = 0;
        for (AbstractService as: l2AbstractService)
            count += as.getAbstractServices().size();
        System.out.println(count);
    }
    
    /*******************************************************************************************/
    /*Abstraction in 3rd level*/
    
    private boolean checkInputEquivalence(AbstractService service){
        boolean flag = false;
        for (AbstractService as: l3AbstractService){
            flag = true;
            if (as.getInputConcept().size() != service.getInputConcept().size()){
                flag = false;continue;
            }
            for (Concept in: as.getInputConcept()){
                if (!service.getInputConcept().contains(in)){
                    flag = false;break;
                }
            }            
            if (flag){
                as.getAbstractServices().add(service);
                for (Instance out: service.getOutput())
                    as.getOutput().add(out);
                double rs = as.getResponseTime() < service.getResponseTime() ? service.getResponseTime() : as.getResponseTime();
                as.setResponseTime(rs);
                double re = as.getReliability() * service.getReliability();
                as.setReliability(re);
                double av = as.getAvailability() * service.getAvailability();
                as.setAvailability(av);
                double tr = as.getThroughput() > service.getThroughput()? service.getThroughput() : as.getThroughput();                
                as.setThroughput(tr);
                double lt = as.getLatency() < service.getLatency()? service.getLatency() : as.getLatency();
                as.setLatency(lt);
                double ic = as.getInvocationCost() + service.getInvocationCost();
                as.setInvocationCost(ic);
                break;
            }
        }
        return flag;
    }
    
    private void generateLevel3AbstractServices(){
        for (AbstractService as: l2AbstractService){
            if (this.checkInputEquivalence(as))
                continue;
            AbstractService newAS = new AbstractService();
            newAS.getAbstractServices().add(as);
            newAS.setInput(as.getInput());
            newAS.setInputConcept(as.getInputConcept());
            for (AbstractService s: as.getAbstractServices()){
                for (Instance out: s.getOutput()){
                    if (!newAS.getOutput().contains(out)){
                        newAS.getOutput().add(out);
                        newAS.getOutputConcept().add(out.getConcept());
                    }                    
                }            
            }
            newAS.setResponseTime(as.getResponseTime());
            newAS.setReliability(as.getReliability());
            newAS.setAvailability(as.getAvailability());
            newAS.setThroughput(as.getThroughput());
            newAS.setLatency(as.getLatency());
            newAS.setInvocationCost(as.getInvocationCost());
            newAS.setLevel(3);
            l3AbstractService.add(newAS);
        }
    }
    
    private void checkCorrectness3(){
        int count = 0;
        for (AbstractService as: l3AbstractService)
            count += as.getAbstractServices().size();
        System.out.println(count);
    }
    
   
    /*******************************************************************************************/
    /*Abstraction in 5th level*/
    
    private void rConstructAbstractServiceByFusion(HashSet<Instance> activatedInputs, AbstractService l5Service, 
            HashMap<AbstractService, AbstractService> map4to5){
        
        ArrayList<Instance> inputList = new ArrayList<>();
        for (Instance i: activatedInputs)
            inputList.add(i);
        
        ArrayList<AbstractService> activatedServiceList = this.getServices(inputList, l5Service.getSubServices(), map4to5); 
        HashSet<AbstractService> activatedServices = new HashSet<>();
        
        for (AbstractService as: activatedServiceList)
            activatedServices.add(as);
        
        if (activatedServices.isEmpty())
            return;
        
        double rs = 0, tr = l5Service.getThroughput(), re = 1, av = 1, ic = 0, lt = 0;
        for (AbstractService a: activatedServices){
            l5Service.getSubServices().add(map4to5.get(a));
            for (Instance out: a.getOutput()){
                l5Service.getOutput().add(out);
                activatedInputs.add(out);
            }
            
            rs = rs < a.getResponseTime() ? a.getResponseTime() : rs;
            tr = tr > a.getThroughput() ? a.getThroughput() : tr;
            re *= a.getReliability();
            av *= a.getAvailability();
            ic += a.getInvocationCost();
            lt = lt < a.getLatency() ? a.getLatency() : lt;
        }
        l5Service.setResponseTime(l5Service.getResponseTime() + rs);
        l5Service.setThroughput(tr);
        l5Service.setReliability(re);
        l5Service.setAvailability(av);
        l5Service.setInvocationCost(l5Service.getInvocationCost() + ic);
        l5Service.setLatency(l5Service.getLatency() + lt);
            
        this.rConstructAbstractServiceByFusion(activatedInputs, l5Service, map4to5);
    }
    
    private void constructAbstractServiceByFusion(AbstractService l3Service, HashMap<AbstractService, AbstractService> map4to5){
        HashSet<Instance> activatedInputs = new HashSet<>();
        AbstractService l5Service = map4to5.get(l3Service);
        for (Instance out: l3Service.getOutput()){
            activatedInputs.add(out);
            l5Service.getOutput().add(out);
        }
        this.rConstructAbstractServiceByFusion(activatedInputs, l5Service, map4to5);
        //System.out.println("Subservices: " + l5Service.getSubServices().size());
    }
    
    private void generateLevel5AbstractServices(){
        HashMap<AbstractService, AbstractService> map4to5 = new HashMap<>();
        /*key: 3rd level abstract service, value: 5th level abstract service*/
        for (AbstractService as: l3AbstractService){
            AbstractService newAS = new AbstractService();
            newAS.setLevel(5);
            map4to5.put(as, newAS);
            newAS.setInput(as.getInput());
            newAS.setInputConcept(as.getInputConcept());
            newAS.getAbstractServices().add(as);
            newAS.setResponseTime(as.getResponseTime());
            newAS.setThroughput(as.getThroughput());
            newAS.setReliability(as.getReliability());
            newAS.setAvailability(as.getAvailability());
            newAS.setLatency(as.getLatency());
            newAS.setInvocationCost(as.getInvocationCost());
            l5AbstractService.add(newAS);
        }
        for (AbstractService as: l3AbstractService){
            this.constructAbstractServiceByFusion(as, map4to5);
        }
    }
    
    public void check(){
        //System.out.println(this.services.size() + " " + this.concepts.size());
        this.generateLevel0AbstractServices();
        this.generateLevel1AbstractServices();
        System.out.println(l1AbstractService.size());
        this.checkCorrectness1();
        this.generateLevel2AbstractServices();
        System.out.println(l1AbstractService.size());
        this.checkCorrectness2();
        this.generateLevel3AbstractServices();
        System.out.println(l1AbstractService.size());
        this.checkCorrectness3();
        this.generateLevel5AbstractServices();
        System.out.println("Start processing");
        this.process();
    }
    
    public void process(){
        new ProcessComposition(l0AbstractService, concepts, instanceMap, inputInstances, outputInstances).process();
        ProcessComposition p = new ProcessComposition(l5AbstractService, concepts, instanceMap, inputInstances, outputInstances);
        p.process();
        //p.refineTR();
        //p.refineTR();
        
        
    }
    
}
