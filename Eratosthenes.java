import java.util.*;
import java.util.concurrent.*;

class Eratosthenes {
    public static void main(String []args) {
	new EratosthenesSil(200000000);
	new EratosthenesSil(200000000, true);
    }
    
}


class EratosthenesSil {

    int threads = 4; //Runtime.getRuntime().availableProcessors();
    CyclicBarrier wait1;
    CyclicBarrier wait2;
    Semaphore allowWrite;
    ArrayList<ArrayList<Long>> facNum; //Double arraylist for å huske faktorer

    byte [] bitArr ;           // bitArr[0] represents the 8 integers:  1,3,5,...,15, and so on
    int  maxNum;               // all primes in this bit-array is <= maxNum
    final  int [] bitMask = {1,2,4,8,16,32,64,128};  // kanskje trenger du denne
    final  int [] bitMask2 ={255-1,255-2,255-4,255-8,255-16,255-32,255-64, 255-128}; // kanskje trenger du denne
    
    
    EratosthenesSil (int maxNum) {
        this.maxNum = maxNum;
	bitArr = new byte [(maxNum/16)+1];
	setAllPrime();

	long sTime, eTime;
	sTime = System.nanoTime();
        generatePrimesByEratosthenes();
	eTime = System.nanoTime() - sTime;
	System.out.println("Seq: " + eTime/1000000 + "ms");
	
	long m = ((long)maxNum)*((long)maxNum);
	System.out.println(m);
	for(long j = m; j>m-100; j--) {
	    if(j > m-6 || j < m-94)
		System.out.print(j + ": ");
	    for(long i : factorize(j)) {
		if(j > m-6 || j < m-94)
		    System.out.print(i + " ");
	    }
	    if(j > m-6 || j < m-94)
		System.out.println();
	}
    } // end konstruktor ErathostenesSil

    EratosthenesSil (int maxNum, boolean parMode) {
	this.maxNum = maxNum;
	bitArr = new byte[(maxNum/16)+1];
	setAllPrime();

	//Initialize ArrayList
	facNum = new ArrayList<ArrayList<Long>>();
	for(int i = 0; i<100; i++) {
	    facNum.add(new ArrayList<Long>());
	}
	allowWrite = new Semaphore(1);
	
	long sTime, eTime;
	sTime = System.nanoTime();
	generatePar();
	eTime = System.nanoTime() - sTime;
	System.out.println("Par: " + eTime/1000000 + "ms");
	long m = ((long)maxNum)*((long)maxNum);
	System.out.println(m);
	/*for(long j = m; j>m-10; j--) {	
	    System.out.print(j + ": ");
	    for(long i : factorize(j)) {
		System.out.print(i + " ");
	    }
	    System.out.println();
	    }*/
	//printAllPrimes();
	parFac();
	System.out.println("test");
	printFactors();
    }

    void printFactors(){
	int b = 5;
	for(ArrayList<Long> j : facNum){
	    long num = (long)maxNum*maxNum-5+b;
	    System.out.print(num+": ");
	    boolean c=false;
	    for(Long i : j){
		if(c) System.out.print("*");
		c=true;
		System.out.print(i);
	    }
	    System.out.println();
	    b--;
	    if(b==0)break;
	}
		
	b=95;
	int d=5;
	for(ArrayList<Long> j : facNum){
	    if(b>0){
		b--;
		continue;
	    }
	    long num = (long)maxNum*maxNum-100+d;
	    System.out.print(num+": ");
	    boolean c=false;
	    for(Long i : j){
		if(c) System.out.print("*");
		c=true;
		System.out.print(i);
	    }
	    System.out.println();
	    d--;
	    if(d==0)break;
	}
    }


