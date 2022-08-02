package org.liujiazhen.bing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件操作工具类
 */
public class FileUtils {

    private static final Path README_PATH = Paths.get("README.md");
    private static final Path BING_PATH = Paths.get("bing-wallpaper.md");

    private static final Path MONTH_PATH = Paths.get("picture/");

    private static final String GITHUB_URL = "https://github.com/liujiazhen";

    /**
     * 读取 bing-wallpaper.md
     */
    public static List<Images> readBing() throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        List<String> allLines = Files.readAllLines(BING_PATH);
        allLines = allLines.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<Images> imgList = new ArrayList<>();
        imgList.add(new Images());
        for (int i = 1; i < allLines.size(); i++) {
            String s = allLines.get(i).trim();
            int descEnd = s.indexOf("]");
            int urlStart = s.lastIndexOf("(") + 1;
            int titleStart = s.indexOf("\"");
            int titleEnd = s.lastIndexOf("\"");

            String date = s.substring(0, 10);
            String desc = s.substring(14, descEnd);
            String url;
            String title;
            if (titleStart != -1 && titleEnd != titleStart) {
                url = s.substring(urlStart, titleStart - 1);
                title = s.substring(titleStart + 1, titleEnd - 1);
            } else {
                url = s.substring(urlStart, s.length() - 1);
                title = "";
            }
            imgList.add(new Images(desc, date, url, title));
        }
        return imgList;
    }

    /**
     * 写入 bing-wallpaper.md
     */
    public static void writeBing(List<Images> imgList) throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        Files.write(BING_PATH, "## Bing Wallpaper".getBytes());
        Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        for (Images images : imgList) {
            Files.write(BING_PATH, images.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
            Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            Files.write(BING_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 读取 README.md
     */
    public static List<Images> readReadme() throws IOException {
        if (!Files.exists(README_PATH)) {
            Files.createFile(README_PATH);
        }
        List<String> allLines = Files.readAllLines(README_PATH);
        List<Images> imgList = new ArrayList<>();
        for (int i = 3; i < allLines.size(); i++) {
            String content = allLines.get(i);
            Arrays.stream(content.split("\\|"))
                    .filter(s -> !s.isEmpty())
                    .map(s -> {
                        int dateStartIndex = s.indexOf("[", 3) + 1;
                        int urlStartIndex = s.indexOf("(", 4) + 1;
                        String date = s.substring(dateStartIndex, dateStartIndex + 10);
                        String url = s.substring(urlStartIndex, s.length() - 1);
                        return new Images(null, date, url);
                    })
                    .forEach(imgList::add);
        }
        return imgList;
    }

    /**
     * 写入 README.md
     */
    public static void writeReadme(List<Images> imgList) throws IOException {
        if (!Files.exists(README_PATH)) {
            Files.createFile(README_PATH);
        }
        List<Images> imagesList = imgList.subList(0, 30);
        FileUtils.writeFile(README_PATH, imagesList, null);

        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        // 归档
        Files.write(README_PATH, "### 历史归档：".getBytes(), StandardOpenOption.APPEND);
        Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        List<String> dateList = imgList.stream().map(Images::getDate).map(date -> date.substring(0, 7)).distinct().collect(Collectors.toList());
        int i = 0;
        for (String date : dateList) {
            String link = String.format("[%s](https://github.com/liujiazhen/bing-wallpaper/tree/main/picture/%s/) | ", date, date);
            Files.write(README_PATH, link.getBytes(), StandardOpenOption.APPEND);
            i++;
            if (i % 8 == 0) {
                Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
        }
    }


    /**
     * 按月份写入图片信息
     */
    public static void writeMonthInfo(List<Images> imgList) throws IOException {
        Map<String, List<Images>> monthMap = new LinkedHashMap<>();
        for (Images images : imgList) {
            String key = images.getDate().substring(0, 7);
            if (monthMap.containsKey(key)) {
                monthMap.get(key).add(images);
            } else {
                ArrayList<Images> list = new ArrayList<>();
                list.add(images);
                monthMap.put(key, list);
            }
        }

        for (String key : monthMap.keySet()) {
            Path path = MONTH_PATH.resolve(key);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            path = path.resolve("README.md");
            writeFile(path, monthMap.get(key), key);
        }
    }

    private static void writeFile(Path path, List<Images> imagesList, String name) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        String title = "## Bing Wallpaper";
        if (name != null) {
            title = "## Bing Wallpaper (" + name + ")";
        }
        Files.write(path, title.getBytes());
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, imagesList.get(0).toLarge().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "|      |      |      |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        Files.write(path, "| :----: | :----: | :----: |".getBytes(), StandardOpenOption.APPEND);
        Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        int i = 1;
        for (Images images : imagesList) {
            Files.write(path, ("|" + images.toString()).getBytes(), StandardOpenOption.APPEND);
            if (i % 3 == 0) {
                Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
                Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
            i++;
        }
        if (i % 3 != 1) {
            Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
        }
    }

}
