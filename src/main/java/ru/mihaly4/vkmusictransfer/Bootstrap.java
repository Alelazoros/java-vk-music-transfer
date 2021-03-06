package ru.mihaly4.vkmusictransfer;

import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.mihaly4.vkmusictransfer.config.PackageConfig;
import ru.mihaly4.vkmusictransfer.config.PackageConfigFactory;
import ru.mihaly4.vkmusictransfer.di.BotModule;
import ru.mihaly4.vkmusictransfer.di.DaggerBotComponent;
import ru.mihaly4.vkmusictransfer.helper.ArgsHelper;
import ru.mihaly4.vkmusictransfer.log.ConsoleLog;
import ru.mihaly4.vkmusictransfer.log.ILog;
import org.apache.commons.cli.ParseException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

public class Bootstrap {
    private static final int EXIT_STATUS_ERROR = 1;

    private static ILog log = new ConsoleLog();

    private Bootstrap(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        PackageConfig conf = PackageConfigFactory.createFromArgs(parseArgs(args));

        Bot bot = DaggerBotComponent.builder()
                .botModule(new BotModule(conf))
                .build()
                .makeBot();

        try {
            botsApi.registerBot(bot);

            log.info("STARTED");
        } catch (TelegramApiRequestException e) {
            log.error("API REQUEST: \n-" + e.getMessage());

            System.exit(EXIT_STATUS_ERROR);
        }
    }

    public static void main(String[] args) {
        new Bootstrap(args);
    }

    private ArgsHelper parseArgs(String[] args) {
        ArgsHelper argsHelper = null;

        try {
            argsHelper = new ArgsHelper(args);
        } catch (ParseException e) {
            log.error("ERROR: \n- " + e.getMessage());

            System.exit(EXIT_STATUS_ERROR);
        }

        return argsHelper;
    }
}
