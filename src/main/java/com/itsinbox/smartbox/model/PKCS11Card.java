package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.Builder;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.cert.CertificateException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public abstract class PKCS11Card extends SmartCard {

   private static final String KEYSTORE_TYPE = "PKCS11";
   private static final String KEYSTORE_PROVIDER = "SunPKCS11";


   public String getVendorName() {
      return "";
   }

   protected boolean moduleExists(String var1) {
      File var2 = new File(var1);
      return var2.exists() && !var2.isDirectory();
   }

   protected String searchModulePaths(String[] var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         Utils.logMessage("Trying PKCS11 module path " + var5);
         if(this.moduleExists(var5)) {
            return var5;
         }
      }

      return null;
   }

   protected abstract String getPKCS11ModuleName(int var1);

   protected abstract String getPKCS11ModulePath(int var1);

   public KeyStore loadKeyStore(char[] var1) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
      int var2 = getOsFamily();
      String var3;
      if(var2 == 1) {
         var3 = "Platform should not use PKCS11 module but MS CAPI instead";
         Utils.logMessage(var3);
         throw new KeyStoreException(var3);
      } else {
         var3 = this.getPKCS11ModulePath(var2);
         String var4;
         if(var3 == null) {
            var4 = "PKCS11 module not found!";
            Utils.logMessage(var4);
            throw new KeyStoreException(var4);
         } else {
            var4 = this.getPKCS11ModuleName(var2);
            String var5 = "name=" + var4 + "\nlibrary=" + var3;
            Utils.logMessage("Loading PKCS11 module: " + var5);
            byte[] var6 = var5.getBytes();
            ByteArrayInputStream var7 = new ByteArrayInputStream(var6);
            Security.addProvider(Security.getProvider("SunPKCS11"));
            CallbackHandlerProtection var9 = new CallbackHandlerProtection(new PKCS11Card.PinCallbackHandler(null));
            Builder var10 = Builder.newInstance("PKCS11", (Provider)null, var9);
            KeyStore var11 = var10.getKeyStore();
            SmartCardLogic._fixAliases(var11);
            return var11;
         }
      }
   }

   public void sendAtr(String var1, String var2) {}

   public String getKeyStoreProvider() {
      return "SunPKCS11";
   }

   public String getKeyStoreType() {
      return "PKCS11";
   }

   private static class PinCallbackHandler implements CallbackHandler {

      private PinCallbackHandler() {}

      public void handle(Callback[] var1) throws IOException, UnsupportedCallbackException {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            Callback var3 = var1[var2];
            if(var3 instanceof PasswordCallback) {
               Utils.logMessage("PIN callback handler invoked");
               PasswordCallback var4 = (PasswordCallback)var3;
               JPasswordField var5 = new JPasswordField();
               int var6 = JOptionPane.showConfirmDialog((Component)null, var5, "PIN", 2, -1);
               if(var6 == 0) {
                  Utils.logMessage("PIN entry confirmed");
                  String var7 = new String(var5.getPassword());
                  var4.setPassword(var7.toCharArray());
               } else {
                  Utils.logMessage("PIN entry cancelled");
               }
               break;
            }
         }

      }

      // $FF: synthetic method
      PinCallbackHandler(Object var1) {
         this();
      }
   }
}
