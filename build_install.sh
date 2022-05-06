#!/bin/bash

# WELCOME MESSAGE
printf "\nWelcome to the $(tput setaf 1)C$(tput setaf 3)R$(tput setaf 2)A$(tput setaf 6)P$(tput setaf 4)L$(tput setaf 5)O$(tput setaf 7)G$(tput sgr0)'s installer\n\n"
sleep 1 && wait

# GET THE PATH OF CRAPLOG-GIT'S FOLDER
crapdir="$(dirname $(realpath $0))"

# INITIAL WARNING ABOUT PRIVILEDGES
printf "This script will later ask for $(tput setaf 1)sudo$(tput sgr0) privileges to copy the executable inside $(tput setaf 3)/usr/bin/$(tput sgr0)\nIf you prefer to do it manually, answer [$(tput bold)N$(tput sgr0)] to the incoming question\n"
printf "CONTINUE? [Y/N] : "
read agree
case "$agree"
	in
		"y" | "Y")
			printf ""
		;;
		*)
			printf "\nInstruction about how to install can be found on Craplog's GitHub page or inside the README.md file in the download folder:\n"
			printf "  - $crapdir/README.md\n"
			printf "  - https://github.com/elB4RTO/craplog-javaGUI\n"
			printf "\nInstallation ABORTED\n\n"
			sleep 1 && wait
			exit
		;;
	esac

# INSTALLATION SECTION
#
# GET THE INSTALLATION VERSION
version=$(cat "./version_check")
version=${version:15:4}

# CHECKING MAVEN COMPILER AVAILABILITY
if [[ $(which mvn) =~ ^/ ]]
	then
		$()
	else
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nThe $(tput setaf 8)Maven Project Manager$(tput sgr0) is required but not installed\n"
		printf "Instruction about how to get it can be found on Craplog's GitHub page or inside the README.md file in the download folder:\n"
		printf "  - $crapdir/README.md\n"
		printf "  - https://github.com/elB4RTO/craplog-javaGUI\n"
		printf "Please install Maven and retry, or download a pre-compiled RELEASE from Craplog's GitHub page\n"
		printf "$(tput setaf 1)Installation ABORTED$(tput sgr0)\n\n"
		exit
	fi

# CHECKING THE EXISTENCE OF A PREVIOUS EXECUTABLE FILE IN /usr/bins
if [ -e /usr/bin/craplog ]
	then
		while true;
			do
				printf "\n$(tput setaf 3)WARNING$(tput sgr0): file $(tput setaf 8)/usr/bin/$(tput setaf 1)craplog$(tput sgr0) already exists\n"
				printf "If you choose to continue, the actual file will be lost forever\nOVERWRITE FILE? [Y/N] : "
				read agree
				case "$agree"
					in
						"y" | "Y")
							printf "\n"
							break
						;;
						*)
							printf "\nInstallation ABORTED\n\n"
							exit
						;;
					esac
			done
	fi

# SAVE THE ACTUAL PATH
actual_path=$(pwd)

# ASK FOR A PATH TO STORE CRAPLOG IN
while :
	do
		printf "INSERT THE FULL (ABSOLUTE) PATH TO WHERE YOU WANT TO INSTALL CRAPLOG IN\n $(tput bold):$(tput sgr0) "
		read crappath
		if [[ $crappath =~ /$ ]]
			then
				crappath=${crappath:0:$(( $(printf $crappath | wc -m) -1 ))}
			fi
		case "$crappath"
			in
				"")
					printf "The path can't be null, please insert a valid path\n\n"
					continue
				;;
				
				"exit" | "q" | "Q")
					printf "\nInstallation ABORTED\n\n"
					exit
				;;
				
				*)
					printf "\nThe final installation path will be:\n$(tput bold)$crappath/craplog/$(tput sgr0)\n"
					printf "CONTINUE? [Y/N] : "
					read agree
					case "$agree"
						in
							"y" | "Y")
								$()
							;;
							*)
								printf "\n"
								continue
							;;
						esac
					# CHECKING EXISTENCE CONFLICTS
					if [ -e "$crappath/craplog" ]
						then
							if [ -f "$crappath/craplog" ]
								then
									printf "NAME CONFLICT: the given path corresponds to a file. Please insert a valid path or delete the file\n"
									continue
								fi
					elif [ ! -e "$crappath" ]
						then
							printf "\nThe given path does not exists yet. Every missing folder in the path will be created\n"
							printf "CONTINUE? [Y/N] : "
							read agree
							case "$agree"
								in
									"y" | "Y")
										mkdir -p "$crappath"
									;;
									*)
										printf "\n"
										continue
									;;
								esac
						
						fi
					# CHECKING THE EXISTENCE OF A PREVIOUS INSTALLATION
					if [ -d "$crappath/craplog" ] \
					&& [[ $(ls -l "$crappath/craplog/" | wc -l) -gt 1 ]]
						then
							printf "\n$(tput setaf 3)WARNING$(tput sgr0): the installation folder is not empty\n"
							printf "If you choose to proceed, the folder will be completely erased\n"
							printf "CONTINUE? [Y/N] : "
							read agree
							case "$agree"
								in
									"y" | "Y")
										while [ -e "$crappath/craplog" ]
											do
												rm -r "$crappath/craplog"
											done
									;;
									*)
										printf "\n"
										continue
									;;
								
								esac
							
						fi
				;;
			esac
			break
	done

