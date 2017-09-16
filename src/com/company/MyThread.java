package com.company;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public class MyThread extends Thread{
    private Socket socket;
    private Server server;
    private InputStreamReader reader;
    char[] chars = new char[64];
    int len;
    private String temp = null;

    public MyThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        init();
    }

    private void init() {
        try {
            reader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("子线程开始工作");
        while(true){
            try {
                System.out.println("线程"+this.getId()+":开始从客户端读取数据——>");
                while ((len = ((Reader) reader).read(chars)) != -1) {
                    temp = new String(chars, 0, len);
                    System.out.println("来自客户端"+socket.getPort()+"的消息:" +temp);
                    server.apppendMsg("来自客户端"+socket.getPort()+"的消息:" +temp);
                    server.sendMsgToAll(this.socket, "客户端"+socket.getPort()+"的说:" +temp);
                }
                if(!socket.getKeepAlive()){
                    ((Reader) reader).close();
//                  temp = "线程"+this.getId()+"——>关闭";
//                  System.out.println(temp);
                    temp = "客户端"+socket.getPort()+":退出";
                    server.apppendMsg(temp);
                    socket.close();
                    this.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    ((Reader) reader).close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
