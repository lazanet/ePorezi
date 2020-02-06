package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.model.AnyCard;
import com.itsinbox.smartbox.model.PKCS11CardEdge;
import com.itsinbox.smartbox.model.PKCS11SafeSign;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import javax.smartcardio.Card;

public class SmartCardFactory {

   private static SmartCardFactory instance;


   public static SmartCardFactory getInstance() {
      if(instance == null) {
         instance = new SmartCardFactory();
      }

      return instance;
   }

   public SmartCard getSmartCard(Card var1) {
      int var3 = SmartCard.getOsFamily();
      Object var2;
      if(var3 == 1) {
         var2 = new AnyCard();
         Utils.logMessage("CertBody: ANY (MS CAPI)");
      } else {
         byte[] var4 = var1.getATR().getBytes();
         String var5 = Utils.bytes2HexString(var4);
         if(this.isKnownATR(var5, PKCS11CardEdge.KNOWN_ATRS)) {
            var2 = new PKCS11CardEdge();
            Utils.logMessage("CertBody: MUP/PKS (CardEdge PKCS11)");
         } else {
            if(!this.isKnownATR(var5, PKCS11SafeSign.KNOWN_ATRS)) {
               Utils.logMessage("CertBody: UNKNOWN (trying PKCS11)");
               return null;
            }

            var2 = new PKCS11SafeSign();
            Utils.logMessage("CertBody: Posta (SafeSign PKCS11)");
         }
      }

      ((SmartCard)var2).setCard(var1);
      ((SmartCard)var2).setChannel(var1.getBasicChannel());
      return (SmartCard)var2;
   }

   private boolean isKnownATR(String var1, String[] var2) {
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if(var1.equals(var6)) {
            return true;
         }
      }

      return false;
   }
}
