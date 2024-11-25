package com.kosoft.trojanjava.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestApiResult<T> {

    private int code;

    private String message;

    private T data;


    public static RestApiResult success()
    {
        return build(HttpStatusCode.SUCCESS.getCode(), HttpStatusCode.SUCCESS.getMessage(),null);
    }
    public static RestApiResult success(String message)
    {
        return build(HttpStatusCode.SUCCESS.getCode(), message,null);
    }
    public static RestApiResult success(Object data)
    {
        return build(HttpStatusCode.SUCCESS.getCode(), HttpStatusCode.SUCCESS.getMessage(),data);
    }

    public static RestApiResult success(String message,Object data)
    {
        return build(HttpStatusCode.SUCCESS.getCode(), message,data);
    }


    public static RestApiResult failed()
    {
        return build(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), HttpStatusCode.INTERNAL_SERVER_ERROR.getMessage(),null);
    }
    public static RestApiResult failed(String message)
    {
        return build(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), message,null);
    }

    public static RestApiResult failed(String message,Object data)
    {
        return build(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), message,data);
    }


    /**
     * 未授权
     * @return
     */
    public static RestApiResult unauthorized()
    {
        return build(HttpStatusCode.UNAUTHORIZED.getCode(), HttpStatusCode.UNAUTHORIZED.getMessage(),null);
    }
    public static RestApiResult unauthorized(String message)
    {
        return build(HttpStatusCode.UNAUTHORIZED.getCode(), message,null);
    }

    public static RestApiResult unauthorized(String message,Object data)
    {
        return build(HttpStatusCode.UNAUTHORIZED.getCode(), message,data);
    }


    /**
     * 无权限
     * @return
     */
    public static RestApiResult forbidden()
    {
        return build(HttpStatusCode.FORBIDDEN.getCode(), HttpStatusCode.FORBIDDEN.getMessage(),null);
    }
    public static RestApiResult forbidden(String message)
    {
        return build(HttpStatusCode.FORBIDDEN.getCode(), message,null);
    }

    public static RestApiResult forbidden(String message,Object data)
    {
        return build(HttpStatusCode.FORBIDDEN.getCode(), message,data);
    }



    public static RestApiResult build(int code,String message,Object data)
    {
        return RestApiResult.builder().code(code)
                .message(message).data(data).build();
    }

}
