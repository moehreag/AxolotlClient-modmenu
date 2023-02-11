package io.github.axolotlclient.modmenu;

import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;

import java.util.ArrayList;
import java.util.List;

public class AxolotlClientModMenuConfig extends ConfigHolder {


    private final List<OptionCategory> categories = new ArrayList<>();

    public final OptionCategory options = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("modmenu");

    @Override
    public List<OptionCategory> getCategories() {
        categories.clear();
        categories.add(options);
        return categories;
    }
}
