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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
<#list rawInfo as raw>
    "${raw.headNameFix?uncap_first}"<#if raw_has_next>,</#if>
</#list>
})
@XmlRootElement(name = "${sheetName}Info")
@Data
/**
* 自动生成代码 无须修改
*/
public class ${dashedToCamel(sheetName)?cap_first}Info {
<#list rawInfo as raw>
    /**
    * ${raw.annotations!raw.headName!''}
    */
    @XmlElement(name = "${raw.headName}",required = true)
<#if (raw.headType!'') == "i">
    protected BigInteger ${raw.headNameFix?uncap_first};
</#if>
<#if (raw.headType!'') == "f">
    protected BigDecimal ${raw.headNameFix?uncap_first};
</#if>
<#if (raw.headType!'') == "s">
    protected String ${raw.headNameFix?uncap_first};
</#if>
<#if (raw.headType!'') == "ai">
    protected List<BigInteger> ${raw.headNameFix?uncap_first} = new ArrayList<>();
</#if>
<#if (raw.headType!'') == "af">
    protected List<BigDecimal> ${raw.headNameFix?uncap_first} = new ArrayList<>();
</#if>
<#if (raw.headType!'') == "as">
    protected List<String> ${raw.headNameFix?uncap_first} = new ArrayList<>();
</#if>
<#if (raw.headType!'') == "d">
    protected ${raw.headNameFix?cap_first} ${raw.headNameFix?uncap_first};
</#if>
<#if (raw.headType!'') == "dc">
    protected ${raw.headNameFix?cap_first} ${raw.headNameFix?uncap_first};
</#if>
<#if (raw.headType!'') == "ad">
    protected List<${raw.headNameFix?cap_first}> ${raw.headNameFix?uncap_first} = new ArrayList<>();
</#if>
<#if (raw.headType!'') == "adc">
    protected List<${raw.headNameFix?cap_first}> ${raw.headNameFix?uncap_first} = new ArrayList<>();
</#if>
        
</#list>
}