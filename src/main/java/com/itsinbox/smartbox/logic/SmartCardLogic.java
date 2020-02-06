package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.utils.Utils;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class SmartCardLogic {

   public static String findAlias(KeyStore keyStore) {
      String ret = null;
      ArrayList aliasList = new ArrayList();

      try {
         Enumeration ex = keyStore.aliases();

         while(ex.hasMoreElements()) {
            String aliasKey = (String)ex.nextElement();
            if(keyStore.isKeyEntry(aliasKey)) {
               Utils.logMessage("Alias: " + aliasKey);
               aliasList.add(aliasKey);
            }
         }
      } catch (KeyStoreException var5) {
         Utils.logMessage("Error while finding alias: " + var5.getMessage());
      }

      if(!aliasList.isEmpty()) {
         ret = findCorrectAlias(keyStore, aliasList);
      }

      return ret;
   }

   public static String extractPersonalId(String dn) {
      String id = "";
      if(dn != null) {
         Pattern pat = Pattern.compile("[0-9]{13}");
         Matcher matcher = pat.matcher(dn);
         if(matcher.find()) {
            Utils.logMessage("PERSONAL ID: " + matcher.group(0));
            id = matcher.group(0);
         }
      }

      return id;
   }

   public static String findCorrectAlias(KeyStore keyStore, List aliasList) {
      String ret = null;
      Iterator var3 = aliasList.iterator();

      while(var3.hasNext()) {
         String alias = (String)var3.next();
         String personalId = "";
         boolean ku = false;

         try {
            Certificate[] ex = keyStore.getCertificateChain(alias);
            if(ex.length > 0) {
               X509Certificate i = (X509Certificate)ex[0];
               String chainMember = i.getSubjectX500Principal().getName();
               Utils.logMessage("DN: " + chainMember);

               try {
                  LdapName keyUsage = new LdapName(chainMember);
                  Iterator ex1 = keyUsage.getRdns().iterator();

                  while(ex1.hasNext()) {
                     Rdn rdn = (Rdn)ex1.next();
                     if(rdn.getType().equals("CN")) {
                        String cn = rdn.getValue().toString();
                        personalId = extractPersonalId(cn);
                     }
                  }
               } catch (InvalidNameException var14) {
                  Utils.logMessage("Error while finding correct alias: " + var14.getMessage());
               }
            }

            for(int var16 = 0; var16 < ex.length; ++var16) {
               X509Certificate var17 = (X509Certificate)ex[var16];
               boolean[] var18 = var17.getKeyUsage();
               if(var18[0]) {
                  Utils.logMessage("digitalSignature");
               }

               if(var18[1]) {
                  Utils.logMessage("nonRepudiation");
               }

               if(var18[2]) {
                  Utils.logMessage("keyEncypherment");
               }

               if(var18[3]) {
                  Utils.logMessage("dataEncypherment");
               }

               if(var18[4]) {
                  Utils.logMessage("keyAgreement");
               }

               if(var18[5]) {
                  Utils.logMessage("keyCertSign");
               }

               if(var18[6]) {
                  Utils.logMessage("cRLSign");
               }

               if(var18[7]) {
                  Utils.logMessage("encipherOnly");
               }

               if(var18[8]) {
                  Utils.logMessage("decipherOnly");
               }

               ku = ku || var18[0] || var18[1];
            }
         } catch (KeyStoreException var15) {
            Utils.logMessage("Error while finding correct alias: " + var15.getMessage());
         }

         if(personalId.length() == 13 && ku) {
            ret = alias;
         }
      }

      return ret;
   }

   public static void _fixAliases(KeyStore keyStore) {
      try {
         Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
         field.setAccessible(true);
         KeyStoreSpi keyStoreVeritable = (KeyStoreSpi)field.get(keyStore);
         if("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
            field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
            field.setAccessible(true);
            Collection ex = (Collection)field.get(keyStoreVeritable);
            Iterator var7 = ex.iterator();

            while(var7.hasNext()) {
               Object entry = var7.next();
               field = entry.getClass().getDeclaredField("certChain");
               field.setAccessible(true);
               X509Certificate[] certificates = (X509Certificate[])((X509Certificate[])field.get(entry));
               String hashCode = Integer.toString(certificates[0].hashCode());
               field = entry.getClass().getDeclaredField("alias");
               field.setAccessible(true);
               String alias = (String)field.get(entry);
               if(!alias.equals(hashCode)) {
                  field.set(entry, alias.concat(" - ").concat(hashCode));
               }
            }
         }
      } catch (Exception var9) {
         Utils.logMessage("Error while fixing alias: " + var9.getMessage());
      }

   }
}
