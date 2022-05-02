
package crapcode;

import crapcode.craputils.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;


public class craplog {
    
    private String MSG_START, MSG_DONE, MSG_CHECKING, MSG_READING, MSG_PARSING,
        MSG_UPDATING, MSG_STORING, MSG_BACKUP, MSG_DELETING, MSG_CLEANING, MSG_FIN;
    
    private boolean processing;

    private String jar_path, logs_dir, stats_dir, trash_dir;
    
    private final HashMap<String,String> proceed;
    private final ArrayList<Path> undo; // used in case of fire to delete newely created paths and/or rename-back moved files
    private boolean makeSessionStats, updateGlobalStats;
    private boolean parseACClogs, parseERRlogs;
    private boolean parseIP, parseREQ, parseRES, parseUA, parseERR, parseLEV;
    // accsee/error -> date -> IP/REQ/RES/UA/ERR/LEV -> item, count
    private final HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> data_collection;
    private ArrayList<String> IPs_to_skip;
    private boolean postBackup, postArchive;
    private int postArchiveType;
    private boolean postDelete, postToTrash;
    
    public craplog( String jar_path ) {
        
        this.processing = false;
        
        this.jar_path  = jar_path;
        this.logs_dir  = "/var/log/apache2";
        this.stats_dir = Paths.get(String.format("%s/crapstats",jar_path)).toAbsolutePath().toString();
        this.trash_dir = String.format("%s/.local/share/Trash/files",System.getProperty("user.home"));
        
        this.proceed = new HashMap<>();
        proceed.put("state", "true");
        this.undo = new ArrayList<>();
        
        this.makeSessionStats  = false;
        this.updateGlobalStats = false;
        this.parseACClogs = false;
        this.parseERRlogs = false;
        this.parseIP  = false;
        this.parseREQ = false;
        this.parseRES = false;
        this.parseUA  = false;
        this.parseERR = false;
        this.parseLEV = false;
        this.postBackup      = false;
        this.postArchive     = false;
        this.postArchiveType = 0;
        this.postDelete  = false;
        this.postToTrash = false;
        
        this.IPs_to_skip = new ArrayList<>();
        
        this.data_collection = new HashMap<>();
        this.data_collection.put("access", new HashMap<>());
        this.data_collection.put("error",  new HashMap<>());
        
        this.readConfigs();
        this.initMSG();
        
    }
    
