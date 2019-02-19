package entity;

import java.io.Serializable;

/**
 * 作者：杨立波
 * 时间：2018/8/31 17:04
 */

/**
 * 返回结果封装
 */
public class Result implements Serializable {

    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
