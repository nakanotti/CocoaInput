package jp.axer.cocoainput.arch.wayland;

import jp.axer.cocoainput.CocoaInput;
import jp.axer.cocoainput.plugin.CocoaInputController;
import jp.axer.cocoainput.plugin.IMEOperator;
import jp.axer.cocoainput.plugin.IMEReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWayland;

import java.io.IOException;
import java.lang.reflect.Field;

public class WaylandController implements CocoaInputController {

	static WaylandIMEOperator focusedOperator = null;
	String toBePreedit = "";
	int preeditBefore = 0, preeditAfter = 0;
	String toBeCommit = "";

	Handle.PreeditCallback preedit_callback = (String str, int before, int after) -> {
        if (WaylandController.focusedOperator != null) {
			toBePreedit = str;
			preeditBefore = before;
			preeditAfter = after;
        }
    };

	Handle.CommitCallback commit_callback = (str) -> {
		if (WaylandController.focusedOperator != null) {
			toBeCommit = str;
		}
	};

	Handle.DoneCallback done_callback = () -> {
        if (WaylandController.focusedOperator != null) {
			if(!toBePreedit.isEmpty()) WaylandController.focusedOperator.owner.setMarkedText(toBePreedit, preeditBefore, preeditAfter, 0, 0);
			if(!toBeCommit.isEmpty() || toBePreedit.isEmpty()) WaylandController.focusedOperator.owner.insertText(toBeCommit, 0, 0);
			preeditBefore = 0;
			preeditAfter = 0;
			toBePreedit = "";
			toBeCommit = "";
        }
    };

	private static final long window = Minecraft.getInstance().getWindow().getWindow();

	public WaylandController() throws IOException {
		Logger.log("This is Wayland Controller");
		CocoaInput.copyLibrary("libwaylandcocoainput.so", "wayland/libwaylandcocoainput.so");
		Logger.log("Call clang initializer");
		Handle.INSTANCE.initialize(this.done_callback, this.preedit_callback, this.commit_callback,
				GLFWNativeWayland.glfwGetWaylandDisplay(),
				Logger.clangLog, Logger.clangError, Logger.clangDebug);
		Handle.INSTANCE.unfocus();
		Logger.log("Finished clang initializer");
		Logger.log("WaylandController finished initialize");

	}

	@Override
	public IMEOperator generateIMEOperator(IMEReceiver arg0) {
		// TODO Auto-generated method stub
		return new WaylandIMEOperator(arg0);
	}

	@Override
	public void screenOpenNotify(Screen gui) {
		try {
			Field wrapper = gui.getClass().getField("wrapper");
			wrapper.setAccessible(true);
			if (wrapper.get(gui) instanceof IMEReceiver)
				return;
		} catch (Exception e) {
			/* relax */}
		if (WaylandController.focusedOperator != null) {
			WaylandController.focusedOperator.setFocused(false);
			WaylandController.focusedOperator = null;
		}
	}

}
