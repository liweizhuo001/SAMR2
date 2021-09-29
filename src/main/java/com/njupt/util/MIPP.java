package com.njupt.util;

import static  com.njupt.util.GlobalParams.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
/**
 * 本体对应的图上的MIMPP类
 * @author Weizhuo Li
 *
 */


public class MIPP {
	protected String unsat;                         //肯定路径(只包含mapping的部分)
	protected String concept;                        //否定路径,和肯定路径只有一个公共点，没有公共边(只包含mapping的部分)
	protected String negative_concept;                                        //MIPP对应的不可满足的结点   
	protected String comefrom;									  //MIPP对应的不可满足的结点的来源
	protected String pairSource;
	public ArrayList<Relationship> Paths1;      
	public ArrayList<Relationship> Paths2;
	public Map<Relationship, Integer> commonClosure;
	public Set<Relationship> incoherenceMappings;                //产生不一致的mappings  
	
	
	
	public MIPP(String unsat, String con1, String con2,String comefrom, String pairSource, ArrayList<Relationship> path1, 
			ArrayList<Relationship> path2)
	{
		this.unsat = unsat;  
		this.concept = con1;
		this.negative_concept = con2;
		this.comefrom=comefrom;
		this.pairSource=pairSource;
		this.Paths1=path1;
		this.Paths2=path2;
		this.incoherenceMappings=getincoherenceMappings();		
	}
	
	
	public Set<Relationship> getincoherenceMappings()
	{
		Set<Relationship> diags = new HashSet<Relationship>();
		for(Relationship rel:Paths1){
			if(rel.getProperty(COMEFROMPROPERTY).toString().equals(CONJUNCTION)){ 
				diags.add(rel);
			}
		}
		for(Relationship rel:Paths2){
			if(rel.getProperty(COMEFROMPROPERTY).toString().equals(CONJUNCTION)){
				diags.add(rel);
			}
		}
		return diags;
	}
	
	public String getUnsatNode() {
		return unsat;
	}
	
	public String getSource() {
		return comefrom;
	}
	
	public void printMIMPP(){
		for(Relationship arc : Paths1){
			System.out.println(arc.getStartNode().getProperty(NAMEPROPERTY)+"->" + arc.getEndNode().getProperty(NAMEPROPERTY));
		}
		for(Relationship arc: Paths2){
			System.out.println(arc.getStartNode().getProperty(NAMEPROPERTY)+"->" + arc.getEndNode().getProperty(NAMEPROPERTY));
		}		
	}

}
