package org.karn.koreanchat;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.DynamicTextNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.styledchat.config.ChatStyle;
import eu.pb4.styledchat.parser.LinkParser;
import eu.pb4.styledchat.StyledChatEvents;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.karn.koreanchat.util.ChatConversion;
import org.karn.koreanchat.util.KoreanChatData;
import org.karn.koreanchat.util.KoreanChatManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public class ChatConversionRegression implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                checkChat(server);
            } finally {
                server.halt(false);
            }
        });
    }

    private static void checkChat(MinecraftServer server) {
        var parser = NodeParser.builder().simplifiedTextFormat().quickText()
                .customTagRegistry(TagRegistry.DEFAULT)
                .add(new LinkParser(DynamicTextNode.of("url", ChatStyle.DYN_KEY))).build();
        var context = ParserContext.of();
        var source = parser.parseNode("<red>dkssud</red> <blue>gktpdy</blue>");
        var converted = ChatConversion.convertChat(source, context).toComponent(context);
        assert converted.getString().equals("안녕 하세요") : converted.getString();
        var styles = new ArrayList<Style>();
        converted.visit((style, text) -> {
            if (!text.isBlank()) styles.add(style);
            return Optional.empty();
        }, Style.EMPTY);
        assert styles.get(0).getColor() != null : "red color lost";
        assert styles.get(0).getColor().getValue() == 0xFF5555 : "red color changed";
        assert styles.get(1).getColor().getValue() == 0x5555FF : "blue color lost";
        assert source.toComponent(context).getString().equals("dkssud gktpdy") : "input mutated";

        var url = "https://example.com/test?x=hello";
        var link = parser.parseNode("dkssud " + url);
        var linked = ChatConversion.convertChat(link, context).toComponent(context);
        assert linked.getString().contains("https://example.com/test?x=hello") : "URL label changed";
        var clicks = new ArrayList<ClickEvent>();
        linked.visit((style, text) -> {
            if (style.getClickEvent() != null) clicks.add(style.getClickEvent());
            return Optional.empty();
        }, Style.EMPTY);
        assert clicks.contains(new ClickEvent.OpenUrl(URI.create("https://example.com/test?x=hello"))) : "URL click lost";

        var gradient = ChatConversion.convertChat(parser.parseNode("<gradient:red:blue>dkssud</gradient>"), context).toComponent(context);
        assert gradient.getString().equals("안녕") : gradient.getString();
        var gradientColors = new ArrayList<Integer>();
        gradient.visit((style, text) -> {
            if (!text.isBlank() && style.getColor() != null) gradientColors.add(style.getColor().getValue());
            return Optional.empty();
        }, Style.EMPTY);
        assert gradientColors.stream().distinct().count() > 1 : "gradient lost";

        assert ChatConversion.convertChat("dkssud \"hello\"").equals("안녕 hello");
        assert ChatConversion.convertChat("안녕 hello").equals("안녕 hello");
        var quoted = parser.parseNode("\"<red>hello</red> world\" dkssud");
        assert ChatConversion.convertChat(quoted, context).toComponent(context).getString().equals("hello world 안녕");
        assert ChatConversion.convertChat("dkssud https://example.com").equals("안녕 https://example.com");
        assert ChatConversion.convertChat("").isEmpty();
        var mixed = parser.parseNode("<red>안녕</red> <blue>hello</blue>");
        assert ChatConversion.convertChat(mixed, context) == mixed;

        var richStyle = Style.EMPTY.withColor(0x123456).withBold(true)
                .withClickEvent(new ClickEvent.RunCommand("/example English"))
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("English tooltip")))
                .withInsertion("English insertion");
        var rich = ChatConversion.convertChat(TextNode.convert(Component.literal("dkssud").setStyle(richStyle)), context)
                .toComponent(context);
        assert rich.getString().equals("안녕");
        assert rich.getStyle().equals(richStyle) : "click, hover or insertion changed";

        var player = new ServerPlayer(server, server.overworld(),
                new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000263"), "Regression"),
                ClientInformation.createDefault());
        var playerContext = ServerPlaceholderContext.of(player);
        var data = new KoreanChatData();
        PlayerDataApi.setCustomDataFor(player, KoreanChatManager.KRC_DATA, data);
        var input = parser.parseNode("<red>dkssud</red> " + url);
        var enabled = StyledChatEvents.MESSAGE_CONTENT.invoker().onMessage(input, playerContext).toComponent(playerContext);
        assert enabled.getString().equals("안녕 " + url) : "enabled event conversion failed";
        data.krc = false;
        PlayerDataApi.setCustomDataFor(player, KoreanChatManager.KRC_DATA, data);
        assert StyledChatEvents.MESSAGE_CONTENT.invoker().onMessage(input, playerContext) == input : "disabled chat converted";
        assert StyledChatEvents.MESSAGE_CONTENT.invoker().onMessage(input, ServerPlaceholderContext.of(server)) == input;
        System.out.println("Chat regression passed: colors, links, gradient, quotes, mixed language, metadata, event registration, toggle");
    }
}
