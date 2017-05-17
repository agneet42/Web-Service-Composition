/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;


import java.util.ArrayList;


public class PNode{
    private ArrayList<Node> nodes = new ArrayList();
    private int invocationCost = 0;
    private int muRT = 0;
    private int sigmaRT = 0;
    private double muT = 0;
    private double sigmaT = 0;
    private double reliability = 1;
    private double availability = 1;
    private int cumulativeCost = 1000000;
    private int cumulativeMuRT = 0;
    private int cumulativeSigmaRT = 0;
    private double cumulativeReliability = 1;
    private double cumulativeAvailability = 1;
    private int trueCost = 0;
    private int label = 0;

    public int getCumulativeMuRT() {
        return cumulativeMuRT;
    }

    public int getTrueCost() {
        return trueCost;
    }

    public void setTrueCost(int trueCost) {
        this.trueCost = trueCost;
    }

    
    public void setCumulativeMuRT(int cumulativeMuRT) {
        this.cumulativeMuRT = cumulativeMuRT;
    }

    public int getCumulativeSigmaRT() {
        return cumulativeSigmaRT;
    }

    public void setCumulativeSigmaRT(int cumulativeSigmaRT) {
        this.cumulativeSigmaRT = cumulativeSigmaRT;
    }

    public double getCumulativeReliability() {
        return cumulativeReliability;
    }

    public void setCumulativeReliability(double cumulativeReliability) {
        this.cumulativeReliability = cumulativeReliability;
    }

    public double getCumulativeAvailability() {
        return cumulativeAvailability;
    }

    public void setCumulativeAvailability(double cumulativeAvailability) {
        this.cumulativeAvailability = cumulativeAvailability;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public int getInvocationCost() {
        return invocationCost;
    }

    public void setInvocationCost(int invocationCost) {
        this.invocationCost = invocationCost;
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

    public double getMuT() {
        return muT;
    }

    public void setMuT(double muT) {
        this.muT = muT;
    }

    public double getSigmaT() {
        return sigmaT;
    }

    public void setSigmaT(double sigmaT) {
        this.sigmaT = sigmaT;
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
    
    

    /*@Override
    public int compare(Double o1, Double o2) {
        return (o1 > o2)? -1 : 1;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    public int getCumulativeCost() {
        return cumulativeCost;
    }

    public void setCumulativeCost(int cumulativeCost) {
        this.cumulativeCost = cumulativeCost;
    }
    
    
}
/*
 public void formulateILP(String file, boolean isUnknown){
        try {
            PrintWriter writer = null;
            this.generateVariables(endNode);
            writer = new PrintWriter(file, "UTF-8");
            System.out.println(vertextVariableMap.size() + edgeVariableList.size());
            //this.generateObjective();
            ArrayList<Integer> constraints = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
                constraints.add(0);
            for (Node n: vertextVariableMap.keySet())
                constraints.set(vertextVariableMap.get(n).getCount(), n.getInvocationCost());
            String str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            constraints = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
                constraints.add(0);
            //First constraint Ns = 1
            constraints.set(vertextVariableMap.get(startNode).getCount(), 1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            constraints.clear();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
                constraints.add(0);
            //Second constraint Ne = 1
            constraints.set(vertextVariableMap.get(endNode).getCount(), 1);
            str = "";
            for (Integer i: constraints)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //reliability time constraints
            ArrayList<Double> constraints1 = new ArrayList<>();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
                constraints1.add(0.0);
            for (Node n: vertextVariableMap.keySet())
                constraints1.set(vertextVariableMap.get(n).getCount(), Math.log((n.getReliabilty())));
            
            str = "";
            for (Double i: constraints1)
                str += i + ",";
            writer.println(str.substring(0, str.length() - 1));
            
            //availability time constraints
            constraints1.clear();
            for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
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
                for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
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
                    for (int i = 0; i < (vertextVariableMap.size() + edgeVariableList.size()); i++)
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
            
            
            
            //for (EdgeVariable e: edgeVariableList)
                //System.out.println(e.getCount());
            //System.out.println(vertextVariableMap.size() + " " + edgeVariableList.size());
            //System.out.println("x" + vertextVariableMap.get(startNode).getCount() + " = 1");
            //System.out.println("x" + vertextVariableMap.get(endNode).getCount() + " = 1");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ProcessComposition.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
 */
