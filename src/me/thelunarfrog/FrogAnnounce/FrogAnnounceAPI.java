package me.thelunarfrog.FrogAnnounce;

import java.util.List;

/**
 * Contains all methods to get variables that other plugins might want to use. Will add any that any other developers would like upon request.
 * @version 1.0.0.0
 * @since FrogAnnounce 2.0.10.13
 * @author Dan | TheLunarFrog
 * @category Public API
 */
public class FrogAnnounceAPI extends FrogAnnounce{
    /**
     * Gets FrogAnnounce's current announcement tag, i.e. [FrogAnnounce] Announcement. Will always announce with announcements. Will include colour code, if present. Returns in string. May be blank.
     * @return Current announcer's tag
     * @since FrogAnnounce 1.6.0.0/FrogAnnounceAPI 1.0.0.0
     */
	public String getTag(){
    		return tag;
    }
	/**
	 * Allows other plugins to get the last announcement index that was announced. Lol, what is API? Just now adding this in v2.0.
	 * @since FrogAnnounce 1.6.0.0/FrogAnnounceAPI 1.0.0.0
	 * @return Last announcement index that the server announced.
	 */
	public int getAnnouncementCounter(){
		return counter;
	}
	/**
	 * Returns whether or not the plugin is announcing messages in a random order or not.
	 * @return random
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public boolean isRandomlyAnnouncing(){
		return random;
	}
	/**
	 * Returns what delay each announcement has in between each other.
	 * @return interval
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public int getInterval(){
		return interval;
	}
	/**
	 * Whether or not the plugin is set to use group-exclusion mode.
	 * @return toGroups
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public boolean usingGroupAnnouncementExclusion(){
		return toGroups;
	}
	/**
	 * Whether or not the server has Vault, basically.
	 * @return usingPerms
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public boolean canUsePermissions(){
		return usingPerms;
	}
	/**
	 * Whether or not the plugin is actually using permissions, rather than just being able to like fetch method canUsePermissions().
	 * @return permissionConfig
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public boolean currentlyUsingPermissions(){
		return permissionConfig;
	}
	/**
	 * Returns to which groups the plugin is announcing to. If usingGroupAnnouncementExclusion() returns false, this is an unused value and therein useless to anyone who wants to use it.
	 * @return Groups
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public List<String> getGroups(){
		return Groups;
	}
	/**
	 * Returns the strings that the user has defined in the configuration to be announced. Returns in the order which they are configured.
	 * @return strings
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public List<String> getAnnouncements(){
		return strings;
	}
	/**
	 * Whether or not FrogAnnounce is currently announcing.
	 * @return running
	 * @since FrogAnnounceAPI 1.0.0.0
	 */
	public boolean areAnnouncementsRunning(){
		return running;
	}
	/**
	 * FrogAnnounce's current task. No use to anyone else really; therefore, deprecated.
	 * @return taskId - The current task ID.
	 * @since FrogAnnounceAPI 1.0.0.0
	 * @deprecated
	 */
	public int getTaskId(){
		return taskId;
	}
	
	public FrogAnnounceAPI(){
	}
}