package ru.mihaly4.vkmusictransfer.command;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import ru.mihaly4.vkmusictransfer.repository.VkRepository;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProfileCommand extends AbstractGrabCommand {
    private VkRepository vkRepository;

    public ProfileCommand(DefaultAbsSender absSender, VkRepository vkRepository) {
        super(absSender);

        this.vkRepository = vkRepository;
    }

    @Override
    protected CompletableFuture<Map<String, String[]>> findAll(String id) {
        return vkRepository.findAllByProfile(Integer.valueOf(id));
    }

    @Override
    protected String getValidateMessage() {
        return "You must enter the profile ID to grab music";
    }
}
