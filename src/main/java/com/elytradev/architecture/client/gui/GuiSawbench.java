/*
 * MIT License
 *
 * Copyright (c) 2017 Benjamin K
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.architecture.client.gui;

import com.elytradev.architecture.common.shape.Shape;
import com.elytradev.architecture.common.shape.ShapePage;
import com.elytradev.architecture.common.tile.ContainerSawbench;
import com.elytradev.architecture.common.tile.TileSawbench;
import com.elytradev.architecture.legacy.base.BaseGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static org.lwjgl.opengl.GL11.*;

public class GuiSawbench extends BaseGui.Screen {

    public static int pageMenuLeft = 176;
    public static int pageMenuTop = 19;
    public static int pageMenuWidth = 58;
    public static int pageMenuRowHeight = 10;
    public static float pageMenuScale = 1;

    public static int shapeMenuLeft = 44;
    public static int shapeMenuTop = 23;
    public static int shapeMenuMargin = 4;
    public static int shapeMenuCellSize = 24;
    public static int shapeMenuRows = 4, shapeMenuCols = 5;
    public static int shapeMenuWidth = shapeMenuCols * shapeMenuCellSize;
    public static int shapeMenuHeight = shapeMenuRows * shapeMenuCellSize;
    public static int selectedShapeTitleLeft = 40;
    public static int selectedShapeTitleTop = 128;
    public static int selectedShapeTitleRight = 168;
    public static int materialUsageLeft = 7;
    public static int materialUsageTop = 82;
    public static float shapeMenuScale = 2;
    public static float shapeMenuItemScale = 2;
    public static float shapeMenuItemUSize = 40, shapeMenuItemVSize = 45;
    public static float shapeMenuItemWidth = shapeMenuItemUSize / shapeMenuItemScale;
    public static float shapeMenuItemHeight = shapeMenuItemVSize / shapeMenuItemScale;

    TileSawbench te;

    public GuiSawbench(EntityPlayer player, TileSawbench te) {
        super(new ContainerSawbench(player, te));
        this.te = te;
    }

    public static GuiSawbench create(EntityPlayer player, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileSawbench)
            return new GuiSawbench(player, (TileSawbench) te);
        else
            return null;
    }

    @Override
    protected void drawBackgroundLayer() {
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("gui/gui_sawbench.png", 256, 256);
        drawTexturedRect(0, 0, this.xSize, this.ySize, 0, 0);
        drawShapeMenu();
        drawShapeSelection();
        drawPageMenu();
        drawSelectedShapeTitle();
        fontRenderer.drawString("Sawbench", 7, 7, 4210752);
    }

    void drawPageMenu() {
        glPushMatrix();
        glTranslatef(pageMenuLeft, pageMenuTop, 0);
        gSave();
        setColor(102 / 255d, 204 / 255d, 1);
        drawRect(0, te.selectedPage * pageMenuRowHeight, pageMenuWidth, pageMenuRowHeight);
        gRestore();
        for (int i = 0; i < TileSawbench.pages.length; i++) {
            drawString(TileSawbench.pages[i].title, 1, 1);
            glTranslatef(0, pageMenuRowHeight, 0);
        }
        glPopMatrix();
    }

    void drawShapeMenu() {
        gSave();
        glPushMatrix();
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glTranslatef(shapeMenuLeft, shapeMenuTop, 0);
        bindTexture("gui/shapemenu_bg.png", 256, 256);
        double w = shapeMenuWidth + 2 * shapeMenuMargin;
        double h = shapeMenuHeight + 2 * shapeMenuMargin;
        drawTexturedRect(-shapeMenuMargin, -shapeMenuMargin, w, h,
                0, 0, shapeMenuScale * w, shapeMenuScale * h);
        bindTexture("gui/shapemenu_items.png", 512, 512);
        int p = te.selectedPage;
        if (p >= 0 && p < TileSawbench.pages.length) {
            ShapePage page = TileSawbench.pages[p];
            if (page != null) {
                Shape[] shapes = page.shapes;
                for (int i = 0; i < shapes.length; i++) {
                    Shape shape = shapes[i];
                    int mrow = i / shapeMenuCols, mcol = i % shapeMenuCols;
                    int id = shape.id;
                    int trow = id / 10, tcol = id % 10;
                    //System.out.printf("SawbenchGUI: Item %s: Rendering shape id %s from (%s, %s) at (%s, %s)\n",
                    //	i, id, trow, tcol, mrow, mcol);
                    drawTexturedRect(
                            (mcol + 0.5) * shapeMenuCellSize - 0.5 * shapeMenuItemWidth,
                            (mrow + 0.5) * shapeMenuCellSize - 0.5 * shapeMenuItemHeight,
                            shapeMenuItemWidth, shapeMenuItemHeight,
                            tcol * shapeMenuItemUSize, trow * shapeMenuItemVSize,
                            shapeMenuItemUSize, shapeMenuItemVSize);
                }
            }
        }
        glPopMatrix();
        gRestore();
    }

    void drawShapeSelection() {
        int i = te.selectedSlots[te.selectedPage];
        int row = i / shapeMenuCols;
        int col = i % shapeMenuCols;
        int x = shapeMenuLeft + shapeMenuCellSize * col;
        int y = shapeMenuTop + shapeMenuCellSize * row;
        //System.out.printf("SawbenchGui.drawShapeSelection: sel=%d x=%d y=%d\n", i, x, y);
        drawTexturedRect(x, y, 24.5, 24.5, 44, 23, 49, 49);
    }

    void drawSelectedShapeTitle() {
        Shape shape = te.getSelectedShape();
        if (shape != null) {
            int x = selectedShapeTitleLeft;
            int w = fontRenderer.getStringWidth(shape.title);
            if (x + w > selectedShapeTitleRight)
                x = selectedShapeTitleRight - w;
            drawString(shape.title, x, selectedShapeTitleTop);
            glPushMatrix();
            glTranslatef(materialUsageLeft, materialUsageTop, 0);
            glScalef(0.5f, 0.5f, 1.0f);
            drawString(String.format("%s makes %s", te.materialMultiple(), te.resultMultiple()), 0, 0);
            glPopMatrix();
        }
    }

    @Override
    protected void mousePressed(int x, int y, int btn) {
        //System.out.printf("SawbenchGui.mousePressed: %d, %d, %d\n", x, y, btn);
        if (x >= pageMenuLeft && y >= pageMenuTop && x < pageMenuLeft + pageMenuWidth)
            clickPageMenu(x - pageMenuLeft, y - pageMenuTop);
        else if (x >= shapeMenuLeft && y >= shapeMenuTop &&
                x < shapeMenuLeft + shapeMenuWidth && y < shapeMenuTop + shapeMenuHeight)
            clickShapeMenu(x - shapeMenuLeft, y - shapeMenuTop);
        else
            super.mousePressed(x, y, btn);
    }

    void clickPageMenu(int x, int y) {
        //System.out.printf("SawbenchGui.clickPageMenu: %d, %d\n", x, y);
        int i = y / pageMenuRowHeight;
        if (i >= 0 && i < TileSawbench.pages.length)
            sendSelectShape(i, te.selectedSlots[i]);
    }

    void clickShapeMenu(int x, int y) {
        //System.out.printf("SawbenchGui.clickShapeMenu: %d, %d\n", x, y);
        int row = y / shapeMenuCellSize;
        int col = x / shapeMenuCellSize;
        if (row >= 0 && row < shapeMenuRows && col >= 0 && col < shapeMenuCols) {
            int i = row * shapeMenuCols + col;
            sendSelectShape(te.selectedPage, i);
        }
    }

    protected void sendSelectShape(int page, int slot) {
        //TODO: Concrete networking.
        //ChannelOutput data = ArchitectureCraft.channel.openServerContainer("SelectShape");
        //data.writeInt(page);
        //data.writeInt(slot);
        //data.close();
    }

}
