import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jibble.pircbot.PircBot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SimpleBot extends PircBot {
    private String city;
    private String weatherUrlPt1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String weatherUrlPt2 = "&APPID=26aa1d90a24c98fad4beaac70ddbf274";
    private String completeWeatherUrl;
    private String malID;
    private String jikanUrlPt1 = "https://api.jikan.moe/v3/anime/";
    private String completeJikanURL;

    public SimpleBot() {
        setName("WeatherAnimeBot");
    }

    //when "time" is sent will tell the time
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        //tokenize the message at whitespaces
        String[] messageArr = message.trim().split("\\s+");


        if (messageArr[0].equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": " + time);
        }
        else if(messageArr[0].equalsIgnoreCase("weather")) {
            //second array element will hold the city name
            city = messageArr[1];
            //create full url
            completeWeatherUrl = weatherUrlPt1 + city + weatherUrlPt2;
            sendMessage(channel, sender + ": " + webRequestWeather(completeWeatherUrl));
        }
        else if(messageArr[0].equalsIgnoreCase("exit")) {
            partChannel(channel);
            quitServer();
        }
        else if(messageArr[0].equalsIgnoreCase("animeInfo")) {
            //get malID
            malID = messageArr[1];
            //create full jikan url
            completeJikanURL = jikanUrlPt1 + malID;

            String[] aniInfoArr = webRequestJikan(completeJikanURL);

            if(aniInfoArr.length > 1) {
                sendMessage(channel, "Title: " + aniInfoArr[0]);
                sendMessage(channel, "Cover Image: " + aniInfoArr[3]);
                sendMessage(channel, "Episodes: " + aniInfoArr[1]);
                sendMessage(channel, "Score: " + aniInfoArr[2]);
            }
            else {
                sendMessage(channel, aniInfoArr[0]);
                sendMessage(channel, "MyAnimeList ID does not exist");
            }
        }



    }

    public String webRequestWeather(String completeURL) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(completeURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            //while there's a line add it to the resulting stringBuilder
            while ((line = read.readLine()) != null) {
                result.append(line);
            }
            read.close();

            return parseWeatherJson(result.toString());
        }
        catch (IOException e) {
            return "Error, Exception: " + e;
        }

    }

    public String parseWeatherJson(String json) {
        NumberFormat decFormat = new DecimalFormat("#0.00");

        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        String cityName = object.get("name").getAsString();

        JsonObject main = object.getAsJsonObject("main");

        double temp = main.get("temp").getAsDouble();
        temp = (temp - 273.15) * 1.8 + 32;

        double tempMin = main.get("temp_min").getAsDouble();
        tempMin = (tempMin - 273.15) * 1.8 + 32;

        double tempMax = main.get("temp_max").getAsDouble();
        tempMax = (tempMax - 273.15) * 1.8 + 32;

        return "The current temperature in " + cityName + " is " + decFormat.format(temp) + "°F with a high of " +
                decFormat.format(tempMax) + "°F and a low of " + decFormat.format(tempMin) + "°F.";
    }

    public String[] webRequestJikan(String completeURL) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(completeURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            //while there's a line add it to the resulting stringBuilder
            while ((line = read.readLine()) != null) {
                result.append(line);
            }
            read.close();

            return parseJikanJson(result.toString());
        }
        catch (IOException e) {
            String[] error = new String[1];
            error[0] = "Error, Exception: " + e;
            return error;
        }

    }

    public String[] parseJikanJson(String json) {

        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

        String[] animeInfo = new String[4];

        animeInfo[0] = object.get("title").getAsString();
        animeInfo[1] = object.get("episodes").getAsString();
        animeInfo[2] = object.get("score").getAsString();
        animeInfo[3] = object.get("image_url").getAsString();

        return animeInfo;
    }
}
