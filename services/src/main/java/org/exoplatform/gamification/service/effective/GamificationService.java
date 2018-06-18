package org.exoplatform.gamification.service.effective;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationContextItemEntity;
import org.exoplatform.gamification.service.dto.effective.GamificationContextHolder;
import org.exoplatform.gamification.storage.dao.GamificationDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GamificationService {

    private static final Log LOG = ExoLogger.getLogger(GamificationService.class);

    protected final GamificationDAO gamificationDAO;

    public GamificationService() {
        this.gamificationDAO = CommonsUtils.getService(GamificationDAO.class);
    }

    /**
     * Find a GamificationContext by username
     *
     * @param username : gamification's username param
     * @return an instance of GamificationContextDTO
     */
    public GamificationContextEntity findGamificationContextByUername(String username) {

        GamificationContextEntity entity = null;

        try {
            //--- Get Entity from DB
            entity = gamificationDAO.findGamificationContextByUsername(username);


        } catch (Exception e) {
            LOG.error("Error to find Gamification entity with username : {}", username, e.getMessage());
        }
        return entity;

    }

    /**
     * Add GamificationContext to DB
     *
     * @param gamificationContextHolder : GamificationContext to be saved
     */
    @ExoTransactional
    public void saveGamificationContext(GamificationContextHolder gamificationContextHolder) {

        try {

            if (gamificationContextHolder.isNew()) {

                gamificationDAO.create(gamificationContextHolder.getGamificationContextEntity());

            } else {

                gamificationDAO.update(gamificationContextHolder.getGamificationContextEntity());

            }

        } catch (Exception e) {
            LOG.error("Error to save gamificationContext for user {}", gamificationContextHolder.getGamificationContextEntity().getUsername(), e);
        }
    }

    /**
     * @param username
     * @return
     */
    @ExoTransactional
    public long getUserGlobalScore(String username) {

        long userScore = 0;

        GamificationContextEntity entity = null;

        try {
            //--- Get Entity from DB
            entity = gamificationDAO.getUserGlobalScore(username);


        } catch (Exception e) {
            LOG.error("Error to find Gamification entity with username : {}", username, e.getMessage());
        }

        if (entity != null) {
            userScore = entity.getScore();
        }

        return userScore;
    }

    /**
     * @param gamificationSearch
     * @return
     */
    @ExoTransactional
    public List<GamificationContextEntity> filter(GamificationSearch gamificationSearch) {

        List<GamificationContextEntity> gamificationContextEntities = null;

        LOG.info("Leaderboard used filter is {}",gamificationSearch.getZone());

        try {

            //TODO : This only a fork, we must get the effective filter from UI
            //List<Leaderboard> lb =  gamificationDAO.findLeaderboardByDomain(gamificationSearch.getZone());
            List<Leaderboard> leaderboardList =  gamificationDAO.findLeaderboardByDomain("Social");

            // Build Gamification Entities
            if (leaderboardList != null && !leaderboardList.isEmpty()) {
                GamificationContextEntity gamificationContextEntity = null;
                gamificationContextEntities = new ArrayList<>();
               for (Leaderboard leaderBoard : leaderboardList) {
                   gamificationContextEntity = new GamificationContextEntity();
                   gamificationContextEntity.setUsername(leaderBoard.getUserId());
                   gamificationContextEntity.setScore(leaderBoard.getScore());
                   gamificationContextEntities.add(gamificationContextEntity);

               }

            }

            // Sort Collection
            gamificationContextEntities.sort((GamificationContextEntity g1, GamificationContextEntity g2)-> (int)g2.getScore()- (int)g1.getScore());

        } catch (Exception e) {
            LOG.error("Error to filter leaderboards by {} and {}", gamificationSearch.getFilter(), gamificationSearch.getZone(), e);
        }

        return gamificationContextEntities;
    }

    /**
     * Get user gamification
     * @param userId
     * @return
     */
    @ExoTransactional
    public Set<GamificationContextItemEntity> getUserGamification(String userId) {

        Set<GamificationContextItemEntity> gamificationContextItemEntitySet = null;

        try {
            //--- Get Entity from DB
            GamificationContextEntity gamificationContextEntity = gamificationDAO.getUserGamification(userId);

            // Get Context items if exists
            if (gamificationContextEntity != null) {

                gamificationContextItemEntitySet = gamificationContextEntity.getGamificationItems();

            }


        } catch (Exception e) {
            LOG.error("Error to load effective gamification for user {} ", userId, e);
        }

        return gamificationContextItemEntitySet;
    }

    /**
     *
     * @param userId
     * @return
     */
    @ExoTransactional
    public List<Piechart> findStatsByUserId(String userId) {

        List<Piechart> leaderboardList = null;

        try {
            //--- Get Stats
            leaderboardList = gamificationDAO.findStatsByUserId(userId);

        } catch (Exception e) {
            LOG.error("Error to load gamification stats for user {} ", userId, e);
        }

        return leaderboardList;
    }


}
