import java.util.*;

class FindHighestSequential {
    
    public static void main(String[] args) {
	if(args.length == 0) {
	    String txt = "Run with: java FindHighestSequential <sizeOfCount>\n";
	    txt += "Amount of threads will equal the amount of cores";
	    System.out.println(txt);
	}
	if(args.length > 0)
	    new Beholder(Integer.parseInt(args[0]));
    }

  
}

class Beholder {
    int[] beholder;
    int[] beholder2;
    Random randomg = new Random(97361);
    
    Beholder(int size) {
	beholder2 = new int[size];
	lagTall();
	for(int i = 0; i<9; i++) {
	    long sTime = System.nanoTime();
	    beholder = beholder2.clone();
	    insertSort(beholder, 0, 49);
	    //skrivUt();
	    sorterRest();
	    long eTime = System.nanoTime();
	    System.out.println((eTime - sTime)/ 1000000.0);
	    //skrivUt();   
	}
	System.out.println();
	//Array sort
	for(int i = 0; i<9; i++) {
	    long sTime = System.nanoTime();
	    beholder = beholder2.clone();
	    Arrays.sort(beholder);
	    long eTime = System.nanoTime();
	    System.out.println((eTime - sTime)/ 1000000.0);
	}
	
    }

    void lagTall() {
	for(int i = 0; i<beholder2.length; i++) {
	    beholder2[i] = randomg.nextInt(10000);
	}
    }
    
    static void insertSort(int []a, int v, int h) {
	int i, t;

	for(int k = v; k < h; k++) {
	    t = a[k+1];
	    i = k;
	    while(i >= v && a[i] < t ) {
		a[i+1] = a[i];
		i--;
	    }
	    a[i+1] = t;
	}
    }
    
    void sorterRest() {
	for(int i = 50; i<beholder.length; i++) {
	    if(beholder[49] < beholder[i]) {
		//Replace
		beholder[49] = beholder[i];
		int p, j; p = beholder[49]; j = 48;
		while(j >= 0 && beholder[j] < p) {
		    beholder[j+1] = beholder[j];
		    j--;
		}
		beholder[j+1] = p;
	    }
	    //Else skip
	}
    }
    
    void skrivUt() {
	for(int i = 0; i < 50; i++) {
	    System.out.print(beholder[i] + " ");
	}
	System.out.println();
    }
}
