package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.ui.modals.HearModalQuantity;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalInventory;
import ure.ui.modals.UModalQuantity;

import java.util.ArrayList;

public class Pile extends UThing implements HearModalQuantity {

    public static final String TYPE = "pile";

    public int count;

    public float weightPer;
    public float valuePer;

    @JsonIgnore
    private UContainer dropdest;

    @Override
    public String name() {
        return Integer.toString(count) + " " + name;
    }

    @Override
    public String getIname() { return name(); }
    @Override
    public String getDname() { return name(); }

    @Override
    public int weight() {
        return (int)(weightPer * count);
    }
    @Override
    public int value() {
        return (int)(valuePer * count);
    }

    @Override
    public void moveTo(UContainer container) {
        super.moveTo(container);
        if (container.things() != null) {
            for (UThing thing : (ArrayList<UThing>)container.things().clone()) {
                if (thing != this && thing instanceof Pile) {
                    if (thing.name.equals(name)) {
                        count += ((Pile) thing).getCount();
                        thing.junk();
                    }
                }
            }
        }
    }

    @Override
    public boolean tryDrop(UContainer dest) {
        if (count <= 1)
            return super.tryDrop(dest);
        UModalQuantity qmodal = new UModalQuantity("Drop how many of your " + name() + "?", 1, count,  this, "drop");
        commander.showModal(qmodal);
        dropdest = dest;
        return false;
    }

    public void hearModalQuantity(String callback, int dropcount) {
        if (dropcount >= count) {
            super.tryDrop(dropdest);
        } else {
            try {
                Pile leftover = (Pile) clone();
                leftover.setCount(count - dropcount);
                setCount(dropcount);
                UContainer oldlocation = location;
                super.tryDrop(dropdest);
                leftover.moveTo(oldlocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        commander.updateInventoryModal();
    }

    public int getCount() { return count; }
    public void setCount(int i) { count = i; }
    public float getWeightPer() { return weightPer; }
    public void setWeightPer(float f) { weightPer = f; }
}
