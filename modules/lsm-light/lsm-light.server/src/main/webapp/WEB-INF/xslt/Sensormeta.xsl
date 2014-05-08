<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
   xmlns:uuid="uuid"
   xmlns:xalan="http://xml.apache.org/xalan"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xs = "http://www.w3.org/2001/XMLSchema"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
   xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
   xmlns:yweather="http://xml.weather.yahoo.com/ns/rss/1.0">
	<xsl:output method="text" media-type="text/plain"/>
	
<!-- 	<xsl:variable name="utc-timestamp" select="current-dateTime()"/> -->
	<xsl:param name="utc-timestamp"/>
	<xsl:param name="prefix"/>
	
	<xsl:param name="sensorId"/>
<!-- 	<xsl:param name="sourceURL"/> -->
<!-- 	<xsl:param name="sourceType"/> -->
	<xsl:param name="sensortype"/>
	<xsl:param name="name"/>
	<xsl:param name="author"/>
	
	<xsl:param name="placeId"/>
	<xsl:param name="lat"/>
	<xsl:param name="lng"/>
	<xsl:param name="geonameId"/>
	<xsl:param name="city"/>
	<xsl:param name="country"/>
	<xsl:param name="province"/>
	<xsl:param name="continent"/>
<!-- 	<xsl:param name="foi"/> -->
	
	<xsl:variable name="cityId" select="uuid:generateID()"/>
	<xsl:variable name="provinceId" select="uuid:generateID()"/>
	<xsl:variable name="countryId" select="uuid:generateID()"/>
	<xsl:variable name="regionId" select="uuid:generateID()"/>
	<xsl:variable name="continentId" select="uuid:generateID()"/>

	
	<xsl:template match="/">
		<xsl:call-template name="information"/>
		<xsl:call-template name="place"/>
