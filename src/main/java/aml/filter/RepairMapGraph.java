/******************************************************************************
* Copyright 2013-2016 LASIGE                                                  *
*                                                                             *
* Licensed under the Apache License, Version 2.0 (the "License"); you may     *
* not use this file except in compliance with the License. You may obtain a   *
* copy of the License at http://www.apache.org/licenses/LICENSE-2.0           *
*                                                                             *
* Unless required by applicable law or agreed to in writing, software         *
* distributed under the License is distributed on an "AS IS" BASIS,           *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
* See the License for the specific language governing permissions and         *
* limitations under the License.                                              *
*                                                                             *
*******************************************************************************
* Map of extended relationships of classes involved in disjoint clauses with  *
* mappings from a given Alignment, which supports repair of that Alignment.   *
*                                                                             *
* @authors Daniel Faria & Emanuel Santos                                      *
******************************************************************************/
package aml.filter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.Refreshable;

import org.apache.commons.lang.StringEscapeUtils;
import org.jfree.xml.generator.model.MappingModel;

import aml.AML;
import aml.match.Alignment;
import aml.match.Mapping;
import aml.ontology.Relationship;
import aml.ontology.RelationshipMap;
import aml.settings.EntityType;
import aml.settings.MappingRelation;
import aml.settings.MappingStatus;
import aml.util.Graph;
import aml.util.KG;
import aml.util.LinearProgram;
import aml.util.Pair;
import aml.util.Table2Map;
import aml.util.Table2Set;
import aml.util.Table3List;
import aml.util.Table3Set;

public class RepairMapGraph implements Iterable<Integer>
{
	
//Attributes
	
	private AML aml;
	private RelationshipMap rels;
	private Alignment a;
	private Vector<Mapping> maps;
	//The list of classes that are relevant for coherence checking
	private HashSet<Integer> classList;
	//The list of classes that must be checked for coherence
	private HashSet<Integer> checkList;
	//The  map of parents relations of checkList classes
	//(class Id, Set of parents class Id)
	private HashMap<Integer,Set<Integer>> originalMap;	
	private HashMap<Integer,Set<Integer>> parentMap;
	//The minimal map of ancestor relations of checkList classes
	//(checkList class Id, classList class Id, Path)	
	private Table3List<Integer,Integer,Path> ancestorMap;  //�洢��path���ǽ�������mapping ��path
	//The length of ancestral paths to facilitate transitive closure
	//(checklist class Id, Path length, classList class Id)
	
	//��������ݽṹ���ܶ���Ҫ����������ancestorMap����Ҫ�Ż�һ��
	private Table3Set<Integer,Integer,Integer> pathLengths;  //���path�������Ǻ���mapping��path	
	//The number of paths to disjoint classes
	private int pathCount;
	//The list of conflict sets
	private Vector<Path> conflictSets;
	//The table of conflicts per mapping
	private Table2Set<Integer,Integer> conflictMappings;
	private Table2Set<Integer,Integer> mappingConflicts;

	//The available CPU threads
	private int threads;
	
	public Graph graph;
	//public HashSet<MIPP> MinimalConflictSet;
	public HashSet<Pair<ArrayList<Integer>,ArrayList<Integer>>> MinimalConflictSet;
	//public ConcurrentSkipListSet<Pair<ArrayList<Integer>,ArrayList<Integer>>> MinimalConflictSet;
	public HashMap<Integer,ArrayList<Pair<ArrayList<Integer>,ArrayList<Integer>>>> MapMinimalConflictSet;
	public ConcurrentHashMap<String,Boolean> checkedState;  	//it can reduce the time consumption
	public HashMap<Integer,Set<Integer>> ancestors;
	public int entailedNum=0,rejectNum=0;
	
	
//Constructors
	
	/**
	 * Constructs a new RepairMap
	 */
	public RepairMapGraph()
	{
		aml = AML.getInstance();
		rels = aml.getRelationshipMap();
		//We use a clone of the alignment to avoid problems if the
		//order of the original alignment is altered
		a = new Alignment(aml.getAlignment());
		//Remove the FLAGGED status from all mappings that have it
		for(Mapping m : a)
			if(m.getStatus().equals(MappingStatus.FLAGGED))
				m.setStatus(MappingStatus.UNKNOWN);
		threads = Runtime.getRuntime().availableProcessors();
		graph=new Graph();
		init();
	} 
	
//Public Methods
	
	/**
	 * @param index: the index of the Mapping to get
	 * @return the conflict sets that contain the given Mapping index
	 */
	public Set<Integer> getConflicts(int index)
	{
		return mappingConflicts.get(index);
	}
	
	/**
	 * @param m: the Mapping to get
	 * @return the list of Mappings in conflict with this Mapping
	 */
	public Vector<Mapping> getConflictMappings(Mapping m)
	{
		int index = a.getIndex(m.getSourceId(), m.getTargetId());
		Vector<Mapping> confs = new Vector<Mapping>();
		if(!mappingConflicts.contains(index))
			return confs;
		for(Integer i : mappingConflicts.get(index))
		{
			for(Integer j : conflictMappings.get(i))
			{
				if(j == index)
					continue;
				Mapping n = a.get(j);
				//Get the Mapping from the original alignment
				n = aml.getAlignment().get(n.getSourceId(), n.getTargetId());
				if(!confs.contains(n))
					confs.add(n);
			}
		}
		return confs;
	}
	
	/**
	 * @return the list of conflict sets of mappings
	 * in the form of indexes (as per the alignment
	 * to repair)
	 */
	public Vector<Path> getConflictSets()
	{
		return conflictSets;
	}
	
	/**
	 * @return the list of minimal incoherence conflict set (as per the alignment
	 * to repair deeply)
	 */
	/*public HashSet<MIPP> getMinimalConflictSets()
	{
		return MinimalConflictSet;
	}*/
	
	public HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> getMinimalConflictSets()
	{
		return MinimalConflictSet;
	}
	
	/**
	 * @return the list of minimal incoherence conflict set (as per the alignment
	 * to repair deeply)
	 */
	public HashMap<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>> getMapMinimalConflictSets()
	{
		return MapMinimalConflictSet;
	}
	
	/**
	 * @param m: the Mapping to search in the RepairMap
	 * @return the index of the Mapping in the RepairMap
	 */
	public int getIndex(Mapping m)
	{
		return a.getIndex(m.getSourceId(), m.getTargetId());
	}
	
	/**
	 * @param source: the id of the source class to search in the RepairMap
	 * @param target: the id of the target class to search in the RepairMap
	 * @return the index of the Mapping between source and target in
	 * the RepairMap
	 */
	public int getIndex(int source, int target)
	{
		return a.getIndex(source, target);
	}

	/**
	 * @param index: the index of the Mapping to get
	 * @return the Mapping at the given index
	 */
	public Mapping getMapping(int index)
	{
		Mapping m = a.get(index);
		return aml.getAlignment().get(m.getSourceId(), m.getTargetId());
	}
	
	/**
	 * @return whether the alignment is coherent
	 */
	public boolean isCoherent()
	{
		//return conflictSets == null || conflictSets.size() == 0;
		return MinimalConflictSet==null ||MinimalConflictSet.size()==0;
	}
	
	
	@Override
	public Iterator<Integer> iterator()
	{
		return mappingConflicts.keySet().iterator();
	}
	
	/**
	 * Sets a Mapping as incorrect and revise its conflicts from
	 * the RepairMapRefine (but does not actually remove the Mapping from
	 * the Alignment)
	 * @param index: the index of the Mapping to revise
	 */
	public boolean reviseJudge(int index)
	{
		ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> mipps=MapMinimalConflictSet.get(index); 
		System.out.println("The number of related Minimal conflict sets is "+ mipps.size());
		ArrayList<KG> KGs= new ArrayList<KG>();		
	    for(Pair<ArrayList<Integer>, ArrayList<Integer>>  pair: MapMinimalConflictSet.get(index))
	    {
	    	KG mipp=generateKG(pair.left,pair.right);
	    	KGs.add(mipp);
	    }	
		ArrayList<Integer> sourceConcepts=new ArrayList<Integer> ();
		ArrayList<Integer> targetConcepts=new ArrayList<Integer> ();
		Map<Integer, Set<Integer>> sourceRel =new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> targetRel =new HashMap<Integer, Set<Integer>>();
		ArrayList<Integer> mappings=new ArrayList<Integer>();
				
		for(KG kg:KGs)
		{
			for(Integer concept:kg.getSourceConcept())
			{
				if(!sourceConcepts.contains(concept))
					sourceConcepts.add(concept);
			}
			for(Integer concept:kg.getTargetConcept())
			{
				if(!targetConcepts.contains(concept))
					targetConcepts.add(concept);
			}
			for(String relation:kg.getSourceRel())
			{
				String parts[]=relation.split("--");
				int sub=Integer.parseInt(parts[0]);
				int parent=Integer.parseInt(parts[1]);
				if (sourceConcepts.contains(parent)) 
				{
					if (sourceRel.keySet().contains(sub)) 
					{
						Set<Integer> set = sourceRel.get(sub);
						set.add(parent);
						sourceRel.put(sub, set);
					} else {
						Set<Integer> set = new HashSet<Integer>();
						set.add(parent);
						sourceRel.put(sub, set);
					}
				}				
			}
			for(String relation:kg.getTargetRel())
			{
				String parts[]=relation.split("--");
				int sub=Integer.parseInt(parts[0]);
				int parent=Integer.parseInt(parts[1]);
				if (targetConcepts.contains(parent)) 
				{
					if (targetRel.keySet().contains(sub)) 
					{
						Set<Integer> set = targetRel.get(sub);
						set.add(parent);
						targetRel.put(sub, set);
					} else {
						Set<Integer> set = new HashSet<Integer>();
						set.add(parent);
						targetRel.put(sub, set);
					}
				}		
				
			}
			for(Integer m:kg.getMappings())
			{
				if(!mappings.contains(m))
					mappings.add(m);
			}
		}
		
		Map<Integer, Set<Integer>> sourceDisRel =new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> targetDisRel =new HashMap<Integer, Set<Integer>>();
		
		for(Integer con:sourceConcepts)
		{
			for(Integer dis:rels.getDisjoint(con))
			{
				if(sourceConcepts.contains(dis))  //����ģ�黯�ڲ��ĸ������
				{
					if(sourceDisRel.keySet().contains(con))
					{
						Set<Integer> map=sourceDisRel.get(con);
						map.add(dis);
						sourceDisRel.put(con, map);
					}
					else
					{
						Set<Integer> map =new HashSet<Integer>();
						map.add(dis);
						sourceDisRel.put(con, map);
					}
				}
			}
		}
		
		for(Integer con:targetConcepts)
		{
			for(Integer dis:rels.getDisjoint(con))
			{
				if(targetConcepts.contains(dis))  //����ģ�黯�ڲ��ĸ������
				{
					if(targetDisRel.keySet().contains(con))
					{
						Set<Integer> map=targetDisRel.get(con);
						map.add(dis);
						targetDisRel.put(con, map);
					}
					else
					{
						Set<Integer> map =new HashSet<Integer>();
						map.add(dis);
						targetDisRel.put(con, map);
					}
				}
			}
		}

		LinearProgram LP1= new LinearProgram();
		LinearProgram LP2= new LinearProgram();
		LinearProgram LPGlobal= new LinearProgram();

		LP1.Intial(sourceConcepts);
		LP1.encodingConstraint(sourceRel,sourceDisRel);	
		LP1.generatePossbileWorld();
		LP1.generatePossbileWorldIndex();

		LP2.Intial(targetConcepts);
		LP2.encodingConstraint(targetRel,targetDisRel);
		LP2.generatePossbileWorld();
		LP2.generatePossbileWorldIndex();
		
		int MergedWorldNumber=LP1.getPossibleWordIndex().size()*LP2.getPossibleWordIndex().size();
		if(MergedWorldNumber>=10000)  //��Ϊ���㸴�Ӷȶ�������Э
		{
			//revise(index,0.1);
			revise2(index,0.1);
			return true;			
		}
		
		ArrayList<String> conditionalConstraints=new ArrayList<String>();
		ArrayList<String> initialMappings=new ArrayList<String>();
		
		for(Integer m:mappings)
		{
			//String string="";
			Mapping map=a.get(m);
			if(map.getRelationship().toString().equals("="))
				initialMappings.add(map.getSourceId()+","+map.getTargetId()+","+map.getRelationship().toString()+","+map.getSimilarity()+","+1.0);
			else if(map.getRelationship().toString().equals("<")) //��Ҫ�����logMap
				initialMappings.add(map.getSourceId()+","+map.getTargetId()+","+"|"+","+map.getSimilarity()+","+1.0);
			System.out.println(map.getSourceId()+","+map.getTargetId()+","+map.getRelationship().toString()+","+map.getSimilarity()+","+1.0);
		}
		//��ԭʼ�ĳ�ͻ�����Ƴ�����	
		LPGlobal.mergeConcept(sourceConcepts,targetConcepts);
		LPGlobal.mergePossbileWorldIndex(LP1.getPossibleWordIndex(),LP2.getPossibleWordIndex());
		LPGlobal.addhardConstraints(conditionalConstraints);	
		LPGlobal.addMapping(initialMappings);  //֮ǰ�Ѿ���ӹ�һ����
		
		Boolean flag=LPGlobal.isProSatifiable(initialMappings);
		System.out.println("The mappings are coherent��:  "+flag);
		if(flag==false)  //���ɽ⣬��Ҫ�����޸�
		{
			Mapping RevisedMap=a.get(index);
			String mapInformation=RevisedMap.getSourceId()+","+RevisedMap.getTargetId()+","+RevisedMap.getRelationship().toString()+","+RevisedMap.getSimilarity()+","+1.0;
			LPGlobal.Revised(mapInformation);				
			String parts[] = LPGlobal.getRevisedMapping().split(",");
			//revise(index,Double.parseDouble(parts[4]));
			revise2(index,Double.parseDouble(parts[4]));
			return true;
		}
		else //��ì�ܣ�ɶ�����޸�
		{
			//remove(index,false);
			remove2(index,false);
			return false;
		}		
	}
	