# MOVE IN CRAPLOG'S FOLDER
cd craplog

# EVERYTHING OK, INSTALL CRAPLOG
printf "\n\n$(tput setaf 15)STARTING INSTALLATION$(tput sgr0)\n\n"

# COMPILE THE PROJECT WITH MAVEN
printf "$(tput setaf 11)Compiling project\n$(tput setaf 7)"
mvn clean install
if [ "$?" = "0" ]
	then
		# compiled succesfully
		printf "$(tput setaf 10)Done compiling$(tput sgr0)\n"
	else
		# an error occured during compilation
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nAn error occured during compilation\n"
		printf "\nInstallation ABORTED\n\n"
		exit
	fi
# MOVE THE JAR INSIDE THE FINAL FOLDER
mv -f "./target/CRAPLOG-$version-jar-with-dependencies.jar" "../pre-made_folder/CRAPLOG-$version.jar"
if [ "$?" = "1" ]
	then
		# an error occured
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nAn error occured while moving the JAR\n"
		printf "\nInstallation ABORTED\n\n"
		exit
	fi
rm "../pre-made_folder/crapstats/DELETEME" &> /dev/null
# COMPLETE THE LAUNCH FILES
if [ ! -d "../installation_stuff/" ]
	then
		# missing stuff
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nThe folder named "$(tput bold)installation_stuff$(tput sgr0)" is needed to complete the installation, but it's missing\n"
		printf "\nInstallation ABORTED\n\n"
		exit
	fi
cd "../installation_stuff/"
printf "\n$(tput setaf 11)Making launchers\n$(tput setaf 7)"
sudo printf "$(cat "./craplog")\n\njava -jar $crappath/craplog/CRAPLOG-$version.jar\n\n" > "/usr/bin/craplog" &&\
sudo chmod +x "/usr/bin/craplog"
if [ "$?" = "1" ]
	then
		# an error occured
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nAn error occured while creating the bins entry\n"
		printf "\nInstallation ABORTED\n\n"
		exit
	fi
if [ ! -d ~/".local/share/applications" ]
	then
		# missing dir
		printf "\n$(tput setaf 3)WARNING$(tput sgr0): the folder designed to hold menu entries does not exist\n"
		printf "Please report this issue here: $(tput setaf 12)https://github.com/elB4RTO/craplog-javaGUI/issues$(tput sgr0)\n"
		printf "$(tput bold)Skipping the creation of a menu entry$(tput sgr0)\n\n"
	else
		printf "$(cat "./craplog.desktop")" > ~/".local/share/applications/craplog.desktop"
		if [ "$?" = "1" ]
			then
				# an error occured during compilation
				printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nAn error occured while creating the menu entry\n"
				printf "$(tput bold)Skipping the creation of a menu entry$(tput sgr0)\n\n"
			fi
	fi
printf "$(tput setaf 10)Done making launchers$(tput sgr0)\n"
# MOVE CRAPLOG'S DIR
cd ..
printf "\n$(tput setaf 11)Moving files\n$(tput setaf 7)"
mv "./pre-made_folder/" "$crappath/craplog"
if [ "$?" = "0" ]
	then
		# succesfully moved
		printf "$(tput setaf 10)Done moving files$(tput sgr0)\n"
	else
		# an error occured
		printf "\n$(tput setaf 1)WARNING$(tput sgr0):\nAn error occured while moving files\n"
		printf "\nInstallation ABORTED\n\n"
		exit
	fi

# FIN
printf "\n$(tput setaf 15)INSTALLATION COMPLETED$(tput sgr0)\n\n\n"
sleep 1 && wait
printf "You can now run CRAPLOG from inside your terminal or from the menu entry\n\n"
sleep 1 && wait
printf "$(tput setaf 3)F$(tput setaf 2)I$(tput setaf 6)N$(tput sgr0)\n\n"

