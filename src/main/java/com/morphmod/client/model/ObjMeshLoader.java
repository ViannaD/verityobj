package com.morphmod.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Carrega um .obj simples (apenas v / vt / vn / f, sem materiais múltiplos) de dentro
 * dos recursos (assets) do mod e devolve uma malha já triangulada em floats prontos
 * para mandar direto pro VertexConsumer.
 *
 * Formato de face suportado: "f v/vt/vn v/vt/vn v/vt/vn ..." (triângulos ou quads,
 * como o Blockbench exporta). Faces com mais de 3 vértices são "leque-trianguladas".
 */
public class ObjMeshLoader {

    public static Optional<ObjMeshData> load(ResourceLocation location) {
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                return Optional.of(parse(reader));
            }
        } catch (IOException e) {
            System.err.println("[morphmod] Falha ao carregar modelo OBJ " + location + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private static ObjMeshData parse(BufferedReader reader) throws IOException {
        List<float[]> positions = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<Float> out = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("v ")) {
                positions.add(parseFloats(line.substring(2), 3));
            } else if (line.startsWith("vt ")) {
                texCoords.add(parseFloats(line.substring(3), 2));
            } else if (line.startsWith("vn ")) {
                normals.add(parseFloats(line.substring(3), 3));
            } else if (line.startsWith("f ")) {
                String[] corners = line.substring(2).trim().split("\\s+");
                // Leque de triângulos: (0,1,2), (0,2,3), (0,3,4)...
                for (int i = 1; i < corners.length - 1; i++) {
                    appendCorner(corners[0], positions, texCoords, normals, out);
                    appendCorner(corners[i], positions, texCoords, normals, out);
                    appendCorner(corners[i + 1], positions, texCoords, normals, out);
                }
            }
        }

        float[] array = new float[out.size()];
        for (int i = 0; i < array.length; i++) array[i] = out.get(i);
        return new ObjMeshData(array);
    }

    private static void appendCorner(String corner, List<float[]> positions, List<float[]> texCoords,
                                      List<float[]> normals, List<Float> out) {
        String[] idx = corner.split("/");
        int vi = Integer.parseInt(idx[0]) - 1;
        float[] pos = positions.get(vi);

        float u = 0f, v = 0f;
        if (idx.length > 1 && !idx[1].isEmpty()) {
            int vti = Integer.parseInt(idx[1]) - 1;
            float[] tc = texCoords.get(vti);
            u = tc[0];
            v = 1.0f - tc[1]; // flip V: OBJ tem v=0 embaixo, textura tem v=0 em cima
        }

        float nx = 0f, ny = 1f, nz = 0f;
        if (idx.length > 2 && !idx[2].isEmpty()) {
            int vni = Integer.parseInt(idx[2]) - 1;
            float[] n = normals.get(vni);
            nx = n[0];
            ny = n[1];
            nz = n[2];
        }

        out.add(pos[0]);
        out.add(pos[1]);
        out.add(pos[2]);
        out.add(u);
        out.add(v);
        out.add(nx);
        out.add(ny);
        out.add(nz);
    }

    private static float[] parseFloats(String data, int count) {
        String[] parts = data.trim().split("\\s+");
        float[] result = new float[count];
        for (int i = 0; i < count; i++) {
            result[i] = Float.parseFloat(parts[i]);
        }
        return result;
    }
}