<!-- 		<xsl:call-template name="featureOfInterest"/> -->
	</xsl:template>
	
	<xsl:template name="information">	
		<xsl:value-of select="concat('&#10;','&#60;',$sensorId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://purl.oclc.org/NET/ssnx/ssn#Sensor','&#62;. '),
	    			concat('&#60;',$sensorId,'&#62; ',
	    			'&#60;','http://purl.org/net/provenance/ns#PerformedAt','&#62; ',
	    			' &#34;',$utc-timestamp,'&#34;&#94;&#94;&#60;','http://www.w3.org/2001/XMLSchema#dateTime&#62;.'),	    	    	
	    			concat('&#60;',$sensorId,'&#62; ',
	    			'&#60;','http://www.w3.org/ns/prov#wasGeneratedBy','&#62; ','&#60;',$author,'&#62;','.'),
	    			concat('&#60;',$sensorId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ','&#34;',$name,'&#34;','.')   			
           			" separator="&#10;"/>
	</xsl:template>
			
<!-- 	<xsl:template name="featureOfInterest">		 -->
<!--          <xsl:value-of select="concat('&#10;','&#60;',$foi,'&#62; ', -->
<!-- 	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ', -->
<!-- 	    			'&#60;','http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest','&#62;.'), -->
<!--          			concat('&#60;',$foi,'&#62; ', -->
<!-- 	    			'&#60;','http://www.w3.org/2003/01/geo/wgs84_pos#lat','&#62; ', -->
<!-- 	    			'&#34;',$lat,'&#34;&#94;&#94;&#60;','http://www.w3.org/2001/XMLSchema#decimal&#62;.'), -->
<!-- 	    			concat('&#60;',$foi,'&#62; ', -->
<!-- 	    			'&#60;','http://www.w3.org/2003/01/geo/wgs84_pos#long','&#62; ', -->
<!-- 	    			'&#34;',$lng,'&#34;&#94;&#94;&#60;','http://www.w3.org/2001/XMLSchema#decimal&#62;.') -->
<!-- 	    			" separator="&#10;"/> -->
<!-- 	</xsl:template> -->

	<xsl:template name="place">
		<xsl:value-of select="concat('&#10;','&#60;',$sensorId,'&#62; ',
	    			'&#60;','http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation','&#62; ',
	    			'&#60;',$placeId,'&#62;. '),
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://www.loa-cnr.it/ontologies/DUL.owl#Place','&#62;.'),
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing','&#62;.'),
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://www.w3.org/2003/01/geo/wgs84_pos#lat','&#62; ',
	    			'&#34;',$lat,'&#34;&#94;&#94;&#60;','http://www.w3.org/2001/XMLSchema#decimal&#62;.'),
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://www.w3.org/2003/01/geo/wgs84_pos#long','&#62; ',
	    			'&#34;',$lng,'&#34;&#94;&#94;&#60;','http://www.w3.org/2001/XMLSchema#decimal&#62;.'),
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ',
	    			'&#34;',$city,'&#44;',$country,'&#34;.'),	    			
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://lsm.deri.ie/ont/lsm.owl#is_in_city','&#62; ',
	    			'&#60;',$prefix,$cityId,'&#62;.'),
	    			concat('&#60;',$prefix,$cityId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://linkedgeodata.org/ontology/City','&#62;.'),
	    			concat('&#60;',$prefix,$cityId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ','&#34;',$city,'&#34;.'),	    				
	    			concat('&#60;',$placeId,'&#62; ',	    			
	    			'&#60;','http://linkedgeodata.org/property/is_in_province','&#62; ',
					'&#60;',$prefix,$provinceId,'&#62;.'),
	    			concat('&#60;',$prefix,$provinceId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://linkedgeodata.org/ontology/Province','&#62;.'),
	    			concat('&#60;',$prefix,$provinceId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ','&#34;',$province,'&#34;.'),
	    			
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://linkedgeodata.org/property/is_in_country','&#62; ',
	    			'&#60;',$prefix,$countryId,'&#62;.'),
	    			concat('&#60;',$prefix,$countryId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://linkedgeodata.org/ontology/Country','&#62;.'),
	    			concat('&#60;',$prefix,$countryId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ','&#34;',$country,'&#34;.'),
	    			
	    			concat('&#60;',$placeId,'&#62; ',
	    			'&#60;','http://linkedgeodata.org/property/is_in_continent','&#62; ',
	    			'&#60;',$prefix,$continentId,'&#62;.'),
	    			concat('&#60;',$prefix,$continentId,'&#62; ',
	    			'&#60;','http://www.w3.org/1999/02/22-rdf-syntax-ns#type','&#62; ',
	    			'&#60;','http://linkedgeodata.org/ontology/Continent','&#62;.'),
	    			concat('&#60;',$prefix,$continentId,'&#62; ',
	    			'&#60;','http://www.w3.org/2000/01/rdf-schema#label','&#62; ','&#34;',$continent,'&#34;.')   			
           			" separator="&#10;"/>
	</xsl:template>
		
	<xsl:function name="uuid:generateID">
       <!-- generate unique ID -->
       <xsl:variable name="duration-from-1582" as="xs:dayTimeDuration" >
           <xsl:sequence select="current-dateTime() - xs:dateTime('1582-10-15T00:00:00.000Z')" />
       </xsl:variable> 
        <xsl:variable name="random-offset" as="xs:integer">
           <xsl:sequence select="uuid:next-nr() mod 10000"></xsl:sequence>
       </xsl:variable>
       <xsl:sequence select="concat(
           (days-from-duration($duration-from-1582) * 24 * 60 * 60 +
           hours-from-duration($duration-from-1582) * 60 * 60 +
           minutes-from-duration($duration-from-1582) * 60 +
           seconds-from-duration($duration-from-1582)) * 1000 * 10000 +
			$random-offset, uuid:get-id())" />  
   </xsl:function>
   <xsl:function name="uuid:next-nr" as="xs:integer">
       <xsl:variable name="node"><xsl:comment /></xsl:variable>
       <xsl:sequence select="xs:integer(replace(generate-id($node),
'\D', ''))" />
   </xsl:function>
   <!-- generates some kind of unique id -->
   <xsl:function name="uuid:get-id" as="xs:string">
       <xsl:sequence select="generate-id(uuid:_get-node())" />
   </xsl:function>
   <xsl:function name="uuid:_get-node"><xsl:comment /></xsl:function>
</xsl:stylesheet>
