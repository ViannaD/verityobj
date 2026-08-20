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
 * Carrega um .obj simples (v / vt / vn / f, sem materiais múltiplos) de dentro
 * dos recursos (assets) do mod e devolve uma malha já triangulada em floats prontos
 * para mandar direto pro VertexConsumer.
 *
 * IMPORTANTE: as normais "vn" do próprio arquivo são ignoradas de propósito.
 * O Blockbench costuma exportar UMA normal "chapada" por face (não suavizada
 * entre vértices vizinhos), o que em superfícies curvas (como uma esfera) cria
 * um efeito de "bola de discoteca" — faixas escuras/claras alternadas, muito
 * visíveis. Em vez disso, recalculamos normais SUAVES aqui: para cada vértice,
 * fazemos a média das normais de todas as faces que o tocam. Isso deixa
 * superfícies curvas com sombreamento liso, como esperado.
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
        // Cada "corner" triangulado: [posIndex, uvIndex]
        List<int[]> corners = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("v ")) {
                positions.add(parseFloats(line.substring(2), 3));
            } else if (line.startsWith("vt ")) {
                texCoords.add(parseFloats(line.substring(3), 2));
            } else if (line.startsWith("vn ")) {
                // Ignorado de propósito - ver comentário da classe.
            } else if (line.startsWith("f ")) {
                String[] tokens = line.substring(2).trim().split("\\s+");
                int[][] parsed = new int[tokens.length][];
                for (int i = 0; i < tokens.length; i++) {
                    parsed[i] = parseCorner(tokens[i]);
                }
                // Leque de triângulos: (0,1,2), (0,2,3), (0,3,4)...
                for (int i = 1; i < parsed.length - 1; i++) {
                    corners.add(parsed[0]);
                    corners.add(parsed[i]);
                    corners.add(parsed[i + 1]);
                }
            }
        }

        float[][] smoothNormals = computeSmoothNormals(positions, corners);

        float[] out = new float[corners.size() * 8];
        int w = 0;
        for (int[] corner : corners) {
            int posIdx = corner[0];
            int uvIdx = corner[1];

            float[] pos = positions.get(posIdx);
            float[] normal = smoothNormals[posIdx];

            float u = 0f, v = 0f;
            if (uvIdx >= 0) {
                float[] tc = texCoords.get(uvIdx);
                u = tc[0];
                v = 1.0f - tc[1]; // flip V: OBJ tem v=0 embaixo, textura tem v=0 em cima
            }

            out[w++] = pos[0];
            out[w++] = pos[1];
            out[w++] = pos[2];
            out[w++] = u;
            out[w++] = v;
            out[w++] = normal[0];
            out[w++] = normal[1];
            out[w++] = normal[2];
        }

        return new ObjMeshData(out);
    }

    /** Calcula, para cada posição, a média normalizada das normais de face de todos os triângulos que a usam. */
    private static float[][] computeSmoothNormals(List<float[]> positions, List<int[]> corners) {
        float[][] accum = new float[positions.size()][3];

        for (int i = 0; i < corners.size(); i += 3) {
            float[] p0 = positions.get(corners.get(i)[0]);
            float[] p1 = positions.get(corners.get(i + 1)[0]);
            float[] p2 = positions.get(corners.get(i + 2)[0]);

            float ex1 = p1[0] - p0[0], ey1 = p1[1] - p0[1], ez1 = p1[2] - p0[2];
            float ex2 = p2[0] - p0[0], ey2 = p2[1] - p0[1], ez2 = p2[2] - p0[2];

            // Produto vetorial (e1 x e2) = normal da face (não normalizado ainda)
            float nx = ey1 * ez2 - ez1 * ey2;
            float ny = ez1 * ex2 - ex1 * ez2;
            float nz = ex1 * ey2 - ey1 * ex2;

            addTo(accum, corners.get(i)[0], nx, ny, nz);
            addTo(accum, corners.get(i + 1)[0], nx, ny, nz);
            addTo(accum, corners.get(i + 2)[0], nx, ny, nz);
        }

        for (float[] n : accum) {
            float len = (float) Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
            if (len > 1e-6f) {
                n[0] /= len;
                n[1] /= len;
                n[2] /= len;
            } else {
                n[0] = 0f;
                n[1] = 1f;
                n[2] = 0f;
            }
        }

        return accum;
    }

    private static void addTo(float[][] accum, int index, float x, float y, float z) {
        accum[index][0] += x;
        accum[index][1] += y;
        accum[index][2] += z;
    }

    /** Retorna [posIndex, uvIndex] (0-based; uvIndex = -1 se não houver). */
    private static int[] parseCorner(String corner) {
        String[] idx = corner.split("/");
        int vi = Integer.parseInt(idx[0]) - 1;
        int vti = -1;
        if (idx.length > 1 && !idx[1].isEmpty()) {
            vti = Integer.parseInt(idx[1]) - 1;
        }
        return new int[]{vi, vti};
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
