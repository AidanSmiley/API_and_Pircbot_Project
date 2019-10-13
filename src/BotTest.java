public class BotTest {
    public static void main(String[] args) {
        SimpleBot weatherBot = new SimpleBot();
        weatherBot.setVerbose(true);
        try {
            weatherBot.connect("irc.freenode.net");
        }
        catch (Exception e) {
            System.out.println("Can't connect: " + e);
            return;
        }

        weatherBot.joinChannel("#weatherBotTest");
        weatherBot.sendMessage("#weatherBotTest", "Type in \"time\" to get the time, \"weather\" and a city " +
                "to get the current weather in that city. Type animeInfo and a MyAnimeList ID to get info on a show.");
    }
}
