package com.asr;

import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

/**
 * oyh
 * 2020.9.1
 * 阿里接口的asr服务调用案例。
 */
public class FileTransRESTfulDemo {
    /**
     * 常量字段
     */
    private static final String KEY_APPKEY = "appkey";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_FILE_LINK = "file_link";
    private static final String KEY_HEADER = "header";
    private static final String KEY_PAYLOAD = "payload";
    private static final String KEY_STATUS_MESSAGE = "status_message";
    private static final String KEY_TASK_ID = "task_id"; private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_SUCCESS_WITH_NO_VALID_FRAGMENT = "SUCCE SS_WITH_NO_VALID_FRAGMENT";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_QUEUEING = "QUEUEING";

    private String appkey;
    private String token;

    /**
     * 带参数的构造函数
     * @param appkey
     * @param token
     */
    public FileTransRESTfulDemo(String appkey, String token) {
        this.appkey = appkey; this.token = token;
    }

    public String submitFileTransRequest(String url, String fileLink) {
        /**
         * 设置http post请求
         * 1.使用http协议
         * 2.录音文件识别服务域名：您的gateway的域名 ip:port
         * 3.录音文件识别请求路径： /stream/vi/filetrans
         * 4.设置必须请求参数：appkey,token,file_link
         * 5.可选请求参数：auto_split,enable_callback,callback_url等
         */
        JSONObject taskObject = new JSONObject();
        taskObject.put(KEY_APPKEY, appkey);
        taskObject.put(KEY_FILE_LINK, fileLink);
        taskObject.put(KEY_TOKEN, token);
        String task = taskObject.toJSONString();
        System.out.println("task:"+task);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), task);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type","application/json")
                .post(requestBody)
                .build();

        String taskId = null;
        try {
            OkHttpClient client = new OkHttpClient();
            /**
             * 发送HTTP POST请求，处理服务端返回的响应
             */
            Response response = client.newCall(request).execute();
            int responseCode = response.code();
            String responseBody = response.body().string();
            System.out.println("请求结果： "+responseBody);
            response.close();

            if (200 == responseCode) {
                JSONObject result = JSONObject.parseObject(responseBody);
                result = result.getJSONObject(KEY_HEADER);
                String statusMessage = result.getString(KEY_STATUS_MESSAGE);
                if (STATUS_SUCCESS.equals(statusMessage)) {
                    taskId = result.getString(KEY_TASK_ID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskId;


    }
}
