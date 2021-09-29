package com.njupt.util;

import static com.njupt.util.GlobalParams.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.parboiled.parserunners.ReportingParseRunner;

import com.neo4j.ver3_3_4.DoulbeOWLMappingToGraphDB;



/**
 * 基于图数据库的Mappings的修正主函数
 * 先从图中计算MIPP，再从MIPP中提取MIMPP
 * [2017-11-30]
 * @author Weizhuo Li
 */

public class ScoringMappingRevision {
	String gPath; 
	String URI1="",URI2="";
	
	GraphDatabaseService graphDB; 	
	DoulbeOWLMappingToGraphDB owlToGraph; //解析方式
	ArrayList<DisjointPair4Mappings> disPairs; //不相交的匹配对
	ArrayList<MIPS> MIPPs; 
	Map<Relationship, List<MIPS>> relMappingMIPSs;
	ArrayList<Relationship> removedMappingRelations;
	
	ArrayList<String> referenceMappings;
	
//	MappingDiagnosis diag ;
//    List<MIMPP> mimpps;     //TBox中所有的MIMPPS(即最小冲突子集)
//    Map<Relationship, List<MIMPP>> relMappingMIMPP; //每个匹配对对应的最小冲突子集
//    
//    HashMap<String, Double> mappings;  //每个匹配，对应一个相应的confidence
//    
//    Set<String> mappingNodes;  //把mappings中涉及到的Node存储下来(无法修改，因为当初你不知道它是概念还是属性)
//    
//    List<Relationship> mappingRelationship;
//    List<Relationship> removedRelationship;
//    ArrayList<String> removedMappings;
//    ArrayList<String> candidatMappings;
//    
//    HashMap<Relationship,HashMap<Node,Double>> candidateRelationships;
//    ArrayList<String> addedRelationship;
	
	public ScoringMappingRevision(String gPath,String oFile1, String oFile2,String mappingPaths, ArrayList<String> referenceMappings) throws IOException
	{
		this.gPath = gPath; 
		this.referenceMappings=referenceMappings;
		this.owlToGraph = new DoulbeOWLMappingToGraphDB(gPath, oFile1, oFile2,mappingPaths,true);
		URI1=owlToGraph.getOwlInfo1().URI.replace(".owl", "").replace(".rdf", "");
		URI2=owlToGraph.getOwlInfo2().URI.replace(".owl", "").replace(".rdf", "");
		
		owlToGraph.createDbFromOwl();
		
		disPairs=owlToGraph.getDisjointPairs(); //获取不相交的结点对
		
		MIPPs=owlToGraph.getMIPPs(disPairs);
		
		relMappingMIPSs=owlToGraph.calRMappingM(MIPPs);
		
		//简单的移除策略
		//removedMappingRelations=owlToGraph.goRevisionSimple(relMappingMIPSs,MIPPs); //简单基于权重大小进行移除
		//removedMappingRelations=owlToGraph.goRevisionComplex(relMappingMIPSs,MIPPs);  //简单基于权重大小进行移除
		
		//交互的移除策略
		//removedMappingRelations=owlToGraph.goRevisionByInteractiveOne2One();  // 专家交互式的判断方法，推理方式1-1的约束
		//removedMappingRelations=owlToGraph.goRevisionByInteractiveByReasoner();  //专家交互式的判断方法，推理方式为Meilicke提出bridge rule
		removedMappingRelations=owlToGraph.goRevisionByInteractiveByEnhancedReasoner();  //专家交互式的判断方法，推理方式为基于图的必包，影响函数不同
		
		
		//基于神谕的移除策略
		//removedMappingRelations=owlToGraph.goRevisionByAutoOne2One(referenceMappings);  //神谕的判断方法，推理方式1-1的约束
		//removedMappingRelations=owlToGraph.goRevisionByAutoByReasoner(referenceMappings);  //神谕的判断方法，推理方式为Meilicke提出bridge rule
		//removedMappingRelations=owlToGraph.goRevisionByAutoByEnhancedReasoner(referenceMappings);  //神谕的判断方法，推理方式为基于图的必包，影响函数不同
		
		
			
		owlToGraph.shutdown();		
		//this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(new File(gPath));	
	}
	
	
	
	public ArrayList<Relationship> getRemovedRelationships()
	{
		return removedMappingRelations;
	}
	
	public ArrayList<String> getRemovedMappings()
	{
		return owlToGraph.removedMappings;
	}
	
	public ArrayList<String> getApprovedMappings()
	{
		return owlToGraph.approvedMappings;
	}
	
	public int approvedNums()
	{
		return owlToGraph.approvedNum;
	}
	
	public int rejectedNums()
	{
		return owlToGraph.rejectNum;
	}
	
	public String getURI1()
	{
		return owlToGraph.getOwlInfo1().URI;
	}
	
	public String getURI2()
	{
		return owlToGraph.getOwlInfo2().URI;
	}
	


}
