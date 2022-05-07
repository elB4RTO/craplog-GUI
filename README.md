# craplog-javaGUI
A tool that scrapes Apache2 logs to create both Session and Global statistics

<br>

``` diff
- !!! THIS REPOSITORY IS NOT FULLY ACTIVE YET !!!
- INSTALLATION SUPPORT FILES ARE NOT PRESENT AT THE TIME OF WRITING
```
<br>

## Table of contents

- [Overview](#overview)
- [Installation and usage](#installation-and-usage)
  - [Requirements, dependencies and plugins](#requirements--dependencies--plugins)
  - [Usage without installation](#usage-without-installation)
  - [Usage with installation](#usage-with-installation)
  - [How to compile](#how-to-compile)
- [Log files](#log-files)
  - [Default logs path](#default-logs-path)
  - [Default logs structure](#default-logs-structure)
- [Statistics](#statistics)
  - [Storage](#storage)
  - [Examined fields](#examined-fields)
  - [Sessions statistics](#sessions-statistics)
  - [Global statistics](#global-statistics)
  - [Whitelist](#whitelist)
- [Extra features](#extra-features)
  - [Note-block](#note-block)
  - [Check updates](#check-updates)
- [Final considerations](#final-considerations)
  - [Estimated working speed](#estimated-working-speed)
  - [Backups](#backups)
- [Contributions](#contributions)

<br><br>

## Overview

CRAPLOG is a tool that takes Apache2 logs in their default form, parses them and creates simple statistics.

<br>

This fully Graphical version of CRAPLOG allows you to work on single-session log files (*\*.log.1*), or to select multiple files to use (eg. older logs/gzipped-logs files).

![screenshot](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java1a.png)

![screenshot](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java1b.png)

<br>

Differently from the other versions, this version of CRAPLOG will store statistics depending on the date of the single lines, to give more imporance to times and to analyze/backtrack statistics more easily.<br>
Be aware that log-files usage is not tracked, be careful of not parsing the same logs twice, which will lead to altered statistics.

![screenshot](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java2.png)

<br>

It is also possible to display the log files contained in the logs folder, to directly view their content.

![screenshot](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java3.png)

<br><br>

Searching for something different? Try the <a href="https://github.com/elB4RTO/CRAPLOG">other versions of CRAPLOG</a>.

<br>

## Installation and usage

### Requirements / Dependencies / Plugins
- *JavaSE 11*
- *Maven plugins*
- *Apache common compress*

<br>

### Usage without installation

- Download a pre-compiled [Release](https://github.com/elB4RTO/craplog-javaGUI/releases)
  <br>*or*<br>
  Follow the step-by-step "[How to compile](#how-to-compile)" guide
  
- Execute the *jar* file by using your installed **Java Runtime Environment** (usually *openJDK jre*)
  <br>*or*<br>
  By using this command from terminal (replace the */path/to/craplog* to fit yours, and use the *version* number you have!):
  <br>`java -jar /path/to/craplog/CRAPLOG-version.jar`<br><br>

<br>

### Usage with installation

- **From source**
  - Download and unzip this repo
    <br>*or*<br>
    `git clone https://github.com/elB4RTO/craplog-javaGUI`<br><br>
  - Open a terminal inside "*craplog-javaGUI-main/craplog*"
    <br>*or*<br>
    `cd craplog-javaGUI/craplog`<br><br>
  - Run the installation script
    <br>`chmod +x ./build_install.sh && ./build_install.sh`<br><br>
- **From binary**
  - Download a pre-compiled [Release](https://github.com/elB4RTO/craplog-javaGUI/releases)
  - Run the installation script
    <br>`chmod +x ./install.sh && ./install.sh`<br><br>

<br>

### How to compile

- Check if you already installed the **Maven Project Manager**:
  <br>`if [[ $(which mvn) =~ ^/ ]]; then echo "You're good to go\!"; else echo "You must install Maven to continue\!"; fi`
  
  If you're **not** seeing the "***You're good to go!***" response, follow the next step before to proceed
  - Install the **Maven Project Manager** from your system's package manager:<br>
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
- Download and unzip this repo
  <br>*or*<br>
  `git clone https://github.com/elB4RTO/craplog-javaGUI`
  
- Open a terminal inside "*craplog-javaGUI-main/craplog*"
  <br>*or*<br>
  `cd craplog-javaGUI/craplog/`<br><br>
- Make sure you're inside the folder containing the "**pom.xml**" file
  <br>`if [ -f "./pom.xml" ]; then echo "You're good to go\!"; else echo "Hmm... no, wrong location"; fi`
  
- Use **Maven** to compile the entire project:
  <br>`mvn clean install`
  
- At this point you should see a new folder named "**target**", which should contain two **jar** archive along with other folders.
  <br>The file named "*CRAPLOG-version.jar*" will make use of dynamic libraries, it's slightly smaller but dependent from the system.
  <br>The file named "*CRAPLOG-version-jar-with-dependencies.jar*" is a standalone: can be portable, it only needs *JRE* to run and is therefore the ***suggested*** one. You can rename it as you please, for example as "*CRAPLOG-version.jar*" using this command:
  <br>`version=$(ls | grep jar-with-dependencies | cut -d \- -f2 | cut -d \. -f1,2) && rm CRAPLOG-$version.jar && mv CRAPLOG-$version-jar-with-dependencies.jar CRAPLOG-$version.jar`<br><br>
- To run Craplog, just use this command (replace the */path/to/craplog* to fit yours, and use the *version* number you have!):
  <br>`java -jar /path/to/craplog/CRAPLOG-version.jar`
  
- You can now move the jar file (just the archive! you'll not need the other folders created during compilation) wherever you want and execute it from there.<br>
A pre-made folder can be found at "*craplog-javaGUI/pre-made_folder*", which contains the configurations file (you'll need it, otherwise you'll have default settings at every run) and the crapstats directory (default to contain the statistics files created, can be modified in the configurations). This folder can be then renamed and/or moved anywhere (better before the first run)<br><br>

**Tip**: you can make a *craplog* file (![like this](https://github.com/elB4RTO/craplog-javaGUI/tree/main/installation_stuff/craplog)) containing the command of the option you choose and move it inside */bin* or */usr/bin* to be able to run Craplog from terminal

**ProTip**: you can then make a *craplog.desktop* file (![like this](https://github.com/elB4RTO/craplog-javaGUI/tree/main/installation_stuff/craplog.desktop)) containing the informations to the *craplog* script (it must be present inside your bins!) and then move the *craplog.desktop* file inside *~/.local/share/applications* to have a menu entry for Craplog

<br><br>

## Log files

At the moment, it still only supports **Apache2** log files in their **default** form, but a different path can easily be set from the `Preferences`→`Settings`>`Paths` menu.

<br>

### Default logs path

/var/log/apache2/

<br>

### Default logs structure

**access.log.1**<br>
IP - - [DATE:TIME] "REQUEST URI" RESPONSE "FROM URI" "USER AGENT"<br>
*123.123.123.123 - - [01/01/2000:00:10:20 +0000] "GET /style.css HTTP/1.1" 200 321 "/index.php" "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Firefox/86.0"*


**error.log.1**<br>
[DATE TIME] [LOG LEVEL] [PID] ERROR REPORT<br>
*[Mon Jan 01 10:20:30.456789 2000] [headers:trace2] [pid 12345] mod_headers.c(874): AH01502: headers: ap_headers_output_filter()*

<br><br>

## Statistics

### Storage

You can now store statistics wherever you want by modifyng the path to be used from the *Settings menu*.<br>
While running Craplog select `Preferences`→`Settings`>`Paths`.<br><br>
Be aware that modifiyng a path which already contains statistics files/folders, will **not** move the contents in the new location!

<br>

### Examined fields

Four fields can be examined while parsing **access** logs:
- IP address of the client
- Requested page/URL
- Response code from the server
- User-agent of the client

<br>

While parsing **error** logs, only two fields will be used:
- Log level
- Error report

<br>

### Sessions statistics

Sessions are made by grouping statistics depending on the date of the single lines of every parsed log file and will be stored consequently: a new folder will be made if that date is not present yet, or the content will be merged if it already exists.<br><br>
Olny '**\*.log.\***' files will be taken as input ('*.1*' in case of a single-session job, different numbers if working with a selection). This is because these files (usually) contain the full logs stack of an entire (past) day.<br>
Running it against a *today*'s file (which is not complete yet) may lead to re-running it in the future on the same file, parsing the same lines twice.<br><br>
Craplog is no more meant to be ran daily, since archived log files can be used as well as normal log files

<br>

### Global statistics

Additionally, GLOBAL statistics may be created and/or updated *consequently* to SESSION statistics.<br>
These statistics are identical to the session ones, in fact they're just merged sessions, for a larger view.

<br>

### Whitelist

You can now add IP addresses to this list (may them be full *IPs*, only a the *net-ID* part or just a portion of your choice), in order to skip the relative lines by whitelisting (or blacklisting..?) them, in both **access** and **error** logs.<br><br>
Please notice that the given sequence must be the starting part: it's not possible (at the moment, and more likely also in future versions) to skip IPs ending or just containing that sequence.<br><br>
As an example, if you insert "123", then only IP addresses starting with that sequence will be skipped.<br>
If you insert ".1", then nothing will be skipped, since no IP will ever start with a dot.<br>
But the shortcut "::1" is used by Apache2 for internal connections and will therefore be valid to skip those lines.

![screenshot](https://github.com/elB4RTO/CRAPLOG/blob/main/crapshots/java4.png)

<br><br>

## Extra features

### Note-block

A note-block utility is available at `Utilities`→`Note` which can be used to temporary write text, notes, etc

<br>

### Check updates

You can use `Utilities`→`Check updates` to query this repo and receive informations about version-updates.<br>
No update will be done though, the utility just checks the version number: the *download* has to be done manually, but a *build_update.sh* script is provided

<br><br>

## Final considerations

### Estimated working speed

1~8 sec / 1 MB

May be higher or lower depending on the complexity of the logs, the length of your GLOBALS (in case you're updating them), your hardware and the workload of your system during the execution.<br>
If CRAPLOG takes more than 1 minute for a 10 MB file, you've probably been tested in some way (better to check).

<br>

### Backups

CRAPLOG will automatically make backups of GLOBAL statistic files (in case of fire).<br>
If something goes wrong and you lose your actual GLOBAL files, you can recover them (at least the last backup).<br><br>
Move inside the folder you choose to store statistics in (if you don't remember the path, you can open the `Preferences`→`Settings`>`Paths` menu, to view it. Beware that modifiyng a path which already contains statistics, will not move the files/folders in the new location), open the "**globals**" folder, show hidden files and open the folder named "**.backups**'. Here you will find the last 3 backups taken.<br>
Folder named '3' is always the oldest and '1' the newest.<br><br>
Starting by this version, a new BACKUP is made every time you run Craplog *successfully* over GLOBALS.<br><br>
Please notice that SESSION statistics will **not** be backed-up

<br><br>

## Contributions

CRAPLOG is under development

If you have suggestions about how to improve it please open an ![issue](https://github.com/elB4RTO/craplog-javaGUI/issues) or make a ![pull request](https://github.com/elB4RTO/craplog-javaGUI/pulls)

If you're not running Apache, but you like this tool: same as the above (bring a sample of a log file)
