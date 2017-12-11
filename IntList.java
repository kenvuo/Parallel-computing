import java.util.Iterator;

/* Simpelt intlist som er basert på arraylist
** Funksjonene er lik som en arraylist */
public class IntList implements Iterable<Integer>{
    int[] intList;
    int size;
	
    IntList(){
	intList = new int[10];
	size = 0;
    }
	
    IntList(int[] intList, int size){
	this.intList = intList;
	this.size=size;
    }
	
    public boolean add(Integer e){
		
	if(e==null) return false;
	if(size == intList.length){
	    int[] tmpIntList = new int[intList.length*2];
	    System.arraycopy(intList,0,tmpIntList,0,size);
	    intList = tmpIntList;
	}
	intList[size] = e;
	size++;
	return true;
    }
	
    public Integer remove(int i){
		
	if(i>=size) return null;
	if(intList.length>10 && size<intList.length/2){
	    int[] tmpIntList = new int[intList.length/2];
	    System.arraycopy(intList,0,tmpIntList,0,size);
	    intList = tmpIntList;
	}
	int retVal = intList[i];
	System.arraycopy(intList, i+1, intList, i, size-i-1);
	intList[--size] = 0;
	return retVal;
    }
	
    public boolean remove(Integer e){
	if(e==null)return false;
		
	if(intList.length>10 && size<intList.length/2){
	    int[] tmpIntList = new int[intList.length/2];
	    System.arraycopy(intList,0,tmpIntList,0,size);
	    intList = tmpIntList;
	}
	for(int i=0;i<size;i++){
	    if(intList[i] == e){
		System.arraycopy(intList, i+1, intList, i, size-i-1);
		intList[--size] = 0;
		return true;
	    }
	}
	return false;
    }
	
    public IntList clone(){
	int[] tmparray = new int[intList.length];
	System.arraycopy(intList,0,tmparray,0,size);
	return new IntList(tmparray,size);
    }
	
    public Integer get(int i){
	if(i>=size) return null;
	return intList[i];
    }
	
    public int size(){
	return size;
    }

    @Override
	public Iterator<Integer> iterator() {
	return new Iterator<Integer>(){
	    int index = 0;
			
	    @Override
		public boolean hasNext() {
		if(index<size()) return true;
		return false;
	    }

	    @Override
		public Integer next() {
		return get(index++);
	    }

	    @Override  
	   	public void remove() {
		throw new UnsupportedOperationException();
	    }		
	};
    }
}
