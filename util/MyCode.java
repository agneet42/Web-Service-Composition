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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

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
    private ArrayList<ArrayList<Instance>> inputInstances = new ArrayList<ArrayList<Instance>>();// ArrayList of all ArrayLists to store input instances of each Service
    public void getServices1(){
        ReadFile rf = new ReadFile();
        rf.readConceptFile("Testset05ma/taxonomy.xml");
        rf.readServiceFile("Testset05/services.xml"); // calling readServiceFile
        ServiceList = rf.getServices(); // storing all services of TestSet01 in ServiceList
        int f = 0;
        for(Service iterService : ServiceList){ // outer loop
            ArrayList<Service> OutputService = new ArrayList<Service>();
            inputInstances.add(iterService.getInput());// getting inputinstance of iterService
            OutputService.add(iterService);
            for(Service iterService1 : ServiceList){ // inner loop
                if(iterService != iterService1){ // checking for equality. if equal, ignoring
                   inputInstances.add(iterService1.getInput()); // getting inputinstance 
                   for(Instance temp : inputInstances.get(0)){ // iterating over 0-index of inputInstance which is the list of all inputInstance of iterService
                       for(Instance temp1 : inputInstances.get(1)){ // iterating over 1-index of inputInstance which is the list of all inputInstance of iterService
                           boolean check = Check(temp,temp1); // calling function Check to check for concepts and superconcepts
                           if(check==true){
                               f=f+1;
                                }
                            }
                        }
                    }
                else
                    break;
                // System.out.print(inputInstances.get(0).get(0).getName());
                 if(f >= inputInstances.get(1).size()){
                    OutputService.add(iterService1);
                    f=0;
                }
                inputInstances.remove(1);
            }
            if(OutputService.size()!=1){
                System.out.print(OutputService.get(0).getName() + " : ");
                for(Service print1 : OutputService){
                    System.out.print(print1.getName()+" ");       //the only change needed in the code to print the required attribute of the Service
                } 
                f=0;
                System.out.println();
            }
            inputInstances.remove(0);
            
          
            
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
    public static void main(String args[])
    {
        MyCode obj = new MyCode();
        obj.getServices1();
    }
}
        
        
      

