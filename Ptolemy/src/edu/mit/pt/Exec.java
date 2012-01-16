package edu.mit.pt;

import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class Exec {
        public static void main(String[] arg) {
                try {
                        JSch jsch = new JSch();
                        
                        String user = "joshma";
                        String host = "athena.dialup.mit.edu";

                        Session session = jsch.getSession(user, host, 22);

                        /*
                         * String xhost="127.0.0.1"; int xport=0; String
                         * display=JOptionPane.showInputDialog("Enter display name",
                         * xhost+":"+xport); xhost=display.substring(0,
                         * display.indexOf(':'));
                         * xport=Integer.parseInt(display.substring(display
                         * .indexOf(':')+1)); session.setX11Host(xhost);
                         * session.setX11Port(xport+6000);
                         */

                        // username and password will be given via UserInfo interface.
                        UserInfo ui = new MyUserInfo();
                        session.setUserInfo(ui);
                        session.connect();

                        String command = "echo \"7\n1\n\n\n\n\n\nqq\n\" | listmaint";

                        Channel channel = session.openChannel("exec");
                        ((ChannelExec) channel).setCommand(command);

                        // X Forwarding
                        // channel.setXForwarding(true);

                        // channel.setInputStream(System.in);
                        channel.setInputStream(null);

                        // channel.setOutputStream(System.out);

                        // FileOutputStream fos=new FileOutputStream("/tmp/stderr");
                        // ((ChannelExec)channel).setErrStream(fos);
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
                                        System.out.println(line);
                                }
                                if (channel.isClosed()) {
                                        System.out.println("exit-status: "
                                                        + channel.getExitStatus());
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
                JTextField passwordField = (JTextField) new JPasswordField(20);
                
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
                        Object[] ob = { passwordField };
                        int result = JOptionPane.showConfirmDialog(null, ob, message,
                                        JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                                passwd = passwordField.getText();
                                return true;
                        } else {
                                return false;
                        }
                }

                public void showMessage(String message) {
                }

                public String[] promptKeyboardInteractive(String destination,
                                String name, String instruction, String[] prompt, boolean[] echo) {
                        return null;
                }
        }
}
