
import crapcode.*;

import java.awt.Font;
import java.awt.FontFormatException;

import java.awt.event.KeyEvent;

import java.io.InputStream;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

import javax.swing.JTable;

import javax.swing.JOptionPane;


public class Main extends javax.swing.JFrame {
    
    private Font main_font, terminal_font;

    private int target_type; // 1: session, 2: from selection
    private List<String> target_files; // files paths
    
    private static craplog craplog;
    
    private static crapsets.window settings;
    
    private DefaultListModel<String> stat_paths;
    private final DefaultListModel<String> log_files, stat_names;
    private int list_acc_index, list_err_index, last_acc_index, last_err_index,
                ip_list_index, req_list_index, res_list_index, ua_list_index, err_list_index, lev_list_index;
    
    
    public Main() {
        
        this.log_files  = new DefaultListModel<>();
        this.stat_names = new DefaultListModel<>();
        this.stat_paths = new DefaultListModel<>();
        this.list_acc_index = this.last_acc_index = this.list_err_index = this.last_err_index =
        this.ip_list_index  = this.req_list_index = this.res_list_index = this.ua_list_index  =
        this.err_list_index = this.lev_list_index = -1;
        
        initComponents();
        
        this.target_type  = 1;
        this.target_files = new ArrayList<>();
        this.jListSELECTION.setModel(log_files);
        this.jListACC.setModel(stat_names);
        this.jListERR.setModel(stat_names);
        this.jProgressBarWORK.setValue(0);
        
        Main.craplog = new craplog();
        
        this.initArgsPanel();
        this.initStyle();
        
        Main.settings = new crapsets.window( Main.craplog, this.main_font );
    }
    
    
    private void initArgsPanel() {
        if ( Main.craplog.getMakeSessions() == true ) {
            this.jCheckBoxSTATSsess.setSelected(true);
        }
        if ( Main.craplog.getUpdateGlobals()== true ) {
            this.jCheckBoxSTATSglob.setSelected(true);
        }
        if ( Main.craplog.getParseError() == true ) {
            this.jCheckBoxLOGSerr.setSelected(true);
        }
        if ( Main.craplog.getParseAccess() == true ) {
            this.jCheckBoxLOGSacc.setSelected(true);
            if ( Main.craplog.getParseIP() == true ) {
                this.jCheckBoxACCip.setSelected(true);
            }
            if ( Main.craplog.getParseREQ() == true ) {
                this.jCheckBoxACCreq.setSelected(true);
            }
            if ( Main.craplog.getParseRES() == true ) {
                this.jCheckBoxACCres.setSelected(true);
            }
            if ( Main.craplog.getParseUA() == true ) {
                this.jCheckBoxACCua.setSelected(true);
            }
            if (Main.craplog.getParseIP()  == false
            &&  Main.craplog.getParseREQ() == false
            &&  Main.craplog.getParseRES() == false
            &&  Main.craplog.getParseUA()  == false ) {
                Main.craplog.setParseAccessFalse();
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxACCip.setEnabled(false);
                this.jCheckBoxACCreq.setEnabled(false);
                this.jCheckBoxACCres.setEnabled(false);
                this.jCheckBoxACCua.setEnabled(false);
            }
        } else {
            Main.craplog.setParseIPFalse();
            this.jCheckBoxACCip.setEnabled(false);
            Main.craplog.setParseREQFalse();
            this.jCheckBoxACCreq.setEnabled(false);
            Main.craplog.setParseRESFalse();
            this.jCheckBoxACCres.setEnabled(false);
            Main.craplog.setParseUAFalse();
            this.jCheckBoxACCua.setEnabled(false);
        }
        // enable start if needed
        if (Main.craplog.getMakeSessions()  == true
        ||  Main.craplog.getUpdateGlobals() == true ) {
            if (Main.craplog.getParseAccess() == true
            ||  Main.craplog.getParseError()  == true ) {
                this.jButtonSTART.setEnabled(true);
            }
            if ( Main.craplog.getPostBackup() == true ) {
                this.jCheckBoxPOSTbackup.setSelected(true);
            }
            if ( Main.craplog.getPostArchive() == true ) {
                this.jCheckBoxPOSTarchive.setSelected(true);
            }
            this.jComboBoxARCHIVEtype.setSelectedIndex( Main.craplog.getPostArchiveType() );
            if ( Main.craplog.getPostDelete() == true ) {
                this.jCheckBoxPOSTdelete.setSelected(true);
            }
            if ( Main.craplog.getPostTrash() == true ) {
                this.jCheckBoxPOSTtrash.setSelected(true);
            }
        } else {
            this.jCheckBoxLOGSacc.setEnabled(false);
            this.jCheckBoxLOGSerr.setEnabled(false);
            Main.craplog.setPostBackupFalse();
            this.jCheckBoxPOSTbackup.setEnabled(false);
            Main.craplog.setPostArchiveFalse();
            this.jCheckBoxPOSTarchive.setEnabled(false);
            this.jComboBoxARCHIVEtype.setEnabled(false);
            Main.craplog.setPostDeleteFalse();
            this.jCheckBoxPOSTdelete.setEnabled(false);
            Main.craplog.setPostTrashFalse();
            this.jCheckBoxPOSTtrash.setEnabled(false);
        }
    }
    
    
    private void initStyle() {
        // initialize window style
        try {
            InputStream fs = getClass().getResourceAsStream("/fonts/Metropolis.ttf");
            this.main_font = Font.createFont( Font.TRUETYPE_FONT, fs );
            
        } catch (FontFormatException | IOException e) {
            System.out.println("Unable to load font: 'Metropolis.ttf'");
        }
        try {
            InputStream fs = getClass().getResourceAsStream("/fonts/3270.ttf");
            this.terminal_font = Font.createFont( Font.TRUETYPE_FONT, fs );
            
        } catch (FontFormatException | IOException e) {
            System.out.println("Unable to load font: '3270.ttf'");
        }
        // load fonts
        // menu bar
        this.jMenuBarMENU.setFont( this.main_font.deriveFont(13f) );
            this.jMenuPREFERENCES.setFont( this.main_font.deriveFont(13f) );
                this.jMenuItemSETTINGS.setFont( this.main_font.deriveFont(13f) );
            this.jMenuUTILITIES.setFont( this.main_font.deriveFont(13f) );
                this.jMenuItemNOTE.setFont( this.main_font.deriveFont(13f) );
                this.jMenuItemUPDATES.setFont( this.main_font.deriveFont(13f) );
        // main panel
        this.jTabbedPaneMAIN.setFont( this.main_font.deriveFont(15f) );
            // make statistics
            this.jSplitPaneMAKE.setFont( this.main_font.deriveFont(13f) );
                // terminal emulator
                this.jTextAreaOUTPUT.setFont( this.terminal_font.deriveFont(1,17f) );
                // work panel
                this.jTabbedPaneWORK.setFont( this.main_font.deriveFont(14f) );
                    // single session panel
                    this.jPanelSESSION.setFont( this.main_font.deriveFont(13f) );
                        this.jLabelARGS.setFont( this.main_font.deriveFont(18f) );
                        this.jLabelSTATS.setFont( this.main_font.deriveFont(2,15f) );
                            this.jCheckBoxSTATSsess.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxSTATSglob.setFont( this.main_font.deriveFont(13f) );
                        this.jLabelLOGS.setFont( this.main_font.deriveFont(2,15f) );
                            this.jCheckBoxLOGSacc.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxLOGSerr.setFont( this.main_font.deriveFont(13f) );
                        this.jLabelACC.setFont( this.main_font.deriveFont(2,15f) );
                            this.jCheckBoxACCip.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxACCreq.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxACCres.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxACCua.setFont( this.main_font.deriveFont(13f) );
                        this.jLabelPOST.setFont( this.main_font.deriveFont(2,15f) );
                            this.jCheckBoxPOSTbackup.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxPOSTarchive.setFont( this.main_font.deriveFont(13f) );
                            this.jComboBoxARCHIVEtype.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxPOSTdelete.setFont( this.main_font.deriveFont(13f) );
                            this.jCheckBoxPOSTtrash.setFont( this.main_font.deriveFont(13f) );
                        this.jButtonSTART.setFont( this.main_font.deriveFont(Font.BOLD,24f) );
                    // selection
                    this.jPanelSELECTION.setFont( this.main_font.deriveFont(13f) );
                        this.jListSELECTION.setFont( this.main_font.deriveFont(14f) );
                        this.jLabelSinfo1.setFont( this.main_font.deriveFont(2,17f) );
                        this.jLabelSinfo2.setFont( this.main_font.deriveFont(2,15f) );
                        this.jLabelSinfo3.setFont( this.main_font.deriveFont(2,15f) );
                        this.jLabelSinfo4.setFont( this.main_font.deriveFont(2,15f) );
                // view stats panel
                this.jTabbedPaneSTATS.setFont( this.main_font.deriveFont(14f) );
                    // access stats
                    this.jListACC.setFont( this.main_font.deriveFont(14f) );
                    this.jTabbedPaneACCESSview.setFont( this.main_font.deriveFont(14f) );
                        this.jTableIP.setFont( this.main_font.deriveFont(14f) );
                        this.jTableREQ.setFont( this.main_font.deriveFont(14f) );
                        this.jTableRES.setFont( this.main_font.deriveFont(14f) );
                        this.jTableUA.setFont( this.main_font.deriveFont(14f) );
                    // errors stats
                    this.jListERR.setFont( this.main_font.deriveFont(14f) );
                    this.jTabbedPaneERRORSview.setFont( this.main_font.deriveFont(14f) );
                        this.jTableERR.setFont( this.main_font.deriveFont(14f) );
                        this.jTableLEV.setFont( this.main_font.deriveFont(14f) );
                // view logs panel
                this.jSplitPaneLOGS.setFont( this.main_font.deriveFont(14f) );
                    // select side
                    this.jTableLOGSlist.setFont( this.main_font.deriveFont(14f) );
                    this.jButtonLOGSview.setFont( this.main_font.deriveFont(18f) );
                    // output side
                    this.jTextPaneLOGS.setFont( this.terminal_font.deriveFont(1,15f) );
    }
    
    
    
