package com.itsinbox.smartbox.gui;

import com.itsinbox.smartbox.SmartBox.Environment;
import com.itsinbox.smartbox.logic.SigningLogic;
import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.logic.SmartCardReader;
import com.itsinbox.smartbox.logic.SmartCardReader.ReaderListener;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.HttpUtils;
import com.itsinbox.smartbox.utils.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

public class SignXmlFrame extends JFrame implements ReaderListener {
    private JButton headerButton;
    private JLabel jLabel1;
    private JButton noButton;
    private JPanel panelSign;
    private JLabel statusLabel;
    private JButton yesButton;
    private SmartCard card = null;
    private CardTerminal terminal = null;
    private String personalId;
    private String firstName;
    private String lastName;
    private String alias;
    private final String xmlUrl;
    private final String servletUrl;
    private final String reqKey;
    private final String backUrl;
    private final String jmbgAuth;
    private final String pibAuth;
    private final String taxFormId;
    private final String itemId;
    private final Environment environment;
    private final String baseUrl;

    public SignXmlFrame(Environment environment, String baseUrl, String reqKey, String xmlUrl, String servletUrl, String backUrl, String jmbgAuth, String pibAuth, String taxFormId, String itemId) {
        this.environment = environment;
        this.baseUrl = baseUrl;
        this.reqKey = reqKey;
        this.xmlUrl = xmlUrl;
        this.servletUrl = servletUrl;
        this.backUrl = backUrl;
        this.jmbgAuth = jmbgAuth;
        this.pibAuth = pibAuth;
        this.taxFormId = taxFormId;
        this.itemId = itemId;
        this.initComponents();
        this.initVisuals();
    }