    void parFac() {
		int posRange = maxNum/threads; 
		int addRange = maxNum%threads;
		if(posRange == 0)
		    wait1 = new CyclicBarrier(addRange+1);
		else
		    wait1 = new CyclicBarrier(threads+1);

		for(int i = 1; i<= maxNum; i+=posRange) {
		    int start = i;
		    if (addRange > 0) {
				i++;
				addRange--;
		    }
		    System.out.println("Thread");
		    new Thread(new WatcherFac(start, i+posRange)).start(); //i+posRange = end
		}
		try { wait1.await(); }
		catch (Exception e) { return; }
		System.out.println("test");
		int cnt = 0;
		for(ArrayList<Long> i : facNum) {
		    long j = 1;
		    for(long k : i)
			j *= k;
		    if(j < (long) maxNum*maxNum - cnt)
			i.add( ((long) maxNum*maxNum - cnt) / j);
		    cnt++;
		}
		
		for(ArrayList<Long> i : facNum)
		    Collections.sort(i);
		
	    } 
	    
	    void setAllPrime() {
		for (int i = 0; i < bitArr.length; i++) {
		    bitArr[i] = (byte)255;
		}
    }
    void crossOut(int i) {
	// set as not prime- cross out (set to 0)  bit represening 'int i'
	// ** <din kode her>
	bitArr[i/16] &= bitMask2[(i%16)/2];
	//bitArr[i/16] &= bitMask2[((i%16)>>1)];
    } //
    
    boolean isPrime (int n) {
	// <din kode her, husk å teste særskilt for 2 (primtall) og andre partall først>
	//Sjekker vis n er en multiplikasjon av 2

	if(n==2) return true;
	//if (n%2==0) return false;
	if ((n&1) == 0) return false;
	/*int index = 7-(n%16/2);
	if( (bitArr[n/16]&bitMask[index]) == bitMask[index] ) return true;
	return false;*/
	else return (bitArr[n>>4] & bitMask[(n&15)>>1]) != 0;
    }
    
    ArrayList<Long> factorize (long num) {
		ArrayList <Long> fakt = new ArrayList <Long>();
		// <Ukeoppgave i Uke 7: din kode her>

		int maks = (int) Math.sqrt(num*1.0) +1;
		int prim = 2;
		
		while (num > 1 & prim < maks) {	    
		    while ( num % prim == 0){
			fakt.add((long) prim);
			num /= prim;
		    }
		    prim = nextPrime(prim);
		}
		if (prim>=maks) fakt.add(num);
		
		return fakt;
    } // end factorize
    
    ArrayList<Long> factorizePar(long num, int startPos, int endPos) {
	if(!isPrime(startPos))
	    startPos = nextPrime(startPos);
	ArrayList<Long> fakt = new ArrayList<Long>();
	
	while(startPos < endPos) {
	    while( num % startPos == 0) {
		fakt.add((long) startPos);
		num /= startPos;
	    }
	    startPos = nextPrime(startPos);
	}
	//System.out.println("factorizePar");
	return fakt;
    }
    
    int nextPrime(int i) {
	// returns next prime number after number 'i'
	// <din kode her>
	
	//if(i%2==0) i+=1;
	if((i&1)==0) i+=1;
	else i+=2;
	while(!isPrime(i)) i+=2;
	return i;
	
	
    } // end nextTrue
    
    
    void printAllPrimes(){
	for ( int i = 2; i*i <= maxNum; i++)
	    if (isPrime(i)) System.out.println(" "+i);
	
    }

    void generatePar() {
	crossOut(1);
	wait1 = new CyclicBarrier(threads + 1);
	wait2 = new CyclicBarrier(threads + 1);

	int firstByte = (int) (Math.sqrt(maxNum) / 16) + 1;
	int size = (int) (bitArr.length - 1 - (Math.sqrt(maxNum) / 16));
	
	for(int i = 0; i< threads; i++) {
	    int startPos = (i * size / threads) + firstByte;
	    int endPos = ((i + 1) * size / threads) + firstByte - 1;
	    if(i == threads - 1) 
		endPos = bitArr.length - 1;
	    new Thread(new Watcher(i, startPos, endPos)).start();
	}
	
	System.out.println("hei");
	
	try  { wait1.await(); 
	    wait2.await(); }
	catch (Exception e) {return;}
    }

