package Tools;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;





//import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
//import org.semanticweb.elk.*;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owl.owlapi.OWLAnonymousIndividualImpl;
import Tools.TreeMap_Tools;

public class OWLAPI_tools {	
		 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		  
		 public OWLOntology onto;
		 String OntoID;
		 OWLReasoner hermit;
		 public OWLDataFactory fac = manager.getOWLDataFactory();
		 //String IndividualID="";
		 //String IndividualID="http://xmlns.com/foaf/0.1";	 
		 public void readOnto(String path) 
		 {
			 try {
				 File file = new File(path);	
				 manager.setSilentMissingImportsHandling(true);
			     onto = manager.loadOntologyFromOntologyDocument(file);    
			     hermit= new Reasoner.ReasonerFactory().createReasoner(onto);//����hermit���������Ҫ����org.semanticweb.HermiT.*;
			     // hermit=new Reasoner(onto);
			     OWLOntologyID ontologyIRI = onto.getOntologyID();
			     OntoID = ontologyIRI.getOntologyIRI().toString();
			     System.out.println("Load ontology sucessfully!");
			     IRI documentIRI = manager.getOntologyDocumentIRI(onto);
			     System.out.println("The path comes from " + documentIRI);// ��ȡ�ļ������·��
			     System.out.println("The OntoID is " + OntoID);
				} catch (OWLOntologyCreationException e) 
				{
					// TODO Auto-generated catch block
					System.out.println("cuowu");
					e.printStackTrace();
				} 		
		 }
		 
		 public void readOnto(URL url) throws URISyntaxException 
		 {
			 try
			 {
				 manager.setSilentMissingImportsHandling(true);
				 //OntoID="file:Datasets/Instance_ontologys/sourceIn.ttl";
				 onto = manager.loadOntology(IRI.create(url));
				 hermit= new Reasoner.ReasonerFactory().createReasoner(onto);
				 OWLOntologyID ontologyIRI = onto.getOntologyID();
				 OntoID = ontologyIRI.getOntologyIRI().toString();
				 System.out.println("Load ontology sucessfully!");
			 }
			 catch (OWLOntologyCreationException e) 
			{
					// TODO Auto-generated catch block
					System.out.println("cuowu");
					e.printStackTrace();
			 } 		
		 }
		 
		 public String getURI()
		 {
			 return OntoID.replace("#", "");
		 }
    
	    public void isConsistent()  //Ƕ����������ж��Ƿ�һ��
	    {
	    	 //hermit.isConsistent();
	    	 System.out.println("The ontology is "+hermit.isConsistent());
	    	/* OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		     ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		     OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		     OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);
		     reasoner.precomputeInferences();
			 boolean consistent = reasoner.isConsistent();
			 System.out.println("Consistent: " + consistent);*/
			 System.out.println("\n");
	    }
	    
	    public ArrayList<String> getOntologyContent()
	    {
	    	ArrayList<String> contents=new ArrayList<String>();
	    	String url=getURI();
	    	int Cnumber=onto.getClassesInSignature().size();
	    	int OPnumber=onto.getObjectPropertiesInSignature().size();
	    	int DPnumber=onto.getObjectPropertiesInSignature().size();
	    	int Inumber=onto.getIndividualsInSignature().size();
	    	int AxiomNumber=onto.getAxiomCount();
	    	int Number=onto.getAxiomCount(AxiomType.SUBCLASS_OF);
	    	
	    	contents.add("�����URLΪ: "+url);
	    	contents.add("�������Ϊ:  "+Cnumber);
	    	contents.add("�������Ը���Ϊ:  "+OPnumber);
	    	contents.add("��ֵ���Ը���Ϊ:  "+DPnumber);
	    	contents.add("ʵ���ĸ���Ϊ:  "+Inumber);
	    	contents.add("���ڵĹ�����:  "+AxiomNumber);
	    	contents.add("���ڵ�Is-a��ϵ��:  "+Number);
	    	//int getAxiomCount(AxiomType<T> axiomType)	    	
	    	return contents;
	    }
	    
	    public ArrayList<String> getConcepts()
		{
	    	ArrayList<String> classes=new ArrayList<String>();
	        for (OWLClass c : onto.getClassesInSignature()) 
	        {
	        	//System.out.println(c.getIRI().getFragment());
	        	String concept=c.getIRI().getFragment();
	        	if(concept!=null)
	        	{
	        		if(concept.charAt(0)=='_')
	        			concept=concept.replaceFirst("_", "");
	        		if(concept.equals("Thing")||concept.equals("Nothing"))//Thing������Ͳ�������
	        			continue;
	        		else if(concept.equals("DbXref")||concept.equals("Subset")||concept.equals("Synonym")||concept.equals("ObsoleteClass")||concept.equals("SynonymType")||concept.equals("Definition"))
	        			continue;
	        		else if(concept.equals("decimal")||concept.equals("nonNegativeInteger"))
	        			continue;
	        		else if(!classes.contains(concept))
	        			classes.add(concept);
	        	}
	        }
	        return classes;
		}
	    
	    public ArrayList<String> getLeaves()
		{
			//��������Ҷ��������������ʽ���з���
			ArrayList<String> leaf=new ArrayList<String>();
			for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    		 NodeSet<OWLClass> Children = hermit.getSubClasses(c, false);//false��ʹ��������������ص�������ڵ�
				 Set<OWLClass> children = Children.getFlattened();
				String sub_information=concept+"--";
				 for (OWLClass sub : children) 
				 {
					 String child=sub.getIRI().getFragment();
					 sub_information=sub_information+","+child;								
				 }		
				 if(sub_information.replace(concept, "").equals("--,Nothing"))
					 leaf.add(concept.toLowerCase());
				 else if(sub_information.replace(concept, "").equals("--"))
				 {
					 leaf.add(concept.toLowerCase());
				 }
				 /*else  ��Ҷ�ӽڵ㲻����
				 {
					 sub_information=sub_information.replace(",Nothing","");
					 sub_information=sub_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 leaf.add(sub_information);
				 }*/
	        }   
			
