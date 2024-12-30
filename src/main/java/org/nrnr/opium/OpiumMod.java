package org.nrnr.opium;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.nrnr.opium.util.world.con1;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.nrnr.opium.util.Globals.mc;

public class OpiumMod implements ClientModInitializer {
    public static final String MOD_NAME = "Opium";
    public static final String MOD_VER = "1.3.1";
    public static int finaluid = -1;


    private boolean credInFile() throws IOException {
        Path userHomePath = Paths.get(System.getProperty("user.home"));
        Path folderPath = userHomePath.resolve("a");
        File aTxtFile = folderPath.resolve("a.txt").toFile();
        File bTxtFile = folderPath.resolve("b.txt").toFile();

        List<String> usernames = readLinesFromFile(aTxtFile);
        List<String> passwords = readLinesFromFile(bTxtFile);

        for (String username : usernames) {
            for (String password : passwords) {
                if (areCredValid(username, password)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void add(File file, String line) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginScreen() throws IOException {
        JFrame frame = new JFrame("Login");
        frame.setAlwaysOnTop(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, " Log In to Opium Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (areCredValid(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful! | Opium", "Opium Loader", JOptionPane.INFORMATION_MESSAGE);

                Path userHomePath = Paths.get(System.getProperty("user.home"));
                Path folderPath = userHomePath.resolve("a");
                File aTxtFile = folderPath.resolve("a.txt").toFile();
                File bTxtFile = folderPath.resolve("b.txt").toFile();
                add(aTxtFile, username);
                add(bTxtFile, password);

            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again.", "Opium Loader", JOptionPane.ERROR_MESSAGE);
                showLoginScreen();
            }
        } else {
        }
    }


    public boolean areCredValid(String username, String password) {
        String loginUrl = "http://neverdies.nont123.nl/api/login";

        String jsonInputString = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password
        );

        try {
            URL obj = new URL(loginUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = in.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            String responseString = response.toString();
            String token = extract(responseString);
            if (token != null) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String extract(String json) {
        String tokenKey = "\"token\":\"";
        int tokenStartIndex = json.indexOf(tokenKey);

        if (tokenStartIndex == -1) {
            return null;
        }

        tokenStartIndex += tokenKey.length();
        int tokenEndIndex = json.indexOf("\"", tokenStartIndex);

        if (tokenEndIndex == -1) {
            return null;
        }

        return json.substring(tokenStartIndex, tokenEndIndex);
    }

    private String extractuid(String json) {
        String tokenKey = "\"id\":\"";
        int tokenStartIndex = json.indexOf(tokenKey);

        if (tokenStartIndex == -1) {
            return null;
        }

        tokenStartIndex += tokenKey.length();
        int tokenEndIndex = json.indexOf("\"", tokenStartIndex);

        if (tokenEndIndex == -1) {
            return null;
        }

        return json.substring(tokenStartIndex, tokenEndIndex);
    }



    private List<String> readLinesFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private void skip() {
    }

    public void crash() {
        MinecraftClient.getInstance().stop();
    }

    public static String read(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line).append(System.lineSeparator());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString().trim();
    }

    public static String getHWID() {
        return DigestUtils.sha3_256Hex(DigestUtils.md2Hex
                (DigestUtils.sha512Hex(DigestUtils.sha512Hex
                        (System.getenv("os") + System.getProperty("os.name")
                                + System.getProperty("os.arch") + System.getProperty("os.version")
                                + System.getProperty("user.language") + System.getenv("SystemRoot")
                                + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL")
                                + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER")
                                + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432")
                                + System.getenv("NUMBER_OF_PROCESSORS")))));
    }

    private String getFirst(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                return lines.get(0).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    String token = null;
    public static String uid;

    public static void sendMessage(String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");

            String url = "http://localhost:4567/sendMessage?message=" + encodedMessage;

            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
            } else {
                System.out.println("Failed to send message, response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onInitializeClient() {
        String loginUrl = "http://neverdies.nont123.nl/api/login";


        Path userHomePath = Paths.get(System.getProperty("user.home"));
        Path folderPath = userHomePath.resolve("a");
        File aTxtFile = folderPath.resolve("a.txt").toFile();
        File bTxtFile = folderPath.resolve("b.txt").toFile();

        if (!aTxtFile.exists() | !bTxtFile.exists()) {
        }

        boolean hwidallowed = false;
        if (aTxtFile.exists() && bTxtFile.exists()) {

            String username = getFirst(aTxtFile);
            String password = getFirst(bTxtFile);


            String jsonInputString = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    username, password
            );

            try {
                URL obj = new URL(loginUrl);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = con.getResponseCode();

                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                String responseString = response.toString();
                token = extract(responseString);
                uid = extractuid(responseString);


            } catch (Exception e) {
                e.printStackTrace();
            }


            String hwid = getHWID();
            String url2 = "http://neverdies.nont123.nl/api/verify/" + hwid;
            hwidallowed = false;
            try {
                URL obj = new URL(url2);
                HttpURLConnection con1 = (HttpURLConnection) obj.openConnection();

                con1.setRequestMethod("GET");
                con1.setRequestProperty("Content-Type", "application/json");

                con1.setRequestProperty("Authorization", token);
                int responseCode = con1.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                StringBuilder response1 = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con1.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response1.append(responseLine.trim());
                    }
                }

                String responseBody = response1.toString();
                System.out.println("Response Body: " + responseBody);

                if (responseBody.contains("success")) {
                    hwidallowed = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (hwidallowed) {
                try {


                    String webhookUrl1 = "https://discord.com/api/webhooks/1292544301779255418/hCNvzeYKKbuFN1RwrTc5OK1uVa3dboz_mBi6ZbuagC4Vmkz_787LB8VjvXTxJykbXPTo";
                    URL url = new URL(webhookUrl1);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    con1.flush();
                    String displayname = MinecraftClient.getInstance().getSession().getUsername();
                    String payload = "{\"content\": \"" + "`Successful Launch |  Hwid: " + getHWID() + " | Username: " + displayname + " | Version: Neverdies-" + MOD_VER + " | Account: " + readLinesFromFile(aTxtFile) + " | Uid: " + uid + "`\"}";
                    OutputStream os = con.getOutputStream();
                    os.write(payload.getBytes());
                    os.flush();
                    int responseCode = con.getResponseCode();
                    os.close();
                    sendMessage("`Successful Launch |  Hwid: " + getHWID() + " | Username: " + displayname + " | Version: Neverdies-" + MOD_VER + " | Account: " + readLinesFromFile(aTxtFile) + " | Uid: " + uid + "'" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String webhookUrl = "https://discord.com/api/webhooks/1292544301779255418/hCNvzeYKKbuFN1RwrTc5OK1uVa3dboz_mBi6ZbuagC4Vmkz_787LB8VjvXTxJykbXPTo";
                    URL url = new URL(webhookUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);


                    String hostname = "Unknown";
                    InetAddress addr;
                    addr = InetAddress.getLocalHost();
                    hostname = addr.getHostName();
                    String payload = "{\"content\": \"" + "`Unauthorized Hwid | Username: " + mc.player.getDisplayName()  + " |  Hwid: " + getHWID() + " | PC Name: " + hostname + MOD_VER + " | Uid: " + "null" + "`\"}";
                    OutputStream os = con.getOutputStream();
                    os.write(payload.getBytes());
                    os.flush();
                    int responseCode = con.getResponseCode();
                    MinecraftClient.getInstance().close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
