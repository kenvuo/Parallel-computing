import java.util.*;
import java.util.concurrent.*;

class FindHighestParallel {
    public static void main(String[] args) {
	if(args.length == 0) {
	    String txt = "Run with: java FindHighestParallel <sizeOfCount>\n";
	    txt += "Amount of threads will equal the amount of cores";
	    System.out.println(txt);
	}
	if(args.length > 0) {
	    new Solver(Integer.parseInt(args[0]));
	}
    }
}

class Solver {
    
    int threads = Runtime.getRuntime().availableProcessors();
    CyclicBarrier cwait, cfinish;
    Random randomg = new Random(97361);
    int []sort;
    int []hei;
    int size;

    Solver(int size) {
	this.size = size;
	sort = new int[size];
	randomizer();	
	for(int i = 0; i<9; i++) {
	    hei = sort.clone();
	    long sTime = System.nanoTime();
	    solveThreads(hei);
	    long eTime = System.nanoTime();
	    System.out.println((eTime - sTime)/1000000.0);
	    //write();
	}
    }
    
    void write() {
	for(int i = 0; i<50; i++)
	    System.out.print(hei[i] + " ");
	System.out.println();
    }

    void randomizer() {
	//Generer tilfeldige tall
	for(int i = 0; i < size; i++) {
	    sort[i] = randomg.nextInt(10000);
	} 
    }

    void solveFirst(int[] a, int sta, int end) {
	int j, t;
	for (int i = sta + 49; i >= sta; i--) {
	    j = i;
	    t = a[i];
	    while(j < sta + 49 && t < a[j + 1]) {
		a[j] = a[j + 1];
		j++;
	    }
	    a[j] = t;
	} 
    }
    
    void solveRest(int a[], int sta, int end) {
	//lik opg1, men med noen få endringer
	//For å fungere med forskjellige arrays
	int k, t;
	for(int i = sta + 50; i <= end; i++) {
	    if(a[i] > a[sta + 49]) {
		t = a[i]; a[i] = a[sta + 49]; k = sta + 48;
		while(k >= sta && t > a[k]) {
		    a[k+1] = a[k];
		    k--;
		}
		a[k+1] = t;
	    }
	}
    }

    void solveCombine(int a[]) {
	//Tar 50 første fra alle de andre trådene
	//og 50 første fra starten av arrayet
	//Sorterer deretter helt lik som solveRest().
	
	int k, t;
	//Henter tråden som ikke er den første
	for(int i = size/threads; (i+49) < size; i+= size/threads) { 
	    //Tar de 50 første verdiene for å kombinere
	    for(int j = i+49; j>=i; j--) {
		if(a[j] > a[49]) {
		    t = a[j]; a[j] = a[49]; k = 48;
		    //Sorterer 1 og 1 inn i den første tråden
		    while(k >= 0 && t > a[k]) { 
			a[k+1] = a[k];
			k--;
		    }
		    a[k+1] = t;
		}
	    }
	}
    }
	
    void solveThreads(int a[]) {
	//Basert på CyclicBarrier malen vi fikk
	cwait = new CyclicBarrier(threads + 1);
	cfinish = new CyclicBarrier(threads + 1);
	for(int i = 0; i < threads; i++)
	    new Thread(new Watcher(i, a)).start();
	try {
	    cwait.await();
	    cfinish.await();
	} catch (Exception e) {
	    return;
	}
    }

    void solvePar(int a[], int sta, int end) {
	solveFirst(a, sta, end);
	solveRest(a, sta, end);
    }

    class Watcher implements Runnable {
	int number;
	int []b;
	
	Watcher(int number, int b[]) {
	    this.number = number;
	    this.b = b;
	}
	
	public void run() {
	    if(number != threads-1) {
		solvePar(b, size/threads*number, (size/threads)*(number+1) - 1);
	    }
	    else{ 
		solvePar(b, size/threads*number, size-1); //Kjøres når den er på siste tråd
	    }try {
		cwait.await(); //Venter på alle trådene før kombinering
		if(number == threads - 1)
		    solveCombine(b);
		cfinish.await();
	    } catch(Exception e) {
		return;
	    }   
	}
    }
}
