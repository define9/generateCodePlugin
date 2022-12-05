package cn.definen.utils;

import cn.definen.model.ClassInfo;
import cn.definen.model.FieldInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ContentUtil {

    public static void checkPath(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 检查文件, 筛选出 文件中没有的属性
     * @param file 文件
     * @param classInfo 类的描述信息
     * @return list
     */
    public static List<FieldInfo> checkFile(File file, ClassInfo classInfo) {
        List<FieldInfo> fields = classInfo.getFields();
        StringBuilder contentBuilder = new StringBuilder();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                contentBuilder.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String content = contentBuilder.toString();
        List<FieldInfo> needGenerateFieldInfos = new ArrayList<>();

        for (FieldInfo field : fields) {
            if (content.contains(field.getOrigin().getReadMethod().getName())) {
                continue;
            }
            needGenerateFieldInfos.add(field);
        }

        return needGenerateFieldInfos;
    }

    public static void insertContent(List<String> contents, File file) throws IOException {
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());

        int count = lines.size();

        while (count-- > 0) {
            String line = lines.get(count);
            if (line.contains("}")) {
                System.out.println("找到最后一个}，在 " + count);
                break;
            }
        }

        // 已找到需要插入代码的行号， 一行一行插入数据（保证格式, idea 代码格式化有点鸡肋， 一定注意格式）。 一个是--问题， 一个是list下标 0 开始
        lines.addAll(count, contents);

        // 将文件重新写入, 也可以删除最后几行， 然后一直调用append
        FileWriter writer = new FileWriter(file);
        writer.write("");
        for (String line : lines) {
            writer.append(line).append("\n");
        }
        writer.close();
    }

    public static void insertContent(String insertStr, File file) throws IOException {
        List<String> list = new ArrayList<>();
        list.add(insertStr);

        insertContent(list, file);
    }
}
