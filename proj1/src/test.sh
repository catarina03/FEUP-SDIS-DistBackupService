#! /usr/bin/bash

# Checking number of arguments
if [ "$#" -le 2 ]; then
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
cd classDir
java TestApp "$1" "$2" "$3" "$4" 

# java TestApp Peer1 BACKUP ../arder.jpg 2
# ./test.sh Peer1 BACKUP ../arder.jpg 2
