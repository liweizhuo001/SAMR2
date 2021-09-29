package com.neo4j.ver3_3_4;

import static com.njupt.util.GlobalParams.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;



import aml.AML;
import aml.match.Mapping;
import aml.ontology.Ontology2Match;
import aml.ontology.RelationshipMap;

import com.njupt.util.AxiomMapping;
import com.njupt.util.GlobalAttribute;
import com.njupt.util.MIPP;
import com.njupt.util.MappingInfo;
import com.njupt.util.OWLInfo;
import com.njupt.util.Tools;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

/**
 * 将OWLInfo与Mappings转换成图数据库，图中有两类节点，分别是概念节点和角色节点。<br>
 * 概念节点：有三个属性分别是节点名，节点类型，节点来源。<br>
 * 角色节点：属性分别是，名称：便于索引，类型：是从concept的关系还是role的关系
 * @author Weizhuo Li
 */
public class KGTriplesToGraphDB {
	public String graphDBPath;
	public GraphDatabaseService graphDb;	
	public Ontology2Match source;
	public Ontology2Match target;
	public AML aml;
	public RelationshipMap rel;
	public Vector<Mapping> mappings;
	public Index<Node> nodeIndex;                               //节点索引，方便查找节点	
	public Index<Relationship> relationshipIndex;        //关系索引，方便查找节点
	
	/**
	 * 构造函数
	 * @param dbPath           		图数据库存储路径
	 * @param owlPath1                                          本体1的路径
	 * @param owlPath2                                          本体2的路径
	 * @param MappingInformation    两个本体之间的匹配信息
	 * @param isClear               是否清除已存在的同名图数据库
	 * @throws IOException 
	 */	
	public KGTriplesToGraphDB(AML a,String dbPath, boolean isClear) throws IOException{		
		//是否清除已经存在的数据库
		graphDBPath=dbPath;
		if(isClear){
			Tools4Graph.clearDb(dbPath);
		}		
		//创建一个新的数据库或者打开一个已经存在的数据库，实例可以在多个线程中共享，但不能创建多个实例来指向同一个数据库
		//graphDb = new EmbeddedGraphDatabase(dbPath);		这是1.8版本中的表达方式，已经过期
		//graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new  File(dbPath) );
		Tools4Graph.registerShutdownHook(graphDb);  //挂钩，程序结束自动调用
		try(Transaction tx = graphDb.beginTx()){
			IndexManager indexM = graphDb.index();
			nodeIndex = indexM.forNodes(NODEINDEX);  //节点索引，参数是索引的名字			
			relationshipIndex = indexM.forRelationships(RELATIONSHIPINDEX); //关系索引
		}
		aml=a;
		source=a.getSource();
		target=a.getTarget();
		rel=a.getRelationshipMap();
		mappings=a.getAlignment().getMappingSet();	
	}
	
	public void createGraph(HashSet<Integer> nodes, HashMap<Integer,Set<Integer>> rel){		
		System.out.println("preprocessing owlInfo for graph ... ...");
		System.out.println("Create graph database from owlInfo ... ...");
		createNodes(nodes);		
		createRelationships(rel);	
		printNodeandRel();
		//this.shutdown();     //无须显式调用，已经设置了挂钩函数，程序结束会调用图的shutdown
	}
	
	/**
	 * 从本体与匹配的信息中创建节点，并记录节点的来源
	 * @param nodes             已经解析的2个本体的信息
	 */		
	public void createNodes(HashSet<Integer> nodes){
		System.out.println(String.format("  #Creating nodes from refined nodes"));
		Transaction tx = graphDb.beginTx();   //开始事务		
		try{
			for (Integer n:nodes)  
			{
				String name=aml.getURIMap().getLocalName(n);
				if(source.getClasses().contains(n))  //可能有一些匿名结点
				{
					createNode(n,name,CONCEPTLABEL,COMEFROMFIRST);
				}
				else if(source.getObjectProperties().contains(n)||source.getDataProperties().contains(n))
				{	
					createNode(n,name,ROLELABEL,COMEFROMFIRST);					
				}
				else if(target.getClasses().contains(n))
				{					
					createNode(n,name,CONCEPTLABEL,COMEFROMSECOND);
				}
				else if(target.getObjectProperties().contains(n)||target.getDataProperties().contains(n))
				{					
					createNode(n,name,ROLELABEL,COMEFROMSECOND);	
				}
				//对这个数字进行解析、获取它的概念名字，来源					
			}
			tx.success(); //提交事务，节点添加完成
		}finally{
			tx.close();
		}
	}	
	
