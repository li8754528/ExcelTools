package com.lyl.exceltools.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
public class ExcelData {
	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * sheet名
	 */
	private String sheetName;
	/**
	 * 2维数据
	 */
	private List<List<String>> dataList = new ArrayList<>();

	/**
	 * 所有数据
	 */
	private List<List<String>> allList = new ArrayList<>();

	/**
	 * 固定第1行 表头 默认值
	 */
	private List<String> headDefault = new ArrayList<>();
	/**
	 * 固定第2行 表头 用作注释 dc/adc可以填格式
	 */
	private List<String> headFolding = new ArrayList<>();
	/**
	 * 固定第3行 表头 类型
	 */
	private List<String> headType = new ArrayList<>();
	/**
	 * 固定第4行 表头 字段名(第一个为uniqueId)
	 */
	private List<String> headName = new ArrayList<>();

	/**
	 * 注解
	 */
	private List<String> annotations = new ArrayList<>();

	public ExcelData(String tableName, String sheetName) {
		this.tableName = tableName;
		this.sheetName = sheetName;
	}
}
