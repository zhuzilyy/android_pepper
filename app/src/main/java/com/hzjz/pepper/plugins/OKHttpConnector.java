package com.hzjz.pepper.plugins;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpConnector {
    private static OkHttpClient okHttpClient;
    private static OKHttpConnector mInstance;

    private OKHttpConnector() {
    }

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (OKHttpConnector.class) {
                if (okHttpClient == null)
                    okHttpClient = new OkHttpClient();
                okHttpClient = okHttpClient.newBuilder().build();
                    okHttpClient = okHttpClient.newBuilder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .build();
            }
        }
        return okHttpClient;
    }

    private OKHttpConnector(OkHttpClient client) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        } else {
            okHttpClient = client;
        }
    }

    public static OKHttpConnector initClient(OkHttpClient client) {
        if (mInstance == null) {
            synchronized (OKHttpConnector.class) {
                if (mInstance == null) {
                    mInstance = new OKHttpConnector(client);
                }
            }
        }
        return mInstance;
    }

    public static void GetAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void PostAsync(String url, Map<String, String> mapParams, Callback callback) {
        Request request;
        if (mapParams != null) {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : mapParams.keySet()) {
                builder.add(key, mapParams.get(key));
            }
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void PostJsonAsync(String url, Map<String, String> mapParams, Callback callback) {
        Request request;
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        if (mapParams != null) {
            for (String key : mapParams.keySet()) {
                jsonObject.put(key, mapParams.get(key));
            }

        } else {
            jsonObject.put("", "");
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFile(String url, String pathName, String fileName, Callback callback) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName,
                        RequestBody.create(MEDIA_TYPE, new File(pathName)));
        //发出请求参数
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "9199fdef135c122")
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 根据文件路径判断MediaType
     *
     * @param path
     * @return
     */
    private static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /**
     * 下载文件
     *
     * @param url
     * @param fileDir
     * @param fileName
     */
    public static void downFile(String url, final String fileDir, final String fileName) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.getStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }

//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream is = null;
//                byte[] buf = new byte[2048];
//                int len = 0;
//                FileOutputStream fos = null;
//                try {
//                    is = response.body().byteStream();
//                    File file = new File(fileDir, fileName);
//                    fos = new FileOutputStream(file);
//                    //---增加的代码---
//                    //计算进度
//                    long totalSize = response.body().contentLength();
//                    long sum = 0;
//                    while ((len = is.read(buf)) != -1) {
//                        sum += len;
//                        //progress就是进度值
//                        int progress = (int) (sum * 1.0f/totalSize * 100);
//                        //---增加的代码---
//                        fos.write(buf, 0, len);
//                    }
//                    fos.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (is != null) is.close();
//                    if (fos != null) fos.close();
//                }
//            }
        });
    }
}
