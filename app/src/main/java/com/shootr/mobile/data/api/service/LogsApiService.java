package com.shootr.mobile.data.api.service;

import java.io.IOException;

public interface LogsApiService {

  String uploadLogs(byte[] logs) throws IOException;
}
