import java.util.Stack;

// Als Binomial-Halde implementierte Minimum-Vorrangwarteschlange
// mit Prioritäten eines beliebigen Typs P (der die Schnittstelle
// Comparable<P> oder Comparable<P'> für einen Obertyp P' von P
// implementieren muss) und zusätzlichen Daten eines beliebigen Typs D.
//FIXME beim 6. einfügen Schleife
class BinHeap<P extends Comparable<? super P>, D> {
  // Refrenz auf erstes Element der Halde
  private Node<P, D> head;
  
  //Konstruktor BinHeap
  public BinHeap() {
    this(null);
  }
  
  public BinHeap(Node head) {
    this.head = head;
  }
  
  private BinHeap mergeBinHeap(BinHeap h1, BinHeap h2) {
    if (h1 == null || h2 == null) return null;
    if (h1.head == null) return h2;
    if (h2.head == null) return h1;
    
    BinHeap h = new BinHeap();
    Stack<Node<P, D>> buffer = new Stack<>();
    buffer.ensureCapacity(3);
    
    int k = 0;
    
    while (!h1.isEmpty() || !h2.isEmpty() || !buffer.isEmpty()) {
      if (!h1.isEmpty() && h1.head.degree == k) {
        buffer.push(h1.head);
        
        if (h1.head.sibling == null) h1.head = null;
        else h1.head = h1.head.sibling;
      }
      if (!h2.isEmpty() && h2.head.degree == k) {
        buffer.push(h2.head);
        
        if (h2.head.sibling == null) h2.head = null;
        else h2.head = h2.head.sibling;
      }
      
      if (buffer.size() == 1 || buffer.size() == 3) {
        Node<P, D> b = buffer.pop();
        b.sibling = null;
        
        if (h.head == null) h.head = b;
        else {
          Node lastElemInH = h.head;
          while (lastElemInH.sibling != null) lastElemInH = lastElemInH.sibling;
          
          lastElemInH.sibling = b;
        }
      }
      if (buffer.size() == 2) {
        Node<P, D> b1 = buffer.pop();
        Node<P, D> b2 = buffer.pop();
        
        b1.sibling = null;
        b2.sibling = null;
        
        if (b1.entry.prio instanceof NegInfinityPrio || b1.entry.prio.compareTo(b2.entry.prio) <= 0) {
          Node<P, D> temp = b1;
          b1 = b2;
          b2 = temp;
        }
        
        b2.sibling = null;
        b2.degree += 1;
        b1.parent = b2;
        
        if (b2.child == null) b2.child = b1.sibling = b1;
        else {
          b1.sibling = b2.child.sibling;
          b2.child = b2.child.sibling = b1;
        }
        
        buffer.push(b2);
      }
      
      k++;
    }
    
    return h;
  }
  
  public int size() {
    if (this.head == null) return 0;
    int s = (int) Math.pow(2, this.head.degree);
    
    Node<P, D> n = this.head;
    while (n.sibling != null) {
      n = n.sibling;
      s += Math.pow(2, n.degree);
    }
    
    return s;
  }
  
  public boolean isEmpty() {
    return size() == 0;
  }
  
  public Entry<P, D> minimum() {
    if (this.head == null) return null;
    
    Node next = this.head;
    Entry<P, D> minPrioEntry = next.entry;
    
    do {
      if (next.prio() instanceof NegInfinityPrio) return next.entry;
      if (((P) next.prio()).compareTo(minPrioEntry.prio) < 0) minPrioEntry = next.entry;
      next = next.sibling;
    }while (next != null);
    
    return minPrioEntry;
  }
  
  public boolean changePrio(Entry<P, D> e, P p) {
    if (this.head == null || e == null || p == null || !this.contains(e)) return false;
    if (p instanceof NegInfinityPrio || p.compareTo(e.prio) <= 0) {
      e.prio = p;
      
      while (e.node.parent != null && (e.prio instanceof NegInfinityPrio || e.prio.compareTo(e.node.parent.entry.prio) < 0)) {
        e.node.entry = e.node.parent.entry;
        e.node.entry.node = e.node;
        e.node.parent.entry = e;
        e.node = e.node.parent;
      }
    }else {
      remove(e);
      e.prio = p;
      BinHeap<P, D> binHeap = new BinHeap<>(new Node(e));
      head = mergeBinHeap(this, binHeap).head;
    }
    
    return true;
  }
  
  public boolean remove(Entry<P, D> e) {
    if (this.head == null || e == null || !contains(e)) return false;
    changePrio(e, (P) new NegInfinityPrio());
    extractMin();
    
    return true;
  }
  
