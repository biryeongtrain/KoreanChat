package org.karn.koreanchat;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.styledchat.StyledChatEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.level.ServerPlayer;
import org.karn.koreanchat.command.ChatToggle;
import org.karn.koreanchat.util.ChatConversion;
import org.karn.koreanchat.util.KoreanChatManager;

public class KoreanChat implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, ignored1) -> {
            ChatToggle.register(dispatcher);
        });
        StyledChatEvents.MESSAGE_CONTENT.register((message, context) -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return message;
            }
            var data = PlayerDataApi.getCustomDataFor(player, KoreanChatManager.KRC_DATA);
            return data != null && data.krc ? ChatConversion.convertChat(message, context.asParserContext()) : message;
        });
    }

}