    private void handleListSelection() {
        try {
            String stat_type = this.jTabbedPaneSTATS.getTitleAt( this.jTabbedPaneSTATS.getSelectedIndex() ).toLowerCase();
            switch (stat_type) {
                case "access":
                    switch ( this.jTabbedPaneACCESSview.getSelectedIndex() ) {
                        case 0:
                            // IP
                            this.jListACC.setSelectedIndex( this.ip_list_index );
                            break;
                        case 1:
                            // REQ
                            this.jListACC.setSelectedIndex( this.req_list_index );
                            break;
                        case 2:
                            // RES
                            this.jListACC.setSelectedIndex( this.res_list_index );
                            break;
                        case 3:
                            // UA
                            this.jListACC.setSelectedIndex( this.ua_list_index );
                            break;
                        default:
                            // ???
                            System.out.println("Unrecognized selection index: "+this.jTabbedPaneACCESSview.getSelectedIndex());
                    }   
                    this.list_acc_index = this.last_acc_index = this.jListACC.getSelectedIndex();
                    break;
                case "errors":
                    switch ( this.jTabbedPaneERRORSview.getSelectedIndex() ) {
                        case 0:
                            // ERR
                            this.jListERR.setSelectedIndex( this.err_list_index );
                            break;
                        case 1:
                            // LEV
                            this.jListERR.setSelectedIndex( this.lev_list_index );
                            break;
                        default:
                            // ???
                            System.out.println("Unrecognized selection index: "+this.jTabbedPaneACCESSview.getSelectedIndex());
                    }   
                    this.list_err_index = this.last_err_index = this.jListERR.getSelectedIndex();
                    break;
                default:
                    // ???
                    System.out.println("Unrecognized stat-type: "+stat_type);
                    break;
            }
            this.showTable();
        } catch (Exception e) {
            // catched during initialization
        }
    }
    
