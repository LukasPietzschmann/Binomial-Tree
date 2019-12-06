import java.util.ArrayList;

// Als Binomial-Halde implementierte Minimum-Vorrangwarteschlange
// mit Prioritäten eines beliebigen Typs P (der die Schnittstelle
// Comparable<P> oder Comparable<P'> für einen Obertyp P' von P
// implementieren muss) und zusätzlichen Daten eines beliebigen Typs D.

class BinHeap <P extends Comparable<? super P>, D> {
  private Node<P, D> head; // Refrenz auf erstes Element der Halde

  //Konstruktor BinHeap
  public  BinHeap(){
    this(null);
  }
  
  public BinHeap(Node head){
    this.head = head;
  }

  // Eintrag einer solchen Warteschlange bzw. Halde, bestehend aus
  // einer Priorität prio mit Typ P und zusätzlichen Daten data mit
  // Typ D.
  // Wenn der Eintrag momentan tatsächlich zu einer Halde gehört,
  // verweist node auf den zugehörigen Knoten eines Binomialbaums
  // dieser Halde.
  public static class Entry<P extends Comparable<? super P>, D> {
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
      e.node = this;
    }
    
    // Priorität des Knotens, d. h. des zugehörigen Eintrags
    // liefern.
    private P prio() {
      return entry.prio;
    }
  }
  
  private BinHeap mergeBinHeap(BinHeap h1, BinHeap h2) {
    BinHeap h = new BinHeap();
    ArrayList<BinHeap> buffer = new ArrayList<>();
    buffer.ensureCapacity(3);
    
    int k = 0;
    
    while (!h1.isEmpty() || !h2.isEmpty() || !buffer.isEmpty()) {
      if (h1.head.degree == k) {
        buffer.add(new BinHeap(h1.head));
        h1.remove(h1.head.entry);
      }
      if (h2.head.degree == k) {
        buffer.add(new BinHeap(h2.head));
        h2.remove(h1.head.entry);
      }
      
      if (buffer.size() == 1 || buffer.size() == 3) {
        BinHeap b = buffer.get(0);
        buffer.remove(b);
        
        Node n = this.head;
        while (n.sibling != this.head) n = n.sibling;
        n.sibling = b.head;
        n.sibling.sibling = this.head;
      }
      if (buffer.size() == 2) {
        BinHeap b1 = buffer.get(0);
        BinHeap b2 = buffer.get(1);
        
        if (b1.head.entry.prio.compareTo(b2.head.entry.prio) <= 0) {
          BinHeap temp = b1;
          b1 = b2;
          b2 = temp;
        }
        
        b2.head.sibling = null;
        b2.head.degree += 1;
        b1.head.parent = b2.head;
  
        if (b2.head.child == null) b2.head.child = b1.head.sibling = b1.head;
        else b1.head.sibling = b2.head.child.sibling;
        b2.head.child = b2.head.child.sibling = b1.head;
      }
      
      k++;
    }
    
    
    return null;
  }
  //liefert die Größe der Halde, die Anzahl momentan gespeicherten Elemeneten
  public int size(){
    Node next = this.head.sibling;
    int degree;
    if(this.head != null) {
      degree = head.degree;
      while (this.head != next) {
        degree += next.degree;
        next = next.sibling;
      }
      return (int) Math.pow(2,degree) - 1;
    }
    return 0;
  }

  public boolean isEmpty(){
    if(this.size() == 0){
      return true;
    }
    return false;
  }

  public Entry<P, D> minimum(){
    Entry next = this.head.sibling.entry;
    Entry temp = null;
    P minprio = this.head.entry.prio();
    if(this.head != null){
      while(this.head.entry != next) {
        if(minprio.compareTo((P)next.prio()) <=0){
          minprio = (P)next.prio();
          temp = next;
        }
      }
      return temp;
    }
    return null;
  }
}