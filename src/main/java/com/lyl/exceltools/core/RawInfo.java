package com.lyl.exceltools.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.lyl.exceltools.utils.StrUtil;

import lombok.Data;

/**
 * @author daibin
 */
@Data
public class RawInfo {
    private String tableName;
    private String sheetName;
    private String headDefault;
    private String headFolding;
    private String headType;
    private String headName;
    private String headNameFix;
    private String annotations;
    private List<String> fields;
    private List<String> data;
    private boolean isObject = false;

    public static List<RawInfo> loadFormExcelData(ExcelData excelData) {

        List<String> headNames = excelData.getHeadName();
        List<String> headTypes = excelData.getHeadType();
        if (headTypes.size() < headNames.size()) {
            for (int i = 0; i < headNames.size() - headTypes.size(); i++) {
                headTypes.add(null);
            }
        }
        List<String> annotations = excelData.getAnnotations();
        if (annotations.size() < headNames.size()) {
            for (int i = 0; i < headNames.size() - annotations.size(); i++) {
                annotations.add(null);
            }
        }
        List<String> headDefaults = excelData.getHeadDefault();
        if (headDefaults.size() < headNames.size()) {
            for (int i = 0; i < headNames.size() - headDefaults.size(); i++) {
                headDefaults.add(null);
            }
        }
        List<String> headFoldings = excelData.getHeadFolding();
        if (headFoldings.size() < headNames.size()) {
            for (int i = 0; i < headNames.size() - headFoldings.size(); i++) {
                headFoldings.add(null);
            }
        }
        List<List<String>> dataList = excelData.getDataList();

        List<RawInfo> res = new ArrayList<>();
        for (int i = 0; i < headNames.size(); i++) {
            String headName = headNames.get(i);
            if (StrUtil.isBlank(headName)) {
                continue;
            }
            RawInfo temp = new RawInfo();
            temp.setTableName(excelData.getTableName());
            temp.setSheetName(excelData.getSheetName());
            // 特殊处理第一列
            if (i == 0) {
                temp.setHeadName("uniqueId");
                temp.setHeadNameFix("uniqueId");
                temp.setAnnotations("唯一id");
                temp.setHeadType("i");
            } else {
                temp.setHeadName(headName);
                temp.setHeadNameFix(FreeMakerGen.checkStr(headName));
                if (annotations.size() > i) {
                    temp.setAnnotations(annotations.get(i));
                } else {
                    temp.setAnnotations("");
                }
                temp.setHeadType(headTypes.get(i));
            }
            temp.setHeadFolding(headFoldings.get(i));
            temp.setHeadDefault(headDefaults.get(i));
            int finalI = i;
            List<String> collect = dataList.stream().map(o -> {
                if (finalI < o.size()) {
                    return o.get(finalI);
                } else {
                    return null;
                }
            }).collect(Collectors.toList());
            temp.setData(collect);

            temp.checkFields();

            res.add(temp);
        }
        return res;
    }

    private void checkFields() {
        if ("d".equals(this.headType) || "ad".equals(this.headType)) {
            fields = new ArrayList<>();
            fields.add("key");
            fields.add("value");
            isObject = true;
        }
        if ("dc".equals(this.headType) || "adc".equals(this.headType)) {
            HashSet<String> set = new HashSet();
            for (String line : data) {
                if (StrUtil.isNotBlank(line)) {
                    String[] out = line.split(";");
                    for (String in : out) {
                        String[] single = in.split(",");
                        for (String field : single) {
                            String s = field.split(":")[0];
                            s = FreeMakerGen.checkStr(s);
                            set.add(s);
                        }
                    }
                }
            }
            fields = new ArrayList<>();
            fields.addAll(set);
            isObject = true;
        }
    }
}
