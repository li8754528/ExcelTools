package com.lyl.exceltools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.lyl.exceltools.core.ExcelData;
import com.lyl.exceltools.core.ExcelListener;
import com.lyl.exceltools.core.FreeMakerGen;
import com.lyl.exceltools.core.RawInfo;
import com.lyl.exceltools.utils.FileUtil;

/**
 * 直接将Excel文件导出XML和Class
 * @author daibin
 */
public class GenExcelToClass {
    public static void main(String[] args) {
        String basePackage, basePath, baseExcelPath, baseXmlPath;
        // 包的根目录
        basePackage = "com.cxx.hf.config.matrix";
        if (args.length == 0) {
            String dir = System.getProperty("user.dir");
            if (dir.endsWith("hf-parent")) {
                dir = dir + File.separator + "hf-config";
            }
            // 代码位置
            basePath = dir + File.separator + "src\\main\\java\\com\\cxx\\hf\\config\\matrix";
            // Excel路径
            baseExcelPath = "D:\\Works\\yjzrby_excel\\data\\excel\\";
            // xml路径
            baseXmlPath = "D:\\Works\\yjzrby_excel\\data\\xml\\";
        } else {
            // 代码位置
            basePath = args[0] + "\\hf-parent\\hf-config\\src\\main\\java\\com\\cxx\\hf\\config\\matrix";
            // Excel路径
            baseExcelPath = args[1];
            // xml路径
            baseXmlPath = args[2];
        }

        File file = new File(baseExcelPath);
        if (!file.isDirectory()) {
            return;
        }
        //excel目录的所有文件
        File[] lsBase = file.listFiles();
        //excel目录下的目录内所有文件合并成同一个xml
        List<File> dirs = new ArrayList<>();
        for (File excel : lsBase) {
            if (excel.isFile()) {
                String name = excel.getName();
                if (name.toLowerCase().endsWith(".xlsx")) {
                    String tableName = name.substring(0, name.lastIndexOf("."));
                    String fileName = excel.getAbsolutePath();
                    doGen(basePackage, basePath, tableName, fileName, baseXmlPath);
                }
            } else if (excel.isDirectory()) {
                dirs.add(excel);
            }
        }

        for (File dir : dirs) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                doGenDir(basePackage, basePath, dir.getName(), files, baseXmlPath);
            }
        }

    }

    /***
     * 同一目录生成一个xml文件
     * @param basePackage
     * @param basePath
     * @param tableName
     * @param files
     * @param baseXmlPath
     */
    private static void doGenDir(String basePackage, String basePath, String tableName, File[] files, String baseXmlPath) {
        ExcelListener excelListener = new ExcelListener();
        FreeMakerGen gen = new FreeMakerGen(basePackage, basePath);
        Set<String> sheetNames = new TreeSet<>();
        for (File excel : files) {
            if (excel.isFile()) {
                String name = excel.getName();
                if (name.toLowerCase().endsWith(".xlsx") && name.startsWith("config")) {
                    ExcelReader excelReader = EasyExcel.read(excel.getAbsolutePath(), excelListener).ignoreEmptyRow(false).build();
                    List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
                    System.out.println("开始解析:" + tableName + File.separator + name);
                    for (ReadSheet sheet : sheets) {
                        String sheetName = sheet.getSheetName();
                        if (sheetName.startsWith("config")) {
                            excelReader.read(sheet);
                            sheetNames.add(sheet.getSheetName());
                        }
                    }
                }
            }
        }

        // 每一行数据
        Map<String, ExcelData> dataMap = excelListener.getDataMap();

        genXml(baseXmlPath, tableName, dataMap);

        AtomicBoolean isSameName = new AtomicBoolean(false);
        AtomicReference<String> sameSheetName = new AtomicReference<>("");
        dataMap.forEach((k, v) -> {
            List<RawInfo> rawInfos = RawInfo.loadFormExcelData(v);
            gen.genDataLine(tableName, k, rawInfos);
            // 如果表名等于sheet名不生成line 对应代码移动到table里
            if (!tableName.equalsIgnoreCase(k)) {
                gen.genSheetClass(tableName, k);
            } else {
                sameSheetName.set(k);
                isSameName.set(true);
            }
            gen.genDataEntity(rawInfos);
        });
        // 生成table 类
        gen.genTableClass(tableName, sheetNames, isSameName.get(), sameSheetName.get());
    }


    private static void doGen(String basePackage, String basePath, String tableName, String fileName, String baseXmlPath) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = EasyExcel.read(fileName, excelListener).ignoreEmptyRow(false).build();
        List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
        System.out.println("开始解析" + tableName);
        for (ReadSheet sheet : sheets) {
            excelReader.read(sheet);
        }

        FreeMakerGen gen = new FreeMakerGen(basePackage, basePath);

        // 每一行数据
        Map<String, ExcelData> dataMap = excelListener.getDataMap();

        genXml(baseXmlPath, tableName, dataMap);

        AtomicBoolean isSameName = new AtomicBoolean(false);
        AtomicReference<String> sameSheetName = new AtomicReference<>("");
        dataMap.forEach((k, v) -> {
            List<RawInfo> rawInfos = RawInfo.loadFormExcelData(v);
            gen.genDataLine(tableName, k, rawInfos);
            // 如果表名等于sheet名不生成line 对应代码移动到table里
            if (!tableName.toLowerCase().replaceAll("_", "").equals(k.toLowerCase().replaceAll("_", ""))) {
                gen.genSheetClass(tableName, k);
            } else {
                sameSheetName.set(k);
                isSameName.set(true);
            }
            gen.genDataEntity(rawInfos);

        });
        // 生成table 类
        gen.genTableClass(tableName, sheets.stream().map(ReadSheet::getSheetName).collect(Collectors.toList()), isSameName.get(), sameSheetName.get());
    }

    private static void genXml(String baseXmlPath, String tableName, Map<String, ExcelData> dataMap) {
        try {
            String pathname = baseXmlPath + tableName + ".xml";
            FileUtil.createFile(new File(pathname));

            // 创建 XmlMapper 对象
            XmlMapper xmlMapper = XmlMapper.builder().defaultUseWrapper(true).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                    .configure(SerializationFeature.WRAP_ROOT_VALUE, true).enable(SerializationFeature.INDENT_OUTPUT).build();
            // 创建 ObjectNode 对象，用于表示 XML 数据结构

            ObjectNode tableNode = JsonNodeFactory.instance.objectNode();

            dataMap.forEach((k, v) -> {
                ObjectNode sheetNode = tableNode.putObject(k);
                List<RawInfo> rawInfos = RawInfo.loadFormExcelData(v);
                System.out.println("创建XML:" + pathname + "-->" + k);
                RawInfo temp = rawInfos.get(0);
                if (temp.getData().size() > 0) {
                    ArrayNode lineNode = JsonNodeFactory.instance.arrayNode();
                    for (int i = 0; i < temp.getData().size(); i++) {
                        int finalI = i;
                        ObjectNode subNode = JsonNodeFactory.instance.objectNode();
                        rawInfos.forEach(rawInfo -> {
                            boolean isArray = rawInfo.getHeadType().startsWith("a");
                            boolean isObj = rawInfo.getFields() != null;

                            if (isObj) {
                                if ("ad".equals(rawInfo.getHeadType()) || "d".equals(rawInfo.getHeadType())) {
                                    ArrayNode subArray = JsonNodeFactory.instance.arrayNode();
                                    String dataTemp = rawInfo.getData().get(finalI);
                                    if (dataTemp != null) {
                                        String[] split = dataTemp.split(";");
                                        for (String s : split) {
                                            ObjectNode splitInnerNode = JsonNodeFactory.instance.objectNode();
                                            String[] splitInner = s.split(":");
                                            splitInnerNode.put("key", splitInner[0]);
                                            splitInnerNode.put("value", splitInner[1]);
                                            subArray.add(splitInnerNode);
                                        }
                                        subNode.set(rawInfo.getHeadName(), subArray);
                                    } else {
                                        subNode.putObject(rawInfo.getHeadName());
                                    }
                                } else if ("adc".equals(rawInfo.getHeadType()) || "dc".equals(rawInfo.getHeadType())) {
                                    ArrayNode subArray = JsonNodeFactory.instance.arrayNode();
                                    String dataTemp = rawInfo.getData().get(finalI);
                                    if (dataTemp != null) {
                                        String[] split = dataTemp.split(";");
                                        for (String value : split) {
                                            String[] splitInner = value.split(",");
                                            ObjectNode splitInnerNode = JsonNodeFactory.instance.objectNode();
                                            for (String s : splitInner) {
                                                String[] splitKeyValue = s.split(":");
                                                splitInnerNode.put(splitKeyValue[0], splitKeyValue[1]);
                                            }
                                            subArray.add(splitInnerNode);
                                        }
                                    }
                                    subNode.set(rawInfo.getHeadName(), subArray);
                                }
                            } else {
                                if (isArray) {
                                    ArrayNode subArray = JsonNodeFactory.instance.arrayNode();
                                    String dataTemp = rawInfo.getData().get(finalI);
                                    if (dataTemp != null) {
                                        String[] split = dataTemp.split(",");
                                        for (String s : split) {
                                            subArray.add(s);
                                        }
                                    }
                                    subNode.set(rawInfo.getHeadName(), subArray);
                                } else {
                                    String tempValue = rawInfo.getData().get(finalI);
                                    if (tempValue == null) {
                                        if ("i".equals(rawInfo.getHeadType())) {
                                            tempValue = rawInfo.getHeadDefault();
                                        }
                                    }
                                    subNode.put(rawInfo.getHeadName(), tempValue);
                                }
                            }
                        });
                        lineNode.add(subNode);
                    }
                    sheetNode.set(k + "Info", lineNode);
                }
            });
            xmlMapper.writer().withRootName(tableName).writeValue(new File(pathname), tableNode);
            System.out.println("xml文件生成成功 = " + pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
