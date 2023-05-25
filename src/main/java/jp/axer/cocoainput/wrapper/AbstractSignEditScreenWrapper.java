package jp.axer.cocoainput.wrapper;

import jp.axer.cocoainput.CocoaInput;
import jp.axer.cocoainput.plugin.IMEOperator;
import jp.axer.cocoainput.plugin.IMEReceiver;
import jp.axer.cocoainput.util.ModLogger;
import jp.axer.cocoainput.util.Rect;
import jp.axer.cocoainput.util.WrapperUtil;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;

public class AbstractSignEditScreenWrapper extends IMEReceiver {
    private AbstractSignEditScreen owner;
    private IMEOperator myIME;


    public AbstractSignEditScreenWrapper(AbstractSignEditScreen field) {
        ModLogger.debug("AbstractSignEditScreen init: " + field.hashCode());
        owner = field;
        myIME = CocoaInput.getController().generateIMEOperator(this);
        myIME.setFocused(true);
    }

    protected SignText getSignText()
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = (Player) mc.player;
        return owner.sign.getTextFacingPlayer(player);
    }

    protected void setText(String text) {
        getSignText().setMessage(owner.line, Component.literal(text));
    	String [] util = owner.messages;
    	util[owner.line]=text;
    }

	protected String getText() {
		return getSignText().getMessage(owner.line,false).getString();
	}

	protected void setCursorInvisible() {
		owner.frame=6;
	} //TODO

	protected int getCursorPos() {
		return owner.signField.getCursorPos();
	}

	protected void setCursorPos(int p) {
		owner.signField.setCursorPos(p,true);
	}

	protected void setSelectionPos(int p) {
		owner.signField.setSelectionRange(p,p);
	}


    @Override
    public Rect getRect() {

        Font fontRendererObj = null;
        try {
            fontRendererObj = WrapperUtil.makeFont(owner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float y = 91 + (owner.line - 1) * (10);
        if (!(owner.sign.getBlockState().getBlock() instanceof StandingSignBlock)) {
            y += 30;
        }
        return new Rect(
        		owner.width/2+fontRendererObj.width(getSignText().getMessage(owner.line,false).toString().substring(0, originalCursorPosition))/2,
//                owner.width / 2 + fontRendererObj.width(owner.sign.getMessage(owner.line,false).getString()) / 2,
                y,
                0,
                0
        );
    }
    public int renewCursorCounter() {
        return owner.frame+(cursorVisible?1:0);
    }

    @Override
	protected int getMarkedLength()
	{
        return 0;
	}

}
