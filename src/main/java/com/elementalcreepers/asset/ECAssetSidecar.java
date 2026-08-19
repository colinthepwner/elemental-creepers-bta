package com.elementalcreepers.asset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import net.minecraft.core.data.registry.Registries;
import org.useless.dragonfly.data.entity.mojang.EntityGeometryMojangData;
import com.elementalcreepers.ElementalCreepers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class ECAssetSidecar {
	private ECAssetSidecar() {}

	public static final String PACK_NAME = "ElementalCreepersAssets";
	private static final String MANIFEST = "/assets/elementalcreepers/asset-sidecar.properties";
	private static final String STAMP = "sidecar-source.txt";

	private static final int BRIDGE_REVISION = 16;

	private static final int MAX_NESTING = 3;

	public static int bridgedCount = -1;
	public static int missingCount = -1;
	public static String sourceArchive = null;
	public static boolean packAutoEnabled = false;
	public static boolean usedCache = false;

	public static void run() {
		File gameDir;
		try {
			gameDir = Minecraft.getMinecraft().getMinecraftDir();
		} catch (Throwable t) {

			return;
		}
		if (gameDir == null) return;

		Map<String, List<String>> manifest = readManifest();
		if (manifest.isEmpty()) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: manifest {} is empty or missing, skipping", MANIFEST);
			return;
		}

		File packDir = new File(gameDir, "texturepacks/" + PACK_NAME);

		Stamp previous = Stamp.read(packDir);
		if (previous != null && previous.stillValid(gameDir)) {
			usedCache = true;
			sourceArchive = previous.label;
			ElementalCreepers.LOGGER.info("Asset sidecar: '{}' is already built from {}, skipping extraction",
				PACK_NAME, previous.label);
			enablePack(packDir);
			return;
		}

		Set<String> wanted = new LinkedHashSet<>(manifest.keySet());

		List<Source> sources = findSources(gameDir, packDir, wanted);
		if (sources.isEmpty()) {
			reportNothingFound(gameDir, packDir, manifest.size());
			return;
		}

		List<Source> used = new ArrayList<>();
		Map<String, byte[]> entries = collect(sources, wanted, used);
		if (entries.isEmpty()) {
			reportNothingFound(gameDir, packDir, manifest.size());
			return;
		}
		sourceArchive = describe(gameDir, used);

		List<String> missing = new ArrayList<>();
		int written = 0;
		try {
			for (Map.Entry<String, List<String>> mapping : manifest.entrySet()) {
				byte[] bytes = entries.get(mapping.getKey());
				if (bytes == null) {
					missing.add(mapping.getKey());
					continue;
				}

				for (String path : mapping.getValue()) {
					write(new File(packDir, path), bytes);
					written++;
				}
			}
			writePackMeta(packDir);
		} catch (IOException e) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: could not write into '{}': {}", packDir.getPath(), e.toString());
			return;
		}

		bridgedCount = written;
		missingCount = missing.size();

		ElementalCreepers.LOGGER.info("Asset sidecar: {} textures bridged from {} into texture pack '{}'",
			written, sourceArchive, PACK_NAME);
		if (used.size() > 1) {
			ElementalCreepers.LOGGER.info("Asset sidecar: sources used, best first: {}", paths(gameDir, used));
		}
		if (!missing.isEmpty()) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: {} textures not found in the archive: {}",
				missing.size(), String.join(", ", missing));
		}

		new Stamp(sourceArchive, used, gameDir).write(packDir);
		enablePack(packDir);
	}

	private static void reportNothingFound(File gameDir, File packDir, int manifestSize) {
		bridgedCount = 0;
		missingCount = manifestSize;

		if (new File(packDir, "assets").isDirectory()) {
			usedCache = true;
			Stamp previous = Stamp.read(packDir);
			sourceArchive = previous != null ? previous.label : "a pack built elsewhere";
			ElementalCreepers.LOGGER.info("Asset sidecar: no copy of Elemental Creepers found under '{}', but "
				+ "texture pack '{}' is already built from {} -- using it as it stands. Nothing needs "
				+ "rebuilding unless you want to.", gameDir.getPath(), PACK_NAME, sourceArchive);
			enablePack(packDir);
			return;
		}

		ElementalCreepers.LOGGER.info("Asset sidecar: no Elemental Creepers art found anywhere under '{}' -- the "
			+ "eight skinned creepers will wear the vanilla creeper texture. Behaviour is unaffected. Drop a "
			+ "copy of ElementalCreepers_v1.4.zip in (zip, jar or unpacked folder, any name, any depth) to "
			+ "restore the original look.", gameDir.getPath());
	}

	private static final class Source implements Comparable<Source> {
		final File file;

		final boolean container;

		final String name;
		final int rank;
		final int depth;

		Source(File file, boolean container, String name, int rank, int depth) {
			this.file = file;
			this.container = container;
			this.name = name;
			this.rank = rank;
			this.depth = depth;
		}

		@Override
		public int compareTo(Source other) {
			if (rank != other.rank) return Integer.compare(rank, other.rank);
			if (depth != other.depth) return Integer.compare(depth, other.depth);
			return file.getPath().compareToIgnoreCase(other.file.getPath());
		}
	}

	private static final int RANK_EXPLICIT = 0;

	private static final int RANK_NAMED = 1;

	private static final int RANK_MODS = 2;

	private static final int RANK_ANYWHERE = 3;

	private static final int MAX_SCAN_DEPTH = 6;

	private static final int MAX_SCAN_FILES = 20000;

	private static final int MAX_CONTAINERS_READ = 400;

	private static final Set<String> SKIPPED_DIRS = new HashSet<>(Arrays.asList(
		"saves", "logs", "crash-reports", "screenshots", "stats", "assets", "libraries", "versions",
		"bin", "natives", "server-resource-packs", "backups", "shaderpacks", ".git", ".fabric", ".mixin.out"));

	private static List<Source> findSources(File gameDir, File packDir, Set<String> wanted) {
		List<Source> sources = new ArrayList<>();

		String rootPath = safePath(gameDir);
		String packPath = safePath(packDir);
		int[] budget = {MAX_SCAN_FILES};
		scan(gameDir, rootPath, packPath, wanted, sources, 0, budget);
		Collections.sort(sources);
		return sources;
	}

	private static void scan(File dir, String rootPath, String packPath, Set<String> wanted,
		List<Source> sources, int depth, int[] budget) {
		if (depth > MAX_SCAN_DEPTH || budget[0] <= 0) return;

		File[] children = dir.listFiles();
		if (children == null) return;

		for (File child : children) {
			if (budget[0] <= 0) return;
			budget[0]--;

			if (child.isDirectory()) {
				String name = child.getName().toLowerCase(Locale.ROOT);

				if (SKIPPED_DIRS.contains(name) || safePath(child).equals(packPath)) continue;
				scan(child, rootPath, packPath, wanted, sources, depth + 1, budget);
				continue;
			}
			if (!child.isFile()) continue;

			String name = baseName(child.getName());
			if (wanted.contains(name)) {
				sources.add(new Source(child, false, name, rank(rootPath, child), depth));
			} else if (!isOwnJar(name) && isZip(child)) {
				sources.add(new Source(child, true, name, rank(rootPath, child), depth));
			}
		}
	}

	private static int rank(String rootPath, File file) {
		String relative = relativeTo(rootPath, safePath(file)).toLowerCase(Locale.ROOT);
		if (baseName(file.getName()).startsWith("elementalcreepers-assets")) return RANK_EXPLICIT;
		if (relative.contains("elementalcreepers") || relative.contains("elemental_creepers")
			|| relative.contains("elemental-creepers") || relative.contains("elemental creepers")) {
			return RANK_NAMED;
		}
		if (relative.startsWith("mods/")) return RANK_MODS;
		return RANK_ANYWHERE;
	}

	private static boolean isOwnJar(String name) {
		return name.toLowerCase(Locale.ROOT).matches("^elementalcreepers-\\d+\\.\\d.*");
	}

	private static boolean isZip(File file) {
		if (file.length() < 4) return false;
		try (InputStream in = new FileInputStream(file)) {
			byte[] header = new byte[4];
			int read = 0;
			while (read < 4) {
				int step = in.read(header, read, 4 - read);
				if (step < 0) return false;
				read += step;
			}
			return isZipHeader(header, 4);
		} catch (IOException e) {
			return false;
		}
	}

	private static boolean isZipHeader(byte[] header, int length) {
		if (length < 4 || header[0] != 'P' || header[1] != 'K') return false;
		int third = header[2] & 0xFF;
		int fourth = header[3] & 0xFF;
		return (third == 0x03 && fourth == 0x04)
			|| (third == 0x05 && fourth == 0x06)
			|| (third == 0x07 && fourth == 0x08);
	}

	private static Map<String, byte[]> collect(List<Source> sources, Set<String> wanted, List<Source> used) {
		Map<String, byte[]> found = new HashMap<>();
		int containersRead = 0;

		for (Source source : sources) {
			if (found.size() >= wanted.size()) break;

			int before = found.size();
			if (source.container) {
				if (containersRead >= MAX_CONTAINERS_READ) continue;
				containersRead++;
				try {
					collectFromZipFile(source.file, wanted, found);
				} catch (IOException e) {

					ElementalCreepers.LOGGER.debug("Asset sidecar: skipped '{}': {}", source.file.getPath(), e.toString());
					continue;
				}
			} else if (!found.containsKey(source.name)) {
				try {
					found.put(source.name, Files.readAllBytes(source.file.toPath()));
				} catch (IOException e) {
					ElementalCreepers.LOGGER.debug("Asset sidecar: skipped '{}': {}", source.file.getPath(), e.toString());
					continue;
				}
			}

			if (found.size() > before) used.add(source);
		}
		return found;
	}

	private static void collectFromZipFile(File file, Set<String> wanted, Map<String, byte[]> found)
		throws IOException {
		try (ZipFile zip = new ZipFile(file)) {
			List<ZipEntry> nested = new ArrayList<>();

			for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
				ZipEntry entry = e.nextElement();
				if (entry.isDirectory()) continue;
				String name = baseName(entry.getName());
				if (wanted.contains(name)) {
					if (!found.containsKey(name)) {
						try (InputStream in = zip.getInputStream(entry)) {
							found.put(name, readFully(in));
						}
					}
				} else if (isNestedArchiveName(name) && !isOwnJar(name)) {
					nested.add(entry);
				}
			}

			for (ZipEntry entry : nested) {
				if (found.size() >= wanted.size()) break;
				try (InputStream in = zip.getInputStream(entry)) {
					collectFromStream(in, wanted, found, 1);
				} catch (IOException ignored) {

				}
			}
		}
	}

	private static void collectFromStream(InputStream raw, Set<String> wanted, Map<String, byte[]> found, int depth)
		throws IOException {
		if (depth > MAX_NESTING) return;

		ZipInputStream zip = new ZipInputStream(new BufferedInputStream(raw));
		ZipEntry entry;
		while ((entry = zip.getNextEntry()) != null) {
			if (entry.isDirectory()) continue;
			if (found.size() >= wanted.size()) return;

			String name = baseName(entry.getName());
			if (wanted.contains(name)) {
				if (!found.containsKey(name)) found.put(name, readFully(zip));
			} else if (isNestedArchiveName(name) && !isOwnJar(name)) {
				collectFromStream(zip, wanted, found, depth + 1);
			}
		}
	}

	private static boolean isNestedArchiveName(String name) {
		return name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".mod")
			|| name.endsWith(".pk3") || name.endsWith(".litemod") || name.endsWith(".disabled");
	}

	private static final class Stamp {
		final String label;
		final List<String> lines = new ArrayList<>();

		Stamp(String label, List<Source> used, File gameDir) {
			this.label = label;
			for (Source source : used) lines.add(line(gameDir, source.file));
		}

		private Stamp(String label, List<String> lines) {
			this.label = label;
			this.lines.addAll(lines);
		}

		static String line(File gameDir, File file) {
			return relativePath(gameDir, file) + "|" + file.length() + "|" + file.lastModified();
		}

		static Stamp read(File packDir) {
			File file = new File(packDir, STAMP);
			if (!file.isFile()) return null;
			try {
				List<String> all = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				if (all.size() < 3 || !all.get(0).trim().equals(String.valueOf(BRIDGE_REVISION))) return null;
				return new Stamp(all.get(1).trim(), all.subList(2, all.size()));
			} catch (IOException | RuntimeException e) {
				return null;
			}
		}

		boolean stillValid(File gameDir) {
			if (lines.isEmpty()) return false;
			for (String recorded : lines) {
				String trimmed = recorded.trim();
				if (trimmed.isEmpty()) continue;
				int split = trimmed.indexOf('|');
				if (split < 0) return false;
				File file = new File(gameDir, trimmed.substring(0, split));
				if (!file.isFile() || !line(gameDir, file).equals(trimmed)) return false;
			}
			return true;
		}

		void write(File packDir) {

			if (lines.isEmpty()) return;
			StringBuilder out = new StringBuilder();
			out.append(BRIDGE_REVISION).append('\n').append(label).append('\n');
			for (String recorded : lines) out.append(recorded).append('\n');
			try {
				ECAssetSidecar.write(new File(packDir, STAMP), out.toString().getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				ElementalCreepers.LOGGER.warn("Asset sidecar: could not record what the pack was built from: {}", e.toString());
			}
		}
	}

	private static String describe(File gameDir, List<Source> used) {
		if (used.isEmpty()) return "nothing";
		if (used.size() == 1) return "'" + used.get(0).file.getName() + "'";

		File shared = commonAncestor(used);
		if (shared != null && !safePath(shared).equals(safePath(gameDir))) {
			return "'" + relativePath(gameDir, shared) + "' (" + used.size() + " files)";
		}
		return "'" + used.get(0).file.getName() + "' and " + (used.size() - 1) + " more";
	}

	private static File commonAncestor(List<Source> used) {
		File shared = null;
		for (Source source : used) {
			File parent = source.file.getParentFile();
			if (parent == null) return null;
			shared = shared == null ? parent : commonAncestor(shared, parent);
			if (shared == null) return null;
		}
		return shared;
	}

	private static File commonAncestor(File a, File b) {
		String candidate = safePath(a);
		String target = safePath(b);
		while (!target.equals(candidate) && !target.startsWith(candidate + File.separator)) {
			File up = new File(candidate).getParentFile();
			if (up == null) return null;
			candidate = safePath(up);
		}
		return new File(candidate);
	}

	private static final int MAX_PATHS_LOGGED = 8;

	private static String paths(File gameDir, List<Source> used) {
		List<String> shown = new ArrayList<>();
		for (Source source : used) {
			if (shown.size() == MAX_PATHS_LOGGED) {
				shown.add("and " + (used.size() - MAX_PATHS_LOGGED) + " more");
				break;
			}
			shown.add(relativePath(gameDir, source.file));
		}
		return String.join(", ", shown);
	}

	private static String relativePath(File root, File file) {
		return relativeTo(safePath(root), safePath(file));
	}

	private static String relativeTo(String rootPath, String filePath) {
		String relative = filePath.startsWith(rootPath + File.separator)
			? filePath.substring(rootPath.length() + 1)
			: filePath;
		return relative.replace(File.separatorChar, '/');
	}

	private static String safePath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	private static void enablePack(File packDir) {
		if (!packDir.isDirectory()) return;
		try {
			TexturePackList packs = Minecraft.getMinecraft().texturePackList;
			packs.updateAvailableTexturePacks();
			for (TexturePack pack : packs.availableTexturePacks()) {
				if (!PACK_NAME.equals(pack.fileName)) continue;
				if (packs.selectedPacks.contains(pack)) {
					packAutoEnabled = true;
					reloadPackAssets(packs);
					return;
				}
				packs.setTexturePack(pack);
				packs.refreshIfReady();
				packAutoEnabled = true;
				reloadPackAssets(packs);
				ElementalCreepers.LOGGER.info("Asset sidecar: texture pack '{}' enabled automatically", PACK_NAME);
				return;
			}
			ElementalCreepers.LOGGER.info("Asset sidecar: texture pack '{}' was written but BTA did not list it; "
				+ "enable it in Options to use it", PACK_NAME);
		} catch (Throwable t) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: could not enable texture pack '{}' automatically ({}); "
				+ "enable it in Options to use it", PACK_NAME, t.toString());
		}
	}

	private static void reloadPackAssets(TexturePackList packs) {
		try {

			ItemModelDispatcher.getInstance().reload();
			Minecraft.getMinecraft().textureManager.refreshTexturesAndDisplayErrors();
		} catch (Throwable t) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: bridged item icons were written but the texture atlas "
				+ "could not be restitched ({}); restart the game to see them", t.toString());
		}

		try {

			boolean known = Registries.NAMESPACES.getItem(ElementalCreepers.MOD_ID) != null;
			ElementalCreepers.LOGGER.info("Asset sidecar: namespace '{}' registered with Dragonfly: {}",
				ElementalCreepers.MOD_ID, known);
			if (!known) {
				Registries.NAMESPACES.register(ElementalCreepers.MOD_ID, ElementalCreepers.MOD_ID);
				ElementalCreepers.LOGGER.info("Asset sidecar: registered namespace '{}' so its models can load",
					ElementalCreepers.MOD_ID);
			}

			EntityGeometryMojangData.Cache.reload(packs);
			EntityRendererDispatcher.instance.reload();
			ElementalCreepers.LOGGER.info("Asset sidecar: entity geometry reloaded from '{}'", PACK_NAME);
		} catch (Throwable t) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: bridged models were written but could not be reloaded ({}); "
				+ "restart the game to see them", t.toString());
		}
	}

	private static void write(File target, byte[] bytes) throws IOException {
		File parent = target.getParentFile();
		if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("could not create " + parent);
		try (OutputStream out = new FileOutputStream(target)) {
			out.write(bytes);
		}
	}

	private static void writePackMeta(File packDir) throws IOException {
		File info = new File(packDir, "pack.txt");
		if (info.isFile()) return;
		write(info, ("Generated by Elemental Creepers for BTA from a locally supplied copy of the original mod.\n"
			+ "Not redistributable. Delete this folder to remove it.\n").getBytes(StandardCharsets.UTF_8));
	}

	private static byte[] readFully(InputStream in) throws IOException {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}

	private static String baseName(String path) {
		int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
		return path.substring(slash + 1).toLowerCase(Locale.ROOT);
	}

	private static Map<String, List<String>> readManifest() {
		Map<String, List<String>> map = new HashMap<>();
		try (InputStream in = ECAssetSidecar.class.getResourceAsStream(MANIFEST)) {
			if (in == null) return map;
			Properties props = new Properties();
			props.load(in);
			for (String key : props.stringPropertyNames()) {
				List<String> paths = new ArrayList<>();
				for (String path : props.getProperty(key).split(",")) {
					String trimmed = path.trim();
					if (!trimmed.isEmpty()) paths.add(trimmed);
				}
				if (!paths.isEmpty()) map.put(key.toLowerCase(Locale.ROOT), paths);
			}
		} catch (IOException e) {
			ElementalCreepers.LOGGER.warn("Asset sidecar: could not read manifest: {}", e.toString());
		}
		return map;
	}
}
