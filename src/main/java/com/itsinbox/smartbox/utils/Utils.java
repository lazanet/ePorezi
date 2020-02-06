package com.itsinbox.smartbox.utils;

import com.itsinbox.smartbox.utils.FileLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Utils {

   private static FileLogger logger;


   public static String int2HexString(int var0) {
      return bytes2HexString((byte[])asByteArray((int[])(new int[]{var0 >>> 24, var0 >>> 16, var0 >>> 8, var0})));
   }

   public static String bytes2HexString(byte ... var0) {
      ArrayList var1 = new ArrayList();
      boolean var2 = true;
      byte[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte var6 = var3[var5];
         if(!var2 || var6 != 0) {
            var2 = false;
            var1.add(String.format("%02X", new Object[]{Byte.valueOf(var6)}));
         }
      }

      StringBuilder var7 = new StringBuilder();
      Iterator var8 = var1.iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         var7.append(var9).append(":");
      }

      var7.deleteCharAt(var7.length() - 1);
      return var7.toString();
   }

   private static byte asByte(int var0) {
      return (byte)(var0 & 255);
   }

   public static byte[] asByteArray(int ... var0) {
      byte[] var1 = new byte[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = asByte(var0[var2]);
      }

      return var1;
   }

   public static void openURL(String var0) throws IOException {
      logMessage("Otvaram " + var0);
      String var1 = System.getProperty("os.name").toLowerCase();
      Runtime var2 = Runtime.getRuntime();

      try {
         if(var1.contains("win")) {
            var2.exec("rundll32 url.dll,FileProtocolHandler " + var0);
         } else if(var1.contains("mac")) {
            var2.exec("open " + var0);
         } else if(var1.contains("nix") || var1.contains("nux")) {
            var2.exec("xdg-open " + var0);
         }
      } catch (IOException var4) {
         logMessage("Ne mogu da otvorim browser!");
      }

      System.exit(0);
   }

   public static void logMessage(String var0) {
      System.out.println(var0);
      if(logger == null) {
         logger = new FileLogger();
      }

      logger.writeToLog(var0);
   }
}
