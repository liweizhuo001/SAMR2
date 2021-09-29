package com.njupt.util;

import static  com.njupt.util.GlobalParams.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

import com.neo4j.ver3_3_4.Tools4Graph;
/**
 * 本体对应的图上的MIMPP类
 * @author Weizhuo Li
 *
 */


public class MIPS {
	protected Node unsat;                         //肯定路径(只包含mapping的部分)
	protected String concept;                        //否定路径,和肯定路径只有一个公共点，没有公共边(只包含mapping的部分)
	protected String negative_concept;                                        //MIPP对应的不可满足的结点   									  //MIPP对应的不可满足的结点的来源
	protected String pairSource;
	public ArrayList<Relationship> Paths1;      
	public ArrayList<Relationship> Paths2;
	public Set<Relationship> incoherenceMappings;                //产生不一致的mappings 
	
	
	
	public MIPS(Node unsat, String con1, String con2, String pairSource, Path path1, Path path2)
	{
		this.unsat = unsat;  
		this.concept = con1;
		this.negative_concept = con2;
		this.pairSource=pairSource;
		this.Paths1=pathToRelList(path1);
		this.Paths2=pathToRelList(path2);
		this.incoherenceMappings=obtainIncoherenceMappings();	
		
	}
	
	//将路径转化为关系
	public  ArrayList<Relationship> pathToRelList(Path path){
		ArrayList<Relationship> relInPath = new ArrayList<>();
		for(Relationship rel : path.relationships()){
			relInPath.add(rel);
		}
		return relInPath;
	}
	
	
	public Set<Relationship> obtainIncoherenceMappings()
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
	
	public Set<Relationship>  unifyMappings(Index<Relationship> relationshipIndex)
	{
		Set<Relationship> unifiedMapping=new HashSet<Relationship>();
		for(Relationship r:incoherenceMappings)
		{
			if(r.getProperty(TYPEPROPERTY).equals("Role"))			//只单独对纯角色的等价进行了限制
			{
				Relationship stemRelationship=null;
				String comefrom=r.getStartNode().getProperty(COMEFROMPROPERTY).toString();
				String subKey=r.getStartNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");	
				String supKey=r.getEndNode().getProperty(NAMEPROPERTY).toString().replace("existence_", "").replace("inverse_", "");
				if(subKey.equals(supKey)&&comefrom.equals(COMEFROMFIRST))
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				else if((subKey.equals(supKey)&&comefrom.equals(COMEFROMSECOND)))
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey+"_2", supKey+"_1");		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				else 
				{
					String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
					stemRelationship=relationshipIndex.get(NAMEPROPERTY, relationshipName).getSingle();
				}
				unifiedMapping.add(stemRelationship);		
			}
			else
			{
				unifiedMapping.add(r);
			}
		}
		return unifiedMapping;
	}
	
	public Set<Relationship>  unifyRels(HashMap<Relationship, String> relToMap, HashMap<String, HashSet<Relationship>> mapToRel)
	{
		Set<Relationship> unifiedMapping=new HashSet<Relationship>();
		for(Relationship r:incoherenceMappings)
		{
			
			String map=relToMap.get(r);
			unifiedMapping.addAll(mapToRel.get(map));
		}
		return unifiedMapping;
	}

	
	public Set<String>  unifyMappings(HashMap<Relationship, String>  relToMap)
	{
		Set<String> unifiedMapping=new HashSet<String>();
	
		for(Relationship r:incoherenceMappings)
		{
			String mappings= relToMap.get(r);
			unifiedMapping.add(mappings);		
		}
		return unifiedMapping;
	}
	
	public Node getUnsatNode() {
		return unsat;
	}
	
	public String getSource() {
		return (String) unsat.getProperty(COMEFROMPROPERTY);
	}
	
	public Set<Relationship> getincoherenceMappings() {
		return incoherenceMappings;
	}
	
	public void printMIPS(){
		System.out.println("The correspondence paths of MIPS are ");
		for(Relationship arc : Paths1)
		{
			System.out.print(arc.getStartNode().getProperty(NAMEPROPERTY)+"->" + arc.getEndNode().getProperty(NAMEPROPERTY)+" ");
		}
		System.out.println();
		for(Relationship arc: Paths2){
			System.out.print(arc.getStartNode().getProperty(NAMEPROPERTY)+"->" + arc.getEndNode().getProperty(NAMEPROPERTY)+" ");
		}	
		System.out.println("\n++++++++++++++++++++++");
	}
	
	public void printMappings() {
		System.out.println("The correspondence mappings of MIPS are ");
		for(Relationship arc : incoherenceMappings)
		{
			System.out.print(arc.getStartNode().getProperty(NAMEPROPERTY)+"->" + arc.getEndNode().getProperty(NAMEPROPERTY)+" ");
		}
		System.out.println();
	}

}
