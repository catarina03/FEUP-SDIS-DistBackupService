#! /usr/bin/bash

# Checking number of arguments
if [ "$#" -ne 9 ]; then
  echo
  echo "Usage: sh $0 <protocol_version> <peer_id> <peer_ap> <MC_addr> <MC_port> <MDB_addr> <MDB_port> <MDR_addr> <MDR_port>"
  exit 1;
fi

# execute peer in new terminal
cd classDir
java peer.Peer "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"

# java peer.Peer 1.0 1 Hello 232.0.0.0 1111 232.0.0.1 2222 232.0.0.2 3333
# java peer.Peer 1.0 2 Hello 232.0.0.0 1111 232.0.0.1 2222 232.0.0.2 3333
