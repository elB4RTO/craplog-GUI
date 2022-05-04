# craplog-javaGUI
A tool that scrapes Apache2 logs to create both Single-Session and Global statistics

<br>

``` diff
- !!! THIS REPOSITORY IS NOT FULLY ACTIVE YET !!!
- INSTALLATION SUPPORT FILES ARE NOT PRESENT AT THE TIME
```
<br>

## Description

CRAPLOG is a tool that takes Apache2 logs in their default form, parses them and creates simple statistics.

<br>

This fully Graphical version of CRAPLOG allows you to work on single-session log files (*\*.log.1*), or to select multiple files to use (eg. older logs/gzipped-logs files).

![image](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java1a.png)

![image](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java1b.png)

<br>

Differently from the other versions, this version of CRAPLOG will store statistics depending on the date of the single lines, to give more imporance to times and to analyze/backtrack statistics more easily.<br>
Be aware that log-files usage is not tracked, be careful of not parsing the same logs twice, which will lead to altered statistics.

![image](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java2.png)

<br>

It is also possible to display the log files contained in the logs folder, to directly view their content.

![image](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java3.png)

<br><br>

Searching for something different? Try the <a href="https://github.com/elB4RTO/CRAPLOG">other versions of CRAPLOG</a>.

<br>

## Minimum requirements / Dependencies / Plugins
- *JavaSE 11*
- *Maven plugins*
- *Apache common compress*

<br>

## Usage without installation

`[...]`

`[...]`

<br>

## Usage with installation

`[missing]`

`[missing]`

<br>

## How to compile

- Install the Maven Project Manager from your system's package manager:<br>
  * *Debian / Ubuntu / Mint / ...*
    <br>`sudo apt install maven`<br>
  
  * *Arch / Manjaro / ...*
    <br>`sudo pacman -S maven`<br>
  
  * *Fedora*
    <br>`sudo dnf install maven`<br>
  
  * *OpenSUSE*
    <br>`sudo zypper install maven`<br>
  
  * *Slackware*
    <br>`sudo slackpkg install apache-maven`<br>
  
  * *Void*
    <br>`sudo xbps-install apache-maven-bin`<br>
  
  * *FreeBSD*
    <br>`sudo pkg install maven`<br><br>
- Download (and unzip if needed) this repo
- Open a terminal (or `cd`) into "*craplog-javaGUI-main/craplog*". Make sure it is the folder containing the "**pom.xml**" file<br><br>
- Use Maven to compile the entire project:<br>`mvn clean install`
- Use Maven again to download and compile the required dependencies/plugins:<br>`mvn dependency:copy-dependencies`
- At this point you should see a new folder named "**target**", which contains the **jar** archive along with other folders.<br>
  The newely created *jar* is a standalone and can be executed in two ways:<br><br>
