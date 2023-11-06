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

<#list imports as import>
import ${import};
</#list>
<#if isSameName>
import ${packageInfo}.${dashedToCamel(tableName)?cap_first}Info;
import java.util.ArrayList;
import java.util.List;
</#if>

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
<#if isSameName>
    "${dashedToCamel(tableName)}Info",
</#if>
<#list sheetNames as sheetName>
    "${dashedToCamel(sheetName)}"<#if sheetName_has_next>,</#if>
</#list>
})
@XmlRootElement(name = "${tableName}")
@Data
/**
* 自动生成代码 无须修改
*/
public class ${dashedToCamel(tableName)?cap_first} {
    <#if isSameName>
        @XmlElement(name = "${tableName}Info", required = true)
        protected List<${dashedToCamel(tableName)?cap_first}Info> ${dashedToCamel(tableName)}Info = new ArrayList<>();

    </#if>
    <#list sheetNames as sheetName>
        @XmlElement(name = "${sheetName}", required = true)
        protected ${dashedToCamel(sheetName)?cap_first} ${dashedToCamel(sheetName)};

    </#list>
}