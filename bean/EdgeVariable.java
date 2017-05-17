/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;

public class EdgeVariable {
    private Node inputNode;
    private Node outputNode;
    private Instance inputInstance;
    private Instance outputInstance;
    private int count;

    public EdgeVariable() {
    }

    public EdgeVariable(Node inputNode, Node outputNode, Instance inputInstance) {
        this.inputNode = inputNode;
        this.outputNode = outputNode;
        this.inputInstance = inputInstance;
    }

    public EdgeVariable(Node inputNode, Node outputNode, Instance inputInstance, Instance outputInstance, int count) {
        this.inputNode = inputNode;
        this.outputNode = outputNode;
        this.inputInstance = inputInstance;
        this.outputInstance = outputInstance;
        this.count = count;
    }

    public EdgeVariable(Node inputNode, Node outputNode, Instance outputInstance, int count) {
        this.inputNode = inputNode;
        this.outputNode = outputNode;
        this.outputInstance = outputInstance;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Node getInputNode() {
        return inputNode;
    }

    public void setInputNode(Node inputNode) {
        this.inputNode = inputNode;
    }

    public Node getOutputNode() {
        return outputNode;
    }

    public void setOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }

    public Instance getInputInstance() {
        return inputInstance;
    }

    public void setInputInstance(Instance inputInstance) {
        this.inputInstance = inputInstance;
    }

    public Instance getOutputInstance() {
        return outputInstance;
    }

    public void setOutputInstance(Instance outputInstance) {
        this.outputInstance = outputInstance;
    }
}