- **option 1**:
  - Open the *.jar* archive, open the *META-INF* folder inside it and then modify the ***MANIFEST.MF*** file adding this line (the position in the file doesn't matter, just make sure to add it before the final carriage return): `Main-Class: Main`
  - Save and update the jar archive
  - To run Craplog, just use this command (replace the */path/to/craplog* to fit yours, and the *version number* you have!):
    <br>`java -jar /path/to/craplog/CRAPLOG-version.jar`<br><br>
- **option 2**:
  - As the above, but without modifying the *MANIFEST.MF* file and directly using this command instead:<br>`java -cp /path/to/craplog/CRAPLOG-version.jar Main`<br><br>
- You can now move the jar file (just the archive! you'll not need the other folders created during compilation) wherever you want and execute it from there.<br>
A pre-made folder can be found inside "*craplog-javaGUI-main*", which contains the configurations file (you'll need it, otherwise you'll have default settings at every run) and the crapstats directory (default to contain the statistics files created, can be modified in the configurations). This folder can be then renamed and/or moved anywhere (better before the first run)<br><br>

**Tip**: you can make a *craplog* file (![like this](https://github.com/elB4RTO/craplog-javaGUI/tree/main/installation_stuff/craplog)) containing the command of the option you choose and move it inside */bin* or */usr/bin* to be able to run Craplog from terminal

**Pro tip**: you can then make a *craplog.desktop* file (![like this](https://github.com/elB4RTO/craplog-javaGUI/tree/main/installation_stuff/craplog.desktop)) containing the informations to the *craplog.sh* script, and then move the *craplog.desktop* file inside *~/.local/share/applications* to have a menu entry for Craplog

<br>

## Log files

At the moment, it still only supports **Apache2** log files in their **default** form, but a different path can easily be set from the `Preferences`→`Settings`>`Paths` menu.

<br>

*DEFAULT PATH:*

/var/log/apache2/

<br>

*DEFAULT LOGS' STRUCTURE:*

**access.log.1**
IP - - [DATE:TIME] "REQUEST URI" RESPONSE "FROM URI" "USER AGENT"
123.123.123.123 - - [01/01/2000:00:10:20 +0000] "GET /style.css HTTP/1.1" 200 321 "/index.php" "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Firefox/86.0"

**error.log.1**
[DATE TIME] [LOG LEVEL] [PID] ERROR REPORT
[Mon Jan 01 10:20:30.456789 2000] [headers:trace2] [pid 12345] mod_headers.c(874): AH01502: headers: ap_headers_output_filter()

<br>

*NOTE*:

Please notice that CRAPLOG will olny take '***.log.***' files as input ('*.1*' in case of a single-session job, different numbers if working with a selection). This is because these files (usually) contain the full log stack of an entire (past) day. Running it against a *today*'s file (which is not complete yet) may lead to re-running it in the future on the same file, parsing the same lines twice.<br>
CRAPLOG is no more meant to be ran daily :)

<br>

## Statistics

You can store statistics wherever you want.

<br>

Four fields may be examined while parsing **access** logs:
- IP address of the client
- Requested page/URL
- Response code from the server
- User-agent of the client

<br>

While parsing **error** logs, only two fields will be used:
- Log level
- Error report

<br>

GLOBAL STATISTICS FILES:

Additionally, GLOBAL statistics may be created and/or updated consequently to session statistics.<br>
These statistics are identical to the session ones, in fact they're just merged sessions, for a larger view.

<br>

### Whitelist

You can now add IP addresses to this list (may them be full IPs, only a the net-ID part or just a portion of your choice), in order to skip the relative lines by whitelisting (or blacklisting..?) them, in both **access** and **error** logs.<br>
Please notice that the given sequence must be the starting part: it's not possible (at the moment, and more likely also in future versions) to skip IPs endings or just containing that sequence.<br>
As an example, if you insert "123", then only IP addresses starting with that sequence will be skipped; if you insert ".1", then nothing will be skipped, since it is considered invalid, but the shortcut "::1" is used by Apache2 for internal connections and will therefore be valid to skip those lines.

![image](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java4.png)

<br>

## Final considerations

<br>

ESTIMATED WORKING SPEED:

1~8 sec / 1 MB

May be higher or lower depending on the complexity of the logs, the length of your GLOBALS (in case you're updating them), your hardware and the workload of your system during the execution.<br>
If CRAPLOG takes more than 1 minute for a 10 MB file, you've probably been tested in some way (better to check).

<br>

BACKUPS:

CRAPLOG will automatically make backups of GLOBAL statistic files (in case of fire).<br>
If something goes wrong and you lose your actual GLOBAL files, you can recover them (at least the last backup).<br>
Move inside the folder you choose to store statistics in (if you don't remember the path, you can open the `Preferences`→`Settings`>`Paths` menu, to view it. Beware that modifiyng a path which already contains statistics, will not move the files/folders in the new location), open the "**globals**" folder, show hidden files and open the folder named "**.backups**'. Here you will find the last 3 backups taken.<br>
Folder named '3' is always the oldest and '1' the newest.<br>
Starting by this version, a new BACKUP is made every time you run CRAPLOG successfully over GLOBALS.

<br>

## Contributions

CRAPLOG is under development

If you have suggestions about how to improve it please open an ![issue](https://github.com/elB4RTO/craplog-javaGUI/issues) or make a ![pull request](https://github.com/elB4RTO/craplog-javaGUI/pulls)

If you're not running Apache, but you like this tool: same as the above (bring a sample of a log file)
