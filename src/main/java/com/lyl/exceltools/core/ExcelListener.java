package com.lyl.exceltools.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lyl.exceltools.utils.StrUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author daibin
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelListener extends AnalysisEventListener<Map<Integer, String>> {
	/**
	 * 自定义用于暂时存储data
	 */
	private Map<String, ExcelData> dataMap = new LinkedHashMap<>();
	boolean nullFlag = false;
	boolean overFlag = false;

	/**
	 * 这个每一条数据解析都会来调用
	 */
	@Override
	public void invoke(Map<Integer, String> data, AnalysisContext context) {
		String sheetName = context.readSheetHolder().getSheetName();
		Integer rowIndex = context.readRowHolder().getRowIndex();
		String tableName = context.readWorkbookHolder().getFile().getName();
		String name = tableName.substring(0, tableName.lastIndexOf('.'));
		ExcelData excelData = dataMap.putIfAbsent(sheetName, new ExcelData(name, sheetName));
		if (excelData == null) {
			excelData = dataMap.get(sheetName);
		}
		ExcelData finalExcelData = excelData;

		ArrayList<String> tempAll = new ArrayList<>();
		data.forEach((k, v) -> tempAll.add(v));
		excelData.getAllList().add(tempAll);

		if (overFlag) {
			return;
		}
		switch (rowIndex) {
		case 0:
			if(finalExcelData.getHeadDefault().isEmpty()){
				data.forEach((k, v) -> finalExcelData.getHeadDefault().add(v));
			}
			break;
		case 1:
			if(finalExcelData.getHeadFolding().isEmpty()){
				data.forEach((k, v) -> finalExcelData.getHeadFolding().add(v));
			}
			break;
		case 2:
			if(finalExcelData.getHeadType().isEmpty()){
				data.forEach((k, v) -> finalExcelData.getHeadType().add(v));
			}
			break;
		case 3:
			if(finalExcelData.getHeadName().isEmpty()){
				data.forEach((k, v) -> finalExcelData.getHeadName().add(v));
			}else {
				if(!new ArrayList<>(data.values()).equals(finalExcelData.getHeadName())){
					throw new RuntimeException(tableName+"->"+sheetName+"与目录内相同sheet字段名不一致!!!");
				}
			}
			break;
		default:
			if (!allNull(data)) {
				if (nullFlag) {
					data.forEach((k, v) -> {
						if (StrUtil.isNotBlank(v)) {
							v = v.replaceAll("\\/\\*\\*", "").replaceAll("\\*\\/","");
						}
						finalExcelData.getAnnotations().add(v);
					});
					overFlag = true;
				} else {
					ArrayList<String> temp = new ArrayList<>();
					data.forEach((k, v) -> temp.add(v));
					excelData.getDataList().add(temp);
				}
			} else {
				nullFlag = true;
			}
		}
	}

	private boolean allNull(Map<Integer, String> data) {
		String temp = data.values().stream().filter(Objects::nonNull).findFirst().orElse(null);
		return temp == null;
	}

	/**
	 * 这里会一行行的返回头
	 */
	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		invoke(headMap, context);
	}

	/**
	 * 所有数据解析完成了 都会来调用
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		overFlag = false;
		nullFlag = false;
	}
}
