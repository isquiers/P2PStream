NODE=('54.209.66.61')
NODES=('3.94.170.64' '100.26.104.102' '3.83.10.22' '3.14.64.40' '54.153.0.234' '54.193.125.205' '52.24.143.203' '34.221.171.231' '35.183.5.27')
NOODES=('3.14.67.164' '54.153.0.234' '54.193.125.205' '52.24.143.203' '34.221.171.231' '35.183.5.27' '35.182.195.196' '13.233.35.167' '13.233.83.84' '13.229.131.254' '3.0.94.77' '54.252.169.133' '13.54.28.88' '54.250.199.120' '13.112.35.80' '18.185.138.61' '52.59.249.174' '34.249.192.207' '54.229.234.118' '35.181.59.157' '35.181.43.210' '18.130.245.254' '35.178.15.57' '54.233.223.52' '18.228.190.7')
BAD=(
'18.228.190.7'
'13.229.131.254'
'3.94.170.64'
'3.0.94.77'
'100.26.104.102'
'13.233.35.167'
'3.83.10.22'
'13.233.83.84'
'3.14.64.40'
'54.252.169.133'
'3.14.67.164'
'13.54.28.88'
'35.183.5.27'
'18.185.138.61'
'54.233.223.52')
OPTIMIZED=(
'3.94.170.64'
'3.83.10.22'
'100.26.104.102'
'3.14.64.40'
'3.14.67.164'
'35.183.5.27'
'35.182.195.196'
'54.153.0.234'
'54.193.125.205'
'52.24.143.203'
'34.221.171.231'
'54.250.199.120'
'13.112.35.80'
'13.229.131.254'
'3.0.94.77')
test=('1'
'2'
'3'
'4'
'5'
'6'
'7'
'8'
'9')
# fisher-yates shuffle from bashfaqs
shuffle() {
   local i tmp size max rand

   # $RANDOM % (i+1) is biased because of the limited range of $RANDOM
   # Compensate by using a range which is a multiple of the array size.
   size=${#test[*]}
   max=$(( 32768 / size * size ))

   for ((i=size-1; i>0; i--)); do
      while (( (rand=$RANDOM) >= max )); do :; done
      rand=$(( rand % (i+1) ))
      tmp=${test[i]} test[i]=${test[rand]} test[rand]=$tmp
   done
}


for node in "${BAD[@]}"; do
  scp -i $1 -r P2PStream/RMItest/ $2@$node:
  ssh -i $1 -o StrictHostKeyChecking=no $2@$node "cd RMItest/ ; chmod +x runNode.sh"
  ssh -i $1 -o StrictHostKeyChecking=no $2@$node 'cd RMItest/ ; nohup ./runNode.sh '$node' > '$node'.log 2>&1 &'
  sleep 2s
done
