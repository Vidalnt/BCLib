package org.betterx.bclib.client.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class UnbakedQuad {

    private static final Vector4f POS = new Vector4f();
    private final float[] data = new float[20]; // 4 points with 3 positions and 2 uvs, 4 * (3 + 2)
    private Direction dir = Direction.UP;
    private boolean useShading = false;
    private int spriteIndex;

    public void addData(int index, float value) {
        data[index] = value;
    }

    public void setSpriteIndex(int index) {
        spriteIndex = index;
    }

    public void setDirection(Direction dir) {
        this.dir = dir;
    }

    public void setShading(boolean useShading) {
        this.useShading = useShading;
    }

    public Vector3f getPos(int index, Vector3f result) {
        int dataIndex = index * 5;
        float x = data[dataIndex++];
        float y = data[dataIndex++];
        float z = data[dataIndex];
        result.set(x, y, z);
        return result;
    }

    public BakedQuad bake(TextureAtlasSprite[] sprites, ModelState modelState) {
        Matrix4fc matrix = modelState.transformation().getMatrix();
        TextureAtlasSprite sprite = sprites[spriteIndex];

        // Crear los 4 vértices transformados
        Vector3f[] positions = new Vector3f[4];
        long[] packedUVs = new long[4];

        for (int i = 0; i < 4; i++) {
            int dataIndex = i * 5;
            float x = data[dataIndex++]; // X
            float y = data[dataIndex++]; // Y
            float z = data[dataIndex++]; // Z
            float u = data[dataIndex++]; // U
            float v = data[dataIndex]; // V

            // Transformar posición
            POS.set(x, y, z, 1.0f);
            POS.mul(matrix);
            positions[i] = new Vector3f(POS.x(), POS.y(), POS.z());

            // Convertir UV a packed long (formato estándar de Minecraft)
            // Los UVs se empaquetan como: (long)(Float.floatToIntBits(u) & 0xFFFFFFFFL) | ((long)(Float.floatToIntBits(v) & 0xFFFFFFFFL) << 32)
            packedUVs[i] = packUV(sprite.getU(u), sprite.getV(v));
        }

        // Crear el BakedQuad con el nuevo constructor de record
        return new BakedQuad(
            positions[0], // position0 (Vector3fc)
            positions[1], // position1 (Vector3fc)
            positions[2], // position2 (Vector3fc)
            positions[3], // position3 (Vector3fc)
            packedUVs[0], // packedUV0 (long)
            packedUVs[1], // packedUV1 (long)
            packedUVs[2], // packedUV2 (long)
            packedUVs[3], // packedUV3 (long)
            0, // tintIndex (int)
            dir, // face (Direction)
            sprite, // sprite (TextureAtlasSprite)
            useShading, // shade (boolean)
            0 // lightEmission (int)
        );
    }

    // Método auxiliar para empaquetar UVs en long
    private static long packUV(float u, float v) {
        int uBits = Float.floatToIntBits(u);
        int vBits = Float.floatToIntBits(v);
        return (uBits & 0xFFFFFFFFL) | ((long) (vBits & 0xFFFFFFFFL) << 32);
    }
}
