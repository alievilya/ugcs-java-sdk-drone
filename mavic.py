import socket
import time
import os
import subprocess

# value = input('value\n')
# command = "yaw,0.5"


# os.chdir("X:/code/rabota/ugcs-java-sdk-master/target/ugcs-java-sdk")
# subprocess.call(
#         'java -cp .;* com.ugcs.ucs.client.samples.SendCommand -c joystick "EMU-101"',
#         shell=True)
# print(os.listdir())
HOST = "localhost"
PORT = 8080


def sendcommand():
    global HOST, PORT, command
    final_command = "exit"
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((HOST, PORT))
    while True:
        print("1 - pitch,\n2 - roll,\n3 - yaw,\n4 - throttle,\n5 - takeoff,\n6 - land,\n7 - joystick, \n8 - manual\n0 - exit,\nq - break")
        val = input("enter command:\n")
        if val == "1":
            command = "direct_vehicle_control:pitch,0.1"
        elif val == "2":
            command = "direct_vehicle_control:roll,0.1"
        elif val == "3":
            command = "direct_vehicle_control:yaw,0.5"
        elif val == "4":
            command = "direct_vehicle_control:throttle,0.1"
        elif val == "-1":
            command = "direct_vehicle_control:pitch,-0.1"
        elif val == "-2":
            command = "direct_vehicle_control:roll,-0.1"
        elif val == "-3":
            command = "direct_vehicle_control:yaw,-0.5"
        elif val == "-4":
            command = "direct_vehicle_control:throttle,-0.1"
        elif val == "5":
            command = "takeoff_command"
        elif val == "6":
            command = "land_command"
        elif val == "7":
            command = "joystick"
        elif val == "8":
            command = "manual"
        elif val == "0":
            command = "exit"

        elif val == "c0":
            command = "direct_payload_control:pitch,0.6"
        elif val == "q":
            break

        # sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # sock.connect((HOST, PORT))

        sock.sendall(("{}\n".format(command)).encode('utf-8'))
        # else:
        #     sock.sendall(("{}\n".format(final_command)).encode('utf-8'))
        data = sock.recv(1024)
        print(data)
        time.sleep(1)
        if data == b'profit\r\n':
            print('ok')
        elif data == b'exiting\r\n':
            print('exited')
        else:
            print('lol')
        # sock.close()


sendcommand()



# java -cp .;* com.ugcs.ucs.client.samples.SendCommand "EMU-101"
# java -cp .;* com.ugcs.ucs.client.samples.SendCommand "Mavic2EnterpriseDual-298cgbkr0a0a48"


# sock.sendall(("yaw,-0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,-0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,-0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,0.3\n").encode('utf-8'))
# time.sleep(3)
# sock.sendall(("yaw,-0.3\n").encode('utf-8'))

# data = sock.recv(1024)
# print(data)
# if data == b'profit\r\n':
#     print('ok')
# else:
#     print('lol')
# sock.close()
