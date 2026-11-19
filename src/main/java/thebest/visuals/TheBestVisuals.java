package thebest.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TheBestVisuals implements ClientModInitializer {
    public static final String MOD_ID = "thebestvisuals";

    private static final int ACCENT = 0xFF7C5CFF;   // purple accent
    private static final int BG     = 0x99101018;   // translucent dark panel
    private static final int TEXT   = 0xFFFFFFFF;
    private static final int DIM    = 0xFFB8B8C8;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of(MOD_ID, "hud"),
                (context, tickCounter) -> render(context)
        );
    }

    private void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) return;

        TextRenderer tr = mc.textRenderer;

        // ---- Watermark (top-left) ----
        String title = "TheBest Visuals";
        int w = tr.getWidth(title) + 12;
        ctx.fill(4, 4, 4 + w, 18, BG);
        ctx.fill(4, 4, 4 + w, 6, ACCENT);
        ctx.drawText(tr, title, 10, 8, TEXT, true);

        // ---- Info panel (under watermark) ----
        List<String> lines = new ArrayList<>();
        lines.add("FPS: " + mc.getCurrentFps());
        lines.add("Ping: " + getPing(mc) + " ms");
        lines.add(String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        lines.add("Dir: " + mc.player.getHorizontalFacing().asString().toUpperCase());
        if (mc.world != null) {
            long t = mc.world.getTimeOfDay() % 24000L;
            int hours = (int) ((t / 1000 + 6) % 24);
            int mins = (int) ((t % 1000) * 60 / 1000);
            lines.add(String.format("Time: %02d:%02d", hours, mins));
        }

        int maxW = 0;
        for (String s : lines) maxW = Math.max(maxW, tr.getWidth(s));
        int boxH = lines.size() * 11 + 6;
        int y0 = 22;
        ctx.fill(4, y0, 4 + maxW + 12, y0 + boxH, BG);
        ctx.fill(4, y0, 6, y0 + boxH, ACCENT);
        int y = y0 + 4;
        for (String s : lines) {
            ctx.drawText(tr, s, 10, y, DIM, true);
            y += 11;
        }
    }

    private int getPing(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null || mc.player == null) return 0;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e == null ? 0 : e.getLatency();
    }
}
