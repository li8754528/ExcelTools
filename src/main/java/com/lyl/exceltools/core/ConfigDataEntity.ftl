<#function dashedToCamel(s)>
    <#return s
    ?replace('(^_+)|(_+$)', '', 'r')
    ?replace('\\_+(\\w)?', ' $1', 'r')
    ?replace('([A-Z])', ' $1', 'r')
    ?capitalize
    ?replace(' ' , '')
    ?uncap_first
    >
</#function>

package ${package};

import lombok.Data;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
<#list rawInfo.fields as field>
    "${field?uncap_first}"<#if field_has_next>,</#if>
</#list>
})
@XmlRootElement(name = "${rawInfo.headName}")
@Data
/**
* 自动生成代码 无须修改
*/
public class ${rawInfo.headName?cap_first} {
<#list rawInfo.fields as field>
    @XmlElement(required = true)
    protected BigDecimal ${field?uncap_first};

</#list>
}