package com.netty.fileserver;

import com.alibaba.fastjson.JSONObject;
import com.netty.fileserver.MultipartRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 作者：DarkKIng
 * 创建日期：2019/12/17
 * 类说明：文件下载handler
 */
@Sharable
public class FileServerHandle extends SimpleChannelInboundHandler<FullHttpRequest> {

    /*客户端读到数据以后，就会执行*/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        //打印请求url
        System.out.println(request.uri());
        //下载任务处理
        if (request.uri().equals("/downFile")) {
            responseExportFile(ctx, "D://test.txt", "model.txt");
        }
        //上传接口处理
        if (request.uri().equals("/upLoadFile")) {
            MultipartRequest MultipartBody = getMultipartBody(request);
            Map<String, FileUpload> fileUploads = MultipartBody.getFileUploads();
            //输出文件信息
            for (String key : fileUploads.keySet()) {
                //获取文件对象
                FileUpload file = fileUploads.get(key);
                System.out.println("fileName is" + file.getFile().getPath());
                //获取文件流
                InputStream in = new FileInputStream(file.getFile());
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                String content = bf.lines().collect(Collectors.joining("\n"));
                //打印文件
                System.out.println("content is \n" + content);
            }
            //输出参数信息
            JSONObject params = MultipartBody.getParams();
            //输出文件信息
            System.out.println(JSONObject.toJSONString(params));

        }
    }

    /*连接建立以后*/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(
                "Hello Netty", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * <p>
     * 返回下载内容
     * </p>
     *
     * @param ctx
     * @author DarkKing 2019-12-17
     */
    public static void responseExportFile(ChannelHandlerContext ctx, String path, String name) {
        File file = new File(path);
        try {
            //随机读取文件
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();
            //定义response对象
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            //设置请求头部
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream; charset=UTF-8");
            response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\";");
            ctx.write(response);
            //设置事件通知对象
            ChannelFuture sendFileFuture = ctx
                    .write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                //文件传输完成执行监听器
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    System.out.println("file {} transfer complete.");
                }

                //文件传输进度监听器
                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) throws Exception {

                    if (total < 0) {
                        System.out.println("file {} transfer progress: {}");
                    } else {
                        System.out.println("file {} transfer progress: {}/{}");
                    }
                }
            });
            //刷新缓冲区数据，文件结束标志符
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述
     * <p>解析文件上传</p>
     *
     * @author DarkKing 2019/10/9 15:24
     * @params [ctx, httpDecode]
     */
    private static MultipartRequest getMultipartBody(FullHttpRequest request) {
        try {
            //创建HTTP对象工厂
            HttpDataFactory factory = new DefaultHttpDataFactory(true);
            //使用HTTP POST解码器
            HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(factory, request);
            httpDecoder.setDiscardThreshold(0);
            if (httpDecoder != null) {
                //获取HTTP请求对象
                final HttpContent chunk = (HttpContent) request;
                //加载对象到加吗器。
                httpDecoder.offer(chunk);
                if (chunk instanceof LastHttpContent) {
                    //自定义对象bean
                    MultipartRequest multipartRequest = new MultipartRequest();
                    //存放文件对象
                    Map<String, FileUpload> fileUploads = new HashMap<>();
                    //存放参数对象
                    JSONObject body = new JSONObject();
                    //通过迭代器获取HTTP的内容
                    java.util.List<InterfaceHttpData> InterfaceHttpDataList = httpDecoder.getBodyHttpDatas();
                    for (InterfaceHttpData data : InterfaceHttpDataList) {
                        //如果数据类型为文件类型，则保存到fileUploads对象中
                        if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
                            FileUpload fileUpload = (FileUpload) data;
                            fileUploads.put(data.getName(), fileUpload);
                        }
                        //如果数据类型为参数类型，则保存到body对象中
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) data;
                            body.put(attribute.getName(), attribute.getValue());
                        }
                    }
                    //存放文件信息
                    multipartRequest.setFileUploads(fileUploads);
                    //存放参数信息
                    multipartRequest.setParams(body);

                    return multipartRequest;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


