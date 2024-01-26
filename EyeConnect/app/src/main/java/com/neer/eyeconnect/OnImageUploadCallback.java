package com.neer.eyeconnect;

public interface OnImageUploadCallback {
    void onSuccess(String imageUrl);

    void onFailure();
}
