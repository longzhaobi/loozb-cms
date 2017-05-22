package com.loozb.core.util;

import com.csource.common.NameValuePair;
import com.csource.fastdfs.*;

/**
 * @Author： 龙召碧
 * @Date: Created in 2017-3-26 17:20
 */
public class FastDFSClient {
    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;

    /**
     * 初始化
     * @param conf
     * @throws Exception
     */
    public FastDFSClient(String conf) throws Exception {
        if (conf.contains("classpath:")) {
            conf = conf.replace("classpath:", this.getClass().getResource("/")
                    .getPath());
        }
        //初始化全局配置。加载一个配置文件。
        ClientGlobal.init(conf);
        //创建一个TrackerClient对象。
        trackerClient = new TrackerClient();
        //创建一个TrackerServer对象。
        trackerServer = trackerClient.getConnection();
        //获得StorageClient对象。
        storageClient = new StorageClient1(trackerServer,storageServer);
    }
    /**
     * 上传文件方法1
     * @param fileName 文件名（全路径）
     * @param extName  文件扩展名
     * @param metas	        文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        return storageClient.upload_file1(fileName, extName, metas);
    }

    public String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName,null,null);
    }

    public String uploadFile(String fileName,String extName) throws Exception {
        return uploadFile(fileName,extName,null);
    }
    /**
     * 上传文件方法2
     * @param fileContent 文件的内容，字节数组
     * @param extName
     * @param metas
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {
        return storageClient.upload_file1(fileContent, extName, metas);
    }

    public String uploadFile(byte[] fileContent) throws Exception {
        return storageClient.upload_file1(fileContent, null, null);
    }

    public String uploadFile(byte[] fileContent, String extName) throws Exception {
        return storageClient.upload_file1(fileContent, extName, null);
    }
}
