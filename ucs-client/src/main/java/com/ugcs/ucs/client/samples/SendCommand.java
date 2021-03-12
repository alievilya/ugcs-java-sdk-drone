package com.ugcs.ucs.client.samples;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.net.Socket;


import com.ugcs.ucs.client.Client;
import com.ugcs.ucs.client.ClientSession;
import com.ugcs.ucs.proto.DomainProto.Command;
import com.ugcs.ucs.proto.DomainProto.CommandArgument;
import com.ugcs.ucs.proto.DomainProto.Subsystem;
import com.ugcs.ucs.proto.DomainProto.Value;
import com.ugcs.ucs.proto.DomainProto.Vehicle;

public final class SendCommand {

    private SendCommand() {
    }

    public static void main(String[] args) {
        String vehicleName = null;
        String commandCode = null;
        Map<String, Double> commandArguments = new HashMap<>();

        boolean usage = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-c")) {
                if (i + 1 == args.length) {
                    usage = true;
                    break;
                }
                commandCode = args[++i];
                continue;
            }
            if (args[i].equals("-a")) {
                if (i + 1 == args.length) {
                    usage = true;
                    break;
                }
                String[] tokens = args[++i].split("=");
                if (tokens.length < 2) {
                    usage = true;
                    break;
                }
                commandArguments.put(tokens[0], Double.parseDouble(tokens[1]));
                continue;
            }
            vehicleName = args[i];
            break;
        }
        if (vehicleName == null)
            usage = true;

        if (usage) {
            System.err.println("SendCommand -c commandCode [-a commandArgument=value]* vehicleName");
            System.err.println("");
            System.err.println("\tList of supported command codes:");
            System.err.println("");
            System.err.println("\t  * arm");
            System.err.println("\t  * disarm");
            System.err.println("\t  * auto");
            System.err.println("\t  * manual");
            System.err.println("\t  * guided");
            System.err.println("\t  * joystick");
            System.err.println("\t  * takeoff_command");
            System.err.println("\t  * land_command");
            System.err.println("\t  * emergency_land");
            System.err.println("\t  * return_to_home");
            System.err.println("\t  * mission_pause");
            System.err.println("\t  * mission_resume");
            System.err.println("\t  * waypoint (latitude, longitude, altitude_amsl/altitude_agl, altitude_origin,");
            System.err.println("\t              ground_speed, vertical_speed, acceptance_radius, heading)");
            System.err.println("\t  * direct_vehicle_control (pitch, roll, yaw, trottle)");
            System.err.println("");
            System.err.println("\tFor more details on the supported commands and its arguments and expected");
            System.err.println("\tvehicle behavior see UgCS User Manual (\"Direct Vehicle Control\" section).");
            System.err.println("\tAlso note that this tool support a limited subset of the vehicles commands:");
            System.err.println("\tcamera and ADS-B commands are not supported, but can be easily implemented");
            System.err.println("\tby modifying a sample source.");
            System.err.println("");
            System.err.println("Examples:");
            System.err.println("");
            System.err.println("\tSendCommand -c arm \"EMU-101\"");
            System.err.println("\tSendCommand -c guided \"EMU-101\"");
            System.err.println("\tSendCommand -c waypoint -a latitude=0.99442 -a longitude=0.42015 "
                    + "-a altitude_agl=100.0 -a ground_speed=5.0 -a vertical_speed=1.0 \"EMU-101\"");
            System.exit(1);
        } else {
            try {
                sendCommand(vehicleName, commandCode, commandArguments);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }

    public static void sendCommand(String vehicleName, String commandCode, Map<String, Double> commandArguments)
            throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream in = classLoader.getResourceAsStream("client.properties")) {
            properties.load(in);
        }
        InetSocketAddress serverAddress = new InetSocketAddress(
                properties.getProperty("server.host", "localhost"),
                Integer.parseInt(properties.getProperty("server.port", "3334")));

//        boolean session_initialized = false;

//            session_initialized = true;
        try (Client client = new Client(serverAddress)) {
            ClientSession session = new ClientSession(client);


            client.connect();

            // Authorize client & login user.
            session.authorizeHci();
            session.login(
                    properties.getProperty("user.login"),
                    properties.getProperty("user.password"));

            // Find a vehicle with the specified name.
            Vehicle vehicle = session.lookupVehicle(vehicleName);
            if (vehicle == null)
                throw new IllegalStateException("Vehicle not found: " + vehicleName);

            // Construct command object.
            Command command_j = buildCommand("joystick", commandArguments);
            Command Controlcommand = buildCommand("joystick", commandArguments); // Подумать как нормально записать
            System.out.println("commands");
            String fromClient;
            String toClient;

            ServerSocket server = new ServerSocket(8080);

//			session.sendCommand(vehicle, command_takeof);
//			Thread.sleep(3500);
            boolean flag1 = true;
            boolean flag2 = true;
            while (flag1) {
                Socket clientSock = server.accept();
                System.out.println("got connection on port 8080");
//                session.gainVehicleControl(vehicle);
//                session.sendCommand(vehicle, command_j);

                while (flag2) {
//                    session.releaseVehicleControl(vehicle);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);

                    fromClient = in.readLine();
                    System.out.println("received: " + fromClient);
                    Map<String, Double> com_arg2 = new HashMap<String, Double>();
                    if (fromClient != null) {  // ДОБАВИТЬ JSON!!!!!!!!!!!!!
                        if (fromClient.equals("exit")) {
                            toClient = "exiting";
                            System.out.println("send exiting");
                            out.println(toClient);
                            session.releaseVehicleControl(vehicle);
                            flag1 = false;
                            flag2 = false;
                            break;
                        }
//                        session.gainVehicleControl(vehicle);
                        String[] str = fromClient.split(":");
                        switch (str[0]) {
                            case "takeoff_command": {
                                Controlcommand = buildCommand("takeoff_command", commandArguments);
                                break;
                            }
                            case "direct_vehicle_control": {
                                String[] com = str[1].split(",");
                                com_arg2.put(com[0], new Double(com[1]));
                                Controlcommand = buildCommand("direct_vehicle_control", com_arg2);
                                break;
                            }
                            case "land_command": {
                                Controlcommand = buildCommand("land_command", com_arg2);
                                break;
                            }
                            case "joystick": {
                                Controlcommand = buildCommand("joystick", com_arg2);
                                break;
                            }
                        }

                        try {
                            System.out.println("Controlcommand: ");
                            System.out.println(Controlcommand);
                            session.gainVehicleControl(vehicle);
                            session.sendCommand(vehicle, Controlcommand);
                        }catch (Exception e ) {
                            System.out.println("error e:");
                            System.out.println(e.getMessage());
                        }
                        toClient = "profit";
                        System.out.println("send profit");
                        out.println(toClient);
//                        client.close();
//                        server.close();

                        session.releaseVehicleControl(vehicle);
                    }

                }
                System.out.println("breaked from 1st loop");
                break;
                // Write exceptions!!!!!!!!!!!!!!!!!!!!!!!!!!!

            }

        }


    }

    private static Command buildCommand(String code, Map<String, Double> arguments) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(arguments);

        Command.Builder builder = Command.newBuilder()
                .setCode(code)
                .setSubsystem(Subsystem.S_FLIGHT_CONTROLLER)
                .setSubsystemId(0);
        for (Map.Entry<String, Double> entry : arguments.entrySet()) {
            builder.addArguments(CommandArgument.newBuilder()
                    .setCode(entry.getKey())
                    .setValue(Value.newBuilder()
                            .setDoubleValue(entry.getValue())));
        }
        return builder.build();
    }
}