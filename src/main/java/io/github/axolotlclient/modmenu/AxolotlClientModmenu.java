package io.github.axolotlclient.modmenu;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.DefaultConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AxolotlClientModmenu extends AbstractModule {

	public static final String MOD_ID = "axolotlclient-modmenu";

	private final List<String> modIds = new ArrayList<>();
	private final HashMap<String, Function<Screen, ? extends Screen>> factories = new HashMap<>();

	public static final HashMap<String, Identifier> iconCache = new HashMap<>();

	public static Logger LOGGER = LogManager.getLogger("AxolotlClient Modmenu");

	public static Predicate<String> fabricModPredicate = s -> s.contains("fabric") && (s.contains("loader") || s.contains("api"));

	private boolean initialized = false;

	public static AxolotlClientModMenuConfig config = new AxolotlClientModMenuConfig();

	public static final OptionCategory mods = new OptionCategory("mods");
	public final BooleanOption showLibraries = new BooleanOption("modmenu.showlibraries", value -> {

		if(initialized) {
			AxolotlClientConfigManager.save(MOD_ID);
			modIds.clear();
			factories.clear();
			mods.clearOptions();
			constructList();
		}
	}, false);

	public final EnumOption sorting = new EnumOption("modmenu.sorting", new String[]{"ascending", "descending"}, value -> {
		if(initialized) {
			AxolotlClientConfigManager.save(MOD_ID);
			modIds.clear();
			factories.clear();
			mods.clearOptions();
			constructList();
		}
	}, "ascending");

	@Override
	public void init() {
		config.options.add(showLibraries);
		config.options.add(sorting);
		AxolotlClientConfigManager.registerConfig(MOD_ID, config, new DefaultConfigManager(MOD_ID, FabricLoader.getInstance().getConfigDir().resolve(MOD_ID+".json"), config.getCategories()));
	}

	@Override
	public void tick() {
		if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder) {

			if(!initialized){
				constructList();
				AxolotlClient.CONFIG.general.addSubCategory(mods);
				initialized = true;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void constructList(){
		try {
			ModContainer minecraft = FabricLoader.getInstance().getModContainer("minecraft")
					.orElseThrow(() -> new IllegalStateException("How did this happen? Please explain."));
			addMod(minecraft, screen -> new SettingsScreen(screen, MinecraftClient.getInstance().options));

			FabricLoader.getInstance()
					.getEntrypointContainers("modmenu", ModMenuApi.class).forEach(container -> {
						try {
							addMod(container.getProvider(), container.getEntrypoint().getConfigScreenFactory());
						} catch (Exception ignored){

						}
					});

			FabricLoader.getInstance()
					.getEntrypointContainers("modmenu", com.terraformersmc.modmenu.api.ModMenuApi.class).forEach(container ->
							addMod(container.getProvider(), screen -> container.getEntrypoint().getModConfigScreenFactory().create(screen)));

			FabricLoader.getInstance()
					.getEntrypointContainers("modmenu", com.terraformersmc.modmenu.api.ModMenuApi.class).forEach(container -> {
						if(!modIds.contains(container.getProvider().getMetadata().getId())) {
							container.getEntrypoint().getProvidedConfigScreenFactories().forEach((s, configScreenFactory) -> {
								if (!factories.containsKey(s)) {
									factories.put(s, configScreenFactory::create);
								}
							});
						}
					});

			FabricLoader.getInstance().getAllMods().stream()
					.filter(container -> !modIds.contains(container.getMetadata().getId())).collect(Collectors.toList())
					.forEach(container ->
							addMod(container, null));


			mods.getOptions().sort(getComparator());
		} catch (NullPointerException ignored) {
		}
	}

	private void addMod(ModContainer container, Function<Screen, ? extends Screen> configScreen){
		if(!modIds.contains(container.getMetadata().getId())) {
			if(configScreen == null && factories.containsKey(container.getMetadata().getId())){
				configScreen = factories.get(container.getMetadata().getId());
			} else {
				factories.put(container.getMetadata().getId(), configScreen);
			}

			final Function<Screen, ? extends Screen> finalConfigScreen = configScreen;

			if(!(isLibrary(container.getMetadata()) && !showLibraries.get())){
				Util.createIcon(container);
				mods.add(new ModOption(Util.getName(container),
						I18n.translate("open_options"),
						(x, y) -> MinecraftClient.getInstance()
								.openScreen(new ModScreen(container,
										finalConfigScreen,
										MinecraftClient.getInstance().currentScreen)), container.getMetadata().getId()));
			}

			modIds.add(container.getMetadata().getId());
		}
	}

	private boolean isLibrary(ModMetadata metadata){
		try {
			for (CustomValue customValue : metadata.getCustomValue("modmenu").getAsObject().get("badges").getAsArray()) {
				if (customValue.getAsString().contains("library")) {
					return true;
				}
			}
		} catch (NullPointerException ignored){}

		if(fabricModPredicate.test(metadata.getId())){
			return true;
		}
		else return metadata.getId().equals("java");
	}

	public Comparator<Identifiable> getComparator(){
		return sorting.get().equals("ascending") ? new Identifiable.AlphabeticalComparator() : new Identifiable.AlphabeticalComparator().reversed();
	}
}
