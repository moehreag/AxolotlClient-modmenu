package io.github.axolotlclient.modmenu;

import io.github.axolotlclient.AxolotlclientConfig.ConfigHolder;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

import java.util.ArrayList;
import java.util.List;

public class AxolotlClientModMenuConfig extends ConfigHolder {


    private final List<OptionCategory> categories = new ArrayList<>();

    public final OptionCategory options = new OptionCategory("modmenu");

    @Override
    public List<OptionCategory> getCategories() {
        categories.clear();
        categories.add(options);
        return categories;
    }
}
