package com.neer.eyeconnect;

public interface ApiCallCallback {
    void onSuccess(String imageUrl);

    void onFailure();
}
