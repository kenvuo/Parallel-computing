import java.util.*;

class ConvexHull {
    int[] x;
    int[] y;
    int MAX_X;
    int MAX_Y;
    int n;
    IntList intlist;
    int depth;

    public static void main(String[] args) {
	if(args.length > 1 || args.length < 1) {
	    System.out.println("Usage: ConvexHull Oblig.3 [n-value]");
	    System.exit(0);
	}  

	/*
	  qfor(int a = 0; a< x.length; a++) 
	  System.out.println(x[a]);
	*/
	
	//TegnUt tegn = new TegnUt(this, intlist);
	Oblig3 o = new Oblig3(args[0]);
	//o.printUt();
	new TegnUt(o, o.intlist);
	//for(int i = 1; i>0; i--)i+=2;
	//System.out.println(o.n);
    }

    Oblig3(String len) {
	n = Integer.parseInt(len);
	x = new int[n];
	y = new int[n];
	NPunkter punkter = new NPunkter(n);
	MAX_X = punkter.maxXY; //MAX X og Y hentes.
	MAX_Y = punkter.maxXY;
	punkter.fyllArrayer(x, y);
	
	intlist = new IntList(); //Brukes for å lagre punker
	double t1 = System.nanoTime();
	sekvMetode();
	double t2 = (System.nanoTime() - t1) / 1000000.0;
	System.out.println("Tid brukt sekv:" + t2);
	
	intlist = new IntList();
	t1 = System.nanoTime();
	paraLos();
	t2 = (System.nanoTime() - t1) / 1000000.0;
	System.out.println("Tid brukt para:" + t2);
    }

    void printUt() {
	System.out.format("P:");
	for(int i = 0; i<intlist.size(); i++) {
	    int n = intlist.get(i);
	    System.out.format("\n%2d: %-6d", i, n);
	}
    }

    void sekvMetode() {
	//Finner minx og maxx
	int minx = 0, maxx = 0;
	for(int i = 0; i<n; i++) { 
	    if(x[minx] > x[i])
		minx = i;
	    if(x[maxx] < x[i])
		maxx = i;
	}
	
	//Trekker linjen mellom de to punktene for minx-maxx
	//Inneholder punktene som ligger på eller under linja p1-p2
	IntList m = new IntList();
	int punkt = finnNegativ(minx, maxx, m);
	intlist.add(minx);
	sekvRek(minx, maxx, punkt, m);

	//Trekker linjen mellom de to punktene for maxx-minx
	//Resetter listen for bruk
	m = new IntList();
	punkt = finnNegativ(maxx, minx, m);
	intlist.add(maxx);
	sekvRek(maxx, minx, punkt, m);

 
    }

    int finnNegativ(int p1, int p2, IntList m) {
	//Finner alle punkter med negativ(eller 0) avstand fra linja.
	//Følger formlen der a=y1-y2, b=x2-x1 og c=y2*x1-y1*x2
	int a = y[p1] - y[p2];
	int b = x[p2] - x[p1];
	int c = ((y[p2] * x[p1]) - (y[p1] * x[p2]));

	//Finner og regner ut distansen gitt ved formelen ax+by+c=0
	int lengde = 1;
	int nyPunkt = 0;
	boolean sjekk = false;
	for(int i = 0; i<n; i++) {
	    int lengde2 = a*x[i] + b*y[i] + c;
	    //Hvis negativ, legg til
	    if(lengde2 <= 0) {
		m.add(i);
		//sjekk = true;
		if(lengde2 < lengde) {
		    lengde = lengde2;
		    nyPunkt = i;
		    sjekk = true;
		}
	    }
	}
	if(sjekk == true) {
	    m.remove((Integer)nyPunkt);
	    m.remove((Integer)p1);
	    m.remove((Integer)p2);
	    return nyPunkt;
	}
	return -1;
    }
    
    int finnNegativRek(int p1, int p2, IntList m, IntList nyM) {
	//Finner punktet med størst negative tall fra p1-p2
	//Følger formlen der a=y1-y2, b=x2-x1 og c=y2*x1-y1*x2
	int a = y[p1] - y[p2];
	int b = x[p2] - x[p1];
	int c = ((y[p2] * x[p1]) - (y[p1] * x[p2]));
	
	//Finner og regner ut distansen gitt ved formelen ax+by+c=0
	int lengde = 1;
	int nyPunkt = 0;
	boolean sjekk = false;
	for(int i = 0; i<m.size(); i++) {
	    int pPos = m.get(i);
	    int lengde2 = a*x[pPos] + b*y[pPos] + c;
	    //Hvis negativ, legger til
	    if(lengde2 <= 0)
		nyM.add(pPos);
	    //Sjekker om den er på linje med p1-p2
	    if(lengde2 < lengde && lengde2 <= 0) {
		sjekk = true; 
		if(lengde2 == 0) {
		    if(	(x[pPos]<=x[p2] && y[pPos]<=y[p2] && x[pPos]>=x[p1] && y[pPos]>=y[p1]) || (x[pPos]<=x[p1] && y[pPos]<=y[p1] && x[pPos]>=x[p2] && y[pPos]>=y[p2]) ){
			lengde = lengde2;
			nyPunkt = pPos;
		    }
		}
		else {
		    lengde = lengde2;
		    nyPunkt = pPos;
		}
	    }
	}
	if(sjekk == true) {
	    //System.out.println("test");
	    nyM.remove((Integer)nyPunkt);
	    return nyPunkt;
	}
	return -1;
    }
    
