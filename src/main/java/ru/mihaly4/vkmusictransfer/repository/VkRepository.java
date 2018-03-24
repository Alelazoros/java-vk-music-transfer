package ru.mihaly4.vkmusictransfer.repository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mihaly4.vkmusictransfer.client.IVkClient;
import ru.mihaly4.vkmusictransfer.decoder.VkMusicLinkDecoder;
import ru.mihaly4.vkmusictransfer.log.ConsoleLog;
import ru.mihaly4.vkmusictransfer.log.ILog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VkRepository {
    private static final int STEP = 50;
    private static final long DDOS_DELAY = 1000;

    private IVkClient client;
    private VkMusicLinkDecoder linkDecoder;
    private ILog log = new ConsoleLog();

    public VkRepository(IVkClient client, VkMusicLinkDecoder linkDecoder) {
        this.client = client;
        this.linkDecoder = linkDecoder;
    }

    public CompletableFuture<Map<String, String[]>> findAllByProfile(int id) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<String, String[]> links = new HashMap<>();
            int oldLinkSize = 0;
            int page = 0;

            do {
                oldLinkSize = links.size();

                Document doc = Jsoup.parse(client.fromProfile(id, STEP * page++));

                Elements tracks = doc.select(".audio_item .ai_body");

                for (int i = 0; i != tracks.size(); i++) {
                    Element track = tracks.get(i);

                    String author = track.selectFirst(".ai_artist").text();
                    String title = track.selectFirst(".ai_title").text();

                    String encodedLink = track.selectFirst("input").val();
                    String link = linkDecoder.decode(encodedLink, client.getUid());

                    links.put(link, new String[]{author, title});
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(DDOS_DELAY);
                } catch (InterruptedException e) {
                    log.error("DDOS DELAY: " + e.getMessage());
                }
            } while (oldLinkSize != links.size());

            return links;
        });
    }
}
