sensorName=${sensorname}
source="${source}"
sourceType=${sourcetype}
sensorType="${sensortype}"
information=${information}
author=${authors}
feature="${featureofinterest}"
fields="<#list fields as field><#if field_has_next>${field.gsnFieldName},<#else>${field.gsnFieldName}</#if></#list>"
<#list fields as field>
field.${field.gsnFieldName}.propertyName="${field.lsmPropertyName}"
field.${field.gsnFieldName}.unit="${field.lsmUnit}"
</#list>
latitude="${latitude}"
longitude="${longitude}"
sensorID="${sensorID}"