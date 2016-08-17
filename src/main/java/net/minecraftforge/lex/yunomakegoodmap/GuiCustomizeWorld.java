package net.minecraftforge.lex.yunomakegoodmap;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiCustomizeWorld extends GuiScreen
{
    private GuiCreateWorld createGUI;
    private ScrollList list;

    public GuiCustomizeWorld(GuiCreateWorld createGUI)
    {
        this.createGUI = createGUI;
    }

    @Override
    public void initGui()
    {
        this.list = new ScrollList();
        collectPlatforms();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            this.mc.displayGuiScreen(this.createGUI);
        }
        else if (button.id == 0)
        {
            this.createGUI.chunkProviderSettingsJson = this.list.list.get(this.list.selected).toString();
            this.mc.displayGuiScreen(this.createGUI);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();
        this.list.handleMouseInput(mouseX, mouseY);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        //this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Select Platform", this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void collectPlatforms()
    {
        List<ResourceLocation> platforms = this.list.list;
        for (String domain : this.mc.getResourceManager().getResourceDomains())
        {
            try
            {
                for (IResource res : this.mc.getResourceManager().getAllResources(new ResourceLocation(domain, "/structures/SkyBlockPlatforms.txt")))
                {
                    for (String name : CharStreams.readLines(new InputStreamReader(res.getInputStream())))
                    {
                        try
                        {
                            if (this.mc.getResourceManager().getResource(new ResourceLocation(domain, "/structures/" + name + ".nbt")) != null)
                            {
                                platforms.add(new ResourceLocation(domain, name));
                            }
                        }
                        catch (IOException e)
                        {
                            //Ugh wish it had a 'hasResource'
                        }
                    }
                }
            }
            catch (IOException e)
            {
                // nom nom nom
            }
        }

        for (File f : YUNoMakeGoodMap.instance.getStructFolder().listFiles(new FilenameFilter()
                        {
                            @Override
                            public boolean accept(File dir, String name)
                            {
                                return name.endsWith(".nbt");
                            }
                        }))
        {
            platforms.add(new ResourceLocation("/config/", f.getName().substring(0, f.getName().length() - 4)));
        }
    }

    private class ScrollList extends GuiScrollingList
    {
        private List<ResourceLocation> list = Lists.newArrayList();
        private int selected = 0;

        public ScrollList()
        {
            super(GuiCustomizeWorld.this.mc, GuiCustomizeWorld.this.width - 20, GuiCustomizeWorld.this.height,
                    32, GuiCustomizeWorld.this.height - 68 + 4, 10, GuiCustomizeWorld.this.fontRendererObj.FONT_HEIGHT + 6,
                    GuiCustomizeWorld.this.width, GuiCustomizeWorld.this.height);
        }

        @Override
        protected int getSize()
        {
            return list.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {
            this.selected = index;
        }

        @Override
        protected boolean isSelected(int index)
        {
            return this.selected == index;
        }

        @Override
        protected void drawBackground()
        {
        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
        {
            ResourceLocation item = list.get(slotIdx);
            FontRenderer font = GuiCustomizeWorld.this.fontRendererObj;

            font.drawString(font.trimStringToWidth(item.toString(), listWidth - 10), this.left + 3 , slotTop +  2, 0xFF2222);
        }
    }
}
