/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import wsc08composition.bean.Concept;
import wsc08composition.bean.Instance;
import wsc08composition.bean.Service;


public class ReadFile {
    private final ArrayList<Service> services = new ArrayList<>();    
    private final HashMap<String, Service> nameToServiceMap = new HashMap<>();
    private final ArrayList<Concept> concepts = new ArrayList<>();
    private final HashMap<String, Instance> instanceMap = new HashMap<>();
    private final ArrayList<Instance> inputInstances = new ArrayList<>();
    private final ArrayList<Instance> outputInstances = new ArrayList<>();

    public ArrayList<Service> getServices() {
        return services;
    }

    public ArrayList<Concept> getConcepts() {
        return concepts;
    }

    public HashMap<String, Instance> getInstanceMap() {
        return instanceMap;
    }

    public ArrayList<Instance> getInputInstances() {
        return inputInstances;
    }

    public ArrayList<Instance> getOutputInstances() {
        return outputInstances;
    }
    
    public void readProblemFile(String f1){
        try {
            File fXmlFile = new File(f1);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = ((Element)((doc.getElementsByTagName("task")).item(0))).getChildNodes();
            //System.out.println(((Element)(nList.item(0))).getTagName() + " " + ((Element)(nList.item(1))).getTagName());
            Node providedNode = nList.item(0);
            Element e1Element = (Element) providedNode;
            Node wantedNode = nList.item(1);
            Element e2Element = (Element) wantedNode;
            
            NodeList inputs = e1Element.getElementsByTagName("instance");
            for (int i = 0; i < inputs.getLength(); i++){
                Node n = inputs.item(i);
                Element e = (Element)n;
                inputInstances.add(instanceMap.get(e.getAttribute("name")));
                //System.out.println("Input : " + e.getAttribute("name"));
            }
            
            NodeList outputs = e2Element.getElementsByTagName("instance");
            for (int i = 0; i < outputs.getLength(); i++){
                Node n = outputs.item(i);
                Element e = (Element)n;
                outputInstances.add(instanceMap.get(e.getAttribute("name")));
                //System.out.println("Output : " + e.getAttribute("name"));
            }
        }
        catch(ParserConfigurationException | SAXException | IOException e){
            System.out.println(e.toString());
        }
    }
    
    public void readServiceFile(String f1){        
        try {
            File fXmlFile = new File(f1);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("service");
            for (int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
		Element eElement = (Element) nNode;
                Service s = new Service();
                s.setName(eElement.getAttribute("name"));
                NodeList input = eElement.getElementsByTagName("inputs");
                Element inputElements = (Element) input.item(0);
                //System.out.println(s.getName());
                NodeList inputList = inputElements.getElementsByTagName("instance");
                //System.out.println(inputList.getLength());
                for (int j = 0; j < inputList.getLength(); j++){
                    Node inputNode = inputList.item(j);
		    Element inputElement = (Element) inputNode;
                    s.getInput().add(instanceMap.get(inputElement.getAttribute("name")));
                    s.getInputConcept().add(instanceMap.get(inputElement.getAttribute("name")).getConcept());
                    //System.out.println("Input: " + inputElement.getAttribute("name"));
                }
                
                NodeList output = eElement.getElementsByTagName("outputs");
                Element outputElements = (Element) output.item(0);
                
                NodeList outputList = outputElements.getElementsByTagName("instance");
                for (int j = 0; j < outputList.getLength(); j++){
                    Node outputNode = outputList.item(j);
		    Element outputElement = (Element) outputNode;
                    s.getOutput().add(instanceMap.get(outputElement.getAttribute("name")));
                    s.getOutputConcept().add(instanceMap.get(outputElement.getAttribute("name")).getConcept());
                    //System.out.println("Output: " + outputElement.getAttribute("name"));
                    //System.out.println("Output : " + n.getName());
                }
                nameToServiceMap.put(s.getName(), s);
		services.add(s);
            }
        }
        catch(ParserConfigurationException | SAXException | IOException e){
            System.out.println(e.toString());
        }
    }
    
    private void recursiveConceptReading(Concept c, Element e){
        concepts.add(c);
        NodeList subList = e.getChildNodes();
        for (int i = 0; i < subList.getLength(); i++){
            Element ep = (Element)(subList.item(i));
            if (ep.getTagName().equals("instance")){
                Instance in = new Instance();
                in.setName(ep.getAttribute("name"));
                in.setConcept(c);
                instanceMap.put(in.getName(), in);
            }
            else if (ep.getTagName().equals("concept")){
                Concept c1 = new Concept();
                c1.setName(ep.getAttribute("name"));
                //System.out.println(c.getName()+ "  " + c1.getName());
                c1.setSuperConcept(c);
                c.getSubSet().add(c1);
                recursiveConceptReading(c1, ep);                
            }
        }
    }
    
    public void readConceptFile(String f1){
        try {
            File fXmlFile = new File(f1);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("concept");
            Element eElement = (Element) (nList.item(0));
            Concept c = new Concept();
            c.setName(eElement.getAttribute("name"));
            this.recursiveConceptReading(c, eElement);
            //System.out.println(eElement.getAttribute("name"));
        }
        catch(ParserConfigurationException | SAXException | IOException e){
            System.out.println(e.toString());
        }
    }
    
    public void readSLAParamFile (String f1){
        try {
            File fXmlFile = new File(f1);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("ServiceLevelObjective");
            //System.out.println(nList.getLength() + " " + nameToServiceMap.size());
            for (int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
		Element eElement = (Element) nNode;       
                String paramName = eElement.getElementsByTagName("SLAParameter").item(0).getChildNodes().item(0).getNodeValue();
                //System.out.println(paramName);
                String paramValue = eElement.getElementsByTagName("Value").item(0).getChildNodes().item(0).getNodeValue();
                //System.out.println(paramValue);
                String parameter = paramName.substring(12);                
                if (parameter.charAt(0) == 'R'){
                    String serviceName = parameter.substring(12);
                    if (nameToServiceMap.keySet().contains(serviceName))
                        nameToServiceMap.get(serviceName).setResponseTime(Integer.parseInt(paramValue));
                    //System.out.println(nameToServiceMap.keySet().contains(serviceName));
                }
                if (parameter.charAt(0) == 'T'){
                    String serviceName = parameter.substring(10);
                    //System.out.println(serviceName);
                    if (nameToServiceMap.keySet().contains(serviceName))
                        nameToServiceMap.get(serviceName).setThroughput(Integer.parseInt(paramValue));
                    //System.out.println(nameToServiceMap.keySet().contains(serviceName));
                }
                
            }
        }
        catch(ParserConfigurationException | SAXException | IOException e){
            System.out.println(e.toString());
        }
    }

}
