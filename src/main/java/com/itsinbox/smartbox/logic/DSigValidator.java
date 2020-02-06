package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.logic.KeyValueKeySelector;
import com.itsinbox.smartbox.utils.Utils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DSigValidator {

   public void validate() {
      DocumentBuilderFactory dbf = null;
      try {
         dbf = DocumentBuilderFactory.class.getDeclaredConstructor().newInstance();
         dbf.setNamespaceAware(true);
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }

      try {
         Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("C:\\Users\\schmee\\Downloads\\Primer_PPPDV_XML_Prilozi.xml"));
         NodeList ex = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
         if(ex.getLength() == 0) {
            return;
         }

         String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
         XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", (Provider)Class.forName(providerName).getDeclaredConstructor().newInstance());
         DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), ex.item(0));
         XMLSignature signature = fac.unmarshalXMLSignature(valContext);
         boolean coreValidity = signature.validate(valContext);
         if(!coreValidity) {
            Utils.logMessage("Error while validating signature!");
         } else {
            Utils.logMessage("Signature passed");
         }
      } catch (XMLSignatureException var9) {
         Utils.logMessage("Error while validating signature: " + var9.getMessage());
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (MarshalException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}
