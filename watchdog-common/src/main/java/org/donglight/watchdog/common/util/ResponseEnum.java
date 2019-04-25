package org.donglight.watchdog.common.util;

/**
 * 〈一句话功能简述〉<br>
 * 功能
 *
 * @author donglight
 * @date 2019/4/19
 * @since 1.0.0
 */
public enum ResponseEnum {

    /**
     *  [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）。
     */
    SUCCESS(200,"success"),

    /**
     *  [POST/PUT/PATCH]：用户新建或修改数据成功。
     */
    CREATED (201,"created or updated success"),

    /**
     * [*]：表示一个请求已经进入后台排队（异步任务）
     */
    ACCEPTED(202,"request has entered the background queue"),

    /**
     * [DELETE]：用户删除数据成功。
     */
    NOCONTENT(204,"delete success"),

    /**
     * [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
     */
    INVALID_REQUEST(400,"invalid request"),

    /**
     * 表示用户没有权限（令牌、用户名、密码错误）。
     */
    UNAUTHORIZED(401,"does not have permission"),

    /**
     * 示用户得到授权（与401错误相对），但是访问是被禁止的。
     */
    FORBIDDEN(403,"access forbidden"),

    /**
     * 用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
     */
    NOT_FOUND(404,"resource not found"),

    /**
     * [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）。
     */
    NOT_ACCEPTABLE(406,"request is not available"),

    /**
     * [GET]：用户请求的资源被永久删除，且不会再得到的。
     */
    GONE(410,"resource is permanently deleted"),

    /**
     * [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误。
     */
    UNPROCESABLE(422,"when an object is created, a validation error occurs"),


    /**
     *  [*]：服务器发生错误，用户将无法判断发出的请求是否成功。
     */
    SERVER_INTERNAL_ERROR(500,"server internal error"),

    /**
     *  注册失败错误
     */
    REGISTER_FAIL(1000,"registration was fail or have  already registered"),

    /**
     * 没有这个ID对应的元素，比如appID,URLId
     */
    NOELEMENT(1001,"No element corresponding to this ID");

    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
