package es.outlook.adriansrj.battleroyale.util.nbt;

import net.kyori.adventure.nbt.CompoundBinaryTag;

/**
 * Represents an object that can be serialized into a NBT compound tag.
 *
 * @author AdrianSR / 25/08/2021 / Time: 09:03 a. m.
 */
public interface NBTSerializable {
	
	public CompoundBinaryTag toNBT ( );
}