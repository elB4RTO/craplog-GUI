# craplog-GUI
A tool that scrapes Apache2 logs to create both Single-Session and Global statistics

<br/>

## Table of contents

- [Overview](#overview)
- [Installation and usage](#installation-and-usage)
  - [Requirements, dependencies and plugins](#requirements--dependencies--plugins)
  - [Usage without installation](#usage-without-installation)
  - [Usage with installation](#usage-with-installation)
- [Compilation](#compilation)
  - [How to compile](#how-to-compile)
  - [Additional steps](#additional-steps)
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

<br/><br/>

## Overview

Craplog is a tool that takes Apache2 logs in their default form, parses them and creates simple statistics.

<br/>

Welcome to the fully **graphical** version

![screenshot](https://raw.githubusercontent.com/elB4RTO/screenshots/main/Craplog/GUI/session.png)

![screenshot](https://raw.githubusercontent.com/elB4RTO/screenshots/main/Craplog/GUI/selection.png)

<br/>

Craplog will store statistics depending on the date of the single lines, to analyze/backtrack statistics more easily.<br/>
Be aware that log-files usage is not tracked, be careful of not parsing the same logs twice, which will lead to altered statistics.

![screenshot](https://raw.githubusercontent.com/elB4RTO/screenshots/main/Craplog/GUI/view_stats.png)

<br/>

It is also possible to display the log files contained in the logs folder, to directly view their content.

![screenshot](https://raw.githubusercontent.com/elB4RTO/screenshots/main/Craplog/GUI/view_logs.png)

<br/><br/>

Searching for something different? Try the [other versions of CRAPLOG](https://github.com/elB4RTO/CRAPLOG).

<br/>

## Installation and usage

### Requirements / Dependencies / Plugins
- *JavaSE 11*
- *Maven plugins*
- *Apache common compress*

<br/>

### Usage without installation

- Download a pre-compiled [Release](https://github.com/elB4RTO/craplog-GUI/releases)
  <br/>*or*<br/>
  Follow the step-by-step "[How to compile](#how-to-compile)" guide

- Execute the *jar* file by using your installed **Java Runtime Environment** (usually *openJDK jre*)
  <br/>*or*<br/>
  By using this command from terminal (replace the */path/to/craplog* to fit yours, and use the *version* number you have!):
  <br/>`java -jar /path/to/craplog/CRAPLOG-version.jar`<br/><br/>

<br/>

### Usage with installation

- **From source**
  - Download and unzip this repo
    <br/>*or*<br/>
    `git clone https://github.com/elB4RTO/craplog-GUI`<br/><br/>
  - Open a terminal inside "*craplog-GUI-main/craplog*"
    <br/>*or*<br/>
    `cd craplog-GUI/craplog`<br/><br/>
  - Run the installation script
    <br/>`chmod +x ./build_install.sh && ./build_install.sh`<br/><br/>
- **From binary**
  - Download a pre-compiled [Release](https://github.com/elB4RTO/craplog-GUI/releases)
  - Run the installation script
    <br/>`chmod +x ./install.sh && ./install.sh`<br/><br/>

<br/>

## Compilation

### How to compile

- Install the **Maven Project Manager** from your system's package manager:<br/>
  * *Debian / Ubuntu / Mint / ...*
    <br/>`sudo apt install maven`<br/>

  * *Arch / Manjaro / ...*
    <br/>`sudo pacman -S maven`<br/>

  * *Fedora*
    <br/>`sudo dnf install maven`<br/>

  * *OpenSUSE*
    <br/>`sudo zypper install maven`<br/>

  * *Slackware*
    <br/>`sudo slackpkg install apache-maven`<br/>

  * *Void*
    <br/>`sudo xbps-install apache-maven-bin`<br/>

  * *FreeBSD*
    <br/>`sudo pkg install maven`<br/><br/>
- Download and unzip this repo
  <br/>*or*<br/>
  `git clone https://github.com/elB4RTO/craplog-GUI`<br/><br/>
- Open a terminal inside "*craplog-GUI-main/craplog*"
  <br/>*or*<br/>
  `cd craplog-GUI/craplog`<br/><br/>
- Make sure you're inside the folder containing the "**pom.xml**" file
  <br/>`if [ -f "./pom.xml" ]; then echo "You're good to go!"; else echo "Hmm... no, wrong location"; fi`<br/><br/>
- Use **Maven** to compile the entire project:
  <br/>`mvn clean install`
  <br/>This command will download and build the necessary dependencies (if you don't have them already), so make sure you're connected to the internet during this step<br/><br/>
- At this point you should see a new folder named "**target**", which contains two **jar** archives along with other folders:<br/><br/>
  - The "*CRAPLOG-x.xx.jar*" file needs the *Apache Common Compress* library to be installed on the system and some [additional steps](#additional-steps) to be taken<br/><br/>
  - The "*CRAPLOG-x.xx-jar-with-dependencies.jar*" file is a ready-to-use standalone (can be portable), the difference in dimensions is negligible and is therefore recommended to use this one.
  <br/>You can use the following commands (as they are) to remove the dependent archive and rename the standalone one:
  <br/>`cd target && version=$(ls | grep CRAPLOG-[0-9]\.[0-9][0-9]-jar-with-dependencies.jar | cut -d \- -f2)`
  <br/>If the previous command succeeded:
  <br/>`rm "CRAPLOG-$version.jar" && mv "CRAPLOG-$version-jar-with-dependencies.jar" "CRAPLOG-$version.jar"`<br/><br/>
    - To run Craplog, just use this command (replace the */path/to/craplog* to fit yours, and use the *version* number you have!):
      <br/>`java -jar /path/to/craplog/CRAPLOG-version.jar`<br/><br/>
- You can now move the jar file (just the archive! Whatever archive you choose, you'll not need the other folders created during compilation) wherever you want and execute it from there.<br/>
A pre-made folder can be found at "*craplog-GUI/pre-made_folder*", which contains the configurations file (you'll need it, otherwise you'll have default settings at every run) and the crapstats directory (default to contain the statistics files created, can be modified in the configurations). This folder can be then renamed and/or moved anywhere (better before the first run)<br/><br/>

**Tip**: you can make a *craplog* script (![like this](https://github.com/elB4RTO/craplog-GUI/tree/main/installation_stuff/craplog)), containing the command of the option you choose and move it inside */bin* or */usr/bin* to be able to run Craplog from terminal

**ProTip**: you can then make a *craplog.desktop* file (![like this](https://github.com/elB4RTO/craplog-GUI/tree/main/installation_stuff/craplog.desktop)), containing the informations to the *craplog* script (it must be present inside your bins!) and then move the *craplog.desktop* file inside *~/.local/share/applications* to have a menu entry for Craplog

<br/>

### Additional steps

Please follow these additional steps **if and only if** you've decided to use the *CRAPLOG-x.xx.jar* file while following the compilation guide<br/>
Also, make sure to replace the */path/to/craplog* to fit yours, and use the *version* number you have<br/>

There are two ways to use this file:<br/>
- By passing the Main class inline, using this command instead of the one provided for the standalone:
  <br/>`java -cp /path/to/craplog/CRAPLOG-version.jar Main`<br/><br/>
- Or by manually adding the **Main Class** to the *MANIFEST*:
  - Open the *jar* archive with your archive manager
  - Step inside the "*META-INF*" folder
  - Extract the "*MANIFEST.MF*" file
  - Modify it by adding the following line (wherever you want, but make sure to have a new-line at the end of the file):
    <br/>`Main-Class: Main`
  - Save the document
  - Delete the old "*MANIFEST.MF*" entry from the archive and add the freshly modified one in the same position
  - Save and update the archive if needed
  - You can now run Craplog using the following command:
    <br/>`java -jar /path/to/craplog/CRAPLOG-version.jar`<br/><br/>

<br/>

## Log files

At the moment, it still only supports **Apache2** log files in their **default** form, but a different path can easily be set from the `Preferences`→`Settings`>`Paths` menu.

Archived (**gzipped**) log files can be used as well as normal files.

<br/>

### Default logs path

/var/log/apache2/

<br/>

### Default logs structure

At the moment of writing, this is the only supported logs structure.<br/><br/>

#### access.log.*

IP - - [DATE:TIME] "REQUEST URI" RESPONSE "FROM URI" "USER AGENT"

*123.123.123.123 - - [01/01/2000:00:10:20 +0000] "GET /style.css HTTP/1.1" 200 321 "/index.php" "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Firefox/86.0"*

<br/>

#### error.log.*

[DATE TIME] [LOG LEVEL] [PID] ERROR REPORT

*[Mon Jan 01 10:20:30.456789 2000] [headers:trace2] [pid 12345] [client 123.123.123.123:45678] AH00128: File does not exist: /var/www/html/domain/readme.txt*

<br/>

## Statistics

### Storage

You can now store statistics wherever you want by modifyng the path to be used from the *Settings menu*.<br/>
While running Craplog select `Preferences`→`Settings`>`Paths`.

Be aware that modifiyng a path which already contains statistics files/folders, will **not** move the contents in the new location!

<br/>

### Examined fields

#### Access logs

Four fields can be examined while parsing **access** logs:
- IP address of the client
- Requested page/URL
- Response code from the server
- User-agent of the client

<br/>

#### Error logs

While parsing **error** logs, only two fields will be used:
- Log level
- Error report

<br/>

### Sessions statistics

**Sessions** are made by grouping statistics depending on the **date** of the single lines and will be stored consequently: new content will be created if that date is not present yet, or it will be merged if the date already exists.

Olny '**\*.log.\***' files will be taken as input ('*.1*' in case of a single-session job, different numbers if working with a selection). This is because these files (usually) contain the full logs stack of an entire (*past*) day.<br/>
Running it against a *today*'s file (which is not complete yet) may lead to re-running it in the future on the same file, parsing the same lines twice.

<br/>

### Global statistics

Additionally, **global statistics** may be created and/or updated *consequently*.<br/>
These statistics are identical to the session ones, in fact they're just merged sessions, for a larger view.

<br/>

### Whitelist

You can now add IP addresses to this list (may them be full *IPs*, only a the *net-ID* part or just a portion of your choice), in order to skip the relative lines by whitelisting (or blacklisting..?) them, in both **access** and **error** logs.

Please notice that the given sequence must be the starting part: it's not possible (at the moment, and more likely also in future versions) to skip IPs ending or just containing that sequence.

As an example, if you insert "123", then only IP addresses starting with that sequence will be skipped.<br/>
If you insert ".1", then nothing will be skipped, since no IP will ever start with a dot.<br/>
But the shortcut "::1" is used by Apache2 for internal connections and will therefore be valid to skip those lines.

![screenshot](https://raw.githubusercontent.com/elB4RTO/screenshots/main/Craplog/GUI/whitelist.png)

<br/><br/>

## Extra features

### Note-block

A note-block utility is available at `Utilities`→`Note` which can be used to temporary write text, notes, etc

<br/>

### Check updates

You can use `Utilities`→`Check updates` to query this repo and receive informations about version-updates.<br/>
No update will be done though, the utility just checks the version number: the *download* has to be done manually, but a *build_update.sh* script is provided

<br/>

## Final considerations

### Estimated working speed

1~10 MB/s

May be higher or lower depending on the complexity of the logs, the complexity of the stored statistics (in case of merge), your hardware and the workload of your system during the execution.<br/>
Usually, if Craplog is taking more than 10 seconds to parse 10 MB of data, it means you've probably been tested in some way (better to check).

<br/>

### Backups

Craplogwill automatically backup **global statistics** files (in case of fire).<br/>
If something goes wrong and you lose your actual globals, you can recover them (at least the last backup).

Move inside the folder you choose to store statistics in (if you don't remember the path, you can open the `Preferences`→`Settings`>`Paths` menu, to view it. Beware that modifiyng a path which already contains statistics, will not move the files/folders in the new location), open the "**globals**" folder, show hidden files and open the folder named "**.backups**'.<br/>
The complete path should look like **/<your_path>/craplog/crapstats/globals/.backups/**<br/>
Here you will find the last 3 backups taken. Folder named '3' is always the oldest and '1' the newest.

Starting by this version, a new ackupis made every time you run Craplog *successfully* over GLOBALS.<br/><br/>
Please notice that session statistics will **not** be backed-up

<br/>

## Contributions

CRAPLOG is under development

If you have suggestions about how to improve it please open an ![issue](https://github.com/elB4RTO/craplog-GUI/issues) or make a ![pull request](https://github.com/elB4RTO/craplog-GUI/pulls)

If you're not running Apache, but you like this tool: same as the above (bring a sample of a log file)

<br/>
