package org.karn.koreanchat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.karn.koreanchat.util.ChatConversion;
import org.karn.koreanchat.util.KoreanChatData;
import org.karn.koreanchat.util.KoreanChatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StyledChatUtils.class)
public class StyledChatUtilsMixin {
    @WrapOperation(method = "formatMessage", at = @At(value = "INVOKE", target = "Leu/pb4/styledchat/StyledChatStyles;getChat(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"))
    private static Text modifyMessage(ServerPlayerEntity player, Text message, Operation<Text> original) {
        if (player != null) {
            KoreanChatData data = PlayerDataApi.getCustomDataFor(player, KoreanChatManager.KRC_DATA);
            if (data != null && data.krc) {
//                if (message instanceof )
                message = Text.literal(ChatConversion.convertChat( message.getString())).setStyle(message.getStyle());
            }
        }
        return original.call(player, message);
    }
}
