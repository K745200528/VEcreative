package com.ezreal.ezchat.utils;

import java.text.DecimalFormat;

/**
 * Created by wudeng on 2017/10/20.
 */

public class ConvertUtils {
    // 创建名为 ConvertUtils 的类

    /**
     * 通过 byte 数值计算得到 String格式的 文件大小，如：11.1KB,10.12MB
     *
     * @param byteSize 文件大小 单位 byte
     * @return String格式的 文件大小
     */
    public static String getSizeString(long byteSize) {
        // 定义一个静态方法 getSizeString，接受一个 long 类型的 byteSize 参数，用于将字节数转换为可读的文件大小表示。

        if (byteSize <= 0) {
            return "0B";
        }
        // 如果 byteSize 小于等于 0 字节，返回 "0B" 表示文件大小为0。

        if (byteSize < 1024) {
            return String.valueOf(byteSize) + "B";
        }
        // 如果 byteSize 小于 1KB，直接返回 byteSize 的字符串形式，后面加上 "B" 表示字节大小。

        long k = byteSize / 1024;
        // 计算 k 为 byteSize 除以 1024 的整数部分，即文件大小以KB为单位。
        if (k < 1024) {
            // 如果 k 小于 1MB，说明文件大小在 1KB 到 1MB 之间。
            double minK = byteSize % 1024 / 1024.0;
            // 计算 minK 为 byteSize 除以 1024 取余后再除以 1024.0 得到的小数，用于表示不足 1KB 的部分。
            return new DecimalFormat("#.00").format(k + minK) + "KB";
            // 使用 DecimalFormat 格式化 k 和 minK，保留两位小数，然后返回 "KB" 表示文件大小。
        }

        long r = 1024 * 1024;
        // 定义 r 为 1MB 的字节数。
        long m = byteSize / r;
        // 计算 m 为 byteSize 除以 r 得到的整数，表示文件大小以MB为单位。
        double minM = byteSize % r / (r * 1.0);
        // 计算 minM 为 byteSize 除以 r 取余后再除以 (r * 1.0) 得到的小数，表示不足 1MB 的部分。
        return new DecimalFormat("#.00").format(m + minM) + "MB";
        // 使用 DecimalFormat 格式化 m 和 minM，保留两位小数，然后返回 "MB" 表示文件大小。
    }
//    这段代码定义了一个静态方法 getSizeString，该方法接受一个表示文件大小的字节数 byteSize，然后将其转换为一个可读的文件大小表示，如"KB"、"MB"等。根据字节数的大小，它采用不同的计算方式，保留两位小数以提供精确的表示。这种方法通常用于显示文件大小信息，以便用户更好地理解文件的大小。

    /**
     * 根据返回状态码得到提示信息
     *
     * @param code 状态码
     * @return 提示信息字符串
     */
    public static String code2String(int code) {
        // 定义一个静态方法 code2String，接受一个整数参数 code
        switch (code) {
            // 根据传入的 code 值进行不同的处理
            case 302:
                return "用户不存在或密码错误";
            // 如果 code 等于 302，返回 "用户不存在或密码错误"
            case 408:
                return "服务器无响应";
            // 如果 code 等于 408，返回 "服务器无响应"
            case 415:
                return "网络中断，与服务器连接失败";
            // 如果 code 等于 415，返回 "网络中断，与服务器连接失败"
            case 416:
                return "请求过频，请稍后再试";
            // 如果 code 等于 416，返回 "请求过频，请稍后再试"
            case 417:
                return "自动登录失败，请手动尝试";
            // 如果 code 等于 417，返回 "自动登录失败，请手动尝试"
            case 1000:
                return "数据库未打开";
            // 如果 code 等于 1000，返回 "数据库未打开"
            case 422:
                return "账户被禁用";
            // 如果 code 等于 422，返回 "账户被禁用"
            default:
                return "";
            // 如果 code 不匹配以上任何情况，返回一个空字符串
        }
    }
//    这段代码是一个静态方法 code2String，用于将特定的整数代码（code）映射为相应的错误消息字符串。传入不同的整数 code 会得到不同的错误消息。这通常用于处理服务器或应用中的错误码，以便将错误码转化为用户可以理解的错误消息，以改善用户体验。在代码的 switch 语句中，针对不同的 code 值，返回相应的错误消息。如果传入的 code 不匹配任何情况，将返回一个空字符串。
}
