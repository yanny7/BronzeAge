package com.yanny.age.bronze.blocks;

import com.yanny.age.bronze.Reference;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static com.yanny.age.bronze.blocks.BoostedFurnaceTileEntity.*;

public class BoostedFurnaceGui extends ContainerScreen<BoostedFurnaceContainer> {
    private static final ResourceLocation GUI = new ResourceLocation(Reference.MODID, "textures/gui/container/boosted_furnace.png");

    private BoostedFurnaceContainer container;

    public BoostedFurnaceGui(BoostedFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (minecraft == null) {
            return;
        }

        minecraft.getTextureManager().bindTexture(GUI);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (container.getBurnTimeLeft() > 0) {
            int k = Math.round((container.getBurnTimeLeft() / (float) container.getBurnTimeTotal()) * 14);
            this.blit(guiLeft + 26, guiTop + 36 + 14 - k, 176, 14 - k, 14, k + 1);
        }

        if (container.getTemperature() > 0) {
            int k = Math.round((container.getTemperature() / (float) (MAX_NO_CHIMNEY_TEMPERATURE + MAX_TEMPERATURE_PER_CHIMNEY * MAX_CHIMNEY_COUNT)) * 56);
            this.blit(guiLeft + 59, guiTop + 15 + 56 - k, 190, 56 - k, 21, k + 1);
        }

        if (container.getProcessLeft() <= MELTING_TICKS) {
            int k = 24 - Math.round((container.getProcessLeft() / (float) MELTING_TICKS) * 24);
            this.blit(guiLeft + 93, guiTop + 35, 211, 0, k + 1, 17);
        }

        drawString(font, container.getTemperature() + " °C", guiLeft + 86,guiTop + 56, -1);

        if (container.getMeltingPoint() < 2000) {
            drawString(font, "> " + container.getMeltingPoint() + " °C", guiLeft + 82, guiTop + 18, -1);
        }
    }
}
