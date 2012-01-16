package edu.mit.pt;

import java.io.InputStream;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class Moira {
        public static List<String> getClasses(String username, String password) {
                try {
                        JSch jsch = new JSch();
                        String user = username;
                        String host = "athena.dialup.mit.edu";
                        Session session = jsch.getSession(user, host, 22);
                        
                        UserInfo ui = new MyUserInfo(password);
                        session.setUserInfo(ui);
                        session.connect();

                        String command = "echo \"7\n1\n\n\n\n\n\nqq\n\" | listmaint";

                        Channel channel = session.openChannel("exec");
                        ((ChannelExec) channel).setCommand(command);
                        channel.setInputStream(null);
                        ((ChannelExec) channel).setErrStream(System.err);
                        InputStream in = channel.getInputStream();
                        channel.connect();
                        byte[] tmp = new byte[1024];
                        while (true) {
                                while (in.available() > 0) {
                                        int i = in.read(tmp, 0, 1024);
                                        if (i < 0)
                                                break;
                                        String line = new String(tmp, 0, i);
                                        Log.v("blah",line);
                                }
                                if (channel.isClosed()) {
                                        System.out.println("exit-status: " + channel.getExitStatus());
                                        break;
                                }
                                try {
                                        Thread.sleep(1000);
                                } catch (Exception ee) {
                                }
                        }
                        channel.disconnect();
                        session.disconnect();
                } catch (Exception e) {
                        System.out.println(e);
                }
        }

        public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
                String passwd;
                public MyUserInfo(String password){
                	passwd = password;
                }
                public String getPassword() {
                        return passwd;
                }

                public boolean promptYesNo(String str) {
                        return true;
                }

                public String getPassphrase() {
                        return null;
                }

                public boolean promptPassphrase(String message) {
                        return true;
                }

                public boolean promptPassword(String message) {
                        return true;
                }

                public void showMessage(String message) {
                }

                public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
                        return null;
                }
        }

        
}
