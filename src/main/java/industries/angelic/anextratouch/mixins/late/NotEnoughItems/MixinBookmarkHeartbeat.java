package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import java.util.IdentityHashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.ItemsGrid;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.bookmark.BookmarkGrid;
import codechicken.nei.bookmark.BookmarkItem;
import codechicken.nei.bookmark.BookmarksGridSlot;
import codechicken.nei.drawable.DrawableResource;
import industries.angelic.anextratouch.handlers.client.BookmarkHeartbeat;

@Mixin(value = BookmarkGrid.class, remap = false)
public abstract class MixinBookmarkHeartbeat extends ItemsGrid<BookmarksGridSlot, BookmarkGrid.BookmarkMouseContext> {

    @Unique
    private final Map<BookmarkItem, Long> anextratouch$hearts = new IdentityHashMap<>();

    @Inject(method = "addItem", at = @At("RETURN"))
    private void anextratouch$added(BookmarkItem item, boolean animate, CallbackInfo ci) {
        long now = System.nanoTime();
        anextratouch$hearts.values()
            .removeIf(start -> now - start >= BookmarkHeartbeat.DURATION_NANOS);
        // Loading saved bookmarks uses animate=false. Recipe ingredients are not new favorites.
        if (animate && NEIClientConfig.areBookmarksAnimated() && item.type != BookmarkItem.BookmarkItemType.INGREDIENT)
            anextratouch$hearts.put(item, now);
    }

    @Inject(
        method = "afterDrawItems(IILcodechicken/nei/bookmark/BookmarkGrid$BookmarkMouseContext;)V",
        at = @At("RETURN"))
    private void anextratouch$draw(int mouseX, int mouseY, BookmarkGrid.BookmarkMouseContext context, CallbackInfo ci) {
        if (!NEIClientConfig.areBookmarksAnimated()) {
            anextratouch$hearts.clear();
            return;
        }
        long now = System.nanoTime();
        anextratouch$hearts.values()
            .removeIf(start -> now - start >= BookmarkHeartbeat.DURATION_NANOS);
        if (anextratouch$hearts.isEmpty()) return;
        BookmarkGrid grid = (BookmarkGrid) (Object) this;
        // This pass is outside NEI's item framebuffer cache, and uses the final visible slot layout.
        for (BookmarksGridSlot slot : getMask()) {
            Long start = anextratouch$hearts.get(grid.getBookmarkItem(slot.itemIndex));
            if (start != null) anextratouch$heart(getSlotRect(slot.slotIndex), now - start);
        }
    }

    @Unique
    private static void anextratouch$heart(Rectangle4i rect, long elapsed) {
        DrawableResource heart = AccessorFavoriteHeart.anextratouch$heart();
        int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1, 1, 1, BookmarkHeartbeat.alpha(elapsed));
            GL11.glTranslatef(rect.x + rect.w / 2F, rect.y + rect.h / 2F, 0);
            float scale = BookmarkHeartbeat.scale(elapsed);
            GL11.glScalef(scale, scale, 1);
            GL11.glTranslatef(-heart.width / 2F, -heart.height / 2F, 0);
            heart.draw(0, 0);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        }
    }
}
