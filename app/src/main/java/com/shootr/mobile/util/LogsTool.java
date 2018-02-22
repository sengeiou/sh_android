package com.shootr.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;

public class LogsTool {

  private byte[] zipCompress(String jsonLog) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    Deflater zipDeflater = new Deflater();
    try {
      zipDeflater.setInput(jsonLog.getBytes());
      zipDeflater.finish();
      byte[] buffer = new byte[1024];
      int count = 0;
      while (!zipDeflater.finished()) {
        count = zipDeflater.deflate(buffer);
        stream.write(buffer, 0, count);
      }
      return stream.toByteArray();
    } finally {
      stream.close();
      zipDeflater.end();
    }
  }

  public byte[] sendLog(List<LogShootr> logShootrs) throws IOException {
    StringBuilder sb = new StringBuilder();
    if (logShootrs.size() > 0) {
      sb.append("[");
      boolean first = true;
      for (LogShootr logShootr : logShootrs) {
        if (!first) {
          sb.append(",");
        }
        sb.append(logShootr.toJson());
        first = false;
      }
      sb.append("]");
    }

    return zipCompress(sb.toString());
  }
}
