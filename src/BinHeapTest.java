// Interaktives Testprogramm für die Klasse BinHeap.
class BinHeapTest {
  public static void main(String[] args) throws java.io.IOException {
    // Leere Halde mit Prioritäten des Typs String und zugehörigen
    // Daten des Typs Integer erzeugen.
    // (Die Implementierung muss aber natürlich auch mit anderen
    // Typen funktionieren.)
    BinHeap<Integer, Integer> heap = new BinHeap<>();
    
    // Feld mit allen eingefügten Einträgen, damit sie später
    // für remove und changePrio verwendet werden können.
    // Achtung: Obwohl die Klasse BinHeap ebenfalls Typparameter
    // besitzt, schreibt man "BinHeap.Entry<String, Integer>" und
    // nicht "BinHeap<String, Integer>.Entry<String, Integer>".
    // Achtung: "new BinHeap.Entry [100]" führt zu einem Hinweis
    // über "unchecked or unsafe operations"; die eigentlich "korrekte"
    // Formulierung "new BinHeap.Entry<String, Integer> [100]"
    // führt jedoch zu einem Übersetzungsfehler!
    BinHeap.Entry<Integer, Integer>[] entrys = new BinHeap.Entry[100];
    
    // Anzahl der bis jetzt eingefügten Einträge.
    int n = 0;
    
    // Standardeingabestrom System.in als InputStreamReader
    // und diesen wiederum als BufferedReader "verpacken",
    // damit man bequem zeilenweise lesen kann.
    java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
    
    // Endlosschleife.
    while (true) {
      // Inhalt und Größe der Halde ausgeben.
      heap.dump();
      System.out.println(heap.size() + " entry(s)");
      
      // Eingabezeile vom Benutzer lesen, ggf. ausgeben (wenn das
      // Programm nicht interaktiv verwendet wird) und in einzelne
      // Wörter zerlegen.
      // Abbruch bei Ende der Eingabe oder leerer Eingabezeile.
      System.out.print(">>> ");
      String line = r.readLine();
      if (line == null || line.equals("")) return;
      if (System.console() == null) System.out.println(line);
      String[] cmd = line.split(" ");
      
      // Fallunterscheidung anhand des ersten Worts.
      switch (cmd[0]) {
        case "+":
          // insert prio
          // Die laufende Nummer n wird als zusätzliche Daten verwendet.
          entrys[n] = heap.insert(Integer.parseInt(cmd[1]), n);
          n++;
          break;
        case "-": // remove entry
          heap.remove(entrys[Integer.parseInt(cmd[1])]);
          break;
        case "?": // minimum
          BinHeap.Entry<Integer, Integer> e = heap.minimum();
          System.out.println("--> " + e.prio() + " " + e.data());
          break;
        case "!": // extractMin
          e = heap.extractMin();
          System.out.println("--> " + e.prio() + " " + e.data());
          break;
        case "=": // changePrio entry prio
          heap.changePrio(entrys[Integer.parseInt(cmd[1])], Integer.parseInt(cmd[2]));
          break;
      }
    }
  }
}