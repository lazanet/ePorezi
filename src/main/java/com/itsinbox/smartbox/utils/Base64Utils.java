package com.itsinbox.smartbox.utils;


public class Base64Utils {

   private static byte[] mBase64EncMap;
   private static byte[] mBase64DecMap;


   public static String base64Encode(byte[] aData) {
      if(aData != null && aData.length != 0) {
         byte[] encodedBuf = new byte[(aData.length + 2) / 3 * 4];
         int srcIndex = 0;

         int destIndex;
         for(destIndex = 0; srcIndex < aData.length - 2; srcIndex += 3) {
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex] >>> 2 & 63];
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 1] >>> 4 & 15 | aData[srcIndex] << 4 & 63];
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 2] >>> 6 & 3 | aData[srcIndex + 1] << 2 & 63];
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 2] & 63];
         }

         if(srcIndex < aData.length) {
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex] >>> 2 & 63];
            if(srcIndex < aData.length - 1) {
               encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 1] >>> 4 & 15 | aData[srcIndex] << 4 & 63];
               encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 1] << 2 & 63];
            } else {
               encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex] << 4 & 63];
            }
         }

         while(destIndex < encodedBuf.length) {
            encodedBuf[destIndex] = 61;
            ++destIndex;
         }

         String result = new String(encodedBuf);
         return result;
      } else {
         throw new IllegalArgumentException("Can not encode NULL or empty byte array.");
      }
   }

   public static byte[] base64Decode(String aData) {
      if(aData != null && aData.length() != 0) {
         byte[] data = aData.getBytes();

         int tail;
         for(tail = data.length; data[tail - 1] == 61; --tail) {
            ;
         }

         byte[] decodedBuf = new byte[tail - data.length / 4];

         int srcIndex;
         for(srcIndex = 0; srcIndex < data.length; ++srcIndex) {
            data[srcIndex] = mBase64DecMap[data[srcIndex]];
         }

         srcIndex = 0;

         int destIndex;
         for(destIndex = 0; destIndex < decodedBuf.length - 2; destIndex += 3) {
            decodedBuf[destIndex] = (byte)(data[srcIndex] << 2 & 255 | data[srcIndex + 1] >>> 4 & 3);
            decodedBuf[destIndex + 1] = (byte)(data[srcIndex + 1] << 4 & 255 | data[srcIndex + 2] >>> 2 & 15);
            decodedBuf[destIndex + 2] = (byte)(data[srcIndex + 2] << 6 & 255 | data[srcIndex + 3] & 63);
            srcIndex += 4;
         }

         if(destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte)(data[srcIndex] << 2 & 255 | data[srcIndex + 1] >>> 4 & 3);
         }

         ++destIndex;
         if(destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte)(data[srcIndex + 1] << 4 & 255 | data[srcIndex + 2] >>> 2 & 15);
         }

         return decodedBuf;
      } else {
         throw new IllegalArgumentException("Can not decode NULL or empty string.");
      }
   }

   static {
      byte[] base64Map = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)43, (byte)47};
      mBase64EncMap = base64Map;
      mBase64DecMap = new byte[128];

      for(int i = 0; i < mBase64EncMap.length; ++i) {
         mBase64DecMap[mBase64EncMap[i]] = (byte)i;
      }

   }
}
