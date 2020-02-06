package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.CertificateException;

public class AnyCard extends SmartCard {

   private static final String KEYSTORE_TYPE = "Windows-MY";
   private static final String KEYSTORE_PROVIDER = "SunMSCAPI";
   public static final String[] KNOWN_EID_ATRS = new String[0];


   public String getVendorName() {
      return "";
   }

   public KeyStore loadKeyStore(char[] var1) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
      int var2 = getOsFamily();
      if(var2 != 1) {
         throw new KeyStoreException("Platform does not support MS CAPI keystore");
      } else {
         System.out.println("Loading MS CAPI store");
         Utils.logMessage("SunMSCAPI");
         Provider var3 = Security.getProvider("SunMSCAPI");
         KeyStore var4 = KeyStore.getInstance("Windows-MY", var3);
         var4.load((LoadStoreParameter)null);
         SmartCardLogic._fixAliases(var4);
         return var4;
      }
   }

   public void sendAtr(String var1, String var2) {}

   public String getKeyStoreProvider() {
      return "SunMSCAPI";
   }

   public String getKeyStoreType() {
      return "Windows-MY";
   }

}
