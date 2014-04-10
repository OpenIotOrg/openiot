package org.openiot.ld4s.resource.ontoClass;

import java.util.LinkedList;

import org.openiot.ld4s.resource.LD4SDataResource;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct an Ontology Class resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LD4SOntoClassResource extends LD4SDataResource {
	/** Service resource name. */
	protected String resourceName = "Ontology Class";

	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;

	/** Resource provided by this Service resource. */
	protected OntoClass ov = null;


	/**
	 * Creates main resources and additional related information
	 * including linked data
	 *
	 * @param m_returned model which the resources to be created should be attached to
	 * @param obj object containing the information to be semantically annotate
	 * @param id resource identification
	 * @return model 
	 * @throws Exception
	 */
	protected Resource makeOVLinkedData() throws Exception {
		Resource resource = makeOVData();
		//set the linking criteria
		this.context = ov.getLink_criteria();
//		resource = addLinkedData(resource, Domain.ALL, this.context);
		return resource;
	}

	/**
	 * Creates main resources and additional related information
	 * excluding linked data
	 *
	 * @param m_returned model which the resources to be created should be attached to
	 * @param obj object containing the information to be semantically annotate
	 * @param id resource identification
	 * @return model 
	 * @throws Exception
	 */
	protected Resource makeOVData() throws Exception {
		Resource resource = createOVResource();
		resource.addProperty(DCTerms.isPartOf,
				this.ld4sServer.getHostName()+"void");
		return resource;
	}

	protected Resource addSets(OntoClassSet set, Property lastProperty, 
			Resource subj){
		if (set.getType() == OntoClassSet.Type.singleClass){
			subj.addProperty(lastProperty, 
					rdfData.createResource(set.getId()));
			return subj;
		}
		Node bnode = null;
		Resource bnode_resource = null;
		if (set.getList() != null){
			for (OntoClassSet curr : set.getList()){
				bnode = Node.createAnon();
				bnode_resource = rdfData.createResource(bnode.getBlankNodeId());
				subj.addProperty(lastProperty, 
						addSets(curr, rdfData.createProperty(OWL.NS + set.getType()), 
								bnode_resource));

			}
		}//the else condition is not supposed to exist; otherwise it's an ignored mistake.
		return subj;
	}

	protected Resource addRestrictions(OntoClassRestriction restr, Resource subj){
		Node bnode = Node.createAnon();
		Resource bnode_resource = rdfData.createResource(bnode.getBlankNodeId());					
		subj.addProperty(RDFS.subClassOf, bnode_resource);

		bnode_resource.addProperty(RDF.type, OWL.Restriction);
		bnode_resource.addProperty(OWL.onProperty, 
				rdfData.createResource(restr.getProperty()));
		switch(restr.getAmount()){
		/** Whose range is xsd:nonNegativeInteger. */
		case cardinality:
		case maxCardinality:
		case minCardinality:
			if (restr.getDataValue() != null){
				bnode_resource.addProperty(
						rdfData.createProperty(OWL.NS +restr.getAmount().name()),
						rdfData.createTypedLiteral(restr.getDataValue(), 
								XSDDatatype.XSDnonNegativeInteger));
			}
			break;
			/** Whose range is a class or a blank node of type owl:DataRange*/
		case allValuesFrom:
		case someValuesFrom:
			OntoClassType classValue = restr.getClassValue();
			if (classValue != null){
				switch (classValue.getChoice()){
				case Datatype:
					LinkedList<String> range = classValue.getEnumValues();
					if (range != null){
						Node bnode1 = Node.createAnon();
						Resource bnode_resource1 = rdfData.createResource(
								bnode1.getBlankNodeId());
						bnode_resource.addProperty(
								rdfData.createProperty(OWL.NS +restr.getAmount().name()),
								bnode_resource1);

						bnode_resource1.addProperty(RDF.type, OWL.DataRange);
						RDFNode[] members = new RDFNode[range.size()];
						int rind = 0;
						for (String curr : range){
							members[rind++] = rdfData.createLiteral(curr);
						}
						bnode_resource1.addProperty(OWL.oneOf, rdfData.createList(members));
						
					}
					break;
				case Class:
				case SUBCLASS:
				default:
					bnode_resource.addProperty(
							rdfData.createProperty(OWL.NS +restr.getAmount().name()),
							rdfData.createResource(classValue.getUri()));
					break;

				}
			}
			break;
		/** Whose range is an individual or data value. */
		case hasValue:
		default:
			if (restr.getClassValue() != null){
				bnode_resource.addProperty(
						OWL.hasValue,
						rdfData.createResource(restr.getClassValue().getUri()));
			}else{
				bnode_resource.addProperty(
						OWL.hasValue,
						rdfData.createTypedLiteral(restr.getDataValue()));
			}
			break;

		}
		return subj;
	}

	/**
	 * Creates the main resource
	 * @param model
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	protected  Resource createOVResource() throws Exception {
		Resource resource = null;
		String subjuri = null;
//		if (resourceId != null){
//			subjuri = this.uristr;	
//		}else{
			subjuri = ov.getResource_id();
//		}
		resource = rdfData.createResource(subjuri);

		OntoClassSet[] item = ov.getDisjointClasses();
		if (item != null){
			for (int ind=0; ind<item.length ;ind++){
				resource = addSets(item[ind], OWL.disjointWith, resource);
			}
		}
		item = ov.getEquivalentClasses();
		if (item != null){
			for (int ind=0; ind<item.length ;ind++){
				resource = addSets(item[ind], OWL.equivalentClass, resource);
			}
		}
		OntoClassRestriction[] restrictions = ov.getRestrictions();
		if (restrictions != null){
			for (int ind=0; ind<restrictions.length ;ind++){
				resource = addRestrictions(restrictions[ind], resource);
			}
		}
		OntoClassType[] types = ov.getOntoClassTypes();
		if (types != null){
			for (int ind=0; ind<types.length ;ind++){
				switch (types[ind].getChoice()){
				case Datatype:
					resource.addProperty(RDF.type, 
							RDFS.Datatype);
					
					LinkedList<String> range = types[ind].getEnumValues();
					if (range != null){
						RDFNode[] members = new RDFNode[range.size()];
						int rind = 0;
						for (String curr : range){
							members[rind++] = rdfData.createLiteral(curr);
						}
						resource.addProperty(OWL.oneOf, rdfData.createList(members));
					}
					break;
				case SUBCLASS:
					if (types[ind].getSuperClass() != null){
						resource.addProperty(RDFS.subClassOf, 
								rdfData.createResource(types[ind].getSuperClass()));
					}
					break;
				case Class:
				default:
					resource.addProperty(RDF.type, rdfData.createResource(types[ind].getUri()));
					break;
				
				}
			}
		}
//		resource = crossResourcesAnnotation(ov, resource);
		return resource;
	}




}
