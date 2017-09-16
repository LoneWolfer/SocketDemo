package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server extends JFrame implements ActionListener{

    private Map<Integer, Socket> clients = new HashMap<>();
    private JTextArea msg = new JTextArea("服务器消息接收器\r\n");
    private JButton MsgAllSend = new JButton("发送群消息");

    //构造方法
    public Server() {
        this.setVisible(true);
        this.setSize(500, 650);
        this.setLayout(new FlowLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

        msg.setAutoscrolls(true);
        msg.setColumns(40);
        msg.setRows(30);
        MsgAllSend.addActionListener(this);
        MsgAllSend.setActionCommand("sendMsg");

        JScrollPane spanel = new JScrollPane(msg);
        this.add(spanel);
        this.add(MsgAllSend);
    }

    public void listenClient() {
        int port = 8899;
        String temp = "";
        // 定义一个ServerSocket监听在端口8899上
        try {
            ServerSocket server = new ServerSocket(port);
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            while (true) {
                System.out.println("服务器端正在监听");
                Socket socket = server.accept();
                clients.put(socket.getPort(), socket);
                temp = "客户端"+socket.getPort()+":连接";
                System.out.println(socket.getRemoteSocketAddress());
                this.apppendMsg(temp);
                new MyThread(socket, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void apppendMsg(String msg){
        this.msg.append(msg+"\r\n");
    }

    public void sendMsgToAll(Socket fromSocket, String msg) {
        Set<Integer> keySet = this.clients.keySet();
        java.util.Iterator<Integer> iter = keySet.iterator();
        while(iter.hasNext()){
            int key = iter.next();
            Socket socket = clients.get(key);
            if(socket != fromSocket){
                try {
                    if(!socket.isClosed()){
                        if(!socket.isOutputShutdown()){
                            Writer writer = new OutputStreamWriter(
                                    socket.getOutputStream());
                            writer.write(msg);
                            writer.flush();
                        }
                    }
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String temp = "";
        if("sendMsg".equals(e.getActionCommand())){
            System.out.println("开始向客户端群发消息");
            Set<Integer> keset = this.clients.keySet();
            java.util.Iterator<Integer> iter = keset.iterator();
            while(iter.hasNext()){
                int key = iter.next();
                Socket socket = clients.get(key);
                try {
                    if(!socket.isClosed()){
                        if(!socket.isOutputShutdown()){
                            temp = "向客户端"+socket.getPort()+"发送消息";
                            System.out.println(temp);
                            Writer writer = new OutputStreamWriter(
                                    socket.getOutputStream());
                            this.apppendMsg(temp);
                            writer.write("来自服务器的问候");
                            writer.flush();
                        }
                    }
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
