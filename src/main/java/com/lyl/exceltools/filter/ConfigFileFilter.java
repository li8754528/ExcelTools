package com.lyl.exceltools.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 策划配置生成文件名过滤器
 * 
 * @author chub
 *
 */
public class ConfigFileFilter {

	private String[] includeSheetNameArr;

	private String xsdPath;

	private Set<String> includeFileNameSet = new HashSet<>();

	public ConfigFileFilter(String[] includeSheetNameArr, String xsdPath) {
		super();
		this.includeSheetNameArr = includeSheetNameArr;
		this.xsdPath = xsdPath;
		parseIncludeFileName();
		printAllIncludeFiles();
	}

	@SuppressWarnings("unchecked")
	private void parseIncludeFileName() {
		SAXReader saxReader = new SAXReader();
		Document xsdDoc = null;
		File xsdFile = new File(xsdPath);
		try {
			xsdDoc = saxReader.read(xsdFile);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("无法解析xsd文件:" + xsdPath);
		}

		Set<String> sheetInfoNameSet = new HashSet<>();
		
		for(String includeSheetName :includeSheetNameArr) {
			String simpleName = includeSheetName.replaceAll("_", "");
			includeFileNameSet.add(simpleName);
			String sheetInfoName = includeSheetName + "Info";
			sheetInfoNameSet.add(sheetInfoName);
			includeFileNameSet.add(sheetInfoName.replaceAll("_", ""));
			includeFileNameSet.add(xsdFile.getName().split("\\.")[0].replaceAll("_", ""));
		}

		// 下面使用xpath表达式解析文档,找出sheet里依赖的文件名

		Element rootElement = xsdDoc.getRootElement();
		List<Element> elementList = rootElement.selectNodes(".//xs:element[@name]");

		Set<String> allFileName = new HashSet<>();
		List<String> sheetField = new ArrayList<>();
		for (int i = 0; i < elementList.size(); i++) {
			Element element = elementList.get(i);
			String name = element.attribute("name").getValue();
			
			List<Element> nodes = element.selectNodes(".//xs:element[@ref]");
			if (nodes == null || nodes.isEmpty()) {
				continue;
			}

			boolean conflict = allFileName.add(name);
			if (conflict) {
				// throw new CommonException("已存在相同名称的属性或sheet :" + name);
			}

			if (!sheetInfoNameSet.remove(name)) {
				continue;
			}
			
			
			for (Element fieldEle : nodes) {
				sheetField.add(fieldEle.attributeValue("ref"));
			}
		}

		if (!sheetInfoNameSet.isEmpty()) {
			throw new RuntimeException(
					String.format("从%s中未找到%s", xsdFile.getName(), Arrays.toString(sheetInfoNameSet.toArray())));
		}

		for (String fieldName : sheetField) {
			if (!allFileName.contains(fieldName)) {
				continue;
			}

			includeFileNameSet.add(fieldName);
		}
	}

	public void printAllIncludeFiles() {
		System.out.println(String.format("解析出sheet=%s 所包含的文件有:%s", Arrays.toString(includeSheetNameArr),
				Arrays.toString(includeFileNameSet.toArray())));
	}

	public boolean includeFile(String fileName) {
		for (String name : includeFileNameSet) {
			if (name.equalsIgnoreCase(fileName)) {
				return true;
			}
		}

		return false;
	}

	public static void main(String[] args) {
		ConfigFileFilter f = new ConfigFileFilter(new String[] {"config_fish_mermaid_events","config_fish_base"},
				"D:\\hf_h5_workspace\\trunk\\hf-config\\src\\main\\java\\com\\cxx\\hf\\config\\xsd\\config_fish.xsd");
		f.printAllIncludeFiles();
	}
}