  public boolean contains(Entry<P, D> e) {
    if (this.head == null || e == null || e.node == null) return false;
    
    Node moveUpNode = e.node;
    while (moveUpNode.parent != null) moveUpNode = moveUpNode.parent;
    
    Node current = head;
    while (current != null) {
      if (current == moveUpNode) return true;
      current = current.sibling;
    }
    return true;
  }
  
  public Entry<P, D> insert(P p, D d) {
    if (p == null) return null;
    
    Entry e = new Entry(p, d);
    BinHeap h2 = new BinHeap(new Node(e));
    this.head = mergeBinHeap(this, h2).head;
    
    return e;
  }
  
  public Entry<P, D> extractMin() {
    if (head == null) return null;
    
    Entry minimum = minimum();
    Node n = head;
    if (minimum.node == head) head = minimum.node.sibling;
    else {
      while (n.sibling != minimum.node) n = n.sibling;
      n.sibling = minimum.node.sibling;
    }
    
    // Wenn dieser Knoten Nachfolger besitzt: Vereinige die Liste seiner Nachfolger
    // (beginnend mit dem Nachfolger mit dem kleinsten Grad, der über child → sibling direkt zugreifbar ist) mit der verbleibenden Halde.
    if (minimum.node.child != null) {
      Node minDegreeNode = minimum.node.child.sibling;
      minimum.node.child.sibling = null;
      
      Node it = minDegreeNode;
      while (it != null) {
        it.parent = null;
        it = it.sibling;
      }
      
      BinHeap h2 = new BinHeap(minDegreeNode);
      this.head = mergeBinHeap(this, h2).head;
    }
    
    return minimum;
  }
  
  public void dump() {
    if (head == null) return;
    System.out.println(head.dump(head, 0));
  }
  
  //<editor-fold desc="Hilfs Klassen">
  // Eintrag einer solchen Warteschlange bzw. Halde, bestehend aus
  // einer Priorität prio mit Typ P und zusätzlichen Daten data mit
  // Typ D.
  // Wenn der Eintrag momentan tatsächlich zu einer Halde gehört,
  // verweist node auf den zugehörigen Knoten eines Binomialbaums
  // dieser Halde.
  public static class Entry<P, D> {
    // Priorität, zusätzliche Daten und zugehöriger Knoten.
    private P prio;
    private D data;
    private Node<P, D> node;
    
    // Eintrag mit Priorität p und zusätzlichen Daten d erzeugen.
    private Entry(P p, D d) {
      prio = p;
      data = d;
    }
    
    // Priorität bzw. zusätzliche Daten liefern.
    public P prio() {
      return prio;
    }
    
    public D data() {
      return data;
    }
  }
  
  
  // Knoten eines Binomialbaums innerhalb einer solchen Halde.
  // Neben den eigentlichen Knotendaten (degree, parent, child,
  // sibling), enthält der Knoten einen Verweis auf den zugehörigen
  // Eintrag.
  private static class Node<P, D> {
    // Zugehöriger Eintrag.
    private Entry<P, D> entry;
    
    // Grad des Knotens.
    private int degree;
    
    // Vorgänger (falls vorhanden; bei einem Wurzelknoten null).
    private Node<P, D> parent;
    
    // Nachfolger mit dem größten Grad
    // (falls vorhanden; bei einem Blattknoten null).
    private Node<P, D> child;
    
    // Zirkuläre Verkettung aller Nachfolger eines Knotens
    // bzw. einfache Verkettung aller Wurzelknoten einer Halde,
    // jeweils sortiert nach aufsteigendem Grad.
    private Node<P, D> sibling;
    
    // Knoten erzeugen, der auf den Eintrag e verweist
    // und umgekehrt.
    private Node(Entry<P, D> e) {
      entry = e;
      e.node = (Node<P, D>) this;
    }
    
    // Priorität des Knotens, d. h. des zugehörigen Eintrags
    // liefern.
    private P prio() {
      return entry.prio;
    }
    
    private String dump(Node first, int height) {
      String out = this.dumpEntry(height);
      
      if (child != null) out += child.dump(child, height + 1);
      if (sibling != first && sibling != null) out += sibling.dump(first, height);
      
      return out;
    }
    
    private String dumpEntry(int height) {
      String out = "";
      for (int i = 0; i < height; i++) out += "  ";
      out += prio() + " " + entry.data() + "\n";
      
      return out;
    }
    
    @Override
    public String toString() {
      return String.format("%s  %s", entry.data, entry.prio);
    }
  }
  
  
  private static class NegInfinityPrio implements Comparable {
    @Override
    public int compareTo(Object o) {
      return -1;
    }
  }
  //</editor-fold>
}