    void sekvRek (int p1, int p2, int p3, IntList m) {
	//System.out.println("sekvRek");
	//Trekker linja fra de to punktene på linja til det nye punktet
	//Får 2 nye linjer
        int nyP1 = p1; //Punktet vi skal trekke linjen fra for første linje
	int nyP2 = p3; //Punktet vi skal trekke linjen til for første linje
	//boolean sjekk = false;
	for(int i = 0; i<2; i++) {
	    //Lager en ny liste som sier hvilken punkt som mangler(ligger på eller er negativ)
	    IntList nyM = new IntList();
	    if(i != 0) {
		nyP1 = p3; //Erstatter og sjekker for den andre linjen
		nyP2 = p2;
		intlist.add(p3);
	    }
	    int nyPunkt = finnNegativRek(nyP1, nyP2, m, nyM);
	    if(nyPunkt != -1) {
		//System.out.println("test2");
		nyM.remove((Integer)nyPunkt);
		sekvRek(nyP1, nyP2, nyPunkt, nyM);
	    }
	}
    }

    void paraLos() {
	int trad = Runtime.getRuntime().availableProcessors();
	depth = 0;
	for(int tmp = trad; tmp>1;tmp/=2) 
	    depth++;
	if(depth < 1) {
	    sekvMetode();
	    return;
	}

	int minx = 0, maxx = 0;
	for(int i = 0; i<n; i++) { 
	    if(x[minx] > x[i])
		minx = i;
	    if(x[maxx] < x[i])
		maxx = i;
	}

	IntList l1 = new IntList();
	Thread trad1 = new Thread(new ParaSolver(true, 1, minx, maxx, new IntList(), l1));
	trad1.start();

	IntList l2 = new IntList();
	Thread trad2 = new Thread(new ParaSolver(true, 1, maxx, minx, new IntList(), l2));
	trad2.start();

	try {
	    trad1.join();
	    trad2.join();
	} catch(InterruptedException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}

	intlist.add(minx);
	if(l1 != null)
	    for(Integer i : l1)
		intlist.add(i);

	intlist.add(maxx);
	if(l2 != null)
	    for(Integer i : l2)
		intlist.add(i);
	
    }

    IntList finnPunktPara(int depth, int p1, int p2, int p3, IntList m) {
	IntList list1 = new IntList(); //For p1
	IntList list2 = new IntList(); //For p2

	if(this.depth > depth) {
	    Thread t1 = new Thread(new ParaSolver(false, depth + 1, p1, p3, m, list1));
	    Thread t2 = new Thread(new ParaSolver(false, depth + 1, p3, p2, m, list2));

	    t1.start();
	    t2.start();

	    try {
		t1.join();
		t2.join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
		System.exit(-1);
	    }
	}
	else {
	    int np1 = p1;
	    int np2 = p3;

	    for(int i = 0; i<2; i++) {
		IntList nyM = new IntList();
		if(i == 1) {
		    np1 = p3;
		    np2 = p2;
		}
		
		int nesteP = finnNegativRek(np1, np2, m, nyM);

		if(nesteP != -1) {
		    nyM.remove((Integer)nesteP);
		    if(i == 0)
			list1 = finnPunktPara(depth, np1, np2, nesteP, nyM);
		    else
			list2 = finnPunktPara(depth, np1, np2, nesteP, nyM);
		}
	    }
	}
	IntList tmpM = new IntList();
	
	if(list1 != null) 
	    for(Integer i : list1) 
		tmpM.add(i);

	tmpM.add(p3);

	if(list2 != null)
	    for(Integer i : list2)
		tmpM.add(i);
	
	return tmpM;
    }
    

    class ParaSolver implements Runnable {
	boolean first;
	int start;
	int p1;
	int p2;
	IntList m;
	IntList tradPunkt;

	ParaSolver(boolean first, int start, int p1, int p2, IntList m, IntList tradPunkt) {
	    this.first = first;
	    this.start = start;
	    this.p1 = p1;
	    this.p2 = p2;
	    this.m = m;
	    this.tradPunkt = tradPunkt;
	}

	public void run() {
	    int nyPunkt = -1;

	    if(first == true) {
		IntList nyM = new IntList();
		nyPunkt = finnNegativ(p1, p2, nyM);
		m = nyM;
	    }
	    else {
		IntList nyM2 = new IntList();
		nyPunkt = finnNegativRek(p1, p2, m, nyM2);
		m = nyM2;
	    }

	    if(nyPunkt != -1) {
		IntList paraPunkt = finnPunktPara(start, p1, p2, nyPunkt, m);
		if(paraPunkt != null)
		    for(Integer i : paraPunkt)
			tradPunkt.add(i);
	    }
	}
    }
}
