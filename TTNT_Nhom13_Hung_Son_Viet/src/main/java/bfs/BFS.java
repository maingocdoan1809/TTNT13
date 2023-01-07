package bfs;

import bfs.Pair;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author MAI NGOC DOAN
 */
enum State {
  SOLVED, // giai duoc
  UNDEFINED // chua biet co giai duoc hay khong
}

// thuat toan tim tren mot cay do thi.
class Node {

  private String name;
  private int value;
  private HashMap<String, Node> children;
  private Logic logic;
  private State state;
  private Node parent;
  public static int cost = 1; // khoảng cách từ Node cha - node con, default = 1

  public Node(Logic logic, String name) {
    this(logic, Integer.MAX_VALUE, name);
  }

  public Node(Logic logic, int value, String name) {
    this.logic = logic;
    this.state = State.UNDEFINED;
    children = new HashMap<>();
    if (value == 0) {
      this.state = State.SOLVED;
    }
    this.value = value;
    this.parent = null;
    this.name = name;
  }

  public HashMap<String, Node> getChildren() {
    return this.children;
  }

  public void addChildNode(Node... nodeName) {
    for (var node : nodeName) {
      node.setParent(this);
      this.children.put(node.name, node);
    }
  }

  public State getState() {
    return this.state;
  }

  public void setRoot() {
    this.parent = null;
  }

  public Logic getLogic() {
    return this.logic;
  }

  public int getValue() {
    return this.value;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public Node getParent() {
    return this.parent;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void returnToParent() {
    Node currParent = parent;
    while (currParent != null) {
      var childrenSet = currParent.children.values();
      for (var child : childrenSet) {
        if (child.getValue() == Integer.MAX_VALUE) {
          return;
        }
      }
      Node[] children = new Node[childrenSet.size()];
      childrenSet.toArray(children);
      currParent.setValue(currParent.logic.compute(children).value());
      currParent.setState(currParent.logic.compute(children).state());
      currParent = currParent.parent;

    }

    return;
  }

  public String getName() {
    return this.name;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public boolean isEndNode() {
    return this.children.size() == 0;
  }

  public void resetChildren() {
    this.children = new HashMap<>();
  }
}

record Pair(int value, State state) {
}

interface Logic {
  Pair compute(Node... children);
}

class LogicAnd implements Logic {
  @Override
  public Pair compute(Node... children) {
    int value = 0;
    boolean isSolved = true;
    for (var child : children) {
      value += child.getValue() + Node.cost;
      boolean currState = (child.getState() == State.SOLVED);
      isSolved &= currState;
    }
    return new Pair(value, isSolved ? State.SOLVED : State.UNDEFINED);
  }
}

class LogicOr implements Logic {
  @Override
  public Pair compute(Node... children) {
    int value = Integer.MAX_VALUE;
    boolean isSolved = false;
    for (var child : children) {
      int childValue = child.getValue() + Node.cost;
      if (value > childValue) {
        value = childValue;
      }
      boolean currState = (child.getState() == State.SOLVED);
      isSolved |= currState;
    }
    return new Pair(value, isSolved ? State.SOLVED : State.UNDEFINED);
  }
}

public class BFS {
  private Node root;

  public BFS(Node root) {
    this.root = root;
  }

  /**
   * 
   * @param qDeque: hàng đợi của thuật toán tìm kiếm rộng
   */
  private void visit(Deque<Node> qDeque) {
    if (qDeque.size() > 0) {
      Node nextNode = qDeque.pollFirst();
      for (var node : nextNode.getChildren().values()) {
        qDeque.addLast(node);
      }
      nextNode.returnToParent();
      visit(qDeque);
    }
  }

  public Node find() {
    var qDeque = new ArrayDeque<Node>();
    qDeque.addFirst(root);
    visit(qDeque);
    return root;
  }

  public static void main(String[] args) {
    Logic logicAnd = new LogicAnd();
    Logic logicOr = new LogicOr();
    Node A = new Node(logicAnd, "A");
    Node B = new Node(logicAnd, "B");
    Node C = new Node(logicOr, "C");
    Node D = new Node(logicOr, "D");
    Node E = new Node(logicOr, 0, "E");
    Node F = new Node(logicOr, 0, "F");
    Node G = new Node(logicOr, 2, "G");
    Node H = new Node(logicOr, "H");
    Node I = new Node(logicAnd, "I");
    Node J = new Node(logicOr, 5, "J");
    Node K = new Node(logicOr, 0, "K");
    //
    A.addChildNode(B, C);
    B.addChildNode(D, E);
    D.addChildNode(F, G);
    C.addChildNode(H);
    H.addChildNode(I);
    I.addChildNode(J, K);
    BFS bfs = new BFS(A);
    bfs.find();
    System.out.println(A.getValue());
    System.out.println(A.getState());
  }
}