    private void showTable() {
        try {
            // retrieve list of stat-files and initially display GLOBALS
            String stat_type = this.jTabbedPaneSTATS.getTitleAt( this.jTabbedPaneSTATS.getSelectedIndex() ).toLowerCase();
            String stat_field;
            int list_index = 0;
            switch (stat_type) {
                case "access":
                    //this.list_acc_index = this.jListACC.getSelectedIndex();
                    switch ( this.jTabbedPaneACCESSview.getSelectedIndex() ) {
                        case 0:
                            stat_field = "IP";
                            list_index = this.ip_list_index = this.jListACC.getSelectedIndex();
                            break;
                        case 1:
                            stat_field = "REQ";
                            list_index = this.req_list_index = this.jListACC.getSelectedIndex();
                            break;
                        case 2:
                            stat_field = "RES";
                            list_index = this.res_list_index = this.jListACC.getSelectedIndex();
                            break;
                        case 3:
                            stat_field = "UA";
                            list_index = this.ua_list_index = this.jListACC.getSelectedIndex();
                            break;
                        default:
                            stat_field = "";
                            System.out.println("Unrecognized selection index: "+this.jTabbedPaneACCESSview.getSelectedIndex());
                    }
                    break;
                case "errors":
                    //list_index = this.list_err_index = this.jListERR.getSelectedIndex();
                    stat_type = stat_type.substring(0, stat_type.length()-1);
                    switch ( this.jTabbedPaneERRORSview.getSelectedIndex() ) {
                        case 0:
                            stat_field = "ERR";
                            list_index = this.err_list_index = this.list_err_index;
                            break;
                        case 1:
                            stat_field = "LEV";
                            list_index = this.lev_list_index = this.list_err_index;
                            break;
                        default:
                            stat_field = "";
                            System.out.println("Unrecognized selection index: "+this.jTabbedPaneACCESSview.getSelectedIndex());
                    }   
                    break;
                default:
                    stat_field = "";
                    System.out.println("Unrecognized stat-type: "+stat_type);
                    break;
            }
            ArrayList<DefaultListModel<String>> tmp
                = crapview.getStatsList(Main.craplog.getStatsDir(), stat_type, stat_field);
            this.stat_paths = tmp.get(1);
            this.stat_names.clear();
            for ( int i=0; i<tmp.get(0).size(); i++) {
                stat_names.addElement( tmp.get(0).get(i) );
            }
            JTable table;
            switch ( stat_field ) {
                case "IP":
                    table = this.jTableIP;
                    break;
                case "REQ":
                    table = this.jTableREQ;
                    break;
                case "RES":
                    table = this.jTableRES;
                    break;
                case "UA":
                    table = this.jTableUA;
                    break;
                case "ERR":
                    table = this.jTableERR;
                    break;
                case "LEV":
                    table = this.jTableLEV;
                    break;
                default:
                    table = new JTable();
            }
            if ( this.list_acc_index < 0 ) {
                this.list_acc_index = 0;
            }
            if ( this.list_err_index < 0 ) {
                this.list_err_index = 0;
            }
            crapview.MakeTable(stat_paths.get(list_index), stat_field, table);
            tmp.clear();
        
        } catch (Exception e) {
            // catched during initialization
        }
    }
    
    
    private void viewLogsList() {
        // load an updated log files list
        crapview.makeViewLogsList(
            Main.craplog.getLogsDir(),
            this.log_files,
            this.jTableLOGSlist );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPaneMAIN = new javax.swing.JTabbedPane();
        jSplitPaneMAKE = new javax.swing.JSplitPane();
        jScrollPaneMAKE = new javax.swing.JScrollPane();
        jPanelMAKE = new javax.swing.JPanel();
        jTabbedPaneWORK = new javax.swing.JTabbedPane();
        jPanelSESSION = new javax.swing.JPanel();
        jPanelARGSpadl = new javax.swing.JPanel();
        jPanelARGSpadr = new javax.swing.JPanel();
        jPanelARGSbox = new javax.swing.JPanel();
        jLabelARGS = new javax.swing.JLabel();
        jSeparatorA1 = new javax.swing.JSeparator();
        jLabelLOGS = new javax.swing.JLabel();
        jCheckBoxLOGSacc = new javax.swing.JCheckBox();
        jCheckBoxLOGSerr = new javax.swing.JCheckBox();
        jSeparatorA2 = new javax.swing.JSeparator();
        jLabelSTATS = new javax.swing.JLabel();
        jCheckBoxSTATSsess = new javax.swing.JCheckBox();
        jCheckBoxSTATSglob = new javax.swing.JCheckBox();
        jSeparatorA3 = new javax.swing.JSeparator();
        jLabelACC = new javax.swing.JLabel();
        jCheckBoxACCip = new javax.swing.JCheckBox();
        jCheckBoxACCreq = new javax.swing.JCheckBox();
        jCheckBoxACCres = new javax.swing.JCheckBox();
        jCheckBoxACCua = new javax.swing.JCheckBox();
        jSeparatorA4 = new javax.swing.JSeparator();
        jLabelPOST = new javax.swing.JLabel();
        jCheckBoxPOSTbackup = new javax.swing.JCheckBox();
        jCheckBoxPOSTarchive = new javax.swing.JCheckBox();
        jComboBoxARCHIVEtype = new javax.swing.JComboBox<>();
        jCheckBoxPOSTdelete = new javax.swing.JCheckBox();
        jCheckBoxPOSTtrash = new javax.swing.JCheckBox();
        jPanelSELECTION = new javax.swing.JPanel();
        jPanelSELpadl = new javax.swing.JPanel();
        jPanelSELpadr = new javax.swing.JPanel();
        jPanelSELbox = new javax.swing.JPanel();
        jSplitPaneSEL = new javax.swing.JSplitPane();
        jScrollPaneSELlist = new javax.swing.JScrollPane();
        jListSELECTION = new javax.swing.JList<>();
        jPanelSELinfo = new javax.swing.JPanel();
        jLabelSinfo1 = new javax.swing.JLabel();
        jSeparatorS1 = new javax.swing.JSeparator();
        jLabelSinfo2 = new javax.swing.JLabel();
        jLabelSinfo3 = new javax.swing.JLabel();
        jLabelSinfo4 = new javax.swing.JLabel();
        jPanelSTART = new javax.swing.JPanel();
        jButtonSTART = new javax.swing.JButton();
        jPanelSTARTpadl = new javax.swing.JPanel();
        jPanelSTARTpadr = new javax.swing.JPanel();
        jProgressBarWORK = new javax.swing.JProgressBar();
        jScrollPaneOUTPUT = new javax.swing.JScrollPane();
        jTextAreaOUTPUT = new javax.swing.JTextArea();
        jTabbedPaneSTATS = new javax.swing.JTabbedPane();
        jSplitPaneACCESS = new javax.swing.JSplitPane();
        jTabbedPaneACCESSview = new javax.swing.JTabbedPane();
        jScrollPaneIP = new javax.swing.JScrollPane();
        jTableIP = new javax.swing.JTable();
        jScrollPaneREQ = new javax.swing.JScrollPane();
        jTableREQ = new javax.swing.JTable();
        jScrollPaneRES = new javax.swing.JScrollPane();
        jTableRES = new javax.swing.JTable();
        jScrollPaneUA = new javax.swing.JScrollPane();
        jTableUA = new javax.swing.JTable();
        jScrollPaneACCESSlist = new javax.swing.JScrollPane();
        jListACC = new javax.swing.JList<>();
        jSplitPaneERRORS = new javax.swing.JSplitPane();
        jTabbedPaneERRORSview = new javax.swing.JTabbedPane();
        jScrollPaneERRview = new javax.swing.JScrollPane();
        jTableERR = new javax.swing.JTable();
        jScrollPaneLEVview = new javax.swing.JScrollPane();
        jTableLEV = new javax.swing.JTable();
        jScrollPaneACCESSlist1 = new javax.swing.JScrollPane();
        jListERR = new javax.swing.JList<>();
        jSplitPaneLOGS = new javax.swing.JSplitPane();
        jScrollPaneLOGSview = new javax.swing.JScrollPane();
        jTextPaneLOGS = new javax.swing.JTextPane();
        jScrollPaneLOGSselect = new javax.swing.JScrollPane();
        jPanelLOGSbox = new javax.swing.JPanel();
        jButtonLOGSview = new javax.swing.JButton();
        jScrollPaneLOGSlist = new javax.swing.JScrollPane();
        jTableLOGSlist = new javax.swing.JTable();
        jMenuBarMENU = new javax.swing.JMenuBar();
        jMenuPREFERENCES = new javax.swing.JMenu();
        jMenuItemSETTINGS = new javax.swing.JMenuItem();
        jMenuUTILITIES = new javax.swing.JMenu();
        jMenuItemNOTE = new javax.swing.JMenuItem();
        jSeparatorMENUut = new javax.swing.JPopupMenu.Separator();
        jMenuItemUPDATES = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CRAPLOG");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPaneMAIN.setFont(new java.awt.Font("Metropolis", 0, 15)); // NOI18N
        jTabbedPaneMAIN.setPreferredSize(new java.awt.Dimension(704, 640));
        jTabbedPaneMAIN.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneMAINStateChanged(evt);
            }
        });

        jSplitPaneMAKE.setDividerLocation(512);
        jSplitPaneMAKE.setPreferredSize(new java.awt.Dimension(704, 640));

        jScrollPaneMAKE.setPreferredSize(new java.awt.Dimension(512, 640));

        jPanelMAKE.setPreferredSize(new java.awt.Dimension(500, 600));

        jTabbedPaneWORK.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTabbedPaneWORK.setPreferredSize(new java.awt.Dimension(496, 500));

        jPanelSESSION.setPreferredSize(new java.awt.Dimension(512, 438));
        jPanelSESSION.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelSESSIONComponentShown(evt);
            }
        });

        javax.swing.GroupLayout jPanelARGSpadlLayout = new javax.swing.GroupLayout(jPanelARGSpadl);
        jPanelARGSpadl.setLayout(jPanelARGSpadlLayout);
        jPanelARGSpadlLayout.setHorizontalGroup(
            jPanelARGSpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );
        jPanelARGSpadlLayout.setVerticalGroup(
            jPanelARGSpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelARGSpadrLayout = new javax.swing.GroupLayout(jPanelARGSpadr);
        jPanelARGSpadr.setLayout(jPanelARGSpadrLayout);
        jPanelARGSpadrLayout.setHorizontalGroup(
            jPanelARGSpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );
        jPanelARGSpadrLayout.setVerticalGroup(
            jPanelARGSpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabelARGS.setFont(new java.awt.Font("Metropolis", 0, 18)); // NOI18N
        jLabelARGS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelARGS.setText("ARGUMENTS");

        jLabelLOGS.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelLOGS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelLOGS.setText(" Log files to parse");
        jLabelLOGS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jCheckBoxLOGSacc.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxLOGSacc.setText("Access logs");
        jCheckBoxLOGSacc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxLOGSacc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLOGSaccActionPerformed(evt);
            }
        });

        jCheckBoxLOGSerr.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxLOGSerr.setText("Error logs");
        jCheckBoxLOGSerr.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxLOGSerr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLOGSerrActionPerformed(evt);
            }
        });

        jLabelSTATS.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelSTATS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelSTATS.setText(" Statistics to be made");
        jLabelSTATS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jCheckBoxSTATSsess.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxSTATSsess.setText("Session");
        jCheckBoxSTATSsess.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxSTATSsess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSTATSsessActionPerformed(evt);
            }
        });

        jCheckBoxSTATSglob.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxSTATSglob.setText("Globals");
        jCheckBoxSTATSglob.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxSTATSglob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSTATSglobActionPerformed(evt);
            }
        });

        jLabelACC.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelACC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelACC.setText("Access logs' fields to parse");
        jLabelACC.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jCheckBoxACCip.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxACCip.setText("IPs");
        jCheckBoxACCip.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxACCip.setPreferredSize(new java.awt.Dimension(112, 24));
        jCheckBoxACCip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxACCipActionPerformed(evt);
            }
        });

        jCheckBoxACCreq.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxACCreq.setText("Requests");
        jCheckBoxACCreq.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxACCreq.setPreferredSize(new java.awt.Dimension(112, 24));
        jCheckBoxACCreq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxACCreqActionPerformed(evt);
            }
        });

        jCheckBoxACCres.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxACCres.setText("Responses");
        jCheckBoxACCres.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxACCres.setPreferredSize(new java.awt.Dimension(112, 24));
        jCheckBoxACCres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxACCresActionPerformed(evt);
            }
        });

        jCheckBoxACCua.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxACCua.setText("User agents");
        jCheckBoxACCua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxACCua.setPreferredSize(new java.awt.Dimension(112, 24));
        jCheckBoxACCua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxACCuaActionPerformed(evt);
            }
        });

        jLabelPOST.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelPOST.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelPOST.setText(" Post-work actions on original files");
        jLabelPOST.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jCheckBoxPOSTbackup.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxPOSTbackup.setText("Backup");
        jCheckBoxPOSTbackup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxPOSTbackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPOSTbackupActionPerformed(evt);
            }
        });

        jCheckBoxPOSTarchive.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxPOSTarchive.setText("Archive");
        jCheckBoxPOSTarchive.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxPOSTarchive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPOSTarchiveActionPerformed(evt);
            }
        });

        jComboBoxARCHIVEtype.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jComboBoxARCHIVEtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "tar.gz", "tar", "zip" }));
        jComboBoxARCHIVEtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxARCHIVEtypeActionPerformed(evt);
            }
        });

        jCheckBoxPOSTdelete.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxPOSTdelete.setText("Delete");
        jCheckBoxPOSTdelete.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxPOSTdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPOSTdeleteActionPerformed(evt);
            }
        });

        jCheckBoxPOSTtrash.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jCheckBoxPOSTtrash.setText("to Trash");
        jCheckBoxPOSTtrash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCheckBoxPOSTtrash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPOSTtrashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelARGSboxLayout = new javax.swing.GroupLayout(jPanelARGSbox);
        jPanelARGSbox.setLayout(jPanelARGSboxLayout);
        jPanelARGSboxLayout.setHorizontalGroup(
            jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelPOST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelARGS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparatorA1)
            .addComponent(jSeparatorA2)
            .addComponent(jLabelLOGS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparatorA3)
            .addComponent(jLabelACC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparatorA4)
            .addComponent(jLabelSTATS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                        .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                                .addComponent(jCheckBoxLOGSacc, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxLOGSerr, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                                .addComponent(jCheckBoxSTATSsess, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxSTATSglob, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                                .addComponent(jCheckBoxACCip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxACCreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxACCres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxACCua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                        .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxPOSTdelete, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxPOSTbackup, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                                .addComponent(jCheckBoxPOSTarchive, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxARCHIVEtype, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBoxPOSTtrash, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanelARGSboxLayout.setVerticalGroup(
            jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelARGSboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelARGS, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorA1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSTATS, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxSTATSsess, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxSTATSglob, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jSeparatorA2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLOGS, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxLOGSacc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxLOGSerr, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparatorA3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelACC, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxACCip, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxACCreq, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxACCres, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxACCua, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparatorA4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPOST, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxPOSTbackup, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxARCHIVEtype, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxPOSTarchive, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelARGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxPOSTdelete)
                    .addComponent(jCheckBoxPOSTtrash))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelSESSIONLayout = new javax.swing.GroupLayout(jPanelSESSION);
        jPanelSESSION.setLayout(jPanelSESSIONLayout);
        jPanelSESSIONLayout.setHorizontalGroup(
            jPanelSESSIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSESSIONLayout.createSequentialGroup()
                .addComponent(jPanelARGSpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelARGSbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelARGSpadr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSESSIONLayout.setVerticalGroup(
            jPanelSESSIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSESSIONLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelSESSIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelARGSpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelARGSpadr, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelARGSbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPaneWORK.addTab("Single session", jPanelSESSION);

        jPanelSELECTION.setPreferredSize(new java.awt.Dimension(512, 430));
        jPanelSELECTION.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanelSELECTIONComponentShown(evt);
            }
        });

        javax.swing.GroupLayout jPanelSELpadlLayout = new javax.swing.GroupLayout(jPanelSELpadl);
        jPanelSELpadl.setLayout(jPanelSELpadlLayout);
        jPanelSELpadlLayout.setHorizontalGroup(
            jPanelSELpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSELpadlLayout.setVerticalGroup(
            jPanelSELpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelSELpadrLayout = new javax.swing.GroupLayout(jPanelSELpadr);
        jPanelSELpadr.setLayout(jPanelSELpadrLayout);
        jPanelSELpadrLayout.setHorizontalGroup(
            jPanelSELpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSELpadrLayout.setVerticalGroup(
            jPanelSELpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPaneSEL.setDividerLocation(180);

        jListSELECTION.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jListSELECTION.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "access.log.1", "error.log.1" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPaneSELlist.setViewportView(jListSELECTION);

        jSplitPaneSEL.setLeftComponent(jScrollPaneSELlist);

        jLabelSinfo1.setFont(new java.awt.Font("Metropolis", 2, 17)); // NOI18N
        jLabelSinfo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSinfo1.setText("Select multiple files to work on");
        jLabelSinfo1.setMinimumSize(new java.awt.Dimension(16, 16));

        jLabelSinfo2.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelSinfo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSinfo2.setText("Arguments from the Single Session");
        jLabelSinfo2.setMinimumSize(new java.awt.Dimension(16, 16));

        jLabelSinfo3.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelSinfo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSinfo3.setText("will be applied while parsing");
        jLabelSinfo3.setMinimumSize(new java.awt.Dimension(16, 16));

        jLabelSinfo4.setFont(new java.awt.Font("Metropolis", 2, 15)); // NOI18N
        jLabelSinfo4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSinfo4.setText("the selected files.");
        jLabelSinfo4.setMinimumSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout jPanelSELinfoLayout = new javax.swing.GroupLayout(jPanelSELinfo);
        jPanelSELinfo.setLayout(jPanelSELinfoLayout);
        jPanelSELinfoLayout.setHorizontalGroup(
            jPanelSELinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSELinfoLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelSELinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparatorS1)
                    .addComponent(jLabelSinfo4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSinfo3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSinfo1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSinfo2, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)))
        );
        jPanelSELinfoLayout.setVerticalGroup(
            jPanelSELinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSELinfoLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabelSinfo1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorS1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelSinfo2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSinfo3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSinfo4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(217, Short.MAX_VALUE))
        );

        jSplitPaneSEL.setRightComponent(jPanelSELinfo);

        javax.swing.GroupLayout jPanelSELboxLayout = new javax.swing.GroupLayout(jPanelSELbox);
        jPanelSELbox.setLayout(jPanelSELboxLayout);
        jPanelSELboxLayout.setHorizontalGroup(
            jPanelSELboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneSEL, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );
        jPanelSELboxLayout.setVerticalGroup(
            jPanelSELboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSELboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPaneSEL)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelSELECTIONLayout = new javax.swing.GroupLayout(jPanelSELECTION);
        jPanelSELECTION.setLayout(jPanelSELECTIONLayout);
        jPanelSELECTIONLayout.setHorizontalGroup(
            jPanelSELECTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSELECTIONLayout.createSequentialGroup()
                .addComponent(jPanelSELpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSELbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSELpadr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        jPanelSELECTIONLayout.setVerticalGroup(
            jPanelSELECTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSELbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelSELpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelSELpadr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPaneWORK.addTab("Selection", jPanelSELECTION);

        jButtonSTART.setBackground(new java.awt.Color(2, 169, 2));
        jButtonSTART.setFont(new java.awt.Font("Metropolis", 1, 24)); // NOI18N
        jButtonSTART.setText("START");
        jButtonSTART.setActionCommand("jButtonSTART");
        jButtonSTART.setAutoscrolls(true);
        jButtonSTART.setEnabled(false);
        jButtonSTART.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSTARTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSTARTpadlLayout = new javax.swing.GroupLayout(jPanelSTARTpadl);
        jPanelSTARTpadl.setLayout(jPanelSTARTpadlLayout);
        jPanelSTARTpadlLayout.setHorizontalGroup(
            jPanelSTARTpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSTARTpadlLayout.setVerticalGroup(
            jPanelSTARTpadlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelSTARTpadrLayout = new javax.swing.GroupLayout(jPanelSTARTpadr);
        jPanelSTARTpadr.setLayout(jPanelSTARTpadrLayout);
        jPanelSTARTpadrLayout.setHorizontalGroup(
            jPanelSTARTpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSTARTpadrLayout.setVerticalGroup(
            jPanelSTARTpadrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jProgressBarWORK.setEnabled(false);

        javax.swing.GroupLayout jPanelSTARTLayout = new javax.swing.GroupLayout(jPanelSTART);
        jPanelSTART.setLayout(jPanelSTARTLayout);
        jPanelSTARTLayout.setHorizontalGroup(
            jPanelSTARTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSTARTLayout.createSequentialGroup()
                .addComponent(jPanelSTARTpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSTARTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonSTART, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBarWORK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSTARTpadr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSTARTLayout.setVerticalGroup(
            jPanelSTARTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSTARTLayout.createSequentialGroup()
                .addComponent(jProgressBarWORK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSTART, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanelSTARTpadl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelSTARTpadr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelMAKELayout = new javax.swing.GroupLayout(jPanelMAKE);
        jPanelMAKE.setLayout(jPanelMAKELayout);
        jPanelMAKELayout.setHorizontalGroup(
            jPanelMAKELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneWORK, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
            .addGroup(jPanelMAKELayout.createSequentialGroup()
                .addComponent(jPanelSTART, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelMAKELayout.setVerticalGroup(
            jPanelMAKELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMAKELayout.createSequentialGroup()
                .addComponent(jTabbedPaneWORK, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSTART, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jScrollPaneMAKE.setViewportView(jPanelMAKE);

        jSplitPaneMAKE.setLeftComponent(jScrollPaneMAKE);

        jTextAreaOUTPUT.setBackground(new java.awt.Color(0, 0, 0));
        jTextAreaOUTPUT.setColumns(20);
        jTextAreaOUTPUT.setFont(new java.awt.Font("Withheld Data", 0, 15)); // NOI18N
        jTextAreaOUTPUT.setForeground(new java.awt.Color(255, 255, 255));
        jTextAreaOUTPUT.setRows(5);
        jScrollPaneOUTPUT.setViewportView(jTextAreaOUTPUT);

        jSplitPaneMAKE.setRightComponent(jScrollPaneOUTPUT);

        jTabbedPaneMAIN.addTab("Make statistics", jSplitPaneMAKE);

        jTabbedPaneSTATS.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTabbedPaneSTATS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneSTATSStateChanged(evt);
            }
        });

        jSplitPaneACCESS.setDividerLocation(200);

        jTabbedPaneACCESSview.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTabbedPaneACCESSview.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneACCESSviewStateChanged(evt);
            }
        });

        jTableIP.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableIP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "IP address", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableIP.setRowHeight(24);
        jTableIP.setShowGrid(true);
        jScrollPaneIP.setViewportView(jTableIP);

        jTabbedPaneACCESSview.addTab("IPs", jScrollPaneIP);

        jTableREQ.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableREQ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Requested page", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableREQ.setRowHeight(24);
        jTableREQ.setShowGrid(true);
        jScrollPaneREQ.setViewportView(jTableREQ);

        jTabbedPaneACCESSview.addTab("Requests", jScrollPaneREQ);

        jTableRES.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableRES.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Response code", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRES.setCellSelectionEnabled(true);
        jTableRES.setRowHeight(24);
        jTableRES.setShowGrid(true);
        jScrollPaneRES.setViewportView(jTableRES);

        jTabbedPaneACCESSview.addTab("Responses", jScrollPaneRES);

        jTableUA.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableUA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "User agent", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableUA.setRowHeight(24);
        jTableUA.setShowGrid(true);
        jScrollPaneUA.setViewportView(jTableUA);

        jTabbedPaneACCESSview.addTab("User agents", jScrollPaneUA);

        jSplitPaneACCESS.setRightComponent(jTabbedPaneACCESSview);

        jListACC.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jListACC.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "GLOBALS", "YYYY-MM-DD" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jListACC.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListACC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListACCMousePressed(evt);
            }
        });
        jListACC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jListACCKeyPressed(evt);
            }
        });
        jScrollPaneACCESSlist.setViewportView(jListACC);

        jSplitPaneACCESS.setLeftComponent(jScrollPaneACCESSlist);

        jTabbedPaneSTATS.addTab("Access", jSplitPaneACCESS);

        jSplitPaneERRORS.setDividerLocation(200);

        jTabbedPaneERRORSview.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTabbedPaneERRORSview.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneERRORSviewStateChanged(evt);
            }
        });

        jTableERR.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableERR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Error description", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableERR.setCellSelectionEnabled(true);
        jTableERR.setRowHeight(24);
        jTableERR.setShowGrid(true);
        jScrollPaneERRview.setViewportView(jTableERR);

        jTabbedPaneERRORSview.addTab("Descriptions", jScrollPaneERRview);

        jTableLEV.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableLEV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Warning level", "Count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLEV.setRowHeight(24);
        jTableLEV.setShowGrid(true);
        jScrollPaneLEVview.setViewportView(jTableLEV);

        jTabbedPaneERRORSview.addTab("Levels", jScrollPaneLEVview);

        jSplitPaneERRORS.setRightComponent(jTabbedPaneERRORSview);

        jListERR.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jListERR.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "GLOBALS", "YYYY-MM-DD" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jListERR.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListERR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListERRMousePressed(evt);
            }
        });
        jListERR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jListERRKeyPressed(evt);
            }
        });
        jScrollPaneACCESSlist1.setViewportView(jListERR);

        jSplitPaneERRORS.setLeftComponent(jScrollPaneACCESSlist1);

        jTabbedPaneSTATS.addTab("Errors", jSplitPaneERRORS);

        jTabbedPaneMAIN.addTab("View statistics", jTabbedPaneSTATS);
        jTabbedPaneSTATS.getAccessibleContext().setAccessibleName("");

        jSplitPaneLOGS.setDividerLocation(288);

        jTextPaneLOGS.setEditable(false);
        jTextPaneLOGS.setFont(new java.awt.Font("3270 Condensed", 0, 15)); // NOI18N
        jScrollPaneLOGSview.setViewportView(jTextPaneLOGS);

        jSplitPaneLOGS.setRightComponent(jScrollPaneLOGSview);

        jPanelLOGSbox.setPreferredSize(new java.awt.Dimension(280, 288));

        jButtonLOGSview.setFont(new java.awt.Font("Metropolis", 0, 18)); // NOI18N
        jButtonLOGSview.setText("View");
        jButtonLOGSview.setPreferredSize(new java.awt.Dimension(128, 32));
        jButtonLOGSview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLOGSviewActionPerformed(evt);
            }
        });

        jTableLOGSlist.setFont(new java.awt.Font("Metropolis", 0, 14)); // NOI18N
        jTableLOGSlist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                " /var/log/apache2/"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLOGSlist.setRowHeight(24);
        jTableLOGSlist.setShowGrid(false);
        jScrollPaneLOGSlist.setViewportView(jTableLOGSlist);

        javax.swing.GroupLayout jPanelLOGSboxLayout = new javax.swing.GroupLayout(jPanelLOGSbox);
        jPanelLOGSbox.setLayout(jPanelLOGSboxLayout);
        jPanelLOGSboxLayout.setHorizontalGroup(
            jPanelLOGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLOGSboxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLOGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonLOGSview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPaneLOGSlist, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelLOGSboxLayout.setVerticalGroup(
            jPanelLOGSboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLOGSboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonLOGSview, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneLOGSlist, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPaneLOGSselect.setViewportView(jPanelLOGSbox);

        jSplitPaneLOGS.setLeftComponent(jScrollPaneLOGSselect);

        jTabbedPaneMAIN.addTab("View logs", jSplitPaneLOGS);

        jMenuBarMENU.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N

        jMenuPREFERENCES.setText("Preferences");
        jMenuPREFERENCES.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N

        jMenuItemSETTINGS.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jMenuItemSETTINGS.setText("Settings  ");
        jMenuItemSETTINGS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSETTINGSActionPerformed(evt);
            }
        });
        jMenuPREFERENCES.add(jMenuItemSETTINGS);

        jMenuBarMENU.add(jMenuPREFERENCES);

        jMenuUTILITIES.setText("Utilities");
        jMenuUTILITIES.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N

        jMenuItemNOTE.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jMenuItemNOTE.setText("Note");
        jMenuItemNOTE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNOTEActionPerformed(evt);
            }
        });
        jMenuUTILITIES.add(jMenuItemNOTE);
        jMenuUTILITIES.add(jSeparatorMENUut);

        jMenuItemUPDATES.setFont(new java.awt.Font("Metropolis", 0, 13)); // NOI18N
        jMenuItemUPDATES.setText("Check updates");
        jMenuItemUPDATES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUPDATESActionPerformed(evt);
            }
        });
        jMenuUTILITIES.add(jMenuItemUPDATES);

        jMenuBarMENU.add(jMenuUTILITIES);

        setJMenuBar(jMenuBarMENU);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneMAIN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneMAIN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPaneMAIN.getAccessibleContext().setAccessibleName("Main");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemSETTINGSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSETTINGSActionPerformed
        // open the settings window
        if ( Main.settings.isVisible() == false ) {
            // set visible
            Main.settings.restore();
        }
        // in any case, give focus
        Main.settings.requestFocus();
    }//GEN-LAST:event_jMenuItemSETTINGSActionPerformed

    private void jCheckBoxLOGSerrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLOGSerrActionPerformed
        // toggle/untoggle parsing error logs
        if ( this.jCheckBoxLOGSerr.isSelected() == true ) {
            craplog.setParseErrorTrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                this.jButtonSTART.setEnabled(true);
                this.jProgressBarWORK.setEnabled(true);
            }
        } else {
            craplog.setParseErrorFalse();
            if ( this.jCheckBoxLOGSacc.isSelected() == false ) {
                this.jButtonSTART.setEnabled(false);
                this.jProgressBarWORK.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jCheckBoxLOGSerrActionPerformed

    private void jCheckBoxSTATSglobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSTATSglobActionPerformed
        // toggle/untoggle making/updating global statistics
        if ( this.jCheckBoxSTATSglob.isSelected() == true ) {
            craplog.setUpdateGlobalsTrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                // enable Access/error logs options
                this.jCheckBoxLOGSacc.setEnabled(true);
                this.jCheckBoxLOGSerr.setEnabled(true);
                // enable post-work options
                this.jCheckBoxPOSTbackup.setEnabled(true);
                this.jCheckBoxPOSTbackupActionPerformed(evt);
                this.jCheckBoxPOSTdelete.setEnabled(true);
                this.jCheckBoxPOSTdeleteActionPerformed(evt);
            }
        } else {
            craplog.setUpdateGlobalsFalse();
            if ( this.jCheckBoxSTATSsess.isSelected() == false ) {
                // disable start button
                this.jButtonSTART.setEnabled(false);
                this.jProgressBarWORK.setEnabled(false);
                // disable parsing Access logs
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSacc.setEnabled(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                // disable parsing Error logs
                this.jCheckBoxLOGSerr.setSelected(false);
                this.jCheckBoxLOGSerr.setEnabled(false);
                this.jCheckBoxLOGSerrActionPerformed(evt);
                // disable backup option
                this.jCheckBoxPOSTbackup.setSelected(false);
                this.jCheckBoxPOSTbackup.setEnabled(false);
                this.jCheckBoxPOSTbackupActionPerformed(evt);
                // disable delete option
                this.jCheckBoxPOSTdelete.setSelected(false);
                this.jCheckBoxPOSTdelete.setEnabled(false);
                this.jCheckBoxPOSTdeleteActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_jCheckBoxSTATSglobActionPerformed

    private void jCheckBoxPOSTarchiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPOSTarchiveActionPerformed
        // toggle/untoggle storing backups as archives
        if ( this.jCheckBoxPOSTarchive.isSelected() == true ) {
            craplog.setPostArchiveTrue();
            this.jComboBoxARCHIVEtype.setEnabled(true);
            this.jComboBoxARCHIVEtypeActionPerformed(evt);
        } else {
            craplog.setPostArchiveFalse();
            this.jComboBoxARCHIVEtype.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxPOSTarchiveActionPerformed

    private void jCheckBoxPOSTdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPOSTdeleteActionPerformed
        // toggle/untoggle deleting original log files when done
        if ( this.jCheckBoxPOSTdelete.isSelected() == true ) {
            craplog.setPostDeleteTrue();
            this.jCheckBoxPOSTtrash.setEnabled(true);
        } else {
            craplog.setPostDeleteFalse();
            // also untoggle and disable trash option
            this.jCheckBoxPOSTtrash.setSelected(false);
            this.jCheckBoxPOSTtrash.setEnabled(false);
            this.jCheckBoxPOSTtrashActionPerformed(evt);
        }
    }//GEN-LAST:event_jCheckBoxPOSTdeleteActionPerformed

    private void jCheckBoxPOSTtrashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPOSTtrashActionPerformed
        // toggle/untoggle moving original files to trash instead of deleting
        if ( this.jCheckBoxPOSTtrash.isSelected() == true ) {
            craplog.setPostTrashTrue();
        } else {
            craplog.setPostTrashFalse();
        }
    }//GEN-LAST:event_jCheckBoxPOSTtrashActionPerformed

    private void jComboBoxARCHIVEtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxARCHIVEtypeActionPerformed
        // 1: tar.gz, 2: tar, 3: zip
        craplog.setPostArchiveType( this.jComboBoxARCHIVEtype.getSelectedIndex() );
    }//GEN-LAST:event_jComboBoxARCHIVEtypeActionPerformed

    private void jButtonLOGSviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLOGSviewActionPerformed
        // show the content of the selected log file
        try {
            String log_file = this.jTableLOGSlist.getValueAt(
                this.jTableLOGSlist.getSelectedRow(),
                this.jTableLOGSlist.getSelectedColumn() ).toString();
            this.jTextPaneLOGS.setText( crapview.viewLogFile( Main.craplog.getLogsDir(), log_file ) );
        } catch (Exception e) {
            // pass
        }
    }//GEN-LAST:event_jButtonLOGSviewActionPerformed

    private void jButtonSTARTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSTARTActionPerformed
        // TODO add your handling code here:
        if ( this.target_type == 2 ) {
            this.target_files = this.jListSELECTION.getSelectedValuesList();
        }
        if ( !this.target_files.isEmpty() ) {
            this.jTextAreaOUTPUT.setText("");
            Main.craplog.Crapstart( this.jProgressBarWORK, this.jTextAreaOUTPUT, this.target_files );
            this.jProgressBarWORK.setValue(0);
        } else {
            JOptionPane.showMessageDialog(null, "Please select one or more files to work on", "No file selected", 2);
        }
    }//GEN-LAST:event_jButtonSTARTActionPerformed

    private void jPanelSESSIONComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelSESSIONComponentShown
        // start as session mode
        this.target_type = 1;
        this.target_files.clear();
        this.target_files.add("access.log.1");
        this.target_files.add("error.log.1");
    }//GEN-LAST:event_jPanelSESSIONComponentShown

    private void jPanelSELECTIONComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelSELECTIONComponentShown
        // start as selection mode
        this.target_type = 2;
        DefaultListModel<String> tmp = crapview.getLogsList( Main.craplog.getLogsDir() );
        this.log_files.clear();
        for ( int i=0; i<tmp.size(); i++ ) {
            this.log_files.addElement( tmp.get(i) );
        }
    }//GEN-LAST:event_jPanelSELECTIONComponentShown

    private void jCheckBoxSTATSsessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSTATSsessActionPerformed
        // toggle/untoggle storing session stats
        if ( this.jCheckBoxSTATSsess.isSelected() == true ) {
            craplog.setMakeSessionsTrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                // enable Access/error logs options
                this.jCheckBoxLOGSacc.setEnabled(true);
                this.jCheckBoxLOGSerr.setEnabled(true);
                // enable post-work options
                this.jCheckBoxPOSTbackup.setEnabled(true);
                this.jCheckBoxPOSTbackupActionPerformed(evt);
                this.jCheckBoxPOSTdelete.setEnabled(true);
                this.jCheckBoxPOSTdeleteActionPerformed(evt);
            }
        } else {
            craplog.setMakeSessionsFalse();
            if ( this.jCheckBoxSTATSglob.isSelected() == false ) {
                // disable start button
                this.jButtonSTART.setEnabled(false);
                this.jProgressBarWORK.setEnabled(false);
                // disable parsing Access logs
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSacc.setEnabled(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                // disable parsing Error logs
                this.jCheckBoxLOGSerr.setSelected(false);
                this.jCheckBoxLOGSerr.setEnabled(false);
                this.jCheckBoxLOGSerrActionPerformed(evt);
                // disable backup option
                this.jCheckBoxPOSTbackup.setSelected(false);
                this.jCheckBoxPOSTbackup.setEnabled(false);
                this.jCheckBoxPOSTbackupActionPerformed(evt);
                // disable delete option
                this.jCheckBoxPOSTdelete.setSelected(false);
                this.jCheckBoxPOSTdelete.setEnabled(false);
                this.jCheckBoxPOSTdeleteActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_jCheckBoxSTATSsessActionPerformed

    private void jCheckBoxLOGSaccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLOGSaccActionPerformed
        // toggle/untoggle parsing access logs
        if ( this.jCheckBoxLOGSacc.isSelected() == true ) {
            craplog.setParseAccessTrue();
            // enable access logs fields
            if ( this.jCheckBoxACCip.isEnabled() == false ) {
                this.jCheckBoxACCip.setEnabled(true);
                this.jCheckBoxACCip.setSelected(true);
                this.jCheckBoxACCipActionPerformed(evt);
            }
            if ( this.jCheckBoxACCreq.isEnabled() == false ) {
                this.jCheckBoxACCreq.setEnabled(true);
                this.jCheckBoxACCreq.setSelected(true);
                this.jCheckBoxACCreqActionPerformed(evt);
            }
            if ( this.jCheckBoxACCres.isEnabled() == false ) {
                this.jCheckBoxACCres.setEnabled(true);
                this.jCheckBoxACCres.setSelected(true);
                this.jCheckBoxACCresActionPerformed(evt);
            }
            if ( this.jCheckBoxACCua.isEnabled() == false ) {
                this.jCheckBoxACCua.setEnabled(true);
                this.jCheckBoxACCua.setSelected(true);
                this.jCheckBoxACCuaActionPerformed(evt);
            }
        } else {
            craplog.setParseAccessFalse();
            if ( this.jCheckBoxLOGSerr.isSelected() == false ) {
                this.jButtonSTART.setEnabled(false);
                this.jProgressBarWORK.setEnabled(false);
            }
            this.jCheckBoxACCip.setSelected(false);
            this.jCheckBoxACCip.setEnabled(false);
            this.jCheckBoxACCreq.setSelected(false);
            this.jCheckBoxACCreq.setEnabled(false);
            this.jCheckBoxACCres.setSelected(false);
            this.jCheckBoxACCres.setEnabled(false);
            this.jCheckBoxACCua.setSelected(false);
            this.jCheckBoxACCua.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxLOGSaccActionPerformed

    private void jCheckBoxACCipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxACCipActionPerformed
        // toggle/untoggle parsing IPs in access logs
        if ( this.jCheckBoxACCip.isSelected() == true ) {
            craplog.setParseIPTrue();
            // enable start button
            if ( this.jButtonSTART.isEnabled() == false ) {
                this.jButtonSTART.setEnabled(true);
                this.jProgressBarWORK.setEnabled(true);
            }
        } else {
            craplog.setParseIPFalse();
            if (this.jCheckBoxACCreq.isSelected() == false
            &&  this.jCheckBoxACCres.isSelected() == false
            &&  this.jCheckBoxACCua.isSelected()  == false ) {
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                if ( this.jCheckBoxLOGSerr.isSelected() == false ) {
                    this.jButtonSTART.setEnabled(false);
                    this.jProgressBarWORK.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_jCheckBoxACCipActionPerformed

    private void jCheckBoxACCreqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxACCreqActionPerformed
        // toggle/untoggle parsing REQ in access logs
        if ( this.jCheckBoxACCreq.isSelected() == true ) {
            craplog.setParseREQTrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                this.jButtonSTART.setEnabled(true);
                this.jProgressBarWORK.setEnabled(true);
            }
        } else {
            craplog.setParseREQFalse();
            if (this.jCheckBoxACCip.isSelected()  == false
            &&  this.jCheckBoxACCres.isSelected() == false
            &&  this.jCheckBoxACCua.isSelected()  == false ) {
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                if ( this.jCheckBoxLOGSerr.isSelected() == false ) {
                    this.jButtonSTART.setEnabled(false);
                    this.jProgressBarWORK.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_jCheckBoxACCreqActionPerformed

    private void jCheckBoxACCresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxACCresActionPerformed
        // toggle/untoggle parsing RES in access logs
        if ( this.jCheckBoxACCres.isSelected() == true ) {
            craplog.setParseRESTrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                this.jButtonSTART.setEnabled(true);
                this.jProgressBarWORK.setEnabled(true);
            }
        } else {
            craplog.setParseRESFalse();
            if (this.jCheckBoxACCip.isSelected()  == false
            &&  this.jCheckBoxACCreq.isSelected() == false
            &&  this.jCheckBoxACCua.isSelected()  == false ) {
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                if ( this.jCheckBoxLOGSerr.isSelected() == false ) {
                    this.jButtonSTART.setEnabled(false);
                    this.jProgressBarWORK.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_jCheckBoxACCresActionPerformed

    private void jCheckBoxACCuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxACCuaActionPerformed
        // toggle/untoggle parsing UA in access logs
        if ( this.jCheckBoxACCua.isSelected() == true ) {
            craplog.setParseUATrue();
            if ( this.jButtonSTART.isEnabled() == false ) {
                this.jButtonSTART.setEnabled(true);
                this.jProgressBarWORK.setEnabled(true);
            }
        } else {
            craplog.setParseUAFalse();
            if (this.jCheckBoxACCip.isSelected()  == false
            &&  this.jCheckBoxACCreq.isSelected() == false
            &&  this.jCheckBoxACCres.isSelected() == false ) {
                this.jCheckBoxLOGSacc.setSelected(false);
                this.jCheckBoxLOGSaccActionPerformed(evt);
                if ( this.jCheckBoxLOGSerr.isSelected() == false ) {
                    this.jButtonSTART.setEnabled(false);
                    this.jProgressBarWORK.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_jCheckBoxACCuaActionPerformed

    private void jCheckBoxPOSTbackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPOSTbackupActionPerformed
        // toggle/untoggle making a backup of original log files
        if ( this.jCheckBoxPOSTbackup.isSelected() == true ) {
            craplog.setPostBackupTrue();
            // disable backup archive option
            this.jCheckBoxPOSTarchive.setEnabled(true);
            this.jCheckBoxPOSTarchiveActionPerformed(evt);
        } else {
            craplog.setPostBackupFalse();
            // disable backup archive option
            this.jCheckBoxPOSTarchive.setSelected(false);
            this.jCheckBoxPOSTarchive.setEnabled(false);
            this.jCheckBoxPOSTarchiveActionPerformed(evt);
            this.jComboBoxARCHIVEtype.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxPOSTbackupActionPerformed

    private void jMenuItemNOTEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNOTEActionPerformed
        // open window to temporary write text in
        String[] args = {};
        crapcode.crapnote.main(args);
    }//GEN-LAST:event_jMenuItemNOTEActionPerformed

    private void jMenuItemUPDATESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUPDATESActionPerformed
        // check version updates
        int result = crapcode.crapup.CheckUpdates();
        if ( result == 0 ) {
            JOptionPane.showMessageDialog(null, "Craplog is up-to-date", "No update available", 1);
        } else if ( result == 1 ) {
            JOptionPane.showMessageDialog(null, "A new version of Craplog is available:\nhttps://github.com/elB4RTO/craplog-javaGUI", "Update available", 2);
        }
        // error messages already shown, skip here
    }//GEN-LAST:event_jMenuItemUPDATESActionPerformed

    private void jTabbedPaneSTATSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneSTATSStateChanged
        // update table view
        this.handleListSelection();
    }//GEN-LAST:event_jTabbedPaneSTATSStateChanged

    private void jTabbedPaneACCESSviewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneACCESSviewStateChanged
        // update table view
        this.handleListSelection();
    }//GEN-LAST:event_jTabbedPaneACCESSviewStateChanged

    private void jTabbedPaneERRORSviewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneERRORSviewStateChanged
        // update table view
        this.handleListSelection();
    }//GEN-LAST:event_jTabbedPaneERRORSviewStateChanged

    private void jListACCKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jListACCKeyPressed
        // show the relative table
        if (evt.getKeyCode() == KeyEvent.VK_ENTER
        &&  this.jListACC.getSelectedIndex() != this.list_acc_index ) {
            this.showTable();
        }
    }//GEN-LAST:event_jListACCKeyPressed

    private void jListERRKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jListERRKeyPressed
        // show the relative table
        if (evt.getKeyCode() == KeyEvent.VK_ENTER
        &&  this.jListERR.getSelectedIndex() != this.list_err_index) {
            this.showTable();
        }
    }//GEN-LAST:event_jListERRKeyPressed

    private void jListACCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListACCMousePressed
        // show the relative table
        if ( this.jListACC.getSelectedIndex() == this.list_acc_index ) {
            if ( this.list_acc_index != this.last_acc_index ) {
                this.last_acc_index = this.list_acc_index;
                this.showTable();
            }
        } else {
            this.list_acc_index = this.jListACC.getSelectedIndex();
        }
    }//GEN-LAST:event_jListACCMousePressed

    private void jListERRMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListERRMousePressed
        // show the relative table
        if ( this.jListERR.getSelectedIndex() == this.list_err_index ) {
            if ( this.list_err_index != this.last_err_index ) {
                this.last_err_index = this.list_err_index;
                this.showTable();
            }
        } else {
            this.list_err_index = this.jListERR.getSelectedIndex();
        }
    }//GEN-LAST:event_jListERRMousePressed

    private void jTabbedPaneMAINStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneMAINStateChanged
        // TODO add your handling code here:
        try {
            switch( this.jTabbedPaneMAIN.getSelectedIndex() ) {
                case 0:
                    // do nothing
                    break;
                case 1:
                    this.handleListSelection();
                    break;
                case 2:
                    this.viewLogsList();
                    break;
                default:
                    // do nothing
                    break;
            }
        
        } catch (Exception e) {
            // mainly catched during initialization
        }
    }//GEN-LAST:event_jTabbedPaneMAINStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // save configurations
        Main.craplog.saveConfigs();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLOGSview;
    private javax.swing.JButton jButtonSTART;
    private javax.swing.JCheckBox jCheckBoxACCip;
    private javax.swing.JCheckBox jCheckBoxACCreq;
    private javax.swing.JCheckBox jCheckBoxACCres;
    private javax.swing.JCheckBox jCheckBoxACCua;
    private javax.swing.JCheckBox jCheckBoxLOGSacc;
    private javax.swing.JCheckBox jCheckBoxLOGSerr;
    private javax.swing.JCheckBox jCheckBoxPOSTarchive;
    private javax.swing.JCheckBox jCheckBoxPOSTbackup;
    private javax.swing.JCheckBox jCheckBoxPOSTdelete;
    private javax.swing.JCheckBox jCheckBoxPOSTtrash;
    private javax.swing.JCheckBox jCheckBoxSTATSglob;
    private javax.swing.JCheckBox jCheckBoxSTATSsess;
    private javax.swing.JComboBox<String> jComboBoxARCHIVEtype;
    private javax.swing.JLabel jLabelACC;
    private javax.swing.JLabel jLabelARGS;
    private javax.swing.JLabel jLabelLOGS;
    private javax.swing.JLabel jLabelPOST;
    private javax.swing.JLabel jLabelSTATS;
    private javax.swing.JLabel jLabelSinfo1;
    private javax.swing.JLabel jLabelSinfo2;
    private javax.swing.JLabel jLabelSinfo3;
    private javax.swing.JLabel jLabelSinfo4;
    private javax.swing.JList<String> jListACC;
    private javax.swing.JList<String> jListERR;
    private javax.swing.JList<String> jListSELECTION;
    private javax.swing.JMenuBar jMenuBarMENU;
    private javax.swing.JMenuItem jMenuItemNOTE;
    private javax.swing.JMenuItem jMenuItemSETTINGS;
    private javax.swing.JMenuItem jMenuItemUPDATES;
    private javax.swing.JMenu jMenuPREFERENCES;
    private javax.swing.JMenu jMenuUTILITIES;
    private javax.swing.JPanel jPanelARGSbox;
    private javax.swing.JPanel jPanelARGSpadl;
    private javax.swing.JPanel jPanelARGSpadr;
    private javax.swing.JPanel jPanelLOGSbox;
    private javax.swing.JPanel jPanelMAKE;
    private javax.swing.JPanel jPanelSELECTION;
    private javax.swing.JPanel jPanelSELbox;
    private javax.swing.JPanel jPanelSELinfo;
    private javax.swing.JPanel jPanelSELpadl;
    private javax.swing.JPanel jPanelSELpadr;
    private javax.swing.JPanel jPanelSESSION;
    private javax.swing.JPanel jPanelSTART;
    private javax.swing.JPanel jPanelSTARTpadl;
    private javax.swing.JPanel jPanelSTARTpadr;
    private javax.swing.JProgressBar jProgressBarWORK;
    private javax.swing.JScrollPane jScrollPaneACCESSlist;
    private javax.swing.JScrollPane jScrollPaneACCESSlist1;
    private javax.swing.JScrollPane jScrollPaneERRview;
    private javax.swing.JScrollPane jScrollPaneIP;
    private javax.swing.JScrollPane jScrollPaneLEVview;
    private javax.swing.JScrollPane jScrollPaneLOGSlist;
    private javax.swing.JScrollPane jScrollPaneLOGSselect;
    private javax.swing.JScrollPane jScrollPaneLOGSview;
    private javax.swing.JScrollPane jScrollPaneMAKE;
    private javax.swing.JScrollPane jScrollPaneOUTPUT;
    private javax.swing.JScrollPane jScrollPaneREQ;
    private javax.swing.JScrollPane jScrollPaneRES;
    private javax.swing.JScrollPane jScrollPaneSELlist;
    private javax.swing.JScrollPane jScrollPaneUA;
    private javax.swing.JSeparator jSeparatorA1;
    private javax.swing.JSeparator jSeparatorA2;
    private javax.swing.JSeparator jSeparatorA3;
    private javax.swing.JSeparator jSeparatorA4;
    private javax.swing.JPopupMenu.Separator jSeparatorMENUut;
    private javax.swing.JSeparator jSeparatorS1;
    private javax.swing.JSplitPane jSplitPaneACCESS;
    private javax.swing.JSplitPane jSplitPaneERRORS;
    private javax.swing.JSplitPane jSplitPaneLOGS;
    private javax.swing.JSplitPane jSplitPaneMAKE;
    private javax.swing.JSplitPane jSplitPaneSEL;
    private javax.swing.JTabbedPane jTabbedPaneACCESSview;
    private javax.swing.JTabbedPane jTabbedPaneERRORSview;
    private javax.swing.JTabbedPane jTabbedPaneMAIN;
    private javax.swing.JTabbedPane jTabbedPaneSTATS;
    private javax.swing.JTabbedPane jTabbedPaneWORK;
    private javax.swing.JTable jTableERR;
    private javax.swing.JTable jTableIP;
    private javax.swing.JTable jTableLEV;
    private javax.swing.JTable jTableLOGSlist;
    private javax.swing.JTable jTableREQ;
    private javax.swing.JTable jTableRES;
    private javax.swing.JTable jTableUA;
    private javax.swing.JTextArea jTextAreaOUTPUT;
    private javax.swing.JTextPane jTextPaneLOGS;
    // End of variables declaration//GEN-END:variables
}