	/**
	 * 创建图数据库单个节点并添加到index中
	 * @param name            节点的名称
	 * @param type              节点的类别
	 * @param comefrom    节点来源本体(first or second)
	 * @return 已创建或者存在的节点
	 */
	private Node createNode(int num,String name, String type, String comefrom){		
		//查询图中是否存在指定的概念节点，query的参数(节点属性名，节点属性值),索引的时候是对某个节点的某个属性来索引
		Node node = nodeIndex.query(NAMEPROPERTY, name).getSingle();
		//创建一个节点，有四个属性(原始代码中对应的编号，节点名，节点类型，节点来源)
		if(node == null){			
			node = graphDb.createNode();
			node.setProperty(NUMPROPERTY, num);
			node.setProperty(NAMEPROPERTY, name);
			node.setProperty(TYPEPROPERTY, type);			
			node.setProperty(COMEFROMPROPERTY, comefrom);	
			if(comefrom.equals(COMEFROMFIRST))  //同名的两个概念可以用不同的本体来源来进行区分
				nodeIndex.add(node, NAMEPROPERTY,name+"_1");
			else
				nodeIndex.add(node, NAMEPROPERTY,name+"_2");
		}				
		return node;
	}	
	
	public void createRelationships(HashMap<Integer,Set<Integer>> node2Parent)
	{
		System.out.println("    #Create relationship on graph of ontologies and their mappings ");
		//Set<OWLAxiom> Axioms = owlInfo.getAxiomsInTBox()
		Transaction tx = graphDb.beginTx();  //一定要加事务请求方案才行。
		try
		{
		  for(Integer child: node2Parent.keySet())
		  {
			//获取2个结点、关系的类型、关系的来源
			String subKey=aml.getURIMap().getLocalName(child);
			String supKey="";
			String comefrom="";
			String relType="";
			for(Integer parent: node2Parent.get(child))
			{
				supKey=aml.getURIMap().getLocalName(parent);
				if(source.contains(child)&&source.contains(parent))	
				{
					comefrom=COMEFROMFIRST;
					if(source.getClasses().contains(child))  //可能有一些匿名结点
					{
						relType=CONCEPTTYPE;
					}
					else if(source.getObjectProperties().contains(child)||source.getDataProperties().contains(child))
					{	
						relType=ROLETYPE;				
					}
					createRelationship(subKey, supKey, comefrom, relType);
				}
				else if(target.contains(child)&&target.contains(parent))	
				{
					comefrom=COMEFROMSECOND;
					if(target.getClasses().contains(child))  //可能有一些匿名结点
					{
						relType=CONCEPTTYPE;
					}
					else if(target.getObjectProperties().contains(child)||target.getDataProperties().contains(child))
					{	
						relType=ROLETYPE;				
					}
					createRelationship(subKey, supKey, comefrom, relType);
					
				}
				else if(source.contains(child)&&target.contains(parent))
				{
					comefrom=CONJUNCTION;
					if(source.getClasses().contains(child)&&target.getClasses().contains(parent)) //概念匹配对
					{
						relType=CONCEPTTYPE;
					}
					else if(source.getObjectProperties().contains(child)&&target.getObjectProperties().contains(parent))//对象属性匹配对
					{	
						relType=ROLETYPE;				
					}
					else if(source.getDataProperties().contains(child)&&target.getDataProperties().contains(parent))//数值属性匹配对
					{	
						relType=ROLETYPE;				
					}
					int index = aml.getAlignment().getIndexBidirectional(child, parent);
					Mapping map=aml.getAlignment().get(index);
					double weight=map.getSimilarity();
					boolean direction=true;
					createWeightedRelationship(subKey, supKey, direction, comefrom, relType, weight);
					
				}
				else if(target.contains(child)&&source.contains(parent))
				{
					comefrom=CONJUNCTION;
					if(target.getClasses().contains(child)&&source.getClasses().contains(parent)) //概念匹配对
					{
						relType=CONCEPTTYPE;
					}
					else if(target.getObjectProperties().contains(child)&&source.getObjectProperties().contains(parent))//对象属性匹配对
					{	
						relType=ROLETYPE;				
					}
					else if(target.getDataProperties().contains(child)&&source.getDataProperties().contains(parent))//数值属性匹配对
					{	
						relType=ROLETYPE;				
					}
					int index = aml.getAlignment().getIndexBidirectional(child, parent);
					Mapping map=aml.getAlignment().get(index);
					double weight=map.getSimilarity();
					boolean direction=false;
					createWeightedRelationship(subKey, supKey, direction, comefrom, relType, weight);					
				}			
			}
		}
		tx.success();
		}
		finally{
		tx.close();
	}
	}
				
