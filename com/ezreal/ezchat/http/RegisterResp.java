package com.ezreal.ezchat.http;

public class RegisterResp {
    // 创建名为 RegisterResp 的类

    private int code;
    // 用于存储整数类型的 code，通常表示请求或响应的状态码

    private String desc;
    // 用于存储字符串类型的 desc，通常包含有关请求或响应的描述性信息

    private Info info;
    // 创建了一个名为 info 的自定义类的实例，该类可能包含更多注册响应信息
//    这段代码定义了一个名为 RegisterResp 的Java类。这个类用于表示用户注册的响应。

    public int getCode() {
        // 获取 code 值的方法
        return code;
    }

    public void setCode(int code) {
        // 设置 code 值的方法
        this.code = code;
    }

    public String getDesc() {
        // 获取 desc 字符串的方法
        return desc;
    }

    public void setDesc(String desc) {
        // 设置 desc 字符串的方法
        this.desc = desc;
    }

    public Info getInfo() {
        // 获取 Info 对象的方法
        return info;
    }

    public void setInfo(Info info) {
        // 设置 Info 对象的方法
        this.info = info;
    }
//    这段代码展示了一个常见的 Java 类，其中包含了一系列 getter 和 setter 方法。这些方法通常用于访问和设置对象的属性。

    public static class Info {
        // 创建一个静态内部类 Info

        private String token;
        // 字符串类型的成员变量 token，用于存储 token 信息
        private String accid;
        // 字符串类型的成员变量 accid，用于存储 accid 信息
        private String name;
        // 字符串类型的成员变量 name，用于存储用户名称

        public String getToken() {
            return token;
        }
        // getToken 方法，用于获取 token 值

        public void setToken(String token) {
            this.token = token;
        }
        // setToken 方法，用于设置 token 值

        public String getAccid() {
            return accid;
        }
        // getAccid 方法，用于获取 accid 值

        public void setAccid(String accid) {
            this.accid = accid;
        }
        // setAccid 方法，用于设置 accid 值

        public String getName() {
            return name;
        }
        // getName 方法，用于获取用户名称

        public void setName(String name) {
            this.name = name;
        }
        // setName 方法，用于设置用户名称
    }
//    这段代码定义了一个名为 Info 的静态内部类，它包含了三个私有成员变量 token、accid 和 name，以及各自对应的 getter 和 setter 方法。
//
//    getToken 方法用于获取 token 的值。
//    setToken 方法用于设置 token 的值。
//    getAccid 方法用于获取 accid 的值。
//    setAccid 方法用于设置 accid 的值。
//    getName 方法用于获取用户名称的值。
//    setName 方法用于设置用户名称的值。
//    这种模式通常用于封装数据，将数据的访问控制在类的内部，通过 getter 和 setter 方法提供对数据的访问和修改。这有助于维护数据的一致性和安全性。
}