	public Vector<Mapping> structureRefine(Vector<Mapping> mappings, HashSet<Integer> indexSet)
	{
		Vector<Mapping> tempMapping =new Vector<Mapping>();
//		boolean flag[]=new boolean[mappings.size()];
//		int num=0;
		for(Mapping m:mappings)
		{
//			num++;
//			System.out.println("The number of checking mapping is "+ (num-1));
			//judge which mapping is checked
//			if(flag[mappings.indexOf(m)])
//				continue;		
			//专门抽出来，作为一个功能，基于一个m返回
			
			int sourceConcept=m.getSourceId();	

			Set<Integer> sourceAncestors=rels.getSuperClasses(sourceConcept, true);
			Set<Integer> sourceDescendants=rels.getSubClasses(sourceConcept, true);
			
			int targetConcept=m.getTargetId();
			Set<Integer> targetAncestors=rels.getSuperClasses(targetConcept, true);
			Set<Integer> targetDescendants=rels.getSubClasses(targetConcept, true);

			//find the potential mappings based on concepts' ancestors
			Vector<Mapping> tempAncestorsMapping=new Vector<Mapping>();
			HashSet<Integer> riskfather=new HashSet<Integer> ();
			for(int conId:sourceAncestors)
			{	
				//Set<Integer> targetConceptSet = aml.getAlignment().getSourceMappings(conId);	
				Set<Integer> targetConceptSet = new HashSet<Integer>(aml.getAlignment().getSourceMappings(conId));			
				targetConceptSet.retainAll(targetAncestors);
				for(int id:targetConceptSet)
				{
					Mapping ancestorMapping = aml.getAlignment().getBidirectional(conId, id);
					int mapID=aml.getAlignment().getIndex(conId, id);
					if(indexSet.contains(mapID))
						riskfather.add(mapID);
					else
						tempAncestorsMapping.add(ancestorMapping);
				}			
			}
			
			//find the potential mappings based on concepts' descendants
			Vector<Mapping> tempDescendantsMapping=new Vector<Mapping>();
			HashSet<Integer> riskchildren=new HashSet<Integer> ();
			for(int conId: sourceDescendants)
			{
				//Set<Integer> targetConceptSet = aml.getAlignment().getSourceMappings(conId);
				Set<Integer> targetConceptSet = new HashSet<Integer>(aml.getAlignment().getSourceMappings(conId));
				targetConceptSet.retainAll(targetDescendants);
				for(int id:targetConceptSet)
				{
					Mapping descendantsMapping = aml.getAlignment().getBidirectional(conId, id);
					int mapID=aml.getAlignment().getIndex(conId, id);
					if(indexSet.contains(mapID))
						riskchildren.add(mapID);
					else
						tempDescendantsMapping.add(descendantsMapping);
				}
			}
//			System.out.println(riskfather.size()*1.0/tempAncestorsMapping.size());
//			System.out.println(riskchildren.size()*1.0/tempDescendantsMapping.size());
			
			if(!tempAncestorsMapping.isEmpty()||!tempDescendantsMapping.isEmpty())
			{
//				flag[mappings.indexOf(m)]=true;
				if(riskfather.isEmpty()&&riskchildren.isEmpty()&&!tempMapping.contains(m)) //rough method
					tempMapping.add(m);	
//				if(riskfather.isEmpty()&&(tempDescendantsMapping.size()>=riskchildren.size())&&!tempMapping.contains(m)) 				
//					tempMapping.add(m);	

			}
			
//			if(!tempAncestorsMapping.isEmpty()||!tempDescendantsMapping.isEmpty())
//			{
//				flag[mappings.indexOf(m)]=true;
//				tempMapping.add(m);
//				for(Mapping am:tempAncestorsMapping)
//				{
//					if(!tempMapping.contains(am))	
//						tempMapping.add(am);	
//					flag[mappings.indexOf(am)]=true;
//				}
//				for(Mapping dm:tempDescendantsMapping)
//				{
//					if(!tempMapping.contains(dm))	
//						tempMapping.add(dm);	
//					flag[mappings.indexOf(dm)]=true;
//				}
//			}		
		}
		//updateGraph(tempMapping);
		return tempMapping;	
	}

	
	public void updateGraph(Mapping m)
	{
		
			int source=m.getSourceId();
			int target=m.getTargetId();
			if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(source, target);
			}
			if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(target, source);
			}
	}
	
	public void updateConflictSet(Mapping map, ArrayList<Mapping> reliableMappings,ArrayList<Mapping> unWantMappings)
	{
		
		int sourceId=map.getSourceId();
		int targetId=map.getTargetId();
		int index = aml.getAlignment().getIndex(sourceId, targetId);
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		HashSet<Integer> unWantMappingIndex=new HashSet<Integer>();
		indexSet.retainAll(MapMinimalConflictSet.keySet()); //�������Ա�֤
		for(Integer in:indexSet)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) {
				//通过路径来获取mappings(这里的mapping应该均是尚未标记的,因为无论执行approve或者reject操作，mapping相应的MIPS都会被消除)
				Set<Integer> mappings = getMappings(mipp.left, mipp.right); 
				// if (mappings.size() == 2)
				if (existRejectiveMapping(mappings, reliableMappings, in)) // 这里可以结合wantmapping来消除冲突的情况
				{
					mappings.remove(in);
					for (Integer num : mappings) // 如果mappings集合除了index中的这个mapping，剩下的这个一定是要被拒绝的。
					{
						Mapping m = maps.get(num);
						m.setStatus(MappingStatus.INCORRECT);
						aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
						if (!unWantMappings.contains(m))
							unWantMappings.add(m);
						int pairs[] = restoreMapping(m);
						Set<Integer> potentialRemovedMappings = new HashSet<>();
						// 对于角色属性进行一个潜在判断
						int incorrectStart = pairs[0];
						int incorrectEnd = pairs[1];
						if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd, reliableMappings,unWantMappings);
						} 
						else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd, reliableMappings,unWantMappings);
						}

						for (Integer potentialmapping : potentialRemovedMappings) 
						{
							if (MapMinimalConflictSet.containsKey(potentialmapping))
								unWantMappingIndex.add(potentialmapping);
						}
					}
					unWantMappingIndex.addAll(mappings);
				}
			}		
		}
			
		//更新MIPSs
		for(Integer removedMapping: unWantMappingIndex)
		{
			if(!MapMinimalConflictSet.keySet().contains(removedMapping))
				continue;
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> relatedMIPP=new HashSet(MapMinimalConflictSet.get(removedMapping));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(removedMapping));
			MapMinimalConflictSet.remove(removedMapping);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Integer r = iter.next().getKey();			
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), relatedMIPP);
				if (leftMIPP.isEmpty()) // 为空则直接移除
				{
					iter.remove();
				} else // 更新原来的MIPPSets的大小
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}			
		}
	}

	
	public void one2oneRestriction(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);
		wantMappings.add(m);
		
		int correctpairs[]=restoreMapping(m);													
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int correctStart = correctpairs[0];
		int correctEnd = correctpairs[1];
		if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
		{			
			extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
		{
			extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}
		
		int sourceId=m.getSourceId();
		int targetId=m.getTargetId();
		
		for(int i=0;i<maps.size();i++)  
		{		
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap = a.get(i);
				int source = tempMap.getSourceId();
				int target = tempMap.getTargetId();
				
				if(sourceId==source||targetId==target)  //����������ͬ��Ŷ�Ӧ�ñ��ܾ�
				{
					tempMap.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(tempMap.getSourceId(), tempMap.getTargetId()).setStatus(MappingStatus.INCORRECT);
					unWantMappings.add(tempMap);
					
					int pairs[]=restoreMapping(tempMap);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					int incorrectStart = pairs[0];
					int incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}
				}			
			}
		}
	}
	
	//Christian�� Tool��֧�ַ���
	public void entailBasedApprove(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		//��ȡMIPSs��Ψһ�뵱ǰ��ͬmappingì�ܵ�mapping
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		HashSet<Integer> unWantMappingIndex=new HashSet<Integer>();
		indexSet.retainAll(MapMinimalConflictSet.keySet()); //�������Ա�֤
		for(Integer in:indexSet)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) {
				//ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = getMappings(mipp.left, mipp.right); 
				// if (mappings.size() == 2)
				if (existRejectiveMapping(mappings, wantMappings, in)) // ������Խ��wantmapping��������ͻ�����
				{
					mappings.remove(in);
					for (Integer num : mappings) // ���mappings���ϳ���index�е����mapping��ʣ�µ����һ����Ҫ���ܾ��ġ�
					{
						Mapping m = maps.get(num);
						m.setStatus(MappingStatus.INCORRECT);
						aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
						if (!unWantMappings.contains(m))
							unWantMappings.add(m);

						int pairs[] = restoreMapping(m);
						Set<Integer> potentialRemovedMappings = new HashSet<>();
						// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
						int incorrectStart = pairs[0];
						int incorrectEnd = pairs[1];
						if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						} 
						else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						}

						for (Integer potentialmapping : potentialRemovedMappings) 
						{
							if (MapMinimalConflictSet.containsKey(potentialmapping))
								unWantMappingIndex.add(potentialmapping);
						}
					}
					unWantMappingIndex.addAll(mappings);
				}
			}		
		}
			
		//����MIPSs
		for(Integer removedMapping: unWantMappingIndex)
		{
			if(!MapMinimalConflictSet.keySet().contains(removedMapping))
				continue;
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> relatedMIPP=new HashSet(MapMinimalConflictSet.get(removedMapping));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(removedMapping));
			MapMinimalConflictSet.remove(removedMapping);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Integer r = iter.next().getKey();			
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), relatedMIPP);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}			
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);
		int source = m.getSourceId();
		int target = m.getTargetId();
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(source, target);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(target, source);
		}
		
		//��Ϊmapping�Ѿ���ͬ�ˣ�����want��unwant���漰m���ӹ�ϵ����Ҫȥ��
		RefreshSet(wantMappings,m);	
		wantMappings.add(m);
		RefreshSet(unWantMappings,m);		
		
		//�漰��ɫ��һ��̽��
		int correctpairs[]=restoreMapping(m);													
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int correctStart = correctpairs[0];
		int correctEnd = correctpairs[1];
		if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
		{			
			extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
		{
			extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		ArrayList<Mapping> tempWantMappings=new ArrayList<Mapping>();
		for(int i=0;i<maps.size();i++)  
		{		
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap = a.get(i);
				int start = tempMap.getSourceId();
				int end = tempMap.getTargetId();
				
				List<ArrayList<Integer>> CandidatePaths=new ArrayList<ArrayList<Integer>>();	
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(start, end);	
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,start, end);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))	
					{
						tempWantMappings.add(entailedMapping);
					}
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(end, start);										
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,end, start);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))
					{
						tempWantMappings.add(entailedMapping);	
					}
				}	
			}		
		}
	
	
		for(Mapping temp: tempWantMappings)
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
			
			int id=a.getIndexBidirectional(source, target);
			/*System.out.println(a.get(id).getRelationship());
			System.out.println(temp.getRelationship());*/
			if(a.get(id).getRelationship().equals(temp.getRelationship())) //���ǵ�������ϵ�����(�����ɫ�в����ڸð��������)
			{
				RefreshSet(wantMappings,temp);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				//��ӱߵĲ���(���ǵȼ۵�����ǲ��ᷢ����)
				if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
					addEdge(source, target);
				else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
					addEdge(target, source);				
				continue;
			}
			
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				addEdge(source, target);
				if(existEquivalence(wantMappings,temp))  //̽���ȼ۵�Ǳ�����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
				    correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}					
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);			
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				addEdge(target, source);
				if(existEquivalence(wantMappings,temp))   //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
					//Ǳ�ڽ�ɫ�����
					correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}				
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(source, target);
				addEdge(target, source);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				
				//Ǳ�ڽ�ɫ�����
				correctpairs=restoreMapping(temp);													
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				correctStart = correctpairs[0];
				correctEnd = correctpairs[1];
				if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
				{			
					extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
				{
					extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}
			}
		}
	}
	
	public void simpleReject(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in:indexSet)
		{
			System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());
			// ����һ��MIPSs,����ʹ�ܾ��˸�mapping��Ҳ���ܱ�֤����һ��mappingһ������ȷ���ߴ���ģ�����Ҫר�����ж�
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
			MapMinimalConflictSet.remove(in);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) {
				Integer r = iter.next().getKey();
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), MIPPSet);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					// MapMinimalConflictSet.remove(r);
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
		//��Ϊmapping�Ѿ��ܾ��ˣ�����wantMapping��unWantMapping�е��ӹ�ϵ����Ҫȥ��(ע���ӹ�ϵ�ı�Ƕ���unknown)
		RefreshSet(wantMappings,m);		
		RefreshSet(unWantMappings,m);	
		unWantMappings.add(m);	
		
		int pairs[]=restoreMapping(m);												
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int incorrectStart = pairs[0];
		int incorrectEnd = pairs[1];	
		//���Ǳ�ڽ�ɫ�����
		if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
		{			
			removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
		{
			removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
		}
	}	
	
	//��ͬ�Ļ�����Ҫִ�� indexMapping���Ƶ���������Լ���indexMapping������ͻ�����
	public void approve(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		//��ȡMIPSs��Ψһ�뵱ǰ��ͬmappingì�ܵ�mapping
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		HashSet<Integer> unWantMappingIndex=new HashSet<Integer>();
		indexSet.retainAll(MapMinimalConflictSet.keySet()); //�������Ա�֤
		for(Integer in:indexSet)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) {
				//ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = getMappings(mipp.left, mipp.right); 
				// if (mappings.size() == 2)
				if (existRejectiveMapping(mappings, wantMappings, in)) // ������Խ��wantmapping��������ͻ�����
				{
					mappings.remove(in);
					for (Integer num : mappings) // ���mappings���ϳ���index�е����mapping��ʣ�µ����һ����Ҫ���ܾ��ġ�
					{
						Mapping m = maps.get(num);
						m.setStatus(MappingStatus.INCORRECT);
						aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
						if (!unWantMappings.contains(m))
							unWantMappings.add(m);

						int pairs[] = restoreMapping(m);
						Set<Integer> potentialRemovedMappings = new HashSet<>();
						// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
						int incorrectStart = pairs[0];
						int incorrectEnd = pairs[1];
						if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						} 
						else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						}

						for (Integer potentialmapping : potentialRemovedMappings) 
						{
							if (MapMinimalConflictSet.containsKey(potentialmapping))
								unWantMappingIndex.add(potentialmapping);
						}
					}
					unWantMappingIndex.addAll(mappings);
				}
			}		
		}
			
		//����MIPSs
		for(Integer removedMapping: unWantMappingIndex)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> relatedMIPP=new HashSet(MapMinimalConflictSet.get(removedMapping));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(removedMapping));
			MapMinimalConflictSet.remove(removedMapping);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Integer r = iter.next().getKey();			
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), relatedMIPP);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}			
		}
				
		//��ȷmapping�ı��
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);
		int source = m.getSourceId();
		int target = m.getTargetId();	
		addEdge(source, target);
		addEdge(target, source);
		
		//��Ϊmapping�Ѿ���ͬ�ˣ�����want��unwant���漰m���ӹ�ϵ����Ҫȥ��
		RefreshSet(wantMappings,m);	
		wantMappings.add(m);
		RefreshSet(unWantMappings,m);		
		
		//�漰��ɫ��һ��̽��
		int correctpairs[]=restoreMapping(m);													
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int correctStart = correctpairs[0];
		int correctEnd = correctpairs[1];
		if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
		{			
			extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
		{
			extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		ArrayList<Mapping> tempUnwantMappings=new ArrayList<>();
		ArrayList<Mapping> tempWantMappings=new ArrayList<Mapping>();
		for(int i=0;i<maps.size();i++)  
		{		
			boolean flag=false;
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap = a.get(i);
				int start = tempMap.getSourceId();
				int end = tempMap.getTargetId();
				
				List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(start, end);								
				Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,start, end);	
				if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))	
				{
					tempWantMappings.add(entailedMapping);
					flag=true;
				}
				CandidatePaths=tempGraph.getPaths(end, start);										
				entailedMapping=getEntailedMapping(CandidatePaths,i,index,end, start);	
				if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))
				{
					tempWantMappings.add(entailedMapping);	
					flag=true;
				}
				//��һ���ھ���ܳ�ͻ��mappings
				//��ʼ��ͼ
				if(flag==true) //һ��δ��ǵ�mapping��������ͬʱ��һ����ͬ�ĵ��Ƶ�������������һ���Ƶ���ì�ܵĵ�
					continue;
				tempGraph.addEdge(start,end);
				tempGraph.addEdge(end,start);
				for(Mapping unwanted: unWantMappings)
				{
					int unwantedSource=unwanted.getSourceId();
					int unwantedTarget=unwanted.getTargetId();
										
					//�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
					//ǿ�ܾ��ķ���
					/*List<ArrayList<Integer>> unWantedPaths=tempGraph.getPaths(unwantedSource, unwantedTarget);						
					boolean flag1=entailStrongRejectedMapping(unWantedPaths,i);					
					unWantedPaths=tempGraph.getPaths(unwantedTarget, unwantedSource);				
					boolean flag2=entailStrongRejectedMapping(unWantedPaths,i);								
					if(flag1||flag2)
					{
						if(!tempUnwantMappings.contains(tempMap))
							tempUnwantMappings.add(tempMap);									
					}*/			
					//���ܾ��ķ���(�������ܾ��Ŀ�ʼ��������㣬�����ص�·���а���δ��ǵ�mapping����ôӦ�ñ����ܾ�)
					List<ArrayList<Integer>> unWantedPaths=tempGraph.getPaths(unwantedSource, unwantedTarget);	
					Mapping entailedMapping1=entailWeakRejectedMapping(unWantedPaths,i,start,end);

					unWantedPaths=tempGraph.getPaths(unwantedTarget, unwantedSource);				
					Mapping entailedMapping2=entailWeakRejectedMapping(unWantedPaths,i,end,start);
					if(entailedMapping1!=null&&(unwanted.getRelationship().equals(MappingRelation.SUBCLASS)
							||(unwanted.getRelationship().equals(MappingRelation.EQUIVALENCE))))
					{
						if(!tempUnwantMappings.contains(entailedMapping1))
							tempUnwantMappings.add(entailedMapping1);		
					}
					if(entailedMapping2!=null&&(unwanted.getRelationship().equals(MappingRelation.SUPERCLASS)
							||(unwanted.getRelationship().equals(MappingRelation.EQUIVALENCE))))
					{
						if(!tempUnwantMappings.contains(entailedMapping2))
							tempUnwantMappings.add(entailedMapping2);		
					}						
				}
				tempGraph.removeEdge(start,end);
				tempGraph.removeEdge(end,start);
			}		
		}
	
	
		for(Mapping temp: tempWantMappings)
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				addEdge(source, target);
				if(existEquivalence(wantMappings,temp))  //̽���ȼ۵�Ǳ�����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
				    correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}					
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);			
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				addEdge(target, source);
				if(existEquivalence(wantMappings,temp))   //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
					//Ǳ�ڽ�ɫ�����
					correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}				
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(source, target);
				addEdge(target, source);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				
				//Ǳ�ڽ�ɫ�����
				correctpairs=restoreMapping(temp);													
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				correctStart = correctpairs[0];
				correctEnd = correctpairs[1];
				if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
				{			
					extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
				{
					extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}
			}
		}
		
		
		for(Mapping temp: tempUnwantMappings)
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				if(existEquivalence(unWantMappings,temp))  //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(unWantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);				
					temp.setStatus(MappingStatus.INCORRECT);				
					aml.getAlignment().get(source, target).setStatus(MappingStatus.INCORRECT);	
					
					int pairs[]=restoreMapping(temp);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					int incorrectStart = pairs[0];
					int incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}
				}
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				if(existEquivalence(unWantMappings,temp))  //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(unWantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);				
					temp.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.INCORRECT);	
					
					int pairs[]=restoreMapping(temp);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					int incorrectStart = pairs[0];
					int incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}
				}
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{				
				temp.setStatus(MappingStatus.INCORRECT);			
				aml.getAlignment().get(temp.getSourceId(), temp.getTargetId()).setStatus(MappingStatus.INCORRECT);
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);			
				//���Ǳ�ڽ�ɫ�����
				int pairs[]=restoreMapping(temp);												
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				int incorrectStart = pairs[0];
				int incorrectEnd = pairs[1];
				
				//���Ǳ�ڽ�ɫ�����
				if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
				{			
					removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
				{
					removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
				}
			}			
		}	
	}
	
	public void strongApprove(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		//��ȡMIPSs��Ψһ�뵱ǰ��ͬmappingì�ܵ�mapping
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		HashSet<Integer> unWantMappingIndex=new HashSet<Integer>();
		indexSet.retainAll(MapMinimalConflictSet.keySet()); //�������Ա�֤
		for(Integer in:indexSet)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) {
				//ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = getMappings(mipp.left, mipp.right); 
				// if (mappings.size() == 2)
				if (existRejectiveMapping(mappings, wantMappings, in)) // ������Խ��wantmapping��������ͻ�����
				{
					mappings.remove(in);
					for (Integer num : mappings) // ���mappings���ϳ���index�е����mapping��ʣ�µ����һ����Ҫ���ܾ��ġ�
					{
						Mapping m = maps.get(num);
						m.setStatus(MappingStatus.INCORRECT);
						aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
						if (!unWantMappings.contains(m))
							unWantMappings.add(m);

						int pairs[] = restoreMapping(m);
						Set<Integer> potentialRemovedMappings = new HashSet<>();
						// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
						int incorrectStart = pairs[0];
						int incorrectEnd = pairs[1];
						if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						} 
						else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						}

						for (Integer potentialmapping : potentialRemovedMappings) 
						{
							if (MapMinimalConflictSet.containsKey(potentialmapping))
								unWantMappingIndex.add(potentialmapping);
						}
					}
					unWantMappingIndex.addAll(mappings);
				}
			}		
		}
			
		//����MIPSs
		for(Integer removedMapping: unWantMappingIndex)
		{
			if(!MapMinimalConflictSet.keySet().contains(removedMapping))
				continue;
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> relatedMIPP=new HashSet(MapMinimalConflictSet.get(removedMapping));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(removedMapping));
			MapMinimalConflictSet.remove(removedMapping);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Integer r = iter.next().getKey();			
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), relatedMIPP);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}			
		}
				
		//��ȷmapping�ı��
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);
		int source = m.getSourceId();
		int target = m.getTargetId();
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(source, target);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(target, source);
		}
		
		//��Ϊmapping�Ѿ���ͬ�ˣ�����want��unwant���漰m���ӹ�ϵ����Ҫȥ��
		RefreshSet(wantMappings,m);	
		wantMappings.add(m);
		RefreshSet(unWantMappings,m);		
		
		//�漰��ɫ��һ��̽��
		int correctpairs[]=restoreMapping(m);													
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int correctStart = correctpairs[0];
		int correctEnd = correctpairs[1];
		if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
		{			
			extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
		{
			extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		ArrayList<Mapping> tempUnwantMappings=new ArrayList<>();
		ArrayList<Mapping> tempWantMappings=new ArrayList<Mapping>();
		for(int i=0;i<maps.size();i++)  
		{		
			boolean flag=false;
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap = a.get(i);
				int start = tempMap.getSourceId();
				int end = tempMap.getTargetId();
				
				List<ArrayList<Integer>> CandidatePaths=new ArrayList<ArrayList<Integer>>();	
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(start, end);	
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,start, end);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))	
					{
						tempWantMappings.add(entailedMapping);
						flag=true;
					}
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(end, start);										
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,end, start);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))
					{
						tempWantMappings.add(entailedMapping);	
						flag=true;
					}
				}
				
				//��һ���ھ���ܳ�ͻ��mappings
				if(flag==true) //һ��δ��ǵ�mapping��������ͬʱ��һ����ͬ�ĵ��Ƶ�������������һ���Ƶ���ì�ܵĵ�
					continue;
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{	
					tempGraph.addEdge(start,end);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(end,start);
				}
								
				for(Mapping unwanted: unWantMappings)
				{
					int unwantedSource=unwanted.getSourceId();
					int unwantedTarget=unwanted.getTargetId();
										
					//�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
					//ǿ�ܾ��ķ���
					List<ArrayList<Integer>> unWantedPaths=tempGraph.getPaths(unwantedSource, unwantedTarget);						
					boolean flag1=entailStrongRejectedMapping(unWantedPaths,i);	
					//boolean flag1=entailStrongRejectedMappingExactly(unWantedPaths,i,tempMap);	
					unWantedPaths=tempGraph.getPaths(unwantedTarget, unwantedSource);				
					boolean flag2=entailStrongRejectedMapping(unWantedPaths,i);		
					//boolean flag2=entailStrongRejectedMappingExactly(unWantedPaths,i,tempMap);	
					if(flag1||flag2)
					{
						if(!tempUnwantMappings.contains(tempMap))
							tempUnwantMappings.add(tempMap);									
					}					
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(start,end);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(end,start);
				}
			}		
		}
	
	
		for(Mapping temp: tempWantMappings)
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
			
			int id=a.getIndexBidirectional(source, target);
			/*System.out.println(a.get(id).getRelationship());
			System.out.println(temp.getRelationship());*/
			if(a.get(id).getRelationship().equals(temp.getRelationship())) //���ǵ�������ϵ�����(�����ɫ�в����ڸð��������)
			{
				RefreshSet(wantMappings,temp);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				//��ӱߵĲ���(���ǵȼ۵�����ǲ��ᷢ����)
				if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
					addEdge(source, target);
				else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
					addEdge(target, source);				
				continue;
			}
			
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				addEdge(source, target);
				if(existEquivalence(wantMappings,temp))  //̽���ȼ۵�Ǳ�����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
				    correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}					
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);			
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				addEdge(target, source);
				if(existEquivalence(wantMappings,temp))   //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
					//Ǳ�ڽ�ɫ�����
					correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}				
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(source, target);
				addEdge(target, source);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				
				//Ǳ�ڽ�ɫ�����
				correctpairs=restoreMapping(temp);													
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				correctStart = correctpairs[0];
				correctEnd = correctpairs[1];
				if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
				{			
					extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
				{
					extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}
			}
		}
		
		
		for(Mapping temp: tempUnwantMappings)  //���ǲ����ǵ����ķ���
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
						
			//�����Ƶ���mappings���δ��Ƕ�Ӧ�ñ��ܾ�
			RefreshSet(wantMappings,temp);	
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS)||temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				tempGraph.removeEdge(source,target);
			}
			if(temp.getRelationship().equals(MappingRelation.SUPERCLASS)||temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				tempGraph.removeEdge(target,source);
			}

			temp.setStatus(MappingStatus.INCORRECT);
			aml.getAlignment().get(temp.getSourceId(), temp.getTargetId()).setStatus(MappingStatus.INCORRECT);
			RefreshSet(unWantMappings,temp);
			if (!unWantMappings.contains(temp))
				unWantMappings.add(temp);
			// ���Ǳ�ڽ�ɫ�����
			int pairs[] = restoreMapping(temp);
			// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
			int incorrectStart = pairs[0];
			int incorrectEnd = pairs[1];

			// ���Ǳ�ڽ�ɫ�����
			if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd))
			{
				removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd,wantMappings, unWantMappings);
			} 
			else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
			{
				removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd,wantMappings, unWantMappings);
			}				
		}	
	}
	
	// �ܾ��Ļ�����Ҫ�����Щ����indexMapping��������и���
	public void strongReject(int index,ArrayList<Mapping> wantMappings,ArrayList<Mapping> unwantMappings)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in: indexSet)
		{
			System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());
			// ����һ��MIPSs,����ʹ�ܾ��˸�mapping��Ҳ���ܱ�֤����һ��mappingһ������ȷ���ߴ���ģ�����Ҫר�����ж�
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
			MapMinimalConflictSet.remove(in);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) {
				Integer r = iter.next().getKey();
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), MIPPSet);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					// MapMinimalConflictSet.remove(r);
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}	
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		//��Ϊmapping�Ѿ��ܾ��ˣ������ӹ�ϵ��Ҫȥ��(ע��wantMapping���ֵ��δ��ǵ�)
		
		RefreshSet(wantMappings,m);		
		RefreshSet(unwantMappings,m);
		unwantMappings.add(m);		
		
			
		int start = m.getSourceId();
		int end = m.getTargetId();			
			
		int pairs[]=restoreMapping(m);												
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int incorrectStart = pairs[0];
		int incorrectEnd = pairs[1];
		
		//���Ǳ�ڽ�ɫ�����
		if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
		{			
			removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
		{
			removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		//���ҹ�����ͼ�������Ҳ��Ҫ�Ƴ�	
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(start,end);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(end,start);
		}
		
		for(int i=0;i<maps.size();i++)  
		{					
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap=maps.get(i); 
				int source=tempMap.getSourceId();
				int target=tempMap.getTargetId();
				
				boolean flag1=false,flag2=false;
				//��ʱ����2�������ı�,�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(source,target);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(start, end);	
				    flag1=entailStrongRejectedMapping(CandidatePaths,i);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(target,source);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(end, start);				
				    flag2=entailStrongRejectedMapping(CandidatePaths,i);
				}
									
				if(flag1||flag2)
				{
					tempMap.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(tempMap.getSourceId(), tempMap.getTargetId()).setStatus(MappingStatus.INCORRECT);
					if(!unwantMappings.contains(tempMap))
						unwantMappings.add(tempMap);
					
				    pairs=restoreMapping(tempMap);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				    incorrectStart = pairs[0];
				    incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}
					if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
					{
						tempGraph.removeEdge(source,target);
					}
					if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
					{
						tempGraph.removeEdge(target,source);
					}
				}	
				
				
			}
		}			
	}
	
	public void strongApproveComplete(int index, ArrayList<Mapping> wantMappings, ArrayList<Mapping> unWantMappings)
	{
		//��ȡMIPSs��Ψһ�뵱ǰ��ͬmappingì�ܵ�mapping
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		HashSet<Integer> unWantMappingIndex=new HashSet<Integer>();
		indexSet.retainAll(MapMinimalConflictSet.keySet()); //�������Ա�֤
		for(Integer in:indexSet)
		{
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MIPPSet) {
				//ͨ��·������ȡmappings(�����mappingӦ�þ�����δ��ǵ�,��Ϊ����ִ��approve����reject������mapping��Ӧ��MIPS���ᱻ����)
				Set<Integer> mappings = getMappings(mipp.left, mipp.right); 
				// if (mappings.size() == 2)
				if (existRejectiveMapping(mappings, wantMappings, in)) // ������Խ��wantmapping��������ͻ�����
				{
					mappings.remove(in);
					for (Integer num : mappings) // ���mappings���ϳ���index�е����mapping��ʣ�µ����һ����Ҫ���ܾ��ġ�
					{
						Mapping m = maps.get(num);
						m.setStatus(MappingStatus.INCORRECT);
						aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
						if (!unWantMappings.contains(m))
							unWantMappings.add(m);

						int pairs[] = restoreMapping(m);
						Set<Integer> potentialRemovedMappings = new HashSet<>();
						// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
						int incorrectStart = pairs[0];
						int incorrectEnd = pairs[1];
						if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						} 
						else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
						{
							potentialRemovedMappings = removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd, wantMappings,unWantMappings);
						}

						for (Integer potentialmapping : potentialRemovedMappings) 
						{
							if (MapMinimalConflictSet.containsKey(potentialmapping))
								unWantMappingIndex.add(potentialmapping);
						}
					}
					unWantMappingIndex.addAll(mappings);
				}
			}		
		}
			
		//����MIPSs
		for(Integer removedMapping: unWantMappingIndex)
		{
			if(!MapMinimalConflictSet.keySet().contains(removedMapping))
				continue;
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> relatedMIPP=new HashSet(MapMinimalConflictSet.get(removedMapping));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(removedMapping));
			MapMinimalConflictSet.remove(removedMapping);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Integer r = iter.next().getKey();			
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), relatedMIPP);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}			
		}
				
		//��ȷmapping�ı��
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);
		int source = m.getSourceId();
		int target = m.getTargetId();
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(source, target);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			addEdge(target, source);
		}
		
		//��Ϊmapping�Ѿ���ͬ�ˣ�����want��unwant���漰m���ӹ�ϵ����Ҫȥ��
		RefreshSet(wantMappings,m);	
		wantMappings.add(m);
		RefreshSet(unWantMappings,m);		
		
		//�漰��ɫ��һ��̽��
		int correctpairs[]=restoreMapping(m);													
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int correctStart = correctpairs[0];
		int correctEnd = correctpairs[1];
		if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
		{			
			extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
		{
			extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		ArrayList<Mapping> tempUnwantMappings=new ArrayList<>();
		ArrayList<Mapping> tempWantMappings=new ArrayList<Mapping>();
		for(int i=0;i<maps.size();i++)  
		{		
			boolean flag=false;
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap = a.get(i);
				int start = tempMap.getSourceId();
				int end = tempMap.getTargetId();
				
				List<ArrayList<Integer>> CandidatePaths=new ArrayList<ArrayList<Integer>>();	
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(start, end);	
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,start, end);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))	
					{
						tempWantMappings.add(entailedMapping);
						flag=true;
					}
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					CandidatePaths=tempGraph.getPaths(end, start);										
					Mapping entailedMapping=getEntailedMapping(CandidatePaths,i,index,end, start);	
					if(entailedMapping!=null&&!tempWantMappings.contains(entailedMapping))
					{
						tempWantMappings.add(entailedMapping);	
						flag=true;
					}
				}
				
				//��һ���ھ���ܳ�ͻ��mappings
				if(flag==true) //һ��δ��ǵ�mapping��������ͬʱ��һ����ͬ�ĵ��Ƶ�������������һ���Ƶ���ì�ܵĵ�
					continue;
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{	
					tempGraph.addEdge(start,end);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(end,start);
				}
								
				for(Mapping unwanted: unWantMappings)
				{
					int unwantedSource=unwanted.getSourceId();
					int unwantedTarget=unwanted.getTargetId();
										
					//�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
					//ǿ�ܾ��ķ���
					List<ArrayList<Integer>> unWantedPaths=tempGraph.getPaths(unwantedSource, unwantedTarget);						
					boolean flag1=entailStrongRejectedMapping(unWantedPaths,i);	
					//boolean flag1=entailStrongRejectedMappingExactly(unWantedPaths,i,tempMap);	
					unWantedPaths=tempGraph.getPaths(unwantedTarget, unwantedSource);				
					boolean flag2=entailStrongRejectedMapping(unWantedPaths,i);		
					//boolean flag2=entailStrongRejectedMappingExactly(unWantedPaths,i,tempMap);	
					if(flag1||flag2)
					{
						if(!tempUnwantMappings.contains(tempMap))
							tempUnwantMappings.add(tempMap);									
					}					
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(start,end);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(end,start);
				}
			}		
		}
	
	
		for(Mapping temp: tempWantMappings)
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
			
			int id=a.getIndexBidirectional(source, target);
			/*System.out.println(a.get(id).getRelationship());
			System.out.println(temp.getRelationship());*/
			if(a.get(id).getRelationship().equals(temp.getRelationship())) //���ǵ�������ϵ�����(�����ɫ�в����ڸð��������)
			{
				RefreshSet(wantMappings,temp);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				//��ӱߵĲ���(���ǵȼ۵�����ǲ��ᷢ����)
				if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
					addEdge(source, target);
				else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
					addEdge(target, source);	
				//strongApproveComplete(id,wantMappings,unWantMappings); //ԭ����Ӧ����������
				continue;
			}
			
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				addEdge(source, target);
				if(existEquivalence(wantMappings,temp))  //̽���ȼ۵�Ǳ�����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
				    correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}		
					//strongApproveComplete(id,wantMappings,unWantMappings); //ԭ����Ӧ����������
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);			
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				addEdge(target, source);
				if(existEquivalence(wantMappings,temp))   //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(wantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);
					temp.setStatus(MappingStatus.CORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);	
					
					//Ǳ�ڽ�ɫ�����
					correctpairs=restoreMapping(temp);													
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					correctStart = correctpairs[0];
					correctEnd = correctpairs[1];
					if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
					{			
						extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
					{
						extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
					}
					//strongApproveComplete(id,wantMappings,unWantMappings); //ԭ����Ӧ����������
				}
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				addEdge(source, target);
				addEdge(target, source);
				temp.setStatus(MappingStatus.CORRECT);
				aml.getAlignment().get(source, target).setStatus(MappingStatus.CORRECT);
				if(!wantMappings.contains(temp))
					wantMappings.add(temp);	
				
				//Ǳ�ڽ�ɫ�����
				correctpairs=restoreMapping(temp);													
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				correctStart = correctpairs[0];
				correctEnd = correctpairs[1];
				if(aml.getSource().getDataProperties().contains(correctStart)&&aml.getTarget().getDataProperties().contains(correctEnd))
				{			
					extendDataPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(correctStart)&&aml.getTarget().getObjectProperties().contains(correctEnd))
				{
					extendObjectPropety4MapppingGraph(correctStart,correctEnd,wantMappings,unWantMappings);
				}
				
				//strongApproveComplete(id,wantMappings,unWantMappings); //ԭ����Ӧ����������
			}
		}
		
		
		for(Mapping temp: tempUnwantMappings)  //���ǲ����ǵ����ķ���
		{
			source = temp.getSourceId();
			target = temp.getTargetId();
						
			//�����Ƶ���mappings���δ��Ƕ�Ӧ�ñ��ܾ�
			RefreshSet(wantMappings,temp);	
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS)||temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				tempGraph.removeEdge(source,target);
			}
			if(temp.getRelationship().equals(MappingRelation.SUPERCLASS)||temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				tempGraph.removeEdge(target,source);
			}

			temp.setStatus(MappingStatus.INCORRECT);
			aml.getAlignment().get(temp.getSourceId(), temp.getTargetId()).setStatus(MappingStatus.INCORRECT);
			RefreshSet(unWantMappings,temp);
			if (!unWantMappings.contains(temp))
				unWantMappings.add(temp);
			// ���Ǳ�ڽ�ɫ�����
			int pairs[] = restoreMapping(temp);
			// ���ڽ�ɫ���Խ���һ��Ǳ���ж�
			int incorrectStart = pairs[0];
			int incorrectEnd = pairs[1];

			// ���Ǳ�ڽ�ɫ�����
			if (aml.getSource().getDataProperties().contains(incorrectStart)&& aml.getTarget().getDataProperties().contains(incorrectEnd))
			{
				removeDataPropety4MapppingGraph(incorrectStart, incorrectEnd,wantMappings, unWantMappings);
			} 
			else if (aml.getSource().getObjectProperties().contains(incorrectStart)&& aml.getTarget().getObjectProperties().contains(incorrectEnd)) 
			{
				removeObjectPropety4MapppingGraph(incorrectStart, incorrectEnd,wantMappings, unWantMappings);
			}		
			//ִ�еݹ����
			int extendIndex=a.getIndexBidirectional(source, target);
			//strongRejectComplete(extendIndex,wantMappings,unWantMappings);
		}	
	}
	
	
	
	// �ܾ��Ļ�����Ҫ�����Щ����indexMapping��������и���
	public void strongRejectComplete(int index,ArrayList<Mapping> wantMappings,ArrayList<Mapping> unwantMappings)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in: indexSet)
		{
			System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());
			// ����һ��MIPSs,����ʹ�ܾ��˸�mapping��Ҳ���ܱ�֤����һ��mappingһ������ȷ���ߴ���ģ�����Ҫר�����ж�
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
			MapMinimalConflictSet.remove(in);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) {
				Integer r = iter.next().getKey();
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), MIPPSet);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					// MapMinimalConflictSet.remove(r);
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}	
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		//��Ϊmapping�Ѿ��ܾ��ˣ������ӹ�ϵ��Ҫȥ��(ע��wantMapping���ֵ��δ��ǵ�)
		
		RefreshSet(wantMappings,m);		
		RefreshSet(unwantMappings,m);
		unwantMappings.add(m);		
		
			
		int start = m.getSourceId();
		int end = m.getTargetId();			
			
		int pairs[]=restoreMapping(m);												
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int incorrectStart = pairs[0];
		int incorrectEnd = pairs[1];
		
		//���Ǳ�ڽ�ɫ�����
		if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
		{			
			removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
		{
			removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		//���ҹ�����ͼ�������Ҳ��Ҫ�Ƴ�	
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(start,end);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(end,start);
		}
		
		for(int i=0;i<maps.size();i++)  
		{					
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap=maps.get(i); 
				int source=tempMap.getSourceId();
				int target=tempMap.getTargetId();
				
				boolean flag1=false,flag2=false;
				//��ʱ����2�������ı�,�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(source,target);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(start, end);	
				    flag1=entailStrongRejectedMapping(CandidatePaths,i);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(target,source);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(end, start);				
				    flag2=entailStrongRejectedMapping(CandidatePaths,i);
				}
									
				if(flag1||flag2)
				{
					tempMap.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(tempMap.getSourceId(), tempMap.getTargetId()).setStatus(MappingStatus.INCORRECT);
					if(!unwantMappings.contains(tempMap))
						unwantMappings.add(tempMap);
					
				    pairs=restoreMapping(tempMap);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				    incorrectStart = pairs[0];
				    incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}			
					// �ݹ����
					int extendIndex=a.getIndexBidirectional(source, target);
					//strongRejectComplete(extendIndex,wantMappings,unwantMappings);
				}
				//����ʱ���ӵ�2�������ı߽���ɾ��
				if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(source,target);
				}
				if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(target,source);
				}
						
			}
		}			
	}
	
	public void strongRejectComplete2(int index,ArrayList<Mapping> wantMappings,ArrayList<Mapping> unwantMappings, ArrayList<Mapping> reliableMappings)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in: indexSet)
		{
			System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());
			// ����һ��MIPSs,����ʹ�ܾ��˸�mapping��Ҳ���ܱ�֤����һ��mappingһ������ȷ���ߴ���ģ�����Ҫר�����ж�
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
			MapMinimalConflictSet.remove(in);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) {
				Integer r = iter.next().getKey();
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), MIPPSet);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					// MapMinimalConflictSet.remove(r);
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}	
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		//��Ϊmapping�Ѿ��ܾ��ˣ������ӹ�ϵ��Ҫȥ��(ע��wantMapping���ֵ��δ��ǵ�)
		
		RefreshSet(wantMappings,m);		
		RefreshSet(unwantMappings,m);
		unwantMappings.add(m);		
		
			
		int start = m.getSourceId();
		int end = m.getTargetId();			
			
		int pairs[]=restoreMapping(m);												
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int incorrectStart = pairs[0];
		int incorrectEnd = pairs[1];
		
		//���Ǳ�ڽ�ɫ�����
		if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
		{			
			removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
		{
			removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
		}
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		
		//���ҹ�����ͼ�������Ҳ��Ҫ�Ƴ�	
		if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(start,end);
		}
		if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
		{
			tempGraph.removeEdge(end,start);
		}
		
		for(int i=0;i<maps.size();i++)  
		{	
			Mapping tempMap=maps.get(i); 
			int source=tempMap.getSourceId();
			int target=tempMap.getTargetId();
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN)||reliableMappings.contains(tempMap))
			{
				boolean flag1=false,flag2=false;
				//��ʱ����2�������ı�,�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
				if(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(source,target);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(start, end);	
				    flag1=entailStrongRejectedMapping(CandidatePaths,i);
				}
				if(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)||tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.addEdge(target,source);
					List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(end, start);				
				    flag2=entailStrongRejectedMapping(CandidatePaths,i);
				}
									
				if(flag1||flag2)
				{
					tempMap.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(tempMap.getSourceId(), tempMap.getTargetId()).setStatus(MappingStatus.INCORRECT);
					if(!unwantMappings.contains(tempMap))
						unwantMappings.add(tempMap);
					if(reliableMappings.contains(tempMap))
					{
						reliableMappings.remove(tempMap);
						removeEdge(source,target);
						removeEdge(target,source);
					}
					
				    pairs=restoreMapping(tempMap);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				    incorrectStart = pairs[0];
				    incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unwantMappings);
					}			
					// �ݹ����
					int extendIndex=a.getIndexBidirectional(source, target);
					//strongRejectComplete(extendIndex,wantMappings,unwantMappings);
				}
				//����ʱ���ӵ�2�������ı߽���ɾ��
				if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(source,target);
				}
				if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
				{
					tempGraph.removeEdge(target,source);
				}
						
			}
		}
		
		
	}
	
	public void weakReject(int index,ArrayList<Mapping> wantMappings,ArrayList<Mapping> unWantMappings)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in:indexSet)
		{
			System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());
			// ����һ��MIPSs,����ʹ�ܾ��˸�mapping��Ҳ���ܱ�֤����һ��mappingһ������ȷ���ߴ���ģ�����Ҫר�����ж�
			HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet = new HashSet(MapMinimalConflictSet.get(in));
			MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
			MapMinimalConflictSet.remove(in);
			Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter = MapMinimalConflictSet
					.entrySet().iterator();
			while (iter.hasNext()) {
				Integer r = iter.next().getKey();
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP = removeAllMIPP(
						MapMinimalConflictSet.get(r), MIPPSet);
				if (leftMIPP.isEmpty()) // Ϊ����ֱ���Ƴ�
				{
					// MapMinimalConflictSet.remove(r);
					iter.remove();
				} else // ����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets = new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(
							leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
			}
		}
				
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
		//��Ϊmapping�Ѿ��ܾ��ˣ�����wantMapping��unWantMapping�е��ӹ�ϵ����Ҫȥ��(ע���ӹ�ϵ�ı�Ƕ���unknown)
		RefreshSet(wantMappings,m);		
		RefreshSet(unWantMappings,m);	
		unWantMappings.add(m);	
		
		
		int start = m.getSourceId();
		int end = m.getTargetId();	
		
		int pairs[]=restoreMapping(m);												
		//���ڽ�ɫ���Խ���һ��Ǳ���ж�
		int incorrectStart = pairs[0];
		int incorrectEnd = pairs[1];
		
		//���Ǳ�ڽ�ɫ�����
		if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
		{			
			removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
		}			
		else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
		{
			removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
		}
		
		
		Graph tempGraph=new Graph();
		tempGraph.init(originalMap);
		//���ҹ�����ͼ�������Ҳ��Ҫ�Ƴ�	
		tempGraph.removeEdge(start,end);
		tempGraph.removeEdge(end,start);
				
		ArrayList<Mapping> tempUnwantMappings=new ArrayList<>();
		for(int i=0;i<maps.size();i++)  
		{					
			if(maps.get(i).getStatus().equals(MappingStatus.UNKNOWN))
			{
				Mapping tempMap=maps.get(i); 
				int source=tempMap.getSourceId();
				int target=tempMap.getTargetId();
				
				//��ʱ����2�������ı�
				tempGraph.addEdge(source,target);
				tempGraph.addEdge(target,source);
				
				//�ж�����mapping��ͼ�νṹ�ܷ��Ƶ������ܾ���mappings
				List<ArrayList<Integer>> CandidatePaths=tempGraph.getPaths(start, end);	
				Mapping entailedMapping1=entailWeakRejectedMapping(CandidatePaths,i,source,target);		
				/*if(entailedMapping1!=null)
					tempUnwantMappings.add(entailedMapping1);*/
				CandidatePaths=tempGraph.getPaths(end, start);				
				Mapping entailedMapping2=entailWeakRejectedMapping(CandidatePaths,i,source,target);	
				/*if(entailedMapping2!=null)
					tempUnwantMappings.add(entailedMapping2);*/				
				if(entailedMapping1!=null&&(tempMap.getRelationship().equals(MappingRelation.SUBCLASS)
						||(tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))))
				{
					if(!tempUnwantMappings.contains(entailedMapping1))
						tempUnwantMappings.add(entailedMapping1);		
				}
				if(entailedMapping2!=null&&(tempMap.getRelationship().equals(MappingRelation.SUPERCLASS)
						||(tempMap.getRelationship().equals(MappingRelation.EQUIVALENCE))))
				{
					if(!tempUnwantMappings.contains(entailedMapping2))
						tempUnwantMappings.add(entailedMapping2);		
				}					
				//����ʱ���ӵ�2�������ı߽���ɾ��
				tempGraph.removeEdge(source,target);
				tempGraph.removeEdge(target,source);
			}
		}
		
		for(Mapping temp: tempUnwantMappings)
		{
			int source = temp.getSourceId();
			int target = temp.getTargetId();
			if(temp.getRelationship().equals(MappingRelation.SUBCLASS))
			{
				if(existEquivalence(unWantMappings,temp))  //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(unWantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);				
					temp.setStatus(MappingStatus.INCORRECT);				
					aml.getAlignment().get(source, target).setStatus(MappingStatus.INCORRECT);		
					
					//���Ǳ�ڽ�ɫ�����
					pairs=restoreMapping(temp);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					incorrectStart = pairs[0];
					incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}
				}
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);
			}
			else if(temp.getRelationship().equals(MappingRelation.SUPERCLASS))
			{
				if(existEquivalence(unWantMappings,temp))  //̽��Ǳ�ڵȼ۵����
				{
					RefreshSet(unWantMappings,temp);
					temp.setRelationship(MappingRelation.EQUIVALENCE);				
					temp.setStatus(MappingStatus.INCORRECT);
					aml.getAlignment().get(source, target).setStatus(MappingStatus.INCORRECT);	
					
					//���Ǳ�ڽ�ɫ�����
					pairs=restoreMapping(temp);												
					//���ڽ�ɫ���Խ���һ��Ǳ���ж�
					incorrectStart = pairs[0];
					incorrectEnd = pairs[1];
					
					//���Ǳ�ڽ�ɫ�����
					if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
					{			
						removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}			
					else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
					{
						removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
					}
				}
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);
			}
			else if(temp.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{				
				temp.setStatus(MappingStatus.INCORRECT);			
				aml.getAlignment().get(temp.getSourceId(), temp.getTargetId()).setStatus(MappingStatus.INCORRECT);
				if(!unWantMappings.contains(temp))
					unWantMappings.add(temp);
				
				//���Ǳ�ڽ�ɫ�����
				pairs=restoreMapping(temp);												
				//���ڽ�ɫ���Խ���һ��Ǳ���ж�
				incorrectStart = pairs[0];
				incorrectEnd = pairs[1];
				
				//���Ǳ�ڽ�ɫ�����
				if(aml.getSource().getDataProperties().contains(incorrectStart)&&aml.getTarget().getDataProperties().contains(incorrectEnd))
				{			
					removeDataPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
				}			
				else if(aml.getSource().getObjectProperties().contains(incorrectStart)&&aml.getTarget().getObjectProperties().contains(incorrectEnd))
				{
					removeObjectPropety4MapppingGraph(incorrectStart,incorrectEnd,wantMappings,unWantMappings);
				}
			}			
		}		
	}

	/**
	 * Sets a Mapping as incorrect and removes its conflicts from
	 * the RepairMap (but does not actually remove the Mapping from
	 * the Alignment)
	 * @param index: the index of the Mapping to remove
	 */
	public void remove(int index,boolean flag)
	{
		System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(index).size());	
		HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet= new HashSet(MapMinimalConflictSet.get(index));
		MinimalConflictSet.removeAll(MapMinimalConflictSet.get(index));
		MapMinimalConflictSet.remove(index);
		Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter= MapMinimalConflictSet.entrySet().iterator();
		while(iter.hasNext())
		{
			Integer  r=iter.next().getKey();	
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP=removeAllMIPP(MapMinimalConflictSet.get(r),MIPPSet);
				if(leftMIPP.isEmpty())  //Ϊ����ֱ���Ƴ�
				{
					//MapMinimalConflictSet.remove(r);
					iter.remove();
				}
				else  //����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets=new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
		}
		if(flag==true)
		{
			Mapping m = a.get(index);
			m.setStatus(MappingStatus.INCORRECT);
			aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
		}
	}
	
	public void remove2(int index,boolean flag)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in: indexSet)
		{	
		HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet= new HashSet(MapMinimalConflictSet.get(in));
		MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
		MapMinimalConflictSet.remove(in);
		Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter= MapMinimalConflictSet.entrySet().iterator();
		while(iter.hasNext())
		{
			Integer  r=iter.next().getKey();	
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP=removeAllMIPP(MapMinimalConflictSet.get(r),MIPPSet);
				if(leftMIPP.isEmpty())  //Ϊ����ֱ���Ƴ�
				{
					//MapMinimalConflictSet.remove(r);
					iter.remove();
				}
				else  //����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets=new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
		}
		
		}
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		for(Integer in: indexSet)
		{
			if(flag==true)
			{
				Mapping m = a.get(in);
				m.setStatus(MappingStatus.INCORRECT);
				aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);
			}
		}
	}
	
	public void revise(int index,double sim)
	{
		System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(index).size());	
		HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet= new HashSet(MapMinimalConflictSet.get(index));
		MinimalConflictSet.removeAll(MapMinimalConflictSet.get(index));
		MapMinimalConflictSet.remove(index);
		Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter= MapMinimalConflictSet.entrySet().iterator();
		while(iter.hasNext())
		{
			Integer  r=iter.next().getKey();	
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP=removeAllMIPP(MapMinimalConflictSet.get(r),MIPPSet);
				if(leftMIPP.isEmpty())  //Ϊ����ֱ���Ƴ�
				{
					//MapMinimalConflictSet.remove(r);
					iter.remove();
				}
				else  //����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets=new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
		}	
		Mapping m = a.get(index);
		m.setStatus(MappingStatus.UNKNOWN);			
		MappingRelation rel = MappingRelation.parseRelation(StringEscapeUtils.unescapeXml("?"));
		m.setRelationship(rel);
		m.setSimilarity(sim);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setSimilarity(sim);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.UNKNOWN);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setRelationship(rel);
	}
	
	public void revise2(int index,double sim)
	{
		HashSet<Integer> indexSet=new HashSet<Integer>();
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		else
			indexSet.add(index);
		indexSet.retainAll(MapMinimalConflictSet.keySet());
		for(Integer in: indexSet)
		{
		System.out.println("The number of related Minimal conflict sets is "+ MapMinimalConflictSet.get(in).size());	
		HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSet= new HashSet(MapMinimalConflictSet.get(in));
		MinimalConflictSet.removeAll(MapMinimalConflictSet.get(in));
		MapMinimalConflictSet.remove(in);
		Iterator<Entry<Integer, ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>>> iter= MapMinimalConflictSet.entrySet().iterator();
		while(iter.hasNext())
		{
			Integer  r=iter.next().getKey();	
				LinkedList<Pair<ArrayList<Integer>, ArrayList<Integer>>> leftMIPP=removeAllMIPP(MapMinimalConflictSet.get(r),MIPPSet);
				if(leftMIPP.isEmpty())  //Ϊ����ֱ���Ƴ�
				{
					//MapMinimalConflictSet.remove(r);
					iter.remove();
				}
				else  //����ԭ����MIPPSets�Ĵ�С
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> MIPPSets=new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>(leftMIPP);
					MapMinimalConflictSet.put(r, MIPPSets);
				}
		}		
		}
		if(aml.getAlignment().getPropertyMap().containsKey(index))
			indexSet.addAll(aml.getAlignment().getPropertyMap().get(index));
		for(Integer in: indexSet)
		{
		Mapping m = a.get(in);
		m.setStatus(MappingStatus.UNKNOWN);			
		MappingRelation rel = MappingRelation.parseRelation(StringEscapeUtils.unescapeXml("?"));
		m.setRelationship(rel);
		m.setSimilarity(sim);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setSimilarity(sim);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.UNKNOWN);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setRelationship(rel);
		}
	}
	
	

	public LinkedList <Pair<ArrayList<Integer>, ArrayList<Integer>>> removeAllMIPP(ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> src, HashSet<Pair<ArrayList<Integer>, ArrayList<Integer>>> othHash) 
	{
		LinkedList <Pair<ArrayList<Integer>, ArrayList<Integer>>> result = new LinkedList(src);// �󼯺���linkedlist
		//HashSet othHash = new HashSet(oth);// С������hashset
		Iterator iter = result.iterator();// ����Iterator�������������ݵĲ���
		while (iter.hasNext()) 
		{
			if (othHash.contains(iter.next())) 
			{
				iter.remove();
			}
		}
		return result;
	}
	
	
	/**
	 * Saves the list of minimal conflict sets to a text file
	 * @param file: the path to the file where to save
	 * @throws FileNotFoundException if unable to create/open file
	 */
	public void saveConflictSets(String file) throws FileNotFoundException
	{
		PrintWriter outStream = new PrintWriter(new FileOutputStream(file));
		int id = 1;
		for(Path p : conflictSets)
		{
			outStream.println("Conflict Set " + id++ + ":");
			for(Integer i : p)
				outStream.println(a.get(i).toString());
		}
		outStream.close();
	}
	