	/**
	 * 创建一个图上的关系来表示包含公理，子类指向父类<br>
	 * 一个关系有三个属性：关系来源(first|second)，关系的名称(sub***sup)，关系的类型(INCLUDEDBY|MEMBEROF)
	 * @param subKey            子类标识符 
	 * @param supKey            父类表示符
	 * @param comefrom          关系来源本体
	 * @param relType           关系的类型，是role的关系还是concept的关系
	 */
	private void createRelationship(String subKey, String supKey, String comefrom, String relType){
		Relationship relationship = null;				
		Node subNode=null, supNode=null;		
		//找出节点来，图中是先把概念节点和role节点添加进去了
		try{
			if(comefrom.equals(COMEFROMFIRST))  //单个本体中的节点不可能同名
			{
			    subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_1").getSingle();		
			    supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_1").getSingle();
			}
			else 
			{
				subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_2").getSingle();		
				supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_2").getSingle();
			}
		}catch(Exception e){
			System.out.println(e);
			System.out.println("Signle ontology Exception: "+subKey + " * " + supKey);
		}			
		if(subNode!=null && supNode!=null){
			String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//中间用3个***连接	
			//IndexHits<Relationship> hits = relationshipIndex.query(NAMEPROPERTY,relationshipName); //查找关系是否存在
			//如果关系不存在，添加关系，并将新的关系添加到关系索引中(在本体匹配中可能不需要)
			//if(hits.size()==0){
				relationship = subNode.createRelationshipTo(supNode,	GlobalAttribute.RelTypes.INCLUDEDBY);
				relationship.setProperty(COMEFROMPROPERTY, comefrom);
				relationship.setProperty(NAMEPROPERTY, relationshipName);
				relationship.setProperty(TYPEPROPERTY, relType);
				//relationship.setProperty(WEIGHTEDPROPERTY, "1.0");
				relationshipIndex.add(relationship, NAMEPROPERTY, relationshipName);
			//}
		}
	}
	
	/**
	 * 创建一个图上的关系来表示mappings
	 * 一个关系有三个属性：关系来源(first|second)，关系的名称(sub***sup)，关系的类型(INCLUDEDBY|MEMBEROF)
	 * @param subKey           子类标识符 
	 * @param supKey           父类表示符
	 * @param direction        方向
	 * @param comefrom         关系来源本体
	 * @param relType          关系的类型，是role的关系还是concept的关系
	 * @param weight           mapping的权重
	 */
	private void createWeightedRelationship(String subKey, String supKey,Boolean direction, String comefrom, String relType, Double weight){
		Relationship relationship = null;				
		Node subNode=null, supNode=null;		
		//找出节点来，图中是先把概念节点和role节点添加进去了
		try{
			if(direction)//即从本体1指向本体2
			{
			  subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_1").getSingle();		
			  supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_2").getSingle();	
			}
			else //即从本体2指向本体1
			{
			  subNode = nodeIndex.query(NAMEPROPERTY, subKey+"_2").getSingle();		
			  supNode = nodeIndex.query(NAMEPROPERTY, supKey+"_1").getSingle();	
			}
		}
		catch(Exception e)
		{
			System.out.println("Mappings Exception: "+subKey + " * " + supKey);
		}			
		if(subNode!=null && supNode!=null){
			String relationshipName ="";
			if(subKey.equals(supKey)&&(direction==true))//名字可能相等的情况，即从本体1指向本体2
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey+"_1", supKey+"_2");	
			}
			else if(subKey.equals(supKey)&&(direction==false))//名字可能相等的情况，即从本体1指向本体2
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey+"_2", supKey+"_1");
			}
			else 
			{
				relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);
			}		
			//String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
			relationship = subNode.createRelationshipTo(supNode, GlobalAttribute.RelTypes.INCLUDEDBY);
			relationship.setProperty(COMEFROMPROPERTY, comefrom);
			relationship.setProperty(NAMEPROPERTY, relationshipName);
			relationship.setProperty(TYPEPROPERTY, relType);
			relationship.setProperty(WEIGHTEDPROPERTY, weight);
			relationshipIndex.add(relationship, NAMEPROPERTY, relationshipName);		
		}
	}
	
