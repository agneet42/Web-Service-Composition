/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wsc08composition.bean;


public class VertextVariable {
    private Node node;
    private int count;

    public VertextVariable() {
    }

    public VertextVariable(Node node) {
        this.node = node;
    }

    public VertextVariable(Node node, int count) {
        this.node = node;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    
}
