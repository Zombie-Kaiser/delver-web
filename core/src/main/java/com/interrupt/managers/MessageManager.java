package com.interrupt.managers;

import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.helpers.Message;
import com.interrupt.utils.JsonUtil;

public class MessageManager {
	public MessageManager() { }
	
	public static Message getMessage(String file) {
		FileHandle handle = findMessageFile(file);
		Message loadedMessage = JsonUtil.fromJson(Message.class, handle);
		return loadedMessage;
	}

	private static FileHandle findMessageFile(String file) {
		if(Options.isChinese()) {
			int dot = file.lastIndexOf('.');
			if(dot > 0) {
				FileHandle zh = Game.findInternalFileInMods("data/messages/" + file.substring(0, dot) + "_zh" + file.substring(dot));
				if(zh != null && zh.exists()) return zh;
			}
		}
		return Game.findInternalFileInMods("data/messages/" + file);
	}
}