//	//不一定只是等价的情况，可能有包含的情况在里面
//	private void createWeightedRelationship(int concept1, int concept2){
//		
//		String con1=aml.getURIMap().getLocalName(concept1);
//		String con2=aml.getURIMap().getLocalName(concept2);
//		
//		String comefrom=CONJUNCTION;	
//		
//		String relType=ROLETYPE;
//		
//		Double weight=1.0;
//		
//		Relationship relationship1 = null;		
//		Relationship relationship2 = null;
//		Node subNode=null, supNode=null;		
//		//找出节点来，图中是先把概念节点和role节点添加进去了
//		try
//		{
//			  subNode = nodeIndex.query(NAMEPROPERTY, con1+"_1").getSingle();		
//			  supNode = nodeIndex.query(NAMEPROPERTY, con2+"_2").getSingle();	
//		}
//		catch(Exception e)
//		{
//			System.out.println("Mappings Exception: "+con1 + " * " + con2);
//		}			
//		if(subNode!=null && supNode!=null)
//		{
//			String relationshipName1 ="";
//			relationshipName1 = Tools4Graph.getRelationshipName(con1+"_1", con2+"_2");			
//			//String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
//			relationship1 = subNode.createRelationshipTo(supNode, GlobalAttribute.RelTypes.INCLUDEDBY);
//			relationship1.setProperty(COMEFROMPROPERTY, comefrom);
//			relationship1.setProperty(NAMEPROPERTY, relationshipName1);
//			relationship1.setProperty(TYPEPROPERTY, relType);
//			relationship1.setProperty(WEIGHTEDPROPERTY, weight);
//			relationshipIndex.add(relationship1, NAMEPROPERTY, relationshipName1);	
//			
//			String relationshipName2 ="";
//			relationshipName2 = Tools4Graph.getRelationshipName(con2+"_2", con1+"_1");			
//			//String relationshipName = Tools4Graph.getRelationshipName(subKey, supKey);		//***
//			relationship2 = supNode.createRelationshipTo(subNode, GlobalAttribute.RelTypes.INCLUDEDBY);
//			relationship2.setProperty(COMEFROMPROPERTY, comefrom);
//			relationship2.setProperty(NAMEPROPERTY, relationshipName2);
//			relationship2.setProperty(TYPEPROPERTY, relType);
//			relationship2.setProperty(WEIGHTEDPROPERTY, weight);
//			relationshipIndex.add(relationship2, NAMEPROPERTY, relationshipName2);						
//		}
//	}
	
	public List<ArrayList<Node>> getPathNodes(String unsat,String concept,String comefrom,String pairSource)
	{
		List<ArrayList<Node>> Paths=new ArrayList<ArrayList<Node>>();
		Map<String, Object> params = new HashMap<>();
		params.put( "start", unsat );	
		params.put( "end", concept );		
		params.put( "comefrom", comefrom );
	    params.put( "pairsource", pairSource );		
	    StringBuilder query = new StringBuilder();	    
	    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
	    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
	    query.append(" and uc.Name=$unsat and other.Name=$concept ");
	    query.append(" and uc.ComeFrom=$comefrom and other.ComeFrom=$pairsource ");
	    query.append("RETURN pp");
//	    query.append("RETURN LENGTH(pp) AS length, pp,");
//	    query.append("other ORDER BY length ");  //排序消耗时间
	    System.out.println(query);
	    //Result result = graphDb.execute(query.toString());
	    Result result = graphDb.execute(query.toString(),params);
	    while (result.hasNext()) 
	    {
	    	Map<String, Object> temp = result.next();		    	
	    	Path pPath = (Path) temp.get("pp");
	    	{   
	    		Paths.add(getNodes4Path(pPath));
	    		printAllPaths(pPath);	
	    	}
	    	System.out.println("===============================");
	    }   	    
	    return Paths;
	}
	
	public List<ArrayList<Relationship>> getPaths(String unsat,String concept,String comefrom,String pairSource)
	{
		List<ArrayList<Relationship>> Paths=new ArrayList<ArrayList<Relationship>>();
		Map<String, Object> params = new HashMap<>();
		params.put( "start", unsat );	
		params.put( "end", concept );		
		params.put( "comefrom", comefrom );
	    params.put( "pairsource", pairSource );		
	    StringBuilder query = new StringBuilder();	    
	    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(other) ");
	    query.append("WHERE all(n in NODES(pp) WHERE 1=length(filter(m in NODES(pp) where m=n))) ");
	    query.append(" and uc.Name=$start and other.Name=$end ");
	    query.append(" and uc.ComeFrom=$comefrom and other.ComeFrom=$pairsource ");
	    query.append("RETURN pp");
//	    query.append("RETURN LENGTH(pp) AS length, pp,");
//	    query.append("other ORDER BY length ");  //排序消耗时间
	    System.out.println(query);
	    //Result result = graphDb.execute(query.toString());
	    Result result = graphDb.execute(query.toString(),params);
	    while (result.hasNext()) 
	    {
	    	Map<String, Object> temp = result.next();		    	
	    	Path pPath = (Path) temp.get("pp");
	    	{   
	    		Paths.add(getRelationship(pPath));
	    		printAllPaths(pPath);	
	    	}
	    	System.out.println("===============================");
	    }   	    
	    return Paths;
	}
	
	public List<MIPP> getMIPPs(String unsat,String concept1,String concept2,String comefrom,String pairSource)
	{
		List<MIPP> mipps = new ArrayList<>();	
//		List<ArrayList<Relationship>> Paths1=new ArrayList<ArrayList<Relationship>>();
//		List<ArrayList<Relationship>> Paths2=new ArrayList<ArrayList<Relationship>>();	

		Transaction tx = graphDb.beginTx();  //一定要加事务请求方案才行。

		Map<String, Object> params = new HashMap<>();
		params.put( "uc", unsat );	
		params.put( "pos", concept1 );	
		params.put( "neg", concept2 );	
		params.put( "comefrom", comefrom );
	    params.put( "pairsource", pairSource );		
	    StringBuilder query = new StringBuilder();	    
	    query.append("MATCH pp=(uc)-[r1:INCLUDEDBY*]->(n),");
	    query.append("np=(uc)-[r2:INCLUDEDBY*]->(t) ");
	    query.append("WHERE all(n in r1 WHERE all(t in r2 WHERE not(n=t)))  ");
	    query.append(" and uc.Name=$uc and n.Name=$pos and t.Name=$neg ");
	    query.append(" and all(x in NODES(pp) WHERE 1=size(filter(y in NODES(pp) where x=y))) "); 
	    query.append(" and all(x in NODES(np) WHERE 1=size(filter(y in NODES(np) where x=y))) ");
	    query.append(" and uc.ComeFrom=$comefrom and n.ComeFrom=$pairsource and t.ComeFrom=$pairsource ");
	    query.append("RETURN pp,np");
	    System.out.println(query);
	    //Result result = graphDb.execute(query.toString());
	    Result result = graphDb.execute(query.toString(),params);
	    while (result.hasNext()) 
	    {
			ArrayList<Relationship> Paths1=new ArrayList<Relationship>();
			ArrayList<Relationship> Paths2=new ArrayList<Relationship>();	
	    	Map<String, Object> temp = result.next();		    	  
		    Path pPath = (Path) temp.get("pp");
		    Path nPath = (Path) temp.get("np");
//		    Paths1.add(getNodes4Path(pPath));
//		    Paths2.add(getNodes4Path(nPath));
		    Paths1=getRelationship(pPath);
		    Paths2=getRelationship(nPath);
		    //需要进行精炼～因为可能共享很多结点
		    ArrayList<Relationship> commonRels=refinePath(Paths1,Paths2);
		    Map<Relationship, Integer> commonClosure=new HashMap<Relationship, Integer>(); 
//		    Node node = nodeIndex.query(NAMEPROPERTY, unsat).getSingle();
//		    ArrayList<Node> pathNode1=new ArrayList<Node>();
//			ArrayList<Node> pathNode2=new ArrayList<Node>();
		    if(!Paths1.isEmpty()&&!Paths2.isEmpty())
		    {
		    	commonClosure=getCommnonClosure(Paths1,Paths2);
		    }		    
		    //commonclosure需要计算。
		    //定义这个数据结构，并且保证其具有contains，equals的特性
		    //MIPP mipp = new MIPP(unsat, concept1, concept2, comefrom,pairSource,Paths1,Paths2,commonClosure); 
		    MIPP mipp = new MIPP(unsat, concept1, concept2, comefrom,pairSource,Paths1,Paths2); 
		    mipps.add(mipp);
	    	System.out.println("===============================");
	    }  
	   
	    return mipps;
	}
	
	
	
	public void printAllPaths(Path p) {  
		Transaction tx = graphDb.beginTx();  //一定要加事务请求方案才行。
		try
		{
		for(Relationship rel : p.relationships()){
			Node start = rel.getStartNode();
			Node end = rel.getEndNode();
			System.out.print(start.getProperty(NAMEPROPERTY)+"->"+end.getProperty(NAMEPROPERTY)+" ");			
		}
		 tx.success();
		}
		finally
		{
			tx.close();
		}
		
		System.out.println();
	}
	
	public static ArrayList<Node> getNodes4Path(Path p)
	{		
		ArrayList<Node> nodes=new ArrayList<Node>();
		for(Node node:p.nodes())
		{
			nodes.add(node);
		}
		return nodes;
	}
	
	public static ArrayList<Relationship> getRelationship(Path p)
	{		
		ArrayList<Relationship> rels=new ArrayList<Relationship>();
		for(Relationship rel:p.relationships())
		{
			rels.add(rel);
		}
		return rels;
	}
	
	public ArrayList<Relationship> refinePath(ArrayList<Relationship> Path1,ArrayList<Relationship> Path2)
	{	
		ArrayList<Relationship> commonRels=new ArrayList<Relationship>();
		if(Path1.size()==1||Path2.size()==1)  //����Ϊ1,���뾫��
			return commonRels;	
		
//		for(Relationship rel:Path1)  //如果只有单个结点怎么办？ 那就默认自己指向自己
//		{
//			if(Path2.contains(rel))
//			{
//				commonRels.add(rel);
//			}
//		}
		Iterator<Relationship> iterator1 = Path1.iterator();
		Iterator<Relationship> iterator2 = Path2.iterator();
		
		while(iterator1.hasNext()&&iterator2.hasNext())
		{
			Relationship num1=iterator1.next();
			Relationship num2=iterator2.next();
			if(num1.equals(num2))  //判断两个关系是否相等
			{
				commonRels.add(num1);
			}	
//			else if(!commonRels.isEmpty()) //关系而不是公共的结点
//			{
//				commonRels.remove(commonRels.size());
//				break;
//			}
			else  //如果不相等，直接跳出，相等进行存储
			{
				break;
			}
		}
				
		if(!commonRels.isEmpty())
		{
			Path1.removeAll(commonRels);
			Path2.removeAll(commonRels);
		}	
		return commonRels;		
	}
	
	
	
	public Map<Relationship, Integer> getCommnonClosure(ArrayList<Relationship> Path1,ArrayList<Relationship> Path2)
	{	
		
		Map<Relationship, Integer> commonClosure=new HashMap<Relationship, Integer>();
		
		ArrayList<Relationship> mapping1=new ArrayList<Relationship>();
		ArrayList<Relationship> mapping2=new ArrayList<Relationship>();
		
		for(Relationship rel:Path1)
		{
			if(rel.getProperty(COMEFROMPROPERTY).toString().equals(CONJUNCTION))
			{ 
				mapping1.add(rel);
			}
		}		
		for(Relationship rel:Path2)
		{
			if(rel.getProperty(COMEFROMPROPERTY).toString().equals(CONJUNCTION))
			{ 
				mapping2.add(rel);
			}
		}
		
		for(Relationship rel1:mapping1)
		{
			Node node1 = rel1.getEndNode();
			List<Node> ancestor1 = getFatherNodeSignleSource(node1);
			for(Relationship rel2:mapping2)
			{
				Node node2 = rel2.getEndNode();
				List<Node> ancestor2 = getFatherNodeSignleSource(node2);
				ancestor2.retainAll(ancestor1);
				commonClosure.put(rel1, ancestor2.size());
				commonClosure.put(rel2, ancestor2.size());			
			}
			
		}
		return commonClosure;
			
	}
	
	
	//可能需要给出一个基于祖先的查找方式
	public static List<Node> getFatherNodeSignleSource(Node node){
		List<Node> nodesInName = new ArrayList<Node>();		
		//nodesInName.add(node);
		String comefrom=node.getProperty(COMEFROMPROPERTY).toString();
		Iterable<Relationship> outRels = node.getRelationships(Direction.OUTGOING);
		if(outRels.iterator().hasNext()){ //是否有入度
			for(Relationship outRel : outRels)   //如果是图的话父亲结点可能不止一个
			{
				String source=outRel.getEndNode().getProperty(COMEFROMPROPERTY).toString();
				if(comefrom.equals(source)) //保证来源相同
					nodesInName.add(outRel.getEndNode());
			}
		}		
		return nodesInName;
	}
	
	public void refreshGraphDB(HashSet<Integer> nodes, HashMap<Integer,Set<Integer>> node2Parent)
	{
		//clear dataBase
		
		Tools4Graph.clearDb(graphDBPath);
		createGraph(nodes,node2Parent);
		
	}
	public void refreshGraphDB()
	{		
		Transaction tx = graphDb.beginTx();
		try
		{
		for(Mapping map: mappings)
		{
			String conceptID1=aml.getURIMap().getLocalName(map.getSourceId());
			String conceptID2=aml.getURIMap().getLocalName(map.getTargetId());
			Node subNode = nodeIndex.query(NAMEPROPERTY,conceptID1+"_1").getSingle();		
			Node supNode = nodeIndex.query(NAMEPROPERTY,conceptID2+"_2").getSingle();
//			Node subNode = nodeIndex.query(NAMEPROPERTY, concept1+"_1").getSingle();		
//			Node supNode = nodeIndex.query(NAMEPROPERTY, concept2+"_2").getSingle();
			int nodeId1=(int) subNode.getId();
			int nodeId2=(int) supNode.getId();
			deleteRelationship2GraphDB(nodeId1,nodeId2);
			deleteRelationship2GraphDB(nodeId2,nodeId1);
			
		}
		printNodeandRel();
		tx.success();
		}
		finally
		{
			tx.close();
		}
		
		//createGraph(nodes,node2Parent);
		//createNodes(nodes);
		//createRelationships(node2Parent);		
	}
	
	public Relationship obtainRel(int index)
	{
		Mapping m=mappings.get(index);
		
		String conceptID1=aml.getURIMap().getLocalName(m.getSourceId());
		String conceptID2=aml.getURIMap().getLocalName(m.getTargetId());
//		Node subNode = nodeIndex.query(NAMEPROPERTY,conceptID1+"_1").getSingle();		
//		Node supNode = nodeIndex.query(NAMEPROPERTY,conceptID2+"_2").getSingle();
		String relationshipName = Tools4Graph.getRelationshipName(conceptID1+"_1",conceptID2+"_2");
		Relationship rel=(Relationship) relationshipIndex.query(NAMEPROPERTY, relationshipName);
		return rel;
	}
	
	
	//insert relationship，需要判断关系的类型
	public void addMappingRelationship2GraphDB(int start, int end, int direction,String relType)
	{
		
		String con1=aml.getURIMap().getLocalName(start);
		String con2=aml.getURIMap().getLocalName(end);		
		String comefrom=CONJUNCTION;	 //如果是屁屁默认赋值为	CONJUNCTION	
		//String relType=ROLETYPE;		
		Double weight=1.0;		
		Relationship relationship1 = null;
		Relationship relationship2 = null;
		Node subNode=null, supNode=null;		
		//找出节点来，图中是先把概念节点和role节点添加进去了
		try
		{
			if(direction==1)
			{
				subNode = nodeIndex.query(NAMEPROPERTY, con1+"_1").getSingle();		
				supNode = nodeIndex.query(NAMEPROPERTY, con2+"_2").getSingle();	
			}
			else if(direction==-1) //方向相反的时候,头尾结点要进行替换
			{
				subNode = nodeIndex.query(NAMEPROPERTY, con2+"_2").getSingle();	
				supNode = nodeIndex.query(NAMEPROPERTY, con1+"_1").getSingle();						
			}
			else
			{
				subNode = nodeIndex.query(NAMEPROPERTY, con1+"_1").getSingle();		
				supNode = nodeIndex.query(NAMEPROPERTY, con2+"_2").getSingle();
			}
		}
		catch(Exception e)
		{
			System.out.println("Mappings Exception: "+con1 + " * " + con2);
		}			
		if(subNode!=null && supNode!=null)
		{
			String relationshipName ="";
			if(direction==1)
			{
				relationshipName = Tools4Graph.getRelationshipName(con1+"_1", con2+"_2");			
				relationship1 = subNode.createRelationshipTo(supNode, GlobalAttribute.RelTypes.INCLUDEDBY);
				relationship1.setProperty(COMEFROMPROPERTY, comefrom);
				relationship1.setProperty(NAMEPROPERTY, relationshipName);
				relationship1.setProperty(TYPEPROPERTY, relType);
				relationship1.setProperty(WEIGHTEDPROPERTY, weight);
				relationshipIndex.add(relationship1, NAMEPROPERTY, relationshipName);	
				System.out.println("Mapping inserted successfully!");
			}
			else if(direction==-1)
			{
				relationshipName = Tools4Graph.getRelationshipName(con1+"_1",con2+"_2");			
				relationship2 = supNode.createRelationshipTo(subNode, GlobalAttribute.RelTypes.INCLUDEDBY);//区别
				relationship2.setProperty(COMEFROMPROPERTY, comefrom);
				relationship2.setProperty(NAMEPROPERTY, relationshipName);
				relationship2.setProperty(TYPEPROPERTY, relType);
				relationship2.setProperty(WEIGHTEDPROPERTY, weight);
				relationshipIndex.add(relationship2, NAMEPROPERTY, relationshipName);
				System.out.println("Mapping inserted successfully!");
			}
			else   //建议名字统一，免得检索不到，搞得太混乱。
			{
				relationshipName = Tools4Graph.getRelationshipName(con1+"_1", con2+"_2");			
				relationship1 = subNode.createRelationshipTo(supNode, GlobalAttribute.RelTypes.INCLUDEDBY);
				relationship1.setProperty(COMEFROMPROPERTY, comefrom);
				relationship1.setProperty(NAMEPROPERTY, relationshipName);
				relationship1.setProperty(TYPEPROPERTY, relType);
				relationship1.setProperty(WEIGHTEDPROPERTY, weight);
				relationshipIndex.add(relationship1, NAMEPROPERTY, relationshipName);	
				System.out.println("Mapping inserted successfully!");
				
				relationshipName = Tools4Graph.getRelationshipName(con1+"_1",con2+"_2");			
				relationship2 = supNode.createRelationshipTo(subNode, GlobalAttribute.RelTypes.INCLUDEDBY);//区别
				relationship2.setProperty(COMEFROMPROPERTY, comefrom);
				relationship2.setProperty(NAMEPROPERTY, relationshipName);
				relationship2.setProperty(TYPEPROPERTY, relType);
				relationship2.setProperty(WEIGHTEDPROPERTY, weight);
				relationshipIndex.add(relationship2, NAMEPROPERTY, relationshipName);
				System.out.println("Mapping inserted successfully!");
			}
			

		}
		
	}
	
	
	//delete relationship by node ID
	public void deleteRelationship2GraphDB(int relID)
	{
		StringBuilder query = new StringBuilder();
        query.append("start r = Relationship("+relID+")");
		query.append("MATCH (t) - [r] -> (p)"); 
		query.append("DELETE r"); 
		//query.append(" RETURN t,p "); 
		Result result = graphDb.execute(query.toString());
	    if(result.hasNext()==false)
	    	System.out.println("This relationship has been deleted!");
	    else
	    	System.out.println("This relationship has not been deleted!");
		
	}
	
	//delete relationship by node ID
	public void deleteRelationship2GraphDB(int nodeID1,int nodeID2)
	{
		StringBuilder query = new StringBuilder();
		String index="start t = Node("+nodeID1+"), p=Node("+nodeID2+")";
		query.append(index);
        //query.append("start t = Node(4), p=Node(0)");
		query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
		query.append("DELETE r"); 
		//query.append(" RETURN t,p "); 
		Result result = graphDb.execute(query.toString());
	    if(result.hasNext()==false)
	    	System.out.println("This relationship has been deleted!");
	    else
	    	System.out.println("This relationship has not been deleted!");
		
	}
	
	//delete relationship 通过具体的信息
	public void deleteRelationship2GraphDB(String start, String end, String comefrom1, String comefrom2)
	{
	    Map<String, Object> params = new HashMap<>();
	    params.put( "start", start );
	    params.put( "end", end );
	    params.put( "comefrom1", comefrom1 );
	    params.put( "comefrom2", comefrom2 );	    
		StringBuilder query = new StringBuilder();
		query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
		query.append("WHERE t.Name=$start and p.Name=$end ");
//		query.append( "and t.ComeFrom=$comefrom1 and p.ComeFrom=$comefrom2"); 
		query.append("DELETE r"); 
//		query.append(" RETURN t,p "); 
		//System.out.println(graphDb.execute(query.toString(),params));
		Result result = graphDb.execute(query.toString(),params);
	    if(result.hasNext()==false)
	    	System.out.println("This relationship has been deleted!");
	    else
	    	System.out.println("This relationship has not been deleted!");
		
	}
	
	//delete relationship 通过具体的信息
	public void deleteMappingp2GraphDB(String start, String end, String comefrom)
	{
	    Map<String, Object> params = new HashMap<>();
	    params.put( "start", start );
	    params.put( "end", end );
	    params.put( "comefrom", comefrom );	    
		StringBuilder query = new StringBuilder();
		query.append("MATCH (t) - [r:INCLUDEDBY] -> (p)"); 
		query.append("WHERE t.Name=$start and p.Name=$end ");
		query.append( "and r.ComeFrom=$comefrom"); 
		query.append("DELETE r"); 
//		query.append(" RETURN t,p "); 
		//System.out.println(graphDb.execute(query.toString(),params));
		Result result = graphDb.execute(query.toString(),params);
	    if(result.hasNext()==false)
	    	System.out.println("Mapping deleted successfully!");
	    else
	    	System.out.println("Mapping deleted successfully!");
		
	}
	
	public void printDeleteResutls(int relID)
	{
		StringBuilder query = new StringBuilder();
		//query.delete(0, query.length());		
        query.append("start r = Relationship("+relID+")");
		query.append("MATCH (t) - [r] -> (p)"); 
		query.append("RETURN r "); 	    
	    Result result = graphDb.execute(query.toString());
	    while (result.hasNext()) 
	    {
	    	Map<String, Object> temp = result.next();
            for ( String key : result.columns() )  //对应的按列来进行读取
            {
               System.out.printf( "%s = %s%n", key, temp.get( key ) );  //%n为换行的格式字符串,只能用在print输出语句中
               Relationship rel=(Relationship) temp.get( key );
               System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY).toString()+" "+rel.getEndNode().getProperty(NAMEPROPERTY));	
               System.out.println(rel.toString()+" "+rel.getProperty(TYPEPROPERTY).toString());
               System.out.println(rel.getType().name());		
               System.out.println("===============================");
            }
	    }
	}
	
	public void printNodeandRel()
	{
		Transaction tx = graphDb.beginTx();   //开始事务		
		try{
		int i=0;
		for (Node node : graphDb.getAllNodes()) {
			++i;
			for(String s:node.getPropertyKeys())
			{
				System.out.println(s);				
			}
			if (node.hasProperty(NAMEPROPERTY))
			{
//				for(Label label : node.getLabels())
//				{
//					System.out.print(label+"  :  ");
//				}
				System.out.println(node);				
				System.out.println(node.getProperty(NAMEPROPERTY) +"  "+node.getProperty(COMEFROMPROPERTY));
			}			
		}
		System.out.println(String.format("Number of nodes is %d", i));
		
		i=0;
		for(Relationship rel :  graphDb.getAllRelationships()){
			++i;
			System.out.println("-----------------------------");
			System.out.println(rel+" "+rel.getId());
			System.out.println(rel.getStartNode().getProperty(NAMEPROPERTY)+" "+rel.getEndNode().getProperty(NAMEPROPERTY));
			System.out.println(rel.getProperty(TYPEPROPERTY).toString()+ "  "+rel.getProperty(COMEFROMPROPERTY)+"  "+rel.getType());
			if(rel.hasProperty(WEIGHTEDPROPERTY))  //判断是否有这个属性
			{
				System.out.println(rel.getProperty(WEIGHTEDPROPERTY).toString());
			}
		}
		System.out.println(String.format("Number of relationship is %d", i));	
		tx.success(); //提交事务，节点添加完成
	  }
		finally
	  {
		tx.close();
	  }
	}
	
	/**
	 * 正常关闭图数据库
	 */
	public void shutdown(){		
		this.graphDb.shutdown();
		System.out.println("Shut down graph database.\n");
	}
}
	

	

