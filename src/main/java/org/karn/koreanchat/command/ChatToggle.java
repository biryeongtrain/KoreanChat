package org.karn.koreanchat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static net.minecraft.commands.Commands.*;
import static org.karn.koreanchat.util.KoreanChatManager.setKoreanChat;


public class ChatToggle {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("krc")
                .then(Commands.literal("on")
                    .executes(ctx -> {
                        setKoreanChat(ctx.getSource().getPlayerOrException(),true);
                        return 1;
                    })
                )
                .then(Commands.literal("off")
                        .executes(ctx -> {
                            setKoreanChat(ctx.getSource().getPlayerOrException(),false);
                            return 1;
                        })
                )
        );
    }
}
