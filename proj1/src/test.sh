#! /usr/bin/bash

# Checking number of arguments
if [ "$#" -lt 2 ]; then
  echo
  echo "Usage: sh $0 <peer_ap> <sub_protocol> <opnd_1> <opnd_2>"
  exit 1;
fi

if [ "$#" -gt 4 ]; then
  echo
  echo "Usage: sh $0 <peer_ap> <sub_protocol> <opnd_1> <opnd_2>"
  exit 1;
fi

# execute peer in new terminal
cd build

if [ "$#" -eq 2 ]; then
java TestApp "$1" "$2"
fi

if [ "$#" -eq 4 ]; then
java TestApp "$1" "$2" "$3" "$4"
fi

if [ "$#" -eq 3 ]; then
java TestApp "$1" "$2" "$3" 
fi



# java TestApp Peer1 BACKUP ../arder.jpg 2
# ./test.sh Peer1 BACKUP ../arder.jpg 2