    public void Crapstart(
        javax.swing.JProgressBar progress_bar,
        javax.swing.JTextArea terminal_emu,
        List<String> target_files
    ) {
        this.processing = true;
        // initialize empty array for file data
        this.increaseProgress( progress_bar, terminal_emu, 0 );
        this.proceed.replace("state", "true");
        this.InitialChecks( target_files );
        terminal_emu.append(this.MSG_DONE);
        if (this.proceed.get("state").equals("true")) {
            this.increaseProgress( progress_bar, terminal_emu, 5 );
            ArrayList<String> data_array;
            // parse log files to collect fields items
            data_array = this.ReadLogs( target_files );
            terminal_emu.append(this.MSG_DONE);
            this.increaseProgress( progress_bar, terminal_emu, 20 );
            // make statistics from collected items
            if (this.proceed.get("state").equals("true")) {
                this.MakeStatistics( data_array );
            }
            terminal_emu.append(this.MSG_DONE);
            // update global-stats if defined
            if (this.updateGlobalStats == true
            &&  this.proceed.get("state").equals("true")) {
                this.increaseProgress( progress_bar, terminal_emu, 40 );
                this.UpdateGlobals();
                terminal_emu.append(this.MSG_DONE);
            }
            // save session-stats if defined
            if (this.makeSessionStats == true
            &&  this.proceed.get("state").equals("true")) {
                this.increaseProgress( progress_bar, terminal_emu, 60 );
                this.StoreSession();
                terminal_emu.append(this.MSG_DONE);
            }
            // execute post-work actions
            this.PostWorkActions( progress_bar, terminal_emu, target_files );
            this.increaseProgress( progress_bar, terminal_emu, 100 );
        }
        this.processing = false;
    }
    
    
    private void increaseProgress(
        javax.swing.JProgressBar progress_bar,
        javax.swing.JTextArea terminal_emu,
        int bar_level
    ) {
        progress_bar.setValue(bar_level);
        switch (bar_level) {
            case 100:
                terminal_emu.append(this.MSG_FIN);
                break;
            case 95:
                terminal_emu.append(this.MSG_CLEANING);
                break;
            case 90:
                terminal_emu.append(this.MSG_DELETING);
                break;
            case 80:
                terminal_emu.append(this.MSG_BACKUP);
                break;
            case 60:
                terminal_emu.append(this.MSG_STORING);
                break;
            case 40:
                terminal_emu.append(this.MSG_UPDATING);
                break;
            case 20:
                terminal_emu.append(this.MSG_PARSING);
                break;
            case 5:
                terminal_emu.append(this.MSG_READING);
                break;
            default:
                terminal_emu.append(this.MSG_START);
                terminal_emu.append(this.MSG_CHECKING);
                break;
        }
    }
    
    
    private void InitialChecks( List<String> target_files ) {
        // check for available permissions on target files/directories
        check.initialChecks(
            this.proceed, target_files,
            this.logs_dir, this.stats_dir );
        // if trash is enabled, check the existence
        if ( this.postToTrash == true ) {
            check.checkTrash( this.proceed, this.trash_dir );
        }
    }
    
    
    private ArrayList<String> ReadLogs( List<String> target_files ) {
        // create an array for data
        ArrayList<String> data_array = new ArrayList<>();
        for( String file_name : target_files ) {
            String file_path = String.format("%s/%s",
                this.logs_dir, file_name );
            ArrayList<String> aux_array;
            aux_array = validate.readFile( this.proceed, file_path );
            if (this.proceed.get("state").equals("false")) {
                break;
            }
            for ( String line : aux_array ) {
                data_array.add( line );
            }
            aux_array.clear();
        }
        return data_array;
    }
    
    
    private void MakeStatistics( ArrayList<String> data_array ) {
        parse.parseData(
            this.data_collection, data_array, this.IPs_to_skip,
            this.parseACClogs, this.parseIP, this.parseREQ, this.parseRES, this.parseUA,
            this.parseERRlogs, this.parseERR, this.parseLEV );
    }
    
    
    private void UpdateGlobals() {
        save.updateGlobals(
            this.proceed, this.undo,
            this.parseACClogs, this.parseERRlogs,
            this.stats_dir, this.data_collection );
    }
    
    
    private void StoreSession() {
        save.storeSession(
            this.proceed, this.undo,
            this.parseACClogs, this.parseERRlogs,
            this.stats_dir, this.data_collection );
    }
    
    
    private void PostWorkActions(
        javax.swing.JProgressBar progress_bar,
        javax.swing.JTextArea terminal_emu,
        List<String> target_files
    ) {
        
        // remove needed copy-files/copy-folders
        ioutils.undoPaths(this.proceed, this.undo);
        
        if (this.proceed.get("state").equals("true")) {
        
            // backup original files
            if ( this.postBackup == true ) {
                this.increaseProgress( progress_bar, terminal_emu, 80 );
                String backup_date = LocalDate.now().toString();
                backup.backupOriginalFiles(
                    this.proceed, this.undo,
                    this.stats_dir, backup_date,
                    this.logs_dir, target_files );

                // compress the backup
                if ( this.postArchive == true ) {
                    // use the selected compression
                    backup.compressBackups(
                        this.proceed, this.undo,
                        this.stats_dir, backup_date,
                        target_files, this.postArchiveType );
                }
                terminal_emu.append(this.MSG_DONE);
            }
            if (this.proceed.get("state").equals("true")) {
                // delete original files
                if ( this.postDelete == true ) {
                    this.increaseProgress( progress_bar, terminal_emu, 90 );
                    if ( this.postToTrash == true ) {
                        // move file to trash instead of removing
                        delete.toTrash( this.logs_dir, target_files, this.trash_dir );
                    } else {
                        // remove files from disk
                        delete.Remove( this.logs_dir, target_files );
                    }
                    terminal_emu.append(this.MSG_DONE);
                }
            }
        }
        this.increaseProgress( progress_bar, terminal_emu, 95 );
        if (this.proceed.get("state").equals("false")) {
            // only clean if something failed
            ioutils.undoPaths(this.proceed, this.undo);
        }
        // clear arrays and hashmaps
        this.undo.clear();
        this.data_collection.get("access").clear();
        this.data_collection.get("error").clear();
        terminal_emu.append(this.MSG_DONE);
    }
    
    
    private void initMSG() {
        this.MSG_START = "   CCCC  RRRR   AAAAA  PPPP   L      OOOOO  GGGGG   \n   C     R   R  A   A  P   P  L      O   O  G       \n   C     RRRR   AAAAA  PPPP   L      O   O  G  GG   \n   C     R  R   A   A  P      L      O   O  G   G   \n   CCCC  R   R  A   A  P      LLLLL  OOOOO  GGGGG   \n\n";
        this.MSG_DONE  = " Done\n\n";
        this.MSG_CHECKING = "Initial checkings ...";
        this.MSG_READING  = "Reading log files ...";
        this.MSG_PARSING  = "Parsing logs ...";
        this.MSG_UPDATING = "Updating GLOBAL statistics ...";
        this.MSG_STORING  = "Storing SESSION statistics ...";
        this.MSG_BACKUP   = "Backing-up original log files ...";
        this.MSG_DELETING = "Deleting original log files ...";
        this.MSG_CLEANING = "Cleaning-up ...";
        this.MSG_FIN = "   FFFFF  II  N   N\n   F      II  NN  N\n   FFF    II  N N N\n   F      II  N  NN\n   F      II  N   N";
    }
    
    
    private String stripTrailing( String text, char c ) {
        // strip every trailing character equals to the given one
        int index = text.length();
        for ( int i=text.length()-1; i>=0; i-- ) {
            if ( text.charAt(i) == c ) {
                index = i;
            } else {
                break;
            }
        }
        return text.substring(0,index);
    }
    
    
    private void readConfigs() {
        Path path = Paths.get(String.format("%s/craplog.conf",this.jar_path)).toAbsolutePath();
        // attempt reading
        try {
            InputStream f_in = Files.newInputStream( path );
            BufferedInputStream buff_in = new BufferedInputStream( f_in );
            // get data
            String[] read = new String( buff_in.readAllBytes() ).split("\n");
            // assign paths
            this.logs_dir  = this.stripTrailing( Paths.get( read[0].trim() ).toAbsolutePath().toString(), '/');
            if ( read[1].trim().equals("crapstats") ) {
                this.stats_dir = this.stripTrailing( Paths.get(String.format("%s/crapstats",this.jar_path )).toAbsolutePath().toString(), '/');
            } else {
                this.stats_dir = this.stripTrailing( Paths.get( read[1].trim() ).toAbsolutePath().toString(), '/');
            }
            if ( read[2].trim().startsWith("~/") ) {
                String tmp = read[2].trim().substring(2);
                this.trash_dir = String.format("%s/%s",System.getProperty("user.home"),tmp);
            } else {
                this.trash_dir = this.stripTrailing( Paths.get( read[2].trim() ).toAbsolutePath().toString(), '/');
            }
            // work options
            this.makeSessionStats  = Boolean.parseBoolean(read[3]);
            this.updateGlobalStats = Boolean.parseBoolean(read[4]);
            this.parseACClogs = Boolean.parseBoolean(read[5]);
            this.parseERRlogs = Boolean.parseBoolean(read[6]);
            this.parseIP  = Boolean.parseBoolean(read[7]);
            this.parseREQ = Boolean.parseBoolean(read[8]);
            this.parseRES = Boolean.parseBoolean(read[9]);
            this.parseUA  = Boolean.parseBoolean(read[10]);
            this.parseERR = Boolean.parseBoolean(read[11]);
            this.parseLEV = Boolean.parseBoolean(read[12]);
            this.postBackup      = Boolean.parseBoolean(read[13]);
            this.postArchive     = Boolean.parseBoolean(read[14]);
            this.postArchiveType = Integer.parseInt(read[15]);
            this.postDelete  = Boolean.parseBoolean(read[16]);
            this.postToTrash = Boolean.parseBoolean(read[17]);
            // ips to skip
            this.IPs_to_skip.clear();
            String[] tmp = read[18].split(" ");
            for ( String ip : tmp ) {
                if ( !ip.isBlank() ) {
                    // probably valid string
                    this.IPs_to_skip.add( ip.trim() );
                }
            }
            // close
            buff_in.close();
            f_in.close();
        
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while searching for configurations file:\n'%s'\n\nUnable to load configuration settings",path), "Configurations file not found", 0);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while reading configurations file:\n'%s'\n\nUnable to load configuration settings",path), "Error reading configurations", 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while handling configurations file:\n'%s'\n\nUnable to load configuration settings",path), "Generic error", 0);
        }
        
    }
    
    public void saveConfigs() {
        Path path = Paths.get(String.format("%s/craplog.conf",this.jar_path)).toAbsolutePath();
        // make the file content
        String write = "";
        // assign paths
        write += String.format("%s\n",this.logs_dir);
        write += String.format("%s\n",this.stats_dir);
        write += String.format("%s\n",this.trash_dir);
        // work options
        write += String.format("%b\n",this.makeSessionStats);
        write += String.format("%b\n",this.updateGlobalStats);
        write += String.format("%b\n",this.parseACClogs);
        write += String.format("%b\n",this.parseERRlogs);
        write += String.format("%b\n",this.parseIP);
        write += String.format("%b\n",this.parseREQ);
        write += String.format("%b\n",this.parseRES);
        write += String.format("%b\n",this.parseUA);
        write += String.format("%b\n",this.parseERR);
        write += String.format("%b\n",this.parseLEV);
        write += String.format("%b\n",this.postBackup);
        write += String.format("%b\n",this.postArchive);
        write += String.format("%d\n",this.postArchiveType);
        write += String.format("%b\n",this.postDelete);
        write += String.format("%b\n",this.postToTrash);
        if ( this.IPs_to_skip.isEmpty() ) {
            write += " ";
        } else {
            for ( String ip : this.IPs_to_skip ) {
                if ( !ip.isBlank() ) {
                    // probably valid string
                    write += String.format("%s ",ip);
                }
            }
            write = write.trim();
        }
        // backup copy
        String new_path = path.toString();
        while (true) {
            if (Files.notExists( Paths.get(new_path) )) {
                break;
            } else {
                new_path += ".copy";
            }
        }
        // attempt writing
        try {
            // make a backup copy of the actual configs
            try {
                Files.move( path, Paths.get(new_path) );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while copying configurations file:\n'%s'\n\nUnable to temporary store a backup of the file.\nWriting aborted",path), "Error writing configurations", 0);
                throw new Exception("skip");
            }
            // write the new file
            OutputStream f_out = Files.newOutputStream( path );
            BufferedOutputStream buff_out = new BufferedOutputStream( f_out );
            // put data
            buff_out.write( write.getBytes() );
            // close
            buff_out.close();
            f_out.close();
            try {
                Files.delete( Paths.get(new_path) );
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while removing temporary\ncopy of configurations file:\n'%s'\n\nPlease remove it manually",path), "Error removing temporary copy", 0);
            }
        
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("An error occured while writing configurations file:\n'%s'",path), "Error writing configurations", 0);
            // writing failed, restore previous configs
            try {
                // delete the possibly corrupted file
                Files.delete( path );
                try {
                    Files.move( Paths.get(new_path), path );
                } catch (IOException ee) {
                    JOptionPane.showMessageDialog(null, String.format("An error occured while restoring configurations file:\n'%s'\n\nPlease manually restore it, removing the trailing '.copy' extension\nto restore your previous configuration",new_path), "Error writing configurations", 0);
                }
            } catch (IOException eee) {
                JOptionPane.showMessageDialog(null, String.format("An error occured while removing newly created configurations file:\n'%s'\n\nPlease remove it manually\nand restore the old configuratio file\n(the one with a trailing '.copy' extension)",path), "Error removing file", 0);
            }
        
        } catch (Exception e) {
            // backing-up failed, skip
        }
        
    }
    
    
    
    // is craplog working
    public boolean isProcessing() {
        return this.processing;
    }
    // set the logs directory
    public void setLogsDir( String path ) {
        this.logs_dir = this.stripTrailing( path, '/' );
    }
    public String getLogsDir() {
        return this.logs_dir;
    }
    // set the stats directory
    public void setStatsDir( String path ) {
        this.stats_dir = this.stripTrailing( path, '/' );
    }
    public String getStatsDir() {
        return this.stats_dir;
    }
    // set the logs directory
    public void setTrashDir( String path ) {
        this.trash_dir = this.stripTrailing( path, '/' );
    }
    public String getTrashDir() {
        return this.trash_dir;
    }
    // set the logs directory
    public void setSkipIPs( ArrayList<String> ip_list ) {
        this.IPs_to_skip = ip_list;
    }
    public ArrayList<String> getSkipIPs() {
        return this.IPs_to_skip;
    }
    // save session stats
    public void setMakeSessionsTrue() {
        this.makeSessionStats = true;
    }
    public void setMakeSessionsFalse() {
        this.makeSessionStats = false;
    }
    public boolean getMakeSessions() {
        return this.makeSessionStats;
    }
    // update global stats
    public void setUpdateGlobalsTrue() {
        this.updateGlobalStats = true;
    }
    public void setUpdateGlobalsFalse() {
        this.updateGlobalStats = false;
    }
    public boolean getUpdateGlobals() {
        return this.updateGlobalStats;
    }
    // work on access logs
    public void setParseAccessTrue() {
        this.parseACClogs = true;
    }
    public void setParseAccessFalse() {
        this.parseACClogs = false;
    }
    public boolean getParseAccess() {
        return this.parseACClogs;
    }
    // work on error logs
    public void setParseErrorTrue() {
        this.parseERRlogs = true;
        this.parseERR = true;
        this.parseLEV = true;
    }
    public void setParseErrorFalse() {
        this.parseERRlogs = false;
        this.parseERR = false;
        this.parseLEV = false;
    }
    public boolean getParseError() {
        return this.parseERRlogs;
    }
    // parse IP in logs
    public void setParseIPTrue() {
        this.parseIP = true;
    }
    public void setParseIPFalse() {
        this.parseIP = false;
    }
    public boolean getParseIP() {
        return this.parseIP;
    }
    // parse REQ in logs
    public void setParseREQTrue() {
        this.parseREQ = true;
    }
    public void setParseREQFalse() {
        this.parseREQ = false;
    }
    public boolean getParseREQ() {
        return this.parseREQ;
    }
    // parse RES in logs
    public void setParseRESTrue() {
        this.parseRES = true;
    }
    public void setParseRESFalse() {
        this.parseRES = false;
    }
    public boolean getParseRES() {
        return this.parseRES;
    }
    // parse UA in logs
    public void setParseUATrue() {
        this.parseUA = true;
    }
    public void setParseUAFalse() {
        this.parseUA = false;
    }
    public boolean getParseUA() {
        return this.parseUA;
    }
    // backup original log files
    public void setPostBackupTrue() {
        this.postBackup = true;
    }
    public void setPostBackupFalse() {
        this.postBackup = false;
    }
    public boolean getPostBackup() {
        return this.postBackup;
    }
    // backup as archive
    public void setPostArchiveTrue() {
        this.postArchive = true;
    }
    public void setPostArchiveFalse() {
        this.postArchive = false;
    }
    public boolean getPostArchive() {
        return this.postArchive;
    }
    // archive type
    public void setPostArchiveType( int value ) {
        this.postArchiveType = value;
    }
    public int getPostArchiveType() {
        return this.postArchiveType;
    }
    // delete original log files
    public void setPostDeleteTrue() {
        this.postDelete = true;
    }
    public void setPostDeleteFalse() {
        this.postDelete = false;
    }
    public boolean getPostDelete() {
        return this.postDelete;
    }
    // move original log files to trash
    public void setPostTrashTrue() {
        this.postToTrash = true;
    }
    public void setPostTrashFalse() {
        this.postToTrash = false;
    }
    public boolean getPostTrash() {
        return this.postToTrash;
    }
    
}
