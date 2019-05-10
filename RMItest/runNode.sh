make clean
make
java -Djava.rmi.server.hostname=$1 Server 54.209.66.61 $1