    void generatePrime(int id, int startPos, int endPos) {
	int first = startPos * 16 + 1;
	int last = endPos * 16 + 16;
	System.out.println(first);
	System.out.println(last);
	
	int m = 3, m2=6,mm =9;     // next prime
	if (id == -1) {
	    
	    while ( mm < last) {
		//ystem.out.println("Generate1.. m;"+m+", mm:"+mm+", bitLen:"+bitArr.length);
		m2 = m+m;
		for ( int k = mm; k < last; k +=m2){
		    //					System.out.println("Generate2.. k:"+ k+", m;"+m+", mm:"+mm);
		    crossOut(k);
		}
		m = nextPrime(m);
		mm= m*m;
		
	    }
	}// end generatePrimesByEratosthenes
	else {
	    m = (int)Math.sqrt(first);
	    mm = first;
	    while(mm < last) {
		//System.out.println("Generate1.. m;"+m+", mm:"+mm+", bitLen:"+bitArr.length);
		m2 = m+m;
		for ( int k = mm; k < last; k +=m2){
		    //					System.out.println("Generate2.. k:"+ k+", m;"+m+", mm:"+mm);
		    crossOut(k);
		}
		m = nextPrime(m);
		mm= m*m;
		//if(mm >= -1) break;
	    }
	}
    }
    
    void generatePrimesByEratosthenes() {
	// krysser av alle  oddetall i 'bitArr[]' som ikke er primtall (setter de =0)
	crossOut(1);      // 1 er ikke et primtall
	// < din Kode her, kryss ut multipla av alle primtall <= sqrt(maxNum),
	// og start avkryssingen av neste primtall p med p*p>
	//maxNum
	/*for(int i = 3; i*i<maxNum; i+=2)
	    if(isPrime(i)) 
		for(int j = i*i; j<=maxNum; j+=2*i)
		    if(j%2!=0)
		    crossOut(j);*/
	//printAllPrimes();
	int m = 3, m2=6,mm =9;     // next prime
	
	while ( mm < maxNum) {
	    //System.out.println("Generate1.. m;"+m+", mm:"+mm+", bitLen:"+bitLen);
	    m2 = m+m;
	    for ( int k = mm; k < maxNum; k +=m2){
		//					System.out.println("Generate2.. k:"+ k+", m;"+m+", mm:"+mm);
		crossOut(k);
	    }
	    m = nextPrime(m);
	    mm= m*m;
	    
	} // end generatePrimesByEratosthenes
    }

    
    
    class Watcher implements Runnable {
	int id, startPos, endPos;
	Watcher(int id, int startPos, int endPos) {
	    this.id = id;
	    this.startPos = startPos;
	    this.endPos = endPos;
	}
	
	public void run() {
	    if(id == 0) {
		generatePrime(-1, 0, startPos - 1); 
		System.out.println("First");
	    }
	    try {
		wait1.await();
		generatePrime(id, startPos, endPos);
		wait2.await();
		System.out.println("Done");
	    } catch(Exception e) { return;}
	}
    }

    class WatcherFac implements Runnable {
	int startPos;
	int endPos;
	WatcherFac(int startPos, int endPos) {
	    this.startPos = startPos;
	    this.endPos = endPos;
	}

	public void run() {
	    if(startPos != endPos) {
		System.out.println("Runnable");
		for(int i = 0; i<100; i++) {
		    ArrayList<Long> factors = factorizePar((long)maxNum*maxNum-i , startPos, endPos);
		    try {
			allowWrite.acquire();
		    }
		    catch (Exception e) {
			return;
		    }
		    for(Long a : factors)
			facNum.get(i).add(a);
		    allowWrite.release();
		}
	    }
	    try { wait1.await(); }
	    catch (Exception e){ return; }
	}
    }

    
} // end class Bool
