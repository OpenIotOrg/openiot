package org.openiot.sdum.core.api.impl;

import org.openiot.commons.sdum.serviceresultset.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.RequestPresentation;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.sdum.serviceresultset.model.Widget;
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;
import org.openiot.commons.sparql.result.model.Binding;
import org.openiot.commons.sparql.result.model.Literal;
import org.openiot.commons.sparql.result.model.Result;
import org.openiot.commons.sparql.result.model.Results;
import org.openiot.commons.sparql.result.model.Sparql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kefnik
 *
 */
public class PollForReportImpl {

	
	final static Logger logger = LoggerFactory.getLogger(PollForReportImpl.class);
	
	private SdumServiceResultSet sdumServiceResultSet=null;
	


	/**
	 * 
	 */
	public PollForReportImpl(String serviceID) {
		
		
		logger.debug("Recieved Parameters: serviceID= {}", serviceID);
		
		
		pollForReport();
		
	}



	
	
	
	
	
	
	private void pollForReport() {
		SdumServiceResultSet testSdumServiceResultSet = new SdumServiceResultSet();
		
		
		
		
		
		
		
		
		//Fill the QueryResult
		QueryResult queryResult = new QueryResult();
		Sparql sparql = new Sparql();
		Results sparqlResults = new Results();
				
		Result sparqlResult = new Result();
		
		Binding sparqlResultBinding = new Binding();
		
		
		sparqlResultBinding.setName("NameValue");
		
		Literal literalValue = new Literal();
		
		
		
		
		
		
		
		
		sparqlResultBinding.setLiteral(literalValue);
		
		sparqlResultBinding.setBnode("BnodeValue");
		
		sparqlResultBinding.setUri("UriValue");
		
		sparqlResult.getBinding().add(sparqlResultBinding);
		
				
		sparqlResults.getResult().add(sparqlResult);
			
		sparql.setResults(sparqlResults);
					
		queryResult.setSparql(sparql);
				
		testSdumServiceResultSet.setQueryResult(queryResult);
		
		
		
		
		
		
		
		
		//Fill the RequestPresentation
		RequestPresentation requestPresentation = new RequestPresentation();
		//Set Widget
		Widget widget = new Widget();
		widget.setWidgetID("graphNode_722933770218514");
		//Add PresentationAttr1
		PresentationAttr presentationAttr1 = new PresentationAttr();
		presentationAttr1.setName("Y_AXIS_LABEL");
		presentationAttr1.setValue("y axis");
		widget.getPresentationAttr().add(presentationAttr1);
		//Add PresentationAttr2
		PresentationAttr presentationAttr2 = new PresentationAttr();
		presentationAttr2.setName("X_AXIS_LABEL");
		presentationAttr2.setValue("x axis");
		widget.getPresentationAttr().add(presentationAttr2);
		//Add PresentationAttr3
		PresentationAttr presentationAttr3 = new PresentationAttr();
		presentationAttr3.setName("SERIES1_LABEL");
		presentationAttr3.setValue("series1s");
		widget.getPresentationAttr().add(presentationAttr3);
		//Add PresentationAttr4
		PresentationAttr presentationAttr4 = new PresentationAttr();
		presentationAttr4.setName("X_AXIS_TYPE");
		presentationAttr4.setValue("Number");
		widget.getPresentationAttr().add(presentationAttr4);
		//Add PresentationAttr5
		PresentationAttr presentationAttr5 = new PresentationAttr();
		presentationAttr5.setName("widgetClass");
		presentationAttr5.setValue("org.openiot.ui.request.commons.nodes.impl.vizualizers.LineChart1");
		widget.getPresentationAttr().add(presentationAttr5);
		
		requestPresentation.getWidget().add(widget);
		testSdumServiceResultSet.setRequestPresentation(requestPresentation);
	
		this.sdumServiceResultSet = testSdumServiceResultSet;
	}







	public SdumServiceResultSet getSdumServiceResultSet() {
		return sdumServiceResultSet;
	}

	
	
	
	

	
	
}
