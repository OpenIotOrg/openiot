package org.openiot.ld4s.resource.link_review;

import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.RevVocab;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LD4SLinkReviewResource extends LD4SDataResource {
	
	/** Service resource name. */
	protected String resourceName = "Link Review";
	
	/** RDF Data Model of this Service resource semantic annotation. */
	protected Model rdfData = null;
	
	/** Resource provided by this Service resource. */
	protected LinkReview ov = null;

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
		resource = addLinkedData(resource, Domain.ALL, this.context);
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
		if (resourceId != null){
			subjuri = this.uristr;	
		}else{
			subjuri = ov.getResource_id();
		}
		resource = rdfData.createResource(subjuri);
		String item = ov.getData_link();
		if (item != null){
			resource.addProperty(RevVocab.IS_FEEDBACK_OF, 
					rdfData.createResource(item));
			
			Double vote = ov.getVote();
			if (vote != -1){
				resource.addProperty(RevVocab.RATING, 
						rdfData.createTypedLiteral(String.valueOf(vote), XSDDatatype.XSDdouble));
				
				
				//for the link this rating is referred to
				Model forlink = retrieve(item, this.namedModel);
				Resource subj = null;
				double rates = 0.0;
				StmtIterator stmtit = null;
				//if this link exists
				if (forlink != null && (subj=forlink.getResource(item)) != null){
					//accordingly update the total amount of votes
					stmtit = forlink.listStatements(subj, RevVocab.RATING, (RDFNode)null);
					Statement st = null;
					while(stmtit.hasNext()){
						st = stmtit.next();
						rates += st.getObject().asLiteral().getDouble();
					}
					rates += vote; 
					forlink.removeAll(null, RevVocab.RATING, (RDFNode)null);
					rdfData.removeAll(null, RevVocab.RATING, (RDFNode)null);
					subj.addProperty(RevVocab.RATING, 
							forlink.createTypedLiteral(String.valueOf(rates), XSDDatatype.XSDdouble));
					//accordingly update the total amount of positive votes 
					if (vote > 0){
						double pos_votes = 0.0;
						stmtit = forlink.listStatements(subj, RevVocab.POSITIVE_VOTES, (RDFNode)null);
						while(stmtit.hasNext()){
							st = stmtit.next();
							pos_votes += st.getObject().asLiteral().getDouble();
						}	
						pos_votes++;
						forlink.removeAll(null, RevVocab.POSITIVE_VOTES, (RDFNode)null);
						rdfData.removeAll(null, RevVocab.POSITIVE_VOTES, (RDFNode)null);
						
						subj.addProperty(RevVocab.POSITIVE_VOTES, 
								forlink.createTypedLiteral(String.valueOf(pos_votes), XSDDatatype.XSDdouble));	
					}
					//accordingly update the list of feedbacks
					subj.addProperty(RevVocab.HAS_FEEDBACK, resource);
					rdfData.add(forlink);
					rdfData.add(subj.getModel());
				}
				
			}
			
			
			String comment = ov.getComment();
			if (comment != null){
				resource.addProperty(RDFS.comment, comment);
			}
		}
		//add other properties; esp. the author and the datetime
		resource = crossResourcesAnnotation(ov, resource);
		
		return resource!=null?resource:rdfData.createResource();
	}

	
	


		  

	
}

