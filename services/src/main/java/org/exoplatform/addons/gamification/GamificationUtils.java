package org.exoplatform.addons.gamification;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.jgroups.util.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GamificationUtils {

    // BLACK LIST GROUP
    public static String BLACK_LISTED_USERS_GROUP = "/leaderboard-blacklist-users";
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_USER_KEY = "GAMIFICATION_DATAMODEL_MIGRATION_USER";
    //scope of gamification migration status
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY = "GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY";
    //status of gamification migration (true if migration completed successfully)
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_DONE = "GAMIFICATION_DATAMODEL_MIGRATION_DONE";

    private static ExecutorService executorService = Executors.newSingleThreadExecutor(new DefaultThreadFactory("GAMIFICATION-DATAMODEL-MIGRATION", false, false));;

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static boolean isGamificationDatamodelMigrated() {
        SettingValue<?> setting = CommonsUtils.getService(SettingService.class).get(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY), GAMIFICATION_DATAMODEL_MIGRATION_DONE);
        return (setting != null && setting.getValue().equals("true"));
    }

    public static void setGamificationDatamodelMigrationDone() {
        CommonsUtils.getService(SettingService.class).set(Context.GLOBAL, Scope.APPLICATION.id(GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY), GAMIFICATION_DATAMODEL_MIGRATION_DONE, SettingValue.create("true"));
    }


    public static boolean isGamificationDatamodelMigrated(String userName) {
        SettingValue<?> setting = CommonsUtils.getService(SettingService.class).get(Context.USER.id(userName), Scope.APPLICATION.id(GAMIFICATION_DATAMODEL_MIGRATION_USER_KEY), GAMIFICATION_DATAMODEL_MIGRATION_DONE);
        return (setting != null && setting.getValue().equals("true"));
    }

    /**
     * Compute the list of user whithin the black list (user we shouldn't display them on leaderboard screen)
     * @param userName : the user social id to check if is already black listed
     * @return : return true is the user is black lisetd false else
     */
    public static boolean isBlackListed(String userName) {

        List<String> blackListedUsers = new ArrayList<String>();

        try {
            ListAccess<User> usersBlackList = CommonsUtils.getService(OrganizationService.class).getUserHandler().findUsersByGroupId(BLACK_LISTED_USERS_GROUP);
            if (null != usersBlackList && usersBlackList.getSize() > 0) {
                User[] managerUser = usersBlackList.load(0, usersBlackList.getSize());
                for (User user : managerUser) {
                    try {
                        blackListedUsers.add(user.getUserName());
                    } catch (Exception e) {

                    }
                }
                return blackListedUsers.stream().anyMatch(userName::equals);
            }
        } catch (Exception e) {

        }

        return false;

    }

}
