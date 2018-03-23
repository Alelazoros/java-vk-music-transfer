package ru.mihaly4.vkmusictransfer.repository;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import ru.mihaly4.vkmusictransfer.client.VkClientInterface;
import ru.mihaly4.vkmusictransfer.decoder.VkMusicLinkDecoder;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class VkRepositoryTest {
    @Test
    public void getFromProfile() throws InterruptedException {
        VkClient client = new VkClient();
        VkRepository repository = new VkRepository(client, new VkMusicLinkDecoder());
        final Map<String, String> links = new HashMap<>();

        final CountDownLatch signal = new CountDownLatch(1);

        repository.findAllByProfile(123456789).thenAccept(action -> {
            links.putAll(action);

            signal.countDown();
        });

        signal.await();

        assertEquals(48, links.size());
    }

    private static class VkClient implements VkClientInterface {
        @Override
        public CompletableFuture<String> fromProfile(int id, int offset) {
            return CompletableFuture.supplyAsync(() -> {
                if (offset != 0) {
                    return "";
                }

                URL fixture = getClass().getResource("/fixture/profile.html");

                try {
                    return Resources.toString(fixture, Charsets.UTF_8);
                } catch (IOException e) {
                    return "";
                }
            });
        }

        @Override
        public String getRemixSid() {
            return "";
        }

        @Override
        public int getUid() {
            return 444529088; // need for to decode
        }
    }
}