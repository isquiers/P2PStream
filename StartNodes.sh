NODE=('54.209.66.61')
NODES=('3.94.170.64' '100.26.104.102' '3.83.10.22' '3.14.64.40')
NOODES=('3.14.67.164' '54.153.0.234' '54.193.125.205' '52.24.143.203' '34.221.171.231' '35.183.5.27' '35.182.195.196' '13.233.35.167' '13.233.83.84' '13.229.131.254' '3.0.94.77' '54.252.169.133' '13.54.28.88' '54.250.199.120' '13.112.35.80' '18.185.138.61' '52.59.249.174' '34.249.192.207' '54.229.234.118' '35.181.59.157' '35.181.43.210' '18.130.245.254' '35.178.15.57' '54.233.223.52' '18.228.190.7')

for node in "${NODE[@]}"; do
  scp -i $1 -r P2PStream/RMItest/ $2@$node:
  ssh -i $1 $2@$node "cd RMItest/ ; chmod +x runNode.sh"
  #ssh -i $1 $2@$node "cd RMItest/ ; mkdir cats"
  ssh -i $1 $2@$node 'cd RMItest/ ; nohup ./runNode.sh '$node' > command.log 2>&1 &'
  # echo ssh -i $1 $2@$node "cd RMItest; make"
  # echo ssh -i $1 $2@$node "cd RMItest; java -Djava.rmi.server.hostname=$node Server $node 54.209.66.61"
  sleep 5s
done