//Private Methods
	
	//Builds the RepairMap
	private void init()
	{
		System.out.println("Building Repair Map");
		long globalTime = System.currentTimeMillis()/1000;
		//Initialize the data structures
		maps=aml.getAlignment().getMappingSet();
		classList = new HashSet<Integer>();
		checkList = new HashSet<Integer>();
		ancestorMap = new Table3List<Integer,Integer,Path>();	
		pathLengths = new Table3Set<Integer,Integer,Integer>();
		conflictSets = new Vector<Path>();
		originalMap=new HashMap<Integer,Set<Integer>>();
		parentMap=new HashMap<Integer,Set<Integer>>();
		ancestors=new HashMap<Integer,Set<Integer>>();
		MinimalConflictSet=new HashSet<Pair<ArrayList<Integer>,ArrayList<Integer>>>();
		//MinimalConflictSet=new ArrayList<>();
		//MinimalConflictSetMap=new ConcurrentHashMap<ArrayList<Integer>,MIPP>();
		//MinimalConflictSetMap=new ConcurrentSkipListSet<Integer>();
		//MapMinimalConflictSet=new HashMap<Integer,ArrayList<MIPP>>();
		MapMinimalConflictSet=new HashMap<Integer,ArrayList<Pair<ArrayList<Integer>,ArrayList<Integer>>>>();
		checkedState =new ConcurrentHashMap<String,Boolean>();
		
		//Build the classList, starting with the classes
		//involved in disjoint clauses
		classList.addAll(rels.getDisjoint());
		//If there aren't any, there is nothing else to do(���ǰ��Զ��޸����ܲ���Ҫ������������������Ҫ��Ϊ��ȷ����û��mips)
		/*if(classList.size() == 0)
		{
			System.out.println("Nothing to repair!");
			return;
		}*/
		
		//Otherwise, add all classes involved in mappings
		for(Integer i : a.getSources())
			if(AML.getInstance().getURIMap().isClass(i))
				classList.add(i);
		for(Integer i : a.getTargets())
			if(AML.getInstance().getURIMap().isClass(i))
				classList.add(i);
		
		//�����ܵ�����Ҳ��ӽ���
		ExtendClassListByProperty();
		
		//Then build the checkList
		long localTime = System.currentTimeMillis()/1000;
		buildCheckList();
		System.out.println("Computed check list in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		HashSet<Integer> t = new HashSet<Integer>(classList);
		t.addAll(checkList);	
		System.out.println("Core fragments: " + t.size() + " classes");
		t.clear();
		System.out.println("The number of subClassOf relationship is: " + parentMap.size());	
		RefineParentMap();	
		
		//The key of our influence arc
		//EnhanceParentMap();	  //Enhanced Relations
		
//		System.out.println("The number of extended subClassOf relationship is: " + parentMap.size());
		graph.init(parentMap);	
		System.out.println("Check list: " + checkList.size() + " classes to check");
		//Build the ancestorMap with transitive closure
		localTime = System.currentTimeMillis()/1000;
		buildAncestorMap();
		System.out.println("Build ancestorMap is finished");
		//buildAncestorMap2();	
		System.out.println("The number of extended subClassOf relationship is: " + parentMap.size());

		System.out.println("Computed ancestral paths in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		System.out.println("Paths to process: " + pathCount);
		
		//And finally, get the list of conflict sets
		localTime = System.currentTimeMillis()/1000;
		//buildConflictSets();  //���̵߳İ취
		
		//Find MIPPs
		buildMIPPs();  
		
		//buildMIPPs2();  //���̵߳İ취
		/*System.out.println("Computed minimal conflict sets in " + 
				(System.currentTimeMillis()/1000-localTime) + " seconds");
		System.out.println("Sets of conflicting mappings: " + conflictSets.size());
		int id=1;
		for(Path p : conflictSets)
		{
			System.out.println("Conflict Set " + id++ + ":");
			for(Integer i : p)
				System.out.println(a.get(i).toString());
		}	*/
		System.out.println("Repair Map finished in " +
				(System.currentTimeMillis()/1000-globalTime) + " seconds");
		//System.out.println("The minimal incoherence path-pairs are :");
		
		/*Iterator<MIPP> iterator = MinimalConflictSet.iterator();
		while (iterator.hasNext()) 
		{
			MIPP mipp=iterator.next();
			type type = (type) iterator.nextElement();
			
		}*/
		/*for(ArrayList<Integer> map:MinimalConflictSetMap.keySet())
		{
			MinimalConflictSet.add(MinimalConflictSetMap.get(map));
		}*/
		
		/*for(MIPP mipp:MinimalConflictSet)
		{
			mipp.print();
		}*/
		//buildClusters();
		System.out.println("The size of MinimalConflictSet is "+ MinimalConflictSet.size());
		System.out.println("The size of MapMinimalConflictSet is "+ MapMinimalConflictSet.size());
		System.out.println();
		
	}
	
	//Computes the list of classes that must be checked for coherence
	private void buildCheckList()
	{
		//Start with the descendants of classList classes that have
		//either 2+ parents with a classList class in their ancestral
		//line or are involved in a disjoint class and have 1+ parents
		HashSet<Integer> descList = new HashSet<Integer>();
		for(Integer i: classList)
		{
			//Store the parentRelations
			//parentMap.put(i, rels.getSuperClasses(i,false));
			//parentMap.put(i, rels.getSuperClasses(i,true));
			//Get the subClasses of classList classes
			for(Integer j : rels.getSubClasses(i,false))
			{
				//Count their parents
				Set<Integer> pars = rels.getSuperClasses(j, true);
				//Check if they have a disjoint clause
				int hasDisjoint = 0;
				if(rels.hasDisjoint(j))
					hasDisjoint = 1;
				//Exclude those that don't have at least two parents
				//or a parent and a disjoint clause
				if(pars.size() + hasDisjoint < 2)
					continue;
				//Count the classList classes in the ancestral
				//line of each parent (or until two parents with
				//classList ancestors are found)
				int count = hasDisjoint;
				for(Integer k : pars)
				{
					if(classList.contains(k))
						count++;
					else
					{
						for(Integer l : rels.getSuperClasses(k, false))
						{
							if(classList.contains(l))
							{
								count++;
								break;
							}
						}
					}
					if(count > 1)
						break;
				}
				//Add those that have at least 2 classList
				//classes in their ancestral line
				if(count > 1)
					descList.add(j);
			}
		}

		
	
		//System.out.println(parentMap.keySet().contains(12));
		
		
		//Filter out classes that have a descendant in the descList
		//or a mapped descendant
		HashSet<Integer> toRemove = new HashSet<Integer>();
		for(Integer i : descList)
		{
			for(Integer j : rels.getSubClasses(i, false))
			{
				if(descList.contains(j) || a.containsClass(j))
				{
					toRemove.add(i);
					break;
				}
			}
		}
		descList.removeAll(toRemove);
		//And those that have the same set or a subset of
		//classList classes in their ancestor line
		toRemove = new HashSet<Integer>();
		Vector<Integer> desc = new Vector<Integer>();
		Vector<Path> paths = new Vector<Path>();
		for(Integer i : descList)
		{
			//Put the classList ancestors in a path
			Path p = new Path();
			for(Integer j : rels.getSuperClasses(i,false))
				if(classList.contains(j))
					p.add(j);
			//Put the class itself in the path if it
			//is also in classList
			if(classList.contains(i))
				p.add(i);			
			
			boolean add = true;
			//Check if any of the selected classes
			for(int j = 0; j < desc.size() && add; j++)
			{
				//subsumes this class (if so, skip it)
				if(paths.get(j).contains(p))
					add = false;
				//is subsumed by this class (if so,
				//remove the latter and proceed)
				else if(p.contains(paths.get(j)))
				{
					desc.remove(j);
					paths.remove(j);
					j--;
				}
			}
			//If no redundancy was found, add the class
			//to the list of selected classes
			if(add)
			{
				desc.add(i);
				paths.add(p);
			}
		}
		//Add all selected classes to the checkList
		checkList.addAll(desc);
		
		//Complete the descendant Classes and their relationship
		//Complete the missing superClasses relationship
		
		
		
		//Now get the list of all mapped classes that are
		//involved in two mappings or have an ancestral
		//path to a mapped class, from only one side
		HashSet<Integer> mapList = new HashSet<Integer>();
		for(Mapping m : a)
		{
			int source = m.getSourceId();
			int target = m.getTargetId();
			//Check if there is no descendant in the checkList
			boolean isRedundant = false;
			HashSet<Integer> descendants = new HashSet<Integer>(rels.getSubClasses(source, false));
			descendants.addAll(rels.getSubClasses(target, false));
			
			//Store the parentRelations according to the mappings
			/*if(parentMap.keySet().contains(source))
				parentMap.get(source).add(target);
			else
			{
				Set<Integer> set=new HashSet<Integer>();
				set.add(target);
				parentMap.put(source,set);
			}
			if(parentMap.keySet().contains(target))
				parentMap.get(target).add(source);
			else
			{
				Set<Integer> set=new HashSet<Integer>();
				set.add(source);
				parentMap.put(target,set);
			}
			*/			
			for(Integer i : descendants)
			{
				if(checkList.contains(i))
				{
					isRedundant = true;
					break;
				}
			}
			if(isRedundant)
				continue;
			//Count the mappings of both source and target classes
			int sourceCount = a.getSourceMappings(source).size();
			int targetCount = a.getTargetMappings(target).size();
			//If the target class has more mappings than the source
			//class (which implies it has at least 2 mappings) add it
			if(targetCount > sourceCount)
				mapList.add(target);
			//If the opposite is true, add the target
			else if(sourceCount > targetCount || sourceCount > 1)
				mapList.add(source);
			//Otherwise, check for mapped ancestors on both sides
			else
			{
				for(Integer j : rels.getSuperClasses(source, false))
					if(a.containsSource(j))
						sourceCount++;
				for(Integer j : rels.getSuperClasses(target, false))
					if(a.containsTarget(j))
						targetCount++;
				if(sourceCount > 1 && targetCount < sourceCount)
					mapList.add(source);
				else if(targetCount > 1)
					mapList.add(target);
			}
		}
		toRemove = new HashSet<Integer>();
		for(Integer i : mapList)
		{
			for(Integer j : rels.getSubClasses(i, false))
			{
				if(mapList.contains(j))
				{
					toRemove.add(i);
					break;
				}
			}
		}
		mapList.removeAll(toRemove);
		//Finally, add the mapList to the checkList
		checkList.addAll(mapList);
		
		HashSet<Integer> t = new HashSet<Integer>(classList);
		t.addAll(checkList);
		for(Integer num:t)
		{
			if(!parentMap.keySet().contains(num))
				parentMap.put(num, rels.getSuperClasses(num,true));
		}	
		int size = 0;
		while (size < parentMap.size()) 
		{
			size = parentMap.size();
			HashMap<Integer, Set<Integer>> teMap = new HashMap<Integer, Set<Integer>>();
			teMap.putAll(parentMap);
			for (Integer child : teMap.keySet()) 
			{
				Set<Integer> parents = teMap.get(child);
				for (Integer parent : parents) 
				{
					if (!teMap.keySet().contains(parent)) 
					{						
						parentMap.put(parent, rels.getSuperClasses(parent, true));
					}
					if (checkList.contains(parent) || classList.contains(parent))
						continue;
				}
			}
			teMap.clear();
		}
		
		//����֮ǰ��bug
		for(Mapping m : a)
		{
			int source = m.getSourceId();
			int target = m.getTargetId();
			if(rels.getSubClasses(source, false).isEmpty())
				checkList.add(source);
			if(rels.getSubClasses(target, false).isEmpty())
				checkList.add(target);
		}
		
			//��չ���Ե�checkList
		//ExtendCheckListByProperty();		
	}

	private void RefineParentMap()
	{
		HashSet<Integer> needConcetps=new HashSet<Integer>();
		needConcetps.addAll(classList);
		needConcetps.addAll(checkList);
		HashMap<Integer, Set<Integer>> SourceParentMap=new HashMap<Integer, Set<Integer>>();
		HashMap<Integer, Set<Integer>> TargetParentMap=new HashMap<Integer, Set<Integer>>();
		Set<Integer> sourceConcept=aml.getSource().getClasses();	
		for(Integer node: parentMap.keySet())
		{
			if(sourceConcept.contains(node))
			{
				SourceParentMap.put(node, parentMap.get(node));
			}
			else 
			{
				TargetParentMap.put(node, parentMap.get(node));
			}
		}	
		parentMap.clear();
		
//		//扩展包含关系
//		for(Integer node: rels.getInfluence())
//		{
//			if(sourceConcept.contains(node))
//			{
//				SourceParentMap.put(node, rels.getInfluence(node));
//			}
//			else 
//			{
//				TargetParentMap.put(node, rels.getInfluence(node));
//			}
//		}
		
		Graph sourceGraph=new Graph();	
		sourceGraph.init(SourceParentMap);		
		for(Integer node: SourceParentMap.keySet())
		{
			Set<Integer> CoreParents=new HashSet<Integer>();
			Set<Integer> ancestors=rels.getSuperClasses(node, false);		
			for(Integer anc: ancestors)
			{
				if(CoreParents.contains(anc)) //���õ��·����ʵ�Ѿ���������(�м�Ľ��ͨ���ڱ������Ƚ���ʱ�����ʵ���)
					continue;
				List<ArrayList<Integer>> getPaths=sourceGraph.getPaths(node, anc);
				for(ArrayList<Integer> path: getPaths)
				{
					for(Integer n: path)
					{
						if (needConcetps.contains(n)&&n!=node) 
						{
							CoreParents.add(n);
							break;
						}
						if(CoreParents.contains(n))  //�����ظ����
						{
							break;
						}
					}
				}
			}
			Set<Integer> copyParentSet=new HashSet<Integer>(CoreParents);
			parentMap.put(node, CoreParents);	
			originalMap.put(node, copyParentSet);	
		}
		
		Graph targetGraph=new Graph();
		targetGraph.init(TargetParentMap);		
		for(Integer node: TargetParentMap.keySet())
		{
			Set<Integer> CoreParents=new HashSet<Integer>();
			Set<Integer> ancestors=rels.getSuperClasses(node, false);		
			for(Integer anc: ancestors)
			{
				if(CoreParents.contains(anc)) //���õ��·����ʵ�Ѿ���������(�м�Ľ��ͨ���ڱ������Ƚ���ʱ�����ʵ���)
					continue;
				List<ArrayList<Integer>> getPaths=targetGraph.getPaths(node, anc);
				for(ArrayList<Integer> path: getPaths)
				{
					for(Integer n: path)
					{
						if (needConcetps.contains(n)&&n!=node) 
						{
							CoreParents.add(n);
							break;
						}
						if(CoreParents.contains(n))  //�����ظ����
						{
							break;
						}
					}
				}
			}
			Set<Integer> copyParentSet=new HashSet<Integer>(CoreParents);
			parentMap.put(node, CoreParents);	
			originalMap.put(node, copyParentSet);	
		}	
		sourceGraph.clear();
		targetGraph.clear();
		
		//�洢һ��ԭʼ	
		/*for(Integer index: parentMap.keySet())
		{
			originalMap.put(index, parentMap.get(index));
		}*/
		
		for(Mapping m : a)  //����mapping�Ĳ���
		{
			int source = m.getSourceId();
			int target = m.getTargetId();		
			//Store the parentRelations according to the mappings
			//System.out.println(m.toString());
			if(m.getRelationship().equals(MappingRelation.SUBCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				if(parentMap.keySet().contains(source))
					parentMap.get(source).add(target);
				else
				{
					Set<Integer> set=new HashSet<Integer>();
					set.add(target);
					parentMap.put(source,set);
				}
			}
			if(m.getRelationship().equals(MappingRelation.SUPERCLASS)||m.getRelationship().equals(MappingRelation.EQUIVALENCE))
			{
				if (parentMap.keySet().contains(target))
					parentMap.get(target).add(source);
				else 
				{
					Set<Integer> set = new HashSet<Integer>();
					set.add(source);
					parentMap.put(target, set);
				}
			}
			
			//���ܲ���Ҫ�ˣ���Ϊ��alignment��������Ѿ����ˡ�
			//�������Զ�parent������
			/*if(aml.getSource().getObjectProperties().contains(source)||aml.getTarget().getObjectProperties().contains(target))
			{
				
			}	
			//��ֵ���Զ�parent������
			else if (aml.getSource().getDataProperties().contains(source)||aml.getTarget().getDataProperties().contains(target)) 
			{
				
			}*/
		}
		
		
	}

	//Builds the map of ancestral relations between all classes
	//in the checkList and all classes in the classList, with
	//(breadth first) transitive closure
	private void EnhanceParentMap()
	{
		for(int pro:aml.getSource().effectivePropertyID)
		{
			HashMap<Integer, Integer> childparent = new HashMap<Integer, Integer>();
			Table2Map<Integer,Integer,Integer> map=aml.getSource().getSomeValues(); 
			if(aml.getSource().getSomeValues()!=null&&aml.getSource().getSomeValues().contains(pro))
				childparent=aml.getSource().getSomeValues().get(pro);				
			for(int child :childparent.keySet())
			{
				int parent =childparent.get(child);
				String anonymousClass = "exist_"+aml.getURIMap().getURI(pro)+" "+aml.getURIMap().getLocalName(parent);
				int inverseClassId=aml.getURIMap().addURI(anonymousClass, EntityType.CLASS);
				aml.getSource().getClasses().add(inverseClassId);	
				//定位找到 Exist P， Exist_inverse P-
				int ExistID = aml.getURIMap().getIndex("exist_"+aml.getURIMap().getURI(pro));
				int ExistInverseID = aml.getURIMap().getIndex("inverse_exist_"+aml.getURIMap().getURI(pro));			
				//添加包含关系  B < Exists P.A  Exists P.A < Exists P 
				rels.addDirectSubclass(child, inverseClassId);
				rels.addDirectSubclass(inverseClassId, ExistID);			
				//添加影响关系   Exist P ～ Exist_inverse P-  Exist_inverse P- ～ Exist P
				//rels.addInfluence(ExistID,ExistInverseID);		
				//rels.addInfluence(ExistInverseID,ExistID);
				//添加影响关系   Exist P^- ～ A   A ～Exist P^-							
				rels.addInfluence(ExistInverseID,parent);
				rels.addInfluence(parent,ExistInverseID);					
			}
		}
		for(int pro:aml.getTarget().effectivePropertyID)
		{
			//System.out.println(aml.getURIMap().getURI(pro));
			HashMap<Integer, Integer> childparent = new HashMap<Integer, Integer>();
			Table2Map<Integer,Integer,Integer> map=aml.getTarget().getSomeValues(); 
			if(aml.getTarget().getSomeValues()!=null&&aml.getTarget().getSomeValues().contains(pro))
				childparent=aml.getTarget().getSomeValues().get(pro);				
			for(int child :childparent.keySet())
			{
				int parent =childparent.get(child);
				String anonymousClass = "exist_"+aml.getURIMap().getURI(pro)+" "+aml.getURIMap().getLocalName(parent);
				int inverseClassId=aml.getURIMap().addURI(anonymousClass, EntityType.CLASS);
				if(aml.getTarget().getObjectProperties().contains(pro)||aml.getTarget().getDataProperties().contains(pro))
					aml.getTarget().getClasses().add(inverseClassId);
				//定位找到 Exist P， Exist_inverse P-
				int ExistID = aml.getURIMap().getIndex("exist_"+aml.getURIMap().getURI(pro));
				int ExistInverseID = aml.getURIMap().getIndex("inverse_exist_"+aml.getURIMap().getURI(pro));			
				//添加包含关系  B < Exists P.A  Exists P.A < Exists P 
				rels.addDirectSubclass(child, inverseClassId);
				rels.addDirectSubclass(inverseClassId, ExistID);			
				//添加影响关系   Exist P ～ Exist_inverse P-  Exist_inverse P- ～ Exist P
				//rels.addInfluence(ExistID,ExistInverseID);		
				//rels.addInfluence(ExistInverseID,ExistID);
				//添加影响关系   Exist P^- ～ A   A ～Exist P^-							
				rels.addInfluence(ExistInverseID,parent);
				rels.addInfluence(parent,ExistInverseID);		
	
			}
		}
		
		for(Integer node: rels.getInfluence())
		{
			if(parentMap.keySet().contains(node))
				parentMap.get(node).addAll(rels.getInfluence(node));
			else
			{
				Set<Integer> set=new HashSet<Integer>();
				set.addAll(rels.getInfluence(node));
				parentMap.put(node,set);
			}
		}
	}
	
	private void buildAncestorMap()
	{
		//First get the "direct" relations between checkList
		//and classList classes, which are present in the
		//RelationshipMap, plus the relations through direct
		//mappings of checkList classes
		for(Integer i : checkList)
		{
			//System.out.println(i);
			//Direct relations
			Set<Integer> ancs = rels.getSuperClasses(i,false);
			for(Integer j : ancs)
				if(classList.contains(j))
					addRelation(i, j, new Path());
			//Mappings
			Set<Integer> maps = a.getMappingsBidirectional(i); //Ӧ�û᷵��2��mapping��(����ֻ������һ��)
			for(Integer j : maps)
			{
				//Get both the mapping and its ancestors
				int index = a.getIndexBidirectional(i, j);
				HashSet<Integer> newAncestors = new HashSet<Integer>(rels.getSuperClasses(j,false));
				newAncestors.add(j);
				//And add them
				for(Integer m : newAncestors)
					if(classList.contains(m))
						addRelation(i,m,new Path(index));
			}
		}
		//Then add paths iteratively by extending paths with new
		//mappings, stopping when the ancestorMap stops growing
		int size = 0;
		for(int i = 0; size < ancestorMap.size(); i++)
		{
			//Set<Integer> concepts=new HashSet<>(ancestorMap.keySet(i));
			size = ancestorMap.size();
			//For each class in the checkList
			for(Integer j : checkList)
			{
				//If it has ancestors through paths with i mappings
				
				if(!pathLengths.contains(j, i))
					continue;
				//We get those ancestors
				HashSet<Integer> ancestors = new HashSet<Integer>(pathLengths.get(j,i));
				//For each such ancestor
				for(Integer k : ancestors)
				{
					//Cycle check 1 (make sure ancestor != self)
					if(k == j)
						continue;
					//Get the paths between the class and its ancestor
					HashSet<Path> paths = new HashSet<Path>();
					for(Path p : ancestorMap.get(j, k))
						if(p.size() == i)
							paths.add(p);
					//Get the ancestor's mappings
					Set<Integer> maps = a.getMappingsBidirectional(k);
					//And for each mapping
					for(Integer l : maps)
					{
						//Cycle check 2 (make sure mapping != self)
						if(l == j)
							continue;
						//We get its ancestors
						int index = a.getIndexBidirectional(k, l);
						HashSet<Integer> newAncestors = new HashSet<Integer>(rels.getSuperClasses(l,false));
						//Plus the mapping itself
						newAncestors.add(l);
						//Now we must increment all paths between j and k
						for(Path p : paths)
						{
							//Cycle check 3 (make sure we don't go through the
							//same mapping twice)
							if(p.contains(index))
								continue;
							//We increment the path by adding the new mapping
							Path q = new Path(p);
							q.add(index);
							//And add a relationship between j and each descendant of
							//the new mapping (including the mapping itself) that is
							//on the checkList
							for(Integer m : newAncestors)
								//Cycle check 4 (make sure mapping descendant != self)
								if(classList.contains(m) && m != j)
									addRelation(j,m,q);
						}
					}
				}
			}
		}
		
		for(int i = 0; size < ancestorMap.size(); i++)
		{
			//Set<Integer> concepts=new HashSet<>(ancestorMap.keySet(i));
			size = ancestorMap.size();
			//For each class in the checkList
			for(Integer j : checkList)
			{
				//If it has ancestors through paths with i mappings
				
				if(!pathLengths.contains(j, i))
					continue;
				//We get those ancestors
				HashSet<Integer> ancestors = new HashSet<Integer>(pathLengths.get(j,i));
				//For each such ancestor
				for(Integer k : ancestors)
				{
					//Cycle check 1 (make sure ancestor != self)
					if(k == j)
						continue;
					//Get the paths between the class and its ancestor
					HashSet<Path> paths = new HashSet<Path>();
					for(Path p : ancestorMap.get(j, k))
						if(p.size() == i)
							paths.add(p);
					//Get the ancestor's mappings
					Set<Integer> maps = a.getMappingsBidirectional(k);
					//And for each mapping
					for(Integer l : maps)
					{
						//Cycle check 2 (make sure mapping != self)
						if(l == j)
							continue;
						//We get its ancestors
						int index = a.getIndexBidirectional(k, l);
						HashSet<Integer> newAncestors = new HashSet<Integer>(rels.getSuperClasses(l,false));
						//Plus the mapping itself
						newAncestors.add(l);
						//Now we must increment all paths between j and k
						for(Path p : paths)
						{
							//Cycle check 3 (make sure we don't go through the
							//same mapping twice)
							if(p.contains(index))
								continue;
							//We increment the path by adding the new mapping
							Path q = new Path(p);
							q.add(index);
							//And add a relationship between j and each descendant of
							//the new mapping (including the mapping itself) that is
							//on the checkList
							for(Integer m : newAncestors)
								//Cycle check 4 (make sure mapping descendant != self)
								if(classList.contains(m) && m != j)
									addRelation(j,m,q);
						}
					}
				}
			}
		}
		
		//Finally add relations between checkList classes and
		//themselves when they are involved in disjoint clauses
		//(to support the buildClassConflicts method)
		for(Integer i : checkList)
			if(rels.hasDisjoint(i))
				ancestorMap.add(i, i, new Path());
		
		/*for(Integer des: ancestorMap.keySet())
		{
			Set<Integer> concepts=new HashSet<>(ancestorMap.keySet(des));
			ancestors.put(des, concepts);
		}*/
		
		Graph tempGraph=new Graph();
		tempGraph.init(parentMap);
			
		for(Integer des: ancestorMap.keySet())
		{
			Set<Integer> concepts=new HashSet<>(ancestorMap.keySet(des));
			concepts.remove(des);  //�Ƴ�����
			for(Integer dis: rels.getDisjoint())
			{
				if(concepts.contains(dis)||des==dis)
					continue;
				List<ArrayList<Integer>> paths=tempGraph.getPaths(des, dis);
				if(!paths.isEmpty())
				{
					concepts.add(dis);											
				}
			}
			ancestors.put(des, concepts);
		}
		
	/*	for(Integer des: ancestorMap.keySet())
		{
			Set<Integer> concepts=new HashSet<>();
			Set<Integer> ancs=new HashSet<>(ancestorMap.keySet(des));
			ancs.remove(des);  //�Ƴ�����
			
			
			for(Integer anc: ancs)  //�������Ƚ��
			{
				List<ArrayList<Integer>> paths=tempGraph.getPaths(des, anc);
				if(!paths.isEmpty())
				{
					concepts.add(anc);											
				}
			}
						
			for(Integer dis: rels.getDisjoint()) //���²��ཻ�Ľ��
			{
				if(concepts.contains(dis)||des==dis)
					continue;
				List<ArrayList<Integer>> paths=tempGraph.getPaths(des, dis);
				if(!paths.isEmpty())
				{
					concepts.add(dis);											
				}
			}
			ancestors.put(des, concepts);
		}*/
				
		ancestorMap.clear();
		pathLengths.clear();			
	}
	
	//Adds a relation to the ancestorMap (and pathLengths)
	private void addRelation(int child, int parent, Path p)
	{
		if(ancestorMap.contains(child,parent))
		{
			Vector<Path> paths = ancestorMap.get(child,parent);
			for(Path q : paths)
				if(p.contains(q))
					return;
		}
		ancestorMap.add(child,parent,p);
		pathLengths.add(child, p.size(), parent);
		if(rels.hasDisjoint(parent))
			pathCount++;
	}
	
	
	
	private void buildMIPPs()
	{
		long num=0;
		for(Integer i : checkList)
		{
			//Get its minimized conflicts
			System.out.println("########################################");
			System.out.println("The check number is "+num+"/"+checkList.size());
			buildClassConflictPaths(i);
			num++;			
		}
		//��ʱ�ͷ�
		ancestors.clear();
		checkedState.clear();
		for (Pair<ArrayList<Integer>, ArrayList<Integer>> mipp : MinimalConflictSet) 
		{
			mipp.expand(a, rels);
			Set<Integer> set=getMappings(mipp.left,mipp.right);
			//Set<Integer> set=mipp.getMappings();
			for(Integer map: set)
			{
				if(MapMinimalConflictSet.keySet().contains(map))
				    MapMinimalConflictSet.get(map).add(mipp);
				else
				{
					ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>> list=new ArrayList<Pair<ArrayList<Integer>, ArrayList<Integer>>>();
					list.add(mipp);
					MapMinimalConflictSet.put(map, list);
				}
			}
		}			
	}
	
	
	private void buildClassConflictPaths(int classId)
	{	
		//First get all ancestors involved in disjoint clauses	
		HashSet<Integer> disj = new HashSet<Integer>();
		/*for(Integer i : ancestorMap.keySet(classId))
			if(rels.hasDisjoint(i))
				disj.add(i);*/
		for(Integer i : ancestors.get(classId))
			if(rels.hasDisjoint(i))
				disj.add(i);
		
		//Plus the class itself, if it has a disjoint clause
		if(rels.hasDisjoint(classId))
			disj.add(classId);	
		
		//System.out.println(classId);
		for(Integer i : disj)
		{
			List<ArrayList<Integer>> Paths1=new ArrayList<ArrayList<Integer>>();
			Paths1=graph.getPaths(classId, i);
			Paths1=FilterPaths(Paths1);
			if(Paths1==null)
				continue;
			for(Integer j : rels.getDisjoint(i))
			{
				/*if(!disj.contains(j))  //i>j���Ǻܶ�ΪʲôҪ������ƣ������ǶԳ��ԵĹ�ϵ��ֻ��Ҫ����һ�뼴��
					continue;*/
				if(i > j || !disj.contains(j))  //i>j���Ǻܶ�ΪʲôҪ������ƣ������ǶԳ��ԵĹ�ϵ��ֻ��Ҫ����һ�뼴��
					continue;
				if(checkedState.keySet().contains(classId+"-"+i+"-"+j))
						continue;
				if(checkedState.keySet().contains(classId+"-"+j+"-"+i))
						continue;	
				
				/*System.out.println("-------------------------------------------------------------");
				System.out.println("The ID of unsatisfiable concept��"+classId+":"+AML.getInstance().getURIMap().getLocalName(classId));
				//�������� AML.getInstance().getURIMap().contains(classId)������������
				System.out.println("Disjoint Node 1��"+i+":"+AML.getInstance().getURIMap().getLocalName(i));
				System.out.println("Disjoint Node 1��"+j+":"+AML.getInstance().getURIMap().getLocalName(j));
				System.out.println("-------------------------------------------------------------");	*/		
				/*ArrayList<Integer> Path1 =new ArrayList<Integer>();
				ArrayList<Integer> Path2 =new ArrayList<Integer>();*/			
				
				List<ArrayList<Integer>> Paths2=new ArrayList<ArrayList<Integer>>();											
				Paths2=graph.getPaths(classId, j);				
				Paths2=FilterPaths(Paths2);
				if(Paths2==null)
					continue;
				//one unsatisfied mapping may cause more than one mipp
				for(ArrayList<Integer> p1:Paths1)
				{					
					for(ArrayList<Integer> p2:Paths2)
					{
						//��Ϊ��ָ�룬��Path1��ȡֵ�����ı�ʱ����Ժ���Ĳ�������Ӱ��
						ArrayList<Integer> temp1=new ArrayList<Integer>();
						temp1.addAll(p1);
						//��Ϊ��ָ�룬��Path2��ȡֵ�����ı�ʱ����Ժ���Ĳ�������Ӱ��
						ArrayList<Integer> temp2=new ArrayList<Integer>();
						temp2.addAll(p2);
						ArrayList<Integer> common=RefinePath(temp1,temp2); 																		
						Pair<ArrayList<Integer>,ArrayList<Integer>> mipp=new Pair<ArrayList<Integer>, ArrayList<Integer>>(temp1,temp2);									
						
						//if there exist more than two influence arcs 
//						if(!satisfiedInfluenceCondition(mipp))
//							continue;
						
						if(!MinimalConflictSet.contains(mipp))	
						{
							//judge the real MIPS
							Set<Integer> set=getMappings(mipp.left,mipp.right);
							if(set.isEmpty())
								continue;
							MinimalConflictSet.add(mipp);	
							//����·����������ʽ�����ǿ����жϴ�classId��������Path�����֮��ĵ㶼�Ѿ���ȫ��������(ע��·����Լ��)��
							for(Integer concept: common)
							{
								if(checkList.contains(concept))
									checkedState.put(concept+"-"+i+"-"+j, true);
							}
							int realUnsatisfied=temp1.get(0);
							if(checkList.contains(realUnsatisfied))
								checkedState.put(realUnsatisfied+"-"+i+"-"+j, true);
						}
						//MinimalConflictSetMap.put(key, mipp);
					}
				}
				/*System.out.println("���˺��·��Ϊ:"); //��ʵ�ǰ������еĸ��׽��
				System.out.println("**********************");
				printResult(Paths1);
				System.out.println("**********************");
				printResult(Paths2);
				System.out.println("**********************");*/
				//System.out.println(checkedState.size());							
			}
		}	
	}
	
	//Adds a path to a list of conflict sets if it is a minimal path
	//(this only results in a minimal list of paths if paths are added
	//in order, after sorting)
	private void addConflict(Path p, Vector<Path> paths)
	{
		for(Path q : paths)
			if(p.contains(q))
				return;
		paths.add(p);
	}
	
	
	public List<ArrayList<Integer>> FilterPaths(List<ArrayList<Integer>> paths)
	{				
		//������Ӧ��ͻ������ӳ���ϵ
		int min=Integer.MAX_VALUE;
		List<ArrayList<Integer>> newPaths=new ArrayList<ArrayList<Integer>>();
		HashMap<Integer, List<ArrayList<Integer>>> pathMap=new HashMap<Integer, List<ArrayList<Integer>>>();
		HashMap<Integer, Vector<Path>> conflictMap=new HashMap<Integer, Vector<Path>>();
		for(ArrayList<Integer> pa:paths)
		{
			//Vector<Path> conflict =new Vector<Path>();
			Path conflict=new Path();
			for(int i=0;i<pa.size()-1;i++)
			{
				int index=-1;
				int node1=pa.get(i);
				int node2=pa.get(i+1);		
				index=a.getIndexBidirectional(node1, node2);
				if(index!=-1)
				{
					//conflict.add(new Path(index));	
					conflict.add(index);	
				}
			}
			if(conflictMap.containsKey(conflict.size()))
			{
				Vector<Path> set=conflictMap.get(conflict.size());
				set.add(conflict);
				conflictMap.put(conflict.size(), set);
				
				List<ArrayList<Integer>> tempPath=pathMap.get(conflict.size());
				tempPath.add(pa);
				pathMap.put(conflict.size(), tempPath);			
			}
			else
			{
				Vector<Path> set=new Vector<Path>();
				List<ArrayList<Integer>> tempPath=new ArrayList<ArrayList<Integer>>();
				set.add(conflict);
				conflictMap.put(conflict.size(), set);
				tempPath.add(pa);
				pathMap.put(conflict.size(), tempPath);
			}
			if(min>conflict.size())
				min=conflict.size();			
		}			
		newPaths=pathMap.get(min);
		return newPaths;
	}
	
	
	//refine the minimal conflict paths
	public ArrayList<Integer> RefinePath(ArrayList<Integer> Path1,ArrayList<Integer> Path2)
	{	
		ArrayList<Integer> commonNums=new ArrayList<Integer>();
		if(Path1.size()==1||Path2.size()==1)  //����Ϊ1,���뾫��
			return commonNums;	
		Iterator<Integer> iterator1 = Path1.iterator();
		Iterator<Integer> iterator2 = Path2.iterator();
		
		while(iterator1.hasNext()&&iterator2.hasNext())
		{
			int num1=iterator1.next();
			int num2=iterator2.next();
			if(num1==num2)
			{
				commonNums.add(num1);
			}	
			else if(!commonNums.isEmpty()) //�����˲���ȵ����
			{
				commonNums.remove(commonNums.size()-1);
				break;
			}
		}
		//
		if(commonNums.size()==Path1.size()||commonNums.size()==Path1.size()) //����·�����ص����������һ��Ԫ��
		{
			commonNums.remove(commonNums.size()-1);
		}
		if(!commonNums.isEmpty())
		{
			Path1.removeAll(commonNums);
			Path2.removeAll(commonNums);
		}	
		return commonNums;
		
	}
	
	public KG generateKG(ArrayList<Integer> Path1,ArrayList<Integer> Path2)
	{	
		KG kg=new KG();

		Set<Integer> mapping=new HashSet<Integer>();
		List<Integer> sourceConcept=new ArrayList<Integer>() ;
		List<Integer> targeConcept=new ArrayList<Integer>() ;
		ArrayList<String> sourceRel=new ArrayList<String>();
		ArrayList<String> targetRel=new ArrayList<String>();
	
		Set<Integer> source=aml.getSource().getClasses();		
	
		int unsatisfiedConcept=Path1.get(0);
		if(source.contains(unsatisfiedConcept))
		{
			sourceConcept.add(unsatisfiedConcept);
		}
		else
		{
			targeConcept.add(unsatisfiedConcept);
		}
		
		for(int i=0;i<Path1.size()-1;i++)
		{
			int index=-1;
			int node1=Path1.get(i);
			int node2=Path1.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				mapping.add(index);
			}
			if(source.contains(node2))
			{
				sourceConcept.add(node2);
			}
			else
			{
				targeConcept.add(node2);		
			}
		}
		
		for(int i=0;i<Path2.size()-1;i++)
		{
			int index=-1;
			int node1=Path2.get(i);
			int node2=Path2.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				mapping.add(index);
			}
			if(source.contains(node2))
			{
				sourceConcept.add(node2);
			}
			else
			{
				targeConcept.add(node2);		
			}
		}
		
		for(int con:sourceConcept)
		{
			for(int parent:parentMap.get(con))
			{
				if(sourceConcept.contains(parent))
					sourceRel.add(con+"--"+parent);
			}
		}
		
		for(int con:targeConcept)
		{
			for(int parent:parentMap.get(con))
			{
				if(targeConcept.contains(parent))
					targetRel.add(con+"--"+parent);
			}
		}
		
		kg.init(sourceConcept,targeConcept,mapping,sourceRel,targetRel);
		return kg;
	}
	
	//refine the minimal conflict paths
	public boolean satisfiedInfluenceCondition(Pair<ArrayList<Integer>,ArrayList<Integer>> mipp)
	{
		int leftNum=0;
		int rightNum=0;
		for(int i=0;i<mipp.left.size()-1;i++)
		{
			int node1=mipp.left.get(i);
			int node2=mipp.left.get(i+1);
			//if(rels.hasInfluenceClause(node1, node2))
			if(rels.getInfluence(node1).contains(node2))
				leftNum++;
		}
		for(int i=0;i<mipp.right.size()-1;i++)
		{
			int node1=mipp.right.get(i);
			int node2=mipp.right.get(i+1);
			//if(rels.hasInfluenceClause(node1, node2))
			if(rels.getInfluence(node1).contains(node2))
				rightNum++;
		}
		if(leftNum+rightNum<=1)
			return true;
		else
			return false;
	}
	
	//�����Ľ���е�·�����Ƿ������ͬ��mappings��ID,������ʼ������ֹ��(num�Ǵ���֤��ƥ��,approveNum���Ѿ���ͬ��ƥ��)
	public Mapping getEntailedMapping(List<ArrayList<Integer>> Paths,int num,int approveNum,int start, int end)
	{
		Mapping mapping=null;
		for(ArrayList<Integer> Path:Paths)
		{
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				index = a.getIndexBidirectional(node1, node2);
				if(index!=-1&&index==approveNum)
				{					
					Mapping m = a.get(num);  //��ȡ����֤��mapping
					int sourceId = m.getSourceId();
					int targetId = m.getTargetId();
					double sim=m.getSimilarity();
					if (sourceId == start && targetId == end) 
					{
						if(mapping==null)						
							mapping= new Mapping(sourceId, targetId,sim, MappingRelation.SUBCLASS);
						else if(mapping!=null&&mapping.getRelationship().equals(MappingRelation.SUPERCLASS))
						{
							mapping.setRelationship(MappingRelation.EQUIVALENCE);	
							return mapping;  //�ȼ�֮�����������
						}
						break;	//һ��·���ﲻ���ܴ���ֱ���ǵȼ�mapping�����					
					} 
					else if(sourceId == end && targetId == start)
					{
						if(mapping==null)						
							mapping= new Mapping(sourceId, targetId,sim, MappingRelation.SUPERCLASS);
						else if(mapping!=null&&mapping.getRelationship().equals(MappingRelation.SUBCLASS))
						{
							mapping.setRelationship(MappingRelation.EQUIVALENCE);	
							return mapping;   //�ȼ�֮�����������
						}
						break; //һ��·���ﲻ���ܴ���ֱ���ǵȼ�mapping�����						
					}
				}			
			}
		}			
		return mapping;
	}
	
	public Mapping getEntailedMapping2(List<ArrayList<Integer>> Paths,int num,int approveNum,int start, int end)
	{
		Mapping mapping=null;
		for(ArrayList<Integer> Path:Paths)
		{
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				index = a.getIndexBidirectional(node1, node2);
				if(index!=-1&&index==approveNum)
				{					
					Mapping m = a.get(num);
					int sourceId = m.getSourceId();
					int targetId = m.getTargetId();
					double sim=m.getSimilarity();
					if (sourceId == start && targetId == end) 
					{
						if(mapping==null)						
							mapping= new Mapping(sourceId, targetId,sim, MappingRelation.SUBCLASS);
						else if(mapping!=null&&mapping.getRelationship().equals(MappingRelation.SUPERCLASS))
						{
							mapping.setRelationship(MappingRelation.EQUIVALENCE);	
							return mapping;  //�ȼ�֮�����������
						}
						break;	//һ��·���ﲻ���ܴ���ֱ���ǵȼ�mapping�����					
					} 
					else 
					{
						if(mapping==null)						
							mapping= new Mapping(sourceId, targetId,sim, MappingRelation.SUPERCLASS);
						else if(mapping!=null&&mapping.getRelationship().equals(MappingRelation.SUBCLASS))
						{
							mapping.setRelationship(MappingRelation.EQUIVALENCE);	
							return mapping;   //�ȼ�֮�����������
						}
						break; //һ��·���ﲻ���ܴ���ֱ���ǵȼ�mapping�����						
					}
				}			
			}
		}			
		return mapping;
	}
	
	public Boolean entailStrongRejectedMapping(List<ArrayList<Integer>> Paths,int checkNum)
	{
		for(ArrayList<Integer> Path:Paths)
		{
			Set<Integer> mapping=new HashSet<Integer>();
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				index = a.getIndexBidirectional(node1, node2);
				if(index!=-1)
				{					
					mapping.add(index);
				}			
			}
			if(mapping.contains(checkNum))
				return true;
			/*if(mapping.contains(checkNum)&&mapping.size()==1)
				return true;*/
		}		
		return false;
	}
	
	public Boolean entailStrongRejectedMappingExactly(List<ArrayList<Integer>> Paths,int checkNum, Mapping checkMapping)
	{
		for(ArrayList<Integer> Path:Paths)
		{
			Set<Integer> mapping=new HashSet<Integer>();
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				index = a.getIndexBidirectional(node1, node2);
				if(index!=-1)
				{					
					mapping.add(index);
				}			
			}
			if(mapping.contains(checkNum))
				return true;
			/*if(mapping.contains(checkNum)&&mapping.size()==1)
				return true;*/
		}		
		return false;
	}
	
	//���ܾ���·�����Ƿ���δ����ǵ�mappingǣ�����У�����еĻ������ܾ�
	public Mapping entailWeakRejectedMapping(List<ArrayList<Integer>> Paths,int checkNum, int source, int target)
	{
		Mapping m=null;
		for(ArrayList<Integer> Path:Paths)
		{
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				index = a.getIndexBidirectional(node1, node2);
				if(index!=-1&&index==checkNum)
				{	
					double sim=a.get(index).getSimilarity();
					if (source == node1 && target == node2) 
					{
						if(m==null)
							m = new Mapping(source, target,sim, MappingRelation.SUBCLASS);
						else if(m!=null&&m.getRelationship().equals(MappingRelation.SUPERCLASS))
						{
							m.setRelationship(MappingRelation.EQUIVALENCE);
							return m;
						}
						break;
					} 
					else 
					{
						if(m==null)
							m = new Mapping(source, target,sim, MappingRelation.SUPERCLASS);
						else if(m!=null&&m.getRelationship().equals(MappingRelation.SUBCLASS))
						{
							m.setRelationship(MappingRelation.EQUIVALENCE);
							return m;	
						}
						break;
					}				
				}			
			}			
		}		
		return m;
	}
	
	public Set<Mapping> getEntailedMappings(List<ArrayList<Integer>> Paths)
	{
		Set<Mapping> mappingSet=new HashSet<Mapping>();
		for(ArrayList<Integer> Path:Paths)
		{
			Set<Mapping> mapping=new HashSet<Mapping>();
			for (int i = 0; i < Path.size() - 1; i++) 
			{
				int index = -1;
				int node1 = Path.get(i);
				int node2 = Path.get(i + 1);
				if(index!=-1)
				{	
					index = a.getIndexBidirectional(node1, node2);
					Mapping m = a.get(index);
					int sourceId = m.getSourceId();
					int targetId = m.getTargetId();
					double sim = m.getSimilarity();
					if (sourceId == node1 && targetId == node2) 
					{
						Mapping newMapping = new Mapping(sourceId, targetId,sim, MappingRelation.SUBCLASS);
						mapping.add(newMapping);
					} 
					else 
					{
						Mapping newMapping = new Mapping(sourceId, targetId,sim, MappingRelation.SUPERCLASS);
						mapping.add(newMapping);
					}
				}			
			}
			mappingSet.addAll(mapping);
		}		
		return mappingSet;
	}
	
	public Set<Integer> getMappings(ArrayList<Integer> Path1,ArrayList<Integer> Path2)
	{
		Set<Integer> mapping=new HashSet<Integer>();
		for(int i=0;i<Path1.size()-1;i++)
		{
			int index=-1;
			int node1=Path1.get(i);
			int node2=Path1.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				mapping.add(index);
			}
		}
		
		for(int i=0;i<Path2.size()-1;i++)
		{
			int index=-1;
			int node1=Path2.get(i);
			int node2=Path2.get(i+1);		
			index=a.getIndexBidirectional(node1, node2);
			if(index!=-1)
			{	
				mapping.add(index);
			}
		}
		return mapping;
	}
	
	public void RefreshSet(ArrayList<Mapping> set, Mapping mapping)
	{
		for(int i=0; i<set.size();i++)
		{
			Mapping m=set.get(i);
			int sourceId=m.getSourceId();
			int targetId=m.getTargetId();
			if(sourceId==mapping.getSourceId()&&targetId==mapping.getTargetId())  //��㶼�ǻ���source��targetֻ�ǹ�ϵ���ܲ�ͬ��ע��ȼ۹�ϵ�ǲ���refresh����Ϊ�Ѿ�����ˡ�
			{
				set.remove(i);
				break;
			}
		}
	}

	public boolean existRejectiveMapping(Set<Integer> mapping, ArrayList<Mapping> wantMapping, int index)
	{
		if(!mapping.contains(index))
			return false;
		Set<Integer> wantset=new HashSet<>();
		for(Mapping m:wantMapping)
		{				
			int id=a.getIndexBidirectional(m.getSourceId(), m.getTargetId());
			wantset.add(id);			
		}
		//�ж�mapping���������wantMapping
		mapping.removeAll(wantMapping);
		if(mapping.size()==2)
			return true;		
		return false;
	}
	
	public boolean existEquivalence(ArrayList<Mapping> set, Mapping mapping)
	{
		for(int i=0; i<set.size();i++)
		{
			Mapping m=set.get(i);
			int sourceId=m.getSourceId();
			int targetId=m.getTargetId();
			if(sourceId==mapping.getSourceId()&&targetId==mapping.getTargetId())
			{
				if(m.getRelationship().equals(MappingRelation.SUBCLASS)&&mapping.getRelationship().equals(MappingRelation.SUPERCLASS))
					return true;
				else if(m.getRelationship().equals(MappingRelation.SUPERCLASS)&&mapping.getRelationship().equals(MappingRelation.SUBCLASS))
					return true;
			}
		}
		return false;
	}
	
	public void ExtendClassListByProperty()
	{
		for(Integer i : a.getSources())
			if(AML.getInstance().getURIMap().isProperty(i))
				classList.add(i);
		for(Integer i : a.getTargets())
			if(AML.getInstance().getURIMap().isProperty(i))
				classList.add(i);
	}
	
	public void ExtendCheckListByProperty()
	{
		HashSet<Integer> removedList=new HashSet<Integer>();
		for(Integer i: classList)
		{
			if(removedList.contains(i))
			    continue;
			boolean flag=false;
			if(aml.getSource().getObjectProperties().contains(i)||aml.getTarget().getObjectProperties().contains(i))
			{
				checkList.add(i);		
				flag=true;
			}
			else if(aml.getSource().getDataProperties().contains(i)||aml.getTarget().getDataProperties().contains(i))
			{
				checkList.add(i);
				flag=true;
			}
			//���ڽ�ɫ����������
			else if(aml.getURIMap().getURI(i).contains("exist_")||aml.getURIMap().getURI(i).contains("inverse_"))
			{
				checkList.add(i);
				flag=true;
			}
			if(flag) //��һ�߲�������ĵ�������ƥ��
			{
				Set<Integer> num=a.getMappingsBidirectional(i);
				removedList.addAll(num);
			}
		}
	}
	
	public void extendDataPropety4MapppingGraph(int source,int target, ArrayList<Mapping> wantMappings,ArrayList<Mapping> unWantMappings)
	{
		int Index=a.getIndexBidirectional(source, target);
		Mapping m=maps.get(Index);
		
		//���¶�Ӧ��mapping
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,m);	
		if(!wantMappings.contains(m))
			wantMappings.add(m);
		RefreshSet(unWantMappings,m);
		
		//���¶�Ӧ��ͼ
		addEdge(source, target);
		addEdge(target, source);
		
		
		String sourceProperty=aml.getURIMap().getURI(source);	
		String sourceUri = "inverse_exist_"+sourceProperty;		
		int sourceExistInverseProperty=aml.getURIMap().getIndex(sourceUri);
		
		String targetProperty=aml.getURIMap().getURI(target);
		String targetUri ="inverse_exist_"+targetProperty;	
		int targetExistInverseProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendIndex=a.getIndexBidirectional(sourceExistInverseProperty, targetExistInverseProperty);
		Mapping existInverseMapping=maps.get(extendIndex);
		
		//���¶�Ӧ��mapping
		existInverseMapping.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(existInverseMapping.getSourceId(), existInverseMapping.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,existInverseMapping);	
		if(!wantMappings.contains(existInverseMapping))
			wantMappings.add(existInverseMapping);
		RefreshSet(unWantMappings,existInverseMapping);
		
		//���¶�Ӧ��ͼ
		addEdge(sourceExistInverseProperty, targetExistInverseProperty);
		addEdge(targetExistInverseProperty, sourceExistInverseProperty);		
	}
	
	public void  extendObjectPropety4MapppingGraph(int source,int target, ArrayList<Mapping> wantMappings,ArrayList<Mapping> unWantMappings)
	{
		
		int Index=a.getIndexBidirectional(source, target);
		Mapping m=maps.get(Index);
		
		//���¶�Ӧ��mapping
		m.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,m);
		if(!wantMappings.contains(m))
			wantMappings.add(m);
		RefreshSet(unWantMappings,m);
		
		//���¶�Ӧ��ͼ
		addEdge(source, target);
		addEdge(target, source);
		
		
		String sourceProperty=aml.getURIMap().getURI(source);	
		String sourceUri = "inverse_exist_"+sourceProperty;		
		int sourceExistInverseProperty=aml.getURIMap().getIndex(sourceUri);
		
		String targetProperty=aml.getURIMap().getURI(target);
		String targetUri ="inverse_exist_"+targetProperty;	
		int targetExistInverseProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendIndex=a.getIndexBidirectional(sourceExistInverseProperty, targetExistInverseProperty);
		Mapping existInverseMapping=maps.get(extendIndex);
		
		//���¶�Ӧ��mapping
		existInverseMapping.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(existInverseMapping.getSourceId(), existInverseMapping.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,existInverseMapping);
		if(!wantMappings.contains(existInverseMapping))
			wantMappings.add(existInverseMapping);
		RefreshSet(unWantMappings,existInverseMapping);
		
		//���¶�Ӧ��ͼ
		addEdge(sourceExistInverseProperty, targetExistInverseProperty);
		addEdge(targetExistInverseProperty, sourceExistInverseProperty);
		
		
	    sourceUri = "exist_"+sourceProperty;		
		int sourceExistProperty=aml.getURIMap().getIndex(sourceUri);
		
		targetUri ="exist_"+targetProperty;	
		int targetExistProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendExistIndex=a.getIndexBidirectional(sourceExistProperty, targetExistProperty);
		Mapping Existmapping=maps.get(extendExistIndex);
		
		//���¶�Ӧ��mapping
		Existmapping.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(Existmapping.getSourceId(), Existmapping.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,Existmapping);	
		if(!wantMappings.contains(Existmapping))
			wantMappings.add(Existmapping);
		RefreshSet(unWantMappings,Existmapping);
		
		//���¶�Ӧ��ͼ
		addEdge(sourceExistProperty, targetExistProperty);
		addEdge(targetExistProperty, sourceExistProperty);
		
		sourceUri = "inverse_"+sourceProperty;		
		int sourceInverseProperty=aml.getURIMap().getIndex(sourceUri);
		
		targetUri ="inverse_"+targetProperty;	
		int targetInverseProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendInverseIndex=a.getIndexBidirectional(sourceInverseProperty, targetInverseProperty);
		Mapping Inversemapping=maps.get(extendInverseIndex);
		
		//���¶�Ӧ��mapping
		Inversemapping.setStatus(MappingStatus.CORRECT);
		aml.getAlignment().get(Inversemapping.getSourceId(), Inversemapping.getTargetId()).setStatus(MappingStatus.CORRECT);	
		RefreshSet(wantMappings,Inversemapping);
		if(!wantMappings.contains(Inversemapping))
			wantMappings.add(Inversemapping);
		RefreshSet(unWantMappings,Inversemapping);
		
		//���¶�Ӧ��ͼ
		addEdge(sourceInverseProperty, targetInverseProperty);
		addEdge(targetInverseProperty, sourceInverseProperty);
	}
	
	public Set<Integer> removeDataPropety4MapppingGraph(int source,int target, ArrayList<Mapping> wantMappings,ArrayList<Mapping> unWantMappings)
	{
		
		Set<Integer> removeMappings=new HashSet<Integer>();
		
		//ԭʼ��mapping���б��
		int Index=a.getIndexBidirectional(source, target);
		Mapping m=maps.get(Index);		
		//���¶�Ӧ��mapping
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,m);		
		RefreshSet(unWantMappings,m);
		if(!unWantMappings.contains(m))
			unWantMappings.add(m);
		removeMappings.add(Index);
		//���¶�Ӧ��ͼ
		removeEdge(source, target);	
		removeEdge(target, source);	
		
				
		String sourceProperty=aml.getURIMap().getURI(source);	
		String sourceUri = "inverse_exist_"+sourceProperty;		
		int sourceExistInverseProperty=aml.getURIMap().getIndex(sourceUri);
		
		String targetProperty=aml.getURIMap().getURI(target);
		String targetUri ="inverse_exist_"+targetProperty;	
		int targetExistInverseProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendIndex=a.getIndexBidirectional(sourceExistInverseProperty, targetExistInverseProperty);
		Mapping existInverseMapping=maps.get(extendIndex);
		
		//���¶�Ӧ��mapping
		existInverseMapping.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(existInverseMapping.getSourceId(), existInverseMapping.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,existInverseMapping);		
		RefreshSet(unWantMappings,existInverseMapping);
		if(!unWantMappings.contains(existInverseMapping))
			unWantMappings.add(existInverseMapping);		
		removeMappings.add(extendIndex);	
		//���¶�Ӧ��ͼ
		removeEdge(sourceExistInverseProperty, targetExistInverseProperty);	
		removeEdge(targetExistInverseProperty, sourceExistInverseProperty);	
		
		return removeMappings;
	}
	
	public Set<Integer>  removeObjectPropety4MapppingGraph(int source,int target, ArrayList<Mapping> wantMappings,ArrayList<Mapping> unWantMappings)
	{
		Set<Integer> removeMappings=new HashSet<Integer>();
		
		int Index=a.getIndexBidirectional(source, target);
		Mapping m=maps.get(Index);		
		//���¶�Ӧ��mapping
		m.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(m.getSourceId(), m.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,m);		
		RefreshSet(unWantMappings,m);
		if(!unWantMappings.contains(m))
			unWantMappings.add(m);
		removeMappings.add(Index);	
		//���¶�Ӧ��ͼ
		removeEdge(source, target);	
		removeEdge(target, source);	
			
		String sourceProperty=aml.getURIMap().getURI(source);	
		String sourceUri = "inverse_exist_"+sourceProperty;		
		int sourceExistInverseProperty=aml.getURIMap().getIndex(sourceUri);
		
		String targetProperty=aml.getURIMap().getURI(target);
		String targetUri ="inverse_exist_"+targetProperty;	
		int targetExistInverseProperty=aml.getURIMap().getIndex(targetUri);
		
		int extendIndex=a.getIndexBidirectional(sourceExistInverseProperty, targetExistInverseProperty);
		Mapping existInverseMapping=maps.get(extendIndex);
		
		//���¶�Ӧ��mapping
		existInverseMapping.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(existInverseMapping.getSourceId(), existInverseMapping.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,existInverseMapping);		
		RefreshSet(unWantMappings,existInverseMapping);
		if(!unWantMappings.contains(existInverseMapping))
			unWantMappings.add(existInverseMapping);		
		removeMappings.add(extendIndex);
		//���¶�Ӧ��ͼ
		removeEdge(sourceExistInverseProperty, targetExistInverseProperty);	
		removeEdge(targetExistInverseProperty, sourceExistInverseProperty);	
		
	    sourceUri = "exist_"+sourceProperty;		
		int sourceExistProperty=aml.getURIMap().getIndex(sourceUri);	
		targetUri ="exist_"+targetProperty;	
		int targetExistProperty=aml.getURIMap().getIndex(targetUri);		
		int extendExistIndex=a.getIndexBidirectional(sourceExistProperty, targetExistProperty);
		Mapping Existmapping=maps.get(extendExistIndex);
		
		//���¶�Ӧ��mapping
		Existmapping.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(Existmapping.getSourceId(), Existmapping.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,Existmapping);		
		RefreshSet(unWantMappings,Existmapping);
		if(!unWantMappings.contains(Existmapping))
			unWantMappings.add(Existmapping);	
		removeMappings.add(extendExistIndex);	
		//���¶�Ӧ��ͼ
		removeEdge(sourceExistProperty, targetExistProperty);	
		removeEdge(targetExistProperty, sourceExistProperty);	
		
		
		sourceUri = "inverse_"+sourceProperty;		
		int sourceInverseProperty=aml.getURIMap().getIndex(sourceUri);	
		targetUri ="inverse_"+targetProperty;	
		int targetInverseProperty=aml.getURIMap().getIndex(targetUri);		
		int extendInverseIndex=a.getIndexBidirectional(sourceInverseProperty, targetInverseProperty);
		Mapping Inversemapping=maps.get(extendInverseIndex);
		
		//���¶�Ӧ��mapping
		Inversemapping.setStatus(MappingStatus.INCORRECT);
		aml.getAlignment().get(Inversemapping.getSourceId(), Inversemapping.getTargetId()).setStatus(MappingStatus.INCORRECT);	
		RefreshSet(wantMappings,Inversemapping);		
		RefreshSet(unWantMappings,Inversemapping);
		if(!unWantMappings.contains(Inversemapping))
			unWantMappings.add(Inversemapping);	
		removeMappings.add(extendInverseIndex);
		//���¶�Ӧ��ͼ
		removeEdge(sourceInverseProperty, targetInverseProperty);	
		removeEdge(targetInverseProperty, sourceInverseProperty);	
		
		return removeMappings;
	}
	
	public int[] restoreMapping(Mapping m)
	{
		int pair[]=new int[2];
		String sourceString=m.getSourceURI();
		String targetString=m.getTargetURI();
		sourceString=sourceString.replace("exist_", "").replace("inverse_", "");
		targetString=targetString.replace("exist_", "").replace("inverse_", "");
		
		int sourceId=aml.getURIMap().getIndex(sourceString);
		int targetId=aml.getURIMap().getIndex(targetString);
		
		pair[0]=sourceId;
		pair[1]=targetId;
		
		return pair;	
	}
	
	public Integer restoreRoleMapping(Integer index)
	{
		Mapping m=maps.get(index);
		String sourceString=m.getSourceURI();
		String targetString=m.getTargetURI();
		sourceString=sourceString.replace("exist_", "").replace("inverse_", "");
		targetString=targetString.replace("exist_", "").replace("inverse_", "");
		
		int sourceId=aml.getURIMap().getIndex(sourceString);
		int targetId=aml.getURIMap().getIndex(targetString);
		
		int originalIndex=a.getIndexBidirectional(sourceId, targetId);		
		return originalIndex;	
	}

	public void addEdge(int source,int target)
	{
		if(originalMap.keySet().contains(source))
			originalMap.get(source).add(target);
		else
		{
			Set<Integer> set=new HashSet<Integer>();
			set.add(target);
			originalMap.put(source,set);
		}
	}
	
	public void removeEdge(int source,int target)
	{
		if(originalMap.keySet().contains(source))
		{
			originalMap.get(source).remove(target);
			if(originalMap.get(source).isEmpty())
				originalMap.remove(source);
		}
		else
		{
			System.out.println("The graph does not contain this edge!");
		}
	}
	
	
	public void printPath(ArrayList<Integer> Path)
	{
		StringBuilder sb = new StringBuilder();
		for (Integer i : Path) {
			sb.append(i + "->");
		}
		sb.append("#");
		System.out.println(sb.toString().replace("->#", ""));		
	}
	
	public void printResult(List<ArrayList<Integer>> paths)
	{
		for(ArrayList<Integer> path:paths)
		{
			StringBuilder sb = new StringBuilder();
			for (Integer i : path) {
				sb.append(i + "->");
			}
			sb.append("#");
			System.out.println(sb.toString().replace("->#", ""));
		}
	}
}