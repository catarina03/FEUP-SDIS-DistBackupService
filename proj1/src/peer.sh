#! /usr/bin/bash

# Checking number of arguments
if [ "$#" -ne 3 ]; then
  echo
  echo "Usage:"
  echo "sh $0 <protocol_version> <peer_id> <peer_ap> <MC_addr> <MC_port> <MDB_addr> <MDB_port> <MDR_addr> <MDR_port>"
  exit 1;
fi

# execute peer in new terminal
java Peer "$1" "$2" "$3"