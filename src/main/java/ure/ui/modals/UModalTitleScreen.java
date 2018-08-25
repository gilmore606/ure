package ure.ui.modals;

import ure.areas.UArea;
import ure.commands.UCommand;
import ure.editors.glyphed.GlyphedModal;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.RexFile;
import ure.ui.modals.widgets.*;

import java.io.File;

public class UModalTitleScreen extends UModal implements HearModalGetString {

    WidgetRexImage logoWidget;
    WidgetText titleWidget;
    WidgetListVert menuWidget;

    int fakeTickCount;
    UArea area;

    String titleMsg = "Example Quest : Curse of the Feature Creep";

    double lastActiveTime;
    int hideSeconds = 10;

    public UModalTitleScreen(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UArea _area) {
        super(_callback,_callbackContext);
        setDimensions(cellwidth,cellheight);
        escapable = false;
        logoWidget = new WidgetRexImage(this,0,0,"ure_logo.xp");
        logoWidget.alpha = 0f;
        addCenteredWidget(logoWidget);
        titleWidget = new WidgetText(this,0,11,titleMsg);
        titleWidget.hidden = true;
        addCenteredWidget(titleWidget);

        setBgColor(new UColor(0.07f,0.07f,0.07f));

        String[] options;
        File file = new File(commander.savePath() + "player");
        if (!file.isFile())
            options = new String[]{"New World", "VaultEd", "GlyphEd", "Credits", "Quit"};
        else
            options = new String[]{"Continue", "New World", "VaultEd", "GlyphEd", "Credits", "Quit"};
        menuWidget = new WidgetListVert(this,0,13,options);
        menuWidget.hidden = true;
        menuWidget.dismissFlash = true;
        addCenteredWidget(menuWidget);

        fakeTickCount = 0;
        area = _area;
        commander.speaker.playBGM(commander.config.getTitleMusic());
        lastActiveTime = System.currentTimeMillis();
    }

    @Override
    public void drawFrame() {
        if ((System.currentTimeMillis() - lastActiveTime) < hideSeconds*1000) {
            super.drawFrame();
        }
    }

    @Override
    public void drawContent() {
        if ((System.currentTimeMillis() - lastActiveTime) < hideSeconds*1000) {
            super.drawContent();
        }
    }
    @Override
    public void hearCommand(UCommand command, GLKey k) {

        if (logoWidget.alpha < 1f) {
            logoWidget.alpha = 1f;
            return;
        }
        if (System.currentTimeMillis() - lastActiveTime > hideSeconds*1000) {
            lastActiveTime = System.currentTimeMillis();
            return;
        }
        lastActiveTime = System.currentTimeMillis();
        super.hearCommand(command, k);
    }
    @Override
    public void mouseClick() {
        if (logoWidget.alpha < 1f) {
            logoWidget.alpha = 1f;
            return;
        }
        if (System.currentTimeMillis() - lastActiveTime > hideSeconds*1000) {
            lastActiveTime = System.currentTimeMillis();
            return;
        }
        lastActiveTime = System.currentTimeMillis();
        super.mouseClick();
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == menuWidget) {
            pickSelection(menuWidget.choice());
        }
    }

    void pickSelection(String option) {
        if (option.equals("New World")) {
            UModalGetString smodal = new UModalGetString("Name your character:", 20,this, "name-new-world");
            commander.showModal(smodal);
        } else if (option.equals("Credits")) {
            UModalNotify nmodal = new UModalNotify("URE: the unRoguelike Engine\n \nSpunky - metaprogramming, persistence, rendering\nMoycakes - OpenGL\nKapho - QA, content\nGilmore - misc");
            nmodal.setPad(1,1);
            nmodal.setTitle("credits");
            commander.showModal(nmodal);
        } else if (option.equals("VaultEd")) {
            commander.launchVaulted();
        } else if (option.equals("GlyphEd")) {
            GlyphedModal modal = new GlyphedModal();
            commander.showModal(modal);
        } else {
            dismiss();
            ((HearModalTitleScreen) callback).hearModalTitleScreen(option, null);
        }
    }

    public void hearModalGetString(String context, String input) {
        if (context.equals("name-new-world")) {
            dismiss();
            ((HearModalTitleScreen) callback).hearModalTitleScreen("New World", input);
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        area.animationTick();
        logoWidget.alpha += 0.02f;
        if (logoWidget.alpha >1f) {
            logoWidget.alpha = 1f;
            titleWidget.hidden = false;
            menuWidget.hidden = false;
        }
        fakeTickCount++;
        if (fakeTickCount > 20) {
            fakeTickCount = 0;
            commander.tickTime();
            commander.letActorsAct();
        }
    }
}
