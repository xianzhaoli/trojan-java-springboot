package com.kosoft.trojanjava.utils;



public enum HttpStatusCode {
    SUCCESS(200,"ok"),
    //接口需要用户登录，用户没登录
    UNAUTHORIZED(401,"unauthorized"),
    //已登录但是没权限
    FORBIDDEN(403,"forbidden"),
    //访问的接口或资源不存在
    NOT_FOUND(404,"not found"),
    // 接口接收的参数和传的参数类型不一致(一般二进制文件上传时候报)
    UNSUPPORTED_MEDIA_TYPE(415,"unsupported media type"),
    //请求参数错误
    BAD_REQUEST(400,"bad request"),
    UNPROCESSABLE_ENTITY(422,"unprocessable entity"),
    //短时间内请求次数太多触发系统流控
    TOO_MANY_REQUESTS(429,"too many requests"),
    //后台错误，代码错误
    INTERNAL_SERVER_ERROR(500,"internal server error"),
    //服务不可用，服务挂了
    SERVICE_UNAVAILABLE(503,"service unavailable"),
    //网关超时，连不上或者挂了
    GATEWAY_TIMEOUT(504,"gateway timeout"),

    USERNAME_PASSWORD_ERROR(1000,"用户名或密码错误"),
    //发送验证码过于频繁
    SEND_PHONE_NUMBER_CAPTCHA_LIMIT_ERROR(1001,"发送验证码过于频繁"),
    //验证码错误
    PHONE_NUMBER_CAPTCHA_ERROR(1002,"验证码错误"),
    //手机号格式错误
    PHONE_NUMBER_FORMAT_ERROR(1003,"手机号格式错误"),
    //手机号已注册
    PHONE_NUMBER_REGISTERED_ERROR(1004,"手机号已注册"),
    //手机号未注册
    PHONE_NUMBER_NOT_REGISTERED_ERROR(1005,"手机号未注册"),
    //用户名重复
    USERNAME_REPEAT_ERROR(1006,"用户名重复"),
    //用户名不允许修改
    USERNAME_NOT_ALLOWED_MODIFY_ERROR(1007,"用户名不允许修改"),
    //新密码不能和旧密码相同
    PASSWORD_NOT_ALLOWED_MODIFY_ERROR(1008,"新密码不能和旧密码相同"),
    //原密码不正确
    PASSWORD_ERROR(1009,"原密码不正确"),
    ;

    private int code;
    private String message;


    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}

