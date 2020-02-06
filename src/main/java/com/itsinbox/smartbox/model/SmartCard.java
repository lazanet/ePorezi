package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.utils.Utils;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

public abstract class SmartCard {

   private Card card;
   private CardChannel channel;
   private KeyStore keyStore;
   private Enumeration aliases;
   public static final int OS_WIN = 1;
   public static final int OS_LIN = 2;
   public static final int OS_MAC = 4;


   public abstract String getVendorName();

   public abstract void sendAtr(String var1, String var2);

   public static int getOsFamily() {
      String var0 = System.getProperty("os.name").toLowerCase();
      return var0.indexOf("win") >= 0?1:(var0.indexOf("mac") < 0 && var0.indexOf("darwin") < 0?(var0.indexOf("nux") >= 0?2:1):4);
   }

   public String introduceYourself() {
      byte[] var1 = this.card.getATR().getBytes();
      return Utils.bytes2HexString((byte[])var1);
   }

   public void disconnect() throws CardException {
      this.disconnect(false);
   }

   public void disconnect(boolean var1) throws CardException {
      this.card.disconnect(var1);
      this.card = null;
   }

   public Card getCard() {
      return this.card;
   }

   public void setCard(Card var1) {
      this.card = var1;
   }

   public CardChannel getChannel() {
      return this.channel;
   }

   public void setChannel(CardChannel var1) {
      this.channel = var1;
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   public void setKeyStore(KeyStore var1) {
      this.keyStore = var1;
   }

   public abstract KeyStore loadKeyStore(char[] var1) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException;

   public Enumeration getAliases() {
      try {
         this.aliases = this.getKeyStore().aliases();
      } catch (KeyStoreException var2) {
         this.aliases = null;
         Utils.logMessage("Error while getting aliases: " + var2.getMessage());
      }

      return this.aliases;
   }

   public X509Certificate getCertificate(String var1) {
      X509Certificate var2;
      if(this.getKeyStore() != null) {
         try {
            var2 = (X509Certificate)this.getKeyStore().getCertificate(var1);
         } catch (KeyStoreException var4) {
            var2 = null;
            Utils.logMessage("Error while getting certificate: " + var4.getMessage());
         }
      } else {
         var2 = null;
      }

      return var2;
   }

   public abstract String getKeyStoreProvider();

   public abstract String getKeyStoreType();
}
