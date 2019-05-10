make clean
make
java -Djava.rmi.server.hostname=$1 Server $1 54.209.66.61
