package com.example.LocknessAPI.commons;

public enum MessageDefine {
    FAIL_TO_UPLOAD_FILE((byte) 1, "Fail to upload file"),
    INVALID_FILE_TYPE((byte) 2, "Invalid file type. Only .splat files are allowed."),
    TASK_ASSIGNMENT_NOT_FOUND((byte) 3, "Task assignment not found"),
    WORKER_INVALID((byte) 4, "Worker invalid"),
    WORKER_NOT_FOUND((byte) 5, "Worker not found"),
    SUCCESS((byte) 0, "File upload completed successfully"),
    NO_AVAILALBE_WORKER((byte) 6, "No available worker"),
    INVALID_TIME((byte) 7, "Invalid time"),
    MODEL_NOT_FOUND((byte) 8, "Model not found"),
    FILE_UPLOADED_SUCCESSFULLY((byte) 9, "File uploaded successfully"),
    FILE_IS_REQUIRED((byte) 10, "File is required"),
    THUMBNAIL_UPLOADED_SUCCESSFULLY((byte) 11, "Thumbnail uploaded successfully"),
    USER_NOT_FOUND((byte) 12, "User not found"),
    NONCE_NOT_FOUND_IN_SESSION((byte) 13, "Nonce not found in session"),
    SIGNATURE_VERIFIED_SUCCESSFULLY((byte) 14, "Signature verified successfully"),
    INVALID_SIGNATURE((byte) 15, "Invalid signature"),
    NO_ACTIVE_SESSION((byte) 16, "No active session"),
    LOGGED_OUT_SUCCESSFULLY((byte) 17, "Logged out successfully");

    private byte code;
    private String message;

    MessageDefine(byte code, String message) {
        this.code = code;
        this.message = message;
    }

    public byte getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
