package industries.angelic.anextratouch.handlers.client.extrasounds;

import net.minecraft.item.ItemStack;

/** Small state machines shared by the optional backpack hooks and their tests. */
public final class BackpackSoundState {

    private boolean initialized;
    private ItemStack previous;

    public ItemStack changed(ItemStack current) {
        ItemStack old = previous;
        boolean same = old == current || old != null && current != null
            && old.getItem() == current.getItem()
            && (!old.getHasSubtypes() || old.getItemDamage() == current.getItemDamage());
        boolean changed = initialized && !same;
        initialized = true;
        previous = current == null ? null : new ItemStack(current.getItem(), 1, current.getItemDamage());
        return changed ? current == null ? old : current : null;
    }

    public static final class Transfer {

        public final int before;
        public final int direction;
        public final int minimum;
        public final boolean discard;
        private final long expires;
        private boolean done;

        public Transfer(int before, int direction, int minimum, boolean discard, long now) {
            this.before = before;
            this.direction = direction;
            this.minimum = minimum;
            this.discard = discard;
            expires = now + 2000000000L;
        }

        public boolean expired(long now) {
            return done || now >= expires;
        }

        public String confirm(int amount, boolean containerResult, long now) {
            if (expired(now)) return null;
            if (discard ? before <= 0 || amount != 0 : !containerResult || (amount - before) * direction < minimum)
                return null;
            done = true;
            return discard ? "backpack.tank.empty" : direction > 0 ? "backpack.tank.fill" : "backpack.tank.drain";
        }
    }
}
