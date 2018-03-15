package net.minecraftforge.lex.yunomakegoodmap;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiCustomizeWorld extends GuiScreen
{
    private GuiCreateWorld createGUI;
    private ScrollList list;
    private GuiScrollingList info;
    private Map<ResourceLocation, StructInfo> structInfo = Maps.newHashMap();
    private static final Gson GSON = new Gson();
    private static final int LIST_WIDTH = 130;

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
        if (this.info != null)
            this.info.handleMouseInput(mouseX, mouseY);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        if (this.info != null)
            this.info.drawScreen(mouseX, mouseY, partialTicks);
        //this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, "Select Platform", this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void collectPlatforms()
    {
        List<ResourceLocation> platforms = this.list.list;
        for (String domain : this.mc.getResourceManager().getResourceDomains())
        {
            try
            {
                for (IResource res : this.mc.getResourceManager().getAllResources(new ResourceLocation(domain, "structures/sky_block_platforms.txt")))
                {
                    for (String name : CharStreams.readLines(new InputStreamReader(res.getInputStream())))
                    {
                        try
                        {
                            if (this.mc.getResourceManager().getResource(new ResourceLocation(domain, "structures/" + name + ".nbt")) != null)
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
            super(GuiCustomizeWorld.this.mc, LIST_WIDTH, GuiCustomizeWorld.this.height,
                    32, GuiCustomizeWorld.this.height - 68 + 4, 10, GuiCustomizeWorld.this.fontRenderer.FONT_HEIGHT + 6,
                    GuiCustomizeWorld.this.width, GuiCustomizeWorld.this.height);
            this.elementClicked(0, false);
        }

        @Override
        protected int getSize()
        {
            return list.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {
            if (index == this.selected)
                return;
            this.selected = index;

            StructInfo info = GuiCustomizeWorld.this.getInfo(list.get(index));
            if (info != null)
            {
                List<String> lines = Lists.newArrayList();
                if (info.description != null)
                {
                    for (String line : info.description)
                        lines.add(line);
                }
                else
                {
                    lines.add(TextFormatting.RED + "NO INFORMATION FOUND");
                    lines.add(TextFormatting.RED + "Please add a jsonfile with description information.");
                }
                GuiCustomizeWorld.this.info = new Info(GuiCustomizeWorld.this.width - LIST_WIDTH - 30,
                        lines, info.logoPath, info.logoSize);
            }
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
            StructInfo info = getInfo(item);
            FontRenderer font = GuiCustomizeWorld.this.fontRenderer;

            font.drawString(font.trimStringToWidth(info.name, listWidth - 10), this.left + 3 , slotTop +  2, 0xFF2222);
        }
    }

    private StructInfo getInfo(ResourceLocation res)
    {
        StructInfo ret = structInfo.get(res);
        if (ret != null)
            return ret;

        try
        {
            if (res.getResourceDomain() == "/config/")
            {
                File jsonFile = new File(YUNoMakeGoodMap.instance.getStructFolder(), res.getResourcePath() + ".json");
                ret = GSON.fromJson(new InputStreamReader(new FileInputStream(jsonFile)), StructInfo.class);

                if (ret.logo != null)
                {
                    File logoFile = new File(YUNoMakeGoodMap.instance.getStructFolder(), ret.logo);
                    if (logoFile.exists())
                    {
                        BufferedImage l = ImageIO.read(new FileInputStream(logoFile));
                        ret.logoPath = this.mc.getTextureManager().getDynamicTextureLocation("platformlogo", new DynamicTexture(l));
                        ret.logoSize = new Dimension(l.getWidth(), l.getHeight());
                    }
                }
            }
            else
            {
                IResource ir = this.mc.getResourceManager().getResource(new ResourceLocation(res.getResourceDomain(), "structures/" + res.getResourcePath() + ".json"));
                ret = GSON.fromJson(new InputStreamReader(ir.getInputStream()), StructInfo.class);

                if (ret.logo != null)
                {
                    ir = this.mc.getResourceManager().getResource(new ResourceLocation(res.getResourceDomain(), "structures/" + ret.logo));
                    if (ir != null)
                    {
                        BufferedImage l = ImageIO.read(ir.getInputStream());
                        ret.logoPath = this.mc.getTextureManager().getDynamicTextureLocation("platformlogo", new DynamicTexture(l));
                        ret.logoSize = new Dimension(l.getWidth(), l.getHeight());
                    }
                }
            }
        }
        catch (IOException e)
        {
            //Ugh wish it had a 'hasResource'
        }

        if (ret == null)
            ret = new StructInfo();

        if (ret.name == null)
            ret.name = res.toString();

        structInfo.put(res, ret);
        return ret;
    }

    private static class StructInfo
    {
        public String name;
        public String logo;
        public String[] description;

        public ResourceLocation logoPath;
        public Dimension logoSize;
    }


    private class Info extends GuiScrollingList
    {
        private ResourceLocation logoPath;
        private Dimension logoDims;
        private List<ITextComponent> lines = null;

        public Info(int width, List<String> lines, ResourceLocation logoPath, Dimension logoDims)
        {
            super(GuiCustomizeWorld.this.mc,
                  width,
                  GuiCustomizeWorld.this.height,
                  32, GuiCustomizeWorld.this.height - 68 + 4,
                  LIST_WIDTH + 20, 60,
                  GuiCustomizeWorld.this.width,
                  GuiCustomizeWorld.this.height);
            this.lines    = resizeContent(lines);
            this.logoPath = logoPath;
            this.logoDims = logoDims;

            this.setHeaderInfo(true, getHeaderHeight());
        }

        @Override protected int getSize() { return 0; }
        @Override protected void elementClicked(int index, boolean doubleClick) { }
        @Override protected boolean isSelected(int index) { return false; }
        @Override protected void drawBackground() {}
        @Override protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) { }

        private List<ITextComponent> resizeContent(List<String> lines)
        {
            List<ITextComponent> ret = new ArrayList<ITextComponent>();
            for (String line : lines)
            {
                if (line == null)
                {
                    ret.add(null);
                    continue;
                }

                ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                ret.addAll(GuiUtilRenderComponents.splitText(chat, this.listWidth-8, GuiCustomizeWorld.this.fontRenderer, false, true));
            }
            return ret;
        }

        private int getHeaderHeight()
        {
          int height = 0;
          if (logoPath != null)
          {
              double scaleX = logoDims.width / 200.0;
              double scaleY = logoDims.height / 65.0;
              double scale = 1.0;
              if (scaleX > 1 || scaleY > 1)
              {
                  scale = 1.0 / Math.max(scaleX, scaleY);
              }
              logoDims.width *= scale;
              logoDims.height *= scale;

              height += logoDims.height;
              height += 10;
          }
          height += (lines.size() * 10);
          if (height < this.bottom - this.top - 8) height = this.bottom - this.top - 8;
          return height;
        }


        protected void drawHeader(int entryRight, int relativeY, Tessellator tess)
        {
            int top = relativeY;

            if (logoPath != null)
            {
                GlStateManager.enableBlend();
                GuiCustomizeWorld.this.mc.renderEngine.bindTexture(logoPath);
                BufferBuilder wr = tess.getBuffer();
                int offset = (this.left + this.listWidth/2) - (logoDims.width / 2);
                wr.begin(7, DefaultVertexFormats.POSITION_TEX);
                wr.pos(offset,                  top + logoDims.height, zLevel).tex(0, 1).endVertex();
                wr.pos(offset + logoDims.width, top + logoDims.height, zLevel).tex(1, 1).endVertex();
                wr.pos(offset + logoDims.width, top,                   zLevel).tex(1, 0).endVertex();
                wr.pos(offset,                  top,                   zLevel).tex(0, 0).endVertex();
                tess.draw();
                GlStateManager.disableBlend();
                top += logoDims.height + 10;
            }

            for (ITextComponent line : lines)
            {
                if (line != null)
                {
                    GlStateManager.enableBlend();
                    GuiCustomizeWorld.this.fontRenderer.drawStringWithShadow(line.getFormattedText(), this.left + 4, top, 0xFFFFFF);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                top += 10;
            }
        }

        @Override
        protected void clickHeader(int x, int y)
        {
            int offset = y;
            if (logoPath != null) {
              offset -= logoDims.height + 10;
            }
            if (offset <= 0)
                return;

            int lineIdx = offset / 10;
            if (lineIdx >= lines.size())
                return;

            ITextComponent line = lines.get(lineIdx);
            if (line != null)
            {
                int k = -4;
                for (ITextComponent part : line)
                {
                    if (!(part instanceof TextComponentString))
                        continue;
                    k += GuiCustomizeWorld.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                    if (k >= x)
                    {
                        GuiCustomizeWorld.this.handleComponentClick(part);
                        break;
                    }
                }
            }
        }
    }
}
