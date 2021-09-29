package com.njupt.util;


/**
 * 辅助类，记录不相交的节点对以及相应的出处[2017-10]
 * @author Weizhuo Li 
 */
public class DisjointPair4Mappings {
	
	String first;
	String second;
	String comefrom;  //不交配对的出处
	public DisjointPair4Mappings(String f, String s, String comefrom){
		this.first = f;
		this.second = s;
		this.comefrom=comefrom;
	}
	
	public DisjointPair4Mappings(){
		this.first= null;
		this.second = null;
		this.comefrom=null;
	}
	
	public String getFirst() {
		return first;
	}
	public String getSecond() {
		return second;
	}
	public String getSource() {
		return comefrom;
	}
	
	@Override
	public String toString(){
		return String.format("DisjointPair(%s,%s) ", first, second);
	}
	
	public int hashCode()
	{
	    //System.out.println(this.name+"....hashCode");
	    return first.hashCode()+second.hashCode()+comefrom.hashCode();
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof DisjointPair4Mappings))
			return false;
		DisjointPair4Mappings m = (DisjointPair4Mappings)o;
		return (this.first.equals(m.first) && this.second.equals(m.second) && this.comefrom.equals(m.comefrom));
	}
	
}
