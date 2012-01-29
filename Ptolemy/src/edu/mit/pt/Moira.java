package edu.mit.pt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class Moira {
	public static List<String> getClasses(String username, String password,
			String semester) throws JSchException, IOException {

		Pattern p = Pattern.compile("(" + semester + "-.+)-reg");

		JSch jsch = new JSch();
		String user = username;
		String host = "athena.dialup.mit.edu";
		Session session = jsch.getSession(user, host, 22);

		UserInfo ui = new MyUserInfo(password);
		session.setUserInfo(ui);
		session.connect();

		String command = "echo \"7\n1\n\n\n\n\n\nqq\n\" | listmaint";

		// Set up the channel
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(null);

		BufferedReader bufferedIn = new BufferedReader(new InputStreamReader(
				channel.getInputStream()));
		channel.connect();

		// Read buffer; add classes matching semester to list.
		List<String> classes = new ArrayList<String>();
		String line;
		while ((line = bufferedIn.readLine()) != null) {
			Matcher m = p.matcher(line);
			if (m.matches())
				classes.add(m.group(1));
		}
		// Disconnect and return.
		channel.disconnect();
		session.disconnect();
		return classes;
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		String passwd;

		public MyUserInfo(String password) {
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

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			return null;
		}
	}

}
