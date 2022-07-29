package org.liujiazhen.bing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wallpaper {
    private static final Logger logger = Logger.getLogger("MyLogging");
    // BING API
    private final static String BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160";

    private final static String BING_URL = "https://cn.bing.com";

    public static void main(String[] args) throws IOException {
        String httpContent = HttpUtls.getHttpContent(BING_API);
        JSONObject jsonObject = JSON.parseObject(httpContent);

        JSONArray jsonArray = jsonObject.getJSONArray("images");

        jsonObject = (JSONObject) jsonArray.get(0);
        // 图片地址
        String url = BING_URL + jsonObject.get("url");
        url = url.substring(0, url.indexOf("&"));

        // 图片时间
        String enddate = (String) jsonObject.get("enddate");
        LocalDate localDate = LocalDate.parse(enddate, DateTimeFormatter.BASIC_ISO_DATE);
        enddate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 图片版权
        String copyright = (String) jsonObject.get("copyright");
        String title = jsonObject.getString("title");

        List<Images> imagesList = FileUtils.readBing();

        final String finalEndDate = enddate;
        Stream<Images> imagesStream = imagesList.stream().filter(v -> finalEndDate.equals(v.getDate()));
        if (imagesStream.findAny().isPresent()) {
            logger.warning("当前日期信息已存在");
            return;
        }

        imagesList.set(0, new Images(copyright, enddate, url, title));
        imagesList = imagesList.stream().distinct().collect(Collectors.toList());

        FileUtils.writeBing(imagesList);
        FileUtils.writeReadme(imagesList);
        FileUtils.writeMonthInfo(imagesList);
    }

}