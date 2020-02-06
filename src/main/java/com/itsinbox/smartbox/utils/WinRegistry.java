package com.itsinbox.smartbox.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class WinRegistry {

   public static final int HKEY_CURRENT_USER = -2147483647;
   public static final int HKEY_LOCAL_MACHINE = -2147483646;
   public static final int REG_SUCCESS = 0;
   public static final int REG_NOTFOUND = 2;
   public static final int REG_ACCESSDENIED = 5;
   private static final int KEY_ALL_ACCESS = 983103;
   private static final int KEY_READ = 131097;
   private static Preferences userRoot = Preferences.userRoot();
   private static Preferences systemRoot = Preferences.systemRoot();
   private static Class userClass = userRoot.getClass();
   private static Method regOpenKey = null;
   private static Method regCloseKey = null;
   private static Method regQueryValueEx = null;
   private static Method regEnumValue = null;
   private static Method regQueryInfoKey = null;
   private static Method regEnumKeyEx = null;
   private static Method regCreateKeyEx = null;
   private static Method regSetValueEx = null;
   private static Method regDeleteKey = null;
   private static Method regDeleteValue = null;


   public static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if(hkey == -2147483646) {
         return readString(systemRoot, hkey, key, valueName);
      } else if(hkey == -2147483647) {
         return readString(userRoot, hkey, key, valueName);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static Map readStringValues(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if(hkey == -2147483646) {
         return readStringValues(systemRoot, hkey, key);
      } else if(hkey == -2147483647) {
         return readStringValues(userRoot, hkey, key);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static List readStringSubKeys(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if(hkey == -2147483646) {
         return readStringSubKeys(systemRoot, hkey, key);
      } else if(hkey == -2147483647) {
         return readStringSubKeys(userRoot, hkey, key);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] ret;
      if(hkey == -2147483646) {
         ret = createKey(systemRoot, hkey, key);
         regCloseKey.invoke(systemRoot, new Object[]{Integer.valueOf(ret[0])});
      } else {
         if(hkey != -2147483647) {
            throw new IllegalArgumentException("hkey=" + hkey);
         }

         ret = createKey(userRoot, hkey, key);
         regCloseKey.invoke(userRoot, new Object[]{Integer.valueOf(ret[0])});
      }

      if(ret[1] != 0) {
         throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
      }
   }

   public static void writeStringValue(int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if(hkey == -2147483646) {
         writeStringValue(systemRoot, hkey, key, valueName, value);
      } else {
         if(hkey != -2147483647) {
            throw new IllegalArgumentException("hkey=" + hkey);
         }

         writeStringValue(userRoot, hkey, key, valueName, value);
      }

   }

   public static void deleteKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = -1;
      if(hkey == -2147483646) {
         rc = deleteKey(systemRoot, hkey, key);
      } else if(hkey == -2147483647) {
         rc = deleteKey(userRoot, hkey, key);
      }

      if(rc != 0) {
         throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
      }
   }

   public static void deleteValue(int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = -1;
      if(hkey == -2147483646) {
         rc = deleteValue(systemRoot, hkey, key, value);
      } else if(hkey == -2147483647) {
         rc = deleteValue(userRoot, hkey, key, value);
      }

      if(rc != 0) {
         throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
      }
   }

   private static int deleteValue(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])((int[])regOpenKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key), Integer.valueOf(983103)}));
      if(handles[1] != 0) {
         return handles[1];
      } else {
         int rc = ((Integer)regDeleteValue.invoke(root, new Object[]{Integer.valueOf(handles[0]), toCstr(value)})).intValue();
         regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
         return rc;
      }
   }

   private static int deleteKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = ((Integer)regDeleteKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key)})).intValue();
      return rc;
   }

   private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])((int[])regOpenKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key), Integer.valueOf(131097)}));
      if(handles[1] != 0) {
         return null;
      } else {
         byte[] valb = (byte[])((byte[])regQueryValueEx.invoke(root, new Object[]{Integer.valueOf(handles[0]), toCstr(value)}));
         regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
         return valb != null?(new String(valb)).trim():null;
      }
   }

   private static Map readStringValues(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      HashMap results = new HashMap();
      int[] handles = (int[])((int[])regOpenKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key), Integer.valueOf(131097)}));
      if(handles[1] != 0) {
         return null;
      } else {
         int[] info = (int[])((int[])regQueryInfoKey.invoke(root, new Object[]{Integer.valueOf(handles[0])}));
         int count = info[0];
         int maxlen = info[3];

         for(int index = 0; index < count; ++index) {
            byte[] name = (byte[])((byte[])regEnumValue.invoke(root, new Object[]{Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1)}));
            String value = readString(hkey, key, new String(name));
            results.put((new String(name)).trim(), value);
         }

         regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
         return results;
      }
   }

   private static List readStringSubKeys(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      ArrayList results = new ArrayList();
      int[] handles = (int[])((int[])regOpenKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key), Integer.valueOf(131097)}));
      if(handles[1] != 0) {
         return null;
      } else {
         int[] info = (int[])((int[])regQueryInfoKey.invoke(root, new Object[]{Integer.valueOf(handles[0])}));
         int count = info[0];
         int maxlen = info[3];

         for(int index = 0; index < count; ++index) {
            byte[] name = (byte[])((byte[])regEnumKeyEx.invoke(root, new Object[]{Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1)}));
            results.add((new String(name)).trim());
         }

         regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
         return results;
      }
   }

   private static int[] createKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      return (int[])((int[])regCreateKeyEx.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key)}));
   }

   private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])((int[])regOpenKey.invoke(root, new Object[]{Integer.valueOf(hkey), toCstr(key), Integer.valueOf(983103)}));
      regSetValueEx.invoke(root, new Object[]{Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value)});
      regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
   }

   private static byte[] toCstr(String str) {
      byte[] result = new byte[str.length() + 1];

      for(int i = 0; i < str.length(); ++i) {
         result[i] = (byte)str.charAt(i);
      }

      result[str.length()] = 0;
      return result;
   }

   static {
      try {
         regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[]{Integer.TYPE, byte[].class, Integer.TYPE});
         regOpenKey.setAccessible(true);
         regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[]{Integer.TYPE});
         regCloseKey.setAccessible(true);
         regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[]{Integer.TYPE, byte[].class});
         regQueryValueEx.setAccessible(true);
         regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
         regEnumValue.setAccessible(true);
         regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[]{Integer.TYPE});
         regQueryInfoKey.setAccessible(true);
         regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
         regEnumKeyEx.setAccessible(true);
         regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", new Class[]{Integer.TYPE, byte[].class});
         regCreateKeyEx.setAccessible(true);
         regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", new Class[]{Integer.TYPE, byte[].class, byte[].class});
         regSetValueEx.setAccessible(true);
         regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", new Class[]{Integer.TYPE, byte[].class});
         regDeleteValue.setAccessible(true);
         regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", new Class[]{Integer.TYPE, byte[].class});
         regDeleteKey.setAccessible(true);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }
}
