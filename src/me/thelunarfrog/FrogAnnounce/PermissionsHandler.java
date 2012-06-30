package me.thelunarfrog.FrogAnnounce;

import org.bukkit.plugin.Plugin;

public class PermissionsHandler extends FrogAnnounce {
    protected boolean checkPEX() {
		boolean PEX = false;
		Plugin test = this.getServer().getPluginManager().getPlugin("PermissionsEX");
		if (test != null){
			PEX = true;
		}
		
		return PEX;
	}
    protected boolean checkPerms(){
		boolean NP = false;
		Plugin nijikoPermissions = this.getServer().getPluginManager().getPlugin("Permissions");
		if (nijikoPermissions != null){
			NP = true;
		}
		return NP;
	}
	protected boolean checkbPerms(){
		boolean bP = false;
		Plugin bPermissions = this.getServer().getPluginManager().getPlugin("bPermissions");
		if(bPermissions != null){
			bP = true;
		}
		return bP;
	}
	protected int getPermissionsSystem(){
		int permissionsSystem;
		if(checkPEX()){
			pexEnabled = true;
			permissionsSystem = 1;
		}else if(checkbPerms()){
			bpEnabled = true;
			permissionsSystem = 2;
		}else if(checkPerms()){
			pEnabled = true;
			permissionsSystem = 3;
		}else{
			permissionsSystem = 0;
		}
		return permissionsSystem;
	}
	public void checkPermissionsVaultPlugins(){
		int m = getPermissionsSystem();
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if(vault != null){
			if(m!=0){
				if(setupPermissions()!=null){
					info("Permissions plugin hooked.");
					usingPerms = true;
				}else if(setupPermissions() == null){
					info("Permissions plugin wasn't found. Defaulting to OP/Non-OP system.");
					usingPerms = false;
				}
			}
		}else{
			warning("Vault is not in your plugins directory! This plugin has a soft dependency of Vault, but if you don't have it, this will still work (you just can't use permission-based stuff).");
		}
	}
	protected PermissionsHandler(){
	}
}
