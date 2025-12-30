package com.jackyblackson.test.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

public class CubicView extends ClickableWidget {
    private final BlockState[][][] blockArray;
    private float rotationX = 0;
    private float rotationY = 0;
    private float scale = 30.0f; // 缩放大小

    public CubicView(BlockState[][][] blockArray,
            int x,
            int y,
            int width,
            int height,
            net.minecraft.text.Text message
    ) {
        super(x, y, width, height, message);
        this.blockArray = blockArray;
    }

    public CubicView(
            int x,
            int y,
            int width,
            int height,
            net.minecraft.text.Text message
    ) {
        super(x, y, width, height, message);
        this.blockArray = new BlockState[5][5][5];
        Random r = new Random();
        for (int xx = 0; xx < 5; xx++) {
            for (int yy = 0; yy < 5; yy++) {
                for (int zz = 0; zz < 5; zz++) {
                    int c = r.nextInt(5);
                    switch (c) {
                        case 0:
                            this.blockArray[xx][yy][zz] = Blocks.STONE.getDefaultState(); // 这里用石头作为示例
                            break;
                        case 1:
                            this.blockArray[xx][yy][zz] = Blocks.ACACIA_STAIRS.getDefaultState(); // 这里用石头作为示例
                            break;
                        case 2:
                            this.blockArray[xx][yy][zz] = Blocks.NETHER_BRICK_WALL.getDefaultState(); // 这里用石头作为示例
                            break;
                        case 3:
                            this.blockArray[xx][yy][zz] = Blocks.GLASS.getDefaultState(); // 这里用石头作为示例
                            break;
                        case 4:
                            this.blockArray[xx][yy][zz] = Blocks.BELL.getDefaultState(); // 这里用石头作为示例
                            break;
                    }
                }
            }
        }
    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        render3DBlockArray(context.getMatrices());
    }

    private void render3DBlockArray(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();

        // Calculate the center position
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Calculate the total size of the block array
        int totalWidth = blockArray.length;
        int totalHeight = blockArray[0].length;
        int totalDepth = blockArray[0][0].length;

        // Calculate the offset to center the block array
        float offsetX = (totalWidth - 1) / scale;
        float offsetY = (totalHeight - 1) / scale;
        float offsetZ = (totalDepth - 1) / scale;

        // Save the current OpenGL state
        RenderSystem.enableDepthTest(); // Enable depth testing
        RenderSystem.enableCull();      // Enable face culling

        matrices.push();

        // Translate to the center of the screen
        matrices.translate(centerX, centerY, 100);

        // Apply scaling
        matrices.scale(scale, scale, scale);

        // Apply rotation
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));

        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

        // Render each block
        for (int x = 0; x < blockArray.length; x++) {
            for (int y = 0; y < blockArray[x].length; y++) {
                for (int z = 0; z < blockArray[x][y].length; z++) {
                    BlockState blockState = blockArray[x][y][z];
                    if (blockState != null) {
                        matrices.push();
                        // Translate to the position of the current block
                        matrices.translate(x + offsetX, y + offsetY, z + offsetZ);

                        blockRenderManager.renderBlockAsEntity(blockState, matrices, vertexConsumers, 15728880, 655360);
                        matrices.pop();
                    }
                }
            }
        }

        vertexConsumers.draw();

        matrices.pop();

        // Restore the OpenGL state
        RenderSystem.disableCull(); // Disable face culling
        RenderSystem.disableDepthTest(); // Disable depth testing
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        rotationX += (float) (deltaY * 0.5);
        rotationY += (float) (deltaX * 0.5);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scale += (float) (verticalAmount * 2.0f + horizontalAmount * 2.0f);
        scale = Math.max(10.0f, Math.min(100.0f, scale)); // 限制缩放范围
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
