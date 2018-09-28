package net.prematic.libraries.command;

import net.prematic.libraries.command.owner.CommandOwner;
import net.prematic.libraries.command.sender.CommandSender;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 *
 *  * Copyright (c) 2018 Davide Wietlisbach on 01.09.18 18:12
 *
 */

public abstract class Command {

    private String name, description, permission, usage;
    private CommandOwner owner;
    private List<String> aliases;

    public Command(String name) {
        this.name = name;
        this.description = "none";
        this.aliases = new LinkedList<>();
    }
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        this.aliases = new LinkedList<>();
    }
    public Command(String name, String description, String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = new LinkedList<>();
    }
    public Command(String name, String description, String permission, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.aliases = new LinkedList<>(Arrays.asList(aliases));
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public String getPermission() {
        return this.permission;
    }
    public String getUsage() {
        return usage;
    }
    public boolean hasUsage() {
        return usage != null;
    }
    public CommandOwner getOwner() {
        return this.owner;
    }
    public List<String> getAliases() {
        return this.aliases;
    }
    public Boolean hasAliases(String command){
        if(this.name.equalsIgnoreCase(command)) return true;
        return aliases.contains(command);
    }
    public void init(CommandOwner owner){
        this.owner = owner;
    }
    public abstract void execute(CommandSender sender, String[] args);
}