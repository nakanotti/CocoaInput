package jp.axer.cocoainput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jp.axer.cocoainput.arch.wayland.WaylandController;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Platform;

import jp.axer.cocoainput.arch.darwin.DarwinController;
import jp.axer.cocoainput.arch.dummy.DummyController;
import jp.axer.cocoainput.arch.win.WinController;
import jp.axer.cocoainput.arch.x11.X11Controller;
import jp.axer.cocoainput.plugin.CocoaInputController;
import jp.axer.cocoainput.util.ConfigPack;
import jp.axer.cocoainput.util.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public class CocoaInput {
	private static CocoaInputController controller;
	private static String zipsource;
	public static ConfigPack config = ConfigPack.defaultConfig;
	
	public CocoaInput(String loader, String zipfile) {
		ModLogger.log("Modloader:" + loader);
		CocoaInput.zipsource = zipfile;
		try {
			switch GLFW.glfwGetPlatform() {
				case GLFW.GLFW_PLATFORM_COCOA:
					CocoaInput.applyController(new DarwinController());
					break;
				case GLFW.GLFW_PLATFORM_WIN32:
					CocoaInput.applyController(new WinController());
					break;
				case GLFW.GLFW_PLATFORM_WAYLAND:
					CocoaInput.applyController(new WaylandController());
					break;
				case GLFW.GLFW_PLATFORM_X11:
					CocoaInput.applyController(new X11Controller());
					break;
				default:
					ModLogger.log("CocoaInput cannot find appropriate Controller in running OS.");
					CocoaInput.applyController(new DummyController());
			}
			ModLogger.log("CocoaInput has been initialized.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isWayland() {
		return GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WAYLAND;
	}

	private boolean isX11() {
		return GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_X11;
	}

	public static double getScreenScaledFactor() {
		return Minecraft.getInstance().getWindow().getGuiScale();
	}

	public static void applyController(CocoaInputController controller) throws IOException {
		CocoaInput.controller = controller;
		ModLogger.log("CocoaInput is now using controller:" + controller.getClass().toString());
	}

	public static CocoaInputController getController() {
		return CocoaInput.controller;
	}

	public void distributeScreen(Screen sc) {
		if (CocoaInput.getController() != null) {
			CocoaInput.getController().screenOpenNotify(sc);
		}
	}

	public static void copyLibrary(String libraryName, String libraryPath) throws IOException {
		InputStream libFile;
		if (zipsource == null) {//Fabric case
			libFile = CocoaInput.class.getResourceAsStream("/" + libraryPath);
		} else {
			try {//Modファイルを検出し、jar内からライブラリを取り出す
				ZipFile jarfile = new ZipFile(CocoaInput.zipsource);
				libFile = jarfile.getInputStream(new ZipEntry(libraryPath));
			} catch (FileNotFoundException e) {//存在しない場合はデバッグモードであるのでクラスパスからライブラリを取り出す
				ModLogger.log("Couldn't get library path. Is this debug mode?'");
				libFile = ClassLoader.getSystemResourceAsStream(libraryPath);
			}
		}
		Minecraft mc = Minecraft.getInstance();
		String nativeDirString = mc.gameDirectory.getAbsolutePath().concat("/native");
		File nativeDir = new File(nativeDirString);
		File copyLibFile = new File(nativeDirString.concat("/" + libraryName));
		try {
			nativeDir.mkdir();
			FileOutputStream fos = new FileOutputStream(copyLibFile);
			copyLibFile.createNewFile();
			IOUtils.copy(libFile, fos);
			fos.close();
		} catch (IOException e1) {
			ModLogger.error("Attempted to copy library to ./native/" + libraryName + " but failed.");
			throw e1;
		}
		System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
		ModLogger.log("CocoaInput has copied library to native directory.");
	}
}
