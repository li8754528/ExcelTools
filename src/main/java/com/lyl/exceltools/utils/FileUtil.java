package com.lyl.exceltools.utils;

import java.io.File;

/**
 * @author liyongliang
 * @date 2023/10/30 17:44
 */
public class FileUtil {

    public static void createFile(File file) {
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static File mkDir(String fileDir) {
        return mkDir(new File(fileDir));
    }

    public static File mkDir(File file) {
        if (!file.getParentFile().exists()) {
            mkDir(file.getParentFile());
        }
        file.mkdir();
        return file;
    }
}
