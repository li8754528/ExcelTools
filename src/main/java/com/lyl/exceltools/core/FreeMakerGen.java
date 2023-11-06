package com.lyl.exceltools.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lyl.exceltools.utils.FileUtil;
import com.lyl.exceltools.utils.StrUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author daibin
 */
public class FreeMakerGen {
    public static List<String> constWorld = Arrays.asList("private", "protected", "public", "abstract", "class", "extends", "final", "implements", "interface",
            "native", "new", "static", "strictfp", "synchronized", "transient", "volatile", "break", "continue", "return", "do", "while", "if", "else", "for",
            "instanceof", "switch", "case", "default", "try", "catch", "throw", "throws", "import", "package", "boolean", "byte", "char", "double", "float",
            "int", "long", "short", "null", "true", "false", "super", "this", "void", "goto", "const");

    private String basePackage;
    private String basePath;
    public List<String> subPackages = new ArrayList<>();

    public FreeMakerGen(String basePackage, String basePath) {
        this.basePackage = basePackage;
        this.basePath = basePath;
    }

    public void genTableClass(String tableName, Collection<String> sheetNames, boolean isSameName, String sameSeetName) {
        HashMap<String, Object> root = new HashMap<>();

        root.put("sheetNames", sheetNames);
        root.put("tableName", tableName);
        root.put("package", getPackageName(basePackage, tableName));
        root.put("packageInfo", getPackageName(getPackageName(basePackage, tableName), sameSeetName));
        root.put("imports", subPackages);
        root.put("isSameName", isSameName);
        print("ConfigTable.ftl", root);
        fprint("ConfigTable.ftl", root, getPackagePath(basePath, tableName), StrUtil.upperFirst(toCamelCase(tableName)) + ".java");
    }

    public void genSheetClass(String tableName, String sheetName) {
        HashMap<String, Object> root = new HashMap<>();

        root.put("sheetName", sheetName);
        String packageName = getPackageName(getPackageName(basePackage, tableName), sheetName);
        root.put("package", packageName);
        String className = StrUtil.upperFirst(toCamelCase(sheetName));
        root.put("className", className);

        subPackages.add(packageName + "." + className);

        print("ConfigSheet.ftl", root);
        fprint("ConfigSheet.ftl", root, getPackagePath(getPackagePath(basePath, tableName), sheetName), className + ".java");
    }

    public static String toCamelCase(CharSequence name) {
        if (null == name) {
            return null;
        }
        final String name2 = name.toString();
        if (name2.contains("_")) {
            final int length = name2.length();
            final StringBuilder sb = new StringBuilder(length);
            boolean upperCase = false;
            for (int i = 0; i < length; i++) {
                char c = name2.charAt(i);

                if (c == StrUtil.UNDERLINE) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    public void genDataLine(String tableName, String sheetName, List<RawInfo> rawInfo) {
        HashMap<String, Object> root = new HashMap<>();

        root.put("sheetName", sheetName);
        root.put("package", getPackageName(getPackageName(basePackage, tableName), sheetName));
        root.put("rawInfo", rawInfo);
        String className = StrUtil.upperFirst(toCamelCase(sheetName));
        root.put("className", className);
        print("ConfigDataLine.ftl", root);
        fprint("ConfigDataLine.ftl", root, getPackagePath(getPackagePath(basePath, tableName), sheetName), className + "Info.java");

    }

    public void genDataEntity(List<RawInfo> rawInfo) {
        for (RawInfo info : rawInfo) {
            if (info.isObject()) {
                String tableName = info.getTableName();
                String sheetName = info.getSheetName();
                HashMap<String, Object> root = new HashMap<>();
                root.put("sheetName", sheetName);
                root.put("package", getPackageName(getPackageName(basePackage, tableName), sheetName));
                root.put("rawInfo", info);
                print("ConfigDataEntity.ftl", root);
                fprint("ConfigDataEntity.ftl", root, getPackagePath(getPackagePath(basePath, tableName), sheetName),
                        StrUtil.upperFirst(info.getHeadName()) + ".java");
            }
        }

    }

    private static String getPackageName(String basePackage, String tableName) {
        String[] s = tableName.split("_");
        StringBuilder basePackageBuilder = new StringBuilder(basePackage);
        int index = 1;
        if (s.length > 2) {
            String[] arr = basePackage.split("\\.");
            if (s[1].equalsIgnoreCase(arr[arr.length - 1])) {
                index = 2;
            }
        }
        for (; index < s.length; index++) {
            String tempName = checkStr(s[index]);
            basePackageBuilder.append(".").append(tempName.toLowerCase());
        }
        return basePackageBuilder.toString();
    }

    private static String getPackagePath(String basePackage, String tableName) {
        String[] s = tableName.split("_");
        StringBuilder basePackageBuilder = new StringBuilder(basePackage);
        int index = 1;
        if (s.length > 2) {
            String[] arr = basePackage.split("/");
            if (s[1].equalsIgnoreCase(arr[arr.length - 1])) {
                index = 2;
            }
        }
        for (; index < s.length; index++) {
            String tempName = checkStr(s[index]);
            basePackageBuilder.append("/").append(tempName.toLowerCase());
        }
        return basePackageBuilder.toString();
    }

    public static String checkStr(String srcStr) {
        String tempStr = srcStr;
        if (constWorld.contains(tempStr)) {
            tempStr += "_";
        } else if (StrUtil.isNumeric(tempStr)) {
            tempStr = "_" + tempStr;
        }
        return tempStr;
    }

    /**
     * 获取模板
     * @param name
     * @return
     */
    public Template getTemplate(String name) {
        try {
            // 通过FreeMarker的Configuration读取相应的ftl
            Configuration cfg = new Configuration();
            // 设定去哪里读取相应的ftl模板文件
            cfg.setClassForTemplateLoading(this.getClass(), "/com/cxx/hf/config/excel/");
            cfg.setDefaultEncoding("UTF-8");
            // 在模板文件目录中找到名称为name的文件
            Template temp = cfg.getTemplate(name);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 输出到控制台
     * @param name 模板文件名
     * @param root
     */
    public void print(String name, Map<String, Object> root) {
//		try {
//			// 通过Template可以将模板文件输出到相应的流
//			Template temp = this.getTemplate(name);
//			temp.process(root, new PrintWriter(System.out));
//		} catch (TemplateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }

    /**
     * 输出到文件
     * @param name
     * @param root
     */
    public void fprint(String name, Map<String, Object> root, String filePath, String fileName) {
        FileWriter out = null;
        try {
            // 通过一个文件输出流，就可以写到相应的文件中
            try {
                String path = filePath;
                FileUtil.mkDir(path);
                File file = new File(path + "/" + fileName);
                out = new FileWriter(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Template temp = this.getTemplate(name);
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
