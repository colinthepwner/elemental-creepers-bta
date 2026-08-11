package com.elementalcreepers;

import com.elementalcreepers.entity.ECEntities;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;

import java.util.ArrayList;
import java.util.List;

import static com.elementalcreepers.ElementalCreepers.MOD_ID;

public final class ECAudit {
	private ECAudit() {}

	private static final String LANG_DIR = "/assets/" + MOD_ID + "/lang/en_US/";

	public static void run() {
		auditNamespace();
		auditSummonIds();
		auditNames();
	}

	private static void auditNamespace() {
		if (Registries.NAMESPACES.getItem(MOD_ID) == null) {
			ElementalCreepers.LOGGER.warn(
				"Audit problem: namespace '{}' is not registered, so BTA will never open {} and every "
					+ "creeper will be called by its own lang key.", MOD_ID, LANG_DIR);
		}
	}

	private static void auditSummonIds() {
		List<String> summonIds = new ArrayList<>();
		for (String id : ECEntities.REGISTERED_IDS) {
			NamespaceID nsid = new NamespaceID(MOD_ID, id);
			boolean known = EntityDispatcher.getInstance().idToEntryMap.get(nsid) != null;
			summonIds.add(nsid + (known ? "" : " (MISSING)"));
		}
		ElementalCreepers.LOGGER.info("Elemental Creepers summon ids: {}", String.join(", ", summonIds));
	}

	private static void auditNames() {
		List<String> unresolved = new ArrayList<>();
		I18n i18n = I18n.getInstance();
		if (i18n == null) {

			return;
		}
		for (String id : ECEntities.REGISTERED_IDS) {
			String key = ECEntities.nameKeyFor(id);
			if (key.equals(i18n.translateKey(key))) {
				unresolved.add(key);
			}
		}
		if (unresolved.isEmpty()) {
			ElementalCreepers.LOGGER.info("Elemental Creepers mob names: all {} resolved from {}",
				ECEntities.REGISTERED_IDS.size(), LANG_DIR);
		} else {
			ElementalCreepers.LOGGER.warn(
				"Audit problem: {} mob name(s) do not resolve and will display as their own key: {}",
				unresolved.size(), String.join(", ", unresolved));
		}
	}
}
