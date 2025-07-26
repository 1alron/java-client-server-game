package com.example.lab1;

import com.example.lab1.model.BuildModel;
import com.example.lab1.model.Model;
import com.example.lab1.model.Target;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ShooterServer {
    public final int port = 3124;
    // sockets
    private ServerSocket serverSocket;
    private Socket socket;

    private ArrayList<ClientConnection> clientConnections = new ArrayList<>();

    private Model model = BuildModel.build();

    public ShooterServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("The server is running on port " + port);

            // adding targets to model
            model.addTarget(new Target(GameUtil.LARGER_TARGET_START_X_POSITION,
                    GameUtil.TARGETS_START_Y_POSITION, GameUtil.LARGER_TARGET_RADIUS));
            model.addTarget(new Target(GameUtil.SMALLER_TARGET_START_X_POSITION,
                    GameUtil.TARGETS_START_Y_POSITION, GameUtil.SMALLER_TARGET_RADIUS));

            while (true) {
                socket = serverSocket.accept();
                int port = socket.getPort();
                System.out.println("Connected on (" + port + ")");

                ClientConnection clientConnection = new ClientConnection(socket, true);

                if (clientConnections.size() < GameUtil.MAX_NUMBER_OF_PLAYERS) {
                    clientConnections.add(clientConnection);

                    // broadcasting
                    model.addObserver(
                            () -> clientConnections.forEach(clConn -> clConn.sendServerResponse(
                                    new ServerResponse(ServerActionType.REFRESH_UI, model.getGameState())))
                    );

                } else {
                    clientConnection.sendServerResponse(new ServerResponse(
                            ServerActionType.REJECT_ADD_PLAYER_AREA_IS_FULL, model.getGameState()));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ShooterServer();
    }
}
