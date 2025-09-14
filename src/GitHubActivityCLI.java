import java.net.*;
import java.io.*;
import java.util.Scanner;

public class GitHubActivityCLI {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);


        System.out.print("Enter the GitHub username to check: ");
        String username = input.nextLine();

        try {
            String apiUrl = "https://api.github.com/users/" + username + "/events";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();


            if (!(responseCode == 200)) {
                System.out.println("Warning: HTTP code " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String json = response.toString();

            System.out.println("\nRecent activity for " + username + ":");

            String[] events = json.split("\\},\\{");


            for (String event : events) {
                String type = "";
                String repo = "";

                int typeIndex = event.indexOf("\"type\":\"");
                if (typeIndex != -1) {
                    int start = typeIndex + 8;
                    int end = event.indexOf("\"", start);
                    type = event.substring(start, end);
                }

                int repoIndex = event.indexOf("\"name\":\"");
                if (repoIndex != -1) {
                    int start = repoIndex + 8;
                    int end = event.indexOf("\"", start);
                    repo = event.substring(start, end);
                }

                switch (type) {
                    case "IssuesEvent":
                        System.out.println("- Opened an issue in " + repo);
                        break;
                    case "WatchEvent":
                        System.out.println("- Starred " + repo);
                        break;
                    default:
                        System.out.println("- Pushed commits to " + repo);
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("Oops! Something went wrong: " + e.getMessage());
        }


        input.close();
    }
}
