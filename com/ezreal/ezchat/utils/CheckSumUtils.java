package com.ezreal.ezchat.utils;

import java.security.MessageDigest;

/**
 * Created by wudeng on 2017/8/25.
 */

public class CheckSumUtils {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    // 定义一个字符数组，用于将字节转换成十六进制字符。

    // 计算并获取CheckSum
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        // 这个方法用于计算并获取一个 CheckSum（校验和）。
        // 参数：
        // - appSecret: 应用的秘钥
        // - nonce: 一个随机字符串，通常用于防止重放攻击
        // - curTime: 当前时间戳

        // 返回值：返回一个字符串，代表计算出的 CheckSum。
        return encode("sha1", appSecret + nonce + curTime);
        // 调用 encode 方法，将 appSecret、nonce 和 curTime 拼接在一起，然后对这个字符串进行 SHA-1 哈希编码，得到 CheckSum。
    }

    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        // 这个方法用于计算并获取一个字符串的 MD5 值。
        // 参数：
        // - requestBody: 待计算的字符串

        // 返回值：返回一个字符串，代表计算出的 MD5 值。
        return encode("md5", requestBody);
        // 调用 encode 方法，将 requestBody 进行 MD5 哈希编码，得到 MD5 值。
    }
//    这段代码定义了一个 CheckSumUtils 工具类，其中包括两个方法：
//
//    getCheckSum(String appSecret, String nonce, String curTime)：这个方法用于计算并获取一个 CheckSum（校验和）字符串，它的输入参数包括应用的秘钥 appSecret、一个随机字符串 nonce 和当前时间戳 curTime。它将这些参数拼接在一起，然后对拼接后的字符串进行 SHA-1 哈希编码，最后返回计算出的 CheckSum。
//
//    getMD5(String requestBody)：这个方法用于计算并获取一个字符串的 MD5 值，输入参数是 requestBody，即待计算的字符串。它将 requestBody 进行 MD5 哈希编码，然后返回计算出的 MD5 值。这通常用于对请求体或其他数据进行完整性校验。

    // 生成随机字符串
    public static String getNonce() {
        // 定义一个公共静态方法 getNonce，返回一个字符串

        String retStr;  // 用于存储生成的随机字符串
        String strTable = "1234567890abcdefghijkmnpqrstuvwxyz";
        // 定义一个包含字符和数字的字符表，排除了一些容易混淆的字符
        int len = strTable.length();  // 字符表的长度
        boolean bDone = true;  // 用于检查生成的字符串是否满足条件

        do {
            retStr = "";  // 初始化生成的字符串
            int count = 0;  // 用于统计字符串中包含的数字字符的数量

            for (int i = 0; i < strTable.length(); i++) {
                double dblR = Math.random() * len;  // 生成一个随机小数
                int intR = (int) Math.floor(dblR);  // 将随机小数转换为整数
                char c = strTable.charAt(intR);  // 从字符表中选择字符

                if ('0' <= c && c <= '9') {
                    count++;  // 统计字符是否是数字字符
                }
                retStr += strTable.charAt(intR);  // 将选中的字符添加到生成的字符串
            }

            if (count >= 2) {
                bDone = false;  // 如果生成的字符串中包含至少两个数字字符，停止生成
            }
        } while (bDone);  // 生成的字符串不满足条件时继续循环

        return retStr;  // 返回生成的随机字符串
    }
//    这段代码定义了一个 getNonce 静态方法，用于生成一个随机字符串。生成的字符串是由指定的字符表（排除了一些容易混淆的字符）中的字符组成，且至少包含两个数字字符。方法的目的是生成一个不易被猜测的随机标识，通常用于增加系统或数据的安全性，例如生成随机的令牌或密钥。

    private static String encode(String algorithm, String value) {
        // 定义一个静态方法，用于将字符串进行编码，采用特定算法

        if (value == null) {
            // 如果输入的值为空
            return null;
            // 返回空值
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            // 创建一个MessageDigest对象，使用特定的算法初始化（如MD5、SHA-1）

            messageDigest.update(value.getBytes());
            // 将字符串内容转换为字节数组，并更新MessageDigest对象

            return getFormattedText(messageDigest.digest());
            // 调用 getFormattedText 方法处理加密后的字节数组，返回编码后的字符串
        } catch (Exception e) {
            // 捕获可能抛出的异常
            throw new RuntimeException(e);
            // 抛出运行时异常，通常是在出现错误时抛出异常
        }
    }
//    这段代码是一个静态方法 encode，用于对输入的字符串 value 进行编码，编码使用的算法由 algorithm 参数指定。它通常用于加密密码、生成哈希值或其他安全相关的操作。此方法首先检查输入值是否为 null，然后创建一个 MessageDigest 对象，使用指定的算法进行初始化（如MD5、SHA-1）。接下来，它将字符串内容转换为字节数组，并通过 update 方法更新 MessageDigest 对象。最后，它调用 getFormattedText 方法，将处理后的字节数组转换为编码后的字符串，并返回结果。
//
//    这段代码还包含了异常处理，如果在编码过程中发生了异常，它会将异常转换为运行时异常并抛出，以便在调用该方法的地方捕获和处理异常。

    private static String getFormattedText(byte[] bytes) {
        // 定义一个静态方法 getFormattedText，用于将字节数组转换为十六进制字符串
        int len = bytes.length;
        // 获取字节数组的长度
        StringBuilder buf = new StringBuilder(len * 2);
        // 创建一个 StringBuilder 对象，用于构建最终的十六进制字符串
        for (byte aByte : bytes) {
            // 遍历字节数组中的每个字节
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            // 将字节的高四位转换为对应的十六进制字符并追加到 StringBuilder 中
            buf.append(HEX_DIGITS[aByte & 0x0f]);
            // 将字节的低四位转换为对应的十六进制字符并追加到 StringBuilder 中
        }
        return buf.toString();
        // 返回构建好的十六进制字符串
    }
//    这段代码定义了一个静态方法 getFormattedText，它接受一个字节数组作为参数，并将该字节数组转换为一个十六进制字符串。这个方法的主要步骤如下：
//
//    创建一个 StringBuilder 对象 buf，用于构建最终的十六进制字符串。
//    通过遍历字节数组中的每个字节，将每个字节的高四位和低四位转换为对应的十六进制字符，然后追加到 buf 中。
//    最后，返回 buf 中构建好的十六进制字符串。
//    这种方法通常用于将字节数组表示的二进制数据以可读的十六进制形式进行显示，例如，用于调试或数据传输中。

}
