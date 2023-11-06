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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "${dashedToCamel(sheetName)}Info"
})
@XmlRootElement(name = "${sheetName}")
@Data
/**
* 自动生成代码 无须修改
*/
public class ${dashedToCamel(sheetName)?cap_first} {
        @XmlElement(name = "${sheetName}Info", required = true)
        protected List<${dashedToCamel(sheetName)?cap_first}Info> ${dashedToCamel(sheetName)}Info = new ArrayList<>();
}