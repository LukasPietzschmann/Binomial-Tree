// Als Binomial-Halde implementierte Minimum-Vorrangwarteschlange
// mit Prioritäten eines beliebigen Typs P (der die Schnittstelle
// Comparable<P> oder Comparable<P'> für einen Obertyp P' von P
// implementieren muss) und zusätzlichen Daten eines beliebigen Typs D.
class BinHeap <P extends Comparable<? super P>, D> {
  private Node<P, D> head; // Refrenz auf erstes Element der Halde

  //Konstruktor BinHeap
  public  BinHeap(){
    head = null;
  }

  // Eintrag einer solchen Warteschlange bzw. Halde, bestehend aus
  // einer Priorität prio mit Typ P und zusätzlichen Daten data mit
  // Typ D.
  // Wenn der Eintrag momentan tatsächlich zu einer Halde gehört,
  // verweist node auf den zugehörigen Knoten eines Binomialbaums
  // dieser Halde.
  public static class Entry <P, D> {
    // Priorität, zusätzliche Daten und zugehöriger Knoten.
    private P prio;
    private D data;
    private Node<P, D> node;
    
    // Eintrag mit Priorität p und zusätzlichen Daten d erzeugen.
    private Entry (P p, D d) {
      prio = p;
      data = d;
    }
    
    // Priorität bzw. zusätzliche Daten liefern.
    public P prio () { return prio; }
    public D data () { return data; }
  }
  
  // Knoten eines Binomialbaums innerhalb einer solchen Halde.
  // Neben den eigentlichen Knotendaten (degree, parent, child,
  // sibling), enthält der Knoten einen Verweis auf den zugehörigen
  // Eintrag.
  private static class Node <P, D> {
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
    private Node (Entry<P, D> e) {
      entry = e;
      e.node = this;
    }
    
    // Priorität des Knotens, d. h. des zugehörigen Eintrags
    // liefern.
    private P prio () { return entry.prio; }
  }
  //liefert die Größe der Halde, die Anzahl momentan gespeicherten Elemeneten
  public int size(){
    Node next = this.head.sibling;
    int degree;
    if(head != null) {
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
}