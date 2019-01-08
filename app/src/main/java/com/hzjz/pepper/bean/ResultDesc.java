package com.hzjz.pepper.bean;

public class ResultDesc {
    private int error_code;
    private String reason;
    private String result;
    public int getError_code() {
        return error_code;
    }
    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public ResultDesc(int error_code, String reason, String result) {
        this.error_code = error_code;
        this.reason = reason;
        this.result = result;
    }
}
