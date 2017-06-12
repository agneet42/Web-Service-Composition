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

import wsc08composition.bean.Concept;
import wsc08composition.bean.EdgeVariable;
import wsc08composition.bean.Instance;
import wsc08composition.bean.Service;
import wsc08composition.bean.Node;
import wsc08composition.bean.PNode;
import wsc08composition.bean.AbstractService;
import wsc08composition.bean.VertextVariable;

public class MyCode{
    private ArrayList<Service> ServiceList = new ArrayList<Service>();  // ArrayList to store all services
    private ArrayList<ArrayList<Instance>> inputInstances = new ArrayList<ArrayList<Instance>>(); // ArrayList of all ArrayLists to store input instances of each Service
    public void getServices1(){
        ReadFile rf = new ReadFile();
        rf.readServiceFile("/home/agneet42/Documents/ISI/satsolver101/WSC08Composition/Testset01/services.xml"); // calling readServiceFile
        ServiceList = rf.getServices(); // storing all services of TestSet01 in ServiceList
        int f = 0;
        for(Service iterService : ServiceList){ // outer loop
            ArrayList<Service> OutputService = new ArrayList<Service>();
            inputInstances.add(iterService.getInput());// getting inputinstance of iterService
            OutputService.add(iterService);
            for(Service iterService1 : ServiceList){ // inner loop
                if(iterService != iterService1){ // checking for equality. if equal, ignoring
                   inputInstances.add(iterService1.getInput()); // getting inputinstance of iterService1
                   for(Instance temp : inputInstances.get(0)){  // iterating over 0-index of inputInstance which is the list of all inputInstance of iterService
                       for(Instance temp1 : inputInstances.get(1)){ // iterating over 1-index of inputInstance which is the list of all inputInstance of iterService
                           boolean check = Check(temp,temp1); // calling function Check to check for concepts and superconcepts
                           if(check==false){
                               f=1;
                               break;
                            }
                        }
                        if(f==1){
                            f=0;break;
                        }
                    }
                }
                if(f==0){
                    OutputService.add(iterService1);
                }
            }
            if(OutputService.size()!=1){
                for(Service print1 : OutputService){
                    System.out.println(print1);       //the only change needed in the code to print the required attribute of the Service
                }
            }
        }
}

    public boolean Check(Instance i1, Instance i2){
     Concept a = i1.getConcept();
     while (true){
            if (a == null)break;
            if (a == i2.getConcept())
                return true;
            a = a.getSuperConcept();
        }
     return false;
    }
}
        
        
      