    private void initComponents() {
        this.panelSign = new JPanel();
        this.yesButton = new JButton();
        this.headerButton = new JButton();
        this.statusLabel = new JLabel();
        this.jLabel1 = new JLabel();
        this.noButton = new JButton();
        this.setMaximumSize(new Dimension(977, 375));
        this.setMinimumSize(new Dimension(977, 375));
        this.setResizable(false);
        this.setSize(new Dimension(977, 375));
        this.panelSign.setPreferredSize(new Dimension(953, 346));
        this.yesButton.setBackground(new Color(210, 44, 52));
        this.yesButton.setFont(new Font("Tahoma", 0, 18));
        this.yesButton.setForeground(new Color(255, 255, 255));
        this.yesButton.setText("ДА");
        this.yesButton.setBorder((Border)null);
        this.yesButton.setBorderPainted(false);
        this.yesButton.setContentAreaFilled(false);
        this.yesButton.setFocusPainted(false);
        this.yesButton.setMaximumSize(new Dimension(200, 50));
        this.yesButton.setMinimumSize(new Dimension(200, 50));
        this.yesButton.setOpaque(true);
        this.yesButton.setPreferredSize(new Dimension(200, 50));
        this.yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SignXmlFrame.this.yesButtonActionPerformed(evt);
            }
        });
        this.headerButton.setIcon(new ImageIcon(this.getClass().getResource("/resources/headerTest.png")));
        this.headerButton.setBorder((Border)null);
        this.headerButton.setBorderPainted(false);
        this.headerButton.setContentAreaFilled(false);
        this.headerButton.setFocusPainted(false);
        this.headerButton.setFocusable(false);
        this.headerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SignXmlFrame.this.headerButtonActionPerformed(evt);
            }
        });
        this.statusLabel.setFont(new Font("Arial", 0, 13));
        this.statusLabel.setHorizontalAlignment(0);
        this.statusLabel.setText("Молимо за стрпљење током потписивања пријаве.");
        this.jLabel1.setFont(new Font("Arial", 1, 16));
        this.jLabel1.setHorizontalAlignment(0);
        this.jLabel1.setText("Да ли сте сигурни да желите да потпишете и поднесете пријаву?\t");
        this.noButton.setBackground(new Color(210, 44, 52));
        this.noButton.setFont(new Font("Tahoma", 0, 18));
        this.noButton.setForeground(new Color(255, 255, 255));
        this.noButton.setText("НЕ");
        this.noButton.setBorder((Border)null);
        this.noButton.setBorderPainted(false);
        this.noButton.setContentAreaFilled(false);
        this.noButton.setFocusPainted(false);
        this.noButton.setMaximumSize(new Dimension(200, 50));
        this.noButton.setMinimumSize(new Dimension(200, 50));
        this.noButton.setOpaque(true);
        this.noButton.setPreferredSize(new Dimension(200, 50));
        this.noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SignXmlFrame.this.noButtonActionPerformed(evt);
            }
        });
        GroupLayout panelSignLayout = new GroupLayout(this.panelSign);
        this.panelSign.setLayout(panelSignLayout);
        panelSignLayout.setHorizontalGroup(panelSignLayout.createParallelGroup(Alignment.LEADING).addGroup(panelSignLayout.createSequentialGroup().addContainerGap().addGroup(panelSignLayout.createParallelGroup(Alignment.LEADING).addComponent(this.statusLabel, -1, -1, 32767).addGroup(panelSignLayout.createSequentialGroup().addGroup(panelSignLayout.createParallelGroup(Alignment.LEADING, false).addComponent(this.headerButton, -1, -1, 32767).addComponent(this.jLabel1, -1, -1, 32767).addGroup(panelSignLayout.createSequentialGroup().addGap(334, 334, 334).addComponent(this.yesButton, -2, 120, -2).addGap(18, 18, 18).addComponent(this.noButton, -2, 120, -2))).addGap(0, 4, 32767))).addContainerGap()));
        panelSignLayout.setVerticalGroup(panelSignLayout.createParallelGroup(Alignment.LEADING).addGroup(panelSignLayout.createSequentialGroup().addContainerGap().addComponent(this.headerButton).addGap(45, 45, 45).addComponent(this.jLabel1).addGap(30, 30, 30).addGroup(panelSignLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.yesButton, -2, -1, -2).addComponent(this.noButton, -2, -1, -2)).addGap(18, 18, 18).addComponent(this.statusLabel).addContainerGap(38, 32767)));
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.panelSign, -2, -1, -2).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.panelSign, -2, -1, -2).addContainerGap(-1, 32767)));
    }

    private void yesButtonActionPerformed(ActionEvent evt) {
        this.disableSignButton();
        Thread t = new Thread(new Runnable() {
            public void run() {
                KeyStore keyStore = SignXmlFrame.this.loadKeyStore();
                if (keyStore != null) {
                    SignXmlFrame.this.statusLabel.setText("Потписивање пријаве у току. Молимо за стрпљење.");
                    SigningLogic sl = new SigningLogic();
                    sl.setChosenAlias(SignXmlFrame.this.alias);
                    sl.setXml(SignXmlFrame.this.xmlUrl);
                    sl.setCard(SignXmlFrame.this.card);
                    sl.setFirstName(SignXmlFrame.this.firstName);
                    sl.setLastName(SignXmlFrame.this.lastName);
                    sl.setPersonalId(SignXmlFrame.this.personalId);
                    if (sl.signXml(keyStore)) {
                        SignXmlFrame.this.statusLabel.setText("Пријава успешно потписана. Шаљем...");

                        try {
                            String signatureString = sl.getSignatureStr();
                            if (signatureString != null) {
                                SignXmlFrame.this.sendSignedXML(sl.getSignatureStr());
                                SignXmlFrame.this.statusLabel.setText("Потписана пријава је послата.");
                                SignXmlFrame.this.card.disconnect();
                                System.exit(0);
                            } else {
                                SignXmlFrame.this.statusLabel.setText("Грешка приликом потписивања пријаве!");
                            }
                        } catch (Exception var4) {
                            SignXmlFrame.this.statusLabel.setText("Грешка у комуникацији са сервером.");
                        }
                    } else {
                        SignXmlFrame.this.statusLabel.setText("Грешка приликом потписивања пријаве!");
                    }
                }

            }
        });
        t.start();
    }

    private void headerButtonActionPerformed(ActionEvent evt) {
    }

    private void noButtonActionPerformed(ActionEvent evt) {
        System.exit(0);
    }

    public void init() {
        this.setDefaultCloseOperation(3);
        this.statusLabel.setText("");
        this.initTerminal();
        this.initCard();
    }

    private void initVisuals() {
        this.setTitle("еПорези 1.0");
        this.setLocationRelativeTo((Component)null);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/app.png")));
        if (this.environment == Environment.PRODUCTION) {
            this.headerButton.setIcon(new ImageIcon(this.getClass().getResource("/resources/header.png")));
        } else {
            this.headerButton.setIcon(new ImageIcon(this.getClass().getResource("/resources/headerTest.png")));
        }

    }

    private void initTerminal() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            this.terminal = (CardTerminal)terminals.get(0);
            this.statusLabel.setText("Картица није пронађена. Молим, убаците картицу у читач.");
            this.disableSignButton();
        } catch (Exception var3) {
            Utils.logMessage("No reader! \n" + var3.toString());
            this.statusLabel.setText("Читач картица није пронађен.");
            this.disableSignButton();
        }

    }

    private void initCard() {
        if (this.terminal != null) {
            SmartCardReader reader = new SmartCardReader(this.terminal);
            reader.addCardListener(this);
        } else {
            this.initTerminalCheck();
        }

    }

    private void initTerminalCheck() {
        final Timer terminalCheckTimer = new Timer();
        TimerTask terminalCheckTask = new TimerTask() {
            public void run() {
                SignXmlFrame.this.initTerminal();
                if (SignXmlFrame.this.terminal != null) {
                    terminalCheckTimer.cancel();
                    terminalCheckTimer.purge();
                    SignXmlFrame.this.initCard();
                }

            }
        };
        terminalCheckTimer.schedule(terminalCheckTask, 1000L, 1000L);
    }

    private KeyStore loadKeyStore() {
        KeyStore keyStore = null;

        try {
            keyStore = this.card.loadKeyStore((char[])null);
        } catch (IOException var4) {
            Utils.logMessage("Error " + var4);
            String errorMsg = "Блокирана картица.";
            if (var4.getCause() != null && var4.getCause().getCause() != null) {
                if ("CKR_PIN_INCORRECT".equals(var4.getCause().getCause().getLocalizedMessage())) {
                    errorMsg = "Погрешан ПИН!";
                } else if ("CKR_PIN_LOCKED".equals(var4.getCause().getCause().getLocalizedMessage())) {
                    errorMsg = "Блокирана картица.";
                }
            }

            this.statusLabel.setText("<html>" + errorMsg + "</html>");
        } catch (CertificateException | KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException var5) {
            Utils.logMessage("Error " + var5);
            this.statusLabel.setText("Дисфункционална картица.");
            return null;
        }

        if (keyStore == null) {
            this.statusLabel.setText("<html>Грешка у одабиру<br>сертификационог тела<br>или његовом софтверу.</html>");
            return null;
        } else {
            this.alias = SmartCardLogic.findAlias(keyStore);
            if (this.alias == null) {
                this.statusLabel.setText("Дошло је до грешке приликом читања сертификата. Молим, покушајте поново.");
                return null;
            } else {
                this.card.setKeyStore(keyStore);
                return keyStore;
            }
        }
    }

    private String getCertificateInfo(String alias) {
        StringBuilder text = new StringBuilder();

        try {
            if (this.card != null) {
                StringBuilder textAreaContent = new StringBuilder();
                Certificate[] chain = this.card.getKeyStore().getCertificateChain(alias);
                if (chain.length > 0) {
                    X509Certificate firstInChain = (X509Certificate)chain[0];
                    String dn = firstInChain.getSubjectX500Principal().getName();

                    try {
                        LdapName ldapDN = new LdapName(dn);
                        Iterator var8 = ldapDN.getRdns().iterator();

                        while(var8.hasNext()) {
                            Rdn rdn = (Rdn)var8.next();
                            if (rdn.getType().equals("CN")) {
                                String cn = rdn.getValue().toString();
                                this.personalId = SmartCardLogic.extractPersonalId(cn);
                                if (cn != null) {
                                    String[] cnsplit = cn.split(" ");
                                    this.firstName = cnsplit[0];
                                    if (cnsplit.length > 1) {
                                        this.lastName = cnsplit[1];
                                    }
                                }
                            }
                        }
                    } catch (InvalidNameException var12) {
                        Utils.logMessage("Error " + var12);
                    }

                    textAreaContent.append(this.firstName).append(" ").append(this.lastName).append(" [").append(this.personalId).append("]");
                    text.append(textAreaContent.toString());
                } else {
                    text.append("Нема података о кориснику");
                }
            }
        } catch (KeyStoreException var13) {
            Utils.logMessage("Error " + var13);
        }

        return text.toString();
    }

    private void sendSignedXML(String signature) throws Exception {
        String url = this.baseUrl + this.servletUrl;
        HashMap<String, String> params = new HashMap();
        params.put("type", "send");
        params.put("action", "no");
        params.put("reqKey", this.reqKey);
        params.put("id", this.taxFormId);
        params.put("itemId", this.itemId);
        params.put("backUrl", this.backUrl);
        params.put("jmbgAuth", this.jmbgAuth);
        params.put("pibAuth", this.pibAuth);
        params.put("signature", signature);
        HttpUtils.sendHttpPost(url, params);
    }

    private void enableSignButton() {
        this.yesButton.setEnabled(true);
        this.yesButton.setBackground(new Color(210, 44, 52));
    }

    private void disableSignButton() {
        this.yesButton.setEnabled(false);
        this.yesButton.setBackground(new Color(214, 215, 216));
    }

    public void inserted(SmartCard card) {
        this.card = card;
        this.statusLabel.setText("Читач и картица препознати.");
        this.enableSignButton();
    }

    public void removed() {
        this.card = null;
        this.statusLabel.setText("Картица није пронађена. Молим, убаците картицу у читач.");
        this.disableSignButton();
    }
}
