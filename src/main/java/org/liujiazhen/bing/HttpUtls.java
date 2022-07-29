package org.liujiazhen.bing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络请求操作工具类
 */
public class HttpUtls {

    /**
     * 获取 HTTP 连接
     */
    public static HttpURLConnection getHttpUrlConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) httpUrl.openConnection();
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        return httpConnection;
    }

    /**
     * 请求指定 URL 返回内容
     */
    public static String getHttpContent(String url) throws IOException {
        HttpURLConnection httpUrlConnection = getHttpUrlConnection(url);
        StringBuilder stringBuilder = new StringBuilder();
        // 获得输入流
        try (InputStream input = httpUrlConnection.getInputStream(); BufferedInputStream bis = new BufferedInputStream(input)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, len));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpUrlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

}