			return leaf;
		}
	    
	    public ArrayList<String> getIsolateNode()
		{
			//��������Ҷ��������������ʽ���з���
	    	ArrayList<String> isolateNodes=new ArrayList<String>();
			ArrayList<String> leafs=new ArrayList<String>();
			leafs=getLeaves();
			for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 System.out.println(concept);
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    	    NodeSet<OWLClass> Father = hermit.getSuperClasses(c, false);//false��ʹ��������������ص�������ڵ�
				Set<OWLClass> father = Father.getFlattened();
				String super_information=concept+"--";
				 for (OWLClass sup : father) 
				 {
					 String child=sup.getIRI().getFragment();
					 super_information=super_information+","+child;								
				 }		
				 if(super_information.replace(concept, "").equals("--,Thing")) //���ײ�ΪT
					 isolateNodes.add(concept.toLowerCase());
				 else if(super_information.replace(concept, "").equals("--"))//���ײ�Ϊ��
				 {
					 isolateNodes.add(concept.toLowerCase());
				 }
	        }   		
			return isolateNodes;
		}

	    
	    public ArrayList<String> getConceptAnnoations()
		{
	    	ArrayList<String> Annoations=new ArrayList<String>();
	    	//OWLDataFactory fac = manager.getOWLDataFactory();
	        for (OWLClass c : onto.getClassesInSignature()) 
	        {
	        	String a=c.getIRI().getFragment();
	        	if(a!=null)
	        	{
	        	if(a.charAt(0)=='_')
					a=a.replaceFirst("_", "");
	        	if(a.equals("Nothing")||a.equals("Thing"))//���汾��
					continue;
	        	else if(a.equals("DbXref")||a.equals("Subset")||a.equals("Synonym")||a.equals("ObsoleteClass")||a.equals("SynonymType")||a.equals("Definition"))
					continue;
	        	String label=null;
	        	System.out.println("+++++++++++");
	        	
	        	for(OWLAnnotation anno : c.getAnnotations(onto, fac.getRDFSLabel()))
	        	{
	        		
	        		if (anno.getValue() instanceof OWLLiteral)
	        		{
	        			OWLLiteral val=(OWLLiteral)anno.getValue();
	        			//if(val.hasLang("pt"))
	        			
	        			label=val.getLiteral();
	        			System.out.println(label);
	        			//System.out.println(c+" labelled "+val.getLiteral());
	        		}
	        	}
	        	System.out.println("+++++++++++");
	        	String comment=null;
	        	for(OWLAnnotation comm : c.getAnnotations(onto, fac.getRDFSComment()))
	        	{
	        		if (comm.getValue() instanceof OWLLiteral)
	        		{
	        			OWLLiteral val=(OWLLiteral)comm.getValue();
	        			//if(val.hasLang("pt"))
	        			comment=val.getLiteral();
	        			//System.out.println(c+" comment is  "+val.getLiteral());
	        		}
	        	}    
	        	
	        	if(label!=null&&!label.equals(a))//�����Լ������Լ���label
	        		Annoations.add(a+"--"+label);	
				else if(label!=null&&comment!=null&&!comment.equals(""))  
				{
					Annoations.add(a+"--"+comment.trim());	
				}
	        	}
	        }
	        return Annoations;
		}
	    
	    public ArrayList<String> getSyn()
	  	{	
	    	ArrayList<String> Synonyms=new ArrayList<String>();
                       
	  	   for (OWLClass concept_name : onto.getClassesInSignature()) 
	  	   {
	  		  String a=concept_name.getIRI().getFragment();
	        	if(a!=null)
	        	{
	        	if(a.charAt(0)=='_')
					a=a.replaceFirst("_", "");
	        	if(a.equals("Nothing")||a.equals("Thing"))//���汾��
					continue;
	        	else if(a.equals("DbXref")||a.equals("Subset")||a.equals("Synonym")||a.equals("ObsoleteClass")||a.equals("SynonymType")||a.equals("Definition"))
					continue;
	             
              String labels=null;  
              
              /*String concept_url= OntoID+"#"+"NCI_C12742";
			    //System.out.println( concept_url);	   
			    OWLClass c = fac.getOWLClass(IRI.create(concept_url));*/
	  	      for(OWLAnnotationAssertionAxiom annotations:concept_name.getAnnotationAssertionAxioms(onto)) //oboInOwl:hasRelatedSynonym�Ľ�����Ҳ����label�ĳ�ȡ��
	  	      {
	  	    	    ////System.out.println("**************");  	
	              //  System.out.println(annotations.getProperty().getIRI().getFragment()); // �����е�ν��
	              //  System.out.println(annotations.getValue()); // �����еĶ�Ӧ��ֵ        			
	               // onto.getAxioms();������⹫��洢����ʽ
	  	        	String label=null; 
	                if (annotations.getValue() instanceof OWLAnonymousIndividualImpl) //�ҵ�http://human.owl#genid4450 ��Ӧ�ľ���Value
	                {
						OWLAnonymousIndividualImpl am = (OWLAnonymousIndividualImpl) annotations.getValue();
						// am.getDataPropertyValues(onto);
						// OWLAnonymousIndividual an = am.asOWLAnonymousIndividual();
						 for (OWLAnnotationAssertionAxiom b :onto.getAnnotationAssertionAxioms(am))
						 {
							 //System.out.println(b.getValue());   //��ȡͬ��ʵ�ֵ
				       		 OWLLiteral syn=(OWLLiteral)b.getValue();	
				       		 label=syn.getLiteral();
				             //System.out.println(label);  //ͬ���
				       		 if(labels==null)
				       			labels=label;
				       		 else
				       			 labels=labels+","+label;
						 }
					}  
	                else
	                {
	                	OWLLiteral val=(OWLLiteral)annotations.getValue();	  //��ȡlabel��ֵ
	                	if(labels==null)
	                		labels=val.getLiteral();
	                	else
	                		labels=labels+","+val.getLiteral();
		                System.out.println(labels);
	                }  
	  	      }
              if(labels!=null&&!labels.equals(a))//�����Լ������Լ���label
              {
              	String pairs=a+"--"+labels;
	 	  	    	Synonyms.add(pairs.replace("--,", "--"));	
              }
	  	   
	  	    System.out.println("**************");   	  
	      }
	  	 }
	  	   
	  	 return Synonyms;
	  }
	    
	    
	    public void getConceptAnnoations(String concept)
	  	{	
			    String concept_url= OntoID+"#"+concept;
			    //System.out.println( concept_url);	   
			    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
	  	    	//OWLDataFactory fac = manager.getOWLDataFactory();
	  	        String label=null;        
	  	      for(OWLAnnotationAssertionAxiom annotations:concept_name.getAnnotationAssertionAxioms(onto)) //oboInOwl:hasRelatedSynonym�Ľ�����Ҳ����label�ĳ�ȡ��
	  	      {
	  	    	    System.out.println("**************");  	
	                //System.out.println(annotations.getProperty().getIRI().getFragment()); // �����е�ν��
	                //System.out.println(annotations.getValue()); // �����еĶ�Ӧ��ֵ        			
	               // onto.getAxioms();������⹫��洢����ʽ
	                if (annotations.getValue() instanceof OWLAnonymousIndividualImpl) //�ҵ�http://human.owl#genid4450 ��Ӧ�ľ���Value
	                {
						OWLAnonymousIndividualImpl am = (OWLAnonymousIndividualImpl) annotations.getValue();
						// am.getDataPropertyValues(onto);
						// OWLAnonymousIndividual an = am.asOWLAnonymousIndividual();
						 for (OWLAnnotationAssertionAxiom a :onto.getAnnotationAssertionAxioms(am))
						 {
							 System.out.println(a.getValue());   //��ȡͬ��ʵ�ֵ
				       		 OWLLiteral syn=(OWLLiteral)a.getValue();	
			        		 label=syn.getLiteral();
				             System.out.println(label);
						 }
					}
	                else
	                {
	                	OWLLiteral val=(OWLLiteral)annotations.getValue();	  //��ȡlabel��ֵ
	        			label=val.getLiteral();
		                System.out.println(label);
	                }              
	  	      }
	  	    System.out.println("**************");  
	  	}
	    
	    public ArrayList<String> getObjectProperties()
	  	{
	    	ArrayList<String> Properties=new ArrayList<String>();
	        for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
            {
	       // 	System.out.println(op.getIRI().getFragment());
	        	String property=op.getIRI().getFragment();
	        	if(property.equals("topObjectProperty")||property.equals("bottomObjectProperty"))//���汾��
					continue;
				//ҽѧ����
				//else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED")||property.equals("UNDEFINED_part_of"))
				else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED"))
					continue;
				else if(!Properties.contains(property))
					Properties.add(property);
            }
	        return Properties;
	  	}
	    
	    public ArrayList<String> getObjectPropertyAnnoations()
		{
	    	ArrayList<String> Annoations=new ArrayList<String>();
	    	//OWLDataFactory fac = manager.getOWLDataFactory();
	        for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
	        {
	        	String property=op.getIRI().getFragment();
	        	if(property.equals("topObjectProperty")||property.equals("bottomObjectProperty"))//���汾��
					continue;
				//ҽѧ����
				//else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED")||property.equals("UNDEFINED_part_of"))
				else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED"))
					continue;
	        	String label=null;
	        	for(OWLAnnotation anno : op.getAnnotations(onto, fac.getRDFSLabel()))
	        	{
	        		if (anno.getValue() instanceof OWLLiteral)
	        		{
	        			OWLLiteral val=(OWLLiteral)anno.getValue();
	        			//if(val.hasLang("pt"))
	        			label=val.getLiteral();
	        			//System.out.println(op+" labelled "+val.getLiteral());
	        		}
	        	}
	        	String comment=null;
	        	for(OWLAnnotation comm : op.getAnnotations(onto, fac.getRDFSComment()))
	        	{
	        		if (comm.getValue() instanceof OWLLiteral)
	        		{
	        			OWLLiteral val=(OWLLiteral)comm.getValue();
	        			//if(val.hasLang("pt"))
	        			comment=val.getLiteral();
	        			//System.out.println(op+" comment is  "+val.getLiteral());
	        		}
	        	}    
	        	
	        	if(label!=null&&!label.equals(property))//�����Լ������Լ���label
	        		Annoations.add(property+"--"+label);	
				else if(label!=null&&comment!=null&&!comment.equals(""))  
				{
					Annoations.add(property+"--"+comment.trim());	
				}
	        }
	        return Annoations;
		}
	    
		public ArrayList<String> getPropertyAndInverse()
		{
			ArrayList<String> propertiesAndInverse=new ArrayList<String>();
			 for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
		     {
				 String property=op.getIRI().getFragment();
					if(property!=null)
					{
						Set<OWLObjectPropertyExpression> inverse= op.getInverses(onto);
						for(OWLObjectPropertyExpression a:inverse)
						{
							//a.asOWLObjectProperty().getIRI().getFragment();
							//System.out.println(property+"--"+a.asOWLObjectProperty().getIRI().getFragment());		
							propertiesAndInverse.add(property+"--"+a.asOWLObjectProperty().getIRI().getFragment());
						}
					}
			}
			return propertiesAndInverse;
		}
	    
	    
	    public ArrayList<String> getDataProperties()
	  	{
	    	ArrayList<String> Properties=new ArrayList<String>();
	        for (OWLDataProperty dp : onto.getDataPropertiesInSignature()) 
            {
	       // 	System.out.println(op.getIRI().getFragment());
	        	String property=dp.getIRI().getFragment();
	        	if(property!=null)
	        	{
				if(property.equals("topDataProperty")||property.equals("bottomDataProperty"))
					continue;
				else if(!Properties.contains(property))
					Properties.add(property);
	        	}

            }
	        return Properties;
	  	}
	    
	    public ArrayList<String> getDataPropertyAnnoations()
		{
	    	ArrayList<String> Annoations=new ArrayList<String>();
	    	//OWLDataFactory fac = manager.getOWLDataFactory();
	        for (OWLDataProperty op : onto.getDataPropertiesInSignature()) 
	        {
	        	String property=op.getIRI().getFragment();
	        	if(property!=null)
	        	{
	        		if(property.equals("topDataProperty")||property.equals("bottomDataProperty"))//���汾��
	        			continue;
	        		//ҽѧ����
	        		//else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED")||property.equals("UNDEFINED_part_of"))
	        		else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED"))
	        			continue;
	        		String label=null;
	        		for(OWLAnnotation anno : op.getAnnotations(onto, fac.getRDFSLabel()))
	        		{
	        			if (anno.getValue() instanceof OWLLiteral)
	        			{
	        				OWLLiteral val=(OWLLiteral)anno.getValue();
	        				//if(val.hasLang("pt"))
	        				label=val.getLiteral();
	        				//System.out.println(op+" labelled "+val.getLiteral());
	        			}
	        		}
	        		String comment=null;
	        		for(OWLAnnotation comm : op.getAnnotations(onto, fac.getRDFSComment()))
	        		{
	        			if (comm.getValue() instanceof OWLLiteral)
	        			{
	        				OWLLiteral val=(OWLLiteral)comm.getValue();
	        				//if(val.hasLang("pt"))
	        				comment=val.getLiteral();
	        				//System.out.println(op+" comment is  "+val.getLiteral());
	        			}
	        		}    

	        		if(label!=null&&!label.equals(property))//�����Լ������Լ���label
	        			Annoations.add(property+"--"+label);	
	        		else if(label!=null&&comment!=null&&!comment.equals(""))  
	        		{
	        			Annoations.add(property+"--"+comment.trim());	
	        		}
	        	}
	        }
	        return Annoations;
		}
	    
	    public ArrayList<String> getConceptSubClasses(String concept)
	    {	    	
	    		ArrayList<String> conceptSubClasses=new ArrayList<String>();  		
	    		/*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	    		//OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
			    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
			    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
			    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);   */ 
			    		    
			    //OWLDataFactory fac = manager.getOWLDataFactory();
			    String concept_url= OntoID+"#"+concept;
			    //System.out.println( concept_url);	   
			    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
			    NodeSet<OWLClass> Children = hermit.getSubClasses(concept_name, false);
			    //NodeSet<OWLClass> Children = reasoner.getSubClasses(concept_name, false);//false��ʹ������������
			    Set<OWLClass> children = Children.getFlattened();
			    for (OWLClass c : children) 
		        {
			    	String child=c.getIRI().getFragment();
		        	if(!child.equals("Nothing"))//Thing������Ͳ���
		        		conceptSubClasses.add(child);
		        		//System.out.println(child);
		        }			 			    
			    return conceptSubClasses;    	
	    }
	    
	    public ArrayList<String> getConceptDirectSubClasses(String concept)
	    {
	    		ArrayList<String> conceptDirectSubClasses=new ArrayList<String>();
	    		/*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
			    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
			    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);    */
			    		    
			   // OWLDataFactory fac = manager.getOWLDataFactory();
			    String concept_url= OntoID+"#"+concept;
			    //System.out.println( concept_url);	   
			    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
			    NodeSet<OWLClass> Children = hermit.getSubClasses(concept_name, true);//false��ʹ������������
			    Set<OWLClass> children = Children.getFlattened();
			    for (OWLClass c : children) 
		        {
			    	String child=c.getIRI().getFragment();
		        	if(!child.equals("Nothing"))//Thing������Ͳ���
		        		conceptDirectSubClasses.add(child);
		        		//System.out.println(child);
		        }			 			    
			    return conceptDirectSubClasses;    	
	    }
	   

	    public ArrayList<String> getSubClasses()
		{
	    	ArrayList<String> Subclasses=new ArrayList<String>();
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
					//a=a.replace("-", "_");//Ϊ�˻�ͼ���㣬��'-'�滻��'_'
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    		 NodeSet<OWLClass> Children = hermit.getSubClasses(c, false);//false��ʹ��������������ص�������ڵ�
	    		// System.out.println("hello!");
				 Set<OWLClass> children = Children.getFlattened();
				 String sub_information=concept+"--";
				 for (OWLClass sub : children) 
				 {
					 String child=sub.getIRI().getFragment();
					 sub_information=sub_information+","+child;								
				 }		
				 if(sub_information.replace(concept, "").equals("--,Nothing"))
					 continue;
				 else if(sub_information.replace(concept, "").equals("--"))
				 {
					 continue;
				 }
				 else
				 {
					 sub_information=sub_information.replace(",Nothing","");
					 sub_information=sub_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 Subclasses.add(sub_information);
				 }
	        }   
	    	return Subclasses;    	
		}
	    
	    public ArrayList<String> getDirectSubClasses()
		{
	    	ArrayList<String> Subclasses=new ArrayList<String>();
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
					//a=a.replace("-", "_");//Ϊ�˻�ͼ���㣬��'-'�滻��'_'
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    		 NodeSet<OWLClass> Children = hermit.getSubClasses(c, true);//true��ʾû��ʹ��������������ص��Ƕ���
				 Set<OWLClass> children = Children.getFlattened();
				 String sub_information=concept+"--";
				 for (OWLClass sub : children) 
				 {
					 String child=sub.getIRI().getFragment();
					 sub_information=sub_information+","+child;			
				 }	
				 if(sub_information.replace(concept, "").equals("--,Nothing"))
					 continue;
				 else if(sub_information.replace(concept, "").equals("--"))
				 {
					 continue;
				 }
				 else
				 {
					 sub_information=sub_information.replace(",Nothing","");
					 sub_information=sub_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 Subclasses.add(sub_information);
				 }
	        }   
	    	return Subclasses;    	
		}
	    
	    public ArrayList<String> getSuperClasses()
		{
	    	ArrayList<String> SuperClasses=new ArrayList<String>();
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
					//a=a.replace("-", "_");//Ϊ�˻�ͼ���㣬��'-'�滻��'_'
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    		 NodeSet<OWLClass> Father = hermit.getSuperClasses(c, false);//false��ʾʹ���������
				 Set<OWLClass> fathers = Father.getFlattened();
				 String super_information=concept+"--";
				 for (OWLClass sup : fathers) 
				 {
					 String father=sup.getIRI().getFragment();
					 super_information=super_information+","+father;			
				 }	
				 if(super_information.replace(concept, "").equals("--,Thing"))
					 continue;
				 else if(super_information.replace(concept, "").equals("--"))
				 {
					 continue;
				 }
				 else
				 {
					 super_information=super_information.replace(",Thing","");
					 super_information=super_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 SuperClasses.add(super_information);
				 }
	        }   
	    	return SuperClasses;    	
		}
	    
	    public ArrayList<String> getDirectSuperClasses()
		{
	    	ArrayList<String> SuperClasses=new ArrayList<String>();
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
					//a=a.replace("-", "_");//Ϊ�˻�ͼ���㣬��'-'�滻��'_'
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
	    		 NodeSet<OWLClass> Father = hermit.getSuperClasses(c, true);//false��ʾû��ʹ���������
				 Set<OWLClass> fathers = Father.getFlattened();
				 String super_information=concept+"--";
				 for (OWLClass sup : fathers) 
				 {
					 String father=sup.getIRI().getFragment();
					 super_information=super_information+","+father;			
				 }	
				 if(super_information.replace(concept, "").equals("--,Thing"))
					 continue;
				 else if(super_information.replace(concept, "").equals("--"))
				 {
					 continue;
				 }
				 else
				 {
					 super_information=super_information.replace(",Thing","");
					 super_information=super_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 SuperClasses.add(super_information);
				 }
	        }   
	    	return SuperClasses;    	
		}
	    
		public ArrayList<String> getSibling(ArrayList<String> Subclasses_Direct)
		{
			ArrayList<String> Sibling=new ArrayList<String>();
			//System.out.println("***********************");
			for(int i=0;i<Subclasses_Direct.size();i++)
			{
				String father_children[]=Subclasses_Direct.get(i).split("--");
				if(father_children[0].equals("Thing"))
					continue;
				else
				{
					//System.out.println(father_children[0]);
					String children[]=father_children[1].split(",");
					if(children.length>1)
					{
						String sibling_information="";
						for(int j=0;j<children.length;j++)
						{
							//������ӵ�λ�ÿ��������һλ��Ҳ�����ǵ�һλ
							String others=father_children[1].replace(children[j]+",", "").replace(","+children[j], "");
							sibling_information=children[j].toString()+"--"+others;
						/*	if(!Sibling.contains(sibling_information))
								Sibling.add(sibling_information);					
							for(int k=0;k<children.length;k++)
							{
								if(k!=j)
								{
									sibling_information=sibling_information+","+children[k].toString();
								}
							}*/
						/*	if(!Sibling.contains(sibling_information))
								Sibling.add(sibling_information);*/
						}		
					}
				}		
			}	
			return Sibling;
		}
		
		public ArrayList<String> getDisjointwith()
		{
			ArrayList<String> Disjointion=new ArrayList<String>();
			//ArrayList<String> SuperClasses=new ArrayList<String>();
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	    		 String concept=c.getIRI().getFragment();
	    		 if(concept==null)
	    			 continue;
	    		 if(concept.charAt(0)=='_')
	    			 concept=concept.replaceFirst("_", "");
					//a=a.replace("-", "_");//Ϊ�˻�ͼ���㣬��'-'�滻��'_'
				if(concept.equals("Thing")||concept.equals("Nothing"))
						continue;
				 Set<OWLClassExpression> disjoints=c.getDisjointClasses(onto);
				 String super_information=concept+"--";
				 ArrayList<String> disjointions=new  ArrayList<String> ();
				 for (OWLClassExpression dis : disjoints) 
				 {
					 String disjoint=dis.asOWLClass().getIRI().getFragment();
					 super_information=super_information+","+disjoint;		
					 for(String sub:getConceptSubClasses(disjoint))
						 disjointions.add(sub);
					 //disjointions.add(disjoint);						 
					 //getConceptSubClasses
				 }				 
				 for(String a:disjointions)
				 {
					 super_information=super_information+","+a;		
				 }
 
	    		/* NodeSet<OWLClass> DisjointList = hermit.getDisjointClasses(c);//false��ʾû��ʹ���������
	    		 System.out.println(DisjointList.isEmpty());
				 Set<OWLClass> disjoints = DisjointList.getFlattened();
				String super_information=concept+"--";
				 for (OWLClass dis : disjoints) 
				 {
					 String disjoint=dis.getIRI().getFragment();
					 super_information=super_information+","+disjoint;			
				 }	*/
				 if(super_information.replace(concept, "").equals("--,Nothing"))
					 continue;
				 else if(super_information.replace(concept, "").equals("--"))
				 {
					 continue;
				 }
				 else
				 {
					 super_information=super_information.replace(",Nothing","");
					 super_information=super_information.replace("--,","--");	//���ǿ��ܳ���Nothing�����
					 Disjointion.add(super_information);
					 ArrayList<String> children=getConceptSubClasses(concept);
					 for(String child:children)
					 {
						 String inferred_information=super_information.replace(concept, child);
						 Disjointion.add(inferred_information);
					 }
				 }
				
				 
	        }   
			return Disjointion;
		}
		
		public void isConceptInstance(String concept,String instance)//�жϱ�֤ԭʼ��"-"����
	    {
	        /*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);  */  
		    
		    //OWLDataFactory fac = manager.getOWLDataFactory();
		    String concept_url= OntoID+"#"+concept;
		    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
		    NodeSet<OWLNamedIndividual> individualsNodeSet = hermit.getInstances(concept_name, false);//false��ʹ������������
		    Iterator<Node<OWLNamedIndividual>> a=individualsNodeSet.iterator();
		  /*  int num=0;
		    while(a.hasNext())
		    {
		    	num++;
		    }
		    System.out.println(concept+"ʵ���ĸ���Ϊ:"+num);*/
		    Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
		    System.out.println(concept+"ʵ���ĸ���Ϊ:"+individuals.size());
		    boolean flag=false;
		    for(OWLNamedIndividual i:individuals)
        	{       
		    	if(i.getIRI().getFragment().equals(instance)||i.getIRI().toString().replace(OntoID+"#", "").equals(instance))
		    	{
		    	  flag =true;
		    	  break;
		    	}     		
        	}	  
		    System.out.println(flag);	 	        
	    }
	    
	    public ArrayList<String> getConceptInstances()	    
	    {
	    	ArrayList<String> Concept_Instances=new ArrayList<String>();
	        /*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);*/
		    String information="";
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {  		
	        	//System.out.println(c.getIRI().getFragment());//����ֱ�ӽ�����URL�ĸ������������ 
	        	NodeSet<OWLNamedIndividual> individualsNodeSet_c = hermit.getInstances(c,false );//false��ʹ������������
	        	Set<OWLNamedIndividual> individuals_c = individualsNodeSet_c.getFlattened(); 
	        	//System.out.println(c.getIRI().getFragment()+":"+individuals_c.size());
	        	String concept=c.getIRI().getFragment();
	        	if(concept==null)
	        		continue;
	        	concept=c.getIRI().getFragment().replace("-", "_");//�����»�������
	        	
	        	if(!concept.equals("Thing")&&individuals_c.size()>0)
	        	{
	        	 information=concept+"--";
	        	// System.out.println("****************************************");	      
	        	// System.out.println("the number of instances of "+c.getIRI().getFragment()+ " is "+individuals_c.size());
	        	 for(OWLNamedIndividual i:individuals_c)
		         {   
		           String individual="";
		          // if(i.getIRI().getNamespace().contains(OntoID))
		           if(i.getIRI().getFragment().contains(OntoID))
		           {
		        	   individual=i.getIRI().getFragment();
		        	// individual=i.getIRI().toString().replace(OntoID+"#", "").replace("-", "_");
		           }
		           else
		           {
		        	  individual=i.getIRI().getFragment().replace("-", "_");
		           }
		           //System.out.println(individual + "\t is an instance of\t" + concept);
		           information=information+","+individual;		        		
		         }
		        //	System.out.println(information);
		        	Concept_Instances.add(information.replace("--,", "--"));
		        	information="";
	        	 }
	        	 
	        }   
	    	return Concept_Instances;    	
	    }
	    
	    public ArrayList<String> getConceptInstances2()	    
	    {
	    	ArrayList<String> Concept_Instances=new ArrayList<String>();
	        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);
		    String information="";
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {  		
	        	//System.out.println(c.getIRI().getFragment());//����ֱ�ӽ�����URL�ĸ������������ 
	        	NodeSet<OWLNamedIndividual> individualsNodeSet_c = reasoner.getInstances(c,true);//false��ʹ������������
	        	Set<OWLNamedIndividual> individuals_c = individualsNodeSet_c.getFlattened(); 
	        	System.out.println(c.getIRI().getFragment()+": "+individuals_c.size());
	        	String concept=c.getIRI().getFragment();
	        	if(concept==null)
	        		continue;
	        	concept=c.getIRI().getFragment().replace("-", "_");//�����»�������
	        	
	        	if(!concept.equals("Thing")&&individuals_c.size()>0)
	        	{
	        	 information=concept+"--";
	        	// System.out.println("****************************************");	      
	        	// System.out.println("the number of instances of "+c.getIRI().getFragment()+ " is "+individuals_c.size());
	        	 for(OWLNamedIndividual i:individuals_c)
		         {   
		           String individual="";
		          // if(i.getIRI().getNamespace().contains(OntoID))
		           if(i.getIRI().getFragment().contains(OntoID))
		           {
		        	   individual=i.getIRI().getFragment();
		        	// individual=i.getIRI().toString().replace(OntoID+"#", "").replace("-", "_");
		           }
		           else
		           {
		        	  individual=i.getIRI().getFragment().replace("-", "_");
		           }
		           //System.out.println(individual + "\t is an instance of\t" + concept);
		           information=information+","+individual;		        		
		         }
		        //	System.out.println(information);
		        	Concept_Instances.add(information.replace("--,", "--"));
		        	information="";
	        	 }
	        	 
	        }   
	    	return Concept_Instances;    	
	    }
	    
	    public ArrayList<String> getConceptInstances(String concept)
	    {
	    	ArrayList<String> Instances=new ArrayList<String>();        		    			
	        /*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);    */
		    
		    //OWLDataFactory fac = manager.getOWLDataFactory();
		    String concept_url= OntoID+"#"+concept;
		    System.out.println(concept_url);	   
		    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
		    NodeSet<OWLNamedIndividual> individualsNodeSet = hermit.getInstances(concept_name, false);//false��ʹ������������		
		    
		    Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
		    System.out.println(concept+"ʵ���ĸ���Ϊ:"+individuals.size());
		    
		    String information=concept.replace("-", "_")+"--";
		   // String information=concept+"--";
		    for(OWLNamedIndividual i:individuals)
        	{       
		    	
		    	//String instance=i.getIRI().getFragment().replace("-", "_");//�����»�������;
		    	//IRI instance=i.getIRI();//�����»�������;
		    	//String IndividualID=find_Instances_url();
		    	String instance="";
		    	//if(i.getIRI().getNamespace().contains(OntoID))
		    	if(i.getIRI().getFragment().contains(OntoID))
		    	{
		    		instance=i.getIRI().toString().replace(OntoID+"#", "");
		    		System.out.println(instance+ "\tinstance of\t" + concept.replace("-", "_"));	//�����»�������;   
		    	}
		    	else
		    	{
		    		instance=i.getIRI().getFragment();
		    		System.out.println(instance+ "\tinstance of\t" + concept.replace("-", "_"));	//�����»�������;   	    		
		    	}
		    	information=information+","+instance.replace("-", "_");
	    		
        	}
		    Instances.add(information.replace("--,", "--"));
		    return  Instances;		        
	    }
	    
	    public ArrayList<String> getConceptInstances2(String concept)
	    {
	    	ArrayList<String> Instances=new ArrayList<String>();        		    			
	      /*  OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config); */   
		    
		    //OWLDataFactory fac = manager.getOWLDataFactory();
		    String concept_url= OntoID+"#"+concept;
		    System.out.println(concept_url);	   
		    OWLClass concept_name = fac.getOWLClass(IRI.create(concept_url));
		    NodeSet<OWLNamedIndividual> individualsNodeSet = hermit.getInstances(concept_name, true);//false��ʹ������������		
		    
		    Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
		    System.out.println(concept+"ʵ���ĸ���Ϊ:"+individuals.size());
		    
		    String information=concept.replace("-", "_")+"--";
		   // String information=concept+"--";
		    for(OWLNamedIndividual i:individuals)
        	{       
		    	
		    	//String instance=i.getIRI().getFragment().replace("-", "_");//�����»�������;
		    	//IRI instance=i.getIRI();//�����»�������;
		    	//String IndividualID=find_Instances_url();
		    	String instance="";
		    	//if(i.getIRI().getNamespace().contains(OntoID))
		    	if(i.getIRI().getFragment().contains(OntoID))
		    	{
		    		instance=i.getIRI().toString().replace(OntoID+"#", "");
		    		System.out.println(instance+ "\tinstance of\t" + concept.replace("-", "_"));	//�����»�������;   
		    	}
		    	else
		    	{
		    		instance=i.getIRI().getFragment();
		    		System.out.println(instance+ "\tinstance of\t" + concept.replace("-", "_"));	//�����»�������;   	    		
		    	}
		    	information=information+","+instance.replace("-", "_");
	    		
        	}
		    Instances.add(information.replace("--,", "--"));
		    return  Instances;		        
	    }
		
	    public ArrayList<String> getRelationInstances()
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 
	    	 /*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			 ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
			 OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
			 OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);  */
			 
	    	 for (OWLClass c : onto.getClassesInSignature()) 
		     {
		        NodeSet<OWLNamedIndividual> instances = hermit.getInstances(c, false);
		        for (OWLNamedIndividual i : instances.getFlattened())
		        {
		        	String instance=i.getIRI().getFragment();
		            for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
		            {
		              String objectpropety=op.getIRI().getFragment();
		              NodeSet<OWLNamedIndividual> petValuesNodeSet = hermit.getObjectPropertyValues(i, op);
		              for (OWLNamedIndividual value : petValuesNodeSet.getFlattened()) 
		              {
		            	 String instance_value= value.getIRI().getFragment();
		                 System.out.println(instance + "\t" + objectpropety + "\t"+ instance_value);
		                 if(instance!=null&&instance_value!=null) //�����й�ϵΪ�յ����
		                 Relations.add(instance.replace("-", "_") + "," + objectpropety.replace("-", "_")+ ","+ instance_value.replace("-", "_"));
		              }
		            }
		        }
		     }
	    	 return Relations;
	    }
	    
	    /*public ArrayList<String> getDataProperyInstances()  //��û��ʵ��
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 
	    	 OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			 ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
			 OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
			 OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);  
			 
	    	 for (OWLClass c : onto.getClassesInSignature()) 
		     {
		        NodeSet<OWLNamedIndividual> instances = hermit.getInstances(c, false);
		        for (OWLNamedIndividual i : instances.getFlattened())
		        {
		        	String instance=i.getIRI().getFragment();
		            for (OWLDataProperty dp : onto.getDataPropertiesInSignature()) 
		            {
		              String objectpropety=dp.getIRI().getFragment();
		              Set<OWLLiteral> petValuesNodeSet = hermit.getDataPropertyValues(i, dp);
		              for (OWLLiteral value : petValuesNodeSet) 
		              {
		            	 String instance_value= value.getLiteral();
		                 System.out.println(instance + "\t" + objectpropety + "\t"+ instance_value);
		                 if(instance!=null&&instance_value!=null) //�����й�ϵΪ�յ����
		                 Relations.add(instance.replace("-", "_") + "," + objectpropety.replace("-", "_")+ ","+ instance_value.replace("-", "_"));
		              }
		            }
		        }
		     }
	    	 return Relations;
	    }*/
	    
	    public  ArrayList<String> getRelationInstances(String individual,String property)
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 /*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			 ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
			 OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
			 OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);  */
			 
			// OWLDataFactory fac = manager.getOWLDataFactory();
			 
			 String individual_url=findInstancesUrl()+individual;//url��һ���Ǳ���ģ�������"http://xmlns.com/foaf/0.1#"
			 String property_url=OntoID+"#"+property;
			 
	    	 OWLNamedIndividual Individual = fac.getOWLNamedIndividual(IRI.create(individual_url));
		     OWLObjectProperty Property = fac.getOWLObjectProperty(IRI.create(property_url));
		       
		     NodeSet<OWLNamedIndividual> petValuesNodeSet = hermit.getObjectPropertyValues(Individual, Property);
		     Set<OWLNamedIndividual> values = petValuesNodeSet.getFlattened();
		     for (OWLNamedIndividual ind : values) 
		     {
		    	String value=ind.getIRI().getFragment();
		       // System.out.println(individual+","+property+","+value);
		        Relations.add(individual+","+property+","+value);
		     }	
		     return Relations;		     
	    }
	    
	    public String findInstancesUrl()
	    {
	    	String IndividualID=OntoID;
	    	/*OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		    ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		    OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		    OWLReasoner reasoner = reasonerFactory.createReasoner(onto, config);*/
		    
	    	for (OWLClass c : onto.getClassesInSignature()) 
	        {
	        	NodeSet<OWLNamedIndividual> individualsNodeSet_c = hermit.getInstances(c,true);//false��ʹ������������
	        	Set<OWLNamedIndividual> individuals_c = individualsNodeSet_c.getFlattened();
	        	if(individuals_c.size()>0)
	        	{
	        		for(OWLNamedIndividual i:individuals_c)
	        	   {   
	        		  //String URL=i.getIRI().getNamespace();//�����»�������;
	        		  String URL=i.getIRI().getFragment();//�����»�������;
	        		  
	        		  //���￴��getScheme()��ʲô��
	        		  if(!URL.contains(OntoID))
	        		  {
	        			  IndividualID=URL;
	        			  //System.out.println(IndividualID);
	        		  	  return IndividualID;
	        		  }
	        	   }	   
	        	}        		        	
	        }
	    	return IndividualID;
	    }
	    
	    public  ArrayList<String> getObjectRelations()
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
	    	 { 		 
	    		 String property=op.getIRI().getFragment();	
	    		// System.out.println(property);
	    		 if(property.equals("topObjectProperty")||property.equals("bottomObjectProperty"))//���汾��
	    			 continue;
	    		 Set<OWLClassExpression> Domain= op.getDomains(onto);
	    		 Set<OWLClassExpression> Range= op.getRanges(onto);
	    		 String domain="";
	    		 String range="";
	    		 Set<OWLClassExpression> domains=null;
	    		 Set<OWLClassExpression> ranges = null;
				 for(OWLClassExpression d:Domain)
				 {	
					 if(d==null)
						 continue;
					 if(!d.isAnonymous())
						 domain=d.asOWLClass().getIRI().getFragment();
					 else
						  domains= d.asDisjunctSet();
				 }
				 for(OWLClassExpression r:Range)
				 {	
					 if(r==null)
						 continue;
					 if(!r.isAnonymous())
						 range=r.asOWLClass().getIRI().getFragment();
					 else
						  ranges= r.asDisjunctSet();
				 }
				 //Ŀǰ���ñ���̬��
				 if(domain==null||range==null)
					 continue;
				 if(!domain.equals("")&&!range.equals(""))
				 {
					 //System.out.println(domain+" "+property+" "+range);
					 String Triple="";
					 ArrayList<String> subjects=getConceptSubClasses(domain);
					 subjects.add(domain);
					 for(String s:subjects)
					 {
						 //System.out.println(s+","+property+","+range);
						 Triple=s+","+property+","+range;
						 if(!Relations.contains(Triple))
						 Relations.add(Triple);
					 }	
					 ArrayList<String> objects=getConceptSubClasses(range);
					 objects.add(range);
					 for(String o:objects)
					 {
						 //System.out.println(s+","+property+","+range);
						 Triple=domain+","+property+","+o;
						 if(!Relations.contains(Triple))
						 Relations.add(Triple);
					 }		
				 }
					 
				 else if(!domain.equals("")&&ranges!=null)
				 {
					 //System.out.println("********************");
					 for(OWLClassExpression a:ranges)
					 {
						 if(a==null)
							 continue;
						 String Triple="";
						 //ֵ���ǵñȽ���ϸ,���ֻ�Զ�����������
						 ArrayList<String> subjects=getConceptSubClasses(domain);
						 subjects.add(domain);
						 for(String s:subjects)
						 {
							 //System.out.println(s+","+property+","+range);
							 Triple=s+","+property+","+a.asOWLClass().getIRI().getFragment();
							 if(!Relations.contains(Triple))
							 Relations.add(Triple);
						 }	
						 //System.out.println(domain+" "+property+" "+a);
					 }
					 //System.out.println("********************");
					 
				 }
				 else if(domains!=null&&!range.equals(""))
				 {
					 //System.out.println("********************");		
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 String Triple="";
						//���������ǵñȽ���ϸ,���ֻ��ֵ��������
						 ArrayList<String> objects=getConceptSubClasses(range);
						 objects.add(range);
						 for(String o:objects)
						 {
							 //System.out.println(s+","+property+","+range);
							 Triple=a.asOWLClass().getIRI().getFragment()+","+property+","+o;
							 if(!Relations.contains(Triple))
							 Relations.add(Triple);
						 }		
						 //System.out.println(a.asOWLClass().getIRI().getFragment()+" "+property+" "+range);
					 }
					// System.out.println("********************");
				 }
				 
				 else if(domains!=null&&ranges!=null)
				 {
					 //System.out.println("********************");		
					 String Triple="";
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 for(OWLClassExpression b:ranges)
						 {
							 if(b==null)
								 continue;
							 //System.out.println(a.asOWLClass().getIRI().getFragment()+" "+property+" "+b.asOWLClass().getIRI().getFragment());
							 Triple=a.asOWLClass().getIRI().getFragment()+","+property+","+b.asOWLClass().getIRI().getFragment();
							 if(!Relations.contains(Triple))
								 Relations.add(Triple);					 
						 }
					 }
					 //System.out.println("********************");
				 }    		 
	    	 }
	    	 
	    	 return Relations;
	    }
	    
	    public  ArrayList<String> getInitialObjectRelations()
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
	    	 { 		 
	    		 String property=op.getIRI().getFragment();	
	    		// System.out.println(property);
	    		 if(property.equals("topObjectProperty")||property.equals("bottomObjectProperty"))//���汾��
	    			 continue;
	    		 Set<OWLClassExpression> Domain= op.getDomains(onto);
	    		 Set<OWLClassExpression> Range= op.getRanges(onto);
	    		 String domain="";
	    		 String range="";
	    		 Set<OWLClassExpression> domains=null;
	    		 Set<OWLClassExpression> ranges = null;
				 for(OWLClassExpression d:Domain)
				 {	
					 if(d==null)
						 continue;
					 if(!d.isAnonymous())
						 domain=d.asOWLClass().getIRI().getFragment();
					 else
						  domains= d.asDisjunctSet();
				 }
				 for(OWLClassExpression r:Range)
				 {	
					 if(r==null)
						 continue;
					 if(!r.isAnonymous())
						 range=r.asOWLClass().getIRI().getFragment();
					 else
						  ranges= r.asDisjunctSet();
				 }
				 //Ŀǰ���ñ���̬��
				 if(domain==null||range==null)
					 continue;
				 if(!domain.equals("")&&!range.equals(""))
				 {
					 String Triple=domain+","+property+","+range;
					 if(!Relations.contains(Triple))
					 Relations.add(Triple);				 
				 }
					 
				 else if(!domain.equals("")&&ranges!=null)
				 {
					 //System.out.println("********************");
					 for(OWLClassExpression a:ranges)
					 {
						 if(a==null)
							 continue;
						 String Triple="";		
						 System.out.println(domain+","+property+","+range);
						 Triple=domain+","+property+","+a.asOWLClass().getIRI().getFragment();
						 if(!Relations.contains(Triple))
						 Relations.add(Triple);
						
						
					 }
					 
				 }
				 else if(domains!=null&&!range.equals(""))
				 {
					 //System.out.println("********************");		
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 String Triple="";
						 System.out.println(domain+","+property+","+range);
						//���������ǵñȽ���ϸ,���ֻ��ֵ��������
						 Triple=a.asOWLClass().getIRI().getFragment()+","+property+","+range;
						 if(!Relations.contains(Triple))
							Relations.add(Triple);
					 }
					
				 }
				 
				 else if(domains!=null&&ranges!=null)
				 {
					 //System.out.println("********************");		
					 String Triple="";
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 for(OWLClassExpression b:ranges)
						 {
							 if(b==null)
								 continue;
							 //System.out.println(a.asOWLClass().getIRI().getFragment()+" "+property+" "+b.asOWLClass().getIRI().getFragment());
							 Triple=a.asOWLClass().getIRI().getFragment()+","+property+","+b.asOWLClass().getIRI().getFragment();
							 if(!Relations.contains(Triple))
								 Relations.add(Triple);					 
						 }
					 }
					 //System.out.println("********************");
				 }    		 
	    	 }
	    	 
	    	 return Relations;
	    }
	    
	    public  ArrayList<String> getDataPropertyRelations()
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 for (OWLDataProperty dp : onto.getDataPropertiesInSignature()) 
	    	 { 		 
	    		 String property=dp.getIRI().getFragment();	
	    		// System.out.println(property);
	    		 if(property==null)
	    			 continue;
	    		 if(property.equals("topDataProperty")||property.equals("bottomDataProperty"))//���汾��
	    			 continue;
	    		 Set<OWLClassExpression> Domain= dp.getDomains(onto);
	    		 Set<OWLDataRange> Range= dp.getRanges(onto);
	    		 String domain="";
	    		 String range="";
	    		 Set<OWLClassExpression> domains=null;
	    		 //Set<OWLClassExpression> ranges = null;
				 for(OWLClassExpression d:Domain)
				 {	
					 if(d==null)
						 continue;
					 if(!d.isAnonymous())
						 domain=d.asOWLClass().getIRI().getFragment();
					 else
						  domains= d.asDisjunctSet();
				 }
				 for(OWLDataRange r:Range)
				 {	
					 if(r==null)
						 continue;
					// if(!r.isDatatype())
					 	// range=r.asOWLDatatype().toString();
					 if(r.isDatatype())
						 range=r.asOWLDatatype().getIRI().getFragment();
				 }
				 //String Triple="";
				 if(!domain.equals("")&&!range.equals(""))
				 {
					 ArrayList<String> subjects=getConceptSubClasses(domain);
					 subjects.add(domain);
					 String Triple="";
					 for(String s:subjects)
					 {
						 //System.out.println(s+","+property+","+range);
						 Triple=s+","+property+","+range;
						 if(!Relations.contains(Triple))
						 Relations.add(Triple);
					 }					 
				 }
				
				 else if(domains!=null&&!range.equals(""))
				 {
					// System.out.println("********************");		
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 domain=a.asOWLClass().getIRI().getFragment();
						 ArrayList<String> subjects=getConceptSubClasses(domain);
						 subjects.add(domain);
						 String Triple="";
						 for(String s:subjects)
						 {
							 //System.out.println(s+","+property+","+range);
							 Triple=s+","+property+","+range;
							 if(!Relations.contains(Triple))
							 Relations.add(Triple);
						 }
					 }
					 //System.out.println("********************");
				 }	
	    	 }
	    	 
	    	 return Relations;
	    }
	    
	    public  ArrayList<String> getInitialDataPropertyRelations()
	    {
	    	 ArrayList<String> Relations=new ArrayList<String>();
	    	 for (OWLDataProperty dp : onto.getDataPropertiesInSignature()) 
	    	 { 		 
	    		 String property=dp.getIRI().getFragment();	
	    		// System.out.println(property);
	    		 if(property==null)
	    			 continue;
	    		 if(property.equals("topDataProperty")||property.equals("bottomDataProperty"))//���汾��
	    			 continue;
	    		 Set<OWLClassExpression> Domain= dp.getDomains(onto);
	    		 Set<OWLDataRange> Range= dp.getRanges(onto);
	    		 String domain="";
	    		 String range="";
	    		 Set<OWLClassExpression> domains=null;
	    		 //Set<OWLClassExpression> ranges = null;
				 for(OWLClassExpression d:Domain)
				 {	
					 if(d==null)
						 continue;
					 if(!d.isAnonymous())
						 domain=d.asOWLClass().getIRI().getFragment();
					 else
						  domains= d.asDisjunctSet();
				 }
				 for(OWLDataRange r:Range)
				 {	
					 if(r==null)
						 continue;
					// if(!r.isDatatype())
					 	// range=r.asOWLDatatype().toString();
					 if(r.isDatatype())
						 range=r.asOWLDatatype().getIRI().getFragment();
				 }
				 //String Triple="";
				 if(!domain.equals("")&&!range.equals(""))
				 {
					 String Triple="";			
						 //System.out.println(s+","+property+","+range);
					Triple=domain+","+property+","+range;
					 if(!Relations.contains(Triple))
					 Relations.add(Triple);			 				 
				 }
				
				 else if(domains!=null&&!range.equals(""))
				 {
					// System.out.println("********************");		
					 for(OWLClassExpression a:domains)
					 {
						 if(a==null)
							 continue;
						 domain=a.asOWLClass().getIRI().getFragment();
						 String Triple="";
						Triple=domain+","+property+","+range;
						if(!Relations.contains(Triple))
						Relations.add(Triple);
					 }
					 //System.out.println("********************");
				 }	
	    	 }
	    	 
	    	 return Relations;
	    }
	    
	    public ArrayList<String> getEquivalentClass()
		{
			ArrayList<String> EquivalentClass=new ArrayList<String>();
			 for (OWLClass c : onto.getClassesInSignature()) 
			 {
				 //	System.out.println(c.getIRI().getFragment());
				 String concept=c.getIRI().getFragment();
				 if(concept==null)
					 continue;
				 if(concept.charAt(0)=='_')
					 concept=concept.replaceFirst("_", "");
				 if(!concept.equals("Thing")||concept.equals("Nothing"))//Thing������Ͳ�������
				 {
					 
					/* Set<OWLClassExpression> equivalents =c.getEquivalentClasses(onto);
					 for(OWLClassExpression equ: equivalents)*/
					 Node<OWLClass> equivalents= hermit.getEquivalentClasses(c);
					 for(OWLClass equ:equivalents)
					 {
						 if(!equ.isAnonymous())
						 {
							 String equivalentConcept=equ.asOWLClass().getIRI().getFragment();
							 if(!equivalentConcept.equals(concept)&&!equivalentConcept.equals("Nothing"))
							 {
									EquivalentClass.add(concept+","+equivalentConcept+","+"Equal");
								//	System.out.println(concept+","+equivalentConcept+","+"Equal");
							 }
						 }	  
					 }
					 
				 }
			 }
			 return EquivalentClass;
		}
				 
	    public  ArrayList<String> getEquivalentObjectProperty()
		{
			ArrayList<String> EquivalentProperties=new ArrayList<String>();
	        for (OWLObjectProperty op : onto.getObjectPropertiesInSignature()) 
            {
	       // 	System.out.println(op.getIRI().getFragment());
	        	String property=op.getIRI().getFragment();
	        	 if(property==null)
					 continue;
	        	if(property.equals("topObjectProperty")||property.equals("bottomObjectProperty"))//���汾��
					continue;
				//ҽѧ����
				//else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED")||property.equals("UNDEFINED_part_of"))
				else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED"))
					continue;
				else
				{
					Node<OWLObjectPropertyExpression> equivalents=hermit.getEquivalentObjectProperties(op);
				 for(OWLObjectPropertyExpression equ:equivalents)
				 {
					 if(!equ.isAnonymous())
					 {
						 String equivalentProperty=equ.asOWLObjectProperty().getIRI().getFragment();
						 if(!equivalentProperty.equals(property)&&!equivalentProperty.equals("Nothing"))
						 {
							 	EquivalentProperties.add(property+","+equivalentProperty+","+"Equal");
								//System.out.println(property+","+equivalentProperty+","+"Equal");
						 }
					 }
				 }
				}			
            }
	        return EquivalentProperties;		
		}		   
	    
	    public  ArrayList<String> getEquivalenDataProperty()
		{
			ArrayList<String> EquivalentProperties=new ArrayList<String>();
	        for (OWLDataProperty dp : onto.getDataPropertiesInSignature()) 
            {
	       // 	System.out.println(op.getIRI().getFragment());
	        	String property=dp.getIRI().getFragment();
	        	 if(property==null)
					 continue;
	        	if(property.equals("topDataProperty")||property.equals("bottomDataProperty"))//���汾��
					continue;
				//ҽѧ����
				//else if(property.equals("ObsoleteProperty")||property.equals("UNDEFINED")||property.equals("UNDEFINED_part_of"))
				else
				{
					Node<OWLDataProperty> equivalents=hermit.getEquivalentDataProperties(dp);
				 for(OWLDataProperty equ:equivalents)
				 {
					 if(!equ.isAnonymous())
					 {
						 String equivalentProperty=equ.getIRI().getFragment();
						 if(!equivalentProperty.equals(property)&&!equivalentProperty.equals("Nothing"))
						 {
							 	EquivalentProperties.add(property+","+equivalentProperty+","+"Equal");
								//System.out.println(property+","+equivalentProperty+","+"Equal");
						 }
					 }
				 }
				}			
            }
	        return EquivalentProperties;		
		}	
	    
	    public ArrayList<String> getSomeRestrictions()//�ҵ�ĳ���������и������������
		{
			ArrayList<String> someRestrictions=new ArrayList<String>();
			 for (OWLClass c : onto.getClassesInSignature()) 
			 {
				 //	System.out.println(c.getIRI().getFragment());
				 String concept=c.getIRI().getFragment();
			 	 if(concept==null)
					 continue;
				 if(concept.charAt(0)=='_')
					 concept=concept.replaceFirst("_", "");
				 if(!concept.equals("Thing")||concept.equals("Nothing"))//Thing������Ͳ�������
				 {		
					 NodeSet<OWLClass> Father = hermit.getSuperClasses(c, false);
					 Set<OWLClass> fathers = Father.getFlattened();
					 fathers.add(c);
					 for (OWLClass sup : fathers) 
					 {
						 RestrictionVisitor restrictionVisitor = new RestrictionVisitor(sup,onto);
						 HashMap<OWLObjectPropertyExpression, Set<OWLClass>> someValue=restrictionVisitor.getRestrictedProperties();
						 if(someValue.size()!=0)
			        	 {
			        		// System.out.println(concept);
			        		// System.out.println("Restricted properties for " + concept + ": " + someValue.size());
			        		 for (OWLObjectPropertyExpression prop : someValue.keySet()) 
			        		 {   			 
			        			 //System.out.println("    " + prop);
			        			 Set<OWLClass> objects=someValue.get(prop);
			        			 for(OWLClass o:objects)
			        			 {
			        				 String relation=prop.asOWLObjectProperty().getIRI().getFragment();
			        				 String object=o.getIRI().getFragment();
			        				 String constraint = concept+","+relation + ","+ "some" + "," + object;
			        				 //System.out.println(constraint);
			        				 someRestrictions.add(constraint);
			        			 }
			        		 }
			        	 }
					 }					
				 }
			 }
		return someRestrictions;
		}
	    
	    public ArrayList<String> getLocalSomeRestrictions()//�ҵ�ĳ���������и������������
	  		{
	  			ArrayList<String> someRestrictions=new ArrayList<String>();
	  			 for (OWLClass c : onto.getClassesInSignature()) 
	  			 {
	  				 //	System.out.println(c.getIRI().getFragment());
	  				 String concept=c.getIRI().getFragment();
	  				 if(concept==null)
						 continue;
	  				 if(concept.charAt(0)=='_')
	  					 concept=concept.replaceFirst("_", "");
	  				 if(!concept.equals("Thing")||concept.equals("Nothing"))//Thing������Ͳ�������
	  				 {		
	  					 RestrictionVisitor restrictionVisitor = new RestrictionVisitor(c,onto);
	  					 HashMap<OWLObjectPropertyExpression, Set<OWLClass>> someValue=restrictionVisitor.getRestrictedProperties();
	  					 if(someValue.size()!=0)
	  					 {
	  						 // System.out.println(concept);
	  						 // System.out.println("Restricted properties for " + concept + ": " + someValue.size());
	  						 for (OWLObjectPropertyExpression prop : someValue.keySet()) 
	  						 {   			 
	  							 //System.out.println("    " + prop);
	  							 Set<OWLClass> objects=someValue.get(prop);
	  							 for(OWLClass o:objects)
	  							 {
	  								 String relation=prop.asOWLObjectProperty().getIRI().getFragment();
	  								 String object=o.getIRI().getFragment();
	  								 String constraint = concept+","+relation + ","+ "some" + "," + object;
	  								// System.out.println(constraint);
	  								 someRestrictions.add(constraint);
	  							 }
	  						 }
	  					 }		 				
	  				 }
	  			 }
	  		return someRestrictions;
	  		}
	    
	    private static class RestrictionVisitor extends OWLClassExpressionVisitorAdapter 
	    {
	    	 
	         private boolean processInherited = true;
	 
	         private Set<OWLClass> processedClasses;
	         
	         private HashMap<OWLObjectPropertyExpression, Set<OWLClass>> restrictedProperties;
	 
	         private OWLOntology ontology;
	 
	         public RestrictionVisitor(OWLClass restrictedClass, OWLOntology onts ) {
	             restrictedProperties = new HashMap<OWLObjectPropertyExpression, Set<OWLClass>>();
	             processedClasses =new HashSet<OWLClass>();
	             ontology = onts;
	             processRestrictions(restrictedClass);
	         }
	 
	         public void setProcessInherited(boolean processInherited) {
	             this.processInherited = processInherited;
	         }
	  
	         public HashMap<OWLObjectPropertyExpression, Set<OWLClass>> getRestrictedProperties() {
	             return restrictedProperties;
	         }

	         public void visit(OWLClass desc) {
	             if (processInherited && !processedClasses.contains(desc)) {
	                 // If we are processing inherited restrictions then
	                 // we recursively visit named supers.  Note that we
	                 // need to keep track of the classes that we have processed
	                 // so that we don't get caught out by cycles in the taxonomy
	                 processedClasses.add(desc);           
	                     for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(desc)) {
	                         ax.getSuperClass().accept(this);
	                     }
	                 
	             }
	         }
	 
	         public void reset() {
	             processedClasses.clear();
	             restrictedProperties.clear();
	         }
	          
	         public void visit(OWLObjectSomeValuesFrom desc) {
	             // This method gets called when a class expression is an
	             // existential (someValuesFrom) restriction and it asks us to visit it
	        	 OWLObjectPropertyExpression property = desc.getProperty();
	     		OWLClassExpression filler = desc.getFiller();
	     		Set<OWLClass> classes = filler.getClassesInSignature();
	     		addProperty(property, classes);
	     		// recurse
	     		filler.accept(this);
	         }
	         
	         private void addProperty( OWLObjectPropertyExpression property, Set<OWLClass> classes) {
	     		Set<OWLClass> existingClasses = restrictedProperties.get(property);
	     		if (existingClasses != null) {
	     			classes.addAll(existingClasses);
	     		}
	     		restrictedProperties.put(property, classes);
	     	}
	         private void processRestrictions(OWLClass restrictedClass)
	         {
	        	 
	        	 for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(restrictedClass)) {
	             	OWLClassExpression cls = ax.getSuperClass();
	             	if(cls.isAnonymous())
	             	{
	             		//System.out.println(restrictedClass.getIRI().getFragment());
	             		//System.out.println("[Inferred] Superclass: " + cls);	
	             		cls.accept(this);
	             	}
	                 // Ask our superclass to accept a visit from the RestrictionVisitor - if it is an
	                 // existential restriction then our restriction visitor will answer it - if not our
	                 // visitor will ignore it
	             	
	             }
	        	 
	        	 for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(restrictedClass)) {
	             	for (OWLClassExpression cls: ax.getClassExpressions()) {
	             		if(cls.isAnonymous())
		             	{
		             		//System.out.println(restrictedClass.getIRI().getFragment());
		             		//System.out.println("Equiv class: " + cls);	
		             		cls.accept(this);
		             	}
	     	            // Ask our class to accept a visit from the RestrictionVisitor - if it is an
	     	            // existential restriction then our restriction visitor will answer it - if not our
	     	            // visitor will ignore it
	     	        	
	             	}
	             }
	        	 //System.out.println("%%%%%%%%%%%%%%%%%%%%%%");
	        	/* if(this.getRestrictedProperties().size()!=0)
	        	 {
	        		 System.out.println(restrictedClass.getIRI().getFragment());
	        		 System.out.println("Restricted properties for " + restrictedClass + ": " + this.getRestrictedProperties().size());
	        		 for (OWLObjectPropertyExpression prop : this.getRestrictedProperties().keySet()) 
	        		 {   			 
	        			 //System.out.println("    " + prop);
	        			 Set<OWLClass> objects=getRestrictedProperties().get(prop);
	        			 for(OWLClass o:objects)
	        			 {
	        				 System.out.println(prop);
	        				 System.out.println(o);
	        			 }
	        		 }
	        	 }*/
	         }
	     }
	    

	    
	    public  ArrayList<String> enhancedRelation(ArrayList<String> ObjectRelations,ArrayList<String> EquivalentClass)
		{
			//System.out.println("*********************************");
			int m=0,n=0;
			for(int i=0;i<EquivalentClass.size();i++)
			{
				String pairs[]=EquivalentClass.get(i).split(",");
				if(pairs.length==4&&pairs[2].equals("some"))  //parts[0] ObjectProperty some parts[1]
				{
					ArrayList<String> triple=new ArrayList<String>();
					triple =findIndexOfRelation(ObjectRelations,pairs[1]);//��λ����,����Ԫ����Բ�ֹһ������ֻ�ж�����Ϊnull���߲�Ϊnull�������
					//boolean flag1=false;//���������Ԫ��Ķ�����Ϊ�յ����
					boolean flag2=false;//���������Ԫ��Ķ�����Ϊ�գ�������ȵ����
					for(int j=0;j<triple.size();j++)
					{
						String parts1[]=triple.get(j).split(",");
						/*for(String a:parts1)
						{
							System.out.println(a+" ");
						}
						System.out.println();*/
						
						int index=Integer.parseInt(parts1[3]);
						String domain=parts1[0];
						if(domain.equals("null"))//������Ϊ�յ���Ԫ���ϵ
						{
							String orginalRelation[]=ObjectRelations.get(index).split(",");
							String newRelation=pairs[0]+","+orginalRelation[1]+","+orginalRelation[2];
							ObjectRelations.remove(index);
							ObjectRelations.add(newRelation);
							if(!ObjectRelations.contains(newRelation))
							{
								//System.out.println("�޸ĵ���Ԫ��Ϊ: "+newRelation);
								m++;
							}
							//flag1=true;//ȷʵ��Ϊ�յ����
						}
						else if(!domain.equals("null")&&pairs[0].equals(domain))
						{
							flag2=true;//������ȵ����
							break;
						}
						
					}
					if(flag2==false)//ȷʵ�������domain��Ϊ0
					{
						triple =findIndexOfRelation(ObjectRelations,pairs[1]);//���¼���һ��
						for(int j=0;j<triple.size();j++)
						{
							String parts2[]=triple.get(j).split(",");
							//int index=Integer.parseInt(parts2[3]);
							//String range=parts2[2];
							//String orginalRelation[]=ObjectRelations.get(index).split(",");
							String newRelation=pairs[0]+","+parts2[1]+","+parts2[2];		
							if(!ObjectRelations.contains(newRelation))
							{
								//System.out.println("��ӵ���Ԫ��Ϊ: "+newRelation);
								ObjectRelations.add(newRelation);
								n++;
							}
						}
					}	
					flag2=false;
				}
			
			}			
		//	System.out.println("���Ĺ�ϵ������Ϊ: "+m);
			//System.out.println("��ӹ�ϵ������Ϊ: "+n);
			return ObjectRelations;
		}
	    
	    public ArrayList<String> findIndexOfRelation(ArrayList<String> Relations,String index)
		{
			ArrayList<String> Triples=new ArrayList<String>();
			for(int i=0;i<Relations.size();i++)
			{
				String parts[]=Relations.get(i).split(",");
				if(index.equals(parts[1]))
				{
					Triples.add(Relations.get(i)+","+i);//ͬʱ��¼������ֵ
				}
			}
			return Triples;
		}
	    
	    public  TreeMap_Tools transformToPartOf(ArrayList<String> Restriction,ArrayList<String> subclasses)
		{
			//System.out.println("*********************************");
			//ArrayList<String> partOf=new ArrayList<String>();
			ArrayList<String> partOf=new ArrayList<String>();
			//HashMap<String,String> partOfMap=new HashMap<String,String>();
			HashMap<String, HashSet<String>> partOfMap=new HashMap<String, HashSet<String>>();
			for(int i=0;i<Restriction.size();i++)
			{
				String pairs[]=Restriction.get(i).split(",");
				if(pairs.length==4&&pairs[2].equals("some"))  //parts[0] ObjectProperty some parts[1]
				{
					String newRelation=pairs[0]+"--"+pairs[3];		
					if(!partOf.contains(newRelation))
					{
						partOf.add(newRelation.toLowerCase());
						HashSet<String> a=new HashSet<String>();
						a.add(pairs[3].toLowerCase());
						if(partOfMap.containsKey(pairs[0].toLowerCase()))
						{
							partOfMap.get(pairs[0].toLowerCase()).add(pairs[3].toLowerCase());
						}
						else
							partOfMap.put(pairs[0].toLowerCase(), a);
						//partOfMap.put(pairs[0].toLowerCase(), pairs[3].toLowerCase());
					}
				}	
			}	
			
			/*System.out.println(partOf.size());
			System.out.println(partOfMap.size());*/
			
			int num1=0;
			Set<String> keyset1=partOfMap.keySet();
			for(String a:keyset1)
			{
				if(partOfMap.get(a).size()==1)
					num1++;
				else
					num1=num1+partOfMap.get(a).size();
				
			}
			//System.out.println("Restricitonת��ΪpartOf������Ϊ: "+num1);
			//System.out.println(partOfMap.getNumberOfMap());
			
			//ArrayList<String> newPartOfRelation=new ArrayList<String>();

			//TreeMap_Tools partOfRelation=new TreeMap_Tools(partOf);
			TreeMap_Tools partOfRelation=new TreeMap_Tools();
			Queue<String> order = new LinkedList<String>();
			for(int i=0;i<partOf.size();i++)
			{
				String part[]=partOf.get(i).split("--");
				String index=part[0];		//��part of�е���λ��Ϊ����
				while(partOfMap.get(index)!=null||!order.isEmpty())//������������ҳ���Ϊ�գ�����������û��ֵ��
				{
							
					if(partOfMap.get(index)!=null)
					{
						HashSet<String> b=new HashSet<String>();	
						b.addAll(partOfMap.get(index));
						for(String a: b)
						{
							order.offer(a);
							if(partOfMap.get(a)!=null)
								partOfMap.get(part[0]).addAll(partOfMap.get(a));
						}
						index=order.remove();
					}
					else
					{
						index=order.remove();
					}			
				}
			}
			
			/*TreeMap_Tools subclass=new TreeMap_Tools(subclasses);
			Set<String> keySet= new HashSet<String>();
			keySet.addAll(partOfMap.keySet());
			for(String a:keySet)
			{
				ArrayList<String> objects=subclass.GetKey_Value(a);
				if(objects!=null)
				{
					for(int i=0;i<objects.size();i++)
					{
						String object=objects.get(i);
						if(!partOfMap.containsKey(object))
						{
							partOfMap.put(object,partOfMap.get(a));
							//partOfMap.get(pairs[0].toLowerCase()).add(pairs[3].toLowerCase());
						}
						else
						{
							partOfMap.get(object).addAll(partOfMap.get(a));
						}
					}
				}
			}
			
			int num2=0;
			Set<String> keyset2=partOfMap.keySet();
			for(String a:keyset2)
			{
				if(partOfMap.get(a).size()==1)
					num2++;
				else
					num2=num2+partOfMap.get(a).size();
				
			}
			System.out.println("Restricitonת��ΪpartOf������Ϊ: "+num2);*/
					
		/*	int num=0;
			Set<String> keyset=partOfMap.keySet();
			for(String a:keyset)
			{
				if(partOfMap.get(a).size()==1)
					num++;
				else
					num=num+partOfMap.get(a).size();
				
			}
			System.out.println("Restricitonת��ΪpartOf������Ϊ: "+num);*/
			Set<String> keyset=partOfMap.keySet();
			for(String key:keyset)
			{
				HashSet<String>  a=partOfMap.get(key);
				ArrayList<String> values=new ArrayList<String>();
				for(String value:a)
				{
					values.add(value);
				}
				partOfRelation.putAdd(key, values);	
			}
			//System.out.println("Restricitonת��ΪpartOf������Ϊ: "+partOfRelation.getNumberOfMap());
			
			return partOfRelation;
		}
		
		public  TreeMap_Tools transformToHaspart(ArrayList<String> Restriction,ArrayList<String> subclasses)
		{
			//System.out.println("*********************************");
			ArrayList<String> hasPart=new ArrayList<String>();
			//HashMap<String,String> hasPartOfMap=new HashMap<String,String>();
			HashMap<String, HashSet<String>> hasPartOfMap=new HashMap<String, HashSet<String>>();
			for(int i=0;i<Restriction.size();i++)
			{
				String pairs[]=Restriction.get(i).split(",");
				if(pairs.length==4&&pairs[2].equals("some"))  //parts[0] ObjectProperty some parts[1]
				{
					String newRelation=pairs[3]+"--"+pairs[0];		
					if(!hasPart.contains(newRelation))
					{
						hasPart.add(newRelation.toLowerCase());
						HashSet<String> a=new HashSet<String>();
						a.add(pairs[0].toLowerCase());
						if(hasPartOfMap.containsKey(pairs[3].toLowerCase()))
						{
							hasPartOfMap.get(pairs[3].toLowerCase()).add(pairs[0].toLowerCase());
						}
						else
							hasPartOfMap.put(pairs[3].toLowerCase(), a);
					//	hasPartOfMap.put(pairs[3].toLowerCase(), pairs[0].toLowerCase());
					}
				}	
			}	
				
			//System.out.println(hasPart.size());
			//System.out.println(hasPartOfMap.size());
			int num1=0;
			Set<String> keyset1=hasPartOfMap.keySet();
			for(String a:keyset1)
			{
				if(hasPartOfMap.get(a).size()==1)
					num1++;
				else
					num1=num1+hasPartOfMap.get(a).size();
				
			}
			//System.out.println("Restricitonת��ΪhasPart������Ϊ: "+num1);
			
			//TreeMap_Tools hasPartRelation=new TreeMap_Tools(hasPart);	
			TreeMap_Tools hasPartRelation=new TreeMap_Tools();	
			Queue<String> order = new LinkedList<String>();
			for(int i=0;i<hasPart.size();i++)
			{
				String part[]=hasPart.get(i).split("--");
				String index=part[0];		//��part of�е���λ��Ϊ����
				while(hasPartOfMap.get(index)!=null||!order.isEmpty())//������������ҳ���Ϊ�գ�����������û��ֵ��
				{				
					if(hasPartOfMap.get(index)!=null)
					{				
						HashSet<String> b=new HashSet<String>();
						b.addAll(hasPartOfMap.get(index));
						for(String a: b)
						{
							order.offer(a);
							if(hasPartOfMap.get(a)!=null)
								hasPartOfMap.get(part[0]).addAll(hasPartOfMap.get(a));
						}
						index=order.remove();
					}
					else
					{
						index=order.remove();
					}			
				}
			}
		
			/*TreeMap_Tools subclass=new TreeMap_Tools(subclasses);
			Set<String> keySet= new HashSet<String>();
			keySet.addAll(hasPartOfMap.keySet());
			for(String a:keySet)
			{
				HashSet<String> b=new HashSet<String>();
				b.addAll(hasPartOfMap.get(a));//��Ҫ��hasPart�й�ϵ�ĺ��沿��
				for(String c:b)
				{
					ArrayList<String> objects=subclass.GetKey_Value(c);//ͨ���������ҵ��Ӽ�������
					if(objects!=null)
					{
						hasPartOfMap.get(a).addAll(objects);
						for(int i=0;i<objects.size();i++)//ͬ�������п��ܻ���hasPart��ϵ��Ӧ���е��������
						{
							String object=objects.get(i);
							if(hasPartOfMap.get(object)!=null)
								hasPartOfMap.get(a).addAll(hasPartOfMap.get(object));
						}
					}
				}
			}
			
			int num2=0;
			Set<String> keyset2=hasPartOfMap.keySet();
			for(String a:keyset2)
			{
				if(hasPartOfMap.get(a).size()==1)
					num2++;
				else
					num2=num2+hasPartOfMap.get(a).size();
				
			}
			System.out.println("Restricitonת��ΪhasPart������Ϊ: "+num2);*/
		/*	int num=0;
			Set<String> keyset=hasPartOfMap.keySet();
			for(String a:keyset)
			{
				if(hasPartOfMap.get(a).size()==1)
					num++;
				else
					num=num+hasPartOfMap.get(a).size();		
			}
			System.out.println("Restricitonת��ΪhasPart������Ϊ: "+num);*/
			Set<String> keyset=hasPartOfMap.keySet();
			for(String key:keyset)
			{
				HashSet<String>  a=hasPartOfMap.get(key);
				ArrayList<String> values=new ArrayList<String>();
				for(String value:a)
				{
					values.add(value);
				}
				hasPartRelation.putAdd(key, values);	
			}
			//System.out.println("Restricitonת��ΪhasPart������Ϊ: "+hasPartRelation.getNumberOfMap());
			
		
			return hasPartRelation;
		}
		
		public  TreeMap_Tools transformToPartOf(ArrayList<String> Restriction)
		{
			
			ArrayList<String> partOf=new ArrayList<String>();
			for(int i=0;i<Restriction.size();i++)
			{
				String pairs[]=Restriction.get(i).split(",");
				if(pairs.length==4&&pairs[2].equals("some"))  //parts[0] ObjectProperty some parts[1]
				{
					String newRelation=pairs[0]+"--"+pairs[3];		
					if(!partOf.contains(newRelation))				
						partOf.add(newRelation.toLowerCase());			
				}	
			}	
			TreeMap_Tools partOfRelation=new TreeMap_Tools(partOf);	
			/*System.out.println("TreeMap�ĸ���Ϊ: "+partOfRelation.size());	
			System.out.println("Restricitonת��ΪpartOf������Ϊ: "+partOfRelation.getNumberOfMap());		*/	
			return partOfRelation;
		}
		
		public  TreeMap_Tools transformToHaspart(ArrayList<String> Restriction)
		{
			//System.out.println("*********************************");
			ArrayList<String> hasPart=new ArrayList<String>();
			for(int i=0;i<Restriction.size();i++)
			{
				String pairs[]=Restriction.get(i).split(",");
				if(pairs.length==4&&pairs[2].equals("some"))  //parts[0] ObjectProperty some parts[1]
				{
					String newRelation=pairs[3]+"--"+pairs[0];		
					if(!hasPart.contains(newRelation))
						hasPart.add(newRelation.toLowerCase());				
				}	
			}	
			TreeMap_Tools hasPartRelation=new TreeMap_Tools(hasPart);	
			//System.out.println("TreeMap�ĸ���Ϊ: "+hasPartRelation.size());	
			//System.out.println("Restricitonת��ΪhasPart������Ϊ: "+hasPartRelation.getNumberOfMap());	
			return hasPartRelation;
		}
	    
}


