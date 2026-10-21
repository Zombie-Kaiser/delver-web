package com.interrupt.dungeoneer.serializers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.interrupt.dungeoneer.tiles.Tile;
import com.interrupt.dungeoneer.tiles.Tile.TileSpaceType;

/**
 * Compact JSON serialization of tiles, persisting exactly the same field
 * set as the original kryo TileSerializer (v1 format).
 */
public class TileJsonSerializer implements Json.Serializer<Tile> {

	@Override
	public void write(Json json, Tile tile, Class knownType) {
		json.writeObjectStart();
		json.writeValue("r", tile.renderSolid, Boolean.class);
		json.writeValue("b", tile.blockMotion, Boolean.class);
		json.writeValue("w", tile.wallTex, Byte.class);
		json.writeValue("s", tile.seen, Boolean.class);
		if(!tile.renderSolid) {
			json.writeValue("n", tile.canNav, Boolean.class);
			json.writeValue("c", tile.ceilTex, Byte.class);
			json.writeValue("f", tile.floorTex, Byte.class);
			json.writeValue("ch", tile.ceilHeight, Float.class);
			json.writeValue("cne", tile.ceilSlopeNE, Float.class);
			json.writeValue("cnw", tile.ceilSlopeNW, Float.class);
			json.writeValue("cse", tile.ceilSlopeSE, Float.class);
			json.writeValue("csw", tile.ceilSlopeSW, Float.class);
			json.writeValue("fh", tile.floorHeight, Float.class);
			json.writeValue("sne", tile.slopeNE, Float.class);
			json.writeValue("snw", tile.slopeNW, Float.class);
			json.writeValue("sse", tile.slopeSE, Float.class);
			json.writeValue("ssw", tile.slopeSW, Float.class);
			if(tile.wallBottomTex != null) json.writeValue("wb", tile.wallBottomTex, Byte.class);
			json.writeValue("cr", tile.ceilTexRot, Byte.class);
			json.writeValue("fr", tile.floorTexRot, Byte.class);
			json.writeValue("t", tile.tileType, Byte.class);
			json.writeValue("sp", tile.tileSpaceType.name(), String.class);
			json.writeValue("dc", tile.drawCeiling, Boolean.class);
			json.writeValue("dw", tile.drawWalls, Boolean.class);
		}
		json.writeObjectEnd();
	}

	@Override
	public Tile read(Json json, JsonValue jsonData, Class type) {
		if(jsonData == null || jsonData.isNull()) return null;

		Tile tile = new Tile();
		tile.renderSolid = jsonData.getBoolean("r", false);
		tile.blockMotion = jsonData.getBoolean("b", false);
		tile.wallTex = jsonData.getByte("w", (byte)0);
		tile.seen = jsonData.getBoolean("s", false);
		if(!tile.renderSolid) {
			tile.canNav = jsonData.getBoolean("n", true);
			tile.ceilTex = jsonData.getByte("c", (byte)1);
			tile.floorTex = jsonData.getByte("f", (byte)2);
			tile.ceilHeight = jsonData.getFloat("ch", 0.5f);
			tile.ceilSlopeNE = jsonData.getFloat("cne", 0);
			tile.ceilSlopeNW = jsonData.getFloat("cnw", 0);
			tile.ceilSlopeSE = jsonData.getFloat("cse", 0);
			tile.ceilSlopeSW = jsonData.getFloat("csw", 0);
			tile.floorHeight = jsonData.getFloat("fh", -0.5f);
			tile.slopeNE = jsonData.getFloat("sne", 0);
			tile.slopeNW = jsonData.getFloat("snw", 0);
			tile.slopeSE = jsonData.getFloat("sse", 0);
			tile.slopeSW = jsonData.getFloat("ssw", 0);
			int wallBottom = jsonData.getInt("wb", -1);
			if(wallBottom >= 0) tile.wallBottomTex = (byte)wallBottom;
			tile.ceilTexRot = jsonData.getByte("cr", (byte)0);
			tile.floorTexRot = jsonData.getByte("fr", (byte)0);
			tile.tileType = jsonData.getByte("t", (byte)0);
			String spaceType = jsonData.getString("sp", null);
			if(spaceType != null) {
				try {
					tile.tileSpaceType = TileSpaceType.valueOf(spaceType);
				}
				catch(IllegalArgumentException ignored) {
				}
			}
			tile.drawCeiling = jsonData.getBoolean("dc", true);
			tile.drawWalls = jsonData.getBoolean("dw", true);
		}
		return tile;
	}
}
